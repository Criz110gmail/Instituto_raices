package escuela.cobranza.dto.response;

import escuela.cobranza.entity.EstadoRegistroCargo;
import escuela.cobranza.entity.SituacionCobro;
import escuela.common.dto.response.AuditoriaResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CargoResponse(
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
        Long cuotaAlumnoId,
        String claveGeneracion,
        String descripcion,
        LocalDate periodoCobroInicio,
        LocalDate periodoCobroFin,
        Long periodoAcademicoId,
        String periodoAcademicoNombre,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento,
        BigDecimal importeOriginal,
        String moneda,
        EstadoRegistroCargo estadoRegistro,
        Instant canceladoEn,
        String motivoCancelacion,
        BigDecimal descuentoTotal,
        BigDecimal recargoTotal,
        BigDecimal importeTotal,
        BigDecimal montoPagado,
        BigDecimal saldoPendiente,
        SituacionCobro situacionCobro,
        boolean vencido,
        AuditoriaResponse auditoria
) {
}
