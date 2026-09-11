package escuela.academico.service.impl;

import escuela.academico.dto.request.GradoRequest;
import escuela.academico.dto.response.GradoResponse;
import escuela.academico.entity.Grado;
import escuela.academico.entity.NivelEducativo;
import escuela.academico.mapper.GradoMapper;
import escuela.academico.repository.GradoRepository;
import escuela.academico.repository.NivelEducativoRepository;
import escuela.academico.service.GradoService;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class GradoServiceImpl implements GradoService {

    private final GradoRepository repository;
    private final NivelEducativoRepository nivelRepository;
    private final GradoMapper mapper;

    @Override
    public GradoResponse crear(GradoRequest request) {
        NivelEducativo nivel = nivelActivo(request.nivelEducativoId());
        validarUnicos(request, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, nivel)));
    }

    @Override
    public GradoResponse actualizar(Long id, GradoRequest request) {
        Grado entidad = buscar(id);
        verificar(entidad, request.version(), "Grado");
        if (!entidad.getNivelEducativo().getId().equals(request.nivelEducativoId())) {
            throw new ReglaNegocioException("No se puede cambiar el nivel educativo de un grado");
        }
        validarUnicos(request, id);
        mapper.actualizar(entidad, request, entidad.getNivelEducativo());
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public GradoResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradoResponse> listarPorNivel(Long nivelEducativoId) {
        return repository.findAllByNivelEducativoIdOrderByOrdenAsc(nivelEducativoId).stream()
                .map(mapper::respuesta).toList();
    }

    @Override
    public void desactivar(Long id, Long version) {
        Grado entidad = buscar(id);
        verificar(entidad, version, "Grado");
        entidad.setActivo(false);
    }

    private Grado buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("el grado", id));
    }

    private NivelEducativo nivelActivo(Long id) {
        NivelEducativo nivel = nivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el nivel educativo", id));
        if (!nivel.isActivo() || !nivel.getInstitucion().isActivo()) {
            throw new ReglaNegocioException("El nivel y su institución deben estar activos para crear grados");
        }
        return nivel;
    }

    private void validarUnicos(GradoRequest request, Long idExcluido) {
        if (repository.existsByNivelEducativoIdAndCodigoIgnoreCaseAndIdNot(
                request.nivelEducativoId(), codigo(request.codigo()), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un grado con ese código en el nivel");
        }
        if (repository.existsByNivelEducativoIdAndOrdenAndIdNot(
                request.nivelEducativoId(), request.orden(), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un grado con ese orden en el nivel");
        }
    }
}
