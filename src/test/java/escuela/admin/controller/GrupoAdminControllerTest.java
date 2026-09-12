package escuela.admin.controller;

import escuela.academico.dto.request.GrupoRequest;
import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.dto.response.GradoResponse;
import escuela.academico.dto.response.GrupoResponse;
import escuela.academico.dto.response.NivelEducativoResponse;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.Turno;
import escuela.academico.service.CicloEscolarService;
import escuela.academico.service.GradoService;
import escuela.academico.service.GrupoService;
import escuela.academico.service.NivelEducativoService;
import escuela.admin.dto.GrupoForm;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.institucion.dto.response.PlantelNivelResponse;
import escuela.institucion.dto.response.PlantelResponse;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelNivelService;
import escuela.institucion.service.PlantelService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrupoAdminControllerTest {

    @Mock private GrupoService service;
    @Mock private InstitucionService institucionService;
    @Mock private PlantelService plantelService;
    @Mock private PlantelNivelService ofertaService;
    @Mock private CicloEscolarService cicloService;
    @Mock private NivelEducativoService nivelService;
    @Mock private GradoService gradoService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private GrupoAdminController controller;

    @BeforeEach
    void prepararAlcance() {
        org.mockito.Mockito.lenient().when(alcance.filtrarInstituciones(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(i -> i.getArgument(0));
        org.mockito.Mockito.lenient().when(alcance.filtrarPlanteles(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(i -> i.getArgument(0));
        org.mockito.Mockito.lenient().when(alcance.filtrarNiveles(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void creaElGrupoConTodosLosDatos() {
        GrupoForm form = formulario();
        relacionesValidas();
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.crear(form, errores, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        ArgumentCaptor<GrupoRequest> request = ArgumentCaptor.forClass(GrupoRequest.class);
        verify(service).crear(request.capture());
        assertThat(request.getValue().plantelId()).isEqualTo(2L);
        assertThat(request.getValue().cicloEscolarId()).isEqualTo(10L);
        assertThat(request.getValue().gradoId()).isEqualTo(30L);
        assertThat(request.getValue().turno()).isEqualTo(Turno.MATUTINO);
        assertThat(request.getValue().capacidad()).isEqualTo(25);
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/grupos");
    }

    @Test
    void rechazaUnGradoFueraDeLaOfertaActiva() {
        GrupoForm form = formulario();
        when(plantelService.obtener(2L)).thenReturn(plantel());
        when(cicloService.obtener(10L)).thenReturn(ciclo());
        when(gradoService.obtener(30L)).thenReturn(grado());
        when(nivelService.obtener(20L)).thenReturn(nivel());
        when(ofertaService.listarPorPlantel(2L)).thenReturn(List.of());
        catalogosVacios();
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.crear(form, errores, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/grupo-form");
        assertThat(errores.getFieldError("gradoId")).isNotNull();
        verify(service, never()).crear(any());
    }

    @Test
    void muestraElDuplicadoEnElMismoFormulario() {
        GrupoForm form = formulario();
        relacionesValidas();
        when(service.crear(any())).thenThrow(
                new RecursoDuplicadoException("Ya existe un grupo con el mismo nombre y turno"));
        catalogosVacios();
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.crear(form, errores, model, new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/grupo-form");
        assertThat(model.get("form")).isSameAs(form);
        assertThat(model.get("errorOperacion")).asString().contains("mismo nombre y turno");
    }

    @Test
    void reconstruyeInstitucionYVersionAlEditar() {
        when(service.obtener(40L)).thenReturn(new GrupoResponse(
                40L, 2L, 10L, 30L, "A", Turno.MATUTINO, "1A-MAT", "Aula 3",
                25, true, auditoria()));
        when(plantelService.obtener(2L)).thenReturn(plantel());
        catalogosVacios();
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.editar(40L, model);

        GrupoForm form = (GrupoForm) model.get("form");
        assertThat(vista).isEqualTo("admin/grupo-form");
        assertThat(form.getInstitucionId()).isEqualTo(1L);
        assertThat(form.getPlantelId()).isEqualTo(2L);
        assertThat(form.getGradoId()).isEqualTo(30L);
        assertThat(form.getVersion()).isEqualTo(6L);
    }

    private void relacionesValidas() {
        when(plantelService.obtener(2L)).thenReturn(plantel());
        when(cicloService.obtener(10L)).thenReturn(ciclo());
        when(gradoService.obtener(30L)).thenReturn(grado());
        when(nivelService.obtener(20L)).thenReturn(nivel());
        when(ofertaService.listarPorPlantel(2L)).thenReturn(List.of(oferta()));
    }

    private void catalogosVacios() {
        when(institucionService.listar()).thenReturn(List.of());
        when(plantelService.listar()).thenReturn(List.of());
        when(nivelService.listar()).thenReturn(List.of());
    }

    private GrupoForm formulario() {
        GrupoForm form = new GrupoForm();
        form.setInstitucionId(1L);
        form.setPlantelId(2L);
        form.setCicloEscolarId(10L);
        form.setGradoId(30L);
        form.setNombre("A");
        form.setTurno(Turno.MATUTINO);
        form.setCodigo("1A-MAT");
        form.setAula("Aula 3");
        form.setCapacidad(25);
        form.setActivo(true);
        return form;
    }

    private PlantelResponse plantel() {
        return new PlantelResponse(2L, 1L, "CENTRO", "Plantel Centro", null, null,
                null, null, null, null, null, null, null, null, true, auditoria());
    }

    private CicloEscolarResponse ciclo() {
        return new CicloEscolarResponse(10L, 1L, "2026-2027", "Ciclo 2026-2027",
                LocalDate.of(2026, 8, 1), LocalDate.of(2027, 7, 31),
                EstadoAcademico.ABIERTO, true, auditoria());
    }

    private GradoResponse grado() {
        return new GradoResponse(30L, 20L, "1", "Primer grado", 1, true, auditoria());
    }

    private NivelEducativoResponse nivel() {
        return new NivelEducativoResponse(20L, 1L, "PRIM", "Primaria", null,
                1, true, auditoria());
    }

    private PlantelNivelResponse oferta() {
        return new PlantelNivelResponse(50L, 2L, 20L, null, true, auditoria());
    }

    private AuditoriaResponse auditoria() {
        Instant ahora = Instant.parse("2026-09-11T12:00:00Z");
        return new AuditoriaResponse(ahora, null, ahora, null, 6L);
    }
}
