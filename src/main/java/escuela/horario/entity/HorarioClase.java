package escuela.horario.entity;

import escuela.academico.entity.*;
import escuela.config.audit.EntidadAuditable;
import escuela.docente.entity.*;
import escuela.institucion.entity.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.*;

@Getter @Setter @Entity @Table(name="horario_clase")
public class HorarioClase extends EntidadAuditable {
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="institucion_id") private Institucion institucion;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="plantel_id") private Plantel plantel;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="ciclo_escolar_id") private CicloEscolar cicloEscolar;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="asignacion_maestro_id") private AsignacionMaestro asignacionMaestro;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="maestro_id") private Maestro maestro;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="grupo_id") private Grupo grupo;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="materia_id") private Materia materia;
    @Column(name="dia_semana",nullable=false) private short diaSemana;
    @Column(name="hora_inicio",nullable=false) private LocalTime horaInicio;
    @Column(name="hora_fin",nullable=false) private LocalTime horaFin;
    @Column(name="fecha_inicio",nullable=false) private LocalDate fechaInicio;
    @Column(name="fecha_fin",nullable=false) private LocalDate fechaFin;
    @Column(length=100) private String aula;
    @Column(length=1000) private String observaciones;
    @Column(nullable=false) private boolean activo=true;
}
