package escuela.admin.service;

import escuela.admin.dto.ResultadoAutocompletado;
import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoRepository;
import escuela.institucion.entity.Institucion;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.cobranza.repository.TipoBecaRepository;
import escuela.cobranza.repository.CargoRepository;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.cobranza.entity.CategoriaConceptoCobro;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.inscripcion.entity.Inscripcion;
import escuela.academico.entity.Grado;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BusquedaAutocompletadoServiceTest {

    private final AlumnoRepository alumnoRepository = mock(AlumnoRepository.class);
    private final TutorRepository tutorRepository = mock(TutorRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final InscripcionRepository inscripcionRepository = mock(InscripcionRepository.class);
    private final ConceptoCobroRepository conceptoCobroRepository = mock(ConceptoCobroRepository.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final BusquedaAutocompletadoService service = new BusquedaAutocompletadoService(
            alumnoRepository, tutorRepository, usuarioRepository, inscripcionRepository,
            conceptoCobroRepository, mock(PeriodoAcademicoRepository.class),
            mock(TipoBecaRepository.class), mock(CargoRepository.class),
            mock(CuentaFinancieraRepository.class), alcance);

    @Test
    void noConsultaLaBaseConMenosDeTresCaracteres() {
        ResultadoAutocompletado resultado = service.alumnos(1L, "an");

        assertThat(resultado.resultados()).isEmpty();
        verify(alcance).validarAdministracionInstitucional(1L);
        verify(alumnoRepository, never()).buscarParaAutocompletado(any(), any(), any());
    }

    @Test
    void devuelveAlumnoConEtiquetaPractica() {
        Alumno alumno = alumno();
        when(alumnoRepository.buscarParaAutocompletado(any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of(alumno)));

        ResultadoAutocompletado resultado = service.alumnos(1L, "  ANA   López ");

        assertThat(resultado.resultados()).singleElement().satisfies(opcion -> {
            assertThat(opcion.id()).isEqualTo(10L);
            assertThat(opcion.titulo()).isEqualTo("A-001 · Ana López");
            assertThat(opcion.detalle()).contains("CURP00000000000000");
        });
        verify(alumnoRepository).buscarParaAutocompletado(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.eq("ana lópez"), any());
    }

    @Test
    void muestraDiezOpcionesInicialesAlSolicitarElPrimerEnfoque() {
        when(alumnoRepository.buscarParaAutocompletado(any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of(alumno())));

        ResultadoAutocompletado resultado = service.alumnos(1L, "__INICIALES__");

        assertThat(resultado.resultados()).hasSize(1);
        verify(alumnoRepository).buscarParaAutocompletado(
                org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq(""),
                org.mockito.ArgumentMatchers.argThat(p -> p.getPageSize() == 10));
    }

    @Test
    void devuelveTutorPorNombreOTelefono() {
        Tutor tutor = new Tutor();
        tutor.setId(20L);
        tutor.setNombres("María");
        tutor.setPrimerApellido("López");
        tutor.setTelefonoPrincipal("3312345678");
        when(tutorRepository.buscarParaAutocompletado(any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of(tutor)));

        ResultadoAutocompletado resultado = service.tutores(1L, "331");

        assertThat(resultado.resultados().getFirst().titulo()).isEqualTo("María López");
        assertThat(resultado.resultados().getFirst().detalle()).isEqualTo("3312345678");
    }

    @Test
    void devuelveUsuarioDisponibleSinCargarLaTablaCompleta() {
        Usuario usuario = new Usuario();
        usuario.setId(30L);
        usuario.setUsername("maria.tutora");
        usuario.setEmail("maria@raices.mx");
        usuario.setEstado(EstadoUsuario.ACTIVO);
        when(usuarioRepository.buscarParaAutocompletado(any(), any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of(usuario)));

        ResultadoAutocompletado resultado = service.usuarios(1L, "maria", 20L);

        assertThat(resultado.resultados().getFirst().titulo()).isEqualTo("maria.tutora");
        verify(usuarioRepository).buscarParaAutocompletado(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.eq("maria"),
                org.mockito.ArgumentMatchers.eq(20L), any());
    }

    @Test
    void buscaInscripcionesVigentesPorPlantelSinCargarElCatalogo() {
        Alumno alumno = alumno();
        Grado grado = new Grado();
        grado.setNombre("Primero");
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(40L);
        inscripcion.setNumeroInscripcion("INS-001");
        inscripcion.setAlumno(alumno);
        inscripcion.setGrado(grado);
        when(inscripcionRepository.buscarParaAutocompletado(any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of(inscripcion)));

        ResultadoAutocompletado resultado = service.inscripciones(8L, "ana");

        assertThat(resultado.resultados().getFirst().titulo()).isEqualTo("INS-001 · Ana López");
        verify(alcance).validarPlantel(8L);
    }

    @Test
    void buscaConceptosActivosPorInstitucion() {
        ConceptoCobro concepto = new ConceptoCobro();
        concepto.setId(50L);
        concepto.setCodigo("MAT");
        concepto.setNombre("Material escolar");
        concepto.setCategoria(CategoriaConceptoCobro.MATERIAL);
        when(conceptoCobroRepository.buscarParaAutocompletado(any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of(concepto)));

        ResultadoAutocompletado resultado = service.conceptosCobro(1L, "material");

        assertThat(resultado.resultados().getFirst().titulo()).isEqualTo("MAT · Material escolar");
        verify(alcance).validarInstitucion(1L);
    }

    private Alumno alumno() {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        Alumno alumno = new Alumno();
        alumno.setId(10L);
        alumno.setInstitucion(institucion);
        alumno.setMatricula("A-001");
        alumno.setNombres("Ana");
        alumno.setPrimerApellido("López");
        alumno.setCurp("CURP00000000000000");
        return alumno;
    }
}
