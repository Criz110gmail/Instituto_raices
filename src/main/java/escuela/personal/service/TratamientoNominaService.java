package escuela.personal.service;

import escuela.common.exception.ReglaNegocioException;
import escuela.admin.dto.OpcionAutocompletado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.data.domain.*;
import java.math.BigDecimal;
import java.util.*;

/** Excepciones auditadas: nunca modifica la configuración ni la fotografía original. */
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class TratamientoNominaService {
 private final NamedParameterJdbcTemplate jdbc;
 private final EmpleadoService empleados;
 private final PreparacionNominaService preparacion;
 public record Linea(String clave,String codigo,String nombre,String naturaleza,BigDecimal importe,String accion,String motivo){}
 public record Evento(String fecha,String usuario,String clave,String accion,String motivo){}
 public record Resumen(BigDecimal ahora,BigDecimal pospuesto,BigDecimal noAplicado,String accion){}
 public record ResumenLote(BigDecimal ahora,BigDecimal pospuesto,BigDecimal noAplicado){}
 public record Pendiente(Long id,Long borradorId,Long filaId,String codigo,String nombre,String periodo,BigDecimal importe,String moneda,String estado,Long liquidacionId){}
 private MapSqlParameterSource p(boolean editar){var a=empleados.autorizarSalarios(editar);if(editar&&a.usuarioId()==null)throw new org.springframework.security.access.AccessDeniedException("Se requiere una cuenta administrativa identificable");return new MapSqlParameterSource("inst",a.institucionId()).addValue("actor",a.usuarioId());}
 private MapSqlParameterSource fila(Long id,Long fila,boolean editar){preparacion.empleadoGuardado(id,fila);return p(editar).addValue("id",id).addValue("fila",fila);}
 public ResumenLote resumenLote(Long id){
  var b=preparacion.obtener(id);return jdbc.queryForObject("SELECT coalesce(sum(CASE WHEN ne.tratamiento='APLICAR' AND NOT EXISTS(SELECT 1 FROM nomina_pago_intento ni WHERE ni.fila_id=ne.id AND ni.estado='PAGADO') THEN ne.propuesto ELSE 0 END),0) ahora,coalesce(sum(CASE WHEN ne.tratamiento='POSPONER' AND NOT EXISTS(SELECT 1 FROM nomina_pago_intento ni WHERE ni.fila_id=ne.id AND ni.estado='PAGADO') THEN ne.propuesto WHEN ne.tratamiento='APLICAR' AND NOT EXISTS(SELECT 1 FROM nomina_pendiente np JOIN nomina_borrador hijo ON hijo.pendiente_origen_id=np.id JOIN nomina_borrador_empleado h ON h.borrador_id=hijo.id JOIN nomina_pago_intento ni ON ni.fila_id=h.id AND ni.estado='PAGADO' WHERE np.fila_origen_id=ne.id) THEN coalesce((SELECT sum(CASE WHEN l.naturaleza='DEDUCCION' THEN -l.importe ELSE l.importe END) FROM nomina_linea_tratamiento l WHERE l.fila_id=ne.id AND l.accion='POSPONER'),0) ELSE 0 END),0) pospuesto,coalesce(sum(CASE WHEN ne.tratamiento='NO_APLICAR' THEN original.propuesto ELSE coalesce((SELECT sum(CASE WHEN l.naturaleza='DEDUCCION' THEN -l.importe ELSE l.importe END) FROM nomina_linea_tratamiento l WHERE l.fila_id=ne.id AND l.accion='NO_APLICAR'),0) END),0) no_aplicado FROM nomina_fila_efectiva ne JOIN nomina_borrador_empleado original ON original.id=ne.id WHERE ne.borrador_id=:id AND ne.revision=:revision AND ne.institucion_id=:inst",p(false).addValue("id",id).addValue("revision",b.revision()),(r,n)->new ResumenLote(r.getBigDecimal("ahora"),r.getBigDecimal("pospuesto"),r.getBigDecimal("no_aplicado")));
 }
 public List<Linea> lineas(Long id,Long fila){return jdbc.query("SELECT * FROM nomina_linea_tratamiento WHERE fila_id=:fila AND institucion_id=:inst ORDER BY categoria,clave",fila(id,fila,false),(r,n)->new Linea(r.getString("clave"),r.getString("codigo"),r.getString("nombre"),r.getString("naturaleza"),r.getBigDecimal("importe"),r.getString("accion"),r.getString("motivo")));}
 public Resumen resumen(Long id,Long fila){return jdbc.queryForObject("SELECT ne.propuesto,ne.tratamiento,original.propuesto original,coalesce((SELECT sum(CASE WHEN l.naturaleza='DEDUCCION' THEN -l.importe ELSE l.importe END) FROM nomina_linea_tratamiento l WHERE l.fila_id=ne.id AND l.accion='POSPONER'),0) pospuesto FROM nomina_fila_efectiva ne JOIN nomina_borrador_empleado original ON original.id=ne.id WHERE ne.id=:fila AND ne.institucion_id=:inst",fila(id,fila,false),(r,n)->{var accion=r.getString("tratamiento");return new Resumen(accion.equals("APLICAR")?r.getBigDecimal("propuesto"):BigDecimal.ZERO,accion.equals("POSPONER")?r.getBigDecimal("propuesto"):r.getBigDecimal("pospuesto"),accion.equals("NO_APLICAR")?r.getBigDecimal("original"):BigDecimal.ZERO,accion);});}
 public Page<Evento> historial(Long id,Long fila,int pagina){var par=fila(id,fila,false);var pg=PageRequest.of(Math.max(0,pagina),20);long n=jdbc.queryForObject("SELECT count(*) FROM nomina_tratamiento WHERE fila_id=:fila",par,Long.class);return new PageImpl<>(jdbc.query("SELECT t.*,u.username,to_char(t.ocurrido_en AT TIME ZONE i.zona_horaria,'DD/MM/YYYY HH24:MI') fecha FROM nomina_tratamiento t JOIN usuario u ON u.id=t.usuario_id JOIN institucion i ON i.id=u.institucion_id WHERE t.fila_id=:fila ORDER BY t.id DESC LIMIT 20 OFFSET :offset",par.addValue("offset",pg.getOffset()),(r,k)->new Evento(r.getString("fecha"),r.getString("username"),r.getString("clave"),r.getString("accion"),r.getString("motivo"))),pg,n);}
 @Transactional public void decidir(Long id,Long fila,Long version,String clave,String accion,String motivo,boolean confirmado){
  var par=fila(id,fila,true);if(!Boolean.TRUE.equals(jdbc.queryForObject("SELECT activo FROM institucion WHERE id=:inst FOR SHARE",par,Boolean.class)))throw regla("La institución está inactiva; sólo consulta.");var inicial=preparacion.obtener(id);
  jdbc.queryForObject("SELECT id FROM periodo_nomina WHERE id=:periodo AND institucion_id=:inst FOR UPDATE",par.addValue("periodo",inicial.periodoId()),Long.class);
  jdbc.queryForObject("SELECT id FROM nomina_borrador WHERE id=:id AND institucion_id=:inst FOR UPDATE",par,Long.class);
  var b=preparacion.obtener(id);if(!b.estado().equals("BORRADOR")||!Objects.equals(b.version(),version))throw regla("El borrador cambió o ya fue aplicado. Recarga antes de continuar; no se guardaron decisiones.");
  if(!confirmado)throw regla("Revisa y confirma el tratamiento antes de guardar.");
  if(motivo==null||motivo.isBlank()||motivo.length()>2000)throw regla("Explica el motivo obligatorio con máximo 2000 caracteres.");
  if(!Set.of("APLICAR","NO_APLICAR","POSPONER").contains(Objects.toString(accion,"")))throw regla("Selecciona un tratamiento válido.");
  if(jdbc.queryForObject("SELECT count(*) FROM nomina_borrador WHERE id=:id AND pendiente_origen_id IS NOT NULL",par,Long.class)>0)throw regla("Una obligación ya confirmada conserva sus importes; no puede excluirse ni volver a posponerse.");
  if(jdbc.queryForObject("SELECT jsonb_array_length(coalesce(fuentes->'prestamos','[]'::jsonb)) FROM nomina_borrador_empleado WHERE id=:fila",par,Integer.class)>0)throw regla("Las cuotas de préstamos conservan su flujo especializado; no cambies su recuperación desde estas excepciones.");
  var resumen=resumen(id,fila);var ls=lineas(id,fila);
  if(!"EMPLEADO".equals(clave)){
   var l=ls.stream().filter(x->x.clave().equals(clave)).findFirst().orElseThrow(()->regla("Selecciona un concepto de esta revisión."));
   if(!Set.of("PERCEPCION","DEDUCCION").contains(l.naturaleza()))throw regla("El sueldo base y las aportaciones no se modifican como conceptos adicionales.");
   if(!resumen.accion().equals("APLICAR"))throw regla("Primero restablece Incluir pago completo para decidir por concepto.");
  }else if(!accion.equals("APLICAR")&&ls.stream().anyMatch(x->!x.accion().equals("APLICAR")))throw regla("Restablece los conceptos a Aplicar antes de decidir sobre todo el empleado; no se mezclan tratamientos.");
  String anterior="EMPLEADO".equals(clave)?resumen.accion():ls.stream().filter(x->x.clave().equals(clave)).findFirst().orElseThrow().accion();
  if(anterior.equals(accion))throw regla("Ese tratamiento ya está registrado. No se duplicó la operación.");
  jdbc.update("INSERT INTO nomina_tratamiento(fila_id,clave,accion,motivo,usuario_id) VALUES(:fila,:clave,:accion,:motivo,:actor)",par.addValue("clave",clave).addValue("accion",accion).addValue("motivo",motivo.strip()));
  var neto=jdbc.queryForObject("SELECT propuesto FROM nomina_fila_efectiva WHERE id=:fila",par,BigDecimal.class);
  if(!("EMPLEADO".equals(clave)&&accion.equals("NO_APLICAR"))&&neto.signum()<=0)throw regla("El resultado es cero o negativo. Esta entrega no liquida netos cero ni negativos; no se guardó el cambio.");
  jdbc.update("UPDATE nomina_borrador SET version=version+1,motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP WHERE id=:id AND institucion_id=:inst",par);
 }
 private static ReglaNegocioException regla(String s){return new ReglaNegocioException(s);}
 public BigDecimal proyectar(Long id,Long fila,String clave,String accion){
  var r=resumen(id,fila);var e=preparacion.empleadoGuardado(id,fila);
  if("EMPLEADO".equals(clave))return "APLICAR".equals(accion)?jdbc.queryForObject("SELECT propuesto FROM nomina_borrador_empleado WHERE id=:fila AND institucion_id=:inst",fila(id,fila,false),BigDecimal.class):BigDecimal.ZERO;
  var l=lineas(id,fila).stream().filter(x->x.clave().equals(clave)).findFirst().orElseThrow(()->regla("Concepto no disponible."));
  BigDecimal efecto=l.naturaleza().equals("DEDUCCION")?l.importe().negate():l.importe();
  return e.propuesto().add(l.accion().equals("APLICAR")?BigDecimal.ZERO:efecto).subtract("APLICAR".equals(accion)?BigDecimal.ZERO:efecto);
 }
 @Transactional public Long prepararPendiente(Long id,Long pendiente,boolean confirmado){
  if(!confirmado)throw regla("Confirma que deseas pagar este pendiente en su periodo original.");
  var par=p(true).addValue("id",id).addValue("pendiente",pendiente);if(!Boolean.TRUE.equals(jdbc.queryForObject("SELECT activo FROM institucion WHERE id=:inst FOR SHARE",par,Boolean.class)))throw regla("La institución está inactiva; sólo consulta.");var b=preparacion.obtener(id);
  jdbc.queryForObject("SELECT id FROM periodo_nomina WHERE id=:periodo AND institucion_id=:inst FOR UPDATE",par.addValue("periodo",b.periodoId()),Long.class);
  jdbc.queryForObject("SELECT id FROM nomina_borrador WHERE id=:id AND institucion_id=:inst FOR UPDATE",par,Long.class);
  var datos=jdbc.queryForList("SELECT p.*,ne.empleado_id,ne.empleado_codigo,ne.empleado_nombre,ne.moneda FROM nomina_pendiente p JOIN nomina_borrador_empleado ne ON ne.id=p.fila_origen_id WHERE p.id=:pendiente AND p.institucion_id=:inst AND ne.borrador_id=:id FOR UPDATE OF p",par).stream().findFirst().orElseThrow(()->regla("Pendiente no disponible para esta institución y nómina."));
  var existente=jdbc.queryForList("SELECT id FROM nomina_borrador WHERE pendiente_origen_id=:pendiente",par,Long.class);if(!existente.isEmpty())return existente.getFirst();
  if(((BigDecimal)datos.get("importe")).signum()<=0)throw regla("Este pendiente requiere recuperación de deducciones o liquidación sin efectivo. Se conserva visible; no se genera una salida ficticia. Su tratamiento contable especializado sigue pendiente.");
  Long nuevo=jdbc.queryForObject("INSERT INTO nomina_borrador(institucion_id,periodo_id,nombre,motivo_operacion,creado_por_id,actualizado_por_id,pendiente_origen_id) VALUES(:inst,:periodo,'Pago de conceptos pospuestos','Liquidación individual del periodo original',:actor,:actor,:pendiente) RETURNING id",par,Long.class);par.addValue("nuevo",nuevo);
  jdbc.update("INSERT INTO nomina_borrador_revision(borrador_id,numero,institucion_id,periodo_id,periodo_codigo,periodo_nombre,calendario_id,calendario_texto,fecha_inicio,fecha_fin,fecha_pago,moneda,datos_periodo,sello_periodo,motivo,creado_por_id) SELECT :nuevo,1,institucion_id,periodo_id,periodo_codigo,periodo_nombre,calendario_id,calendario_texto,fecha_inicio,fecha_fin,fecha_pago,moneda,datos_periodo,sello_periodo,'Fotografía del pendiente original',:actor FROM nomina_borrador_revision WHERE borrador_id=:id AND numero=:revision",par.addValue("revision",b.revision()));
  jdbc.update("INSERT INTO nomina_borrador_empleado(borrador_id,revision,institucion_id,periodo_id,empleado_id,empleado_codigo,empleado_nombre,fecha_inicio,fecha_fin,sueldo,percepciones,deducciones,aportaciones,propuesto,moneda,fuentes,sello,activo) SELECT :nuevo,1,ne.institucion_id,ne.periodo_id,ne.empleado_id,ne.empleado_codigo,ne.empleado_nombre,ne.fecha_inicio,ne.fecha_fin,0,coalesce((SELECT sum((x->>'importe')::numeric) FROM jsonb_array_elements(p.detalle) x WHERE x->>'naturaleza'='PERCEPCION'),0),coalesce((SELECT sum((x->>'importe')::numeric) FROM jsonb_array_elements(p.detalle) x WHERE x->>'naturaleza'='DEDUCCION'),0),0,p.importe,ne.moneda,jsonb_build_object('conceptos',(SELECT jsonb_agg((x->'fuente')||jsonb_build_object('importe',(x->>'importe')::numeric,'concepto_codigo',x->>'codigo','concepto_nombre',x->>'nombre','naturaleza',x->>'naturaleza')) FROM jsonb_array_elements(p.detalle) x),'sueldos','[]'::jsonb,'ocasionales','[]'::jsonb),ne.sello,false FROM nomina_pendiente p JOIN nomina_borrador_empleado ne ON ne.id=p.fila_origen_id WHERE p.id=:pendiente AND p.institucion_id=:inst",par);
  return nuevo;
 }
 public List<OpcionAutocompletado> buscarEmpleadosPendientes(Long id,String q){
  preparacion.obtener(id);var texto=OrganizacionLaboralService.texto(q,200);
  if(!texto.isBlank()&&texto.length()<3)return List.of();
  return jdbc.query("SELECT DISTINCT ne.empleado_id,ne.empleado_codigo,ne.empleado_nombre FROM nomina_pendiente p JOIN nomina_borrador_empleado ne ON ne.id=p.fila_origen_id WHERE ne.borrador_id=:id AND ne.institucion_id=:inst AND p.institucion_id=:inst AND strpos(lower(ne.empleado_codigo||' '||ne.empleado_nombre),lower(:q))>0 ORDER BY ne.empleado_nombre,ne.empleado_id LIMIT 11",p(false).addValue("id",id).addValue("q",texto),(r,n)->new OpcionAutocompletado(r.getLong("empleado_id"),r.getString("empleado_codigo")+" · "+r.getString("empleado_nombre"),"Empleado con conceptos pospuestos en esta nómina"));
 }
 public Page<Pendiente> pendientes(Long id,String buscar,String estado,int pagina){return pendientes(id,buscar,estado,pagina,null);}
 public Page<Pendiente> pendientes(Long id,String buscar,String estado,int pagina,Long empleadoId){
  preparacion.obtener(id);var pg=PageRequest.of(Math.max(0,pagina),25);var par=p(false).addValue("id",id).addValue("q",OrganizacionLaboralService.texto(buscar,200)).addValue("estado",Set.of("PENDIENTE","PAGADO").contains(Objects.toString(estado,""))?estado:"TODOS");
  par.addValue("empleado",empleadoId);
  String sql=" FROM nomina_pendiente p JOIN nomina_borrador_empleado ne ON ne.id=p.fila_origen_id JOIN periodo_nomina pe ON pe.id=ne.periodo_id LEFT JOIN nomina_borrador hijo ON hijo.pendiente_origen_id=p.id WHERE ne.borrador_id=:id AND p.institucion_id=:inst AND ne.institucion_id=:inst AND (CAST(:empleado AS bigint) IS NULL OR ne.empleado_id=:empleado) AND strpos(lower(ne.empleado_codigo||' '||ne.empleado_nombre),lower(:q))>0 ";
  String pagado="EXISTS(SELECT 1 FROM nomina_borrador_empleado h JOIN nomina_pago_intento ni ON ni.fila_id=h.id AND ni.estado='PAGADO' WHERE h.borrador_id=hijo.id)";
  sql+=" AND (:estado='TODOS' OR (:estado='PAGADO')="+pagado+") ";
  long n=jdbc.queryForObject("SELECT count(*)"+sql,par,Long.class);
  return new PageImpl<>(jdbc.query("SELECT p.id,ne.borrador_id,ne.id fila_id,ne.empleado_codigo,ne.empleado_nombre,pe.codigo,p.importe,ne.moneda,hijo.id liquidacion_id,CASE WHEN "+pagado+" THEN 'PAGADO' ELSE 'PENDIENTE' END estado"+sql+" ORDER BY p.id LIMIT 25 OFFSET :offset",par.addValue("offset",pg.getOffset()),(r,k)->new Pendiente(r.getLong("id"),r.getLong("borrador_id"),r.getLong("fila_id"),r.getString("empleado_codigo"),r.getString("empleado_nombre"),r.getString("codigo"),r.getBigDecimal("importe"),r.getString("moneda"),r.getString("estado"),r.getObject("liquidacion_id",Long.class))),pg,n);
 }
}
