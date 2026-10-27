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
import java.time.*;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ProgramacionNominaService {
 private final EmpleadoService empleados;
 private final NamedParameterJdbcTemplate jdbc;
 public record Calendario(Long id,String codigo,String nombre,String descripcion,String frecuencia,boolean activo,Long version,boolean frecuenciaFija){
  public String frecuenciaTexto(){return etiqueta(frecuencia);}
 }
 public record Periodo(Long id,Long calendarioId,String codigo,String nombre,String calendarioCodigo,String calendarioNombre,String frecuencia,Long calendarioVersion,
  LocalDate inicio,LocalDate fin,LocalDate corte,LocalDate pago,String estado,String motivoRegistro,String motivoOperacion,Long version,String situacion){
  public String frecuenciaTexto(){return etiqueta(frecuencia);}
  public String estadoTexto(){return estado.equals("PROGRAMADO")?"Programado":"Cancelado";}
  public long dias(){return java.time.temporal.ChronoUnit.DAYS.between(inicio,fin)+1;}
  public String calendarioTexto(){return calendarioId==null?"Sin calendario · periodo independiente":calendarioCodigo+" · "+calendarioNombre;}
 }
 public record Contexto(Long version,String calendario,String frecuencia){}
 public record Historia(Long id,String fecha,String usuario,String operacion,String detalle){}
 public static String etiqueta(String frecuencia){return CondicionesSalarialesService.etiqueta(Objects.toString(frecuencia,""));}
 private static final String CAL_SELECT="SELECT c.*,EXISTS(SELECT 1 FROM periodo_nomina p WHERE p.calendario_id=c.id) frecuencia_fija ";
 private static final RowMapper<Calendario> CAL_MAP=(r,n)->new Calendario(r.getLong("id"),r.getString("codigo"),r.getString("nombre"),r.getString("descripcion"),r.getString("frecuencia"),r.getBoolean("activo"),r.getLong("version"),r.getBoolean("frecuencia_fija"));
 private MapSqlParameterSource parametros(boolean escribir){var p=empleados.autorizarSalarios(escribir);return new MapSqlParameterSource("institucion",p.institucionId()).addValue("actor",p.usuarioId());}
 private LocalDate hoy(MapSqlParameterSource p){String zona=jdbc.queryForObject("SELECT zona_horaria FROM institucion WHERE id=:institucion",p,String.class);return LocalDate.now(ZoneId.of(zona));}
 private RowMapper<Periodo> periodoMap(LocalDate hoy){return (r,n)->{
  var inicio=r.getObject("fecha_inicio",LocalDate.class);var fin=r.getObject("fecha_fin",LocalDate.class);String estado=r.getString("estado");
  String situacion=estado.equals("CANCELADO")?"No aplica":hoy.isBefore(inicio)?"Próximo":hoy.isAfter(fin)?"Transcurrido":"En curso";
  return new Periodo(r.getLong("id"),r.getObject("calendario_id",Long.class),r.getString("codigo"),r.getString("nombre"),r.getString("calendario_codigo"),r.getString("calendario_nombre"),r.getString("frecuencia"),r.getObject("calendario_version",Long.class),inicio,fin,r.getObject("fecha_corte",LocalDate.class),r.getObject("fecha_pago",LocalDate.class),estado,r.getString("motivo_registro"),r.getString("motivo_operacion"),r.getLong("version"),situacion);
 };}
 public Calendario calendario(Long id){return calendario(id,false);}
 private Calendario calendario(Long id,boolean bloquear){var p=parametros(bloquear).addValue("id",id);return jdbc.query(CAL_SELECT+" FROM calendario_nomina c WHERE c.institucion_id=:institucion AND c.id=:id"+(bloquear?" FOR UPDATE OF c":""),p,CAL_MAP).stream().findFirst().orElseThrow(()->new AccessDeniedException("Calendario de nómina no autorizado"));}
 public Periodo periodo(Long id){return periodo(id,false);}
 private Periodo periodo(Long id,boolean bloquear){var p=parametros(bloquear).addValue("id",id);return jdbc.query("SELECT * FROM periodo_nomina WHERE institucion_id=:institucion AND id=:id"+(bloquear?" FOR UPDATE":""),p,periodoMap(hoy(p))).stream().findFirst().orElseThrow(()->new AccessDeniedException("Periodo de nómina no autorizado"));}
 public Contexto contexto(Long id){var c=calendario(id);if(!c.activo())throw regla("El calendario está inactivo; selecciona uno activo para registrar periodos.");return new Contexto(c.version(),c.codigo()+" · "+c.nombre(),c.frecuenciaTexto());}
 private MapSqlParameterSource filtros(FiltroProgramacionNomina original,boolean periodos){
  var f=original.normalizado(periodos);f.validar();var p=parametros(false);
  if(f.filtroCalendarioId()!=null)calendario(f.filtroCalendarioId());if(f.filtroPeriodoId()!=null)periodo(f.filtroPeriodoId());
  return p.addValue("q",f.q()).addValue("estado",f.estado()).addValue("frecuencia",f.filtroFrecuencia()).addValue("calendario",f.filtroCalendarioId(),java.sql.Types.BIGINT).addValue("periodo",f.filtroPeriodoId(),java.sql.Types.BIGINT).addValue("desde",f.desde(),java.sql.Types.DATE).addValue("hasta",f.hasta(),java.sql.Types.DATE);
 }
 private static final String CAL_WHERE=" FROM calendario_nomina c WHERE c.institucion_id=:institucion AND (:calendario IS NULL OR c.id=:calendario) AND (:frecuencia='' OR c.frecuencia=:frecuencia) AND (:estado='TODOS' OR c.activo=(:estado='ACTIVO')) AND strpos(lower(c.codigo||' '||c.nombre||' '||coalesce(c.descripcion,'')),lower(:q))>0";
 private static final String PER_WHERE=" FROM periodo_nomina WHERE institucion_id=:institucion AND (:calendario IS NULL OR calendario_id=:calendario) AND (:periodo IS NULL OR id=:periodo) AND (:frecuencia='' OR frecuencia=:frecuencia) AND (:estado='TODOS' OR estado=:estado) AND (:desde IS NULL OR fecha_fin>=:desde) AND (:hasta IS NULL OR fecha_inicio<=:hasta) AND strpos(lower(concat_ws(' ',codigo,nombre,calendario_codigo,calendario_nombre,motivo_registro,motivo_operacion)),lower(:q))>0";
 public Page<Calendario> calendarios(FiltroProgramacionNomina original){var f=original.normalizado(false);var p=filtros(f,false);var pg=PageRequest.of(f.pagina(),f.tamanio());Long total=jdbc.queryForObject("SELECT count(*)"+CAL_WHERE,p,Long.class);p.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset());return new PageImpl<>(jdbc.query(CAL_SELECT+CAL_WHERE+" ORDER BY c.id LIMIT :limite OFFSET :offset",p,CAL_MAP),pg,total==null?0:total);}
 public Page<Periodo> periodos(FiltroProgramacionNomina original){var f=original.normalizado(true);var p=filtros(f,true);var pg=PageRequest.of(f.pagina(),f.tamanio());Long total=jdbc.queryForObject("SELECT count(*)"+PER_WHERE,p,Long.class);p.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset());return new PageImpl<>(jdbc.query("SELECT *"+PER_WHERE+" ORDER BY id LIMIT :limite OFFSET :offset",p,periodoMap(hoy(p))),pg,total==null?0:total);}
 public List<OpcionAutocompletado> buscar(boolean periodos,String q,boolean captura){
  var p=parametros(false);String t=OrganizacionLaboralService.texto(q,200);if(!t.isEmpty()&&t.length()<3)return List.of();p.addValue("q","%"+t.toLowerCase(Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%");
  String tabla=periodos?"periodo_nomina":"calendario_nomina";String activo=!periodos&&captura?" AND activo":"";
  return jdbc.query("SELECT * FROM "+tabla+" WHERE institucion_id=:institucion"+activo+" AND lower(codigo||' '||nombre) LIKE :q ESCAPE '!' ORDER BY id LIMIT 11",p,(r,n)->new OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),etiqueta(r.getString("frecuencia"))+" · "+(periodos?Objects.toString(r.getString("calendario_nombre"),"Periodo independiente")+" · "+(r.getString("estado").equals("PROGRAMADO")?"Programado":"Cancelado"):r.getBoolean("activo")?"Activo":"Inactivo")));
 }
 private void institucionActiva(MapSqlParameterSource p){if(!Boolean.TRUE.equals(jdbc.queryForObject("SELECT activo FROM institucion WHERE id=:institucion FOR SHARE",p,Boolean.class)))throw regla("La institución está inactiva; sólo permite consultar su programación.");}
 private static void identidad(String codigo,String nombre,Long version){if(codigo==null||!codigo.matches("[A-Za-z0-9][A-Za-z0-9_-]{0,49}")||nombre==null||nombre.isBlank()||nombre.length()>150||version==null||version<0)throw regla("Captura código y nombre válidos. Si el registro cambió, recarga antes de guardar.");}
 private static void motivo(String m){if(m==null||m.isBlank()||m.length()>2000)throw regla("Explica el motivo obligatorio con máximo 2000 caracteres.");}
 private static void version(Long actual,Long enviada){if(!Objects.equals(actual,enviada))throw regla("El registro cambió en otra pestaña o por otro usuario. Recarga antes de guardar; no se sobrescribieron sus cambios.");}
 @Transactional public Long guardarCalendario(Long id,CalendarioNominaForm f){
  var p=parametros(true);institucionActiva(p);identidad(f.getCodigo(),f.getNombre(),f.getVersion());if(!Set.of("MENSUAL","QUINCENAL","SEMANAL").contains(Objects.toString(f.getFrecuencia(),""))||f.getDescripcion()!=null&&f.getDescripcion().length()>2000)throw regla("Selecciona una frecuencia válida; descripción de máximo 2000 caracteres.");
  if(id!=null){var a=calendario(id,true);version(a.version(),f.getVersion());if(a.frecuenciaFija()&&!a.frecuencia().equals(f.getFrecuencia()))throw regla("Este calendario ya tiene periodos, incluso si fueron cancelados. Conserva su frecuencia o crea otro calendario.");}
  p.addValue("id",id==null?0L:id).addValue("codigo",f.getCodigo().toUpperCase(Locale.ROOT)).addValue("nombre",f.getNombre().strip()).addValue("descripcion",OrganizacionLaboralService.texto(f.getDescripcion(),2000)).addValue("frecuencia",f.getFrecuencia()).addValue("activo",f.isActivo()).addValue("version",f.getVersion());
  if(jdbc.queryForObject("SELECT count(*) FROM calendario_nomina WHERE institucion_id=:institucion AND codigo=:codigo AND id<>:id",p,Long.class)>0)throw regla("Ya existe un calendario con ese código en tu institución.");
  if(id==null)return jdbc.queryForObject("INSERT INTO calendario_nomina(institucion_id,codigo,nombre,descripcion,frecuencia,activo,creado_por_id,actualizado_por_id) VALUES(:institucion,:codigo,:nombre,:descripcion,:frecuencia,:activo,:actor,:actor) RETURNING id",p,Long.class);
  jdbc.update("UPDATE calendario_nomina SET codigo=:codigo,nombre=:nombre,descripcion=:descripcion,frecuencia=:frecuencia,activo=:activo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id AND institucion_id=:institucion",p);return id;
 }
 @Transactional public Long guardarPeriodo(Long id,PeriodoNominaForm f){
  var p=parametros(true);institucionActiva(p);
  // Serializa altas por institución; no inventa un calendario oculto.
  jdbc.queryForObject("SELECT id FROM institucion WHERE id=:institucion FOR UPDATE",p,Long.class);
  String nombre=f.getNombre()==null||f.getNombre().isBlank()?f.getCodigo():f.getNombre();
  String razon=f.getMotivo()==null||f.getMotivo().isBlank()?(id==null?"Registro de periodo independiente":"Actualización de fechas del periodo"):f.getMotivo();
  identidad(f.getCodigo(),nombre,f.getVersion());motivo(razon);
  Periodo anterior=null;if(id!=null){anterior=periodo(id,true);version(anterior.version(),f.getVersion());if(!anterior.estado().equals("PROGRAMADO"))throw regla("El periodo fue cancelado; se conserva sólo para consulta.");periodoLibre(id);if(f.getNombre()==null||f.getNombre().isBlank())nombre=anterior.nombre();}
  if(f.getInicio()==null||f.getFin()==null||f.getPago()==null)throw regla("Captura inicio, fin y fecha prevista de pago.");
  if(f.getFin().isBefore(f.getInicio()))throw regla("El fin del periodo no puede ser anterior al inicio.");
  if(f.getPago().isBefore(f.getInicio()))throw regla("La fecha prevista de pago no puede ser anterior al inicio del periodo.");
  if(f.getCorte()!=null&&f.getCorte().isBefore(f.getInicio()))throw regla("La fecha límite para recibir incidencias de nómina no puede ser anterior al inicio del periodo.");
  if(f.getCorte()!=null&&f.getPago().isBefore(f.getCorte()))throw regla("La fecha prevista de pago debe ser igual o posterior a la fecha límite para recibir incidencias de nómina.");
  p.addValue("id",id==null?0L:id).addValue("codigo",f.getCodigo().toUpperCase(Locale.ROOT)).addValue("nombre",nombre.strip()).addValue("inicio",f.getInicio()).addValue("fin",f.getFin()).addValue("corte",f.getCorte(),java.sql.Types.DATE).addValue("pago",f.getPago()).addValue("motivo",razon.strip());
  if(jdbc.queryForObject("SELECT count(*) FROM periodo_nomina WHERE institucion_id=:institucion AND codigo=:codigo AND id<>:id",p,Long.class)>0)throw regla("Ya existe un periodo con ese código en tu institución, incluso si fue cancelado. Usa otro código.");
  if(jdbc.queryForObject("SELECT count(*) FROM periodo_nomina WHERE institucion_id=:institucion AND estado='PROGRAMADO' AND id<>:id AND fecha_inicio=:inicio AND fecha_fin=:fin",p,Long.class)>0)throw regla("Ya existe un periodo con esa misma cobertura. Usa el periodo existente.");
  if(id==null)return jdbc.queryForObject("INSERT INTO periodo_nomina(institucion_id,codigo,nombre,fecha_inicio,fecha_fin,fecha_corte,fecha_pago,motivo_registro,motivo_operacion,creado_por_id,actualizado_por_id) VALUES(:institucion,:codigo,:nombre,:inicio,:fin,:corte,:pago,:motivo,:motivo,:actor,:actor) RETURNING id",p,Long.class);
  jdbc.update("UPDATE periodo_nomina SET codigo=:codigo,nombre=:nombre,fecha_inicio=:inicio,fecha_fin=:fin,fecha_corte=:corte,fecha_pago=:pago,motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id AND institucion_id=:institucion",p);return id;
 }
 @Transactional public void cancelar(Long id,Long enviada,String m){
  var p=parametros(true);institucionActiva(p);motivo(m);var previo=periodo(id);if(previo.calendarioId()!=null)calendario(previo.calendarioId(),true);var actual=periodo(id,true);version(actual.version(),enviada);if(!actual.estado().equals("PROGRAMADO"))throw regla("El periodo ya estaba cancelado; no se realizó otra operación.");
  periodoLibre(id);jdbc.update("UPDATE periodo_nomina SET estado='CANCELADO',motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id AND institucion_id=:institucion",p.addValue("id",id).addValue("motivo",m.strip()));
 }
 private void periodoLibre(Long id){if(Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM nomina_borrador WHERE periodo_id=:id AND institucion_id=:institucion AND estado<>'CANCELADO') OR EXISTS(SELECT 1 FROM concepto_empleado WHERE periodo_id=:id AND institucion_id=:institucion AND estado='ACTIVA')",parametros(true).addValue("id",id),Boolean.class)))throw regla("El periodo ya tiene nóminas o conceptos únicos asociados. Conserva sus fechas e historial; no se modificó ningún registro.");}
 public Page<Historia> historial(boolean periodos,Long id,int pagina){
  if(periodos)periodo(id);else calendario(id);var p=parametros(false).addValue("tipo",periodos?"PERIODO":"CALENDARIO").addValue("id",id);var pg=PageRequest.of(Math.max(0,pagina),20);Long total=jdbc.queryForObject("SELECT count(*) FROM programacion_nomina_historial WHERE institucion_id=:institucion AND tipo=:tipo AND registro_id=:id",p,Long.class);p.addValue("offset",pg.getOffset());
  var filas=jdbc.query("SELECT h.id,to_char(h.ocurrido_en AT TIME ZONE i.zona_horaria,'DD/MM/YYYY HH24:MI') fecha,u.username usuario,h.operacion,CASE WHEN h.tipo='PERIODO' THEN (h.datos->'despues'->>'estado')||' · '||(h.datos->'despues'->>'motivo_operacion') ELSE (h.datos->'despues'->>'codigo')||' · '||(h.datos->'despues'->>'nombre')||' · '||(h.datos->'despues'->>'frecuencia')||' · '||CASE WHEN (h.datos->'despues'->>'activo')::boolean THEN 'Activo' ELSE 'Inactivo' END END detalle FROM programacion_nomina_historial h JOIN institucion i ON i.id=h.institucion_id JOIN usuario u ON u.id=h.usuario_id WHERE h.institucion_id=:institucion AND h.tipo=:tipo AND h.registro_id=:id ORDER BY h.id DESC LIMIT 20 OFFSET :offset",p,(r,n)->new Historia(r.getLong("id"),r.getString("fecha"),r.getString("usuario"),r.getString("operacion"),r.getString("detalle")));
  return new PageImpl<>(filas,pg,total==null?0:total);
 }
 private static ReglaNegocioException regla(String mensaje){return new ReglaNegocioException(mensaje);}
}
