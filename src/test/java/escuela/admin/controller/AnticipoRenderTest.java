package escuela.admin.controller;
import escuela.cobranza.dto.AnticipoForm;
import escuela.cobranza.service.AcuerdoAnticipadoService.*;
import escuela.common.support.FormatoMoneda;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.springframework.data.domain.PageImpl;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
class AnticipoRenderTest {
 @Test void renderizaFormularioPreviaDetalleYFiltrosConTemaYConfirmaciones()throws Exception {
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(views).build();var context=mvc.getDispatcherServlet().getWebApplicationContext();var registrar=context.getClass().getDeclaredMethod("addBean",String.class,Object.class);registrar.setAccessible(true);registrar.invoke(context,"formatoMoneda",new FormatoMoneda());
  var form=mvc.perform(get("/prueba/anticipo/form")).andReturn().getResponse();assertThat(form.getStatus()).isEqualTo(200);assertThat(form.getContentAsString()).contains("/css/forms.css","form-grid","data-money-confirm=\"anticipo-propuesta\"","$720.00","Conservar beca","Sustituir beca","Guardar propuesta","Visualizar acuerdo","id=\"anticipation-preview-section\"","aria-labelledby=\"anticipation-preview-title\"","class=\"anticipation-row\"","Saldo actual: $800.00");
  var detail=mvc.perform(get("/prueba/anticipo/detalle")).andReturn().getResponse();assertThat(detail.getStatus()).isEqualTo(200);assertThat(detail.getContentAsString()).contains("data-money-confirm=\"anticipo-pago\"","data-money-confirm=\"anticipo-cancelar\"","anticipo-institucion\" value=\"1\"","anticipo-plantel\" value=\"2\"","anticipo-payment-account","Cuenta donde se recibió el dinero","type=\"text\" name=\"referencia\"");
  var list=mvc.perform(get("/prueba/anticipo/listado")).andReturn().getResponse();assertThat(list.getStatus()).isEqualTo(200);assertThat(list.getContentAsString()).contains("filters agreement-filters","Exportar Excel","Propuesta vencida","Pago completo requerido");
 }
 @Controller static class Pantalla {
  @GetMapping("/prueba/anticipo/{pantalla}")String mostrar(@PathVariable String pantalla,@ModelAttribute("form")AnticipoForm f,Model m){
   var row=new Fila(7L,"Hermano · A-01","Colegiatura","01/10/2026 — 31/10/2026",new BigDecimal("1000"),new BigDecimal("200"),new BigDecimal("800"),new BigDecimal("800"),new BigDecimal("80"),new BigDecimal("720"),"MXN");
   var d=new Vista(8L,0L,"ANT-TEST","Familia","Centro","MXN",LocalDate.now().plusDays(1),"CONSERVAR","PORCENTAJE","Prueba autorizada","Administración","PROPUESTO",new BigDecimal("800"),new BigDecimal("80"),new BigDecimal("720"),List.of(row),null,null,null,1L,2L,"10 % adicional",false);
   f.setInstitucionId(1L);f.setTutorId(3L);f.setFechaLimite(d.limite());f.setTipoBeneficio("PORCENTAJE");f.setValor(new BigDecimal("10"));f.setCargoIds(List.of(7L));f.setMotivo("Prueba autorizada");f.setHuella("prueba");
   m.addAttribute("previa",new VistaPrevia(List.of(row),d.base(),d.beneficio(),d.pagar(),"MXN","prueba"));m.addAttribute("seleccionadas",List.of(row));m.addAttribute("detalle",d);m.addAttribute("instituciones",List.of());m.addAttribute("tutorSeleccionado","Familia");m.addAttribute("modulos",List.of());m.addAttribute("usuarioSesion","Administración");m.addAttribute("puedeRegistrarPago",true);m.addAttribute("institucionId",1L);m.addAttribute("estado","");m.addAttribute("texto","");m.addAttribute("resultado",new PageImpl<>(List.of(d)));
   return pantalla.equals("form")?"admin/anticipo-form":pantalla.equals("detalle")?"admin/anticipo-detalle":"admin/anticipos";
  }
 }
}
