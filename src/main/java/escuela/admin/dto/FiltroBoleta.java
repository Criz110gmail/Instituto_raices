package escuela.admin.dto;

import java.util.Set;

public record FiltroBoleta(Long institucionId, Long cicloId, Long plantelId,
                           Long grupoId, String grupoTexto, String q,
                           int pagina, int tamanio) {
    public FiltroBoleta normalizado() {
        String grupo = grupoTexto == null ? "" : grupoTexto.trim().replaceAll("\\s+", " ");
        String busqueda = q == null ? "" : q.trim().replaceAll("\\s+", " ");
        int tamano = Set.of(10, 25, 50, 100).contains(tamanio) ? tamanio : 25;
        return new FiltroBoleta(institucionId, cicloId, plantelId, grupoId, grupo,
                busqueda.substring(0, Math.min(busqueda.length(), 100)), Math.max(0, pagina), tamano);
    }

    public FiltroBoleta conPagina(int nuevaPagina, int nuevoTamanio) {
        return new FiltroBoleta(institucionId, cicloId, plantelId, grupoId, grupoTexto,
                q, nuevaPagina, nuevoTamanio);
    }
}

