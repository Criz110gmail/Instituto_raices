package escuela.admin.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record ResultadoCatalogo(ModuloCatalogo modulo, List<String> columnas, Page<FilaCatalogo> pagina) {
}
