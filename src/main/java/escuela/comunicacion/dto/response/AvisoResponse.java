package escuela.comunicacion.dto.response;

import escuela.comunicacion.entity.EstadoAviso;
import escuela.common.dto.response.AuditoriaResponse;

import java.time.*;

public record AvisoResponse(Long id, Long institucionId, String institucion,
                            Long plantelId, String plantel, String titulo, String contenido,
                            EstadoAviso estado, Instant publicadoEn, LocalDateTime expiraLocal,
                            Instant retiradoEn, String motivoRetiro, String zonaHoraria,
                            AuditoriaResponse auditoria) { }
