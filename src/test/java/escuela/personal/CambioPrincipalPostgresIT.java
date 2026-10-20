package escuela.personal;

import escuela.SistemaAdministrativoEscolarApplication;
import escuela.personal.dto.CambioPrincipalForm;
import escuela.personal.service.*;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Sólo base temporal explícita. No lee .env ni permite una URL de operación. */
@EnabledIfEnvironmentVariable(named="QA_CAMBIO_PRINCIPAL_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_cambio_qa")
class CambioPrincipalPostgresIT {
    private UsernamePasswordAuthenticationToken auth(){var permisos=List.of("EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR").stream().map(SimpleGrantedAuthority::new).toList();var p=new UsuarioPrincipal(94000L,94000L,Set.of(),true,false,"qa.cambio","",permisos);return new UsernamePasswordAuthenticationToken(p,"",permisos);}
    @Test void rollbackRealCambioConcurrenteYClasificacionPaginada()throws Exception {
        var storage=java.nio.file.Files.createTempDirectory(java.nio.file.Path.of("target"),"qa-cambio-").toAbsolutePath();
        try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run(
                "--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_CAMBIO_PRINCIPAL_JDBC"),
                "--spring.datasource.username=postgres","--spring.datasource.password=qa_cambio_aislado",
                "--spring.security.user.name=qa-recuperacion","--spring.security.user.password=qa-no-usada",
                "--app.archivos.directorio="+storage)) {
            var jdbc=app.getBean(JdbcTemplate.class);
            jdbc.update("insert into institucion(id,codigo,nombre,zona_horaria) values(94000,'QA-CAMBIO','Institución ficticia','America/Mexico_City')");
            jdbc.update("insert into usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) values(94000,94000,'qa.cambio','qa@example.invalid','ADMINISTRATIVO','ACTIVO','hash-ficticio-no-usado')");
            jdbc.update("insert into empleado(id,institucion_id,codigo,nombres,primer_apellido) values(94000,94000,'EMP-CAMBIO-QA','Personal','Ficticio')");
            jdbc.update("insert into empleado_laboral(empleado_id) values(94000)");
            jdbc.update("insert into plantel(id,institucion_id,codigo,nombre) values(94000,94000,'QA-A','Plantel A'),(94001,94000,'QA-B','Plantel B')");
            LocalDate hoy=LocalDate.now(ZoneId.of("America/Mexico_City")),fecha=hoy.plusDays(2);
            jdbc.update("insert into empleado_plantel(id,empleado_id,plantel_id,fecha_inicio,principal) values(94000,94000,94000,?,true),(94001,94000,94001,?,false)",hoy.minusDays(10),hoy.minusDays(10));
            var service=app.getBean(CambioPrincipalEmpleadoService.class);
            var empleados=app.getBean(EmpleadoService.class);
            SecurityContextHolder.getContext().setAuthentication(auth());
            var vista=service.visualizar(94000L,fecha,94001L,true);
            assertThat(jdbc.queryForObject("select count(*) from empleado_plantel",Integer.class)).isEqualTo(2);
            var mvc=MockMvcBuilders.webAppContextSetup((WebApplicationContext)app).addFilters(app.getBean(FilterChainProxy.class)).build();
            var session=new MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth()));
            var preview=mvc.perform(get("/admin/empleados/94000/planteles/cambio-principal/vista-previa").session(session).param("fecha",fecha.toString()).param("plantelId","94001")).andReturn().getResponse();
            assertThat(preview.getStatus()).isEqualTo(200);assertThat(preview.getHeader("Cache-Control")).contains("no-store");assertThat(preview.getContentAsString()).contains("Plantel A","Plantel B",fecha.toString());
            SecurityContextHolder.getContext().setAuthentication(auth());
            var form=new CambioPrincipalForm();form.setFecha(fecha);form.setPlantelId(94001L);form.setOrigenId(vista.origenId());form.setVersionOrigen(vista.versionOrigen());form.setDestinoId(vista.destinoId());form.setVersionDestino(vista.versionDestino());form.setConservarAnterior(true);form.setMotivo("Reorganización ficticia de QA");
            // Falla deliberada tras cerrar filas, antes de terminar la inserción: debe revertir TODO.
            jdbc.execute("CREATE FUNCTION qa_rechazar_nueva() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN IF NEW.plantel_id=94001 AND NEW.principal THEN RAISE EXCEPTION 'Falla ficticia de QA'; END IF; RETURN NEW; END $$");
            jdbc.execute("CREATE TRIGGER qa_falla BEFORE INSERT ON empleado_plantel FOR EACH ROW EXECUTE FUNCTION qa_rechazar_nueva()");
            assertThatThrownBy(()->service.confirmar(94000L,form)).isInstanceOf(RuntimeException.class);
            assertThat(jdbc.queryForObject("select count(*) from empleado_plantel where fecha_fin is null",Integer.class)).isEqualTo(2);
            assertThat(jdbc.queryForObject("select count(*) from auditoria where accion='EMPLEADO_PRINCIPAL_CAMBIADO'",Integer.class)).isZero();
            jdbc.execute("DROP TRIGGER qa_falla ON empleado_plantel");
            var pool=Executors.newFixedThreadPool(2);var inicio=new CountDownLatch(1);
            try {
                Callable<Boolean> tarea=()->{SecurityContextHolder.getContext().setAuthentication(auth());try{inicio.await();service.confirmar(94000L,form);return true;}catch(RuntimeException e){return false;}finally{SecurityContextHolder.clearContext();}};
                var uno=pool.submit(tarea);var dos=pool.submit(tarea);inicio.countDown();
                assertThat(List.of(uno.get(30,TimeUnit.SECONDS),dos.get(30,TimeUnit.SECONDS))).containsExactlyInAnyOrder(true,false);
            } finally {pool.shutdownNow();}
            SecurityContextHolder.getContext().setAuthentication(auth());
            assertThat(jdbc.queryForObject("select count(*) from empleado_plantel",Integer.class)).isEqualTo(4);
            assertThat(jdbc.queryForObject("select count(*) from empleado_plantel where fecha_fin=?",Integer.class,fecha.minusDays(1))).isEqualTo(2);
            assertThat(jdbc.queryForObject("select count(*) from auditoria where accion='EMPLEADO_PRINCIPAL_CAMBIADO' and usuario_actor_id=94000 and motivo='Reorganización ficticia de QA'",Integer.class)).isEqualTo(1);
            assertThat(empleados.plantelesGrupo(94000L,0,"ACTUALES").getContent().getFirst().plantel()).isEqualTo("Plantel A");
            assertThat(empleados.plantelesGrupo(94000L,0,"PROXIMAS").getContent().getFirst().plantel()).isEqualTo("Plantel B");
            // Historial amplio: siempre páginas de20 y orden por fecha, no descarga completa.
            for(int n=1;n<=23;n++)jdbc.update("insert into empleado_plantel(empleado_id,plantel_id,fecha_inicio,fecha_fin,principal) values(94000,94000,?,?,false)",hoy.minusDays(20+n*2),hoy.minusDays(20+n*2));
            var historicas=empleados.plantelesGrupo(94000L,0,"HISTORICAS");assertThat(historicas.getContent()).hasSize(20);assertThat(historicas.getTotalElements()).isEqualTo(23);assertThat(empleados.plantelesGrupo(94000L,1,"HISTORICAS").getContent()).hasSize(3);
            var detalle=mvc.perform(get("/admin/empleados/94000").session(session).param("paginaAnteriores","0")).andReturn().getResponse();
            assertThat(detalle.getStatus()).isEqualTo(200);assertThat(detalle.getContentAsString()).contains("Asignaciones actuales","Próximas asignaciones","Ver asignaciones anteriores","employee-campus-history\" open","paginaAnteriores=1");
            // Restricciones reales aún impiden dos principales simultáneos.
            assertThatThrownBy(()->jdbc.update("insert into empleado_plantel(empleado_id,plantel_id,fecha_inicio,principal) values(94000,94000,?,true)",fecha)).isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
        } finally {SecurityContextHolder.clearContext();}
    }
}
