package escuela.admin.dto;

import java.util.List;

public record BoletaDetalle(Long inscripcionId, Long institucionId, String institucion,
                            String numeroInscripcion, String matricula, String alumno,
                            String plantel, String ciclo, String grado, String grupo,
                            List<BoletaCalificacionFila> calificaciones) { }

