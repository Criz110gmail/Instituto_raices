package escuela.finanzas.entity;

import escuela.cobranza.entity.Cargo;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "aplicacion_pago")
public class AplicacionPago extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pago_id", nullable = false) private Pago pago;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cargo_id", nullable = false) private Cargo cargo;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id") private SolicitudAplicacionPago solicitud;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal monto;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10) private OperacionAplicacionPago operacion;
    @Column(name = "fecha_aplicacion", nullable = false) private Instant fechaAplicacion;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversa_de_id") private AplicacionPago reversaDe;
    @OneToOne(mappedBy = "reversaDe", fetch = FetchType.LAZY) private AplicacionPago reversa;
    @Column(length = 2000) private String motivo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_pago_id") private DevolucionPago devolucionPago;
}
