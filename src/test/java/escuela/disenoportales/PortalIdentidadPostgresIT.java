package escuela.disenoportales;
import escuela.SistemaAdministrativoEscolarApplication;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.mock.web.*;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
@EnabledIfEnvironmentVariable(named="QA_PORTALES_JDBC",matches="jdbc:postgresql://nexo-portales-qa:5432/nexo_portales_qa")
class PortalIdentidadPostgresIT {
 UsuarioPrincipal user(long id,long i,boolean scope,String... perms){return new UsuarioPrincipal(id,i,Set.of(),scope,false,"qa","",Arrays.stream(perms).map(SimpleGrantedAuthority::new).toList());}
 MockHttpSession session(UsuarioPrincipal p){var s=new MockHttpSession();s.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(UsernamePasswordAuthenticationToken.authenticated(p,"",p.authorities())));return s;}
 @Test void aislamientoHistorialPublicacionConcurrenciaYVistasReales()throws Exception{
  try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_PORTALES_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-ficticia","--spring.security.user.name=qa-recuperacion","--spring.security.user.password=qa-ficticia","--app.sitio.codigo=QA-PORTALES","--app.portales.url-publica=http://localhost:18783")){
   var j=app.getBean(JdbcTemplate.class);assertThat(j.queryForObject("SELECT count(*) FROM guia_proceso WHERE concat_ws(' ',titulo,resumen,requisitos,ejemplo) ~* 'ra[ií]ces'",Long.class)).isZero();assertThat(j.queryForObject("SELECT count(*) FROM guia_proceso_paso WHERE concat_ws(' ',titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion) ~* 'ra[ií]ces'",Long.class)).isZero();j.update("INSERT INTO institucion(id,codigo,nombre) VALUES(97000,'QA-PORTALES','Instituto Horizonte'),(97001,'QA-OTRA','Otra institución')");j.update("INSERT INTO usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) VALUES(97000,97000,'qa.portales','portales@example.invalid','ADMINISTRATIVO','ACTIVO','no-usado'),(97001,97001,'qa.otra','otra@example.invalid','ADMINISTRATIVO','ACTIVO','no-usado')");
   var picture=new java.awt.image.BufferedImage(120,80,java.awt.image.BufferedImage.TYPE_INT_ARGB);var graphics=picture.createGraphics();graphics.setColor(new java.awt.Color(25,79,61));graphics.fillRoundRect(0,0,120,80,18,18);graphics.setColor(java.awt.Color.WHITE);graphics.setFont(new java.awt.Font("SansSerif",java.awt.Font.BOLD,56));graphics.drawString("H",38,61);graphics.dispose();var png=new java.io.ByteArrayOutputStream();javax.imageio.ImageIO.write(picture,"png",png);j.update("INSERT INTO sitio_web_imagen(id,institucion_id,contenido,mime,creado_por) OVERRIDING SYSTEM VALUE VALUES(97000,97000,?,'image/png',97000)",png.toByteArray());j.update("INSERT INTO sitio_web_publicacion(institucion_id,nombre,eslogan,portada,nosotros,oferta,talleres,inscripciones,principal,acento,version,publicado_por,logo_id) VALUES(97000,'Instituto Horizonte','QA','QA','QA','QA','QA','QA','#194f3d','#f5c542',1,97000,97000)");
   var service=app.getBean(PortalIdentidadService.class);var admin=user(97000,97000,true,"PORTAL_IDENTIDAD_ADMINISTRAR");var read=user(97000,97000,true,"PORTAL_IDENTIDAD_LEER");var other=user(97001,97001,true,"PORTAL_IDENTIDAD_ADMINISTRAR");
   assertThatThrownBy(()->service.borrador(user(97000,97000,false,"PORTAL_IDENTIDAD_ADMINISTRAR"),PortalTipo.ADMIN)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);assertThat(service.publico("QA-PORTALES",PortalTipo.ADMIN).version()).isZero();var f=PortalIdentidadService.form(service.borrador(admin,PortalTipo.ADMIN));f.setOriginal(false);f.setPrincipal("#ffffff");f.setAcento("#201040");service.guardar(admin,PortalTipo.ADMIN,f);
   assertThat(service.publico("QA-PORTALES",PortalTipo.ADMIN).version()).isZero();assertThatThrownBy(()->service.guardar(admin,PortalTipo.ADMIN,f)).hasMessageContaining("cambió");assertThatThrownBy(()->service.publicar(read,PortalTipo.ADMIN,1)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);assertThatThrownBy(()->service.guardar(read,PortalTipo.ADMIN,f)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);service.publicar(admin,PortalTipo.ADMIN,1);assertThat(service.publico("QA-PORTALES",PortalTipo.ADMIN).principal()).isEqualTo("#ffffff");assertThat(service.publico("QA-PORTALES",PortalTipo.ADMIN).logo()).isNotEmpty();assertThat(service.publico("QA-OTRA",PortalTipo.ADMIN).version()).isZero();assertThat(service.publico("QA-PORTALES",PortalTipo.FAMILIAS).version()).isZero();assertThatThrownBy(()->service.revision(other,PortalTipo.ADMIN,1)).isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
   f.setVersion(1);f.setPrincipal("#194f3d");service.guardar(admin,PortalTipo.ADMIN,f);assertThat(service.publico("QA-PORTALES",PortalTipo.ADMIN).version()).isEqualTo(1);assertThatThrownBy(()->service.publicar(admin,PortalTipo.ADMIN,1)).hasMessageContaining("cambió");service.publicar(admin,PortalTipo.ADMIN,2);service.restaurar(admin,PortalTipo.ADMIN,1,2);assertThat(service.borrador(admin,PortalTipo.ADMIN).principal()).isEqualTo("#ffffff");assertThat(service.publico("QA-PORTALES",PortalTipo.ADMIN).principal()).isEqualTo("#194f3d");
   var concurrent=PortalIdentidadService.form(service.borrador(admin,PortalTipo.ADMIN));var pool=java.util.concurrent.Executors.newFixedThreadPool(2);var start=new java.util.concurrent.CountDownLatch(1);try{var futures=new ArrayList<java.util.concurrent.Future<Boolean>>();for(int n=0;n<2;n++)futures.add(pool.submit(()->{start.await();try{service.guardar(admin,PortalTipo.ADMIN,concurrent);return true;}catch(escuela.common.exception.ReglaNegocioException e){return false;}}));start.countDown();int won=0;for(var future:futures)if(future.get())won++;assertThat(won).isEqualTo(1);}finally{pool.shutdownNow();}
   var mvc=MockMvcBuilders.webAppContextSetup((WebApplicationContext)app).addFilters(app.getBean(FilterChainProxy.class)).build();var s=session(admin);var csrfResponse=mvc.perform(get("/sesion/estado").session(s)).andReturn();CsrfToken csrf=(CsrfToken)csrfResponse.getRequest().getAttribute(CsrfToken.class.getName());assertThat(mvc.perform(post("/admin/portales/admin/publicar").session(s).param("version","4")).andReturn().getResponse().getStatus()).isEqualTo(403);assertThat(mvc.perform(get("/admin/portales/admin/editar").session(session(read))).andReturn().getResponse().getStatus()).isEqualTo(403);assertThat(mvc.perform(get("/admin/portales").session(session(user(97000,97000,true)))).andReturn().getResponse().getStatus()).isEqualTo(403);
   var qa=Path.of("target/qa-portales");Files.createDirectories(qa);var listing=mvc.perform(get("/admin/portales").session(s)).andReturn();assertThat(listing.getResponse().getStatus()).isEqualTo(200);Files.writeString(qa.resolve("portales.html"),listing.getResponse().getContentAsString());assertThat(listing.getResponse().getContentAsString()).contains("<strong>qa</strong>","Personal · Mis recibos","Copiar enlace","/accesos/QA-PORTALES/admin");
   for(var tipo:PortalTipo.values()){
    var edit=mvc.perform(get("/admin/portales/"+tipo.codigo+"/editar").session(s)).andReturn();assertThat(edit.getResponse().getStatus()).as(tipo.codigo).isEqualTo(200);Files.writeString(qa.resolve(tipo.codigo+"-editor.html"),edit.getResponse().getContentAsString());
    var preview=mvc.perform(get("/admin/portales/"+tipo.codigo+"/vista-previa").session(s)).andReturn();assertThat(preview.getResponse().getStatus()).isEqualTo(200);assertThat(preview.getResponse().getHeader("X-Frame-Options")).isEqualTo("SAMEORIGIN");assertThat(preview.getResponse().getContentAsString()).contains("data-pi-preview=\"true\"");Files.writeString(qa.resolve(tipo.codigo+"-preview.html"),preview.getResponse().getContentAsString());
    var publishedPreview=mvc.perform(get("/admin/portales/"+tipo.codigo+"/vista-previa").param("modo","publicado").session(s)).andReturn();assertThat(publishedPreview.getResponse().getStatus()).isEqualTo(200);Files.writeString(qa.resolve(tipo.codigo+"-published-preview.html"),publishedPreview.getResponse().getContentAsString());
    var pub=mvc.perform(get("/accesos/QA-PORTALES/"+tipo.codigo).cookie(new jakarta.servlet.http.Cookie("JSESSIONID","vencida-ficticia"))).andReturn();assertThat(pub.getResponse().getStatus()).isEqualTo(200);assertThat(pub.getRequest().getSession(false)).isNull();assertThat(pub.getResponse().getHeader("X-Frame-Options")).isEqualTo("DENY");assertThat(pub.getResponse().getContentAsString()).contains("/images/logo1.png","Logotipo de la institución","name=\"institucionAcceso\" value=\"QA-PORTALES\"");Files.writeString(qa.resolve(tipo.codigo+"-publico.html"),pub.getResponse().getContentAsString());
   }
   for(var tipo:PortalTipo.values()){
    var inside=mvc.perform(get("/admin/portales/"+tipo.codigo+"/vista-previa").param("interior","true").session(s)).andReturn();
    assertThat(inside.getResponse().getStatus()).isEqualTo(200);
    assertThat(inside.getResponse().getHeader("X-Frame-Options")).isEqualTo("SAMEORIGIN");
    assertThat(inside.getResponse().getContentAsString()).contains("Sin datos reales","Ver modal de confirmación").doesNotContain("method=\"post\"");
    Files.writeString(qa.resolve(tipo.codigo+"-interior.html"),inside.getResponse().getContentAsString());
   }
   var logoResponse=mvc.perform(get("/accesos/QA-PORTALES/admin/logo.png").cookie(new jakarta.servlet.http.Cookie("JSESSIONID","vencida-ficticia"))).andReturn();assertThat(logoResponse.getResponse().getStatus()).isEqualTo(200);assertThat(logoResponse.getResponse().getContentType()).isEqualTo("image/png");assertThat(mvc.perform(get("/accesos/QA-OTRA/admin/logo.png")).andReturn().getResponse().getStatus()).isEqualTo(404);
   for(var tipo:PortalTipo.values()){var t=service.borrador(admin,tipo);if(t.isTieneLogo())Files.write(qa.resolve(tipo.codigo+"-logo.png"),t.logo());}
   assertThat(mvc.perform(get("/accesos/NO-EXISTE/admin")).andReturn().getResponse().getStatus()).isEqualTo(404);assertThat(mvc.perform(get("/accesos/QA-PORTALES/no-existe").session(s)).andReturn().getResponse().getStatus()).isEqualTo(404);
   String hash=app.getBean(org.springframework.security.crypto.password.PasswordEncoder.class).encode("qa-pass-ficticia");j.update("UPDATE usuario SET username='persona',password_hash=? WHERE id IN(97000,97001)",hash);var tokenAccess=mvc.perform(get("/acceso/csrf")).andReturn();var loginSession=(MockHttpSession)tokenAccess.getRequest().getSession(false);var loginCsrf=(CsrfToken)tokenAccess.getRequest().getAttribute(CsrfToken.class.getName());var login=mvc.perform(post("/login").session(loginSession).param("username","persona").param("password","qa-pass-ficticia").param("institucionAcceso","QA-PORTALES").param("_csrf",loginCsrf.getToken())).andReturn();assertThat(login.getResponse().getRedirectedUrl()).isEqualTo("/admin");var actualSession=(MockHttpSession)login.getRequest().getSession(false);var security=(SecurityContextImpl)actualSession.getAttribute("SPRING_SECURITY_CONTEXT");assertThat(((UsuarioPrincipal)security.getAuthentication().getPrincipal()).institucionId()).isEqualTo(97000);var logoutToken=mvc.perform(get("/sesion/estado").session(actualSession)).andReturn();assertThat(logoutToken.getResponse().getContentAsString()).contains("QA-PORTALES");var afterLoginCsrf=(CsrfToken)logoutToken.getRequest().getAttribute(CsrfToken.class.getName());assertThat(mvc.perform(post("/logout").session(actualSession).param("_csrf",afterLoginCsrf.getToken())).andReturn().getResponse().getRedirectedUrl()).isEqualTo("/login?logout&institucion=QA-PORTALES");
   var tokenFailure=mvc.perform(get("/acceso/csrf")).andReturn();var badSession=(MockHttpSession)tokenFailure.getRequest().getSession(false);var badCsrf=(CsrfToken)tokenFailure.getRequest().getAttribute(CsrfToken.class.getName());var bad=mvc.perform(post("/login").session(badSession).param("username","QA-OTRA\\persona").param("password","qa-pass-ficticia").param("institucionAcceso","QA-PORTALES").param("origen","maestros").param("_csrf",badCsrf.getToken())).andReturn();assertThat(bad.getResponse().getRedirectedUrl()).isEqualTo("/maestros/acceso?error&institucion=QA-PORTALES");
   var invalid=mvc.perform(post("/admin/portales/admin").session(s).header(csrf.getHeaderName(),csrf.getToken()).param("version","4").param("principal","red").param("fondo","#ffffff").param("acento","#123456")).andReturn();assertThat(invalid.getResponse().getStatus()).isEqualTo(200);assertThat(invalid.getResponse().getContentAsString()).contains("value=\"red\"");assertThat(service.borrador(admin,PortalTipo.ADMIN).version()).isEqualTo(4);assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isZero();
   var identity=mvc.perform(get("/sesion/identidad").param("portal","admin").session(session(user(97000,97000,false)))).andReturn();
   assertThat(identity.getResponse().getStatus()).isEqualTo(200);
   assertThat(identity.getResponse().getHeader("Cache-Control")).isEqualTo("no-store");
   assertThat(identity.getResponse().getContentAsString()).contains("\"principal\":\"#194f3d\"","\"version\":2").doesNotContain("Instituto Horizonte");
   assertThat(mvc.perform(get("/sesion/identidad").param("portal","admin").session(session(other))).andReturn().getResponse().getContentAsString()).contains("\"original\":true");
   assertThat(mvc.perform(get("/sesion/identidad").param("portal","admin")).andReturn().getResponse().getStatus()).isEqualTo(401);
   var restored=mvc.perform(post("/admin/portales/admin/restaurar").session(s).header(csrf.getHeaderName(),csrf.getToken()).param("revision","1").param("version","4")).andReturn();
   assertThat(restored.getResponse().getRedirectedUrl()).isEqualTo("/admin/portales/admin/editar");
   assertThat(service.borrador(admin,PortalTipo.ADMIN).principal()).isEqualTo("#ffffff");
   assertThat(service.publico("QA-PORTALES",PortalTipo.ADMIN).principal()).isEqualTo("#194f3d");
   service.publicar(admin,PortalTipo.ADMIN,5);
   assertThat(service.publico("QA-PORTALES",PortalTipo.ADMIN).principal()).isEqualTo("#ffffff");
   assertThat(mvc.perform(get("/login")).andReturn().getResponse().getContentAsString()).contains("--pi-principal:#ffffff");
   service.restaurar(admin,PortalTipo.ADMIN,0,5);
   assertThat(service.borrador(admin,PortalTipo.ADMIN).original()).isTrue();
   assertThat(service.publico("QA-PORTALES",PortalTipo.ADMIN).original()).isFalse();
   service.publicar(admin,PortalTipo.ADMIN,6);
   assertThat(service.publico("QA-PORTALES",PortalTipo.ADMIN).original()).isTrue();
   var defaultLogin=mvc.perform(get("/login")).andReturn();
   assertThat(defaultLogin.getResponse().getContentAsString()).contains("/images/logo1.png","--pi-principal:#214936");
   Files.writeString(qa.resolve("admin-default-login.html"),defaultLogin.getResponse().getContentAsString());
   assertThat(mvc.perform(get("/images/logo1.png")).andReturn().getResponse().getStatus()).isEqualTo(200);

   assertThat(mvc.perform(get("/sesion/identidad").param("portal","admin").session(s)).andReturn().getResponse().getContentAsString()).contains("\"original\":true");
   var advanced=PortalIdentidadService.form(service.borrador(admin,PortalTipo.ADMIN));advanced.setOriginal(false);advanced.setOscuroPersonalizado(true);advanced.setOscuroPrincipal("#220033");advanced.setOscuroFondo("#090510");advanced.setOscuroAcento("#ffee00");
   service.guardar(admin,PortalTipo.ADMIN,advanced);
   assertThat(service.borrador(admin,PortalTipo.ADMIN).oscuroPrincipal()).isEqualTo("#220033");
   assertThat(service.publico("QA-PORTALES",PortalTipo.ADMIN).oscuroPrincipal()).isNull();
   service.publicar(admin,PortalTipo.ADMIN,7);
   assertThat(mvc.perform(get("/sesion/identidad").param("portal","admin").session(s)).andReturn().getResponse().getContentAsString()).contains("\"oscuroAcento\":\"#ffee00\"");
   assertThat(mvc.perform(get("/login")).andReturn().getResponse().getContentAsString()).contains("--pi-dark-principal:#220033");
   service.restaurar(admin,PortalTipo.ADMIN,7,7);
   assertThat(service.borrador(admin,PortalTipo.ADMIN).oscuroFondo()).isEqualTo("#090510");
   advanced.setVersion(8);advanced.setOscuroAcento("red");
   assertThatThrownBy(()->service.guardar(admin,PortalTipo.ADMIN,advanced)).isInstanceOf(escuela.common.exception.ReglaNegocioException.class);
   var badDark=mvc.perform(post("/admin/portales/admin").session(s).header(csrf.getHeaderName(),csrf.getToken()).param("version","8").param("principal","#194f3d").param("fondo","#eff5ef").param("acento","#f5c542").param("original","false").param("oscuroPersonalizado","true").param("oscuroPrincipal","#220033").param("oscuroFondo","#090510").param("oscuroAcento","red")).andReturn();
   assertThat(badDark.getResponse().getStatus()).isEqualTo(200);
   assertThat(badDark.getResponse().getContentAsString()).contains("value=\"red\"");
   assertThat(service.borrador(admin,PortalTipo.ADMIN).version()).isEqualTo(8);



  }
 }
}
