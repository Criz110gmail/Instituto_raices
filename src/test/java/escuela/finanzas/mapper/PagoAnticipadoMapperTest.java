package escuela.finanzas.mapper;

import escuela.alumno.entity.Alumno;
import escuela.cobranza.entity.*;
import escuela.finanzas.entity.*;
import escuela.inscripcion.entity.Inscripcion;
import escuela.institucion.entity.*;
import escuela.tutor.entity.Tutor;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PagoAnticipadoMapperTest {
    private final PagoMapper mapper = new PagoMapper();

    @Test void conservarBecaIncluyeBeneficioAntesDelAbono() {
        Pago pago = pago("CONSERVAR", "720.00");
        var fila = mapper.respuesta(pago).solicitudes().getFirst();
        assertThat(fila.acuerdoAnticipado()).isTrue();
        assertThat(fila.saldoCargoActual()).isEqualByComparingTo("800.00");
        assertThat(fila.beneficioAnticipado()).isEqualByComparingTo("80.00");
        assertThat(fila.becaSustituida()).isEqualByComparingTo("0.00");
        assertThat(fila.saldoEstimadoTrasValidacion()).isEqualByComparingTo("0.00");
        assertThat(pago.getAcuerdoAnticipado().getCargos().getFirst().getCargo().getAjustes()).hasSize(1);
        assertThat(pago.getAplicaciones()).isEmpty();
    }

    @Test void sustituirBecaIncluyeCompensacionYBeneficio() {
        var fila = mapper.respuesta(pago("SUSTITUIR", "920.00")).solicitudes().getFirst();
        assertThat(fila.becaSustituida()).isEqualByComparingTo("200.00");
        assertThat(fila.saldoEstimadoTrasValidacion()).isEqualByComparingTo("0.00");
    }

    @Test void pagoOrdinarioConservaRestanteSinBeneficio() {
        Pago pago = pago("CONSERVAR", "720.00");
        pago.setAcuerdoAnticipadoId(null);
        var fila = mapper.respuesta(pago).solicitudes().getFirst();
        assertThat(fila.acuerdoAnticipado()).isFalse();
        assertThat(fila.beneficioAnticipado()).isEqualByComparingTo("0.00");
        assertThat(fila.saldoEstimadoTrasValidacion()).isEqualByComparingTo("80.00");
    }

    @Test void noProyectaBeneficiosDeAcuerdosCanceladosNiPagosResueltos() {
        Pago pago = pago("CONSERVAR", "720.00");
        pago.getAcuerdoAnticipado().setEstado("CANCELADO");
        assertThat(mapper.respuesta(pago).solicitudes().getFirst().acuerdoAnticipado()).isFalse();
        pago.getAcuerdoAnticipado().setEstado("PROPUESTO");
        pago.setEstado(EstadoPago.VALIDADO);
        assertThat(mapper.respuesta(pago).solicitudes().getFirst().acuerdoAnticipado()).isFalse();
    }

    private Pago pago(String politica, String monto) {
        var institucion = new Institucion(); institucion.setId(1L);
        var plantel = new Plantel(); plantel.setId(2L);
        var tutor = new Tutor(); tutor.setId(3L);
        var alumno = new Alumno(); alumno.setMatricula("ALUM-TEST"); alumno.setNombres("Martha");
        var inscripcion = new Inscripcion(); inscripcion.setAlumno(alumno);
        var concepto = new ConceptoCobro(); concepto.setNombre("Colegiatura");
        var cargo = new Cargo(); cargo.setId(4L); cargo.setInscripcion(inscripcion);
        cargo.setConceptoCobro(concepto); cargo.setImporteOriginal(new BigDecimal("1000.00")); cargo.setMoneda("MXN");
        var beca = new AjusteCargo(); beca.setTipo(TipoAjusteCargo.BECA);
        beca.setEfecto(EfectoAjusteCargo.DISMINUCION); beca.setMonto(new BigDecimal("200.00")); cargo.getAjustes().add(beca);
        var acuerdo = new AcuerdoAnticipado(); acuerdo.setId(5L); acuerdo.setEstado("PROPUESTO"); acuerdo.setPoliticaBeca(politica);
        var detalle = new AcuerdoAnticipadoCargo(); detalle.setCargo(cargo); detalle.setAcuerdo(acuerdo);
        detalle.setBeca(new BigDecimal("200.00")); detalle.setBeneficio(new BigDecimal("80.00")); acuerdo.getCargos().add(detalle);
        var pago = new Pago(); pago.setInstitucion(institucion); pago.setPlantelRegistro(plantel); pago.setTutor(tutor);
        pago.setMonto(new BigDecimal(monto)); pago.setMoneda("MXN"); pago.setAcuerdoAnticipadoId(5L); pago.setAcuerdoAnticipado(acuerdo);
        var solicitud = new SolicitudAplicacionPago(); solicitud.setCargo(cargo); solicitud.setMontoSolicitado(pago.getMonto());
        pago.getSolicitudes().add(solicitud);
        return pago;
    }
}
