package escuela.admin.controller;

import escuela.finanzas.dto.response.CorteCajaResponse;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class CorteCajaObservacionesRenderTest {
    @Test void observacionesSonTarjetaDeConsultaConTextoEscapadoYSinFormularioDeCierre()throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(views).build();
        var html=mvc.perform(get("/prueba/corte-nota")).andReturn().getResponse().getContentAsString();
        assertThat(html).contains("cash-opening-note","Registro de apertura","Observaciones de apertura","Información de consulta","1000","&lt;script&gt;")
            .doesNotContain("<form","<textarea","<script>");
        var vacio=mvc.perform(get("/prueba/corte-nota").param("tiene","false")).andReturn().getResponse().getContentAsString();
        assertThat(vacio).doesNotContain("cash-opening-note");
    }
    @Controller static class Pantalla {
        @GetMapping("/prueba/corte-diferencia")String diferencia(@RequestParam(defaultValue="0")String diferencia,Model model){
            boolean abierto=diferencia.equals("pendiente");
            model.addAttribute("fila",new escuela.admin.dto.CorteCajaFila(7L,"Caja","Plantel",abierto?"ABIERTO":"CERRADO","Fecha","Admin",null,new java.math.BigDecimal("1000"),abierto?null:new java.math.BigDecimal("1150"),null,abierto?null:new java.math.BigDecimal(diferencia),"MXN"));
            return "admin/cortes-caja :: cutDifference";
        }
        @GetMapping("/prueba/corte-cerrado")String cerrado(@RequestParam(defaultValue="0")String diferencia,@RequestParam(defaultValue="CERRADO")String estado,Model model){
            var corte=mock(CorteCajaResponse.class);
            when(corte.estado()).thenReturn(escuela.finanzas.entity.EstadoCorteCaja.valueOf(estado));
            when(corte.institucionId()).thenReturn(1L);when(corte.cuentaId()).thenReturn(2L);
            when(corte.cuentaCodigo()).thenReturn("CAJA-CORTE-TEST");when(corte.cuentaNombre()).thenReturn("Caja prueba");
            when(corte.diferencia()).thenReturn(new java.math.BigDecimal(diferencia));
            when(corte.saldoEsperado()).thenReturn(new java.math.BigDecimal("1150"));
            when(corte.efectivoDeclarado()).thenReturn(new java.math.BigDecimal("1150").add(new java.math.BigDecimal(diferencia)));
            when(corte.totalIngresos()).thenReturn(new java.math.BigDecimal("200"));
            when(corte.totalEgresos()).thenReturn(new java.math.BigDecimal("50"));
            when(corte.movimientosContabilizados()).thenReturn(2L);when(corte.secuenciaInicial()).thenReturn(0L);when(corte.secuenciaFinal()).thenReturn(2L);
            when(corte.cerradoPor()).thenReturn("criz");
            when(corte.justificacionDiferencia()).thenReturn(diferencia.equals("0")?null:"Diferencia justificada");
            when(corte.observacionesCierre()).thenReturn("Prueba de cierre sin diferencia <script>texto</script>");
            model.addAttribute("corte",corte);model.addAttribute("cerradoEnLocal","15 oct 2026 · 15:53");
            return "admin/corte-caja-detalle :: closedSummary";
        }
        @GetMapping("/prueba/corte-cierre")String cierre(@RequestParam(defaultValue="ABIERTO")String estado,@RequestParam(defaultValue="true")boolean puede,Model model){
            var corte=mock(CorteCajaResponse.class);
            when(corte.id()).thenReturn(7L);
            when(corte.estado()).thenReturn(escuela.finanzas.entity.EstadoCorteCaja.valueOf(estado));
            when(corte.cuentaCodigo()).thenReturn("CAJA-CORTE-TEST");
            when(corte.cuentaNombre()).thenReturn("Caja <prueba>");
            when(corte.plantel()).thenReturn("Plantel de prueba");
            when(corte.moneda()).thenReturn("MXN");
            var form=new escuela.admin.dto.CierreCorteCajaForm();form.setVersion(2L);
            model.addAttribute("corte",corte);model.addAttribute("form",form);model.addAttribute("puedeAdministrar",puede);
            return "admin/corte-caja-detalle :: closingForm";
        }
        @GetMapping("/prueba/corte-action")String accion(@RequestParam(defaultValue="ABIERTO")String estado,@RequestParam(defaultValue="true")boolean puede,Model model){
            model.addAttribute("fila",new escuela.admin.dto.CorteCajaFila(7L,"Caja","Plantel",estado,"Fecha","Admin",null,null,null,null,null,"MXN"));
            model.addAttribute("puedeAdministrar",puede);return "admin/cortes-caja :: cutAction";
        }
        @GetMapping("/prueba/corte-nota")String mostrar(@RequestParam(defaultValue="true")boolean tiene,Model model){
            var corte=mock(CorteCajaResponse.class);
            when(corte.observacionesApertura()).thenReturn(tiene?"Prueba de corte con saldo inicial de 1000, ingreso de 200 y egreso de 50.\n<script>Texto de prueba</script>":null);
            model.addAttribute("corte",corte);return "admin/corte-caja-detalle :: openingNotes";
        }
    }

    @Test void listadoDistingueDiferenciaDelSaldoEsperadoIncluyendoCeroYPendiente()throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(views).build();
        var ctx=mvc.getDispatcherServlet().getWebApplicationContext();var add=ctx.getClass().getDeclaredMethod("addBean",String.class,Object.class);
        add.setAccessible(true);add.invoke(ctx,"formatoMoneda",new escuela.common.support.FormatoMoneda());
        for(String valor:new String[]{"0","-1","1","pendiente"}) {
            String html=mvc.perform(get("/prueba/corte-diferencia").param("diferencia",valor)).andReturn().getResponse().getContentAsString();
            assertThat(html).contains("data-label=\"Diferencia del cierre\"").doesNotContain("data-label=\"Resultado\"");
            if(valor.equals("pendiente"))assertThat(html).contains("Pendiente").doesNotContain("Esperado:","Sin diferencia");
            else {
                assertThat(html).contains("Esperado: $1,150.00");
                if(valor.equals("0"))assertThat(html).contains("Sin diferencia · $0.00");
                else assertThat(html).contains(valor.equals("-1")?"-$1.00":"$1.00").doesNotContain("Sin diferencia");
            }
        }
        try(var input=getClass().getClassLoader().getResourceAsStream("templates/admin/cortes-caja.html")) {
            assertThat(new String(input.readAllBytes(),java.nio.charset.StandardCharsets.UTF_8)).contains("<th>Diferencia del cierre</th>").doesNotContain("<th>Resultado</th>");
        }
    }

    @Test void resultadoCerradoUsaTarjetasCompartidasImportesClarosNotasEscapadasYBotones()throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(views).build();
        var ctx=mvc.getDispatcherServlet().getWebApplicationContext();var add=ctx.getClass().getDeclaredMethod("addBean",String.class,Object.class);
        add.setAccessible(true);add.invoke(ctx,"formatoMoneda",new escuela.common.support.FormatoMoneda());
        for(String diferencia:new String[]{"0","-1","1"}) {
            var response=mvc.perform(get("/prueba/corte-cerrado").param("diferencia",diferencia)).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            String html=response.getContentAsString();
            assertThat(html).contains("payment-detail-grid cash-close-totals","payment-detail-grid cash-close-record","cash-close-difference","Corte de caja cerrado","$1,150.00","$200.00","$50.00","Folios 1 a 2","Cerrado por criz","15 oct 2026","class=\"detail-close-button\"","class=\"payment-list-button\"","href=\"/admin/cortes-caja\"","/admin/movimientos-financieros?institucionId=1&amp;cuentaId=2","&lt;script&gt;")
                .doesNotContain("<form","<script>");
            if(diferencia.equals("0"))assertThat(html).contains("Sin diferencia:","$0.00").doesNotContain("Justificación de la diferencia");
            else assertThat(html).contains(diferencia.equals("-1")?"Faltante:":"Sobrante:","Justificación de la diferencia","Diferencia justificada");
        }
        assertThat(mvc.perform(get("/prueba/corte-cerrado").param("estado","ABIERTO")).andReturn().getResponse().getContentAsString()).doesNotContain("cash-closed-summary");
    }

    @Test void cierreUsaModalComunConDatosEscapadosYBotonCerrarDespuesSoloSiAbiertoConPermiso()throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(views).build();
        for(String estado:new String[]{"ABIERTO","CERRADO"})for(boolean puede:new boolean[]{true,false}) {
            String html=mvc.perform(get("/prueba/corte-cierre").param("estado",estado).param("puede",String.valueOf(puede))).andReturn().getResponse().getContentAsString();
            if(estado.equals("ABIERTO")&&puede)assertThat(html).contains("data-money-confirm=\"cierre-caja\"","data-confirm-corte=\"7\"","data-confirm-currency=\"MXN\"","Caja &lt;prueba&gt;","action=\"/admin/cortes-caja/7/cerrar\"","name=\"version\"","name=\"claveIdempotencia\"","class=\"detail-close-button\" href=\"/admin/cortes-caja\">Cerrar después</a>","name=\"efectivoDeclarado\"")
                .doesNotContain("data-confirm-expected","data-confirm-difference");
            else assertThat(html).doesNotContain("<form","data-money-confirm");
        }
    }

    @Test void accionTieneDisenoComunYDistingueConteoDeConsultaSegunEstadoYPermiso()throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(views).build();
        for(String estado:new String[]{"ABIERTO","CERRADO"})for(boolean puede:new boolean[]{true,false}) {
            String html=mvc.perform(get("/prueba/corte-action").param("estado",estado).param("puede",String.valueOf(puede))).andReturn().getResponse().getContentAsString();
            assertThat(html).contains("payment-list-button cash-cut-action","href=\"/admin/cortes-caja/7\"","aria-hidden=\"true\"");
            if(estado.equals("ABIERTO")&&puede)assertThat(html).contains("cash-cut-action-close","Contar y cerrar");
            else assertThat(html).contains("cash-cut-action-view","Ver detalle").doesNotContain("Contar y cerrar");
        }
    }
}
