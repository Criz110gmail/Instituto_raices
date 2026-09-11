package escuela.academico.service.impl;

import escuela.academico.dto.request.PeriodoAcademicoRequest;
import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.NivelEducativo;
import escuela.academico.entity.TipoPeriodoAcademico;
import escuela.academico.mapper.PeriodoAcademicoMapper;
import escuela.academico.repository.CicloEscolarRepository;
import escuela.academico.repository.NivelEducativoRepository;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PeriodoAcademicoServiceImplTest {

    private final PeriodoAcademicoRepository repository = mock(PeriodoAcademicoRepository.class);
    private final CicloEscolarRepository cicloRepository = mock(CicloEscolarRepository.class);
    private final NivelEducativoRepository nivelRepository = mock(NivelEducativoRepository.class);
    private PeriodoAcademicoServiceImpl service;
    private CicloEscolar ciclo;
    private NivelEducativo nivel;

    @BeforeEach
    void preparar() {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        institucion.setActivo(true);

        ciclo = new CicloEscolar();
        ciclo.setId(10L);
        ciclo.setInstitucion(institucion);
        ciclo.setFechaInicio(LocalDate.of(2026, 8, 1));
        ciclo.setFechaFin(LocalDate.of(2027, 7, 31));
        ciclo.setEstado(EstadoAcademico.ABIERTO);

        nivel = new NivelEducativo();
        nivel.setId(20L);
        nivel.setInstitucion(institucion);
        nivel.setActivo(true);

        when(cicloRepository.findById(10L)).thenReturn(Optional.of(ciclo));
        when(nivelRepository.findById(20L)).thenReturn(Optional.of(nivel));
        service = new PeriodoAcademicoServiceImpl(
                repository, cicloRepository, nivelRepository, new PeriodoAcademicoMapper());
    }

    @Test
    void rechazaFechasFueraDelCiclo() {
        PeriodoAcademicoRequest request = request(
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 9, 30));

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("dentro del ciclo");
    }

    @Test
    void rechazaPeriodosSolapados() {
        PeriodoAcademicoRequest request = request(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 10, 31));
        when(repository.existeSolapamiento(10L, 20L,
                request.fechaInicio(), request.fechaFin(), 0L)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("solapa");
    }

    @Test
    void rechazaCambiosEnCicloCerrado() {
        ciclo.setEstado(EstadoAcademico.CERRADO);

        assertThatThrownBy(() -> service.crear(request(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 10, 31))))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ciclo cerrado");
    }

    private PeriodoAcademicoRequest request(LocalDate inicio, LocalDate fin) {
        return new PeriodoAcademicoRequest(10L, 20L, "TRI1", "Primer trimestre",
                TipoPeriodoAcademico.TRIMESTRE, 1, inicio, fin,
                EstadoAcademico.ABIERTO, null, null);
    }
}
