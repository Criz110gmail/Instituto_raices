package escuela.tutor.dto.response;

import escuela.common.dto.response.AuditoriaResponse;

import java.time.LocalDate;

public record TutorResponse(
        Long id,
        Long institucionId,
        Long usuarioId,
        String usuarioNombre,
        String nombres,
        String primerApellido,
        String segundoApellido,
        String telefonoPrincipal,
        String telefonoSecundario,
        String email,
        LocalDate fechaNacimiento,
        String calle,
        String numeroExterior,
        String numeroInterior,
        String colonia,
        String ciudad,
        String estado,
        String codigoPostal,
        String pais,
        String ocupacion,
        String lugarTrabajo,
        String telefonoTrabajo,
        boolean activo,
        AuditoriaResponse auditoria
) {
}
