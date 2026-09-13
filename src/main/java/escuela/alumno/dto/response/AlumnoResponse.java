package escuela.alumno.dto.response;

import escuela.common.dto.response.AuditoriaResponse;

import java.time.LocalDate;

public record AlumnoResponse(
        Long id,
        Long institucionId,
        String matricula,
        String nombres,
        String primerApellido,
        String segundoApellido,
        String curp,
        LocalDate fechaNacimiento,
        String sexo,
        String lugarNacimiento,
        String nacionalidad,
        String telefono,
        String email,
        String calle,
        String numeroExterior,
        String numeroInterior,
        String colonia,
        String ciudad,
        String estado,
        String codigoPostal,
        String pais,
        LocalDate fechaIngreso,
        String observaciones,
        boolean activo,
        Long fotografiaArchivoId,
        AuditoriaResponse auditoria
) {
}
