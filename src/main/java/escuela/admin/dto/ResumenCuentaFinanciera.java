package escuela.admin.dto;

import java.math.BigDecimal;

public record ResumenCuentaFinanciera(
        Long id, String etiqueta, String tipo, String alcance, String moneda,
        BigDecimal saldoActual, long movimientos
) {
}
