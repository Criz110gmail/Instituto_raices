package escuela.alumno.dto;

import escuela.alumno.entity.*;
import java.time.LocalDate;

public record FiltroActualizacionExpediente(
        Long institucionId, Long alumnoId, TipoActualizacionExpediente tipo,
        EstadoActualizacionExpediente estado, LocalDate desde, LocalDate hasta,
        String texto, int pagina, int tamanio) {
    public FiltroActualizacionExpediente normalizado() {
        return new FiltroActualizacionExpediente(institucionId, alumnoId, tipo, estado, desde, hasta,
                texto == null ? "" : texto.trim(), Math.max(0, pagina),
                java.util.Set.of(10, 25, 50, 100).contains(tamanio) ? tamanio : 25);
    }
    public FiltroActualizacionExpediente conPagina(int pagina, int tamanio) {
        return new FiltroActualizacionExpediente(institucionId, alumnoId, tipo, estado, desde, hasta,
                texto, pagina, tamanio);
    }
}
