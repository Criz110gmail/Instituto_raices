package escuela.finanzas.service.impl;

import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.MovimientoManualRequest;
import escuela.finanzas.dto.response.MovimientoFinancieroResponse;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.finanzas.repository.MotivoFinancieroRepository;
import escuela.finanzas.repository.MovimientoFinancieroRepository;
import escuela.finanzas.service.MovimientoManualService;
import escuela.institucion.entity.Plantel;
import escuela.institucion.repository.PlantelRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;

import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoManualServiceImpl implements MovimientoManualService {
    private final CuentaFinancieraRepository cuentaRepository;
    private final MotivoFinancieroRepository motivoRepository;
    private final MovimientoFinancieroRepository movimientoRepository;
    private final PlantelRepository plantelRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlcanceDatosService alcance;

    @Override
    public MovimientoFinancieroResponse registrar(MovimientoManualRequest request) {
        CuentaFinanciera cuenta = cuentaRepository.findByIdForUpdate(request.cuentaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta financiera", request.cuentaId()));
        validarCuenta(request, cuenta);
        validarActor(cuenta);

        var existente = movimientoRepository.findByInstitucionIdAndClaveIdempotencia(
                cuenta.getInstitucion().getId(), request.claveIdempotencia());
        if (existente.isPresent()) return respuesta(existente.get());

        MotivoFinanciero motivo = motivoRepository.findById(request.motivoFinancieroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el motivo financiero", request.motivoFinancieroId()));
        validarMotivo(cuenta, motivo, request.direccion());
        Plantel plantel = resolverPlantel(request, cuenta);

        ZoneId zona = ZoneId.of(cuenta.getInstitucion().getZonaHoraria());
        Instant fecha = request.fechaOperacion().atZone(zona).toInstant();
        if (request.fechaOperacion().toLocalDate().isBefore(cuenta.getFechaSaldoInicial()))
            throw new ReglaNegocioException("La fecha del movimiento no puede ser anterior a la apertura de la cuenta");
        if (fecha.isAfter(Instant.now()))
            throw new ReglaNegocioException("La fecha del movimiento no puede estar en el futuro");

        var ultimo = movimientoRepository.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(cuenta.getId());
        long secuencia = ultimo.map(m -> m.getSecuenciaCuenta() + 1).orElse(1L);
        var anterior = ultimo.map(MovimientoFinanciero::getSaldoPosterior).orElse(cuenta.getSaldoInicial())
                .setScale(2, RoundingMode.HALF_UP);
        var monto = request.monto().setScale(2, RoundingMode.HALF_UP);
        var posterior = request.direccion() == DireccionMovimiento.INGRESO
                ? anterior.add(monto) : anterior.subtract(monto);
        if (posterior.signum() < 0)
            throw new ReglaNegocioException("El egreso excede el saldo disponible de la cuenta");

        MovimientoFinanciero movimiento = new MovimientoFinanciero();
        movimiento.setInstitucion(cuenta.getInstitucion());
        movimiento.setCuenta(cuenta);
        movimiento.setPlantelOperacion(plantel);
        movimiento.setFechaOperacion(fecha);
        movimiento.setSecuenciaCuenta(secuencia);
        movimiento.setDireccion(request.direccion());
        movimiento.setClase(ClaseMovimiento.OPERACION);
        movimiento.setMonto(monto);
        movimiento.setMotivoFinanciero(motivo);
        movimiento.setConcepto(limpiar(request.concepto()));
        movimiento.setReferencia(limpiar(request.referencia()));
        movimiento.setTerceroNombre(limpiar(request.terceroNombre()));
        movimiento.setClaveIdempotencia(request.claveIdempotencia());
        movimiento.setSaldoAnterior(anterior);
        movimiento.setSaldoPosterior(posterior);
        return respuesta(movimientoRepository.saveAndFlush(movimiento));
    }

    private void validarCuenta(MovimientoManualRequest request, CuentaFinanciera cuenta) {
        if (!cuenta.getInstitucion().getId().equals(request.institucionId()))
            throw new ReglaNegocioException("La cuenta no pertenece a la institución seleccionada");
        if (!cuenta.isActivo()) throw new ReglaNegocioException("La cuenta financiera está inactiva");
        if (cuenta.getPlantel() == null) alcance.validarAdministracionInstitucional(request.institucionId());
        else alcance.validarPlantel(cuenta.getPlantel().getId());
    }

    private void validarActor(CuentaFinanciera cuenta) {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioPrincipal principal)
                || principal.accesoRecuperacion() || principal.usuarioId() == null)
            throw new ReglaNegocioException("El registro requiere una cuenta administrativa identificable; el acceso de recuperación no puede publicar movimientos");
        var actor = usuarioRepository.findById(principal.usuarioId())
                .orElseThrow(() -> new AccessDeniedException("El usuario de la sesión ya no está disponible"));
        if (!actor.getInstitucion().getId().equals(cuenta.getInstitucion().getId()))
            throw new AccessDeniedException("La cuenta no pertenece a la institución de la sesión");
    }

    private void validarMotivo(CuentaFinanciera cuenta, MotivoFinanciero motivo, DireccionMovimiento direccion) {
        if (!motivo.isActivo() || !motivo.getInstitucion().getId().equals(cuenta.getInstitucion().getId()))
            throw new ReglaNegocioException("El motivo financiero no está activo o no pertenece a la institución");
        boolean compatible = motivo.getNaturaleza() == NaturalezaMotivoFinanciero.AMBOS
                || motivo.getNaturaleza().name().equals(direccion.name());
        if (!compatible) throw new ReglaNegocioException("La naturaleza del motivo no corresponde al tipo de movimiento");
    }

    private Plantel resolverPlantel(MovimientoManualRequest request, CuentaFinanciera cuenta) {
        if (cuenta.getPlantel() != null) {
            if (request.plantelOperacionId() != null
                    && !cuenta.getPlantel().getId().equals(request.plantelOperacionId()))
                throw new ReglaNegocioException("El plantel de operación debe coincidir con el alcance de la cuenta");
            return cuenta.getPlantel();
        }
        if (request.plantelOperacionId() == null) return null;
        Plantel plantel = plantelRepository.findById(request.plantelOperacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el plantel", request.plantelOperacionId()));
        if (!plantel.getInstitucion().getId().equals(cuenta.getInstitucion().getId()))
            throw new ReglaNegocioException("El plantel de operación no pertenece a la institución");
        alcance.validarPlantel(plantel.getId());
        return plantel;
    }

    private MovimientoFinancieroResponse respuesta(MovimientoFinanciero movimiento) {
        return new MovimientoFinancieroResponse(movimiento.getId(), movimiento.getCuenta().getId(),
                movimiento.getCuenta().getNombre(), movimiento.getFechaOperacion(),
                movimiento.getSecuenciaCuenta(), movimiento.getMonto(), movimiento.getCuenta().getMoneda(),
                movimiento.getSaldoAnterior(), movimiento.getSaldoPosterior());
    }
}
