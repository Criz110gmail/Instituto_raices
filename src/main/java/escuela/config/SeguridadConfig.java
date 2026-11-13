package escuela.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
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
                // Sólo el lienzo puede incrustarse en páginas de este mismo origen.
                .headers(headers -> headers.frameOptions(frame -> frame.disable())
                        .addHeaderWriter((request, response) -> response.setHeader("X-Frame-Options",
                                "GET".equals(request.getMethod()) &&
                                ((request.getContextPath() + "/admin/diseno-web/visual/lienzo").equals(request.getRequestURI()) || request.getRequestURI().substring(request.getContextPath().length()).matches("/admin/portales/(admin|familias|maestros|personal)/vista-previa"))
                                        ? "SAMEORIGIN" : "DENY")))
                .csrf(csrf -> csrf.csrfTokenRepository(new SesionCsrfTokenRepository()))
                // El éxito ya determina el portal; no guardar peticiones antiguas en sesiones nuevas.
                .requestCache(cache -> cache.requestCache(new org.springframework.security.web.savedrequest.NullRequestCache()))
                .addFilterBefore(new SesionInvalidaPostFilter(accesoDenegadoHandler), CsrfFilter.class)
                .addFilterAfter(new SesionActividadFilter(accesoDenegadoHandler), CsrfFilter.class)
                .addFilterAfter(new SesionInvalidaFilter(accesoDenegadoHandler), AnonymousAuthenticationFilter.class)
                .addFilterBefore(new InstitucionAccesoFilter(), org.springframework.security.web.authentication.logout.LogoutFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET,"/accesos/*/admin","/accesos/*/familias","/accesos/*/maestros","/accesos/*/personal","/accesos/*/*/logo.png").permitAll()
                        .requestMatchers(HttpMethod.POST,"/admin/portales/**").hasAuthority("PORTAL_IDENTIDAD_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET,"/admin/portales/*/editar").hasAuthority("PORTAL_IDENTIDAD_ADMINISTRAR")
                        .requestMatchers("/admin/portales","/admin/portales/**").hasAnyAuthority("PORTAL_IDENTIDAD_LEER","PORTAL_IDENTIDAD_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/sitio/*", "/sitio/*/imagenes/*").permitAll()
                        .requestMatchers("/admin/diseno-web", "/admin/diseno-web/**").hasAuthority("SITIO_WEB_EDITAR")
                        .requestMatchers(HttpMethod.GET, "/sesion/estado", "/acceso/csrf").permitAll()
                        .requestMatchers(HttpMethod.POST, "/acceso/comprobar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/sesion/renovar").authenticated()
                        .requestMatchers(HttpMethod.GET, "/sesion/identidad").authenticated()
                        .requestMatchers("/admin/pendientes", "/admin/pendientes/**").authenticated()
                        .requestMatchers(HttpMethod.GET,"/admin/recibos-nomina", "/admin/recibos-nomina/**").hasAnyAuthority("CONDICION_SALARIAL_LEER","CONDICION_SALARIAL_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST,"/admin/empleados/*/nomina","/admin/empleados/*/nomina/**").hasAuthority("EMPLEADO_NOMINA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET,"/admin/empleados/*/nomina","/admin/empleados/*/nomina/**").hasAnyAuthority("EMPLEADO_NOMINA_LEER","EMPLEADO_NOMINA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST,"/admin/configuracion-nomina", "/admin/configuracion-nomina/**", "/admin/preparacion-nomina", "/admin/preparacion-nomina/**", "/admin/calendarios-nomina", "/admin/calendarios-nomina/**", "/admin/periodos-nomina", "/admin/periodos-nomina/**", "/admin/conceptos-empleado", "/admin/conceptos-empleado/**", "/admin/conceptos-nomina", "/admin/conceptos-nomina/**", "/admin/jornadas", "/admin/jornadas/**", "/admin/esquemas", "/admin/esquemas/**", "/admin/asignaciones", "/admin/asignaciones/**", "/admin/puestos", "/admin/puestos/**", "/admin/empleados/*/condiciones", "/admin/empleados/*/condiciones/**", "/admin/empleados/*/puesto", "/admin/empleados/*/puesto/**")
                        .hasAuthority("CONDICION_SALARIAL_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET,"/admin/configuracion-nomina", "/admin/configuracion-nomina/**", "/admin/preparacion-nomina", "/admin/preparacion-nomina/**", "/admin/calendarios-nomina", "/admin/calendarios-nomina/**", "/admin/periodos-nomina", "/admin/periodos-nomina/**", "/admin/conceptos-empleado", "/admin/conceptos-empleado/**", "/admin/conceptos-nomina", "/admin/conceptos-nomina/**", "/admin/personal/filtros/**", "/admin/jornadas", "/admin/jornadas/**", "/admin/esquemas", "/admin/esquemas/**", "/admin/asignaciones", "/admin/asignaciones/**", "/admin/puestos", "/admin/puestos/**", "/admin/empleados/*/condiciones", "/admin/empleados/*/condiciones/**", "/admin/empleados/*/puesto", "/admin/empleados/*/puesto/**")
                        .hasAnyAuthority("CONDICION_SALARIAL_LEER","CONDICION_SALARIAL_ADMINISTRAR")
                        .requestMatchers("/", "/login", "/familias", "/maestros/acceso", "/activar-cuenta", "/restablecer-password", "/acceso-denegado", "/salud", "/actuator/health", "/css/**", "/js/**", "/favicon.svg", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/logo.png", "/images/logo1.png", "/images/imagen.png", "/images/sitio-objetos-escolares.svg").permitAll()
                        .requestMatchers("/personal/acceso").permitAll()
                        .requestMatchers("/personal", "/personal/**").hasAuthority("PORTAL_EMPLEADO_ACCEDER")
                        .requestMatchers("/maestros/**").hasAuthority("PORTAL_MAESTRO_ACCEDER")
                        .requestMatchers(HttpMethod.GET, "/portal/**")
                        .hasAuthority("PORTAL_TUTOR_ACCEDER")
                        .requestMatchers(HttpMethod.POST, "/portal/notificaciones/**")
                        .hasAuthority("PORTAL_TUTOR_ACCEDER")
                        .requestMatchers(HttpMethod.POST, "/portal/expediente/**")
                        .hasAuthority("PORTAL_TUTOR_ACCEDER")
                        .requestMatchers("/portal/pagos/**")
                        .hasAuthority("PORTAL_TUTOR_ACCEDER")
                        .requestMatchers("/admin/portal-soporte/**")
                        .hasAuthority("PORTAL_TUTOR_SOPORTE")
                        .requestMatchers(HttpMethod.GET, "/admin/guias/**", "/admin/catalogos/guias/**")
                        .hasAuthority("GUIA_PROCESOS_CONSULTAR")
                        .requestMatchers("/admin/guias/**", "/admin/catalogos/guias/**").denyAll()
                        .requestMatchers("/admin/portal-maestros-soporte/**", "/admin/catalogos/portal-maestro/**")
                        .hasAuthority("PORTAL_MAESTRO_SOPORTE")
                        .requestMatchers(HttpMethod.GET,"/admin/empleados/*/puestos/buscar").hasAuthority("EMPLEADO_LABORAL_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET,"/admin/empleados/**").hasAnyAuthority("EMPLEADO_LEER","EMPLEADO_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST,"/admin/empleados/*/laboral","/admin/empleados/*/planteles","/admin/empleados/*/planteles/*/finalizar","/admin/empleados/*/planteles/cambio-principal").hasAuthority("EMPLEADO_LABORAL_ADMINISTRAR")
                        .requestMatchers("/admin/empleados/**").hasAuthority("EMPLEADO_ADMINISTRAR")
                        .requestMatchers("/admin/maestros/**").hasAuthority("MAESTRO_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/planeaciones/**")
                        .hasAnyAuthority("PLANEACION_LEER", "PLANEACION_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/planeaciones/**")
                        .hasAuthority("PLANEACION_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/horarios-clases/nuevo", "/admin/horarios-clases/*/editar",
                                "/admin/horarios-clases/asignaciones")
                        .hasAuthority("HORARIO_CLASE_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/horarios-clases/**")
                        .hasAuthority("HORARIO_CLASE_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/horarios-clases/**")
                        .hasAnyAuthority("HORARIO_CLASE_LEER", "HORARIO_CLASE_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/calendario-escolar/nuevo", "/admin/calendario-escolar/*/editar")
                        .hasAuthority("CALENDARIO_ESCOLAR_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/calendario-escolar/**")
                        .hasAuthority("CALENDARIO_ESCOLAR_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/calendario-escolar/**")
                        .hasAnyAuthority("CALENDARIO_ESCOLAR_LEER", "CALENDARIO_ESCOLAR_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/actualizaciones-expediente/*/aprobar",
                                "/admin/actualizaciones-expediente/*/rechazar")
                        .hasAuthority("ACTUALIZACION_EXPEDIENTE_REVISAR")
                        .requestMatchers(HttpMethod.GET, "/admin/actualizaciones-expediente/**")
                        .hasAnyAuthority("ACTUALIZACION_EXPEDIENTE_LEER", "ACTUALIZACION_EXPEDIENTE_REVISAR")
                        .requestMatchers(HttpMethod.GET, "/admin/proveedores/nuevo", "/admin/proveedores/*/editar")
                        .hasAuthority("PROVEEDOR_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/proveedores/**")
                        .hasAuthority("PROVEEDOR_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/proveedores/**")
                        .hasAnyAuthority("PROVEEDOR_LEER", "PROVEEDOR_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/compras/*/comprobantes/nuevo", "/admin/compras/*/comprobantes/respaldo",
                                "/admin/compras/*/comprobantes/*/retirar", "/admin/compras/*/comprobantes/*/importar")
                        .hasAuthority("COMPRA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/compras/*/comprobantes/**")
                        .hasAuthority("COMPRA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/compras/nueva", "/admin/compras/*/editar")
                        .hasAuthority("COMPRA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/compras/*/confirmar")
                        .hasAuthority("COMPRA_CONFIRMAR")
                        .requestMatchers(HttpMethod.POST, "/admin/compras/*/cancelar")
                        .hasAuthority("COMPRA_CANCELAR")
                        .requestMatchers(HttpMethod.POST, "/admin/compras", "/admin/compras/*")
                        .hasAuthority("COMPRA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/compras/**")
                        .hasAnyAuthority("COMPRA_LEER", "COMPRA_ADMINISTRAR", "COMPRA_CONFIRMAR", "COMPRA_CANCELAR")
                        .requestMatchers("/admin/instituciones/**").hasAuthority("INSTITUCION_ADMINISTRAR")
                        .requestMatchers("/admin/planteles/**").hasAuthority("PLANTEL_ADMINISTRAR")
                        .requestMatchers("/admin/niveles/**").hasAuthority("NIVEL_ADMINISTRAR")
                        .requestMatchers("/admin/oferta/**").hasAuthority("OFERTA_ADMINISTRAR")
                        .requestMatchers("/admin/grados/**").hasAuthority("GRADO_ADMINISTRAR")
                        .requestMatchers("/admin/ciclos/**").hasAuthority("CICLO_ADMINISTRAR")
                        .requestMatchers("/admin/periodos/**").hasAuthority("PERIODO_ADMINISTRAR")
                        .requestMatchers("/admin/grupos/**").hasAuthority("GRUPO_ADMINISTRAR")
                        .requestMatchers("/admin/materias/**").hasAuthority("MATERIA_ADMINISTRAR")
                        .requestMatchers("/admin/calificaciones/**").hasAuthority("CALIFICACION_ADMINISTRAR")
                        .requestMatchers("/admin/asistencia/**").hasAuthority("ASISTENCIA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/alumnos/*/fotografias/*")
                        .hasAnyAuthority("ALUMNO_LEER", "ALUMNO_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/alumnos/*/documentos/*")
                        .hasAnyAuthority("ALUMNO_LEER", "ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/alumnos/**").hasAuthority("ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/tutores/**").hasAuthority("TUTOR_ADMINISTRAR")
                        .requestMatchers("/admin/vinculos-tutor/**").hasAuthority("VINCULO_TUTOR_ADMINISTRAR")
                        .requestMatchers("/admin/inscripciones/*/pago-anticipado", "/admin/inscripciones/*/pago-anticipado/**").hasAuthority("PAGO_REGISTRAR")
                        .requestMatchers("/admin/inscripciones/**").hasAuthority("INSCRIPCION_ADMINISTRAR")
                        .requestMatchers("/admin/conceptos-cobro/**").hasAuthority("CONCEPTO_COBRO_ADMINISTRAR")
                        .requestMatchers("/admin/cuotas-alumno/**").hasAuthority("CUOTA_ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/cargos/**").hasAuthority("CARGO_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST,"/admin/convenios-pago/anticipados/*/pago").access(org.springframework.security.authorization.AuthorizationManagers.allOf(org.springframework.security.authorization.AuthorityAuthorizationManager.hasAuthority("CONVENIO_PAGO_ADMINISTRAR"),org.springframework.security.authorization.AuthorityAuthorizationManager.hasAuthority("PAGO_REGISTRAR")))
                        .requestMatchers("/admin/convenios-pago/**").hasAuthority("CONVENIO_PAGO_ADMINISTRAR")
                        .requestMatchers("/admin/tipos-beca/**").hasAuthority("TIPO_BECA_ADMINISTRAR")
                        .requestMatchers("/admin/becas-alumno/**").hasAuthority("BECA_ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/ajustes-cargo/**").hasAuthority("AJUSTE_CARGO_ADMINISTRAR")
                        .requestMatchers("/admin/politicas-recargo/**").hasAuthority("POLITICA_RECARGO_ADMINISTRAR")
                        .requestMatchers("/admin/motivos-financieros/**").hasAuthority("MOTIVO_FINANCIERO_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST,"/admin/cuentas-financieras/*/chequeras", "/admin/cuentas-financieras/*/chequeras/**").hasAuthority("CHEQUERA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET,"/admin/prestamos", "/admin/prestamos/**").hasAnyAuthority("PRESTAMO_LEER","PRESTAMO_ADMINISTRAR")
                        .requestMatchers(HttpMethod.POST,"/admin/prestamos", "/admin/prestamos/**").hasAuthority("PRESTAMO_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET,"/admin/cuentas-financieras/*/chequeras", "/admin/cuentas-financieras/*/chequeras/**").hasAnyAuthority("CHEQUERA_LEER","CHEQUERA_ADMINISTRAR")
                        .requestMatchers("/admin/cuentas-financieras/**").hasAuthority("CUENTA_FINANCIERA_ADMINISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/pagos/*/editar", "/admin/pagos/*/comprobantes/*",
                                "/admin/pagos/*/comprobante-pago")
                        .hasAnyAuthority("PAGO_LEER", "PAGO_REGISTRAR", "PAGO_VALIDAR", "PAGO_DEVOLVER", "PAGO_CANCELAR")
                        .requestMatchers(HttpMethod.POST, "/admin/pagos/*/validar", "/admin/pagos/*/rechazar")
                        .hasAuthority("PAGO_VALIDAR")
                        .requestMatchers(HttpMethod.POST, "/admin/pagos/*/devolver")
                        .hasAuthority("PAGO_DEVOLVER")
                        .requestMatchers(HttpMethod.POST, "/admin/pagos/*/cancelar")
                        .hasAuthority("PAGO_CANCELAR")
                        .requestMatchers(HttpMethod.POST,"/admin/pagos/*/saldo-favor/*/revertir").hasAuthority("PAGO_CANCELAR")
                        .requestMatchers(HttpMethod.POST,"/admin/pagos/*/saldo-favor").hasAuthority("PAGO_VALIDAR")
                        .requestMatchers("/admin/pagos/*/saldo-favor/**","/admin/pagos/*/saldo-favor")
                        .hasAnyAuthority("PAGO_VALIDAR","PAGO_CANCELAR")
                        .requestMatchers("/admin/pagos/**").hasAuthority("PAGO_REGISTRAR")
                        .requestMatchers("/admin/autocompletado/compras/filtros/**")
                        .hasAuthority("COMPRA_LEER")
                        .requestMatchers("/admin/autocompletado/compras/**")
                        .hasAuthority("COMPRA_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/alumnos")
                        .hasAnyAuthority("VINCULO_TUTOR_ADMINISTRAR", "ALUMNO_ADMINISTRAR", "INSCRIPCION_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/alumnos-adeudos").hasAnyAuthority("CARGO_LEER", "CARGO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/alumnos-cuotas").hasAnyAuthority("CUOTA_ALUMNO_LEER", "CUOTA_ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/conceptos-filtro")
                        .hasAnyAuthority("PAGO_LEER", "CARGO_LEER", "CARGO_ADMINISTRAR", "CUOTA_ALUMNO_LEER", "CUOTA_ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/alumnos-pagos")
                        .hasAuthority("PAGO_LEER")
                        .requestMatchers("/admin/autocompletado/tutores")
                        .hasAnyAuthority("VINCULO_TUTOR_ADMINISTRAR", "TUTOR_ADMINISTRAR", "PAGO_REGISTRAR", "CONVENIO_PAGO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/cuentas-pago", "/admin/autocompletado/cargos-pago")
                        .hasAnyAuthority("PAGO_REGISTRAR", "PAGO_VALIDAR")
                        .requestMatchers("/admin/autocompletado/motivos-movimiento")
                        .hasAuthority("MOVIMIENTO_FINANCIERO_REGISTRAR")
                        .requestMatchers("/admin/autocompletado/cuentas-movimiento")
                        .hasAnyAuthority("MOVIMIENTO_FINANCIERO_LEER", "MOVIMIENTO_FINANCIERO_REGISTRAR",
                                "TRANSFERENCIA_CUENTA_REGISTRAR", "PAGO_DEVOLVER",
                                "RETIRO_FONDO_LEER", "RETIRO_FONDO_REGISTRAR")
                        .requestMatchers("/admin/autocompletado/cuentas-caja")
                        .hasAnyAuthority("CORTE_CAJA_LEER", "CORTE_CAJA_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/alumnos-reporte", "/admin/autocompletado/cuentas-reporte")
                        .hasAuthority("REPORTE_FINANCIERO_CONSULTAR")
                        .requestMatchers("/admin/autocompletado/destinatarios-evento")
                        .hasAuthority("EVENTO_ESCOLAR_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/usuarios")
                        .hasAnyAuthority("TUTOR_ADMINISTRAR", "USUARIO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/inscripciones", "/admin/autocompletado/conceptos-cobro")
                        .hasAnyAuthority("CUOTA_ALUMNO_ADMINISTRAR", "CARGO_ADMINISTRAR", "BECA_ALUMNO_ADMINISTRAR", "POLITICA_RECARGO_ADMINISTRAR", "CONVENIO_PAGO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/tipos-beca")
                        .hasAuthority("BECA_ALUMNO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/periodos-cargo")
                        .hasAuthority("CARGO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/grados-materia")
                        .hasAuthority("MATERIA_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/grupos-calificacion")
                        .hasAnyAuthority("CALIFICACION_ADMINISTRAR", "ASISTENCIA_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/grupos-boleta")
                        .hasAuthority("BOLETA_CONSULTAR")
                        .requestMatchers("/admin/autocompletado/grupos-maestro", "/admin/autocompletado/materias-maestro")
                        .hasAuthority("MAESTRO_ADMINISTRAR")
                        .requestMatchers("/admin/autocompletado/periodos-calificacion",
                                "/admin/autocompletado/materias-calificacion")
                        .hasAuthority("CALIFICACION_ADMINISTRAR")
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
                        .requestMatchers("/admin/catalogos/materias/**").hasAnyAuthority("MATERIA_LEER", "MATERIA_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/calificaciones/**").hasAnyAuthority("CALIFICACION_LEER", "CALIFICACION_ADMINISTRAR")
                        .requestMatchers("/admin/catalogos/asistencia/**").hasAnyAuthority("ASISTENCIA_LEER", "ASISTENCIA_ADMINISTRAR")
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
                        .requestMatchers(HttpMethod.GET, "/admin/retiros-fondo/nuevo")
                        .hasAuthority("RETIRO_FONDO_REGISTRAR")
                        .requestMatchers(HttpMethod.POST, "/admin/retiros-fondo")
                        .hasAuthority("RETIRO_FONDO_REGISTRAR")
                        .requestMatchers(HttpMethod.GET, "/admin/retiros-fondo", "/admin/retiros-fondo/**")
                        .hasAnyAuthority("RETIRO_FONDO_LEER", "RETIRO_FONDO_REGISTRAR")
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
                        .requestMatchers(HttpMethod.GET, "/admin/boletas/**")
                        .hasAuthority("BOLETA_CONSULTAR")
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
                            if(InstitucionAccesoFilter.codigoValido(request.getParameter("institucionAcceso")))request.getSession().setAttribute(InstitucionAccesoFilter.CONTEXTO,request.getParameter("institucionAcceso"));
                            boolean entradaFamiliar = "familias".equals(request.getParameter("origen"));
                            boolean entradaMaestro = "maestros".equals(request.getParameter("origen"));
                            boolean entradaPersonal = "personal".equals(request.getParameter("origen"));
                            boolean tienePersonal=authentication.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("PORTAL_EMPLEADO_ACCEDER"));
                            boolean tienePortal = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("PORTAL_TUTOR_ACCEDER"));
                            boolean tienePortalMaestro = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("PORTAL_MAESTRO_ACCEDER"));
                            response.sendRedirect(request.getContextPath()
                                    + (entradaPersonal ? (tienePersonal ? "/personal" : "/acceso-denegado")
                                    : entradaFamiliar ? (tienePortal ? "/portal" : "/acceso-denegado")
                                    : entradaMaestro ? (tienePortalMaestro ? "/maestros" : "/acceso-denegado")
                                    : tienePortalMaestro && !tienePortal ? "/maestros"
                                    : tienePortal && !tienePortalMaestro ? "/portal" : tienePersonal ? "/personal" : "/admin"));
                        })
                        .failureHandler((request, response, exception) -> response.sendRedirect(
                                request.getContextPath() + InstitucionAccesoFilter.continuar(request,("familias".equals(request.getParameter("origen"))
                                        ? "/familias?error" : "maestros".equals(request.getParameter("origen"))
                                        ? "/maestros/acceso?error" : "personal".equals(request.getParameter("origen")) ? "/personal/acceso?error" : "/login?error"))))
                        .permitAll())
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(excepciones -> excepciones
                        .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/personal/acceso"),
                                request -> request.getRequestURI().startsWith(request.getContextPath()+"/personal"))
                        .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/familias"),
                                request -> request.getRequestURI().startsWith(
                                        request.getContextPath() + "/portal"))
                        .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/maestros/acceso"),
                                request -> request.getRequestURI().startsWith(
                                        request.getContextPath() + "/maestros"))
                        .accessDeniedHandler(accesoDenegadoHandler))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessHandler((request, response, authentication) -> response.sendRedirect(
                                request.getContextPath() + InstitucionAccesoFilter.continuar(request,rutaDespuesDeCerrarSesion(request.getParameter("origen")))))
                        .deleteCookies("JSESSIONID", "SESSION")
                        .permitAll())
                .build();
    }

    static String rutaDespuesDeCerrarSesion(String origen) {
        if ("personal".equals(origen)) return "/personal/acceso?logout";
        if ("familias".equals(origen)) return "/familias?logout";
        if ("maestros".equals(origen)) return "/maestros/acceso?logout";
        return "/login?logout";
    }
}
