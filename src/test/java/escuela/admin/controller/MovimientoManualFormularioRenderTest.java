package escuela.admin.controller;
import escuela.finanzas.service.*;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
class MovimientoManualFormularioRenderTest {
    @Test void formularioUsaBuscadorModalYCancelarConDisenoSinCargarTodoElCatalogo()throws Exception {
        var motivos=mock(MotivoFinancieroService.class);
        var controller=new MovimientoManualAdminController(mock(MovimientoManualService.class),motivos,mock(InstitucionService.class),mock(PlantelService.class),mock(AlcanceDatosService.class));
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(controller).setViewResolvers(views).build();
        var r=mvc.perform(get("/admin/movimientos-financieros/nuevo")).andReturn().getResponse();
        assertThat(r.getStatus()).isEqualTo(200);
        assertThat(r.getContentAsString()).contains("data-money-confirm=\"movimiento-manual\"","/admin/autocompletado/motivos-movimiento","data-extra-input=\"direccion\"","id=\"motivoTexto\"","class=\"detail-close-button\"")
            .doesNotContain("<select id=\"motivoFinancieroId\"");
        verify(motivos,never()).listarActivos(any());
    }
}
