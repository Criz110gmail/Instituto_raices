package escuela.personal.service;

import escuela.personal.dto.*;
import escuela.admin.dto.OpcionAutocompletado;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/** Consulta la fuente canónica concepto_empleado; no duplica salarios ni calcula pagos. */
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class NominaEmpleadoService {
 private final EmpleadoService empleados;
 private final ConceptoEmpleadoService conceptos;
 private final NamedParameterJdbcTemplate jdbc;
 private final PreparacionNominaService preparacion;
 public record Fila(Long id,String codigo,String nombre,String naturaleza,BigDecimal importe,String moneda,
  LocalDate inicio,LocalDate fin,String estado,String modelo,String frecuencia,String modalidad,String periodo,
  Long periodoId,Long periodoVersion,Long version,String motivo,boolean modificable,boolean cancelable){
  public String tipo(){return ConceptoNominaService.etiqueta(naturaleza);}
  public String frecuenciaTexto(){return switch(Objects.toString(frecuencia,"LEGADO")){case "MENSUAL"->"Mensual";case "QUINCENAL"->"Quincenal";case "SEMANAL"->"Semanal";case "UNICA"->"Una sola vez";default->"Por nómina · anterior";};}
 }
 public record Periodo(Long id,String texto,LocalDate inicio,LocalDate fin,Long version){}
 private MapSqlParameterSource p(Long id,boolean escribir){var actor=empleados.autorizarNomina(escribir);empleados.autorizar(id,true,false);return new MapSqlParameterSource("institucion",actor.institucionId()).addValue("empleado",id).addValue("hoy",conceptos.hoy());}
 private static final String WHERE="""
  FROM concepto_empleado a WHERE a.institucion_id=:institucion AND a.empleado_id=:empleado
  AND (:concepto IS NULL OR a.concepto_id=:concepto)
  AND strpos(lower(a.concepto_codigo||' '||a.concepto_nombre||' '||a.motivo),lower(:q))>0
  AND (:naturaleza='' OR a.naturaleza=:naturaleza)
  AND (:frecuencia='' OR :frecuencia='LEGADO' AND a.modelo='LEGADO' OR a.frecuencia_importe=:frecuencia)
  AND (:desde IS NULL OR a.fecha_fin IS NULL OR a.fecha_fin>=:desde) AND (:hasta IS NULL OR a.fecha_inicio<=:hasta)
  AND (:estado='TODAS' OR :estado='CANCELADA' AND a.estado='CANCELADA' OR a.estado='ACTIVA' AND
   (:estado='ACTUAL' AND a.fecha_inicio<=:hoy AND (a.fecha_fin IS NULL OR a.fecha_fin>=:hoy)
    OR :estado='PROXIMA' AND a.fecha_inicio>:hoy OR :estado='ANTERIOR' AND a.fecha_fin<:hoy))
  """;
 public Page<Fila> listar(Long id,FiltroNominaEmpleado original){
  var f=original.normalizado();var par=parametros(id,f);
  var pg=PageRequest.of(f.pagina(),f.tamanio());Long n=jdbc.queryForObject("SELECT count(*) "+WHERE,par,Long.class);
  par.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset());var hoy=(LocalDate)par.getValue("hoy");
  return new PageImpl<>(jdbc.query("SELECT a.* "+WHERE+" ORDER BY a.fecha_inicio DESC,a.id DESC LIMIT :limite OFFSET :offset",par,(r,i)->{
   var ini=r.getObject("fecha_inicio",LocalDate.class);var fin=r.getObject("fecha_fin",LocalDate.class);boolean activa="ACTIVA".equals(r.getString("estado"));
   String estado=!activa?"Cancelada":ini.isAfter(hoy)?"Próxima":fin!=null&&fin.isBefore(hoy)?"Anterior":"Vigente";
   return new Fila(r.getLong("id"),r.getString("concepto_codigo"),r.getString("concepto_nombre"),r.getString("naturaleza"),r.getBigDecimal("importe"),r.getString("moneda"),ini,fin,estado,r.getString("modelo"),r.getString("frecuencia_importe"),r.getString("modalidad"),r.getString("periodo_codigo"),r.getObject("periodo_id",Long.class),r.getObject("periodo_version",Long.class),r.getLong("version"),r.getString("motivo"),activa&&(fin==null||!fin.isBefore(hoy)),activa&&ini.isAfter(hoy));
  }),pg,n==null?0:n);
 }
 private MapSqlParameterSource parametros(Long id,FiltroNominaEmpleado f){
  f.validar();return p(id,false).addValue("concepto",f.filtroConceptoId(),java.sql.Types.BIGINT).addValue("q",f.q()).addValue("estado",f.estado()).addValue("naturaleza",f.naturaleza()).addValue("frecuencia",f.frecuencia()).addValue("desde",f.desde(),java.sql.Types.DATE).addValue("hasta",f.hasta(),java.sql.Types.DATE);
 }
 public PreparacionNominaService.Simulacion simular(Long id,FiltroNominaEmpleado original){
  var f=original.normalizado();f.validar();return preparacion.simularEmpleado(id,f.desde(),f.hasta());
 }
 public Map<String,Object> metadatos(Long empleado,Long asignacion){var par=p(empleado,false).addValue("id",asignacion);return jdbc.queryForList("SELECT modelo,frecuencia_importe,modalidad,periodo_id,periodo_codigo,periodo_version FROM concepto_empleado WHERE id=:id AND empleado_id=:empleado AND institucion_id=:institucion",par).stream().findFirst().orElseThrow(()->new AccessDeniedException("Concepto no autorizado"));}
 public String etiquetaConcepto(Long empleado,Long concepto){var par=p(empleado,false).addValue("concepto",concepto);return jdbc.query("SELECT codigo||' · '||nombre FROM concepto_nomina WHERE id=:concepto AND institucion_id=:institucion",par,(r,i)->r.getString(1)).stream().findFirst().orElseThrow(()->new AccessDeniedException("Concepto no autorizado"));}
 public List<OpcionAutocompletado> filtrarConceptos(Long empleado,String q){var par=p(empleado,false);String t=OrganizacionLaboralService.texto(q,200);if(!t.isEmpty()&&t.length()<3)return List.of();return jdbc.query("SELECT id,codigo,nombre,naturaleza FROM concepto_nomina WHERE institucion_id=:institucion AND strpos(lower(codigo||' '||nombre),lower(:q))>0 ORDER BY id LIMIT 11",par.addValue("q",t),(r,i)->new OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),ConceptoNominaService.etiqueta(r.getString("naturaleza"))));}
 public ConceptoEmpleadoService.Asignacion obtener(Long empleado,Long id){p(empleado,false);var a=conceptos.obtener(id);if(!Objects.equals(a.empleadoId(),empleado))throw new AccessDeniedException("Concepto de otro empleado");return a;}
 public List<OpcionAutocompletado> buscar(Long empleado,String q){var par=p(empleado,true);String t=OrganizacionLaboralService.texto(q,200);if(!t.isEmpty()&&t.length()<3)return List.of();
  return jdbc.query("SELECT id,codigo,nombre,naturaleza FROM concepto_nomina WHERE institucion_id=:institucion AND activo AND aplicacion<>'CON_SALDO' AND naturaleza IN ('PERCEPCION','DEDUCCION') AND regexp_replace(upper(codigo),'[^A-Z0-9]','','g') NOT IN ('SUELDO','SUELDOBASE','SALARIO','SALARIOBASE','SUELDOBASEMENSUAL') AND regexp_replace(upper(nombre),'[^A-Z0-9]','','g') NOT IN ('SUELDO','SUELDOBASE','SALARIO','SALARIOBASE','SUELDOBASEMENSUAL') AND strpos(lower(codigo||' '||nombre),lower(:q))>0 ORDER BY id LIMIT 11",par.addValue("q",t),(r,i)->new OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),ConceptoNominaService.etiqueta(r.getString("naturaleza"))));
 }
 public List<OpcionAutocompletado> periodos(Long empleado,String q){var par=p(empleado,true);String t=OrganizacionLaboralService.texto(q,200);if(!t.isEmpty()&&t.length()<3)return List.of();return jdbc.query("SELECT id,codigo,nombre,fecha_inicio,fecha_fin FROM periodo_nomina WHERE institucion_id=:institucion AND estado='PROGRAMADO' AND fecha_inicio>=:hoy AND strpos(lower(codigo||' '||nombre),lower(:q))>0 ORDER BY fecha_inicio,id LIMIT 11",par.addValue("q",t),(r,i)->new OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),r.getString("fecha_inicio")+" — "+r.getString("fecha_fin")));}
 public Periodo periodo(Long empleado,Long id){var par=p(empleado,true).addValue("id",id);return jdbc.query("SELECT id,codigo,nombre,fecha_inicio,fecha_fin,version FROM periodo_nomina WHERE id=:id AND institucion_id=:institucion AND estado='PROGRAMADO' AND fecha_inicio>=:hoy",par,(r,i)->new Periodo(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),r.getObject("fecha_inicio",LocalDate.class),r.getObject("fecha_fin",LocalDate.class),r.getLong("version"))).stream().findFirst().orElseThrow(()->new ReglaNegocioException("Selecciona un periodo programado que todavía no haya iniciado."));}
 @Transactional public Long guardar(Long empleado,ConceptoEmpleadoForm form){p(empleado,true);if(!Objects.equals(empleado,form.getEmpleadoId()))throw new AccessDeniedException("Empleado no autorizado");if(form.getAnteriorId()!=null)obtener(empleado,form.getAnteriorId());return conceptos.asignarConfiguracion(form);}
 @Transactional public void gestionar(Long empleado,Long id,Long version,String motivo,LocalDate fin,boolean cancelar){p(empleado,true);obtener(empleado,id);conceptos.gestionar(id,version,motivo,fin,cancelar);}
}
