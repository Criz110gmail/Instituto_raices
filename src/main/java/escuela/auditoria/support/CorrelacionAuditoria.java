package escuela.auditoria.support;

import java.util.UUID;

public final class CorrelacionAuditoria {
    private static final ThreadLocal<String> ACTUAL = new ThreadLocal<>();

    private CorrelacionAuditoria() { }

    public static String iniciar() {
        String id = UUID.randomUUID().toString();
        ACTUAL.set(id);
        return id;
    }

    public static String actualOGenerar() {
        String id = ACTUAL.get();
        return id == null ? UUID.randomUUID().toString() : id;
    }

    public static void limpiar() { ACTUAL.remove(); }
}
