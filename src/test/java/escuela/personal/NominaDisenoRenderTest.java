package escuela.personal;

import escuela.admin.dto.ModuloCatalogo;
import escuela.personal.dto.*;
import escuela.personal.service.PreparacionNominaService.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/** Sólo render con datos ficticios; no inicia aplicación ni consulta la base. */
class NominaDisenoRenderTest {
 static BigDecimal money(String x){return new BigDecimal(x);}
 @Controller static class Vista {
  @GetMapping("/qa/nomina/{vista}") String render(@PathVariable String vista,Model m){
   var filtro=new FiltroNomina("","TODOS",null,null,null,null,null,null,null,null,0,25).normalizado();
   var inicio=LocalDate.of(2027,7,1);var fin=LocalDate.of(2027,7,31);var uuid=UUID.fromString("00000000-0000-0000-0000-000000000001");
   m.addAttribute("filtro",filtro);m.addAttribute("usuarioSesion","Usuario ficticio");m.addAttribute("modulos",List.of(ModuloCatalogo.PREPARACION_NOMINA,ModuloCatalogo.PERIODOS_NOMINA));m.addAttribute("moduloActual",ModuloCatalogo.PREPARACION_NOMINA);m.addAttribute("puedeEditar",true);m.addAttribute("volverUrl","/admin/preparacion-nomina");m.addAttribute("resultado",Page.empty());
   if(vista.equals("nominas")){
    m.addAttribute("resultado",new PageImpl<>(List.of("BORRADOR","AUTORIZADA","PARCIALMENTE_PAGADA","PAGADA","APLICADA","CANCELADO").stream().map(estado->new Borrador(2L,1L,"Nómina ficticia",estado,1,0L,"Motivo","NOV-2026-TEST","Noviembre","Periodo independiente",inicio,fin,fin,"MXN","sello",1,money("12080"))).toList()));
    return "admin/nomina-listado";
   }
   if(List.of("detalle","revision").contains(vista)){
    m.addAttribute("puedeAplicar",false);
    m.addAttribute("borrador",new Borrador(9L,6L,"Actualización de Luis","BORRADOR",2,1L,"Cambio de prueba","DIC-2026-FONDOS-TEST","Diciembre","Periodo independiente",inicio,fin,fin,"MXN","sello",1,money("522.58")));
    m.addAttribute("consultaRevisionExplicita",vista.equals("revision"));m.addAttribute("revisionConsulta",vista.equals("revision")?1:2);m.addAttribute("cobertura",new Cobertura("Periodo independiente","DIC-2026-FONDOS-TEST","Diciembre",inicio,fin,fin,"MXN"));
    m.addAttribute("totales",new Totales(1,money("483.87"),money("38.71"),money("0"),money("0"),money("522.58")));m.addAttribute("guardados",Page.empty());m.addAttribute("historial",Page.empty());m.addAttribute("revisiones",new PageImpl<>(List.of(new Revision(2,"29/10/2026 16:29","qa","Cambio"),new Revision(1,"29/10/2026 16:18","qa","Registro"))));return "admin/nomina-detalle";
   }
   if(vista.startsWith("concepto-")){
    m.addAttribute("id",8L);m.addAttribute("empleado",new EmpleadoVista(8L,1L,"EMP-QA-LUIS","Luis","Prueba",null,null,null,true,false,0L,null,false,null,null));m.addAttribute("filtroNomina",new FiltroNominaEmpleado("transporte","TODAS","PERCEPCION","MENSUAL",inicio,fin,1,10));m.addAttribute("volverUrl","/admin/empleados/8/nomina?q=transporte&estado=TODAS&pagina=1&tamanio=10");m.addAttribute("puedeEditarNomina",true);m.addAttribute("hoyNomina",LocalDate.of(2026,10,30));
    var asignacion=new escuela.personal.service.ConceptoEmpleadoService.Asignacion(9L,8L,"Luis Prueba","EMP-QA-LUIS",4L,"TRANS-RED-TEST","Transporte de prueba","PERCEPCION",money("80"),"MXN",inicio,fin,"Próxima","Motivo ficticio","Registro ficticio",0L,null,null,true,true);
    if(vista.equals("concepto-detalle")){m.addAttribute("asignacion",asignacion);m.addAttribute("meta",Map.of("modelo","V2","frecuencia_importe","MENSUAL","modalidad","RECURRENTE"));m.addAttribute("historialConcepto",Page.empty());return "admin/empleado-nomina-detalle";}
    var f=new ConceptoEmpleadoForm();f.setEmpleadoId(8L);f.setEmpleadoTexto("EMP-QA-LUIS · Luis Prueba");f.setInicio(inicio);m.addAttribute("form",f);if(vista.equals("concepto-cambio")){m.addAttribute("anterior",asignacion);f.setConceptoId(4L);f.setConceptoTexto("TRANS-RED-TEST · Transporte de prueba");f.setImporte(money("80"));}return "admin/empleado-nomina-form";
   }
   if(vista.startsWith("configuracion")){
    m.addAttribute("id",6L);m.addAttribute("empleado",new EmpleadoVista(6L,1L,"EMP-QA","Diana","Prueba",null,null,null,true,false,0L,null,false,null,null));m.addAttribute("filtroNomina",FiltroNominaEmpleado.inicial());m.addAttribute("puedeEditarNomina",true);m.addAttribute("conceptosNomina",Page.empty());
    if(!vista.equals("configuracion-vacia"))m.addAttribute("filtroNomina",new FiltroNominaEmpleado("","TODAS","","",inicio,fin,0,25));
    if(vista.equals("configuracion-bloqueada"))m.addAttribute("simulacionNomina",new Simulacion(inicio,fin,"","MXN","Selecciona las fechas completas del periodo.",null,null,null,null));
    else if(!vista.equals("configuracion-vacia"))m.addAttribute("simulacionNomina",new Simulacion(inicio,fin,"JUL-2027-RED-TEST","MXN","Configuración calculada por fechas",money("14000"),money("630"),money("442.85"),money("14187.15")));
    return "admin/empleado-nomina";
   }
   if(vista.equals("periodo-detalle")){
    m.addAttribute("filtro",new FiltroProgramacionNomina("","TODOS","",null,"",null,"",null,null,0,25).normalizado(true));m.addAttribute("tipo","periodos-nomina");m.addAttribute("esPeriodo",true);m.addAttribute("titulo","Periodos de nómina");m.addAttribute("historial",Page.empty());m.addAttribute("tabInicial","gestion");
    m.addAttribute("registro",new escuela.personal.service.ProgramacionNominaService.Periodo(7L,null,"DIC-2026-FONDOS-TEST","Diciembre de prueba",null,null,null,null,LocalDate.of(2026,12,1),LocalDate.of(2026,12,15),null,LocalDate.of(2026,12,15),"PROGRAMADO","Registro de prueba","Registro de prueba",0L,"Próximo"));return "admin/programacion-nomina-detalle";
   }
   if(vista.equals("periodos")){m.addAttribute("filtro",new FiltroProgramacionNomina("","TODOS","",null,"",null,"",null,null,0,25).normalizado(true));m.addAttribute("tipo","periodos-nomina");m.addAttribute("esPeriodo",true);m.addAttribute("titulo","Periodos de nómina");m.addAttribute("moduloActual",ModuloCatalogo.PERIODOS_NOMINA);return "admin/programacion-nomina-listado";}
   if(vista.startsWith("directo-")){
    var pago=new PagoIndividualNominaForm();m.addAttribute("individualDirecto",true);m.addAttribute("formMultiple",pago);m.addAttribute("rutaMultiple","/admin/preparacion-nomina/pago-individual");m.addAttribute("tituloReferencia","Pago individual · un empleado y un periodo");m.addAttribute("cuentasEndpoint","/qa/cuentas");m.addAttribute("motivosEndpoint","/qa/motivos");
    if(!vista.equals("directo-vacio")){
     pago.setPeriodoId(9L);pago.setPeriodoTexto("SEP-2027-IND-TEST · Primera quincena de septiembre");pago.setEmpleadoId(6L);pago.setEmpleadoTexto("EMP-QA · Diana de prueba");m.addAttribute("totalSolicitado",money("6975.71"));
     m.addAttribute("empleadoIndividual",new Candidato(6L,"EMP-QA","Diana de prueba","LISTO","Configuración por vigencias",money("7000"),money("190"),money("214.29"),money("0"),money("6975.71"),"MXN","{}","sello"));
     var septiembre=LocalDate.of(2027,9,1);m.addAttribute("periodoIndividual",new escuela.personal.service.ProgramacionNominaService.Periodo(9L,null,"SEP-2027-IND-TEST","Primera quincena",null,null,null,null,septiembre,septiembre.plusDays(14),null,septiembre.plusDays(14),"PROGRAMADO","Prueba","Prueba",0L,"Próximo"));
     m.addAttribute("lineasIndividuales",List.of(new PartidaIndividual("APOYO_48","Apoyo técnico","SUELDO","MENSUAL",money("7000")),new PartidaIndividual("TRANS","Transporte mensual de prueba","PERCEPCION","MENSUAL",money("40")),new PartidaIndividual("QUIN","Apoyo quincenal de prueba","PERCEPCION","QUINCENAL",money("150")),new PartidaIndividual("DED","Deducción semanal de prueba","DEDUCCION","SEMANAL",money("214.29"))));
    }
    return "admin/nomina-pago-multiple";
   }
   if(vista.equals("intento-cancelado")){
    m.addAttribute("borrador",new Borrador(2L,1L,"Julio Diana","AUTORIZADA",1,0L,"Motivo","JUL-2027-RED-TEST","Julio","Periodo independiente",inicio,fin,fin,"MXN","sello",1,money("14187.15")));
    m.addAttribute("empleadoPago",new escuela.personal.service.PagoEmpleadoNominaService.Fila(2L,6L,"EMP-QA","Diana de prueba",money("14187.15"),"MXN","PENDIENTE",null));
    m.addAttribute("autorizada",true);m.addAttribute("puedePagar",true);m.addAttribute("puedeRevertir",true);m.addAttribute("puedeEditar",true);m.addAttribute("pagoTab","historial");m.addAttribute("pagoFiltro",new FiltroPagoNomina("","TODOS",0,25).normalizado());m.addAttribute("rutaPagos","/admin/preparacion-nomina/2/pagos");m.addAttribute("formPago",new PagoEmpleadoNominaForm());m.addAttribute("cuotas",List.of());
    m.addAttribute("intentos",new PageImpl<>(List.of(new escuela.personal.service.PagoEmpleadoNominaService.Intento(1L,"REVERTIDO",money("14187.15"),"MXN",47L,"NOM-RED-JUL-2027","Pago original de prueba","28/10/2026 11:32","qa",49L,"RECUP-NOM-RED-JUL-01","Recuperación de banco y caja de prueba","28/10/2026 15:32","qa"))));
    m.addAttribute("fuentesPago",List.of(Map.of("pago",1L,"cuenta","BBVA-QA · Banco ficticio","medio","TRANSFERENCIA","importe",money("10000"),"movimiento",47L,"compensacion",49L),Map.of("pago",1L,"cuenta","CAJA-QA · Caja ficticia","medio","EFECTIVO","importe",money("4187.15"),"movimiento",48L,"compensacion",50L)));
    return "admin/nomina-pago-empleado";
   }
   var fila=new FilaPreparacion(6L,"EMP-QA","Diana de prueba","LISTO","Configuración por vigencias; cálculo proporcional por días reales.",!vista.equals("previa-vacia"),money("14000"),money("630"),money("442.85"),money("0"),money("14187.15"));
   if(List.of("pago","pago-nueva-fuente","pago-revision","pagos","pagos-pagados","pagos-lectura","pagos-filtrados","pagos-error").contains(vista)){
    boolean pagada=List.of("pagos-pagados","pagos-lectura","pagos-error").contains(vista);
    m.addAttribute("borrador",new Borrador(2L,1L,"Julio Diana",pagada?"PAGADA":"BORRADOR",1,0L,"Motivo","JUL-2027-RED-TEST","Julio","Periodo independiente",inicio,fin,fin,"MXN","sello",1,fila.propuesto()));
    if(vista.startsWith("pagos")){
     m.addAttribute("rutaPagos","/admin/preparacion-nomina/2/pagos");m.addAttribute("pagoFiltro",new FiltroPagoNomina("","TODOS",0,25).normalizado());m.addAttribute("puedePagar",true);m.addAttribute("puedeRevertir",false);m.addAttribute("autorizada",false);
     m.addAttribute("resumenPagos",new escuela.personal.service.PagoEmpleadoNominaService.Resumen(1,pagada?1:0,fila.propuesto(),pagada?fila.propuesto():money("0")));m.addAttribute("resumenDecisiones",new escuela.personal.service.TratamientoNominaService.ResumenLote(fila.propuesto(),money("0"),money("0")));
     m.addAttribute("autorizada",pagada);m.addAttribute("puedeRevertir",pagada&&!vista.equals("pagos-lectura"));
     if(vista.equals("pagos-lectura")){m.addAttribute("puedeEditar",false);m.addAttribute("puedePagar",false);}
     if(vista.equals("pagos-filtrados"))m.addAttribute("pagoFiltro",new FiltroPagoNomina("Diana","PENDIENTE",0,25).normalizado());
     if(vista.equals("pagos-error")){m.addAttribute("pagosTab","cancelacion");m.addAttribute("errorOperacion","Motivo de prueba inválido; no se movió dinero.");}
     m.addAttribute("cancelacionForm",new PagoEmpleadoNominaForm());m.addAttribute("cuentasCancelar",List.of(new escuela.personal.service.PagoEmpleadoNominaService.CuentaCancelacion("BBVA-QA",money("10000")),new escuela.personal.service.PagoEmpleadoNominaService.CuentaCancelacion("CAJA-QA",money("4187.15"))));
     m.addAttribute("resultado",new PageImpl<>(List.of(new escuela.personal.service.PagoEmpleadoNominaService.Fila(2L,6L,"EMP-QA","Diana de prueba",fila.propuesto(),"MXN",pagada?"PAGADO":"PENDIENTE",pagada?1L:null))));return "admin/nomina-pagos";
    }
    var pago=new PagoMultipleNominaForm();pago.setVersion(0L);pago.setMotivoPagoId(1L);pago.setMotivoPagoTexto("NOMINA · Pago de nómina");pago.setReferencia("NOM-RED-JUL-2027");pago.setMotivo("Pago íntegro de Diana desde banco y caja");
   pago.agregarFuente();
    pago.getFuentes().get(0).setCuentaId(1L);pago.getFuentes().get(0).setCuentaTexto("BBVA-QA · Banco ficticio");pago.getFuentes().get(0).setImporte(money("10000"));pago.getFuentes().get(1).setCuentaId(2L);pago.getFuentes().get(1).setCuentaTexto("CAJA-QA · Caja ficticia");pago.getFuentes().get(1).setMedio("EFECTIVO");pago.getFuentes().get(1).setImporte(money("4187.15"));
    m.addAttribute("formMultiple",pago);m.addAttribute("rutaMultiple","/admin/preparacion-nomina/2/pago-multiple");m.addAttribute("tituloReferencia","BOR-000002 · JUL-2027-RED-TEST");m.addAttribute("totalSolicitado",fila.propuesto());m.addAttribute("cuentasEndpoint","/qa/cuentas");m.addAttribute("motivosEndpoint","/qa/motivos");
    if(vista.equals("pago-nueva-fuente")){m.addAttribute("fuenteNueva",1);var nueva=pago.getFuentes().get(1);nueva.setCuentaId(null);nueva.setCuentaTexto("");nueva.setImporte(null);nueva.setMedio(null);}
    if(vista.equals("pago-revision"))m.addAttribute("vistaMultiple",new escuela.personal.service.PagoMultipleNominaService.Vista(0L,List.of(new escuela.personal.service.PagoMultipleNominaService.Fuente(1L,"BBVA-QA · Banco ficticio","TRANSFERENCIA",money("10000"),money("33330")),new escuela.personal.service.PagoMultipleNominaService.Fuente(2L,"CAJA-QA · Caja ficticia","EFECTIVO",money("4187.15"),money("16950"))),1,fila.propuesto(),"sello"));
    return "admin/nomina-pago-multiple";
   }
   m.addAttribute("empleado",fila);m.addAttribute("lote",new Preparacion(uuid,1L,null,null,"INDIVIDUAL","ABIERTA",0L,"JUL-2027-RED-TEST","Julio2027","Periodo independiente",inicio,fin,fin,"MXN","Ensayo visual","Motivo ficticio","sello","27/10/2026 20:00"));m.addAttribute("volverLote","/admin/preparacion-nomina/vista/"+uuid);
   if(vista.equals("actualizacion")){m.addAttribute("lote",new Preparacion(uuid,1L,9L,1L,"INDIVIDUAL","ABIERTA",0L,"JUL-2027-RED-TEST","Julio2027","Periodo independiente",inicio,fin,fin,"MXN","Ensayo visual","Motivo ficticio","sello","27/10/2026 20:00"));m.addAttribute("totalesAnteriores",new Totales(1,fila.sueldo(),money("0"),fila.deducciones(),fila.aportaciones(),money("13557.15")));}
   m.addAttribute("lineas",List.of(new Linea("APOYO_48","Apoyo técnico","SUELDO",money("14000"),"2027-06-16",null),new Linea("TRANS","Transporte","PERCEPCION",money("80"),"2027-07-01",null),new Linea("QUIN","Apoyo quincenal","PERCEPCION",money("300"),"2027-06-01",null),new Linea("EXTRA","Apoyo único","PERCEPCION",money("250"),"2027-07-01","2027-07-31"),new Linea("DED","Deducción semanal","DEDUCCION",money("442.85"),"2027-06-01",null)));
   if(vista.equals("desglose"))return "admin/nomina-periodo-empleado";
   if(List.of("tratamientos","decision","pendientes").contains(vista)){
    m.addAttribute("borrador",new Borrador(1L,1L,"Prueba","BORRADOR",1,0L,"Motivo","JUL-2027-RED-TEST","Julio","Periodo independiente",inicio,fin,fin,"MXN","sello",1,fila.propuesto()));
    m.addAttribute("empleado",new Guardado(1L,6L,"EMP-QA","Diana de prueba",fila.sueldo(),fila.percepciones(),fila.deducciones(),fila.aportaciones(),fila.propuesto(),"MXN","sello",1));
    m.addAttribute("lineasTratamiento",List.of(new escuela.personal.service.TratamientoNominaService.Linea("conceptos:1","TRANS","Transporte","PERCEPCION",money("80"),"POSPONER","Pagar después"),new escuela.personal.service.TratamientoNominaService.Linea("sueldos:1","APOYO","Apoyo técnico","SUELDO",money("14000"),"APLICAR","")));
    m.addAttribute("tratamientoResumen",new escuela.personal.service.TratamientoNominaService.Resumen(money("14107.15"),money("80"),money("0"),"APLICAR"));
    m.addAttribute("eventos",new PageImpl<>(List.of(new escuela.personal.service.TratamientoNominaService.Evento("27/10/2026 18:00","qa","conceptos:1","POSPONER","Pagar después"))));
    m.addAttribute("rutaTratamiento","/admin/preparacion-nomina/1/empleados/1/tratamientos");m.addAttribute("puedeDecidir",true);m.addAttribute("versionCapturada",0L);m.addAttribute("claveCapturada","conceptos:1");m.addAttribute("accionCapturada","POSPONER");m.addAttribute("motivoCapturado","Pagar después");m.addAttribute("revisado",true);m.addAttribute("netoProyectado",money("14107.15"));m.addAttribute("selloDecision","qa");
    if(vista.equals("decision"))return "admin/nomina-tratamiento-form";
    if(vista.equals("tratamientos"))return "admin/nomina-tratamientos";
    m.addAttribute("buscarPendiente","");m.addAttribute("estadoPendiente","PENDIENTE");m.addAttribute("pendienteFiltro",new FiltroPendienteNomina("","PENDIENTE",0,6L,"EMP-QA · Diana de prueba").normalizado());m.addAttribute("rutaPendientes","/admin/preparacion-nomina/1/pendientes");m.addAttribute("puedePagar",true);
    m.addAttribute("resultado",new PageImpl<>(List.of(new escuela.personal.service.TratamientoNominaService.Pendiente(1L,1L,1L,"EMP-QA","Diana de prueba","JUL-2027-RED-TEST",money("80"),"MXN","PENDIENTE",null))));return "admin/nomina-pendientes";
   }
   if(vista.equals("historico")){m.addAttribute("empleado",new Guardado(1L,6L,"EMP-QA","Diana de prueba",fila.sueldo(),fila.percepciones(),fila.deducciones(),fila.aportaciones(),fila.propuesto(),"MXN","sello",1));m.addAttribute("borrador",new Borrador(1L,1L,"Prueba","BORRADOR",1,0L,"Motivo","JUL-2027-RED-TEST","Julio","Periodo independiente",inicio,fin,fin,"MXN","sello",1,fila.propuesto()));return "admin/nomina-empleado";}
   m.addAttribute("resultado",new PageImpl<>(List.of(fila)));m.addAttribute("consulta",new FiltroPreparacionNomina("","LISTO",0,null,null).normalizado());m.addAttribute("rutaLote","/admin/preparacion-nomina/vista/"+uuid);m.addAttribute("conteos",Map.of("INCLUIDOS",fila.seleccionado()?1L:0L,"EXCLUIDOS",fila.seleccionado()?0L:1L,"REVISION",0L,"NO_INCLUIDO",0L));m.addAttribute("totales",fila.seleccionado()?new Totales(1,fila.sueldo(),fila.percepciones(),fila.deducciones(),fila.aportaciones(),fila.propuesto()):new Totales(0,money("0"),money("0"),money("0"),money("0"),money("0")));return "admin/nomina-periodo-previa";
  }
 }
 @Test void filtrosUniformesYDesglosesCompartenResumenSinModificarImportes()throws Exception{
  var r=new ClassLoaderTemplateResolver();r.setPrefix("templates/");r.setSuffix(".html");r.setCharacterEncoding("UTF-8");var engine=new SpringTemplateEngine();engine.setTemplateResolver(r);var resolver=new ThymeleafViewResolver();resolver.setTemplateEngine(engine);resolver.setCharacterEncoding("UTF-8");
  var mvc=MockMvcBuilders.standaloneSetup(new Vista()).setViewResolvers(resolver).build();var dir=Files.createDirectories(Path.of("target/qa-nomina-alineacion"));
  for(String vista:List.of("concepto-detalle","concepto-alta","concepto-cambio","detalle","revision","actualizacion","nominas","periodos","periodo-detalle","previa","previa-vacia","desglose","historico","tratamientos","decision","pendientes","pagos","pagos-pagados","pagos-lectura","pagos-filtrados","pagos-error","pago","pago-nueva-fuente","pago-revision","directo-vacio","directo-calculado","intento-cancelado","configuracion","configuracion-vacia","configuracion-bloqueada")){
   var request=get("/qa/nomina/"+vista);if(vista.equals("revision"))request.param("revisionConsulta","1");
   var response=mvc.perform(request).andReturn().getResponse();assertThat(response.getStatus()).isEqualTo(200);var html=response.getContentAsString();
   if(vista.startsWith("concepto-"))assertThat(html).contains("Ubicación en configuración de nómina","Personal y nómina · Empleados","Empleado: <span>Luis Prueba</span>","Sección actual:","← Volver a configuración","q=transporte&amp;estado=TODAS&amp;pagina=1&amp;tamanio=10").doesNotContain("← Volver al listado");
   if(vista.equals("concepto-detalle"))assertThat(html).contains("Detalle del concepto","<span aria-hidden=\"true\">←</span> Volver a configuración","data-money-confirm=\"nomina-cancelar\"");
   if(vista.equals("concepto-alta"))assertThat(html).contains("Agregar concepto","data-money-confirm=\"nomina-configurar\"");
   if(vista.equals("concepto-cambio"))assertThat(html).contains("Programar cambio de concepto","Concepto que cambiará").doesNotContain("Ubicación en nómina");
   if(vista.equals("revision"))assertThat(html).contains("Volver al historial del borrador","/admin/preparacion-nomina/9?q=&amp;estado=TODOS&amp;pagina=0&amp;tamanio=25#historial");
   if(vista.equals("detalle"))assertThat(html).contains("← Volver al listado").doesNotContain("Volver al historial del borrador");
   if(List.of("detalle","revision").contains(vista))assertThat(html).contains("class=\"nomina-primary-action\">Revisar actualización →</button>");
   if(List.of("previa","actualizacion").contains(vista))assertThat(html).containsPattern("<a class=\"nomina-primary-action\"[^>]*>Exportar consulta a Excel</a>");
   if(vista.equals("actualizacion"))assertThat(html).contains("Volver al borrador sin actualizar","#gestion");
   if(vista.equals("historico"))assertThat(html).contains("revisionConsulta=1#empleados","Volver al borrador");
   if(List.of("desglose","historico").contains(vista))assertThat(html).contains("nomina-net-summary","nomina-net-amount","$14,187.15","$14,000.00","+ $630.00","− $442.85");
   else if(List.of("nominas","periodos","previa","pendientes").contains(vista)) {assertThat(html).contains("payroll-aligned-filters");assertThat(html).containsPattern("<a class=\"module-action\"[^>]*>Limpiar</a>");}
   if(vista.equals("historico"))assertThat(html).contains("finance-shell nomina-detail").containsPattern("<a class=\"nomina-primary-action\"[^>]*>Decisiones y pendientes de este periodo</a>");
   if(vista.equals("tratamientos"))assertThat(html).containsPattern("<a class=\"nomina-primary-action\"[^>]*>Decidir sobre todo el empleado</a>").containsPattern("<a class=\"nomina-primary-action\"[^>]*>Exportar decisiones a Excel</a>");
   if(vista.equals("tratamientos"))assertThat(html).contains("nomina-treatment-decision","Pagar después").containsPattern("<div class=\"nomina-treatment-decision\"><span[^>]*>Posponer</span><p class=\"field-help\">Pagar después</p></div>");
   if(vista.equals("pagos"))assertThat(html).containsPattern("<a class=\"nomina-primary-action\"[^>]*data-open-student-tab=\"empleados\"[^>]*>Ver pagos por empleado");
   if(vista.equals("pendientes"))assertThat(html).contains("nomina-pending-filters","/pendientes/empleados/buscar","name=\"empleadoPendienteId\"","name=\"empleadoPendienteTexto\"","data-refresh-on-focus=\"true\"","empleadoPendienteId=6");
   if(vista.equals("decision"))assertThat(html).contains("<select name=\"clave\" required>","value=\"conceptos:1\" selected=\"selected\"","data-money-confirm=\"nomina-tratamiento\"");
   if(vista.equals("nominas"))assertThat(html).contains("nomina-list-state","data-payroll-state=\"BORRADOR\"","data-payroll-state=\"AUTORIZADA\"","data-payroll-state=\"PARCIALMENTE_PAGADA\"","data-payroll-state=\"PAGADA\"","data-payroll-state=\"APLICADA\"","data-payroll-state=\"CANCELADO\"","Pendiente de pago","Aplicada con pendientes","Aplicada · sin pendientes");
   if(vista.equals("periodo-detalle"))assertThat(html).contains("/css/nomina.css","nomina-period-edit","finance-shell nomina-detail").containsPattern("<a class=\"nomina-primary-action\"[^>]*>Editar →</a>");
   if(vista.equals("previa-vacia"))assertThat(html).contains("No has incluido ningún empleado en el borrador","Ir a seleccionar empleados","data-nomina-empty-warning","role=\"alert\"","data-confirm-people=\"0\"").doesNotContain("disabled=\"disabled\"").containsPattern("<a class=\"nomina-primary-action\"[^>]*>Ir a seleccionar empleados</a>");
   if(vista.equals("previa"))assertThat(html).doesNotContain("data-nomina-empty-warning").contains("data-money-confirm=\"nomina-borrador\"","data-confirm-amount=\"14187.15\"","id=\"nomina-empleado-6\"");
   if(vista.equals("pagos"))assertThat(html).contains("nomina-collective-entry","class=\"nomina-primary-action\"","Preparar pago colectivo","nomina-decisions-heading","nomina-decisions-form").containsPattern("<a class=\"nomina-primary-action\"[^>]*>Ver conceptos pospuestos").contains("<button class=\"nomina-primary-action\">Confirmar decisiones sin mover dinero</button>","data-money-confirm=\"nomina-congelar\"");
   if(vista.equals("pago")||vista.equals("pago-revision"))assertThat(html).contains("form-card employee-card","BBVA-QA","CAJA-QA","fuentes[0].importe","fuentes[1].medio","motivoPagoTexto","referencia");
   if(vista.equals("pago"))assertThat(html).contains("nomina-source-grid","nomina-payment-source","Revisar distribución").doesNotContain("data-payment-review-source","Editar distribución","data-new-source");
   if(vista.equals("pago-nueva-fuente"))assertThat(html).contains("id=\"nomina-fuente-1\" data-new-source=\"true\"","BBVA-QA","fuentes[0].importe","value=\"10000\"","NOM-RED-JUL-2027").doesNotContain("CAJA-QA");
   if(vista.startsWith("directo-"))assertThat(html).contains("nomina-direct-selection","nomina-direct-query","Selecciona el periodo y el empleado","name=\"periodoId\"","name=\"empleadoId\"","Consultar cálculo","data-refresh-on-focus=\"true\"");
   if(vista.equals("directo-vacio"))assertThat(html).doesNotContain("nomina-payment-source","Total a pagar");
   if(vista.equals("directo-calculado"))assertThat(html).contains("$6,975.71","SEP-2027-IND-TEST","EMP-QA · Diana de prueba","nomina-payment-source","nomina-individual-breakdown","Sueldo base proporcional","$7,000.00","$40.00","$150.00","− $214.29","Total de percepciones","$7,190.00","Total de deducciones","01/09/2027","15/09/2027");
   if(vista.equals("pago-revision"))assertThat(html).contains("Sólo revisión","nomina-payment-review","Editar distribución","data-payment-review-source","/editar","data-money-confirm=\"nomina-multifuente\"").doesNotContain("nomina-source-grid","autocomplete-input","Agregar otra cuenta","Quitar cuenta","Volver a revisar");
   if(vista.startsWith("pagos"))assertThat(html).contains("data-student-tabs","data-student-panel=\"resumen\"","data-student-panel=\"empleados\"","data-student-panel=\"decisiones\"","#empleados","Pagos por empleado","data-refresh-on-focus=\"true\"","name=\"empleadoPagoId\"","name=\"empleadoPagoTexto\"","/pagos/empleados/buscar","data-nomina-payments-filter","data-nomina-payments-table");
   if(vista.equals("pagos-pagados"))assertThat(html).contains("data-student-panel=\"cancelacion\"","nomina-cancelar-todos").doesNotContain("Preparar pago colectivo");
   if(vista.equals("pagos-lectura"))assertThat(html).doesNotContain("data-student-tab=\"cancelacion\"","Confirmar decisiones sin mover dinero","Preparar pago colectivo");
   if(vista.equals("pagos-filtrados"))assertThat(html).contains("data-active-tab=\"empleados\"","value=\"Diana\"");
   if(vista.equals("pagos-error"))assertThat(html).contains("data-force-active-tab=\"cancelacion\"","Motivo de prueba inválido");
   if(vista.equals("intento-cancelado"))assertThat(html).contains("Pago cancelado","nomina-attempt-cancelled","Recuperación del pago cancelado","Recuperación #49","Recuperación #50","Ver pago e intentos","nomina-location","nomina-individual-entry","class=\"nomina-primary-action\"","Pagar desde una o varias cuentas").doesNotContain(">REVERTIDO<");
   if(List.of("pagos","pago","previa","desglose","historico","tratamientos","decision","pendientes").contains(vista))assertThat(html).contains("nomina-location","Sección actual:");
   if(vista.startsWith("configuracion"))assertThat(html).contains("Simulador de nómina del empleado","no reserva pagos","no cambian esta estimación","data-student-tabs","data-student-panel=\"conceptos\"","data-student-panel=\"simulador\"","data-student-panel=\"sueldo\"","Simular nómina","nomina-config-grid").doesNotContain("Totales de los conceptos filtrados");
   if(vista.equals("configuracion"))assertThat(html).contains("$14,000.00","$630.00","$14,630.00","$442.85","$14,187.15","Total estimado a pagar","01/07/2027","31/07/2027");
   if(vista.equals("configuracion-vacia"))assertThat(html).contains("para simular cuánto correspondería pagar").doesNotContain("Total estimado a pagar");
   if(vista.equals("configuracion-bloqueada"))assertThat(html).contains("No es posible calcular este rango","Selecciona las fechas completas").doesNotContain("Total estimado a pagar");
   if(vista.equals("pago"))assertThat(html).contains("nomina-source-medium","readonly","Medio de pago · automático").doesNotContain("<select id=\"fuentes0.medio");
   assertThat(html).doesNotContain("undefined");Files.writeString(dir.resolve(vista+".html"),html);
  }
 }
}
