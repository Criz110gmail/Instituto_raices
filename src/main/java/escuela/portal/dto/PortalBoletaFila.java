package escuela.portal.dto;

public record PortalBoletaFila(Long inscripcionId, String ciclo, String plantel,
                               String grado, String grupo, long materias, long periodos) { }
