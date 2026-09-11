package escuela.academico.service.impl;

import escuela.academico.dto.request.CicloEscolarRequest;
import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.mapper.CicloEscolarMapper;
import escuela.academico.repository.CicloEscolarRepository;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CicloEscolarServiceImplTest {

    private final CicloEscolarRepository repository = mock(CicloEscolarRepository.class);
    private final PeriodoAcademicoRepository periodoRepository = mock(PeriodoAcademicoRepository.class);
    private final InstitucionRepository institucionRepository = mock(InstitucionRepository.class);
    private CicloEscolarServiceImpl service;
    private Institucion institucion;

    @BeforeEach
    void preparar() {
        institucion = new Institucion();
        institucion.setId(1L);
        institucion.setActivo(true);
        when(institucionRepository.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(institucion));
        when(repository.saveAndFlush(any(CicloEscolar.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        service = new CicloEscolarServiceImpl(
                repository, periodoRepository, institucionRepository, new CicloEscolarMapper());
    }

    @Test
    void sustituyeElCicloPredeterminadoDeFormaControlada() {
        CicloEscolar anterior = new CicloEscolar();
        anterior.setPredeterminado(true);
        when(repository.findByInstitucionIdAndPredeterminadoTrueAndIdNot(1L, 0L))
                .thenReturn(Optional.of(anterior));

        service.crear(request(true, LocalDate.of(2026, 8, 1), LocalDate.of(2027, 7, 31)));

        assertThat(anterior.isPredeterminado()).isFalse();
    }

    @Test
    void rechazaUnRangoDeFechasInvertido() {
        assertThatThrownBy(() -> service.crear(request(false,
                LocalDate.of(2027, 7, 31), LocalDate.of(2026, 8, 1))))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("fecha inicial");
    }

    private CicloEscolarRequest request(boolean predeterminado, LocalDate inicio, LocalDate fin) {
        return new CicloEscolarRequest(1L, "2026-2027", "Ciclo 2026-2027",
                inicio, fin, EstadoAcademico.PLANIFICADO, predeterminado, null);
    }
}
