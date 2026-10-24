package escuela.personal.service;

import escuela.personal.dto.AplicarNominaForm;
import escuela.admin.dto.OpcionAutocompletado;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.MovimientoManualRequest;
import escuela.finanzas.entity.DireccionMovimiento;
import escuela.finanzas.service.MovimientoManualService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.security.access.AccessDeniedException;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class AplicacionNominaService {
 @org.springframework.beans.factory.annotation.Value("${app.nomina.consolidada-habilitada:false}") private boolean consolidadaHabilitada;
 private final PreparacionNominaService preparacion;
 private final EmpleadoService empleados;
 private final NamedParameterJdbcTemplate jdbc;
 private final MovimientoManualService movimientos;
 public record Cuenta(Long id,String codigo,String nombre,String moneda,BigDecimal saldo,Long version){}
 public record Motivo(Long id,String codigo,String nombre,Long version){}
 public record Vista(PreparacionNominaService.Borrador borrador,PreparacionNominaService.Totales totales,Cuenta cuenta,Motivo motivo,BigDecimal saldoPosterior,String sello){}
 public record Aplicacion(Long movimientoId,String cuenta,String fecha,String usuario,BigDecimal monto,String moneda,String motivo){}
 private MapSqlParameterSource parametros(boolean escribir){var a=empleados.autorizarSalarios(escribir);return new MapSqlParameterSource("institucion",a.institucionId()).addValue("actor",a.usuarioId());}
 public boolean puedeAplicar(){return consolidadaHabilitada&&empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR")&&empleados.permitido("MOVIMIENTO_FINANCIERO_REGISTRAR");}
 private void autorizar(){empleados.autorizarSalarios(true);if(!puedeAplicar())throw new AccessDeniedException("Se requiere administrar salarios y registrar movimientos financieros para aplicar nómina");}
 public void validarBorrador(Long id){autorizar();var b=preparacion.obtener(id);if(!b.estado().equals("BORRADOR"))throw new ReglaNegocioException("Esta nómina ya fue aplicada o cancelada; sólo está disponible para consulta.");}
 private Cuenta cuenta(Long id,String moneda){return jdbc.query("SELECT c.id,c.codigo,c.nombre,c.moneda,c.version,coalesce((SELECT m.saldo_posterior FROM movimiento_financiero m WHERE m.cuenta_id=c.id ORDER BY m.secuencia_cuenta DESC LIMIT 1),c.saldo_inicial) saldo FROM cuenta_financiera c WHERE c.id=:id AND c.institucion_id=:institucion AND c.activo AND c.moneda=:moneda AND c.tipo IN ('CAJA','BANCO')",parametros(true).addValue("id",id).addValue("moneda",moneda),(r,n)->new Cuenta(r.getLong("id"),r.getString("codigo"),r.getString("nombre"),r.getString("moneda"),r.getBigDecimal("saldo"),r.getLong("version"))).stream().findFirst().orElseThrow(()->new ReglaNegocioException("La cuenta debe ser caja o banco, estar activa y pertenecer a la institución y moneda de la nómina."));}
 private Motivo motivo(Long id){return jdbc.query("SELECT id,codigo,nombre,version FROM motivo_financiero WHERE id=:id AND institucion_id=:institucion AND activo AND naturaleza IN ('EGRESO','AMBOS')",parametros(true).addValue("id",id),(r,n)->new Motivo(r.getLong("id"),r.getString("codigo"),r.getString("nombre"),r.getLong("version"))).stream().findFirst().orElseThrow(()->new ReglaNegocioException("Selecciona un motivo financiero activo que admita egresos y pertenezca a la institución."));}
 public Vista revisar(Long id,AplicarNominaForm f){validarBorrador(id);var b=preparacion.obtener(id);if(!Objects.equals(b.version(),f.getVersion()))throw new ReglaNegocioException("El borrador cambió en otra pestaña; vuelve a revisarlo antes de aplicar.");var t=preparacion.totalesGuardados(id,b.revision());if(t.propuesto().signum()<=0||t.propuesto().compareTo(new BigDecimal("99999999999999999.99"))>0)throw new ReglaNegocioException("El total a registrar debe ser positivo y estar dentro de la capacidad financiera; no se generará un egreso de cero.");var c=cuenta(f.getCuentaId(),b.moneda());var m=motivo(f.getMotivoPagoId());if(c.saldo().compareTo(t.propuesto())<0)throw new ReglaNegocioException("El total de nómina supera el saldo disponible de la cuenta elegida.");String sello=PreparacionNominaService.sello(b.id()+"|"+b.version()+"|"+b.revision()+"|"+t.propuesto()+"|"+c+"|"+m);return new Vista(b,t,c,m,c.saldo().subtract(t.propuesto()),sello);}
 public List<OpcionAutocompletado> cuentas(Long id,String q){validarBorrador(id);var b=preparacion.obtener(id);var texto=OrganizacionLaboralService.texto(q,200);if(!texto.isEmpty()&&texto.length()<3)return List.of();return jdbc.query("SELECT id,codigo,nombre,tipo,moneda FROM cuenta_financiera WHERE institucion_id=:institucion AND activo AND moneda=:moneda AND tipo IN ('CAJA','BANCO') AND strpos(lower(codigo||' '||nombre),lower(:q))>0 ORDER BY id LIMIT 11",parametros(true).addValue("q",texto).addValue("moneda",b.moneda()),(r,n)->new OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),r.getString("tipo")+" · "+r.getString("moneda")));}
 public List<OpcionAutocompletado> motivos(Long id,String q){validarBorrador(id);var texto=OrganizacionLaboralService.texto(q,200);if(!texto.isEmpty()&&texto.length()<3)return List.of();return jdbc.query("SELECT id,codigo,nombre FROM motivo_financiero WHERE institucion_id=:institucion AND activo AND naturaleza IN ('EGRESO','AMBOS') AND strpos(lower(codigo||' '||nombre),lower(:q))>0 ORDER BY id LIMIT 11",parametros(true).addValue("q",texto),(r,n)->new OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),"Egreso de nómina"));}
 @Transactional public Long aplicar(Long id,AplicarNominaForm f,String sello,boolean salidaConfirmada){
  autorizar();if(!salidaConfirmada)throw new ReglaNegocioException("Confirma que revisaste la nómina y que corresponde registrar la salida real de dinero.");if(f.getMotivo()==null||f.getMotivo().isBlank()||f.getMotivo().length()>2000)throw new ReglaNegocioException("Captura el motivo de aplicación con máximo 2000 caracteres.");
  var b=preparacion.bloquearParaAplicar(id,f.getVersion());var par=parametros(true).addValue("cuenta",f.getCuentaId());
  if(jdbc.query("SELECT id FROM cuenta_financiera WHERE id=:cuenta AND institucion_id=:institucion FOR UPDATE",par,(r,n)->r.getLong(1)).isEmpty())throw new AccessDeniedException("Cuenta no autorizada");
  if(jdbc.query("SELECT id FROM motivo_financiero WHERE id=:motivoId AND institucion_id=:institucion FOR SHARE",par.addValue("motivoId",f.getMotivoPagoId()),(r,n)->r.getLong(1)).isEmpty())throw new ReglaNegocioException("El motivo financiero ya no está disponible; vuelve a revisar la aplicación.");
  var v=revisar(id,f);if(!v.sello().equals(sello))throw new ReglaNegocioException("El saldo, la cuenta o el motivo financiero cambiaron desde la revisión. Revisa de nuevo antes de aplicar; no se movió dinero.");
  String zona=jdbc.queryForObject("SELECT zona_horaria FROM institucion WHERE id=:institucion",par,String.class);var ahora=LocalDateTime.now(ZoneId.of(zona)).withSecond(0).withNano(0);
  var mov=movimientos.registrarNomina(new MovimientoManualRequest(empleados.autorizarSalarios(true).institucionId(),v.cuenta().id(),null,ahora,DireccionMovimiento.EGRESO,v.motivo().id(),v.totales().propuesto(),"Nómina "+b.folio()+" · "+b.periodoCodigo()+" · revisión "+b.revision(),b.folio(),"Personal de la institución","NOMINA:"+b.id()));
  par.addValue("id",id).addValue("periodo",b.periodoId()).addValue("revision",b.revision()).addValue("movimiento",mov.id()).addValue("motivoFinanciero",v.motivo().id()).addValue("codigo",v.cuenta().codigo()).addValue("nombre",v.cuenta().nombre()).addValue("cantidad",v.totales().empleados()).addValue("monto",v.totales().propuesto()).addValue("moneda",b.moneda()).addValue("anterior",mov.saldoAnterior()).addValue("posterior",mov.saldoPosterior()).addValue("motivo",f.getMotivo().strip());
  jdbc.update("INSERT INTO nomina_aplicacion(borrador_id,institucion_id,periodo_id,revision,movimiento_id,cuenta_id,motivo_financiero_id,cuenta_codigo,cuenta_nombre,empleados,monto,moneda,saldo_anterior,saldo_posterior,aplicado_por_id,motivo) VALUES(:id,:institucion,:periodo,:revision,:movimiento,:cuenta,:motivoFinanciero,:codigo,:nombre,:cantidad,:monto,:moneda,:anterior,:posterior,:actor,:motivo)",par);
  jdbc.update("UPDATE nomina_borrador SET estado='APLICADA',motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id AND institucion_id=:institucion",par);return mov.id();
 }
 public Aplicacion obtener(Long id){preparacion.obtener(id);return jdbc.query("SELECT a.*,to_char(a.aplicado_en AT TIME ZONE i.zona_horaria,'DD/MM/YYYY HH24:MI') fecha,u.username FROM nomina_aplicacion a JOIN institucion i ON i.id=a.institucion_id JOIN usuario u ON u.id=a.aplicado_por_id WHERE a.borrador_id=:id AND a.institucion_id=:institucion",parametros(false).addValue("id",id),(r,n)->new Aplicacion(r.getLong("movimiento_id"),r.getString("cuenta_codigo")+" · "+r.getString("cuenta_nombre"),r.getString("fecha"),r.getString("username"),r.getBigDecimal("monto"),r.getString("moneda"),r.getString("motivo"))).stream().findFirst().orElse(null);}
}
