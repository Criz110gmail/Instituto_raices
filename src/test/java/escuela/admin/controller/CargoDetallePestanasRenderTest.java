package escuela.admin.controller;

import escuela.admin.dto.AjusteCargoForm;
import escuela.cobranza.dto.response.CargoResponse;
import escuela.cobranza.entity.*;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.support.FormatoMoneda;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class CargoDetallePestanasRenderTest {
    @Controller static class Vista {
        final StaticApplicationContext app=new StaticApplicationContext();
        Vista(){app.getBeanFactory().registerSingleton("formatoMoneda",new FormatoMoneda());}
        @GetMapping("/test/cargo") String render(@RequestParam(defaultValue="EMITIDO") String estado,Model m){
            var f=LocalDate.of(2026,10,7);var zero=new BigDecimal("0.00");
            m.addAttribute("cargo",new CargoResponse(1L,2L,3L,4L,"Centro",5L,"A-1","Ana Prueba","INS-1",
                    6L,"TEST","Prueba",7L,"UNICO","Cuota prueba",f,f,null,null,f,null,f,
                    new BigDecimal("400.00"),"MXN",EstadoRegistroCargo.valueOf(estado),null,null,
                    zero,zero,new BigDecimal("400.00"),new BigDecimal("300.00"),new BigDecimal("100.00"),
                    SituacionCobro.PARCIAL,false,new AuditoriaResponse(Instant.now(),1L,Instant.now(),1L,0L),null,null));
            var form=new AjusteCargoForm();form.setMonto(new BigDecimal("100.00"));form.setMotivo("Prueba <script> literal");
            m.addAttribute("ajusteForm",form);m.addAttribute("tiposAjuste",new TipoAjusteCargo[]{TipoAjusteCargo.DESCUENTO,TipoAjusteCargo.RECARGO,TipoAjusteCargo.CORRECCION});
            m.addAttribute("efectosAjuste",EfectoAjusteCargo.values());m.addAttribute("ajustes",List.of());m.addAttribute("pagosPendientes",List.of());
            m.addAttribute("puedeRegistrarPago",true);m.addAttribute("puedeCorregirCargo",true);m.addAttribute("puedeValidarPago",true);
            m.addAttribute("usuarioSesion","admin");m.addAttribute("pestanaActiva","ajustes");
            m.addAttribute(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,new ThymeleafEvaluationContext(app,null));
            return "admin/cargo-detalle";
        }
    }
    String render(String state) throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new Vista()).setViewResolvers(views).build();
        // MockMvc usa un contexto stub de clase interna: registrar el bean de presentación
        // aquí permite renderizar el HTML real sin levantar la aplicación ni la base.
        var context=mvc.getDispatcherServlet().getWebApplicationContext();
        var addBean=context.getClass().getDeclaredMethod("addBean",String.class,Object.class);
        addBean.setAccessible(true);addBean.invoke(context,"formatoMoneda",new FormatoMoneda());
        return mvc.perform(get("/test/cargo").param("estado",state)).andReturn().getResponse().getContentAsString();
    }
    @Test void cincoPestanasDatosPersistentesCapturaYVistaPrevia() throws Exception {
        String html=render("EMITIDO");
        for(String id:List.of("resumen","transferencias","ajustes","historial","gestion"))
            assertThat(html).contains("id=\"tab-cargo-"+id+"\"","id=\"panel-cargo-"+id+"\"");
        assertThat(html).contains("Ana Prueba","Saldo pendiente actual","$100.00","data-force-active-tab=\"ajustes\"",
                "data-charge-total=\"400.00\"","data-charge-paid=\"300.00\"","Así quedaría el cargo","charge-preview-balance",
                "Sin transferencias por revisar","type=\"text\"","data-money","Cancelar cargo al alumno","&lt;script&gt;");
    }
    @Test void canceladoNoPermiteCapturarNuevoAjusteNiRegistrarPago() throws Exception {
        assertThat(render("CANCELADO")).contains("Este cargo no admite nuevos ajustes","Cancelación")
                .doesNotContain("id=\"charge-adjustment-form\"","Registrar pago <span","Cancelar cargo al alumno</button>");
    }
}
