package escuela.finanzas.service;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.MovimientoManualRequest;
import escuela.finanzas.dto.request.RetiroFondoRequest;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Service
@RequiredArgsConstructor
@Transactional
public class RetiroFondoService {
    private final CuentaFinancieraRepository cuentas;
    private final RetiroFondoRepository retiros;
    private final MovimientoFinancieroRepository movimientos;
    private final MotivoFinancieroRepository motivos;
    private final UsuarioRepository usuarios;
    private final MovimientoManualService operaciones;
    private final AlcanceDatosService alcance;
    private final RegistroAuditoriaService auditoria;

    public Long ejecutar(RetiroFondoRequest request) {
        validarCampos(request);
        CuentaFinanciera cuenta = cuentas.findByIdForUpdate(request.cuentaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta financiera", request.cuentaId()));
        if (!cuenta.getInstitucion().getId().equals(request.institucionId()))
            throw new ReglaNegocioException("La cuenta no pertenece a la institución seleccionada");
        if (cuenta.getPlantel() == null) alcance.validarAdministracionInstitucional(request.institucionId());
        else alcance.validarPlantel(cuenta.getPlantel().getId());
        Usuario actor = actor(cuenta);
        Instant fecha = request.fechaOperacion().atZone(ZoneId.of(cuenta.getInstitucion().getZonaHoraria())).toInstant();
        BigDecimal monto = request.monto().setScale(2, RoundingMode.UNNECESSARY);
        String beneficiario = limpiar(request.beneficiario());
        String concepto = limpiar(request.concepto());
        String referencia = limpiar(request.referencia());
        String observaciones = limpiar(request.observaciones());
        Long plantelEsperado = cuenta.getPlantel() == null ? request.plantelOperacionId()
                : cuenta.getPlantel().getId();

        var anterior = retiros.findByInstitucionIdAndClaveIdempotencia(
                request.institucionId(), request.claveIdempotencia());
        if (anterior.isPresent()) {
            RetiroFondo existente = anterior.get();
            if (!existente.getCuenta().getId().equals(request.cuentaId())
                    || !Objects.equals(existente.getPlantelOperacion() == null ? null
                            : existente.getPlantelOperacion().getId(), plantelEsperado)
                    || !existente.getFechaOperacion().equals(fecha)
                    || existente.getMonto().compareTo(monto) != 0
                    || !existente.getMotivoFinanciero().getId().equals(request.motivoFinancieroId())
                    || !existente.getBeneficiario().equals(beneficiario)
                    || !existente.getConcepto().equals(concepto)
                    || !existente.getReferencia().equals(referencia)
                    || !Objects.equals(existente.getObservaciones(), observaciones))
                throw new ReglaNegocioException("La clave de este retiro ya corresponde a otros datos");
            return existente.getId();
        }

        MotivoFinanciero motivo = motivos.findById(request.motivoFinancieroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el motivo financiero", request.motivoFinancieroId()));
        if (Set.of("COBROS_ESCOLARES", "TRASPASO_INTERNO", "DEVOLUCION_PAGO")
                .contains(motivo.getCodigo().toUpperCase()))
            throw new ReglaNegocioException("Selecciona un motivo de egreso externo, no un motivo técnico");
        var publicado = operaciones.registrar(new MovimientoManualRequest(request.institucionId(),
                request.cuentaId(), request.plantelOperacionId(), request.fechaOperacion(),
                DireccionMovimiento.EGRESO, request.motivoFinancieroId(), monto,
                "Retiro externo: " + concepto, referencia, beneficiario,
                "RETIRO:" + request.claveIdempotencia()));
        MovimientoFinanciero movimiento = movimientos.findById(publicado.id())
                .orElseThrow(() -> new IllegalStateException("No se publicó el movimiento del retiro"));
        if (movimiento.getDireccion() != DireccionMovimiento.EGRESO
                || movimiento.getClase() != ClaseMovimiento.OPERACION
                || !movimiento.getCuenta().getId().equals(cuenta.getId())
                || movimiento.getMonto().compareTo(monto) != 0
                || !Objects.equals(movimiento.getConcepto(), "Retiro externo: " + concepto)
                || !Objects.equals(movimiento.getReferencia(), referencia)
                || !Objects.equals(movimiento.getTerceroNombre(), beneficiario))
            throw new ReglaNegocioException("La clave del retiro coincide con otro movimiento; no se registró un segundo egreso");
        RetiroFondo retiro = new RetiroFondo();
        retiro.setInstitucion(cuenta.getInstitucion()); retiro.setCuenta(cuenta);
        retiro.setPlantelOperacion(movimiento.getPlantelOperacion());
        retiro.setMovimiento(movimiento); retiro.setAutorizadoPor(actor);
        retiro.setMotivoFinanciero(motivo); retiro.setFechaOperacion(fecha);
        retiro.setMonto(monto); retiro.setBeneficiario(beneficiario);
        retiro.setConcepto(concepto); retiro.setReferencia(referencia);
        retiro.setObservaciones(observaciones);
        retiro.setClaveIdempotencia(request.claveIdempotencia());
        retiro = retiros.saveAndFlush(retiro);
        auditoria.registrar(request.institucionId(), AccionAuditoria.RETIRO_FONDO_EJECUTADO,
                "RETIRO_FONDO", retiro.getId(), concepto,
                Map.of("cuentaId", cuenta.getId(), "movimientoId", movimiento.getId(),
                        "monto", monto, "moneda", cuenta.getMoneda()));
        return retiro.getId();
    }

    private void validarCampos(RetiroFondoRequest r) {
        if (r == null || r.institucionId() == null || r.cuentaId() == null
                || r.fechaOperacion() == null || r.motivoFinancieroId() == null
                || r.monto() == null || r.monto().signum() <= 0 || r.monto().scale() > 2
                || r.claveIdempotencia() == null || r.claveIdempotencia().isBlank()
                || r.claveIdempotencia().length() > 100)
            throw new ReglaNegocioException("Completa cuenta, fecha, motivo, monto y clave del retiro");
        textoObligatorio(r.beneficiario(), 180, "destinatario");
        textoObligatorio(r.concepto(), 230, "concepto");
        textoObligatorio(r.referencia(), 150, "folio o referencia del comprobante");
        if (r.observaciones() != null && r.observaciones().length() > 500)
            throw new ReglaNegocioException("Las observaciones no pueden exceder 500 caracteres");
    }

    private void textoObligatorio(String valor, int maximo, String campo) {
        if (valor == null || valor.isBlank() || valor.trim().length() > maximo)
            throw new ReglaNegocioException("Indica un " + campo + " de máximo " + maximo + " caracteres");
    }

    private Usuario actor(CuentaFinanciera cuenta) {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioPrincipal principal)
                || principal.accesoRecuperacion() || principal.usuarioId() == null)
            throw new ReglaNegocioException("El retiro requiere una cuenta administrativa identificable");
        Usuario usuario = usuarios.findById(principal.usuarioId())
                .orElseThrow(() -> new AccessDeniedException("El usuario de la sesión ya no está disponible"));
        if (!usuario.getInstitucion().getId().equals(cuenta.getInstitucion().getId()))
            throw new AccessDeniedException("La cuenta no pertenece a la institución de la sesión");
        return usuario;
    }
}
