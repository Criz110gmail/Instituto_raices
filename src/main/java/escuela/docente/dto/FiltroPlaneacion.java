package escuela.docente.dto;

import escuela.docente.entity.EstadoPlaneacion;
import java.time.LocalDate;

public record FiltroPlaneacion(Long institucionId, Long plantelId, Long maestroId, Long grupoId,
                              EstadoPlaneacion estado, LocalDate desde, LocalDate hasta,
                              String texto, int pagina, int tamanio) {
    public FiltroPlaneacion normalizado() {
        return new FiltroPlaneacion(institucionId, plantelId, maestroId, grupoId, estado, desde, hasta,
                texto == null ? "" : texto.trim(), Math.max(0,pagina),
                tamanio==25||tamanio==50||tamanio==100?tamanio:10);
    }
    public FiltroPlaneacion conPagina(int p, int t) {
        return new FiltroPlaneacion(institucionId,plantelId,maestroId,grupoId,estado,desde,hasta,texto,p,t);
    }
}
