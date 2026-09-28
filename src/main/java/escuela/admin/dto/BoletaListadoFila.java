package escuela.admin.dto;

public record BoletaListadoFila(Long inscripcionId, String matricula, String alumno,
                                String plantel, String grado, String grupo,
                                long periodosPublicados, long materiasPublicadas) { }

