package escuela.admin.dto;

public record OpcionGrado(
        Long id, Long nivelEducativoId, Long institucionId, String etiqueta, boolean activo
) {
}
