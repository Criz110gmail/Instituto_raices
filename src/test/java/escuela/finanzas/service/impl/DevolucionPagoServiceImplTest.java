package escuela.finanzas.service.impl;

import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.repository.CargoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.DevolucionPagoRequest;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.tutor.entity.Tutor;
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

class DevolucionPagoServiceImplTest {
    private final PagoRepository pagos = mock(PagoRepository.class);
    private final DevolucionPagoRepository devoluciones = mock(DevolucionPagoRepository.class);
    private final AplicacionPagoRepository aplicaciones = mock(AplicacionPagoRepository.class);
    private final CuentaFinancieraRepository cuentas = mock(CuentaFinancieraRepository.class);
    private final MovimientoFinancieroRepository movimientos = mock(MovimientoFinancieroRepository.class);
    private final MotivoFinancieroRepository motivos = mock(MotivoFinancieroRepository.class);
    private final CargoRepository cargos = mock(CargoRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final RegistroAuditoriaService auditoria = mock(RegistroAuditoriaService.class);
    private final DevolucionPagoServiceImpl service = new DevolucionPagoServiceImpl(
            pagos, devoluciones, aplicaciones, cuentas, movimientos, motivos, cargos, usuarios, alcance, auditoria);

    private Institucion institucion;
    private Pago pago;
    private CuentaFinanciera cuenta;
    private Cargo cargo;

    @BeforeEach
    void preparar() {
        institucion = new Institucion(); institucion.setId(1L); institucion.setNombre("Raíces");
        institucion.setZonaHoraria("America/Mexico_City");
        Plantel plantel = new Plantel(); plantel.setId(2L); plantel.setNombre("Centro");
        plantel.setInstitucion(institucion);
        Tutor tutor = new Tutor(); tutor.setId(3L); tutor.setInstitucion(institucion);
        pago = new Pago(); pago.setId(50L); pago.setVersion(0L); pago.setInstitucion(institucion);
        pago.setPlantelRegistro(plantel); pago.setTutor(tutor); pago.setFolio("PAG-001");
        pago.setFechaPago(Instant.now().minus(Duration.ofDays(1))); pago.setMonto(new BigDecimal("1000.00"));
        pago.setMoneda("MXN"); pago.setEstado(EstadoPago.VALIDADO);
        cuenta = new CuentaFinanciera(); cuenta.setId(8L); cuenta.setInstitucion(institucion);
        cuenta.setPlantel(plantel); cuenta.setNombre("Caja principal"); cuenta.setMoneda("MXN");
        cuenta.setSaldoInicial(new BigDecimal("2000.00")); cuenta.setFechaSaldoInicial(LocalDate.now().minusYears(1));
        cuenta.setActivo(true);
        cargo = new Cargo(); cargo.setId(10L);

        when(pagos.findByIdForUpdate(50L)).thenReturn(Optional.of(pago));
        when(cuentas.findByIdForUpdate(8L)).thenReturn(Optional.of(cuenta));
        when(devoluciones.findByInstitucionIdAndClaveIdempotencia(1L, "devolucion-1"))
                .thenReturn(Optional.empty());
        when(devoluciones.findAllByPagoIdAndEstadoOrderByFechaDescIdDesc(50L, EstadoDevolucionPago.EJECUTADA))
                .thenReturn(List.of());
        when(aplicaciones.findAplicacionesActivasByPagoIdForUpdate(50L)).thenReturn(List.of());
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(8L)).thenReturn(Optional.empty());
        when(devoluciones.saveAndFlush(any())).thenAnswer(i -> {
            DevolucionPago d = i.getArgument(0); d.setId(70L); return d;
        });
        MotivoFinanciero motivo = new MotivoFinanciero(); motivo.setId(4L); motivo.setInstitucion(institucion);
        when(motivos.findByInstitucionIdAndCodigoIgnoreCase(1L, "DEVOLUCION_PAGO"))
                .thenReturn(Optional.of(motivo));
        when(movimientos.saveAndFlush(any())).thenAnswer(i -> {
            MovimientoFinanciero m = i.getArgument(0); m.setId(90L); return m;
        });
        Usuario actor = new Usuario(); actor.setId(9L); actor.setUsername("tesoreria"); actor.setInstitucion(institucion);
        when(usuarios.findById(9L)).thenReturn(Optional.of(actor));
        var principal = new UsuarioPrincipal(9L, 1L, Set.of(2L), false, false,
                "tesoreria", "x", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void limpiar() { SecurityContextHolder.clearContext(); }

    @Test
    void devuelveDisponibleYPublicaUnSoloEgreso() {
        var respuesta = service.ejecutar(request("200.00", List.of()));

        assertThat(respuesta.monto()).isEqualByComparingTo("200.00");
        assertThat(respuesta.movimiento().saldoAnterior()).isEqualByComparingTo("2000.00");
        assertThat(respuesta.movimiento().saldoPosterior()).isEqualByComparingTo("1800.00");
        ArgumentCaptor<MovimientoFinanciero> captor = ArgumentCaptor.forClass(MovimientoFinanciero.class);
        verify(movimientos).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getClase()).isEqualTo(ClaseMovimiento.DEVOLUCION);
        assertThat(captor.getValue().getDireccion()).isEqualTo(DireccionMovimiento.EGRESO);
        verify(aplicaciones, never()).saveAllAndFlush(any());
    }

    @Test
    void devolucionParcialRevierteAplicacionCompletaYReaplicaElRemanente() {
        AplicacionPago original = aplicacion(20L, "600.00");
        when(aplicaciones.findAplicacionesActivasByPagoIdForUpdate(50L)).thenReturn(List.of(original));
        when(cargos.findByIdForUpdate(10L)).thenReturn(Optional.of(cargo));

        service.ejecutar(request("700.00", List.of(20L)));

        ArgumentCaptor<List<AplicacionPago>> captor = ArgumentCaptor.forClass(List.class);
        verify(aplicaciones).saveAllAndFlush(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue().get(0).getOperacion()).isEqualTo(OperacionAplicacionPago.REVERTIR);
        assertThat(captor.getValue().get(0).getMonto()).isEqualByComparingTo("600.00");
        assertThat(captor.getValue().get(0).getReversaDe()).isSameAs(original);
        assertThat(captor.getValue().get(1).getOperacion()).isEqualTo(OperacionAplicacionPago.APLICAR);
        assertThat(captor.getValue().get(1).getMonto()).isEqualByComparingTo("300.00");
    }

    @Test
    void exigeSeleccionarAbonosSuficientesAntesDeCrearLaDevolucion() {
        when(aplicaciones.findAplicacionesActivasByPagoIdForUpdate(50L))
                .thenReturn(List.of(aplicacion(20L, "600.00")));

        assertThatThrownBy(() -> service.ejecutar(request("700.00", List.of())))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("aplicaciones suficientes");
        verify(devoluciones, never()).saveAndFlush(any());
        verify(movimientos, never()).saveAndFlush(any());
    }

    @Test
    void impideDevolverMasQueElImportePendienteDelPago() {
        DevolucionPago previa = new DevolucionPago(); previa.setMonto(new BigDecimal("300.00"));
        when(devoluciones.findAllByPagoIdAndEstadoOrderByFechaDescIdDesc(50L, EstadoDevolucionPago.EJECUTADA))
                .thenReturn(List.of(previa));

        assertThatThrownBy(() -> service.ejecutar(request("700.01", List.of())))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("supera el importe");
        verify(devoluciones, never()).saveAndFlush(any());
    }

    @Test
    void rechazaCuentaSinSaldoSuficiente() {
        cuenta.setSaldoInicial(new BigDecimal("99.99"));
        assertThatThrownBy(() -> service.ejecutar(request("100.00", List.of())))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("saldo suficiente");
        verify(devoluciones, never()).saveAndFlush(any());
    }

    private AplicacionPago aplicacion(Long id, String monto) {
        AplicacionPago aplicacion = new AplicacionPago(); aplicacion.setId(id); aplicacion.setPago(pago);
        aplicacion.setCargo(cargo); aplicacion.setMonto(new BigDecimal(monto));
        aplicacion.setOperacion(OperacionAplicacionPago.APLICAR); return aplicacion;
    }

    private DevolucionPagoRequest request(String monto, List<Long> aplicaciones) {
        return new DevolucionPagoRequest(50L, 8L,
                LocalDateTime.now(ZoneId.of("America/Mexico_City")).minusMinutes(1),
                new BigDecimal(monto), "Pago duplicado", "María López", "REF-1",
                aplicaciones, "devolucion-1", 0L);
    }
}
