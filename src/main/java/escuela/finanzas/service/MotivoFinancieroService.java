package escuela.finanzas.service;

import escuela.finanzas.dto.request.MotivoFinancieroRequest;
import escuela.finanzas.dto.response.MotivoFinancieroResponse;

import java.util.List;

public interface MotivoFinancieroService {
    MotivoFinancieroResponse crear(MotivoFinancieroRequest request);
    MotivoFinancieroResponse actualizar(Long id, MotivoFinancieroRequest request);
    MotivoFinancieroResponse obtener(Long id);
    List<MotivoFinancieroResponse> listarActivos(Long institucionId);
    void desactivar(Long id, Long version);
}
