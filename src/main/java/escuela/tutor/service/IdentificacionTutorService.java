package escuela.tutor.service;

import escuela.archivo.dto.ArchivoDescarga;
import escuela.tutor.dto.response.IdentificacionTutorResponse;
import escuela.tutor.entity.TipoIdentificacionTutor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IdentificacionTutorService {
    IdentificacionTutorResponse asignar(Long tutorId, TipoIdentificacionTutor tipo, MultipartFile archivo);
    void retirar(Long tutorId);
    IdentificacionTutorResponse actual(Long tutorId);
    List<IdentificacionTutorResponse> historial(Long tutorId);
    ArchivoDescarga descargar(Long tutorId, Long identificacionId);
}
