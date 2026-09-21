package escuela.finanzas.service.impl;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.CargoRepository;
import escuela.common.exception.*;
import escuela.finanzas.dto.request.*;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.entity.*;
import escuela.finanzas.mapper.PagoMapper;
import escuela.finanzas.repository.*;
import escuela.finanzas.service.ValidacionPagoService;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
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
import static escuela.cobranza.support.CalculoCargo.saldo;

@Service
@RequiredArgsConstructor
@Transactional
public class ValidacionPagoServiceImpl implements ValidacionPagoService {
    private static final String MOTIVO_COBROS = "COBROS_ESCOLARES";

    private final PagoRepository pagoRepository;
    private final CuentaFinancieraRepository cuentaRepository;
    private final CargoRepository cargoRepository;
    private final SolicitudAplicacionPagoRepository solicitudRepository;
    private final AplicacionPagoRepository aplicacionRepository;
    private final MovimientoFinancieroRepository movimientoRepository;
    private final MotivoFinancieroRepository motivoRepository;
    private final AlumnoTutorRepository vinculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagoMapper mapper;
    private final RegistroAuditoriaService auditoria;

    @Override
    public PagoResponse validar(Long pagoId, ValidacionPagoRequest request) {
        Pago pago = buscarBloqueado(pagoId);
        if (pago.getEstado() == EstadoPago.VALIDADO) return mapper.respuesta(pago);
        exigirPendiente(pago);
        verificar(pago, request.version(), "Pago");
        Usuario actor = actorPersistido(pago);
        if (request.cuentaDestinoId() == null) {
            throw new ReglaNegocioException("Selecciona la cuenta destino donde se recibió el pago");
        }

        CuentaFinanciera cuenta = cuentaRepository.findByIdForUpdate(request.cuentaDestinoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta destino", request.cuentaDestinoId()));
        validarCuenta(pago, cuenta);

        List<SolicitudAplicacionPago> solicitudes = solicitudRepository
                .findAllByPagoIdOrderByCargoIdAsc(pagoId);
        BigDecimal disponible = pago.getMonto();
        Instant ahora = Instant.now();
        LocalDate hoyInstitucion = ahora.atZone(ZoneId.of(pago.getInstitucion().getZonaHoraria())).toLocalDate();
        for (SolicitudAplicacionPago solicitud : solicitudes) {
            if (disponible.signum() == 0) break;
            Cargo cargo = cargoRepository.findByIdForUpdate(solicitud.getCargo().getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("el cargo", solicitud.getCargo().getId()));
            validarCoherenciaCargo(pago, cargo);
            if (cargo.getEstadoRegistro() != EstadoRegistroCargo.EMITIDO
                    || !vinculoRepository.tieneResponsabilidadFinancieraVigente(
                    cargo.getInscripcion().getAlumno().getId(), pago.getTutor().getId(), hoyInstitucion)) {
                continue;
            }
            BigDecimal monto = solicitud.getMontoSolicitado().min(saldo(cargo)).min(disponible)
                    .setScale(2, RoundingMode.HALF_UP);
            if (monto.signum() <= 0) continue;
            AplicacionPago aplicacion = new AplicacionPago();
            aplicacion.setPago(pago);
            aplicacion.setCargo(cargo);
            aplicacion.setSolicitud(solicitud);
            aplicacion.setMonto(monto);
            aplicacion.setOperacion(OperacionAplicacionPago.APLICAR);
            aplicacion.setFechaAplicacion(ahora);
            aplicacion = aplicacionRepository.save(aplicacion);
            pago.getAplicaciones().add(aplicacion);
            cargo.getAplicaciones().add(aplicacion);
            disponible = disponible.subtract(monto);
        }

        MovimientoFinanciero movimiento = publicarIngreso(pago, cuenta, actor, ahora);
        pago.setCuentaDestino(cuenta);
        pago.setValidadoPor(actor);
        pago.setValidadoEn(ahora);
        pago.setEstado(EstadoPago.VALIDADO);
        pago.setMovimiento(movimiento);
        Pago guardado = pagoRepository.saveAndFlush(pago);
        PagoResponse respuesta = mapper.respuesta(guardado);
        auditoria.registrar(pago.getInstitucion().getId(), AccionAuditoria.PAGO_VALIDADO,
                "PAGO", pago.getId(), null, Map.of("folio", pago.getFolio(), "monto", pago.getMonto(),
                        "moneda", pago.getMoneda(), "cuentaDestinoId", cuenta.getId(),
                        "montoAplicado", respuesta.montoAplicado(), "montoDisponible", respuesta.montoDisponible()));
        return respuesta;
    }

    @Override
    public PagoResponse rechazar(Long pagoId, RechazoPagoRequest request) {
        Pago pago = buscarBloqueado(pagoId);
        String motivo = limpiar(request.motivo());
        if (motivo == null || motivo.length() > 2000) {
            throw new ReglaNegocioException("Indica un motivo de rechazo de máximo 2000 caracteres");
        }
        if (pago.getEstado() == EstadoPago.RECHAZADO
                && Objects.equals(pago.getMotivoRechazoCancelacion(), motivo)) return mapper.respuesta(pago);
        exigirPendiente(pago);
        verificar(pago, request.version(), "Pago");
        actorPersistido(pago);
        pago.setEstado(EstadoPago.RECHAZADO);
        pago.setMotivoRechazoCancelacion(motivo);
        Pago guardado = pagoRepository.saveAndFlush(pago);
        auditoria.registrar(pago.getInstitucion().getId(), AccionAuditoria.PAGO_RECHAZADO,
                "PAGO", pago.getId(), motivo, Map.of("folio", pago.getFolio(), "monto", pago.getMonto(),
                        "moneda", pago.getMoneda()));
        return mapper.respuesta(guardado);
    }

    private Pago buscarBloqueado(Long id) {
        return pagoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el pago", id));
    }

    private void exigirPendiente(Pago pago) {
        if (pago.getEstado() != EstadoPago.PENDIENTE_VALIDACION) {
            throw new ReglaNegocioException("Sólo un pago pendiente puede validarse o rechazarse");
        }
    }

    private Usuario actorPersistido(Pago pago) {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioPrincipal principal)
                || principal.accesoRecuperacion() || principal.usuarioId() == null) {
            throw new ReglaNegocioException("La validación de pagos requiere una cuenta administrativa identificable; el acceso de recuperación no puede autorizar movimientos");
        }
        Usuario usuario = usuarioRepository.findById(principal.usuarioId())
                .orElseThrow(() -> new AccessDeniedException("El usuario de la sesión ya no está disponible"));
        if (!usuario.getInstitucion().getId().equals(pago.getInstitucion().getId())) {
            throw new AccessDeniedException("El pago no pertenece a la institución de la sesión");
        }
        return usuario;
    }

    private void validarCuenta(Pago pago, CuentaFinanciera cuenta) {
        boolean alcance = cuenta.getInstitucion().getId().equals(pago.getInstitucion().getId())
                && (cuenta.getPlantel() == null
                || cuenta.getPlantel().getId().equals(pago.getPlantelRegistro().getId()));
        if (!cuenta.isActivo() || !alcance) {
            throw new ReglaNegocioException("La cuenta destino no está activa o disponible para el plantel del pago");
        }
        if (!cuenta.getMoneda().equals(pago.getMoneda())) {
            throw new ReglaNegocioException("La cuenta destino debe usar la misma moneda del pago");
        }
        if (pago.getMetodo() == MetodoPago.EFECTIVO && cuenta.getTipo() != TipoCuentaFinanciera.CAJA) {
            throw new ReglaNegocioException("El efectivo debe ingresar a una cuenta de tipo caja");
        }
        if (pago.getMetodo() == MetodoPago.TRANSFERENCIA && cuenta.getTipo() == TipoCuentaFinanciera.CAJA) {
            throw new ReglaNegocioException("La transferencia debe ingresar a una cuenta bancaria o de inversión");
        }
        LocalDate fechaPago = pago.getFechaPago().atZone(ZoneId.of(pago.getInstitucion().getZonaHoraria())).toLocalDate();
        if (fechaPago.isBefore(cuenta.getFechaSaldoInicial())) {
            throw new ReglaNegocioException("La fecha del pago es anterior a la fecha de apertura de la cuenta destino");
        }
    }

    private void validarCoherenciaCargo(Pago pago, Cargo cargo) {
        if (!cargo.getInscripcion().getAlumno().getInstitucion().getId().equals(pago.getInstitucion().getId())
                || !cargo.getMoneda().equals(pago.getMoneda())) {
            throw new ReglaNegocioException("La distribución contiene un cargo incompatible con la institución o moneda del pago");
        }
    }

    private MovimientoFinanciero publicarIngreso(Pago pago, CuentaFinanciera cuenta,
                                                   Usuario actor, Instant ahora) {
        Optional<MovimientoFinanciero> existente = movimientoRepository.findByPagoId(pago.getId());
        if (existente.isPresent()) return existente.get();
        motivoRepository.crearCobrosEscolaresSiAusente(pago.getInstitucion().getId(), actor.getId());
        MotivoFinanciero motivo = motivoRepository
                .findByInstitucionIdAndCodigoIgnoreCase(pago.getInstitucion().getId(), MOTIVO_COBROS)
                .orElseThrow(() -> new IllegalStateException("No fue posible preparar el motivo de cobro"));
        Optional<MovimientoFinanciero> ultimo = movimientoRepository
                .findFirstByCuentaIdOrderBySecuenciaCuentaDesc(cuenta.getId());
        long secuencia = ultimo.map(m -> m.getSecuenciaCuenta() + 1).orElse(1L);
        BigDecimal anterior = ultimo.map(MovimientoFinanciero::getSaldoPosterior)
                .orElse(cuenta.getSaldoInicial()).setScale(2, RoundingMode.HALF_UP);

        MovimientoFinanciero movimiento = new MovimientoFinanciero();
        movimiento.setInstitucion(pago.getInstitucion());
        movimiento.setCuenta(cuenta);
        movimiento.setPlantelOperacion(pago.getPlantelRegistro());
        movimiento.setFechaOperacion(pago.getFechaPago());
        movimiento.setSecuenciaCuenta(secuencia);
        movimiento.setDireccion(DireccionMovimiento.INGRESO);
        movimiento.setClase(ClaseMovimiento.COBRO);
        movimiento.setMonto(pago.getMonto());
        movimiento.setMotivoFinanciero(motivo);
        movimiento.setConcepto("Pago escolar " + pago.getFolio());
        movimiento.setReferencia(pago.getReferencia());
        movimiento.setTerceroNombre(pago.getNombrePagador() == null
                ? pago.getTutor().getNombres() + " " + pago.getTutor().getPrimerApellido()
                : pago.getNombrePagador());
        if (!pago.getComprobantes().isEmpty()) {
            movimiento.setComprobanteArchivo(pago.getComprobantes().getFirst().getArchivo());
        }
        movimiento.setPago(pago);
        movimiento.setClaveIdempotencia("COBRO:PAGO:" + pago.getId());
        movimiento.setSaldoAnterior(anterior);
        movimiento.setSaldoPosterior(anterior.add(pago.getMonto()));
        return movimientoRepository.save(movimiento);
    }
}
