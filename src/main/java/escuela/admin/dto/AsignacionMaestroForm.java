package escuela.admin.dto;

import escuela.docente.dto.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter @Setter
public class AsignacionMaestroForm {
    @NotNull private Long grupoId;
    private String grupoTexto;
    @NotNull private Long materiaId;
    private String materiaTexto;
    @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaInicio;
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaFin;
    private Long version;
    public AsignacionMaestroRequest request(){return new AsignacionMaestroRequest(grupoId,materiaId,fechaInicio,fechaFin,version);}
}
