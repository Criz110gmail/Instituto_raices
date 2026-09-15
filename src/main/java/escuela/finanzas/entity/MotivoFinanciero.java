package escuela.finanzas.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "motivo_financiero")
public class MotivoFinanciero extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;
    @Column(nullable = false, length = 50) private String codigo;
    @Column(nullable = false, length = 150) private String nombre;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10) private NaturalezaMotivoFinanciero naturaleza;
    @Column(nullable = false, length = 100) private String categoria;
    @Column(nullable = false) private boolean activo = true;
}
