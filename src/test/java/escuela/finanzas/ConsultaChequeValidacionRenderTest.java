package escuela.finanzas;

import escuela.admin.controller.ConsultaChequeController;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.FiltroCheque;
import escuela.finanzas.service.ChequeraService;
import escuela.finanzas.service.ConsultaChequeService;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class ConsultaChequeValidacionRenderTest {
    @Test void fechasInvertidasConservanCapturaYUnSoloAvisoDentroDeFiltros() throws Exception {
        var consulta = mock(ConsultaChequeService.class);
        var chequeras = mock(ChequeraService.class);
        when(chequeras.cuenta(1L)).thenReturn(new ChequeraService.Cuenta(1L, 1L, "BANCO-QA", "Banco ficticio", true));
        when(consulta.listar(eq(1L), any())).thenAnswer(invocacion -> {
            FiltroCheque f = invocacion.getArgument(1);
            if (f.desde() != null && f.hasta() != null && f.desde().isAfter(f.hasta())) {
                throw new ReglaNegocioException("La fecha inicial de emisión no puede ser posterior a la final.");
            }
            return Page.empty();
        });
        when(consulta.totales(eq(1L), any())).thenReturn(new ConsultaChequeService.Totales(0, 0, BigDecimal.ZERO, BigDecimal.ZERO));
        var plantillas = new ClassLoaderTemplateResolver();
        plantillas.setPrefix("templates/"); plantillas.setSuffix(".html"); plantillas.setCharacterEncoding("UTF-8");
        var engine = new SpringTemplateEngine(); engine.setTemplateResolver(plantillas);
        var vistas = new ThymeleafViewResolver(); vistas.setTemplateEngine(engine); vistas.setCharacterEncoding("UTF-8");
        var mvc = MockMvcBuilders.standaloneSetup(new ConsultaChequeController(consulta, chequeras)).setViewResolvers(vistas).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("qa-ficticio", "", List.of(new SimpleGrantedAuthority("CHEQUERA_LEER"))));
        try {
            var respuesta = mvc.perform(get("/admin/cuentas-financieras/1/chequeras/cheques")
                    .param("desde", "2026-11-04").param("hasta", "2026-11-03").param("q", "Ficticio").param("estado", "CANCELADO")).andReturn().getResponse();
            assertThat(respuesta.getStatus()).isEqualTo(200);
            var html = respuesta.getContentAsString();
            var inicio = html.indexOf("<form method=\"get\"");
            var fin = html.indexOf("</form>", inicio);
            var filtros = html.substring(inicio, fin);
            assertThat(filtros).contains("data-nomina-filter-error", "role=\"alert\"", "tabindex=\"-1\"",
                    "Revisa los filtros", "La fecha inicial de emisión", "value=\"2026-11-04\"", "value=\"2026-11-03\"", "value=\"Ficticio\"");
            assertThat(html.split("data-nomina-filter-error", -1)).hasSize(2);
            assertThat(html).contains("nomina-filtros.js?v=20261106-scroll");
            var dir = Files.createDirectories(Path.of("target/qa-cheques-validacion"));
            Files.writeString(dir.resolve("error.html"), html);
            verify(consulta, never()).totales(eq(1L), any());
            var valida = mvc.perform(get("/admin/cuentas-financieras/1/chequeras/cheques")
                    .param("desde", "2026-11-03").param("hasta", "2026-11-04")).andReturn().getResponse();
            assertThat(valida.getStatus()).isEqualTo(200);
            assertThat(valida.getContentAsString()).doesNotContain("data-nomina-filter-error");
            Files.writeString(dir.resolve("valida.html"), valida.getContentAsString());
        } finally { SecurityContextHolder.clearContext(); }
    }
}
