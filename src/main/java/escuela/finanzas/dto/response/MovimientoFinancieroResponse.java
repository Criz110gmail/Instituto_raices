package escuela.finanzas.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record MovimientoFinancieroResponse(
        Long id, Long cuentaId, String cuentaNombre, Instant fechaOperacion,
        Long secuenciaCuenta, BigDecimal monto, String moneda,
        BigDecimal saldoAnterior, BigDecimal saldoPosterior
) { }
