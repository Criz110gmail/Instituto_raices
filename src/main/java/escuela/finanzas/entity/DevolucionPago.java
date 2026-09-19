package escuela.finanzas.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "devolucion_pago")
public class DevolucionPago extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false) private Institucion institucion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pago_id", nullable = false) private Pago pago;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_origen_id", nullable = false) private CuentaFinanciera cuentaOrigen;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal monto;
    @Column(nullable = false) private Instant fecha;
    @Column(nullable = false, length = 2000) private String motivo;
    @Column(nullable = false, length = 180) private String beneficiario;
    @Column(length = 150) private String referencia;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autorizado_por_id", nullable = false) private Usuario autorizadoPor;
    @Column(name = "clave_idempotencia", nullable = false, length = 120) private String claveIdempotencia;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12) private EstadoDevolucionPago estado = EstadoDevolucionPago.EJECUTADA;
    @OneToOne(mappedBy = "devolucionPago", fetch = FetchType.LAZY) private MovimientoFinanciero movimiento;
}
