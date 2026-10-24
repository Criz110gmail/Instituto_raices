package escuela.personal.service;

import escuela.personal.dto.*;
import escuela.admin.dto.OpcionAutocompletado;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.LocalDate;
import java.math.BigDecimal;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ConceptoPeriodoNominaService {
 private final EmpleadoService empleados;
 private final ConceptoNominaService catalogo;
 private final ProgramacionNominaService programacion;
 private final PreparacionNominaService preparacion;
 private final NamedParameterJdbcTemplate jdbc;
 public record Contexto(Long conceptoVersion,Long laboralVersion,Long calendarioVersion,String selloPeriodo,String concepto,String periodo,String moneda,String tipo,String inicio,String fin){}
 public record Fila(Long id,Long empleadoId,String concepto,String naturaleza,String periodo,BigDecimal importe,String moneda,LocalDate inicio,LocalDate fin,String estado,String motivo,String motivoOperacion,Long version,boolean aplicada){public String tipo(){return ConceptoNominaService.etiqueta(naturaleza);}}
 public record Historia(String fecha,String usuario,String motivo,String estado){}
 private MapSqlParameterSource p(Long empleado,boolean escribir){var a=empleados.autorizarSalarios(escribir);empleados.autorizar(empleado,true,false);return new MapSqlParameterSource("inst",a.institucionId()).addValue("actor",a.usuarioId()).addValue("emp",empleado);}
 private static final String APLICADA="EXISTS(SELECT 1 FROM nomina_aplicacion na JOIN nomina_borrador_empleado ne ON ne.borrador_id=na.borrador_id AND ne.revision=na.revision WHERE ne.empleado_id=o.empleado_id AND ne.fuentes->'ocasionales' @> jsonb_build_array(jsonb_build_object('id',o.id))) OR EXISTS(SELECT 1 FROM nomina_autorizacion na JOIN nomina_borrador_empleado ne ON ne.borrador_id=na.borrador_id AND ne.revision=na.revision WHERE ne.empleado_id=o.empleado_id AND ne.fuentes->'ocasionales' @> jsonb_build_array(jsonb_build_object('id',o.id)))";
 private static final String SELECT="SELECT o.*,"+APLICADA+" aplicada FROM concepto_empleado_periodo o ";
 private static final RowMapper<Fila> MAP=(r,n)->new Fila(r.getLong("id"),r.getLong("empleado_id"),r.getString("concepto_codigo")+" · "+r.getString("concepto_nombre"),r.getString("naturaleza"),r.getString("periodo_texto"),r.getBigDecimal("importe"),r.getString("moneda"),r.getObject("fecha_inicio",LocalDate.class),r.getObject("fecha_fin",LocalDate.class),r.getString("estado"),r.getString("motivo"),r.getString("motivo_operacion"),r.getLong("version"),r.getBoolean("aplicada"));
 public Fila obtener(Long emp,Long id){return jdbc.query(SELECT+" WHERE o.empleado_id=:emp AND o.institucion_id=:inst AND o.id=:id",p(emp,false).addValue("id",id),MAP).stream().findFirst().orElseThrow(()->new AccessDeniedException("Concepto ocasional no autorizado"));}
 public Page<Fila> listar(Long emp,FiltroConceptoPeriodoNomina original){var f=original.normalizado();var par=p(emp,false);
  if(f.periodoOcasionalId()==null&&!f.periodoOcasionalTexto().isBlank())throw regla("Selecciona el periodo de la ayuda o limpia el filtro.");
  if(f.periodoOcasionalId()!=null)programacion.periodo(f.periodoOcasionalId());
  String where=" WHERE o.empleado_id=:emp AND o.institucion_id=:inst AND (:estado='TODOS' OR o.estado=:estado) AND (:periodo IS NULL OR o.periodo_id=:periodo) AND strpos(lower(o.concepto_codigo||' '||o.concepto_nombre||' '||o.periodo_texto||' '||o.motivo),lower(:q))>0";
  par.addValue("estado",f.estadoOcasional()).addValue("periodo",f.periodoOcasionalId(),java.sql.Types.BIGINT).addValue("q",f.buscarOcasional());var pg=PageRequest.of(f.paginaOcasional(),f.tamanioOcasional());long total=jdbc.queryForObject("SELECT count(*) FROM concepto_empleado_periodo o"+where,par,Long.class);par.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset());return new PageImpl<>(jdbc.query(SELECT+where+" ORDER BY o.id DESC LIMIT :limite OFFSET :offset",par,MAP),pg,total);
 }
 public List<OpcionAutocompletado> conceptos(Long emp,String q){var par=p(emp,false);String t=OrganizacionLaboralService.texto(q,200);if(!t.isEmpty()&&t.length()<3)return List.of();return jdbc.query("SELECT id,codigo,nombre,naturaleza FROM concepto_nomina WHERE institucion_id=:inst AND activo AND aplicacion='OCASIONAL' AND strpos(lower(codigo||' '||nombre),lower(:q))>0 ORDER BY id LIMIT 11",par.addValue("q",t),(r,n)->new OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),ConceptoNominaService.etiqueta(r.getString("naturaleza"))+" · Sólo un periodo"));}
 public List<OpcionAutocompletado> periodos(Long emp,String q,boolean todos){var par=p(emp,false);String t=OrganizacionLaboralService.texto(q,200);if(!t.isEmpty()&&t.length()<3)return List.of();String where=todos?"":" AND pe.estado='PROGRAMADO' AND c.activo AND c.frecuencia='MENSUAL' AND EXISTS(SELECT 1 FROM empleado_calendario_nomina ec WHERE ec.empleado_id=:emp AND ec.calendario_id=c.id AND ec.institucion_id=:inst)";return jdbc.query("SELECT pe.id,pe.codigo,pe.nombre,c.nombre calendario FROM periodo_nomina pe JOIN calendario_nomina c ON c.id=pe.calendario_id WHERE pe.institucion_id=:inst"+where+" AND strpos(lower(pe.codigo||' '||pe.nombre),lower(:q))>0 ORDER BY pe.fecha_inicio DESC,pe.id LIMIT 11",par.addValue("q",t),(r,n)->new OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),r.getString("calendario")));}
 public Contexto contexto(Long emp,Long concepto,Long periodo){var par=p(emp,false);var c=catalogo.obtener(concepto);var per=preparacion.contexto(periodo);
  if(!c.activo()||!"OCASIONAL".equals(c.aplicacion()))throw regla("Selecciona un concepto activo de aplicación ocasional.");
  if(!per.advertencia().isBlank())throw regla(per.advertencia());
  if(per.periodo().fin().isBefore(empleados.hoyLaboral(emp)))throw regla("El periodo ya terminó; sólo se permite consultar su historia.");
  var filas=jdbc.queryForList("SELECT e.activo,l.situacion,l.fecha_ingreso,l.version laboral,ec.version configuracion,ec.calendario_id FROM empleado e JOIN empleado_laboral l ON l.empleado_id=e.id LEFT JOIN empleado_calendario_nomina ec ON ec.empleado_id=e.id WHERE e.id=:emp AND e.institucion_id=:inst",par);
  if(filas.isEmpty())throw regla("Confirma primero los datos laborales y el calendario del empleado.");var e=filas.getFirst();
  if(!Boolean.TRUE.equals(e.get("activo"))||!"ACTIVO".equals(e.get("situacion"))||e.get("fecha_ingreso")==null)throw regla("El empleado debe estar activo y tener ingreso laboral confirmado.");
  if(e.get("calendario_id")==null||!Objects.equals(((Number)e.get("calendario_id")).longValue(),per.periodo().calendarioId()))throw regla("Selecciona un periodo del calendario asignado al empleado en Configuración de nómina.");
  return new Contexto(c.version(),((Number)e.get("laboral")).longValue(),((Number)e.get("configuracion")).longValue(),per.sello(),c.codigo()+" · "+c.nombre(),per.periodo().codigo()+" · "+per.periodo().nombre(),per.moneda(),c.tipoTexto(),per.periodo().inicio().toString(),per.periodo().fin().toString());
 }
 private void bloquearPeriodo(Long emp,Long periodo){var per=programacion.periodo(periodo);var par=p(emp,true).addValue("per",periodo).addValue("cal",per.calendarioId());
  jdbc.queryForObject("SELECT id FROM calendario_nomina WHERE id=:cal AND institucion_id=:inst FOR SHARE",par,Long.class);
  jdbc.queryForObject("SELECT id FROM periodo_nomina WHERE id=:per AND institucion_id=:inst FOR SHARE",par,Long.class);
  jdbc.queryForObject("SELECT id FROM empleado WHERE id=:emp AND institucion_id=:inst FOR UPDATE",par,Long.class);
  jdbc.queryForObject("SELECT empleado_id FROM empleado_laboral WHERE empleado_id=:emp FOR UPDATE",par,Long.class);
  if(!Boolean.TRUE.equals(jdbc.queryForObject("SELECT activo FROM institucion WHERE id=:inst FOR SHARE",par,Boolean.class)))throw regla("La institución está inactiva; sólo consulta.");
 }
 @Transactional public Long guardar(Long emp,ConceptoPeriodoNominaForm f){
  var par=p(emp,true);if(f.getPeriodoId()==null||f.getConceptoId()==null)throw regla("Selecciona periodo y concepto.");bloquearPeriodo(emp,f.getPeriodoId());
  jdbc.query("SELECT id FROM concepto_nomina WHERE id=:concepto AND institucion_id=:inst FOR SHARE",par.addValue("concepto",f.getConceptoId()),(r,n)->r.getLong(1));
  var c=contexto(emp,f.getConceptoId(),f.getPeriodoId());
  if(!Objects.equals(c.conceptoVersion(),f.getConceptoVersion())||!Objects.equals(c.laboralVersion(),f.getLaboralVersion())||!Objects.equals(c.calendarioVersion(),f.getCalendarioVersion())||!Objects.equals(c.selloPeriodo(),f.getSelloPeriodo())||!Objects.equals(c.concepto(),f.getConceptoTexto())||!Objects.equals(c.periodo(),f.getPeriodoTexto()))throw regla("Cambió la configuración, el concepto o el periodo. Vuelve a seleccionarlos antes de confirmar.");
  if(f.getImporte()==null||f.getImporte().signum()<=0||f.getImporte().scale()>2||f.getImporte().precision()-f.getImporte().scale()>17)throw regla("Captura un importe positivo con máximo dos decimales.");validarMotivo(f.getMotivo());
  var per=programacion.periodo(f.getPeriodoId());
  if(jdbc.queryForObject("SELECT count(*) FROM nomina_borrador_empleado ne WHERE ne.periodo_id=:periodo AND ne.empleado_id=:emp AND (EXISTS(SELECT 1 FROM nomina_aplicacion a WHERE a.borrador_id=ne.borrador_id AND a.revision=ne.revision) OR EXISTS(SELECT 1 FROM nomina_autorizacion a WHERE a.borrador_id=ne.borrador_id AND a.revision=ne.revision))",par.addValue("periodo",f.getPeriodoId()),Long.class)>0)throw regla("El empleado ya tiene una nómina aplicada o autorizada para este periodo; no se cambia su cálculo histórico.");
  if(jdbc.queryForObject("SELECT count(*) FROM concepto_empleado_periodo WHERE empleado_id=:emp AND periodo_id=:periodo AND concepto_id=:concepto AND estado='ACTIVA'",par,Long.class)>0)throw regla("Este concepto ya está configurado para el empleado y periodo. No se guardó un duplicado.");
  var concepto=catalogo.obtener(f.getConceptoId());par.addValue("codigo",concepto.codigo()).addValue("nombre",concepto.nombre()).addValue("tipo",concepto.naturaleza()).addValue("version",c.conceptoVersion()).addValue("monto",f.getImporte()).addValue("moneda",c.moneda()).addValue("inicio",per.inicio()).addValue("fin",per.fin()).addValue("texto",c.periodo()).addValue("sello",c.selloPeriodo()).addValue("motivo",f.getMotivo().strip());
  return jdbc.queryForObject("INSERT INTO concepto_empleado_periodo(institucion_id,empleado_id,periodo_id,concepto_id,concepto_codigo,concepto_nombre,naturaleza,concepto_version,importe,moneda,fecha_inicio,fecha_fin,periodo_texto,sello_periodo,motivo,motivo_operacion,creado_por_id,actualizado_por_id) VALUES(:inst,:emp,:periodo,:concepto,:codigo,:nombre,:tipo,:version,:monto,:moneda,:inicio,:fin,:texto,:sello,:motivo,:motivo,:actor,:actor) RETURNING id",par,Long.class);
 }
 @Transactional public void cancelar(Long emp,Long id,Long version,String motivo){var par=p(emp,true).addValue("id",id);obtener(emp,id);Long periodo=jdbc.queryForObject("SELECT periodo_id FROM concepto_empleado_periodo WHERE id=:id AND empleado_id=:emp",par,Long.class);bloquearPeriodo(emp,periodo);var f=obtener(emp,id);validarMotivo(motivo);
  if(!Objects.equals(f.version(),version)||!"ACTIVA".equals(f.estado()))throw regla("El registro cambió o ya fue cancelado. Recarga antes de continuar.");if(f.aplicada())throw regla("El concepto forma parte de una nómina autorizada o aplicada; sólo consulta.");
  jdbc.update("UPDATE concepto_empleado_periodo SET estado='CANCELADA',motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id AND empleado_id=:emp AND institucion_id=:inst",par.addValue("motivo",motivo.strip()));
 }
 public Page<Historia> historial(Long emp,Long id,int pagina){obtener(emp,id);var par=p(emp,false).addValue("id",id);var pg=PageRequest.of(Math.max(0,pagina),20);long n=jdbc.queryForObject("SELECT count(*) FROM concepto_empleado_periodo_historial WHERE asignacion_id=:id AND institucion_id=:inst",par,Long.class);par.addValue("offset",pg.getOffset());return new PageImpl<>(jdbc.query("SELECT to_char(h.ocurrido_en AT TIME ZONE i.zona_horaria,'DD/MM/YYYY HH24:MI') fecha,u.username,h.datos->'despues'->>'motivo_operacion' motivo,h.datos->'despues'->>'estado' estado FROM concepto_empleado_periodo_historial h JOIN institucion i ON i.id=h.institucion_id JOIN usuario u ON u.id=h.usuario_id WHERE h.asignacion_id=:id AND h.institucion_id=:inst ORDER BY h.id DESC LIMIT 20 OFFSET :offset",par,(r,k)->new Historia(r.getString("fecha"),r.getString("username"),r.getString("motivo"),r.getString("estado"))),pg,n);}
 private static void validarMotivo(String m){if(m==null||m.isBlank()||m.length()>2000)throw regla("Explica el motivo con máximo 2000 caracteres.");}
 private static ReglaNegocioException regla(String s){return new ReglaNegocioException(s);}
}
