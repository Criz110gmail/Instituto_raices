package escuela.cobranza.dto.response;

import escuela.cobranza.entity.*;
import escuela.common.dto.response.AuditoriaResponse;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BecaAlumnoResponse(Long id, Long inscripcionId, Long institucionId, Long plantelId,
                                 String alumnoMatricula, String alumnoNombre, String numeroInscripcion,
                                 Long tipoBecaId, String tipoBecaNombre, Long conceptoCobroId,
                                 String conceptoNombre, ModalidadBeca modalidad, BigDecimal porcentaje,
                                 BigDecimal montoFijo, String moneda, LocalDate fechaInicio,
                                 LocalDate fechaFin, String motivo, EstadoBeca estado,
                                 String autorizadoPor, AuditoriaResponse auditoria) { }
