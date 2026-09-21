package escuela.seguridad.service.impl;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Plantel;
import escuela.institucion.repository.PlantelRepository;
import escuela.seguridad.dto.request.AsignacionRolRequest;
import escuela.seguridad.dto.response.AsignacionRolResponse;
import escuela.seguridad.entity.AlcanceRol;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.Permiso;
import escuela.seguridad.entity.Rol;
import escuela.seguridad.entity.RolPermiso;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.entity.UsuarioRol;
import escuela.seguridad.repository.PermisoRepository;
import escuela.seguridad.repository.RolPermisoRepository;
import escuela.seguridad.repository.RolRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.repository.UsuarioRolRepository;
import escuela.seguridad.service.AdministracionAccesoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class AdministracionAccesoServiceImpl implements AdministracionAccesoService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PlantelRepository plantelRepository;
    private final RegistroAuditoriaService auditoria;

    @Override
    public Long agregarPermiso(Long rolId, Long permisoId) {
        Rol rol = rol(rolId);
        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el permiso", permisoId));
        if (!rol.isActivo()) {
            throw new ReglaNegocioException("No se pueden asignar permisos a un rol inactivo");
        }
        var existente = rolPermisoRepository.findByRolIdAndPermisoId(rolId, permisoId);
        if (existente.isPresent()) {
            if (existente.get().isActivo()) {
                throw new RecursoDuplicadoException("El rol ya tiene asignado ese permiso");
            }
            existente.get().setActivo(true);
            Long id = rolPermisoRepository.saveAndFlush(existente.get()).getId();
            registrarPermiso(rol, permiso, id);
            return id;
        }
        RolPermiso relacion = new RolPermiso();
        relacion.setRol(rol);
        relacion.setPermiso(permiso);
        Long id = rolPermisoRepository.saveAndFlush(relacion).getId();
        registrarPermiso(rol, permiso, id);
        return id;
    }

    @Override
    public Long asignarRol(AsignacionRolRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el usuario", request.usuarioId()));
        Rol rol = rol(request.rolId());
        if (!usuario.getInstitucion().getId().equals(rol.getInstitucion().getId())) {
            throw new ReglaNegocioException("El usuario y el rol deben pertenecer a la misma institución");
        }
        if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
            throw new ReglaNegocioException("No se pueden asignar roles a un usuario inactivo");
        }
        if (!rol.isActivo()) {
            throw new ReglaNegocioException("No se puede asignar un rol inactivo");
        }

        Plantel plantel = validarPlantel(request, usuario);
        Optional<UsuarioRol> existente = plantel == null
                ? usuarioRolRepository.findByUsuarioIdAndRolIdAndAlcanceAndPlantelIsNull(
                        usuario.getId(), rol.getId(), request.alcance())
                : usuarioRolRepository.findByUsuarioIdAndRolIdAndAlcanceAndPlantelId(
                        usuario.getId(), rol.getId(), request.alcance(), plantel.getId());
        if (existente.isPresent()) {
            if (existente.get().isActivo()) {
                throw new RecursoDuplicadoException("El usuario ya tiene esa asignación de rol");
            }
            existente.get().setActivo(true);
            Long id = usuarioRolRepository.saveAndFlush(existente.get()).getId();
            registrarRolUsuario(usuario, rol, request, id);
            return id;
        }

        UsuarioRol asignacion = new UsuarioRol();
        asignacion.setUsuario(usuario);
        asignacion.setRol(rol);
        asignacion.setAlcance(request.alcance());
        asignacion.setPlantel(plantel);
        asignacion.setActivo(true);
        Long id = usuarioRolRepository.saveAndFlush(asignacion).getId();
        registrarRolUsuario(usuario, rol, request, id);
        return id;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionRolResponse> listarAsignaciones(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNoEncontradoException("el usuario", usuarioId);
        }
        return usuarioRolRepository.findAllByUsuarioIdOrderByRolNombreAsc(usuarioId).stream()
                .map(a -> new AsignacionRolResponse(
                        a.getId(), a.getRol().getId(), a.getRol().getCodigo(), a.getRol().getNombre(),
                        a.getAlcance(), a.getPlantel() == null ? null : a.getPlantel().getId(),
                        a.getPlantel() == null ? null : a.getPlantel().getNombre(),
                        a.isActivo(), a.getVersion()))
                .toList();
    }

    @Override
    public void desactivarAsignacion(Long usuarioId, Long usuarioRolId, Long version) {
        UsuarioRol asignacion = usuarioRolRepository.findById(usuarioRolId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la asignación de rol", usuarioRolId));
        if (!asignacion.getUsuario().getId().equals(usuarioId)) {
            throw new ReglaNegocioException("La asignación no pertenece al usuario indicado");
        }
        verificar(asignacion, version, "Asignación de rol");
        asignacion.setActivo(false);
        auditoria.registrar(asignacion.getUsuario().getInstitucion().getId(), AccionAuditoria.ROL_USUARIO_RETIRADO,
                "USUARIO_ROL", asignacion.getId(), null, Map.of("usuarioId", asignacion.getUsuario().getId(),
                        "rolId", asignacion.getRol().getId(), "alcance", asignacion.getAlcance().name()));
    }

    private void registrarPermiso(Rol rol, Permiso permiso, Long relacionId) {
        auditoria.registrar(rol.getInstitucion().getId(), AccionAuditoria.PERMISO_ROL_ASIGNADO,
                "ROL_PERMISO", relacionId, null,
                Map.of("rolId", rol.getId(), "rol", rol.getCodigo(), "permisoId", permiso.getId(),
                        "permiso", permiso.getCodigo()));
    }

    private void registrarRolUsuario(Usuario usuario, Rol rol, AsignacionRolRequest request, Long asignacionId) {
        Map<String,Object> cambios = new LinkedHashMap<>();
        cambios.put("usuarioId", usuario.getId()); cambios.put("rolId", rol.getId());
        cambios.put("rol", rol.getCodigo()); cambios.put("alcance", request.alcance().name());
        if (request.plantelId() != null) cambios.put("plantelId", request.plantelId());
        auditoria.registrar(usuario.getInstitucion().getId(), AccionAuditoria.ROL_USUARIO_ASIGNADO,
                "USUARIO_ROL", asignacionId, null, cambios);
    }

    private Plantel validarPlantel(AsignacionRolRequest request, Usuario usuario) {
        if (request.alcance() == AlcanceRol.PLANTEL) {
            if (request.plantelId() == null) {
                throw new ReglaNegocioException("El plantel es obligatorio para un alcance de plantel");
            }
            Plantel plantel = plantelRepository.findById(request.plantelId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("el plantel", request.plantelId()));
            if (!plantel.getInstitucion().getId().equals(usuario.getInstitucion().getId())) {
                throw new ReglaNegocioException("El plantel debe pertenecer a la institución del usuario");
            }
            if (!plantel.isActivo()) {
                throw new ReglaNegocioException("El plantel debe estar activo para asignarle acceso");
            }
            return plantel;
        }
        if (request.plantelId() != null) {
            throw new ReglaNegocioException("El plantel solo se permite para un alcance de plantel");
        }
        return null;
    }

    private Rol rol(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el rol", id));
    }
}
