package escuela.admin.dto;

import escuela.comunicacion.dto.request.AvisoRequest;
import escuela.comunicacion.dto.response.AvisoResponse;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter @Setter
public class AvisoForm {
    @NotNull private Long institucionId;
    private Long plantelId;
    @NotBlank @Size(max=200) private String titulo;
    @NotBlank @Size(max=8000) private String contenido;
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm") private LocalDateTime expiraLocal;
    private Long version;
    public AvisoRequest request() { return new AvisoRequest(institucionId, plantelId, titulo, contenido, expiraLocal, version); }
    public static AvisoForm desde(AvisoResponse r) { AvisoForm f=new AvisoForm(); f.institucionId=r.institucionId();
        f.plantelId=r.plantelId(); f.titulo=r.titulo(); f.contenido=r.contenido();
        f.expiraLocal=r.expiraLocal(); f.version=r.auditoria().version(); return f; }
}
