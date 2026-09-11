package escuela.academico.service.impl;

import escuela.academico.dto.request.CicloEscolarRequest;
import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.entity.CicloEscolar;
import escuela.academico.mapper.CicloEscolarMapper;
import escuela.academico.repository.CicloEscolarRepository;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.academico.service.CicloEscolarService;
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
public class CicloEscolarServiceImpl implements CicloEscolarService {

    private final CicloEscolarRepository repository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final InstitucionRepository institucionRepository;
    private final CicloEscolarMapper mapper;

    @Override
    public CicloEscolarResponse crear(CicloEscolarRequest request) {
        Institucion institucion = institucionBloqueada(request.institucionId());
        validarInstitucionActiva(institucion);
        validarFechas(request);
        validarCodigo(request, 0L);
        desmarcarPredeterminadoAnterior(request, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, institucion)));
    }

    @Override
    public CicloEscolarResponse actualizar(Long id, CicloEscolarRequest request) {
        institucionBloqueada(request.institucionId());
        CicloEscolar entidad = buscar(id);
        verificar(entidad, request.version(), "Ciclo escolar");
        if (!entidad.getInstitucion().getId().equals(request.institucionId())) {
            throw new ReglaNegocioException("No se puede cambiar la institución de un ciclo escolar");
        }
        validarFechas(request);
        if (periodoRepository.existenFueraDeRango(id, request.fechaInicio(), request.fechaFin())) {
            throw new ReglaNegocioException("Las nuevas fechas dejarían periodos fuera del ciclo escolar");
        }
        validarCodigo(request, id);
        desmarcarPredeterminadoAnterior(request, id);
        mapper.actualizar(entidad, request, entidad.getInstitucion());
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public CicloEscolarResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CicloEscolarResponse> listarPorInstitucion(Long institucionId) {
        return repository.findAllByInstitucionIdOrderByFechaInicioDesc(institucionId).stream()
                .map(mapper::respuesta).toList();
    }

    private CicloEscolar buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el ciclo escolar", id));
    }

    private Institucion institucionBloqueada(Long id) {
        return institucionRepository.buscarPorIdConBloqueo(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", id));
    }

    private void validarInstitucionActiva(Institucion institucion) {
        if (!institucion.isActivo()) {
            throw new ReglaNegocioException("No se pueden crear ciclos en una institución inactiva");
        }
    }

    private void validarFechas(CicloEscolarRequest request) {
        if (request.fechaInicio().isAfter(request.fechaFin())) {
            throw new ReglaNegocioException("La fecha inicial del ciclo no puede ser posterior a la final");
        }
    }

    private void validarCodigo(CicloEscolarRequest request, Long idExcluido) {
        if (repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(
                request.institucionId(), codigo(request.codigo()), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un ciclo con ese código en la institución");
        }
    }

    private void desmarcarPredeterminadoAnterior(CicloEscolarRequest request, Long idExcluido) {
        if (request.predeterminado()) {
            repository.findByInstitucionIdAndPredeterminadoTrueAndIdNot(
                    request.institucionId(), idExcluido).ifPresent(ciclo -> ciclo.setPredeterminado(false));
        }
    }
}
