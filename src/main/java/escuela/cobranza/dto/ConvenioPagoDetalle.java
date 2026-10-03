package escuela.cobranza.dto;
import escuela.cobranza.entity.EstadoConvenioPago;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public record ConvenioPagoDetalle(Long id,Long version,String folio,String institucion,String tutor,
 LocalDate fechaAcuerdo,LocalDate fechaVencimiento,String descripcion,String motivo,String condiciones,String moneda,
 BigDecimal saldoOriginal,BigDecimal montoAcordado,BigDecimal condonado,EstadoConvenioPago estado,String situacion,
 String motivoCancelacion,List<CargoConvenioOpcion> originales,List<CargoConvenioOpcion> nuevos){}
