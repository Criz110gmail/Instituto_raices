package escuela.academico.service.impl;

import escuela.academico.dto.request.NivelEducativoRequest;
import escuela.academico.dto.response.NivelEducativoResponse;
import escuela.academico.entity.NivelEducativo;
import escuela.academico.mapper.NivelEducativoMapper;
import escuela.academico.repository.NivelEducativoRepository;
import escuela.academico.service.NivelEducativoService;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class NivelEducativoServiceImpl implements NivelEducativoService {

    private final NivelEducativoRepository repository;
    private final InstitucionRepository institucionRepository;
    private final NivelEducativoMapper mapper;

    @Override
    public NivelEducativoResponse crear(NivelEducativoRequest request) {
        Institucion institucion = institucionActiva(request.institucionId());
        validarUnicos(request, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, institucion)));
    }

    @Override
    public NivelEducativoResponse actualizar(Long id, NivelEducativoRequest request) {
        NivelEducativo entidad = buscar(id);
        verificar(entidad, request.version(), "Nivel educativo");
        if (!entidad.getInstitucion().getId().equals(request.institucionId())) {
            throw new ReglaNegocioException("No se puede cambiar la institución de un nivel educativo");
        }
        validarUnicos(request, id);
        mapper.actualizar(entidad, request, entidad.getInstitucion());
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public NivelEducativoResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NivelEducativoResponse> listar() {
        return repository.findAllByOrderByNombreAsc().stream().map(mapper::respuesta).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NivelEducativoResponse> listarPorInstitucion(Long institucionId) {
        return repository.findAllByInstitucionIdOrderByOrdenAsc(institucionId).stream()
                .map(mapper::respuesta).toList();
    }

    @Override
    public void desactivar(Long id, Long version) {
        NivelEducativo entidad = buscar(id);
        verificar(entidad, version, "Nivel educativo");
        entidad.setActivo(false);
    }

    private NivelEducativo buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el nivel educativo", id));
    }

    private Institucion institucionActiva(Long id) {
        Institucion institucion = institucionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", id));
        if (!institucion.isActivo()) {
            throw new ReglaNegocioException("No se pueden crear niveles en una institución inactiva");
        }
        return institucion;
    }

    private void validarUnicos(NivelEducativoRequest request, Long idExcluido) {
        if (repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(
                request.institucionId(), codigo(request.codigo()), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un nivel con ese código en la institución");
        }
        if (repository.existsByInstitucionIdAndOrdenAndIdNot(
                request.institucionId(), request.orden(), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un nivel con ese orden en la institución");
        }
    }
}
