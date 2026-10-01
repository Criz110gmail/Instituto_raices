package escuela.archivo.imagen;

import escuela.common.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
public class ProcesadorFotografia {

    public static final long TAMANO_MAXIMO_ENTRADA = 20L * 1024 * 1024;
    private static final long TAMANO_MAXIMO_SALIDA = 6L * 1024 * 1024;
    private static final Duration TIEMPO_MAXIMO = Duration.ofSeconds(25);
    private static final Set<String> MARCAS_HEIF = Set.of(
            "heic", "heix", "hevc", "hevx", "heim", "heis", "hevm", "hevs", "mif1", "msf1");

    private final String comando;
    private final Semaphore espacios = new Semaphore(2, true);

    public ProcesadorFotografia(@Value("${app.imagenes.comando:/usr/bin/magick}") String comando) {
        this.comando = comando;
    }

    public ImagenOptimizada procesar(MultipartFile archivo) {
        byte[] original = leerYValidar(archivo);
        String formato = detectarFormato(original);
        Path directorio = null;
        boolean adquirido = false;
        try {
            adquirido = espacios.tryAcquire(10, TimeUnit.SECONDS);
            if (!adquirido) {
                throw new ReglaNegocioException("Hay varias fotografías procesándose. Intenta nuevamente en unos segundos");
            }
            directorio = Files.createTempDirectory("nexo-fotografia-");
            Path entrada = directorio.resolve("entrada." + formato);
            Path salida = directorio.resolve("salida.jpg");
            Files.write(entrada, original);
            convertir(entrada, salida);
            byte[] optimizada = Files.readAllBytes(salida);
            validarSalida(optimizada);
            return new ImagenOptimizada(optimizada, nombreJpeg(archivo.getOriginalFilename()),
                    "image/jpeg", checksum(optimizada), original.length);
        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();
            throw new ReglaNegocioException("Se interrumpió el procesamiento de la fotografía. Intenta nuevamente");
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible procesar la fotografía seleccionada");
        } finally {
            if (adquirido) espacios.release();
            limpiar(directorio);
        }
    }

    private byte[] leerYValidar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty() || archivo.getSize() <= 0) {
            throw new ReglaNegocioException("Selecciona una fotografía");
        }
        if (archivo.getSize() > TAMANO_MAXIMO_ENTRADA) {
            throw new ReglaNegocioException("La fotografía original no puede superar 20 MB");
        }
        try {
            byte[] contenido = archivo.getBytes();
            if (contenido.length == 0 || contenido.length > TAMANO_MAXIMO_ENTRADA) {
                throw new ReglaNegocioException("La fotografía original no puede superar 20 MB");
            }
            return contenido;
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer la fotografía seleccionada");
        }
    }

    String detectarFormato(byte[] contenido) {
        if (contenido.length >= 3 && (contenido[0] & 0xff) == 0xff
                && (contenido[1] & 0xff) == 0xd8 && (contenido[2] & 0xff) == 0xff) return "jpg";
        byte[] png = {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
        if (contenido.length >= png.length) {
            boolean coincide = true;
            for (int i = 0; i < png.length; i++) coincide &= contenido[i] == png[i];
            if (coincide) return "png";
        }
        if (esHeif(contenido)) return "heic";
        throw new ReglaNegocioException("La fotografía debe ser un archivo JPEG, PNG, HEIC o HEIF válido");
    }

    private boolean esHeif(byte[] contenido) {
        if (contenido.length < 16 || contenido[4] != 'f' || contenido[5] != 't'
                || contenido[6] != 'y' || contenido[7] != 'p') return false;
        int limite = Math.min(contenido.length - 3, 80);
        for (int posicion = 8; posicion < limite; posicion += 4) {
            String marca = new String(contenido, posicion, 4, StandardCharsets.US_ASCII)
                    .toLowerCase(Locale.ROOT);
            if (MARCAS_HEIF.contains(marca)) return true;
        }
        return false;
    }

    private void convertir(Path entrada, Path salida) throws IOException, InterruptedException {
        Process proceso;
        try {
            proceso = new ProcessBuilder(comando,
                    "-limit", "thread", "2",
                    "-limit", "memory", "256MiB",
                    "-limit", "map", "512MiB",
                    "-limit", "disk", "512MiB",
                    "-limit", "time", "25",
                    "-limit", "width", "12000",
                    "-limit", "height", "12000",
                    "-define", "heic:max-items=32",
                    "-define", "heic:max-components=16",
                    "-define", "heic:max-iloc-extents-per-item=32",
                    "-define", "heic:max-children-per-box=64",
                    entrada + "[0]",
                    "-auto-orient",
                    "-strip",
                    "-colorspace", "sRGB",
                    "-resize", "1920x1920>",
                    "-background", "white",
                    "-alpha", "remove",
                    "-alpha", "off",
                    "-sampling-factor", "4:2:0",
                    "-quality", "85",
                    "jpeg:" + salida)
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start();
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("El servicio de optimización de imágenes no está disponible");
        }
        if (!proceso.waitFor(TIEMPO_MAXIMO.toSeconds(), TimeUnit.SECONDS)) {
            proceso.destroyForcibly();
            throw new ReglaNegocioException("La fotografía tardó demasiado en procesarse. Intenta con otra imagen");
        }
        if (proceso.exitValue() != 0 || !Files.isRegularFile(salida)) {
            throw new ReglaNegocioException("La fotografía está dañada o su formato HEIC/HEIF no es compatible");
        }
    }

    private void validarSalida(byte[] contenido) {
        if (contenido.length == 0 || contenido.length > TAMANO_MAXIMO_SALIDA) {
            throw new ReglaNegocioException("No fue posible reducir la fotografía a un tamaño seguro");
        }
        try {
            var imagen = ImageIO.read(new ByteArrayInputStream(contenido));
            if (imagen == null || imagen.getWidth() <= 0 || imagen.getHeight() <= 0
                    || imagen.getWidth() > 1920 || imagen.getHeight() > 1920) {
                throw new ReglaNegocioException("No fue posible verificar la fotografía optimizada");
            }
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible verificar la fotografía optimizada");
        }
    }

    private String nombreJpeg(String original) {
        String nombre = original == null ? "" : original.replace('\\', '/');
        nombre = nombre.substring(nombre.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}]", "").trim();
        int punto = nombre.lastIndexOf('.');
        if (punto > 0) nombre = nombre.substring(0, punto);
        if (nombre.isBlank()) nombre = "fotografia";
        if (nombre.length() > 246) nombre = nombre.substring(0, 246);
        return nombre + ".jpg";
    }

    private String checksum(byte[] contenido) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contenido));
        } catch (NoSuchAlgorithmException excepcion) {
            throw new IllegalStateException("SHA-256 no está disponible", excepcion);
        }
    }

    private void limpiar(Path directorio) {
        if (directorio == null) return;
        try {
            Files.deleteIfExists(directorio.resolve("entrada.jpg"));
            Files.deleteIfExists(directorio.resolve("entrada.png"));
            Files.deleteIfExists(directorio.resolve("entrada.heic"));
            Files.deleteIfExists(directorio.resolve("salida.jpg"));
            Files.deleteIfExists(directorio);
        } catch (IOException ignorada) {
            // El sistema operativo terminará limpiando cualquier temporal residual.
        }
    }
}
