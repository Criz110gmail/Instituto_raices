package escuela.docente.entity;

import escuela.academico.entity.Materia;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="planeacion_materia")
public class PlaneacionMateria extends EntidadAuditable {
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="planeacion_id", nullable=false) private PlaneacionSemanal planeacion;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="materia_id", nullable=false) private Materia materia;
    @Column(nullable=false) private int orden;
}
