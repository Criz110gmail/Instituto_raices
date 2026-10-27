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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ConfiguracionNominaService {
 private final EmpleadoService empleados;
 private final ConceptoEmpleadoService conceptos;
 private final NamedParameterJdbcTemplate jdbc;
 public record Fila(Long id,String codigo,String nombre,boolean activo,String situacion,String puesto,
  String jornada,BigDecimal horas,BigDecimal sueldo,String moneda,String base,String frecuencia,
  Long calendarioId,String calendario,String frecuenciaCalendario,boolean calendarioActivo,Long version,
  BigDecimal percepciones,BigDecimal deducciones,BigDecimal aportaciones){
  public boolean compatible(){return calendarioId!=null&&sueldo!=null&&Objects.equals(frecuencia,frecuenciaCalendario);}
 }
 public record Historia(String fecha,String usuario,String anterior,String nuevo,String motivo){}
 public LocalDate hoy(){return conceptos.hoy();}
 private MapSqlParameterSource parametros(boolean escribir){var a=empleados.autorizarSalarios(escribir);return new MapSqlParameterSource("inst",a.institucionId()).addValue("actor",a.usuarioId());}
 public FiltroConfiguracionNomina filtro(FiltroConfiguracionNomina f){var n=f.normalizado(hoy());n.validar();if(n.empleadoId()!=null)empleados.autorizar(n.empleadoId(),true,false);if(n.filtroCalendarioId()!=null)calendario(n.filtroCalendarioId(),false);return n;}
 private static final String FROM="""
  FROM empleado e LEFT JOIN empleado_laboral l ON l.empleado_id=e.id
  LEFT JOIN empleado_calendario_nomina ec ON ec.empleado_id=e.id AND ec.institucion_id=e.institucion_id
  LEFT JOIN calendario_nomina cn ON cn.id=ec.calendario_id AND cn.institucion_id=e.institucion_id
  LEFT JOIN LATERAL (SELECT s.* FROM condicion_salarial s WHERE s.empleado_id=e.id AND s.institucion_id=e.institucion_id
    AND s.estado='ACTIVA' AND s.fecha_inicio<=:fecha AND (s.fecha_fin IS NULL OR s.fecha_fin>=:fecha) ORDER BY s.id DESC LIMIT 1) s ON true
  LEFT JOIN asignacion_esquema_laboral ae ON ae.condicion_id=s.id
  LEFT JOIN LATERAL (SELECT coalesce(sum(c.importe) FILTER(WHERE naturaleza='PERCEPCION'),0) percepciones,
    coalesce(sum(c.importe) FILTER(WHERE naturaleza='DEDUCCION'),0) deducciones,
    coalesce(sum(c.importe) FILTER(WHERE naturaleza='APORTACION_PATRONAL'),0) aportaciones
    FROM concepto_empleado c WHERE c.empleado_id=e.id AND c.institucion_id=e.institucion_id AND c.estado='ACTIVA' AND c.modelo='LEGADO'
      AND c.fecha_inicio<=:fecha AND (c.fecha_fin IS NULL OR c.fecha_fin>=:fecha)) c ON true
  WHERE e.institucion_id=:inst
  """;
 private static final String SELECT="SELECT e.id,e.codigo,concat_ws(' ',e.nombres,e.primer_apellido,e.segundo_apellido) nombre,e.activo,l.situacion,s.puesto_codigo||' · '||s.puesto_nombre puesto,ae.jornada_nombre jornada,coalesce(s.horas_puesto,ae.horas_semanales) horas_semanales,s.importe_bruto,s.moneda,s.base,s.frecuencia,ec.calendario_id,cn.codigo||' · '||cn.nombre calendario,cn.frecuencia frecuencia_calendario,coalesce(cn.activo,false) calendario_activo,coalesce(ec.version,0) version,c.percepciones,c.deducciones,c.aportaciones ";
 private static final RowMapper<Fila> MAP=(r,n)->new Fila(r.getLong("id"),r.getString("codigo"),r.getString("nombre"),r.getBoolean("activo"),r.getString("situacion"),r.getString("puesto"),r.getString("jornada"),r.getBigDecimal("horas_semanales"),r.getBigDecimal("importe_bruto"),r.getString("moneda"),r.getString("base"),r.getString("frecuencia"),r.getObject("calendario_id",Long.class),r.getString("calendario"),r.getString("frecuencia_calendario"),r.getBoolean("calendario_activo"),r.getLong("version"),r.getBigDecimal("percepciones"),r.getBigDecimal("deducciones"),r.getBigDecimal("aportaciones"));
 public Page<Fila> listar(FiltroConfiguracionNomina original){var f=filtro(original);var p=parametros(false).addValue("fecha",f.fecha()).addValue("q",f.q()).addValue("empleado",f.empleadoId(),java.sql.Types.BIGINT).addValue("calendario",f.filtroCalendarioId(),java.sql.Types.BIGINT).addValue("estado",f.estado());
  String where=" AND (:empleado IS NULL OR e.id=:empleado) AND (:calendario IS NULL OR ec.calendario_id=:calendario) AND strpos(lower(e.codigo||' '||concat_ws(' ',e.nombres,e.primer_apellido,e.segundo_apellido)),lower(:q))>0 AND (:estado='TODOS' OR :estado='CON_CALENDARIO' AND ec.empleado_id IS NOT NULL OR :estado='SIN_CALENDARIO' AND ec.empleado_id IS NULL OR :estado='SIN_SUELDO' AND s.id IS NULL)";
  Long n=jdbc.queryForObject("SELECT count(*)"+FROM+where,p,Long.class);var pg=PageRequest.of(f.pagina(),25);p.addValue("offset",pg.getOffset());return new PageImpl<>(jdbc.query(SELECT+FROM+where+" ORDER BY e.id LIMIT 25 OFFSET :offset",p,MAP),pg,n==null?0:n);
 }
 public Fila obtener(Long id,LocalDate fecha){empleados.autorizar(id,true,false);return jdbc.queryForObject(SELECT+FROM+" AND e.id=:id",parametros(false).addValue("id",id).addValue("fecha",fecha==null?hoy():fecha),MAP);}
 public String calendario(Long id,boolean activo){return jdbc.query("SELECT codigo||' · '||nombre texto FROM calendario_nomina WHERE institucion_id=:inst AND id=:id"+(activo?" AND activo":""),parametros(false).addValue("id",id),(r,n)->r.getString(1)).stream().findFirst().orElseThrow(()->new ReglaNegocioException("Selecciona un calendario autorizado"+(activo?" y activo":"")+"."));}
 public List<OpcionAutocompletado> calendarios(String q,boolean activos){String t=OrganizacionLaboralService.texto(q,200);if(!t.isEmpty()&&t.length()<3)return List.of();return jdbc.query("SELECT id,codigo,nombre,frecuencia,activo FROM calendario_nomina WHERE institucion_id=:inst"+(activos?" AND activo":"")+" AND strpos(lower(codigo||' '||nombre),lower(:q))>0 ORDER BY id LIMIT 11",parametros(false).addValue("q",t),(r,n)->new OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),r.getString("frecuencia")+" · "+(r.getBoolean("activo")?"Activo":"Inactivo")));}
 @Transactional public void guardarCalendario(Long id,CalendarioEmpleadoForm f){var p=parametros(true).addValue("id",id);empleados.autorizar(id,true,false);
  if(!Boolean.TRUE.equals(jdbc.queryForObject("SELECT activo FROM institucion WHERE id=:inst FOR SHARE",p,Boolean.class)))throw new ReglaNegocioException("La institución está inactiva; sólo se permite consultar.");
  jdbc.queryForObject("SELECT id FROM empleado WHERE id=:id AND institucion_id=:inst FOR UPDATE",p,Long.class);
  var anteriores=jdbc.queryForList("SELECT version,calendario_id FROM empleado_calendario_nomina WHERE empleado_id=:id AND institucion_id=:inst FOR UPDATE",p);
  long version=anteriores.isEmpty()?0:((Number)anteriores.getFirst().get("version")).longValue();if(!Objects.equals(version,f.getVersion()))throw new ReglaNegocioException("La configuración cambió en otra pestaña. Recarga antes de guardar.");
  if(f.getMotivo()==null||f.getMotivo().isBlank()||f.getMotivo().length()>2000)throw new ReglaNegocioException("Captura el motivo de hasta 2000 caracteres.");
  p.addValue("calendario",f.getCalendarioId());if(jdbc.query("SELECT id FROM calendario_nomina WHERE id=:calendario AND institucion_id=:inst AND activo FOR SHARE",p,(r,n)->r.getLong(1)).isEmpty())throw new ReglaNegocioException("Selecciona un calendario activo de tu institución.");
  if(!Objects.equals(calendario(f.getCalendarioId(),true),f.getCalendarioTexto()))throw new ReglaNegocioException("El calendario cambió o no se seleccionó de la ayuda. Vuelve a seleccionarlo antes de confirmar.");
  if(!anteriores.isEmpty()&&Objects.equals(((Number)anteriores.getFirst().get("calendario_id")).longValue(),f.getCalendarioId()))throw new ReglaNegocioException("Ese calendario ya está asignado; no es necesario guardarlo otra vez.");
  p.addValue("motivo",f.getMotivo().strip()).addValue("texto",calendario(f.getCalendarioId(),true));if(anteriores.isEmpty())jdbc.update("INSERT INTO empleado_calendario_nomina(empleado_id,institucion_id,calendario_id,calendario_texto,motivo,actualizado_por_id,version) VALUES(:id,:inst,:calendario,:texto,:motivo,:actor,1)",p);
  else jdbc.update("UPDATE empleado_calendario_nomina SET calendario_id=:calendario,calendario_texto=:texto,motivo=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE empleado_id=:id AND institucion_id=:inst",p);
 }
 public Page<Historia> historial(Long id,int pagina){obtener(id,hoy());var p=parametros(false).addValue("id",id);var pg=PageRequest.of(Math.max(0,pagina),20);Long n=jdbc.queryForObject("SELECT count(*) FROM empleado_calendario_nomina_historial WHERE empleado_id=:id AND institucion_id=:inst",p,Long.class);p.addValue("offset",pg.getOffset());return new PageImpl<>(jdbc.query("SELECT to_char(h.ocurrido_en AT TIME ZONE i.zona_horaria,'DD/MM/YYYY HH24:MI') fecha,u.username,h.anterior->>'calendario_texto' anterior,h.nuevo->>'calendario_texto' nuevo,h.motivo FROM empleado_calendario_nomina_historial h JOIN institucion i ON i.id=h.institucion_id JOIN usuario u ON u.id=h.usuario_id WHERE h.empleado_id=:id AND h.institucion_id=:inst ORDER BY h.id DESC LIMIT 20 OFFSET :offset",p,(r,k)->new Historia(r.getString("fecha"),r.getString("username"),r.getString("anterior"),r.getString("nuevo"),r.getString("motivo"))),pg,n==null?0:n);}
}
