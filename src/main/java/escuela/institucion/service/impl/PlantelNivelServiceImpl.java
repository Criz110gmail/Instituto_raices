package escuela.institucion.service.impl;

import escuela.academico.entity.NivelEducativo;
import escuela.academico.repository.NivelEducativoRepository;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.dto.request.PlantelNivelRequest;
import escuela.institucion.dto.response.PlantelNivelResponse;
import escuela.institucion.entity.Plantel;
import escuela.institucion.entity.PlantelNivel;
import escuela.institucion.mapper.PlantelNivelMapper;
import escuela.institucion.repository.PlantelNivelRepository;
import escuela.institucion.repository.PlantelRepository;
import escuela.institucion.service.PlantelNivelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class PlantelNivelServiceImpl implements PlantelNivelService {

    private final PlantelNivelRepository repository;
    private final PlantelRepository plantelRepository;
    private final NivelEducativoRepository nivelRepository;
    private final PlantelNivelMapper mapper;

    @Override
    public PlantelNivelResponse crear(PlantelNivelRequest request) {
        Plantel plantel = plantel(request.plantelId());
        NivelEducativo nivel = nivel(request.nivelEducativoId());
        validarRelacion(plantel, nivel);
        validarDuplicado(request.plantelId(), request.nivelEducativoId(), 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, plantel, nivel)));
    }

    @Override
    public PlantelNivelResponse actualizar(Long id, PlantelNivelRequest request) {
        PlantelNivel entidad = buscar(id);
        verificar(entidad, request.version(), "Oferta educativa");
        if (!entidad.getPlantel().getId().equals(request.plantelId())
                || !entidad.getNivelEducativo().getId().equals(request.nivelEducativoId())) {
            throw new ReglaNegocioException("No se pueden cambiar plantel o nivel de una oferta existente");
        }
        mapper.actualizar(entidad, request, entidad.getPlantel(), entidad.getNivelEducativo());
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public PlantelNivelResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantelNivelResponse> listarPorPlantel(Long plantelId) {
        return repository.findAllByPlantelId(plantelId).stream().map(mapper::respuesta).toList();
    }

    @Override
    public void desactivar(Long id, Long version) {
        PlantelNivel entidad = buscar(id);
        verificar(entidad, version, "Oferta educativa");
        entidad.setActivo(false);
    }

    private PlantelNivel buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la oferta educativa", id));
    }

    private Plantel plantel(Long id) {
        return plantelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el plantel", id));
    }

    private NivelEducativo nivel(Long id) {
        return nivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el nivel educativo", id));
    }

    private void validarRelacion(Plantel plantel, NivelEducativo nivel) {
        if (!plantel.isActivo() || !nivel.isActivo()) {
            throw new ReglaNegocioException("El plantel y el nivel deben estar activos para crear una oferta");
        }
        if (!plantel.getInstitucion().getId().equals(nivel.getInstitucion().getId())) {
            throw new ReglaNegocioException("El plantel y el nivel deben pertenecer a la misma institución");
        }
    }

    private void validarDuplicado(Long plantelId, Long nivelId, Long idExcluido) {
        if (repository.existsByPlantelIdAndNivelEducativoIdAndIdNot(plantelId, nivelId, idExcluido)) {
            throw new RecursoDuplicadoException("El plantel ya tiene registrada esa oferta educativa");
        }
    }
}
