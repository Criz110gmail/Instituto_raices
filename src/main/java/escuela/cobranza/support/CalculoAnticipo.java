package escuela.cobranza.support;
import escuela.common.exception.ReglaNegocioException;
import java.math.*;
import java.util.*;
public final class CalculoAnticipo {
 private CalculoAnticipo(){}
 public static List<BigDecimal> beneficios(List<BigDecimal> bases,String tipo,BigDecimal valor,int mes) {
  BigDecimal total=bases.stream().reduce(BigDecimal.ZERO,BigDecimal::add);
  if(bases.isEmpty()||bases.stream().anyMatch(b->b.signum()<=0))throw new ReglaNegocioException("Selecciona mensualidades con saldo positivo");
  BigDecimal descuento;
  if("MENSUALIDAD".equals(tipo)) {
   if(mes<0||mes>=bases.size())throw new ReglaNegocioException("Selecciona qué mensualidad será bonificada");
   descuento=bases.get(mes);
  } else {
   if(valor==null||valor.signum()<=0)throw new ReglaNegocioException("Captura un beneficio mayor que cero");
   if("PORCENTAJE".equals(tipo)) {
    if(valor.compareTo(new BigDecimal("100"))>0)throw new ReglaNegocioException("El porcentaje no puede superar 100");
    descuento=total.multiply(valor).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP);
   }else if("MONTO".equals(tipo)) {
    try{descuento=valor.setScale(2,RoundingMode.UNNECESSARY);}catch(ArithmeticException e){throw new ReglaNegocioException("La cantidad fija admite hasta dos decimales");}
   }else throw new ReglaNegocioException("Selecciona un tipo de beneficio válido");
  }
  if(descuento.signum()<=0||descuento.compareTo(total)>=0)throw new ReglaNegocioException("El beneficio debe ser positivo y dejar al menos $0.01 por pagar");
  List<BigDecimal> partes=new ArrayList<>(); BigDecimal asignado=BigDecimal.ZERO;
  for(int i=0;i<bases.size();i++) {
   BigDecimal parte="MENSUALIDAD".equals(tipo)?(i==mes?descuento:new BigDecimal("0.00")):descuento.multiply(bases.get(i)).divide(total,2,RoundingMode.DOWN);
   partes.add(parte);asignado=asignado.add(parte);
  }
  BigDecimal restante=descuento.subtract(asignado);
  for(int i=bases.size()-1;restante.signum()>0&&i>=0;i--){BigDecimal extra=bases.get(i).subtract(partes.get(i)).min(restante);partes.set(i,partes.get(i).add(extra));restante=restante.subtract(extra);}
  return List.copyOf(partes);
 }
}
