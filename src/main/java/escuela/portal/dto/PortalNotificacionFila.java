package escuela.portal.dto;

import java.time.LocalDateTime;

public record PortalNotificacionFila(Long id,String tipo,String titulo,String mensaje,
                                     LocalDateTime creada,boolean leida,String destino) { }
