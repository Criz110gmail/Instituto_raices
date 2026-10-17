package escuela.admin.controller;

import escuela.admin.dto.TransferenciaCuentaForm;
import escuela.institucion.dto.response.InstitucionResponse;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
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

class TransferenciaConfirmacionRenderTest {
 @Test void formularioRealIncluyeResumenCancelacionConDisenoYPostOriginal() throws Exception {
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
  var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
  var vistas=new ThymeleafViewResolver();vistas.setTemplateEngine(engine);vistas.setCharacterEncoding("UTF-8");
  var form=new TransferenciaCuentaForm();
  form.setInstitucionId(1L);
  form.setCuentaOrigenId(4L);form.setCuentaOrigenTexto("BANCO-COMPRAS-TEST · Cuenta ficticia");
  form.setCuentaDestinoId(5L);form.setCuentaDestinoTexto("BANCO-TRASPASO-TEST · Cuenta destino ficticia");
  form.setMonto(new BigDecimal("300.00"));form.setFecha(LocalDateTime.of(2026,10,16,19,30));
  form.setReferencia("TRASPASO-TEST-001");form.setObservaciones("Prueba ficticia sin operaciones reales");
  var mvc=MockMvcBuilders.standaloneSetup(new Pantalla(form)).setViewResolvers(vistas).build();
  var response=mvc.perform(get("/qa/transferencia")).andReturn().getResponse();
  assertThat(response.getStatus()).isEqualTo(200);var html=response.getContentAsString();
  assertThat(html).contains("data-money-confirm=\"transferencia-cuentas\"","action=\"/admin/transferencias\"",
    "name=\"claveIdempotencia\"","/js/money-input.js?v=20261016-transferencia",
    "class=\"detail-close-button\" href=\"/admin/movimientos-financieros\">Cancelar",
    "Aplicar transferencia","name=\"cuentaOrigenId\"","name=\"cuentaDestinoId\"");
  assertThat(html).contains("value=\"300.00\"","TRASPASO-TEST-001","Cuenta destino ficticia");
  Path dir=Path.of("target/qa-transferencia");Files.createDirectories(dir);
  // El fixture usa el formulario real y valores sólo ficticios; un servidor QA bloquea POST.
  Files.writeString(dir.resolve("formulario.html"),html);
 }
 @Controller static class Pantalla {
  private final TransferenciaCuentaForm form;
  Pantalla(TransferenciaCuentaForm form){this.form=form;}
  @GetMapping("/qa/transferencia") String vista(Model model){
   model.addAttribute("form",form);model.addAttribute("usuarioSesion","usuario.prueba");
   model.addAttribute("instituciones",List.of(new InstitucionResponse(1L,"QA","Institución de prueba",null,null,null,null,null,null,null,null,null,null,"MX",null,"America/Mexico_City","MXN",true,null)));
   return "admin/transferencia-cuenta-form";
  }
 }
}
