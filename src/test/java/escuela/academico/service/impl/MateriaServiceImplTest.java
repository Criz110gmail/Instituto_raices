package escuela.academico.service.impl;

import escuela.academico.dto.request.MateriaGradoRequest;
import escuela.academico.dto.request.MateriaRequest;
import escuela.academico.entity.Grado;
import escuela.academico.entity.Materia;
import escuela.academico.entity.MateriaGrado;
import escuela.academico.entity.NivelEducativo;
import escuela.academico.entity.TipoEvaluacion;
import escuela.academico.mapper.MateriaMapper;
import escuela.academico.repository.GradoRepository;
import escuela.academico.repository.MateriaGradoRepository;
import escuela.academico.repository.MateriaRepository;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MateriaServiceImplTest {
    private final MateriaRepository repository = mock(MateriaRepository.class);
    private final MateriaGradoRepository planRepository = mock(MateriaGradoRepository.class);
    private final InstitucionRepository institucionRepository = mock(InstitucionRepository.class);
    private final GradoRepository gradoRepository = mock(GradoRepository.class);
    private final EntityManager entityManager = mock(EntityManager.class);
    private final MateriaServiceImpl service = new MateriaServiceImpl(repository, planRepository,
            institucionRepository, gradoRepository, new MateriaMapper(), entityManager);
    private Institucion institucion;
    private Materia materia;

    @BeforeEach
    void preparar() {
        institucion = new Institucion(); institucion.setId(1L); institucion.setActivo(true);
        materia = new Materia(); materia.setId(10L); materia.setInstitucion(institucion);
        materia.setCodigo("MAT"); materia.setNombre("Matemáticas"); materia.setActivo(true);
        when(institucionRepository.findById(1L)).thenReturn(Optional.of(institucion));
        when(repository.findById(10L)).thenReturn(Optional.of(materia));
        when(repository.saveAndFlush(any(Materia.class))).thenAnswer(invocacion -> {
            Materia guardada = invocacion.getArgument(0); guardada.setId(10L); guardada.setVersion(0L); return guardada;
        });
        when(planRepository.saveAndFlush(any(MateriaGrado.class))).thenAnswer(invocacion -> {
            MateriaGrado plan = invocacion.getArgument(0); plan.setId(20L); plan.setVersion(0L); return plan;
        });
    }

    @Test
    void creaMateriaNormalizandoCodigo() {
        var respuesta = service.crear(new MateriaRequest(1L, " mat ", "Matemáticas",
                "Pensamiento matemático", true, null));

        assertThat(respuesta.codigo()).isEqualTo("MAT");
        assertThat(respuesta.institucionId()).isEqualTo(1L);
    }

    @Test
    void impideCodigoDuplicadoEnInstitucion() {
        when(repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(1L, "MAT", 0L))
                .thenReturn(true);

        assertThatThrownBy(() -> service.crear(new MateriaRequest(1L, "MAT", "Matemáticas",
                null, true, null))).isInstanceOf(RecursoDuplicadoException.class);
    }

    @Test
    void configuraEscalaNumericaPorGrado() {
        Grado grado = grado(institucion);
        when(gradoRepository.findById(5L)).thenReturn(Optional.of(grado));

        var respuesta = service.asignarGrado(10L, planNumerico());

        assertThat(respuesta.tipoEvaluacion()).isEqualTo(TipoEvaluacion.NUMERICA);
        assertThat(respuesta.escalaMaxima()).isEqualByComparingTo("10");
        assertThat(respuesta.minimaAprobatoria()).isEqualByComparingTo("6");
    }

    @Test
    void rechazaEscalaIncoherente() {
        Grado grado = grado(institucion);
        when(gradoRepository.findById(5L)).thenReturn(Optional.of(grado));
        MateriaGradoRequest request = new MateriaGradoRequest(5L, TipoEvaluacion.NUMERICA,
                BigDecimal.TEN, BigDecimal.ZERO, new BigDecimal("6"), 1, null, true, null);

        assertThatThrownBy(() -> service.asignarGrado(10L, request))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("escala");
    }

    @Test
    void impideAsignarGradoDeOtraInstitucion() {
        Institucion otra = new Institucion(); otra.setId(2L); otra.setActivo(true);
        when(gradoRepository.findById(5L)).thenReturn(Optional.of(grado(otra)));

        assertThatThrownBy(() -> service.asignarGrado(10L, planNumerico()))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("misma institución");
    }

    private Grado grado(Institucion propietaria) {
        NivelEducativo nivel = new NivelEducativo(); nivel.setId(3L); nivel.setNombre("Primaria");
        nivel.setInstitucion(propietaria); nivel.setActivo(true);
        Grado grado = new Grado(); grado.setId(5L); grado.setNombre("Primero");
        grado.setNivelEducativo(nivel); grado.setActivo(true); return grado;
    }

    private MateriaGradoRequest planNumerico() {
        return new MateriaGradoRequest(5L, TipoEvaluacion.NUMERICA, BigDecimal.ZERO,
                BigDecimal.TEN, new BigDecimal("6"), 1, new BigDecimal("5"), true, null);
    }
}
