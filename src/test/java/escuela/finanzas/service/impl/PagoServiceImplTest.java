package escuela.finanzas.service.impl;

import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.CargoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.*;
import escuela.finanzas.entity.*;
import escuela.finanzas.mapper.PagoMapper;
import escuela.finanzas.repository.*;
import escuela.institucion.entity.*;
import escuela.institucion.repository.*;
import escuela.inscripcion.entity.Inscripcion;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PagoServiceImplTest {
    private final PagoRepository pagoRepository = mock(PagoRepository.class);
    private final ComprobantePagoRepository comprobanteRepository = mock(ComprobantePagoRepository.class);
    private final SolicitudAplicacionPagoRepository solicitudRepository = mock(SolicitudAplicacionPagoRepository.class);
    private final ArchivoRepository archivoRepository = mock(ArchivoRepository.class);
    private final InstitucionRepository institucionRepository = mock(InstitucionRepository.class);
    private final PlantelRepository plantelRepository = mock(PlantelRepository.class);
    private final TutorRepository tutorRepository = mock(TutorRepository.class);
    private final CuentaFinancieraRepository cuentaRepository = mock(CuentaFinancieraRepository.class);
    private final CargoRepository cargoRepository = mock(CargoRepository.class);
    private final AlumnoTutorRepository vinculoRepository = mock(AlumnoTutorRepository.class);
    private final AlmacenamientoArchivo almacenamiento = mock(AlmacenamientoArchivo.class);
    private final PagoServiceImpl service = new PagoServiceImpl(pagoRepository, comprobanteRepository,
            solicitudRepository, archivoRepository, institucionRepository, plantelRepository,
            tutorRepository, cuentaRepository, cargoRepository, vinculoRepository,
            almacenamiento, new PagoMapper());

    private Institucion institucion;
    private Plantel plantel;
    private Tutor tutor;
    private Cargo cargoA;
    private Cargo cargoB;
    private Cargo cargoC;

    @BeforeEach
    void preparar() {
        institucion = new Institucion(); institucion.setId(1L); institucion.setNombre("Raíces");
        institucion.setActivo(true); institucion.setMonedaPredeterminada("MXN");
        institucion.setZonaHoraria("America/Mexico_City");
        plantel = new Plantel(); plantel.setId(2L); plantel.setNombre("Centro");
        plantel.setInstitucion(institucion); plantel.setActivo(true);
        tutor = new Tutor(); tutor.setId(3L); tutor.setNombres("María");
        tutor.setPrimerApellido("López"); tutor.setInstitucion(institucion); tutor.setActivo(true);
        Usuario usuarioTutor = new Usuario(); usuarioTutor.setId(7L); usuarioTutor.setUsername("familia");
        usuarioTutor.setInstitucion(institucion); tutor.setUsuario(usuarioTutor);
        cargoA = cargo(10L, "A-01", "Ana", "Colegiatura", "Septiembre", "1200.00");
        cargoB = cargo(11L, "B-01", "Bruno", "Material", "Libros", "800.00");
        cargoC = cargo(12L, "C-01", "Carla", "Transporte", "Octubre", "400.00");
        when(institucionRepository.findById(1L)).thenReturn(Optional.of(institucion));
        when(plantelRepository.findById(2L)).thenReturn(Optional.of(plantel));
        when(tutorRepository.findById(3L)).thenReturn(Optional.of(tutor));
        when(cargoRepository.findById(10L)).thenReturn(Optional.of(cargoA));
        when(cargoRepository.findById(11L)).thenReturn(Optional.of(cargoB));
        when(cargoRepository.findById(12L)).thenReturn(Optional.of(cargoC));
        when(vinculoRepository.tieneResponsabilidadFinancieraVigente(any(), eq(3L), any())).thenReturn(true);
        when(pagoRepository.saveAndFlush(any())).thenAnswer(inv -> { Pago p = inv.getArgument(0); p.setId(50L); return p; });
        when(archivoRepository.saveAndFlush(any())).thenAnswer(inv -> { var a = inv.getArgument(0, escuela.archivo.entity.Archivo.class); a.setId(60L); return a; });
    }

    @Test
    void registraUnPagoParaDosHijosSinAfectarLosCargos() {
        var respuesta = service.registrar(request(MetodoPago.EFECTIVO, "2000.00", null,
                List.of(solicitud(10L, "1200.00"), solicitud(11L, "500.00"))), List.of());

        assertThat(respuesta.estado()).isEqualTo(EstadoPago.PENDIENTE_VALIDACION);
        assertThat(respuesta.montoSolicitado()).isEqualByComparingTo("1700.00");
        assertThat(respuesta.montoSinAsignar()).isEqualByComparingTo("300.00");
        assertThat(respuesta.solicitudes()).hasSize(2);
        verify(solicitudRepository, times(2)).save(any());
        verify(cargoRepository, never()).save(any());
    }

    @Test
    void portalRegistraUnaTransferenciaDistribuidaEntreTresHijos() {
        MockMultipartFile pdf = new MockMultipartFile("comprobantes", "transferencia.pdf",
                "application/pdf", "%PDF-1.4\ncontenido".getBytes());
        UsuarioPrincipal principal = new UsuarioPrincipal(7L, 1L, Set.of(), false, false,
                "familia", "x", List.of());

        var respuesta = service.registrarDesdePortal(request(MetodoPago.TRANSFERENCIA, "900.00", null,
                List.of(solicitud(10L, "200.00"), solicitud(11L, "300.00"),
                        solicitud(12L, "400.00"))), List.of(pdf), principal);

        assertThat(respuesta.origenRegistro()).isEqualTo(OrigenRegistroPago.PORTAL_FAMILIAR);
        assertThat(respuesta.reportadoPor()).isEqualTo("familia");
        assertThat(respuesta.solicitudes()).extracting(s -> s.matricula())
                .containsExactly("A-01", "B-01", "C-01");
        assertThat(respuesta.montoSolicitado()).isEqualByComparingTo("900.00");
        verify(vinculoRepository, times(3))
                .tieneResponsabilidadFinancieraVigente(any(), eq(3L), any());
        verify(cargoRepository, never()).save(any());
    }

    @Test
    void transferenciaExigeComprobante() {
        assertThatThrownBy(() -> service.registrar(request(MetodoPago.TRANSFERENCIA,
                "500.00", null, List.of()), List.of()))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("requiere al menos");
    }

    @Test
    void impideDistribuirMasQueElPago() {
        assertThatThrownBy(() -> service.registrar(request(MetodoPago.EFECTIVO,
                "500.00", null, List.of(solicitud(10L, "600.00"))), List.of()))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("superar el monto total");
    }

    @Test
    void impideCargoSinResponsabilidadFinanciera() {
        when(vinculoRepository.tieneResponsabilidadFinancieraVigente(any(), eq(3L), any())).thenReturn(false);
        assertThatThrownBy(() -> service.registrar(request(MetodoPago.EFECTIVO,
                "500.00", null, List.of(solicitud(10L, "500.00"))), List.of()))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("responsabilidad financiera");
    }

    @Test
    void efectivoNoPuedeDeclararCuentaBancaria() {
        CuentaFinanciera cuenta = cuenta(TipoCuentaFinanciera.BANCO);
        when(cuentaRepository.findById(8L)).thenReturn(Optional.of(cuenta));
        assertThatThrownBy(() -> service.registrar(request(MetodoPago.EFECTIVO,
                "500.00", 8L, List.of()), List.of()))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("tipo caja");
    }

    @Test
    void rechazaComprobanteConContenidoFalso() {
        MockMultipartFile falso = new MockMultipartFile("comprobantes", "falso.pdf",
                "application/pdf", "esto no es pdf".getBytes());
        assertThatThrownBy(() -> service.registrar(request(MetodoPago.TRANSFERENCIA,
                "500.00", null, List.of()), List.of(falso)))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("JPEG, PNG o PDF");
    }

    @Test
    void guardaComprobantePrivadoSinValidarElPago() {
        MockMultipartFile pdf = new MockMultipartFile("comprobantes", "transferencia.pdf",
                "application/pdf", "%PDF-1.4\ncontenido".getBytes());
        var respuesta = service.registrar(request(MetodoPago.TRANSFERENCIA,
                "500.00", null, List.of()), List.of(pdf));

        assertThat(respuesta.comprobantes()).hasSize(1);
        assertThat(respuesta.estado()).isEqualTo(EstadoPago.PENDIENTE_VALIDACION);
        verify(almacenamiento).guardar(contains("/pagos/50/"), any());
        verify(comprobanteRepository).save(any());
    }

    private PagoRequest request(MetodoPago metodo, String monto, Long cuenta,
                                List<SolicitudAplicacionPagoRequest> solicitudes) {
        return new PagoRequest(1L, 2L, 3L, null, "PAG-001", Instant.now().minusSeconds(60),
                new BigDecimal(monto), "MXN", metodo, cuenta, "REF", null, solicitudes);
    }

    private SolicitudAplicacionPagoRequest solicitud(Long cargoId, String monto) {
        return new SolicitudAplicacionPagoRequest(cargoId, new BigDecimal(monto));
    }

    private CuentaFinanciera cuenta(TipoCuentaFinanciera tipo) {
        CuentaFinanciera cuenta = new CuentaFinanciera(); cuenta.setId(8L); cuenta.setActivo(true);
        cuenta.setInstitucion(institucion); cuenta.setPlantel(plantel); cuenta.setTipo(tipo);
        return cuenta;
    }

    private Cargo cargo(Long id, String matricula, String nombre, String concepto,
                        String descripcion, String importe) {
        Alumno alumno = new Alumno(); alumno.setId(id + 100); alumno.setMatricula(matricula);
        alumno.setNombres(nombre); alumno.setPrimerApellido("López"); alumno.setInstitucion(institucion);
        Inscripcion inscripcion = new Inscripcion(); inscripcion.setId(id + 200); inscripcion.setAlumno(alumno);
        inscripcion.setPlantel(plantel);
        ConceptoCobro conceptoCobro = new ConceptoCobro(); conceptoCobro.setNombre(concepto);
        Cargo cargo = new Cargo(); cargo.setId(id); cargo.setInscripcion(inscripcion);
        cargo.setConceptoCobro(conceptoCobro); cargo.setDescripcion(descripcion);
        cargo.setImporteOriginal(new BigDecimal(importe)); cargo.setMoneda("MXN");
        cargo.setEstadoRegistro(EstadoRegistroCargo.EMITIDO);
        return cargo;
    }
}
