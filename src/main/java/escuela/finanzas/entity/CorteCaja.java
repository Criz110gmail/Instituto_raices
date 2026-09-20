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
@Table(name = "corte_caja")
public class CorteCaja extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false) private Institucion institucion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false) private CuentaFinanciera cuenta;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10) private EstadoCorteCaja estado = EstadoCorteCaja.ABIERTO;
    @Column(name = "abierto_en", nullable = false) private Instant abiertoEn;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "abierto_por_id", nullable = false) private Usuario abiertoPor;
    @Column(name = "secuencia_inicial", nullable = false) private Long secuenciaInicial;
    @Column(name = "saldo_inicial_sistema", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoInicialSistema;
    @Column(name = "observaciones_apertura", length = 2000) private String observacionesApertura;
    @Column(name = "clave_apertura", nullable = false, length = 120) private String claveApertura;
    @Column(name = "cerrado_en") private Instant cerradoEn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cerrado_por_id") private Usuario cerradoPor;
    @Column(name = "secuencia_final") private Long secuenciaFinal;
    @Column(name = "movimientos_contabilizados") private Long movimientosContabilizados;
    @Column(name = "total_ingresos", precision = 19, scale = 2) private BigDecimal totalIngresos;
    @Column(name = "total_egresos", precision = 19, scale = 2) private BigDecimal totalEgresos;
    @Column(name = "saldo_esperado", precision = 19, scale = 2) private BigDecimal saldoEsperado;
    @Column(name = "efectivo_declarado", precision = 19, scale = 2) private BigDecimal efectivoDeclarado;
    @Column(precision = 19, scale = 2) private BigDecimal diferencia;
    @Column(name = "justificacion_diferencia", length = 2000) private String justificacionDiferencia;
    @Column(name = "observaciones_cierre", length = 2000) private String observacionesCierre;
    @Column(name = "clave_cierre", length = 120) private String claveCierre;
}
