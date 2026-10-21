package escuela.personal.dto;
import jakarta.validation.constraints.*;import lombok.Getter;import lombok.Setter;
import java.time.LocalDate;import org.springframework.format.annotation.DateTimeFormat;
@Getter @Setter public class AsignacionLaboralForm {
 @NotNull private Long empleadoId;
 @NotNull private Long esquemaId;
 private String empleadoTexto,esquemaTexto;
 @NotNull private Long versionEsquema,versionJornada,versionPuesto,versionLaboral,revision;
 @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate inicio;
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fin;
 @NotBlank @Size(max=2000) private String motivo;
}
