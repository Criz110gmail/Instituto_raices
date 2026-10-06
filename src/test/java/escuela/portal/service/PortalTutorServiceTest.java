package escuela.portal.service;

import escuela.admin.dto.*;
import escuela.admin.repository.ReporteFinancieroRepository;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.portal.dto.*;
import escuela.portal.repository.PortalTutorRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PortalTutorServiceTest {
    private final PortalTutorRepository portal = mock(PortalTutorRepository.class);
    private final ReporteFinancieroRepository reportes = mock(ReporteFinancieroRepository.class);
    private final InstitucionService instituciones = mock(InstitucionService.class);
    private final NotificacionPortalService notificaciones = mock(NotificacionPortalService.class);
    private final PortalTutorService service = new PortalTutorService(portal, reportes, instituciones, notificaciones);
    private UsuarioPrincipal principal;

    @BeforeEach
    void preparar() {
        principal = new UsuarioPrincipal(7L, 1L, Set.of(), false, false,
                "familia", "x", List.of());
        when(instituciones.obtener(1L)).thenReturn(new InstitucionResponse(1L, "RAICES",
                "Instituto Raíces", null, null, null, null, null, null, null,
                null, null, null, "MX", null, "America/Mexico_City", "MXN", true, null));
        when(portal.nombreTutor(7L, 1L)).thenReturn("María Familia");
        when(portal.eventos(anyLong(), eq(1L), anyString(), any(), anyInt(), anyInt()))
                .thenReturn(Page.empty());
        when(portal.avisos(anyLong(), eq(1L), anyString(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Page.empty());
        when(notificaciones.sincronizarYConsultar(any(), anyInt()))
                .thenReturn(new PortalNotificaciones(0, Page.empty()));
    }

    @Test
    void rechazaAccesoDeRecuperacionOSinUsuarioPersistido() {
        var recuperacion = new UsuarioPrincipal(null, null, Set.of(), true, true,
                "bootstrap", "x", List.of());

        assertThatThrownBy(() -> service.consultar(recuperacion, null, 0, 0, 0, 0, 0))
                .isInstanceOf(AccessDeniedException.class).hasMessageContaining("cuenta de tutor");
        verifyNoInteractions(portal);
    }

    @Test
    void exigeTutorActivoVinculadoConLaCuenta() {
        when(portal.nombreTutor(7L, 1L)).thenReturn(null);

        assertThatThrownBy(() -> service.consultar(principal, null, 0, 0, 0, 0, 0))
                .isInstanceOf(AccessDeniedException.class).hasMessageContaining("tutor activo");
        verify(portal, never()).hijos(anyLong(), anyLong(), any());
    }

    @Test
    void cuentaSinVinculosVigentesMuestraEstadoVacio() {
        when(portal.hijos(eq(7L), eq(1L), any())).thenReturn(List.of());

        PortalTutorResultado resultado = service.consultar(principal, null, 0, 0, 0, 0, 0);

        assertThat(resultado.hijo()).isNull();
        assertThat(resultado.hijos()).isEmpty();
        verifyNoInteractions(reportes);
    }

    @Test
    void vinculoSinResponsabilidadFinancieraNoExponeCargos() {
        PortalHijoResumen hijo = hijo(false, true);
        when(portal.hijos(eq(7L), eq(1L), any())).thenReturn(List.of(hijo));

        PortalTutorResultado resultado = service.consultar(principal, 20L, 0, 0, 0, 0, 0);

        assertThat(resultado.hijo()).isEqualTo(hijo);
        assertThat(resultado.estadoCuenta()).isNull();
        verifyNoInteractions(reportes);
        verify(portal).eventos(eq(20L), eq(1L), eq("America/Mexico_City"), any(), eq(0), eq(10));
    }

    @Test
    void vinculoFinancieroObtieneResumenYPaginaDerivados() {
        PortalHijoResumen hijo = hijo(true, true);
        when(portal.hijos(eq(7L), eq(1L), any())).thenReturn(List.of(hijo));
        when(reportes.estadoCuenta(any(), any(), any())).thenReturn(Page.empty());
        when(reportes.resumenEstadoCuenta(any(), any(), any(), eq("MXN")))
                .thenReturn(new ResumenEstadoCuenta(2, new BigDecimal("1500"),
                        new BigDecimal("500"), new BigDecimal("1000"), BigDecimal.ZERO, "MXN"));

        PortalTutorResultado resultado = service.consultar(principal, 20L, -4, -3, -2, -1, -1);

        assertThat(resultado.estadoCuenta().resumen().saldo()).isEqualByComparingTo("1000");
        verify(reportes).estadoCuenta(argThat(f -> f.alumnoId().equals(20L) && f.pagina() == 0
                        && f.situacion().equals("POR_PAGAR")),
                argThat(AlcanceReporteFinanciero::institucional), any());
        verify(reportes).resumenEstadoCuenta(argThat(f -> f.alumnoId().equals(20L)
                        && f.situacion().equals("TODOS")), any(), any(), eq("MXN"));
        verify(portal).eventos(eq(20L), eq(1L), anyString(), any(), eq(0), eq(10));
    }

    @Test
    void historialDePagosFiltraPorHijoMesYAnio() {
        PortalHijoResumen hijo = hijo(true, true);
        when(portal.hijos(eq(7L), eq(1L), any())).thenReturn(List.of(hijo));
        when(reportes.estadoCuenta(any(), any(), any())).thenReturn(Page.empty());
        when(reportes.resumenEstadoCuenta(any(), any(), any(), eq("MXN")))
                .thenReturn(new ResumenEstadoCuenta(0, BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, "MXN"));
        when(portal.pagos(7L, 1L, 20L, "America/Mexico_City", 9, 2026, 0, 10))
                .thenReturn(Page.empty());

        service.consultarPagos(principal, 20L, 0, 0, 9, 2026);

        verify(portal).pagos(7L, 1L, 20L, "America/Mexico_City", 9, 2026, 0, 10);
    }

    @Test
    void soporteConservaPendientesPaginadosYResumenCompleto() {
        when(portal.hijos(eq(7L), eq(1L), any())).thenReturn(List.of(hijo(true, true)));
        when(reportes.estadoCuenta(any(), any(), any())).thenReturn(Page.empty());
        service.consultarPagosComoSoporte(principal, 20L, 2, 3, 10, 2026);
        verify(reportes).estadoCuenta(argThat(f -> f.situacion().equals("POR_PAGAR")
                && f.institucionId().equals(1L) && f.alumnoId().equals(20L)
                && f.pagina() == 2 && f.tamanio() == 10), any(), any());
        verify(reportes).resumenEstadoCuenta(argThat(f -> f.situacion().equals("TODOS")),
                any(), any(), eq("MXN"));
        verify(portal).pagos(7L, 1L, 20L, "America/Mexico_City", 10, 2026, 3, 10);
        verifyNoInteractions(notificaciones);
    }

    @Test
    void impideSeleccionarAlumnoFueraDeLosVinculosVigentes() {
        when(portal.hijos(eq(7L), eq(1L), any())).thenReturn(List.of(hijo(true, true)));

        assertThatThrownBy(() -> service.consultar(principal, 999L, 0, 0, 0, 0, 0))
                .hasMessageContaining("ya no tiene un vínculo vigente");
        verifyNoInteractions(reportes);
    }

    @Test
    void fotografiaTambienValidaElVinculoVigente() {
        when(portal.hijos(eq(7L), eq(1L), any())).thenReturn(List.of(hijo(true, true)));

        assertThat(service.validarHijo(principal, 20L).alumnoId()).isEqualTo(20L);
        assertThatThrownBy(() -> service.validarHijo(principal, 21L))
                .isInstanceOf(AccessDeniedException.class);
    }

    private PortalHijoResumen hijo(boolean responsable, boolean verFinanzas) {
        return new PortalHijoResumen(20L, "A-020", "Ana Raíces", "MADRE", true,
                responsable, verFinanzas, true, "Centro", "2026-2027", "Primero", "A");
    }
}
