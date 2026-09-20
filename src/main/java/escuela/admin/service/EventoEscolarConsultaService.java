package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.comunicacion.entity.*;
import escuela.comunicacion.repository.EventoEscolarRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class EventoEscolarConsultaService {
    private static final DateTimeFormatter FECHA = DateTimeFormatter
            .ofPattern("dd MMM yyyy · HH:mm", new Locale("es", "MX"));
    private final EventoEscolarRepository repository;
    private final InstitucionService institucionService;
    private final AlcanceDatosService alcance;

    public Page<EventoEscolarFila> consultar(FiltroEventoEscolar original) {
        FiltroEventoEscolar f = original.normalizado();
        if (f.institucionId() == null) throw new ReglaNegocioException("Selecciona una institución");
        alcance.validarInstitucion(f.institucionId());
        if (f.plantelId() != null) alcance.validarPlantel(f.plantelId());
        if (f.fechaDesde() != null && f.fechaHasta() != null && f.fechaDesde().isAfter(f.fechaHasta()))
            throw new ReglaNegocioException("La fecha inicial no puede ser posterior a la fecha final");
        ZoneId zona = ZoneId.of(institucionService.obtener(f.institucionId()).zonaHoraria());
        Specification<EventoEscolar> spec = (root, query, cb) -> cb.equal(root.get("institucion").get("id"), f.institucionId());
        spec = spec.and(alcance.especificacion(ModuloCatalogo.EVENTOS_ESCOLARES));
        if (f.plantelId() != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("plantel").get("id"), f.plantelId()));
        if (!f.texto().isBlank()) {
            String patron = "%" + f.texto().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.or(cb.like(cb.lower(root.get("titulo")), patron),
                    cb.like(cb.lower(root.get("ubicacion")), patron),
                    cb.like(cb.lower(cb.coalesce(root.get("descripcion"), "")), patron)));
        }
        if (!f.estado().equals("TODOS")) spec = spec.and((root, query, cb) ->
                cb.equal(root.get("estado"), EstadoEventoEscolar.valueOf(f.estado())));
        if (!f.tipo().equals("TODOS")) spec = spec.and((root, query, cb) ->
                cb.equal(root.get("tipo"), TipoEventoEscolar.valueOf(f.tipo())));
        if (f.fechaDesde() != null) { Instant desde = f.fechaDesde().atStartOfDay(zona).toInstant();
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("inicioEn"), desde)); }
        if (f.fechaHasta() != null) { Instant hasta = f.fechaHasta().plusDays(1).atStartOfDay(zona).toInstant();
            spec = spec.and((root, query, cb) -> cb.lessThan(root.get("inicioEn"), hasta)); }
        Page<EventoEscolar> pagina = repository.findAll(spec,
                PageRequest.of(f.pagina(), f.tamanio(), Sort.by(Sort.Order.desc("inicioEn"), Sort.Order.desc("id"))));
        return pagina.map(e -> fila(e, zona));
    }

    private EventoEscolarFila fila(EventoEscolar e, ZoneId zona) {
        return new EventoEscolarFila(e.getId(), e.getTitulo(), e.getInstitucion().getNombre(),
                e.getPlantel() == null ? "Institucional" : e.getPlantel().getNombre(),
                e.getCicloEscolar().getNombre(), FECHA.withZone(zona).format(e.getInicioEn()),
                FECHA.withZone(zona).format(e.getFinEn()), e.getTipo().name(), e.getEstado().name(),
                e.getAlcance().name(), e.getCantidadDestinatarios());
    }
}
