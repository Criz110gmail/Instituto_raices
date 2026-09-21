package escuela.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
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
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            SesionExpiradaAccessDeniedHandler accesoDenegadoHandler)
            throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login", "/familias", "/activar-cuenta", "/restablecer-password", "/acceso-denegado", "/salud", "/actuator/health", "/css/**", "/js/**", "/favicon.svg", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/portal/**")
                        .hasAuthority("PORTAL_TUTOR_ACCEDER")
                        .requestMatchers(HttpMethod.POST, "/portal/notificaciones/**")
                        .hasAuthority("PORTAL_TUTOR_ACCEDER")
                        .requestMatchers("/admin/instituciones/**").hasAuthority("INSTITUCION_ADMINISTRAR")
                        .requestMatchers("/admin/planteles/**").hasAuthority("PLANTEL_ADMINISTRAR")
                        .requestMatchers("/admin/niveles/**").hasAuthority("NIVEL_ADMINISTRAR")
                        .requestMatchers("/admin/oferta/**").hasAuthority("OFERTA_ADMINISTRAR")
                        .requestMatchers("/admin/grados/**").hasAuthority("GRADO_ADMINISTRAR")
                        .requestMatchers("/admin/ciclos/**").hasAuthority("CICLO_ADMINISTRAR")
                        .requestMatchers("/admin/periodos/**").hasAuthority("PERIODO_ADMINISTRAR")
                        .requestMatchers("/admin/grupos/**").hasAuthority("GRUPO_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/alumnos/*/fotografias/*")
                        .hasAnyAuthority("ALUMNO_LEER", "ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/alumnos/**").hasAuthority("ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/tutores/**").hasAuthority("TUTOR_ADMINISTRAR")
                        .requestMatchers("/admin/vinculos-tutor/**").hasAuthority("VINCULO_TUTOR_ADMINISTRAR")
                        .requestMatchers("/admin/inscripciones/**").hasAuthority("INSCRIPCION_ADMINISTRAR")
                        .requestMatchers("/admin/conceptos-cobro/**").hasAuthority("CONCEPTO_COBRO_ADMINISTRAR")
                        .requestMatchers("/admin/cuotas-alumno/**").hasAuthority("CUOTA_ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/cargos/**").hasAuthority("CARGO_ADMINISTRAR")
                        .requestMatchers("/admin/tipos-beca/**").hasAuthority("TIPO_BECA_ADMINISTRAR")
                        .requestMatchers("/admin/becas-alumno/**").hasAuthority("BECA_ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/ajustes-cargo/**").hasAuthority("AJUSTE_CARGO_ADMINISTRAR")
                        .requestMatchers("/admin/politicas-recargo/**").hasAuthority("POLITICA_RECARGO_ADMINISTRAR")
                        .requestMatchers("/admin/motivos-financieros/**").hasAuthority("MOTIVO_FINANCIERO_ADMINISTRAR")
                        .requestMatchers("/admin/cuentas-financieras/**").hasAuthority("CUENTA_FINANCIERA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/pagos/*/editar", "/admin/pagos/*/comprobantes/*")
                        .hasAnyAuthority("PAGO_LEER", "PAGO_REGISTRAR", "PAGO_VALIDAR", "PAGO_DEVOLVER", "PAGO_CANCELAR")
                        .requestMatchers(HttpMethod.POST, "/admin/pagos/*/validar", "/admin/pagos/*/rechazar")
                        .hasAuthority("PAGO_VALIDAR")
                        .requestMatchers(HttpMethod.POST, "/admin/pagos/*/devolver")
                        .hasAuthority("PAGO_DEVOLVER")
                        .requestMatchers(HttpMethod.POST, "/admin/pagos/*/cancelar")
                        .hasAuthority("PAGO_CANCELAR")
                        .requestMatchers("/admin/pagos/**").hasAuthority("PAGO_REGISTRAR")
                        .requestMatchers("/admin/autocompletado/alumnos")
                        .hasAnyAuthority("VINCULO_TUTOR_ADMINISTRAR", "ALUMNO_ADMINISTRAR", "INSCRIPCION_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/tutores")
                        .hasAnyAuthority("VINCULO_TUTOR_ADMINISTRAR", "TUTOR_ADMINISTRAR", "PAGO_REGISTRAR")
                        .requestMatchers("/admin/autocompletado/cuentas-pago", "/admin/autocompletado/cargos-pago")
                        .hasAnyAuthority("PAGO_REGISTRAR", "PAGO_VALIDAR")
                        .requestMatchers("/admin/autocompletado/cuentas-movimiento")
                        .hasAnyAuthority("MOVIMIENTO_FINANCIERO_LEER", "MOVIMIENTO_FINANCIERO_REGISTRAR",
                                "TRANSFERENCIA_CUENTA_REGISTRAR", "PAGO_DEVOLVER")
                        .requestMatchers("/admin/autocompletado/cuentas-caja")
                        .hasAnyAuthority("CORTE_CAJA_LEER", "CORTE_CAJA_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/alumnos-reporte", "/admin/autocompletado/cuentas-reporte")
                        .hasAuthority("REPORTE_FINANCIERO_CONSULTAR")
                        .requestMatchers("/admin/autocompletado/destinatarios-evento")
                        .hasAuthority("EVENTO_ESCOLAR_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/usuarios")
                        .hasAnyAuthority("TUTOR_ADMINISTRAR", "USUARIO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/inscripciones", "/admin/autocompletado/conceptos-cobro")
                        .hasAnyAuthority("CUOTA_ALUMNO_ADMINISTRAR", "CARGO_ADMINISTRAR", "BECA_ALUMNO_ADMINISTRAR", "POLITICA_RECARGO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/tipos-beca")
                        .hasAuthority("BECA_ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/periodos-cargo")
                        .hasAuthority("CARGO_ADMINISTRAR")
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
                        .requestMatchers("/admin/catalogos/tutores/**").hasAnyAuthority("TUTOR_LEER", "TUTOR_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/vinculos-tutor/**").hasAnyAuthority("VINCULO_TUTOR_LEER", "VINCULO_TUTOR_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/inscripciones/**").hasAnyAuthority("INSCRIPCION_LEER", "INSCRIPCION_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/conceptos-cobro/**").hasAnyAuthority("CONCEPTO_COBRO_LEER", "CONCEPTO_COBRO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/cuotas-alumno/**").hasAnyAuthority("CUOTA_ALUMNO_LEER", "CUOTA_ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/cargos/**").hasAnyAuthority("CARGO_LEER", "CARGO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/tipos-beca/**").hasAnyAuthority("TIPO_BECA_LEER", "TIPO_BECA_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/becas-alumno/**").hasAnyAuthority("BECA_ALUMNO_LEER", "BECA_ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/ajustes-cargo/**").hasAnyAuthority("AJUSTE_CARGO_LEER", "AJUSTE_CARGO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/politicas-recargo/**").hasAnyAuthority("POLITICA_RECARGO_LEER", "POLITICA_RECARGO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/motivos-financieros/**").hasAnyAuthority("MOTIVO_FINANCIERO_LEER", "MOTIVO_FINANCIERO_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/cuentas-financieras/**").hasAnyAuthority("CUENTA_FINANCIERA_LEER", "CUENTA_FINANCIERA_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/pagos/**").hasAnyAuthority("PAGO_LEER", "PAGO_REGISTRAR", "PAGO_VALIDAR", "PAGO_DEVOLVER", "PAGO_CANCELAR")
                        .requestMatchers(HttpMethod.GET, "/admin/movimientos-financieros/nuevo")
                        .hasAuthority("MOVIMIENTO_FINANCIERO_REGISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/movimientos-financieros")
                        .hasAuthority("MOVIMIENTO_FINANCIERO_REGISTRAR")
                        .requestMatchers("/admin/transferencias/**")
                        .hasAuthority("TRANSFERENCIA_CUENTA_REGISTRAR")
                        .requestMatchers("/admin/reversiones/**")
                        .hasAuthority("MOVIMIENTO_FINANCIERO_REVERTIR")
                        .requestMatchers(HttpMethod.GET, "/admin/cortes-caja/nuevo")
                        .hasAuthority("CORTE_CAJA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/cortes-caja/**")
                        .hasAuthority("CORTE_CAJA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/cortes-caja/**")
                        .hasAnyAuthority("CORTE_CAJA_LEER", "CORTE_CAJA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/reportes-financieros/**")
                        .hasAuthority("REPORTE_FINANCIERO_CONSULTAR")
                        .requestMatchers(HttpMethod.GET, "/admin/eventos-escolares/nuevo",
                                "/admin/eventos-escolares/*/editar")
                        .hasAuthority("EVENTO_ESCOLAR_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/eventos-escolares/**")
                        .hasAuthority("EVENTO_ESCOLAR_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/eventos-escolares/**")
                        .hasAnyAuthority("EVENTO_ESCOLAR_LEER", "EVENTO_ESCOLAR_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/avisos/nuevo", "/admin/avisos/*/editar")
                        .hasAuthority("AVISO_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/avisos/**")
                        .hasAuthority("AVISO_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/avisos/**")
                        .hasAnyAuthority("AVISO_LEER", "AVISO_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/auditoria/**")
                        .hasAuthority("AUDITORIA_CONSULTAR")
                        .requestMatchers(HttpMethod.GET, "/admin/movimientos-financieros")
                        .hasAnyAuthority("MOVIMIENTO_FINANCIERO_LEER", "MOVIMIENTO_FINANCIERO_REGISTRAR",
                                "TRANSFERENCIA_CUENTA_REGISTRAR", "MOVIMIENTO_FINANCIERO_REVERTIR",
                                "CORTE_CAJA_LEER", "CORTE_CAJA_ADMINISTRAR")
                        .requestMatchers("/admin/movimientos-financieros/**", "/admin/catalogos/movimientos-financieros/**")
                        .hasAuthority("MOVIMIENTO_FINANCIERO_LEER")
                        .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            boolean entradaFamiliar = "familias".equals(request.getParameter("origen"));
                            boolean tienePortal = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("PORTAL_TUTOR_ACCEDER"));
                            response.sendRedirect(request.getContextPath()
                                    + (entradaFamiliar && tienePortal ? "/portal" : "/admin"));
                        })
                        .failureHandler((request, response, exception) -> response.sendRedirect(
                                request.getContextPath() + ("familias".equals(request.getParameter("origen"))
                                        ? "/familias?error" : "/login?error")))
                        .permitAll())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sesion -> sesion.invalidSessionUrl("/login?sesionExpirada"))
                .exceptionHandling(excepciones -> excepciones
                        .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/familias"),
                                request -> request.getRequestURI().startsWith(
                                        request.getContextPath() + "/portal"))
                        .accessDeniedHandler(accesoDenegadoHandler))
                .logout(logout -> logout
                        .logoutSuccessHandler((request, response, authentication) -> response.sendRedirect(
                                request.getContextPath() + ("familias".equals(request.getParameter("origen"))
                                        ? "/familias?logout" : "/")))
                        .deleteCookies("JSESSIONID"))
                .build();
    }
}
