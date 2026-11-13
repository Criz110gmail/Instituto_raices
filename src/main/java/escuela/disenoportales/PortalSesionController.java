package escuela.disenoportales;

import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Sólo colores publicados de la institución autenticada, sin datos del usuario. */
@RestController
@RequiredArgsConstructor
public class PortalSesionController {
 private final PortalIdentidadService service;
 public record Identidad(String portal,long version,boolean original,String principal,String fondo,String acento,String oscuroPrincipal,String oscuroFondo,String oscuroAcento) {}
 @GetMapping("/sesion/identidad")
 ResponseEntity<Identidad> identidad(@AuthenticationPrincipal UsuarioPrincipal usuario,@RequestParam String portal){
  var tipo=PortalTipo.de(portal);
  var tema=service.identidadSesion(usuario,tipo);
  return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new Identidad(tipo.codigo,tema.version(),tema.original(),tema.principal(),tema.fondo(),tema.acento(),tema.oscuroPrincipal(),tema.oscuroFondo(),tema.oscuroAcento()));
 }
}
