package escuela.finanzas.service;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.MovimientoManualRequest;
import escuela.finanzas.dto.request.RetiroFondoRequest;
import escuela.finanzas.dto.response.MovimientoFinancieroResponse;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RetiroFondoServiceTest {
    private final CuentaFinancieraRepository cuentas = mock(CuentaFinancieraRepository.class);
    private final RetiroFondoRepository retiros = mock(RetiroFondoRepository.class);
    private final MovimientoFinancieroRepository movimientos = mock(MovimientoFinancieroRepository.class);
    private final MotivoFinancieroRepository motivos = mock(MotivoFinancieroRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final MovimientoManualService operaciones = mock(MovimientoManualService.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final RegistroAuditoriaService auditoria = mock(RegistroAuditoriaService.class);
    private final RetiroFondoService service = new RetiroFondoService(cuentas, retiros, movimientos,
            motivos, usuarios, operaciones, alcance, auditoria);
    private CuentaFinanciera cuenta;
    private MotivoFinanciero motivo;

    @BeforeEach
    void preparar() {
        Institucion institucion = new Institucion(); institucion.setId(1L);
        institucion.setZonaHoraria("America/Mexico_City");
        Plantel plantel = new Plantel(); plantel.setId(2L); plantel.setInstitucion(institucion);
        cuenta = new CuentaFinanciera(); cuenta.setId(3L); cuenta.setInstitucion(institucion);
        cuenta.setPlantel(plantel); cuenta.setMoneda("MXN"); cuenta.setActivo(true);
        when(cuentas.findByIdForUpdate(3L)).thenReturn(Optional.of(cuenta));
        Usuario actor = new Usuario(); actor.setId(9L); actor.setInstitucion(institucion);
        when(usuarios.findById(9L)).thenReturn(Optional.of(actor));
        motivo = new MotivoFinanciero(); motivo.setId(5L); motivo.setInstitucion(institucion);
        motivo.setCodigo("SERVICIOS");
        when(motivos.findById(5L)).thenReturn(Optional.of(motivo));
        MovimientoFinanciero movimiento = new MovimientoFinanciero(); movimiento.setId(20L);
        movimiento.setPlantelOperacion(plantel);
        movimiento.setCuenta(cuenta); movimiento.setDireccion(DireccionMovimiento.EGRESO);
        movimiento.setClase(ClaseMovimiento.OPERACION); movimiento.setMonto(new BigDecimal("25.00"));
        movimiento.setConcepto("Retiro externo: Material"); movimiento.setReferencia("FACT-7");
        movimiento.setTerceroNombre("Proveedor Uno");
        when(movimientos.findById(20L)).thenReturn(Optional.of(movimiento));
        when(operaciones.registrar(any())).thenReturn(new MovimientoFinancieroResponse(20L, 3L,
                "Caja", Instant.now(), 1L, new BigDecimal("25.00"), "MXN",
                new BigDecimal("100.00"), new BigDecimal("75.00")));
        when(retiros.saveAndFlush(any())).thenAnswer(i -> {
            RetiroFondo retiro = i.getArgument(0); retiro.setId(30L); return retiro;
        });
        var principal = new UsuarioPrincipal(9L, 1L, Set.of(2L), false, false,
                "tesoreria", "x", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach void cerrar() { SecurityContextHolder.clearContext(); }

    @Test
    void publicaUnSoloEgresoYConservaVinculoYAuditoria() {
        assertThat(service.ejecutar(request())).isEqualTo(30L);
        ArgumentCaptor<MovimientoManualRequest> movimiento = ArgumentCaptor.forClass(MovimientoManualRequest.class);
        verify(operaciones).registrar(movimiento.capture());
        assertThat(movimiento.getValue().direccion()).isEqualTo(DireccionMovimiento.EGRESO);
        assertThat(movimiento.getValue().terceroNombre()).isEqualTo("Proveedor Uno");
        assertThat(movimiento.getValue().claveIdempotencia()).isEqualTo("RETIRO:clave-1");
        ArgumentCaptor<RetiroFondo> retiro = ArgumentCaptor.forClass(RetiroFondo.class);
        verify(retiros).saveAndFlush(retiro.capture());
        assertThat(retiro.getValue().getMovimiento().getId()).isEqualTo(20L);
        verify(auditoria).registrar(eq(1L), eq(AccionAuditoria.RETIRO_FONDO_EJECUTADO),
                eq("RETIRO_FONDO"), eq(30L), any(), anyMap());
    }

    @Test
    void reintentoIdenticoNoVuelveAMoverFondos() {
        RetiroFondo existente = new RetiroFondo(); existente.setId(30L); existente.setCuenta(cuenta);
        existente.setPlantelOperacion(cuenta.getPlantel()); existente.setFechaOperacion(request().fechaOperacion()
                .atZone(ZoneId.of("America/Mexico_City")).toInstant());
        existente.setMonto(new BigDecimal("25.00")); existente.setMotivoFinanciero(motivo);
        existente.setBeneficiario("Proveedor Uno"); existente.setConcepto("Material");
        existente.setReferencia("FACT-7");
        when(retiros.findByInstitucionIdAndClaveIdempotencia(1L, "clave-1"))
                .thenReturn(Optional.of(existente));
        assertThat(service.ejecutar(request())).isEqualTo(30L);
        verifyNoInteractions(operaciones);
        verify(retiros, never()).saveAndFlush(any());
    }

    @Test
    void claveReutilizadaConOtrosDatosSeRechaza() {
        RetiroFondo existente = new RetiroFondo(); existente.setId(30L); existente.setCuenta(cuenta);
        existente.setFechaOperacion(request().fechaOperacion().atZone(ZoneId.of("America/Mexico_City")).toInstant());
        existente.setMonto(new BigDecimal("50.00")); existente.setMotivoFinanciero(motivo);
        existente.setBeneficiario("Proveedor Uno"); existente.setConcepto("Material");
        existente.setReferencia("FACT-7");
        when(retiros.findByInstitucionIdAndClaveIdempotencia(1L, "clave-1"))
                .thenReturn(Optional.of(existente));
        assertThatThrownBy(() -> service.ejecutar(request()))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("otros datos");
        verifyNoInteractions(operaciones);
    }

    @Test
    void noAceptaRetiroSinReferenciaNiDestinatario() {
        RetiroFondoRequest r = request();
        assertThatThrownBy(() -> service.ejecutar(new RetiroFondoRequest(r.institucionId(),
                r.cuentaId(), r.plantelOperacionId(), r.fechaOperacion(), r.motivoFinancieroId(),
                r.monto(), "", r.concepto(), r.referencia(), r.observaciones(), r.claveIdempotencia())))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("destinatario");
        verifyNoInteractions(operaciones);
    }

    @Test
    void noPermiteUsarMotivoTecnicoDeTraspaso() {
        motivo.setCodigo("TRASPASO_INTERNO");
        assertThatThrownBy(() -> service.ejecutar(request()))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("motivo técnico");
        verifyNoInteractions(operaciones);
    }

    private RetiroFondoRequest request() {
        return new RetiroFondoRequest(1L, 3L, null,
                LocalDateTime.of(2026, 9, 15, 12, 0), 5L, new BigDecimal("25.00"),
                "Proveedor Uno", "Material", "FACT-7", null, "clave-1");
    }
}
