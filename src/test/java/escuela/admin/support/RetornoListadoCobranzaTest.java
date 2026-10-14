package escuela.admin.support;

import escuela.admin.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class RetornoListadoCobranzaTest {
    @Test void conservaTodosLosFiltrosYPaginaSinAceptarDestinosExternos() {
        var f=new FiltroCatalogo("Ana & Luis","VALIDADO",null,2,50,java.time.LocalDate.of(2026,10,1),java.time.LocalDate.of(2026,10,31),"REGISTRO","ANUAL_LIQUIDADO",2027,"ANTICIPO-TEST-05");
        String url=RetornoListadoCobranza.listado(ModuloCatalogo.PAGOS,f);
        assertThat(url).contains("q=Ana%20%26%20Luis","pagina=2","tamanio=50","desde=2026-10-01","hasta=2026-10-31","conceptoCobro=ANTICIPO-TEST-05","cobertura=ANUAL_LIQUIDADO","anioCobertura=2027");
        assertThat(RetornoListadoCobranza.validar(url,"/admin/pagos/1/editar")).isEqualTo(url);
        for(String externo:new String[]{"https://evil.test/admin/catalogos/pagos","//evil.test/admin/catalogos/pagos","/admin/catalogos/cargos","/admin/catalogos/pagos#x","/admin/catalogos/%70agos","/admin/catalogos/pagos\r\nX: test"})
            assertThat(RetornoListadoCobranza.validar(externo,"/admin/pagos/1/editar")).isNull();
    }
    @Test void detalleErroresYRedireccionesConservanContextoSinContaminarOtrasPestanas()throws Exception {
        var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).addInterceptors(new RetornoListadoCobranza()).build();
        for(String slug:new String[]{"pagos","cargos","cuotas-alumno"}) {
            String retorno="/admin/catalogos/"+slug+"?q=Alumno&conceptoCobro=TEST&pagina=2&tamanio=50";
            var detalle=mvc.perform(get("/admin/"+slug+"/1/editar").param("retorno",retorno)).andReturn();
            assertThat(detalle.getModelAndView().getModel()).containsEntry("retornoListado",retorno);
            assertThat(mvc.perform(get("/admin/"+slug+"/1/editar")).andReturn().getModelAndView().getModel()).doesNotContainKey("retornoListado");
            var volver=mvc.perform(post("/admin/"+slug+"/1").param("retorno",retorno)).andReturn().getResponse();
            assertThat(volver.getRedirectedUrl()).isEqualTo(retorno);
            var accion=mvc.perform(post("/admin/"+slug+"/1/accion").param("retorno",retorno)).andReturn().getResponse();
            var destino=org.springframework.web.util.UriComponentsBuilder.fromUriString(accion.getRedirectedUrl()).build();
            String conservado=java.net.URLDecoder.decode(destino.getQueryParams().getFirst("retorno"),java.nio.charset.StandardCharsets.UTF_8);
            assertThat(conservado).isEqualTo(retorno);
        }
    }
    @Controller static class Pantalla {
        @GetMapping("/admin/{slug}/{id}/editar")String detalle(){return "pantalla";}
        @PostMapping("/admin/{slug}/{id}")String guardar(@PathVariable String slug){return "redirect:/admin/catalogos/"+slug;}
        @PostMapping("/admin/{slug}/{id}/accion")String accion(@PathVariable String slug,@PathVariable Long id){return "redirect:/admin/"+slug+"/"+id+"/editar";}
    }
}
