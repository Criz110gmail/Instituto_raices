package escuela.cobranza.dto.response;

import org.springframework.data.domain.Page;
import java.math.BigDecimal;

public record VistaPreviaRecargosResponse(Page<VistaPreviaRecargoFila> pagina,
        long cargosAplicables, long recargosNuevos, BigDecimal saldoActual,
        BigDecimal totalRecargos, BigDecimal nuevoSaldo) {
}
