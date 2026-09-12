package escuela.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeguridadConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login", "/activar-cuenta", "/restablecer-password", "/acceso-denegado", "/salud", "/actuator/health", "/css/**", "/js/**", "/favicon.svg", "/error").permitAll()
                        .requestMatchers("/admin/instituciones/**").hasAuthority("INSTITUCION_ADMINISTRAR")
                        .requestMatchers("/admin/planteles/**").hasAuthority("PLANTEL_ADMINISTRAR")
                        .requestMatchers("/admin/niveles/**").hasAuthority("NIVEL_ADMINISTRAR")
                        .requestMatchers("/admin/oferta/**").hasAuthority("OFERTA_ADMINISTRAR")
                        .requestMatchers("/admin/grados/**").hasAuthority("GRADO_ADMINISTRAR")
                        .requestMatchers("/admin/ciclos/**").hasAuthority("CICLO_ADMINISTRAR")
                        .requestMatchers("/admin/periodos/**").hasAuthority("PERIODO_ADMINISTRAR")
                        .requestMatchers("/admin/grupos/**").hasAuthority("GRUPO_ADMINISTRAR")
                        .requestMatchers("/admin/alumnos/**").hasAuthority("ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/roles/**", "/admin/catalogos/roles/**").hasAuthority("ROL_ADMINISTRAR")
                        .requestMatchers("/admin/usuarios/**", "/admin/catalogos/usuarios/**").hasAuthority("USUARIO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/instituciones/**").hasAnyAuthority("INSTITUCION_LEER", "INSTITUCION_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/planteles/**").hasAnyAuthority("PLANTEL_LEER", "PLANTEL_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/niveles/**").hasAnyAuthority("NIVEL_LEER", "NIVEL_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/oferta/**").hasAnyAuthority("OFERTA_LEER", "OFERTA_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/grados/**").hasAnyAuthority("GRADO_LEER", "GRADO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/ciclos/**").hasAnyAuthority("CICLO_LEER", "CICLO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/periodos/**").hasAnyAuthority("PERIODO_LEER", "PERIODO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/grupos/**").hasAnyAuthority("GRUPO_LEER", "GRUPO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/alumnos/**").hasAnyAuthority("ALUMNO_LEER", "ALUMNO_ADMINISTRAR")
                        .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/admin", true).permitAll())
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(excepciones -> excepciones.accessDeniedPage("/acceso-denegado"))
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .build();
    }
}
