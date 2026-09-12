package escuela.tutor.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.tutor.dto.request.TutorRequest;
import escuela.tutor.entity.Tutor;
import escuela.tutor.mapper.TutorMapper;
import escuela.tutor.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TutorServiceImplTest {

    private final TutorRepository repository = mock(TutorRepository.class);
    private final InstitucionRepository institucionRepository = mock(InstitucionRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final TutorServiceImpl service = new TutorServiceImpl(repository,
            institucionRepository, usuarioRepository, new TutorMapper());
    private Institucion institucion;
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        institucion = institucion(1L);
        usuario = new Usuario();
        usuario.setId(20L);
        usuario.setInstitucion(institucion);
        usuario.setUsername("maria.tutora");
        usuario.setEstado(EstadoUsuario.ACTIVO);
        when(institucionRepository.findById(1L)).thenReturn(Optional.of(institucion));
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuario));
        when(repository.saveAndFlush(any(Tutor.class))).thenAnswer(invocacion -> {
            Tutor tutor = invocacion.getArgument(0);
            tutor.setId(10L);
            tutor.setVersion(0L);
            return tutor;
        });
    }

    @Test
    void creaTutorNormalizandoContactoYDomicilio() {
        service.crear(request(1L, 20L, null));

        ArgumentCaptor<Tutor> captor = ArgumentCaptor.forClass(Tutor.class);
        verify(repository).saveAndFlush(captor.capture());
        Tutor tutor = captor.getValue();
        assertThat(tutor.getNombres()).isEqualTo("María");
        assertThat(tutor.getEmail()).isEqualTo("maria@raices.mx");
        assertThat(tutor.getPais()).isEqualTo("MX");
        assertThat(tutor.getUsuario()).isSameAs(usuario);
    }

    @Test
    void permiteTutorSinCuentaDeUsuario() {
        service.crear(request(1L, null, null));

        ArgumentCaptor<Tutor> captor = ArgumentCaptor.forClass(Tutor.class);
        verify(repository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getUsuario()).isNull();
    }

    @Test
    void rechazaUsuarioDeOtraInstitucion() {
        usuario.setInstitucion(institucion(2L));

        assertThatThrownBy(() -> service.crear(request(1L, 20L, null)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("misma institución");
    }

    @Test
    void rechazaCuentaYaVinculadaAOtroTutor() {
        when(repository.existsByUsuarioIdAndIdNot(20L, 0L)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request(1L, 20L, null)))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("otro tutor");
    }

    @Test
    void rechazaUsuarioInactivo() {
        usuario.setEstado(EstadoUsuario.INACTIVO);

        assertThatThrownBy(() -> service.crear(request(1L, 20L, null)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("usuario inactivo");
    }

    @Test
    void rechazaFechaNacimientoFutura() {
        TutorRequest request = request(1L, null, null);
        TutorRequest futuro = new TutorRequest(request.institucionId(), request.usuarioId(),
                request.nombres(), request.primerApellido(), request.segundoApellido(),
                request.telefonoPrincipal(), request.telefonoSecundario(), request.email(),
                LocalDate.now().plusDays(1), request.calle(), request.numeroExterior(),
                request.numeroInterior(), request.colonia(), request.ciudad(), request.estado(),
                request.codigoPostal(), request.pais(), request.ocupacion(), request.lugarTrabajo(),
                request.telefonoTrabajo(), request.activo(), request.version());

        assertThatThrownBy(() -> service.crear(futuro))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no puede ser futura");
    }

    @Test
    void noPermiteCambiarInstitucion() {
        Tutor tutor = existente();
        when(repository.findById(10L)).thenReturn(Optional.of(tutor));

        assertThatThrownBy(() -> service.actualizar(10L, request(2L, null, 3L)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("institución");
    }

    @Test
    void desactivaSinEliminar() {
        Tutor tutor = existente();
        when(repository.findById(10L)).thenReturn(Optional.of(tutor));

        service.desactivar(10L, 3L);

        assertThat(tutor.isActivo()).isFalse();
        verify(repository, never()).delete(any(Tutor.class));
    }

    private TutorRequest request(Long institucionId, Long usuarioId, Long version) {
        return new TutorRequest(institucionId, usuarioId, " María ", " López ", null,
                "3312345678", null, " Maria@Raices.MX ", LocalDate.of(1990, 5, 10),
                "Juárez", "10", null, "Centro", "Guadalajara", "Jalisco", "44100",
                "mx", "Docente", "Instituto", null, true, version);
    }

    private Tutor existente() {
        Tutor tutor = new Tutor();
        tutor.setId(10L);
        tutor.setVersion(3L);
        tutor.setInstitucion(institucion);
        tutor.setActivo(true);
        return tutor;
    }

    private Institucion institucion(Long id) {
        Institucion valor = new Institucion();
        valor.setId(id);
        valor.setActivo(true);
        return valor;
    }
}
