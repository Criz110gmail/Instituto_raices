package escuela.finanzas.service.impl;

import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.*;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
import escuela.institucion.entity.*;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CorteCajaServiceImplTest {
    private final CorteCajaRepository cortes = mock(CorteCajaRepository.class);
    private final CuentaFinancieraRepository cuentas = mock(CuentaFinancieraRepository.class);
    private final MovimientoFinancieroRepository movimientos = mock(MovimientoFinancieroRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final CorteCajaServiceImpl service = new CorteCajaServiceImpl(cortes, cuentas, movimientos, usuarios, alcance);

    private Institucion institucion;
    private Plantel plantel;
    private CuentaFinanciera cuenta;
    private Usuario actor;

    @BeforeEach
    void preparar() {
        institucion = new Institucion(); institucion.setId(1L); institucion.setNombre("Raíces");
        plantel = new Plantel(); plantel.setId(2L); plantel.setNombre("Centro"); plantel.setInstitucion(institucion);
        cuenta = new CuentaFinanciera(); cuenta.setId(8L); cuenta.setInstitucion(institucion); cuenta.setPlantel(plantel);
        cuenta.setCodigo("CAJ-01"); cuenta.setNombre("Caja principal"); cuenta.setTipo(TipoCuentaFinanciera.CAJA);
        cuenta.setMoneda("MXN"); cuenta.setSaldoInicial(new BigDecimal("100.00"));
        cuenta.setFechaSaldoInicial(LocalDate.now().minusYears(1)); cuenta.setActivo(true);
        actor = new Usuario(); actor.setId(9L); actor.setUsername("tesoreria"); actor.setInstitucion(institucion);
        when(cuentas.findByIdForUpdate(8L)).thenReturn(Optional.of(cuenta));
        when(usuarios.findById(9L)).thenReturn(Optional.of(actor));
        when(cortes.findByInstitucionIdAndClaveApertura(anyLong(), anyString())).thenReturn(Optional.empty());
        when(cortes.findByInstitucionIdAndClaveCierre(anyLong(), anyString())).thenReturn(Optional.empty());
        when(cortes.findByCuentaIdAndEstado(8L, EstadoCorteCaja.ABIERTO)).thenReturn(Optional.empty());
        when(cortes.saveAndFlush(any())).thenAnswer(invocacion -> {
            CorteCaja corte = invocacion.getArgument(0);
            if (corte.getId() == null) corte.setId(30L);
            return corte;
        });
        var principal = new UsuarioPrincipal(9L, 1L, Set.of(2L), false, false,
                "tesoreria", "x", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void limpiar() { SecurityContextHolder.clearContext(); }

    @Test
    void abreConSaldoYSecuenciaVigentes() {
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(8L))
                .thenReturn(Optional.of(movimiento(14L, "275.50")));

        var respuesta = service.abrir(apertura("abrir-1"));

        assertThat(respuesta.secuenciaInicial()).isEqualTo(14L);
        assertThat(respuesta.saldoInicialSistema()).isEqualByComparingTo("275.50");
        assertThat(respuesta.estado()).isEqualTo(EstadoCorteCaja.ABIERTO);
        verify(alcance).validarPlantel(2L);
    }

    @Test
    void impideDosCortesAbiertosParaLaMismaCaja() {
        when(cortes.findByCuentaIdAndEstado(8L, EstadoCorteCaja.ABIERTO))
                .thenReturn(Optional.of(corteAbierto()));

        assertThatThrownBy(() -> service.abrir(apertura("abrir-1")))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("ya tiene un corte abierto");
        verify(cortes, never()).saveAndFlush(any());
    }

    @Test
    void cierraUsandoFoliosYCalculaConciliacionExacta() {
        CorteCaja corte = corteAbierto();
        prepararCierre(corte, 13L, "150.00", 3L, "80.00", "30.00");

        var respuesta = service.cerrar(30L, cierre("150.00", null, "cerrar-1", 0L));

        assertThat(respuesta.estado()).isEqualTo(EstadoCorteCaja.CERRADO);
        assertThat(respuesta.saldoEsperado()).isEqualByComparingTo("150.00");
        assertThat(respuesta.diferencia()).isEqualByComparingTo("0.00");
        assertThat(respuesta.movimientosContabilizados()).isEqualTo(3L);
        verify(movimientos).resumirParaCorte(8L, 10L, 13L);
    }

    @Test
    void exigeJustificarFaltanteOSobrante() {
        CorteCaja corte = corteAbierto();
        prepararCierre(corte, 13L, "150.00", 3L, "80.00", "30.00");

        assertThatThrownBy(() -> service.cerrar(30L, cierre("149.00", null, "cerrar-1", 0L)))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("Explica la diferencia");
        assertThat(corte.getEstado()).isEqualTo(EstadoCorteCaja.ABIERTO);
    }

    @Test
    void cierreIdempotenteDevuelveElResultadoAunqueLaVersionEnviadaSeaAnterior() {
        CorteCaja cerrado = corteAbierto();
        cerrado.setEstado(EstadoCorteCaja.CERRADO); cerrado.setEfectivoDeclarado(new BigDecimal("150.00"));
        cerrado.setSaldoEsperado(new BigDecimal("150.00")); cerrado.setDiferencia(BigDecimal.ZERO);
        cerrado.setCerradoEn(java.time.Instant.now()); cerrado.setCerradoPor(actor); cerrado.setSecuenciaFinal(13L);
        cerrado.setMovimientosContabilizados(3L); cerrado.setTotalIngresos(new BigDecimal("80.00"));
        cerrado.setTotalEgresos(new BigDecimal("30.00")); cerrado.setClaveCierre("cerrar-1"); cerrado.setVersion(1L);
        when(cortes.findById(30L)).thenReturn(Optional.of(cerrado));
        when(cortes.findByIdForUpdate(30L)).thenReturn(Optional.of(cerrado));
        when(cortes.findByInstitucionIdAndClaveCierre(1L, "cerrar-1")).thenReturn(Optional.of(cerrado));

        var respuesta = service.cerrar(30L, cierre("150.00", null, "cerrar-1", 0L));

        assertThat(respuesta.estado()).isEqualTo(EstadoCorteCaja.CERRADO);
        verify(cortes, never()).saveAndFlush(any());
    }

    @Test
    void rechazaUnaCuentaQueNoSeaCaja() {
        cuenta.setTipo(TipoCuentaFinanciera.BANCO);
        assertThatThrownBy(() -> service.abrir(apertura("abrir-1")))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("tipo caja");
    }

    @Test
    void accesoDeRecuperacionNoPuedeConciliarEfectivo() {
        var principal = new UsuarioPrincipal(null, null, Set.of(), true, true,
                "bootstrap", "x", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        assertThatThrownBy(() -> service.abrir(apertura("abrir-1")))
                .hasMessageContaining("acceso de recuperación");
    }

    private void prepararCierre(CorteCaja corte, long secuencia, String saldo, long cantidad,
                                String ingresos, String egresos) {
        when(cortes.findById(30L)).thenReturn(Optional.of(corte));
        when(cortes.findByIdForUpdate(30L)).thenReturn(Optional.of(corte));
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(8L))
                .thenReturn(Optional.of(movimiento(secuencia, saldo)));
        ResumenMovimientosCorte resumen = mock(ResumenMovimientosCorte.class);
        when(resumen.getCantidad()).thenReturn(cantidad);
        when(resumen.getIngresos()).thenReturn(new BigDecimal(ingresos));
        when(resumen.getEgresos()).thenReturn(new BigDecimal(egresos));
        when(movimientos.resumirParaCorte(8L, 10L, secuencia)).thenReturn(resumen);
    }

    private CorteCaja corteAbierto() {
        CorteCaja corte = new CorteCaja(); corte.setId(30L); corte.setVersion(0L);
        corte.setInstitucion(institucion); corte.setCuenta(cuenta); corte.setEstado(EstadoCorteCaja.ABIERTO);
        corte.setAbiertoEn(java.time.Instant.now().minusSeconds(3600)); corte.setAbiertoPor(actor);
        corte.setSecuenciaInicial(10L); corte.setSaldoInicialSistema(new BigDecimal("100.00"));
        corte.setClaveApertura("abrir-1"); return corte;
    }

    private MovimientoFinanciero movimiento(long secuencia, String saldo) {
        MovimientoFinanciero movimiento = new MovimientoFinanciero();
        movimiento.setSecuenciaCuenta(secuencia); movimiento.setSaldoPosterior(new BigDecimal(saldo));
        return movimiento;
    }

    private AperturaCorteCajaRequest apertura(String clave) {
        return new AperturaCorteCajaRequest(1L, 8L, "Turno matutino", clave);
    }

    private CierreCorteCajaRequest cierre(String efectivo, String justificacion, String clave, long version) {
        return new CierreCorteCajaRequest(new BigDecimal(efectivo), justificacion,
                "Conteo terminado", clave, version);
    }
}
