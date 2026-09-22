package escuela.finanzas.mapper;

import escuela.finanzas.dto.response.*;
import escuela.finanzas.entity.*;
import escuela.tutor.entity.Tutor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static escuela.common.mapper.AuditoriaMapper.desde;

@Component
public class PagoMapper {
    public PagoResponse respuesta(Pago pago) {
        BigDecimal solicitado = pago.getSolicitudes().stream()
                .map(SolicitudAplicacionPago::getMontoSolicitado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal aplicado = pago.getAplicaciones().stream()
                .map(a -> a.getOperacion() == OperacionAplicacionPago.APLICAR
                        ? a.getMonto() : a.getMonto().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal devuelto = pago.getDevoluciones().stream()
                .filter(d -> d.getEstado() == EstadoDevolucionPago.EJECUTADA)
                .map(DevolucionPago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PagoResponse(pago.getId(), pago.getInstitucion().getId(), pago.getInstitucion().getNombre(),
                pago.getPlantelRegistro().getId(), pago.getPlantelRegistro().getNombre(),
                pago.getTutor().getId(), nombreTutor(pago.getTutor()), pago.getNombrePagador(),
                pago.getFolio(), pago.getFechaPago(), pago.getMonto(), pago.getMoneda(),
                pago.getMetodo(), pago.getEstado(),
                pago.getOrigenRegistro(), pago.getReportadoPor() == null ? null : pago.getReportadoPor().getUsername(),
                pago.getCuentaDeclarada() == null ? null : pago.getCuentaDeclarada().getId(),
                pago.getCuentaDeclarada() == null ? null : pago.getCuentaDeclarada().getNombre(),
                pago.getCuentaDestino() == null ? null : pago.getCuentaDestino().getId(),
                pago.getCuentaDestino() == null ? null : pago.getCuentaDestino().getNombre(),
                pago.getReferencia(), pago.getObservaciones(), pago.getValidadoEn(),
                pago.getValidadoPor() == null ? null : pago.getValidadoPor().getUsername(),
                pago.getMotivoRechazoCancelacion(),
                pago.getComprobantes().stream().map(c -> new ComprobantePagoResponse(c.getId(),
                        c.getArchivo().getNombreOriginal(), c.getArchivo().getTipoMime(),
                        c.getArchivo().getTamanoBytes(), c.getCreadoEn())).toList(),
                pago.getSolicitudes().stream().map(this::solicitud).toList(),
                pago.getAplicaciones().stream()
                        .filter(a -> a.getOperacion() == OperacionAplicacionPago.APLICAR && a.getReversa() == null)
                        .map(this::aplicacion).toList(), solicitado, pago.getMonto().subtract(solicitado), aplicado,
                devuelto, pago.getEstado() == EstadoPago.VALIDADO
                        ? pago.getMonto().subtract(aplicado).subtract(devuelto) : BigDecimal.ZERO,
                movimiento(pago.getMovimiento(), pago.getMoneda()),
                desde(pago));
    }

    private AplicacionPagoResponse aplicacion(AplicacionPago aplicacion) {
        var cargo = aplicacion.getCargo();
        var alumno = cargo.getInscripcion().getAlumno();
        return new AplicacionPagoResponse(aplicacion.getId(), cargo.getId(),
                nombreAlumno(alumno.getNombres(), alumno.getPrimerApellido(), alumno.getSegundoApellido()),
                alumno.getMatricula(), cargo.getConceptoCobro().getNombre(), cargo.getDescripcion(),
                aplicacion.getMonto(), cargo.getMoneda(), aplicacion.getFechaAplicacion());
    }

    private MovimientoFinancieroResponse movimiento(MovimientoFinanciero movimiento, String moneda) {
        if (movimiento == null) return null;
        return new MovimientoFinancieroResponse(movimiento.getId(), movimiento.getCuenta().getId(),
                movimiento.getCuenta().getNombre(), movimiento.getFechaOperacion(),
                movimiento.getSecuenciaCuenta(), movimiento.getMonto(), moneda,
                movimiento.getSaldoAnterior(), movimiento.getSaldoPosterior());
    }

    private String nombreAlumno(String... partes) {
        return Stream.of(partes).filter(v -> v != null && !v.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }

    private SolicitudAplicacionPagoResponse solicitud(SolicitudAplicacionPago solicitud) {
        var cargo = solicitud.getCargo();
        var alumno = cargo.getInscripcion().getAlumno();
        return new SolicitudAplicacionPagoResponse(solicitud.getId(), cargo.getId(),
                Stream.of(alumno.getNombres(), alumno.getPrimerApellido(), alumno.getSegundoApellido())
                        .filter(v -> v != null && !v.isBlank()).collect(java.util.stream.Collectors.joining(" ")),
                alumno.getMatricula(), cargo.getConceptoCobro().getNombre(), cargo.getDescripcion(),
                solicitud.getMontoSolicitado(), cargo.getMoneda());
    }

    private String nombreTutor(Tutor tutor) {
        return Stream.of(tutor.getNombres(), tutor.getPrimerApellido(), tutor.getSegundoApellido())
                .filter(v -> v != null && !v.isBlank()).collect(java.util.stream.Collectors.joining(" "));
    }
}
