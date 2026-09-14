package escuela.cobranza.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.inscripcion.entity.Inscripcion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "cuota_alumno")
public class CuotaAlumno extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concepto_cobro_id", nullable = false)
    private ConceptoCobro conceptoCobro;

    @Column(name = "importe_base", nullable = false, precision = 14, scale = 2)
    private BigDecimal importeBase;

    @Column(nullable = false, length = 3)
    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private FrecuenciaCuota frecuencia;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "dia_vencimiento")
    private Integer diaVencimiento;

    @Column(name = "fecha_vencimiento_unico")
    private LocalDate fechaVencimientoUnico;

    @Column(name = "generacion_automatica", nullable = false)
    private boolean generacionAutomatica;

    @Column(name = "motivo_importe_personalizado", length = 2000)
    private String motivoImportePersonalizado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoCuota estado;
}
