package escuela.personal.service;

import escuela.personal.dto.*;
import escuela.personal.repository.EmpleadoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class CondicionesSalarialesService {
 private final EmpleadoService empleados;
 private final EmpleadoRepository expedientes;
 private final NamedParameterJdbcTemplate jdbc;
 public record Puesto(Long id,String codigo,String nombre,String descripcion,boolean activo,Long version){}
 public record Condicion(Long id,String puestoCodigo,String puestoNombre,BigDecimal importeBruto,String moneda,
  String base,String frecuencia,LocalDate inicio,LocalDate fin,String estado,String motivo,Long version){}
 public record Historia(Long id,String fecha,String usuario,String operacion,String motivo){}
 private static final RowMapper<Puesto> PUESTO=(r,n)->new Puesto(r.getLong("id"),r.getString("codigo"),r.getString("nombre"),r.getString("descripcion"),r.getBoolean("activo"),r.getLong("version"));
 private static final RowMapper<Condicion> CONDICION=(r,n)->new Condicion(r.getLong("id"),r.getString("puesto_codigo"),r.getString("puesto_nombre"),r.getBigDecimal("importe_bruto"),r.getString("moneda"),r.getString("base"),r.getString("frecuencia"),r.getObject("fecha_inicio",LocalDate.class),r.getObject("fecha_fin",LocalDate.class),r.getString("estado"),r.getString("motivo"),r.getLong("version"));
 public static String etiqueta(String valor){return switch(valor){case "MENSUAL"->"Mensual";case "SEMANAL"->"Semanal";case "DIARIA"->"Diaria";case "POR_HORA"->"Por hora";case "QUINCENAL"->"Quincenal";default->valor;};}
 private MapSqlParameterSource parametros(UsuarioPrincipal p){return new MapSqlParameterSource("institucion",p.institucionId()).addValue("actor",p.usuarioId());}
 private LocalDate hoy(Long id){return empleados.hoyLaboral(id);}
 public String moneda(Long id){autorizar(id,false);return empleados.autorizar(id,true,false).getInstitucion().getMonedaPredeterminada();}
 private void autorizar(Long id,boolean escribir){empleados.autorizarSalarios(escribir);empleados.autorizar(id,true,false);}
 private void bloquear(Long id){expedientes.bloquear(id).orElseThrow(()->regla("Empleado no disponible."));}
 public Page<Puesto> puestos(String q,String estado,int pagina,int tamanio){
  var p=parametros(empleados.autorizarSalarios(false));String texto=q==null?"":q.strip();
  p.addValue("q",texto.substring(0,Math.min(200,texto.length()))).addValue("estado",Set.of("ACTIVO","INACTIVO").contains(estado==null?"":estado)?estado:"TODOS");
  var pg=PageRequest.of(Math.max(0,pagina),Set.of(10,25,50,100).contains(tamanio)?tamanio:25);
  String where=" FROM puesto_laboral WHERE institucion_id=:institucion AND strpos(lower(codigo||' '||nombre),lower(:q))>0 AND (:estado='TODOS' OR activo=(:estado='ACTIVO'))";
  Long total=jdbc.queryForObject("SELECT count(*)"+where,p,Long.class);
  p.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset());
  return new PageImpl<>(jdbc.query("SELECT *"+where+" ORDER BY nombre,id LIMIT :limite OFFSET :offset",p,PUESTO),pg,total==null?0:total);
 }
 public List<Puesto> buscarPuestos(String q){
  var p=parametros(empleados.autorizarSalarios(false));String t=q==null?"":q.strip();
  if(!t.isEmpty()&&t.length()<3)return List.of();
  p.addValue("q",t.substring(0,Math.min(t.length(),100)));
  return jdbc.query("SELECT * FROM puesto_laboral WHERE institucion_id=:institucion AND activo AND strpos(lower(codigo||' '||nombre),lower(:q))>0 ORDER BY nombre,id LIMIT 11",p,PUESTO);
 }
 public Puesto puesto(Long id){return puesto(id,false);}
 private Puesto puesto(Long id,boolean bloquear){
  var p=parametros(empleados.autorizarSalarios(bloquear)).addValue("id",id);
  return jdbc.query("SELECT * FROM puesto_laboral WHERE id=:id AND institucion_id=:institucion"+(bloquear?" FOR UPDATE":""),p,PUESTO).stream().findFirst().orElseThrow(()->new AccessDeniedException("Puesto no autorizado"));
 }
 @Transactional public Long guardarPuesto(Long id,PuestoForm f){
  var p=parametros(empleados.autorizarSalarios(true));
  if(!Boolean.TRUE.equals(jdbc.queryForObject("SELECT activo FROM institucion WHERE id=:institucion",p,Boolean.class)))throw regla("La institución está inactiva; sólo se permite consultar su configuración.");
  if(f.getCodigo()==null||f.getNombre()==null||f.getCodigo().isBlank()||f.getNombre().isBlank())throw regla("Captura código y nombre del puesto.");
  p.addValue("codigo",f.getCodigo().strip().toUpperCase(Locale.ROOT)).addValue("nombre",f.getNombre().strip())
   .addValue("descripcion",f.getDescripcion()==null?"":f.getDescripcion().strip()).addValue("activo",f.isActivo());
  if(id==null)return jdbc.queryForObject("INSERT INTO puesto_laboral(institucion_id,codigo,nombre,descripcion,activo,creado_por_id,actualizado_por_id) VALUES(:institucion,:codigo,:nombre,:descripcion,:activo,:actor,:actor) RETURNING id",p,Long.class);
  var actual=puesto(id,true);if(!Objects.equals(actual.version(),f.getVersion()))throw regla("El puesto cambió desde que lo abriste. Recarga antes de guardar.");
  p.addValue("id",id).addValue("version",f.getVersion());
  int n=jdbc.update("UPDATE puesto_laboral SET codigo=:codigo,nombre=:nombre,descripcion=:descripcion,activo=:activo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id AND institucion_id=:institucion AND version=:version",p);
  if(n!=1)throw regla("El puesto cambió; recarga la información.");return id;
 }
 public long revision(Long id){autorizar(id,false);return revisionInterna(id);}
 private long revisionInterna(Long id){var n=jdbc.queryForObject("SELECT coalesce(max(id),0) FROM personal_salarial_historial WHERE empleado_id=:empleado",new MapSqlParameterSource("empleado",id),Long.class);return n==null?0:n;}
 private MapSqlParameterSource filtro(Long id,FiltroSalarial original){
  autorizar(id,false);var f=original.normalizado();
  if(f.desde()!=null&&f.hasta()!=null&&f.desde().isAfter(f.hasta()))throw regla("El inicio del rango debe ser anterior o igual al fin.");
  return new MapSqlParameterSource("empleado",id).addValue("q",f.q()).addValue("estado",f.estado()).addValue("hoy",hoy(id))
   .addValue("desde",f.desde(),java.sql.Types.DATE).addValue("hasta",f.hasta(),java.sql.Types.DATE);
 }
 private static final String WHERE="""
  FROM condicion_salarial WHERE empleado_id=:empleado
   AND strpos(lower(puesto_codigo||' '||puesto_nombre||' '||motivo),lower(:q))>0
   AND (:desde IS NULL OR fecha_fin IS NULL OR fecha_fin>=:desde)
   AND (:hasta IS NULL OR fecha_inicio<=:hasta)
   AND (:estado='TODAS' OR :estado='CANCELADA' AND estado='CANCELADA'
    OR estado='ACTIVA' AND (:estado='ACTUAL' AND fecha_inicio<=:hoy AND (fecha_fin IS NULL OR fecha_fin>=:hoy)
     OR :estado='PROXIMA' AND fecha_inicio>:hoy OR :estado='ANTERIOR' AND fecha_fin<:hoy))
  """;
 public Page<Condicion> listar(Long id,FiltroSalarial original){
  var f=original.normalizado();var p=filtro(id,f);var pg=PageRequest.of(f.pagina(),f.tamanio());
  Long total=jdbc.queryForObject("SELECT count(*) "+WHERE,p,Long.class);
  p.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset());
  return new PageImpl<>(jdbc.query("SELECT * "+WHERE+" ORDER BY fecha_inicio DESC,id DESC LIMIT :limite OFFSET :offset",p,CONDICION),pg,total==null?0:total);
 }
 public Condicion aplicable(Long id,LocalDate fecha){autorizar(id,false);return jdbc.query("SELECT * FROM condicion_salarial WHERE empleado_id=:empleado AND estado='ACTIVA' AND fecha_inicio<=:fecha AND (fecha_fin IS NULL OR fecha_fin>=:fecha) ORDER BY id LIMIT 1",new MapSqlParameterSource("empleado",id).addValue("fecha",fecha==null?hoy(id):fecha),CONDICION).stream().findFirst().orElse(null);}
 public Page<Historia> historial(Long id,int pagina){
  autorizar(id,false);var p=new MapSqlParameterSource("empleado",id).addValue("zona",empleados.autorizar(id,true,false).getInstitucion().getZonaHoraria());
  Long total=jdbc.queryForObject("SELECT count(*) FROM personal_salarial_historial WHERE empleado_id=:empleado",p,Long.class);var pg=PageRequest.of(Math.max(0,pagina),20);
  p.addValue("offset",pg.getOffset());
  var filas=jdbc.query("SELECT id,to_char(ocurrido_en AT TIME ZONE :zona,'DD/MM/YYYY HH24:MI') fecha,usuario_id::text usuario,operacion,datos->'despues'->>'motivo_operacion' motivo FROM personal_salarial_historial WHERE empleado_id=:empleado ORDER BY id DESC LIMIT 20 OFFSET :offset",p,(r,n)->new Historia(r.getLong("id"),r.getString("fecha"),r.getString("usuario"),r.getString("operacion"),r.getString("motivo")));
  return new PageImpl<>(filas,pg,total==null?0:total);
 }
 @Transactional public Long programar(Long id,CondicionSalarialForm f){
  autorizar(id,true);bloquear(id);var e=empleados.autorizar(id,true,false);var l=empleados.laboral(id);var hoy=hoy(id);
  if(!e.getInstitucion().isActivo())throw regla("La institución está inactiva; no se registran condiciones nuevas.");
  if(!e.isActivo()||!"ACTIVO".equals(l.getSituacion())||l.getFechaIngreso()==null)throw regla("Confirma primero la actividad y fecha de ingreso en Datos laborales. No se configura sueldo a personal en baja o por confirmar.");
  if(!Objects.equals(l.getVersion(),f.getVersionLaboral())||!Objects.equals(revisionInterna(id),f.getRevision()))throw regla("Los datos laborales o las condiciones cambiaron. Recarga y revisa antes de confirmar.");
  if(f.getFechaInicio()==null||f.getFechaInicio().isBefore(hoy)||f.getFechaInicio().isBefore(l.getFechaIngreso()))throw regla("La vigencia debe comenzar hoy o después y no antes del ingreso laboral; no se reescribe el pasado.");
  if(f.getFechaFin()!=null&&f.getFechaFin().isBefore(f.getFechaInicio()))throw regla("El fin de vigencia no puede ser anterior al inicio.");
  if(f.getImporteBruto()==null||f.getImporteBruto().signum()<=0||f.getImporteBruto().scale()>2||f.getImporteBruto().precision()-f.getImporteBruto().scale()>17)throw regla("Captura un sueldo bruto positivo con máximo dos decimales.");
  if(f.getBase()==null||f.getFrecuencia()==null||!Set.of("MENSUAL","SEMANAL","DIARIA","POR_HORA").contains(f.getBase())||!Set.of("SEMANAL","QUINCENAL","MENSUAL").contains(f.getFrecuencia()))throw regla("Selecciona una base y frecuencia válidas.");
  if(f.getMotivo()==null||f.getMotivo().isBlank()||f.getMotivo().length()>2000)throw regla("Explica el motivo de la condición salarial (máximo 2000 caracteres).");
  var p=parametros(empleados.autorizarSalarios(true)).addValue("empleado",id).addValue("inicio",f.getFechaInicio());
  var puesto=puesto(f.getPuestoId(),true);if(!puesto.activo())throw regla("El puesto está inactivo; selecciona uno activo.");
  if(!Objects.equals(f.getPuestoTexto(),puesto.codigo()+" · "+puesto.nombre()))throw regla("El puesto cambió desde la selección. Quita la selección y vuelve a elegirlo antes de confirmar.");
  Long posteriores=jdbc.queryForObject("SELECT count(*) FROM condicion_salarial WHERE empleado_id=:empleado AND estado='ACTIVA' AND fecha_inicio>=:inicio",p,Long.class);
  if(posteriores!=null&&posteriores>0)throw regla("Ya existe una condición en esa fecha o posterior. Consulta la programación; no se insertan cambios entre vigencias existentes.");
  var previas=jdbc.query("SELECT * FROM condicion_salarial WHERE empleado_id=:empleado AND estado='ACTIVA' AND fecha_inicio<:inicio AND (fecha_fin IS NULL OR fecha_fin>=:inicio) FOR UPDATE",p,CONDICION);
  var anterior=previas.isEmpty()?null:previas.getFirst();
  if(anterior!=null){p.addValue("anterior",anterior.id()).addValue("cierre",f.getFechaInicio().minusDays(1)).addValue("motivoCierre","Sustituida por una nueva condición desde "+f.getFechaInicio());
   jdbc.update("UPDATE condicion_salarial SET fecha_fin=:cierre,motivo_operacion=:motivoCierre,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:anterior",p);}
  String moneda=e.getInstitucion().getMonedaPredeterminada();
  p.addValue("puesto",puesto.id()).addValue("codigo",puesto.codigo()).addValue("nombre",puesto.nombre()).addValue("importe",f.getImporteBruto())
   .addValue("moneda",moneda).addValue("base",f.getBase()).addValue("frecuencia",f.getFrecuencia()).addValue("fin",f.getFechaFin(),java.sql.Types.DATE)
   .addValue("motivo",f.getMotivo().strip()).addValue("anteriorId",anterior==null?null:anterior.id(),java.sql.Types.BIGINT).addValue("finAnterior",anterior==null?null:anterior.fin(),java.sql.Types.DATE);
  return jdbc.queryForObject("INSERT INTO condicion_salarial(empleado_id,institucion_id,puesto_id,puesto_codigo,puesto_nombre,importe_bruto,moneda,base,frecuencia,fecha_inicio,fecha_fin,motivo,motivo_operacion,anterior_id,fin_anterior,creado_por_id,actualizado_por_id) VALUES(:empleado,:institucion,:puesto,:codigo,:nombre,:importe,:moneda,:base,:frecuencia,:inicio,:fin,:motivo,:motivo,:anteriorId,:finAnterior,:actor,:actor) RETURNING id",p,Long.class);
 }
 @Transactional public void cancelar(Long empleadoId,Long condicionId,Long version,String motivo){
  autorizar(empleadoId,true);bloquear(empleadoId);
  if(motivo==null||motivo.isBlank()||motivo.length()>2000)throw regla("Explica por qué cancelas la programación (máximo 2000 caracteres).");
  var p=parametros(empleados.autorizarSalarios(true)).addValue("empleado",empleadoId).addValue("id",condicionId).addValue("motivo",motivo.strip());
  var fila=jdbc.queryForList("SELECT * FROM condicion_salarial WHERE empleado_id=:empleado AND id=:id FOR UPDATE",p).stream().findFirst().orElseThrow(()->new AccessDeniedException("Condición no autorizada"));
  if(!Objects.equals(((Number)fila.get("version")).longValue(),version)||!"ACTIVA".equals(fila.get("estado")))throw regla("La condición cambió; recarga antes de continuar.");
  LocalDate inicio=((java.sql.Date)fila.get("fecha_inicio")).toLocalDate();
  if(!inicio.isAfter(hoy(empleadoId)))throw regla("Sólo se cancela una programación que aún no inicia. Las condiciones vigentes o históricas se conservan.");
  p.addValue("inicio",inicio);
  Long n=jdbc.queryForObject("SELECT count(*) FROM condicion_salarial WHERE empleado_id=:empleado AND estado='ACTIVA' AND fecha_inicio>:inicio",p,Long.class);
  if(n!=null&&n>0)throw regla("Cancela primero la última programación futura; hay otra posterior que depende de ésta.");
  jdbc.update("UPDATE condicion_salarial SET estado='CANCELADA',motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id",p);
  if(fila.get("anterior_id")!=null){p.addValue("anterior",fila.get("anterior_id")).addValue("fin",fila.get("fin_anterior"),java.sql.Types.DATE).addValue("cierre",inicio.minusDays(1));
   int restauradas=jdbc.update("UPDATE condicion_salarial SET fecha_fin=:fin,motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:anterior AND empleado_id=:empleado AND estado='ACTIVA' AND fecha_fin=:cierre",p);
   if(restauradas!=1)throw regla("La vigencia anterior cambió. No se puede restaurar automáticamente; recarga la información.");}
 }
 private static ReglaNegocioException regla(String m){return new ReglaNegocioException(m);}
}
