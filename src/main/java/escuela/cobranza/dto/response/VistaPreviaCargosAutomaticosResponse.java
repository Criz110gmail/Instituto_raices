package escuela.cobranza.dto.response;

import org.springframework.data.domain.Page;
import java.math.BigDecimal;

public record VistaPreviaCargosAutomaticosResponse(Page<VistaPreviaCargoAutomaticoFila> pagina,
        long cuotasConPendientes, long pagosPorGenerar, BigDecimal importeTotal) {
}
