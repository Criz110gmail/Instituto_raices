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
import escuela.archivo.imagen.ProcesadorFotografia;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FotografiaAlumnoServiceImpl implements FotografiaAlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final AlumnoFotografiaRepository fotografiaRepository;
    private final ArchivoRepository archivoRepository;
    private final AlmacenamientoArchivo almacenamiento;
    private final ProcesadorFotografia procesadorFotografia;

    @Override
    public FotografiaAlumnoResponse asignar(Long alumnoId, MultipartFile fotografia) {
        var imagen = procesadorFotografia.procesar(fotografia);
        Alumno alumno = buscarConBloqueo(alumnoId);
        if (!alumno.isActivo()) {
            throw new ReglaNegocioException("No se puede cambiar la fotografía de un alumno inactivo");
        }
        String clave = alumno.getInstitucion().getId() + "/alumnos/" + alumnoId + "/"
                + UUID.randomUUID() + ".bin";
        try (InputStream contenido = new ByteArrayInputStream(imagen.contenido())) {
            almacenamiento.guardar(clave, contenido);
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer la fotografía seleccionada");
        }

        try {
            Archivo archivo = new Archivo();
            archivo.setInstitucion(alumno.getInstitucion());
            archivo.setClaveAlmacenamiento(clave);
            archivo.setNombreOriginal(imagen.nombreArchivo());
            archivo.setTipoMime(imagen.tipoMime());
            archivo.setTamanoBytes(imagen.tamanoBytes());
            archivo.setChecksumSha256(imagen.checksumSha256());
            archivo.setEstado(EstadoArchivo.DISPONIBLE);
            archivo = archivoRepository.saveAndFlush(archivo);

            fotografiaRepository.findFirstByAlumnoIdAndRetiradaEnIsNull(alumnoId)
                    .ifPresent(anterior -> {
                        anterior.setRetiradaEn(Instant.now());
                        fotografiaRepository.saveAndFlush(anterior);
                    });

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

}
