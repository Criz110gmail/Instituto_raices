package escuela.finanzas.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public interface PagoCargoPendiente {
    Long getPagoId();
    String getFolio();
    Instant getFechaPago();
    BigDecimal getMontoSolicitado();
    String getMoneda();
    String getEstado();
    String getTutorNombre();
    String getReferencia();
    Integer getComprobantes();
    String getOrigenRegistro();
}
