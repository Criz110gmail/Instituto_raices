package escuela.admin.controller;

import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.service.CicloEscolarService;
import escuela.admin.dto.ModuloCatalogo;
import escuela.cobranza.service.ConceptoCobroService;
import escuela.cobranza.service.CobranzaInscripcionService;
import escuela.cobranza.service.CuotaAlumnoService;
import escuela.inscripcion.dto.response.InscripcionResponse;
import escuela.inscripcion.service.InscripcionService;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CuotaAlumnoAdminControllerTest {
    @Test
    void sugerenciaValidaAlcanceYUsaInterseccionDeInscripcionYCiclo() {
        var inscripciones = mock(InscripcionService.class);
        var ciclos = mock(CicloEscolarService.class);
        var alcance = mock(AlcanceDatosService.class);
        var inscripcion = mock(InscripcionResponse.class);
        var ciclo = mock(CicloEscolarResponse.class);
        when(inscripciones.obtener(7L)).thenReturn(inscripcion);
        when(inscripcion.cicloEscolarId()).thenReturn(3L);
        when(inscripcion.fechaInicio()).thenReturn(LocalDate.of(2026, 9, 14));
        when(inscripcion.fechaFin()).thenReturn(null);
        when(ciclos.obtener(3L)).thenReturn(ciclo);
        when(ciclo.fechaInicio()).thenReturn(LocalDate.of(2026, 9, 1));
        when(ciclo.fechaFin()).thenReturn(LocalDate.of(2027, 6, 15));
        var controller = new CuotaAlumnoAdminController(mock(CuotaAlumnoService.class),
                mock(ConceptoCobroService.class), mock(CobranzaInscripcionService.class),
                inscripciones, ciclos, mock(InstitucionService.class), mock(PlantelService.class), alcance);
        var rango = controller.vigenciaInscripcion(7L);
        assertThat(rango.inicio()).isEqualTo(LocalDate.of(2026, 9, 14));
        assertThat(rango.fin()).isEqualTo(LocalDate.of(2027, 6, 15));
        verify(alcance).validarRecurso(ModuloCatalogo.INSCRIPCIONES, 7L);
    }
}
