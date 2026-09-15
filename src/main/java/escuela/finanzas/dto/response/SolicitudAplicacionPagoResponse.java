package escuela.finanzas.dto.response;

import java.math.BigDecimal;

public record SolicitudAplicacionPagoResponse(Long id, Long cargoId, String alumno,
                                              String matricula, String concepto,
                                              String descripcionCargo,
                                              BigDecimal montoSolicitado, String moneda) {
}
