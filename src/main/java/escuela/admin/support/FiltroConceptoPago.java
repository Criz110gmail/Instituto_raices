package escuela.admin.support;

import escuela.admin.dto.FiltroCatalogo;
import escuela.cobranza.entity.AcuerdoAnticipadoCargo;
import escuela.finanzas.entity.*;
import jakarta.persistence.criteria.*;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

/** EXISTS evita duplicar pagos cuando el mismo concepto incluye varias mensualidades. */
public final class FiltroConceptoPago {
    private FiltroConceptoPago() {}

    public static Specification<Pago> especificacion(FiltroCatalogo filtro) {
        return (pago,query,cb)-> {
            String texto=filtro.conceptoCobro()==null?"":filtro.conceptoCobro().trim();
            if(texto.isEmpty())return cb.conjunction();
            String patron="%"+texto.toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%";
            var solicitudes=query.subquery(Integer.class);
            var solicitud=solicitudes.from(SolicitudAplicacionPago.class);
            solicitudes.select(cb.literal(1)).where(cb.equal(solicitud.get("pago"),pago),coincide(solicitud.get("cargo"),cb,patron));
            var aplicaciones=query.subquery(Integer.class);
            var aplicacion=aplicaciones.from(AplicacionPago.class);
            aplicaciones.select(cb.literal(1)).where(cb.equal(aplicacion.get("pago"),pago),coincide(aplicacion.get("cargo"),cb,patron));
            var acuerdos=query.subquery(Integer.class);
            var detalle=acuerdos.from(AcuerdoAnticipadoCargo.class);
            acuerdos.select(cb.literal(1)).where(cb.equal(detalle.get("acuerdo").get("id"),pago.get("acuerdoAnticipadoId")),coincide(detalle.get("cargo"),cb,patron));
            return cb.or(cb.exists(solicitudes),cb.exists(aplicaciones),cb.exists(acuerdos));
        };
    }

    private static Predicate coincide(Path<?> cargo,CriteriaBuilder cb,String patron) {
        var concepto=cargo.get("conceptoCobro");
        return cb.or(cb.like(cb.lower(concepto.get("codigo")),patron,'\\'),
                     cb.like(cb.lower(concepto.get("nombre")),patron,'\\'));
    }
}
