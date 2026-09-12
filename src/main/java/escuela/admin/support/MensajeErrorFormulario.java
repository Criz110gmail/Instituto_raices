package escuela.admin.support;

import escuela.common.exception.ReglaNegocioException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.sql.SQLException;

public final class MensajeErrorFormulario {

    private MensajeErrorFormulario() {
    }

    public static String desde(RuntimeException excepcion) {
        if (excepcion instanceof ReglaNegocioException) {
            return excepcion.getMessage();
        }
        if (excepcion instanceof ObjectOptimisticLockingFailureException) {
            return "La información fue modificada por otro proceso. Recarga los datos e intenta nuevamente.";
        }
        if (excepcion instanceof DataIntegrityViolationException) {
            return mensajeIntegridad(excepcion);
        }
        throw excepcion;
    }

    private static String mensajeIntegridad(Throwable excepcion) {
        SQLException sql = buscarSql(excepcion);
        String estado = sql == null ? null : sql.getSQLState();
        if ("23505".equals(estado)) {
            return "Ya existe un registro con la misma combinación de datos. Revisa el código, el orden y las relaciones seleccionadas.";
        }
        if ("23503".equals(estado)) {
            return "No fue posible guardar porque uno de los registros relacionados ya no está disponible.";
        }
        if ("23514".equals(estado) || "23P01".equals(estado)) {
            return "Uno o más datos no cumplen las reglas del sistema. Revisa los valores e intenta nuevamente.";
        }
        return "No fue posible guardar por una restricción de integridad. Revisa los datos e intenta nuevamente.";
    }

    private static SQLException buscarSql(Throwable excepcion) {
        Throwable actual = excepcion;
        while (actual != null) {
            if (actual instanceof SQLException sql) {
                return sql;
            }
            actual = actual.getCause();
        }
        return null;
    }
}
