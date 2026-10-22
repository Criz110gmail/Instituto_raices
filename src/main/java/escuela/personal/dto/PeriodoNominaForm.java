package escuela.personal.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Getter @Setter
public class PeriodoNominaForm {
 @NotBlank(message="Captura el código del periodo.") @Size(max=50) @Pattern(regexp="[A-Za-z0-9][A-Za-z0-9_-]{0,49}",message="Usa letras, números, guion o guion bajo, sin espacios.") private String codigo;
 @NotBlank(message="Captura el nombre del periodo.") @Size(max=150) private String nombre;
 @NotNull(message="Selecciona un calendario de las sugerencias.") @Positive private Long calendarioId;
 @NotBlank @Size(max=450) private String calendarioTexto;
 @NotNull(message="Espera la consulta del calendario antes de confirmar.") @PositiveOrZero private Long calendarioVersion;
 @NotNull(message="Captura el inicio del periodo.") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate inicio;
 @NotNull(message="Captura el fin del periodo.") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fin;
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate corte;
 @NotNull(message="Captura la fecha prevista de pago.") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate pago;
 @NotBlank(message="Explica el motivo de registro o cambio.") @Size(max=2000) private String motivo;
 @NotNull @PositiveOrZero private Long version=0L;
}
