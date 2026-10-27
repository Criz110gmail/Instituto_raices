package escuela.personal.service;

import escuela.personal.dto.*;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import java.time.*;import java.math.BigDecimal;import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class OrganizacionLaboralService {
 private final EmpleadoService empleados;
 private final CondicionesSalarialesService salarios;
 private final NamedParameterJdbcTemplate jdbc;
 public record Catalogo(Long id,String codigo,String nombre,String descripcion,boolean activo,Long version,
  BigDecimal horas,Long puestoId,String puestoTexto,Long jornadaId,String jornadaTexto,BigDecimal importe,String moneda,String base,String frecuencia){}
 public record Contexto(Catalogo esquema,Long versionEsquema,Long versionJornada,Long versionPuesto,Long versionLaboral,Long revision,String empleado,LocalDate hoy){}
 public record Asignacion(Long id,Long empleadoId,String empleado,String codigoEmpleado,String puesto,String esquema,String jornada,BigDecimal horas,
  BigDecimal importe,String moneda,String base,String frecuencia,LocalDate inicio,LocalDate fin,String estado,String motivo,Long version,boolean cancelable){}
 private MapSqlParameterSource p(boolean escribir){var a=empleados.autorizarSalarios(escribir);return new MapSqlParameterSource("institucion",a.institucionId()).addValue("actor",a.usuarioId());}
 public static boolean esJornada(String tipo){if(!Set.of("jornadas","esquemas").contains(tipo))throw new AccessDeniedException("Catálogo laboral no disponible");return tipo.equals("jornadas");}
 private String tabla(String tipo){return esJornada(tipo)?"jornada_laboral":"esquema_remuneracion";}
 private String seleccion(String tipo){return esJornada(tipo)?"SELECT c.*,c.horas_semanales horas,NULL::bigint puesto,NULL::text puesto_texto,NULL::bigint jornada,NULL::text jornada_texto,NULL::numeric importe,NULL::text moneda,NULL::text base,NULL::text frecuencia FROM jornada_laboral c":
  "SELECT c.*,j.horas_semanales horas,p.id puesto,p.codigo||' · '||p.nombre puesto_texto,j.id jornada,j.codigo||' · '||j.nombre jornada_texto,c.importe_bruto importe FROM esquema_remuneracion c JOIN puesto_laboral p ON p.id=c.puesto_id JOIN jornada_laboral j ON j.id=c.jornada_id";}
 private static final RowMapper<Catalogo> CATALOGO=(r,n)->new Catalogo(r.getLong("id"),r.getString("codigo"),r.getString("nombre"),r.getString("descripcion"),r.getBoolean("activo"),r.getLong("version"),r.getBigDecimal("horas"),r.getObject("puesto",Long.class),r.getString("puesto_texto"),r.getObject("jornada",Long.class),r.getString("jornada_texto"),r.getBigDecimal("importe"),r.getString("moneda"),r.getString("base"),r.getString("frecuencia"));
 public Page<Catalogo> listar(String tipo,String q,String estado,int pagina,int tamanio){return listar(tipo,q,estado,pagina,tamanio,new FiltrosCatalogosPersonal());}
 public Page<Catalogo> listar(String tipo,String q,String estado,int pagina,int tamanio,FiltrosCatalogosPersonal f){
  f.validar();
  var params=p(false).addValue("q",texto(q,200)).addValue("estado",estado(estado));var pg=pagina(pagina,tamanio);
  String where=" WHERE c.institucion_id=:institucion AND strpos(lower(c.codigo||' '||c.nombre),lower(:q))>0 AND (:estado='TODOS' OR c.activo=(:estado='ACTIVO'))";
  if(esJornada(tipo)){if(f.getFiltroJornadaId()!=null){obtener(tipo,f.getFiltroJornadaId());params.addValue("filtroJornada",f.getFiltroJornadaId());where+=" AND c.id=:filtroJornada";}}
  else{
   if(f.getFiltroEsquemaId()!=null){obtener(tipo,f.getFiltroEsquemaId());params.addValue("filtroEsquema",f.getFiltroEsquemaId());where+=" AND c.id=:filtroEsquema";}
   if(f.getFiltroPuestoId()!=null){salarios.puesto(f.getFiltroPuestoId());params.addValue("filtroPuesto",f.getFiltroPuestoId());where+=" AND c.puesto_id=:filtroPuesto";}
   if(f.getFiltroJornadaId()!=null){obtener("jornadas",f.getFiltroJornadaId());params.addValue("filtroJornada",f.getFiltroJornadaId());where+=" AND c.jornada_id=:filtroJornada";}
  }
  Long total=jdbc.queryForObject("SELECT count(*) FROM "+tabla(tipo)+" c"+where,params,Long.class);
  params.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset());
  return new PageImpl<>(jdbc.query(seleccion(tipo)+where+" ORDER BY c.nombre,c.id LIMIT :limite OFFSET :offset",params,CATALOGO),pg,total==null?0:total);
 }
 public Catalogo obtener(String tipo,Long id){return jdbc.query(seleccion(tipo)+" WHERE c.id=:id AND c.institucion_id=:institucion",p(false).addValue("id",id),CATALOGO).stream().findFirst().orElseThrow(()->new AccessDeniedException("Registro no autorizado"));}
 public List<Catalogo> buscar(String tipo,String q){if(q!=null&&!q.isBlank()&&q.strip().length()<3){p(false);return List.of();}return listar(tipo,q,"ACTIVO",0,10).getContent();}
 public List<escuela.admin.dto.OpcionAutocompletado> buscarFiltros(String tipo,String q){
  var params=p(false);String t=texto(q,200);
  if(!Set.of("puestos","jornadas","esquemas").contains(tipo))throw new AccessDeniedException("Filtro no disponible");
  if(!t.isEmpty()&&t.length()<3)return List.of();
  String tabla=tipo.equals("puestos")?"puesto_laboral":tabla(tipo);
  return jdbc.query("SELECT id,codigo,nombre,activo FROM "+tabla+" WHERE institucion_id=:institucion AND strpos(lower(codigo||' '||nombre),lower(:q))>0 ORDER BY nombre,id LIMIT 11",params.addValue("q",t),(r,n)->new escuela.admin.dto.OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),r.getBoolean("activo")?"Activo":"Inactivo · consulta histórica"));
 }
 public void prepararFiltros(FiltrosCatalogosPersonal f){
  p(false);
  if(f.getFiltroPuestoId()!=null){var c=salarios.puesto(f.getFiltroPuestoId());f.setFiltroPuestoTexto(c.codigo()+" · "+c.nombre());}
  if(f.getFiltroJornadaId()!=null){var c=obtener("jornadas",f.getFiltroJornadaId());f.setFiltroJornadaTexto(c.codigo()+" · "+c.nombre());}
  if(f.getFiltroEsquemaId()!=null){var c=obtener("esquemas",f.getFiltroEsquemaId());f.setFiltroEsquemaTexto(c.codigo()+" · "+c.nombre());}
 }
 public List<escuela.admin.dto.OpcionAutocompletado> buscarEmpleados(String q){p(false);return empleados.buscarEmpleados(q);}
 private void institucionActiva(MapSqlParameterSource params){if(!Boolean.TRUE.equals(jdbc.queryForObject("SELECT activo FROM institucion WHERE id=:institucion",params,Boolean.class)))throw regla("La institución está inactiva; sólo permite consulta.");}
 @Transactional public Long guardar(String tipo,Long id,ConfiguracionLaboralForm f){
  var params=p(true);institucionActiva(params);String tabla=tabla(tipo);
  if(f.getCodigo()==null||f.getCodigo().isBlank()||f.getCodigo().strip().length()>50||f.getNombre()==null||f.getNombre().isBlank()||f.getNombre().strip().length()>150||f.getDescripcion()!=null&&f.getDescripcion().length()>2000)throw regla("Captura código y nombre válidos; descripción máximo2000 caracteres.");
  params.addValue("codigo",f.getCodigo().strip().toUpperCase(Locale.ROOT)).addValue("nombre",f.getNombre().strip()).addValue("descripcion",texto(f.getDescripcion(),2000)).addValue("activo",f.isActivo());
  String campos,values,update;
  if(esJornada(tipo)){
   var h=f.getHorasSemanales();if(h==null||h.signum()<=0||h.compareTo(new BigDecimal("168"))>0||h.scale()>2)throw regla("Indica horas semanales mayores que0 y hasta168, con máximo dos decimales. Es capacidad semanal, no cálculo de nómina.");
   params.addValue("horas",h);campos="horas_semanales";values=":horas";update="horas_semanales=:horas";
  }else{
   if(f.getPuestoId()==null||f.getJornadaId()==null)throw regla("Selecciona puesto y jornada desde las ayudas; escribir el nombre sin seleccionar no es suficiente.");
   var puesto=salarios.puesto(f.getPuestoId());var jornada=obtener("jornadas",f.getJornadaId());
   if(!Objects.equals(f.getPuestoTexto(),puesto.codigo()+" · "+puesto.nombre())||!Objects.equals(f.getJornadaTexto(),jornada.codigo()+" · "+jornada.nombre()))throw regla("Puesto o jornada cambiaron; vuelve a seleccionarlos.");
   if(f.isActivo()&&(!puesto.activo()||!jornada.activo()))throw regla("La configuración activa requiere puesto y jornada activos.");
   var importe=f.getImporteBruto();if(importe==null||importe.signum()<=0||importe.scale()>2||importe.precision()-importe.scale()>17)throw regla("Captura sueldo bruto positivo con máximo dos decimales.");
   if(!Set.of("MENSUAL","SEMANAL","DIARIA","POR_HORA").contains(Objects.toString(f.getBase(),""))||!Set.of("MENSUAL","QUINCENAL","SEMANAL").contains(Objects.toString(f.getFrecuencia(),"")))throw regla("Selecciona base y frecuencia válidas.");
   params.addValue("puesto",puesto.id()).addValue("jornada",jornada.id()).addValue("importe",importe).addValue("base",f.getBase()).addValue("frecuencia",f.getFrecuencia());
   campos="puesto_id,jornada_id,importe_bruto,moneda,base,frecuencia";values=":puesto,:jornada,:importe,(SELECT moneda_predeterminada FROM institucion WHERE id=:institucion),:base,:frecuencia";update="puesto_id=:puesto,jornada_id=:jornada,importe_bruto=:importe,base=:base,frecuencia=:frecuencia";
  }
  if(id==null)return jdbc.queryForObject("INSERT INTO "+tabla+"(institucion_id,codigo,nombre,descripcion,activo,"+campos+",creado_por_id,actualizado_por_id) VALUES(:institucion,:codigo,:nombre,:descripcion,:activo,"+values+",:actor,:actor) RETURNING id",params,Long.class);
  obtener(tipo,id);params.addValue("id",id).addValue("version",f.getVersion());
  if(jdbc.update("UPDATE "+tabla+" SET codigo=:codigo,nombre=:nombre,descripcion=:descripcion,activo=:activo,"+update+",version=version+1,actualizado_en=CURRENT_TIMESTAMP,actualizado_por_id=:actor WHERE id=:id AND institucion_id=:institucion AND version=:version",params)!=1)throw regla("El registro cambió; recarga antes de guardar.");return id;
 }
 public LocalDate hoy(){var params=p(false);String zona=jdbc.queryForObject("SELECT zona_horaria FROM institucion WHERE id=:institucion",params,String.class);return LocalDate.now(ZoneId.of(zona));}
 public String moneda(){return jdbc.queryForObject("SELECT moneda_predeterminada FROM institucion WHERE id=:institucion",p(false),String.class);}
 public Contexto contexto(Long empleado,Long esquema){p(false);var e=empleados.obtener(empleado);empleados.autorizar(empleado,true,false);var l=empleados.laboral(empleado);var s=obtener("esquemas",esquema);var j=obtener("jornadas",s.jornadaId());var puesto=salarios.puesto(s.puestoId());return new Contexto(s,s.version(),j.version(),puesto.version(),l.getVersion(),salarios.revision(empleado),e.codigo()+" · "+e.nombre(),empleados.hoyLaboral(empleado));}
 @Transactional public Long asignar(AsignacionLaboralForm f){
  var params=p(true);institucionActiva(params);
  // Mismo orden que condiciones individuales: empleado, catálogos, condición.
  jdbc.queryForList("SELECT id FROM empleado WHERE id=:empleado AND institucion_id=:institucion FOR UPDATE",params.addValue("empleado",f.getEmpleadoId()));
  jdbc.queryForList("SELECT id FROM esquema_remuneracion WHERE id=:id AND institucion_id=:institucion FOR UPDATE",params.addValue("id",f.getEsquemaId()));
  var contexto=contexto(f.getEmpleadoId(),f.getEsquemaId());var s=contexto.esquema();
  jdbc.queryForList("SELECT id FROM jornada_laboral WHERE id=:id AND institucion_id=:institucion FOR UPDATE",params.addValue("id",s.jornadaId()));
  jdbc.queryForList("SELECT id FROM puesto_laboral WHERE id=:id AND institucion_id=:institucion FOR UPDATE",params.addValue("id",s.puestoId()));
  contexto=contexto(f.getEmpleadoId(),f.getEsquemaId());s=contexto.esquema();var j=obtener("jornadas",s.jornadaId());var puesto=salarios.puesto(s.puestoId());
  if(!s.activo()||!j.activo()||!puesto.activo())throw regla("La configuración, jornada y puesto deben estar activos.");
  if(!Objects.equals(f.getVersionEsquema(),contexto.versionEsquema())||!Objects.equals(f.getVersionJornada(),contexto.versionJornada())||!Objects.equals(f.getVersionPuesto(),contexto.versionPuesto()))throw regla("La configuración, jornada o puesto cambió. Vuelve a seleccionarlo y revisa el resumen.");
  if(!Objects.equals(s.moneda(),moneda()))throw regla("La moneda de la configuración no coincide con la institución; revisa la configuración.");
  var condicion=new CondicionSalarialForm();condicion.setPuestoId(puesto.id());condicion.setPuestoTexto(puesto.codigo()+" · "+puesto.nombre());condicion.setImporteBruto(s.importe());condicion.setBase(s.base());condicion.setFrecuencia(s.frecuencia());condicion.setFechaInicio(f.getInicio());condicion.setFechaFin(f.getFin());condicion.setMotivo(f.getMotivo());condicion.setVersionLaboral(f.getVersionLaboral());condicion.setRevision(f.getRevision());
  Long id=salarios.programar(f.getEmpleadoId(),condicion);
  params.addValue("condicion",id).addValue("esquema",s.id()).addValue("esquemaVersion",s.version()).addValue("esquemaCodigo",s.codigo()).addValue("esquemaNombre",s.nombre()).addValue("jornada",j.id()).addValue("jornadaVersion",j.version()).addValue("jornadaCodigo",j.codigo()).addValue("jornadaNombre",j.nombre()).addValue("descripcion",j.descripcion()).addValue("horas",j.horas()).addValue("puestoVersion",puesto.version());
  jdbc.update("INSERT INTO asignacion_esquema_laboral(institucion_id,empleado_id,condicion_id,esquema_id,esquema_version,esquema_codigo,esquema_nombre,jornada_id,jornada_version,jornada_codigo,jornada_nombre,jornada_descripcion,horas_semanales,puesto_version,creado_por_id) VALUES(:institucion,:empleado,:condicion,:esquema,:esquemaVersion,:esquemaCodigo,:esquemaNombre,:jornada,:jornadaVersion,:jornadaCodigo,:jornadaNombre,:descripcion,:horas,:puestoVersion,:actor)",params);
  return id;
 }
 private static final String ASIGNACIONES=" FROM condicion_salarial c JOIN empleado e ON e.id=c.empleado_id LEFT JOIN asignacion_esquema_laboral a ON a.condicion_id=c.id ";
 private static final RowMapper<Asignacion> ASIGNACION=(r,n)->new Asignacion(r.getLong("id"),r.getLong("empleado_id"),r.getString("empleado"),r.getString("codigo_empleado"),r.getString("puesto_codigo")+" · "+r.getString("puesto_nombre"),r.getString("esquema"),r.getString("jornada"),r.getBigDecimal("horas_semanales"),r.getBigDecimal("importe_bruto"),r.getString("moneda"),r.getString("base"),r.getString("frecuencia"),r.getObject("fecha_inicio",LocalDate.class),r.getObject("fecha_fin",LocalDate.class),r.getString("situacion"),r.getString("motivo"),r.getLong("version"),r.getBoolean("cancelable"));
 private static final String CAMPOS="SELECT c.*,e.codigo codigo_empleado,e.nombres||' '||e.primer_apellido||coalesce(' '||e.segundo_apellido,'') empleado,coalesce(a.esquema_codigo||' · '||a.esquema_nombre,CASE WHEN c.horas_puesto IS NOT NULL THEN 'Asignación desde puesto' ELSE 'Condición individual previa' END) esquema,a.jornada_codigo||' · '||a.jornada_nombre jornada,coalesce(c.horas_puesto,a.horas_semanales) horas_semanales,CASE WHEN c.estado='CANCELADA' THEN 'Cancelada' WHEN c.fecha_inicio>:hoy THEN 'Próxima' WHEN c.fecha_fin<:hoy THEN 'Anterior' ELSE 'Vigente' END situacion,(c.estado='ACTIVA' AND c.fecha_inicio>:hoy AND NOT EXISTS(SELECT 1 FROM condicion_salarial posterior WHERE posterior.empleado_id=c.empleado_id AND posterior.estado='ACTIVA' AND posterior.fecha_inicio>c.fecha_inicio)) cancelable";
 public Page<Asignacion> asignaciones(Long empleado,String q,String estado,LocalDate desde,LocalDate hasta,int pagina,int tamanio){return asignaciones(empleado,q,estado,desde,hasta,pagina,tamanio,new FiltrosCatalogosPersonal());}
 public Page<Asignacion> asignaciones(Long empleado,String q,String estado,LocalDate desde,LocalDate hasta,int pagina,int tamanio,FiltrosCatalogosPersonal f){
  f.validar();prepararFiltros(f);
  var params=p(false).addValue("empleado",empleado,java.sql.Types.BIGINT).addValue("q",texto(q,200)).addValue("estado",Objects.toString(estado,"TODAS")).addValue("desde",desde,java.sql.Types.DATE).addValue("hasta",hasta,java.sql.Types.DATE).addValue("hoy",hoy());
  if(desde!=null&&hasta!=null&&desde.isAfter(hasta))throw regla("El inicio del rango debe ser anterior o igual al fin.");
  String where=" WHERE c.institucion_id=:institucion AND (:empleado IS NULL OR c.empleado_id=:empleado) AND strpos(lower(e.codigo||' '||e.nombres||' '||e.primer_apellido||' '||c.puesto_codigo||' '||c.puesto_nombre||' '||coalesce(a.esquema_codigo,'')||' '||coalesce(a.esquema_nombre,'')||' '||coalesce(a.jornada_codigo,'')||' '||coalesce(a.jornada_nombre,'')),lower(:q))>0 AND (:desde IS NULL OR c.fecha_fin IS NULL OR c.fecha_fin>=:desde) AND (:hasta IS NULL OR c.fecha_inicio<=:hasta) AND (:estado='TODAS' OR :estado='CANCELADA' AND c.estado='CANCELADA' OR c.estado='ACTIVA' AND (:estado='PROXIMA' AND c.fecha_inicio>:hoy OR :estado='ACTUAL' AND c.fecha_inicio<=:hoy AND (c.fecha_fin IS NULL OR c.fecha_fin>=:hoy) OR :estado='ANTERIOR' AND c.fecha_fin<:hoy))";
  if(f.getFiltroPuestoId()!=null){params.addValue("filtroPuesto",f.getFiltroPuestoId());where+=" AND c.puesto_id=:filtroPuesto";}
  if(f.getFiltroJornadaId()!=null){params.addValue("filtroJornada",f.getFiltroJornadaId());where+=" AND a.jornada_id=:filtroJornada";}
  if(f.getFiltroEsquemaId()!=null){params.addValue("filtroEsquema",f.getFiltroEsquemaId());where+=" AND a.esquema_id=:filtroEsquema";}
  Long total=jdbc.queryForObject("SELECT count(*)"+ASIGNACIONES+where,params,Long.class);var pg=pagina(pagina,tamanio);params.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset());
  return new PageImpl<>(jdbc.query(CAMPOS+ASIGNACIONES+where+" ORDER BY c.fecha_inicio DESC,c.id DESC LIMIT :limite OFFSET :offset",params,ASIGNACION),pg,total==null?0:total);
 }
 public Asignacion asignacion(Long id){return jdbc.query(CAMPOS+ASIGNACIONES+" WHERE c.institucion_id=:institucion AND c.id=:id",p(false).addValue("id",id).addValue("hoy",hoy()),ASIGNACION).stream().findFirst().orElseThrow(()->new AccessDeniedException("Asignación no autorizada"));}
 @Transactional public void cancelar(Long id,Long version,String motivo){var a=asignacion(id);p(true);salarios.cancelar(a.empleadoId(),id,version,motivo);}
 public static String texto(String t,int max){return t==null?"":t.strip().substring(0,Math.min(t.strip().length(),max));}
 public static String estado(String e){return Set.of("ACTIVO","INACTIVO").contains(Objects.toString(e,""))?e:"TODOS";}
 private static PageRequest pagina(int pagina,int tamanio){return PageRequest.of(Math.max(0,pagina),Set.of(10,25,50,100).contains(tamanio)?tamanio:25);}
 private static ReglaNegocioException regla(String t){return new ReglaNegocioException(t);}
}
