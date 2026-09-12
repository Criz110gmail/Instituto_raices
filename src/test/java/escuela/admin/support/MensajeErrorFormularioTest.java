package escuela.admin.support;

import escuela.common.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class MensajeErrorFormularioTest {

    @Test
    void conservaElMensajeDeUnaReglaDeNegocio() {
        String mensaje = MensajeErrorFormulario.desde(
                new ReglaNegocioException("Ya existe un registro con ese código"));

        assertThat(mensaje).isEqualTo("Ya existe un registro con ese código");
    }

    @Test
    void traduceUnaRestriccionUnicaSinExponerDetallesSql() {
        RuntimeException excepcion = new DataIntegrityViolationException(
                "insert into tabla...", new SQLException("detalle interno", "23505"));

        String mensaje = MensajeErrorFormulario.desde(excepcion);

        assertThat(mensaje).contains("Ya existe un registro")
                .doesNotContain("insert", "detalle interno", "23505");
    }

    @Test
    void explicaElConflictoOptimista() {
        String mensaje = MensajeErrorFormulario.desde(
                new ObjectOptimisticLockingFailureException("Grado", 7L));

        assertThat(mensaje).contains("modificada por otro proceso", "Recarga");
    }
}
