package escuela.finanzas.service;

import escuela.finanzas.dto.*;
import escuela.seguridad.service.*;
import escuela.admin.dto.*;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ChequeraService {
 private final NamedParameterJdbcTemplate jdbc;
 private final AlcanceDatosService alcance;
 public record Cuenta(Long id,Long institucion,String codigo,String nombre,boolean activo){}
 public record Chequera(Long id,String nombre,Long folioInicial,Long folioFinal,boolean activo,String motivo,Long version){public long cantidad(){return folioFinal-folioInicial+1;}}
 public record Historial(String fecha,String usuario,String nombre,Long inicial,Long fin,boolean activo,String motivo){}
 private static final RowMapper<Chequera> MAP=(r,n)->new Chequera(r.getLong("id"),r.getString("nombre"),r.getLong("folio_inicial"),r.getLong("folio_final"),r.getBoolean("activo"),r.getString("motivo"),r.getLong("version"));
 private UsuarioPrincipal autorizar(boolean escribir){
  var a=SecurityContextHolder.getContext().getAuthentication();
  if(a==null||!(a.getPrincipal() instanceof UsuarioPrincipal p)||p.accesoRecuperacion()||p.usuarioId()==null||p.institucionId()==null)throw denegado();
  var permisos=p.authorities().stream().map(x->x.getAuthority()).toList();
  if(!permisos.contains("CHEQUERA_ADMINISTRAR")&&(escribir||!permisos.contains("CHEQUERA_LEER")))throw denegado();
  if(!permisos.contains("CUENTA_FINANCIERA_LEER")&&!permisos.contains("CUENTA_FINANCIERA_ADMINISTRAR"))throw denegado();
  if(jdbc.queryForObject("SELECT count(*) FROM usuario WHERE id=:actor AND institucion_id=:inst AND estado='ACTIVO' AND tipo_cuenta='ADMINISTRATIVO'",new MapSqlParameterSource("actor",p.usuarioId()).addValue("inst",p.institucionId()),Long.class)!=1)throw denegado();
  return p;
 }
 public boolean puedeEditar(){try{autorizar(true);return true;}catch(AccessDeniedException e){return false;}}
 public Cuenta cuenta(Long id){
  var p=autorizar(false);alcance.validarRecurso(ModuloCatalogo.CUENTAS_FINANCIERAS,id);
  return jdbc.query("SELECT c.id,c.institucion_id,c.codigo,c.nombre,c.activo AND i.activo activo FROM cuenta_financiera c JOIN institucion i ON i.id=c.institucion_id WHERE c.id=:id AND c.institucion_id=:inst AND c.tipo='BANCO'",new MapSqlParameterSource("id",id).addValue("inst",p.institucionId()),(r,n)->new Cuenta(r.getLong("id"),r.getLong("institucion_id"),r.getString("codigo"),r.getString("nombre"),r.getBoolean("activo"))).stream().findFirst().orElseThrow(ChequeraService::denegado);
 }
 private MapSqlParameterSource par(Long cuenta){var c=cuenta(cuenta);return new MapSqlParameterSource("cuenta",c.id()).addValue("inst",c.institucion());}
 public Chequera obtener(Long cuenta,Long id){return jdbc.query("SELECT * FROM chequera WHERE id=:id AND cuenta_id=:cuenta AND institucion_id=:inst",par(cuenta).addValue("id",id),MAP).stream().findFirst().orElseThrow(ChequeraService::denegado);}
 public Page<Chequera> listar(Long cuenta,FiltroChequera original){
  var f=original.normalizado();var p=par(cuenta);if(f.chequeraId()!=null)obtener(cuenta,f.chequeraId());
  if(f.chequeraId()==null&&!f.chequeraTexto().isBlank())throw regla("Selecciona la chequera de las sugerencias o limpia ese campo.");
  if(f.folio()!=null&&(f.folio()<1||f.folio()>999999999999L))throw regla("El folio debe estar entre 1 y 999999999999.");
  p.addValue("q",f.q()).addValue("estado",f.estado()).addValue("folio",f.folio(),java.sql.Types.BIGINT).addValue("id",f.chequeraId(),java.sql.Types.BIGINT);
  String where=" FROM chequera WHERE cuenta_id=:cuenta AND institucion_id=:inst AND (:id IS NULL OR id=:id) AND strpos(lower(nombre),lower(:q))>0 AND (:estado='TODOS' OR activo=(:estado='ACTIVO')) AND (:folio IS NULL OR :folio BETWEEN folio_inicial AND folio_final)";
  long total=jdbc.queryForObject("SELECT count(*)"+where,p,Long.class);var pg=PageRequest.of(f.pagina(),f.tamanio());p.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset());
  return new PageImpl<>(jdbc.query("SELECT *"+where+" ORDER BY folio_inicial,id LIMIT :limite OFFSET :offset",p,MAP),pg,total);
 }
 public List<OpcionAutocompletado> buscar(Long cuenta,String q){var p=par(cuenta);String t=FiltroChequera.texto(q,200);if(!t.isEmpty()&&t.length()<3)return List.of();return jdbc.query("SELECT * FROM chequera WHERE cuenta_id=:cuenta AND institucion_id=:inst AND strpos(lower(nombre||' '||folio_inicial||' '||folio_final),lower(:q))>0 ORDER BY nombre,id LIMIT 11",p.addValue("q",t),MAP).stream().map(c->new OpcionAutocompletado(c.id(),c.nombre(),"Folios "+c.folioInicial()+" — "+c.folioFinal()+" · "+(c.activo()?"Activa":"Inactiva"))).toList();}
 @Transactional public Long guardar(Long cuenta,Long id,ChequeraForm f){
  var actor=autorizar(true);var c=cuenta(cuenta);var p=par(cuenta).addValue("actor",actor.usuarioId());
  jdbc.queryForObject("SELECT id FROM cuenta_financiera WHERE id=:cuenta FOR UPDATE",p,Long.class);
  if(!cuenta(cuenta).activo())throw regla("La cuenta o la institución están inactivas. Sólo puedes consultar sus chequeras.");
  if(f.getNombre()==null||f.getNombre().isBlank()||f.getNombre().length()>150||f.getMotivo()==null||f.getMotivo().isBlank()||f.getMotivo().length()>2000||f.getFolioInicial()==null||f.getFolioFinal()==null||f.getFolioInicial()<1||f.getFolioFinal()<f.getFolioInicial()||f.getFolioFinal()>999999999999L||f.getVersion()==null||f.getVersion()<0)throw regla("Revisa nombre, motivo y rango de folios: el final debe ser igual o mayor al inicial.");
  if(id!=null){var anterior=obtener(cuenta,id);if(!Objects.equals(anterior.version(),f.getVersion()))throw regla("La chequera cambió desde que la abriste. Recarga antes de guardar.");if((!Objects.equals(anterior.folioInicial(),f.getFolioInicial())||!Objects.equals(anterior.folioFinal(),f.getFolioFinal()))&&jdbc.queryForObject("SELECT count(*) FROM cheque_nomina WHERE chequera_id=:chequera",p.addValue("chequera",id),Long.class)>0)throw regla("Esta chequera ya tiene cheques emitidos. Puedes cambiar nombre o estado, pero debes conservar su rango de folios.");}
  p.addValue("id",id==null?0L:id).addValue("nombre",f.getNombre().strip()).addValue("inicio",f.getFolioInicial()).addValue("fin",f.getFolioFinal()).addValue("activo",f.isActivo()).addValue("motivo",f.getMotivo().strip()).addValue("version",f.getVersion());
  if(jdbc.queryForObject("SELECT count(*) FROM chequera WHERE cuenta_id=:cuenta AND id<>:id AND int8range(folio_inicial,folio_final,'[]') && int8range(:inicio,:fin,'[]')",p,Long.class)>0)throw regla("Estos folios se superponen con otra chequera de esta cuenta, incluso si está inactiva. Elige otro rango.");
  if(id==null)return jdbc.queryForObject("INSERT INTO chequera(cuenta_id,institucion_id,nombre,folio_inicial,folio_final,activo,motivo,creado_por_id,actualizado_por_id) VALUES(:cuenta,:inst,:nombre,:inicio,:fin,:activo,:motivo,:actor,:actor) RETURNING id",p,Long.class);
  if(jdbc.update("UPDATE chequera SET nombre=:nombre,folio_inicial=:inicio,folio_final=:fin,activo=:activo,motivo=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id AND cuenta_id=:cuenta AND version=:version",p)!=1)throw regla("La chequera cambió. Recarga antes de guardar.");return id;
 }
 public Page<Historial> historial(Long cuenta,Long id,int pagina){obtener(cuenta,id);var p=par(cuenta).addValue("id",id);var pg=PageRequest.of(Math.min(100000,Math.max(0,pagina)),25);long total=jdbc.queryForObject("SELECT count(*) FROM chequera_historial WHERE chequera_id=:id AND institucion_id=:inst",p,Long.class);p.addValue("offset",pg.getOffset());return new PageImpl<>(jdbc.query("SELECT to_char(h.ocurrido_en AT TIME ZONE i.zona_horaria,'DD/MM/YYYY HH24:MI') fecha,u.username,h.actual->>'nombre' nombre,(h.actual->>'folio_inicial')::bigint inicio,(h.actual->>'folio_final')::bigint fin,(h.actual->>'activo')::boolean activo,h.actual->>'motivo' motivo FROM chequera_historial h JOIN institucion i ON i.id=h.institucion_id JOIN usuario u ON u.id=h.usuario_id WHERE h.chequera_id=:id AND h.institucion_id=:inst ORDER BY h.id DESC LIMIT 25 OFFSET :offset",p,(r,n)->new Historial(r.getString("fecha"),r.getString("username"),r.getString("nombre"),r.getLong("inicio"),r.getLong("fin"),r.getBoolean("activo"),r.getString("motivo"))),pg,total);}
 private static AccessDeniedException denegado(){return new AccessDeniedException("No tienes acceso a esta chequera o cuenta bancaria.");}
 private static ReglaNegocioException regla(String m){return new ReglaNegocioException(m);}
}
