package escuela.cobranza.dto.response;

import org.springframework.data.domain.Page;
import java.math.BigDecimal;

public record VistaPreviaRecargosResponse(Page<VistaPreviaRecargoFila> pagina,
        long cargosAplicables, long recargosNuevos, BigDecimal saldoActual,
        BigDecimal totalRecargos, BigDecimal nuevoSaldo, java.util.UUID seleccionId) {
    public VistaPreviaRecargosResponse(Page<VistaPreviaRecargoFila> pagina,long cargos,long recargos,BigDecimal saldo,BigDecimal total,BigDecimal nuevo){this(pagina,cargos,recargos,saldo,total,nuevo,null);}
}
