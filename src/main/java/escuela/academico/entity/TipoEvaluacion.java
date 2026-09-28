package escuela.academico.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoEvaluacion {
    NUMERICA("Numérica"),
    CUALITATIVA("Cualitativa");

    private final String etiqueta;
}

