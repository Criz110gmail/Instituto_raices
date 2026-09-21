package escuela.portal.dto;

import org.springframework.data.domain.Page;

public record PortalNotificaciones(long pendientes, Page<PortalNotificacionFila> pagina) { }
