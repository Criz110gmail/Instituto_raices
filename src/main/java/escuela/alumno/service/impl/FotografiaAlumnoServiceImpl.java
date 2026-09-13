package escuela.alumno.service.impl;

import escuela.alumno.dto.response.FotografiaAlumnoResponse;
import escuela.alumno.entity.Alumno;
import escuela.alumno.entity.AlumnoFotografia;
import escuela.alumno.repository.AlumnoFotografiaRepository;
import escuela.alumno.repository.AlumnoRepository;
import escuela.alumno.service.FotografiaAlumnoService;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.archivo.entity.Archivo;
import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
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
import java.time.Instant;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FotografiaAlumnoServiceImpl implements FotografiaAlumnoService {

    static final long TAMANO_MAXIMO = 5L * 1024 * 1024;
    private static final long PIXELES_MAXIMOS = 25_000_000L;
    private final AlumnoRepository alumnoRepository;
    private final AlumnoFotografiaRepository fotografiaRepository;
    private final ArchivoRepository archivoRepository;
    private final AlmacenamientoArchivo almacenamiento;

    @Override
    public FotografiaAlumnoResponse asignar(Long alumnoId, MultipartFile fotografia) {
        ImagenValidada imagen = validar(fotografia);
        Alumno alumno = buscarConBloqueo(alumnoId);
        if (!alumno.isActivo()) {
            throw new ReglaNegocioException("No se puede cambiar la fotografía de un alumno inactivo");
        }
        String clave = alumno.getInstitucion().getId() + "/alumnos/" + alumnoId + "/"
                + UUID.randomUUID() + ".bin";
        try (InputStream contenido = fotografia.getInputStream()) {
            almacenamiento.guardar(clave, contenido);
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer la fotografía seleccionada");
        }

        try {
            Archivo archivo = new Archivo();
            archivo.setInstitucion(alumno.getInstitucion());
            archivo.setClaveAlmacenamiento(clave);
            archivo.setNombreOriginal(nombreSeguro(fotografia.getOriginalFilename(), imagen.extension()));
            archivo.setTipoMime(imagen.tipoMime());
            archivo.setTamanoBytes(fotografia.getSize());
            archivo.setChecksumSha256(checksum(fotografia));
            archivo.setEstado(EstadoArchivo.DISPONIBLE);
            archivo = archivoRepository.saveAndFlush(archivo);

            fotografiaRepository.findFirstByAlumnoIdAndRetiradaEnIsNull(alumnoId)
                    .ifPresent(anterior -> anterior.setRetiradaEn(Instant.now()));

            AlumnoFotografia relacion = new AlumnoFotografia();
            relacion.setAlumno(alumno);
            relacion.setArchivo(archivo);
            relacion = fotografiaRepository.saveAndFlush(relacion);
            alumno.setFotografiaArchivo(archivo);
            alumnoRepository.saveAndFlush(alumno);
            return respuesta(relacion);
        } catch (RuntimeException excepcion) {
            almacenamiento.eliminarSiExiste(clave);
            throw excepcion;
        }
    }

    @Override
    public void retirar(Long alumnoId) {
        Alumno alumno = buscarConBloqueo(alumnoId);
        AlumnoFotografia actual = fotografiaRepository
                .findFirstByAlumnoIdAndRetiradaEnIsNull(alumnoId)
                .orElseThrow(() -> new ReglaNegocioException("El alumno no tiene una fotografía actual"));
        actual.setRetiradaEn(Instant.now());
        alumno.setFotografiaArchivo(null);
        fotografiaRepository.saveAndFlush(actual);
        alumnoRepository.saveAndFlush(alumno);
    }

    @Override
    @Transactional(readOnly = true)
    public FotografiaAlumnoResponse actual(Long alumnoId) {
        buscar(alumnoId);
        return fotografiaRepository.findFirstByAlumnoIdAndRetiradaEnIsNull(alumnoId)
                .map(this::respuesta).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FotografiaAlumnoResponse> historial(Long alumnoId) {
        buscar(alumnoId);
        return fotografiaRepository.findAllByAlumnoIdOrderByCreadoEnDesc(alumnoId).stream()
                .map(this::respuesta).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoDescarga descargar(Long alumnoId, Long fotografiaId) {
        buscar(alumnoId);
        AlumnoFotografia fotografia = fotografiaRepository.findByIdAndAlumnoId(fotografiaId, alumnoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la fotografía", fotografiaId));
        Archivo archivo = fotografia.getArchivo();
        if (archivo.getEstado() != EstadoArchivo.DISPONIBLE) {
            throw new ReglaNegocioException("La fotografía no está disponible");
        }
        return new ArchivoDescarga(almacenamiento.abrir(archivo.getClaveAlmacenamiento()),
                archivo.getNombreOriginal(), archivo.getTipoMime(), archivo.getTamanoBytes());
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
            if (java.util.Arrays.equals(cabecera, png)) {
                return new ImagenValidada("image/png", "png");
            }
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
                boolean formatoCorrecto = tipo.tipoMime().equals("image/png")
                        ? formato.contains("png") : formato.contains("jpeg") || formato.contains("jpg");
                if (!formatoCorrecto || ancho <= 0 || alto <= 0
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
        String nombre = original == null ? "fotografia." + extension
                : original.replace('\\', '/').substring(original.replace('\\', '/').lastIndexOf('/') + 1)
                .replaceAll("[\\p{Cntrl}]", "").trim();
        if (nombre.isBlank()) nombre = "fotografia." + extension;
        return nombre.length() > 255 ? nombre.substring(nombre.length() - 255) : nombre;
    }

    private Alumno buscar(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el alumno", id));
    }

    private Alumno buscarConBloqueo(Long id) {
        return alumnoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el alumno", id));
    }

    private FotografiaAlumnoResponse respuesta(AlumnoFotografia fotografia) {
        Archivo archivo = fotografia.getArchivo();
        return new FotografiaAlumnoResponse(fotografia.getId(), archivo.getNombreOriginal(),
                archivo.getTipoMime(), archivo.getTamanoBytes(), fotografia.getCreadoEn(),
                fotografia.getRetiradaEn(), fotografia.getRetiradaEn() == null);
    }

    private ReglaNegocioException imagenInvalida() {
        return new ReglaNegocioException("La fotografía está dañada o sus dimensiones no son seguras");
    }

    private record ImagenValidada(String tipoMime, String extension) {
    }
}
