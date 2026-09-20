package escuela.portal.controller;

import escuela.alumno.service.FotografiaAlumnoService;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.portal.service.PortalTutorService;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
@RequestMapping("/portal")
public class PortalTutorController {
    private final PortalTutorService service;
    private final FotografiaAlumnoService fotografiaService;

    @GetMapping
    String portal(@RequestParam(required = false) Long alumnoId,
                  @RequestParam(defaultValue = "0") int paginaEventos,
                  @RequestParam(defaultValue = "0") int paginaCargos,
                  @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        model.addAttribute("portal", service.consultar(principal, alumnoId, paginaEventos, paginaCargos));
        return "portal/inicio";
    }

    @GetMapping("/alumnos/{alumnoId}/fotografia")
    ResponseEntity<Resource> fotografia(@PathVariable Long alumnoId,
                                         @AuthenticationPrincipal UsuarioPrincipal principal) {
        service.validarHijo(principal, alumnoId);
        var actual = fotografiaService.actual(alumnoId);
        if (actual == null) return avatarGenerico();
        ArchivoDescarga descarga = fotografiaService.descargar(alumnoId, actual.id());
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType(descarga.tipoMime()))
                .contentLength(descarga.tamanoBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(descarga.nombreOriginal(), StandardCharsets.UTF_8).build().toString())
                .body(descarga.recurso());
    }

    private ResponseEntity<Resource> avatarGenerico() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(new ClassPathResource("static/images/avatar-generico.svg"));
    }
}
