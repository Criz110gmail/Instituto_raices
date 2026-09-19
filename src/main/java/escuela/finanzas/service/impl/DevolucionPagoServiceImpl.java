package escuela.finanzas.service.impl;

import escuela.cobranza.entity.Cargo;
import escuela.cobranza.repository.CargoRepository;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.DevolucionPagoRequest;
import escuela.finanzas.dto.response.*;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
import escuela.finanzas.service.DevolucionPagoService;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class DevolucionPagoServiceImpl implements DevolucionPagoService {
    private static final String MOTIVO_DEVOLUCION = "DEVOLUCION_PAGO";
    private final PagoRepository pagoRepository;
    private final DevolucionPagoRepository devolucionRepository;
    private final AplicacionPagoRepository aplicacionRepository;
    private final CuentaFinancieraRepository cuentaRepository;
    private final MovimientoFinancieroRepository movimientoRepository;
    private final MotivoFinancieroRepository motivoRepository;
    private final CargoRepository cargoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlcanceDatosService alcance;

    @Override
    public DevolucionPagoResponse ejecutar(DevolucionPagoRequest request) {
        Pago pago = pagoRepository.findByIdForUpdate(request.pagoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el pago", request.pagoId()));
        if (pago.getEstado() != EstadoPago.VALIDADO)
            throw new ReglaNegocioException("Sólo se puede devolver un pago validado");
        verificar(pago, request.pagoVersion(), "Pago");

        CuentaFinanciera cuenta = cuentaRepository.findByIdForUpdate(request.cuentaOrigenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta de origen", request.cuentaOrigenId()));
        validarCuenta(pago, cuenta);
        Usuario actor = actorPersistido(pago);

        var existente = devolucionRepository.findByInstitucionIdAndClaveIdempotencia(
                pago.getInstitucion().getId(), request.claveIdempotencia());
        if (existente.isPresent()) {
            validarReintento(existente.get(), request);
            return respuesta(existente.get());
        }

        ZoneId zona = ZoneId.of(pago.getInstitucion().getZonaHoraria());
        Instant fecha = request.fecha().atZone(zona).toInstant();
        if (fecha.isAfter(Instant.now()))
            throw new ReglaNegocioException("La fecha de devolución no puede estar en el futuro");
        if (fecha.isBefore(pago.getFechaPago()) || request.fecha().toLocalDate().isBefore(cuenta.getFechaSaldoInicial()))
            throw new ReglaNegocioException("La devolución no puede ser anterior al pago ni a la apertura de la cuenta");

        BigDecimal monto = request.monto().setScale(2, RoundingMode.HALF_UP);
        List<DevolucionPago> previas = ejecutadas(pago.getId());
        BigDecimal devuelto = sumaDevoluciones(previas);
        if (devuelto.add(monto).compareTo(pago.getMonto()) > 0)
            throw new ReglaNegocioException("El monto supera el importe del pago pendiente de devolver");

        List<AplicacionPago> activas = aplicacionRepository.findAplicacionesActivasByPagoIdForUpdate(pago.getId());
        BigDecimal aplicado = sumaAplicaciones(activas);
        BigDecimal disponible = pago.getMonto().subtract(aplicado).subtract(devuelto)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal porLiberar = monto.subtract(disponible).max(BigDecimal.ZERO);
        List<AplicacionPago> seleccionadas = seleccionar(activas, request.aplicacionIdsRevertir());
        if (sumaAplicaciones(seleccionadas).compareTo(porLiberar) < 0)
            throw new ReglaNegocioException("Selecciona aplicaciones suficientes para liberar el importe que se devolverá");

        seleccionadas.stream().map(a -> a.getCargo().getId()).distinct().sorted()
                .forEach(id -> cargoRepository.findByIdForUpdate(id)
                        .orElseThrow(() -> new RecursoNoEncontradoException("el cargo", id)));
        EstadoCuenta estadoCuenta = estado(cuenta);
        if (estadoCuenta.saldo().compareTo(monto) < 0)
            throw new ReglaNegocioException("La cuenta de origen no tiene saldo suficiente para ejecutar la devolución");

        DevolucionPago devolucion = new DevolucionPago();
        devolucion.setInstitucion(pago.getInstitucion()); devolucion.setPago(pago);
        devolucion.setCuentaOrigen(cuenta); devolucion.setMonto(monto); devolucion.setFecha(fecha);
        devolucion.setMotivo(limpiar(request.motivo())); devolucion.setBeneficiario(limpiar(request.beneficiario()));
        devolucion.setReferencia(limpiar(request.referencia())); devolucion.setAutorizadoPor(actor);
        devolucion.setClaveIdempotencia(request.claveIdempotencia());
        devolucion.setEstado(EstadoDevolucionPago.EJECUTADA);
        devolucion = devolucionRepository.saveAndFlush(devolucion);

        List<AplicacionPago> ajustes = ajustarAplicaciones(pago, seleccionadas, porLiberar,
                devolucion, fecha, request.motivo());
        if (!ajustes.isEmpty()) aplicacionRepository.saveAllAndFlush(ajustes);

        motivoRepository.crearDevolucionPagoSiAusente(pago.getInstitucion().getId(), actor.getId());
        MotivoFinanciero motivo = motivoRepository
                .findByInstitucionIdAndCodigoIgnoreCase(pago.getInstitucion().getId(), MOTIVO_DEVOLUCION)
                .orElseThrow(() -> new IllegalStateException("No fue posible preparar el motivo de devolución"));
        MovimientoFinanciero movimiento = new MovimientoFinanciero();
        movimiento.setInstitucion(pago.getInstitucion()); movimiento.setCuenta(cuenta);
        movimiento.setPlantelOperacion(pago.getPlantelRegistro()); movimiento.setFechaOperacion(fecha);
        movimiento.setSecuenciaCuenta(estadoCuenta.secuencia()); movimiento.setDireccion(DireccionMovimiento.EGRESO);
        movimiento.setClase(ClaseMovimiento.DEVOLUCION); movimiento.setMonto(monto);
        movimiento.setMotivoFinanciero(motivo); movimiento.setConcepto("Devolución del pago " + pago.getFolio());
        movimiento.setReferencia(devolucion.getReferencia()); movimiento.setTerceroNombre(devolucion.getBeneficiario());
        movimiento.setDevolucionPago(devolucion);
        movimiento.setClaveIdempotencia("DEVOLUCION:PAGO:" + devolucion.getId());
        movimiento.setSaldoAnterior(estadoCuenta.saldo());
        movimiento.setSaldoPosterior(estadoCuenta.saldo().subtract(monto));
        movimiento = movimientoRepository.saveAndFlush(movimiento);
        devolucion.setMovimiento(movimiento);
        return respuesta(devolucion, movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenDevolucionPagoResponse resumen(Long pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el pago", pagoId));
        List<AplicacionPago> todas = aplicacionRepository.findAllByPagoIdOrderByIdAsc(pagoId);
        Set<Long> reversadas = todas.stream().filter(a -> a.getOperacion() == OperacionAplicacionPago.REVERTIR)
                .map(a -> a.getReversaDe().getId()).collect(Collectors.toSet());
        List<AplicacionPago> activas = todas.stream()
                .filter(a -> a.getOperacion() == OperacionAplicacionPago.APLICAR && !reversadas.contains(a.getId()))
                .toList();
        List<DevolucionPago> devoluciones = ejecutadas(pagoId);
        BigDecimal aplicado = sumaAplicaciones(activas);
        BigDecimal devuelto = sumaDevoluciones(devoluciones);
        return new ResumenDevolucionPagoResponse(pago.getMonto(), aplicado, devuelto,
                pago.getMonto().subtract(aplicado).subtract(devuelto).max(BigDecimal.ZERO),
                activas.stream().map(this::aplicacionResponse).toList(),
                devoluciones.stream().map(this::respuesta).toList());
    }

    private List<AplicacionPago> ajustarAplicaciones(Pago pago, List<AplicacionPago> seleccionadas,
                                                      BigDecimal porLiberar, DevolucionPago devolucion,
                                                      Instant fecha, String motivo) {
        List<AplicacionPago> ajustes = new ArrayList<>();
        BigDecimal restante = porLiberar;
        for (AplicacionPago original : seleccionadas) {
            if (restante.signum() <= 0) break;
            BigDecimal liberado = original.getMonto().min(restante);
            AplicacionPago reversa = new AplicacionPago();
            reversa.setPago(pago); reversa.setCargo(original.getCargo()); reversa.setMonto(original.getMonto());
            reversa.setOperacion(OperacionAplicacionPago.REVERTIR); reversa.setFechaAplicacion(fecha);
            reversa.setReversaDe(original); reversa.setMotivo(limpiar(motivo)); reversa.setDevolucionPago(devolucion);
            ajustes.add(reversa);
            BigDecimal remanente = original.getMonto().subtract(liberado);
            if (remanente.signum() > 0) {
                AplicacionPago reaplicacion = new AplicacionPago();
                reaplicacion.setPago(pago); reaplicacion.setCargo(original.getCargo());
                reaplicacion.setMonto(remanente); reaplicacion.setOperacion(OperacionAplicacionPago.APLICAR);
                reaplicacion.setFechaAplicacion(fecha); reaplicacion.setDevolucionPago(devolucion);
                ajustes.add(reaplicacion);
            }
            restante = restante.subtract(liberado);
        }
        return ajustes;
    }

    private List<AplicacionPago> seleccionar(List<AplicacionPago> activas, List<Long> ids) {
        Set<Long> solicitadas = new LinkedHashSet<>(ids);
        List<AplicacionPago> seleccionadas = activas.stream().filter(a -> solicitadas.contains(a.getId())).toList();
        if (seleccionadas.size() != solicitadas.size())
            throw new ReglaNegocioException("Una aplicación seleccionada ya no está disponible para reversarse");
        return seleccionadas;
    }

    private void validarCuenta(Pago pago, CuentaFinanciera cuenta) {
        if (!cuenta.isActivo() || !cuenta.getInstitucion().getId().equals(pago.getInstitucion().getId()))
            throw new ReglaNegocioException("La cuenta de devolución no está activa o no pertenece a la institución");
        if (!cuenta.getMoneda().equals(pago.getMoneda()))
            throw new ReglaNegocioException("La cuenta de devolución debe usar la moneda del pago");
        if (cuenta.getPlantel() == null) alcance.validarAdministracionInstitucional(pago.getInstitucion().getId());
        else alcance.validarPlantel(cuenta.getPlantel().getId());
    }

    private Usuario actorPersistido(Pago pago) {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioPrincipal principal)
                || principal.accesoRecuperacion() || principal.usuarioId() == null)
            throw new ReglaNegocioException("La devolución requiere una cuenta administrativa identificable; el acceso de recuperación no puede devolver fondos");
        Usuario actor = usuarioRepository.findById(principal.usuarioId())
                .orElseThrow(() -> new AccessDeniedException("El usuario de la sesión ya no está disponible"));
        if (!actor.getInstitucion().getId().equals(pago.getInstitucion().getId()))
            throw new AccessDeniedException("El pago no pertenece a la institución de la sesión");
        return actor;
    }

    private EstadoCuenta estado(CuentaFinanciera cuenta) {
        var ultimo = movimientoRepository.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(cuenta.getId());
        return new EstadoCuenta(ultimo.map(m -> m.getSecuenciaCuenta() + 1).orElse(1L),
                ultimo.map(MovimientoFinanciero::getSaldoPosterior).orElse(cuenta.getSaldoInicial())
                        .setScale(2, RoundingMode.HALF_UP));
    }

    private List<DevolucionPago> ejecutadas(Long pagoId) {
        return devolucionRepository.findAllByPagoIdAndEstadoOrderByFechaDescIdDesc(
                pagoId, EstadoDevolucionPago.EJECUTADA);
    }

    private BigDecimal sumaAplicaciones(List<AplicacionPago> aplicaciones) {
        return aplicaciones.stream().map(AplicacionPago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal sumaDevoluciones(List<DevolucionPago> devoluciones) {
        return devoluciones.stream().map(DevolucionPago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void validarReintento(DevolucionPago devolucion, DevolucionPagoRequest request) {
        if (!devolucion.getPago().getId().equals(request.pagoId())
                || !devolucion.getCuentaOrigen().getId().equals(request.cuentaOrigenId())
                || devolucion.getMonto().compareTo(request.monto()) != 0)
            throw new ReglaNegocioException("La clave de reintento ya pertenece a otra devolución");
    }

    private DevolucionPagoResponse respuesta(DevolucionPago devolucion) {
        MovimientoFinanciero movimiento = movimientoRepository.findByDevolucionPagoId(devolucion.getId())
                .orElse(null);
        return respuesta(devolucion, movimiento);
    }

    private DevolucionPagoResponse respuesta(DevolucionPago devolucion, MovimientoFinanciero movimiento) {
        return new DevolucionPagoResponse(devolucion.getId(), devolucion.getPago().getId(),
                devolucion.getCuentaOrigen().getId(), devolucion.getCuentaOrigen().getNombre(),
                devolucion.getFecha(), devolucion.getMonto(), devolucion.getPago().getMoneda(),
                devolucion.getMotivo(), devolucion.getBeneficiario(), devolucion.getReferencia(),
                devolucion.getAutorizadoPor().getUsername(), devolucion.getEstado(),
                movimiento == null ? null : new MovimientoFinancieroResponse(movimiento.getId(),
                        movimiento.getCuenta().getId(), movimiento.getCuenta().getNombre(),
                        movimiento.getFechaOperacion(), movimiento.getSecuenciaCuenta(), movimiento.getMonto(),
                        movimiento.getCuenta().getMoneda(), movimiento.getSaldoAnterior(), movimiento.getSaldoPosterior()));
    }

    private AplicacionPagoResponse aplicacionResponse(AplicacionPago aplicacion) {
        Cargo cargo = aplicacion.getCargo(); var alumno = cargo.getInscripcion().getAlumno();
        String nombre = java.util.stream.Stream.of(alumno.getNombres(), alumno.getPrimerApellido(), alumno.getSegundoApellido())
                .filter(v -> v != null && !v.isBlank()).collect(Collectors.joining(" "));
        return new AplicacionPagoResponse(aplicacion.getId(), cargo.getId(), nombre, alumno.getMatricula(),
                cargo.getConceptoCobro().getNombre(), cargo.getDescripcion(), aplicacion.getMonto(),
                cargo.getMoneda(), aplicacion.getFechaAplicacion());
    }

    private record EstadoCuenta(long secuencia, BigDecimal saldo) { }
}
