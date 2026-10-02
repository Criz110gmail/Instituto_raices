package escuela.admin.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

class CargoFormTest {

    @Test
    void mesCompletoCalculaPrimerYUltimoDia() {
        CargoForm form = new CargoForm();
        form.setModoPeriodo(ModoPeriodoCargo.MES_COMPLETO);
        form.setMesPeriodo(YearMonth.of(2026, 2));

        var request = form.request();

        assertThat(request.periodoCobroInicio()).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(request.periodoCobroFin()).isEqualTo(LocalDate.of(2026, 2, 28));
    }

    @Test
    void fechaEspecificaUsaElMismoDiaComoInicioYFin() {
        CargoForm form = new CargoForm();
        form.setModoPeriodo(ModoPeriodoCargo.FECHA_ESPECIFICA);
        form.setFechaEspecifica(LocalDate.of(2026, 9, 14));

        var request = form.request();

        assertThat(request.periodoCobroInicio()).isEqualTo(LocalDate.of(2026, 9, 14));
        assertThat(request.periodoCobroFin()).isEqualTo(LocalDate.of(2026, 9, 14));
    }

    @Test
    void rangoPersonalizadoConservaLasFechasCapturadas() {
        CargoForm form = new CargoForm();
        form.setModoPeriodo(ModoPeriodoCargo.RANGO_PERSONALIZADO);
        form.setPeriodoCobroInicio(LocalDate.of(2026, 9, 14));
        form.setPeriodoCobroFin(LocalDate.of(2026, 9, 30));

        var request = form.request();

        assertThat(request.periodoCobroInicio()).isEqualTo(LocalDate.of(2026, 9, 14));
        assertThat(request.periodoCobroFin()).isEqualTo(LocalDate.of(2026, 9, 30));
    }

    @Test
    void fechaHistoricaConservaElMotivoDeTrazabilidad() {
        CargoForm form = new CargoForm();
        form.setModificarFechaRegistro(true);
        form.setMotivoFechaRegistroDiferente("Captura del recibo físico anterior");

        assertThat(form.request().motivoFechaRegistroDiferente())
                .isEqualTo("Captura del recibo físico anterior");
    }
}
