package escuela.config;

import escuela.admin.controller.CompraBusquedaController;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.compras.service.CompraBusquedaService;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class CompraBusquedaSeguridadTest {
    @Test void filtrosExigenLecturaDeComprasSinPermisoDeCaptura() throws Exception {
        try(var context=new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.register(LoginReinicioSeguridadTest.Config.class,SeguridadConfig.class,SesionExpiradaAccessDeniedHandler.class,CompraBusquedaController.class,Config.class);context.refresh();
            var mvc=MockMvcBuilders.webAppContextSetup(context).addFilters(context.getBean(FilterChainProxy.class)).build();
            var service=context.getBean(CompraBusquedaService.class);
            when(service.proveedoresFiltro(1L,"")).thenReturn(ResultadoAutocompletado.vacio());
            when(service.cuentasFiltro(1L,null,"")).thenReturn(ResultadoAutocompletado.vacio());
            for(String permiso:List.of("COMPRA_LEER","PROVEEDOR_ADMINISTRAR","MOVIMIENTO_FINANCIERO_LEER")) {
                var session=new MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(UsernamePasswordAuthenticationToken.authenticated("qa","",List.of(new SimpleGrantedAuthority(permiso)))));
                for(String tipo:List.of("proveedores","cuentas"))assertThat(mvc.perform(get("/admin/autocompletado/compras/filtros/"+tipo).param("institucionId","1").session(session)).andReturn().getResponse().getStatus()).isEqualTo(permiso.equals("COMPRA_LEER")?200:403);
            }
            verify(service).proveedoresFiltro(1L,"");verify(service).cuentasFiltro(1L,null,"");
        }
    }
    @Test void tresCatalogosExigenPermisoDeCapturaDeComprasNoOtroModulo() throws Exception {
        try(var context=new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.register(LoginReinicioSeguridadTest.Config.class,SeguridadConfig.class,SesionExpiradaAccessDeniedHandler.class,
                CompraBusquedaController.class,Config.class);context.refresh();
            var mvc=MockMvcBuilders.webAppContextSetup(context).addFilters(context.getBean(FilterChainProxy.class)).build();
            var service=context.getBean(CompraBusquedaService.class);
            when(service.proveedores(1L,"")).thenReturn(ResultadoAutocompletado.vacio());
            when(service.cuentas(1L,null,"")).thenReturn(ResultadoAutocompletado.vacio());
            when(service.motivos(1L,"")).thenReturn(ResultadoAutocompletado.vacio());
            for(String permiso:List.of("COMPRA_ADMINISTRAR","COMPRA_LEER","PROVEEDOR_ADMINISTRAR","MOVIMIENTO_FINANCIERO_REGISTRAR")) {
                var session=new MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(
                    UsernamePasswordAuthenticationToken.authenticated("qa","",List.of(new SimpleGrantedAuthority(permiso)))));
                for(String tipo:List.of("proveedores","cuentas","motivos")) {
                    var r=mvc.perform(get("/admin/autocompletado/compras/"+tipo).param("institucionId","1").session(session)).andReturn().getResponse();
                    assertThat(r.getStatus()).isEqualTo(permiso.equals("COMPRA_ADMINISTRAR")?200:403);
                }
            }
            verify(service).proveedores(1L,"");verify(service).cuentas(1L,null,"");verify(service).motivos(1L,"");
        }
    }
    @Configuration static class Config {@Bean CompraBusquedaService busqueda(){return mock(CompraBusquedaService.class);}}
}
