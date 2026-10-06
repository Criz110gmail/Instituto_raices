package escuela.admin.dto;

import escuela.cobranza.entity.FrecuenciaCuota;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CuotaAlumnoFormTest {

    @Test
    void formularioAceptaMesHtmlSinExigirUnDia() {
        var form = new CuotaAlumnoForm();
        var binder = new org.springframework.validation.DataBinder(form);
        binder.setConversionService(new org.springframework.format.support.DefaultFormattingConversionService());
        binder.bind(new org.springframework.beans.MutablePropertyValues(java.util.Map.of(
                "primerMes", "2026-10", "ultimoMes", "2026-12")));
        assertThat(binder.getBindingResult().hasErrors()).isFalse();
        assertThat(form.getPrimerMes()).isEqualTo(java.time.YearMonth.of(2026, 10));
        assertThat(form.getUltimoMes()).isEqualTo(java.time.YearMonth.of(2026, 12));
    }

    @Test
    void unicaSugiereVigenciaSinCambiarLaFechaLimite() {
        CuotaAlumnoForm form = new CuotaAlumnoForm();
        form.setFrecuencia(FrecuenciaCuota.UNICA);
        form.setFechaVencimientoUnico(LocalDate.of(2026, 10, 20));
        form.prepararCalendario(LocalDate.of(2026, 9, 14), LocalDate.of(2027, 6, 15));
        assertThat(form.getFechaInicio()).isEqualTo(LocalDate.of(2026, 9, 14));
        assertThat(form.getFechaFin()).isEqualTo(LocalDate.of(2027, 6, 15));
        assertThat(form.request().fechaVencimientoUnico()).isEqualTo(LocalDate.of(2026, 10, 20));
    }

    @Test
    void unicaConservaVigenciaPersonalizada() {
        CuotaAlumnoForm form = new CuotaAlumnoForm();
        form.setFrecuencia(FrecuenciaCuota.UNICA);
        form.setFechaInicio(LocalDate.of(2026, 10, 5));
        form.setFechaFin(LocalDate.of(2026, 10, 31));
        form.prepararCalendario(LocalDate.of(2026, 9, 14), LocalDate.of(2027, 6, 15));
        assertThat(form.getFechaInicio()).isEqualTo(LocalDate.of(2026, 10, 5));
        assertThat(form.getFechaFin()).isEqualTo(LocalDate.of(2026, 10, 31));
    }

    @Test
    void mensualDerivaMesesYRespetaLimitesParcialesDeInscripcion() {
        CuotaAlumnoForm form = new CuotaAlumnoForm();
        form.setPrimerMes(java.time.YearMonth.of(2026, 9));
        form.setUltimoMes(java.time.YearMonth.of(2027, 6));
        form.prepararCalendario(LocalDate.of(2026, 9, 14), LocalDate.of(2027, 6, 15));
        assertThat(form.getFechaInicio()).isEqualTo(LocalDate.of(2026, 9, 14));
        assertThat(form.getFechaFin()).isEqualTo(LocalDate.of(2027, 6, 15));
    }

    @Test
    void mensualConservaFechasHistoricasDelMismoMesAlEditar() {
        CuotaAlumnoForm form = new CuotaAlumnoForm();
        form.setFechaInicio(LocalDate.of(2026, 10, 5));
        form.setFechaFin(LocalDate.of(2026, 12, 20));
        form.setPrimerMes(java.time.YearMonth.of(2026, 10));
        form.setUltimoMes(java.time.YearMonth.of(2026, 12));
        form.prepararCalendario(LocalDate.of(2026, 9, 14), LocalDate.of(2027, 6, 15));
        assertThat(form.getFechaInicio()).isEqualTo(LocalDate.of(2026, 10, 5));
        assertThat(form.getFechaFin()).isEqualTo(LocalDate.of(2026, 12, 20));
    }

    @Test
    void mensualNoRecortaSilenciosamenteMesesFueraDeInscripcion() {
        CuotaAlumnoForm form = new CuotaAlumnoForm();
        form.setPrimerMes(java.time.YearMonth.of(2026, 8));
        form.setUltimoMes(java.time.YearMonth.of(2027, 7));
        form.prepararCalendario(LocalDate.of(2026, 9, 14), LocalDate.of(2027, 6, 15));
        assertThat(form.getFechaInicio()).isEqualTo(LocalDate.of(2026, 8, 1));
        assertThat(form.getFechaFin()).isEqualTo(LocalDate.of(2027, 7, 31));
    }

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
