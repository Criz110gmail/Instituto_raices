package escuela.admin.controller;
import escuela.common.support.FormatoMoneda;
import escuela.finanzas.dto.response.CoberturaAnualPagoResponse;
import java.math.BigDecimal;
import java.util.List;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.entity.EstadoPago;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
class CoberturaAnualPagoRenderTest {
 private org.springframework.test.web.servlet.MockMvc mvc()throws Exception{
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
  var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
  var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(views).build();var ctx=mvc.getDispatcherServlet().getWebApplicationContext();var add=ctx.getClass().getDeclaredMethod("addBean",String.class,Object.class);add.setAccessible(true);add.invoke(ctx,"formatoMoneda",new FormatoMoneda());
  return mvc;
 }
 @Test void tarjetaDistinguePendienteLiquidadoYRevertidoSinFingirDoceAbonos()throws Exception{
  var mvc=mvc();
  for(String estado:List.of("LIQUIDADO","PENDIENTE","NO_VIGENTE","NO_LIQUIDADO")){
   var r=mvc.perform(get("/prueba/cobertura-anual").param("estado",estado)).andReturn().getResponse();
   assertThat(r.getStatus()).isEqualTo(200);assertThat(r.getContentAsString()).contains("Año completo 2027 · 12/12 meses","Meses pagados completos","Meses bonificados pactados","Beneficio anual pactado","$1,000.00");
   assertThat(r.getContentAsString()).contains(switch(estado){case "LIQUIDADO"->"Anual liquidado";case "PENDIENTE"->"Anual pendiente de validación";case "NO_VIGENTE"->"Anual cancelado o rechazado";default->"Anual no liquidado por este pago";});
  }
 }
 @Test void pestanaPanelYAccesoDeResumenExistenSoloConCoberturaAnual()throws Exception{
  var mvc=mvc();
  for(boolean tiene:List.of(true,false)){
   var nav=mvc.perform(get("/prueba/cobertura-anual").param("estado","LIQUIDADO").param("vista","nav").param("tiene",String.valueOf(tiene))).andReturn().getResponse().getContentAsString();
   var panel=mvc.perform(get("/prueba/cobertura-anual").param("estado","LIQUIDADO").param("vista","panel").param("tiene",String.valueOf(tiene))).andReturn().getResponse().getContentAsString();
   var summary=mvc.perform(get("/prueba/cobertura-anual").param("estado","LIQUIDADO").param("vista","summary").param("tiene",String.valueOf(tiene))).andReturn().getResponse().getContentAsString();
   if(tiene){assertThat(nav).contains("tab-pago-anual","Cobertura anual","#cobertura-anual-pago");assertThat(panel).contains("panel-pago-anual","data-student-panel=\"anual\"","Meses pagados completos");assertThat(summary).contains("data-open-student-tab=\"anual\"","data-focus-student-tab=\"true\"","Año completo 2027");}
   else{assertThat(nav).doesNotContain("tab-pago-anual");assertThat(panel).doesNotContain("panel-pago-anual");assertThat(summary).doesNotContain("data-open-student-tab");}
  }
 }
 @Test void expedienteYAcuerdoQuedanDentroDeResumenSinDuplicarElImporteDelEncabezado()throws Exception{
  var mvc=mvc();
  for(var estado:EstadoPago.values()){
   for(boolean acuerdo:List.of(true,false)){
    var summary=mvc.perform(get("/prueba/cobertura-anual").param("estado","PENDIENTE").param("vista","expediente").param("estadoPago",estado.name()).param("acuerdo",String.valueOf(acuerdo))).andReturn().getResponse();
    assertThat(summary.getStatus()).isEqualTo(200);
    var html=summary.getContentAsString();
    assertThat(html).contains("panel-pago-resumen","Expediente financiero","Resumen del pago","Tutor · Plantel","Cuenta declarada","Referencia").doesNotContain("payment-identity-amount");
    if(acuerdo)assertThat(html).contains("Pago completo de un acuerdo anticipado","Ver acuerdo y mensualidades");
    else assertThat(html).doesNotContain("Pago completo de un acuerdo anticipado");
    var identity=mvc.perform(get("/prueba/cobertura-anual").param("estado","PENDIENTE").param("vista","identity").param("estadoPago",estado.name())).andReturn().getResponse().getContentAsString();
    assertThat(identity).contains("PAG-PRUEBA","Importe recibido registrado","$11,000.00").doesNotContain("Expediente financiero","Ver acuerdo","<p");
   }
  }
  var source=new String(getClass().getResourceAsStream("/templates/admin/pago-detalle.html").readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);
  var beforeNav=source.substring(0,source.indexOf("<nav th:fragment=\"paymentNavigation\""));
  assertThat(beforeNav).doesNotContain("Expediente financiero","Pago completo de un acuerdo anticipado");
 }
 @Test void avisoDeRepeticionSeRenderizaComoEstadoInformativoSoloCuandoExiste()throws Exception{
  var mvc=mvc();
  var con=mvc.perform(get("/prueba/cobertura-anual").param("estado","LIQUIDADO").param("vista","notice")).andReturn().getResponse().getContentAsString();
  assertThat(con).contains("payment-repeat-notice","role=\"status\"","Este pago ya estaba validado","No se realizó ningún movimiento adicional.");
  var sin=mvc.perform(get("/prueba/cobertura-anual").param("estado","LIQUIDADO").param("vista","notice").param("tiene","false")).andReturn().getResponse().getContentAsString();
  assertThat(sin).doesNotContain("payment-repeat-notice");
 }
 @Controller static class Pantalla{
  @GetMapping("/prueba/cobertura-anual")String mostrar(@RequestParam String estado,@RequestParam(defaultValue="card")String vista,@RequestParam(defaultValue="true")boolean tiene,@RequestParam(defaultValue="VALIDADO")EstadoPago estadoPago,@RequestParam(defaultValue="false")boolean acuerdo,Model model){
   model.addAttribute("coberturasAnuales",tiene?List.of(new CoberturaAnualPagoResponse(1L,2L,"Alumno","A-01","Colegiatura",2027,12,11,1,new BigDecimal("1000"),new BigDecimal("11000"),"MXN",estado)):List.of());
   var pago=mock(PagoResponse.class);when(pago.estado()).thenReturn(estadoPago);
   when(pago.folio()).thenReturn("PAG-PRUEBA");when(pago.monto()).thenReturn(new BigDecimal("11000"));
   when(pago.tutorNombre()).thenReturn("Tutor");when(pago.plantelRegistroNombre()).thenReturn("Plantel");
   when(pago.metodo()).thenReturn(escuela.finanzas.entity.MetodoPago.TARJETA);
   when(pago.origenRegistro()).thenReturn(escuela.finanzas.entity.OrigenRegistroPago.ADMINISTRACION);
   model.addAttribute("pago",pago);
   if(tiene)model.addAttribute("avisoOperacion","Este pago ya fue validado desde otra pestaña o por otro usuario. No se realizó ningún movimiento adicional.");
   if(acuerdo)model.addAttribute("anticipo",new escuela.cobranza.service.AcuerdoAnticipadoService.Vista(1L,0L,"ANT-PRUEBA","Tutor","Plantel","MXN",java.time.LocalDate.of(2027,1,1),"CONSERVAR","MENSUALIDAD","Prueba","Admin","APLICADO",new BigDecimal("12000"),new BigDecimal("1000"),new BigDecimal("11000"),List.of(),1L,"PAG-PRUEBA",null,1L,1L,"Diciembre bonificado",false));
   return "admin/pago-detalle :: "+switch(vista){case "notice"->"repeatedValidationNotice";case "nav"->"paymentNavigation";case "panel"->"annualCoveragePanel";case "summary"->"annualSummary";case "expediente"->"paymentSummary";case "identity"->"paymentIdentity";default->"annualCoverage";};
  }
 }
}
