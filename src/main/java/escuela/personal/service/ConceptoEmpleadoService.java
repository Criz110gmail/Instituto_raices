package escuela.personal.service;

import escuela.personal.dto.*;
import escuela.admin.dto.OpcionAutocompletado;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ConceptoEmpleadoService {
 private final EmpleadoService empleados;
 private final ConceptoNominaService catalogo;
 private final NamedParameterJdbcTemplate jdbc;
 public record Asignacion(Long id,Long empleadoId,String empleado,String codigoEmpleado,Long conceptoId,
  String codigo,String nombre,String naturaleza,BigDecimal importe,String moneda,LocalDate inicio,LocalDate fin,
  String estado,String motivo,String motivoOperacion,Long version,Long anteriorId,LocalDate finAnterior,boolean cancelable,boolean modificable) {
  public String tipoTexto(){return ConceptoNominaService.etiqueta(naturaleza);}
  public String efecto(){return efectoDe(naturaleza);}
 }
 public record Contexto(Long conceptoVersion,Long laboralVersion,String moneda,String tipo,String efecto,String concepto,String empleado){}
 public record Historia(Long id,String fecha,String usuario,String operacion,String motivo){}
 private static final String SELECT="SELECT a.*, e.codigo codigo_empleado,e.nombres||' '||e.primer_apellido||coalesce(' '||e.segundo_apellido,'') nombre_empleado ";
 private static final String FROM=" FROM concepto_empleado a JOIN empleado e ON e.id=a.empleado_id AND e.institucion_id=a.institucion_id ";
 private MapSqlParameterSource parametros(boolean escritura){var p=empleados.autorizarSalarios(escritura);return new MapSqlParameterSource("institucion",p.institucionId()).addValue("actor",p.usuarioId()).addValue("hoy",hoy());}
 public LocalDate hoy(){var p=empleados.autorizarSalarios(false);String zona=jdbc.queryForObject("SELECT zona_horaria FROM institucion WHERE id=:id",new MapSqlParameterSource("id",p.institucionId()),String.class);return LocalDate.now(ZoneId.of(zona));}
 private RowMapper<Asignacion> mapper(LocalDate hoy){return (r,n)->{
  if("V2".equals(r.getString("modelo")))empleados.autorizarNomina(false);
  var inicio=r.getObject("fecha_inicio",LocalDate.class);var fin=r.getObject("fecha_fin",LocalDate.class);boolean activa="ACTIVA".equals(r.getString("estado"));
  String estado=!activa?"Cancelada":inicio.isAfter(hoy)?"Próxima":fin!=null&&fin.isBefore(hoy)?"Anterior":"Vigente";
  return new Asignacion(r.getLong("id"),r.getLong("empleado_id"),r.getString("nombre_empleado"),r.getString("codigo_empleado"),r.getLong("concepto_id"),r.getString("concepto_codigo"),r.getString("concepto_nombre"),r.getString("naturaleza"),r.getBigDecimal("importe"),r.getString("moneda"),inicio,fin,estado,r.getString("motivo"),r.getString("motivo_operacion"),r.getLong("version"),r.getObject("anterior_id",Long.class),r.getObject("fin_anterior",LocalDate.class),activa&&inicio.isAfter(hoy),activa&&(fin==null||!fin.isBefore(hoy)));
 };}
 public Asignacion obtener(Long id){var p=parametros(false).addValue("id",id);return jdbc.query(SELECT+FROM+" WHERE a.institucion_id=:institucion AND a.id=:id",p,mapper((LocalDate)p.getValue("hoy"))).stream().findFirst().orElseThrow(()->new AccessDeniedException("Concepto por empleado no autorizado"));}
 public Contexto contexto(Long empleadoId,Long conceptoId){
  return contexto(empleadoId,conceptoId,false);
 }
 public Contexto contextoConfiguracion(Long empleadoId,Long conceptoId){empleados.autorizarNomina(true);return contexto(empleadoId,conceptoId,true);}
 private Contexto contexto(Long empleadoId,Long conceptoId,boolean configuracion){
  var p=parametros(false).addValue("empleado",empleadoId);empleados.autorizar(empleadoId,true,false);var c=catalogo.obtener(conceptoId);
  var filas=jdbc.queryForList("SELECT e.activo,l.situacion,l.fecha_ingreso,l.version laboral_version,i.moneda_predeterminada,e.codigo||' · '||e.nombres||' '||e.primer_apellido||coalesce(' '||e.segundo_apellido,'') etiqueta FROM empleado e JOIN empleado_laboral l ON l.empleado_id=e.id JOIN institucion i ON i.id=e.institucion_id WHERE e.id=:empleado AND e.institucion_id=:institucion",p);
  if(filas.isEmpty())throw regla("Confirma primero los datos laborales del empleado.");var datos=filas.getFirst();
  if(!Boolean.TRUE.equals(datos.get("activo"))||!"ACTIVO".equals(datos.get("situacion"))||datos.get("fecha_ingreso")==null)throw regla("Confirma actividad y fecha de ingreso en Datos laborales del empleado antes de asignar conceptos.");
  if(configuracion){
   if("CON_SALDO".equals(c.aplicacion()))throw regla("Los préstamos se administran desde su módulo; no agregues otra deducción del mismo saldo.");
   if(!c.activo()||!Set.of("PERCEPCION","DEDUCCION").contains(c.naturaleza()))throw regla("Selecciona una percepción o deducción activa. Las aportaciones patronales históricas se conservan por separado.");
   if(esSueldoBase(c.codigo(),c.nombre()))throw regla("El sueldo base se obtiene del puesto asignado; no se puede agregar otra vez como concepto adicional.");
  }else if(!c.activo()||!"HABITUAL".equals(c.aplicacion()))throw regla("Selecciona un concepto activo y habitual. Los ocasionales se capturarán al preparar una nómina específica.");
  return new Contexto(c.version(),((Number)datos.get("laboral_version")).longValue(),(String)datos.get("moneda_predeterminada"),c.tipoTexto(),c.efecto(),c.codigo()+" · "+c.nombre(),(String)datos.get("etiqueta"));
 }
 public Page<Asignacion> vigentesEnFecha(Long empleadoId,LocalDate fecha,int pagina){
  empleados.autorizarSalarios(false);empleados.autorizar(empleadoId,true,false);
  var p=parametros(false).addValue("empleado",empleadoId).addValue("fecha",fecha);
  String where=" WHERE a.institucion_id=:institucion AND a.empleado_id=:empleado AND a.modelo='LEGADO' AND a.estado='ACTIVA' AND a.fecha_inicio<=:fecha AND (a.fecha_fin IS NULL OR a.fecha_fin>=:fecha)";
  Long n=jdbc.queryForObject("SELECT count(*)"+FROM+where,p,Long.class);var pg=PageRequest.of(Math.max(0,pagina),25);p.addValue("offset",pg.getOffset());
  return new PageImpl<>(jdbc.query(SELECT+FROM+where+" ORDER BY a.id LIMIT 25 OFFSET :offset",p,mapper(fecha)),pg,n==null?0:n);
 }
 public List<OpcionAutocompletado> buscarConceptos(String q,boolean captura){
  var p=parametros(false);String t=OrganizacionLaboralService.texto(q,200);if(!t.isEmpty()&&t.length()<3)return List.of();
  String where=captura?" AND activo AND aplicacion='HABITUAL'":"";
  return jdbc.query("SELECT id,codigo,nombre,naturaleza,aplicacion,activo FROM concepto_nomina WHERE institucion_id=:institucion"+where+" AND lower(codigo||' '||nombre) LIKE :q ESCAPE '!' ORDER BY id LIMIT 11",p.addValue("q",patron(t)),(r,n)->new OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),ConceptoNominaService.etiqueta(r.getString("naturaleza"))+" · "+ConceptoNominaService.etiqueta(r.getString("aplicacion"))+" · "+(r.getBoolean("activo")?"Activo":"Inactivo")));
 }
 public List<OpcionAutocompletado> buscarEmpleados(String q){
  var p=parametros(false);String t=OrganizacionLaboralService.texto(q,200);if(!t.isEmpty()&&t.length()<3)return List.of();
  return jdbc.query("SELECT id,codigo,nombres||' '||primer_apellido||coalesce(' '||segundo_apellido,'') nombre FROM empleado WHERE institucion_id=:institucion AND lower(codigo||' '||nombres||' '||primer_apellido||coalesce(' '||segundo_apellido,'')) LIKE :q ESCAPE '!' ORDER BY id LIMIT 11",p.addValue("q",patron(t)),(r,n)->new OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),"Empleado de tu institución"));
 }
 private static String patron(String s){return "%"+s.toLowerCase(Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";}
 private static final String WHERE="""
  WHERE a.institucion_id=:institucion AND a.modelo='LEGADO'
   AND (:empleado IS NULL OR a.empleado_id=:empleado) AND (:concepto IS NULL OR a.concepto_id=:concepto)
   AND (:tipo='' OR a.naturaleza=:tipo)
   AND strpos(lower(e.codigo||' '||e.nombres||' '||e.primer_apellido||' '||coalesce(e.segundo_apellido,'')||' '||a.concepto_codigo||' '||a.concepto_nombre||' '||a.motivo),lower(:q))>0
   AND (:desde IS NULL OR a.fecha_fin IS NULL OR a.fecha_fin>=:desde) AND (:hasta IS NULL OR a.fecha_inicio<=:hasta)
   AND (:estado='TODAS' OR :estado='CANCELADA' AND a.estado='CANCELADA'
    OR a.estado='ACTIVA' AND (:estado='ACTUAL' AND a.fecha_inicio<=:hoy AND (a.fecha_fin IS NULL OR a.fecha_fin>=:hoy)
     OR :estado='PROXIMA' AND a.fecha_inicio>:hoy OR :estado='ANTERIOR' AND a.fecha_fin<:hoy))
  """;
 public Page<Asignacion> listar(FiltroConceptoEmpleado original){
  var f=original.normalizado();f.validar();var p=parametros(false);if(f.filtroEmpleadoId()!=null)empleados.autorizar(f.filtroEmpleadoId(),true,false);if(f.filtroConceptoId()!=null)catalogo.obtener(f.filtroConceptoId());
  p.addValue("empleado",f.filtroEmpleadoId(),java.sql.Types.BIGINT).addValue("concepto",f.filtroConceptoId(),java.sql.Types.BIGINT).addValue("tipo",f.filtroNaturaleza()).addValue("q",f.q()).addValue("estado",f.estado()).addValue("desde",f.desde(),java.sql.Types.DATE).addValue("hasta",f.hasta(),java.sql.Types.DATE);
  var page=PageRequest.of(f.pagina(),f.tamanio());Long total=jdbc.queryForObject("SELECT count(*)"+FROM+WHERE,p,Long.class);p.addValue("limite",page.getPageSize()).addValue("offset",page.getOffset());
  return new PageImpl<>(jdbc.query(SELECT+FROM+WHERE+" ORDER BY a.id LIMIT :limite OFFSET :offset",p,mapper((LocalDate)p.getValue("hoy"))),page,total==null?0:total);
 }
 public Page<Historia> historial(Long id,int pagina){
  obtener(id);var p=parametros(false).addValue("id",id);var pg=PageRequest.of(Math.max(0,pagina),20);
  Long total=jdbc.queryForObject("SELECT count(*) FROM concepto_empleado_historial WHERE asignacion_id=:id AND institucion_id=:institucion",p,Long.class);
  p.addValue("offset",pg.getOffset());return new PageImpl<>(jdbc.query("SELECT h.id,to_char(h.ocurrido_en AT TIME ZONE i.zona_horaria,'DD/MM/YYYY HH24:MI') fecha,u.username usuario,h.operacion,h.datos->'despues'->>'motivo_operacion' motivo FROM concepto_empleado_historial h JOIN institucion i ON i.id=h.institucion_id JOIN usuario u ON u.id=h.usuario_id WHERE h.asignacion_id=:id AND h.institucion_id=:institucion ORDER BY h.id DESC LIMIT 20 OFFSET :offset",p,(r,n)->new Historia(r.getLong("id"),r.getString("fecha"),r.getString("usuario"),r.getString("operacion"),r.getString("motivo"))),pg,total==null?0:total);
 }
 private void bloquearEmpleado(Long id){empleados.autorizar(id,true,false);
  jdbc.queryForObject("SELECT id FROM empleado WHERE id=:empleado AND institucion_id=:institucion FOR UPDATE",parametros(true).addValue("empleado",id),Long.class);
  // Lecturas JDBC posteriores consultan las versiones persistidas, no una entidad en caché.
  jdbc.queryForObject("SELECT id FROM empleado_laboral WHERE empleado_id=:empleado FOR UPDATE",new MapSqlParameterSource("empleado",id),Long.class);
 }
 private void institucionActiva(MapSqlParameterSource p){if(!Boolean.TRUE.equals(jdbc.queryForObject("SELECT activo FROM institucion WHERE id=:institucion FOR SHARE",p,Boolean.class)))throw regla("La institución está inactiva; sólo se permite consultar.");}
 @Transactional public Long asignar(ConceptoEmpleadoForm f){
  return asignar(f,false);
 }
 @Transactional public Long asignarConfiguracion(ConceptoEmpleadoForm f){empleados.autorizarNomina(true);return asignar(f,true);}
 private Long asignar(ConceptoEmpleadoForm f,boolean configuracion){
  var p=parametros(true);institucionActiva(p);if(f.getEmpleadoId()==null||f.getConceptoId()==null)throw regla("Selecciona empleado y concepto de las sugerencias.");
  // Mismo orden que las capturas ocasionales anteriores: periodo → empleado → concepto.
  if(configuracion&&"UNICA".equals(f.getModalidad())&&f.getPeriodoId()!=null)jdbc.query("SELECT id FROM periodo_nomina WHERE id=:periodo AND institucion_id=:institucion FOR UPDATE",new MapSqlParameterSource("periodo",f.getPeriodoId()).addValue("institucion",p.getValue("institucion")),(r,n)->r.getLong(1));
  bloquearEmpleado(f.getEmpleadoId());p.addValue("concepto",f.getConceptoId());
  jdbc.query("SELECT id FROM concepto_nomina WHERE id=:concepto AND institucion_id=:institucion FOR UPDATE",p,(r,n)->r.getLong(1));
  var c=contexto(f.getEmpleadoId(),f.getConceptoId(),configuracion);
  if(!Objects.equals(c.conceptoVersion(),f.getConceptoVersion())||!Objects.equals(c.laboralVersion(),f.getLaboralVersion())||!Objects.equals(c.concepto(),f.getConceptoTexto())||!Objects.equals(c.empleado(),f.getEmpleadoTexto()))throw regla("El concepto o empleado cambió desde la selección. Vuelve a seleccionarlos y revisa antes de confirmar.");
  var hoy=(LocalDate)p.getValue("hoy");var ingreso=jdbc.queryForObject("SELECT fecha_ingreso FROM empleado_laboral WHERE empleado_id=:empleado",new MapSqlParameterSource("empleado",f.getEmpleadoId()),LocalDate.class);
  p.addValue("modelo",configuracion?"V2":"LEGADO").addValue("frecuenciaImporte",null).addValue("modalidad",null)
   .addValue("periodoId",null,java.sql.Types.BIGINT).addValue("periodoCodigo",null).addValue("periodoInicio",null,java.sql.Types.DATE).addValue("periodoFin",null,java.sql.Types.DATE).addValue("periodoVersion",null,java.sql.Types.BIGINT);
  if(configuracion){
   if(!Set.of("RECURRENTE","UNICA").contains(Objects.toString(f.getModalidad(),"")))throw regla("Selecciona si el concepto se repite o se aplica sólo en un periodo.");
   p.addValue("modalidad",f.getModalidad());
   if("UNICA".equals(f.getModalidad())){
    if(f.getPeriodoId()==null)throw regla("Selecciona el periodo de la lista para el concepto de una sola vez.");
    var filas=jdbc.queryForList("SELECT id,codigo,nombre,fecha_inicio,fecha_fin,version,estado FROM periodo_nomina WHERE id=:periodo AND institucion_id=:institucion FOR SHARE",new MapSqlParameterSource("periodo",f.getPeriodoId()).addValue("institucion",p.getValue("institucion")));
    if(filas.isEmpty())throw new AccessDeniedException("Periodo no autorizado");var per=filas.getFirst();
    if(!"PROGRAMADO".equals(per.get("estado"))||!Objects.equals(((Number)per.get("version")).longValue(),f.getPeriodoVersion())||!Objects.equals(per.get("codigo")+" · "+per.get("nombre"),f.getPeriodoTexto()))throw regla("El periodo cambió o fue cancelado. Vuelve a seleccionarlo antes de confirmar.");
    var pi=((java.sql.Date)per.get("fecha_inicio")).toLocalDate();var pf=((java.sql.Date)per.get("fecha_fin")).toLocalDate();
    if(!Objects.equals(pi,f.getInicio())||!Objects.equals(pf,f.getFin()))throw regla("Las fechas del concepto único deben coincidir con el periodo seleccionado.");
    if(jdbc.queryForObject("SELECT count(*) FROM concepto_empleado_periodo WHERE empleado_id=:empleado AND concepto_id=:concepto AND periodo_id=:periodo AND estado='ACTIVA'",new MapSqlParameterSource("empleado",f.getEmpleadoId()).addValue("concepto",f.getConceptoId()).addValue("periodo",f.getPeriodoId()),Long.class)>0)throw regla("Este concepto ya está registrado en la captura anterior del mismo empleado y periodo. No se duplica.");
    p.addValue("frecuenciaImporte","UNICA").addValue("periodoId",f.getPeriodoId()).addValue("periodoCodigo",per.get("codigo")).addValue("periodoInicio",pi).addValue("periodoFin",pf).addValue("periodoVersion",f.getPeriodoVersion());
   }else{
    if(!Set.of("MENSUAL","QUINCENAL","SEMANAL").contains(Objects.toString(f.getFrecuenciaImporte(),"")))throw regla("Selecciona frecuencia mensual, quincenal o semanal.");
    if(f.getPeriodoId()!=null)throw regla("Un concepto recurrente no debe tener un periodo único seleccionado.");
    p.addValue("frecuenciaImporte",f.getFrecuenciaImporte());
   }
  }
  if(f.getInicio()==null||f.getInicio().isBefore(hoy)||f.getInicio().isBefore(ingreso))throw regla("El inicio debe ser hoy o futuro y no anterior al ingreso laboral. No se reescribe el pasado.");
  if(f.getFin()!=null&&f.getFin().isBefore(f.getInicio()))throw regla("El fin de vigencia no puede ser anterior al inicio.");
  if(f.getImporte()==null||f.getImporte().signum()<=0||f.getImporte().scale()>2||f.getImporte().precision()-f.getImporte().scale()>17)throw regla("Captura un importe fijo mayor que cero con máximo dos decimales.");
  validarMotivo(f.getMotivo());p.addValue("empleado",f.getEmpleadoId()).addValue("inicio",f.getInicio()).addValue("fin",f.getFin(),java.sql.Types.DATE);
  Asignacion anterior=null;
  if(f.getAnteriorId()!=null){
   anterior=obtener(f.getAnteriorId());if(!Objects.equals(anterior.empleadoId(),f.getEmpleadoId())||!Objects.equals(anterior.conceptoId(),f.getConceptoId())||!Objects.equals(anterior.version(),f.getAnteriorVersion())||!anterior.modificable())throw regla("La vigencia anterior cambió; vuelve a abrir el detalle y revisa la programación.");
   var previo=jdbc.queryForMap("SELECT modelo,modalidad FROM concepto_empleado WHERE id=:id",new MapSqlParameterSource("id",anterior.id()));
   if("V2".equals(previo.get("modelo"))&&!configuracion)throw regla("Gestiona este concepto desde la pestaña Configuración de nómina del empleado.");
   if("UNICA".equals(previo.get("modalidad"))||configuracion&&"UNICA".equals(f.getModalidad()))throw regla("Los conceptos de una sola vez no se sustituyen por otra vigencia. Cancela la programación futura y registra la correcta.");
   if(!f.getInicio().isAfter(anterior.inicio())||!f.getInicio().isAfter(hoy)||anterior.fin()!=null&&f.getInicio().isAfter(anterior.fin()))throw regla("El cambio debe comenzar después del inicio anterior y de hoy, dentro de su vigencia.");
   posteriores(anterior,p);
   p.addValue("anterior",anterior.id()).addValue("cierre",f.getInicio().minusDays(1)).addValue("motivoCierre",OrganizacionLaboralService.texto("Sustituida desde "+f.getInicio()+": "+f.getMotivo().strip(),2000));
   jdbc.update("UPDATE concepto_empleado SET fecha_fin=:cierre,motivo_operacion=:motivoCierre,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:anterior",p);
  }
  if(jdbc.queryForObject("SELECT count(*) FROM concepto_empleado WHERE empleado_id=:empleado AND concepto_id=:concepto AND estado='ACTIVA' AND daterange(fecha_inicio,fecha_fin,'[]') && daterange(:inicio,:fin,'[]')",p,Long.class)>0)throw regla("Ya existe este concepto para el empleado durante esas fechas. Abre su detalle y usa Programar cambio, o elige una vigencia que no se cruce.");
  var concepto=catalogo.obtener(f.getConceptoId());p.addValue("codigo",concepto.codigo()).addValue("nombre",concepto.nombre()).addValue("tipo",concepto.naturaleza()).addValue("conceptoVersion",concepto.version()).addValue("importe",f.getImporte()).addValue("moneda",c.moneda()).addValue("motivo",f.getMotivo().strip()).addValue("anterior",anterior==null?null:anterior.id(),java.sql.Types.BIGINT).addValue("finAnterior",anterior==null?null:anterior.fin(),java.sql.Types.DATE);
  return jdbc.queryForObject("INSERT INTO concepto_empleado(institucion_id,empleado_id,concepto_id,concepto_codigo,concepto_nombre,naturaleza,aplicacion,concepto_version,importe,moneda,fecha_inicio,fecha_fin,motivo,motivo_operacion,anterior_id,fin_anterior,creado_por_id,actualizado_por_id,modelo,frecuencia_importe,modalidad,periodo_id,periodo_codigo,periodo_inicio,periodo_fin,periodo_version) VALUES(:institucion,:empleado,:concepto,:codigo,:nombre,:tipo,'HABITUAL',:conceptoVersion,:importe,:moneda,:inicio,:fin,:motivo,:motivo,:anterior,:finAnterior,:actor,:actor,:modelo,:frecuenciaImporte,:modalidad,:periodoId,:periodoCodigo,:periodoInicio,:periodoFin,:periodoVersion) RETURNING id",p,Long.class);
 }
 private void posteriores(Asignacion a,MapSqlParameterSource p){
  p.addValue("empleado",a.empleadoId()).addValue("concepto",a.conceptoId()).addValue("inicioAnterior",a.inicio());
  if(jdbc.queryForObject("SELECT count(*) FROM concepto_empleado WHERE empleado_id=:empleado AND concepto_id=:concepto AND estado='ACTIVA' AND fecha_inicio>:inicioAnterior",p,Long.class)>0)throw regla("Hay una programación posterior de este concepto. Revisa primero la última vigencia; no se intercalan cambios.");
 }
 @Transactional public void gestionar(Long id,Long version,String motivo,LocalDate fin,boolean cancelar){
  var p=parametros(true);institucionActiva(p);validarMotivo(motivo);var inicial=obtener(id);bloquearEmpleado(inicial.empleadoId());var a=obtener(id);
  if(!Objects.equals(a.version(),version)||"Cancelada".equals(a.estado()))throw regla("El registro cambió en otra pestaña o por otro usuario. Recarga; no se realizó otra operación.");
  var modelo=jdbc.queryForMap("SELECT modelo,modalidad FROM concepto_empleado WHERE id=:id",new MapSqlParameterSource("id",id));
  if("V2".equals(modelo.get("modelo"))){empleados.autorizarNomina(true);if(!cancelar&&"UNICA".equals(modelo.get("modalidad")))throw regla("Un concepto único conserva la cobertura de su periodo. Cancela sólo si la programación todavía no inicia.");}
  posteriores(a,p);var hoy=(LocalDate)p.getValue("hoy");p.addValue("id",id).addValue("motivo",motivo.strip());
  if(cancelar){
   if(!a.cancelable())throw regla("Sólo se cancela una programación que aún no inicia. Para una vigente, usa Terminar vigencia sin borrar su historial.");
   jdbc.update("UPDATE concepto_empleado SET estado='CANCELADA',motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id",p);
   if(a.anteriorId()!=null){p.addValue("anterior",a.anteriorId()).addValue("fin",a.finAnterior(),java.sql.Types.DATE).addValue("cierre",a.inicio().minusDays(1));
    int n=jdbc.update("UPDATE concepto_empleado SET fecha_fin=:fin,motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:anterior AND estado='ACTIVA' AND fecha_fin=:cierre",p);
    if(n!=1)throw regla("La vigencia anterior cambió; no se puede restaurar automáticamente. Recarga.");
   }
  }else{
   if(!"Vigente".equals(a.estado())||fin==null||fin.isBefore(hoy)||fin.isBefore(a.inicio())||a.fin()!=null&&!fin.isBefore(a.fin()))throw regla("Una vigencia actual puede terminar hoy o después, sin extender su fin existente. La fecha indicada es el último día incluido.");
   p.addValue("fin",fin);jdbc.update("UPDATE concepto_empleado SET fecha_fin=:fin,motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id",p);
  }
 }
 private static void validarMotivo(String motivo){if(motivo==null||motivo.isBlank()||motivo.length()>2000)throw regla("Explica el motivo obligatorio con máximo 2000 caracteres.");}
 public static boolean esSueldoBase(String codigo,String nombre){
  return java.util.stream.Stream.of(codigo,nombre).filter(Objects::nonNull).map(x->x.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]","")).anyMatch(x->Set.of("SUELDO","SUELDOBASE","SALARIOBASE","SUELDOBASEMENSUAL","SALARIO").contains(x));
 }
 private static String efectoDe(String tipo){return switch(tipo){case "PERCEPCION"->"Suma al empleado";case "DEDUCCION"->"Resta al empleado";default->"Costo de la escuela; no reduce el pago del empleado";};}
 private static ReglaNegocioException regla(String mensaje){return new ReglaNegocioException(mensaje);}
}
