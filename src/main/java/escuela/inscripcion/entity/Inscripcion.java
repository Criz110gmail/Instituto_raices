package escuela.inscripcion.entity;

import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.Grado;
import escuela.alumno.entity.Alumno;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Plantel;
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

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "inscripcion")
public class Inscripcion extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plantel_id", nullable = false)
    private Plantel plantel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ciclo_escolar_id", nullable = false)
    private CicloEscolar cicloEscolar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grado_id", nullable = false)
    private Grado grado;

    @Column(name = "numero_inscripcion", nullable = false, length = 60)
    private String numeroInscripcion;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInscripcion estado;

    @Column(name = "motivo_baja_cancelacion", length = 2000)
    private String motivoBajaCancelacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_anterior_id")
    private Inscripcion inscripcionAnterior;

    @Column(length = 4000)
    private String observaciones;
}
