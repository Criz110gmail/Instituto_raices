package escuela.docente.dto;

import escuela.seguridad.entity.EstadoUsuario;

public record PortalMaestroCuentaResponse(Long usuarioId, String username, String email,
                                          EstadoUsuario estado, boolean credencialConfigurada,
                                          Long version) { }
