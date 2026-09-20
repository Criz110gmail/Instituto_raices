package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.admin.repository.ReporteFinancieroRepository;
import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.entity.Institucion;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReporteFinancieroConsultaServiceTest {
    private final ReporteFinancieroRepository repository = mock(ReporteFinancieroRepository.class);
    private final AlumnoRepository alumnos = mock(AlumnoRepository.class);
    private final CuentaFinancieraRepository cuentas = mock(CuentaFinancieraRepository.class);
    private final InstitucionService instituciones = mock(InstitucionService.class);
    private final PlantelService planteles = mock(PlantelService.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final ReporteFinancieroConsultaService service = new ReporteFinancieroConsultaService(
            repository, alumnos, cuentas, instituciones, planteles, alcance);

    @Test
    void usaLaFechaLocalDeLaInstitucionAlNormalizar() {
        when(instituciones.obtener(1L)).thenReturn(institucion("Pacific/Kiritimati"));

        var filtro = service.normalizar(new FiltroEstadoCuentaAlumno(
                1L, null, "", null, "TODOS", null, -2, 7));

        assertThat(filtro.fechaCorte()).isEqualTo(LocalDate.now(ZoneId.of("Pacific/Kiritimati")));
        assertThat(filtro.pagina()).isZero();
        assertThat(filtro.tamanio()).isEqualTo(25);
        verify(alcance).validarInstitucion(1L);
    }

    @Test
    void devuelveEstadoVacioSinConsultarCargosCuandoNoHayAlumno() {
        when(instituciones.obtener(1L)).thenReturn(institucion("America/Mexico_City"));

        ResultadoEstadoCuentaAlumno resultado = service.estadoCuenta(new FiltroEstadoCuentaAlumno(
                1L, null, "", null, "TODOS", LocalDate.of(2026, 9, 19), 0, 25));

        assertThat(resultado.pagina()).isEmpty();
        assertThat(resultado.resumen().saldo()).isEqualByComparingTo(BigDecimal.ZERO);
        verifyNoInteractions(repository, alumnos);
    }

    @Test
    void rechazaAlumnoDeOtraInstitucionAntesDeConsultarElReporte() {
        when(instituciones.obtener(1L)).thenReturn(institucion("America/Mexico_City"));
        Institucion otra = new Institucion();
        otra.setId(2L);
        Alumno alumno = new Alumno();
        alumno.setId(9L);
        alumno.setInstitucion(otra);
        when(alumnos.findById(9L)).thenReturn(Optional.of(alumno));

        assertThatThrownBy(() -> service.estadoCuenta(new FiltroEstadoCuentaAlumno(
                1L, 9L, "A-9", null, "TODOS", LocalDate.of(2026, 9, 19), 0, 25)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no pertenece");
        verifyNoInteractions(repository);
    }

    @Test
    void rechazaPeriodoInvertidoSinConsultarMovimientos() {
        when(instituciones.obtener(1L)).thenReturn(institucion("America/Mexico_City"));

        assertThatThrownBy(() -> service.tesoreria(new FiltroReporteTesoreria(
                1L, null, "", null, "DIARIA", LocalDate.of(2026, 9, 20),
                LocalDate.of(2026, 9, 19), 0, 25)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("fecha inicial");
        verifyNoInteractions(repository);
    }

    @Test
    void convierteElPeriodoCompletoALimitesInstantaneosEnLaZonaInstitucional() {
        when(instituciones.obtener(1L)).thenReturn(institucion("America/Mexico_City"));
        when(alcance.alcanceInstitucionalActual(1L)).thenReturn(true);
        when(alcance.plantelesActuales(1L)).thenReturn(Set.of());
        when(repository.tesoreria(any(), any(), any(), any(), anyString())).thenReturn(Page.empty());
        when(repository.resumenTesoreria(any(), any(), any(), any(), anyString()))
                .thenReturn(new ResumenTesoreria(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, true, "MXN"));
        LocalDate desde = LocalDate.of(2026, 4, 5);
        LocalDate hasta = LocalDate.of(2026, 4, 7);

        service.tesoreria(new FiltroReporteTesoreria(
                1L, null, "", null, "DIARIA", desde, hasta, 0, 25));

        ArgumentCaptor<Instant> inicio = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> fin = ArgumentCaptor.forClass(Instant.class);
        verify(repository).tesoreria(any(), any(), inicio.capture(), fin.capture(),
                eq("America/Mexico_City"));
        ZoneId zona = ZoneId.of("America/Mexico_City");
        assertThat(inicio.getValue()).isEqualTo(desde.atStartOfDay(zona).toInstant());
        assertThat(fin.getValue()).isEqualTo(hasta.plusDays(1).atStartOfDay(zona).toInstant());
    }

    private InstitucionResponse institucion(String zona) {
        return new InstitucionResponse(1L, "RAI", "Instituto Raíces", null, null,
                null, null, null, null, null, null, null, null, "MX", null,
                zona, "MXN", true, null);
    }
}
