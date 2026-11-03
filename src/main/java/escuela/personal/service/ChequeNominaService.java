package escuela.personal.service;

import escuela.finanzas.service.ChequeraService;
import escuela.admin.dto.OpcionAutocompletado;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.security.access.AccessDeniedException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ChequeNominaService {
 private final NamedParameterJdbcTemplate jdbc;
 private final EmpleadoService empleados;
 private final ChequeraService chequeras;
 public record Contexto(Long id,String texto,Long inicio,Long fin,Long proximo,Long version,LocalDate hoy){}
 public record Documento(Long chequeraId,String chequera,String version,Long folio,LocalDate fecha,String beneficiario){}
 public record Historico(Long pago,Long folio,String chequera,String cuenta,String beneficiario,LocalDate fecha,BigDecimal importe,String moneda,boolean cancelado,String motivoCancelacion){}
 public boolean habilitado(){return empleados.permitido("MOVIMIENTO_FINANCIERO_REGISTRAR")&&(empleados.permitido("CHEQUERA_LEER")||empleados.permitido("CHEQUERA_ADMINISTRAR"))&&(empleados.permitido("CUENTA_FINANCIERA_LEER")||empleados.permitido("CUENTA_FINANCIERA_ADMINISTRAR"));}
 private MapSqlParameterSource p(boolean cancelar){var a=empleados.autorizarSalarios(true);if(a.usuarioId()==null||!habilitado()||(cancelar&&!empleados.permitido("MOVIMIENTO_FINANCIERO_REVERTIR")))throw new AccessDeniedException("Se requieren acceso a chequeras, a la cuenta bancaria y permisos financieros de nómina.");return new MapSqlParameterSource("inst",a.institucionId()).addValue("actor",a.usuarioId());}
 private MapSqlParameterSource lectura(){var a=empleados.autorizarSalarios(false);return new MapSqlParameterSource("inst",a.institucionId());}
 public LocalDate hoy(){return jdbc.queryForObject("SELECT (CURRENT_TIMESTAMP AT TIME ZONE zona_horaria)::date FROM institucion WHERE id=:inst",p(false),LocalDate.class);}
 private Long proximo(Long cuenta,Long inicio,Long fin){return jdbc.queryForObject("SELECT min(x.folio) FROM (SELECT cast(:inicio AS bigint) folio UNION SELECT folio+1 FROM cheque_nomina WHERE cuenta_id=:cuenta AND folio>=:inicio AND folio<:fin) x WHERE NOT EXISTS(SELECT 1 FROM cheque_nomina c WHERE c.cuenta_id=:cuenta AND c.folio=x.folio)",p(false).addValue("cuenta",cuenta).addValue("inicio",inicio).addValue("fin",fin),Long.class);}
 public Contexto contexto(Long cuenta,Long id){p(false);var banco=chequeras.cuenta(cuenta);var ch=chequeras.obtener(cuenta,id);if(!banco.activo()||!ch.activo())throw regla("Selecciona una cuenta y chequera activas.");return new Contexto(ch.id(),ch.nombre(),ch.folioInicial(),ch.folioFinal(),proximo(cuenta,ch.folioInicial(),ch.folioFinal()),ch.version(),hoy());}
 public List<OpcionAutocompletado> buscar(Long cuenta,String q){p(false);var banco=chequeras.cuenta(cuenta);if(!banco.activo())return List.of();var t=OrganizacionLaboralService.texto(q,200);if(!t.isBlank()&&t.length()<3)return List.of();return jdbc.query("SELECT id,nombre,folio_inicial,folio_final FROM chequera WHERE cuenta_id=:cuenta AND institucion_id=:inst AND activo AND strpos(lower(nombre||' '||folio_inicial||' '||folio_final),lower(:q))>0 ORDER BY nombre,id LIMIT 11",p(false).addValue("cuenta",cuenta).addValue("q",t),(r,n)->new OpcionAutocompletado(r.getLong("id"),r.getString("nombre"),"Folios "+r.getLong("folio_inicial")+" — "+r.getLong("folio_final")));}
 public Documento revisar(Long cuenta,Long chequera,Long folio,LocalDate fecha,String beneficiario){
  if(chequera==null||folio==null||fecha==null||beneficiario==null||beneficiario.isBlank()||beneficiario.length()>250)throw regla("Completa chequera, folio, fecha de emisión y beneficiario del cheque.");
  var c=contexto(cuenta,chequera);if(folio<c.inicio()||folio>c.fin())throw regla("El folio del cheque debe estar entre "+c.inicio()+" y "+c.fin()+".");if(fecha.isAfter(c.hoy()))throw regla("La fecha de emisión del cheque no puede ser posterior a hoy.");
  if(jdbc.queryForObject("SELECT count(*) FROM cheque_nomina WHERE cuenta_id=:cuenta AND folio=:folio",p(false).addValue("cuenta",cuenta).addValue("folio",folio),Long.class)>0)throw regla("El cheque "+folio+" ya fue utilizado en esta cuenta, incluso si se canceló. Selecciona otro folio; no se movió dinero.");
  return new Documento(c.id(),c.texto(),c.version().toString(),folio,fecha,beneficiario.strip());
 }
 @Transactional public void emitir(Long pago,int ordinal,Long cuenta,Documento d,BigDecimal importe,String moneda){
  jdbc.update("INSERT INTO cheque_nomina(institucion_id,cuenta_id,chequera_id,chequera_nombre,chequera_version,pago_id,ordinal,folio,fecha_emision,beneficiario,importe,moneda,usuario_id) VALUES(:inst,:cuenta,:chequera,:nombre,:version,:pago,:ordinal,:folio,:fecha,:beneficiario,:importe,:moneda,:actor)",p(false).addValue("cuenta",cuenta).addValue("chequera",d.chequeraId()).addValue("nombre",d.chequera()).addValue("version",Long.valueOf(d.version())).addValue("pago",pago).addValue("ordinal",ordinal).addValue("folio",d.folio()).addValue("fecha",d.fecha()).addValue("beneficiario",d.beneficiario()).addValue("importe",importe).addValue("moneda",moneda));
 }
 public boolean hayVigentes(Long borrador,Long fila){return jdbc.queryForObject("SELECT count(*) FROM cheque_nomina c JOIN nomina_pago_intento ni ON ni.id=c.pago_id JOIN nomina_borrador_empleado ne ON ne.id=ni.fila_id WHERE ne.borrador_id=:id AND c.institucion_id=:inst AND ni.estado='PAGADO' AND (cast(:fila AS bigint) IS NULL OR ne.id=:fila)",lectura().addValue("id",borrador).addValue("fila",fila),Long.class)>0;}
 public List<Historico> historial(Long borrador,Long fila){return jdbc.query("SELECT ch.*,f.cuenta_texto,cc.cheque_id IS NOT NULL cancelado,cc.motivo motivo_cancelacion FROM cheque_nomina ch JOIN nomina_pago_fuente f ON f.pago_id=ch.pago_id AND f.ordinal=ch.ordinal JOIN nomina_pago_intento ni ON ni.id=ch.pago_id JOIN nomina_borrador_empleado ne ON ne.id=ni.fila_id LEFT JOIN cheque_nomina_cancelacion cc ON cc.cheque_id=ch.id WHERE ne.borrador_id=:id AND ne.id=:fila AND ch.institucion_id=:inst ORDER BY ch.id DESC LIMIT 100",lectura().addValue("id",borrador).addValue("fila",fila),(r,n)->new Historico(r.getLong("pago_id"),r.getLong("folio"),r.getString("chequera_nombre"),r.getString("cuenta_texto"),r.getString("beneficiario"),r.getObject("fecha_emision",LocalDate.class),r.getBigDecimal("importe"),r.getString("moneda"),r.getBoolean("cancelado"),r.getString("motivo_cancelacion")));}
 public void comprobarCancelacion(Long pago,boolean noCobrable){var a=empleados.autorizarSalarios(true);long n=jdbc.queryForObject("SELECT count(*) FROM cheque_nomina WHERE pago_id=:pago AND institucion_id=:inst",new MapSqlParameterSource("pago",pago).addValue("inst",a.institucionId()),Long.class);if(n==0)return;p(true);if(!noCobrable)throw regla("Confirma que los cheques de este pago ya no pueden cobrarse y que el importe está recuperado o liberado en sus cuentas originales. Sin esa certeza no se cancela ni se devuelve saldo.");}
 @Transactional public void cancelarFuente(Long pago,int ordinal,Long movimiento,String referencia,String motivo){var a=empleados.autorizarSalarios(true);if(jdbc.queryForObject("SELECT count(*) FROM cheque_nomina WHERE pago_id=:pago AND ordinal=:ordinal AND institucion_id=:inst",new MapSqlParameterSource("pago",pago).addValue("ordinal",ordinal).addValue("inst",a.institucionId()),Long.class)==0)return;
  jdbc.update("INSERT INTO cheque_nomina_cancelacion(cheque_id,movimiento_id,usuario_id,referencia,motivo,no_cobrable_confirmado) SELECT id,:mov,:actor,:ref,:motivo,true FROM cheque_nomina WHERE pago_id=:pago AND ordinal=:ordinal AND institucion_id=:inst",p(true).addValue("pago",pago).addValue("ordinal",ordinal).addValue("mov",movimiento).addValue("ref",referencia.strip()).addValue("motivo",motivo.strip()));
 }
 private static ReglaNegocioException regla(String m){return new ReglaNegocioException(m);}
}
