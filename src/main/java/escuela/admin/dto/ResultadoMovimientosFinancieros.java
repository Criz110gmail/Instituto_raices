package escuela.admin.dto;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public record ResultadoMovimientosFinancieros(
        Page<MovimientoFinancieroFila> pagina,
        ResumenCuentaFinanciera cuenta,
        BigDecimal ingresosPagina,
        BigDecimal egresosPagina
) {
}
