package escuela.comunicacion.dto.response;

import escuela.comunicacion.entity.TipoDestinatarioEvento;

public record DestinatarioEventoResponse(TipoDestinatarioEvento tipo, Long id,
                                         String titulo, String detalle) {
    public String clave() { return tipo.name() + ":" + id; }
}
