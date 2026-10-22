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
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ConceptoNominaService {
 private final EmpleadoService empleados;
 private final NamedParameterJdbcTemplate jdbc;
 public record Concepto(Long id,String codigo,String nombre,String descripcion,String naturaleza,String aplicacion,boolean activo,Long version){
  public String tipoTexto(){return etiqueta(naturaleza);}
  public String aplicacionTexto(){return etiqueta(aplicacion);}
  public String efecto(){return switch(naturaleza){case "PERCEPCION"->"Suma al empleado";case "DEDUCCION"->"Resta al empleado";default->"Costo de la escuela; no reduce el pago del empleado";};}
 }
 public static String etiqueta(String valor){return switch(Objects.toString(valor,"")){case "PERCEPCION"->"Percepción";case "DEDUCCION"->"Deducción";case "APORTACION_PATRONAL"->"Aportación patronal";case "HABITUAL"->"Habitual";case "OCASIONAL"->"Ocasional";default->"";};}
 private static final RowMapper<Concepto> MAP=(r,n)->new Concepto(r.getLong("id"),r.getString("codigo"),r.getString("nombre"),r.getString("descripcion"),r.getString("naturaleza"),r.getString("aplicacion"),r.getBoolean("activo"),r.getLong("version"));
 private MapSqlParameterSource parametros(boolean escribir){var p=empleados.autorizarSalarios(escribir);return new MapSqlParameterSource("institucion",p.institucionId()).addValue("actor",p.usuarioId());}
 public Concepto obtener(Long id){return jdbc.query("SELECT * FROM concepto_nomina WHERE institucion_id=:institucion AND id=:id",parametros(false).addValue("id",id),MAP).stream().findFirst().orElseThrow(()->new AccessDeniedException("Concepto de nómina no autorizado"));}
 public Page<Concepto> listar(FiltroConceptoNomina original){
  var p=parametros(false);var f=original.normalizado();f.validar();if(f.conceptoId()!=null)obtener(f.conceptoId());
  p.addValue("q",f.q()).addValue("estado",f.estado()).addValue("naturaleza",f.filtroNaturaleza()).addValue("aplicacion",f.filtroAplicacion()).addValue("concepto",f.conceptoId(),java.sql.Types.BIGINT);
  String where=" FROM concepto_nomina WHERE institucion_id=:institucion AND (:concepto IS NULL OR id=:concepto) AND strpos(lower(codigo||' '||nombre||' '||coalesce(descripcion,'')),lower(:q))>0 AND (:estado='TODOS' OR activo=(:estado='ACTIVO')) AND (:naturaleza='' OR naturaleza=:naturaleza) AND (:aplicacion='' OR aplicacion=:aplicacion)";
  var pg=PageRequest.of(f.pagina(),f.tamanio());Long total=jdbc.queryForObject("SELECT count(*)"+where,p,Long.class);p.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset());
  return new PageImpl<>(jdbc.query("SELECT *"+where+" ORDER BY id LIMIT :limite OFFSET :offset",p,MAP),pg,total==null?0:total);
 }
 public List<OpcionAutocompletado> buscar(String q){
  var p=parametros(false);String texto=OrganizacionLaboralService.texto(q,200);if(!texto.isEmpty()&&texto.length()<3)return List.of();
  return jdbc.query("SELECT * FROM concepto_nomina WHERE institucion_id=:institucion AND strpos(lower(codigo||' '||nombre),lower(:q))>0 ORDER BY nombre,id LIMIT 11",p.addValue("q",texto),MAP).stream().map(c->new OpcionAutocompletado(c.id(),c.codigo()+" · "+c.nombre(),c.tipoTexto()+" · "+c.aplicacionTexto()+" · "+(c.activo()?"Activo":"Inactivo"))).toList();
 }
 @Transactional public Long guardar(Long id,ConceptoNominaForm f){
  var p=parametros(true);if(!Boolean.TRUE.equals(jdbc.queryForObject("SELECT activo FROM institucion WHERE id=:institucion",p,Boolean.class)))throw regla("La institución está inactiva; sólo permite consultar el catálogo.");
  if(f.getCodigo()==null||!f.getCodigo().matches("[A-Za-z0-9][A-Za-z0-9_-]{0,49}")||f.getNombre()==null||f.getNombre().isBlank()||f.getNombre().length()>150||f.getDescripcion()!=null&&f.getDescripcion().length()>2000)throw regla("Captura código, nombre y descripción con los formatos indicados.");
  if(!Set.of("PERCEPCION","DEDUCCION","APORTACION_PATRONAL").contains(Objects.toString(f.getNaturaleza(),""))||!Set.of("HABITUAL","OCASIONAL").contains(Objects.toString(f.getAplicacion(),""))||f.getVersion()==null||f.getVersion()<0)throw regla("Selecciona tipo y aplicación válidos; recarga si cambió el registro.");
  if(id!=null)obtener(id);
  p.addValue("id",id==null?0L:id).addValue("codigo",f.getCodigo().toUpperCase(Locale.ROOT)).addValue("nombre",f.getNombre().strip()).addValue("descripcion",OrganizacionLaboralService.texto(f.getDescripcion(),2000)).addValue("naturaleza",f.getNaturaleza()).addValue("aplicacion",f.getAplicacion()).addValue("activo",f.isActivo()).addValue("version",f.getVersion());
  if(jdbc.queryForObject("SELECT count(*) FROM concepto_nomina WHERE institucion_id=:institucion AND codigo=:codigo AND id<>:id",p,Long.class)>0)throw regla("Ya existe un concepto de nómina con ese código en tu institución.");
  if(id==null)return jdbc.queryForObject("INSERT INTO concepto_nomina(institucion_id,codigo,nombre,descripcion,naturaleza,aplicacion,activo,creado_por_id,actualizado_por_id) VALUES(:institucion,:codigo,:nombre,:descripcion,:naturaleza,:aplicacion,:activo,:actor,:actor) RETURNING id",p,Long.class);
  int n=jdbc.update("UPDATE concepto_nomina SET codigo=:codigo,nombre=:nombre,descripcion=:descripcion,naturaleza=:naturaleza,aplicacion=:aplicacion,activo=:activo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id AND institucion_id=:institucion AND version=:version",p);
  if(n!=1)throw regla("El concepto cambió en otra pestaña o por otro usuario. Recarga antes de guardar para no sobrescribir sus cambios.");return id;
 }
 private static ReglaNegocioException regla(String mensaje){return new ReglaNegocioException(mensaje);}
}
