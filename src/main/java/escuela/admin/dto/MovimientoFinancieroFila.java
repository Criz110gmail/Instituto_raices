package escuela.admin.dto;

import java.math.BigDecimal;

public record MovimientoFinancieroFila(
        Long id, String fecha, String cuenta, String plantel, String direccion, String clase,
        String concepto, String referencia, String tercero, BigDecimal monto,
        String moneda, BigDecimal saldoAnterior, BigDecimal saldoPosterior,
        String folioPago, Long secuenciaCuenta
) {
}
