package escuela.admin.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.Set;

public record FiltroEventoEscolar(Long institucionId, Long plantelId, String texto,
                                  String estado, String tipo,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                                  int pagina, int tamanio) {
    public FiltroEventoEscolar normalizado() {
        String q = texto == null ? "" : texto.trim().replaceAll("\\s+", " ");
        String e = estado == null ? "TODOS" : estado.toUpperCase();
        String t = tipo == null ? "TODOS" : tipo.toUpperCase();
        if (!Set.of("TODOS", "BORRADOR", "PUBLICADO", "CANCELADO").contains(e)) e = "TODOS";
        if (!Set.of("TODOS", "JUNTA", "FESTIVAL", "SUSPENSION", "ACTIVIDAD", "OTRO").contains(t)) t = "TODOS";
        int tam = Set.of(10, 25, 50, 100).contains(tamanio) ? tamanio : 25;
        return new FiltroEventoEscolar(institucionId, plantelId, q, e, t, fechaDesde, fechaHasta,
                Math.max(0, pagina), tam);
    }
    public FiltroEventoEscolar conPagina(int nuevaPagina, int nuevoTamanio) {
        return new FiltroEventoEscolar(institucionId, plantelId, texto, estado, tipo,
                fechaDesde, fechaHasta, nuevaPagina, nuevoTamanio);
    }
}
