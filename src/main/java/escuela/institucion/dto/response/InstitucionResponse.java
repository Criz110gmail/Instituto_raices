package escuela.institucion.dto.response;

import escuela.common.dto.response.AuditoriaResponse;

public record InstitucionResponse(
        Long id, String codigo, String nombre, String nombreComercial, String razonSocial,
        String rfc, String email, String telefono, String sitioWeb, String domicilioFiscal,
        String ciudad, String estado, String codigoPostal, String pais, Long logoArchivoId,
        String zonaHoraria, String monedaPredeterminada, boolean activo, AuditoriaResponse auditoria
) {
}
