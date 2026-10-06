package escuela.docente.service;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.docente.entity.Maestro;
import escuela.docente.repository.MaestroRepository;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.TipoCuentaUsuario;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SoportePortalMaestroService {
    private final MaestroRepository maestros;
    private final AlcanceDatosService alcance;
    private final RegistroAuditoriaService auditoria;

    public record MaestroSoporteFila(Long id, String numero, String nombre, String usuario) { }

    @Transactional(readOnly = true)
    public Page<MaestroSoporteFila> listar(UsuarioPrincipal admin, String q, int pagina, int tamanio) {
        validar(admin);
        String texto = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        String patron = "%" + texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
        Specification<Maestro> criterio = (r, consulta, cb) -> {
            var cuenta = r.join("usuario");
            var identidad = cb.concat(cb.concat(cb.concat(cb.concat(r.get("numeroEmpleado"), " "),
                    r.get("nombres")), " "), cb.concat(r.get("primerApellido"),
                    cb.concat(" ", cb.coalesce(r.get("segundoApellido"), ""))));
            return cb.and(cb.equal(r.get("institucion").get("id"), admin.institucionId()),
                    cb.equal(cuenta.get("institucion").get("id"), admin.institucionId()),
                    cb.isTrue(r.get("activo")), cb.equal(cuenta.get("estado"), EstadoUsuario.ACTIVO),
                    cb.equal(cuenta.get("tipoCuenta"), TipoCuentaUsuario.PORTAL_MAESTRO),
                    cb.or(cb.like(cb.lower(identidad), patron, '\\'),
                            cb.like(cb.lower(cuenta.get("username")), patron, '\\')));
        };
        int cantidad = Set.of(10, 25, 50, 100).contains(tamanio) ? tamanio : 25;
        return maestros.findAll(criterio, PageRequest.of(Math.max(0, pagina), cantidad,
                Sort.by("primerApellido", "nombres", "id"))).map(m -> new MaestroSoporteFila(
                m.getId(), m.getNumeroEmpleado(), nombre(m), m.getUsuario().getUsername()));
    }

    @Transactional
    public UsuarioPrincipal contexto(UsuarioPrincipal admin, Long maestroId, String seccion) {
        validar(admin);
        Maestro maestro = maestros.findById(maestroId).orElseThrow(this::denegado);
        var usuario = maestro.getUsuario();
        if (!maestro.isActivo() || !maestro.getInstitucion().getId().equals(admin.institucionId())
                || usuario == null || usuario.getEstado() != EstadoUsuario.ACTIVO
                || usuario.getTipoCuenta() != TipoCuentaUsuario.PORTAL_MAESTRO
                || !usuario.getInstitucion().getId().equals(admin.institucionId())) throw denegado();
        auditoria.registrar(admin.institucionId(), AccionAuditoria.PORTAL_MAESTRO_SOPORTE,
                "MAESTRO", maestroId, "Consulta de soporte del portal docente",
                Map.of("seccion", seccion));
        // This principal is passed only to read services; it never replaces the authenticated administrator.
        return new UsuarioPrincipal(usuario.getId(), admin.institucionId(), Set.of(), false, false,
                usuario.getUsername(), "", List.of(new SimpleGrantedAuthority("PORTAL_MAESTRO_ACCEDER")));
    }

    private void validar(UsuarioPrincipal admin) {
        if (admin == null || admin.usuarioId() == null || admin.accesoRecuperacion()
                || !admin.alcanceInstitucional() || admin.institucionId() == null
                || admin.authorities().stream().noneMatch(a -> a.getAuthority().equals("PORTAL_MAESTRO_SOPORTE")))
            throw denegado();
        alcance.validarAdministracionInstitucional(admin.institucionId());
    }

    private AccessDeniedException denegado() { return new AccessDeniedException("El portal docente no está disponible para soporte"); }
    private String nombre(Maestro m) {
        return String.join(" ", m.getNombres(), m.getPrimerApellido(), m.getSegundoApellido() == null ? "" : m.getSegundoApellido()).trim();
    }
}
