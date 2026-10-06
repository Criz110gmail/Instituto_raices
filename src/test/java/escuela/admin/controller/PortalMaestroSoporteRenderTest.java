package escuela.admin.controller;

import escuela.docente.dto.MaestroResponse;
import escuela.docente.dto.PlaneacionFila;
import escuela.docente.dto.PlaneacionDocumento;
import escuela.docente.entity.EstadoPlaneacion;
import escuela.docente.controller.PortalMaestroController;
import escuela.docente.service.*;
import escuela.horario.service.HorarioClaseService;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.List;
import java.util.Set;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class PortalMaestroSoporteRenderTest {
    @Test void renderizaPortalCompartidoSinAccionesDeEscritura() throws Exception {
        var soporte = mock(SoportePortalMaestroService.class);
        var planes = mock(PlaneacionService.class);
        var admin = new UsuarioPrincipal(1L, 2L, Set.of(), true, false, "admin", "",
                List.of(new SimpleGrantedAuthority("PORTAL_MAESTRO_SOPORTE")));
        var docente = new UsuarioPrincipal(9L, 2L, Set.of(), false, false, "docente", "",
                List.of(new SimpleGrantedAuthority("PORTAL_MAESTRO_ACCEDER")));
        when(soporte.contexto(admin, 7L, "PLANEACIONES")).thenReturn(docente);
        when(planes.perfil(docente)).thenReturn(new MaestroResponse(7L, 2L, "Escuela de prueba", "M-7",
                "Ana", "Prueba", null, "Ana Prueba", "prueba@example.invalid", null,
                true, 9L, "docente", EstadoUsuario.ACTIVO, 0L));
        var inicio = LocalDate.of(2026, 10, 5); var fin = LocalDate.of(2026, 10, 9);
        when(planes.listarMaestro(eq(docente), any(), any(), any(), eq(0))).thenReturn(new PageImpl<>(List.of(
                new PlaneacionFila(10L, "Ana Prueba", "M-7", "Plantel de prueba", "A", "Primero", inicio,
                        fin, EstadoPlaneacion.BORRADOR, 0, 1, 0L)), PageRequest.of(0, 20), 21));
        var documento = mock(PlaneacionDocumento.class);
        when(documento.planeacionId()).thenReturn(10L); when(documento.grupoId()).thenReturn(4L);
        when(documento.grupo()).thenReturn("A"); when(documento.plantel()).thenReturn("Plantel de prueba");
        when(documento.ciclo()).thenReturn("2026-2027"); when(documento.estado()).thenReturn("Borrador");
        when(documento.fechaInicio()).thenReturn(inicio); when(documento.fechaFin()).thenReturn(fin);
        when(documento.proposito()).thenReturn("Propósito de prueba");
        when(documento.materias()).thenReturn(List.of(new PlaneacionDocumento.Materia(5L, "MAT", "Matemáticas")));
        when(soporte.contexto(admin, 7L, "CAPTURA_PLANEACION")).thenReturn(docente);
        when(planes.detalleMaestro(docente, 10L)).thenReturn(documento);
        var controller = new PortalMaestroSoporteAdminController(soporte, planes,
                mock(PortalAlumnoMaestroService.class), mock(FotografiaMaestroService.class),
                mock(HorarioClaseService.class), mock(PdfPlaneacionService.class));
        var templates = new ClassLoaderTemplateResolver();
        templates.setPrefix("templates/"); templates.setSuffix(".html"); templates.setCharacterEncoding("UTF-8");
        var engine = new SpringTemplateEngine(); engine.setTemplateResolver(templates);
        var views = new ThymeleafViewResolver(); views.setTemplateEngine(engine); views.setCharacterEncoding("UTF-8");
        var normal = new PortalMaestroController(planes, mock(PdfPlaneacionService.class),
                mock(PortalAlumnoMaestroService.class), mock(FotografiaMaestroService.class), mock(HorarioClaseService.class));
        var mvc = MockMvcBuilders.standaloneSetup(controller, normal).setViewResolvers(views)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, "", admin.authorities()));
        try {
            var respuesta = mvc.perform(get("/admin/portal-maestros-soporte/7")).andReturn().getResponse();
            assertThat(respuesta.getStatus()).isEqualTo(200);
            assertThat(respuesta.getContentAsString()).contains("Ana Prueba", "Cambiar maestro",
                    "Soporte administrativo", "href=\"/admin/portal-maestros-soporte/7/horario\"",
                    "pagina=1", "href=\"/admin/portal-maestros-soporte/7/planeaciones/10\"")
                    .doesNotContain("Nueva planeación", "Crear mi primera planeación", "action=\"/logout\"");
            var captura = mvc.perform(get("/admin/portal-maestros-soporte/7/planeaciones/10/captura"))
                    .andReturn().getResponse();
            assertThat(captura.getStatus()).isEqualTo(200);
            assertThat(org.unbescape.html.HtmlEscape.unescapeHtml(captura.getContentAsString())).contains("Propósito de prueba", "Matemáticas", "disabled=\"disabled\"")
                    .doesNotContain("Guardar planeación", "src=\"/js/planeacion-form.js\"");
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(docente, "", docente.authorities()));
            var real = mvc.perform(get("/maestros")).andReturn().getResponse();
            assertThat(real.getStatus()).isEqualTo(200);
            assertThat(real.getContentAsString()).contains("Nueva planeación", "Cerrar sesión", "href=\"/maestros/horario\"")
                    .doesNotContain("Soporte administrativo", "Cambiar maestro");
        } finally { SecurityContextHolder.clearContext(); }
    }
}
