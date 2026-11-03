package escuela.finanzas;

import escuela.SistemaAdministrativoEscolarApplication;
import escuela.finanzas.dto.*;
import escuela.finanzas.service.ChequeraService;
import escuela.seguridad.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/** Sólo base efímera expresamente identificada. No consulta credenciales reales. */
@EnabledIfEnvironmentVariable(named="QA_FLUJO_NOMINA_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_flujo_nomina_qa")
class ChequeraPostgresIT {
 @Test void catalogoPrivadoConRangosAuditoriaConcurrenciaYExcel() throws Exception {
  try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_FLUJO_NOMINA_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-sintetica","--spring.security.user.name=qa-chequera","--spring.security.user.password=qa-sintetica","--app.archivos.directorio=target/qa-chequeras")){
   var j=app.getBean(JdbcTemplate.class);var s=app.getBean(ChequeraService.class);
   j.update("INSERT INTO institucion(id,codigo,nombre,zona_horaria,moneda_predeterminada) VALUES(490000,'QA-CHEQ','Institución ficticia','America/Mexico_City','MXN'),(490001,'QA-CHEQ-OTRA','Otra ficticia','America/Mexico_City','MXN')");
   j.update("INSERT INTO usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) VALUES(490000,490000,'qa.chequera','chequera@example.invalid','ADMINISTRATIVO','ACTIVO','ficticio-no-utilizado')");
   j.update("INSERT INTO cuenta_financiera(id,institucion_id,codigo,nombre,tipo,banco_nombre,numero_cuenta,moneda,saldo_inicial,fecha_saldo_inicial) VALUES(490000,490000,'BANCO-QA','Banco ficticio','BANCO','Ficticio','1234','MXN',1000,'2026-01-01'),(490001,490000,'CAJA-QA','Caja ficticia','CAJA',NULL,NULL,'MXN',500,'2026-01-01'),(490002,490001,'OTRA-QA','Banco ajeno','BANCO','Ficticio','2345','MXN',2000,'2026-01-01')");
   var admin=auth(true,true);SecurityContextHolder.getContext().setAuthentication(admin);
   assertThat(ModuloPermiso.expandir(Set.of("CHEQUERA_LEER"))).doesNotContain("CHEQUERA_ADMINISTRAR");
   assertThat(ModuloPermiso.dePermiso("CHEQUERA_LEER").orElseThrow().nombre()).isEqualTo("Chequeras - Consultar");
   assertThat(j.queryForObject("SELECT count(*) FROM rol_permiso rp JOIN permiso p ON p.id=rp.permiso_id WHERE p.codigo LIKE 'CHEQUERA_%'",Long.class)).isZero();
   assertThatThrownBy(()->s.cuenta(490001L)).isInstanceOf(AccessDeniedException.class);assertThatThrownBy(()->s.cuenta(490002L)).isInstanceOf(AccessDeniedException.class);
   var f=form("Chequera principal QA",1000,1099);Long id=s.guardar(490000L,null,f);assertThat(s.obtener(490000L,id).cantidad()).isEqualTo(100);
   var filtros=new FiltroChequera("principal","ACTIVO",1050L,null,"",0,25);assertThat(s.listar(490000L,filtros)).hasSize(1);assertThat(s.buscar(490000L,"")).hasSize(1);assertThat(s.buscar(490000L,"ab")).isEmpty();
   assertThatThrownBy(()->s.guardar(490000L,null,form("Otra",1099,1200))).hasMessageContaining("superponen");
   assertThatThrownBy(()->s.guardar(490000L,null,form("Rango inválido",200,100))).hasMessageContaining("rango de folios");
   var modificar=form("Chequera principal QA — revisión",1000,1099);modificar.setActivo(false);s.guardar(490000L,id,modificar);
   assertThat(s.historial(490000L,id,0)).hasSize(2);assertThat(s.historial(490000L,id,0).getContent().getFirst().activo()).isFalse();
   assertThatThrownBy(()->s.guardar(490000L,id,modificar)).hasMessageContaining("cambió");
   assertThatThrownBy(()->s.guardar(490000L,null,form("Superpuesta inactiva",1000,1001))).hasMessageContaining("inactiva");
   assertThatThrownBy(()->j.update("DELETE FROM chequera WHERE id=?",id)).isInstanceOf(RuntimeException.class);assertThatThrownBy(()->j.update("DELETE FROM chequera_historial WHERE chequera_id=?",id)).isInstanceOf(RuntimeException.class);
   assertThatThrownBy(()->j.update("UPDATE cuenta_financiera SET tipo='CAJA',banco_nombre=NULL,numero_cuenta=NULL WHERE id=490000")).isInstanceOf(RuntimeException.class);
   var pool=java.util.concurrent.Executors.newFixedThreadPool(2);var inicio=new java.util.concurrent.CountDownLatch(1);
   try{var tareas=new ArrayList<java.util.concurrent.Future<Boolean>>();for(int n=0;n<2;n++)tareas.add(pool.submit(()->{SecurityContextHolder.getContext().setAuthentication(admin);try{inicio.await();s.guardar(490000L,null,form("Concurrente",2000,2099));return true;}catch(escuela.common.exception.ReglaNegocioException e){return false;}finally{SecurityContextHolder.clearContext();}}));inicio.countDown();int exitos=0;for(var t:tareas)if(t.get(30,java.util.concurrent.TimeUnit.SECONDS))exitos++;assertThat(exitos).isEqualTo(1);}finally{pool.shutdownNow();}
   SecurityContextHolder.getContext().setAuthentication(auth(false,true));assertThat(s.puedeEditar()).isFalse();assertThat(s.obtener(490000L,id)).isNotNull();assertThatThrownBy(()->s.guardar(490000L,null,form("Sin permiso",3000,3099))).isInstanceOf(AccessDeniedException.class);
   SecurityContextHolder.getContext().setAuthentication(auth(true,false));assertThatThrownBy(()->s.cuenta(490000L)).isInstanceOf(AccessDeniedException.class);
   var limitado=auth(true,true);var principal=(UsuarioPrincipal)limitado.getPrincipal();SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(new UsuarioPrincipal(principal.usuarioId(),principal.institucionId(),Set.of(),false,false,principal.username(),"",principal.authorities()),"",principal.authorities()));assertThatThrownBy(()->s.cuenta(490000L)).isInstanceOf(AccessDeniedException.class);
   SecurityContextHolder.getContext().setAuthentication(admin);
   j.update("UPDATE cuenta_financiera SET activo=false WHERE id=490000");assertThatThrownBy(()->s.guardar(490000L,null,form("Cuenta inactiva",3000,3099))).hasMessageContaining("inactivas");j.update("UPDATE cuenta_financiera SET activo=true WHERE id=490000");
   var mvc=org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup((org.springframework.web.context.WebApplicationContext)app).addFilters(app.getBean(org.springframework.security.web.FilterChainProxy.class)).build();var session=session(admin);var dir=java.nio.file.Files.createDirectories(java.nio.file.Path.of("target/qa-chequeras"));
   for(var ruta:Map.of("listado","/admin/cuentas-financieras/490000/chequeras","nuevo","/admin/cuentas-financieras/490000/chequeras/nuevo","editar","/admin/cuentas-financieras/490000/chequeras/"+id+"/editar","historial","/admin/cuentas-financieras/490000/chequeras/"+id+"/historial","cuenta","/admin/cuentas-financieras/490000/editar","cuentas","/admin/catalogos/cuentas-financieras").entrySet()){
    var r=mvc.perform(get(ruta.getValue()).session(session)).andReturn().getResponse();assertThat(r.getStatus()).as(ruta.getKey()).isEqualTo(200);java.nio.file.Files.writeString(dir.resolve(ruta.getKey()+".html"),r.getContentAsString());
   }
   var read=session(auth(false,true));var r=mvc.perform(get("/admin/cuentas-financieras/490000/chequeras").session(read)).andReturn().getResponse();assertThat(r.getStatus()).isEqualTo(200);assertThat(r.getContentAsString()).doesNotContain("Nueva chequera ＋",">Editar</a>");
   assertThat(mvc.perform(get("/admin/cuentas-financieras/490000/chequeras/nuevo").session(read)).andReturn().getResponse().getStatus()).isIn(302,403);
   assertThat(mvc.perform(post("/admin/cuentas-financieras/490000/chequeras").session(read).with(csrf())).andReturn().getResponse().getStatus()).isIn(302,403);
   var x=mvc.perform(get("/admin/cuentas-financieras/490000/chequeras/excel").session(read).param("estado","INACTIVO").param("folio","1050")).andReturn().getResponse();assertThat(x.getStatus()).isEqualTo(200);
   try(var book=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(x.getContentAsByteArray()))){assertThat(book.getSheetAt(0).getLastRowNum()).isEqualTo(1);assertThat(book.getSheetAt(0).getRow(1).getCell(4).getStringCellValue()).isEqualTo("Inactiva");}
   var error=mvc.perform(post("/admin/cuentas-financieras/490000/chequeras").session(session).with(csrf()).param("nombre","Conservar captura").param("folioInicial","1099").param("folioFinal","1200").param("motivo","Probar solapamiento").param("version","0").param("q","principal").param("pagina","2")).andReturn().getResponse();assertThat(error.getStatus()).isEqualTo(200);assertThat(error.getContentAsString()).contains("Conservar captura","superponen","pagina=2");
   var obligatorio=mvc.perform(post("/admin/cuentas-financieras/490000/chequeras").session(session).with(csrf()).param("nombre","").param("folioInicial","0").param("folioFinal","0").param("motivo","").param("version","0")).andReturn().getResponse();assertThat(obligatorio.getStatus()).isEqualTo(200);assertThat(obligatorio.getContentAsString()).contains("Revisa los campos señalados","field-error");
   var guardado=mvc.perform(post("/admin/cuentas-financieras/490000/chequeras").session(session).with(csrf()).param("nombre","Alta desde formulario").param("folioInicial","3100").param("folioFinal","3199").param("motivo","Prueba HTTP con filtros conservados").param("activo","true").param("version","0").param("q","principal").param("pagina","2")).andReturn().getResponse();assertThat(guardado.getStatus()).isEqualTo(302);assertThat(guardado.getRedirectedUrl()).contains("q=principal","pagina=2");
   SecurityContextHolder.getContext().setAuthentication(admin);assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isZero();assertThat(j.queryForObject("SELECT saldo_inicial FROM cuenta_financiera WHERE id=490000",java.math.BigDecimal.class)).isEqualByComparingTo("1000");
  }finally{SecurityContextHolder.clearContext();}
 }
 private static ChequeraForm form(String nombre,long inicio,long fin){var f=new ChequeraForm();f.setNombre(nombre);f.setFolioInicial(inicio);f.setFolioFinal(fin);f.setMotivo("Registro o cambio ficticio para probar el catálogo");return f;}
 private static UsernamePasswordAuthenticationToken auth(boolean administrar,boolean cuenta){var permisos=new ArrayList<String>();permisos.add(administrar?"CHEQUERA_ADMINISTRAR":"CHEQUERA_LEER");if(cuenta){permisos.add("CUENTA_FINANCIERA_LEER");if(administrar)permisos.add("CUENTA_FINANCIERA_ADMINISTRAR");}var roles=permisos.stream().map(SimpleGrantedAuthority::new).toList();return new UsernamePasswordAuthenticationToken(new UsuarioPrincipal(490000L,490000L,Set.of(),true,false,"qa.chequera","",roles),"",roles);}
 private static org.springframework.mock.web.MockHttpSession session(org.springframework.security.core.Authentication a){var s=new org.springframework.mock.web.MockHttpSession();s.setAttribute("SPRING_SECURITY_CONTEXT",new org.springframework.security.core.context.SecurityContextImpl(a));return s;}
}
