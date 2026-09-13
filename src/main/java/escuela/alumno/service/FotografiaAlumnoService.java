package escuela.alumno.service;

import escuela.alumno.dto.response.FotografiaAlumnoResponse;
import escuela.archivo.dto.ArchivoDescarga;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FotografiaAlumnoService {
    FotografiaAlumnoResponse asignar(Long alumnoId, MultipartFile fotografia);
    void retirar(Long alumnoId);
    FotografiaAlumnoResponse actual(Long alumnoId);
    List<FotografiaAlumnoResponse> historial(Long alumnoId);
    ArchivoDescarga descargar(Long alumnoId, Long fotografiaId);
}
