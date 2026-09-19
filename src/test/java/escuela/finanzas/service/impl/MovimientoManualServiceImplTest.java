package escuela.finanzas.service.impl;

import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.MovimientoManualRequest;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.finanzas.repository.MotivoFinancieroRepository;
import escuela.finanzas.repository.MovimientoFinancieroRepository;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.institucion.repository.PlantelRepository;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovimientoManualServiceImplTest {
    private final CuentaFinancieraRepository cuentas = mock(CuentaFinancieraRepository.class);
    private final MotivoFinancieroRepository motivos = mock(MotivoFinancieroRepository.class);
    private final MovimientoFinancieroRepository movimientos = mock(MovimientoFinancieroRepository.class);
    private final PlantelRepository planteles = mock(PlantelRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final MovimientoManualServiceImpl service = new MovimientoManualServiceImpl(
            cuentas, motivos, movimientos, planteles, usuarios, alcance);

    private Institucion institucion;
    private Plantel plantel;
    private CuentaFinanciera cuenta;
    private MotivoFinanciero motivo;

    @BeforeEach
    void preparar() {
        institucion = new Institucion(); institucion.setId(1L); institucion.setNombre("Raíces");
        institucion.setZonaHoraria("America/Mexico_City");
        plantel = new Plantel(); plantel.setId(2L); plantel.setNombre("Centro"); plantel.setInstitucion(institucion);
        cuenta = new CuentaFinanciera(); cuenta.setId(3L); cuenta.setNombre("Caja"); cuenta.setInstitucion(institucion);
        cuenta.setPlantel(plantel); cuenta.setActivo(true); cuenta.setMoneda("MXN");
        cuenta.setSaldoInicial(new BigDecimal("100.00")); cuenta.setFechaSaldoInicial(LocalDate.now().minusYears(1));
        motivo = new MotivoFinanciero(); motivo.setId(4L); motivo.setInstitucion(institucion);
        motivo.setActivo(true); motivo.setNaturaleza(NaturalezaMotivoFinanciero.AMBOS);
        Usuario actor = new Usuario(); actor.setId(9L); actor.setInstitucion(institucion);
        when(cuentas.findByIdForUpdate(3L)).thenReturn(Optional.of(cuenta));
        when(motivos.findById(4L)).thenReturn(Optional.of(motivo));
        when(usuarios.findById(9L)).thenReturn(Optional.of(actor));
        when(movimientos.findByInstitucionIdAndClaveIdempotencia(1L, "operacion-1"))
                .thenReturn(Optional.empty());
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(3L)).thenReturn(Optional.empty());
        when(movimientos.saveAndFlush(any())).thenAnswer(invocacion -> {
            MovimientoFinanciero movimiento = invocacion.getArgument(0); movimiento.setId(20L); return movimiento;
        });
        var principal = new UsuarioPrincipal(9L, 1L, Set.of(2L), false, false,
                "tesoreria", "x", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void limpiar() { SecurityContextHolder.clearContext(); }

    @Test
    void registraIngresoConSecuenciaYSaldoCalculadosBajoBloqueo() {
        var respuesta = service.registrar(request(DireccionMovimiento.INGRESO, "25.50"));

        assertThat(respuesta.secuenciaCuenta()).isEqualTo(1L);
        assertThat(respuesta.saldoAnterior()).isEqualByComparingTo("100.00");
        assertThat(respuesta.saldoPosterior()).isEqualByComparingTo("125.50");
        ArgumentCaptor<MovimientoFinanciero> captor = ArgumentCaptor.forClass(MovimientoFinanciero.class);
        verify(movimientos).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getClase()).isEqualTo(ClaseMovimiento.OPERACION);
        assertThat(captor.getValue().getPlantelOperacion()).isEqualTo(plantel);
        verify(alcance).validarPlantel(2L);
    }

    @Test
    void rechazaEgresoQueDejariaSaldoNegativo() {
        assertThatThrownBy(() -> service.registrar(request(DireccionMovimiento.EGRESO, "100.01")))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("saldo disponible");
        verify(movimientos, never()).saveAndFlush(any());
    }

    @Test
    void rechazaMotivoDeNaturalezaContraria() {
        motivo.setNaturaleza(NaturalezaMotivoFinanciero.INGRESO);
        assertThatThrownBy(() -> service.registrar(request(DireccionMovimiento.EGRESO, "10.00")))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("naturaleza");
    }

    @Test
    void accesoDeRecuperacionNoPuedePublicarDinero() {
        var principal = new UsuarioPrincipal(null, null, Set.of(), true, true, "bootstrap", "x", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        assertThatThrownBy(() -> service.registrar(request(DireccionMovimiento.INGRESO, "10.00")))
                .hasMessageContaining("cuenta administrativa identificable");
    }

    private MovimientoManualRequest request(DireccionMovimiento direccion, String monto) {
        return new MovimientoManualRequest(1L, 3L, 2L,
                LocalDateTime.now(ZoneId.of("America/Mexico_City")).minusMinutes(1),
                direccion, 4L, new BigDecimal(monto), "Operación de prueba", "REF-1", "Proveedor",
                "operacion-1");
    }
}
