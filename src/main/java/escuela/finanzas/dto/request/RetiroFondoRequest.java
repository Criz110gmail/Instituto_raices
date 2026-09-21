package escuela.finanzas.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RetiroFondoRequest(Long institucionId, Long cuentaId, Long plantelOperacionId,
                                 LocalDateTime fechaOperacion, Long motivoFinancieroId,
                                 BigDecimal monto, String beneficiario, String concepto,
                                 String referencia, String observaciones, String claveIdempotencia) { }
