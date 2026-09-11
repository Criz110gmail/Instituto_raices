package escuela.institucion.dto.response;

import escuela.common.dto.response.AuditoriaResponse;

public record PlantelResponse(
        Long id, Long institucionId, String codigo, String nombre, String telefono, String email,
        String calle, String numeroExterior, String numeroInterior, String colonia, String ciudad,
        String estado, String codigoPostal, String pais, boolean activo, AuditoriaResponse auditoria
) {
}
