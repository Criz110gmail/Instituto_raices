package escuela.academico.entity;

import escuela.config.audit.EntidadAuditable;
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

@Getter
@Setter
@Entity
@Table(name = "materia_grado")
public class MateriaGrado extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grado_id", nullable = false)
    private Grado grado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evaluacion", nullable = false, length = 15)
    private TipoEvaluacion tipoEvaluacion;

    @Column(name = "escala_minima", precision = 7, scale = 2) private BigDecimal escalaMinima;
    @Column(name = "escala_maxima", precision = 7, scale = 2) private BigDecimal escalaMaxima;
    @Column(name = "minima_aprobatoria", precision = 7, scale = 2) private BigDecimal minimaAprobatoria;
    @Column(nullable = false) private int decimales;
    @Column(nullable = false) private int orden;
    @Column(name = "horas_semanales", precision = 5, scale = 2) private BigDecimal horasSemanales;
    @Column(name = "incluir_boleta", nullable = false) private boolean incluirBoleta = true;
    @Column(nullable = false) private boolean activo = true;
}

