package escuela.cobranza.dto.response;

public record GeneracionCargosResponse(
        int cuotasRevisadas,
        int cargosGenerados,
        int cargosYaExistentes
) {
}
