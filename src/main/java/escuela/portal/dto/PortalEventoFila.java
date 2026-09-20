package escuela.portal.dto;

import java.time.LocalDateTime;

public record PortalEventoFila(
        Long id, String titulo, String tipo, String ubicacion, String descripcion,
        LocalDateTime inicio, LocalDateTime fin, String plantel, String alcance) { }
