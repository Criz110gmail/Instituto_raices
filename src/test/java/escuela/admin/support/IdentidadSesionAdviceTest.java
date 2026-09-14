package escuela.admin.support;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;

class IdentidadSesionAdviceTest {

    private final IdentidadSesionAdvice advice = new IdentidadSesionAdvice();

    @Test
    void exponeElNombreDelUsuarioAutenticado() {
        var autenticacion = UsernamePasswordAuthenticationToken.authenticated(
                "admin.raices", "oculta", java.util.List.of());

        assertThat(advice.usuarioSesion(autenticacion)).isEqualTo("admin.raices");
    }

    @Test
    void toleraSolicitudesSinAutenticacion() {
        assertThat(advice.usuarioSesion(null)).isEmpty();
    }
}
