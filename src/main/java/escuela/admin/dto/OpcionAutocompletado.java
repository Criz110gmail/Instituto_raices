package escuela.admin.dto;

import java.math.BigDecimal;

public record OpcionAutocompletado(Long id, String titulo, String detalle, BigDecimal monto) {
    public OpcionAutocompletado(Long id, String titulo, String detalle) {
        this(id, titulo, detalle, null);
    }
}
