package escuela.finanzas.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record AplicacionPagoResponse(
        Long id, Long cargoId, String alumno, String matricula,
        String concepto, String descripcionCargo, BigDecimal monto,
        String moneda, Instant fechaAplicacion, BigDecimal totalCargo, BigDecimal saldoCargoActual
) {
    public AplicacionPagoResponse(Long id, Long cargoId, String alumno, String matricula,
                                  String concepto, String descripcionCargo, BigDecimal monto,
                                  String moneda, Instant fechaAplicacion) {
        this(id, cargoId, alumno, matricula, concepto, descripcionCargo, monto,
                moneda, fechaAplicacion, null, null);
    }
}
