package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.service.*;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class CatalogoRangosRenderTest {
    @Test void tresModulosRenderizanRangoYLoConservanEnExcelYPaginacion() throws Exception {
        var consulta=mock(CatalogoConsultaService.class);
        when(consulta.consultar(any(),any())).thenAnswer(i->{
            ModuloCatalogo m=i.getArgument(0);
            var fila=new FilaCatalogo(1L,m.columnas().stream().map(c->"Dato").toList(),"Pendiente","aviso");
            return new ResultadoCatalogo(m,m.columnas(),new PageImpl<>(List.of(fila),PageRequest.of(0,10),11));
        });
        var templates=new ClassLoaderTemplateResolver();templates.setPrefix("templates/");templates.setSuffix(".html");templates.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(templates);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new CatalogoAdminController(consulta,mock(ExcelCatalogoService.class))).setViewResolvers(views).build();
        var auth=new TestingAuthenticationToken("admin","","CARGO_LEER","CUOTA_ALUMNO_LEER","PAGO_LEER");
        for(String slug:new String[]{"cargos","cuotas-alumno","pagos"}) {
            var response=mvc.perform(get("/admin/catalogos/"+slug).principal(auth).param("desde","2026-10-01").param("hasta","2026-10-31").param("tipoFecha","REGISTRO").param("tamanio","10")).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Filtrar por fechas","Mes anterior","desde=2026-10-01","hasta=2026-10-31","tipoFecha=REGISTRO","pagina=1");
            assertThat(response.getContentAsString()).contains("class=\"catalogo-filter-actions\"");
        }
        verify(consulta,times(3)).consultar(any(),argThat(f->LocalDate.of(2026,10,1).equals(f.desde())&&LocalDate.of(2026,10,31).equals(f.hasta())));
    }
}
