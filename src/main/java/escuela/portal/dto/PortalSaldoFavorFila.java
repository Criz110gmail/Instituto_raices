package escuela.portal.dto;
import java.math.BigDecimal;
public record PortalSaldoFavorFila(String folio,String plantel,String moneda,String fecha,
 BigDecimal recibido,BigDecimal aplicado,BigDecimal devuelto,BigDecimal disponible) { }
