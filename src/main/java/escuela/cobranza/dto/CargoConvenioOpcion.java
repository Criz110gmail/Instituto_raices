package escuela.cobranza.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
public record CargoConvenioOpcion(Long id,Long inscripcionId,String alumno,String matricula,String concepto,
 String descripcion,LocalDate vencimiento,BigDecimal total,BigDecimal pagado,BigDecimal saldo,String moneda){}
