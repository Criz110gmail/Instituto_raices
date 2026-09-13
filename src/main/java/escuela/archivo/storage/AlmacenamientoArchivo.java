package escuela.archivo.storage;

import org.springframework.core.io.Resource;

import java.io.InputStream;

public interface AlmacenamientoArchivo {
    void guardar(String clave, InputStream contenido);
    Resource abrir(String clave);
    void eliminarSiExiste(String clave);
}
