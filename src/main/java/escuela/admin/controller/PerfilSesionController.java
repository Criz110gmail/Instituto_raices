package escuela.admin.controller;

import escuela.archivo.dto.ArchivoDescarga;
import escuela.seguridad.service.FotografiaUsuarioService;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class PerfilSesionController {

    private final FotografiaUsuarioService fotografiaService;

    @GetMapping("/admin/perfil/fotografia")
    ResponseEntity<Resource> fotografia(@AuthenticationPrincipal UsuarioPrincipal principal) {
        if (principal != null && principal.usuarioId() != null) {
            return fotografiaService.descargarActual(principal.usuarioId())
                    .map(this::respuesta).orElseGet(this::avatarGenerico);
        }
        return avatarGenerico();
    }

    private ResponseEntity<Resource> respuesta(ArchivoDescarga descarga) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType(descarga.tipoMime()))
                .contentLength(descarga.tamanoBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(descarga.nombreOriginal(), StandardCharsets.UTF_8).build().toString())
                .body(descarga.recurso());
    }

    private ResponseEntity<Resource> avatarGenerico() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(new ClassPathResource("static/images/avatar-generico.svg"));
    }
}
