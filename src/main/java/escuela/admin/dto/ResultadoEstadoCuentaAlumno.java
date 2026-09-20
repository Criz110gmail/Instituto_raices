package escuela.admin.dto;

import org.springframework.data.domain.Page;

public record ResultadoEstadoCuentaAlumno(Long alumnoId, String alumno,
                                           String matricula, Page<EstadoCuentaCargoFila> pagina,
                                           ResumenEstadoCuenta resumen) { }
