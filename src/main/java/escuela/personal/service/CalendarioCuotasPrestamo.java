package escuela.personal.service;
import escuela.common.exception.ReglaNegocioException;
import java.math.*;
import java.time.*;
import java.util.*;
/** Importes fijos, no tasas implícitas. El último vencimiento ajusta los centavos. */
public final class CalendarioCuotasPrestamo {
 private CalendarioCuotasPrestamo(){}
 public record Cuota(int numero,LocalDate fecha,BigDecimal capital,BigDecimal interes){public BigDecimal total(){return capital.add(interes);}}
 public static List<Cuota> calcular(BigDecimal capital,BigDecimal interes,int numero,LocalDate inicio,String frecuencia){
  if(capital==null||interes==null||inicio==null||numero<1||numero>600||capital.signum()<0||interes.signum()<0||capital.scale()>2||interes.scale()>2||!Set.of("MENSUAL","QUINCENAL","SEMANAL").contains(frecuencia))throw new ReglaNegocioException("Revisa capital, interés fijo total, número de cuotas y frecuencia.");
  BigDecimal cap=capital.divide(BigDecimal.valueOf(numero),2,RoundingMode.DOWN),intf=interes.divide(BigDecimal.valueOf(numero),2,RoundingMode.DOWN);var salida=new ArrayList<Cuota>();
  if(frecuencia.equals("QUINCENAL")&&inicio.getDayOfMonth()!=15&&inicio.getDayOfMonth()!=inicio.lengthOfMonth())throw new ReglaNegocioException("Para descuentos quincenales selecciona el día 15 o el último día del mes.");
  for(int i=0;i<numero;i++){int mitad=(inicio.getDayOfMonth()==15?0:1)+i;var mes=inicio.withDayOfMonth(1).plusMonths(mitad/2);LocalDate fecha=switch(frecuencia){case "SEMANAL"->inicio.plusWeeks(i);case "QUINCENAL"->mes.withDayOfMonth(mitad%2==0?15:mes.lengthOfMonth());default->inicio.plusMonths(i);};var c=new Cuota(i+1,fecha,i==numero-1?capital.subtract(cap.multiply(BigDecimal.valueOf(i))):cap,i==numero-1?interes.subtract(intf.multiply(BigDecimal.valueOf(i))):intf);if(c.total().signum()<=0)throw new ReglaNegocioException("El importe es demasiado pequeño para ese número de cuotas.");salida.add(c);}
  return List.copyOf(salida);
 }
}
