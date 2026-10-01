package escuela.alumno.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EstadoActualizacionExpediente {
    ENVIADA("Pendiente de revisión"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada");

    private final String etiqueta;
}
