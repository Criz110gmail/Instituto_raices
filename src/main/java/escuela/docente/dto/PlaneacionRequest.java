package escuela.docente.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record PlaneacionRequest(
        @NotNull Long grupoId,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin,
        @NotEmpty List<@NotNull Long> materiaIds,
        @Size(max=12000) String situacionDidactica,
        @NotBlank @Size(max=20000) String proposito,
        @Size(max=12000) String ejesArticuladores,
        @Size(max=12000) String conocimientos,
        @Size(max=12000) String habilidades,
        @Size(max=12000) String actitudes,
        @Size(max=12000) String tecnicaEvaluacion,
        @Size(max=12000) String instrumentoEvaluacion,
        @Size(max=12000) String recursos,
        @Size(max=12000) String actividadesPermanentes,
        @Size(max=12000) String ajustesRazonables,
        @Size(max=12000) String observaciones,
        @Valid List<Alineacion> alineaciones,
        @NotEmpty @Valid List<Actividad> actividades,
        Long version) {
    public record Alineacion(Long materiaId, @NotBlank @Size(max=250) String campoFormativo,
                             @NotBlank @Size(max=12000) String contenido,
                             @NotBlank @Size(max=12000) String procesoDesarrollo) { }
    public record Actividad(@NotNull Long materiaId, @NotNull LocalDate fecha,
                            @NotBlank @Size(max=250) String titulo,
                            @NotBlank @Size(max=20000) String inicio,
                            @NotBlank @Size(max=20000) String desarrollo,
                            @NotBlank @Size(max=20000) String cierre,
                            @Min(1) @Max(1440) Integer duracionMinutos,
                            @Size(max=12000) String tarea,
                            @Size(max=12000) String observaciones) { }
}
