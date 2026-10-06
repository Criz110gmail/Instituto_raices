package escuela.admin.controller;
import escuela.cobranza.dto.response.CargoResponse;
import escuela.cobranza.entity.EstadoRegistroCargo;
import escuela.cobranza.service.*;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.support.FormatoMoneda;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
class CargoCorreccionRenderTest {
    @Test void formularioYValidacionRenderizanSinGuardarHastaConfirmar() throws Exception {
        var cargos=mock(CargoService.class);var correcciones=mock(CargoCorreccionService.class);
        var cargo=mock(CargoResponse.class);
        when(cargo.id()).thenReturn(18L);when(cargo.alumnoNombre()).thenReturn("Alumno de prueba");when(cargo.conceptoNombre()).thenReturn("Prueba tope");
        when(cargo.estadoRegistro()).thenReturn(EstadoRegistroCargo.CANCELADO);when(cargo.importeOriginal()).thenReturn(new BigDecimal("1000.00"));when(cargo.moneda()).thenReturn("MXN");
        when(cargo.fechaVencimiento()).thenReturn(LocalDate.of(2026,10,10));when(cargo.periodoCobroInicio()).thenReturn(LocalDate.of(2026,10,1));when(cargo.periodoCobroFin()).thenReturn(LocalDate.of(2027,6,15));
        when(cargo.auditoria()).thenReturn(new AuditoriaResponse(null,1L,null,1L,0L));when(cargos.obtener(18L)).thenReturn(cargo);
        when(correcciones.enlaces(18L)).thenReturn(new CargoCorreccionService.Enlaces(null,null,null));when(correcciones.fechaSugerida(18L)).thenReturn(LocalDate.of(2026,10,5));
        var templates=new ClassLoaderTemplateResolver();templates.setPrefix("templates/");templates.setSuffix(".html");templates.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(templates);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new CargoCorreccionAdminController(cargos,correcciones,mock(AlcanceDatosService.class))).setViewResolvers(views).build();
        // El contexto standalone de Spring es package-private; registra el helper sólo en la prueba.
        var contexto=mvc.getDispatcherServlet().getWebApplicationContext();
        var registrar=contexto.getClass().getDeclaredMethod("addBean",String.class,Object.class);
        registrar.setAccessible(true);registrar.invoke(contexto,"formatoMoneda",new FormatoMoneda());
        var html=mvc.perform(get("/admin/cargos/18/corregir")).andReturn().getResponse().getContentAsString();
        assertThat(html).contains("Corregir y reemplazar cargo","10/10/2026","value=\"2026-10-05\"","$1,000.00","Confirmar y reemplazar cargo","form-section");
        var error=mvc.perform(post("/admin/cargos/18/corregir").param("version","0").param("fechaVencimiento","2026-10-05").param("motivo","Error de fecha")).andReturn().getResponse().getContentAsString();
        assertThat(error).contains("Confirma que revisaste los datos");verify(correcciones,never()).reemplazar(any(),any(),any(),any());
    }
}
