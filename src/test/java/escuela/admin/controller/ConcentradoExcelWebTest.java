package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.service.*;
import escuela.common.support.FormatoMoneda;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class ConcentradoExcelWebTest {
    @Test void rutaExportaConLosCuatroFiltrosYNombreXlsx() throws Exception {
        var consulta=mock(ReporteFinancieroConsultaService.class);
        var excel=mock(ExcelConcentradoCobranzaService.class);
        doAnswer(i->{((java.io.OutputStream)i.getArgument(1)).write(new byte[]{'P','K'});return null;}).when(excel).exportar(any(),any());
        when(consulta.normalizar(any(FiltroConcentradoCobranza.class))).thenAnswer(i->i.getArgument(0));
        var controlador=new ReporteFinancieroAdminController(consulta,mock(ExcelReporteFinancieroService.class),excel,
                mock(EstadoCuentaCuentaService.class),mock(ExcelEstadoCuentaCuentaService.class),mock(JasperReporteFinancieroService.class),
                mock(InstitucionService.class),mock(PlantelService.class),mock(AlcanceDatosService.class));
        var mvc=MockMvcBuilders.standaloneSetup(controlador).build();
        var respuesta=mvc.perform(get("/admin/reportes-financieros/concentrado-cobranza/excel")
                .param("institucionId","1").param("plantelId","3").param("agrupacion","PLANTEL").param("fechaCorte","2026-10-16"))
                .andReturn().getResponse();
        assertThat(respuesta.getStatus()).isEqualTo(200);
        assertThat(respuesta.getContentType()).isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        assertThat(respuesta.getHeader("Content-Disposition")).contains("concentrado-cobranza.xlsx");
        verify(excel).exportar(eq(new FiltroConcentradoCobranza(1L,3L,"PLANTEL",LocalDate.of(2026,10,16),0,100)),any());
    }

    @Test void plantillaEnlazaExcelConFiltrosActualesYFechaVisibleCompleta() throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(views).build();
        var contexto=mvc.getDispatcherServlet().getWebApplicationContext();
        var registrar=contexto.getClass().getDeclaredMethod("addBean",String.class,Object.class);
        registrar.setAccessible(true);registrar.invoke(contexto,"formatoMoneda",new FormatoMoneda());
        var respuesta=mvc.perform(get("/prueba/concentrado")).andReturn().getResponse();
        assertThat(respuesta.getStatus()).isEqualTo(200);
        assertThat(respuesta.getContentAsString()).contains("Exportar Excel","16/10/2026",
                "concentrado-cobranza/excel?institucionId=1&amp;plantelId=3&amp;agrupacion=PLANTEL&amp;fechaCorte=2026-10-16");
    }

    @Controller static class Pantalla {
        @GetMapping("/prueba/concentrado") String mostrar(Model model) {
            model.addAttribute("filtroConcentrado",new FiltroConcentradoCobranza(1L,3L,"PLANTEL",LocalDate.of(2026,10,16),0,25));
            model.addAttribute("resultadoConcentrado",new ResultadoConcentradoCobranza(Page.empty(),ResumenEstadoCuenta.vacio("MXN")));
            model.addAttribute("instituciones",List.of());model.addAttribute("planteles",List.of());model.addAttribute("modulos",List.of());
            return "admin/concentrado-cobranza";
        }
    }
}
