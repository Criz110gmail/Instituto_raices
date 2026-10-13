package escuela.finanzas.dto.response;
import java.math.BigDecimal;
public record CoberturaAnualPagoResponse(Long pagoId,Long alumnoId,String alumno,String matricula,
 String concepto,Integer anio,Integer meses,Integer mesesPagados,Integer mesesBonificados,
 BigDecimal beneficio,BigDecimal aplicado,String moneda,String estado) {
 public String estadoTexto(){return switch(estado){case "LIQUIDADO"->"Anual liquidado";case "PENDIENTE"->"Anual pendiente de validación";case "NO_VIGENTE"->"Anual cancelado o rechazado";default->"Anual no liquidado por este pago";};}
 public String resumen(){return alumno+" · "+matricula+" · "+concepto+" · Año completo "+anio+" · 12/12 meses · "+estadoTexto();}
}
