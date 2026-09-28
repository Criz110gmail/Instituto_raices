package escuela.calificacion.service.impl;

import escuela.academico.entity.*;
import escuela.academico.repository.GrupoRepository;
import escuela.academico.repository.MateriaGradoRepository;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.alumno.entity.Alumno;
import escuela.calificacion.dto.CapturaCalificacionRequest;
import escuela.calificacion.entity.Calificacion;
import escuela.calificacion.entity.EstadoCalificacion;
import escuela.calificacion.repository.CalificacionRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CalificacionServiceImplTest {
    private final CalificacionRepository repository = mock(CalificacionRepository.class);
    private final InscripcionRepository inscripciones = mock(InscripcionRepository.class);
    private final GrupoRepository grupos = mock(GrupoRepository.class);
    private final PeriodoAcademicoRepository periodos = mock(PeriodoAcademicoRepository.class);
    private final MateriaGradoRepository planes = mock(MateriaGradoRepository.class);
    private final CalificacionServiceImpl service = new CalificacionServiceImpl(
            repository, inscripciones, grupos, periodos, planes);
    private final List<Calificacion> almacenadas = new ArrayList<>();
    private Grupo grupo;
    private PeriodoAcademico periodo;
    private MateriaGrado plan;
    private List<Inscripcion> alumnos;

    @BeforeEach
    void preparar() {
        Institucion institucion = new Institucion(); institucion.setId(1L); institucion.setNombre("Instituto");
        Plantel plantel = new Plantel(); plantel.setId(2L); plantel.setNombre("Centro"); plantel.setInstitucion(institucion);
        NivelEducativo nivel = new NivelEducativo(); nivel.setId(3L); nivel.setNombre("Primaria"); nivel.setInstitucion(institucion);
        Grado grado = new Grado(); grado.setId(4L); grado.setNombre("Primero"); grado.setNivelEducativo(nivel);
        CicloEscolar ciclo = new CicloEscolar(); ciclo.setId(5L); ciclo.setNombre("2026-2027"); ciclo.setInstitucion(institucion);
        grupo = new Grupo(); grupo.setId(6L); grupo.setNombre("1 A"); grupo.setPlantel(plantel); grupo.setGrado(grado); grupo.setCicloEscolar(ciclo);
        periodo = new PeriodoAcademico(); periodo.setId(7L); periodo.setNombre("Primer bimestre");
        periodo.setNivelEducativo(nivel); periodo.setCicloEscolar(ciclo);
        periodo.setFechaInicio(LocalDate.of(2026, 8, 1)); periodo.setFechaFin(LocalDate.of(2026, 10, 1));
        Materia materia = new Materia(); materia.setId(8L); materia.setNombre("Matemáticas"); materia.setInstitucion(institucion); materia.setActivo(true);
        plan = new MateriaGrado(); plan.setId(9L); plan.setMateria(materia); plan.setGrado(grado); plan.setActivo(true);
        plan.setTipoEvaluacion(TipoEvaluacion.NUMERICA); plan.setEscalaMinima(BigDecimal.ZERO);
        plan.setEscalaMaxima(BigDecimal.TEN); plan.setMinimaAprobatoria(new BigDecimal("6")); plan.setDecimales(1);
        alumnos = List.of(inscripcion(10L, "A-001", "Ana"), inscripcion(11L, "A-002", "Luis"));

        when(grupos.findById(6L)).thenReturn(Optional.of(grupo));
        when(periodos.findById(7L)).thenReturn(Optional.of(periodo));
        when(planes.findById(9L)).thenReturn(Optional.of(plan));
        when(inscripciones.buscarParaCalificaciones(6L, periodo.getFechaInicio(), periodo.getFechaFin()))
                .thenReturn(alumnos);
        when(repository.findAllByInscripcionIdInAndMateriaGradoIdAndPeriodoAcademicoId(any(), eq(9L), eq(7L)))
                .thenAnswer(inv -> new ArrayList<>(almacenadas));
        when(repository.saveAllAndFlush(any())).thenAnswer(inv -> {
            List<Calificacion> recibidas = new ArrayList<>();
            ((Iterable<Calificacion>) inv.getArgument(0)).forEach(c -> {
                if (c.getId() == null) { c.setId(100L + recibidas.size()); c.setVersion(0L); }
                recibidas.add(c);
            });
            almacenadas.clear(); almacenadas.addAll(recibidas); return recibidas;
        });
    }

    @Test
    void guardaBorradorConEscalaCongelada() {
        service.guardar(request("8.5", null), false);

        assertThat(almacenadas).hasSize(1);
        Calificacion guardada = almacenadas.getFirst();
        assertThat(guardada.getValorNumerico()).isEqualByComparingTo("8.5");
        assertThat(guardada.getEscalaMaxima()).isEqualByComparingTo("10");
        assertThat(guardada.getEstado()).isEqualTo(EstadoCalificacion.BORRADOR);
    }

    @Test
    void rechazaValorFueraDeEscala() {
        assertThatThrownBy(() -> service.guardar(request("11", null), false))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("escala");
    }

    @Test
    void publicarExigeResultadoParaTodos() {
        assertThatThrownBy(() -> service.guardar(request("9", null), true))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("todos");
        verify(repository, never()).saveAllAndFlush(any());
    }

    @Test
    void publicaElBloqueCompleto() {
        service.guardar(request("9", "7.5"), true);

        assertThat(almacenadas).hasSize(2).allSatisfy(c -> {
            assertThat(c.getEstado()).isEqualTo(EstadoCalificacion.PUBLICADA);
            assertThat(c.getPublicadoEn()).isNotNull();
        });
    }

    @Test
    void reabreResultadosPublicados() {
        service.guardar(request("9", "7.5"), true);
        service.reabrir(6L, 7L, 9L);

        assertThat(almacenadas).allSatisfy(c -> {
            assertThat(c.getEstado()).isEqualTo(EstadoCalificacion.BORRADOR);
            assertThat(c.getPublicadoEn()).isNull();
        });
    }

    private CapturaCalificacionRequest request(String primero, String segundo) {
        return new CapturaCalificacionRequest(6L, 7L, 9L, List.of(
                fila(10L, primero), fila(11L, segundo)));
    }

    private CapturaCalificacionRequest.Fila fila(Long inscripcionId, String valor) {
        return new CapturaCalificacionRequest.Fila(inscripcionId, null,
                valor == null ? null : new BigDecimal(valor), null, null, null);
    }

    private Inscripcion inscripcion(Long id, String matricula, String nombre) {
        Alumno alumno = new Alumno(); alumno.setId(id + 100); alumno.setMatricula(matricula);
        alumno.setNombres(nombre); alumno.setPrimerApellido("Pérez");
        Inscripcion inscripcion = new Inscripcion(); inscripcion.setId(id); inscripcion.setAlumno(alumno);
        inscripcion.setPlantel(grupo.getPlantel()); inscripcion.setGrado(grupo.getGrado());
        inscripcion.setCicloEscolar(grupo.getCicloEscolar()); return inscripcion;
    }
}
