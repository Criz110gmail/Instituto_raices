package escuela.alumno.dto;

import escuela.alumno.entity.TipoDocumentoAlumno;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class PortalDocumentoPropuestaForm {
    @NotNull(message = "Selecciona el tipo de documento") private TipoDocumentoAlumno tipoDocumento;
    @Size(max = 500, message = "La descripción admite máximo 500 caracteres") private String descripcion;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaDocumento;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate vigenteHasta;
    @NotNull(message = "Selecciona un archivo") private MultipartFile archivo;
    @Size(max = 2000, message = "El mensaje admite máximo 2000 caracteres") private String mensajeTutor;
    @AssertTrue(message = "Debes aceptar el consentimiento para enviar la propuesta")
    private boolean consentimiento;
}
