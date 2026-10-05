package escuela.finanzas.service;

import escuela.finanzas.entity.MovimientoFinanciero;
import escuela.finanzas.repository.MovimientoFinancieroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaldoCuentaFinancieraService {
    private final MovimientoFinancieroRepository movimientos;

    public BigDecimal consultar(Long cuentaId, BigDecimal saldoInicial) {
        return movimientos.findFirstByCuentaIdOrderBySecuenciaCuentaDesc(cuentaId)
                .map(MovimientoFinanciero::getSaldoPosterior).orElse(saldoInicial);
    }
}
