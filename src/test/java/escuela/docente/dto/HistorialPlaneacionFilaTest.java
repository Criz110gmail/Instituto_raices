package escuela.docente.dto;

import escuela.docente.entity.EstadoPlaneacion;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class HistorialPlaneacionFilaTest {

    @Test
    void conservaElNombreUsadoPorLaVistaAdministrativa() {
        var fila = new HistorialPlaneacionFila(
                EstadoPlaneacion.ENVIADA,
                EstadoPlaneacion.EN_REVISION,
                null,
                "Administración",
                Instant.parse("2026-10-05T12:00:00Z"));

        assertThat(fila.getEstadoNuevo()).isEqualTo(EstadoPlaneacion.EN_REVISION);
        assertThat(fila.getEstadoNuevo().getEtiqueta()).isEqualTo("En revisión");
    }
}
