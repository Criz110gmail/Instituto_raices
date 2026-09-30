package escuela.calendario.dto;

import escuela.calendario.entity.TipoFechaCalendario;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.*;

@Getter @Setter
public class CalendarioEscolarForm {
    @NotNull(message="Selecciona una institución") private Long institucionId;
    @NotNull(message="Selecciona un ciclo escolar") private Long cicloEscolarId;
    private Long plantelId;private Long nivelEducativoId;
    @NotNull(message="Selecciona el tipo de fecha") private TipoFechaCalendario tipo=TipoFechaCalendario.DIA_INHABIL;
    @NotBlank(message="Indica un título") @Size(max=200,message="El título admite máximo 200 caracteres") private String titulo;
    @NotNull(message="Indica la fecha inicial") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaInicio;
    @NotNull(message="Indica la fecha final") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaFin;
    @DateTimeFormat(pattern="HH:mm") private LocalTime horaInicio;
    @DateTimeFormat(pattern="HH:mm") private LocalTime horaFin;
    private boolean suspendeClases;
    @Size(max=3000,message="La descripción admite máximo 3000 caracteres") private String descripcion;
    private boolean activo=true;private Long version;
}
