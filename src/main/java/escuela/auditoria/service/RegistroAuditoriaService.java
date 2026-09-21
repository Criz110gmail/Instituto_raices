package escuela.auditoria.service;

import escuela.auditoria.entity.*;
import escuela.auditoria.repository.AuditoriaRepository;
import escuela.auditoria.support.CorrelacionAuditoria;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.service.UsuarioPrincipal;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistroAuditoriaService {
    private static final Set<String> CLAVES_PROHIBIDAS = Set.of(
            "password", "passwordhash", "token", "hash", "secreto", "numerocuenta",
            "clabe", "contenidoarchivo", "comprobante");

    private final AuditoriaRepository repository;
    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.MANDATORY)
    public void registrar(Long institucionId, AccionAuditoria accion, String tipoEntidad,
                          Object entidadId, String motivo, Map<String, ?> cambios) {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        UsuarioPrincipal principal = autenticacion != null && autenticacion.getPrincipal() instanceof UsuarioPrincipal p
                ? p : null;
        Usuario actor = principal != null && !principal.accesoRecuperacion() && principal.usuarioId() != null
                ? entityManager.getReference(Usuario.class, principal.usuarioId()) : null;
        String actorSistema = actor == null
                ? principal != null && principal.accesoRecuperacion() ? "ACCESO_RECUPERACION" : "SISTEMA"
                : null;
        Institucion institucion = entityManager.getReference(Institucion.class, institucionId);
        repository.save(Auditoria.crear(institucion, actor, actorSistema, accion, tipoEntidad,
                String.valueOf(entidadId), limpiar(motivo), serializar(cambios),
                CorrelacionAuditoria.actualOGenerar()));
    }

    private String serializar(Map<String, ?> cambios) {
        if (cambios == null || cambios.isEmpty()) return null;
        Map<String, Object> seguros = new TreeMap<>();
        cambios.forEach((clave, valor) -> {
            if (clave != null && CLAVES_PROHIBIDAS.stream().noneMatch(p -> clave.toLowerCase(Locale.ROOT).contains(p))) {
                seguros.put(clave, valor instanceof String texto && texto.length() > 1000
                        ? texto.substring(0, 1000) : valor);
            }
        });
        if (seguros.isEmpty()) return null;
        String json = seguros.entrySet().stream()
                .map(entrada -> "\"" + escapar(entrada.getKey()) + "\":" + convertirValor(entrada.getValue()))
                .collect(Collectors.joining(",", "{", "}"));
        return json.length() > 12000 ? json.substring(0, 12000) : json;
    }

    private String convertirValor(Object valor) {
        if (valor == null) return "null";
        if (valor instanceof Number || valor instanceof Boolean) return valor.toString();
        return "\"" + escapar(String.valueOf(valor)) + "\"";
    }

    private String escapar(String valor) {
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String limpiar(String valor) {
        if (valor == null || valor.isBlank()) return null;
        String limpio = valor.trim();
        return limpio.length() > 2000 ? limpio.substring(0, 2000) : limpio;
    }
}
