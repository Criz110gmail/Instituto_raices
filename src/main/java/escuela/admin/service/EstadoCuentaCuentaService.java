package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstadoCuentaCuentaService {
    private final ReporteFinancieroConsultaService reportes;
    private final MovimientoFinancieroConsultaService movimientos;
    private final InstitucionService instituciones;
    private final AlcanceDatosService alcance;

    public FiltroEstadoCuentaCuenta normalizar(FiltroEstadoCuentaCuenta filtro) {
        if (filtro.institucionId() == null)
            throw new ReglaNegocioException("Selecciona una institución");
        alcance.validarInstitucion(filtro.institucionId());
        ZoneId zona = ZoneId.of(instituciones.obtener(filtro.institucionId()).zonaHoraria());
        return filtro.normalizado(LocalDate.now(zona));
    }

    public ResultadoEstadoCuentaCuenta consultar(FiltroEstadoCuentaCuenta original) {
        FiltroEstadoCuentaCuenta filtro = normalizar(original);
        if (filtro.cuentaId() == null) {
            return new ResultadoEstadoCuentaCuenta(null, null, Page.empty());
        }
        // Tesorería comprueba el alcance de la cuenta antes de exponer movimientos.
        var resumen = reportes.tesoreria(new FiltroReporteTesoreria(filtro.institucionId(),
                filtro.cuentaId(), filtro.cuentaTexto(), null, "MENSUAL",
                filtro.desde(), filtro.hasta(), 0, 10)).resumen();
        var detalle = movimientos.consultar(new FiltroMovimientoFinanciero(filtro.institucionId(),
                filtro.cuentaId(), filtro.cuentaTexto(), null, "TODOS", "TODOS",
                filtro.desde(), filtro.hasta(), filtro.pagina(), filtro.tamanio()));
        return new ResultadoEstadoCuentaCuenta(detalle.cuenta(), resumen, detalle.pagina());
    }
}
