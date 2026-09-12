package escuela.admin.controller;

import escuela.academico.dto.request.PeriodoAcademicoRequest;
import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.dto.response.NivelEducativoResponse;
import escuela.academico.dto.response.PeriodoAcademicoResponse;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.TipoPeriodoAcademico;
import escuela.academico.service.CicloEscolarService;
import escuela.academico.service.NivelEducativoService;
import escuela.academico.service.PeriodoAcademicoService;
import escuela.admin.dto.PeriodoAcademicoForm;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodoAcademicoAdminControllerTest {

    @Mock private PeriodoAcademicoService service;
    @Mock private CicloEscolarService cicloService;
    @Mock private NivelEducativoService nivelService;
    @Mock private InstitucionService institucionService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private PeriodoAcademicoAdminController controller;

    @BeforeEach
    void prepararAlcance() {
        org.mockito.Mockito.lenient().when(alcance.filtrarInstituciones(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(i -> i.getArgument(0));
        org.mockito.Mockito.lenient().when(alcance.filtrarNiveles(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void creaElPeriodoConTodosLosDatos() {
        PeriodoAcademicoForm form = formulario();
        when(cicloService.obtener(10L)).thenReturn(ciclo(10L, 1L));
        when(nivelService.obtener(20L)).thenReturn(nivel(20L, 1L));
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.crear(form, errores, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        ArgumentCaptor<PeriodoAcademicoRequest> request =
                ArgumentCaptor.forClass(PeriodoAcademicoRequest.class);
        verify(service).crear(request.capture());
        assertThat(request.getValue().cicloEscolarId()).isEqualTo(10L);
        assertThat(request.getValue().nivelEducativoId()).isEqualTo(20L);
        assertThat(request.getValue().tipo()).isEqualTo(TipoPeriodoAcademico.TRIMESTRE);
        assertThat(request.getValue().orden()).isEqualTo(1);
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/periodos");
    }

    @Test
    void rechazaRelacionesDeOtraInstitucionAntesDeGuardar() {
        PeriodoAcademicoForm form = formulario();
        when(cicloService.obtener(10L)).thenReturn(ciclo(10L, 2L));
        when(nivelService.obtener(20L)).thenReturn(nivel(20L, 1L));
        when(institucionService.listar()).thenReturn(List.of());
        when(nivelService.listar()).thenReturn(List.of());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.crear(form, errores, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/periodo-form");
        assertThat(errores.getFieldError("cicloEscolarId")).isNotNull();
        verify(service, never()).crear(any());
    }

    @Test
    void muestraElSolapamientoEnElMismoFormulario() {
        PeriodoAcademicoForm form = formulario();
        when(cicloService.obtener(10L)).thenReturn(ciclo(10L, 1L));
        when(nivelService.obtener(20L)).thenReturn(nivel(20L, 1L));
        when(service.crear(any())).thenThrow(
                new ReglaNegocioException("El periodo se solapa con otro periodo del mismo ciclo y nivel"));
        when(institucionService.listar()).thenReturn(List.of());
        when(nivelService.listar()).thenReturn(List.of());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.crear(form, errores, model, new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/periodo-form");
        assertThat(model.get("form")).isSameAs(form);
        assertThat(model.get("errorOperacion")).asString().contains("solapa");
    }

    @Test
    void reconstruyeInstitucionYVersionAlEditar() {
        when(service.obtener(30L)).thenReturn(new PeriodoAcademicoResponse(
                30L, 10L, 20L, "TRI1", "Primer trimestre",
                TipoPeriodoAcademico.TRIMESTRE, 1, LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 10, 31), EstadoAcademico.CERRADO, "Concluido", auditoria()));
        when(cicloService.obtener(10L)).thenReturn(ciclo(10L, 1L));
        when(institucionService.listar()).thenReturn(List.of());
        when(nivelService.listar()).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.editar(30L, model);

        PeriodoAcademicoForm form = (PeriodoAcademicoForm) model.get("form");
        assertThat(vista).isEqualTo("admin/periodo-form");
        assertThat(form.getInstitucionId()).isEqualTo(1L);
        assertThat(form.getCicloEscolarId()).isEqualTo(10L);
        assertThat(form.getNivelEducativoId()).isEqualTo(20L);
        assertThat(form.getEstado()).isEqualTo(EstadoAcademico.CERRADO);
        assertThat(form.getVersion()).isEqualTo(5L);
    }

    private PeriodoAcademicoForm formulario() {
        PeriodoAcademicoForm form = new PeriodoAcademicoForm();
        form.setInstitucionId(1L);
        form.setCicloEscolarId(10L);
        form.setNivelEducativoId(20L);
        form.setCodigo("TRI1");
        form.setNombre("Primer trimestre");
        form.setTipo(TipoPeriodoAcademico.TRIMESTRE);
        form.setOrden(1);
        form.setFechaInicio(LocalDate.of(2026, 8, 1));
        form.setFechaFin(LocalDate.of(2026, 10, 31));
        form.setEstado(EstadoAcademico.ABIERTO);
        form.setObservaciones("Periodo inicial");
        return form;
    }

    private CicloEscolarResponse ciclo(Long id, Long institucionId) {
        return new CicloEscolarResponse(id, institucionId, "2026-2027", "Ciclo 2026-2027",
                LocalDate.of(2026, 8, 1), LocalDate.of(2027, 7, 31),
                EstadoAcademico.ABIERTO, true, auditoria());
    }

    private NivelEducativoResponse nivel(Long id, Long institucionId) {
        return new NivelEducativoResponse(id, institucionId, "PRIM", "Primaria", null,
                1, true, auditoria());
    }

    private AuditoriaResponse auditoria() {
        Instant ahora = Instant.parse("2026-09-11T12:00:00Z");
        return new AuditoriaResponse(ahora, null, ahora, null, 5L);
    }
}
