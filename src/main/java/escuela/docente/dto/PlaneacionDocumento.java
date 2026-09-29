package escuela.docente.dto;

import java.time.*;
import java.util.List;

public record PlaneacionDocumento(Long planeacionId, Long grupoId, int revision, String institucion,
                                  String maestro, String numeroEmpleado, String plantel,
                                  String ciclo, String grado, String grupo,
                                  LocalDate fechaInicio, LocalDate fechaFin, String estado,
                                  String situacionDidactica, String proposito, String ejesArticuladores,
                                  String conocimientos, String habilidades, String actitudes,
                                  String tecnicaEvaluacion, String instrumentoEvaluacion,
                                  String recursos, String actividadesPermanentes,
                                  String ajustesRazonables, String observaciones,
                                  List<Materia> materias, List<Alineacion> alineaciones,
                                  List<Actividad> actividades, Instant publicadaEn) {
    public record Materia(Long id, String codigo, String nombre) { }
    public record Alineacion(Long materiaId, String materia, String campoFormativo,
                             String contenido, String procesoDesarrollo) { }
    public record Actividad(Long materiaId, String materia, LocalDate fecha, String titulo,
                            String inicio, String desarrollo, String cierre,
                            Integer duracionMinutos, String tarea, String observaciones) { }
}
