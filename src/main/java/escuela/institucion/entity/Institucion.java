package escuela.institucion.entity;

import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "institucion")
public class Institucion extends EntidadAuditable {

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;

    @Column(name = "razon_social", length = 250)
    private String razonSocial;

    @Column(length = 13)
    private String rfc;

    @Column(length = 254)
    private String email;

    @Column(length = 30)
    private String telefono;

    @Column(name = "sitio_web", length = 300)
    private String sitioWeb;

    @Column(name = "domicilio_fiscal", columnDefinition = "text")
    private String domicilioFiscal;

    @Column(length = 120)
    private String ciudad;

    @Column(length = 120)
    private String estado;

    @Column(name = "codigo_postal", length = 12)
    private String codigoPostal;

    @Column(length = 2)
    private String pais;

    @Column(name = "logo_archivo_id")
    private Long logoArchivoId;

    @Column(name = "zona_horaria", nullable = false, length = 60)
    private String zonaHoraria;

    @Column(name = "moneda_predeterminada", nullable = false, length = 3)
    private String monedaPredeterminada;

    @Column(nullable = false)
    private boolean activo;
}
