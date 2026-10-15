package escuela.admin.service;
import escuela.cobranza.repository.CargoRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CargosPagoExclusionesTest {
    @Test void excluyeAntesDeOrdenarYLimitarSinCambiarAlcanceSaldoNiConsultaOriginal()throws Exception {
        String original=CargoRepository.class.getMethod("buscarParaSolicitudPago",Long.class,Long.class,String.class,Pageable.class).getAnnotation(Query.class).value();
        String filtrada=CargoRepository.class.getMethod("buscarParaSolicitudPagoExcluyendo",Long.class,Long.class,String.class,java.util.Collection.class,Pageable.class).getAnnotation(Query.class).value();
        assertThat(filtrada).contains("a.institucion_id = :institucionId","v.tutor_id = :tutorId","es_responsable_financiero","GREATEST","NOT IN (:excluirIds)");
        assertThat(filtrada.indexOf("NOT IN")).isLessThan(filtrada.indexOf("ORDER BY"));
        assertThat(filtrada.replace(" AND c.id NOT IN (:excluirIds)","")).isEqualTo(original);
    }
}
