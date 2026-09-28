package escuela.alumno.entity;

import escuela.archivo.entity.Archivo;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "alumno_documento")
public class AlumnoDocumento extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "archivo_id", nullable = false, unique = true)
    private Archivo archivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoDocumentoAlumno tipo;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "fecha_documento")
    private LocalDate fechaDocumento;

    @Column(name = "vigente_hasta")
    private LocalDate vigenteHasta;

    @Column(name = "retirado_en")
    private Instant retiradoEn;
}

