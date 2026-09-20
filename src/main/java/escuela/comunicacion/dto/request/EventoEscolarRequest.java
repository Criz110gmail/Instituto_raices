package escuela.comunicacion.dto.request;

import escuela.comunicacion.entity.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record EventoEscolarRequest(
        @NotNull Long institucionId, @NotNull Long cicloEscolarId, Long plantelId,
        @NotBlank @Size(max = 200) String titulo,
        @NotBlank @Size(max = 250) String ubicacion,
        @Size(max = 5000) String descripcion,
        @NotNull LocalDateTime inicioLocal, @NotNull LocalDateTime finLocal,
        @NotNull TipoEventoEscolar tipo, @NotNull AlcanceEventoEscolar alcance,
        @Valid List<DestinatarioEventoRequest> destinatarios, Long version) { }
