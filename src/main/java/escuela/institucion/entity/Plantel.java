package escuela.institucion.entity;

import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "plantel")
public class Plantel extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 30)
    private String telefono;

    @Column(length = 254)
    private String email;

    @Column(length = 200)
    private String calle;

    @Column(name = "numero_exterior", length = 30)
    private String numeroExterior;

    @Column(name = "numero_interior", length = 30)
    private String numeroInterior;

    @Column(length = 150)
    private String colonia;

    @Column(length = 120)
    private String ciudad;

    @Column(length = 120)
    private String estado;

    @Column(name = "codigo_postal", length = 12)
    private String codigoPostal;

    @Column(length = 2)
    private String pais;

    @Column(nullable = false)
    private boolean activo;
}
