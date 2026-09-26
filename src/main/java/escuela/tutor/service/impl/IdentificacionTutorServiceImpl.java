package escuela.tutor.service.impl;

import escuela.archivo.dto.ArchivoDescarga;
import escuela.archivo.entity.Archivo;
import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.tutor.dto.response.IdentificacionTutorResponse;
import escuela.tutor.entity.TipoIdentificacionTutor;
import escuela.tutor.entity.Tutor;
import escuela.tutor.entity.TutorIdentificacion;
import escuela.tutor.repository.TutorIdentificacionRepository;
import escuela.tutor.repository.TutorRepository;
import escuela.tutor.service.IdentificacionTutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IdentificacionTutorServiceImpl implements IdentificacionTutorService {
    static final long TAMANO_MAXIMO = 10L * 1024 * 1024;

    private final TutorRepository tutorRepository;
    private final TutorIdentificacionRepository identificacionRepository;
    private final ArchivoRepository archivoRepository;
    private final AlmacenamientoArchivo almacenamiento;

    @Override
    public IdentificacionTutorResponse asignar(Long tutorId, TipoIdentificacionTutor tipo,
                                                MultipartFile archivoRecibido) {
        if (tipo == null) {
            throw new ReglaNegocioException("Selecciona el tipo de identificación oficial");
        }
        ArchivoValidado validado = validar(archivoRecibido);
        Tutor tutor = buscarConBloqueo(tutorId);
        if (!tutor.isActivo()) {
            throw new ReglaNegocioException("No se puede agregar una identificación a un tutor inactivo");
        }
        String clave = tutor.getInstitucion().getId() + "/tutores/" + tutorId
                + "/identificaciones/" + UUID.randomUUID() + ".bin";
        try (InputStream contenido = archivoRecibido.getInputStream()) {
            almacenamiento.guardar(clave, contenido);
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer la identificación seleccionada");
        }

        try {
            Archivo archivo = new Archivo();
            archivo.setInstitucion(tutor.getInstitucion());
            archivo.setClaveAlmacenamiento(clave);
            archivo.setNombreOriginal(nombreSeguro(archivoRecibido.getOriginalFilename(), validado.extension()));
            archivo.setTipoMime(validado.tipoMime());
            archivo.setTamanoBytes(archivoRecibido.getSize());
            archivo.setChecksumSha256(checksum(archivoRecibido));
            archivo.setEstado(EstadoArchivo.DISPONIBLE);
            archivo = archivoRepository.saveAndFlush(archivo);

            identificacionRepository.findFirstByTutorIdAndRetiradaEnIsNull(tutorId)
                    .ifPresent(anterior -> anterior.setRetiradaEn(Instant.now()));

            TutorIdentificacion identificacion = new TutorIdentificacion();
            identificacion.setTutor(tutor);
            identificacion.setArchivo(archivo);
            identificacion.setTipo(tipo);
            return respuesta(identificacionRepository.saveAndFlush(identificacion));
        } catch (RuntimeException excepcion) {
            almacenamiento.eliminarSiExiste(clave);
            throw excepcion;
        }
    }

    @Override
    public void retirar(Long tutorId) {
        buscarConBloqueo(tutorId);
        TutorIdentificacion actual = identificacionRepository
                .findFirstByTutorIdAndRetiradaEnIsNull(tutorId)
                .orElseThrow(() -> new ReglaNegocioException("El tutor no tiene una identificación vigente"));
        actual.setRetiradaEn(Instant.now());
        identificacionRepository.saveAndFlush(actual);
    }

    @Override
    @Transactional(readOnly = true)
    public IdentificacionTutorResponse actual(Long tutorId) {
        buscar(tutorId);
        return identificacionRepository.findFirstByTutorIdAndRetiradaEnIsNull(tutorId)
                .map(this::respuesta).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentificacionTutorResponse> historial(Long tutorId) {
        buscar(tutorId);
        return identificacionRepository.findAllByTutorIdOrderByCreadoEnDesc(tutorId).stream()
                .map(this::respuesta).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoDescarga descargar(Long tutorId, Long identificacionId) {
        buscar(tutorId);
        TutorIdentificacion identificacion = identificacionRepository
                .findByIdAndTutorId(identificacionId, tutorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la identificación", identificacionId));
        Archivo archivo = identificacion.getArchivo();
        if (archivo.getEstado() != EstadoArchivo.DISPONIBLE) {
            throw new ReglaNegocioException("La identificación no está disponible");
        }
        return new ArchivoDescarga(almacenamiento.abrir(archivo.getClaveAlmacenamiento()),
                archivo.getNombreOriginal(), archivo.getTipoMime(), archivo.getTamanoBytes());
    }

    private ArchivoValidado validar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty() || archivo.getSize() <= 0) {
            throw new ReglaNegocioException("Selecciona un archivo de identificación");
        }
        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new ReglaNegocioException("La identificación no puede superar 10 MB");
        }
        try (InputStream entrada = archivo.getInputStream()) {
            byte[] cabecera = entrada.readNBytes(8);
            if (cabecera.length >= 5 && cabecera[0] == '%' && cabecera[1] == 'P'
                    && cabecera[2] == 'D' && cabecera[3] == 'F' && cabecera[4] == '-') {
                return new ArchivoValidado("application/pdf", "pdf");
            }
            if (cabecera.length >= 3 && (cabecera[0] & 0xff) == 0xff
                    && (cabecera[1] & 0xff) == 0xd8 && (cabecera[2] & 0xff) == 0xff) {
                return new ArchivoValidado("image/jpeg", "jpg");
            }
            byte[] png = {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
            if (java.util.Arrays.equals(cabecera, png)) {
                return new ArchivoValidado("image/png", "png");
            }
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer la identificación seleccionada");
        }
        throw new ReglaNegocioException("La identificación debe ser un PDF, JPEG o PNG válido");
    }

    private String checksum(MultipartFile archivo) {
        try (InputStream entrada = archivo.getInputStream()) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bloque = new byte[8192];
            int leidos;
            while ((leidos = entrada.read(bloque)) >= 0) {
                if (leidos > 0) digest.update(bloque, 0, leidos);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException excepcion) {
            throw new ReglaNegocioException("No fue posible verificar la identificación seleccionada");
        }
    }

    private String nombreSeguro(String original, String extension) {
        String normalizado = original == null ? "identificacion." + extension
                : original.replace('\\', '/');
        String nombre = normalizado.substring(normalizado.lastIndexOf('/') + 1)
                .replaceAll("[\\p{Cntrl}]", "").trim();
        if (nombre.isBlank()) nombre = "identificacion." + extension;
        return nombre.length() > 255 ? nombre.substring(nombre.length() - 255) : nombre;
    }

    private Tutor buscar(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el tutor", id));
    }

    private Tutor buscarConBloqueo(Long id) {
        return tutorRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el tutor", id));
    }

    private IdentificacionTutorResponse respuesta(TutorIdentificacion identificacion) {
        Archivo archivo = identificacion.getArchivo();
        return new IdentificacionTutorResponse(identificacion.getId(), identificacion.getTipo(),
                identificacion.getTipo().etiqueta(), archivo.getNombreOriginal(), archivo.getTipoMime(),
                archivo.getTamanoBytes(), identificacion.getCreadoEn(), identificacion.getRetiradaEn(),
                identificacion.getRetiradaEn() == null);
    }

    private record ArchivoValidado(String tipoMime, String extension) {}
}
