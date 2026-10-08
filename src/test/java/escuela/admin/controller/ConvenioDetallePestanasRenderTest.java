package escuela.admin.controller;
import escuela.cobranza.dto.*;
import escuela.cobranza.entity.EstadoConvenioPago;
import escuela.common.support.FormatoMoneda;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
class ConvenioDetallePestanasRenderTest {
 @Controller static class Vista {
  final StaticApplicationContext app=new StaticApplicationContext();
  Vista(){app.getBeanFactory().registerSingleton("formatoMoneda",new FormatoMoneda());}
  @GetMapping("/test/convenio") String render(@RequestParam(defaultValue="VIGENTE") String estado,Model m){
   var fecha=LocalDate.of(2026,10,8);
   m.addAttribute("detalle",new ConvenioPagoDetalle(1L,0L,"CV-TEST","Escuela","Tutor Prueba",fecha,fecha.plusDays(12),
     "Convenio prueba","Motivo del acuerdo","Liquidar antes del vencimiento","MXN",new BigDecimal("1000.00"),new BigDecimal("800.00"),new BigDecimal("200.00"),
     EstadoConvenioPago.valueOf(estado),estado,"Cancelación <script> literal",
     List.of(new CargoConvenioOpcion(10L,1L,"Ana <script>","A-1","Original","Original",fecha.plusDays(12),new BigDecimal("600"),BigDecimal.ZERO,new BigDecimal("600"),"MXN"),
             new CargoConvenioOpcion(11L,2L,"Luis","A-2","Original","Original",fecha.plusDays(12),new BigDecimal("400"),BigDecimal.ZERO,new BigDecimal("400"),"MXN")),
     List.of(new CargoConvenioOpcion(12L,1L,"Ana <script>","A-1","Convenio","Acuerdo",fecha.plusDays(12),new BigDecimal("480"),BigDecimal.ZERO,new BigDecimal("480"),"MXN"),
             new CargoConvenioOpcion(13L,2L,"Luis","A-2","Convenio","Acuerdo",fecha.plusDays(12),new BigDecimal("320"),BigDecimal.ZERO,new BigDecimal("320"),"MXN"))));
   var form=new CancelarConvenioForm();form.setVersion(0L);form.setMotivo("Motivo <script> literal");
   m.addAttribute("cancelacion",form);m.addAttribute("puedeAdministrar",true);m.addAttribute("pestanaActiva","gestion");
   m.addAttribute("usuarioSesion","admin");
   m.addAttribute(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,new ThymeleafEvaluationContext(app,null));
   return "admin/convenio-pago-detalle";
  }
 }
 String render(String estado)throws Exception{
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
  var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
  var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
  var mvc=MockMvcBuilders.standaloneSetup(new Vista()).setViewResolvers(views).build();
  var context=mvc.getDispatcherServlet().getWebApplicationContext();
  var addBean=context.getClass().getDeclaredMethod("addBean",String.class,Object.class);addBean.setAccessible(true);
  addBean.invoke(context,"formatoMoneda",new FormatoMoneda());
  return mvc.perform(get("/test/convenio").param("estado",estado)).andReturn().getResponse().getContentAsString();
 }
 @Test void tresPestaniasYFormularioConEstiloComun()throws Exception{
  String html=render("VIGENTE");
  for(String tab:List.of("resumen","adeudos","gestion"))assertThat(html).contains("id=\"tab-convenio-"+tab+"\"","id=\"panel-convenio-"+tab+"\"");
  assertThat(html).contains("$1,000.00","$800.00","$200.00","data-force-active-tab=\"gestion\"",
    "class=\"entity-form\"","class=\"form-grid\"","class=\"wide\"","required","Cancelar y reactivar adeudos","&lt;script&gt;",
    "Adeudos originales conservados","Nuevos cargos por alumno","/js/student-tabs.js");
 }
 @Test void canceladoConservaMotivoSinOtraAccion()throws Exception{
  assertThat(render("CANCELADO")).contains("Convenio cancelado","Cancelación &lt;script&gt; literal","Los adeudos originales se reactivaron")
   .doesNotContain("Cancelar y reactivar adeudos</button>");
 }
 @Test void cancelacionTieneModalConDatosEscapadosYPostOriginal()throws Exception{
  assertThat(render("VIGENTE")).contains("data-money-confirm=\"cancelacion-convenio\"",
   "data-confirm-folio=\"CV-TEST\"", "data-confirm-original=\"1000.00\"", "data-confirm-amount=\"800.00\"",
   "data-convenio-reactivar", "data-convenio-cancelar", "data-amount=\"600\"", "data-amount=\"480\"",
   "data-label=\"#10 · Ana &lt;script&gt; · Original\"", "method=\"post\"", "action=\"/admin/convenios-pago/1/cancelar\"",
   "name=\"version\"", "/js/money-input.js", "/css/money-input.css");
  assertThat(render("CANCELADO")).doesNotContain("data-money-confirm=\"cancelacion-convenio\"");
 }
}
