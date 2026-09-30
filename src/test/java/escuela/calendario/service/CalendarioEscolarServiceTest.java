package escuela.calendario.service;

import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.repository.CicloEscolarRepository;
import escuela.academico.repository.NivelEducativoRepository;
import escuela.calendario.dto.CalendarioEscolarForm;
import escuela.calendario.entity.TipoFechaCalendario;
import escuela.calendario.repository.CalendarioEscolarRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import escuela.institucion.repository.PlantelRepository;
import escuela.institucion.repository.PlantelNivelRepository;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CalendarioEscolarServiceTest {
    private final CalendarioEscolarRepository repository = mock(CalendarioEscolarRepository.class);
    private final InstitucionRepository instituciones = mock(InstitucionRepository.class);
    private final CicloEscolarRepository ciclos = mock(CicloEscolarRepository.class);
    private final PlantelRepository planteles = mock(PlantelRepository.class);
    private final NivelEducativoRepository niveles = mock(NivelEducativoRepository.class);
    private final PlantelNivelRepository ofertas = mock(PlantelNivelRepository.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final CalendarioEscolarService service = new CalendarioEscolarService(repository,
            instituciones, ciclos, planteles, niveles, ofertas, alcance);

    @BeforeEach
    void preparar() {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        CicloEscolar ciclo = new CicloEscolar();
        ciclo.setId(2L);
        ciclo.setInstitucion(institucion);
        ciclo.setEstado(EstadoAcademico.ABIERTO);
        ciclo.setFechaInicio(LocalDate.of(2026, 8, 1));
        ciclo.setFechaFin(LocalDate.of(2027, 7, 15));
        when(instituciones.findById(1L)).thenReturn(Optional.of(institucion));
        when(ciclos.findById(2L)).thenReturn(Optional.of(ciclo));
    }

    @Test
    void variacionDeHorarioExigeAmbasHoras() {
        CalendarioEscolarForm form = base();
        form.setTipo(TipoFechaCalendario.VARIACION_HORARIO);

        assertThatThrownBy(() -> service.crear(form))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("requiere hora inicial y final");
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void rechazaFechasFueraDelCicloConUnMensajeExplicito() {
        CalendarioEscolarForm form = base();
        form.setFechaInicio(LocalDate.of(2027, 7, 16));
        form.setFechaFin(LocalDate.of(2027, 7, 17));

        assertThatThrownBy(() -> service.crear(form))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("dentro del ciclo escolar")
                .hasMessageContaining("2026-08-01")
                .hasMessageContaining("2027-07-15");
        verify(repository, never()).saveAndFlush(any());
    }

    private CalendarioEscolarForm base() {
        CalendarioEscolarForm form = new CalendarioEscolarForm();
        form.setInstitucionId(1L);
        form.setCicloEscolarId(2L);
        form.setTipo(TipoFechaCalendario.DIA_INHABIL);
        form.setTitulo("Consejo técnico");
        form.setFechaInicio(LocalDate.of(2026, 9, 25));
        form.setFechaFin(LocalDate.of(2026, 9, 25));
        form.setActivo(true);
        return form;
    }
}
