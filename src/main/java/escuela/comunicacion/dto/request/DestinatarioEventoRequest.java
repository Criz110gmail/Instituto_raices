package escuela.comunicacion.dto.request;

import escuela.comunicacion.entity.TipoDestinatarioEvento;
import jakarta.validation.constraints.NotNull;

public record DestinatarioEventoRequest(@NotNull TipoDestinatarioEvento tipo, @NotNull Long id) { }
