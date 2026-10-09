package escuela.admin.controller;

import escuela.admin.dto.SaldoFavorForm;
import escuela.finanzas.entity.*;
import escuela.finanzas.mapper.PagoMapper;
import escuela.finanzas.service.SaldoFavorService;
import escuela.institucion.entity.*;
import escuela.tutor.entity.Tutor;
import escuela.common.support.FormatoMoneda;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.data.domain.PageImpl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class SaldoFavorRenderTest {
    @Test void renderizaFormularioHistorialYValidacionConElTemaCompartido() throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new PantallaPrueba()).setViewResolvers(views).build();
        var context=mvc.getDispatcherServlet().getWebApplicationContext();
        var registrar=context.getClass().getDeclaredMethod("addBean",String.class,Object.class);
        registrar.setAccessible(true);registrar.invoke(context,"formatoMoneda",new FormatoMoneda());
        var response=mvc.perform(get("/prueba/saldo")).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains("/css/forms.css","Saldo a favor del tutor","$100.00","Historial de aplicaciones",
            "data-money-confirm=\"saldo-favor\"","data-money-confirm=\"saldo-favor-reversion\"","Exportar Excel","Revertir aplicación","Administración",
            "credit-management-card","form-grid credit-search-grid","autocomplete-label","aria-describedby=\"credit-status\"");
        var validacion=mvc.perform(get("/prueba/validacion")).andReturn().getResponse();
        assertThat(validacion.getStatus()).isEqualTo(200);
        assertThat(validacion.getContentAsString()).contains("received-fields","Importe realmente recibido","Saldo a favor estimado","name=\"montoRecibido\"","hidden");
        var distribucion=mvc.perform(get("/prueba/distribucion")).andReturn().getResponse();
        assertThat(distribucion.getStatus()).isEqualTo(200);
        assertThat(distribucion.getContentAsString()).contains("payment-credit-card","payment-credit-amount","payment-credit-action","Gestionar saldo a favor e historial");
        try(var css=getClass().getResourceAsStream("/static/css/forms.css")) {
            assertThat(new String(css.readAllBytes(),java.nio.charset.StandardCharsets.UTF_8))
                .contains(".saldo-row input.money-entry",".credit-management-card .saldo-row .money-entry:focus");
        }
    }
    @Controller static class PantallaPrueba {
        @GetMapping({"/prueba/saldo","/prueba/validacion","/prueba/distribucion"}) String mostrar(jakarta.servlet.http.HttpServletRequest request,@ModelAttribute("form")SaldoFavorForm f,Model m) {
            var inst=new Institucion();inst.setId(1L);inst.setNombre("Escuela");
            var plantel=new Plantel();plantel.setId(2L);plantel.setNombre("Centro");
            var tutor=new Tutor();tutor.setId(3L);tutor.setNombres("Familia");
            var p=new Pago();p.setId(50L);p.setInstitucion(inst);p.setPlantelRegistro(plantel);p.setTutor(tutor);p.setFolio("PAG-TEST");
            p.setMonto(new BigDecimal("100"));p.setMoneda("MXN");p.setVersion(0L);p.setFechaPago(Instant.now());p.setMetodo(MetodoPago.TRANSFERENCIA);
            boolean validacion=request.getRequestURI().endsWith("validacion");p.setEstado(validacion?EstadoPago.PENDIENTE_VALIDACION:EstadoPago.VALIDADO);
            m.addAttribute("pago",new PagoMapper().respuesta(p));f.setVersion(0L);m.addAttribute("usuarioSesion","Administración");
            m.addAttribute("puedeAplicar",true);m.addAttribute("puedeRevertir",true);m.addAttribute("operacion","");m.addAttribute("puedeValidar",true);
            m.addAttribute("accesoRecuperacion",false);m.addAttribute("puedeCancelar",false);m.addAttribute("puedeDevolver",false);
            m.addAttribute("puedeSaldoFavor",true);
            m.addAttribute("resumenDevolucion",new escuela.finanzas.dto.response.ResumenDevolucionPagoResponse(p.getMonto(),BigDecimal.ZERO,BigDecimal.ZERO,p.getMonto(),List.of(),List.of()));
            m.addAttribute("historial",new PageImpl<>(List.of(new SaldoFavorService.DatoAplicacion(9L,"Hijo 2","Mensualidad",new BigDecimal("40"),"MXN","09/10/2026 10:00","APLICAR","Aplicar al hermano","Administración",true))));
            return request.getRequestURI().endsWith("saldo")?"admin/saldo-favor-form":"admin/pago-detalle";
        }
    }
}
