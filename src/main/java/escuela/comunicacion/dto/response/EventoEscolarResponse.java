package escuela.comunicacion.dto.response;

import escuela.comunicacion.entity.*;
import escuela.common.dto.response.AuditoriaResponse;

import java.time.*;
import java.util.List;

public record EventoEscolarResponse(
        Long id, Long institucionId, String institucion, Long cicloEscolarId, String cicloEscolar,
        Long plantelId, String plantel, String titulo, String ubicacion, String descripcion,
        LocalDateTime inicioLocal, LocalDateTime finLocal, String zonaHoraria,
        TipoEventoEscolar tipo, EstadoEventoEscolar estado, AlcanceEventoEscolar alcance,
        Instant publicadoEn, Instant canceladoEn, String motivoCancelacion,
        List<DestinatarioEventoResponse> destinatarios, AuditoriaResponse auditoria) { }
