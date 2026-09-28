package escuela.academico.service.impl;

import escuela.academico.dto.request.MateriaGradoRequest;
import escuela.academico.dto.request.MateriaRequest;
import escuela.academico.dto.response.MateriaGradoResponse;
import escuela.academico.dto.response.MateriaResponse;
import escuela.academico.entity.Grado;
import escuela.academico.entity.Materia;
import escuela.academico.entity.MateriaGrado;
import escuela.academico.entity.TipoEvaluacion;
import escuela.academico.mapper.MateriaMapper;
import escuela.academico.repository.GradoRepository;
import escuela.academico.repository.MateriaGradoRepository;
import escuela.academico.repository.MateriaRepository;
import escuela.academico.service.MateriaService;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class MateriaServiceImpl implements MateriaService {
    private final MateriaRepository repository;
    private final MateriaGradoRepository planRepository;
    private final InstitucionRepository institucionRepository;
    private final GradoRepository gradoRepository;
    private final MateriaMapper mapper;

    @Override
    public MateriaResponse crear(MateriaRequest request) {
        Institucion institucion = institucionActiva(request.institucionId());
        validarCodigo(request, 0L);
        Materia materia = repository.saveAndFlush(mapper.nueva(request, institucion));
        return mapper.respuesta(materia, 0);
    }

    @Override
    public MateriaResponse actualizar(Long id, MateriaRequest request) {
        Materia materia = buscar(id);
        verificar(materia, request.version(), "Materia");
        if (!materia.getInstitucion().getId().equals(request.institucionId())) {
            throw new ReglaNegocioException("No se puede cambiar la institución de una materia");
        }
        validarCodigo(request, id);
        mapper.actualizar(materia, request, materia.getInstitucion());
        materia = repository.saveAndFlush(materia);
        return respuesta(materia);
    }

    @Override
    @Transactional(readOnly = true)
    public MateriaResponse obtener(Long id) {
        return respuesta(buscar(id));
    }

    @Override
    public void desactivar(Long id, Long version) {
        Materia materia = buscar(id);
        verificar(materia, version, "Materia");
        materia.setActivo(false);
        planRepository.findAllByMateriaIdOrderByActivoDescOrdenAsc(id).stream()
                .filter(MateriaGrado::isActivo).forEach(plan -> plan.setActivo(false));
    }

    @Override
    public MateriaGradoResponse asignarGrado(Long materiaId, MateriaGradoRequest request) {
        Materia materia = buscar(materiaId);
        if (!materia.isActivo()) throw new ReglaNegocioException("La materia debe estar activa");
        Grado grado = grado(request.gradoId());
        validarMismaInstitucion(materia, grado);
        if (planRepository.existsByMateriaIdAndGradoId(materiaId, grado.getId())) {
            throw new RecursoDuplicadoException("La materia ya está configurada para ese grado");
        }
        validarEscala(request);
        validarOrden(request.gradoId(), request.orden(), 0L);
        MateriaGrado plan = new MateriaGrado();
        plan.setMateria(materia);
        plan.setGrado(grado);
        copiar(plan, request);
        return respuesta(planRepository.saveAndFlush(plan));
    }

    @Override
    public MateriaGradoResponse actualizarGrado(Long materiaId, Long planId,
                                                 MateriaGradoRequest request) {
        MateriaGrado plan = planRepository.findByIdAndMateriaId(planId, materiaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el plan de materia", planId));
        verificar(plan, request.version(), "Plan de materia");
        if (!plan.getGrado().getId().equals(request.gradoId())) {
            throw new ReglaNegocioException("No se puede cambiar el grado de un plan existente");
        }
        validarEscala(request);
        validarOrden(request.gradoId(), request.orden(), planId);
        copiar(plan, request);
        return respuesta(planRepository.saveAndFlush(plan));
    }

    @Override
    public void desactivarGrado(Long materiaId, Long planId, Long version) {
        MateriaGrado plan = planRepository.findByIdAndMateriaId(planId, materiaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el plan de materia", planId));
        verificar(plan, version, "Plan de materia");
        plan.setActivo(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MateriaGradoResponse> listarPlanes(Long materiaId) {
        buscar(materiaId);
        return planRepository.findAllByMateriaIdOrderByActivoDescOrdenAsc(materiaId).stream()
                .map(this::respuesta).toList();
    }

    private void copiar(MateriaGrado plan, MateriaGradoRequest request) {
        plan.setTipoEvaluacion(request.tipoEvaluacion());
        boolean numerica = request.tipoEvaluacion() == TipoEvaluacion.NUMERICA;
        plan.setEscalaMinima(numerica ? request.escalaMinima() : null);
        plan.setEscalaMaxima(numerica ? request.escalaMaxima() : null);
        plan.setMinimaAprobatoria(numerica ? request.minimaAprobatoria() : null);
        plan.setDecimales(numerica ? request.decimales() : 0);
        plan.setOrden(request.orden());
        plan.setHorasSemanales(request.horasSemanales());
        plan.setIncluirBoleta(request.incluirBoleta());
        plan.setActivo(true);
    }

    private void validarEscala(MateriaGradoRequest request) {
        if (request.tipoEvaluacion() == null) return;
        if (request.tipoEvaluacion() == TipoEvaluacion.CUALITATIVA) {
            if (request.escalaMinima() != null || request.escalaMaxima() != null
                    || request.minimaAprobatoria() != null) {
                throw new ReglaNegocioException("La evaluación cualitativa no utiliza escala numérica");
            }
            return;
        }
        BigDecimal minima = request.escalaMinima();
        BigDecimal maxima = request.escalaMaxima();
        BigDecimal aprobatoria = request.minimaAprobatoria();
        if (minima == null || maxima == null || aprobatoria == null) {
            throw new ReglaNegocioException("Completa la escala y la calificación mínima aprobatoria");
        }
        if (minima.compareTo(maxima) >= 0
                || aprobatoria.compareTo(minima) < 0 || aprobatoria.compareTo(maxima) > 0) {
            throw new ReglaNegocioException("La escala numérica y la mínima aprobatoria no son válidas");
        }
    }

    private void validarMismaInstitucion(Materia materia, Grado grado) {
        if (!grado.getNivelEducativo().getInstitucion().getId().equals(materia.getInstitucion().getId())) {
            throw new ReglaNegocioException("El grado y la materia deben pertenecer a la misma institución");
        }
        if (!grado.isActivo() || !grado.getNivelEducativo().isActivo()) {
            throw new ReglaNegocioException("El nivel y el grado deben estar activos");
        }
    }

    private void validarCodigo(MateriaRequest request, Long excluido) {
        if (repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(
                request.institucionId(), codigo(request.codigo()), excluido)) {
            throw new RecursoDuplicadoException("Ya existe una materia con ese código en la institución");
        }
    }

    private void validarOrden(Long gradoId, Integer orden, Long excluido) {
        if (gradoId != null && orden != null
                && planRepository.existsByGradoIdAndOrdenAndActivoTrueAndIdNot(
                gradoId, orden, excluido)) {
            throw new RecursoDuplicadoException(
                    "Ya existe otra materia activa con ese orden dentro del grado");
        }
    }

    private MateriaResponse respuesta(Materia materia) {
        return mapper.respuesta(materia, planRepository.countByMateriaIdAndActivoTrue(materia.getId()));
    }

    private Materia buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la materia", id));
    }

    private Grado grado(Long id) {
        return gradoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el grado", id));
    }

    private Institucion institucionActiva(Long id) {
        Institucion institucion = institucionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", id));
        if (!institucion.isActivo()) throw new ReglaNegocioException("La institución debe estar activa");
        return institucion;
    }

    private MateriaGradoResponse respuesta(MateriaGrado plan) {
        return new MateriaGradoResponse(plan.getId(), plan.getGrado().getId(),
                plan.getGrado().getNombre(), plan.getGrado().getNivelEducativo().getNombre(),
                plan.getTipoEvaluacion(), plan.getEscalaMinima(), plan.getEscalaMaxima(),
                plan.getMinimaAprobatoria(), plan.getDecimales(), plan.getOrden(),
                plan.getHorasSemanales(), plan.isIncluirBoleta(), plan.isActivo(), desde(plan));
    }
}
