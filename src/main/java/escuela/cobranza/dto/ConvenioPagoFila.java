package escuela.cobranza.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
public record ConvenioPagoFila(Long id,String folio,String tutor,LocalDate fechaAcuerdo,LocalDate fechaVencimiento,
 BigDecimal saldoOriginal,BigDecimal montoAcordado,BigDecimal condonado,String moneda,String estado){}
