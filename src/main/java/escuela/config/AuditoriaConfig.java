package escuela.config;

import escuela.seguridad.service.UsuarioPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditoriaConfig {

    @Bean
    AuditorAware<Long> auditorActual() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(autenticacion -> autenticacion.isAuthenticated())
                .map(autenticacion -> autenticacion.getPrincipal())
                .filter(UsuarioPrincipal.class::isInstance)
                .map(UsuarioPrincipal.class::cast)
                .filter(principal -> !principal.accesoRecuperacion())
                .map(UsuarioPrincipal::usuarioId);
    }
}
