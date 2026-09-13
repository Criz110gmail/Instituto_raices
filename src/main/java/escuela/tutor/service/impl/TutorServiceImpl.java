package escuela.tutor.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.tutor.dto.request.TutorRequest;
import escuela.tutor.dto.response.TutorResponse;
import escuela.tutor.entity.Tutor;
import escuela.tutor.mapper.TutorMapper;
import escuela.tutor.repository.TutorRepository;
import escuela.tutor.service.TutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class TutorServiceImpl implements TutorService {

    private final TutorRepository repository;
    private final InstitucionRepository institucionRepository;
    private final UsuarioRepository usuarioRepository;
    private final TutorMapper mapper;

    @Override
    public TutorResponse crear(TutorRequest request) {
        Institucion institucion = institucionActiva(request.institucionId());
        Usuario usuario = usuarioValido(request.usuarioId(), institucion.getId(), 0L);
        validarFecha(request.fechaNacimiento());
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, institucion, usuario)));
    }

    @Override
    public TutorResponse actualizar(Long id, TutorRequest request) {
        Tutor entidad = buscar(id);
        verificar(entidad, request.version(), "Tutor");
        if (!entidad.getInstitucion().getId().equals(request.institucionId())) {
            throw new ReglaNegocioException("No se puede cambiar la institución de un tutor");
        }
        Usuario usuario = usuarioValido(request.usuarioId(), request.institucionId(), id);
        validarFecha(request.fechaNacimiento());
        mapper.actualizar(entidad, request, entidad.getInstitucion(), usuario);
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public TutorResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutorResponse> listarActivosPorInstitucion(Long institucionId) {
        return repository
                .findAllByInstitucionIdAndActivoTrueOrderByPrimerApellidoAscSegundoApellidoAscNombresAsc(
                        institucionId)
                .stream().map(mapper::respuesta).toList();
    }

    @Override
    public void desactivar(Long id, Long version) {
        Tutor entidad = buscar(id);
        verificar(entidad, version, "Tutor");
        entidad.setActivo(false);
    }

    private Tutor buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el tutor", id));
    }

    private Institucion institucionActiva(Long id) {
        Institucion institucion = institucionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", id));
        if (!institucion.isActivo()) {
            throw new ReglaNegocioException("La institución debe estar activa para registrar tutores");
        }
        return institucion;
    }

    private Usuario usuarioValido(Long usuarioId, Long institucionId, Long tutorExcluido) {
        if (usuarioId == null) return null;
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el usuario", usuarioId));
        if (!usuario.getInstitucion().getId().equals(institucionId)) {
            throw new ReglaNegocioException("El usuario y el tutor deben pertenecer a la misma institución");
        }
        if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
            throw new ReglaNegocioException("No se puede vincular un usuario inactivo");
        }
        if (repository.existsByUsuarioIdAndIdNot(usuarioId, tutorExcluido)) {
            throw new RecursoDuplicadoException("Ese usuario ya está vinculado con otro tutor");
        }
        return usuario;
    }

    private void validarFecha(LocalDate fechaNacimiento) {
        if (fechaNacimiento != null && fechaNacimiento.isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de nacimiento no puede ser futura");
        }
    }
}
