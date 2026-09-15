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
        return new PagoResponse(pago.getId(), pago.getInstitucion().getId(), pago.getInstitucion().getNombre(),
                pago.getPlantelRegistro().getId(), pago.getPlantelRegistro().getNombre(),
                pago.getTutor().getId(), nombreTutor(pago.getTutor()), pago.getNombrePagador(),
                pago.getFolio(), pago.getFechaPago(), pago.getMonto(), pago.getMoneda(),
                pago.getMetodo(), pago.getEstado(),
                pago.getCuentaDeclarada() == null ? null : pago.getCuentaDeclarada().getId(),
                pago.getCuentaDeclarada() == null ? null : pago.getCuentaDeclarada().getNombre(),
                pago.getReferencia(), pago.getObservaciones(),
                pago.getComprobantes().stream().map(c -> new ComprobantePagoResponse(c.getId(),
                        c.getArchivo().getNombreOriginal(), c.getArchivo().getTipoMime(),
                        c.getArchivo().getTamanoBytes(), c.getCreadoEn())).toList(),
                pago.getSolicitudes().stream().map(this::solicitud).toList(), solicitado,
                pago.getMonto().subtract(solicitado), desde(pago));
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
