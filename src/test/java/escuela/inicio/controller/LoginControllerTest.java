package escuela.inicio.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private final LoginController controller = new LoginController();

    @Test
    void muestraElAccesoDelPersonal() {
        assertThat(controller.login()).isEqualTo("login");
    }

    @Test
    void muestraElAccesoDeLasFamilias() {
        assertThat(controller.familias()).isEqualTo("login-familias");
    }
}
