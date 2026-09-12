package escuela.seguridad.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.dto.request.RolRequest;
import escuela.seguridad.dto.response.RolResponse;
import escuela.seguridad.dto.response.PermisoResponse;
import escuela.seguridad.entity.Permiso;
import escuela.seguridad.entity.Rol;
import escuela.seguridad.entity.RolPermiso;
import escuela.seguridad.mapper.RolMapper;
import escuela.seguridad.repository.RolRepository;
import escuela.seguridad.repository.PermisoRepository;
import escuela.seguridad.repository.RolPermisoRepository;
import escuela.seguridad.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class RolServiceImpl implements RolService {

    private final RolRepository repository;
    private final InstitucionRepository institucionRepository;
    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final RolMapper mapper;

    @Override
    public RolResponse crear(RolRequest request, Set<Long> permisoIds) {
        Institucion institucion = institucion(request.institucionId());
        validar(request, institucion, 0L);
        Rol rol = repository.saveAndFlush(mapper.nuevo(request, institucion));
        sincronizarPermisos(rol, permisoIds);
        return mapper.respuesta(rol);
    }

    @Override
    public RolResponse actualizar(Long id, RolRequest request, Set<Long> permisoIds) {
        Rol rol = buscar(id);
        verificar(rol, request.version(), "Rol");
        if (!rol.getInstitucion().getId().equals(request.institucionId())) {
            throw new ReglaNegocioException("No se puede cambiar la institución de un rol existente");
        }
        validar(request, rol.getInstitucion(), id);
        mapper.actualizar(rol, request, rol.getInstitucion());
        repository.saveAndFlush(rol);
        sincronizarPermisos(rol, permisoIds);
        return mapper.respuesta(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public RolResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoResponse> listarPermisos() {
        return permisoRepository.findAllByOrderByCodigoAsc().stream()
                .map(p -> new PermisoResponse(p.getId(), p.getCodigo(), p.getDescripcion()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> permisosAsignados(Long rolId) {
        buscar(rolId);
        return rolPermisoRepository.findAllByRolIdAndActivoTrueOrderByPermisoCodigoAsc(rolId)
                .stream().map(rp -> rp.getPermiso().getId()).collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public void desactivar(Long id, Long version) {
        Rol rol = buscar(id);
        verificar(rol, version, "Rol");
        rol.setActivo(false);
    }

    private void validar(RolRequest request, Institucion institucion, Long idExcluido) {
        if (!institucion.isActivo()) {
            throw new ReglaNegocioException("La institución debe estar activa para administrar roles");
        }
        if (repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(
                institucion.getId(), codigo(request.codigo()), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un rol con ese código en la institución");
        }
    }

    private void sincronizarPermisos(Rol rol, Set<Long> permisoIds) {
        Set<Long> solicitados = permisoIds == null ? Set.of() : Set.copyOf(permisoIds);
        List<Permiso> permisos = permisoRepository.findAllById(solicitados);
        Set<Long> encontrados = permisos.stream().map(Permiso::getId)
                .collect(java.util.stream.Collectors.toSet());
        if (!encontrados.equals(solicitados)) {
            throw new ReglaNegocioException("Uno o más permisos seleccionados no existen");
        }

        List<RolPermiso> existentes = rolPermisoRepository.findAllByRolIdOrderByPermisoCodigoAsc(rol.getId());
        Set<Long> yaRegistrados = new HashSet<>();
        for (RolPermiso relacion : existentes) {
            Long permisoId = relacion.getPermiso().getId();
            relacion.setActivo(solicitados.contains(permisoId));
            yaRegistrados.add(permisoId);
        }
        for (Permiso permiso : permisos) {
            if (!yaRegistrados.contains(permiso.getId())) {
                RolPermiso relacion = new RolPermiso();
                relacion.setRol(rol);
                relacion.setPermiso(permiso);
                relacion.setActivo(true);
                existentes.add(relacion);
            }
        }
        if (!existentes.isEmpty()) {
            rolPermisoRepository.saveAllAndFlush(existentes);
        }
    }

    private Rol buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el rol", id));
    }

    private Institucion institucion(Long id) {
        return institucionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", id));
    }
}
