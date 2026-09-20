package escuela.admin.dto;

import org.springframework.data.domain.Page;

public record ResultadoReporteTesoreria(Page<ReporteTesoreriaFila> pagina,
                                         ResumenTesoreria resumen) { }
