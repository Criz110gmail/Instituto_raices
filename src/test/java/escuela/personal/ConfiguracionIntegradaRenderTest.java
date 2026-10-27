package escuela.personal;

import escuela.personal.controller.ConfiguracionNominaController;
import escuela.personal.service.*;
import escuela.personal.dto.*;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class ConfiguracionIntegradaRenderTest {
 @Test void capturaSeparadaConTabsConfirmacionYRetornoSinSeleccionarOtroSueldo()throws Exception{
  var s=mock(ConfiguracionNominaService.class);var c=mock(ConceptoEmpleadoService.class);var e=mock(EmpleadoService.class);var i=mock(ConfiguracionEmpleadoNominaService.class);var salarios=mock(CondicionesSalarialesService.class);
  var permisos=List.of(new SimpleGrantedAuthority("EMPLEADO_LEER"),new SimpleGrantedAuthority("EMPLEADO_LABORAL_LEER"),new SimpleGrantedAuthority("CONDICION_SALARIAL_ADMINISTRAR"));
  when(e.autorizarSalarios(false)).thenReturn(new UsuarioPrincipal(1L,1L,Set.of(),true,false,"qa","",permisos));when(e.permitido(anyString())).thenReturn(true);when(s.hoy()).thenReturn(LocalDate.of(2026,10,24));
  var fila=new ConfiguracionNominaService.Fila(3L,"EMP-QA","Persona Ficticia",true,"ACTIVO",null,null,null,null,"MXN",null,null,null,null,null,false,0L,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO);
  when(s.obtener(eq(3L),any())).thenReturn(fila);var f=new ConfiguracionEmpleadoNominaForm();f.setRevisionConceptos(0L);f.setVersionLaboral(0L);f.getSueldo().setRevision(0L);f.getCalendario().setVersion(0L);when(i.preparar(3L,null)).thenReturn(f);
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
  var mvc=MockMvcBuilders.standaloneSetup(new ConfiguracionNominaController(s,c,e,i,salarios)).setViewResolvers(views).build();
  var r=mvc.perform(get("/admin/configuracion-nomina/3/editar").param("q","Origen").param("pagina","2").param("fecha","2027-01-15")).andReturn().getResponse();assertThat(r.getStatus()).isEqualTo(200);assertThat(r.getContentAsString()).contains("data-money-confirm=\"configuracion-integrada\"","data-config-fields=\"sueldo\" disabled","config-concept-template","conceptos[ROWINDEX].conceptoId","q=Origen","pagina=2","fecha=2027-01-15","Cancelar");
  assertThat(r.getContentAsString()).contains("<div class=\"form-grid\"><div class=\"wide\"><div class=\"autocomplete\"", "calendario.calendario-texto");
  verify(i,never()).guardar(anyLong(),any());
 }
 @Test void capturaNoTieneOtraTablaSalarialNiPermisosNuevos()throws Exception{
  var source=java.nio.file.Files.readString(java.nio.file.Path.of("src/main/java/escuela/personal/service/ConfiguracionEmpleadoNominaService.java"));
  assertThat(source).contains("@Transactional public void guardar","salarios.programar","conceptos.asignar","configuracion.guardarCalendario","FOR UPDATE","revisionConceptos").doesNotContain("INSERT INTO condicion_salarial","INSERT INTO movimiento_financiero","INSERT INTO rol_permiso");
 }
 @Test void estilosDeCapturaYAccionSeparadosSinCambiarLaPaleta()throws Exception{
  var css=java.nio.file.Files.readString(java.nio.file.Path.of("src/main/resources/static/css/nomina.css"));
  var detalle=java.nio.file.Files.readString(java.nio.file.Path.of("src/main/resources/templates/admin/configuracion-nomina-detalle.html"));
  assertThat(detalle).contains("detail-section config-entry-actions");
  assertThat(css).contains(".nomina-detail .config-entry-actions{display:grid;justify-items:start;gap:14px}",
    ".config-integrada .form-grid .autocomplete-control .autocomplete-input{padding-right:44px}",
    "background:var(--surface);color:var(--ink)",
    "@media(max-width:720px){.config-integrada .form-grid{grid-template-columns:minmax(0,1fr)}}");
 }
}
