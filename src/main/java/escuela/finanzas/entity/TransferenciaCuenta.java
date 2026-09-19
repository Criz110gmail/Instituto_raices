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
@Table(name = "transferencia_cuenta")
public class TransferenciaCuenta extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false) private Institucion institucion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_origen_id", nullable = false) private CuentaFinanciera cuentaOrigen;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_destino_id", nullable = false) private CuentaFinanciera cuentaDestino;
    @Column(nullable = false) private Instant fecha;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal monto;
    @Column(length = 150) private String referencia;
    @Column(columnDefinition = "text") private String observaciones;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autorizado_por_id", nullable = false) private Usuario autorizadoPor;
    @Column(name = "clave_idempotencia", nullable = false, length = 120) private String claveIdempotencia;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12) private EstadoTransferenciaCuenta estado = EstadoTransferenciaCuenta.APLICADA;
    @OneToOne(mappedBy = "transferenciaOrigen", fetch = FetchType.LAZY)
    private ReversionFinanciera reversion;
}
