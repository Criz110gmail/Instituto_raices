package escuela.archivo.storage;

import escuela.common.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class AlmacenamientoArchivoLocal implements AlmacenamientoArchivo {

    private final Path raiz;

    public AlmacenamientoArchivoLocal(@Value("${app.archivos.directorio}") String directorio) {
        try {
            raiz = Path.of(directorio).toAbsolutePath().normalize();
            Files.createDirectories(raiz);
        } catch (IOException | RuntimeException excepcion) {
            throw new IllegalStateException("No fue posible inicializar el almacenamiento privado", excepcion);
        }
    }

    @Override
    public void guardar(String clave, InputStream contenido) {
        Path destino = resolver(clave);
        Path temporal = null;
        try {
            Files.createDirectories(destino.getParent());
            temporal = destino.getParent().resolve(".carga-" + UUID.randomUUID() + ".tmp");
            Files.copy(contenido, temporal, StandardCopyOption.REPLACE_EXISTING);
            try {
                Files.move(temporal, destino, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignorada) {
                Files.move(temporal, destino);
            }
        } catch (IOException excepcion) {
            eliminarTemporal(temporal);
            throw new ReglaNegocioException("No fue posible guardar el archivo privado. Intenta nuevamente");
        }
    }

    @Override
    public Resource abrir(String clave) {
        Path archivo = resolver(clave);
        if (!Files.isRegularFile(archivo, LinkOption.NOFOLLOW_LINKS)) {
            throw new ReglaNegocioException("El archivo privado no está disponible");
        }
        return new FileSystemResource(archivo);
    }

    @Override
    public void eliminarSiExiste(String clave) {
        try {
            Files.deleteIfExists(resolver(clave));
        } catch (IOException ignorada) {
            // Limpieza de compensación: no debe ocultar la causa original de la operación.
        }
    }

    private Path resolver(String clave) {
        Path resultado = raiz.resolve(clave).normalize();
        if (!resultado.startsWith(raiz)) {
            throw new ReglaNegocioException("La referencia del archivo no es válida");
        }
        return resultado;
    }

    private void eliminarTemporal(Path temporal) {
        if (temporal == null) return;
        try {
            Files.deleteIfExists(temporal);
        } catch (IOException ignorada) {
            // No hay una acción segura adicional durante una carga fallida.
        }
    }
}
