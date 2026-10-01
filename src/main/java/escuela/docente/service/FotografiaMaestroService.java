package escuela.docente.service;

import escuela.archivo.dto.ArchivoDescarga;
import escuela.archivo.entity.Archivo;
import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.imagen.ProcesadorFotografia;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.docente.dto.FotografiaMaestroResponse;
import escuela.docente.entity.Maestro;
import escuela.docente.repository.MaestroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FotografiaMaestroService {
    private final MaestroRepository maestros;
    private final ArchivoRepository archivos;
    private final AlmacenamientoArchivo almacenamiento;
    private final ProcesadorFotografia procesadorFotografia;

    public FotografiaMaestroResponse asignar(Long maestroId, MultipartFile foto) {
        var imagen = procesadorFotografia.procesar(foto);
        Maestro maestro = bloquear(maestroId);
        if (!maestro.isActivo()) {
            throw new ReglaNegocioException("No se puede cambiar la fotografía de un maestro inactivo");
        }
        String clave = maestro.getInstitucion().getId() + "/maestros/" + maestroId + "/"
                + UUID.randomUUID() + ".bin";
        try (InputStream contenido = new ByteArrayInputStream(imagen.contenido())) {
            almacenamiento.guardar(clave, contenido);
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer la fotografía optimizada");
        }
        try {
            Archivo anterior = maestro.getFotografiaArchivo();
            Archivo archivo = new Archivo();
            archivo.setInstitucion(maestro.getInstitucion());
            archivo.setClaveAlmacenamiento(clave);
            archivo.setNombreOriginal(imagen.nombreArchivo());
            archivo.setTipoMime(imagen.tipoMime());
            archivo.setTamanoBytes(imagen.tamanoBytes());
            archivo.setChecksumSha256(imagen.checksumSha256());
            archivo.setEstado(EstadoArchivo.DISPONIBLE);
            archivo = archivos.saveAndFlush(archivo);
            maestro.setFotografiaArchivo(archivo);
            maestros.saveAndFlush(maestro);
            if (anterior != null) {
                anterior.setEstado(EstadoArchivo.RETIRADO);
                archivos.save(anterior);
            }
            return respuesta(archivo);
        } catch (RuntimeException excepcion) {
            almacenamiento.eliminarSiExiste(clave);
            throw excepcion;
        }
    }

    public void retirar(Long id) {
        Maestro maestro = bloquear(id);
        Archivo archivo = maestro.getFotografiaArchivo();
        if (archivo == null) throw new ReglaNegocioException("El maestro no tiene una fotografía actual");
        maestro.setFotografiaArchivo(null);
        archivo.setEstado(EstadoArchivo.RETIRADO);
        maestros.saveAndFlush(maestro);
        archivos.save(archivo);
    }

    @Transactional(readOnly = true)
    public FotografiaMaestroResponse actual(Long id) {
        Maestro maestro = buscar(id);
        return maestro.getFotografiaArchivo() == null ? null : respuesta(maestro.getFotografiaArchivo());
    }

    @Transactional(readOnly = true)
    public ArchivoDescarga descargar(Long id) {
        Maestro maestro = buscar(id);
        Archivo archivo = maestro.getFotografiaArchivo();
        if (archivo == null || archivo.getEstado() != EstadoArchivo.DISPONIBLE) {
            throw new RecursoNoEncontradoException("la fotografía del maestro", id);
        }
        return new ArchivoDescarga(almacenamiento.abrir(archivo.getClaveAlmacenamiento()),
                archivo.getNombreOriginal(), archivo.getTipoMime(), archivo.getTamanoBytes());
    }

    private Maestro buscar(Long id) {
        return maestros.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el maestro", id));
    }

    private Maestro bloquear(Long id) {
        return maestros.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el maestro", id));
    }

    private FotografiaMaestroResponse respuesta(Archivo archivo) {
        return new FotografiaMaestroResponse(archivo.getId(), archivo.getNombreOriginal(),
                archivo.getTipoMime(), archivo.getTamanoBytes());
    }
}
