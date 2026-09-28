package escuela.admin.dto;

import escuela.alumno.entity.TipoDocumentoAlumno;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class DocumentoAlumnoForm {
    @NotNull private TipoDocumentoAlumno tipo;
    @Size(max = 500) private String descripcion;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaDocumento;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate vigenteHasta;
    @NotNull private MultipartFile archivo;
}

