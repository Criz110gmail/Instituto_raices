package escuela.personal.entity;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Getter @Setter @Entity @Table(name="empleado_laboral")
public class EmpleadoLaboral extends EntidadAuditable {
 @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="empleado_id",nullable=false,updatable=false) private Empleado empleado;
 @Column(length=150) private String puesto;
 @Column(name="fecha_ingreso") private LocalDate fechaIngreso;
 @Column(nullable=false,length=20) private String situacion="POR_CONFIRMAR";
 @Column(name="fecha_baja") private LocalDate fechaBaja;
 @Column(name="motivo_baja",length=2000) private String motivoBaja;
}
