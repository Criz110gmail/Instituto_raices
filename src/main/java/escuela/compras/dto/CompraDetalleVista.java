package escuela.compras.dto;

import escuela.compras.entity.EstadoCompra;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CompraDetalleVista(Long id,String folio,Long institucionId,String institucion,Long plantelId,String plantel,
 String proveedor,String proveedorRfc,String cuenta,String motivo,Instant fecha,String referencia,String observaciones,
 BigDecimal subtotal,BigDecimal impuesto,BigDecimal total,EstadoCompra estado,Long movimientoId,Long reversionId,
 Instant canceladaEn,String canceladaPor,String motivoCancelacion,Long version,List<Partida> partidas){
 public record Partida(String descripcion,BigDecimal cantidad,BigDecimal precioUnitario,BigDecimal porcentajeImpuesto,BigDecimal subtotal,BigDecimal impuesto,BigDecimal total){}
}
