package escuela.admin.dto;

public record FiltroCatalogo(String q, String estado, int pagina, int tamanio) {

    public FiltroCatalogo normalizado() {
        return new FiltroCatalogo(q == null ? "" : q.trim(),
                estado == null ? "TODOS" : estado.trim().toUpperCase(),
                Math.max(0, pagina), Math.max(10, Math.min(100, tamanio)));
    }

    public FiltroCatalogo conPagina(int nuevaPagina) {
        return new FiltroCatalogo(q, estado, nuevaPagina, tamanio);
    }
}
