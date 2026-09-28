package escuela.alumno.service.impl;

import escuela.alumno.dto.response.DocumentoAlumnoResponse;
import escuela.alumno.entity.Alumno;
import escuela.alumno.entity.AlumnoDocumento;
import escuela.alumno.entity.TipoDocumentoAlumno;
import escuela.alumno.repository.AlumnoDocumentoRepository;
import escuela.alumno.repository.AlumnoRepository;
import escuela.alumno.service.DocumentoAlumnoService;
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

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentoAlumnoServiceImpl implements DocumentoAlumnoService {
    static final long TAMANO_MAXIMO = 10L * 1024 * 1024;

    private final AlumnoRepository alumnoRepository;
    private final AlumnoDocumentoRepository documentoRepository;
    private final ArchivoRepository archivoRepository;
    private final AlmacenamientoArchivo almacenamiento;

    @Override
    public DocumentoAlumnoResponse agregar(Long alumnoId, TipoDocumentoAlumno tipo,
                                            String descripcion, LocalDate fechaDocumento,
                                            LocalDate vigenteHasta, MultipartFile archivoRecibido) {
        if (tipo == null) throw new ReglaNegocioException("Selecciona el tipo de documento");
        if (vigenteHasta != null && fechaDocumento != null && vigenteHasta.isBefore(fechaDocumento)) {
            throw new ReglaNegocioException("La vigencia no puede ser anterior a la fecha del documento");
        }
        ArchivoValidado validado = validar(archivoRecibido);
        Alumno alumno = buscarConBloqueo(alumnoId);
        if (!alumno.isActivo()) {
            throw new ReglaNegocioException("No se pueden agregar documentos a un alumno inactivo");
        }
        String clave = alumno.getInstitucion().getId() + "/alumnos/" + alumnoId
                + "/documentos/" + UUID.randomUUID() + ".bin";
        try (InputStream contenido = archivoRecibido.getInputStream()) {
            almacenamiento.guardar(clave, contenido);
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer el documento seleccionado");
        }
        try {
            Archivo archivo = new Archivo();
            archivo.setInstitucion(alumno.getInstitucion());
            archivo.setClaveAlmacenamiento(clave);
            archivo.setNombreOriginal(nombreSeguro(archivoRecibido.getOriginalFilename(), validado.extension()));
            archivo.setTipoMime(validado.tipoMime());
            archivo.setTamanoBytes(archivoRecibido.getSize());
            archivo.setChecksumSha256(checksum(archivoRecibido));
            archivo.setEstado(EstadoArchivo.DISPONIBLE);
            archivo = archivoRepository.saveAndFlush(archivo);

            AlumnoDocumento documento = new AlumnoDocumento();
            documento.setAlumno(alumno);
            documento.setArchivo(archivo);
            documento.setTipo(tipo);
            documento.setDescripcion(limpiar(descripcion));
            documento.setFechaDocumento(fechaDocumento);
            documento.setVigenteHasta(vigenteHasta);
            return respuesta(documentoRepository.saveAndFlush(documento));
        } catch (RuntimeException excepcion) {
            almacenamiento.eliminarSiExiste(clave);
            throw excepcion;
        }
    }

    @Override
    public void retirar(Long alumnoId, Long documentoId, Long version) {
        buscarConBloqueo(alumnoId);
        AlumnoDocumento documento = documentoRepository.findByIdAndAlumnoId(documentoId, alumnoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el documento", documentoId));
        verificar(documento, version, "Documento del alumno");
        if (documento.getRetiradoEn() != null) {
            throw new ReglaNegocioException("El documento ya fue retirado del expediente vigente");
        }
        documento.setRetiradoEn(Instant.now());
        documentoRepository.saveAndFlush(documento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoAlumnoResponse> historial(Long alumnoId) {
        buscar(alumnoId);
        return documentoRepository.findAllByAlumnoIdOrderByCreadoEnDesc(alumnoId).stream()
                .map(this::respuesta).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoDescarga descargar(Long alumnoId, Long documentoId) {
        buscar(alumnoId);
        AlumnoDocumento documento = documentoRepository.findByIdAndAlumnoId(documentoId, alumnoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el documento", documentoId));
        Archivo archivo = documento.getArchivo();
        if (archivo.getEstado() != EstadoArchivo.DISPONIBLE) {
            throw new ReglaNegocioException("El documento no está disponible");
        }
        return new ArchivoDescarga(almacenamiento.abrir(archivo.getClaveAlmacenamiento()),
                archivo.getNombreOriginal(), archivo.getTipoMime(), archivo.getTamanoBytes());
    }

    private ArchivoValidado validar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty() || archivo.getSize() <= 0) {
            throw new ReglaNegocioException("Selecciona un documento");
        }
        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new ReglaNegocioException("El documento no puede superar 10 MB");
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
            throw new ReglaNegocioException("No fue posible leer el documento seleccionado");
        }
        throw new ReglaNegocioException("El documento debe ser un PDF, JPEG o PNG válido");
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
            throw new ReglaNegocioException("No fue posible verificar el documento seleccionado");
        }
    }

    private String nombreSeguro(String original, String extension) {
        String normalizado = original == null ? "documento." + extension : original.replace('\\', '/');
        String nombre = normalizado.substring(normalizado.lastIndexOf('/') + 1)
                .replaceAll("[\\p{Cntrl}]", "").trim();
        if (nombre.isBlank()) nombre = "documento." + extension;
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

    private DocumentoAlumnoResponse respuesta(AlumnoDocumento documento) {
        Archivo archivo = documento.getArchivo();
        return new DocumentoAlumnoResponse(documento.getId(), documento.getTipo(),
                documento.getTipo().getEtiqueta(), documento.getDescripcion(),
                documento.getFechaDocumento(), documento.getVigenteHasta(),
                archivo.getNombreOriginal(), archivo.getTipoMime(), archivo.getTamanoBytes(),
                documento.getCreadoEn(), documento.getRetiradoEn(), documento.getVersion(),
                documento.getRetiradoEn() == null);
    }

    private record ArchivoValidado(String tipoMime, String extension) {}
}

