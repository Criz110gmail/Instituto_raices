package escuela.finanzas.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "retiro_fondo")
public class RetiroFondo extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false) private Institucion institucion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false) private CuentaFinanciera cuenta;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantel_operacion_id") private Plantel plantelOperacion;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movimiento_id", nullable = false, unique = true) private MovimientoFinanciero movimiento;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autorizado_por_id", nullable = false) private Usuario autorizadoPor;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "motivo_financiero_id", nullable = false) private MotivoFinanciero motivoFinanciero;
    @Column(name = "fecha_operacion", nullable = false) private Instant fechaOperacion;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal monto;
    @Column(nullable = false, length = 180) private String beneficiario;
    @Column(nullable = false, length = 250) private String concepto;
    @Column(nullable = false, length = 150) private String referencia;
    @Column(length = 500) private String observaciones;
    @Column(name = "clave_idempotencia", nullable = false, length = 100) private String claveIdempotencia;
}
