package escuela.finanzas.service.impl;

import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.ReversionFinancieraRequest;
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

class ReversionFinancieraServiceImplTest {
    private final MovimientoFinancieroRepository movimientos = mock(MovimientoFinancieroRepository.class);
    private final TransferenciaCuentaRepository transferencias = mock(TransferenciaCuentaRepository.class);
    private final ReversionFinancieraRepository reversiones = mock(ReversionFinancieraRepository.class);
    private final CuentaFinancieraRepository cuentas = mock(CuentaFinancieraRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final RegistroAuditoriaService auditoria = mock(RegistroAuditoriaService.class);
    private final ReversionFinancieraServiceImpl service = new ReversionFinancieraServiceImpl(
            movimientos, transferencias, reversiones, cuentas, usuarios, alcance, auditoria);

    private Institucion institucion;
    private Plantel plantel;
    private CuentaFinanciera origen;
    private CuentaFinanciera destino;
    private MotivoFinanciero motivo;
    private MovimientoFinanciero operacion;

    @BeforeEach
    void preparar() {
        institucion = new Institucion(); institucion.setId(1L); institucion.setNombre("Raíces");
        institucion.setZonaHoraria("America/Mexico_City");
        plantel = new Plantel(); plantel.setId(2L); plantel.setInstitucion(institucion);
        origen = cuenta(8L, "Caja", "100.00");
        destino = cuenta(3L, "Banco", "100.00");
        motivo = new MotivoFinanciero(); motivo.setId(4L); motivo.setInstitucion(institucion);
        operacion = movimiento(20L, origen, DireccionMovimiento.INGRESO, "100.00", "100.00", "200.00");
        operacion.setClase(ClaseMovimiento.OPERACION); operacion.setVersion(0L);

        when(movimientos.findByIdForUpdate(20L)).thenReturn(Optional.of(operacion));
        when(cuentas.findByIdForUpdate(8L)).thenReturn(Optional.of(origen));
        when(reversiones.findByInstitucionIdAndClaveIdempotencia(1L, "reversa-1"))
                .thenReturn(Optional.empty());
        when(reversiones.saveAndFlush(any())).thenAnswer(i -> {
            ReversionFinanciera r = i.getArgument(0); r.setId(60L); return r;
        });
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(8L))
                .thenReturn(Optional.of(operacion));
        when(movimientos.saveAndFlush(any())).thenAnswer(i -> {
            MovimientoFinanciero m = i.getArgument(0); m.setId(70L); return m;
        });
        when(movimientos.saveAllAndFlush(any())).thenAnswer(i -> {
            List<MovimientoFinanciero> lista = i.getArgument(0);
            lista.get(0).setId(71L); lista.get(1).setId(72L); return lista;
        });
        Usuario actor = new Usuario(); actor.setId(9L); actor.setUsername("tesoreria");
        actor.setInstitucion(institucion); when(usuarios.findById(9L)).thenReturn(Optional.of(actor));
        var principal = new UsuarioPrincipal(9L, 1L, Set.of(2L), false, false,
                "tesoreria", "x", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void limpiar() { SecurityContextHolder.clearContext(); }

    @Test
    void revierteIngresoManualConEgresoTrazable() {
        var respuesta = service.revertirMovimiento(20L, request(0L));

        assertThat(respuesta.tipo()).isEqualTo(TipoReversionFinanciera.MOVIMIENTO_MANUAL);
        assertThat(respuesta.movimientos()).singleElement().satisfies(m -> {
            assertThat(m.saldoAnterior()).isEqualByComparingTo("200.00");
            assertThat(m.saldoPosterior()).isEqualByComparingTo("100.00");
        });
        ArgumentCaptor<MovimientoFinanciero> captor = ArgumentCaptor.forClass(MovimientoFinanciero.class);
        verify(movimientos).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getClase()).isEqualTo(ClaseMovimiento.REVERSO);
        assertThat(captor.getValue().getDireccion()).isEqualTo(DireccionMovimiento.EGRESO);
        assertThat(captor.getValue().getReversaDe()).isSameAs(operacion);
        assertThat(captor.getValue().getReversionFinanciera().getId()).isEqualTo(60L);
    }

    @Test
    void impideReversarIngresoCuandoElSaldoYaNoAlcanza() {
        MovimientoFinanciero ultimo = movimiento(30L, origen, DireccionMovimiento.EGRESO,
                "150.00", "200.00", "50.00");
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(8L)).thenReturn(Optional.of(ultimo));

        assertThatThrownBy(() -> service.revertirMovimiento(20L, request(0L)))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("saldo suficiente");
        verify(reversiones, never()).saveAndFlush(any());
    }

