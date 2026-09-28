package escuela.asistencia.dto;

import java.time.LocalDate;
import java.util.List;

public record HojaAsistenciaResponse(Long grupoId, String grupo, String grado, String plantel,
                                     String ciclo, LocalDate fecha, List<FilaAsistenciaResponse> filas) {}
