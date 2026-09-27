package escuela.admin.dto;

import org.springframework.data.domain.Page;

public record ResultadoConcentradoCobranza(Page<ConcentradoCobranzaFila> pagina,
                                            ResumenEstadoCuenta resumen) { }
