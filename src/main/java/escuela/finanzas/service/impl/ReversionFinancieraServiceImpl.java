package escuela.finanzas.service.impl;

import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.ReversionFinancieraRequest;
import escuela.finanzas.dto.response.*;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
import escuela.finanzas.service.ReversionFinancieraService;
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

import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class ReversionFinancieraServiceImpl implements ReversionFinancieraService {
    private final MovimientoFinancieroRepository movimientoRepository;
    private final TransferenciaCuentaRepository transferenciaRepository;
    private final ReversionFinancieraRepository reversionRepository;
    private final CuentaFinancieraRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlcanceDatosService alcance;

    @Override
    @Transactional(readOnly = true)
    public ObjetivoReversionResponse obtenerMovimiento(Long movimientoId) {
        MovimientoFinanciero movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el movimiento", movimientoId));
        validarOperacionManual(movimiento);
        validarAlcance(movimiento.getCuenta());
        return objetivoMovimiento(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public ObjetivoReversionResponse obtenerTransferencia(Long transferenciaId) {
        TransferenciaCuenta transferencia = transferenciaRepository.findById(transferenciaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la transferencia", transferenciaId));
        validarAlcance(transferencia.getCuentaOrigen());
        validarAlcance(transferencia.getCuentaDestino());
        return objetivoTransferencia(transferencia);
    }

    @Override
    public ReversionFinancieraResponse revertirMovimiento(Long movimientoId,
                                                           ReversionFinancieraRequest request) {
        MovimientoFinanciero original = movimientoRepository.findByIdForUpdate(movimientoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el movimiento", movimientoId));
        validarOperacionManual(original);
        verificar(original, request.version(), "Movimiento financiero");
        Usuario actor = actorPersistido(original.getInstitucion().getId());
        Optional<ReversionFinanciera> existente = reversionRepository
                .findByInstitucionIdAndClaveIdempotencia(original.getInstitucion().getId(),
                        request.claveIdempotencia());
        if (existente.isPresent()) {
            validarReintentoMovimiento(existente.get(), movimientoId);
            return respuesta(existente.get());
        }
        if (original.getReversa() != null)
            throw new ReglaNegocioException("El movimiento ya tiene una reversa publicada");

        CuentaFinanciera cuenta = cuentaRepository.findByIdForUpdate(original.getCuenta().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta financiera",
                        original.getCuenta().getId()));
        validarCuentaActiva(cuenta); validarAlcance(cuenta);
        Instant fecha = validarFecha(request.fecha(), original.getFechaOperacion(), List.of(cuenta));
        EstadoCuenta estado = estado(cuenta);
        DireccionMovimiento direccion = opuesta(original.getDireccion());
        validarFondos(direccion, estado.saldo(), original.getMonto());

        ReversionFinanciera reversion = cabecera(TipoReversionFinanciera.MOVIMIENTO_MANUAL,
                original.getInstitucion(), original, null, fecha, request, actor);
        reversion = reversionRepository.saveAndFlush(reversion);
        MovimientoFinanciero reversa = reversa(original, cuenta, reversion, fecha, direccion, estado,
                "Reversa de operación: " + original.getConcepto(), "MOVIMIENTO");
        reversa = movimientoRepository.saveAndFlush(reversa);
        return respuesta(reversion, List.of(reversa));
    }

    @Override
    public ReversionFinancieraResponse revertirTransferencia(Long transferenciaId,
                                                              ReversionFinancieraRequest request) {
        TransferenciaCuenta transferencia = transferenciaRepository.findByIdForUpdate(transferenciaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la transferencia", transferenciaId));
        verificar(transferencia, request.version(), "Transferencia");
        Usuario actor = actorPersistido(transferencia.getInstitucion().getId());
        Optional<ReversionFinanciera> existente = reversionRepository
                .findByInstitucionIdAndClaveIdempotencia(transferencia.getInstitucion().getId(),
                        request.claveIdempotencia());
        if (existente.isPresent()) {
            validarReintentoTransferencia(existente.get(), transferenciaId);
            return respuesta(existente.get());
        }
        if (transferencia.getEstado() != EstadoTransferenciaCuenta.APLICADA || transferencia.getReversion() != null)
            throw new ReglaNegocioException("La transferencia ya fue revertida");

        List<MovimientoFinanciero> originales = movimientoRepository
                .findAllByTransferenciaIdOrderByDireccionDesc(transferenciaId);
        if (originales.size() != 2)
            throw new ReglaNegocioException("La transferencia no conserva sus dos movimientos originales");

        CuentaFinanciera primera = bloquear(Math.min(transferencia.getCuentaOrigen().getId(),
                transferencia.getCuentaDestino().getId()));
        CuentaFinanciera segunda = bloquear(Math.max(transferencia.getCuentaOrigen().getId(),
                transferencia.getCuentaDestino().getId()));
        CuentaFinanciera origen = cuenta(transferencia.getCuentaOrigen().getId(), primera, segunda);
        CuentaFinanciera destino = cuenta(transferencia.getCuentaDestino().getId(), primera, segunda);
        validarCuentaActiva(origen); validarCuentaActiva(destino);
        validarAlcance(origen); validarAlcance(destino);
        Instant fecha = validarFecha(request.fecha(), transferencia.getFecha(), List.of(origen, destino));
        EstadoCuenta estadoOrigen = estado(origen); EstadoCuenta estadoDestino = estado(destino);
        validarFondos(DireccionMovimiento.EGRESO, estadoDestino.saldo(), transferencia.getMonto());

        ReversionFinanciera reversion = cabecera(TipoReversionFinanciera.TRANSFERENCIA,
                transferencia.getInstitucion(), null, transferencia, fecha, request, actor);
        reversion = reversionRepository.saveAndFlush(reversion);
        MovimientoFinanciero salidaOriginal = originales.stream()
                .filter(m -> m.getDireccion() == DireccionMovimiento.EGRESO).findFirst().orElseThrow();
        MovimientoFinanciero entradaOriginal = originales.stream()
                .filter(m -> m.getDireccion() == DireccionMovimiento.INGRESO).findFirst().orElseThrow();
        MovimientoFinanciero retornoOrigen = reversa(salidaOriginal, origen, reversion, fecha,
                DireccionMovimiento.INGRESO, estadoOrigen,
                "Reversa de transferencia desde " + destino.getNombre(), "ORIGEN");
        MovimientoFinanciero salidaDestino = reversa(entradaOriginal, destino, reversion, fecha,
                DireccionMovimiento.EGRESO, estadoDestino,
                "Reversa de transferencia a " + origen.getNombre(), "DESTINO");
        List<MovimientoFinanciero> guardados = movimientoRepository
                .saveAllAndFlush(List.of(retornoOrigen, salidaDestino));
        transferencia.setEstado(EstadoTransferenciaCuenta.REVERTIDA);
        transferenciaRepository.saveAndFlush(transferencia);
        return respuesta(reversion, guardados);
    }

    private ReversionFinanciera cabecera(TipoReversionFinanciera tipo,
                                          escuela.institucion.entity.Institucion institucion,
                                          MovimientoFinanciero movimiento, TransferenciaCuenta transferencia,
                                          Instant fecha, ReversionFinancieraRequest request, Usuario actor) {
        ReversionFinanciera reversion = new ReversionFinanciera();
        reversion.setInstitucion(institucion); reversion.setTipo(tipo);
        reversion.setMovimientoOrigen(movimiento); reversion.setTransferenciaOrigen(transferencia);
        reversion.setFecha(fecha); reversion.setMotivo(limpiar(request.motivo()));
        reversion.setAutorizadoPor(actor); reversion.setClaveIdempotencia(request.claveIdempotencia());
        return reversion;
    }

    private MovimientoFinanciero reversa(MovimientoFinanciero original, CuentaFinanciera cuenta,
                                          ReversionFinanciera reversion, Instant fecha,
                                          DireccionMovimiento direccion, EstadoCuenta estado,
                                          String concepto, String lado) {
        MovimientoFinanciero movimiento = new MovimientoFinanciero();
        movimiento.setInstitucion(original.getInstitucion()); movimiento.setCuenta(cuenta);
        movimiento.setPlantelOperacion(original.getPlantelOperacion()); movimiento.setFechaOperacion(fecha);
        movimiento.setSecuenciaCuenta(estado.secuencia()); movimiento.setDireccion(direccion);
        movimiento.setClase(ClaseMovimiento.REVERSO); movimiento.setMonto(original.getMonto());
        movimiento.setMotivoFinanciero(original.getMotivoFinanciero()); movimiento.setConcepto(concepto);
        movimiento.setReferencia(original.getReferencia()); movimiento.setTerceroNombre(original.getTerceroNombre());
        movimiento.setReversaDe(original); movimiento.setReversionFinanciera(reversion);
        movimiento.setClaveIdempotencia("REVERSO:" + reversion.getId() + ":" + lado);
        movimiento.setSaldoAnterior(estado.saldo());
        movimiento.setSaldoPosterior(direccion == DireccionMovimiento.INGRESO
                ? estado.saldo().add(original.getMonto()) : estado.saldo().subtract(original.getMonto()));
        return movimiento;
    }

    private Instant validarFecha(LocalDateTime local, Instant original, List<CuentaFinanciera> cuentas) {
        ZoneId zona = ZoneId.of(cuentas.getFirst().getInstitucion().getZonaHoraria());
        Instant fecha = local.atZone(zona).toInstant();
        if (fecha.isAfter(Instant.now())) throw new ReglaNegocioException("La fecha de reversa no puede estar en el futuro");
        if (fecha.isBefore(original)) throw new ReglaNegocioException("La reversa no puede ser anterior a la operación original");
        if (cuentas.stream().anyMatch(c -> local.toLocalDate().isBefore(c.getFechaSaldoInicial())))
            throw new ReglaNegocioException("La reversa no puede ser anterior a la apertura de las cuentas");
        return fecha;
    }

    private void validarOperacionManual(MovimientoFinanciero movimiento) {
        if (movimiento.getClase() != ClaseMovimiento.OPERACION)
            throw new ReglaNegocioException("Sólo las operaciones manuales se revierten desde esta acción");
    }

    private void validarCuentaActiva(CuentaFinanciera cuenta) {
        if (!cuenta.isActivo()) throw new ReglaNegocioException("La cuenta financiera está inactiva");
    }

    private void validarAlcance(CuentaFinanciera cuenta) {
        if (cuenta.getPlantel() == null) alcance.validarAdministracionInstitucional(cuenta.getInstitucion().getId());
        else alcance.validarPlantel(cuenta.getPlantel().getId());
    }

    private Usuario actorPersistido(Long institucionId) {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioPrincipal principal)
                || principal.accesoRecuperacion() || principal.usuarioId() == null)
            throw new ReglaNegocioException("La reversa requiere una cuenta administrativa identificable; el acceso de recuperación no puede mover fondos");
        Usuario actor = usuarioRepository.findById(principal.usuarioId())
                .orElseThrow(() -> new AccessDeniedException("El usuario de la sesión ya no está disponible"));
        if (!actor.getInstitucion().getId().equals(institucionId))
            throw new AccessDeniedException("La operación no pertenece a la institución de la sesión");
        return actor;
    }

    private CuentaFinanciera bloquear(Long id) {
        return cuentaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta financiera", id));
    }

    private CuentaFinanciera cuenta(Long id, CuentaFinanciera primera, CuentaFinanciera segunda) {
        return primera.getId().equals(id) ? primera : segunda;
    }

    private EstadoCuenta estado(CuentaFinanciera cuenta) {
        Optional<MovimientoFinanciero> ultimo = movimientoRepository
                .findFirstByCuentaIdOrderBySecuenciaCuentaDesc(cuenta.getId());
        return new EstadoCuenta(ultimo.map(m -> m.getSecuenciaCuenta() + 1).orElse(1L),
                ultimo.map(MovimientoFinanciero::getSaldoPosterior).orElse(cuenta.getSaldoInicial())
                        .setScale(2, RoundingMode.HALF_UP));
    }

    private void validarFondos(DireccionMovimiento direccion, BigDecimal saldo, BigDecimal monto) {
        if (direccion == DireccionMovimiento.EGRESO && saldo.compareTo(monto) < 0)
            throw new ReglaNegocioException("La cuenta no tiene saldo suficiente para ejecutar la reversa");
    }

    private DireccionMovimiento opuesta(DireccionMovimiento direccion) {
        return direccion == DireccionMovimiento.INGRESO
                ? DireccionMovimiento.EGRESO : DireccionMovimiento.INGRESO;
    }

    private void validarReintentoMovimiento(ReversionFinanciera existente, Long movimientoId) {
        if (existente.getTipo() != TipoReversionFinanciera.MOVIMIENTO_MANUAL
                || !existente.getMovimientoOrigen().getId().equals(movimientoId))
            throw new ReglaNegocioException("La clave de reintento ya pertenece a otra reversa");
    }

    private void validarReintentoTransferencia(ReversionFinanciera existente, Long transferenciaId) {
        if (existente.getTipo() != TipoReversionFinanciera.TRANSFERENCIA
                || !existente.getTransferenciaOrigen().getId().equals(transferenciaId))
            throw new ReglaNegocioException("La clave de reintento ya pertenece a otra reversa");
    }

    private ReversionFinancieraResponse respuesta(ReversionFinanciera reversion) {
        return respuesta(reversion, reversion.getMovimientos());
    }

    private ReversionFinancieraResponse respuesta(ReversionFinanciera reversion,
                                                   List<MovimientoFinanciero> movimientos) {
        Long objetivoId = reversion.getTipo() == TipoReversionFinanciera.MOVIMIENTO_MANUAL
                ? reversion.getMovimientoOrigen().getId() : reversion.getTransferenciaOrigen().getId();
        return new ReversionFinancieraResponse(reversion.getId(), reversion.getTipo(), objetivoId,
                reversion.getFecha(), reversion.getMotivo(), reversion.getAutorizadoPor().getUsername(),
                movimientos.stream().map(this::movimientoResponse).toList());
    }

    private MovimientoFinancieroResponse movimientoResponse(MovimientoFinanciero movimiento) {
        return new MovimientoFinancieroResponse(movimiento.getId(), movimiento.getCuenta().getId(),
                movimiento.getCuenta().getNombre(), movimiento.getFechaOperacion(), movimiento.getSecuenciaCuenta(),
                movimiento.getMonto(), movimiento.getCuenta().getMoneda(), movimiento.getSaldoAnterior(),
                movimiento.getSaldoPosterior());
    }

    private ObjetivoReversionResponse objetivoMovimiento(MovimientoFinanciero movimiento) {
        return new ObjetivoReversionResponse(TipoReversionFinanciera.MOVIMIENTO_MANUAL, movimiento.getId(),
                movimiento.getInstitucion().getId(), movimiento.getConcepto(),
                movimiento.getCuenta().getNombre() + " · " + movimiento.getDireccion().name(),
                movimiento.getMonto(), movimiento.getCuenta().getMoneda(), movimiento.getFechaOperacion(),
                movimiento.getVersion(), movimiento.getReversa() != null);
    }

    private ObjetivoReversionResponse objetivoTransferencia(TransferenciaCuenta transferencia) {
        return new ObjetivoReversionResponse(TipoReversionFinanciera.TRANSFERENCIA, transferencia.getId(),
                transferencia.getInstitucion().getId(), "Transferencia entre cuentas",
                transferencia.getCuentaOrigen().getNombre() + " → " + transferencia.getCuentaDestino().getNombre(),
                transferencia.getMonto(), transferencia.getCuentaOrigen().getMoneda(), transferencia.getFecha(),
                transferencia.getVersion(), transferencia.getEstado() == EstadoTransferenciaCuenta.REVERTIDA);
    }

    private record EstadoCuenta(long secuencia, BigDecimal saldo) { }
}
