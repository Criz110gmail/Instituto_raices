package escuela.personal;

import escuela.SistemaAdministrativoEscolarApplication;
import escuela.personal.dto.*;import escuela.personal.service.*;import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;import org.springframework.security.core.authority.SimpleGrantedAuthority;import org.springframework.security.core.context.*;
import org.springframework.mock.web.MockHttpSession;import org.springframework.security.web.FilterChainProxy;import org.springframework.test.web.servlet.setup.MockMvcBuilders;import org.springframework.web.context.WebApplicationContext;
import java.util.*;import java.time.*;import java.math.BigDecimal;import java.io.*;import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/** Base desechable explícita; nunca utiliza .env ni registros operativos. */
@EnabledIfEnvironmentVariable(named="QA_SALARIOS_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_salarios_qa")
class OrganizacionLaboralPostgresIT {
 private UsernamePasswordAuthenticationToken auth(String... permisos){var a=Arrays.stream(permisos).map(SimpleGrantedAuthority::new).toList();return new UsernamePasswordAuthenticationToken(new UsuarioPrincipal(96000L,96000L,Set.of(),true,false,"qa.organizacion","",a),"",a);}
 private UsernamePasswordAuthenticationToken completo(){return auth("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","EMPLEADO_LABORAL_ADMINISTRAR","CONDICION_SALARIAL_ADMINISTRAR","GUIA_PROCESOS_CONSULTAR");}
 private void sesion(){SecurityContextHolder.getContext().setAuthentication(completo());}
 private ConfiguracionLaboralForm jornada(String codigo){var f=new ConfiguracionLaboralForm();f.setCodigo(codigo);f.setNombre("Jornada ficticia "+codigo);f.setHorasSemanales(new BigDecimal("30.00"));f.setDescripcion("Lunes a viernes de08:00 a14:00; sólo QA");return f;}
 private ConfiguracionLaboralForm esquema(OrganizacionLaboralService s,CondicionesSalarialesService salarios,Long puesto,Long jornada){var f=new ConfiguracionLaboralForm();f.setCodigo("ESQ-QA");f.setNombre("Docente mensual ficticio");f.setPuestoId(puesto);var p=salarios.puesto(puesto);f.setPuestoTexto(p.codigo()+" · "+p.nombre());f.setJornadaId(jornada);var j=s.obtener("jornadas",jornada);f.setJornadaTexto(j.codigo()+" · "+j.nombre());f.setImporteBruto(new BigDecimal("12000.00"));return f;}
 private AsignacionLaboralForm asignar(OrganizacionLaboralService s,Long esquema,LocalDate inicio){var c=s.contexto(96000L,esquema);var f=new AsignacionLaboralForm();f.setEmpleadoId(96000L);f.setEsquemaId(esquema);f.setVersionEsquema(c.versionEsquema());f.setVersionJornada(c.versionJornada());f.setVersionPuesto(c.versionPuesto());f.setVersionLaboral(c.versionLaboral());f.setRevision(c.revision());f.setInicio(inicio);f.setMotivo("Asignación ficticia para probar snapshot y vigencias");return f;}
 @Test void migracionesCatalogosAsignacionAtomicaSnapshotPermisosRenderExcelYGuia()throws Exception{
  var storage=java.nio.file.Files.createTempDirectory(java.nio.file.Path.of("target"),"qa-organizacion-").toAbsolutePath();
  try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_SALARIOS_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-no-usada","--spring.security.user.name=qa-recuperacion","--spring.security.user.password=qa-no-usada","--app.archivos.directorio="+storage)){
   var jdbc=app.getBean(JdbcTemplate.class);jdbc.update("insert into institucion(id,codigo,nombre,zona_horaria,moneda_predeterminada) values(96000,'QA-ORG','Institución ficticia organizacion','America/Mexico_City','MXN'),(96001,'QA-ORG-OTRA','Otra institución','America/Mexico_City','MXN')");
   jdbc.update("insert into usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) values(96000,96000,'qa.organizacion','qa.org@example.invalid','ADMINISTRATIVO','ACTIVO','hash-no-usado')");
   jdbc.update("insert into empleado(id,institucion_id,codigo,nombres,primer_apellido) values(96000,96000,'EMP-ORG-QA','Ana','Capacitación'),(96001,96001,'EMP-AJENO','Otro','Empleado')");
   var hoy=LocalDate.now(ZoneId.of("America/Mexico_City"));jdbc.update("insert into empleado_laboral(empleado_id,puesto,fecha_ingreso,situacion) values(96000,'Referencia previa',?,'ACTIVO'),(96001,NULL,NULL,'POR_CONFIRMAR')",hoy.minusDays(10));
   var s=app.getBean(OrganizacionLaboralService.class);var salarios=app.getBean(CondicionesSalarialesService.class);sesion();
   // Puesto legado sin horas/sueldo: conserva las asignaciones de plantilla previas.
   Long puesto=jdbc.queryForObject("INSERT INTO puesto_laboral(institucion_id,codigo,nombre,creado_por_id,actualizado_por_id) VALUES(96000,'DOC-ORG-QA','Docente ficticio',96000,96000) RETURNING id",Long.class);
   var j=jornada("MAT-QA");Long jornada=s.guardar("jornadas",null,j);j.setVersion(0L);var e=esquema(s,salarios,puesto,jornada);Long esquema=s.guardar("esquemas",null,e);e.setVersion(0L);
   assertThat(s.buscar("jornadas","ma")).isEmpty();assertThat(s.buscar("jornadas","")).hasSize(1);assertThat(s.buscarEmpleados("")).hasSize(1);assertThat(s.listar("esquemas","ESQ-QA","ACTIVO",0,10).getTotalElements()).isEqualTo(1);
   assertThatThrownBy(()->s.contexto(96001L,esquema)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
   j.setHorasSemanales(new BigDecimal("169"));assertThatThrownBy(()->s.guardar("jornadas",jornada,j)).hasMessageContaining("horas semanales");j.setHorasSemanales(new BigDecimal("30"));
   var primera=asignar(s,esquema,hoy.plusDays(1));Long inicial=s.asignar(primera);assertThat(s.asignacion(inicial).horas()).isEqualByComparingTo("30");assertThat(s.asignacion(inicial).importe()).isEqualByComparingTo("12000");
   var capturaVieja=asignar(s,esquema,hoy.plusDays(60));j.setHorasSemanales(new BigDecimal("25"));s.guardar("jornadas",jornada,j);j.setVersion(1L);
   assertThatThrownBy(()->s.asignar(capturaVieja)).hasMessageContaining("cambió");assertThat(s.asignacion(inicial).horas()).isEqualByComparingTo("30");
   e.setImporteBruto(new BigDecimal("13000"));s.guardar("esquemas",esquema,e);e.setVersion(1L);assertThat(s.asignacion(inicial).importe()).isEqualByComparingTo("12000");
   var segunda=asignar(s,esquema,hoy.plusDays(60));
   jdbc.execute("CREATE FUNCTION qa_falla_asignacion() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'Falla deliberada de snapshot'; END $$");jdbc.execute("CREATE TRIGGER qa_falla_asignacion BEFORE INSERT ON asignacion_esquema_laboral FOR EACH ROW EXECUTE FUNCTION qa_falla_asignacion()");
   long revision=salarios.revision(96000L);assertThatThrownBy(()->s.asignar(segunda)).isInstanceOf(RuntimeException.class);assertThat(salarios.revision(96000L)).isEqualTo(revision);assertThat(s.asignacion(inicial).fin()).isNull();jdbc.execute("DROP TRIGGER qa_falla_asignacion ON asignacion_esquema_laboral");
   var pool=Executors.newFixedThreadPool(2);var latch=new CountDownLatch(1);try{Callable<Boolean> tarea=()->{sesion();try{latch.await();s.asignar(segunda);return true;}catch(RuntimeException ex){return false;}finally{SecurityContextHolder.clearContext();}};var uno=pool.submit(tarea);var dos=pool.submit(tarea);latch.countDown();assertThat(List.of(uno.get(30,TimeUnit.SECONDS),dos.get(30,TimeUnit.SECONDS))).containsExactlyInAnyOrder(true,false);}finally{pool.shutdownNow();}
   sesion();var lista=s.asignaciones(96000L,"","TODAS",null,null,0,10);assertThat(lista.getTotalElements()).isEqualTo(2);var aumento=lista.getContent().getFirst();assertThat(aumento.importe()).isEqualByComparingTo("13000");assertThat(aumento.horas()).isEqualByComparingTo("25");assertThat(s.asignacion(inicial).fin()).isEqualTo(hoy.plusDays(59));
   var mvc=MockMvcBuilders.webAppContextSetup((WebApplicationContext)app).addFilters(app.getBean(FilterChainProxy.class)).build();var session=new MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(completo()));
   var dir=java.nio.file.Path.of("target/qa-organizacion");java.nio.file.Files.createDirectories(dir);
   for(String ruta:List.of("/admin/jornadas","/admin/esquemas","/admin/jornadas/nuevo","/admin/esquemas/nuevo","/admin/jornadas/"+jornada+"/editar","/admin/esquemas/"+esquema+"/editar","/admin/asignaciones","/admin/asignaciones/nuevo","/admin/asignaciones/"+aumento.id(),"/admin/empleados/96000")){
    var response=mvc.perform(get(ruta).session(session)).andReturn().getResponse();assertThat(response.getStatus()).as(ruta).isEqualTo(200);assertThat(response.getContentAsString()).doesNotContain("Whitelabel");java.nio.file.Files.writeString(dir.resolve(ruta.replace('/','_')+".html"),response.getContentAsString());
   }
   // Filtros independientes: referencias exactas, texto canónico, mismos filtros en Excel.
   sesion();var filtros=new FiltrosCatalogosPersonal();filtros.setFiltroPuestoId(puesto);filtros.setFiltroJornadaId(jornada);filtros.setFiltroEsquemaId(esquema);
   assertThat(s.asignaciones(96000L,"ESQ-QA","TODAS",null,null,0,10,filtros).getTotalElements()).isEqualTo(2);
   assertThat(s.listar("esquemas","","TODOS",0,10,filtros).getTotalElements()).isEqualTo(1);
   assertThat(salarios.puestos("","TODOS",0,10,puesto).getTotalElements()).isEqualTo(1);
   for(String tipo:List.of("puestos","jornadas","esquemas")){
    var url="/admin/personal/filtros/"+tipo+"/buscar";
    var respuesta=mvc.perform(get(url).session(session)).andReturn().getResponse();assertThat(respuesta.getStatus()).isEqualTo(200);
    assertThat(new tools.jackson.databind.ObjectMapper().readTree(respuesta.getContentAsString()).get("resultados").size()).isEqualTo(1);
    assertThat(respuesta.getHeader("Cache-Control")).contains("no-store");
    assertThat(new tools.jackson.databind.ObjectMapper().readTree(mvc.perform(get(url).session(session).param("q","xy")).andReturn().getResponse().getContentAsString()).get("resultados").size()).isZero();
    java.nio.file.Files.writeString(dir.resolve("filtros-"+tipo+".json"),respuesta.getContentAsString());
   }
   for(String ruta:List.of("/admin/puestos","/admin/jornadas","/admin/esquemas","/admin/asignaciones","/admin/esquemas/"+esquema+"/editar","/admin/asignaciones/"+inicial,"/admin/empleados/96000/condiciones")){
    var respuesta=mvc.perform(get(ruta).session(session).param("filtroPuestoId",puesto.toString()).param("filtroJornadaId",jornada.toString()).param("filtroEsquemaId",esquema.toString())).andReturn().getResponse();
    assertThat(respuesta.getStatus()).as(ruta).isEqualTo(200);
    assertThat(respuesta.getContentAsString()).contains(ruta.equals("/admin/jornadas")?"MAT-QA":"DOC-ORG-QA").doesNotContain("Whitelabel");
    java.nio.file.Files.writeString(dir.resolve(ruta.replace('/','_')+"-filtros.html"),respuesta.getContentAsString());
   }
   for(String tipo:List.of("puestos","jornadas","esquemas","asignaciones")){
    var respuesta=mvc.perform(get("/admin/"+tipo+"/excel").session(session).param("filtroPuestoId",puesto.toString()).param("filtroJornadaId",jornada.toString()).param("filtroEsquemaId",esquema.toString())).andReturn().getResponse();
    assertThat(respuesta.getStatus()).as(tipo).isEqualTo(200);
    try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new ByteArrayInputStream(respuesta.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(tipo.equals("asignaciones")?2:1);}
    var incompleta=mvc.perform(get("/admin/"+tipo).session(session).param("filtro"+(tipo.equals("puestos")?"Puesto":tipo.equals("jornadas")?"Jornada":"Esquema")+"Texto","Texto sin seleccionar")).andReturn().getResponse();
    assertThat(incompleta.getStatus()).isEqualTo(200);assertThat(incompleta.getContentAsString()).contains("de la lista de sugerencias");
   }
   var filtrada=mvc.perform(get("/admin/asignaciones").session(session).param("filtroEsquemaId",esquema.toString())).andReturn().getResponse().getContentAsString();
   assertThat(filtrada).contains("filtroEsquemaId="+esquema,"value=\"ESQ-QA · Docente mensual ficticio\"","/admin/personal/filtros/esquemas/buscar");
   // El límite se aplica en SQL, no después de cargar el catálogo entero.
   for(int n=0;n<12;n++)jdbc.update("insert into puesto_laboral(institucion_id,codigo,nombre,activo,creado_por_id,actualizado_por_id) values(96000,?,? ,false,96000,96000)","P-AUX-"+n,"Zeta puesto "+n);
   var diez=mvc.perform(get("/admin/personal/filtros/puestos/buscar").session(session)).andReturn().getResponse();
   var diezJson=new tools.jackson.databind.ObjectMapper().readTree(diez.getContentAsString());assertThat(diezJson.get("resultados").size()).isEqualTo(10);assertThat(diezJson.get("hayMas").asBoolean()).isTrue();
   sesion();assertThat(s.buscarFiltros("puestos","P-AUX-")).hasSize(11);assertThat(salarios.buscarPuestos("P-AUX-")).isEmpty();
   var sinSalario=new MockHttpSession();sinSalario.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth("EMPLEADO_LEER")));assertThat(mvc.perform(get("/admin/personal/filtros/esquemas/buscar").session(sinSalario)).andReturn().getResponse().getStatus()).isBetween(300,499);
   var nuevo=mvc.perform(get("/admin/jornadas/nuevo").session(session).param("q","MAT-QA").param("estado","ACTIVO").param("pagina","2").param("tamanio","10")).andReturn().getResponse().getContentAsString();assertThat(nuevo).contains("q=MAT-QA&amp;estado=ACTIVO&amp;pagina=2&amp;tamanio=10","data-money-confirm=\"laboral-catalogo\"").doesNotContain("<table");
   for(var entrada:Map.of("empleados","/admin/asignaciones/empleados/buscar","esquemas","/admin/esquemas/buscar","jornadas","/admin/jornadas/buscar","puestos","/admin/puestos/buscar","contexto","/admin/asignaciones/contexto?empleadoId=96000&esquemaId="+esquema).entrySet()){
    var response=mvc.perform(get(entrada.getValue()).session(session)).andReturn().getResponse();assertThat(response.getStatus()).isEqualTo(200);java.nio.file.Files.writeString(dir.resolve(entrada.getKey()+".json"),response.getContentAsString());
   }
   var guia=mvc.perform(get("/admin/guias/empleado-condiciones-salariales").session(session)).andReturn().getResponse();assertThat(guia.getStatus()).isEqualTo(200);assertThat(guia.getContentAsString()).contains("Cancelar únicamente el aumento futuro","DOC-PRI-TEST","Caso confirmado por el propietario");java.nio.file.Files.writeString(dir.resolve("guia.html"),guia.getContentAsString());
   var guiaExcel=mvc.perform(get("/admin/guias/excel").session(session).param("q","Puestos y sueldos: registrar, programar aumento y cancelar el futuro")).andReturn().getResponse();assertThat(guiaExcel.getStatus()).isEqualTo(200);try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new ByteArrayInputStream(guiaExcel.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(8);}
   var guiaAsignacion=mvc.perform(get("/admin/guias/asignaciones-laborales-configuracion-historica").session(session)).andReturn().getResponse();
   assertThat(guiaAsignacion.getStatus()).isEqualTo(200);assertThat(guiaAsignacion.getContentAsString()).contains("Puesto, jornada y sueldo: asignar","Cancelar únicamente la última programación futura","JOR-MAT-TEST");
   java.nio.file.Files.writeString(dir.resolve("guia-asignaciones.html"),guiaAsignacion.getContentAsString());
   assertThat(jdbc.queryForObject("select count(*) from guia_proceso_paso where guia_id=(select id from guia_proceso where slug='asignaciones-laborales-configuracion-historica')",Long.class)).isEqualTo(12);
   var excelGuiaAsignacion=mvc.perform(get("/admin/guias/excel").session(session).param("q","JOR-MAT-TEST").param("categoria","Personal")).andReturn().getResponse();
   assertThat(excelGuiaAsignacion.getStatus()).isEqualTo(200);try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new ByteArrayInputStream(excelGuiaAsignacion.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(12);}
   var guiaLimitada=new MockHttpSession();guiaLimitada.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth("GUIA_PROCESOS_CONSULTAR")));assertThat(mvc.perform(get("/admin/guias/asignaciones-laborales-configuracion-historica").session(guiaLimitada)).andReturn().getResponse().getStatus()).isBetween(300,499);
   var detalle=mvc.perform(get("/admin/asignaciones/"+inicial).session(session)).andReturn().getResponse().getContentAsString();assertThat(detalle).contains("MAT-QA","30.00","12,000.00","data-student-tabs");
   // Autocompletado público de identidad: límite10, mínimo3, alcance y selección por ID.
   for(int n=0;n<12;n++)jdbc.update("insert into empleado(id,institucion_id,codigo,nombres,primer_apellido) values(?,96000,?,?,'Zeta')",96100+n,"EMP-AUX-"+n,"Persona "+n);
   var ayuda=mvc.perform(get("/admin/empleados/buscar").session(session)).andReturn().getResponse();assertThat(ayuda.getStatus()).isEqualTo(200);var json=new tools.jackson.databind.ObjectMapper().readTree(ayuda.getContentAsString());assertThat(json.get("resultados").size()).isEqualTo(10);assertThat(json.get("hayMas").asBoolean()).isTrue();assertThat(ayuda.getContentAsString()).doesNotContain("EMP-AJENO","puesto","importeBruto");java.nio.file.Files.writeString(dir.resolve("empleados.json"),ayuda.getContentAsString());
   for(String endpoint:List.of("/admin/empleados/buscar","/admin/asignaciones/empleados/buscar")){
    var corta=mvc.perform(get(endpoint).session(session).param("q","An")).andReturn().getResponse();assertThat(new tools.jackson.databind.ObjectMapper().readTree(corta.getContentAsString()).get("resultados").size()).isZero();
    var encontrada=mvc.perform(get(endpoint).session(session).param("q","Ana")).andReturn().getResponse();assertThat(new tools.jackson.databind.ObjectMapper().readTree(encontrada.getContentAsString()).get("resultados").size()).isEqualTo(1);
   }
   String etiqueta="EMP-ORG-QA · Ana Capacitación";
   var filtrados=mvc.perform(get("/admin/empleados").session(session).param("empleadoId","96000").param("empleadoTexto",etiqueta)).andReturn().getResponse();assertThat(filtrados.getStatus()).isEqualTo(200);assertThat(filtrados.getContentAsString()).contains("value=\""+etiqueta+"\"","1 empleados","empleadoId=96000").doesNotContain("EMP-AUX-","EMP-AJENO");java.nio.file.Files.writeString(dir.resolve("empleados-filtro.html"),filtrados.getContentAsString());
   var filtradas=mvc.perform(get("/admin/asignaciones").session(session).param("empleadoId","96000").param("empleadoTexto",etiqueta)).andReturn().getResponse();assertThat(filtradas.getStatus()).isEqualTo(200);assertThat(filtradas.getContentAsString()).contains("value=\""+etiqueta+"\"","2 registros","/admin/asignaciones/empleados/buscar","filtro-empleado-resultados");
   for(String ruta:List.of("/admin/empleados","/admin/asignaciones")){var incompleta=mvc.perform(get(ruta).session(session).param("empleadoTexto","Ana sin seleccionar")).andReturn().getResponse();assertThat(incompleta.getStatus()).isEqualTo(200);assertThat(incompleta.getContentAsString()).contains("Selecciona un empleado de la lista").doesNotContain("Persona 1");}
   var empleadoExcel=mvc.perform(get("/admin/empleados/excel").session(session).param("empleadoId","96000").param("empleadoTexto",etiqueta)).andReturn().getResponse();assertThat(empleadoExcel.getStatus()).isEqualTo(200);try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new ByteArrayInputStream(empleadoExcel.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);assertThat(libro.getSheetAt(0).getRow(1).getCell(0).getStringCellValue()).isEqualTo("EMP-ORG-QA");}
   var general=new MockHttpSession();general.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth("EMPLEADO_LEER")));assertThat(mvc.perform(get("/admin/empleados/buscar").session(general)).andReturn().getResponse().getStatus()).isEqualTo(200);assertThat(mvc.perform(get("/admin/asignaciones/empleados/buscar").session(general)).andReturn().getResponse().getStatus()).isBetween(300,499);
   String retorno="/admin/empleados?empleadoId=96000&estado=ACTIVO&pagina=0&tamanio=10";var conRetorno=mvc.perform(get("/admin/empleados/96000").session(session).param("retorno",retorno)).andReturn().getResponse();assertThat(conRetorno.getStatus()).isEqualTo(200);assertThat(conRetorno.getContentAsString()).contains("href=\"/admin/empleados?empleadoId=96000&amp;estado=ACTIVO&amp;pagina=0&amp;tamanio=10\"","data-retorno-listado");
   var sinCsrf=mvc.perform(post("/admin/asignaciones").session(session)).andReturn().getResponse();assertThat(sinCsrf.getStatus()).isBetween(300,499);
   var export=mvc.perform(get("/admin/asignaciones/excel").session(session).param("q","EMP-ORG-QA").param("estado","PROXIMA").param("desde",hoy.plusDays(60).toString())).andReturn().getResponse();assertThat(export.getStatus()).isEqualTo(200);try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new ByteArrayInputStream(export.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);assertThat(libro.getSheetAt(0).getRow(1).getCell(6).getNumericCellValue()).isEqualTo(13000);}
   for(var permisos:List.of(new String[]{"EMPLEADO_LEER","EMPLEADO_LABORAL_LEER"},new String[]{"CONDICION_SALARIAL_LEER"})){
    var limitada=new MockHttpSession();limitada.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth(permisos)));for(String ruta:List.of("/admin/jornadas","/admin/esquemas/buscar","/admin/asignaciones/excel","/admin/asignaciones/"+inicial)){var response=mvc.perform(get(ruta).session(limitada)).andReturn().getResponse();assertThat(response.getStatus()).isBetween(300,499);}
   }
   var lectura=new MockHttpSession();lectura.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_LEER")));var soloLectura=mvc.perform(get("/admin/asignaciones/"+aumento.id()).session(lectura)).andReturn().getResponse();assertThat(soloLectura.getStatus()).isEqualTo(200);assertThat(soloLectura.getContentAsString()).doesNotContain("Revisar cancelación","Programar cambio");assertThat(mvc.perform(get("/admin/jornadas/nuevo").session(lectura)).andReturn().getResponse().getStatus()).isBetween(300,499);
   sesion();s.cancelar(aumento.id(),aumento.version(),"Cancelar programación ficticia");assertThat(s.asignacion(inicial).fin()).isNull();assertThat(s.asignacion(aumento.id()).estado()).isEqualTo("Cancelada");assertThat(salarios.aplicable(96000L,hoy.plusDays(70)).importeBruto()).isEqualByComparingTo("12000");
   assertThatThrownBy(()->jdbc.update("delete from asignacion_esquema_laboral where condicion_id=?",inicial)).isInstanceOf(org.springframework.dao.DataAccessException.class);
   assertThat(jdbc.queryForObject("select count(*) from guia_proceso_paso where guia_id=(select id from guia_proceso where slug='empleado-condiciones-salariales')",Long.class)).isEqualTo(8);
   assertThat(jdbc.queryForObject("select count(*) from movimiento_financiero",Long.class)).isZero();assertThat(jdbc.queryForObject("select puesto from empleado_laboral where empleado_id=96000",String.class)).isEqualTo("Referencia previa");
  }finally{SecurityContextHolder.clearContext();}
 }
}
