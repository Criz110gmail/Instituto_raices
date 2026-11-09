package escuela.compras.service;

import escuela.SistemaAdministrativoEscolarApplication;
import escuela.compras.dto.*;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.*;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@EnabledIfEnvironmentVariable(named="QA_COMPRAS_JDBC",matches="jdbc:postgresql://nexo-compras-v167-db:5432/nexo_compras_qa")
class CompraComprobantePostgresIT {
    private UsernamePasswordAuthenticationToken auth(long institucion,String permiso){var a=List.of(new SimpleGrantedAuthority(permiso));return UsernamePasswordAuthenticationToken.authenticated(new UsuarioPrincipal(institucion,institucion,Set.of(),true,false,"qa.compras","",a),"",a);}
    private MockHttpSession sesion(UsernamePasswordAuthenticationToken a){var s=new MockHttpSession();s.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(a));return s;}
    private CompraDocumentoForm archivo(String tipo,byte[] contenido){var f=new CompraDocumentoForm();f.setTipo(tipo);f.setArchivo(new MockMultipartFile("archivo","ficticio."+(tipo.equals("XML")?"xml":"pdf"),"application/octet-stream",contenido));f.setVersion(1L);f.setConfirmado(true);f.setDescripcion("Comprobante ficticio");return f;}
    private CompraForm compra(){var f=new CompraForm();f.setInstitucionId(97000L);f.setProveedorId(97000L);f.setCuentaId(97000L);f.setMotivoFinancieroId(97000L);f.setFechaOperacion(java.time.LocalDateTime.of(2026,11,9,10,0));var p=new CompraPartidaForm();p.setDescripcion("Compra ficticia");p.setCantidad(new BigDecimal("2"));p.setPrecioUnitario(new BigDecimal("100"));p.setPorcentajeImpuesto(new BigDecimal("16"));f.getPartidas().add(p);return f;}
    @Test void expedientePrivadoNormalizadoVersionadoConFiltrosPermisosYReemplazoExplicitoSinDinero()throws Exception {
        var ruta=Files.createTempDirectory(Path.of("target"),"qa-comprobantes-").toAbsolutePath();
        try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=",
            "--spring.datasource.url="+System.getenv("QA_COMPRAS_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-ficticia",
            "--spring.security.user.name=qa-recuperacion","--spring.security.user.password=qa-ficticia","--app.archivos.directorio="+ruta)) {
            var j=app.getBean(JdbcTemplate.class);
            j.update("INSERT INTO institucion(id,codigo,nombre,rfc,zona_horaria,moneda_predeterminada) VALUES(97000,'QA-COM','Escuela ficticia','BBB010101BBB','America/Mexico_City','MXN'),(97001,'QA-OTRA','Otra escuela ficticia','CCC010101CCC','America/Mexico_City','MXN')");
            j.update("INSERT INTO usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) VALUES(97000,97000,'qa.compras','qa@example.invalid','ADMINISTRATIVO','ACTIVO','no-usado'),(97001,97001,'qa.otra','otra@example.invalid','ADMINISTRATIVO','ACTIVO','no-usado')");
            j.update("INSERT INTO proveedor(id,institucion_id,razon_social,rfc) VALUES(97000,97000,'Proveedor ficticio','AAA010101AAA')");
            j.update("INSERT INTO cuenta_financiera(id,institucion_id,codigo,nombre,tipo,moneda,saldo_inicial,fecha_saldo_inicial) VALUES(97000,97000,'QA-CAJA','Caja ficticia','CAJA','MXN',1000,'2026-11-01')");
            j.update("INSERT INTO motivo_financiero(id,institucion_id,codigo,nombre,naturaleza,categoria) VALUES(97000,97000,'QA-COMPRA','Compra ficticia','EGRESO','QA')");
            var admin=auth(97000,"COMPRA_ADMINISTRAR");SecurityContextHolder.getContext().setAuthentication(admin);
            var compras=app.getBean(CompraService.class);var s=app.getBean(CompraComprobanteService.class);Long id=compras.crear(compra()),otro=compras.crear(compra());
            byte[] xml=Files.readAllBytes(Path.of("src/test/resources/compras/cfdi-prueba.xml"));
            var respaldo=new CompraRespaldoForm();respaldo.setVersion(0L);respaldo.setConfirmado(true);respaldo.setTipo("SIN_COMPROBANTE");
            assertThatThrownBy(()->s.clasificar(id,respaldo)).hasMessageContaining("Explica");
            respaldo.setTipo("CFDI");assertThatThrownBy(()->s.clasificar(id,respaldo)).hasMessageContaining("Primero adjunta");
            respaldo.setTipo("TICKET_RECIBO");s.clasificar(id,respaldo);assertThatThrownBy(()->s.clasificar(id,respaldo)).hasMessageContaining("cambió");
            var cancelado=archivo("XML",xml);cancelado.setConfirmado(false);assertThatThrownBy(()->s.agregar(id,cancelado)).hasMessageContaining("confirma");
            s.agregar(id,archivo("XML",xml));Long doc=s.expediente(id,"XML","VIGENTES","",0).documentos().getContent().getFirst().id();
            assertThat(s.fiscal(id,doc).diferencias()).isEmpty();assertThat(s.fiscal(id,doc).partidas()).hasSize(1);
            assertThat(j.queryForObject("SELECT count(*) FROM compra_cfdi_concepto",Long.class)).isEqualTo(1);
            assertThat(j.queryForObject("SELECT count(*) FROM compra_cfdi_impuesto",Long.class)).isEqualTo(2);
            assertThat(j.queryForObject("SELECT count(*) FROM compra_cfdi_relacion",Long.class)).isEqualTo(1);
            try(var in=s.abrir(id,doc).recurso().getInputStream()){assertThat(in.readAllBytes()).isEqualTo(xml);}
            assertThatThrownBy(()->s.agregar(otro,archivo("XML",xml))).hasMessageContaining("UUID");
            assertThatThrownBy(()->s.abrir(otro,doc)).hasMessageContaining("comprobante");
            assertThatThrownBy(()->j.update("DELETE FROM compra_cfdi WHERE documento_id=?",doc)).isInstanceOf(org.springframework.dao.DataAccessException.class);
            var mv=MockMvcBuilders.webAppContextSetup((WebApplicationContext)app).addFilters(app.getBean(FilterChainProxy.class)).build();var session=sesion(admin);
            var dir=Path.of("target/qa-comprobantes-render");Files.createDirectories(dir);
            for(String endpoint:List.of("/admin/compras/"+id,"/admin/compras/"+id+"/comprobantes/nuevo","/admin/compras/"+id+"/comprobantes/respaldo","/admin/compras/"+id+"/comprobantes/"+doc+"/fiscal","/admin/compras/"+id+"/comprobantes/"+doc+"/retirar")) {
                var r=mv.perform(get(endpoint).session(session).param("origen","/admin/compras?texto=QA&pagina=2&tamanio=10")).andReturn().getResponse();assertThat(r.getStatus()).as(endpoint).isEqualTo(200);
                Files.writeString(dir.resolve(endpoint.endsWith("nuevo")?"nuevo.html":endpoint.endsWith("respaldo")?"respaldo.html":endpoint.endsWith("fiscal")?"fiscal.html":endpoint.endsWith("retirar")?"retirar.html":"detalle.html"),r.getContentAsString());
            }
            var lectura=sesion(auth(97000,"COMPRA_LEER"));
            assertThat(mv.perform(get("/admin/compras/"+id+"/comprobantes/nuevo").session(lectura)).andReturn().getResponse().getStatus()).isEqualTo(403);
            var fiscalLectura=mv.perform(get("/admin/compras/"+id+"/comprobantes/"+doc+"/fiscal").session(lectura)).andReturn().getResponse();assertThat(fiscalLectura.getStatus()).isEqualTo(200);assertThat(fiscalLectura.getContentAsString()).doesNotContain("Revisar importación");
            var descargado=mv.perform(get("/admin/compras/"+id+"/comprobantes/"+doc+"/abrir").session(lectura)).andReturn().getResponse();assertThat(descargado.getContentAsByteArray()).isEqualTo(xml);assertThat(descargado.getHeader("Cache-Control")).contains("no-store");
            var nuevo=mv.perform(get("/admin/compras/"+id+"/comprobantes/nuevo").session(session)).andReturn();var csrf=(CsrfToken)nuevo.getRequest().getAttribute(CsrfToken.class.getName());
            assertThat(mv.perform(post("/admin/compras/"+id+"/comprobantes/respaldo").session(session)).andReturn().getResponse().getStatus()).isBetween(300,499);
            assertThat(mv.perform(post("/admin/compras/"+id+"/comprobantes/respaldo").session(lectura).param(csrf.getParameterName(),csrf.getToken())).andReturn().getResponse().getStatus()).isBetween(300,499);
            // MVC deja su propio contexto limpio; restablecer el actor antes de llamar servicios.
            SecurityContextHolder.getContext().setAuthentication(admin);
            var ultimo=s.expediente(id,"","TODOS","",0).respaldo();respaldo.setVersion(ultimo.id());respaldo.setTipo("CFDI");s.clasificar(id,respaldo);
            assertThatThrownBy(()->s.retirar(id,doc,"Retirar ficticio",true)).hasMessageContaining("clasificación");
            var edicion=compras.formulario(id);edicion.getPartidas().getFirst().setPrecioUnitario(new BigDecimal("110"));compras.actualizar(id,edicion);
            assertThatThrownBy(()->s.validarConfirmacion(id)).hasMessageContaining("diferencias");
            long versionImportacion=compras.detalle(id).version();
            s.importar(id,doc,versionImportacion,"Importación ficticia controlada",true);
            s.validarConfirmacion(id);
            assertThat(compras.detalle(id).version()).isGreaterThan(1L);assertThat(compras.detalle(id).partidas().getFirst().descripcion()).contains("Papel ficticio");
            assertThatThrownBy(()->s.importar(id,doc,1L,"No repetir con versión antigua",true)).hasMessageContaining("cambió");
            ultimo=s.expediente(id,"","TODOS","",0).respaldo();respaldo.setVersion(ultimo.id());respaldo.setTipo("TICKET_RECIBO");s.clasificar(id,respaldo);
            s.retirar(id,doc,"Retiro ficticio; conservar original",true);
            assertThat(s.expediente(id,"XML","VIGENTES","",0).documentos()).isEmpty();assertThat(s.expediente(id,"XML","RETIRADOS","AAAAAAAA",0).documentos().getTotalElements()).isEqualTo(1);
            assertThatThrownBy(()->s.agregar(otro,archivo("XML",xml))).hasMessageContaining("UUID");
            var excel=mv.perform(get("/admin/compras/"+id+"/comprobantes/excel").session(session).param("documentoEstado","RETIRADOS").param("documentoTipo","XML")).andReturn().getResponse();assertThat(excel.getStatus()).isEqualTo(200);
            try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(excel.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);assertThat(libro.getSheetAt(0).getRow(1).getCell(4).getStringCellValue()).isEqualTo("Retirado");}
            SecurityContextHolder.getContext().setAuthentication(auth(97001,"COMPRA_ADMINISTRAR"));assertThatThrownBy(()->s.abrir(id,doc)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
            SecurityContextHolder.getContext().setAuthentication(auth(97000,"COMPRA_LEER"));assertThatThrownBy(()->s.agregar(id,archivo("PDF","%PDF-ficticio".getBytes()))).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
            assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isZero();
            assertThat(j.queryForObject("SELECT saldo_inicial FROM cuenta_financiera WHERE id=97000",BigDecimal.class)).isEqualByComparingTo("1000.00");
            assertThat(j.queryForObject("SELECT estado FROM compra WHERE id=?",String.class,id)).isEqualTo("BORRADOR");
        } finally {SecurityContextHolder.clearContext();}
    }
}
