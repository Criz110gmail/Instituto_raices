package escuela.compras.dto;

import escuela.compras.entity.EstadoCompra;
import java.math.BigDecimal;
import java.time.Instant;

public record CompraFila(Long id,String folio,Instant fecha,String proveedor,String rfc,String plantel,String cuenta,
                         String referencia,BigDecimal total,EstadoCompra estado,Long movimientoId,Long version){}
