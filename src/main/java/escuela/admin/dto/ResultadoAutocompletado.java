package escuela.admin.dto;

import java.util.List;

public record ResultadoAutocompletado(List<OpcionAutocompletado> resultados, boolean hayMas) {
    public ResultadoAutocompletado {
        resultados = List.copyOf(resultados);
    }

    public static ResultadoAutocompletado vacio() {
        return new ResultadoAutocompletado(List.of(), false);
    }
}
