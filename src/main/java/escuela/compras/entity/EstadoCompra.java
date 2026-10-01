package escuela.compras.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public enum EstadoCompra {
    BORRADOR("Borrador"), CONFIRMADA("Confirmada"), CANCELADA("Cancelada");
    private final String etiqueta;
}
