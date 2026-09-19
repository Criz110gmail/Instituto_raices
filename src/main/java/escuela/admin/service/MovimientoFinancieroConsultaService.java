package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.entity.DireccionMovimiento;
import escuela.finanzas.entity.MovimientoFinanciero;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.finanzas.repository.MovimientoFinancieroRepository;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovimientoFinancieroConsultaService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter
            .ofPattern("dd MMM yyyy · HH:mm", new Locale("es", "MX"));

    private final MovimientoFinancieroRepository movimientoRepository;
    private final CuentaFinancieraRepository cuentaRepository;
    private final InstitucionRepository institucionRepository;
    private final AlcanceDatosService alcance;

    public ResultadoMovimientosFinancieros consultar(FiltroMovimientoFinanciero original) {
        FiltroMovimientoFinanciero filtro = original.normalizado();
        if (filtro.institucionId() == null) {
            throw new ReglaNegocioException("Selecciona una institución para consultar el libro financiero");
        }
        if (filtro.fechaDesde() != null && filtro.fechaHasta() != null
                && filtro.fechaDesde().isAfter(filtro.fechaHasta())) {
            throw new ReglaNegocioException("La fecha inicial no puede ser posterior a la fecha final");
        }
        alcance.validarInstitucion(filtro.institucionId());
        var institucion = institucionRepository.findById(filtro.institucionId())
                .orElseThrow(() -> new ReglaNegocioException("La institución seleccionada no existe"));
        ZoneId zona = ZoneId.of(institucion.getZonaHoraria());
        Specification<MovimientoFinanciero> especificacion = especificacion(filtro, zona);
        Page<MovimientoFinancieroFila> pagina = movimientoRepository.findAll(especificacion,
                PageRequest.of(filtro.pagina(), filtro.tamanio(),
                        Sort.by(Sort.Order.desc("fechaOperacion"), Sort.Order.desc("id"))))
                .map(movimiento -> fila(movimiento, zona));

        BigDecimal ingresos = pagina.stream()
                .filter(fila -> fila.direccion().equals(DireccionMovimiento.INGRESO.name()))
                .map(MovimientoFinancieroFila::monto).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal egresos = pagina.stream()
                .filter(fila -> fila.direccion().equals(DireccionMovimiento.EGRESO.name()))
                .map(MovimientoFinancieroFila::monto).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ResultadoMovimientosFinancieros(pagina, resumenCuenta(filtro), ingresos, egresos);
    }

    private Specification<MovimientoFinanciero> especificacion(FiltroMovimientoFinanciero filtro, ZoneId zona) {
        return Specification.where(alcance.<MovimientoFinanciero>especificacion(ModuloCatalogo.MOVIMIENTOS_FINANCIEROS))
                .and((root, query, cb) -> cb.equal(root.get("institucion").get("id"), filtro.institucionId()))
                .and((root, query, cb) -> filtro.cuentaId() == null ? cb.conjunction()
                        : cb.equal(root.get("cuenta").get("id"), filtro.cuentaId()))
                .and((root, query, cb) -> filtro.plantelId() == null ? cb.conjunction()
                        : cb.equal(root.get("plantelOperacion").get("id"), filtro.plantelId()))
                .and((root, query, cb) -> filtro.direccion().equals("TODOS") ? cb.conjunction()
                        : cb.equal(root.get("direccion").as(String.class), filtro.direccion()))
                .and((root, query, cb) -> filtro.clase().equals("TODOS") ? cb.conjunction()
                        : cb.equal(root.get("clase").as(String.class), filtro.clase()))
                .and((root, query, cb) -> filtro.fechaDesde() == null ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("fechaOperacion"),
                        filtro.fechaDesde().atStartOfDay(zona).toInstant()))
                .and((root, query, cb) -> filtro.fechaHasta() == null ? cb.conjunction()
                        : cb.lessThan(root.get("fechaOperacion"),
                        filtro.fechaHasta().plusDays(1).atStartOfDay(zona).toInstant()));
    }

    private ResumenCuentaFinanciera resumenCuenta(FiltroMovimientoFinanciero filtro) {
        if (filtro.cuentaId() == null) return null;
        var cuenta = cuentaRepository.findOne(Specification
                        .where(alcance.<escuela.finanzas.entity.CuentaFinanciera>especificacion(
                                ModuloCatalogo.CUENTAS_FINANCIERAS))
                        .and((root, query, cb) -> cb.equal(root.get("id"), filtro.cuentaId()))
                        .and((root, query, cb) -> cb.equal(root.get("institucion").get("id"),
                                filtro.institucionId())))
                .orElseThrow(() -> new ReglaNegocioException("La cuenta seleccionada no está disponible en tu alcance"));
        var ultimo = movimientoRepository.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(cuenta.getId());
        BigDecimal saldo = ultimo.map(MovimientoFinanciero::getSaldoPosterior).orElse(cuenta.getSaldoInicial());
        long movimientos = movimientoRepository.count((root, query, cb) ->
                cb.equal(root.get("cuenta").get("id"), cuenta.getId()));
        String alcanceCuenta = cuenta.getPlantel() == null ? "Institucional" : cuenta.getPlantel().getNombre();
        return new ResumenCuentaFinanciera(cuenta.getId(), cuenta.getCodigo() + " · " + cuenta.getNombre(),
                cuenta.getTipo().name(), alcanceCuenta, cuenta.getMoneda(), saldo, movimientos);
    }

    private MovimientoFinancieroFila fila(MovimientoFinanciero movimiento, ZoneId zona) {
        return new MovimientoFinancieroFila(movimiento.getId(),
                FECHA.withZone(zona).format(movimiento.getFechaOperacion()),
                movimiento.getCuenta().getCodigo() + " · " + movimiento.getCuenta().getNombre(),
                movimiento.getPlantelOperacion() == null ? "Institucional" : movimiento.getPlantelOperacion().getNombre(),
                movimiento.getDireccion().name(), movimiento.getClase().name(), movimiento.getConcepto(),
                valor(movimiento.getReferencia()), valor(movimiento.getTerceroNombre()), movimiento.getMonto(),
                movimiento.getCuenta().getMoneda(), movimiento.getSaldoAnterior(), movimiento.getSaldoPosterior(),
                movimiento.getPago() == null ? "—" : movimiento.getPago().getFolio(), movimiento.getSecuenciaCuenta(),
                movimiento.getTransferencia() == null ? null : movimiento.getTransferencia().getId(),
                movimiento.getReversaDe() == null ? null : movimiento.getReversaDe().getId(),
                movimiento.getClase() == escuela.finanzas.entity.ClaseMovimiento.OPERACION
                        ? movimiento.getReversa() != null
                        : movimiento.getClase() == escuela.finanzas.entity.ClaseMovimiento.TRASPASO
                        && movimiento.getTransferencia().getEstado()
                        == escuela.finanzas.entity.EstadoTransferenciaCuenta.REVERTIDA);
    }

    private String valor(String valor) {
        return valor == null || valor.isBlank() ? "—" : valor;
    }
}
