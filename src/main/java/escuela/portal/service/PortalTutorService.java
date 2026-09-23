package escuela.portal.service;

import escuela.admin.dto.*;
import escuela.admin.repository.ReporteFinancieroRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.portal.dto.*;
import escuela.portal.repository.PortalTutorRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortalTutorService {
    private static final int TAMANIO = 10;
    private final PortalTutorRepository portalRepository;
    private final ReporteFinancieroRepository reporteRepository;
    private final InstitucionService institucionService;
    private final NotificacionPortalService notificaciones;

    public PortalTutorResultado consultar(UsuarioPrincipal principal, Long alumnoId,
                                           int paginaEventos, int paginaCargos, int paginaAvisos,
                                           int paginaNotificaciones, int paginaPagos) {
        return consultar(principal, alumnoId, paginaEventos, paginaCargos, paginaAvisos,
                paginaNotificaciones, paginaPagos, true);
    }

    public PortalTutorResultado consultarComoSoporte(UsuarioPrincipal principal, Long alumnoId,
                                                       int paginaEventos, int paginaCargos, int paginaAvisos,
                                                       int paginaPagos) {
        return consultar(principal, alumnoId, paginaEventos, paginaCargos, paginaAvisos, 0, paginaPagos, false);
    }

    private PortalTutorResultado consultar(UsuarioPrincipal principal, Long alumnoId,
                                           int paginaEventos, int paginaCargos, int paginaAvisos,
                                           int paginaNotificaciones, int paginaPagos,
                                           boolean sincronizarNotificaciones) {
        validarPrincipal(principal);
        var institucion = institucionService.obtener(principal.institucionId());
        ZoneId zona = ZoneId.of(institucion.zonaHoraria());
        LocalDate hoy = LocalDate.now(zona);
        String tutor = portalRepository.nombreTutor(principal.usuarioId(), principal.institucionId());
        if (tutor == null) throw new AccessDeniedException(
                "La cuenta no está vinculada con un tutor activo de la institución");
        List<PortalHijoResumen> hijos = portalRepository.hijos(
                principal.usuarioId(), principal.institucionId(), hoy);
        PortalHijoResumen hijo = seleccionar(hijos, alumnoId);
        var bandeja = sincronizarNotificaciones
                ? notificaciones.sincronizarYConsultar(principal, paginaNotificaciones)
                : new PortalNotificaciones(0, org.springframework.data.domain.Page.<PortalNotificacionFila>empty());
        if (hijo == null) return new PortalTutorResultado(tutor, institucion.nombre(), hijos, null, null,
                org.springframework.data.domain.Page.empty(), org.springframework.data.domain.Page.empty(), bandeja,
                org.springframework.data.domain.Page.empty());
        int eventosPagina = Math.max(0, paginaEventos);
        int cargosPagina = Math.max(0, paginaCargos);
        Instant desdeEventos = hoy.minusDays(30).atStartOfDay(zona).toInstant();
        var eventos = portalRepository.eventos(hijo.alumnoId(), principal.institucionId(),
                institucion.zonaHoraria(), desdeEventos, eventosPagina, TAMANIO);
        var avisos = portalRepository.avisos(hijo.alumnoId(), principal.institucionId(),
                institucion.zonaHoraria(), hoy, Instant.now(), Math.max(0, paginaAvisos), TAMANIO);
        ResultadoEstadoCuentaAlumno estadoCuenta = hijo.accesoFinanciero()
                ? estadoCuenta(hijo, principal.institucionId(), hoy, zona, institucion.monedaPredeterminada(), cargosPagina)
                : null;
        org.springframework.data.domain.Page<PortalPagoFila> pagos = hijo.accesoFinanciero()
                ? Optional.ofNullable(portalRepository.pagos(principal.usuarioId(), principal.institucionId(), institucion.zonaHoraria(), Math.max(0,paginaPagos), TAMANIO))
                    .orElseGet(() -> org.springframework.data.domain.Page.<PortalPagoFila>empty())
                : org.springframework.data.domain.Page.<PortalPagoFila>empty();
        return new PortalTutorResultado(tutor, institucion.nombre(), hijos, hijo, estadoCuenta, eventos, avisos, bandeja, pagos);
    }

    public PortalHijoResumen validarHijo(UsuarioPrincipal principal, Long alumnoId) {
        validarPrincipal(principal);
        var institucion = institucionService.obtener(principal.institucionId());
        LocalDate hoy = LocalDate.now(ZoneId.of(institucion.zonaHoraria()));
        return portalRepository.hijos(principal.usuarioId(), principal.institucionId(), hoy).stream()
                .filter(h -> h.alumnoId().equals(alumnoId)).findFirst()
                .orElseThrow(() -> new AccessDeniedException("El alumno no está disponible para esta cuenta"));
    }

    private ResultadoEstadoCuentaAlumno estadoCuenta(PortalHijoResumen hijo, Long institucionId,
                                                       LocalDate hoy, ZoneId zona, String moneda,
                                                       int pagina) {
        FiltroEstadoCuentaAlumno filtro = new FiltroEstadoCuentaAlumno(institucionId, hijo.alumnoId(),
                hijo.nombre(), null, "TODOS", hoy, pagina, TAMANIO);
        Instant corte = hoy.plusDays(1).atStartOfDay(zona).toInstant();
        AlcanceReporteFinanciero alcance = new AlcanceReporteFinanciero(true, Set.of());
        return new ResultadoEstadoCuentaAlumno(hijo.alumnoId(), hijo.nombre(), hijo.matricula(),
                reporteRepository.estadoCuenta(filtro, alcance, corte),
                reporteRepository.resumenEstadoCuenta(filtro, alcance, corte, moneda));
    }

    private PortalHijoResumen seleccionar(List<PortalHijoResumen> hijos, Long alumnoId) {
        if (hijos.isEmpty()) return null;
        if (alumnoId == null) return hijos.getFirst();
        return hijos.stream().filter(h -> h.alumnoId().equals(alumnoId)).findFirst()
                .orElseThrow(() -> new ReglaNegocioException(
                        "El alumno seleccionado ya no tiene un vínculo vigente con esta cuenta"));
    }

    private void validarPrincipal(UsuarioPrincipal principal) {
        if (principal == null || principal.usuarioId() == null || principal.institucionId() == null
                || principal.accesoRecuperacion()) {
            throw new AccessDeniedException("El portal familiar requiere una cuenta de tutor activa");
        }
    }
}
