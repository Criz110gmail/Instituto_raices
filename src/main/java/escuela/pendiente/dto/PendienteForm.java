package escuela.pendiente.dto;

import escuela.pendiente.entity.EstadoPendiente;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter @Setter
public class PendienteForm {
 @NotBlank @Size(max=180) private String titulo;
 @Size(max=4000) private String descripcion;
 @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaCompromiso;
 @NotNull private EstadoPendiente estado=EstadoPendiente.PENDIENTE;
 private Long version;
}
