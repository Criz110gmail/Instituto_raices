package escuela.tutor.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tutor")
public class Tutor extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String nombres;

    @Column(name = "primer_apellido", nullable = false, length = 100)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 100)
    private String segundoApellido;

    @Column(name = "telefono_principal", nullable = false, length = 30)
    private String telefonoPrincipal;

    @Column(name = "telefono_secundario", length = 30)
    private String telefonoSecundario;

    @Column(length = 254)
    private String email;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 150)
    private String calle;

    @Column(name = "numero_exterior", length = 30)
    private String numeroExterior;

    @Column(name = "numero_interior", length = 30)
    private String numeroInterior;

    @Column(length = 120)
    private String colonia;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 100)
    private String estado;

    @Column(name = "codigo_postal", length = 15)
    private String codigoPostal;

    @Column(length = 2)
    private String pais;

    @Column(length = 120)
    private String ocupacion;

    @Column(name = "lugar_trabajo", length = 180)
    private String lugarTrabajo;

    @Column(name = "telefono_trabajo", length = 30)
    private String telefonoTrabajo;

    @Column(nullable = false)
    private boolean activo = true;
}
