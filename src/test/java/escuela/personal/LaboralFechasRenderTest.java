package escuela.personal;

import escuela.personal.dto.LaboralForm;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class LaboralFechasRenderTest {
    @Controller static class Vista extends EmpleadoRenderTest.Vista {
        @GetMapping("/test/laboral-fechas") String fecha(Model m){
            var vista=super.render(true,true,m);var f=new LaboralForm();f.setVersion(2L);f.setSituacion("BAJA");
            f.setPuesto("Secretaria de prueba");f.setFechaIngreso(LocalDate.of(2026,10,1));
            f.setFechaBaja(LocalDate.of(2026,10,20));f.setMotivoBaja("Motivo ficticio");m.addAttribute("laboralForm",f);return vista;
        }
        @PostMapping("/test/laboral-fechas") @ResponseBody String capturar(@ModelAttribute LaboralForm f,BindingResult errores){
            return errores.hasErrors()?"FECHA_INVALIDA":f.getFechaIngreso()+"|"+f.getFechaBaja();
        }
    }
    private org.springframework.test.web.servlet.MockMvc mvc(){
        var r=new ClassLoaderTemplateResolver();r.setPrefix("templates/");r.setSuffix(".html");r.setCharacterEncoding("UTF-8");
        var e=new SpringTemplateEngine();e.setTemplateResolver(r);var v=new ThymeleafViewResolver();v.setTemplateEngine(e);v.setCharacterEncoding("UTF-8");
        return MockMvcBuilders.standaloneSetup(new Vista()).setViewResolvers(v).build();
    }
    @Test void ambasFechasGuardadasSeRenderizanComoIsoEnEspanolEIngles()throws Exception{
        var mvc=mvc();
        for(var locale:List.of(Locale.forLanguageTag("es-MX"),Locale.US)){
            var respuesta=mvc.perform(get("/test/laboral-fechas").locale(locale)).andReturn().getResponse();assertThat(respuesta.getStatus()).isEqualTo(200);
            var html=respuesta.getContentAsString();
            assertThat(Pattern.compile("<input[^>]*name=\"fechaIngreso\"[^>]*value=\"2026-10-01\"").matcher(html).find()).isTrue();
            assertThat(Pattern.compile("<input[^>]*name=\"fechaBaja\"[^>]*value=\"2026-10-20\"").matcher(html).find()).isTrue();
        }
    }
    @Test void capturaIsoConservaDiaYMesYPermiteBajaVacia()throws Exception{
        var mvc=mvc();
        for(var locale:List.of(Locale.forLanguageTag("es-MX"),Locale.US)){
            var completa=mvc.perform(post("/test/laboral-fechas").locale(locale).param("fechaIngreso","2026-10-01").param("fechaBaja","2026-10-20")).andReturn().getResponse();
            assertThat(completa.getContentAsString()).isEqualTo("2026-10-01|2026-10-20");
            var sinBaja=mvc.perform(post("/test/laboral-fechas").locale(locale).param("fechaIngreso","2026-10-01").param("fechaBaja","")).andReturn().getResponse();
            assertThat(sinBaja.getContentAsString()).isEqualTo("2026-10-01|null");
        }
    }
}
