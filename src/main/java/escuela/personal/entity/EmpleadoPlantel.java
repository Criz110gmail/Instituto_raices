package escuela.personal.entity;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Plantel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Getter @Setter @Entity @Table(name="empleado_plantel")
public class EmpleadoPlantel extends EntidadAuditable {
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="empleado_id",nullable=false,updatable=false) private Empleado empleado;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="plantel_id",nullable=false,updatable=false) private Plantel plantel;
 @Column(name="fecha_inicio",nullable=false,updatable=false) private LocalDate fechaInicio;
 @Column(name="fecha_fin") private LocalDate fechaFin;
 @Column(nullable=false,updatable=false) private boolean principal;
}
