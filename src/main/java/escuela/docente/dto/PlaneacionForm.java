package escuela.docente.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.*;

@Getter @Setter
public class PlaneacionForm {
    @NotNull private Long grupoId;
    private String grupoTexto;
    @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaInicio;
    @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaFin;
    @NotEmpty private List<Long> materiaIds = new ArrayList<>();
    @Size(max=12000) private String situacionDidactica;
    @NotBlank @Size(max=20000) private String proposito;
    @Size(max=12000) private String ejesArticuladores;
    @Size(max=12000) private String conocimientos;
    @Size(max=12000) private String habilidades;
    @Size(max=12000) private String actitudes;
    @Size(max=12000) private String tecnicaEvaluacion;
    @Size(max=12000) private String instrumentoEvaluacion;
    @Size(max=12000) private String recursos;
    @Size(max=12000) private String actividadesPermanentes;
    @Size(max=12000) private String ajustesRazonables;
    @Size(max=12000) private String observaciones;
    @Valid private List<AlineacionForm> alineaciones = new ArrayList<>();
    @NotEmpty @Valid private List<ActividadForm> actividades = new ArrayList<>();
    private Long version;

    public PlaneacionRequest request(){return new PlaneacionRequest(grupoId,fechaInicio,fechaFin,materiaIds,situacionDidactica,proposito,ejesArticuladores,conocimientos,habilidades,actitudes,tecnicaEvaluacion,instrumentoEvaluacion,recursos,actividadesPermanentes,ajustesRazonables,observaciones,alineaciones.stream().map(AlineacionForm::request).toList(),actividades.stream().map(ActividadForm::request).toList(),version);}
    public static PlaneacionForm desde(PlaneacionDocumento d,Long version){
        var f=new PlaneacionForm();f.grupoId=d.grupoId();f.grupoTexto=d.plantel()+" · "+d.grado()+" · "+d.grupo();f.fechaInicio=d.fechaInicio();f.fechaFin=d.fechaFin();f.materiaIds=new ArrayList<>(d.materias().stream().map(PlaneacionDocumento.Materia::id).toList());f.situacionDidactica=d.situacionDidactica();f.proposito=d.proposito();f.ejesArticuladores=d.ejesArticuladores();f.conocimientos=d.conocimientos();f.habilidades=d.habilidades();f.actitudes=d.actitudes();f.tecnicaEvaluacion=d.tecnicaEvaluacion();f.instrumentoEvaluacion=d.instrumentoEvaluacion();f.recursos=d.recursos();f.actividadesPermanentes=d.actividadesPermanentes();f.ajustesRazonables=d.ajustesRazonables();f.observaciones=d.observaciones();f.version=version;
        f.alineaciones=d.alineaciones().stream().map(x->{var a=new AlineacionForm();a.materiaId=x.materiaId();a.campoFormativo=x.campoFormativo();a.contenido=x.contenido();a.procesoDesarrollo=x.procesoDesarrollo();return a;}).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        f.actividades=d.actividades().stream().map(x->{var a=new ActividadForm();a.materiaId=x.materiaId();a.fecha=x.fecha();a.titulo=x.titulo();a.inicio=x.inicio();a.desarrollo=x.desarrollo();a.cierre=x.cierre();a.duracionMinutos=x.duracionMinutos();a.tarea=x.tarea();a.observaciones=x.observaciones();return a;}).collect(java.util.stream.Collectors.toCollection(ArrayList::new));return f;
    }
    @Getter @Setter public static class AlineacionForm {
        private Long materiaId;
        @NotBlank @Size(max=250) private String campoFormativo;
        @NotBlank @Size(max=12000) private String contenido;
        @NotBlank @Size(max=12000) private String procesoDesarrollo;
        PlaneacionRequest.Alineacion request(){return new PlaneacionRequest.Alineacion(materiaId,campoFormativo,contenido,procesoDesarrollo);}
    }
    @Getter @Setter public static class ActividadForm {
        @NotNull private Long materiaId;
        @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fecha;
        @NotBlank @Size(max=250) private String titulo;
        @NotBlank @Size(max=20000) private String inicio;
        @NotBlank @Size(max=20000) private String desarrollo;
        @NotBlank @Size(max=20000) private String cierre;
        @Min(1) @Max(1440) private Integer duracionMinutos;
        @Size(max=12000) private String tarea;
        @Size(max=12000) private String observaciones;
        PlaneacionRequest.Actividad request(){return new PlaneacionRequest.Actividad(materiaId,fecha,titulo,inicio,desarrollo,cierre,duracionMinutos,tarea,observaciones);}
    }
}
