package escuela.seguridad.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.dto.request.UsuarioRequest;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.mapper.UsuarioMapper;
import escuela.seguridad.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsuarioServiceImplTest {

    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final InstitucionRepository institucionRepository = mock(InstitucionRepository.class);
    private final UsuarioServiceImpl service = new UsuarioServiceImpl(
            repository, institucionRepository, new UsuarioMapper());
    private Institucion institucion;

    @BeforeEach
    void preparar() {
        institucion = new Institucion();
        institucion.setId(1L);
        institucion.setActivo(true);
        when(institucionRepository.findById(1L)).thenReturn(Optional.of(institucion));
    }

    @Test
    void rechazaUsernameDuplicadoDentroDeLaInstitucion() {
        when(repository.existsByInstitucionIdAndUsernameIgnoreCaseAndIdNot(1L, "admin", 0L))
                .thenReturn(true);

        assertThatThrownBy(() -> service.crearInvitado(
                new UsuarioRequest(1L, "admin", "admin@escuela.mx", null)))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("ese nombre");
    }

    @Test
    void noActivaUsuarioSinPasswordConfigurado() {
        Usuario usuario = new Usuario();
        usuario.setId(8L);
        usuario.setVersion(2L);
        usuario.setInstitucion(institucion);
        usuario.setEstado(EstadoUsuario.INVITADO);
        when(repository.findById(8L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> service.cambiarEstado(8L, 2L, EstadoUsuario.ACTIVO))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("aceptar su invitación");
    }
}