    @Test
    void noAdmiteReversarCobrosDesdeElFlujoGeneral() {
        operacion.setClase(ClaseMovimiento.COBRO);
        assertThatThrownBy(() -> service.revertirMovimiento(20L, request(0L)))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("operaciones manuales");
    }

    @Test
    void revierteTransferenciaConDosMovimientosInversos() {
        TransferenciaCuenta transferencia = transferencia();
        MovimientoFinanciero salida = movimiento(31L, origen, DireccionMovimiento.EGRESO,
                "30.00", "100.00", "70.00");
        MovimientoFinanciero entrada = movimiento(32L, destino, DireccionMovimiento.INGRESO,
                "30.00", "100.00", "130.00");
        salida.setClase(ClaseMovimiento.TRASPASO); entrada.setClase(ClaseMovimiento.TRASPASO);
        salida.setTransferencia(transferencia); entrada.setTransferencia(transferencia);
        when(transferencias.findByIdForUpdate(40L)).thenReturn(Optional.of(transferencia));
        when(movimientos.findAllByTransferenciaIdOrderByDireccionDesc(40L)).thenReturn(List.of(entrada, salida));
        when(cuentas.findByIdForUpdate(3L)).thenReturn(Optional.of(destino));
        when(cuentas.findByIdForUpdate(8L)).thenReturn(Optional.of(origen));
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(8L)).thenReturn(Optional.of(salida));
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(3L)).thenReturn(Optional.of(entrada));

        var respuesta = service.revertirTransferencia(40L, request(0L));

        assertThat(respuesta.movimientos()).hasSize(2);
        assertThat(respuesta.movimientos()).extracting(m -> m.saldoPosterior().toPlainString())
                .containsExactly("100.00", "100.00");
        assertThat(transferencia.getEstado()).isEqualTo(EstadoTransferenciaCuenta.REVERTIDA);
        ArgumentCaptor<List<MovimientoFinanciero>> captor = ArgumentCaptor.forClass(List.class);
        verify(movimientos).saveAllAndFlush(captor.capture());
        assertThat(captor.getValue()).extracting(MovimientoFinanciero::getDireccion)
                .containsExactly(DireccionMovimiento.INGRESO, DireccionMovimiento.EGRESO);
        assertThat(captor.getValue()).allMatch(m -> m.getClase() == ClaseMovimiento.REVERSO);
    }

    @Test
    void impideRevertirTransferenciaSiLaCuentaReceptoraYaGastoElDinero() {
        TransferenciaCuenta transferencia = transferencia();
        MovimientoFinanciero salida = movimiento(31L, origen, DireccionMovimiento.EGRESO,
                "30.00", "100.00", "70.00");
        MovimientoFinanciero entrada = movimiento(32L, destino, DireccionMovimiento.INGRESO,
                "30.00", "100.00", "130.00");
        salida.setClase(ClaseMovimiento.TRASPASO); entrada.setClase(ClaseMovimiento.TRASPASO);
        when(transferencias.findByIdForUpdate(40L)).thenReturn(Optional.of(transferencia));
        when(movimientos.findAllByTransferenciaIdOrderByDireccionDesc(40L)).thenReturn(List.of(entrada, salida));
        when(cuentas.findByIdForUpdate(3L)).thenReturn(Optional.of(destino));
        when(cuentas.findByIdForUpdate(8L)).thenReturn(Optional.of(origen));
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(8L)).thenReturn(Optional.of(salida));
        MovimientoFinanciero saldoBajo = movimiento(33L, destino, DireccionMovimiento.EGRESO,
                "115.00", "130.00", "15.00");
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(3L)).thenReturn(Optional.of(saldoBajo));

        assertThatThrownBy(() -> service.revertirTransferencia(40L, request(0L)))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("saldo suficiente");
        verify(reversiones, never()).saveAndFlush(any());
    }

    @Test
    void accesoDeRecuperacionNoPuedeRevertirFondos() {
        var principal = new UsuarioPrincipal(null, null, Set.of(), true, true,
                "bootstrap", "x", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        assertThatThrownBy(() -> service.revertirMovimiento(20L, request(0L)))
                .hasMessageContaining("acceso de recuperación");
    }

    @Test
    void reintentoIdempotenteDeTransferenciaIgnoraLaVersionYaIncrementada() {
        TransferenciaCuenta transferencia = transferencia();
        transferencia.setVersion(1L); transferencia.setEstado(EstadoTransferenciaCuenta.REVERTIDA);
        ReversionFinanciera existente = new ReversionFinanciera(); existente.setId(60L);
        existente.setTipo(TipoReversionFinanciera.TRANSFERENCIA); existente.setInstitucion(institucion);
        existente.setTransferenciaOrigen(transferencia); existente.setFecha(Instant.now().minusSeconds(30));
        existente.setMotivo("Captura incorrecta"); existente.setAutorizadoPor(usuarios.findById(9L).orElseThrow());
        when(transferencias.findByIdForUpdate(40L)).thenReturn(Optional.of(transferencia));
        when(reversiones.findByInstitucionIdAndClaveIdempotencia(1L, "reversa-1"))
                .thenReturn(Optional.of(existente));

        var respuesta = service.revertirTransferencia(40L, request(0L));

        assertThat(respuesta.id()).isEqualTo(60L);
        verify(cuentas, never()).findByIdForUpdate(anyLong());
        verify(reversiones, never()).saveAndFlush(any());
    }

    private TransferenciaCuenta transferencia() {
        TransferenciaCuenta transferencia = new TransferenciaCuenta(); transferencia.setId(40L);
        transferencia.setVersion(0L); transferencia.setInstitucion(institucion);
        transferencia.setCuentaOrigen(origen); transferencia.setCuentaDestino(destino);
        transferencia.setFecha(Instant.now().minusSeconds(600));
        transferencia.setMonto(new BigDecimal("30.00"));
        transferencia.setEstado(EstadoTransferenciaCuenta.APLICADA); return transferencia;
    }

    private CuentaFinanciera cuenta(Long id, String nombre, String saldo) {
        CuentaFinanciera cuenta = new CuentaFinanciera(); cuenta.setId(id); cuenta.setNombre(nombre);
        cuenta.setInstitucion(institucion); cuenta.setPlantel(plantel); cuenta.setActivo(true);
        cuenta.setMoneda("MXN"); cuenta.setSaldoInicial(new BigDecimal(saldo));
        cuenta.setFechaSaldoInicial(LocalDate.now().minusYears(1)); return cuenta;
    }

    private MovimientoFinanciero movimiento(Long id, CuentaFinanciera cuenta,
                                             DireccionMovimiento direccion, String monto,
                                             String anterior, String posterior) {
        MovimientoFinanciero movimiento = new MovimientoFinanciero(); movimiento.setId(id);
        movimiento.setInstitucion(institucion); movimiento.setCuenta(cuenta); movimiento.setPlantelOperacion(plantel);
        movimiento.setFechaOperacion(Instant.now().minusSeconds(600)); movimiento.setSecuenciaCuenta(id);
        movimiento.setDireccion(direccion); movimiento.setMonto(new BigDecimal(monto));
        movimiento.setSaldoAnterior(new BigDecimal(anterior)); movimiento.setSaldoPosterior(new BigDecimal(posterior));
        movimiento.setMotivoFinanciero(motivo); movimiento.setConcepto("Operación de prueba");
        return movimiento;
    }

    private ReversionFinancieraRequest request(Long version) {
        return new ReversionFinancieraRequest(
                LocalDateTime.now(ZoneId.of("America/Mexico_City")).minusMinutes(1),
                "Captura incorrecta", "reversa-1", version);
    }
}
