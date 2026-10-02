package escuela.cobranza.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VistaPreviaRecargoFila(Long cargoId, String alumnoMatricula, String alumnoNombre,
        String plantelNombre, String concepto, LocalDate fechaVencimiento, long diasAtraso,
        String politica, int periodosNuevos, BigDecimal saldoActual, BigDecimal recargoNuevo,
        BigDecimal nuevoSaldo) {
}
