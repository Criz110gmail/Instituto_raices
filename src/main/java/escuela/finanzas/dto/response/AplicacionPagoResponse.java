package escuela.finanzas.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record AplicacionPagoResponse(
        Long id, Long cargoId, String alumno, String matricula,
        String concepto, String descripcionCargo, BigDecimal monto,
        String moneda, Instant fechaAplicacion
) { }
