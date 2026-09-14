package escuela.cobranza.mapper;

import escuela.academico.entity.PeriodoAcademico;
import escuela.cobranza.dto.request.CargoManualRequest;
import escuela.cobranza.dto.response.CargoResponse;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.EstadoRegistroCargo;
import escuela.cobranza.entity.SituacionCobro;
import escuela.inscripcion.entity.Inscripcion;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class CargoMapper {

    public Cargo nuevoManual(CargoManualRequest request, Inscripcion inscripcion,
                             ConceptoCobro concepto, PeriodoAcademico periodo,
                             String claveGeneracion) {
        Cargo cargo = new Cargo();
        cargo.setInscripcion(inscripcion);
        cargo.setConceptoCobro(concepto);
        cargo.setClaveGeneracion(claveGeneracion);
        cargo.setDescripcion(limpiar(request.descripcion()));
        cargo.setPeriodoCobroInicio(request.periodoCobroInicio());
        cargo.setPeriodoCobroFin(request.periodoCobroFin());
        cargo.setPeriodoAcademico(periodo);
        cargo.setFechaEmision(request.fechaEmision());
        cargo.setFechaVencimiento(request.fechaVencimiento());
        cargo.setImporteOriginal(request.importeOriginal());
        cargo.setMoneda(codigo(request.moneda()));
        cargo.setEstadoRegistro(EstadoRegistroCargo.EMITIDO);
        return cargo;
    }

    public CargoResponse respuesta(Cargo cargo) {
        var inscripcion = cargo.getInscripcion();
        var alumno = inscripcion.getAlumno();
        boolean cancelado = cargo.getEstadoRegistro() == EstadoRegistroCargo.CANCELADO;
        BigDecimal cero = BigDecimal.ZERO.setScale(2);
        BigDecimal total = cargo.getImporteOriginal();
        BigDecimal saldo = cancelado ? cero : total;
        SituacionCobro situacion = cancelado ? SituacionCobro.CANCELADO
                : total.signum() == 0 ? SituacionCobro.PAGADO : SituacionCobro.PENDIENTE;
        boolean vencido = !cancelado && saldo.signum() > 0
                && cargo.getFechaVencimiento().isBefore(LocalDate.now());
        return new CargoResponse(cargo.getId(), inscripcion.getId(),
                alumno.getInstitucion().getId(), inscripcion.getPlantel().getId(),
                inscripcion.getPlantel().getNombre(), alumno.getId(), alumno.getMatricula(),
                nombre(alumno.getNombres(), alumno.getPrimerApellido(), alumno.getSegundoApellido()),
                inscripcion.getNumeroInscripcion(), cargo.getConceptoCobro().getId(),
                cargo.getConceptoCobro().getCodigo(), cargo.getConceptoCobro().getNombre(),
                cargo.getCuotaAlumno() == null ? null : cargo.getCuotaAlumno().getId(),
                cargo.getClaveGeneracion(), cargo.getDescripcion(), cargo.getPeriodoCobroInicio(),
                cargo.getPeriodoCobroFin(), cargo.getPeriodoAcademico() == null ? null
                        : cargo.getPeriodoAcademico().getId(),
                cargo.getPeriodoAcademico() == null ? null : cargo.getPeriodoAcademico().getNombre(),
                cargo.getFechaEmision(), cargo.getFechaVencimiento(), cargo.getImporteOriginal(),
                cargo.getMoneda(), cargo.getEstadoRegistro(), cargo.getCanceladoEn(),
                cargo.getMotivoCancelacion(), cero, cero, total, cero, saldo, situacion,
                vencido, desde(cargo));
    }

    private String nombre(String... partes) {
        return Stream.of(partes).filter(valor -> valor != null && !valor.isBlank())
                .collect(Collectors.joining(" "));
    }
}
