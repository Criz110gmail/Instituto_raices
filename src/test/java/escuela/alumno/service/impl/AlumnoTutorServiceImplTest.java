package escuela.alumno.service.impl;

import escuela.alumno.dto.request.AlumnoTutorRequest;
import escuela.alumno.entity.Alumno;
import escuela.alumno.entity.AlumnoTutor;
import escuela.alumno.entity.ParentescoTutor;
import escuela.alumno.mapper.AlumnoTutorMapper;
import escuela.alumno.repository.AlumnoRepository;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlumnoTutorServiceImplTest {

    private final AlumnoTutorRepository repository = mock(AlumnoTutorRepository.class);
    private final AlumnoRepository alumnoRepository = mock(AlumnoRepository.class);
    private final TutorRepository tutorRepository = mock(TutorRepository.class);
    private final AlumnoTutorServiceImpl service = new AlumnoTutorServiceImpl(repository,
            alumnoRepository, tutorRepository, new AlumnoTutorMapper());
    private Alumno alumno;
    private Tutor tutor;

    @BeforeEach
    void preparar() {
        Institucion institucion = institucion(1L);
        alumno = alumno(10L, institucion);
        tutor = tutor(20L, institucion);
        when(alumnoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(alumno));
        when(tutorRepository.findById(20L)).thenReturn(Optional.of(tutor));
        when(repository.saveAndFlush(any(AlumnoTutor.class))).thenAnswer(invocacion -> {
            AlumnoTutor vinculo = invocacion.getArgument(0);
            vinculo.setId(30L);
            vinculo.setVersion(0L);
            return vinculo;
        });
    }

    @Test
    void creaVinculoConAutorizacionesYNormalizaParentescoOtro() {
        AlumnoTutorRequest request = request(ParentescoTutor.OTRO, " Abuela ", true,
                LocalDate.of(2026, 1, 1), null, null);

        service.crear(request);

        ArgumentCaptor<AlumnoTutor> captor = ArgumentCaptor.forClass(AlumnoTutor.class);
        verify(repository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getParentescoOtro()).isEqualTo("Abuela");
        assertThat(captor.getValue().isContactoPrincipal()).isTrue();
        assertThat(captor.getValue().isPuedeRecoger()).isTrue();
    }

    @Test
    void rechazaAlumnoYTutorDeInstitucionesDistintas() {
        tutor.setInstitucion(institucion(2L));

        assertThatThrownBy(() -> service.crear(request(ParentescoTutor.MADRE, null,
                false, LocalDate.now(), null, null)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("misma institución");
    }

    @Test
    void rechazaVinculoActivoConPersonaInactiva() {
        tutor.setActivo(false);

        assertThatThrownBy(() -> service.crear(request(ParentescoTutor.PADRE, null,
                false, LocalDate.now(), null, null)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("deben estar activos");
    }

    @Test
    void exigeDescripcionParaParentescoOtro() {
        assertThatThrownBy(() -> service.crear(request(ParentescoTutor.OTRO, " ",
                false, LocalDate.now(), null, null)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Especifica el parentesco");
    }

    @Test
    void rechazaFechaFinAnteriorAlInicio() {
        assertThatThrownBy(() -> service.crear(request(ParentescoTutor.TUTOR_LEGAL, null,
                false, LocalDate.of(2026, 5, 2), LocalDate.of(2026, 5, 1), null)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("fecha de fin");
    }

    @Test
    void rechazaPeriodosSuperpuestosParaLaMismaPareja() {
        AlumnoTutor existente = existente(31L, alumno, tutor,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), false);
        when(repository.findAllByAlumnoIdAndTutorIdAndActivoTrueAndIdNot(10L, 20L, 0L))
                .thenReturn(List.of(existente));

        assertThatThrownBy(() -> service.crear(request(ParentescoTutor.MADRE, null,
                false, LocalDate.of(2026, 6, 1), null, null)))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Ya existe un vínculo");
    }

    @Test
    void rechazaDosContactosPrincipalesSuperpuestos() {
        AlumnoTutor existente = existente(31L, alumno, tutor(21L, alumno.getInstitucion()),
                LocalDate.of(2026, 1, 1), null, true);
        when(repository.findAllByAlumnoIdAndContactoPrincipalTrueAndActivoTrueAndIdNot(10L, 0L))
                .thenReturn(List.of(existente));

        assertThatThrownBy(() -> service.crear(request(ParentescoTutor.PADRE, null,
                true, LocalDate.of(2026, 2, 1), null, null)))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("contacto principal");
    }

    @Test
    void permitePeriodosNoSuperpuestos() {
        AlumnoTutor anterior = existente(31L, alumno, tutor,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), false);
        when(repository.findAllByAlumnoIdAndTutorIdAndActivoTrueAndIdNot(10L, 20L, 0L))
                .thenReturn(List.of(anterior));

        service.crear(request(ParentescoTutor.MADRE, null, false,
                LocalDate.of(2026, 1, 1), null, null));

        verify(repository).saveAndFlush(any(AlumnoTutor.class));
    }

    @Test
    void noPermiteReasignarPersonasEnEdicion() {
        AlumnoTutor existente = existente(30L, alumno, tutor, LocalDate.now(), null, false);
        existente.setVersion(4L);
        when(repository.findById(30L)).thenReturn(Optional.of(existente));
        AlumnoTutorRequest request = new AlumnoTutorRequest(11L, 20L,
                ParentescoTutor.MADRE, null, false, false, false, false, false,
                true, LocalDate.now(), null, null, true, 4L);

        assertThatThrownBy(() -> service.actualizar(30L, request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("No se puede cambiar");
    }

    @Test
    void revocaSinEliminarElHistorial() {
        AlumnoTutor existente = existente(30L, alumno, tutor, LocalDate.now(), null, false);
        existente.setVersion(4L);
        when(repository.findById(30L)).thenReturn(Optional.of(existente));

        service.desactivar(30L, 4L);

        assertThat(existente.isActivo()).isFalse();
        verify(repository, never()).delete(any(AlumnoTutor.class));
    }

    private AlumnoTutorRequest request(ParentescoTutor parentesco, String otro,
                                       boolean principal, LocalDate inicio,
                                       LocalDate fin, Long version) {
        return new AlumnoTutorRequest(10L, 20L, parentesco, otro, principal, true,
                true, true, true, true, inicio, fin, " Referencia ", true, version);
    }

    private AlumnoTutor existente(Long id, Alumno alumno, Tutor tutor, LocalDate inicio,
                                  LocalDate fin, boolean principal) {
        AlumnoTutor vinculo = new AlumnoTutor();
        vinculo.setId(id);
        vinculo.setAlumno(alumno);
        vinculo.setTutor(tutor);
        vinculo.setFechaInicio(inicio);
        vinculo.setFechaFin(fin);
        vinculo.setContactoPrincipal(principal);
        vinculo.setActivo(true);
        return vinculo;
    }

    private Alumno alumno(Long id, Institucion institucion) {
        Alumno valor = new Alumno();
        valor.setId(id);
        valor.setInstitucion(institucion);
        valor.setMatricula("A-001");
        valor.setNombres("Ana");
        valor.setPrimerApellido("López");
        valor.setActivo(true);
        return valor;
    }

    private Tutor tutor(Long id, Institucion institucion) {
        Tutor valor = new Tutor();
        valor.setId(id);
        valor.setInstitucion(institucion);
        valor.setNombres("María");
        valor.setPrimerApellido("López");
        valor.setActivo(true);
        return valor;
    }

    private Institucion institucion(Long id) {
        Institucion valor = new Institucion();
        valor.setId(id);
        valor.setActivo(true);
        return valor;
    }
}
