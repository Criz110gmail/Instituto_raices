package escuela.portal.service;

import escuela.comunicacion.entity.*;
import escuela.comunicacion.repository.NotificacionUsuarioRepository;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.institucion.service.InstitucionService;
import escuela.portal.dto.PortalNotificaciones;
import escuela.portal.repository.PortalNotificacionRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.time.*;

@Service @RequiredArgsConstructor
public class NotificacionPortalService {
    private static final int TAMANIO=10;
    private final PortalNotificacionRepository portal;
    private final NotificacionUsuarioRepository repository;
    private final InstitucionService instituciones;

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public PortalNotificaciones sincronizarYConsultar(UsuarioPrincipal principal,int pagina){
        validar(principal);var institucion=instituciones.obtener(principal.institucionId());
        ZoneId zona=ZoneId.of(institucion.zonaHoraria());LocalDate hoy=LocalDate.now(zona);Instant ahora=Instant.now();
        portal.sincronizar(principal.usuarioId(),principal.institucionId(),hoy,
                hoy.minusDays(30).atStartOfDay(zona).toInstant(),
                hoy.minusDays(90).atStartOfDay(zona).toInstant(),ahora);
        return portal.consultar(principal.usuarioId(),institucion.zonaHoraria(),Math.max(0,pagina),TAMANIO);
    }

    @Transactional
    public String marcarLeida(UsuarioPrincipal principal,Long id){
        validar(principal);var institucion=instituciones.obtener(principal.institucionId());
        ZoneId zona=ZoneId.of(institucion.zonaHoraria());LocalDate hoy=LocalDate.now(zona);Instant ahora=Instant.now();
        NotificacionUsuario n=repository.findByIdForUpdate(id)
                .orElseThrow(()->new RecursoNoEncontradoException("la notificación",id));
        if(!n.getUsuario().getId().equals(principal.usuarioId()))throw new AccessDeniedException("La notificación no pertenece a esta cuenta");
        boolean accesible=switch(n.getTipo()){
            case EVENTO -> portal.eventoAccesible(principal.usuarioId(),principal.institucionId(),n.getEvento().getId(),hoy);
            case AVISO -> portal.avisoAccesible(principal.usuarioId(),principal.institucionId(),n.getAviso().getId(),hoy,ahora);
            case PAGO_VALIDADO -> portal.pagoAccesible(principal.usuarioId(),principal.institucionId(),n.getPago().getId(),"VALIDADO",hoy);
            case PAGO_RECHAZADO -> portal.pagoAccesible(principal.usuarioId(),principal.institucionId(),n.getPago().getId(),"RECHAZADO",hoy);
            case PAGO_CANCELADO -> portal.pagoAccesible(principal.usuarioId(),principal.institucionId(),n.getPago().getId(),"CANCELADO",hoy);
        };
        if(!accesible)throw new AccessDeniedException("El contenido de la notificación ya no está disponible para esta cuenta");
        if(n.getLeidaEn()==null)n.setLeidaEn(ahora);
        repository.saveAndFlush(n);return switch(n.getTipo()){
            case EVENTO -> "agenda";
            case AVISO -> "avisos";
            case PAGO_VALIDADO,PAGO_RECHAZADO,PAGO_CANCELADO -> "cuenta";
        };
    }

    private void validar(UsuarioPrincipal p){if(p==null||p.usuarioId()==null||p.institucionId()==null||p.accesoRecuperacion())
        throw new AccessDeniedException("Las notificaciones requieren una cuenta de tutor activa");}
}
