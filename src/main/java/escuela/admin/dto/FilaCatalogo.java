package escuela.admin.dto;

import java.util.List;

public record FilaCatalogo(Long id, List<String> celdas, String estado, String tono) {
}
