package escuela.admin.support;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.assertThat;
import escuela.admin.controller.PoliticaRecargoAdminController;
import escuela.cobranza.service.*;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
class PoliticaRecargoClaridadTest {
    @Test void elHtmlRenderizadoExponeLosCamposQueLeeElEjemplo() throws Exception {
        var instituciones=mock(InstitucionService.class);
        var alcance=mock(AlcanceDatosService.class);
        when(instituciones.listar()).thenReturn(List.of());
        when(alcance.filtrarInstituciones(anyList())).thenReturn(List.of());
        var templates=new ClassLoaderTemplateResolver(); templates.setPrefix("templates/"); templates.setSuffix(".html");
        var engine=new SpringTemplateEngine(); engine.setTemplateResolver(templates);
        var views=new ThymeleafViewResolver(); views.setTemplateEngine(engine); views.setCharacterEncoding("UTF-8");
        var controller=new PoliticaRecargoAdminController(mock(PoliticaRecargoService.class),mock(ConceptoCobroService.class),
                instituciones,mock(PlantelService.class),alcance);
        var mvc=MockMvcBuilders.standaloneSetup(controller).setViewResolvers(views).build();
        var response=mvc.perform(get("/admin/politicas-recargo/nuevo")).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains("id=\"porcentaje\"", "id=\"montoFijo\"", "id=\"periodicidad\"",
                "id=\"valorLimite\"", "recargo-form.js?v=20261006-2", "SIMULACIÓN", "Actualizar ejemplo");
    }
    @Test void laGuiaUsaElNuevoVocabularioSinRegistrarOperaciones() throws Exception {
        String sql=Files.readString(Path.of("src/main/resources/db/migration/V58__actualizar_guia_tope_recargos.sql"));
        assertThat(sql).contains("Tope de recargos", "Sin tope adicional", "Política habilitada", "version_contenido=2")
                .doesNotContain("INSERT INTO pago", "UPDATE cargo", "UPDATE politica_recargo");
    }
    @Test void separaTopeActivacionYEjemploSinDatosOperativos() throws Exception {
        String html=Files.readString(Path.of("src/main/resources/templates/admin/politica-recargo-form.html"));
        assertThat(html).contains("¿Cuánto puede acumularse en recargos?", "Sin tope adicional",
                "Hasta un monto máximo", "Hasta un porcentaje del cargo original", "Política habilitada",
                "Guardar esta política no agrega recargos", "no genera ajustes", "Deuda sin pagos",
                "th:field=\"*{tipoLimite}\"", "th:field=\"*{valorLimite}\"")
                .doesNotContain("<legend>Límite y operación</legend>","<span>Tipo de límite *</span>","<span>Valor del límite *</span>",
                        "name=\"ejemplo-original\"", "th:field=\"*{ejemploOriginal}\"");
    }
}
