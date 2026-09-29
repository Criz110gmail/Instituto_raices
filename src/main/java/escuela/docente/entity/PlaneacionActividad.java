package escuela.docente.entity;

import escuela.academico.entity.Materia;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @Entity @Table(name="planeacion_actividad")
public class PlaneacionActividad extends EntidadAuditable {
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="planeacion_id", nullable=false) private PlaneacionSemanal planeacion;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="materia_id", nullable=false) private Materia materia;
    @Column(nullable=false) private LocalDate fecha;
    @Column(nullable=false, length=250) private String titulo;
    @Column(nullable=false, columnDefinition="text") private String inicio;
    @Column(nullable=false, columnDefinition="text") private String desarrollo;
    @Column(nullable=false, columnDefinition="text") private String cierre;
    @Column(name="duracion_minutos") private Integer duracionMinutos;
    @Column(columnDefinition="text") private String tarea;
    @Column(columnDefinition="text") private String observaciones;
    @Column(nullable=false) private int orden;
}
