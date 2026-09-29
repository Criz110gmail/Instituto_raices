package escuela.docente.entity;

import escuela.academico.entity.Grupo;
import escuela.academico.entity.Materia;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @Entity @Table(name = "asignacion_maestro")
public class AsignacionMaestro extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maestro_id", nullable = false) private Maestro maestro;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false) private Grupo grupo;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_id", nullable = false) private Materia materia;
    @Column(name = "fecha_inicio", nullable = false) private LocalDate fechaInicio;
    @Column(name = "fecha_fin") private LocalDate fechaFin;
    @Column(nullable = false) private boolean activo = true;
}
