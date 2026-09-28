package escuela.asistencia.service.impl;

import escuela.academico.entity.*;
import escuela.academico.repository.GrupoRepository;
import escuela.alumno.entity.Alumno;
import escuela.asistencia.dto.CapturaAsistenciaRequest;
import escuela.asistencia.entity.*;
import escuela.asistencia.repository.AsistenciaRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.*;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AsistenciaServiceImplTest {
    private final AsistenciaRepository repository=mock(AsistenciaRepository.class);
    private final GrupoRepository grupos=mock(GrupoRepository.class);
    private final InscripcionRepository inscripciones=mock(InscripcionRepository.class);
    private final AsistenciaServiceImpl service=new AsistenciaServiceImpl(repository,grupos,inscripciones);
    private final List<Asistencia> guardadas=new ArrayList<>();
    private final LocalDate fecha=LocalDate.of(2026,9,15);
    private Grupo grupo; private List<Inscripcion> alumnos;

    @BeforeEach void preparar(){
        Institucion i=new Institucion();i.setId(1L); Plantel p=new Plantel();p.setId(2L);p.setNombre("Centro");p.setInstitucion(i);
        NivelEducativo n=new NivelEducativo();n.setId(3L); Grado g=new Grado();g.setId(4L);g.setNombre("Primero");g.setNivelEducativo(n);
        CicloEscolar c=new CicloEscolar();c.setId(5L);c.setNombre("2026-2027");c.setFechaInicio(LocalDate.of(2026,8,1));c.setFechaFin(LocalDate.of(2027,7,15));
        grupo=new Grupo();grupo.setId(6L);grupo.setNombre("A");grupo.setActivo(true);grupo.setPlantel(p);grupo.setGrado(g);grupo.setCicloEscolar(c);
        alumnos=List.of(inscripcion(10L,"A1","Ana"),inscripcion(11L,"A2","Luis"));
        when(grupos.findById(6L)).thenReturn(Optional.of(grupo)); when(inscripciones.buscarParaAsistencia(6L,fecha)).thenReturn(alumnos);
        when(repository.findAllByInscripcionIdInAndGrupoIdAndFecha(any(),eq(6L),eq(fecha))).thenAnswer(x->new ArrayList<>(guardadas));
        when(repository.saveAllAndFlush(any())).thenAnswer(x->{List<Asistencia> r=new ArrayList<>();((Iterable<Asistencia>)x.getArgument(0)).forEach(r::add);guardadas.clear();guardadas.addAll(r);return r;});
    }
    @Test void hojaNuevaMarcaPresentesPorDefecto(){assertThat(service.hoja(6L,fecha).filas()).allMatch(f->f.estado()==EstadoAsistencia.PRESENTE);}
    @Test void guardaEstadosDeTodosLosAlumnos(){service.guardar(request(List.of(fila(10L,EstadoAsistencia.AUSENTE),fila(11L,EstadoAsistencia.RETARDO))));assertThat(guardadas).extracting(Asistencia::getEstado).containsExactly(EstadoAsistencia.AUSENTE,EstadoAsistencia.RETARDO);}
    @Test void rechazaListaIncompleta(){assertThatThrownBy(()->service.guardar(request(List.of(fila(10L,EstadoAsistencia.PRESENTE))))).isInstanceOf(ReglaNegocioException.class).hasMessageContaining("lista");}
    private CapturaAsistenciaRequest request(List<CapturaAsistenciaRequest.Fila> f){return new CapturaAsistenciaRequest(6L,fecha,f);}
    private CapturaAsistenciaRequest.Fila fila(Long id,EstadoAsistencia e){return new CapturaAsistenciaRequest.Fila(id,e,null,null);}
    private Inscripcion inscripcion(Long id,String matricula,String nombre){Alumno a=new Alumno();a.setMatricula(matricula);a.setNombres(nombre);a.setPrimerApellido("Pérez");Inscripcion x=new Inscripcion();x.setId(id);x.setAlumno(a);return x;}
}
