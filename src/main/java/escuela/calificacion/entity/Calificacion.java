package escuela.calificacion.entity;

import escuela.academico.entity.MateriaGrado;
import escuela.academico.entity.PeriodoAcademico;
import escuela.academico.entity.TipoEvaluacion;
import escuela.config.audit.EntidadAuditable;
import escuela.inscripcion.entity.Inscripcion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "calificacion")
public class Calificacion extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_grado_id", nullable = false)
    private MateriaGrado materiaGrado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evaluacion", nullable = false, length = 15)
    private TipoEvaluacion tipoEvaluacion;

    @Column(name = "escala_minima", precision = 7, scale = 2)
    private BigDecimal escalaMinima;
    @Column(name = "escala_maxima", precision = 7, scale = 2)
    private BigDecimal escalaMaxima;
    @Column(name = "minima_aprobatoria", precision = 7, scale = 2)
    private BigDecimal minimaAprobatoria;
    @Column(nullable = false)
    private int decimales;
    @Column(name = "valor_numerico", precision = 7, scale = 2)
    private BigDecimal valorNumerico;
    @Column(name = "valor_cualitativo", length = 100)
    private String valorCualitativo;
    @Column(length = 1000)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoCalificacion estado = EstadoCalificacion.BORRADOR;
    @Column(name = "publicado_en")
    private Instant publicadoEn;
    @Column(name = "publicado_por_id")
    private Long publicadoPorId;
}
