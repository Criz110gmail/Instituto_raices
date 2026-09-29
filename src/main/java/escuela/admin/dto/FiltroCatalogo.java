package escuela.admin.dto;

import java.time.LocalDate;

public record FiltroCatalogo(String q, String estado, LocalDate fecha, int pagina, int tamanio) {

    public FiltroCatalogo(String q, String estado, int pagina, int tamanio) {
        this(q, estado, null, pagina, tamanio);
    }

    public FiltroCatalogo normalizado() {
        return new FiltroCatalogo(q == null ? "" : q.trim(),
                estado == null ? "TODOS" : estado.trim().toUpperCase(), fecha,
                Math.max(0, pagina), Math.max(10, Math.min(100, tamanio)));
    }

    public FiltroCatalogo conPagina(int nuevaPagina) {
        return new FiltroCatalogo(q, estado, fecha, nuevaPagina, tamanio);
    }
}
