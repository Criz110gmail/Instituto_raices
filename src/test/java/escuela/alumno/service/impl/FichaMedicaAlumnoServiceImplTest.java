package escuela.alumno.service.impl;

import escuela.alumno.dto.request.FichaMedicaAlumnoRequest;
import escuela.alumno.entity.Alumno;
import escuela.alumno.entity.FichaMedicaAlumno;
import escuela.alumno.entity.TipoSanguineo;
import escuela.alumno.repository.AlumnoRepository;
import escuela.alumno.repository.FichaMedicaAlumnoRepository;
import escuela.common.exception.ConflictoVersionException;
import escuela.common.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FichaMedicaAlumnoServiceImplTest {
    private final AlumnoRepository alumnoRepository = mock(AlumnoRepository.class);
    private final FichaMedicaAlumnoRepository repository = mock(FichaMedicaAlumnoRepository.class);
    private final FichaMedicaAlumnoServiceImpl service =
            new FichaMedicaAlumnoServiceImpl(alumnoRepository, repository);

    @Test
    void creaFichaUnicaNormalizandoTextos() {
        Alumno alumno = alumno(true);
        when(alumnoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(alumno));
        when(repository.findByAlumnoId(10L)).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(FichaMedicaAlumno.class))).thenAnswer(invocacion -> {
            FichaMedicaAlumno ficha = invocacion.getArgument(0);
            ficha.setId(20L); ficha.setVersion(0L); return ficha;
        });

        var respuesta = service.guardar(10L, request("  Penicilina  ", null));

        assertThat(respuesta.tipoSanguineo()).isEqualTo(TipoSanguineo.O_POSITIVO);
        assertThat(respuesta.alergias()).isEqualTo("Penicilina");
        assertThat(respuesta.autorizaAtencionEmergencia()).isTrue();
    }

    @Test
    void protegeActualizacionConVersionOptimista() {
        Alumno alumno = alumno(true);
        FichaMedicaAlumno ficha = new FichaMedicaAlumno();
        ficha.setId(20L); ficha.setVersion(3L); ficha.setAlumno(alumno);
        when(alumnoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(alumno));
        when(repository.findByAlumnoId(10L)).thenReturn(Optional.of(ficha));

        assertThatThrownBy(() -> service.guardar(10L, request(null, 2L)))
                .isInstanceOf(ConflictoVersionException.class);
    }

    @Test
    void impideModificarFichaDeAlumnoInactivo() {
        when(alumnoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(alumno(false)));

        assertThatThrownBy(() -> service.guardar(10L, request(null, null)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("inactivo");
    }

    private Alumno alumno(boolean activo) {
        Alumno alumno = new Alumno(); alumno.setId(10L); alumno.setActivo(activo); return alumno;
    }

    private FichaMedicaAlumnoRequest request(String alergias, Long version) {
        return new FichaMedicaAlumnoRequest(TipoSanguineo.O_POSITIVO, alergias,
                null, null, null, null, null, "IMSS", "123", null,
                "Mamá", "3312345678", null, true, version);
    }
}

