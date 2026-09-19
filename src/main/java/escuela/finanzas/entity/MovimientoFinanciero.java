package escuela.finanzas.entity;

import escuela.archivo.entity.Archivo;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "movimiento_financiero")
public class MovimientoFinanciero extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false) private Institucion institucion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false) private CuentaFinanciera cuenta;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantel_operacion_id") private Plantel plantelOperacion;
    @Column(name = "fecha_operacion", nullable = false) private Instant fechaOperacion;
    @Column(name = "secuencia_cuenta", nullable = false) private Long secuenciaCuenta;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10) private DireccionMovimiento direccion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15) private ClaseMovimiento clase;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal monto;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "motivo_financiero_id", nullable = false) private MotivoFinanciero motivoFinanciero;
    @Column(nullable = false, length = 250) private String concepto;
    @Column(length = 150) private String referencia;
    @Column(name = "tercero_nombre", length = 180) private String terceroNombre;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_archivo_id") private Archivo comprobanteArchivo;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id") private Pago pago;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferencia_id") private TransferenciaCuenta transferencia;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_pago_id") private DevolucionPago devolucionPago;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversa_de_id") private MovimientoFinanciero reversaDe;
    @Column(name = "clave_idempotencia", nullable = false, length = 120) private String claveIdempotencia;
    @Column(name = "saldo_anterior", nullable = false, precision = 19, scale = 2) private BigDecimal saldoAnterior;
    @Column(name = "saldo_posterior", nullable = false, precision = 19, scale = 2) private BigDecimal saldoPosterior;
}
