package escuela.finanzas.service;

import escuela.finanzas.entity.MovimientoFinanciero;
import escuela.finanzas.repository.MovimientoFinancieroRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SaldoCuentaFinancieraServiceTest {
    private final MovimientoFinancieroRepository movimientos = mock(MovimientoFinancieroRepository.class);
    private final SaldoCuentaFinancieraService service = new SaldoCuentaFinancieraService(movimientos);

    @Test void sinMovimientosMuestraElSaldoInicial() {
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(1L)).thenReturn(Optional.empty());
        assertThat(service.consultar(1L, new BigDecimal("1000.00"))).isEqualByComparingTo("1000.00");
    }

    @Test void incluyeLosMovimientosSinSumarDosVecesElSaldoInicial() {
        var ultimo = new MovimientoFinanciero();
        ultimo.setSaldoPosterior(new BigDecimal("1330.00"));
        when(movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(1L)).thenReturn(Optional.of(ultimo));
        assertThat(service.consultar(1L, new BigDecimal("1000.00"))).isEqualByComparingTo("1330.00");
    }
}
