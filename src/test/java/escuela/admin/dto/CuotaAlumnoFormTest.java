package escuela.admin.dto;

import escuela.cobranza.entity.FrecuenciaCuota;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CuotaAlumnoFormTest {

    @Test
    void cuotaUnicaDescartaElDiaMensualPredeterminado() {
        CuotaAlumnoForm form = new CuotaAlumnoForm();
        form.setFrecuencia(FrecuenciaCuota.UNICA);
        form.setDiaVencimiento(10);
        form.setFechaVencimientoUnico(LocalDate.of(2026, 9, 30));

        var request = form.request();

        assertThat(request.diaVencimiento()).isNull();
        assertThat(request.fechaVencimientoUnico()).isEqualTo(LocalDate.of(2026, 9, 30));
    }

    @Test
    void cuotaMensualDescartaUnaFechaUnicaAnterior() {
        CuotaAlumnoForm form = new CuotaAlumnoForm();
        form.setFrecuencia(FrecuenciaCuota.MENSUAL);
        form.setDiaVencimiento(10);
        form.setFechaVencimientoUnico(LocalDate.of(2026, 9, 30));

        var request = form.request();

        assertThat(request.diaVencimiento()).isEqualTo(10);
        assertThat(request.fechaVencimientoUnico()).isNull();
    }
}
