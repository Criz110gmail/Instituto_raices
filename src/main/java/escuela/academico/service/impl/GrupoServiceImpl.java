package escuela.academico.service.impl;

import escuela.academico.dto.request.GrupoRequest;
import escuela.academico.dto.response.GrupoResponse;
import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.Grado;
import escuela.academico.entity.Grupo;
import escuela.academico.mapper.GrupoMapper;
import escuela.academico.repository.CicloEscolarRepository;
import escuela.academico.repository.GradoRepository;
import escuela.academico.repository.GrupoRepository;
import escuela.academico.service.GrupoService;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Plantel;
import escuela.institucion.repository.PlantelNivelRepository;
import escuela.institucion.repository.PlantelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class GrupoServiceImpl implements GrupoService {

    private final GrupoRepository repository;
    private final PlantelRepository plantelRepository;
    private final CicloEscolarRepository cicloRepository;
    private final GradoRepository gradoRepository;
    private final PlantelNivelRepository plantelNivelRepository;
    private final GrupoMapper mapper;

    @Override
    public GrupoResponse crear(GrupoRequest request) {
        Plantel plantel = plantel(request.plantelId());
        CicloEscolar ciclo = ciclo(request.cicloEscolarId());
        Grado grado = grado(request.gradoId());
        validar(request, plantel, ciclo, grado, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, plantel, ciclo, grado)));
    }

    @Override
    public GrupoResponse actualizar(Long id, GrupoRequest request) {
        Grupo entidad = buscar(id);
        verificar(entidad, request.version(), "Grupo");
        if (!entidad.getPlantel().getId().equals(request.plantelId())
                || !entidad.getCicloEscolar().getId().equals(request.cicloEscolarId())
                || !entidad.getGrado().getId().equals(request.gradoId())) {
            throw new ReglaNegocioException("No se pueden cambiar plantel, ciclo o grado de un grupo existente");
        }
        validar(request, entidad.getPlantel(), entidad.getCicloEscolar(), entidad.getGrado(), id);
        mapper.actualizar(entidad, request, entidad.getPlantel(), entidad.getCicloEscolar(), entidad.getGrado());
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public GrupoResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrupoResponse> listarPorPlantelYCiclo(Long plantelId, Long cicloEscolarId) {
        return repository.findAllByPlantelIdAndCicloEscolarIdOrderByNombreAsc(plantelId, cicloEscolarId)
                .stream().map(mapper::respuesta).toList();
    }

    @Override
    public void desactivar(Long id, Long version) {
        Grupo entidad = buscar(id);
        verificar(entidad, version, "Grupo");
        entidad.setActivo(false);
    }

    private void validar(GrupoRequest request, Plantel plantel, CicloEscolar ciclo,
                         Grado grado, Long idExcluido) {
        Long institucionId = plantel.getInstitucion().getId();
        if (!institucionId.equals(ciclo.getInstitucion().getId())
                || !institucionId.equals(grado.getNivelEducativo().getInstitucion().getId())) {
            throw new ReglaNegocioException("Plantel, ciclo y grado deben pertenecer a la misma institución");
        }
        if (!plantel.isActivo() || !plantel.getInstitucion().isActivo()
                || !grado.isActivo() || !grado.getNivelEducativo().isActivo()) {
            throw new ReglaNegocioException("Plantel, institución, nivel y grado deben estar activos");
        }
        if (ciclo.getEstado() == EstadoAcademico.CERRADO) {
            throw new ReglaNegocioException("No se pueden modificar grupos de un ciclo cerrado");
        }
        Long nivelId = grado.getNivelEducativo().getId();
        if (!plantelNivelRepository.existsByPlantelIdAndNivelEducativoIdAndActivoTrue(
                plantel.getId(), nivelId)) {
            throw new ReglaNegocioException("El plantel no ofrece activamente el nivel del grado");
        }
        if (repository.existsByPlantelIdAndCicloEscolarIdAndGradoIdAndTurnoAndNombreIgnoreCaseAndIdNot(
                request.plantelId(), request.cicloEscolarId(), request.gradoId(), request.turno(),
                request.nombre().trim(), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un grupo con el mismo nombre y turno");
        }
        String codigoNormalizado = codigo(request.codigo());
        if (codigoNormalizado != null
                && repository.existsByPlantelIdAndCicloEscolarIdAndCodigoIgnoreCaseAndIdNot(
                request.plantelId(), request.cicloEscolarId(), codigoNormalizado, idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un grupo con ese código en el plantel y ciclo");
        }
    }

    private Grupo buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("el grupo", id));
    }

    private Plantel plantel(Long id) {
        return plantelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el plantel", id));
    }

    private CicloEscolar ciclo(Long id) {
        return cicloRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el ciclo escolar", id));
    }

    private Grado grado(Long id) {
        return gradoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el grado", id));
    }
}
