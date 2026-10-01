package escuela.archivo.imagen;

import escuela.common.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProcesadorFotografiaTest {
    private final ProcesadorFotografia procesador = new ProcesadorFotografia("/comando-inexistente");

    @Test
    void reconoceFirmasPermitidasSinConfiarEnLaExtension() {
        assertThat(procesador.detectarFormato(new byte[]{(byte) 0xff, (byte) 0xd8, (byte) 0xff}))
                .isEqualTo("jpg");
        assertThat(procesador.detectarFormato(new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47,
                0x0d, 0x0a, 0x1a, 0x0a})).isEqualTo("png");
        byte[] heic = new byte[24];
        System.arraycopy("ftyp".getBytes(StandardCharsets.US_ASCII), 0, heic, 4, 4);
        System.arraycopy("heic".getBytes(StandardCharsets.US_ASCII), 0, heic, 8, 4);
        assertThat(procesador.detectarFormato(heic)).isEqualTo("heic");
    }

    @Test
    void rechazaContenidoDisfrazadoDeImagen() {
        assertThatThrownBy(() -> procesador.detectarFormato("no es una foto".getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("HEIC");
    }

    @Test
    void rechazaEntradaMayorA20MbAntesDeEjecutarElConversor() {
        byte[] grande = new byte[(int) ProcesadorFotografia.TAMANO_MAXIMO_ENTRADA + 1];
        var archivo = new MockMultipartFile("archivo", "grande.heic", "image/heic", grande);
        assertThatThrownBy(() -> procesador.procesar(archivo))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("20 MB");
    }
}
