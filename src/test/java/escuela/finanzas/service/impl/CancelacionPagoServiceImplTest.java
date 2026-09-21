package escuela.finanzas.service.impl;

import escuela.admin.dto.ModuloCatalogo;
import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.repository.CargoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.CancelacionPagoRequest;
import escuela.finanzas.entity.*;
import escuela.finanzas.mapper.PagoMapper;
import escuela.finanzas.repository.*;
import escuela.institucion.entity.*;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.tutor.entity.Tutor;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CancelacionPagoServiceImplTest {
    private final PagoRepository pagos = mock(PagoRepository.class);
    private final DevolucionPagoRepository devoluciones = mock(DevolucionPagoRepository.class);
    private final AplicacionPagoRepository aplicaciones = mock(AplicacionPagoRepository.class);
    private final CargoRepository cargos = mock(CargoRepository.class);
    private final CuentaFinancieraRepository cuentas = mock(CuentaFinancieraRepository.class);
    private final MovimientoFinancieroRepository movimientos = mock(MovimientoFinancieroRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final PagoMapper mapper = mock(PagoMapper.class);
    private final RegistroAuditoriaService auditoria = mock(RegistroAuditoriaService.class);
    private final CancelacionPagoServiceImpl service = new CancelacionPagoServiceImpl(pagos, devoluciones,
            aplicaciones, cargos, cuentas, movimientos, usuarios, alcance, mapper, auditoria);
    private Pago pago;
    private CuentaFinanciera cuenta;
    private MovimientoFinanciero original;

    @BeforeEach
    void preparar() {
        Institucion institucion = new Institucion(); institucion.setId(1L);
        Plantel plantel = new Plantel(); plantel.setId(2L); plantel.setInstitucion(institucion);
        Tutor tutor = new Tutor(); tutor.setId(3L);
        Usuario actor = new Usuario(); actor.setId(9L); actor.setInstitucion(institucion);
        pago = new Pago(); pago.setId(50L); pago.setVersion(0L); pago.setInstitucion(institucion);
        pago.setPlantelRegistro(plantel); pago.setTutor(tutor); pago.setFolio("PAG-001");
        pago.setMonto(new BigDecimal("1000.00")); pago.setMoneda("MXN");
        pago.setEstado(EstadoPago.PENDIENTE_VALIDACION);
        cuenta = new CuentaFinanciera(); cuenta.setId(8L); cuenta.setInstitucion(institucion);
        cuenta.setPlantel(plantel); cuenta.setSaldoInicial(new BigDecimal("1100.00"));
        original = new MovimientoFinanciero(); original.setId(80L); original.setCuenta(cuenta);
        original.setClase(ClaseMovimiento.COBRO); original.setDireccion(DireccionMovimiento.INGRESO);
        original.setMonto(new BigDecimal("1000.00")); original.setSecuenciaCuenta(1L);
        original.setSaldoPosterior(new BigDecimal("1100.00"));
        MotivoFinanciero motivo = new MotivoFinanciero(); motivo.setId(7L); original.setMotivoFinanciero(motivo);
        when(pagos.findByIdForUpdate(50L)).thenReturn(Optional.of(pago));
        when(pagos.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        when(usuarios.findById(9L)).thenReturn(Optional.of(actor));
        when(cuentas.findByIdForUpdate(8L)).thenReturn(Optional.of(cuenta));
        when(movimientos.findByPagoId(50L)).thenReturn(Optional.of(original));
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(8L)).thenReturn(Optional.of(original));
        when(movimientos.saveAndFlush(any())).thenAnswer(i -> {
            MovimientoFinanciero movimiento = i.getArgument(0); movimiento.setId(81L); return movimiento;
        });
        var principal = new UsuarioPrincipal(9L, 1L, Set.of(2L), true, false, "operador", "x",
                List.of(new SimpleGrantedAuthority("PAGO_CANCELAR")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void limpiar() { SecurityContextHolder.clearContext(); }

    @Test
    void cancelaPendienteSinCrearMovimiento() {
        service.cancelar(50L, new CancelacionPagoRequest(0L, " Registro duplicado "));

        assertThat(pago.getEstado()).isEqualTo(EstadoPago.CANCELADO);
        assertThat(pago.getMotivoRechazoCancelacion()).isEqualTo("Registro duplicado");
        assertThat(pago.getCanceladoEn()).isNotNull();
        assertThat(pago.getCanceladoPor().getId()).isEqualTo(9L);
        verify(alcance).validarRecurso(ModuloCatalogo.PAGOS, 50L);
        verifyNoInteractions(devoluciones, aplicaciones, cargos, cuentas, movimientos);
        verify(auditoria).registrar(eq(1L), eq(AccionAuditoria.PAGO_CANCELADO), eq("PAGO"), eq(50L),
                eq("Registro duplicado"), anyMap());
    }

    @Test
    void cancelaValidadoConReversasYUnEgresoCompensatorio() {
        pago.setEstado(EstadoPago.VALIDADO);
        Cargo cargo = new Cargo(); cargo.setId(10L);
        AplicacionPago aplicacion = new AplicacionPago(); aplicacion.setId(70L);
        aplicacion.setPago(pago); aplicacion.setCargo(cargo);
        aplicacion.setOperacion(OperacionAplicacionPago.APLICAR);
        aplicacion.setMonto(new BigDecimal("400.00"));
        pago.getAplicaciones().add(aplicacion);
        when(aplicaciones.findAplicacionesActivasByPagoIdForUpdate(50L)).thenReturn(List.of(aplicacion));
        when(cargos.findByIdForUpdate(10L)).thenReturn(Optional.of(cargo));

        service.cancelar(50L, new CancelacionPagoRequest(0L, "Pago capturado por error"));

        assertThat(pago.getEstado()).isEqualTo(EstadoPago.CANCELADO);
        assertThat(pago.getAplicaciones()).hasSize(2);
        AplicacionPago reversa = pago.getAplicaciones().getLast();
        assertThat(reversa.getOperacion()).isEqualTo(OperacionAplicacionPago.REVERTIR);
        assertThat(reversa.getReversaDe()).isSameAs(aplicacion);
        assertThat(reversa.getMonto()).isEqualByComparingTo("400.00");
        verify(aplicaciones).saveAllAndFlush(List.of(reversa));
        var captor = org.mockito.ArgumentCaptor.forClass(MovimientoFinanciero.class);
        verify(movimientos).saveAndFlush(captor.capture());
        MovimientoFinanciero compensacion = captor.getValue();
        assertThat(compensacion.getClase()).isEqualTo(ClaseMovimiento.ANULACION);
        assertThat(compensacion.getDireccion()).isEqualTo(DireccionMovimiento.EGRESO);
        assertThat(compensacion.getReversaDe()).isSameAs(original);
        assertThat(compensacion.getSaldoPosterior()).isEqualByComparingTo("100.00");
        assertThat(compensacion.getClaveIdempotencia()).isEqualTo("ANULACION:PAGO:50");
    }

    @Test
    void impideCancelarPagoConDevolucionEjecutada() {
        pago.setEstado(EstadoPago.VALIDADO);
        when(devoluciones.existsByPagoIdAndEstado(50L, EstadoDevolucionPago.EJECUTADA)).thenReturn(true);

        assertThatThrownBy(() -> service.cancelar(50L, new CancelacionPagoRequest(0L, "Error")))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("devoluciones ejecutadas");
        verify(movimientos, never()).saveAndFlush(any());
        verifyNoInteractions(auditoria);
    }

    @Test
    void impideCompensarSiLosFondosYaNoEstanEnLaCuenta() {
        pago.setEstado(EstadoPago.VALIDADO);
        original.setSaldoPosterior(new BigDecimal("200.00"));

        assertThatThrownBy(() -> service.cancelar(50L, new CancelacionPagoRequest(0L, "Error")))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("saldo suficiente");
        verify(movimientos, never()).saveAndFlush(any());
        verifyNoInteractions(aplicaciones, auditoria);
    }

    @Test
    void unReintentoNoDuplicaLaCancelacion() {
        pago.setEstado(EstadoPago.CANCELADO);
        pago.setMotivoRechazoCancelacion("Duplicado");

        service.cancelar(50L, new CancelacionPagoRequest(0L, "Duplicado"));

        verify(pagos, never()).saveAndFlush(any());
        verifyNoInteractions(movimientos, aplicaciones, auditoria);
    }
}
