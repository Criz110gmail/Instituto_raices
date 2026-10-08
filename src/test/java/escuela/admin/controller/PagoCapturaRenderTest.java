package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.finanzas.entity.MetodoPago;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class PagoCapturaRenderTest {
    @Test void capturaNormalYRapidaRenderizanOrdenResumenYConfirmacionSinRegistrar() throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new CapturaPrueba()).setViewResolvers(views).build();
        for(String rapido:new String[]{"false","true"}) {
            var response=mvc.perform(get("/prueba/pago-captura").param("rapido",rapido)).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            var html=response.getContentAsString();int posicion=-1;
            for(String seccion:new String[]{"1 · Origen y responsable","2 · Adeudos que se van a pagar","3 · Datos del pago","4 · Comprobantes y observaciones","5 · Revisa y registra"}) {
                assertThat(html.indexOf(seccion)).isGreaterThan(posicion);posicion=html.indexOf(seccion);
            }
            assertThat(html).contains("payment-review-charges","payment-review-account","allow-unassigned",
                    "data-money-confirm=\"registro-pago\"","action=\"/admin/pagos\"","multipart/form-data",
                    "Registrar como pendiente abrirá un modal","data-fixed-charge=\""+rapido+"\"");
        }
    }
    @Controller static class CapturaPrueba {
        @GetMapping("/prueba/pago-captura") String mostrar(@RequestParam boolean rapido,@ModelAttribute("form") PagoForm f,Model model) {
            f.setMoneda("MXN");f.setMetodo(MetodoPago.EFECTIVO);f.setMonto(new BigDecimal("500"));
            f.setTutorId(2L);f.setTutorEtiqueta("Tutor de prueba");
            var s=new SolicitudAplicacionPagoForm();s.setCargoId(1L);s.setCargoEtiqueta("Alumno · cuota");
            s.setMontoSolicitado(new BigDecimal("500"));s.setSaldoReferencia(new BigDecimal("500"));f.getSolicitudes().add(s);
            model.addAttribute("pagoRapido",rapido);model.addAttribute("instituciones",List.of());
            model.addAttribute("planteles",List.of());model.addAttribute("metodos",MetodoPago.values());model.addAttribute("usuarioSesion","prueba");
            return "admin/pago-form";
        }
    }
}
