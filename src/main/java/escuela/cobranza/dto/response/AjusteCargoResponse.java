package escuela.cobranza.dto.response;

import escuela.cobranza.entity.*;
import escuela.common.dto.response.AuditoriaResponse;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AjusteCargoResponse(Long id, Long cargoId, Long institucionId, Long plantelId,
                                  String alumnoMatricula, String alumnoNombre, String conceptoNombre,
                                  TipoAjusteCargo tipo, EfectoAjusteCargo efecto, BigDecimal monto,
                                  BigDecimal baseCalculo, BigDecimal porcentajeAplicado,
                                  Long becaAlumnoId, String motivo, String autorizadoPor,
                                  LocalDate fechaEfectiva, Long reversaDeId, boolean reversado,
                                  AuditoriaResponse auditoria) { }
