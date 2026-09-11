package escuela.inicio.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InicioControllerTest {

    private final InicioController controller = new InicioController();

    @Test
    void muestraLaPaginaInicial() {
        assertThat(controller.inicio()).isEqualTo("inicio");
    }

    @Test
    void redirigeElAliasDeSaludAlActuator() {
        assertThat(controller.salud()).isEqualTo("redirect:/actuator/health");
    }
}
