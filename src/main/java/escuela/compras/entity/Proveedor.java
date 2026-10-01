package escuela.compras.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="proveedor")
public class Proveedor extends EntidadAuditable {
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="institucion_id",nullable=false) private Institucion institucion;
    @Column(name="razon_social",nullable=false,length=180) private String razonSocial;
    @Column(name="nombre_comercial",length=180) private String nombreComercial;
    @Column(length=13) private String rfc;
    @Column(name="contacto_nombre",length=180) private String contactoNombre;
    @Column(length=30) private String telefono;
    @Column(length=254) private String correo;
    @Column(length=500) private String direccion;
    @Column(length=2000) private String notas;
    @Column(nullable=false) private boolean activo=true;
}
