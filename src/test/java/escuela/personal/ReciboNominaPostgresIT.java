package escuela.personal;

import escuela.SistemaAdministrativoEscolarApplication;
import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.finanzas.dto.ChequeraForm;
import escuela.finanzas.service.*;
import escuela.admin.service.JasperReciboNominaService;
import escuela.seguridad.service.UsuarioPrincipal;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/** Opera únicamente en base efímera sintética de recibos; no reutilizar respaldo humano. */
@EnabledIfEnvironmentVariable(named="QA_RECIBOS_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_recibos_qa")
class ReciboNominaPostgresIT {
    private static final long INST=680000;
    private UsernamePasswordAuthenticationToken auth(String... permisos){var a=Arrays.stream(permisos).map(SimpleGrantedAuthority::new).toList();return new UsernamePasswordAuthenticationToken(new UsuarioPrincipal(INST,INST,Set.of(),true,false,"qa.recibos","",a),"",a);}
    private final String[] permisos={"EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR","CONDICION_SALARIAL_LEER","EMPLEADO_NOMINA_ADMINISTRAR","MOVIMIENTO_FINANCIERO_REGISTRAR","MOVIMIENTO_FINANCIERO_REVERTIR","CUENTA_FINANCIERA_LEER","CHEQUERA_ADMINISTRAR","PRESTAMO_ADMINISTRAR"};
    private void admin(){SecurityContextHolder.getContext().setAuthentication(auth(permisos));}
    private MockHttpSession sesion(UsernamePasswordAuthenticationToken a){var s=new MockHttpSession();s.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(a));return s;}
    private FiltroReciboNomina filtro(String estado,Long empleado){return new FiltroReciboNomina("",estado,empleado,"",null,"",null,null,0,25);}
    private void concepto(ConceptoEmpleadoService s,long empleado,BigDecimal importe){var c=s.contextoConfiguracion(empleado,INST);var f=new ConceptoEmpleadoForm();f.setEmpleadoId(empleado);f.setConceptoId(INST);f.setEmpleadoTexto(c.empleado());f.setConceptoTexto(c.concepto());f.setConceptoVersion(c.conceptoVersion());f.setLaboralVersion(c.laboralVersion());f.setImporte(importe);f.setFrecuenciaImporte("MENSUAL");f.setInicio(LocalDate.of(2030,1,1));f.setFin(LocalDate.of(2030,12,31));f.setMotivo("Importe ficticio para recibos");s.asignarConfiguracion(f);}
    private long prestamo(PrestamoProgramadoService s,long empleado){var f=new PrestamoProgramaForm();f.setEmpleadoId(empleado);f.setConceptoId(INST+1);f.setConceptoVersion(0L);f.setCapital(new BigDecimal("100"));f.setCuotas(1);f.setInicio(LocalDate.of(2030,1,31));f.setMotivo("Préstamo ficticio para recibos");f.setSello(s.revisar(null,false,f).sello());long id=s.guardar(null,false,f);var entrega=new EntregarPrestamoForm();entrega.setVersion(0L);entrega.setCuentaId(INST);entrega.setMotivoPagoId(INST);entrega.setReferencia("PRE-REC-QA-"+empleado);entrega.setMotivo("Entrega aislada");entrega.setEntregaConfirmada(true);s.entregar(id,entrega,s.revisarEntrega(id,entrega).sello());return id;}
    @Test void recibosDePagosCanceladosCeroYLegadoSinEfectosDeConsulta()throws Exception{
        try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_RECIBOS_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-sintetica","--spring.security.user.name=qa-recuperacion","--spring.security.user.password=qa-sintetica","--app.archivos.directorio=target/qa-recibos-storage","--app.nomina.consolidada-habilitada=true")){
            var j=app.getBean(JdbcTemplate.class);
            j.update("INSERT INTO institucion(id,codigo,nombre,zona_horaria,moneda_predeterminada) VALUES(680000,'QA-RECIBOS','Escuela Ficticia de Recibos','America/Mexico_City','MXN'),(680001,'QA-AJENA-REC','Ajena','America/Mexico_City','MXN')");
            j.update("INSERT INTO usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) VALUES(680000,680000,'qa.recibos','recibos@example.invalid','ADMINISTRATIVO','ACTIVO','no-credencial-real')");
            for(long e=INST;e<INST+3;e++){j.update("INSERT INTO empleado(id,institucion_id,codigo,nombres,primer_apellido) VALUES(?,680000,?,'Persona','Ficticia')",e,"EMP-REC-"+e);j.update("INSERT INTO empleado_laboral(empleado_id,situacion,fecha_ingreso) VALUES(?,'ACTIVO','2026-01-01')",e);}
            j.update("INSERT INTO cuenta_financiera(id,institucion_id,codigo,nombre,tipo,banco_nombre,numero_cuenta,moneda,saldo_inicial,fecha_saldo_inicial) VALUES(680000,680000,'CAJA-QA','Caja ficticia','CAJA',NULL,NULL,'MXN',1000,'2026-01-01'),(680001,680000,'BANCO-QA','Banco ficticio','BANCO','Banco sintético','123456','MXN',1000,'2026-01-01')");
            j.update("INSERT INTO motivo_financiero(id,institucion_id,codigo,nombre,naturaleza,categoria) VALUES(680000,680000,'REC-QA','Nómina ficticia','AMBOS','PERSONAL')");
            j.update("INSERT INTO concepto_nomina(id,institucion_id,codigo,nombre,naturaleza,aplicacion,creado_por_id,actualizado_por_id) VALUES(680000,680000,'APOYO-QA','Apoyo ficticio','PERCEPCION','HABITUAL',680000,680000),(680001,680000,'PRE-QA','Cuota de préstamo ficticio','DEDUCCION','CON_SALDO',680000,680000)");
            admin();var conceptos=app.getBean(ConceptoEmpleadoService.class);concepto(conceptos,INST,new BigDecimal("200"));concepto(conceptos,INST+1,new BigDecimal("100"));concepto(conceptos,INST+2,new BigDecimal("300"));
            var prestamos=app.getBean(PrestamoProgramadoService.class);prestamo(prestamos,INST);prestamo(prestamos,INST+1);
            var periodos=app.getBean(ProgramacionNominaService.class);var pf=new PeriodoNominaForm();pf.setCodigo("ENE-2030-REC-QA");pf.setInicio(LocalDate.of(2030,1,1));pf.setFin(LocalDate.of(2030,1,31));pf.setPago(pf.getFin());long periodo=periodos.guardarPeriodo(null,pf);
            var chequeras=app.getBean(ChequeraService.class);var cf=new ChequeraForm();cf.setNombre("Chequera ficticia");cf.setFolioInicial(1001L);cf.setFolioFinal(1010L);cf.setMotivo("Prueba aislada");long chequera=chequeras.guardar(INST+1,null,cf);
            var multiples=app.getBean(PagoMultipleNominaService.class);var pagos=app.getBean(PagoEmpleadoNominaService.class);var preparacion=app.getBean(PreparacionNominaService.class);
            var f=new PagoIndividualNominaForm();f.setEmpleadoId(INST);f.setPeriodoId(periodo);f.setMotivoPagoId(INST);f.setReferencia("REC-PAGO-QA");f.setMotivo("Pago aislado con dos fuentes");
            var banco=f.getFuentes().getFirst();banco.setCuentaId(INST+1);banco.setImporte(new BigDecimal("60"));banco.setTipoEntrega("CHEQUE");banco.setChequeraId(chequera);banco.setFolioCheque(1001L);banco.setFechaCheque(app.getBean(ChequeNominaService.class).hoy());banco.setBeneficiarioCheque("Persona Ficticia");
            var efectivo=new PagoMultipleNominaForm.Fuente();efectivo.setCuentaId(INST);efectivo.setImporte(new BigDecimal("40"));f.getFuentes().add(efectivo);f.setSello(multiples.revisarIndividual(f).sello());f.setConfirmado(true);long nomina=multiples.pagarIndividual(f);
            var fila=pagos.listar(nomina,"","PAGADO",0,25).getContent().getFirst();long primerPago=fila.pagoId();
            var devolver=new PagoEmpleadoNominaForm();devolver.setVersion(preparacion.obtener(nomina).version());devolver.setMotivoPagoId(INST);devolver.setReferencia("DEV-REC-QA");devolver.setMotivo("Fondos ficticios recuperados");devolver.setConfirmado(true);devolver.setChequesAnulados(true);pagos.revertir(nomina,fila.id(),primerPago,devolver);
            var repago=new PagoMultipleNominaForm();repago.setFilaId(fila.id());repago.setVersion(preparacion.obtener(nomina).version());repago.setMotivoPagoId(INST);repago.setReferencia("REPAGO-REC-QA");repago.setMotivo("Nuevo intento aislado");repago.setConfirmado(true);var cash=new PagoMultipleNominaForm.Fuente();cash.setCuentaId(INST);cash.setImporte(new BigDecimal("100"));repago.getFuentes().clear();repago.getFuentes().add(cash);repago.setSello(multiples.revisar(nomina,repago).sello());multiples.pagar(nomina,repago);long segundoPago=pagos.fila(nomina,fila.id()).pagoId();
            var cero=new PagoIndividualNominaForm();cero.setEmpleadoId(INST+1);cero.setPeriodoId(periodo);cero.setMotivo("Cierre ficticio de neto cero con préstamo");cero.getFuentes().clear();cero.setSello(multiples.revisarIndividual(cero).sello());cero.setConfirmado(true);long nominaCero=multiples.pagarIndividual(cero);long filaCero=pagos.listar(nominaCero,"","CERO_CONFIRMADO",0,25).getContent().getFirst().id();
            var vista=preparacion.prepararPeriodo(periodo,"INDIVIDUAL");preparacion.seleccionarPreparacion(vista,0L,INST+2,true);long legado=preparacion.guardarPreparacion(vista,1L,"Histórica ficticia","Compatibilidad de recibos");
            var af=new AplicarNominaForm();af.setVersion(preparacion.obtener(legado).version());af.setCuentaId(INST+1);af.setMotivoPagoId(INST);af.setMotivo("Aplicación consolidada ficticia");var aplicacion=app.getBean(AplicacionNominaService.class);aplicacion.aplicar(legado,af,aplicacion.revisar(legado,af).sello(),true);
            var recibos=app.getBean(ReciboNominaService.class);long movimientos=j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class);
            assertThat(recibos.listar(filtro("TODOS",null)).getTotalElements()).isEqualTo(4);
            assertThat(recibos.listar(new FiltroReciboNomina("","TODOS",null,"",periodo,"",LocalDate.of(2030,1,15),LocalDate.of(2030,1,16),0,25)).getTotalElements()).isEqualTo(4);
            assertThat(recibos.listar(new FiltroReciboNomina("","TODOS",null,"",null,"",LocalDate.of(2030,2,1),null,0,25))).isEmpty();
            assertThat(recibos.listar(new FiltroReciboNomina("RN-P-"+String.format(Locale.ROOT,"%06d",primerPago),"TODOS",null,"",null,"",null,null,0,25)).getTotalElements()).isEqualTo(1);
            assertThat(recibos.obtener("PAGO",primerPago).recibo().estado()).isEqualTo("CANCELADO");assertThat(recibos.obtener("PAGO",primerPago).fuentes()).hasSize(2);assertThat(recibos.obtener("PAGO",primerPago).cancelacion()).contains("Fondos ficticios recuperados");
            assertThat(recibos.obtener("PAGO",segundoPago).recibo().neto()).isEqualByComparingTo("100");
            var rc=recibos.obtener("CERO",filaCero);assertThat(rc.recibo().neto()).isZero();assertThat(rc.fuentes()).isEmpty();assertThat(rc.prestamos().getFirst().capital()).isEqualByComparingTo("100");
            var hist=recibos.listar(filtro("HISTORICO",null)).getContent().getFirst();assertThat(recibos.obtener("HISTORICO",hist.documentoId()).recibo().neto()).isEqualByComparingTo("300");assertThat(recibos.listar(filtro("PAGADO",INST)).getTotalElements()).isEqualTo(1);
            assertThat(recibos.buscar("Pe",false)).isEmpty();assertThat(recibos.buscar("",false)).hasSize(3);assertThat(recibos.buscar("ENE",true)).hasSize(1);
            assertThatThrownBy(()->recibos.listar(new FiltroReciboNomina("","TODOS",999999L,"",null,"",null,null,0,25))).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
            assertThatThrownBy(()->recibos.obtener("PAGO",999999L)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
            // Cambio actual no debe reescribir la fotografía del recibo.
            j.update("UPDATE empleado SET nombres='Nombre actualizado' WHERE id=680000");assertThat(recibos.obtener("PAGO",segundoPago).recibo().nombre()).isEqualTo("Persona Ficticia");
            var mvc=MockMvcBuilders.webAppContextSetup((org.springframework.web.context.WebApplicationContext)app).addFilters(app.getBean(org.springframework.security.web.FilterChainProxy.class)).build();var sesion=sesion(auth(permisos));var dir=Files.createDirectories(Path.of("target/qa-recibos-nomina"));
            for(var ruta:List.of("/admin/recibos-nomina","/admin/recibos-nomina/PAGO/"+primerPago,"/admin/recibos-nomina/CERO/"+filaCero)){
                var respuesta=mvc.perform(get(ruta).session(sesion)).andReturn().getResponse();assertThat(respuesta.getStatus()).as(ruta).isEqualTo(200);Files.writeString(dir.resolve(ruta.equals("/admin/recibos-nomina")?"listado.html":ruta.contains("CERO")?"cero.html":"cancelado.html"),respuesta.getContentAsString());
            }
            var webpdf=mvc.perform(get("/admin/recibos-nomina/PAGO/"+segundoPago+"/pdf").session(sesion)).andReturn().getResponse();assertThat(webpdf.getContentType()).isEqualTo("application/pdf");assertThat(webpdf.getHeader("Content-Disposition")).startsWith("inline;");assertThat(webpdf.getHeader("Cache-Control")).contains("no-store");
            var invalido=mvc.perform(get("/admin/recibos-nomina").session(sesion).param("desde","2030-02-01").param("hasta","2030-01-01")).andReturn().getResponse();assertThat(invalido.getStatus()).isEqualTo(200);assertThat(invalido.getContentAsString()).contains("Revisa los filtros").contains("2030-02-01").contains("2030-01-01");
            var excel=mvc.perform(get("/admin/recibos-nomina/excel").session(sesion).param("estado","NETO_CERO")).andReturn().getResponse();try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(excel.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);assertThat(libro.getSheetAt(0).getRow(1).getCell(8).getNumericCellValue()).isZero();}
            var lectorPrestamos=sesion(auth("PRESTAMO_LEER"));for(var ruta:List.of("/admin/recibos-nomina","/admin/recibos-nomina/excel","/admin/recibos-nomina/PAGO/"+segundoPago+"/pdf","/admin/recibos-nomina/empleados/buscar"))assertThat(mvc.perform(get(ruta).session(lectorPrestamos)).andReturn().getResponse().getStatus()).as(ruta).isEqualTo(403);
            SecurityContextHolder.getContext().setAuthentication(auth("PRESTAMO_LEER"));assertThatThrownBy(()->recibos.obtener("CERO",filaCero)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);admin();
            var ajenoAuth=new UsernamePasswordAuthenticationToken(new UsuarioPrincipal(INST,INST+1,Set.of(),true,false,"qa.recibos","",auth(permisos).getAuthorities()),"",auth(permisos).getAuthorities());SecurityContextHolder.getContext().setAuthentication(ajenoAuth);assertThatThrownBy(()->recibos.obtener("PAGO",segundoPago)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);admin();
            assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isEqualTo(movimientos);
            var pdf=app.getBean(JasperReciboNominaService.class);for(var rr:recibos.listar(filtro("TODOS",null))){try(var out=Files.newOutputStream(dir.resolve(rr.estado().toLowerCase(Locale.ROOT)+".pdf"))){pdf.exportar(rr.tipo(),rr.documentoId(),out);}}
            probarPortal(app,j,mvc,dir,filaCero,segundoPago,hist.documentoId());
        }finally{SecurityContextHolder.clearContext();}
    }
    private void adminPortal(){var ampliados=new ArrayList<>(Arrays.asList(permisos));ampliados.add("EMPLEADO_ADMINISTRAR");ampliados.add("MAESTRO_ADMINISTRAR");SecurityContextHolder.getContext().setAuthentication(auth(ampliados.toArray(String[]::new)));}
    private AccesoRecibosForm accion(AccesoRecibosEmpleadoService acceso,long empleado,String accion){adminPortal();var f=new AccesoRecibosForm();f.setVersion(acceso.obtener(empleado).version());f.setAccion(accion);f.setConfirmado(true);return f;}
    private void probarPortal(org.springframework.context.ConfigurableApplicationContext app,JdbcTemplate j,org.springframework.test.web.servlet.MockMvc mvc,Path dir,long filaCero,long pagoAjeno,long historico)throws Exception{
        var acceso=app.getBean(AccesoRecibosEmpleadoService.class);var recibos=app.getBean(ReciboNominaService.class);var detalles=app.getBean(escuela.seguridad.service.impl.UsuarioSistemaDetailsService.class);var activar=app.getBean(escuela.seguridad.service.InvitacionUsuarioService.class);
        long movimientos=j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class);long roles=j.queryForObject("SELECT count(*) FROM usuario_rol",Long.class);
        adminPortal();var sesionAlta=sesion((UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication());
        var antes=mvc.perform(get("/admin/empleados/680001/portal-recibos").session(sesionAlta)).andReturn().getResponse();assertThat(antes.getStatus()).isEqualTo(200);assertThat(antes.getContentAsString()).contains("name=\"username\"","name=\"email\"");Files.writeString(dir.resolve("cuenta-sin-usuario.html"),antes.getContentAsString());
        var login=mvc.perform(get("/personal/acceso")).andReturn().getResponse();assertThat(login.getStatus()).isEqualTo(200);assertThat(login.getContentAsString()).contains("data-login-refresh=\"personal\"","name=\"origen\" value=\"personal\"");Files.writeString(dir.resolve("login-personal.html"),login.getContentAsString());
        var alta=accion(acceso,INST+1,"HABILITAR");
        var altaHttp=mvc.perform(post("/admin/empleados/680001/portal-recibos").session(sesionAlta).with(csrf())
                .param("version",alta.getVersion().toString()).param("accion","HABILITAR").param("confirmado","true")
                .param("username","sofia.recibos.qa").param("email","sofia-recibos@example.invalid")).andReturn();
        assertThat(altaHttp.getResponse().getStatus()).isEqualTo(302);
        assertThat(altaHttp.getFlashMap().get("enlaceExpira")).isNotNull();
        String primeraUrl=(String)altaHttp.getFlashMap().get("enlaceAcceso");assertThat(primeraUrl).contains("/activar-cuenta?token=");
        var mostrado=mvc.perform(get("/admin/empleados/680001/portal-recibos").session(sesionAlta).flashAttrs(altaHttp.getFlashMap())).andReturn().getResponse();
        assertThat(mostrado.getStatus()).isEqualTo(200);assertThat(mostrado.getContentAsString()).contains("Enlace de contraseña","/activar-cuenta?token=");
        // El enlace puede recuperarse cuando la primera respuesta se perdió; el anterior se revoca.
        var nueva=accion(acceso,INST+1,"ENLACE");
        var nuevoHttp=mvc.perform(post("/admin/empleados/680001/portal-recibos").session(sesionAlta).with(csrf())
                .param("version",nueva.getVersion().toString()).param("accion","ENLACE").param("confirmado","true")).andReturn();
        assertThat(nuevoHttp.getResponse().getStatus()).isEqualTo(302);
        String nuevaUrl=(String)nuevoHttp.getFlashMap().get("enlaceAcceso");assertThat(nuevaUrl).contains("/activar-cuenta?token=").isNotEqualTo(primeraUrl);
        String token=nuevaUrl.substring(nuevaUrl.indexOf("token=")+6);
        var sesionActivacion=new MockHttpSession();
        assertThat(mvc.perform(get("/activar-cuenta").session(sesionActivacion).param("token",token)).andReturn().getResponse().getStatus()).isEqualTo(200);
        var activacion=mvc.perform(post("/activar-cuenta").session(sesionActivacion).with(csrf()).param("token",token)
                .param("password","Clave-Ficticia-Recibos-2030!").param("confirmarPassword","Clave-Ficticia-Recibos-2030!")).andReturn().getResponse();
        assertThat(activacion.getStatus()).isEqualTo(200);assertThat(activacion.getContentAsString()).contains("Activación completada");
        var sesionLogin=new MockHttpSession();
        var ingreso=mvc.perform(post("/login").session(sesionLogin).with(csrf()).param("origen","personal")
                .param("username","QA-RECIBOS\\sofia.recibos.qa").param("password","Clave-Ficticia-Recibos-2030!")).andReturn();
        assertThat(ingreso.getResponse().getRedirectedUrl()).isEqualTo("/personal");
        var sesionReal=(MockHttpSession)ingreso.getRequest().getSession(false);
        assertThat(mvc.perform(get("/personal").session(sesionReal)).andReturn().getResponse().getContentAsString()).contains("RN-C-");
        var duplicado=accion(acceso,INST+2,"HABILITAR");
        var duplicadoHttp=mvc.perform(post("/admin/empleados/680002/portal-recibos").session(sesionAlta).with(csrf())
                .param("version",duplicado.getVersion().toString()).param("accion","HABILITAR").param("confirmado","true")
                .param("username","sofia.recibos.qa").param("email","otra-recibos@example.invalid")).andReturn().getResponse();
        assertThat(duplicadoHttp.getStatus()).isEqualTo(200);assertThat(duplicadoHttp.getContentAsString()).contains("ya pertenece a otra cuenta");
        assertThat(j.queryForObject("SELECT portal_recibos_habilitado FROM empleado WHERE id=680002",Boolean.class)).isFalse();
        var recuperar=accion(acceso,INST+1,"ENLACE");
        var recuperacionHttp=mvc.perform(post("/admin/empleados/680001/portal-recibos").session(sesionAlta).with(csrf())
                .param("version",recuperar.getVersion().toString()).param("accion","ENLACE").param("confirmado","true")).andReturn();
        assertThat(recuperacionHttp.getResponse().getStatus()).isEqualTo(302);
        String recuperacion=(String)recuperacionHttp.getFlashMap().get("enlaceAcceso");assertThat(recuperacion).contains("/restablecer-password?token=");
        String tokenRecuperacion=recuperacion.substring(recuperacion.indexOf("token=")+6);var sesionRecuperacion=new MockHttpSession();
        assertThat(mvc.perform(get("/restablecer-password").session(sesionRecuperacion).param("token",tokenRecuperacion)).andReturn().getResponse().getStatus()).isEqualTo(200);
        var restablecida=mvc.perform(post("/restablecer-password").session(sesionRecuperacion).with(csrf()).param("token",tokenRecuperacion)
                .param("password","Nueva-Clave-Ficticia-2030!").param("confirmarPassword","Nueva-Clave-Ficticia-2030!")).andReturn().getResponse();
        assertThat(restablecida.getStatus()).isEqualTo(200);assertThat(restablecida.getContentAsString()).contains("Contraseña actualizada");
        var nuevoIngreso=mvc.perform(post("/login").session(new MockHttpSession()).with(csrf()).param("origen","personal")
                .param("username","QA-RECIBOS\\sofia.recibos.qa").param("password","Nueva-Clave-Ficticia-2030!")).andReturn();
        assertThat(nuevoIngreso.getResponse().getRedirectedUrl()).isEqualTo("/personal");
        adminPortal();
        assertThatThrownBy(()->activar.activar(new escuela.seguridad.dto.request.ActivacionUsuarioRequest(token,"Clave-Ficticia-Recibos-2030!"))).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(()->acceso.gestionar(INST+1,alta)).isInstanceOf(RuntimeException.class);
        long usuarioPersonal=acceso.obtener(INST+1).usuarioId();assertThatThrownBy(()->j.update("UPDATE usuario SET tipo_cuenta='ADMINISTRATIVO' WHERE id=?",usuarioPersonal)).isInstanceOf(org.springframework.dao.DataAccessException.class);
        var propio=(UsuarioPrincipal)detalles.loadUserByUsername("QA-RECIBOS\\sofia.recibos.qa");var autenticado=new UsernamePasswordAuthenticationToken(propio,"",propio.getAuthorities());var sesionPersonal=sesion(autenticado);
        assertThat(propio.getAuthorities()).extracting(x->x.getAuthority()).containsExactly("PORTAL_EMPLEADO_ACCEDER");
        var html=mvc.perform(get("/personal").session(sesionPersonal)).andReturn().getResponse();assertThat(html.getStatus()).isEqualTo(200);assertThat(html.getHeader("Cache-Control")).contains("no-store");assertThat(html.getContentAsString()).contains("Mis recibos de nómina","RN-C-").doesNotContain("RN-P-","RN-H-","Fondos ficticios recuperados","Banco ficticio");Files.writeString(dir.resolve("portal-personal.html"),html.getContentAsString());
        for(var ruta:List.of("/personal/recibos/PAGO/"+pagoAjeno+"/pdf","/personal/recibos/HISTORICO/"+historico+"/pdf","/personal?empleadoId="+INST,"/admin/recibos-nomina","/admin/recibos-nomina/excel","/admin/usuarios","/maestros"))assertThat(mvc.perform(get(ruta).session(sesionPersonal)).andReturn().getResponse().getStatus()).as(ruta).isEqualTo(403);
        var pdf=mvc.perform(get("/personal/recibos/CERO/"+filaCero+"/pdf").session(sesionPersonal)).andReturn().getResponse();assertThat(pdf.getContentType()).isEqualTo("application/pdf");try(var doc=org.apache.pdfbox.Loader.loadPDF(pdf.getContentAsByteArray())){assertThat(new org.apache.pdfbox.text.PDFTextStripper().getText(doc)).contains("$0.00 MXN","Cuota de préstamo ficticio").doesNotContain("qa.recibos","CAJA-QA","PRE-000002");}
        // Cuenta docente preexistente: reutilizar sin crear otro usuario.
        j.update("INSERT INTO usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) VALUES(680010,680000,'docente.recibos.qa','docente-recibos@example.invalid','PORTAL_MAESTRO','ACTIVO','no-credencial-real')");
        j.update("INSERT INTO maestro(id,institucion_id,empleado_id,usuario_id,numero_empleado) VALUES(680010,680000,680000,680010,'DOC-REC-QA')");
        assertThatThrownBy(()->j.update("UPDATE empleado SET usuario_portal_id=680010 WHERE id=680002")).isInstanceOf(org.springframework.dao.DataAccessException.class);
        long usuariosAntes=j.queryForObject("SELECT count(*) FROM usuario",Long.class);acceso.gestionar(INST,accion(acceso,INST,"HABILITAR"));assertThat(acceso.obtener(INST).usuarioId()).isEqualTo(680010L);assertThat(j.queryForObject("SELECT count(*) FROM usuario",Long.class)).isEqualTo(usuariosAntes);
        var docente=(UsuarioPrincipal)detalles.loadUserByUsername("QA-RECIBOS\\docente.recibos.qa");assertThat(docente.getAuthorities()).extracting(x->x.getAuthority()).containsExactlyInAnyOrder("PORTAL_MAESTRO_ACCEDER","PORTAL_EMPLEADO_ACCEDER");var sesionDocente=sesion(new UsernamePasswordAuthenticationToken(docente,"",docente.getAuthorities()));
        var propiosDocente=mvc.perform(get("/personal").session(sesionDocente)).andReturn().getResponse();assertThat(propiosDocente.getStatus()).isEqualTo(200);assertThat(propiosDocente.getContentAsString()).contains("RN-P-","Volver a maestros").doesNotContain("RN-C-","RN-H-");
        acceso.gestionar(INST,accion(acceso,INST,"DESHABILITAR"));assertThat(j.queryForObject("SELECT estado FROM usuario WHERE id=680010",String.class)).isEqualTo("ACTIVO");assertThat(mvc.perform(get("/personal").session(sesionDocente)).andReturn().getResponse().getStatus()).isEqualTo(403);assertThat(mvc.perform(get("/maestros").session(sesionDocente)).andReturn().getResponse().getStatus()).isEqualTo(200);
        // Desactivación dedicada: una sesión antigua pierde inmediatamente listado/PDF.
        acceso.gestionar(INST+1,accion(acceso,INST+1,"DESHABILITAR"));assertThat(mvc.perform(get("/personal/recibos/CERO/"+filaCero+"/pdf").session(sesionPersonal)).andReturn().getResponse().getStatus()).isEqualTo(403);
        assertThatThrownBy(()->detalles.loadUserByUsername("QA-RECIBOS\\sofia.recibos.qa")).isInstanceOf(org.springframework.security.authentication.DisabledException.class);
        // También reutilizar al habilitar cuenta docente DESPUÉS de la cuenta personal.
        var tercera=accion(acceso,INST+2,"HABILITAR");tercera.setUsername("tercera.recibos.qa");tercera.setEmail("tercera-recibos@example.invalid");var enlace3=acceso.gestionar(INST+2,tercera);activar.activar(new escuela.seguridad.dto.request.ActivacionUsuarioRequest(enlace3.token(),"Clave-Ficticia-Recibos-2030!"));long numeroUsuarios=j.queryForObject("SELECT count(*) FROM usuario",Long.class);
        j.update("INSERT INTO maestro(id,institucion_id,empleado_id,numero_empleado) VALUES(680020,680000,680002,'DOC-REC-POST-QA')");
        var compartida=app.getBean(escuela.docente.service.AccesoPortalMaestroService.class).crear(680020L,new escuela.docente.dto.PortalMaestroCuentaRequest("no.se.duplica","ignorado@example.invalid"));assertThat(j.queryForObject("SELECT count(*) FROM usuario",Long.class)).isEqualTo(numeroUsuarios);adminPortal();assertThat(compartida.usuarioId()).isEqualTo(acceso.obtener(INST+2).usuarioId());
        var administrador=sesion(auth(java.util.stream.Stream.concat(Arrays.stream(permisos),java.util.stream.Stream.of("EMPLEADO_ADMINISTRAR","MAESTRO_ADMINISTRAR")).toArray(String[]::new)));
        var cuenta=mvc.perform(get("/admin/empleados/680001/portal-recibos").session(administrador)).andReturn().getResponse();assertThat(cuenta.getStatus()).isEqualTo(200);assertThat(cuenta.getContentAsString()).contains("data-money-confirm=\"empleado-acceso-recibos\"");Files.writeString(dir.resolve("cuenta-personal.html"),cuenta.getContentAsString());
        assertThat(mvc.perform(post("/admin/empleados/680001/portal-recibos").session(administrador).param("accion","HABILITAR")).andReturn().getResponse().getStatus()).isEqualTo(403);
        adminPortal();assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isEqualTo(movimientos);assertThat(j.queryForObject("SELECT count(*) FROM usuario_rol",Long.class)).isEqualTo(roles);admin();
    }
}
