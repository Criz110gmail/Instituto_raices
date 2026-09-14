package escuela.cobranza.service.impl;

import escuela.cobranza.dto.request.ConceptoCobroRequest;
import escuela.cobranza.entity.CategoriaConceptoCobro;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.EstadoCuota;
import escuela.cobranza.mapper.ConceptoCobroMapper;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.cobranza.repository.CuotaAlumnoRepository;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConceptoCobroServiceImplTest {

    private final ConceptoCobroRepository repository = mock(ConceptoCobroRepository.class);
    private final CuotaAlumnoRepository cuotaRepository = mock(CuotaAlumnoRepository.class);
    private final InstitucionRepository institucionRepository = mock(InstitucionRepository.class);
    private final ConceptoCobroServiceImpl service = new ConceptoCobroServiceImpl(
            repository, cuotaRepository, institucionRepository, new ConceptoCobroMapper());

    @Test
    void creaConceptoNormalizadoSinPrecioGlobal() {
        Institucion institucion = institucion(1L);
        when(institucionRepository.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(institucion));
        when(repository.saveAndFlush(any())).thenAnswer(invocacion -> {
            ConceptoCobro concepto = invocacion.getArgument(0);
            concepto.setId(10L);
            concepto.setVersion(0L);
            return concepto;
        });

        var respuesta = service.crear(request(" coleg-mensual ", true));

        assertThat(respuesta.codigo()).isEqualTo("COLEG-MENSUAL");
        assertThat(respuesta.nombre()).isEqualTo("Colegiatura mensual");
        assertThat(respuesta.permiteBeca()).isTrue();
    }

    @Test
    void rechazaCodigoDuplicadoEnLaInstitucion() {
        when(institucionRepository.buscarPorIdConBloqueo(1L))
                .thenReturn(Optional.of(institucion(1L)));
        when(repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(
                1L, "COLEG-MENSUAL", 0L)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request("COLEG-MENSUAL", false)))
                .isInstanceOf(RecursoDuplicadoException.class);
    }

    @Test
    void noDesactivaConceptoConCuotasActivas() {
        ConceptoCobro concepto = new ConceptoCobro();
        concepto.setId(10L);
        concepto.setVersion(2L);
        concepto.setInstitucion(institucion(1L));
        concepto.setActivo(true);
        when(repository.findById(10L)).thenReturn(Optional.of(concepto));
        when(cuotaRepository.existsByConceptoCobroIdAndEstado(10L, EstadoCuota.ACTIVA))
                .thenReturn(true);

        assertThatThrownBy(() -> service.desactivar(10L, 2L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("cuotas activas");
    }

    private ConceptoCobroRequest request(String codigo, boolean permiteBeca) {
        return new ConceptoCobroRequest(1L, codigo, " Colegiatura mensual ", null,
                CategoriaConceptoCobro.COLEGIATURA, permiteBeca, true, false, true, null);
    }

    private Institucion institucion(Long id) {
        Institucion institucion = new Institucion();
        institucion.setId(id);
        institucion.setNombre("Institución");
        institucion.setActivo(true);
        return institucion;
    }
}
