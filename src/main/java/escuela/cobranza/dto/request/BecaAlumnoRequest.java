package escuela.cobranza.dto.request;

import escuela.cobranza.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BecaAlumnoRequest(@NotNull Long inscripcionId, @NotNull Long tipoBecaId,
                                @NotNull Long conceptoCobroId, @NotNull ModalidadBeca modalidad,
                                BigDecimal porcentaje, BigDecimal montoFijo, @Size(max = 3) String moneda,
                                @NotNull LocalDate fechaInicio, @NotNull LocalDate fechaFin,
                                @NotBlank @Size(max = 2000) String motivo,
                                @NotNull EstadoBeca estado, Long version) { }
