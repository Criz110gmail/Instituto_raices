package escuela.admin.dto;

public record AuditoriaFila(Long id, String fecha, String actor, String accion,
                            String tipoEntidad, String entidadId, String motivo,
                            String cambios, String correlacion) { }
