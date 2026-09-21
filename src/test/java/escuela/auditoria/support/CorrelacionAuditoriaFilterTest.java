package escuela.auditoria.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelacionAuditoriaFilterTest {
    @AfterEach
    void limpiarContexto() {
        CorrelacionAuditoria.limpiar();
    }

    @Test
    void comparteLaCorrelacionDuranteLaPeticionYLaLimpiaAlTerminar() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> observadaEnCadena = new AtomicReference<>();

        new CorrelacionAuditoriaFilter().doFilterInternal(new MockHttpServletRequest(), response,
                (request, respuesta) -> observadaEnCadena.set(CorrelacionAuditoria.actualOGenerar()));

        String cabecera = response.getHeader(CorrelacionAuditoriaFilter.CABECERA);
        assertThat(cabecera).isNotBlank().isEqualTo(observadaEnCadena.get());
        assertThat(CorrelacionAuditoria.actualOGenerar()).isNotEqualTo(cabecera);
    }
}
