package escuela.finanzas.dto.response;

import escuela.finanzas.entity.TipoReversionFinanciera;

import java.math.BigDecimal;
import java.time.Instant;

public record ObjetivoReversionResponse(
        TipoReversionFinanciera tipo, Long id, Long institucionId,
        String titulo, String detalle, BigDecimal monto, String moneda,
        Instant fechaOriginal, Long version, boolean revertido) { }
