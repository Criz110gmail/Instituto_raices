package escuela.common.support;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Formato visual único para importes expresados en pesos. */
@Component("formatoMoneda")
public class FormatoMoneda {

    private static final DecimalFormatSymbols SIMBOLOS = DecimalFormatSymbols.getInstance(Locale.US);

    public String pesos(Number valor) {
        return valor == null ? "—" : formatear(new BigDecimal(valor.toString()));
    }

    public static String formatear(BigDecimal valor) {
        if (valor == null) return "—";
        DecimalFormat formato = new DecimalFormat("$#,##0.00;-$#,##0.00", SIMBOLOS);
        formato.setParseBigDecimal(true);
        return formato.format(valor);
    }
}
