package escuela.admin.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.Set;

public record FiltroConcentradoCobranza(Long institucionId, Long plantelId, String agrupacion,
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCorte,
                                        int pagina, int tamanio) {
    public FiltroConcentradoCobranza normalizado(LocalDate hoy) {
        String grupo = agrupacion == null ? "CONCEPTO" : agrupacion.toUpperCase();
        if (!Set.of("CONCEPTO", "PLANTEL", "CICLO").contains(grupo)) grupo = "CONCEPTO";
        int tam = Set.of(10, 25, 50, 100).contains(tamanio) ? tamanio : 25;
        return new FiltroConcentradoCobranza(institucionId, plantelId, grupo,
                fechaCorte == null ? hoy : fechaCorte, Math.max(0, pagina), tam);
    }
    public FiltroConcentradoCobranza conPagina(int nuevaPagina, int nuevoTamanio) {
        return new FiltroConcentradoCobranza(institucionId, plantelId, agrupacion,
                fechaCorte, nuevaPagina, nuevoTamanio);
    }
}
