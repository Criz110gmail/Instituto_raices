package escuela.admin.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FiltroBoletaTest {

    @Test
    void conservaElAlumnoSeleccionadoAlNormalizarYPaginar() {
        FiltroBoleta filtro = new FiltroBoleta(1L, 2L, null, null, "",
                "ALU-001 · Ana Pérez · Activa", 25L, -1, 25);

        FiltroBoleta normalizado = filtro.normalizado();
        FiltroBoleta siguiente = normalizado.conPagina(1, 25);

        assertThat(normalizado.alumnoId()).isEqualTo(25L);
        assertThat(normalizado.pagina()).isZero();
        assertThat(siguiente.alumnoId()).isEqualTo(25L);
        assertThat(siguiente.q()).isEqualTo("ALU-001 · Ana Pérez · Activa");
    }
}
