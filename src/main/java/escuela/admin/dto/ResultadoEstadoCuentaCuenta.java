package escuela.admin.dto;

import org.springframework.data.domain.Page;
import java.math.BigDecimal;

public record ResultadoEstadoCuentaCuenta(ResumenCuentaFinanciera cuenta,
                                          ResumenTesoreria resumen,
                                          Page<MovimientoFinancieroFila> movimientos) {
    public BigDecimal aperturaRegistradaEnPeriodo() {
        if (resumen == null) return BigDecimal.ZERO;
        return resumen.saldoCierre().subtract(resumen.saldoApertura())
                .subtract(resumen.ingresosOperativos()).subtract(resumen.traspasosEntrada())
                .add(resumen.egresosOperativos()).add(resumen.traspasosSalida());
    }
}
