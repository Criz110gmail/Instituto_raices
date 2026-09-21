package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.auditoria.entity.Auditoria;
import escuela.auditoria.repository.AuditoriaRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditoriaConsultaService {
    private static final DateTimeFormatter FECHA = DateTimeFormatter
            .ofPattern("dd MMM yyyy · HH:mm:ss", new Locale("es", "MX"));

    private final AuditoriaRepository repository;
    private final InstitucionRepository institucionRepository;
    private final AlcanceDatosService alcance;

    public Page<AuditoriaFila> consultar(FiltroAuditoria original) {
        FiltroAuditoria filtro = original.normalizado();
        if (filtro.institucionId() == null) throw new ReglaNegocioException("Selecciona una institución");
        if (filtro.fechaDesde() != null && filtro.fechaHasta() != null
                && filtro.fechaDesde().isAfter(filtro.fechaHasta()))
            throw new ReglaNegocioException("La fecha inicial no puede ser posterior a la fecha final");
        alcance.validarAdministracionInstitucional(filtro.institucionId());
        var institucion = institucionRepository.findById(filtro.institucionId())
                .orElseThrow(() -> new ReglaNegocioException("La institución seleccionada no existe"));
        ZoneId zona = ZoneId.of(institucion.getZonaHoraria());
        return repository.findAll(especificacion(filtro, zona), PageRequest.of(filtro.pagina(), filtro.tamanio(),
                Sort.by(Sort.Order.desc("ocurridoEn"), Sort.Order.desc("id"))))
                .map(a -> fila(a, zona));
    }

    private Specification<Auditoria> especificacion(FiltroAuditoria f, ZoneId zona) {
        return Specification.<Auditoria>where((r,q,cb)->cb.equal(r.get("institucion").get("id"),f.institucionId()))
                .and((r,q,cb)->f.accion().equals("TODAS")?cb.conjunction():cb.equal(r.get("accion").as(String.class),f.accion()))
                .and((r,q,cb)->f.tipoEntidad().isBlank()?cb.conjunction():cb.equal(cb.upper(r.get("tipoEntidad")),f.tipoEntidad()))
                .and((r,q,cb)->f.entidadId().isBlank()?cb.conjunction():cb.equal(r.get("entidadId"),f.entidadId()))
                .and((r,q,cb)->f.actor().isBlank()?cb.conjunction():cb.or(
                        cb.like(cb.lower(r.join("usuarioActor", JoinType.LEFT).get("username")),patron(f.actor())),
                        cb.like(cb.lower(r.get("actorSistema")),patron(f.actor()))))
                .and((r,q,cb)->f.correlacion().isBlank()?cb.conjunction():cb.like(cb.lower(r.get("correlacionId")),patron(f.correlacion())))
                .and((r,q,cb)->f.fechaDesde()==null?cb.conjunction():cb.greaterThanOrEqualTo(r.get("ocurridoEn"),f.fechaDesde().atStartOfDay(zona).toInstant()))
                .and((r,q,cb)->f.fechaHasta()==null?cb.conjunction():cb.lessThan(r.get("ocurridoEn"),f.fechaHasta().plusDays(1).atStartOfDay(zona).toInstant()));
    }

    private AuditoriaFila fila(Auditoria a, ZoneId zona) {
        String actor = a.getUsuarioActor() == null ? a.getActorSistema() : a.getUsuarioActor().getUsername();
        return new AuditoriaFila(a.getId(), FECHA.withZone(zona).format(a.getOcurridoEn()), actor,
                a.getAccion().name(), a.getTipoEntidad(), a.getEntidadId(), valor(a.getMotivo()),
                valor(a.getCambios()), a.getCorrelacionId());
    }

    private String patron(String texto) { return "%" + texto.toLowerCase(Locale.ROOT) + "%"; }
    private String valor(String texto) { return texto == null || texto.isBlank() ? "—" : texto; }
}
