package escuela.institucion.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.dto.request.PlantelRequest;
import escuela.institucion.dto.response.PlantelResponse;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.institucion.mapper.PlantelMapper;
import escuela.institucion.repository.InstitucionRepository;
import escuela.institucion.repository.PlantelRepository;
import escuela.institucion.service.PlantelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class PlantelServiceImpl implements PlantelService {

    private final PlantelRepository repository;
    private final InstitucionRepository institucionRepository;
    private final PlantelMapper mapper;

    @Override
    public PlantelResponse crear(PlantelRequest request) {
        Institucion institucion = institucionActiva(request.institucionId());
        validarCodigo(request.institucionId(), request.codigo(), 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, institucion)));
    }

    @Override
    public PlantelResponse actualizar(Long id, PlantelRequest request) {
        Plantel entidad = buscar(id);
        verificar(entidad, request.version(), "Plantel");
        if (!entidad.getInstitucion().getId().equals(request.institucionId())) {
            throw new ReglaNegocioException("No se puede cambiar la institución propietaria de un plantel");
        }
        validarCodigo(request.institucionId(), request.codigo(), id);
        mapper.actualizar(entidad, request, entidad.getInstitucion());
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public PlantelResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantelResponse> listar() {
        return repository.findAllByOrderByNombreAsc().stream().map(mapper::respuesta).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantelResponse> listarPorInstitucion(Long institucionId) {
        return repository.findAllByInstitucionIdOrderByNombreAsc(institucionId).stream()
                .map(mapper::respuesta).toList();
    }

    @Override
    public void desactivar(Long id, Long version) {
        Plantel entidad = buscar(id);
        verificar(entidad, version, "Plantel");
        entidad.setActivo(false);
    }

    private Plantel buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("el plantel", id));
    }

    private Institucion institucionActiva(Long id) {
        Institucion institucion = institucionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", id));
        if (!institucion.isActivo()) {
            throw new ReglaNegocioException("No se pueden crear planteles en una institución inactiva");
        }
        return institucion;
    }

    private void validarCodigo(Long institucionId, String valor, Long idExcluido) {
        if (repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(
                institucionId, codigo(valor), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un plantel con ese código en la institución");
        }
    }
}
