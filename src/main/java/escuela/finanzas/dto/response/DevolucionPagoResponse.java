package escuela.finanzas.dto.response;

import escuela.finanzas.entity.EstadoDevolucionPago;

import java.math.BigDecimal;
import java.time.Instant;

public record DevolucionPagoResponse(Long id, Long pagoId, Long cuentaOrigenId,
                                     String cuentaOrigenNombre, Instant fecha,
                                     BigDecimal monto, String moneda, String motivo,
                                     String beneficiario, String referencia,
                                     String autorizadoPor, EstadoDevolucionPago estado,
                                     MovimientoFinancieroResponse movimiento) { }
