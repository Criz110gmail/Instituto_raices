package escuela.finanzas.service.impl;

import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.TransferenciaCuentaRequest;
import escuela.finanzas.dto.response.MovimientoFinancieroResponse;
import escuela.finanzas.dto.response.TransferenciaCuentaResponse;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
import escuela.finanzas.service.TransferenciaCuentaService;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferenciaCuentaServiceImpl implements TransferenciaCuentaService {
    private static final String MOTIVO_TRASPASO = "TRASPASO_INTERNO";
    private final CuentaFinancieraRepository cuentaRepository;
    private final TransferenciaCuentaRepository transferenciaRepository;
    private final MovimientoFinancieroRepository movimientoRepository;
    private final MotivoFinancieroRepository motivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlcanceDatosService alcance;

    @Override
    public TransferenciaCuentaResponse transferir(TransferenciaCuentaRequest request) {
        if (request.cuentaOrigenId().equals(request.cuentaDestinoId()))
            throw new ReglaNegocioException("La cuenta de origen y la de destino deben ser diferentes");

        long primerId = Math.min(request.cuentaOrigenId(), request.cuentaDestinoId());
        long segundoId = Math.max(request.cuentaOrigenId(), request.cuentaDestinoId());
        CuentaFinanciera primera = bloquear(primerId);
        CuentaFinanciera segunda = bloquear(segundoId);
        CuentaFinanciera origen = primera.getId().equals(request.cuentaOrigenId()) ? primera : segunda;
        CuentaFinanciera destino = primera.getId().equals(request.cuentaDestinoId()) ? primera : segunda;
        validarCuentas(request, origen, destino);
        Usuario actor = actorPersistido(origen);

        var existente = transferenciaRepository.findByInstitucionIdAndClaveIdempotencia(
                request.institucionId(), request.claveIdempotencia());
        if (existente.isPresent()) {
            validarReintento(existente.get(), request);
            return respuesta(existente.get());
        }

        ZoneId zona = ZoneId.of(origen.getInstitucion().getZonaHoraria());
        Instant fecha = request.fecha().atZone(zona).toInstant();
        if (fecha.isAfter(Instant.now()))
            throw new ReglaNegocioException("La fecha de la transferencia no puede estar en el futuro");
        if (request.fecha().toLocalDate().isBefore(origen.getFechaSaldoInicial())
                || request.fecha().toLocalDate().isBefore(destino.getFechaSaldoInicial()))
            throw new ReglaNegocioException("La fecha de la transferencia no puede ser anterior a la apertura de las cuentas");

        BigDecimal monto = request.monto().setScale(2, RoundingMode.HALF_UP);
        EstadoCuenta estadoOrigen = estado(origen);
        EstadoCuenta estadoDestino = estado(destino);
        if (estadoOrigen.saldo().subtract(monto).signum() < 0)
            throw new ReglaNegocioException("La cuenta de origen no tiene saldo suficiente para la transferencia");

        TransferenciaCuenta transferencia = new TransferenciaCuenta();
        transferencia.setInstitucion(origen.getInstitucion());
        transferencia.setCuentaOrigen(origen); transferencia.setCuentaDestino(destino);
        transferencia.setFecha(fecha); transferencia.setMonto(monto);
        transferencia.setReferencia(limpiar(request.referencia()));
        transferencia.setObservaciones(limpiar(request.observaciones()));
        transferencia.setAutorizadoPor(actor);
        transferencia.setClaveIdempotencia(request.claveIdempotencia());
        transferencia.setEstado(EstadoTransferenciaCuenta.APLICADA);
        transferencia = transferenciaRepository.saveAndFlush(transferencia);

        motivoRepository.crearTraspasoInternoSiAusente(request.institucionId(), actor.getId());
        MotivoFinanciero motivo = motivoRepository
                .findByInstitucionIdAndCodigoIgnoreCase(request.institucionId(), MOTIVO_TRASPASO)
                .orElseThrow(() -> new IllegalStateException("No fue posible preparar el motivo de traspaso"));

        MovimientoFinanciero salida = movimiento(transferencia, origen, motivo, fecha,
                DireccionMovimiento.EGRESO, monto, estadoOrigen,
                "Transferencia a " + destino.getNombre(), "SALIDA");
        MovimientoFinanciero entrada = movimiento(transferencia, destino, motivo, fecha,
                DireccionMovimiento.INGRESO, monto, estadoDestino,
                "Transferencia desde " + origen.getNombre(), "ENTRADA");
        List<MovimientoFinanciero> guardados = movimientoRepository.saveAllAndFlush(List.of(salida, entrada));
        return respuesta(transferencia, guardados.get(0), guardados.get(1));
    }

    private CuentaFinanciera bloquear(Long id) {
        return cuentaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta financiera", id));
    }

    private void validarCuentas(TransferenciaCuentaRequest request, CuentaFinanciera origen,
                                CuentaFinanciera destino) {
        if (!origen.getInstitucion().getId().equals(request.institucionId())
                || !destino.getInstitucion().getId().equals(request.institucionId()))
            throw new ReglaNegocioException("Ambas cuentas deben pertenecer a la institución seleccionada");
        if (!origen.isActivo() || !destino.isActivo())
            throw new ReglaNegocioException("Ambas cuentas deben estar activas");
        if (!origen.getMoneda().equals(destino.getMoneda()))
            throw new ReglaNegocioException("Las cuentas deben utilizar la misma moneda");
        validarAlcance(origen); validarAlcance(destino);
    }

    private void validarAlcance(CuentaFinanciera cuenta) {
        if (cuenta.getPlantel() == null) alcance.validarAdministracionInstitucional(cuenta.getInstitucion().getId());
        else alcance.validarPlantel(cuenta.getPlantel().getId());
    }

    private Usuario actorPersistido(CuentaFinanciera cuenta) {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioPrincipal principal)
                || principal.accesoRecuperacion() || principal.usuarioId() == null)
            throw new ReglaNegocioException("La transferencia requiere una cuenta administrativa identificable; el acceso de recuperación no puede mover fondos");
        Usuario actor = usuarioRepository.findById(principal.usuarioId())
                .orElseThrow(() -> new AccessDeniedException("El usuario de la sesión ya no está disponible"));
        if (!actor.getInstitucion().getId().equals(cuenta.getInstitucion().getId()))
            throw new AccessDeniedException("Las cuentas no pertenecen a la institución de la sesión");
        return actor;
    }

    private EstadoCuenta estado(CuentaFinanciera cuenta) {
        var ultimo = movimientoRepository.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(cuenta.getId());
        return new EstadoCuenta(ultimo.map(m -> m.getSecuenciaCuenta() + 1).orElse(1L),
                ultimo.map(MovimientoFinanciero::getSaldoPosterior).orElse(cuenta.getSaldoInicial())
                        .setScale(2, RoundingMode.HALF_UP));
    }

    private MovimientoFinanciero movimiento(TransferenciaCuenta transferencia, CuentaFinanciera cuenta,
                                             MotivoFinanciero motivo, Instant fecha,
                                             DireccionMovimiento direccion, BigDecimal monto,
                                             EstadoCuenta estadoCuenta, String concepto, String lado) {
        MovimientoFinanciero movimiento = new MovimientoFinanciero();
        movimiento.setInstitucion(transferencia.getInstitucion()); movimiento.setCuenta(cuenta);
        movimiento.setPlantelOperacion(cuenta.getPlantel()); movimiento.setFechaOperacion(fecha);
        movimiento.setSecuenciaCuenta(estadoCuenta.secuencia()); movimiento.setDireccion(direccion);
        movimiento.setClase(ClaseMovimiento.TRASPASO); movimiento.setMonto(monto);
        movimiento.setMotivoFinanciero(motivo); movimiento.setConcepto(concepto);
        movimiento.setReferencia(transferencia.getReferencia()); movimiento.setTransferencia(transferencia);
        movimiento.setClaveIdempotencia("TRASPASO:" + transferencia.getId() + ":" + lado);
        movimiento.setSaldoAnterior(estadoCuenta.saldo());
        movimiento.setSaldoPosterior(direccion == DireccionMovimiento.INGRESO
                ? estadoCuenta.saldo().add(monto) : estadoCuenta.saldo().subtract(monto));
        return movimiento;
    }

    private void validarReintento(TransferenciaCuenta existente, TransferenciaCuentaRequest request) {
        if (!existente.getCuentaOrigen().getId().equals(request.cuentaOrigenId())
                || !existente.getCuentaDestino().getId().equals(request.cuentaDestinoId())
                || existente.getMonto().compareTo(request.monto()) != 0)
            throw new ReglaNegocioException("La clave de reintento ya pertenece a otra transferencia");
    }

    private TransferenciaCuentaResponse respuesta(TransferenciaCuenta transferencia) {
        List<MovimientoFinanciero> movimientos = movimientoRepository
                .findAllByTransferenciaIdOrderByDireccionDesc(transferencia.getId());
        MovimientoFinanciero salida = movimientos.stream().filter(m -> m.getDireccion() == DireccionMovimiento.EGRESO)
                .findFirst().orElseThrow();
        MovimientoFinanciero entrada = movimientos.stream().filter(m -> m.getDireccion() == DireccionMovimiento.INGRESO)
                .findFirst().orElseThrow();
        return respuesta(transferencia, salida, entrada);
    }

    private TransferenciaCuentaResponse respuesta(TransferenciaCuenta transferencia,
                                                   MovimientoFinanciero salida,
                                                   MovimientoFinanciero entrada) {
        return new TransferenciaCuentaResponse(transferencia.getId(), transferencia.getInstitucion().getId(),
                transferencia.getCuentaOrigen().getId(), transferencia.getCuentaOrigen().getNombre(),
                transferencia.getCuentaDestino().getId(), transferencia.getCuentaDestino().getNombre(),
                transferencia.getFecha(), transferencia.getMonto(), transferencia.getCuentaOrigen().getMoneda(),
                transferencia.getReferencia(), transferencia.getEstado(),
                movimientoResponse(salida), movimientoResponse(entrada));
    }

    private MovimientoFinancieroResponse movimientoResponse(MovimientoFinanciero movimiento) {
        return new MovimientoFinancieroResponse(movimiento.getId(), movimiento.getCuenta().getId(),
                movimiento.getCuenta().getNombre(), movimiento.getFechaOperacion(), movimiento.getSecuenciaCuenta(),
                movimiento.getMonto(), movimiento.getCuenta().getMoneda(), movimiento.getSaldoAnterior(),
                movimiento.getSaldoPosterior());
    }

    private record EstadoCuenta(long secuencia, BigDecimal saldo) { }
}
