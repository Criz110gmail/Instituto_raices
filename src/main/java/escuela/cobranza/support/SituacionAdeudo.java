package escuela.cobranza.support;

import escuela.cobranza.entity.*;
import escuela.finanzas.entity.AplicacionPago;
import escuela.finanzas.entity.OperacionAplicacionPago;
import jakarta.persistence.criteria.Expression;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

/** Situación de consulta; no modifica los estados persistidos ni operaciones financieras. */
public final class SituacionAdeudo {
    private SituacionAdeudo() { }

    public static String etiqueta(Cargo cargo, LocalDate hoy) {
        if (cargo.getEstadoRegistro() == EstadoRegistroCargo.CANCELADO) return "Cancelado";
        if (cargo.getEstadoRegistro() == EstadoRegistroCargo.CONVENIDO) return "Incluido en convenio";
        if (CalculoCargo.saldo(cargo).signum() == 0) return "Liquidado";
        boolean parcial = CalculoCargo.aplicado(cargo).signum() > 0;
        boolean vencido = cargo.getFechaVencimiento().isBefore(hoy);
        if (parcial) return vencido ? "Parcial · Vencido" : "Pago parcial";
        return vencido ? "Vencido" : "Pendiente sin abonos";
    }

    public static Specification<Cargo> filtro(String situacion, LocalDate hoy) {
        return (root, query, cb) -> {
            var emitido = cb.equal(root.get("estadoRegistro"), EstadoRegistroCargo.EMITIDO);
            // Compatibilidad con enlaces anteriores; Emitido no se ofrece en el selector.
            switch (situacion) {
                case "TODOS": return cb.conjunction();
                case "EMITIDO": return emitido;
                case "CANCELADO": return cb.equal(root.get("estadoRegistro"), EstadoRegistroCargo.CANCELADO);
                case "CONVENIDO": return cb.equal(root.get("estadoRegistro"), EstadoRegistroCargo.CONVENIDO);
                case "CON_SALDO", "PENDIENTE", "PARCIAL", "VENCIDO", "LIQUIDADO": break;
                default: return cb.disjunction();
            }

            // Agregaciones independientes evitan multiplicar ajustes por aplicaciones.
            // Se ejecutan antes del conteo y LIMIT, tanto para pantalla como para Excel.
            var ajustes = query.subquery(BigDecimal.class);
            var ajuste = ajustes.from(AjusteCargo.class);
            var montoAjuste = ajuste.<BigDecimal>get("monto");
            Expression<BigDecimal> ajusteFirmado = cb.<BigDecimal>selectCase()
                    .when(cb.equal(ajuste.get("efecto"), EfectoAjusteCargo.AUMENTO), montoAjuste)
                    .otherwise(cb.neg(montoAjuste));
            ajustes.select(cb.sum(ajusteFirmado)).where(cb.equal(ajuste.get("cargo"), root));

            var aplicaciones = query.subquery(BigDecimal.class);
            var aplicacion = aplicaciones.from(AplicacionPago.class);
            var montoAplicado = aplicacion.<BigDecimal>get("monto");
            Expression<BigDecimal> aplicadoFirmado = cb.<BigDecimal>selectCase()
                    .when(cb.equal(aplicacion.get("operacion"), OperacionAplicacionPago.APLICAR), montoAplicado)
                    .otherwise(cb.neg(montoAplicado));
            aplicaciones.select(cb.sum(aplicadoFirmado)).where(cb.equal(aplicacion.get("cargo"), root));

            var neto = cb.sum(root.<BigDecimal>get("importeOriginal"), cb.coalesce(ajustes, BigDecimal.ZERO));
            Expression<BigDecimal> total = cb.<BigDecimal>selectCase()
                    .when(cb.greaterThan(neto, BigDecimal.ZERO), neto).otherwise(BigDecimal.ZERO);
            var abonado = cb.coalesce(aplicaciones, BigDecimal.ZERO);
            var saldo = cb.diff(total, abonado);
            var debe = cb.greaterThan(saldo, BigDecimal.ZERO);
            var vencido = cb.lessThan(root.<LocalDate>get("fechaVencimiento"), hoy);
            return switch (situacion) {
                case "CON_SALDO" -> cb.and(emitido, debe);
                case "PENDIENTE" -> cb.and(emitido, debe, cb.lessThanOrEqualTo(abonado, BigDecimal.ZERO), cb.not(vencido));
                case "PARCIAL" -> cb.and(emitido, debe, cb.greaterThan(abonado, BigDecimal.ZERO));
                case "VENCIDO" -> cb.and(emitido, debe, vencido);
                case "LIQUIDADO" -> cb.and(emitido, cb.lessThanOrEqualTo(saldo, BigDecimal.ZERO));
                default -> cb.disjunction();
            };
        };
    }
}
