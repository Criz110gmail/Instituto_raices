package escuela.admin.dto;

import escuela.inscripcion.dto.request.AsignacionGrupoRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class AsignacionGrupoForm {
    @NotNull private Long grupoId;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio = LocalDate.now();
    @Size(max = 2000) private String motivo;

    public AsignacionGrupoRequest request() {
        return new AsignacionGrupoRequest(grupoId, fechaInicio, motivo);
    }
}
