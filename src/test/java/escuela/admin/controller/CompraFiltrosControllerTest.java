package escuela.admin.controller;

import escuela.compras.dto.*;
import escuela.compras.entity.EstadoCompra;
import escuela.compras.service.*;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.math.BigDecimal;
import java.time.*;
import java.nio.file.*;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class CompraFiltrosControllerTest {
    @Test void seleccionRemotaSeConservaEnPantallaPaginacionYExportacionSinListasCompletas() throws Exception {
        var busqueda=mock(CompraBusquedaService.class);var service=mock(CompraService.class);var excel=mock(ExcelCompraService.class);
        var instituciones=mock(InstitucionService.class);var planteles=mock(PlantelService.class);var alcance=mock(AlcanceDatosService.class);
        var institucion=mock(InstitucionResponse.class);when(institucion.id()).thenReturn(1L);when(institucion.nombre()).thenReturn("Escuela de prueba");
        when(instituciones.listar()).thenReturn(List.of(institucion));when(alcance.filtrarInstituciones(anyList())).thenReturn(List.of(institucion));
        when(planteles.listar()).thenReturn(List.of());when(alcance.filtrarPlanteles(anyList())).thenReturn(List.of());
        when(busqueda.proveedorFiltroSeleccionado(1L,3L)).thenReturn("Proveedor de prueba");when(busqueda.cuentaFiltroSeleccionada(1L,2L,4L)).thenReturn("CUENTA-PRUEBA · Cuenta de prueba");
        var filtro=new FiltroCompra(1L,2L,3L,4L,EstadoCompra.CANCELADA,LocalDate.of(2026,10,16),LocalDate.of(2026,10,16),"COM-00000001",1,10);
        var fila=new CompraFila(1L,"COM-00000001",Instant.parse("2026-10-16T23:58:00Z"),"Proveedor de prueba",null,"Plantel de prueba","Cuenta de prueba","TICKET-PRUEBA-001",new BigDecimal("406.00"),EstadoCompra.CANCELADA,4L,0L);
        when(service.listar(filtro)).thenReturn(new PageImpl<>(List.of(fila),PageRequest.of(1,10),21));
        var controller=new CompraAdminController(busqueda,service,excel,instituciones,planteles,alcance);
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);var vistas=new ThymeleafViewResolver();vistas.setTemplateEngine(engine);vistas.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(controller).setViewResolvers(vistas).build();var ctx=mvc.getDispatcherServlet().getWebApplicationContext();var add=ctx.getClass().getDeclaredMethod("addBean",String.class,Object.class);add.setAccessible(true);add.invoke(ctx,"formatoMoneda",new escuela.common.support.FormatoMoneda());
        var auth=UsernamePasswordAuthenticationToken.authenticated("usuario.prueba","",List.of(new SimpleGrantedAuthority("COMPRA_LEER")));
        var r=mvc.perform(get("/admin/compras").principal(auth).param("institucionId","1").param("plantelId","2").param("proveedorId","3").param("cuentaId","4").param("estado","CANCELADA").param("desde","2026-10-16").param("hasta","2026-10-16").param("texto","COM-00000001").param("pagina","1").param("tamanio","10")).andReturn().getResponse();
        assertThat(r.getStatus()).isEqualTo(200);String html=r.getContentAsString();
        assertThat(html).contains("/admin/autocompletado/compras/filtros/proveedores","/admin/autocompletado/compras/filtros/cuentas","value=\"Proveedor de prueba\"","value=\"CUENTA-PRUEBA · Cuenta de prueba\"","name=\"proveedorId\" value=\"3\"","name=\"cuentaId\" value=\"4\"","proveedorId=3","cuentaId=4","pagina=2");
        assertThat(html).doesNotContain("<select name=\"proveedorId\"","<select name=\"cuentaId\"");
        verify(service).listar(filtro);verify(service,never()).cuentasActivas(anyLong());
        mvc.perform(get("/admin/compras/excel").param("institucionId","1").param("plantelId","2").param("proveedorId","3").param("cuentaId","4").param("estado","CANCELADA").param("desde","2026-10-16").param("hasta","2026-10-16").param("texto","COM-00000001"));
        verify(excel).exportar(eq(filtro.conPagina(0,100)),any());
        var dir=Path.of("target/qa-compras");Files.createDirectories(dir);Files.writeString(dir.resolve("listado.html"),html);
    }
}
