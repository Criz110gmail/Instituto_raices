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
        }finally{SecurityContextHolder.clearContext();}
    }
}
