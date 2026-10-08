package escuela.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class SeguridadConfigTest {

    @Test
    void configuraBCryptParaLasCredencialesDelSistema() {
        SeguridadConfig config = new SeguridadConfig();
        PasswordEncoder encoder = config.passwordEncoder();
        String passwordCodificada = encoder.encode("password-prueba");

        assertThat(passwordCodificada).startsWith("$2");
        assertThat(encoder.matches("password-prueba", passwordCodificada)).isTrue();
    }

    @Test
    void dirigeCadaCierreDeSesionAlLoginCorrespondiente() {
        assertThat(SeguridadConfig.rutaDespuesDeCerrarSesion("familias"))
                .isEqualTo("/familias?logout");
        assertThat(SeguridadConfig.rutaDespuesDeCerrarSesion("maestros"))
                .isEqualTo("/maestros/acceso?logout");
        assertThat(SeguridadConfig.rutaDespuesDeCerrarSesion(null))
                .isEqualTo("/login?logout");
    }
}
