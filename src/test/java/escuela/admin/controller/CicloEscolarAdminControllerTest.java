package escuela.admin.controller;

import escuela.academico.dto.request.CicloEscolarRequest;
import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.service.CicloEscolarService;
import escuela.admin.dto.CicloEscolarForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CicloEscolarAdminControllerTest {

    @Mock private CicloEscolarService service;
    @Mock private InstitucionService institucionService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private CicloEscolarAdminController controller;

    @BeforeEach
    void prepararAlcance() {
        org.mockito.Mockito.lenient().when(alcance.filtrarInstituciones(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void preparaUnCicloNuevoComoPlanificado() {
        when(institucionService.listar()).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.nuevo(model);

        CicloEscolarForm form = (CicloEscolarForm) model.get("form");
        assertThat(vista).isEqualTo("admin/ciclo-form");
        assertThat(form.getEstado()).isEqualTo(EstadoAcademico.PLANIFICADO);
        assertThat(model).containsKeys("instituciones", "estados");
        assertThat(ModuloCatalogo.CICLOS.mantenimientoDisponible()).isTrue();
        assertThat(ModuloCatalogo.CICLOS.rutaMantenimiento()).isEqualTo("/admin/ciclos");
        assertThat(ModuloCatalogo.CICLOS.segmentoNuevo()).isEqualTo("/nuevo");
    }

    @Test
    void creaElCicloConFechasEstadoYPredeterminado() {
        CicloEscolarForm form = formulario();
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.crear(form, errores, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        ArgumentCaptor<CicloEscolarRequest> request = ArgumentCaptor.forClass(CicloEscolarRequest.class);
        verify(service).crear(request.capture());
        assertThat(request.getValue().institucionId()).isEqualTo(1L);
        assertThat(request.getValue().fechaInicio()).isEqualTo(LocalDate.of(2026, 8, 1));
        assertThat(request.getValue().estado()).isEqualTo(EstadoAcademico.ABIERTO);
        assertThat(request.getValue().predeterminado()).isTrue();
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/ciclos");
    }

    @Test
    void muestraLaReglaDeFechasEnElMismoFormulario() {
        CicloEscolarForm form = formulario();
        when(service.crear(any())).thenThrow(
                new ReglaNegocioException("La fecha inicial del ciclo no puede ser posterior a la final"));
        when(institucionService.listar()).thenReturn(List.of());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.crear(form, errores, model, new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/ciclo-form");
        assertThat(model.get("form")).isSameAs(form);
        assertThat(model.get("errorOperacion")).asString().contains("fecha inicial");
    }

    @Test
    void reconstruyeTodosLosDatosAlEditar() {
        CicloEscolarResponse ciclo = new CicloEscolarResponse(
                8L, 1L, "2026-2027", "Ciclo escolar 2026-2027",
                LocalDate.of(2026, 8, 1), LocalDate.of(2027, 7, 31),
                EstadoAcademico.CERRADO, true, auditoria());
        when(service.obtener(8L)).thenReturn(ciclo);
        when(institucionService.listar()).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.editar(8L, model);

        CicloEscolarForm form = (CicloEscolarForm) model.get("form");
        assertThat(vista).isEqualTo("admin/ciclo-form");
        assertThat(form.getInstitucionId()).isEqualTo(1L);
        assertThat(form.getEstado()).isEqualTo(EstadoAcademico.CERRADO);
        assertThat(form.isPredeterminado()).isTrue();
        assertThat(form.getVersion()).isEqualTo(4L);
        assertThat(model.get("edicion")).isEqualTo(true);
    }

    private CicloEscolarForm formulario() {
        CicloEscolarForm form = new CicloEscolarForm();
        form.setInstitucionId(1L);
        form.setCodigo("2026-2027");
        form.setNombre("Ciclo escolar 2026-2027");
        form.setFechaInicio(LocalDate.of(2026, 8, 1));
        form.setFechaFin(LocalDate.of(2027, 7, 31));
        form.setEstado(EstadoAcademico.ABIERTO);
        form.setPredeterminado(true);
        return form;
    }

    private AuditoriaResponse auditoria() {
        Instant ahora = Instant.parse("2026-09-11T12:00:00Z");
        return new AuditoriaResponse(ahora, null, ahora, null, 4L);
    }
}
