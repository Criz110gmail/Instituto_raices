package escuela.admin.service;

import escuela.cobranza.repository.CargoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import static org.assertj.core.api.Assertions.assertThat;

class CargosPagoSaldoAntesDePaginarTest {
    @Test void descartaLiquidadosEnSqlAntesDeLimitarYConservaAlcance() throws Exception {
        var sql=CargoRepository.class.getMethod("buscarParaSolicitudPago",Long.class,Long.class,String.class,Pageable.class).getAnnotation(Query.class).value();
        assertThat(sql).contains("a.institucion_id = :institucionId", "v.tutor_id = :tutorId",
                "v.es_responsable_financiero = true", "c.estado_registro = 'EMITIDO'",
                "GREATEST(c.importe_original + COALESCE", "FROM ajuste_cargo ac WHERE ac.cargo_id = c.id",
                "FROM aplicacion_pago ap WHERE ap.cargo_id = c.id", "ap.operacion = 'APLICAR'",
                "ac.efecto = 'AUMENTO'");
        assertThat(sql.indexOf("), 0) > 0")).isLessThan(sql.indexOf("ORDER BY"));
    }
}
