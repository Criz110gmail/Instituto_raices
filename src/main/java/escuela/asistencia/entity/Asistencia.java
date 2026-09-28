package escuela.asistencia.entity;

import escuela.academico.entity.Grupo;
import escuela.config.audit.EntidadAuditable;
import escuela.inscripcion.entity.Inscripcion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @Entity
@Table(name = "asistencia")
public class Asistencia extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;
    @Column(nullable = false) private LocalDate fecha;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 15)
    private EstadoAsistencia estado;
    @Column(length = 1000) private String observaciones;
}
