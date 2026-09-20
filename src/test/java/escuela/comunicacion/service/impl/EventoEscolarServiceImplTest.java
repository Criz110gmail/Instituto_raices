package escuela.comunicacion.service.impl;

import escuela.academico.entity.*;
import escuela.academico.repository.*;
import escuela.alumno.repository.AlumnoRepository;
import escuela.comunicacion.dto.request.*;
import escuela.comunicacion.entity.*;
import escuela.comunicacion.mapper.EventoEscolarMapper;
import escuela.comunicacion.repository.EventoEscolarRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.*;
import escuela.institucion.repository.*;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventoEscolarServiceImplTest {
    private final EventoEscolarRepository eventos = mock(EventoEscolarRepository.class);
    private final InstitucionRepository instituciones = mock(InstitucionRepository.class);
    private final CicloEscolarRepository ciclos = mock(CicloEscolarRepository.class);
    private final PlantelRepository planteles = mock(PlantelRepository.class);
    private final NivelEducativoRepository niveles = mock(NivelEducativoRepository.class);
    private final GradoRepository grados = mock(GradoRepository.class);
    private final GrupoRepository grupos = mock(GrupoRepository.class);
    private final AlumnoRepository alumnos = mock(AlumnoRepository.class);
    private final PlantelNivelRepository ofertas = mock(PlantelNivelRepository.class);
    private final InscripcionRepository inscripciones = mock(InscripcionRepository.class);
    private final EventoEscolarMapper mapper = mock(EventoEscolarMapper.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final EventoEscolarServiceImpl service = new EventoEscolarServiceImpl(eventos, instituciones,
            ciclos, planteles, niveles, grados, grupos, alumnos, ofertas, inscripciones, mapper, alcance);

    private Institucion institucion;
    private CicloEscolar ciclo;
    private Plantel plantel;

    @BeforeEach
    void preparar() {
        institucion = new Institucion(); institucion.setId(1L); institucion.setNombre("Instituto Raíces");
        institucion.setZonaHoraria("America/Mexico_City"); institucion.setActivo(true);
        ciclo = new CicloEscolar(); ciclo.setId(2L); ciclo.setInstitucion(institucion);
        ciclo.setFechaInicio(LocalDate.of(2026, 8, 1)); ciclo.setFechaFin(LocalDate.of(2027, 7, 31));
        ciclo.setEstado(EstadoAcademico.ABIERTO);
        plantel = new Plantel(); plantel.setId(3L); plantel.setInstitucion(institucion); plantel.setActivo(true);
        when(instituciones.findById(1L)).thenReturn(Optional.of(institucion));
        when(ciclos.findById(2L)).thenReturn(Optional.of(ciclo));
        when(planteles.findById(3L)).thenReturn(Optional.of(plantel));
        when(alcance.alcanceInstitucionalActual(1L)).thenReturn(true);
        when(eventos.saveAndFlush(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void creaBorradorInstitucionalYConvierteLaZonaHoraria() {
        service.crear(request(AlcanceEventoEscolar.INSTITUCION, null, List.of(),
                LocalDateTime.of(2026, 9, 10, 8, 0), LocalDateTime.of(2026, 9, 10, 9, 0)));

        ArgumentCaptor<EventoEscolar> captor = ArgumentCaptor.forClass(EventoEscolar.class);
        verify(eventos).saveAndFlush(captor.capture());
        EventoEscolar guardado = captor.getValue();
        assertThat(guardado.getEstado()).isEqualTo(EstadoEventoEscolar.BORRADOR);
        assertThat(guardado.getInicioEn()).isEqualTo(Instant.parse("2026-09-10T14:00:00Z"));
        assertThat(guardado.getDestinatarios()).isEmpty();
        verify(alcance).validarInstitucion(1L);
    }

    @Test
    void rechazaFinAnteriorAlInicio() {
        assertThatThrownBy(() -> service.crear(request(AlcanceEventoEscolar.INSTITUCION, null, List.of(),
                LocalDateTime.of(2026, 9, 10, 9, 0), LocalDateTime.of(2026, 9, 10, 8, 0))))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("anterior");
        verify(eventos, never()).saveAndFlush(any());
    }

    @Test
    void seleccionExigeAlMenosUnDestinatario() {
        assertThatThrownBy(() -> service.crear(request(AlcanceEventoEscolar.SELECCION, null, List.of(),
                LocalDateTime.of(2026, 9, 10, 8, 0), LocalDateTime.of(2026, 9, 10, 9, 0))))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("al menos un destinatario");
    }

    @Test
    void rechazaGrupoQuePerteneceAOtroCiclo() {
        CicloEscolar otroCiclo = new CicloEscolar(); otroCiclo.setId(99L);
        Grupo grupo = new Grupo(); grupo.setId(20L); grupo.setActivo(true); grupo.setCicloEscolar(otroCiclo);
        grupo.setPlantel(plantel); when(grupos.findById(20L)).thenReturn(Optional.of(grupo));
        var destinos = List.of(new DestinatarioEventoRequest(TipoDestinatarioEvento.GRUPO, 20L));

        assertThatThrownBy(() -> service.crear(request(AlcanceEventoEscolar.SELECCION, null, destinos,
                LocalDateTime.of(2026, 9, 10, 8, 0), LocalDateTime.of(2026, 9, 10, 9, 0))))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("ciclo y alcance");
    }

    @Test
    void publicaUnBorradorConMarcaDeTiempo() {
        EventoEscolar evento = evento(EstadoEventoEscolar.BORRADOR, AlcanceEventoEscolar.PLANTEL);
        when(eventos.findByIdForUpdate(10L)).thenReturn(Optional.of(evento));

        service.publicar(10L, 0L);

        assertThat(evento.getEstado()).isEqualTo(EstadoEventoEscolar.PUBLICADO);
        assertThat(evento.getPublicadoEn()).isNotNull();
        verify(alcance).validarPlantel(3L);
        verify(eventos).saveAndFlush(evento);
    }

    @Test
    void noPermitePublicarDosVeces() {
        EventoEscolar evento = evento(EstadoEventoEscolar.PUBLICADO, AlcanceEventoEscolar.PLANTEL);
        when(eventos.findByIdForUpdate(10L)).thenReturn(Optional.of(evento));

        assertThatThrownBy(() -> service.publicar(10L, 0L))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("ya fue publicado");
        verify(eventos, never()).saveAndFlush(any());
    }

    @Test
    void cancelarConservaElRegistroYElMotivoNormalizado() {
        EventoEscolar evento = evento(EstadoEventoEscolar.PUBLICADO, AlcanceEventoEscolar.PLANTEL);
        when(eventos.findByIdForUpdate(10L)).thenReturn(Optional.of(evento));

        service.cancelar(10L, 0L, "  Tormenta eléctrica  ");

        assertThat(evento.getEstado()).isEqualTo(EstadoEventoEscolar.CANCELADO);
        assertThat(evento.getMotivoCancelacion()).isEqualTo("Tormenta eléctrica");
        assertThat(evento.getCanceladoEn()).isNotNull();
        verify(eventos).saveAndFlush(evento);
    }

    private EventoEscolarRequest request(AlcanceEventoEscolar tipoAlcance, Long plantelId,
                                          List<DestinatarioEventoRequest> destinatarios,
                                          LocalDateTime inicio, LocalDateTime fin) {
        return new EventoEscolarRequest(1L, 2L, plantelId, "Junta de familias", "Auditorio",
                "Información del ciclo", inicio, fin, TipoEventoEscolar.JUNTA, tipoAlcance,
                destinatarios, null);
    }

    private EventoEscolar evento(EstadoEventoEscolar estado, AlcanceEventoEscolar tipoAlcance) {
        EventoEscolar evento = new EventoEscolar(); evento.setId(10L); evento.setVersion(0L);
        evento.setInstitucion(institucion); evento.setCicloEscolar(ciclo); evento.setPlantel(plantel);
        evento.setEstado(estado); evento.setAlcance(tipoAlcance); return evento;
    }
}
