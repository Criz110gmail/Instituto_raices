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
        escuela.finanzas.entity.OrigenRegistroPago origenRegistro, String reportadoPor,
        Long cuentaDeclaradaId, String cuentaDeclaradaNombre,
        Long cuentaDestinoId, String cuentaDestinoNombre,
        String referencia, String observaciones, Instant validadoEn,
        String validadoPor, String motivoRechazoCancelacion,
        List<ComprobantePagoResponse> comprobantes,
        List<SolicitudAplicacionPagoResponse> solicitudes,
        List<AplicacionPagoResponse> aplicaciones,
        BigDecimal montoSolicitado, BigDecimal montoSinAsignar,
        BigDecimal montoAplicado, BigDecimal montoDevuelto, BigDecimal montoDisponible,
        MovimientoFinancieroResponse movimiento,
        AuditoriaResponse auditoria
) {
}
