package escuela.portal.support;

import escuela.admin.dto.*;
import escuela.common.support.FormatoMoneda;
import escuela.portal.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.data.domain.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

class PortalPagosRenderTest {
    @Test void renderizaAbonoYDeudaSinConfundirPagoEnRevision() throws Exception {
        var cargo = new EstadoCuentaCargoFila(1L, "Centro", "2026-2027", "Colegiatura",
                "Octubre 2026", LocalDate.of(2026,10,1), LocalDate.of(2026,10,20),
                dinero("1000"), dinero("0"), dinero("1000"), dinero("300"), dinero("700"), "PARCIAL", "MXN");
        var pago = new PortalPagoFila(2L, "PAG-PRUEBA", LocalDateTime.of(2026,10,6,12,0),
                dinero("200"), "MXN", "TRANSFERENCIA", "PENDIENTE_VALIDACION", "Prueba", 1);
        String html = render(new PageImpl<>(List.of(cargo)), dinero("700"), dinero("0"), new PageImpl<>(List.of(pago)), false);
        assertThat(html).contains("Resumen de tu cuenta", "Lo que falta por pagar", "Tus pagos y comprobantes",
                "$1,000.00", "$300.00", "$700.00", "$200.00", "Con abonos", "En revisión",
                "Disponible al validar", "Octubre 2026", "20/10/2026")
                .doesNotContain("¡Estás al corriente!", "Ver comprobante PDF");
    }

    @Test void sinDeudaMuestraAlCorrienteYConservaHistorial() throws Exception {
        var pago = new PortalPagoFila(2L, "PAG-PRUEBA", LocalDateTime.of(2026,10,6,12,0),
                dinero("1000"), "MXN", "EFECTIVO", "VALIDADO", null, 0);
        assertThat(render(Page.empty(), dinero("0"), dinero("0"), new PageImpl<>(List.of(pago)), false))
                .contains("¡Estás al corriente!", "Ver comprobante PDF", "target=\"_blank\"", "Validado")
                .doesNotContain("family-charge-table", "No hay cargos registrados");
    }

    @Test void paginaFueraDeRangoNoDiceAlCorrienteYSoporteConservaSuRuta() throws Exception {
        var pagina = new PageImpl<EstadoCuentaCargoFila>(List.of(), PageRequest.of(2,10), 11);
        assertThat(render(pagina, dinero("700"), dinero("700"), Page.empty(), true))
                .contains("No hay cargos en esta página", "Volver a la primera página", "están vencidos",
                        "/admin/portal-soporte/7/pagos")
                .doesNotContain("¡Estás al corriente!", "Reportar transferencia");
    }

    @Test void pagoRechazadoOfreceMotivoEscapadoSinOfrecerloParaValidados() throws Exception {
        var rechazado=new PortalPagoFila(9L,"PAG-PRUEBA",LocalDateTime.of(2026,10,7,12,0),
                dinero("400"),"MXN","TRANSFERENCIA","RECHAZADO","TEST-RECHAZO-400",1,
                "El comprobante es incorrecto. <script>prueba</script>\nAdjunta el correcto.");
        var html=render(Page.empty(),dinero("400"),dinero("0"),new PageImpl<>(List.of(rechazado)),false);
        assertThat(html).contains("Ver motivo del rechazo","data-ver-rechazo","data-motivo=",
                "&lt;script&gt;","aria-haspopup=\"dialog\"","motivo-rechazo-modal","no hagas otra transferencia bancaria")
                .doesNotContain("<script>prueba</script>");
        assertThat(render(Page.empty(),dinero("400"),dinero("0"),new PageImpl<>(List.of(rechazado)),true))
                .contains("Ver motivo del rechazo");
        var validado=new PortalPagoFila(10L,"PAG-VALIDADO",LocalDateTime.of(2026,10,7,12,0),
                dinero("400"),"MXN","TRANSFERENCIA","VALIDADO",null,1);
        assertThat(render(Page.empty(),dinero("0"),dinero("0"),new PageImpl<>(List.of(validado)),false))
                .doesNotContain("data-ver-rechazo");
    }

    private String render(Page<EstadoCuentaCargoFila> cargos, BigDecimal saldo, BigDecimal vencido,
                          Page<PortalPagoFila> pagos, boolean soporte) throws Exception {
        String template = Files.readString(Path.of("src/main/resources/templates/portal/seccion.html"));
        int inicio = template.indexOf("<th:block th:if=\"${seccion=='PAGOS'}\">");
        template = template.substring(inicio, template.indexOf("</th:block>", inicio) + "</th:block>".length());
        var hijo = new PortalHijoResumen(20L, "A-020", "Ana Prueba", "MADRE", true,
                true, true, true, "Centro", "2026-2027", "Primero", "A");
        var cuenta = new ResultadoEstadoCuentaAlumno(20L, "Ana Prueba", "A-020", cargos,
                new ResumenEstadoCuenta(2, dinero("1000"), dinero("300"), saldo, vencido, "MXN"));
        var portal = new PortalTutorResultado("Familia Prueba", "Escuela", List.of(hijo), hijo,
                cuenta, Page.empty(), Page.empty(), new PortalNotificaciones(0, Page.empty()), pagos);
        var app = new StaticApplicationContext();
        app.getBeanFactory().registerSingleton("formatoMoneda", new FormatoMoneda());
        var context = new Context();
        context.setVariable("portal", portal); context.setVariable("seccion", "PAGOS");
        context.setVariable("soporte", soporte);
        context.setVariable("rutaInicio", soporte ? "/admin/portal-soporte/7" : "/portal");
        context.setVariable("rutaSeccion", soporte ? "/admin/portal-soporte/7/pagos" : "/portal/pagos");
        context.setVariable("aniosPago", List.of(2026)); context.setVariable("nombreMesPago", "Todos los meses");
        context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                new ThymeleafEvaluationContext(app, null));
        var engine = new SpringTemplateEngine(); engine.setTemplateResolver(new StringTemplateResolver());
        return engine.process(template, context);
    }
    private BigDecimal dinero(String valor) { return new BigDecimal(valor); }
}
