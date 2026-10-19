package escuela.docente.entity;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import escuela.archivo.entity.Archivo;
import escuela.personal.entity.Empleado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter @Entity @Table(name="maestro")
public class Maestro extends EntidadAuditable {
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="institucion_id",nullable=false) private Institucion institucion;
 @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="usuario_id") private Usuario usuario;
 @OneToOne(fetch=FetchType.LAZY,optional=false,cascade=CascadeType.PERSIST) @JoinColumn(name="empleado_id",nullable=false) private Empleado empleado=new Empleado();
 @Column(name="numero_empleado",nullable=false,length=50) private String numeroEmpleado;
 @Column(nullable=false) private boolean activo=true;
 public void setInstitucion(Institucion i){institucion=i;if(empleado!=null&&empleado.getInstitucion()==null)empleado.setInstitucion(i);}
 public void setNumeroEmpleado(String n){numeroEmpleado=n;if(empleado!=null&&empleado.getCodigo()==null)empleado.setCodigo(n);}
 // Adaptadores de lectura académicos; la persistencia personal existe sólo en Empleado.
 public String getNombres(){return empleado.getNombres();} public void setNombres(String v){empleado.setNombres(v);}
 public String getPrimerApellido(){return empleado.getPrimerApellido();} public void setPrimerApellido(String v){empleado.setPrimerApellido(v);}
 public String getSegundoApellido(){return empleado.getSegundoApellido();} public void setSegundoApellido(String v){empleado.setSegundoApellido(v);}
 public String getEmail(){return empleado.getEmail();} public void setEmail(String v){empleado.setEmail(v);}
 public String getTelefono(){return empleado.getTelefono();} public void setTelefono(String v){empleado.setTelefono(v);}
 public Archivo getFotografiaArchivo(){return empleado.getFotografiaArchivo();} public void setFotografiaArchivo(Archivo v){empleado.setFotografiaArchivo(v);}
}
