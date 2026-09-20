package escuela.admin.dto;

import org.springframework.data.domain.Page;

public record ResultadoCortesCaja(Page<CorteCajaFila> pagina, long abiertos) { }
