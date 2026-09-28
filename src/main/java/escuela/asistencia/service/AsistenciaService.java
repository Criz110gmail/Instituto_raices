package escuela.asistencia.service;

import escuela.asistencia.dto.CapturaAsistenciaRequest;
import escuela.asistencia.dto.HojaAsistenciaResponse;
import java.time.LocalDate;

public interface AsistenciaService {
    HojaAsistenciaResponse hoja(Long grupoId, LocalDate fecha);
    HojaAsistenciaResponse guardar(CapturaAsistenciaRequest request);
}
