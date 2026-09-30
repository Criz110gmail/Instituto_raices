package escuela.calendario.entity;

import escuela.academico.entity.*;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.*;

@Getter @Setter @Entity @Table(name="calendario_escolar_detalle")
public class CalendarioEscolarDetalle extends EntidadAuditable {
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="institucion_id") private Institucion institucion;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="ciclo_escolar_id") private CicloEscolar cicloEscolar;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="plantel_id") private Plantel plantel;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="nivel_educativo_id") private NivelEducativo nivelEducativo;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private TipoFechaCalendario tipo;
    @Column(nullable=false,length=200) private String titulo;
    @Column(name="fecha_inicio",nullable=false) private LocalDate fechaInicio;
    @Column(name="fecha_fin",nullable=false) private LocalDate fechaFin;
    @Column(name="hora_inicio") private LocalTime horaInicio;
    @Column(name="hora_fin") private LocalTime horaFin;
    @Column(name="suspende_clases",nullable=false) private boolean suspendeClases;
    @Column(length=3000) private String descripcion;
    @Column(nullable=false) private boolean activo=true;
}
