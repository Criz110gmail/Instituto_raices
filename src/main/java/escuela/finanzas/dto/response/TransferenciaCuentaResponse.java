package escuela.finanzas.dto.response;

import escuela.finanzas.entity.EstadoTransferenciaCuenta;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferenciaCuentaResponse(
        Long id, Long institucionId,
        Long cuentaOrigenId, String cuentaOrigenNombre,
        Long cuentaDestinoId, String cuentaDestinoNombre,
        Instant fecha, BigDecimal monto, String moneda,
        String referencia, EstadoTransferenciaCuenta estado,
        MovimientoFinancieroResponse salida, MovimientoFinancieroResponse entrada) { }
