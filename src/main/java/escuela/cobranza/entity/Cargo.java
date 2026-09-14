package escuela.cobranza.entity;

import escuela.academico.entity.PeriodoAcademico;
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
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "cargo")
public class Cargo extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concepto_cobro_id", nullable = false)
    private ConceptoCobro conceptoCobro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuota_alumno_id")
    private CuotaAlumno cuotaAlumno;

    @Column(name = "clave_generacion", nullable = false, length = 180, unique = true)
    private String claveGeneracion;

    @Column(nullable = false, length = 250)
    private String descripcion;

    @Column(name = "periodo_cobro_inicio", nullable = false)
    private LocalDate periodoCobroInicio;

    @Column(name = "periodo_cobro_fin", nullable = false)
    private LocalDate periodoCobroFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_academico_id")
    private PeriodoAcademico periodoAcademico;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "importe_original", nullable = false, precision = 14, scale = 2)
    private BigDecimal importeOriginal;

    @Column(nullable = false, length = 3)
    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_registro", nullable = false, length = 12)
    private EstadoRegistroCargo estadoRegistro = EstadoRegistroCargo.EMITIDO;

    @Column(name = "cancelado_en")
    private Instant canceladoEn;

    @Column(name = "motivo_cancelacion", length = 2000)
    private String motivoCancelacion;
}
