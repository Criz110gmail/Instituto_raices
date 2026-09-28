package escuela.portal.service;

import escuela.admin.dto.BoletaCalificacionFila;
import escuela.admin.dto.BoletaDetalle;
import escuela.admin.service.BoletaDetalleService;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.portal.dto.PortalHijoResumen;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PortalBoletaServiceTest {
    private final PortalTutorService portal = mock(PortalTutorService.class);
    private final InscripcionRepository inscripciones = mock(InscripcionRepository.class);
    private final BoletaDetalleService detalles = mock(BoletaDetalleService.class);
    private final PortalBoletaService service = new PortalBoletaService(portal, inscripciones, detalles);
    private final UsuarioPrincipal principal = new UsuarioPrincipal(7L, 1L, Set.of(), false, false,
            "familia", "x", List.of(new SimpleGrantedAuthority("PORTAL_TUTOR_ACCEDER")));

    @Test
    void listaSoloLasBoletasDelHijoValidadoYPaginaEnBase() {
        PortalHijoResumen hijo = hijo();
        Inscripcion inscripcion = mock(Inscripcion.class);
        when(inscripcion.getId()).thenReturn(9L);
        when(portal.validarHijo(principal, 20L)).thenReturn(hijo);
        when(inscripciones.buscarBoletasPortal(eq(20L), eq(1L), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(inscripcion), PageRequest.of(0, 10), 1));
        when(detalles.crear(List.of(inscripcion))).thenReturn(Map.of(9L, boleta()));

        var resultado = service.listar(principal, 20L, -3);

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().getFirst().ciclo()).isEqualTo("2026-2027");
        assertThat(resultado.getContent().getFirst().materias()).isEqualTo(2);
        verify(inscripciones).buscarBoletasPortal(eq(20L), eq(1L),
                argThat(p -> p.getPageNumber() == 0 && p.getPageSize() == 10));
    }

    @Test
    void noPermiteAbrirUnaInscripcionAjenaAlHijo() {
        when(portal.validarHijo(principal, 20L)).thenReturn(hijo());
        when(inscripciones.buscarBoletaPortal(99L, 20L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.detalle(principal, 20L, 99L))
                .hasMessageContaining("no está disponible para esta cuenta familiar");
        verifyNoInteractions(detalles);
    }

    private PortalHijoResumen hijo() {
        return new PortalHijoResumen(20L, "A-020", "Ana Raíces", "MADRE", true,
                true, true, true, "Centro", "2026-2027", "Primero", "A");
    }

    private BoletaDetalle boleta() {
        return new BoletaDetalle(9L, 1L, "Instituto Raíces", "INS-1", "A-020", "Ana Raíces",
                "Centro", "2026-2027", "Primero", "A · MATUTINO", List.of(
                new BoletaCalificacionFila("Periodo 1", "Matemáticas", "9", "0–10", ""),
                new BoletaCalificacionFila("Periodo 1", "Arte", "Destacado", "Cualitativa", "")));
    }
}
