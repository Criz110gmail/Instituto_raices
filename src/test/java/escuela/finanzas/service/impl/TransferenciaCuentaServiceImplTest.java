package escuela.finanzas.service.impl;

import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.TransferenciaCuentaRequest;
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
import org.mockito.InOrder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransferenciaCuentaServiceImplTest {
    private final CuentaFinancieraRepository cuentas = mock(CuentaFinancieraRepository.class);
    private final TransferenciaCuentaRepository transferencias = mock(TransferenciaCuentaRepository.class);
    private final MovimientoFinancieroRepository movimientos = mock(MovimientoFinancieroRepository.class);
    private final MotivoFinancieroRepository motivos = mock(MotivoFinancieroRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final RegistroAuditoriaService auditoria = mock(RegistroAuditoriaService.class);
    private final TransferenciaCuentaServiceImpl service = new TransferenciaCuentaServiceImpl(
            cuentas, transferencias, movimientos, motivos, usuarios, alcance, auditoria);

    private Institucion institucion;
    private CuentaFinanciera origen;
    private CuentaFinanciera destino;

    @BeforeEach
    void preparar() {
        institucion = new Institucion(); institucion.setId(1L); institucion.setNombre("Raíces");
        institucion.setZonaHoraria("America/Mexico_City");
        Plantel plantel = new Plantel(); plantel.setId(2L); plantel.setInstitucion(institucion);
        origen = cuenta(8L, "Caja", "100.00", plantel);
        destino = cuenta(3L, "Banco", "20.00", plantel);
        when(cuentas.findByIdForUpdate(3L)).thenReturn(Optional.of(destino));
        when(cuentas.findByIdForUpdate(8L)).thenReturn(Optional.of(origen));
        when(transferencias.findByInstitucionIdAndClaveIdempotencia(1L, "transferencia-1"))
                .thenReturn(Optional.empty());
        Usuario actor = new Usuario(); actor.setId(9L); actor.setInstitucion(institucion);
        when(usuarios.findById(9L)).thenReturn(Optional.of(actor));
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(anyLong())).thenReturn(Optional.empty());
        when(transferencias.saveAndFlush(any())).thenAnswer(i -> {
            TransferenciaCuenta t=i.getArgument(0);t.setId(40L);return t;
        });
        MotivoFinanciero motivo = new MotivoFinanciero(); motivo.setId(5L); motivo.setInstitucion(institucion);
        motivo.setNaturaleza(NaturalezaMotivoFinanciero.AMBOS); motivo.setActivo(true);
        when(motivos.findByInstitucionIdAndCodigoIgnoreCase(1L, "TRASPASO_INTERNO"))
                .thenReturn(Optional.of(motivo));
        when(movimientos.saveAllAndFlush(any())).thenAnswer(i -> {
            List<MovimientoFinanciero> lista = i.getArgument(0);
            lista.get(0).setId(50L); lista.get(1).setId(51L); return lista;
        });
        var principal = new UsuarioPrincipal(9L, 1L, Set.of(2L), false, false,
                "tesoreria", "x", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void limpiar() { SecurityContextHolder.clearContext(); }

    @Test
    void aplicaSalidaYEntradaAtomicasBloqueandoPorIdAscendente() {
        var respuesta = service.transferir(request(8L, 3L, "30.00"));

        assertThat(respuesta.salida().saldoAnterior()).isEqualByComparingTo("100.00");
        assertThat(respuesta.salida().saldoPosterior()).isEqualByComparingTo("70.00");
        assertThat(respuesta.entrada().saldoAnterior()).isEqualByComparingTo("20.00");
        assertThat(respuesta.entrada().saldoPosterior()).isEqualByComparingTo("50.00");
        InOrder orden = inOrder(cuentas);
        orden.verify(cuentas).findByIdForUpdate(3L);
        orden.verify(cuentas).findByIdForUpdate(8L);
        ArgumentCaptor<List<MovimientoFinanciero>> captor = ArgumentCaptor.forClass(List.class);
        verify(movimientos).saveAllAndFlush(captor.capture());
        assertThat(captor.getValue()).extracting(MovimientoFinanciero::getClase)
                .containsExactly(ClaseMovimiento.TRASPASO, ClaseMovimiento.TRASPASO);
        assertThat(captor.getValue()).extracting(MovimientoFinanciero::getDireccion)
                .containsExactly(DireccionMovimiento.EGRESO, DireccionMovimiento.INGRESO);
        assertThat(captor.getValue()).allMatch(m -> m.getTransferencia().getId().equals(40L));
    }

    @Test
    void rechazaFondosInsuficientesSinCrearTransferencia() {
        assertThatThrownBy(() -> service.transferir(request(8L, 3L, "100.01")))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("saldo suficiente");
        verify(transferencias, never()).saveAndFlush(any());
        verify(movimientos, never()).saveAllAndFlush(any());
    }

    @Test
    void rechazaMonedasDiferentes() {
        destino.setMoneda("USD");
        assertThatThrownBy(() -> service.transferir(request(8L, 3L, "10.00")))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("misma moneda");
    }

    @Test
    void rechazaLaMismaCuentaAntesDeSolicitarBloqueos() {
        assertThatThrownBy(() -> service.transferir(request(8L, 8L, "10.00")))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("diferentes");
        verifyNoInteractions(cuentas);
    }

    @Test
    void accesoDeRecuperacionNoPuedeTransferirFondos() {
        var principal = new UsuarioPrincipal(null, null, Set.of(), true, true, "bootstrap", "x", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        assertThatThrownBy(() -> service.transferir(request(8L, 3L, "10.00")))
                .hasMessageContaining("acceso de recuperación");
    }

    private CuentaFinanciera cuenta(Long id, String nombre, String saldo, Plantel plantel) {
        CuentaFinanciera cuenta = new CuentaFinanciera(); cuenta.setId(id); cuenta.setNombre(nombre);
        cuenta.setInstitucion(institucion); cuenta.setPlantel(plantel); cuenta.setActivo(true);
        cuenta.setMoneda("MXN"); cuenta.setSaldoInicial(new BigDecimal(saldo));
        cuenta.setFechaSaldoInicial(LocalDate.now().minusYears(1)); return cuenta;
    }

    private TransferenciaCuentaRequest request(Long origenId, Long destinoId, String monto) {
        return new TransferenciaCuentaRequest(1L, origenId, destinoId,
                LocalDateTime.now(ZoneId.of("America/Mexico_City")).minusMinutes(1),
                new BigDecimal(monto), "REF-1", "Depósito interno", "transferencia-1");
    }
}
