package escuela.admin.support;

import escuela.cobranza.entity.AcuerdoAnticipadoCargo;
import escuela.finanzas.entity.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

/** Incluye el historial solicitado/aplicado y anticipos sin duplicar pagos familiares. */
public final class FiltroAlumnoPago {
    private FiltroAlumnoPago() {}

    public static Specification<Pago> especificacion(Long alumnoId) {
        return (pago, query, cb) -> alumnoId == null ? cb.conjunction()
                : coincide(pago, query, cb, cb.literal(alumnoId));
    }

    public static Predicate coincide(Path<Pago> pago, CommonAbstractCriteria query, CriteriaBuilder cb,
                                      Expression<Long> alumnoId) {
        var solicitudes = query.subquery(Integer.class);
        var solicitud = solicitudes.from(SolicitudAplicacionPago.class);
        solicitudes.select(cb.literal(1)).where(cb.equal(solicitud.get("pago"), pago),
                cb.equal(solicitud.get("cargo").get("inscripcion").get("alumno").get("id"), alumnoId));
        var aplicaciones = query.subquery(Integer.class);
        var aplicacion = aplicaciones.from(AplicacionPago.class);
        aplicaciones.select(cb.literal(1)).where(cb.equal(aplicacion.get("pago"), pago),
                cb.equal(aplicacion.get("cargo").get("inscripcion").get("alumno").get("id"), alumnoId));
        var acuerdos = query.subquery(Integer.class);
        var detalle = acuerdos.from(AcuerdoAnticipadoCargo.class);
        acuerdos.select(cb.literal(1)).where(cb.equal(detalle.get("acuerdo").get("id"), pago.get("acuerdoAnticipadoId")),
                cb.equal(detalle.get("cargo").get("inscripcion").get("alumno").get("id"), alumnoId));
        return cb.or(cb.exists(solicitudes), cb.exists(aplicaciones), cb.exists(acuerdos));
    }
}
