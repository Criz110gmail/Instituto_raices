package escuela.seguridad.service;

import escuela.archivo.dto.ArchivoDescarga;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface FotografiaUsuarioService {
    void asignar(Long usuarioId, MultipartFile fotografia);
    void retirar(Long usuarioId);
    Optional<ArchivoDescarga> descargarActual(Long usuarioId);
}
