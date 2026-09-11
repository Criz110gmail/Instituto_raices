package escuela.common.mapper;

import java.util.Locale;

public final class NormalizacionTexto {

    private NormalizacionTexto() {
    }

    public static String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }

    public static String codigo(String valor) {
        String limpio = limpiar(valor);
        return limpio == null ? null : limpio.toUpperCase(Locale.ROOT);
    }

    public static String email(String valor) {
        String limpio = limpiar(valor);
        return limpio == null ? null : limpio.toLowerCase(Locale.ROOT);
    }
}
