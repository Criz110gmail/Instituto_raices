package escuela.finanzas.dto.response;

import escuela.finanzas.entity.TipoReversionFinanciera;

import java.time.Instant;
import java.util.List;

public record ReversionFinancieraResponse(
        Long id, TipoReversionFinanciera tipo, Long objetivoId,
        Instant fecha, String motivo, String autorizadoPor,
        List<MovimientoFinancieroResponse> movimientos) { }
