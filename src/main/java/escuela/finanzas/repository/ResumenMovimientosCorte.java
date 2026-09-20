package escuela.finanzas.repository;

import java.math.BigDecimal;

public interface ResumenMovimientosCorte {
    Long getCantidad();
    BigDecimal getIngresos();
    BigDecimal getEgresos();
}
