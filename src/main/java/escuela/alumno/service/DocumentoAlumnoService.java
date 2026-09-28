package escuela.alumno.service;

import escuela.alumno.dto.response.DocumentoAlumnoResponse;
import escuela.alumno.entity.TipoDocumentoAlumno;
import escuela.archivo.dto.ArchivoDescarga;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface DocumentoAlumnoService {
    DocumentoAlumnoResponse agregar(Long alumnoId, TipoDocumentoAlumno tipo, String descripcion,
                                     LocalDate fechaDocumento, LocalDate vigenteHasta,
                                     MultipartFile archivo);
    void retirar(Long alumnoId, Long documentoId, Long version);
    List<DocumentoAlumnoResponse> historial(Long alumnoId);
    ArchivoDescarga descargar(Long alumnoId, Long documentoId);
}

