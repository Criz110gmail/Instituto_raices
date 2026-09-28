package escuela.admin.dto;

import org.springframework.data.domain.Page;

public record ResultadoBoletas(Page<BoletaListadoFila> pagina) { }

