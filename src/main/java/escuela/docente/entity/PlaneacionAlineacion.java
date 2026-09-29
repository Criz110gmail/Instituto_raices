package escuela.docente.entity;

import escuela.academico.entity.Materia;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="planeacion_alineacion")
public class PlaneacionAlineacion extends EntidadAuditable {
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="planeacion_id", nullable=false) private PlaneacionSemanal planeacion;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="materia_id") private Materia materia;
    @Column(name="campo_formativo", nullable=false, length=250) private String campoFormativo;
    @Column(nullable=false, columnDefinition="text") private String contenido;
    @Column(name="proceso_desarrollo", nullable=false, columnDefinition="text") private String procesoDesarrollo;
    @Column(nullable=false) private int orden;
}
