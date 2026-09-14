package escuela.cobranza.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
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

@Getter
@Setter
@Entity
@Table(name = "concepto_cobro")
public class ConceptoCobro extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 2000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaConceptoCobro categoria;

    @Column(name = "permite_beca", nullable = false)
    private boolean permiteBeca;

    @Column(name = "permite_descuento", nullable = false)
    private boolean permiteDescuento;

    @Column(name = "permite_recargo", nullable = false)
    private boolean permiteRecargo;

    @Column(nullable = false)
    private boolean activo;
}
