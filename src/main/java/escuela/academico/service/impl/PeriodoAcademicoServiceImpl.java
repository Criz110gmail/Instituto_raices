package escuela.academico.service.impl;

import escuela.academico.dto.request.PeriodoAcademicoRequest;
import escuela.academico.dto.response.PeriodoAcademicoResponse;
import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.NivelEducativo;
import escuela.academico.entity.PeriodoAcademico;
import escuela.academico.mapper.PeriodoAcademicoMapper;
import escuela.academico.repository.CicloEscolarRepository;
import escuela.academico.repository.NivelEducativoRepository;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.academico.service.PeriodoAcademicoService;
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
public class PeriodoAcademicoServiceImpl implements PeriodoAcademicoService {

    private final PeriodoAcademicoRepository repository;
    private final CicloEscolarRepository cicloRepository;
    private final NivelEducativoRepository nivelRepository;
    private final PeriodoAcademicoMapper mapper;

    @Override
    public PeriodoAcademicoResponse crear(PeriodoAcademicoRequest request) {
        CicloEscolar ciclo = ciclo(request.cicloEscolarId());
        NivelEducativo nivel = nivel(request.nivelEducativoId());
        validar(request, ciclo, nivel, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, ciclo, nivel)));
    }

    @Override
    public PeriodoAcademicoResponse actualizar(Long id, PeriodoAcademicoRequest request) {
        PeriodoAcademico entidad = buscar(id);
        verificar(entidad, request.version(), "Periodo académico");
        if (!entidad.getCicloEscolar().getId().equals(request.cicloEscolarId())
                || !entidad.getNivelEducativo().getId().equals(request.nivelEducativoId())) {
            throw new ReglaNegocioException("No se pueden cambiar ciclo o nivel de un periodo existente");
        }
        validar(request, entidad.getCicloEscolar(), entidad.getNivelEducativo(), id);
        mapper.actualizar(entidad, request, entidad.getCicloEscolar(), entidad.getNivelEducativo());
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodoAcademicoResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodoAcademicoResponse> listarPorCicloYNivel(Long cicloId, Long nivelId) {
        return repository.findAllByCicloEscolarIdAndNivelEducativoIdOrderByOrdenAsc(cicloId, nivelId)
                .stream().map(mapper::respuesta).toList();
    }

    private void validar(PeriodoAcademicoRequest request, CicloEscolar ciclo,
                         NivelEducativo nivel, Long idExcluido) {
        if (ciclo.getEstado() == EstadoAcademico.CERRADO) {
            throw new ReglaNegocioException("No se pueden modificar periodos de un ciclo cerrado");
        }
        if (!ciclo.getInstitucion().getId().equals(nivel.getInstitucion().getId())) {
            throw new ReglaNegocioException("El ciclo y el nivel deben pertenecer a la misma institución");
        }
        if (request.fechaInicio().isAfter(request.fechaFin())) {
            throw new ReglaNegocioException("La fecha inicial del periodo no puede ser posterior a la final");
        }
        if (request.fechaInicio().isBefore(ciclo.getFechaInicio())
                || request.fechaFin().isAfter(ciclo.getFechaFin())) {
            throw new ReglaNegocioException("Las fechas del periodo deben estar dentro del ciclo escolar");
        }
        if (repository.existsByCicloEscolarIdAndNivelEducativoIdAndCodigoIgnoreCaseAndIdNot(
                request.cicloEscolarId(), request.nivelEducativoId(), codigo(request.codigo()), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un periodo con ese código para el ciclo y nivel");
        }
        if (repository.existsByCicloEscolarIdAndNivelEducativoIdAndOrdenAndIdNot(
                request.cicloEscolarId(), request.nivelEducativoId(), request.orden(), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un periodo con ese orden para el ciclo y nivel");
        }
        if (repository.existeSolapamiento(request.cicloEscolarId(), request.nivelEducativoId(),
                request.fechaInicio(), request.fechaFin(), idExcluido)) {
            throw new ReglaNegocioException("El periodo se solapa con otro periodo del mismo ciclo y nivel");
        }
    }

    private PeriodoAcademico buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el periodo académico", id));
    }

    private CicloEscolar ciclo(Long id) {
        return cicloRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el ciclo escolar", id));
    }

    private NivelEducativo nivel(Long id) {
        return nivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el nivel educativo", id));
    }
}
