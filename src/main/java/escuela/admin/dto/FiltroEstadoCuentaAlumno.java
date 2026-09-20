package escuela.admin.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

public record FiltroEstadoCuentaAlumno(Long institucionId, Long alumnoId, String alumnoTexto,
                                       Long plantelId, String situacion,
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCorte,
                                       int pagina, int tamanio) {
    private static final Set<String> SITUACIONES = Set.of(
            "TODOS", "PENDIENTE", "PARCIAL", "PAGADO", "VENCIDO", "CANCELADO");

    public FiltroEstadoCuentaAlumno normalizado(LocalDate hoy) {
        String texto = alumnoTexto == null ? "" : alumnoTexto.trim().replaceAll("\\s+", " ");
        String estado = situacion == null ? "TODOS" : situacion.toUpperCase();
        if (!SITUACIONES.contains(estado)) estado = "TODOS";
        int tam = Set.of(10, 25, 50, 100).contains(tamanio) ? tamanio : 25;
        return new FiltroEstadoCuentaAlumno(institucionId, alumnoId, texto, plantelId, estado,
                fechaCorte == null ? hoy : fechaCorte, Math.max(0, pagina), tam);
    }

    public FiltroEstadoCuentaAlumno conPagina(int nuevaPagina, int nuevoTamanio) {
        return new FiltroEstadoCuentaAlumno(institucionId, alumnoId, alumnoTexto, plantelId,
                situacion, fechaCorte, nuevaPagina, nuevoTamanio);
    }
}
