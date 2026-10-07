package escuela.admin.controller;
import escuela.cobranza.dto.response.CargoResponse;
import escuela.cobranza.service.*;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.support.FormatoMoneda;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
class TransferenciaVencidaRenderTest {
    @Test void usaFormularioComunYConservaErroresSinAutorizar() throws Exception {
        var cargos=mock(CargoService.class);var transferencias=mock(TransferenciaVencidaService.class);
        var cargo=mock(CargoResponse.class);when(cargo.id()).thenReturn(19L);
        when(cargo.alumnoNombre()).thenReturn("Alumno prueba");when(cargo.conceptoNombre()).thenReturn("Cuota");
        when(cargo.saldoPendiente()).thenReturn(new BigDecimal("850.00"));when(cargo.moneda()).thenReturn("MXN");
        when(cargo.fechaVencimiento()).thenReturn(LocalDate.of(2026,10,5));
        when(cargo.auditoria()).thenReturn(new AuditoriaResponse(null,1L,null,1L,0L));when(cargos.obtener(19L)).thenReturn(cargo);
        when(transferencias.consultar(19L)).thenReturn(new TransferenciaVencidaService.Configuracion(false,true));
        var templates=new ClassLoaderTemplateResolver();templates.setPrefix("templates/");templates.setSuffix(".html");templates.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(templates);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new TransferenciaVencidaAdminController(cargos,transferencias,mock(AlcanceDatosService.class))).setViewResolvers(views).build();
        var contexto=mvc.getDispatcherServlet().getWebApplicationContext();var registrar=contexto.getClass().getDeclaredMethod("addBean",String.class,Object.class);registrar.setAccessible(true);registrar.invoke(contexto,"formatoMoneda",new FormatoMoneda());
        var html=mvc.perform(get("/admin/cargos/19/transferencia-vencida")).andReturn().getResponse().getContentAsString();
        assertThat(html).contains("$850.00","05/10/2026","form-section","policy-card","entity-form","Habilitada","name=\"permitir\"","action=\"/admin/cargos/19/transferencia-vencida\"");
        var error=mvc.perform(post("/admin/cargos/19/transferencia-vencida").param("version","0").param("permitir","true").param("motivo"," ")).andReturn().getResponse().getContentAsString();
        assertThat(error).contains("field-error");verify(transferencias,never()).configurar(any(),any(),anyBoolean(),any());
    }
}
