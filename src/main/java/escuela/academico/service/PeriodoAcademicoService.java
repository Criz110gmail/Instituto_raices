package escuela.academico.service;

import escuela.academico.dto.request.PeriodoAcademicoRequest;
import escuela.academico.dto.response.PeriodoAcademicoResponse;

import java.util.List;

public interface PeriodoAcademicoService {
    PeriodoAcademicoResponse crear(PeriodoAcademicoRequest request);
    PeriodoAcademicoResponse actualizar(Long id, PeriodoAcademicoRequest request);
    PeriodoAcademicoResponse obtener(Long id);
    List<PeriodoAcademicoResponse> listarPorCicloYNivel(Long cicloEscolarId, Long nivelEducativoId);
}
