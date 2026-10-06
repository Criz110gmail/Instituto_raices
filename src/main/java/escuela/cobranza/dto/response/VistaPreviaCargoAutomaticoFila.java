package escuela.cobranza.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VistaPreviaCargoAutomaticoFila(Long cuotaId, String alumnoMatricula,
        String alumnoNombre, String plantelNombre, String concepto, String frecuencia,
        String periodo, LocalDate fechaVencimiento, BigDecimal importe, String moneda,
        BigDecimal montoBeca, String descripcionBeca, BigDecimal importeNeto, String claveSeleccion) {
}
