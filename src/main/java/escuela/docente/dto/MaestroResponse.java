package escuela.docente.dto;

import escuela.seguridad.entity.EstadoUsuario;

public record MaestroResponse(Long id, Long institucionId, String institucion, String numeroEmpleado,
                              String nombres, String primerApellido, String segundoApellido,
                              String nombreCompleto, String email, String telefono, boolean activo,
                              Long usuarioId, String username, EstadoUsuario estadoCuenta,
                              Long version) { }
