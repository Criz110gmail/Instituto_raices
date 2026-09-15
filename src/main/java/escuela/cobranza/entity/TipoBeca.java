package escuela.cobranza.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name = "tipo_beca")
public class TipoBeca extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;
    @Column(nullable = false, length = 50) private String codigo;
    @Column(nullable = false, length = 150) private String nombre;
    @Column(length = 2000) private String descripcion;
    @Column(nullable = false) private boolean activo = true;
}
