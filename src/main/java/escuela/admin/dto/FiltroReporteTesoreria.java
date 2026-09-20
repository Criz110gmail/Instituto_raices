package escuela.admin.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

public record FiltroReporteTesoreria(Long institucionId, Long cuentaId, String cuentaTexto,
                                     Long plantelId, String agrupacion,
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                                     int pagina, int tamanio) {
    public FiltroReporteTesoreria normalizado(LocalDate hoy) {
        String cuenta = cuentaTexto == null ? "" : cuentaTexto.trim().replaceAll("\\s+", " ");
        String grupo = agrupacion == null ? "DIARIA" : agrupacion.toUpperCase();
        if (!Set.of("DIARIA", "MENSUAL", "ANUAL").contains(grupo)) grupo = "DIARIA";
        LocalDate hasta = fechaHasta == null ? hoy : fechaHasta;
        LocalDate desde = fechaDesde == null ? hasta.withDayOfMonth(1) : fechaDesde;
        int tam = Set.of(10, 25, 50, 100).contains(tamanio) ? tamanio : 25;
        return new FiltroReporteTesoreria(institucionId, cuentaId, cuenta, plantelId,
                grupo, desde, hasta, Math.max(0, pagina), tam);
    }

    public FiltroReporteTesoreria conPagina(int nuevaPagina, int nuevoTamanio) {
        return new FiltroReporteTesoreria(institucionId, cuentaId, cuentaTexto, plantelId,
                agrupacion, fechaDesde, fechaHasta, nuevaPagina, nuevoTamanio);
    }
}
