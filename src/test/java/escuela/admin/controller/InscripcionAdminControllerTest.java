package escuela.admin.controller;

import escuela.academico.service.CicloEscolarService;
import escuela.academico.service.GradoService;
import escuela.academico.service.GrupoService;
import escuela.academico.service.NivelEducativoService;
import escuela.alumno.dto.response.AlumnoResponse;
import escuela.alumno.dto.response.FotografiaAlumnoResponse;
import escuela.alumno.service.AlumnoService;
import escuela.alumno.service.FotografiaAlumnoService;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.inscripcion.dto.response.InscripcionResponse;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.service.InscripcionService;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelNivelService;
import escuela.institucion.service.PlantelService;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InscripcionAdminControllerTest {

    @Mock private InscripcionService service;
    @Mock private AlumnoService alumnoService;
    @Mock private FotografiaAlumnoService fotografiaService;
    @Mock private InstitucionService institucionService;
    @Mock private PlantelService plantelService;
    @Mock private PlantelNivelService ofertaService;
    @Mock private CicloEscolarService cicloService;
    @Mock private NivelEducativoService nivelService;
    @Mock private GradoService gradoService;
    @Mock private GrupoService grupoService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private InscripcionAdminController controller;

    @BeforeEach
    void catalogosVacios() {
        when(institucionService.listar()).thenReturn(List.of());
        when(plantelService.listar()).thenReturn(List.of());
        when(nivelService.listar()).thenReturn(List.of());
        when(alcance.filtrarInstituciones(anyList())).thenAnswer(i -> i.getArgument(0));
        when(alcance.filtrarPlanteles(anyList())).thenAnswer(i -> i.getArgument(0));
        when(alcance.filtrarNiveles(anyList())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void muestraFichaYFotografiaDelAlumnoEnElDetalleDeInscripcion() {
        InscripcionResponse inscripcion = inscripcion();
        AlumnoResponse alumno = alumno();
        FotografiaAlumnoResponse foto = new FotografiaAlumnoResponse(80L, "ana.jpg",
                "image/jpeg", 2048, Instant.parse("2026-09-14T12:00:00Z"), null, true);
        when(service.obtener(40L)).thenReturn(inscripcion);
        when(alumnoService.obtener(10L)).thenReturn(alumno);
        when(fotografiaService.actual(10L)).thenReturn(foto);
        when(service.listarAsignaciones(40L)).thenReturn(List.of());
        when(grupoService.listarPorPlantelYCiclo(20L, 30L)).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.editar(40L, model);

        assertThat(vista).isEqualTo("admin/inscripcion-form");
        assertThat(model.get("alumnoFicha")).isSameAs(alumno);
        assertThat(model.get("fotografiaAlumno")).isSameAs(foto);
        assertThat(model.get("inscripcion")).isSameAs(inscripcion);
    }

    private InscripcionResponse inscripcion() {
        return new InscripcionResponse(40L, 1L, 10L, "A-001", "Ana López",
                20L, "Plantel Centro", 30L, "2026-2027", 35L, "Primero",
                "INS-001", LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 14),
                null, EstadoInscripcion.ACTIVA, null, null, null, auditoria());
    }

    private AlumnoResponse alumno() {
        return new AlumnoResponse(10L, 1L, "A-001", "Ana", "López", null,
                "CURP00000000000000", LocalDate.of(2018, 3, 12), "F", "Guadalajara",
                "Mexicana", null, null, null, null, null, null, null, null,
                null, "MX", LocalDate.of(2026, 9, 14), null, true, 70L, auditoria());
    }

    private AuditoriaResponse auditoria() {
        Instant instante = Instant.parse("2026-09-14T12:00:00Z");
        return new AuditoriaResponse(instante, 1L, instante, 1L, 0L);
    }
}
