package escuela.portal.service;

import escuela.alumno.entity.Alumno;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.repository.CargoRepository;
import escuela.finanzas.dto.request.PagoRequest;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.finanzas.service.PagoService;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.entity.Institucion;
import escuela.institucion.service.InstitucionService;
import escuela.inscripcion.entity.Inscripcion;
import escuela.portal.dto.PortalPagoForm;
import escuela.portal.dto.PortalSolicitudPagoForm;
import escuela.portal.repository.PortalTutorRepository;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortalPagoServiceTest {
    @Mock private InstitucionService instituciones;
    @Mock private PortalTutorRepository portal;
    @Mock private TutorRepository tutores;
    @Mock private CargoRepository cargos;
    @Mock private CuentaFinancieraRepository cuentas;
    @Mock private PagoService pagos;
    private PortalPagoService service;
    private UsuarioPrincipal principal;
    private Tutor tutor;

    @BeforeEach
    void preparar() {
        service = new PortalPagoService(instituciones, portal, tutores, cargos, cuentas, pagos);
        principal = new UsuarioPrincipal(7L, 1L, Set.of(), false, false,
                "familia", "x", List.of());
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        Usuario usuario = new Usuario(); usuario.setId(7L); usuario.setInstitucion(institucion);
        tutor = new Tutor(); tutor.setId(3L); tutor.setInstitucion(institucion);
        tutor.setUsuario(usuario); tutor.setActivo(true);
        lenient().when(tutores.findByUsuarioIdAndInstitucionIdAndActivoTrue(7L, 1L))
                .thenReturn(Optional.of(tutor));
        lenient().when(instituciones.obtener(1L)).thenReturn(new InstitucionResponse(1L,
                "RAICES", "Instituto Raíces", null, null, null, null, null, null,
                null, null, null, null, "MX", null, "America/Mexico_City", "MXN", true, null));
    }

    @Test
    void distingueCargosDelMismoAlumnoConEtiquetaUnica() {
        Cargo septiembre = cargo(10L, 20L, "A-020", "Ana", "Colegiatura", "Septiembre");
        Cargo octubre = cargo(11L, 20L, "A-020", "Ana", "Colegiatura", "Octubre");
        when(cargos.buscarParaPortal(eq(1L), eq(3L), eq("ana"), any()))
                .thenReturn(new SliceImpl<>(List.of(septiembre, octubre)));

        var resultado = service.buscarCargos(principal, " Ana ");

        assertThat(resultado.resultados()).extracting(opcion -> opcion.titulo())
                .containsExactly(
                        "A-020 · Ana López · Colegiatura · Septiembre · #10",
                        "A-020 · Ana López · Colegiatura · Octubre · #11")
                .doesNotHaveDuplicates();
    }

    @Test
    void conservaTresDistribucionesAlConstruirLaSolicitud() {
        PortalPagoForm form = new PortalPagoForm();
        form.setPlantelRegistroId(2L);
        form.setFechaPago(LocalDateTime.now().minusMinutes(5));
        form.setMonto(new BigDecimal("900.00"));
        form.setCuentaDeclaradaId(8L);
        form.setReferencia("RASTREO-123");
        form.setSolicitudes(List.of(solicitud(10L, "200.00"), solicitud(11L, "300.00"),
                solicitud(12L, "400.00")));
        MockMultipartFile comprobante = new MockMultipartFile("comprobantes", "pago.pdf",
                "application/pdf", "%PDF-1.4".getBytes());
        when(pagos.registrarDesdePortal(any(), anyList(), eq(principal)))
                .thenReturn(mock(PagoResponse.class));

        service.reportar(principal, form, List.of(comprobante));

        ArgumentCaptor<PagoRequest> captor = ArgumentCaptor.forClass(PagoRequest.class);
        verify(pagos).registrarDesdePortal(captor.capture(), eq(List.of(comprobante)), eq(principal));
        assertThat(captor.getValue().solicitudes()).extracting(s -> s.cargoId())
                .containsExactly(10L, 11L, 12L);
        assertThat(captor.getValue().solicitudes()).extracting(s -> s.montoSolicitado())
                .containsExactly(new BigDecimal("200.00"), new BigDecimal("300.00"),
                        new BigDecimal("400.00"));
    }

    @Test
    void noEnviaUnaDistribucionQueNoCoincideConLaTransferencia() {
        PortalPagoForm form = new PortalPagoForm();
        form.setMonto(new BigDecimal("500.00"));
        form.setSolicitudes(List.of(solicitud(10L, "400.00")));

        assertThatThrownBy(() -> service.reportar(principal, form,
                List.of(new MockMultipartFile("comprobantes", "pago.pdf",
                        "application/pdf", "%PDF-1.4".getBytes()))))
                .hasMessageContaining("debe coincidir exactamente");
        verifyNoInteractions(pagos);
    }

    @Test
    void rechazaUnaSesionQueNoCorrespondeAUnTutorActivo() {
        when(tutores.findByUsuarioIdAndInstitucionIdAndActivoTrue(7L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarCargos(principal, "ana"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("tutor activo");
    }

    private PortalSolicitudPagoForm solicitud(Long cargoId, String monto) {
        PortalSolicitudPagoForm solicitud = new PortalSolicitudPagoForm();
        solicitud.setCargoId(cargoId);
        solicitud.setMontoSolicitado(new BigDecimal(monto));
        return solicitud;
    }

    private Cargo cargo(Long id, Long alumnoId, String matricula, String nombre,
                        String conceptoNombre, String descripcion) {
        Alumno alumno = new Alumno(); alumno.setId(alumnoId); alumno.setMatricula(matricula);
        alumno.setNombres(nombre); alumno.setPrimerApellido("López");
        Inscripcion inscripcion = new Inscripcion(); inscripcion.setAlumno(alumno);
        ConceptoCobro concepto = new ConceptoCobro(); concepto.setNombre(conceptoNombre);
        Cargo cargo = new Cargo(); cargo.setId(id); cargo.setInscripcion(inscripcion);
        cargo.setConceptoCobro(concepto); cargo.setDescripcion(descripcion);
        cargo.setFechaVencimiento(LocalDate.of(2026, 10, 10));
        cargo.setImporteOriginal(new BigDecimal("500.00")); cargo.setMoneda("MXN");
        return cargo;
    }
}
