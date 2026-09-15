package escuela.finanzas.entity;

import escuela.cobranza.entity.Cargo;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "solicitud_aplicacion_pago")
public class SolicitudAplicacionPago extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pago_id", nullable = false)
    private Pago pago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cargo_id", nullable = false)
    private Cargo cargo;

    @Column(name = "monto_solicitado", nullable = false, precision = 19, scale = 2)
    private BigDecimal montoSolicitado;
}
