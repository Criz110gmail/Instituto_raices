package escuela.cobranza.support;

import escuela.cobranza.entity.*;
import escuela.finanzas.entity.OperacionAplicacionPago;

import java.math.*;

public final class CalculoCargo {
    private CalculoCargo() { }

    public static BigDecimal total(Cargo cargo) {
        BigDecimal total = cargo.getImporteOriginal();
        for (AjusteCargo ajuste : cargo.getAjustes()) {
            total = ajuste.getEfecto() == EfectoAjusteCargo.AUMENTO
                    ? total.add(ajuste.getMonto()) : total.subtract(ajuste.getMonto());
        }
        return total.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal aplicado(Cargo cargo) {
        return cargo.getAplicaciones().stream()
                .map(a -> a.getOperacion() == OperacionAplicacionPago.APLICAR
                        ? a.getMonto() : a.getMonto().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal saldo(Cargo cargo) {
        return total(cargo).subtract(aplicado(cargo)).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
