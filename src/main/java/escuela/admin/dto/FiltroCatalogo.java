package escuela.admin.dto;

import java.time.LocalDate;

public record FiltroCatalogo(String q, String estado, LocalDate fecha, int pagina, int tamanio,
                             LocalDate desde, LocalDate hasta, String tipoFecha) {

    public FiltroCatalogo(String q, String estado, LocalDate fecha, int pagina, int tamanio) {
        this(q, estado, fecha, pagina, tamanio, null, null, "VENCIMIENTO");
    }

    public FiltroCatalogo(String q, String estado, int pagina, int tamanio) {
        this(q, estado, null, pagina, tamanio);
    }

    public FiltroCatalogo normalizado() {
        return new FiltroCatalogo(q == null ? "" : q.trim(),
                estado == null ? "TODOS" : estado.trim().toUpperCase(), fecha,
                Math.max(0, pagina), Math.max(10, Math.min(100, tamanio)), desde, hasta,
                "REGISTRO".equals(tipoFecha) ? "REGISTRO" : "VENCIMIENTO");
    }

    public FiltroCatalogo conPagina(int nuevaPagina) {
        return new FiltroCatalogo(q, estado, fecha, nuevaPagina, tamanio, desde, hasta, tipoFecha);
    }

    public boolean tieneRango() { return desde != null || hasta != null; }
    public void validarRango() {
        if (desde != null && hasta != null && desde.isAfter(hasta))
            throw new escuela.common.exception.ReglaNegocioException("La fecha Desde no puede ser posterior a Hasta. Corrige el rango para buscar o exportar.");
    }
}
