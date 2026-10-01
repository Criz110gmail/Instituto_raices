package escuela.alumno.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoActualizacionExpediente {
    DOCUMENTO("Documento"),
    FICHA_MEDICA("Información médica");

    private final String etiqueta;
}
