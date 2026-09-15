package escuela.finanzas.service.impl;

import escuela.common.exception.*;
import escuela.finanzas.dto.request.CuentaFinancieraRequest;
import escuela.finanzas.dto.response.CuentaFinancieraResponse;
import escuela.finanzas.entity.*;
import escuela.finanzas.mapper.CuentaFinancieraMapper;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.finanzas.repository.MovimientoFinancieroRepository;
import escuela.finanzas.service.CuentaFinancieraService;
import escuela.institucion.entity.*;
import escuela.institucion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaFinancieraServiceImpl implements CuentaFinancieraService {

    private final CuentaFinancieraRepository repository;
    private final InstitucionRepository institucionRepository;
    private final PlantelRepository plantelRepository;
    private final MovimientoFinancieroRepository movimientoRepository;
    private final CuentaFinancieraMapper mapper;

    @Override
    public CuentaFinancieraResponse crear(CuentaFinancieraRequest request) {
        Institucion institucion = institucionRepository.buscarPorIdConBloqueo(request.institucionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", request.institucionId()));
        if (!institucion.isActivo()) {
            throw new ReglaNegocioException("La institución debe estar activa para crear una cuenta financiera");
        }
        Plantel plantel = obtenerPlantel(request.plantelId(), institucion, true);
        validar(request, institucion, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nueva(normalizar(request), institucion, plantel)));
    }

    @Override
    public CuentaFinancieraResponse actualizar(Long id, CuentaFinancieraRequest request) {
        CuentaFinanciera cuenta = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta financiera", id));
        verificar(cuenta, request.version(), "Cuenta financiera");
        if (!cuenta.getInstitucion().getId().equals(request.institucionId())) {
            throw new ReglaNegocioException("No se puede cambiar la institución de una cuenta financiera");
        }
        if (movimientoRepository.existsByCuentaId(id)
                && (cuenta.getSaldoInicial().compareTo(request.saldoInicial()) != 0
                || !cuenta.getFechaSaldoInicial().equals(request.fechaSaldoInicial())
                || !cuenta.getMoneda().equalsIgnoreCase(request.moneda()))) {
            throw new ReglaNegocioException("La moneda, fecha y saldo inicial ya no pueden cambiarse porque la cuenta tiene movimientos");
        }
        Plantel plantel = obtenerPlantel(request.plantelId(), cuenta.getInstitucion(), request.activo());
        validar(request, cuenta.getInstitucion(), id);
        mapper.actualizar(cuenta, normalizar(request), plantel);
        return mapper.respuesta(repository.saveAndFlush(cuenta));
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaFinancieraResponse obtener(Long id) {
        return mapper.respuesta(repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta financiera", id)));
    }

    @Override
    public void desactivar(Long id, Long version) {
        CuentaFinanciera cuenta = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta financiera", id));
        verificar(cuenta, version, "Cuenta financiera");
        cuenta.setActivo(false);
    }

    private void validar(CuentaFinancieraRequest request, Institucion institucion, Long id) {
        if (repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(
                institucion.getId(), codigo(request.codigo()), id)) {
            throw new RecursoDuplicadoException("Ya existe una cuenta financiera con ese código en la institución");
        }
        if (!codigo(request.moneda()).equals(institucion.getMonedaPredeterminada())) {
            throw new ReglaNegocioException("La moneda debe coincidir con la moneda predeterminada de la institución");
        }
        if (request.fechaSaldoInicial().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha del saldo inicial no puede estar en el futuro");
        }
        boolean tieneBanco = limpiar(request.bancoNombre()) != null;
        boolean tieneNumero = limpiar(request.numeroCuenta()) != null;
        boolean tieneClabe = limpiar(request.clabe()) != null;
        if (request.tipo() == TipoCuentaFinanciera.CAJA && (tieneBanco || tieneNumero || tieneClabe)) {
            throw new ReglaNegocioException("Una cuenta de caja no debe contener banco, número de cuenta ni CLABE");
        }
        if (request.tipo() != TipoCuentaFinanciera.CAJA && (!tieneBanco || (!tieneNumero && !tieneClabe))) {
            throw new ReglaNegocioException("Una cuenta bancaria o de inversión requiere banco y al menos número de cuenta o CLABE");
        }
    }

    private Plantel obtenerPlantel(Long plantelId, Institucion institucion, boolean exigirActivo) {
        if (plantelId == null) return null;
        Plantel plantel = plantelRepository.findById(plantelId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el plantel", plantelId));
        if (!plantel.getInstitucion().getId().equals(institucion.getId())) {
            throw new ReglaNegocioException("El plantel de la cuenta no pertenece a la institución indicada");
        }
        if (exigirActivo && !plantel.isActivo()) {
            throw new ReglaNegocioException("El plantel debe estar activo para asignarle una cuenta financiera activa");
        }
        return plantel;
    }

    private CuentaFinancieraRequest normalizar(CuentaFinancieraRequest request) {
        boolean caja = request.tipo() == TipoCuentaFinanciera.CAJA;
        return new CuentaFinancieraRequest(request.institucionId(), request.plantelId(),
                codigo(request.codigo()), limpiar(request.nombre()), request.tipo(),
                caja ? null : limpiar(request.bancoNombre()), limpiar(request.titular()),
                caja ? null : limpiar(request.numeroCuenta()), caja ? null : limpiar(request.clabe()),
                codigo(request.moneda()), request.saldoInicial(), request.fechaSaldoInicial(),
                request.activo(), request.version());
    }
}
