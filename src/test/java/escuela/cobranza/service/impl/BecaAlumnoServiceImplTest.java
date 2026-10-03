package escuela.cobranza.service.impl;

import escuela.cobranza.dto.request.BecaAlumnoRequest;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.EstadoBeca;
import escuela.cobranza.entity.ModalidadBeca;
import escuela.cobranza.entity.TipoBeca;
import escuela.cobranza.mapper.BecaAlumnoMapper;
import escuela.cobranza.repository.BecaAlumnoRepository;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.cobranza.repository.TipoBecaRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.seguridad.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BecaAlumnoServiceImplTest {

    private final BecaAlumnoRepository repository = mock(BecaAlumnoRepository.class);
    private final InscripcionRepository inscripcionRepository = mock(InscripcionRepository.class);
    private final TipoBecaRepository tipoRepository = mock(TipoBecaRepository.class);
    private final ConceptoCobroRepository conceptoRepository = mock(ConceptoCobroRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final BecaAlumnoServiceImpl service = new BecaAlumnoServiceImpl(repository,
            inscripcionRepository, tipoRepository, conceptoRepository, usuarioRepository,
            new BecaAlumnoMapper());

    @Test
    void muestraElRangoPermitidoConFormatoDiaMesAnio() {
        Inscripcion inscripcion = mock(Inscripcion.class, RETURNS_DEEP_STUBS);
        TipoBeca tipo = mock(TipoBeca.class, RETURNS_DEEP_STUBS);
        ConceptoCobro concepto = mock(ConceptoCobro.class, RETURNS_DEEP_STUBS);
        when(inscripcion.getAlumno().getInstitucion().getId()).thenReturn(1L);
        when(inscripcion.getFechaInicio()).thenReturn(LocalDate.of(2026, 9, 29));
        when(inscripcion.getFechaFin()).thenReturn(LocalDate.of(2027, 6, 15));
        when(inscripcion.getCicloEscolar().getFechaFin()).thenReturn(LocalDate.of(2027, 6, 30));
        when(inscripcion.getEstado()).thenReturn(EstadoInscripcion.ACTIVA);
        when(tipo.getInstitucion().getId()).thenReturn(1L);
        when(tipo.isActivo()).thenReturn(true);
        when(concepto.getInstitucion().getId()).thenReturn(1L);
        when(concepto.isActivo()).thenReturn(true);
        when(concepto.isPermiteBeca()).thenReturn(true);
        when(inscripcionRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(inscripcion));
        when(tipoRepository.findById(20L)).thenReturn(Optional.of(tipo));
        when(conceptoRepository.findById(30L)).thenReturn(Optional.of(concepto));
        BecaAlumnoRequest request = new BecaAlumnoRequest(10L, 20L, 30L,
                ModalidadBeca.PORCENTAJE, new BigDecimal("25"), null, null,
                LocalDate.of(2026, 9, 28), LocalDate.of(2027, 6, 15),
                "Aprovechamiento académico", EstadoBeca.ACTIVA, null);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("La beca debe quedar dentro de la inscripción: 29/09/2026 a 15/06/2027");
    }
}
