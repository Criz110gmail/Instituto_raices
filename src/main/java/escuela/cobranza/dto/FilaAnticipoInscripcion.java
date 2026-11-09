package escuela.cobranza.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FilaAnticipoInscripcion(String clave,Long cuotaId,Long cuotaVersion,Long cargoId,Long cargoVersion,
 String concepto,LocalDate inicio,LocalDate fin,LocalDate vencimiento,BigDecimal importe,String moneda,String estado,boolean disponible) {}
