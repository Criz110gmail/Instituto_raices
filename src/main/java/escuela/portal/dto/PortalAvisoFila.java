package escuela.portal.dto;

import java.time.LocalDateTime;

public record PortalAvisoFila(Long id, String titulo, String contenido, String alcance,
                              LocalDateTime publicado, LocalDateTime expira) { }
