package escuela.admin.dto;

import java.math.BigDecimal;

public record ResumenEstadoCuenta(long cargos, BigDecimal importeTotal,
                                  BigDecimal aplicado, BigDecimal saldo,
                                  BigDecimal saldoVencido, String moneda) {
    public static ResumenEstadoCuenta vacio(String moneda) {
        return new ResumenEstadoCuenta(0, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, moneda);
    }
}
