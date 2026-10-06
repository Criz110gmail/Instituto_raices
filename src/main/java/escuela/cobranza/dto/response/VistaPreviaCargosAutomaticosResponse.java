package escuela.cobranza.dto.response;

import org.springframework.data.domain.Page;
import java.math.BigDecimal;

public record VistaPreviaCargosAutomaticosResponse(Page<VistaPreviaCargoAutomaticoFila> pagina,
        long cuotasConPendientes, long pagosPorGenerar, BigDecimal importeTotal,
        BigDecimal becaTotal, BigDecimal importeNetoTotal, java.util.UUID seleccionId) {
    public VistaPreviaCargosAutomaticosResponse(Page<VistaPreviaCargoAutomaticoFila> pagina,long cuotas,long pagos,BigDecimal total,BigDecimal beca,BigDecimal neto){this(pagina,cuotas,pagos,total,beca,neto,null);}
}
