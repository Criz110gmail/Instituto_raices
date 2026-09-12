package escuela.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class SeguridadConfigTest {

    @Test
    void codificaLaPasswordDelAdministradorTemporalAntesDeAutenticar() {
        SeguridadConfig config = new SeguridadConfig();
        SecurityProperties properties = new SecurityProperties();
        properties.getUser().setName("administrador-prueba");
        properties.getUser().setPassword("password-temporal-prueba");
        properties.getUser().setRoles(java.util.List.of("USER"));
        PasswordEncoder encoder = config.passwordEncoder();

        var servicio = config.usuarioTemporal(properties, encoder);
        var usuario = servicio.loadUserByUsername("administrador-prueba");

        assertThat(usuario.getPassword()).startsWith("$2");
        assertThat(encoder.matches("password-temporal-prueba", usuario.getPassword())).isTrue();
    }
}
