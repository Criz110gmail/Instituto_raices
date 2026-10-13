package escuela.admin.controller;

import escuela.cobranza.service.AcuerdoAnticipadoService.*;
import escuela.common.support.FormatoMoneda;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.dto.response.AplicacionPagoResponse;
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

class PagoBonificadaRenderTest {
 @Test void muestraAmbosMesesSinInventarUnAbonoParaElBonificadoYConservaHistoria()throws Exception {
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
  var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
  var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(views).build();
  var context=mvc.getDispatcherServlet().getWebApplicationContext();var add=context.getClass().getDeclaredMethod("addBean",String.class,Object.class);add.setAccessible(true);add.invoke(context,"formatoMoneda",new FormatoMoneda());
  for(String estado:new String[]{"APLICADO","CANCELADO"}) {
   var response=mvc.perform(get("/prueba/bonificada").param("estado",estado)).andReturn().getResponse();
   assertThat(response.getStatus()).isEqualTo(200);
   assertThat(response.getContentAsString()).contains("Noviembre","Diciembre","$800.00","$0.00","Importe acordado de este mes","No representa un ingreso adicional","Meses incluidos en este pago");
   if(estado.equals("APLICADO")){
    assertThat(response.getContentAsString()).contains("Bonificada, sin abono","data-bonified-month","Importe pagado por este mes","No se recibió dinero por este mes");
    var lista=response.getContentAsString().substring(response.getContentAsString().indexOf("requested-list applied-list"));
    lista=lista.substring(0,lista.indexOf("distribution-empty")<0?lista.indexOf("detail-totals"):lista.indexOf("distribution-empty"));
    assertThat(java.util.regex.Pattern.compile("<article(?:\\s|>)").matcher(lista).results().count()).isEqualTo(2);
   }
   else assertThat(response.getContentAsString()).contains("Bonificación pactada, sin abono","El acuerdo no está aplicado actualmente");
  }
 }
 @Controller static class Pantalla {
  @GetMapping("/prueba/bonificada")String mostrar(@RequestParam String estado,Model m){
   BigDecimal mil=new BigDecimal("1000"),beca=new BigDecimal("200"),base=new BigDecimal("800");
   var filas=List.of(new Fila(1L,"Alumno","Colegiatura","Noviembre",mil,beca,base,base,BigDecimal.ZERO,base,"MXN"),new Fila(2L,"Alumno","Colegiatura","Diciembre",mil,beca,base,base,base,BigDecimal.ZERO,"MXN"));
   m.addAttribute("anticipo",new Vista(1L,0L,"ANT-PRUEBA","Tutor","Plantel","MXN",LocalDate.now().plusDays(1),"CONSERVAR","MENSUALIDAD","Prueba","Admin",estado,new BigDecimal("1600"),base,base,filas,27L,"PAG-PRUEBA",null,1L,1L,"Una mensualidad bonificada",false));
   var pago=mock(PagoResponse.class);
   when(pago.estado()).thenReturn(estado.equals("APLICADO")?EstadoPago.VALIDADO:EstadoPago.CANCELADO);
   when(pago.aplicaciones()).thenReturn(List.of(new AplicacionPagoResponse(1L,1L,"Alumno","A-01","Colegiatura","Noviembre",base,"MXN",null,base,BigDecimal.ZERO)));
   when(pago.montoAplicado()).thenReturn(base);when(pago.montoDevuelto()).thenReturn(BigDecimal.ZERO);when(pago.montoDisponible()).thenReturn(BigDecimal.ZERO);when(pago.moneda()).thenReturn("MXN");
   m.addAttribute("pago",pago);m.addAttribute("puedeSaldoFavor",false);
   return "admin/pago-detalle :: paymentDistribution";
  }
 }
}
