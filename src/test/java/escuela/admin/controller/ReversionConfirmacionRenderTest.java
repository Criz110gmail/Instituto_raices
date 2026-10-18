package escuela.admin.controller;

import escuela.admin.dto.ReversionFinancieraForm;
import escuela.common.support.FormatoMoneda;
import escuela.finanzas.dto.response.ObjetivoReversionResponse;
import escuela.finanzas.entity.TipoReversionFinanciera;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.*;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class ReversionConfirmacionRenderTest {
 private String render(boolean transferencia,boolean revertido) throws Exception {
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
  var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
  var vistas=new ThymeleafViewResolver();vistas.setTemplateEngine(engine);vistas.setCharacterEncoding("UTF-8");
  var mvc=MockMvcBuilders.standaloneSetup(new Pantalla(transferencia,revertido)).setViewResolvers(vistas).build();
  var contexto=mvc.getDispatcherServlet().getWebApplicationContext();var add=contexto.getClass().getDeclaredMethod("addBean",String.class,Object.class);
  add.setAccessible(true);add.invoke(contexto,"formatoMoneda",new FormatoMoneda());
  var response=mvc.perform(get("/qa/reversion")).andReturn().getResponse();assertThat(response.getStatus()).isEqualTo(200);
  return response.getContentAsString();
 }
 @Test void transferenciaIncluyeModalDatosSegurosYPostOriginal() throws Exception {
  var html=render(true,false);
  assertThat(html).contains("data-money-confirm=\"reversion-financiera\"","data-confirm-transfer=\"true\"",
   "data-confirm-id=\"1\"","data-confirm-amount=\"300.00\"","data-confirm-currency=\"MXN\"",
   "action=\"/admin/reversiones/transferencias/1\"","name=\"claveIdempotencia\"","name=\"version\"",
   "class=\"detail-close-button\" href=\"/admin/movimientos-financieros\">Cancelar",
   "/js/money-input.js?v=20261017-reversion","Origen &lt;prueba&gt; → Destino ficticio");
  Path dir=Path.of("target/qa-reversion");Files.createDirectories(dir);Files.writeString(dir.resolve("formulario.html"),html);
 }
 @Test void operacionManualConservaSuAccionYResumen() throws Exception {
  assertThat(render(false,false)).contains("data-confirm-transfer=\"false\"","action=\"/admin/reversiones/movimientos/1\"","Confirmar reversa trazable");
 }
 @Test void revertidaNoOfreceFormularioNiSegundaConfirmacion() throws Exception {
  assertThat(render(true,true)).contains("Esta operación ya fue revertida").doesNotContain("data-money-confirm=\"reversion-financiera\"","Confirmar reversa trazable");
 }
 @Controller static class Pantalla {
  private final boolean transferencia,revertido;
  Pantalla(boolean transferencia,boolean revertido){this.transferencia=transferencia;this.revertido=revertido;}
  @GetMapping("/qa/reversion") String vista(Model model) {
   var form=new ReversionFinancieraForm();form.setVersion(0L);form.setFecha(LocalDateTime.of(2026,10,17,16,30));
   form.setMotivo("Prueba ficticia: conservar historial, sin operación real.");
   model.addAttribute("form",form);model.addAttribute("usuarioSesion","usuario.prueba");model.addAttribute("esTransferencia",transferencia);
   model.addAttribute("fechaOriginalLocal","16 oct 2026 · 20:01");
   model.addAttribute("objetivo",new ObjetivoReversionResponse(transferencia?TipoReversionFinanciera.TRANSFERENCIA:TipoReversionFinanciera.MOVIMIENTO_MANUAL,
    1L,1L,"Transferencia entre cuentas","Origen <prueba> → Destino ficticio",new BigDecimal("300.00"),"MXN",Instant.parse("2026-10-17T02:01:00Z"),0L,revertido));
   return "admin/reversion-financiera-form";
  }
 }
}
