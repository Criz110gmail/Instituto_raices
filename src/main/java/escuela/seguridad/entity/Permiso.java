package escuela.seguridad.entity;

import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permiso")
public class Permiso extends EntidadAuditable {
    @Column(nullable = false, length = 80)
    private String codigo;

    @Column(nullable = false, length = 250)
    private String descripcion;
}
