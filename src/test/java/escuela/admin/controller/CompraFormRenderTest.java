package escuela.admin.controller;

import escuela.compras.dto.*;
import escuela.compras.entity.EstadoCompra;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.*;
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

class CompraFormRenderTest {
    @Test void detalleMuestraContadorYConfirmacionSoloEnBorradorConPermiso() throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var vistas=new ThymeleafViewResolver();vistas.setTemplateEngine(engine);vistas.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(vistas).build();
        var ctx=mvc.getDispatcherServlet().getWebApplicationContext();var add=ctx.getClass().getDeclaredMethod("addBean",String.class,Object.class);
        add.setAccessible(true);add.invoke(ctx,"formatoMoneda",new escuela.common.support.FormatoMoneda());
        for(String estado:List.of("BORRADOR","CONFIRMADA","CANCELADA"))for(boolean permiso:List.of(true,false)) {
            var r=mvc.perform(get("/qa/compra-detalle").param("estado",estado).param("permiso",String.valueOf(permiso))).andReturn().getResponse();
            assertThat(r.getStatus()).isEqualTo(200);String html=r.getContentAsString();
            assertThat(html).contains("class=\"purchase-line-count\"","aria-label=\"2 partidas registradas\"","id=\"compra-partidas\"");
            assertThat(html).contains("data-student-tab=\"resumen\"","data-student-tab=\"productos\"","data-student-tab=\"gestion\"","data-student-panel=\"gestion\"");
            if(estado.equals("BORRADOR")&&permiso)assertThat(html).contains("data-money-confirm=\"compra-egreso\"","data-confirm-amount=\"406.00\"","data-confirm-account=\"Cuenta de prueba\"");
            else assertThat(html).doesNotContain("data-money-confirm=\"compra-egreso\"");
            if(permiso){var dir=Path.of("target/qa-compras");Files.createDirectories(dir);Files.writeString(dir.resolve(estado.equals("BORRADOR")?"detalle.html":"detalle-"+estado.toLowerCase()+".html"),html.replace("action=\"/admin/","action=\"/qa-no-guardar/"));}
            if(estado.equals("CONFIRMADA")&&permiso)assertThat(html).contains("Cancelación controlada","Cancelar mediante reversa","data-money-confirm=\"compra-cancelacion\"","data-confirm-amount=\"406.00\"");
            else assertThat(html).doesNotContain("data-money-confirm=\"compra-cancelacion\"");
        }
    }
    @Test void formulariosRealesRenderizanAltaEdicionYSeleccionRecuperadaSinCatalogosCompletos() throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var vistas=new ThymeleafViewResolver();vistas.setTemplateEngine(engine);vistas.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(vistas).build();
        var dir=Path.of("target/qa-compras");Files.createDirectories(dir);
        for(String tipo:List.of("compra","proveedor")) for(boolean edicion:List.of(false,true)) {
            var response=mvc.perform(get("/qa/compra-form").param("tipo",tipo).param("edicion",String.valueOf(edicion))).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);String html=response.getContentAsString();
            assertThat(html).contains("data-money-confirm=\""+(tipo.equals("compra")?"compra-borrador":"proveedor")+"\"","/js/money-input.js");
            if(tipo.equals("compra"))assertThat(html).contains("value=\"Papelería de prueba\"","name=\"proveedorId\"","data-refresh-on-focus=\"true\"","value=\"3\"")
                .doesNotContain("<select id=\"proveedorId\"","<select id=\"cuentaId\"","<select id=\"motivoFinancieroId\"");
            if(tipo.equals("compra"))assertThat(html).contains("data-student-tab=\"datos\"","data-student-tab=\"partidas\"","class=\"purchase-secondary-button\"");
            Files.writeString(dir.resolve(tipo+(edicion?"-editar":"-nuevo")+".html"),html.replace("action=\"/admin/","action=\"/qa-no-guardar/"));
        }
        // Pantallas ficticias estáticas para QA visual; no sesión ni servidor de negocio.
        for(String folder:List.of("css","js","fonts")) {
            var origin=Path.of("src/main/resources/static",folder);if(!Files.exists(origin))continue;
            try(var paths=Files.walk(origin)){for(var p:paths.toList()){var destino=dir.resolve(folder).resolve(origin.relativize(p));if(Files.isDirectory(p))Files.createDirectories(destino);else Files.copy(p,destino,StandardCopyOption.REPLACE_EXISTING);}}
        }
    }
    @Controller static class Pantalla {
        @GetMapping("/qa/compra-detalle") String detalle(@RequestParam EstadoCompra estado,@RequestParam boolean permiso,Model m) {
            var partidas=List.of(new CompraDetalleVista.Partida("Papel de prueba",new BigDecimal("2"),new BigDecimal("100.00"),new BigDecimal("16"),new BigDecimal("200.00"),new BigDecimal("32.00"),new BigDecimal("232.00")),
                new CompraDetalleVista.Partida("Lápices de prueba",new BigDecimal("3"),new BigDecimal("50.00"),new BigDecimal("16"),new BigDecimal("150.00"),new BigDecimal("24.00"),new BigDecimal("174.00")));
            m.addAttribute("detalle",new CompraDetalleVista(7L,"COM-00000007",1L,"Escuela de prueba",2L,"Plantel de prueba","Proveedor de prueba",null,"Cuenta de prueba","Papelería",java.time.Instant.parse("2026-10-16T18:00:00Z"),"TICKET-PRUEBA-001","Sólo datos ficticios",new BigDecimal("350.00"),new BigDecimal("56.00"),new BigDecimal("406.00"),estado,null,null,estado==EstadoCompra.CANCELADA?java.time.Instant.parse("2026-10-16T19:00:00Z"):null,"usuario.prueba","Cancelación ficticia",0L,partidas));
            m.addAttribute("modulos",List.of());m.addAttribute("moduloActual",escuela.admin.dto.ModuloCatalogo.COMPRAS);
            var cancelacion=new CancelarCompraForm();cancelacion.setVersion(0L);m.addAttribute("cancelacion",cancelacion);
            m.addAttribute("puedeAdministrar",true);m.addAttribute("puedeConfirmar",permiso);m.addAttribute("puedeCancelar",permiso);m.addAttribute("usuarioSesion","usuario.prueba");
            return "admin/compra-detalle";
        }
        @GetMapping("/qa/compra-form") String mostrar(@RequestParam String tipo,@RequestParam boolean edicion,Model m) {
            m.addAttribute("edicion",edicion);m.addAttribute("id",7L);m.addAttribute("usuarioSesion","usuario.prueba");
            m.addAttribute("instituciones",List.of(Map.of("id",1L,"nombre","Escuela de prueba")));
            m.addAttribute("planteles",List.of(Map.of("id",2L,"nombre","Plantel de prueba","institucionId",1L)));
            if(tipo.equals("compra")){
                var f=new CompraForm();f.setInstitucionId(1L);f.setPlantelId(2L);f.setProveedorId(3L);f.setCuentaId(4L);f.setMotivoFinancieroId(5L);f.setVersion(0L);
                f.setFechaOperacion(LocalDateTime.of(2026,10,16,12,0));f.setDocumentoReferencia("TICKET-PRUEBA-001");f.setObservaciones("Compra ficticia sin operaciones reales");
                var p=new CompraPartidaForm();p.setDescripcion("Paquetes de papel de prueba");p.setCantidad(new BigDecimal("2"));p.setPrecioUnitario(new BigDecimal("100"));p.setPorcentajeImpuesto(new BigDecimal("16"));f.getPartidas().add(p);
                m.addAttribute("form",f);m.addAttribute("proveedorTexto","Papelería de prueba");m.addAttribute("cuentaTexto","CUENTA-PRUEBA · Cuenta de prueba");m.addAttribute("motivoTexto","PAPEL · Papelería de prueba");
                return "admin/compra-form";
            }
            var f=new ProveedorForm();f.setInstitucionId(1L);f.setRazonSocial("Papelería de prueba");f.setActivo(true);f.setVersion(0L);m.addAttribute("form",f);return "admin/proveedor-form";
        }
    }
}
