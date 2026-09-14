package escuela.seguridad.service.impl;

import escuela.archivo.dto.ArchivoDescarga;
import escuela.archivo.entity.Archivo;
import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.FotografiaUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FotografiaUsuarioServiceImpl implements FotografiaUsuarioService {

    static final long TAMANO_MAXIMO = 5L * 1024 * 1024;
    private static final long PIXELES_MAXIMOS = 25_000_000L;
    private final UsuarioRepository usuarioRepository;
    private final ArchivoRepository archivoRepository;
    private final AlmacenamientoArchivo almacenamiento;

    @Override
    public void asignar(Long usuarioId, MultipartFile fotografia) {
        ImagenValidada imagen = validar(fotografia);
        Usuario usuario = buscarConBloqueo(usuarioId);
        String clave = usuario.getInstitucion().getId() + "/usuarios/" + usuarioId + "/"
                + UUID.randomUUID() + ".bin";
        try (InputStream contenido = fotografia.getInputStream()) {
            almacenamiento.guardar(clave, contenido);
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer la fotografía seleccionada");
        }

        try {
            Archivo archivo = new Archivo();
            archivo.setInstitucion(usuario.getInstitucion());
            archivo.setClaveAlmacenamiento(clave);
            archivo.setNombreOriginal(nombreSeguro(fotografia.getOriginalFilename(), imagen.extension()));
            archivo.setTipoMime(imagen.tipoMime());
            archivo.setTamanoBytes(fotografia.getSize());
            archivo.setChecksumSha256(checksum(fotografia));
            archivo.setEstado(EstadoArchivo.DISPONIBLE);
            archivo = archivoRepository.saveAndFlush(archivo);

            Archivo anterior = usuario.getFotografiaArchivo();
            if (anterior != null) anterior.setEstado(EstadoArchivo.RETIRADO);
            usuario.setFotografiaArchivo(archivo);
            usuarioRepository.saveAndFlush(usuario);
        } catch (RuntimeException excepcion) {
            almacenamiento.eliminarSiExiste(clave);
            throw excepcion;
        }
    }

    @Override
    public void retirar(Long usuarioId) {
        Usuario usuario = buscarConBloqueo(usuarioId);
        Archivo actual = usuario.getFotografiaArchivo();
        if (actual == null) {
            throw new ReglaNegocioException("El usuario no tiene una fotografía personalizada");
        }
        actual.setEstado(EstadoArchivo.RETIRADO);
        usuario.setFotografiaArchivo(null);
        usuarioRepository.saveAndFlush(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArchivoDescarga> descargarActual(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el usuario", usuarioId));
        Archivo archivo = usuario.getFotografiaArchivo();
        if (archivo == null || archivo.getEstado() != EstadoArchivo.DISPONIBLE) return Optional.empty();
        return Optional.of(new ArchivoDescarga(almacenamiento.abrir(archivo.getClaveAlmacenamiento()),
                archivo.getNombreOriginal(), archivo.getTipoMime(), archivo.getTamanoBytes()));
    }

    private Usuario buscarConBloqueo(Long id) {
        return usuarioRepository.buscarPorIdConBloqueo(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el usuario", id));
    }

    private ImagenValidada validar(MultipartFile fotografia) {
        if (fotografia == null || fotografia.isEmpty() || fotografia.getSize() <= 0) {
            throw new ReglaNegocioException("Selecciona una fotografía");
        }
        if (fotografia.getSize() > TAMANO_MAXIMO) {
            throw new ReglaNegocioException("La fotografía no puede superar 5 MB");
        }
        ImagenValidada tipo = detectarTipo(fotografia);
        validarDimensiones(fotografia, tipo);
        return tipo;
    }

    private ImagenValidada detectarTipo(MultipartFile fotografia) {
        try (InputStream entrada = fotografia.getInputStream()) {
            byte[] cabecera = entrada.readNBytes(8);
            if (cabecera.length >= 3 && (cabecera[0] & 0xff) == 0xff
                    && (cabecera[1] & 0xff) == 0xd8 && (cabecera[2] & 0xff) == 0xff) {
                return new ImagenValidada("image/jpeg", "jpg");
            }
            byte[] png = {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
            if (Arrays.equals(cabecera, png)) return new ImagenValidada("image/png", "png");
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer la fotografía seleccionada");
        }
        throw new ReglaNegocioException("La fotografía debe ser un archivo JPEG o PNG válido");
    }

    private void validarDimensiones(MultipartFile fotografia, ImagenValidada tipo) {
        try (InputStream entrada = fotografia.getInputStream();
             ImageInputStream imagen = ImageIO.createImageInputStream(entrada)) {
            if (imagen == null) throw imagenInvalida();
            Iterator<ImageReader> lectores = ImageIO.getImageReaders(imagen);
            if (!lectores.hasNext()) throw imagenInvalida();
            ImageReader lector = lectores.next();
            try {
                lector.setInput(imagen, true, true);
                int ancho = lector.getWidth(0);
                int alto = lector.getHeight(0);
                String formato = lector.getFormatName().toLowerCase(Locale.ROOT);
                boolean correcto = tipo.tipoMime().equals("image/png")
                        ? formato.contains("png") : formato.contains("jpeg") || formato.contains("jpg");
                if (!correcto || ancho <= 0 || alto <= 0
                        || (long) ancho * alto > PIXELES_MAXIMOS) throw imagenInvalida();
            } finally {
                lector.dispose();
            }
        } catch (IOException excepcion) {
            throw imagenInvalida();
        }
    }

    private String checksum(MultipartFile fotografia) {
        try (InputStream entrada = fotografia.getInputStream()) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bloque = new byte[8192];
            int leidos;
            while ((leidos = entrada.read(bloque)) >= 0) {
                if (leidos > 0) digest.update(bloque, 0, leidos);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException excepcion) {
            throw new ReglaNegocioException("No fue posible verificar la fotografía seleccionada");
        }
    }

    private String nombreSeguro(String original, String extension) {
        String normalizado = original == null ? "" : original.replace('\\', '/');
        String nombre = normalizado.isBlank() ? "fotografia." + extension
                : normalizado.substring(normalizado.lastIndexOf('/') + 1)
                .replaceAll("[\\p{Cntrl}]", "").trim();
        if (nombre.isBlank()) nombre = "fotografia." + extension;
        return nombre.length() > 255 ? nombre.substring(nombre.length() - 255) : nombre;
    }

    private ReglaNegocioException imagenInvalida() {
        return new ReglaNegocioException("La fotografía está dañada o sus dimensiones no son seguras");
    }

    private record ImagenValidada(String tipoMime, String extension) {
    }
}
