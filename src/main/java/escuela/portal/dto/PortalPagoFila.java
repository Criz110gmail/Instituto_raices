package escuela.portal.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record PortalPagoFila(Long id, String folio, LocalDateTime fecha, BigDecimal monto,
                             String moneda, String estado, String referencia, int comprobantes) { }
