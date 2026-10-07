package escuela.portal.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record PortalPagoFila(Long id, String folio, LocalDateTime fecha, BigDecimal monto,
                             String moneda, String metodo, String estado, String referencia,
                             int comprobantes, String motivoRechazo) {
    public PortalPagoFila(Long id,String folio,LocalDateTime fecha,BigDecimal monto,String moneda,
                          String metodo,String estado,String referencia,int comprobantes) {
        this(id,folio,fecha,monto,moneda,metodo,estado,referencia,comprobantes,null);
    }
}
