package escuela.cobranza.support;

import escuela.cobranza.entity.*;
import escuela.finanzas.entity.*;
import escuela.admin.dto.ModuloCatalogo;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SituacionAdeudoTest {
    private final LocalDate hoy = LocalDate.of(2026, 10, 8);

    @Test void pendientesParcialesYVencidosNoSeConfunden() {
        var c = cargo();
        assertThat(etiqueta(c)).isEqualTo("Pendiente sin abonos");
        abono(c, "300", OperacionAplicacionPago.APLICAR);
        assertThat(etiqueta(c)).isEqualTo("Pago parcial");
        c.setFechaVencimiento(hoy.minusDays(1));
        assertThat(etiqueta(c)).isEqualTo("Parcial · Vencido");
        abono(c, "300", OperacionAplicacionPago.REVERTIR);
        assertThat(etiqueta(c)).isEqualTo("Vencido");
    }

    @Test void liquidadoPorPagosOAjustesYDevolucionReabreSaldo() {
        var c = cargo();
        ajuste(c, "200", EfectoAjusteCargo.DISMINUCION);
        ajuste(c, "80", EfectoAjusteCargo.AUMENTO);
        abono(c, "880", OperacionAplicacionPago.APLICAR);
        assertThat(etiqueta(c)).isEqualTo("Liquidado");
        abono(c, "100", OperacionAplicacionPago.REVERTIR);
        assertThat(etiqueta(c)).isEqualTo("Pago parcial");
        ajuste(c, "100", EfectoAjusteCargo.DISMINUCION);
        assertThat(etiqueta(c)).isEqualTo("Liquidado");
        var descuentoTotal = cargo();
        ajuste(descuentoTotal, "1000", EfectoAjusteCargo.DISMINUCION);
        assertThat(etiqueta(descuentoTotal)).isEqualTo("Liquidado");
    }

    @Test void canceladoYConvenidoTienenPrioridadSobreSaldoYFecha() {
        var c = cargo();
        c.setFechaVencimiento(hoy.minusDays(1));
        c.setEstadoRegistro(EstadoRegistroCargo.CONVENIDO);
        assertThat(etiqueta(c)).isEqualTo("Incluido en convenio");
        c.setEstadoRegistro(EstadoRegistroCargo.CANCELADO);
        assertThat(etiqueta(c)).isEqualTo("Cancelado");
    }

    @Test void opcionesClarasConservanTodosSinOfrecerEmitido() {
        assertThat(ModuloCatalogo.CARGOS.estadosFiltro()).extracting(java.util.Map.Entry::getKey)
                .containsExactly("TODOS", "CON_SALDO", "PENDIENTE", "PARCIAL", "VENCIDO", "LIQUIDADO", "CONVENIDO", "CANCELADO");
    }

    private String etiqueta(Cargo c) { return SituacionAdeudo.etiqueta(c, hoy); }
    private Cargo cargo() {
        var c = new Cargo(); c.setImporteOriginal(new BigDecimal("1000"));
        c.setFechaVencimiento(hoy); return c;
    }
    private void abono(Cargo c, String monto, OperacionAplicacionPago op) {
        var a = new AplicacionPago(); a.setMonto(new BigDecimal(monto)); a.setOperacion(op);
        c.getAplicaciones().add(a);
    }
    private void ajuste(Cargo c, String monto, EfectoAjusteCargo efecto) {
        var a = new AjusteCargo(); a.setMonto(new BigDecimal(monto)); a.setEfecto(efecto);
        c.getAjustes().add(a);
    }
}
