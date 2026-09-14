package escuela.cobranza.dto.response;

import escuela.cobranza.entity.EstadoCuota;
import escuela.cobranza.entity.FrecuenciaCuota;
import escuela.common.dto.response.AuditoriaResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuotaAlumnoResponse(
        Long id,
        Long inscripcionId,
        Long institucionId,
        Long plantelId,
        String plantelNombre,
        Long alumnoId,
        String alumnoMatricula,
        String alumnoNombre,
        String numeroInscripcion,
        Long conceptoCobroId,
        String conceptoCodigo,
        String conceptoNombre,
        BigDecimal importeBase,
        String moneda,
        FrecuenciaCuota frecuencia,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer diaVencimiento,
        LocalDate fechaVencimientoUnico,
        boolean generacionAutomatica,
        String motivoImportePersonalizado,
        EstadoCuota estado,
        AuditoriaResponse auditoria
) {
}
