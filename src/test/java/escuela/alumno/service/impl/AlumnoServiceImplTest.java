package escuela.alumno.service.impl;

import escuela.alumno.dto.request.AlumnoRequest;
import escuela.alumno.entity.Alumno;
import escuela.alumno.mapper.AlumnoMapper;
import escuela.alumno.repository.AlumnoRepository;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
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

class AlumnoServiceImplTest {

    private final AlumnoRepository repository = mock(AlumnoRepository.class);
    private final InstitucionRepository institucionRepository = mock(InstitucionRepository.class);
    private final AlumnoServiceImpl service = new AlumnoServiceImpl(
            repository, institucionRepository, new AlumnoMapper());
    private Institucion institucion;

    @BeforeEach
    void preparar() {
        institucion = new Institucion();
        institucion.setId(1L);
        institucion.setActivo(true);
        when(institucionRepository.findById(1L)).thenReturn(Optional.of(institucion));
        when(repository.saveAndFlush(any(Alumno.class))).thenAnswer(invocacion -> {
            Alumno alumno = invocacion.getArgument(0);
            alumno.setId(10L);
            alumno.setVersion(0L);
            return alumno;
        });
    }

    @Test
    void creaAlumnoNormalizandoMatriculaCurpYCorreo() {
        service.crear(request(1L, null, " alu-001 ", "abcd1234efgh567890"));

        ArgumentCaptor<Alumno> captor = ArgumentCaptor.forClass(Alumno.class);
        verify(repository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getMatricula()).isEqualTo("ALU-001");
        assertThat(captor.getValue().getCurp()).isEqualTo("ABCD1234EFGH567890");
        assertThat(captor.getValue().getEmail()).isEqualTo("alumno@raices.mx");
    }

    @Test
    void rechazaMatriculaDuplicadaEnLaInstitucion() {
        when(repository.existsByInstitucionIdAndMatriculaIgnoreCaseAndIdNot(
                1L, "ALU-001", 0L)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request(1L, null, "ALU-001", null)))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("matrícula");
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void rechazaCurpDuplicadaEnLaInstitucion() {
        when(repository.existsByInstitucionIdAndCurpIgnoreCaseAndIdNot(
                1L, "ABCD1234EFGH567890", 0L)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(
                request(1L, null, "ALU-002", "ABCD1234EFGH567890")))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("CURP");
    }

    @Test
    void noPermiteCambiarLaInstitucion() {
        Alumno alumno = alumnoExistente();
        when(repository.findById(10L)).thenReturn(Optional.of(alumno));

        assertThatThrownBy(() -> service.actualizar(
                10L, request(2L, 3L, "ALU-001", null)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("institución");
    }

    @Test
    void rechazaIngresoAnteriorAlNacimiento() {
        AlumnoRequest invalido = new AlumnoRequest(1L, "ALU-001", "Ana", "Pérez",
                null, null, LocalDate.of(2018, 1, 1), null, null, null, null,
                null, null, null, null, null, null, null, null, "MX",
                LocalDate.of(2017, 1, 1), null, true, null);

        assertThatThrownBy(() -> service.crear(invalido))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("anterior al nacimiento");
    }

    @Test
    void desactivaSinEliminarElExpediente() {
        Alumno alumno = alumnoExistente();
        when(repository.findById(10L)).thenReturn(Optional.of(alumno));

        service.desactivar(10L, 3L);

        assertThat(alumno.isActivo()).isFalse();
        verify(repository, never()).delete(any(Alumno.class));
    }

    private AlumnoRequest request(Long institucionId, Long version, String matricula, String curp) {
        return new AlumnoRequest(institucionId, matricula, " Ana Lucía ", " Pérez ",
                " López ", curp, LocalDate.of(2018, 1, 1), "Femenino", "Guadalajara",
                "Mexicana", "3312345678", " Alumno@Raices.MX ", "Juárez", "10",
                null, "Centro", "Guadalajara", "Jalisco", "44100", "mx",
                LocalDate.of(2024, 8, 20), null, true, version);
    }

    private Alumno alumnoExistente() {
        Alumno alumno = new Alumno();
        alumno.setId(10L);
        alumno.setVersion(3L);
        alumno.setInstitucion(institucion);
        alumno.setActivo(true);
        return alumno;
    }
}
