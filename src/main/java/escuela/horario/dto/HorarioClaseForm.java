package escuela.horario.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.*;

@Getter @Setter
public class HorarioClaseForm {
    @NotNull(message="Selecciona una asignación docente") private Long asignacionMaestroId;
    @NotNull(message="Selecciona el día") @Min(1) @Max(7) private Integer diaSemana;
    @NotNull(message="Indica la hora de inicio") @DateTimeFormat(pattern="HH:mm") private LocalTime horaInicio;
    @NotNull(message="Indica la hora de fin") @DateTimeFormat(pattern="HH:mm") private LocalTime horaFin;
    @NotNull(message="Indica desde cuándo aplica") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaInicio;
    @NotNull(message="Indica hasta cuándo aplica") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaFin;
    @Size(max=100,message="El aula admite máximo 100 caracteres") private String aula;
    @Size(max=1000,message="Las observaciones admiten máximo 1000 caracteres") private String observaciones;
    private boolean activo=true;
    private Long version;
}
