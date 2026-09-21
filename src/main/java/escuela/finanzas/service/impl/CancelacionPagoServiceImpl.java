package escuela.finanzas.service.impl;

import escuela.admin.dto.ModuloCatalogo;
import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.cobranza.repository.CargoRepository;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.CancelacionPagoRequest;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.entity.*;
import escuela.finanzas.mapper.PagoMapper;
import escuela.finanzas.repository.*;
import escuela.finanzas.service.CancelacionPagoService;
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
import java.util.*;

import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelacionPagoServiceImpl implements CancelacionPagoService {
    private final PagoRepository pagoRepository;
    private final DevolucionPagoRepository devolucionRepository;
    private final AplicacionPagoRepository aplicacionRepository;
    private final CargoRepository cargoRepository;
    private final CuentaFinancieraRepository cuentaRepository;
    private final MovimientoFinancieroRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlcanceDatosService alcance;
    private final PagoMapper mapper;
    private final RegistroAuditoriaService auditoria;

    @Override
    public PagoResponse cancelar(Long pagoId, CancelacionPagoRequest request) {
        Pago pago = pagoRepository.findByIdForUpdate(pagoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el pago", pagoId));
        alcance.validarRecurso(ModuloCatalogo.PAGOS, pagoId);
        Usuario actor = actorPersistido(pago);
        String motivo = limpiar(request.motivo());
        if (motivo == null || motivo.length() > 2000)
            throw new ReglaNegocioException("Explica la cancelación en un máximo de 2000 caracteres");
        if (pago.getEstado() == EstadoPago.CANCELADO) {
            if (Objects.equals(pago.getMotivoRechazoCancelacion(), motivo)) return mapper.respuesta(pago);
            throw new ReglaNegocioException("El pago ya está cancelado con otro motivo");
        }
        if (pago.getEstado() == EstadoPago.RECHAZADO)
            throw new ReglaNegocioException("Un pago rechazado ya quedó resuelto y no puede cancelarse");
        verificar(pago, request.version(), "Pago");

        Instant ahora = Instant.now();
        int aplicacionesRevertidas = 0;
        Long movimientoAnulacionId = null;
        EstadoPago estadoAnterior = pago.getEstado();
        if (estadoAnterior == EstadoPago.VALIDADO) {
            if (devolucionRepository.existsByPagoIdAndEstado(pagoId, EstadoDevolucionPago.EJECUTADA))
                throw new ReglaNegocioException("El pago tiene devoluciones ejecutadas; resuélvelas antes de cancelar el pago");
            MovimientoFinanciero original = movimientoRepository.findByPagoId(pagoId)
                    .orElseThrow(() -> new ReglaNegocioException("El pago validado no conserva su ingreso original"));
            if (original.getClase() != ClaseMovimiento.COBRO || original.getDireccion() != DireccionMovimiento.INGRESO
                    || original.getReversa() != null)
                throw new ReglaNegocioException("El ingreso original no está disponible para cancelación");

            CuentaFinanciera cuenta = cuentaRepository.findByIdForUpdate(original.getCuenta().getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta destino", original.getCuenta().getId()));
            if (cuenta.getPlantel() == null) alcance.validarAdministracionInstitucional(pago.getInstitucion().getId());
            else alcance.validarPlantel(cuenta.getPlantel().getId());
            var ultimo = movimientoRepository.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(cuenta.getId());
            BigDecimal saldo = ultimo.map(MovimientoFinanciero::getSaldoPosterior)
                    .orElse(cuenta.getSaldoInicial()).setScale(2, RoundingMode.HALF_UP);
            if (saldo.compareTo(original.getMonto()) < 0)
                throw new ReglaNegocioException("La cuenta no tiene saldo suficiente para compensar el ingreso; revisa los movimientos posteriores");

            List<AplicacionPago> activas = aplicacionRepository.findAplicacionesActivasByPagoIdForUpdate(pagoId);
            pago.getAplicaciones().size(); // Inicializar antes de insertar reversas para no duplicar la colección en memoria.
            activas.stream().map(a -> a.getCargo().getId()).distinct().sorted().forEach(id ->
                    cargoRepository.findByIdForUpdate(id)
                            .orElseThrow(() -> new RecursoNoEncontradoException("el cargo", id)));
            List<AplicacionPago> reversas = new ArrayList<>();
            for (AplicacionPago aplicacion : activas) {
                AplicacionPago reversa = new AplicacionPago();
                reversa.setPago(pago); reversa.setCargo(aplicacion.getCargo());
                reversa.setMonto(aplicacion.getMonto()); reversa.setOperacion(OperacionAplicacionPago.REVERTIR);
                reversa.setFechaAplicacion(ahora); reversa.setReversaDe(aplicacion); reversa.setMotivo(motivo);
                reversas.add(reversa);
                aplicacion.setReversa(reversa);
            }
            aplicacionRepository.saveAllAndFlush(reversas);
            pago.getAplicaciones().addAll(reversas);
            aplicacionesRevertidas = reversas.size();

            MovimientoFinanciero anulacion = new MovimientoFinanciero();
            anulacion.setInstitucion(pago.getInstitucion()); anulacion.setCuenta(cuenta);
            anulacion.setPlantelOperacion(pago.getPlantelRegistro()); anulacion.setFechaOperacion(ahora);
            anulacion.setSecuenciaCuenta(ultimo.map(m -> m.getSecuenciaCuenta() + 1).orElse(1L));
            anulacion.setDireccion(DireccionMovimiento.EGRESO); anulacion.setClase(ClaseMovimiento.ANULACION);
            anulacion.setMonto(original.getMonto()); anulacion.setMotivoFinanciero(original.getMotivoFinanciero());
            anulacion.setConcepto("Cancelación del pago " + pago.getFolio());
            anulacion.setReversaDe(original); anulacion.setClaveIdempotencia("ANULACION:PAGO:" + pagoId);
            anulacion.setSaldoAnterior(saldo); anulacion.setSaldoPosterior(saldo.subtract(original.getMonto()));
            anulacion = movimientoRepository.saveAndFlush(anulacion);
            original.setReversa(anulacion);
            movimientoAnulacionId = anulacion.getId();
        }

        pago.setEstado(EstadoPago.CANCELADO);
        pago.setMotivoRechazoCancelacion(motivo);
        pago.setCanceladoEn(ahora);
        pago.setCanceladoPor(actor);
        Pago guardado = pagoRepository.saveAndFlush(pago);
        Map<String, Object> cambios = new LinkedHashMap<>();
        cambios.put("folio", pago.getFolio()); cambios.put("estadoAnterior", estadoAnterior.name());
        cambios.put("monto", pago.getMonto()); cambios.put("moneda", pago.getMoneda());
        cambios.put("aplicacionesRevertidas", aplicacionesRevertidas);
        if (movimientoAnulacionId != null) cambios.put("movimientoAnulacionId", movimientoAnulacionId);
        auditoria.registrar(pago.getInstitucion().getId(), AccionAuditoria.PAGO_CANCELADO,
                "PAGO", pagoId, motivo, cambios);
        return mapper.respuesta(guardado);
    }

    private Usuario actorPersistido(Pago pago) {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioPrincipal principal)
                || principal.accesoRecuperacion() || principal.usuarioId() == null)
            throw new ReglaNegocioException("La cancelación requiere una cuenta administrativa identificable");
        if (autenticacion.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("PAGO_CANCELAR")))
            throw new AccessDeniedException("No tienes permiso para cancelar pagos");
        Usuario actor = usuarioRepository.findById(principal.usuarioId())
                .orElseThrow(() -> new AccessDeniedException("El usuario de la sesión ya no está disponible"));
        if (!actor.getInstitucion().getId().equals(pago.getInstitucion().getId()))
            throw new AccessDeniedException("El pago no pertenece a la institución de la sesión");
        return actor;
    }
}
