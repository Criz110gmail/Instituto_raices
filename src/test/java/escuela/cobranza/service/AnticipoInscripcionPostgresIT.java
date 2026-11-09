package escuela.cobranza.service;

import escuela.SistemaAdministrativoEscolarApplication;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@EnabledIfEnvironmentVariable(named="QA_ANTICIPO_JDBC",matches="jdbc:postgresql://nexo-anticipo-qa:5432/nexo_anticipo_qa")
class AnticipoInscripcionPostgresIT {
    @Test void preparaSoloAlumnoSeleccionadoYValidaUnPagoConAplicacionesSeparadas()throws Exception {
        var storage=Files.createTempDirectory(Path.of("target"),"qa-anticipo-").toAbsolutePath();
        try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_ANTICIPO_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-ficticia","--spring.security.user.name=qa-recuperacion","--spring.security.user.password=qa-ficticia","--app.archivos.directorio="+storage)) {
            var j=app.getBean(JdbcTemplate.class);
            j.update("INSERT INTO institucion(id,codigo,nombre) VALUES(97000,'QA-ANT','Escuela ficticia')");
            j.update("INSERT INTO usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) VALUES(97000,97000,'qa.anticipo','qa@example.invalid','ADMINISTRATIVO','ACTIVO','no-usado')");
            j.update("INSERT INTO plantel(id,institucion_id,codigo,nombre) VALUES(97000,97000,'QA','Plantel ficticio')");
            j.update("INSERT INTO nivel_educativo(id,institucion_id,codigo,nombre,orden) VALUES(97000,97000,'QA','Nivel ficticio',1)");
            j.update("INSERT INTO grado(id,nivel_educativo_id,codigo,nombre,orden) VALUES(97000,97000,'QA','Grado ficticio',1)");
            j.update("INSERT INTO ciclo_escolar(id,institucion_id,codigo,nombre,fecha_inicio,fecha_fin,estado) VALUES(97000,97000,'QA','Ciclo ficticio','2026-01-01','2027-12-31','ABIERTO')");
            j.update("INSERT INTO alumno(id,institucion_id,matricula,nombres,primer_apellido,fecha_nacimiento,fecha_ingreso) VALUES(97000,97000,'QA-A','Ana','Ficticia','2016-01-01','2026-01-01'),(97001,97000,'QA-B','Otro','Ficticio','2016-01-01','2026-01-01')");
            j.update("INSERT INTO inscripcion(id,alumno_id,plantel_id,ciclo_escolar_id,grado_id,numero_inscripcion,fecha_inscripcion,fecha_inicio,fecha_fin,estado) VALUES(97000,97000,97000,97000,97000,'QA-A','2026-01-01','2026-01-01','2027-12-31','ACTIVA'),(97001,97001,97000,97000,97000,'QA-B','2026-01-01','2026-01-01','2027-12-31','ACTIVA')");
            j.update("INSERT INTO concepto_cobro(id,institucion_id,codigo,nombre,categoria) VALUES(97000,97000,'QA-MES','Colegiatura ficticia','COLEGIATURA')");
            j.update("INSERT INTO cuota_alumno(id,inscripcion_id,concepto_cobro_id,importe_base,moneda,frecuencia,fecha_inicio,fecha_fin,dia_vencimiento,generacion_automatica,estado) VALUES(97000,97000,97000,500,'MXN','MENSUAL','2027-01-01','2027-03-31',10,true,'ACTIVA'),(97001,97001,97000,500,'MXN','MENSUAL','2027-01-01','2027-03-31',10,true,'ACTIVA')");
            j.update("INSERT INTO tutor(id,institucion_id,nombres,primer_apellido,telefono_principal) VALUES(97000,97000,'Tutor','Ficticio','0000000000')");
            j.update("INSERT INTO alumno_tutor(alumno_id,tutor_id,parentesco,es_responsable_financiero,fecha_inicio) VALUES(97000,97000,'TUTOR_LEGAL',true,'2026-01-01')");
            j.update("INSERT INTO cuenta_financiera(id,institucion_id,plantel_id,codigo,nombre,tipo,moneda,saldo_inicial,fecha_saldo_inicial) VALUES(97000,97000,97000,'QA-CAJA','Caja ficticia','CAJA','MXN',0,'2026-01-01')");
            var permisos=java.util.stream.Stream.of("INSCRIPCION_ADMINISTRAR","CARGO_ADMINISTRAR","PAGO_REGISTRAR","PAGO_VALIDAR","CUENTA_FINANCIERA_LEER").map(SimpleGrantedAuthority::new).toList();
            var auth=UsernamePasswordAuthenticationToken.authenticated(new UsuarioPrincipal(97000L,97000L,Set.of(97000L),false,false,"qa.anticipo","",permisos),"",permisos);
            SecurityContextHolder.getContext().setAuthentication(auth);var s=app.getBean(CargoService.class);var desde=LocalDate.of(2027,1,1);var hasta=LocalDate.of(2027,3,31);var vista=s.anticipables(97000L,desde,hasta);
            assertThat(vista).hasSize(3);assertThat(j.queryForObject("SELECT count(*) FROM cargo",Long.class)).isZero();
            var elegidas=Set.of(vista.get(0).clave(),vista.get(1).clave());var cargos=s.confirmarAnticipo(97000L,desde,hasta,vista,elegidas);assertThat(cargos).hasSize(2);
            assertThat(j.queryForObject("SELECT count(*) FROM cargo WHERE inscripcion_id=97001",Long.class)).isZero();
            assertThatThrownBy(()->s.confirmarAnticipo(97000L,desde,hasta,vista,elegidas)).hasMessageContaining("Cambió");
            assertThat(j.queryForObject("SELECT count(*) FROM cargo",Long.class)).isEqualTo(2);
            var vistaConcurrente=s.anticipables(97000L,desde,hasta);var marzo=Set.of(vistaConcurrente.get(2).clave());var barrera=new java.util.concurrent.CountDownLatch(1);var pool=java.util.concurrent.Executors.newFixedThreadPool(2);
            try {var futuros=new ArrayList<java.util.concurrent.Future<Boolean>>();for(int n=0;n<2;n++)futuros.add(pool.submit(()->{SecurityContextHolder.getContext().setAuthentication(auth);try{barrera.await();s.confirmarAnticipo(97000L,desde,hasta,vistaConcurrente,marzo);return true;}catch(escuela.common.exception.ReglaNegocioException e){return false;}finally{SecurityContextHolder.clearContext();}}));barrera.countDown();int exitos=0;for(var futuro:futuros)if(futuro.get(20,java.util.concurrent.TimeUnit.SECONDS))exitos++;assertThat(exitos).isEqualTo(1);}finally{pool.shutdownNow();}
            assertThat(j.queryForObject("SELECT count(*) FROM cargo",Long.class)).isEqualTo(3);
            var mvc=MockMvcBuilders.webAppContextSetup((WebApplicationContext)app).addFilters(app.getBean(FilterChainProxy.class)).build();var session=new MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth));
            var inicio=mvc.perform(get("/admin/inscripciones/97000/pago-anticipado").session(session)).andReturn();assertThat(inicio.getResponse().getStatus()).isEqualTo(200);var csrf=(CsrfToken)inicio.getRequest().getAttribute(CsrfToken.class.getName());
            var permisosLimitados=List.of(new SimpleGrantedAuthority("PAGO_REGISTRAR"));var limitado=new MockHttpSession();limitado.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(UsernamePasswordAuthenticationToken.authenticated(new UsuarioPrincipal(97000L,97000L,Set.of(97000L),false,false,"qa.limitado","",permisosLimitados),"",permisosLimitados)));
            assertThat(mvc.perform(get("/admin/inscripciones/97000/pago-anticipado").session(limitado)).andReturn().getResponse().getStatus()).isEqualTo(403);
            assertThat(mvc.perform(post("/admin/inscripciones/97000/pago-anticipado/consultar").session(session).param("desde","2027-01-01").param("hasta","2027-03-31")).andReturn().getResponse().getStatus()).isBetween(300,499);
            var consulta=mvc.perform(post("/admin/inscripciones/97000/pago-anticipado/consultar").session(session).param(csrf.getParameterName(),csrf.getToken()).param("desde","2027-01-01").param("hasta","2027-03-31")).andReturn();assertThat(consulta.getResponse().getStatus()).isEqualTo(200);String token=consulta.getModelAndView().getModel().get("token").toString();
            var sinConfirmar=mvc.perform(post("/admin/inscripciones/97000/pago-anticipado/preparar").session(session).param(csrf.getParameterName(),csrf.getToken()).param("token",token).param("elegidas",elegidas.toArray(String[]::new))).andReturn();assertThat(sinConfirmar.getResponse().getStatus()).isEqualTo(200);assertThat(sinConfirmar.getModelAndView().getViewName()).isEqualTo("admin/inscripcion-anticipo");assertThat(j.queryForObject("SELECT count(*) FROM cargo",Long.class)).isEqualTo(3);
            var qa=Path.of("target/qa-anticipo-inscripcion");Files.createDirectories(qa);Files.writeString(qa.resolve("consulta.html"),consulta.getResponse().getContentAsString());
            var preparar=mvc.perform(post("/admin/inscripciones/97000/pago-anticipado/preparar").session(session).param(csrf.getParameterName(),csrf.getToken()).param("token",token).param("elegidas",elegidas.toArray(String[]::new)).param("confirmado","true")).andReturn();assertThat(preparar.getResponse().getStatus()).isEqualTo(200);assertThat(preparar.getModelAndView().getViewName()).isEqualTo("admin/pago-form");
            var f=(escuela.admin.dto.PagoForm)preparar.getModelAndView().getModel().get("form");assertThat(f.getSolicitudes()).hasSize(2);assertThat(f.getMonto()).isEqualByComparingTo("1000");Files.writeString(qa.resolve("pago.html"),preparar.getResponse().getContentAsString());assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isZero();
            SecurityContextHolder.getContext().setAuthentication(auth);
            var pago=app.getBean(escuela.finanzas.service.PagoService.class).registrar(new escuela.finanzas.dto.request.PagoRequest(97000L,97000L,97000L,"Tutor ficticio",Instant.now(),f.getMonto(),"MXN",escuela.finanzas.entity.MetodoPago.EFECTIVO,97000L,"QA-ANTICIPO","Prueba aislada",f.getSolicitudes().stream().map(escuela.admin.dto.SolicitudAplicacionPagoForm::request).toList()),List.of());
            assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isZero();
            app.getBean(escuela.finanzas.service.ValidacionPagoService.class).validarConResultado(pago.id(),new escuela.finanzas.dto.request.ValidacionPagoRequest(97000L,null,pago.auditoria().version()));
            assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isEqualTo(1);assertThat(j.queryForObject("SELECT count(*) FROM aplicacion_pago",Long.class)).isEqualTo(2);
        }finally{SecurityContextHolder.clearContext();}
    }
}
