package escuela.config;

import escuela.config.audit.ActorAuditoriaEntityListener;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuditoriaConfigTest {

    private final AuditoriaConfig config = new AuditoriaConfig();

    @AfterEach
    void limpiarSesion() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void obtieneElIdDelUsuarioPersistidoAutenticado() {
        UsuarioPrincipal principal = new UsuarioPrincipal(27L, 3L, Set.of(), true,
                false, "operador", "hash", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        assertThat(config.auditorActual().getCurrentAuditor()).contains(27L);
    }

    @Test
    void noAtribuyeCambiosAlAccesoDeRecuperacion() {
        UsuarioPrincipal principal = new UsuarioPrincipal(null, null, Set.of(), true,
                true, "recuperacion", "hash", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        assertThat(config.auditorActual().getCurrentAuditor()).isEmpty();
    }

    @Test
    void ignoraSesionesAnonimas() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "prueba", "anonimo", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        assertThat(config.auditorActual().getCurrentAuditor()).isEmpty();
    }

    @Test
    void limpiaActorAnteriorCuandoActualizaElAccesoDeRecuperacion() {
        UsuarioPrincipal principal = new UsuarioPrincipal(null, null, Set.of(), true,
                true, "recuperacion", "hash", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
        Institucion entidad = new Institucion();
        entidad.setActualizadoPorId(27L);

        new ActorAuditoriaEntityListener().antesDeActualizar(entidad);

        assertThat(entidad.getActualizadoPorId()).isNull();
    }
}
