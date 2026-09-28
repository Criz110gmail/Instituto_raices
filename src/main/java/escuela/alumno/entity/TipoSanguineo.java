package escuela.alumno.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoSanguineo {
    DESCONOCIDO("No especificado"),
    A_POSITIVO("A+"),
    A_NEGATIVO("A−"),
    B_POSITIVO("B+"),
    B_NEGATIVO("B−"),
    AB_POSITIVO("AB+"),
    AB_NEGATIVO("AB−"),
    O_POSITIVO("O+"),
    O_NEGATIVO("O−");

    private final String etiqueta;
}

