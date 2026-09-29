package escuela.docente.entity;

import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.Grupo;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @Entity @Table(name = "planeacion_semanal")
public class PlaneacionSemanal extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name="institucion_id", nullable=false) private Institucion institucion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name="maestro_id", nullable=false) private Maestro maestro;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name="grupo_id", nullable=false) private Grupo grupo;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name="ciclo_escolar_id", nullable=false) private CicloEscolar cicloEscolar;
    @Column(name="fecha_inicio", nullable=false) private LocalDate fechaInicio;
    @Column(name="fecha_fin", nullable=false) private LocalDate fechaFin;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=25) private EstadoPlaneacion estado = EstadoPlaneacion.BORRADOR;
    @Column(name="numero_revision", nullable=false) private int numeroRevision;
    @Column(name="situacion_didactica", columnDefinition="text") private String situacionDidactica;
    @Column(nullable=false, columnDefinition="text") private String proposito;
    @Column(name="ejes_articuladores", columnDefinition="text") private String ejesArticuladores;
    @Column(columnDefinition="text") private String conocimientos;
    @Column(columnDefinition="text") private String habilidades;
    @Column(columnDefinition="text") private String actitudes;
    @Column(name="tecnica_evaluacion", columnDefinition="text") private String tecnicaEvaluacion;
    @Column(name="instrumento_evaluacion", columnDefinition="text") private String instrumentoEvaluacion;
    @Column(columnDefinition="text") private String recursos;
    @Column(name="actividades_permanentes", columnDefinition="text") private String actividadesPermanentes;
    @Column(name="ajustes_razonables", columnDefinition="text") private String ajustesRazonables;
    @Column(columnDefinition="text") private String observaciones;
    @Column(name="enviada_en") private Instant enviadaEn;
    @Column(name="revision_iniciada_en") private Instant revisionIniciadaEn;
    @Column(name="publicada_en") private Instant publicadaEn;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="publicada_por_id") private Usuario publicadaPor;
    @OneToMany(mappedBy="planeacion", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("orden asc") private List<PlaneacionMateria> materias = new ArrayList<>();
    @OneToMany(mappedBy="planeacion", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("orden asc") private List<PlaneacionAlineacion> alineaciones = new ArrayList<>();
    @OneToMany(mappedBy="planeacion", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("fecha asc, orden asc") private List<PlaneacionActividad> actividades = new ArrayList<>();
}
