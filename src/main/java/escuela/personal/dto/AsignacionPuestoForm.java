package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Getter @Setter public class AsignacionPuestoForm {
 @NotNull private Long puestoId;
 @NotBlank @Size(max=250) private String puestoTexto;
 @NotNull private Long puestoVersion;
 @NotNull @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE) private LocalDate inicio;
 @NotBlank(message="Explica el motivo de la asignación o cambio.") @Size(max=2000) private String motivo;
 @NotNull private Long versionLaboral;
 @NotNull private Long revision;
 @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE) private LocalDate consulta;
 private int pagina;
 public CondicionSalarialForm condicion(){var f=new CondicionSalarialForm();f.setPuestoId(puestoId);f.setPuestoTexto(puestoTexto);f.setFechaInicio(inicio);f.setMotivo(motivo);f.setVersionLaboral(versionLaboral);f.setRevision(revision);return f;}
}
