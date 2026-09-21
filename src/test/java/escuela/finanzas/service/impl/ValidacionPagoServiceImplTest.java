package escuela.finanzas.service.impl;

import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.CargoRepository;
import escuela.finanzas.dto.request.*;
import escuela.finanzas.entity.*;
import escuela.finanzas.mapper.PagoMapper;
import escuela.finanzas.repository.*;
import escuela.institucion.entity.*;
import escuela.inscripcion.entity.Inscripcion;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.tutor.entity.Tutor;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ValidacionPagoServiceImplTest {
    private final PagoRepository pagos = mock(PagoRepository.class);
    private final CuentaFinancieraRepository cuentas = mock(CuentaFinancieraRepository.class);
    private final CargoRepository cargos = mock(CargoRepository.class);
    private final SolicitudAplicacionPagoRepository solicitudes = mock(SolicitudAplicacionPagoRepository.class);
    private final AplicacionPagoRepository aplicaciones = mock(AplicacionPagoRepository.class);
    private final MovimientoFinancieroRepository movimientos = mock(MovimientoFinancieroRepository.class);
    private final MotivoFinancieroRepository motivos = mock(MotivoFinancieroRepository.class);
    private final AlumnoTutorRepository vinculos = mock(AlumnoTutorRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final RegistroAuditoriaService auditoria = mock(RegistroAuditoriaService.class);
    private final ValidacionPagoServiceImpl service = new ValidacionPagoServiceImpl(pagos, cuentas, cargos,
            solicitudes, aplicaciones, movimientos, motivos, vinculos, usuarios, new PagoMapper(), auditoria);

    private Institucion institucion;
    private Plantel plantel;
    private Tutor tutor;
    private Usuario actor;
    private Pago pago;
    private CuentaFinanciera cuenta;

    @BeforeEach
    void preparar() {
        institucion = new Institucion(); institucion.setId(1L); institucion.setNombre("Raíces");
        institucion.setZonaHoraria("America/Mexico_City");
        plantel = new Plantel(); plantel.setId(2L); plantel.setNombre("Centro"); plantel.setInstitucion(institucion);
        tutor = new Tutor(); tutor.setId(3L); tutor.setNombres("María"); tutor.setPrimerApellido("López"); tutor.setInstitucion(institucion);
        actor = new Usuario(); actor.setId(9L); actor.setUsername("cajera"); actor.setInstitucion(institucion);
        pago = new Pago(); pago.setId(50L); pago.setVersion(0L); pago.setInstitucion(institucion);
        pago.setPlantelRegistro(plantel); pago.setTutor(tutor); pago.setFolio("PAG-001");
        pago.setFechaPago(Instant.now().minusSeconds(60)); pago.setMonto(new BigDecimal("1000.00"));
        pago.setMoneda("MXN"); pago.setMetodo(MetodoPago.EFECTIVO); pago.setEstado(EstadoPago.PENDIENTE_VALIDACION);
        cuenta = new CuentaFinanciera(); cuenta.setId(8L); cuenta.setInstitucion(institucion); cuenta.setPlantel(plantel);
        cuenta.setNombre("Caja principal"); cuenta.setMoneda("MXN"); cuenta.setTipo(TipoCuentaFinanciera.CAJA);
        cuenta.setActivo(true); cuenta.setSaldoInicial(new BigDecimal("100.00")); cuenta.setFechaSaldoInicial(LocalDate.now().minusYears(1));
        when(pagos.findByIdForUpdate(50L)).thenReturn(Optional.of(pago));
        when(pagos.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        when(cuentas.findByIdForUpdate(8L)).thenReturn(Optional.of(cuenta));
        when(usuarios.findById(9L)).thenReturn(Optional.of(actor));
        when(movimientos.findByPagoId(50L)).thenReturn(Optional.empty());
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(8L)).thenReturn(Optional.empty());
        MotivoFinanciero motivo = new MotivoFinanciero(); motivo.setId(7L); motivo.setInstitucion(institucion);
        when(motivos.findByInstitucionIdAndCodigoIgnoreCase(1L, "COBROS_ESCOLARES")).thenReturn(Optional.of(motivo));
        when(aplicaciones.save(any())).thenAnswer(i -> i.getArgument(0));
        when(movimientos.save(any())).thenAnswer(i -> { MovimientoFinanciero m=i.getArgument(0);m.setId(80L);return m; });
        when(vinculos.tieneResponsabilidadFinancieraVigente(anyLong(), eq(3L), any())).thenReturn(true);
        var principal = new UsuarioPrincipal(9L, 1L, Set.of(2L), true, false, "cajera", "x",
                List.of(new SimpleGrantedAuthority("PAGO_VALIDAR")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void limpiar() { SecurityContextHolder.clearContext(); }

    @Test
    void validaUnaVezAjustandoCadaSolicitudAlSaldoVigente() {
        Cargo cargoA = cargo(10L, "A-01", "Ana", "1000.00");
        Cargo cargoB = cargo(11L, "B-01", "Bruno", "300.00");
        AplicacionPago previa = new AplicacionPago(); previa.setMonto(new BigDecimal("800.00"));
        previa.setOperacion(OperacionAplicacionPago.APLICAR); cargoA.getAplicaciones().add(previa);
        SolicitudAplicacionPago solicitudA = solicitud(70L, cargoA, "500.00");
        SolicitudAplicacionPago solicitudB = solicitud(71L, cargoB, "300.00");
        when(solicitudes.findAllByPagoIdOrderByCargoIdAsc(50L)).thenReturn(List.of(solicitudA, solicitudB));
        when(cargos.findByIdForUpdate(10L)).thenReturn(Optional.of(cargoA));
        when(cargos.findByIdForUpdate(11L)).thenReturn(Optional.of(cargoB));

        var respuesta = service.validar(50L, new ValidacionPagoRequest(8L, 0L));

        assertThat(respuesta.estado()).isEqualTo(EstadoPago.VALIDADO);
        assertThat(respuesta.montoAplicado()).isEqualByComparingTo("500.00");
        assertThat(respuesta.montoDisponible()).isEqualByComparingTo("500.00");
        assertThat(respuesta.movimiento().monto()).isEqualByComparingTo("1000.00");
        assertThat(respuesta.movimiento().saldoPosterior()).isEqualByComparingTo("1100.00");
        ArgumentCaptor<AplicacionPago> captor = ArgumentCaptor.forClass(AplicacionPago.class);
        verify(aplicaciones, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).extracting(AplicacionPago::getMonto)
                .containsExactly(new BigDecimal("200.00"), new BigDecimal("300.00"));
        verify(movimientos, times(1)).save(any());
    }

    @Test
    void rechazoConservaPagoSinMovimientoNiAplicaciones() {
        var respuesta = service.rechazar(50L, new RechazoPagoRequest("Comprobante ilegible", 0L));
        assertThat(respuesta.estado()).isEqualTo(EstadoPago.RECHAZADO);
        assertThat(respuesta.motivoRechazoCancelacion()).isEqualTo("Comprobante ilegible");
        verifyNoInteractions(aplicaciones);
        verify(movimientos, never()).save(any());
    }

    @Test
    void efectivoNoPuedeValidarseEnCuentaBancaria() {
        cuenta.setTipo(TipoCuentaFinanciera.BANCO);
        assertThatThrownBy(() -> service.validar(50L, new ValidacionPagoRequest(8L, 0L)))
                .isInstanceOf(escuela.common.exception.ReglaNegocioException.class)
                .hasMessageContaining("tipo caja");
        verify(movimientos, never()).save(any());
    }

    @Test
    void exigeSeleccionarCuentaDestino() {
        assertThatThrownBy(() -> service.validar(50L, new ValidacionPagoRequest(null, 0L)))
                .isInstanceOf(escuela.common.exception.ReglaNegocioException.class)
                .hasMessageContaining("Selecciona la cuenta destino");
        verify(movimientos, never()).save(any());
    }

    @Test
    void accesoDeRecuperacionNoPuedeAutorizarDinero() {
        var principal = new UsuarioPrincipal(null, null, Set.of(), true, true, "bootstrap", "x",
                List.of(new SimpleGrantedAuthority("PAGO_VALIDAR")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        assertThatThrownBy(() -> service.rechazar(50L, new RechazoPagoRequest("Duplicado", 0L)))
                .hasMessageContaining("cuenta administrativa identificable");
    }

    private Cargo cargo(Long id, String matricula, String nombre, String total) {
        Alumno alumno = new Alumno(); alumno.setId(id + 100); alumno.setMatricula(matricula);
        alumno.setNombres(nombre); alumno.setPrimerApellido("López"); alumno.setInstitucion(institucion);
        Inscripcion inscripcion = new Inscripcion(); inscripcion.setAlumno(alumno); inscripcion.setPlantel(plantel);
        ConceptoCobro concepto = new ConceptoCobro(); concepto.setNombre("Colegiatura");
        Cargo cargo = new Cargo(); cargo.setId(id); cargo.setInscripcion(inscripcion); cargo.setConceptoCobro(concepto);
        cargo.setDescripcion("Septiembre"); cargo.setImporteOriginal(new BigDecimal(total)); cargo.setMoneda("MXN");
        cargo.setEstadoRegistro(EstadoRegistroCargo.EMITIDO); return cargo;
    }

    private SolicitudAplicacionPago solicitud(Long id, Cargo cargo, String monto) {
        SolicitudAplicacionPago solicitud = new SolicitudAplicacionPago(); solicitud.setId(id);
        solicitud.setPago(pago); solicitud.setCargo(cargo); solicitud.setMontoSolicitado(new BigDecimal(monto));
        pago.getSolicitudes().add(solicitud); return solicitud;
    }
}
