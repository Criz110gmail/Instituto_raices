package escuela.cobranza.dto.response;

import java.util.List;
import java.util.Set;

public record CobranzaInscripcionResumen(
        List<CuotaAlumnoResponse> cuotas,
        List<CargoResponse> cargos,
        Set<Long> cuotasConCargo,
        boolean cargoPagado,
        TutorPagoSugerido tutorSugerido
) {
    public CobranzaInscripcionResumen {
        cuotas = List.copyOf(cuotas);
        cargos = List.copyOf(cargos);
        cuotasConCargo = Set.copyOf(cuotasConCargo);
    }

    public record TutorPagoSugerido(Long id, String etiqueta, int responsablesDisponibles) {
    }
}
