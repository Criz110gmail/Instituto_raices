package escuela.docente.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name = "maestro")
public class Maestro extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false) private Institucion institucion;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "usuario_id") private Usuario usuario;
    @Column(name = "numero_empleado", nullable = false, length = 50) private String numeroEmpleado;
    @Column(nullable = false, length = 150) private String nombres;
    @Column(name = "primer_apellido", nullable = false, length = 100) private String primerApellido;
    @Column(name = "segundo_apellido", length = 100) private String segundoApellido;
    @Column(nullable = false, length = 254) private String email;
    @Column(length = 30) private String telefono;
    @Column(nullable = false) private boolean activo = true;
}
