package escuela.personal.entity;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.archivo.entity.Archivo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter @Entity @Table(name="empleado")
public class Empleado extends EntidadAuditable {
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="institucion_id",nullable=false) private Institucion institucion;
 @Column(nullable=false,length=50) private String codigo;
 @Column(nullable=false,length=150) private String nombres;
 @Column(name="primer_apellido",nullable=false,length=100) private String primerApellido;
 @Column(name="segundo_apellido",length=100) private String segundoApellido;
 @Column(length=254) private String email;
 @Column(length=30) private String telefono;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="fotografia_archivo_id") private Archivo fotografiaArchivo;
 @Column(name="maestro_origen_id",updatable=false) private Long maestroOrigenId;
 @Column(nullable=false) private boolean activo=true;
 public String nombre(){return java.util.stream.Stream.of(nombres,primerApellido,segundoApellido).filter(x->x!=null&&!x.isBlank()).collect(java.util.stream.Collectors.joining(" "));}
}
