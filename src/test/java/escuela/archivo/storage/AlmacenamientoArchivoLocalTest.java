package escuela.archivo.storage;

import escuela.common.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlmacenamientoArchivoLocalTest {

    @TempDir Path temporal;

    @Test
    void guardaYAbreDentroDeLaRaizPrivada() throws Exception {
        AlmacenamientoArchivoLocal almacenamiento = new AlmacenamientoArchivoLocal(temporal.toString());

        almacenamiento.guardar("1/alumnos/10/foto.bin",
                new ByteArrayInputStream("privado".getBytes(StandardCharsets.UTF_8)));

        assertThat(almacenamiento.abrir("1/alumnos/10/foto.bin").getContentAsByteArray())
                .isEqualTo("privado".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void bloqueaIntentosDeSalirDeLaRaiz() {
        AlmacenamientoArchivoLocal almacenamiento = new AlmacenamientoArchivoLocal(temporal.toString());

        assertThatThrownBy(() -> almacenamiento.abrir("../../fuera.bin"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no es válida");
    }
}
