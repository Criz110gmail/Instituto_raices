package escuela.admin.dto;

import escuela.common.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class FiltroEstadoCuentaCuentaTest {
    @Test
    void calculaCorteMensualBisiestoYAnual() {
        var mensual = new FiltroEstadoCuentaCuenta(1L, 2L, " Caja ", "MENSUAL", 2024, 2, -1, 900)
                .normalizado(LocalDate.of(2026, 9, 21));
        assertThat(mensual.desde()).isEqualTo(LocalDate.of(2024, 2, 1));
        assertThat(mensual.hasta()).isEqualTo(LocalDate.of(2024, 2, 29));
        assertThat(mensual.pagina()).isZero();
        assertThat(mensual.tamanio()).isEqualTo(25);
        var anual = new FiltroEstadoCuentaCuenta(1L, 2L, "", "ANUAL", 2026, 99, 0, 10)
                .normalizado(LocalDate.of(2026, 9, 21));
        assertThat(anual.desde()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(anual.hasta()).isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(anual.mes()).isNull();
    }

    @Test
    void rechazaMesYAnoInvalidos() {
        assertThatThrownBy(() -> new FiltroEstadoCuentaCuenta(1L, 2L, "", "MENSUAL", 2026, 13, 0, 25)
                .normalizado(LocalDate.of(2026, 9, 21))).isInstanceOf(ReglaNegocioException.class);
        assertThatThrownBy(() -> new FiltroEstadoCuentaCuenta(1L, 2L, "", "ANUAL", 2030, null, 0, 25)
                .normalizado(LocalDate.of(2026, 9, 21))).isInstanceOf(ReglaNegocioException.class);
    }
}
