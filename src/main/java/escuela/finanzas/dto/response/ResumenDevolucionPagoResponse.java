package escuela.finanzas.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ResumenDevolucionPagoResponse(BigDecimal montoPago, BigDecimal montoAplicado,
                                            BigDecimal montoDevuelto, BigDecimal montoDisponible,
                                            List<AplicacionPagoResponse> aplicacionesActivas,
                                            List<DevolucionPagoResponse> devoluciones) { }
