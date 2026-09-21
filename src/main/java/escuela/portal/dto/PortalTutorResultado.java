package escuela.portal.dto;

import escuela.admin.dto.ResultadoEstadoCuentaAlumno;
import org.springframework.data.domain.Page;

import java.util.List;

public record PortalTutorResultado(
        String tutor, String institucion, List<PortalHijoResumen> hijos, PortalHijoResumen hijo,
        ResultadoEstadoCuentaAlumno estadoCuenta, Page<PortalEventoFila> eventos,
        Page<PortalAvisoFila> avisos, PortalNotificaciones notificaciones) { }
