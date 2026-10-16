package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.seguridad.dto.response.UsuarioResponse;
import escuela.seguridad.entity.*;
import escuela.common.dto.response.AuditoriaResponse;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class UsuarioOrdenRenderTest {
    @Test void identidadAutorizacionYActivacionEnOrdenConEnlaceAlFinal() throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var vistas=new ThymeleafViewResolver();vistas.setTemplateEngine(engine);vistas.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(vistas).build();
        for(String estado:List.of("INVITADO","ACTIVO","INACTIVO")) {
            var response=mvc.perform(get("/qa/usuario").param("estado",estado)).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);String html=response.getContentAsString();
            int identidad=html.indexOf("Paso 1 · Identidad"),roles=html.indexOf("Paso 2 · Autorización"),activar=html.indexOf("Paso 3 · Estado y activación");
            assertThat(identidad).isPositive();assertThat(roles).isGreaterThan(identidad);assertThat(activar).isGreaterThan(roles);
            assertThat(html).contains("role=\"tablist\"","data-student-tab=\"identidad\"","data-student-tab=\"autorizacion\"","data-student-tab=\"activacion\"","class=\"user-access-grid\"");
            if(estado.equals("INVITADO")) {
                assertThat(html.indexOf("Generar invitación")).isGreaterThan(activar);
                assertThat(html.indexOf("Copia esta invitación ahora")).isGreaterThan(html.indexOf("Generar invitación"));
                assertThat(html).contains("/admin/usuarios/7/invitacion","method=\"post\"");
            } else assertThat(html).doesNotContain(">Generar invitación<");
            var dir=Path.of("target/qa-usuario");Files.createDirectories(dir);Files.writeString(dir.resolve(estado.toLowerCase()+".html"),html);
        }
    }
    @Controller static class Pantalla {
        @GetMapping("/qa/usuario") String mostrar(@RequestParam String estado,Model m) {
            var usuario=new UsuarioResponse(7L,1L,"usuario.prueba","prueba@example.com",EstadoUsuario.valueOf(estado),TipoCuentaUsuario.ADMINISTRATIVO,
                    !estado.equals("INVITADO"),false,new AuditoriaResponse(Instant.now(),null,null,null,0L));
            m.addAttribute("form",UsuarioForm.desde(usuario));m.addAttribute("id",7L);m.addAttribute("edicion",true);m.addAttribute("usuario",usuario);
            m.addAttribute("usuarioSesion","administrador.prueba");m.addAttribute("instituciones",List.of());m.addAttribute("planteles",List.of());
            m.addAttribute("roles",List.of());m.addAttribute("asignaciones",List.of());m.addAttribute("alcances",AlcanceRol.values());m.addAttribute("asignacionForm",new AsignacionRolForm());
            if(estado.equals("INVITADO")){m.addAttribute("invitacionEnlace","/activar-cuenta?token=ejemplo-ficticio");m.addAttribute("invitacionExpira",Instant.parse("2026-10-18T12:00:00Z"));}
            return "admin/usuario-form";
        }
    }
}
