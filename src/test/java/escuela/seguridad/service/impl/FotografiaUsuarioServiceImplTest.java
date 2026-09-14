package escuela.seguridad.service.impl;

import escuela.archivo.entity.Archivo;
import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FotografiaUsuarioServiceImplTest {

    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final ArchivoRepository archivoRepository = mock(ArchivoRepository.class);
    private final AlmacenamientoArchivo almacenamiento = mock(AlmacenamientoArchivo.class);
    private final FotografiaUsuarioServiceImpl service = new FotografiaUsuarioServiceImpl(
            usuarioRepository, archivoRepository, almacenamiento);
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        usuario = new Usuario();
        usuario.setId(7L);
        usuario.setInstitucion(institucion);
        when(usuarioRepository.buscarPorIdConBloqueo(7L)).thenReturn(Optional.of(usuario));
        when(archivoRepository.saveAndFlush(any(Archivo.class))).thenAnswer(invocacion -> {
            Archivo archivo = invocacion.getArgument(0);
            archivo.setId(20L);
            return archivo;
        });
    }

    @Test
    void guardaFotografiaPrivadaYLaAsignaAlUsuario() throws Exception {
        service.asignar(7L, png("perfil.png"));

        var captor = org.mockito.ArgumentCaptor.forClass(Archivo.class);
        verify(archivoRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getTipoMime()).isEqualTo("image/png");
        assertThat(captor.getValue().getChecksumSha256()).hasSize(64);
        assertThat(usuario.getFotografiaArchivo()).isSameAs(captor.getValue());
        verify(almacenamiento).guardar(any(), any());
    }

    @Test
    void reemplazaLaAnteriorMarcandolaComoRetirada() throws Exception {
        Archivo anterior = new Archivo();
        anterior.setEstado(EstadoArchivo.DISPONIBLE);
        usuario.setFotografiaArchivo(anterior);

        service.asignar(7L, png("nueva.png"));

        assertThat(anterior.getEstado()).isEqualTo(EstadoArchivo.RETIRADO);
        assertThat(usuario.getFotografiaArchivo()).isNotSameAs(anterior);
        verify(almacenamiento, never()).eliminarSiExiste(any());
    }

    @Test
    void restauraAvatarGenericoSinBorrarElArchivoPrivado() {
        Archivo anterior = new Archivo();
        anterior.setEstado(EstadoArchivo.DISPONIBLE);
        usuario.setFotografiaArchivo(anterior);

        service.retirar(7L);

        assertThat(usuario.getFotografiaArchivo()).isNull();
        assertThat(anterior.getEstado()).isEqualTo(EstadoArchivo.RETIRADO);
        verify(almacenamiento, never()).eliminarSiExiste(any());
    }

    @Test
    void rechazaUnArchivoQueNoEsImagenReal() {
        MockMultipartFile falsa = new MockMultipartFile("archivo", "perfil.png", "image/png",
                "contenido inválido".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        assertThatThrownBy(() -> service.asignar(7L, falsa))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("JPEG o PNG");
        verify(almacenamiento, never()).guardar(any(), any());
    }

    private MockMultipartFile png(String nombre) throws Exception {
        BufferedImage imagen = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        ImageIO.write(imagen, "png", salida);
        return new MockMultipartFile("archivo", nombre, "image/png", salida.toByteArray());
    }
}
