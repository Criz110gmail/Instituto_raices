package escuela.alumno.entity;

import escuela.archivo.entity.Archivo;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "alumno")
public class Alumno extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;

    @Column(nullable = false, length = 50)
    private String matricula;

    @Column(nullable = false, length = 150)
    private String nombres;

    @Column(name = "primer_apellido", nullable = false, length = 100)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 100)
    private String segundoApellido;

    @Column(length = 18)
    private String curp;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 30)
    private String sexo;

    @Column(name = "lugar_nacimiento", length = 150)
    private String lugarNacimiento;

    @Column(length = 80)
    private String nacionalidad;

    @Column(length = 30)
    private String telefono;

    @Column(length = 254)
    private String email;

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

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fotografia_archivo_id")
    private Archivo fotografiaArchivo;

    @Column(columnDefinition = "text")
    private String observaciones;

    @Column(nullable = false)
    private boolean activo = true;
}
