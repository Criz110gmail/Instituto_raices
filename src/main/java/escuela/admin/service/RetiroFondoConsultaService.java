package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.entity.RetiroFondo;
import escuela.finanzas.repository.RetiroFondoRepository;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.service.AlcanceDatosService;
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
public class RetiroFondoConsultaService {
    private static final DateTimeFormatter FECHA = DateTimeFormatter
            .ofPattern("dd MMM yyyy · HH:mm", new Locale("es", "MX"));
    private final RetiroFondoRepository retiros;
    private final InstitucionRepository instituciones;
    private final AlcanceDatosService alcance;

    public Page<RetiroFondoFila> consultar(FiltroRetiroFondo original) {
        FiltroRetiroFondo filtro = original.normalizado();
        if (filtro.institucionId() == null)
            throw new ReglaNegocioException("Selecciona una institución para consultar retiros");
        alcance.validarInstitucion(filtro.institucionId());
        ZoneId zona = ZoneId.of(instituciones.findById(filtro.institucionId())
                .orElseThrow(() -> new ReglaNegocioException("La institución no existe")).getZonaHoraria());
        Specification<RetiroFondo> especificacion = Specification
                .where(alcance.<RetiroFondo>especificacion(ModuloCatalogo.MOVIMIENTOS_FINANCIEROS))
                .and((root, query, cb) -> cb.equal(root.get("institucion").get("id"), filtro.institucionId()))
                .and((root, query, cb) -> filtro.cuentaId() == null ? cb.conjunction()
                        : cb.equal(root.get("cuenta").get("id"), filtro.cuentaId()))
                .and((root, query, cb) -> filtro.plantelId() == null ? cb.conjunction()
                        : cb.equal(root.get("plantelOperacion").get("id"), filtro.plantelId()))
                .and((root, query, cb) -> filtro.fechaDesde() == null ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("fechaOperacion"),
                        filtro.fechaDesde().atStartOfDay(zona).toInstant()))
                .and((root, query, cb) -> filtro.fechaHasta() == null ? cb.conjunction()
                        : cb.lessThan(root.get("fechaOperacion"),
                        filtro.fechaHasta().plusDays(1).atStartOfDay(zona).toInstant()))
                .and((root, query, cb) -> filtro.beneficiario().isEmpty() ? cb.conjunction()
                        : cb.like(cb.lower(root.get("beneficiario")),
                        "%" + filtro.beneficiario().toLowerCase(Locale.ROOT)
                                .replace("\\", "\\\\").replace("%", "\\%")
                                .replace("_", "\\_") + "%", '\\'));
        return retiros.findAll(especificacion, PageRequest.of(filtro.pagina(), filtro.tamanio(),
                Sort.by(Sort.Order.desc("fechaOperacion"), Sort.Order.desc("id"))))
                .map(r -> fila(r, zona));
    }

    private RetiroFondoFila fila(RetiroFondo r, ZoneId zona) {
        return new RetiroFondoFila(r.getId(), FECHA.withZone(zona).format(r.getFechaOperacion()),
                r.getCuenta().getCodigo() + " · " + r.getCuenta().getNombre(),
                r.getPlantelOperacion() == null ? "Institucional" : r.getPlantelOperacion().getNombre(),
                r.getBeneficiario(), r.getMotivoFinanciero().getNombre(), r.getConcepto(),
                r.getReferencia(), r.getMonto(), r.getCuenta().getMoneda(),
                r.getAutorizadoPor().getUsername(),
                r.getMovimiento().getReversa() == null ? "EJECUTADO" : "REVERTIDO",
                r.getMovimiento().getId());
    }
}
