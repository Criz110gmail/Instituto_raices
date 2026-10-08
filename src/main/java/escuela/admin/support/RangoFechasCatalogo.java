package escuela.admin.support;

import escuela.admin.dto.FiltroCatalogo;
import escuela.cobranza.entity.*;
import escuela.finanzas.entity.Pago;
import jakarta.persistence.criteria.*;
import java.time.*;
import java.util.ArrayList;
import org.springframework.data.jpa.domain.Specification;

public final class RangoFechasCatalogo {
    private RangoFechasCatalogo() { }

    public static Specification<Cargo> cargos(FiltroCatalogo f) {
        return (r,q,cb) -> fechas(r.get("REGISTRO".equals(f.tipoFecha()) ? "fechaEmision" : "fechaVencimiento"),f,cb);
    }
    public static Specification<Pago> pagos(FiltroCatalogo f) {
        return (r,q,cb) -> {
            if (!f.tieneRango()) return cb.conjunction();
            // Mismo día local que se muestra en la tabla; no UTC ni fecha de validación.
            var local = cb.function("timezone",LocalDateTime.class,r.get("institucion").get("zonaHoraria"),r.get("fechaPago"));
            return fechas(cb.function("date",LocalDate.class,local),f,cb);
        };
    }
    public static Specification<CuotaAlumno> cuotas(FiltroCatalogo f) {
        return (r,q,cb) -> {
            if (!f.tieneRango()) return cb.conjunction();
            var unica = cb.and(cb.equal(r.get("frecuencia"),FrecuenciaCuota.UNICA),fechas(r.get("fechaVencimientoUnico"),f,cb));
            var mensual = cb.and(cb.equal(r.get("frecuencia"),FrecuenciaCuota.MENSUAL),
                    cb.isTrue(cb.function("cuota_vencimiento_en_rango",Boolean.class,r.get("fechaInicio"),r.get("fechaFin"),r.get("diaVencimiento"),
                            f.desde()==null?cb.nullLiteral(LocalDate.class):cb.literal(f.desde()),
                            f.hasta()==null?cb.nullLiteral(LocalDate.class):cb.literal(f.hasta()))));
            return cb.or(unica,mensual);
        };
    }
    private static Predicate fechas(Expression<LocalDate> fecha,FiltroCatalogo f,CriteriaBuilder cb) {
        var p = new ArrayList<Predicate>();
        if(f.desde()!=null) p.add(cb.greaterThanOrEqualTo(fecha,f.desde()));
        if(f.hasta()!=null) p.add(cb.lessThanOrEqualTo(fecha,f.hasta()));
        return cb.and(p.toArray(Predicate[]::new));
    }
}
