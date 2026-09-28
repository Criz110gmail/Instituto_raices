package escuela.calificacion.service;

import escuela.calificacion.dto.CapturaCalificacionRequest;
import escuela.calificacion.dto.HojaCalificacionesResponse;
import escuela.calificacion.dto.PortalCalificacionResponse;
import org.springframework.data.domain.Page;

public interface CalificacionService {
    HojaCalificacionesResponse hoja(Long grupoId, Long periodoId, Long materiaGradoId);
    HojaCalificacionesResponse guardar(CapturaCalificacionRequest request, boolean publicar);
    HojaCalificacionesResponse reabrir(Long grupoId, Long periodoId, Long materiaGradoId);
    Page<PortalCalificacionResponse> publicadasAlumno(Long alumnoId, int pagina, int tamanio);
}
