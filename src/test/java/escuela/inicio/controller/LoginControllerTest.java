package escuela.inicio.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private final LoginController controller = new LoginController();

    @Test
    void muestraElAccesoDelPersonal() {
        assertThat(controller.login(null)).isEqualTo("login");
    }

    @Test
    void muestraElAccesoDeLasFamilias() {
        assertThat(controller.familias()).isEqualTo("login-familias");
    }

    @Test
    void muestraElAccesoDeLosMaestros() {
        assertThat(controller.maestros()).isEqualTo("login-maestros");
    }

    @Test
    void conservaElDisenoDelPortalCuandoLaSesionCaduca() {
        assertThat(controller.login("maestros")).isEqualTo("login-maestros");
        assertThat(controller.login("familias")).isEqualTo("login-familias");
    }
}
