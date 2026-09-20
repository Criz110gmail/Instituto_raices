package escuela.finanzas.service.impl;

import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.*;
import escuela.finanzas.dto.response.CorteCajaResponse;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
import escuela.finanzas.service.CorteCajaService;
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
import java.time.Instant;
import java.util.Optional;

import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class CorteCajaServiceImpl implements CorteCajaService {
    private final CorteCajaRepository corteRepository;
    private final CuentaFinancieraRepository cuentaRepository;
    private final MovimientoFinancieroRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlcanceDatosService alcance;

    @Override
    public CorteCajaResponse abrir(AperturaCorteCajaRequest request) {
        CuentaFinanciera cuenta = cuentaRepository.findByIdForUpdate(request.cuentaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta financiera", request.cuentaId()));
        validarCuenta(request.institucionId(), cuenta, true);
        Usuario actor = actorPersistido(cuenta.getInstitucion().getId());
        Optional<CorteCaja> existente = corteRepository.findByInstitucionIdAndClaveApertura(
                request.institucionId(), request.claveIdempotencia());
        if (existente.isPresent()) {
            if (!existente.get().getCuenta().getId().equals(cuenta.getId()))
                throw new ReglaNegocioException("La clave de reintento ya pertenece a otro corte");
            return respuesta(existente.get());
        }
        if (corteRepository.findByCuentaIdAndEstado(cuenta.getId(), EstadoCorteCaja.ABIERTO).isPresent())
            throw new ReglaNegocioException("La cuenta ya tiene un corte abierto; ciérralo antes de iniciar otro");

        EstadoCuenta estado = estado(cuenta);
        CorteCaja corte = new CorteCaja();
        corte.setInstitucion(cuenta.getInstitucion()); corte.setCuenta(cuenta);
        corte.setEstado(EstadoCorteCaja.ABIERTO); corte.setAbiertoEn(Instant.now());
        corte.setAbiertoPor(actor); corte.setSecuenciaInicial(estado.ultimaSecuencia());
        corte.setSaldoInicialSistema(estado.saldo());
        corte.setObservacionesApertura(limpiar(request.observaciones()));
        corte.setClaveApertura(request.claveIdempotencia());
        return respuesta(corteRepository.saveAndFlush(corte));
    }

    @Override
    public CorteCajaResponse cerrar(Long corteId, CierreCorteCajaRequest request) {
        CorteCaja referencia = corteRepository.findById(corteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el corte de caja", corteId));
        CuentaFinanciera cuenta = cuentaRepository.findByIdForUpdate(referencia.getCuenta().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta financiera",
                        referencia.getCuenta().getId()));
        CorteCaja corte = corteRepository.findByIdForUpdate(corteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el corte de caja", corteId));
        validarCuenta(corte.getInstitucion().getId(), cuenta, false);
        Usuario actor = actorPersistido(corte.getInstitucion().getId());

        Optional<CorteCaja> existente = corteRepository.findByInstitucionIdAndClaveCierre(
                corte.getInstitucion().getId(), request.claveIdempotencia());
        if (existente.isPresent()) {
            if (!existente.get().getId().equals(corteId)
                    || existente.get().getEfectivoDeclarado().compareTo(request.efectivoDeclarado()) != 0)
                throw new ReglaNegocioException("La clave de reintento ya pertenece a otro cierre");
            return respuesta(existente.get());
        }
        verificar(corte, request.version(), "Corte de caja");
        if (corte.getEstado() != EstadoCorteCaja.ABIERTO)
            throw new ReglaNegocioException("El corte de caja ya está cerrado");

        EstadoCuenta estado = estado(cuenta);
        ResumenMovimientosCorte resumen = movimientoRepository.resumirParaCorte(cuenta.getId(),
                corte.getSecuenciaInicial(), estado.ultimaSecuencia());
        BigDecimal ingresos = escala(resumen.getIngresos());
        BigDecimal egresos = escala(resumen.getEgresos());
        BigDecimal esperado = corte.getSaldoInicialSistema().add(ingresos).subtract(egresos)
                .setScale(2, RoundingMode.HALF_UP);
        if (esperado.compareTo(estado.saldo()) != 0)
            throw new IllegalStateException("El saldo secuencial de la cuenta no coincide con los movimientos del corte");
        BigDecimal declarado = request.efectivoDeclarado().setScale(2, RoundingMode.HALF_UP);
        BigDecimal diferencia = declarado.subtract(esperado).setScale(2, RoundingMode.HALF_UP);
        String justificacion = limpiar(request.justificacionDiferencia());
        if (diferencia.signum() != 0 && (justificacion == null || justificacion.isBlank()))
            throw new ReglaNegocioException("Explica la diferencia entre el efectivo declarado y el saldo esperado");

        corte.setEstado(EstadoCorteCaja.CERRADO); corte.setCerradoEn(Instant.now());
        corte.setCerradoPor(actor); corte.setSecuenciaFinal(estado.ultimaSecuencia());
        corte.setMovimientosContabilizados(resumen.getCantidad());
        corte.setTotalIngresos(ingresos); corte.setTotalEgresos(egresos);
        corte.setSaldoEsperado(esperado); corte.setEfectivoDeclarado(declarado);
        corte.setDiferencia(diferencia); corte.setJustificacionDiferencia(justificacion);
        corte.setObservacionesCierre(limpiar(request.observaciones()));
        corte.setClaveCierre(request.claveIdempotencia());
        return respuesta(corteRepository.saveAndFlush(corte));
    }

    @Override
    @Transactional(readOnly = true)
    public CorteCajaResponse obtener(Long corteId) {
        CorteCaja corte = corteRepository.findById(corteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el corte de caja", corteId));
        validarCuenta(corte.getInstitucion().getId(), corte.getCuenta(), false);
        return respuesta(corte);
    }

    private void validarCuenta(Long institucionId, CuentaFinanciera cuenta, boolean exigirActiva) {
        if (!cuenta.getInstitucion().getId().equals(institucionId))
            throw new ReglaNegocioException("La cuenta no pertenece a la institución seleccionada");
        if (cuenta.getTipo() != TipoCuentaFinanciera.CAJA)
            throw new ReglaNegocioException("Los cortes sólo se realizan sobre cuentas de tipo caja");
        if (exigirActiva && !cuenta.isActivo())
            throw new ReglaNegocioException("La caja debe estar activa para abrir un corte");
        if (cuenta.getPlantel() == null) alcance.validarAdministracionInstitucional(institucionId);
        else alcance.validarPlantel(cuenta.getPlantel().getId());
    }

    private Usuario actorPersistido(Long institucionId) {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioPrincipal principal)
                || principal.accesoRecuperacion() || principal.usuarioId() == null)
            throw new ReglaNegocioException("El corte requiere una cuenta administrativa identificable; el acceso de recuperación no puede conciliar efectivo");
        Usuario actor = usuarioRepository.findById(principal.usuarioId())
                .orElseThrow(() -> new AccessDeniedException("El usuario de la sesión ya no está disponible"));
        if (!actor.getInstitucion().getId().equals(institucionId))
            throw new AccessDeniedException("La caja no pertenece a la institución de la sesión");
        return actor;
    }

    private EstadoCuenta estado(CuentaFinanciera cuenta) {
        Optional<MovimientoFinanciero> ultimo = movimientoRepository
                .findFirstByCuentaIdOrderBySecuenciaCuentaDesc(cuenta.getId());
        return new EstadoCuenta(ultimo.map(MovimientoFinanciero::getSecuenciaCuenta).orElse(0L),
                ultimo.map(MovimientoFinanciero::getSaldoPosterior).orElse(cuenta.getSaldoInicial())
                        .setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal escala(BigDecimal valor) {
        return (valor == null ? BigDecimal.ZERO : valor).setScale(2, RoundingMode.HALF_UP);
    }

    private CorteCajaResponse respuesta(CorteCaja corte) {
        return new CorteCajaResponse(corte.getId(), corte.getInstitucion().getId(), corte.getCuenta().getId(),
                corte.getCuenta().getCodigo(), corte.getCuenta().getNombre(), corte.getCuenta().getMoneda(),
                corte.getCuenta().getPlantel() == null ? "Institucional" : corte.getCuenta().getPlantel().getNombre(),
                corte.getEstado(), corte.getAbiertoEn(), corte.getAbiertoPor().getUsername(),
                corte.getSecuenciaInicial(), corte.getSaldoInicialSistema(), corte.getObservacionesApertura(),
                corte.getCerradoEn(), corte.getCerradoPor() == null ? null : corte.getCerradoPor().getUsername(),
                corte.getSecuenciaFinal(), corte.getMovimientosContabilizados(), corte.getTotalIngresos(),
                corte.getTotalEgresos(), corte.getSaldoEsperado(), corte.getEfectivoDeclarado(),
                corte.getDiferencia(), corte.getJustificacionDiferencia(), corte.getObservacionesCierre(),
                corte.getVersion());
    }

    private record EstadoCuenta(long ultimaSecuencia, BigDecimal saldo) { }
}
