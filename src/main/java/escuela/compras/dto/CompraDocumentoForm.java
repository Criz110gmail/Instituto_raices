package escuela.compras.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class CompraDocumentoForm {
    @NotNull private Long version;
    @NotBlank @Pattern(regexp="PDF|IMAGEN|XML") private String tipo="PDF";
    @Size(max=300) private String descripcion;
    @Size(max=2000) private String justificacion;
    private MultipartFile archivo;
    private boolean confirmado;
}
