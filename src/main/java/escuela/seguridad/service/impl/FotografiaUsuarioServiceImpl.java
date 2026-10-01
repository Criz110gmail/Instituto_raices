package escuela.seguridad.service.impl;

import escuela.archivo.dto.ArchivoDescarga;
import escuela.archivo.entity.Archivo;
import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.imagen.ProcesadorFotografia;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FotografiaUsuarioServiceImpl implements FotografiaUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ArchivoRepository archivoRepository;
    private final AlmacenamientoArchivo almacenamiento;
    private final ProcesadorFotografia procesadorFotografia;

    @Override
    public void asignar(Long usuarioId, MultipartFile fotografia) {
        var imagen = procesadorFotografia.procesar(fotografia);
        Usuario usuario = buscarConBloqueo(usuarioId);
        String clave = usuario.getInstitucion().getId() + "/usuarios/" + usuarioId + "/"
                + UUID.randomUUID() + ".bin";
        try (InputStream contenido = new ByteArrayInputStream(imagen.contenido())) {
            almacenamiento.guardar(clave, contenido);
        } catch (IOException excepcion) {
            throw new ReglaNegocioException("No fue posible leer la fotografía seleccionada");
        }

        try {
            Archivo archivo = new Archivo();
            archivo.setInstitucion(usuario.getInstitucion());
            archivo.setClaveAlmacenamiento(clave);
            archivo.setNombreOriginal(imagen.nombreArchivo());
            archivo.setTipoMime(imagen.tipoMime());
            archivo.setTamanoBytes(imagen.tamanoBytes());
            archivo.setChecksumSha256(imagen.checksumSha256());
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

}
