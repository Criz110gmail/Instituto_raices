package escuela.finanzas.dto.response;

import escuela.common.dto.response.AuditoriaResponse;
import escuela.finanzas.entity.EstadoPago;
import escuela.finanzas.entity.MetodoPago;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PagoResponse(
        Long id, Long institucionId, String institucionNombre,
        Long plantelRegistroId, String plantelRegistroNombre,
        Long tutorId, String tutorNombre, String nombrePagador,
        String folio, Instant fechaPago, BigDecimal monto, String moneda,
        MetodoPago metodo, EstadoPago estado,
        Long cuentaDeclaradaId, String cuentaDeclaradaNombre,
        String referencia, String observaciones,
        List<ComprobantePagoResponse> comprobantes,
        List<SolicitudAplicacionPagoResponse> solicitudes,
        BigDecimal montoSolicitado, BigDecimal montoSinAsignar,
        AuditoriaResponse auditoria
) {
}
