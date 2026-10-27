package escuela.personal.service;

import escuela.personal.dto.PagoMultipleNominaForm;
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

/** Una transacción para toda la distribución; cada empleado conserva sus fuentes exactas. */
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class PagoMultipleNominaService {
 private final NamedParameterJdbcTemplate jdbc;
 private final EmpleadoService empleados;
 private final PreparacionNominaService preparacion;
 private final PagoEmpleadoNominaService pagos;
 private final MovimientoManualService movimientos;
 public record Fuente(Long id,String texto,String medio,BigDecimal importe,BigDecimal saldo){}
 public record Vista(Long version,List<Fuente> fuentes,long empleados,BigDecimal total,String sello){}
 public record FuenteHistorica(Long pago,String cuenta,String medio,BigDecimal importe,Long movimiento,Long compensacion){}
 public List<FuenteHistorica> fuentes(Long id,Long fila){
  pagos.fila(id,fila);
  return jdbc.query("SELECT f.pago_id,f.cuenta_texto cuenta,f.medio,f.importe,f.movimiento_id,r.movimiento_id compensacion FROM nomina_pago_fuente f JOIN nomina_pago_intento ni ON ni.id=f.pago_id JOIN cuenta_financiera c ON c.id=f.cuenta_id LEFT JOIN nomina_pago_fuente_reversa r ON r.pago_id=f.pago_id AND r.ordinal=f.ordinal WHERE ni.fila_id=:fila AND ni.institucion_id=:inst ORDER BY f.pago_id DESC,f.ordinal LIMIT 100",p().addValue("fila",fila),(r,n)->new FuenteHistorica(r.getLong("pago_id"),r.getString("cuenta"),r.getString("medio"),r.getBigDecimal("importe"),r.getLong("movimiento_id"),r.getObject("compensacion",Long.class)));
 }
 private MapSqlParameterSource p(){var a=empleados.autorizarSalarios(true);if(!pagos.puedePagar()||a.usuarioId()==null)throw new AccessDeniedException("Se requiere administración salarial y autorización financiera identificable");return new MapSqlParameterSource("inst",a.institucionId()).addValue("actor",a.usuarioId());}
 private void texto(String valor,int max,String campo){if(valor==null||valor.isBlank()||valor.length()>max)throw regla("Captura "+campo+" con máximo "+max+" caracteres.");}
 private static ReglaNegocioException regla(String s){return new ReglaNegocioException(s);}
 private List<PagoEmpleadoNominaService.Fila> filas(Long id,Long fila){
  if(fila!=null){var r=pagos.fila(id,fila);if(r.pagoId()!=null)throw regla("Este empleado ya tiene un pago vigente; no se movió dinero.");return List.of(r);}
  var out=new ArrayList<PagoEmpleadoNominaService.Fila>();int pagina=0;org.springframework.data.domain.Page<PagoEmpleadoNominaService.Fila> page;
  do{page=pagos.listar(id,"","PENDIENTE",pagina++,100);out.addAll(page.getContent());}while(page.hasNext());
  // Las cancelaciones se pagan individualmente en su periodo original.
  long cancelados=jdbc.queryForObject("SELECT count(*) FROM nomina_pago_intento ni JOIN nomina_borrador_empleado ne ON ne.id=ni.fila_id WHERE ne.borrador_id=:id AND ni.estado='REVERTIDO' AND NOT EXISTS(SELECT 1 FROM nomina_pago_intento x WHERE x.fila_id=ne.id AND x.estado='PAGADO')",p().addValue("id",id),Long.class);
  if(cancelados>0)throw regla("Hay pagos cancelados: vuelve a pagarlos individualmente desde Pagos por empleado; no se incorporan a otro pago colectivo.");
  return List.copyOf(out);
 }
 private Fuente cuenta(PagoMultipleNominaForm.Fuente f,String moneda){
  if(f.getCuentaId()==null||f.getImporte()==null||f.getImporte().signum()<=0||f.getImporte().scale()>2)throw regla("Cada fuente requiere cuenta e importe positivo con máximo dos decimales.");
  var par=p().addValue("cuenta",f.getCuentaId()).addValue("moneda",moneda);
  return jdbc.query("SELECT c.id,c.codigo||' · '||c.nombre texto,c.tipo,coalesce((SELECT saldo_posterior FROM movimiento_financiero WHERE cuenta_id=c.id ORDER BY secuencia_cuenta DESC LIMIT 1),c.saldo_inicial) saldo FROM cuenta_financiera c WHERE c.id=:cuenta AND c.institucion_id=:inst AND c.activo AND c.plantel_id IS NULL AND c.moneda=:moneda AND c.tipo IN ('CAJA','BANCO')",par,(r,n)->{
   String medio=r.getString("tipo").equals("CAJA")?"EFECTIVO":"TRANSFERENCIA";
   if(!medio.equals(f.getMedio()))throw regla("Elige Efectivo para caja o Transferencia para banco. Cheques se implementarán después.");
   return new Fuente(r.getLong("id"),r.getString("texto"),medio,f.getImporte(),r.getBigDecimal("saldo"));
  }).stream().findFirst().orElseThrow(()->regla("Selecciona una cuenta institucional activa de la moneda de la nómina."));
 }
 public Vista revisar(Long id,PagoMultipleNominaForm f){
  p();var b=preparacion.obtener(id);if(!Objects.equals(b.version(),f.getVersion()))throw regla("La nómina cambió. Recarga antes de revisar; no se movió dinero.");
  if(!Set.of("BORRADOR","AUTORIZADA","PARCIALMENTE_PAGADA").contains(b.estado()))throw regla("Esta nómina no tiene pagos nuevos disponibles.");
  return revisarDatos(id,b.version(),b.moneda(),filas(id,f.getFilaId()),f);
 }
 private Vista revisarDatos(Long id,Long version,String moneda,List<PagoEmpleadoNominaService.Fila> filas,PagoMultipleNominaForm f){
  texto(f.getReferencia(),150,"referencia");texto(f.getMotivo(),2000,"motivo");if(f.getSolicitud()==null)throw regla("Recarga el formulario antes de confirmar.");
  if(f.getFuentes()==null||f.getFuentes().size()>20)throw regla("Usa entre una y veinte fuentes de pago.");
  if(filas.isEmpty())throw regla("No hay empleados pendientes de pago.");
  BigDecimal total=filas.stream().map(PagoEmpleadoNominaService.Fila::neto).reduce(BigDecimal.ZERO,BigDecimal::add);
  if(filas.stream().anyMatch(x->x.neto().signum()<=0))throw regla("Los netos cero o negativos requieren resolver su tratamiento contable. No se genera un egreso ficticio.");
  if(f.getMotivoPagoId()==null||jdbc.queryForObject("SELECT count(*) FROM motivo_financiero WHERE id=:mot AND institucion_id=:inst AND activo AND naturaleza IN ('EGRESO','AMBOS')",p().addValue("mot",f.getMotivoPagoId()),Long.class)!=1)throw regla("Selecciona un motivo financiero activo de egreso.");
  var fuentes=new ArrayList<Fuente>();var ids=new HashSet<Long>();
  for(var x:f.getFuentes()){
   if(x.getCuentaId()==null&&x.getImporte()==null)continue;
   var c=cuenta(x,moneda);x.setCuentaTexto(c.texto());if(!ids.add(c.id()))throw regla("No repitas una cuenta; reúne su importe en una sola fuente.");
   if(c.saldo().compareTo(c.importe())<0)throw regla(c.texto()+": disponible $"+c.saldo()+", solicitado $"+c.importe()+", faltan $"+c.importe().subtract(c.saldo())+". No se movió dinero.");fuentes.add(c);
  }
  if(fuentes.isEmpty()||fuentes.stream().map(Fuente::importe).reduce(BigDecimal.ZERO,BigDecimal::add).compareTo(total)!=0)throw regla("La suma de las fuentes debe ser exactamente $"+total+". No se aceptan pagos parciales de sueldo.");
  String sello=PreparacionNominaService.sello(id+"|"+version+"|"+filas+"|"+fuentes+"|"+f.getSolicitud()+"|"+f.getMotivoPagoId()+"|"+f.getReferencia().strip()+"|"+f.getMotivo().strip());
  return new Vista(version,List.copyOf(fuentes),filas.size(),total,sello);
 }
 @Transactional public void pagar(Long id,PagoMultipleNominaForm f){
  var par=p().addValue("id",id);if(!f.isConfirmado())throw regla("Confirma la salida real de dinero. No se envía una transferencia al banco.");
  jdbc.queryForObject("SELECT id FROM periodo_nomina WHERE id=:periodo AND institucion_id=:inst FOR UPDATE",p().addValue("periodo",preparacion.obtener(id).periodoId()),Long.class);
  jdbc.queryForObject("SELECT id FROM nomina_borrador WHERE id=:id AND institucion_id=:inst FOR UPDATE",par,Long.class);
  var previo=revisar(id,f);if(!Objects.equals(previo.sello(),f.getSello()))throw regla("La captura o los saldos cambiaron. Revisa nuevamente antes de confirmar.");
  var rows=filas(id,f.getFilaId());
  for(var row:rows)jdbc.queryForObject("SELECT id FROM empleado WHERE id=:empleado AND institucion_id=:inst FOR UPDATE",p().addValue("empleado",row.empleadoId()),Long.class);
  for(var fuente:previo.fuentes().stream().sorted(Comparator.comparing(Fuente::id)).toList())jdbc.queryForObject("SELECT id FROM cuenta_financiera WHERE id=:cuenta AND institucion_id=:inst FOR UPDATE",p().addValue("cuenta",fuente.id()),Long.class);
  if(!Objects.equals(revisar(id,f).sello(),f.getSello()))throw regla("El saldo cambió. Revisa nuevamente; ninguna cuenta fue afectada.");
  // Congela sólo al confirmar la operación final, en la misma transacción del pago.
  if(preparacion.obtener(id).estado().equals("BORRADOR"))pagos.autorizar(id,f.getVersion(),f.getMotivo());
  BigDecimal restanteFuente=previo.fuentes().getFirst().importe();int indice=0;
  for(var row:rows){
   // La recuperación de préstamos conserva su flujo anterior; no se mezcla con esta entrega.
   if(!pagos.cuotas(id,row.id()).isEmpty())throw regla("Este empleado tiene cuotas de préstamo. Su integración multicuenta queda pendiente; no se aplicó ningún pago.");
   Long pago=jdbc.queryForObject("SELECT nextval(pg_get_serial_sequence('nomina_pago_intento','id'))",par,Long.class);BigDecimal restante=row.neto();Long primero=null;int ordinal=0;
   while(restante.signum()>0){var fuente=previo.fuentes().get(indice);BigDecimal importe=restante.min(restanteFuente);ordinal++;
    var mov=movimientos.registrarNominaEmpleado(new MovimientoManualRequest(empleados.autorizarSalarios(true).institucionId(),fuente.id(),null,ahora(),DireccionMovimiento.EGRESO,f.getMotivoPagoId(),importe,"Pago de nómina "+preparacion.obtener(id).folio(),"NPI-"+pago,"Personal de la institución","NOMINA-EMP:"+pago+":"+ordinal));
    if(primero==null)primero=mov.id();jdbc.update("INSERT INTO nomina_pago_fuente(pago_id,ordinal,cuenta_id,cuenta_texto,medio,importe,movimiento_id) VALUES(:pago,:ordinal,:cuenta,:textoCuenta,:medio,:importe,:mov)",p().addValue("pago",pago).addValue("ordinal",ordinal).addValue("cuenta",fuente.id()).addValue("textoCuenta",fuente.texto()).addValue("medio",fuente.medio()).addValue("importe",importe).addValue("mov",mov.id()));
    restante=restante.subtract(importe);restanteFuente=restanteFuente.subtract(importe);if(restanteFuente.signum()==0&&indice+1<previo.fuentes().size())restanteFuente=previo.fuentes().get(++indice).importe();
   }
   // Identidad por operación y empleado: el mismo reintento no genera otro intento.
   UUID solicitud=UUID.nameUUIDFromBytes((f.getSolicitud()+":"+row.id()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
   jdbc.update("INSERT INTO nomina_pago_intento(fila_id,institucion_id,id,solicitud,estado,monto,moneda,movimiento_id,referencia,motivo,usuario_id) VALUES(:fila,:inst,:pago,:solicitud,'PAGADO',:monto,:moneda,:mov,:ref,:motivo,:actor)",p().addValue("fila",row.id()).addValue("pago",pago).addValue("solicitud",solicitud).addValue("monto",row.neto()).addValue("moneda",row.moneda()).addValue("mov",primero).addValue("ref",f.getReferencia().strip()).addValue("motivo",f.getMotivo().strip()));
  }
  estado(id,f.getMotivo());
 }
 public PreparacionNominaService.Candidato calculoIndividual(escuela.personal.dto.PagoIndividualNominaForm f){
  p();if(f.getPeriodoId()==null||f.getEmpleadoId()==null)throw regla("Selecciona un periodo y un empleado de las sugerencias.");
  var filtro=new escuela.personal.dto.FiltroNomina("","TODOS",null,null,null,null,f.getEmpleadoId(),null,null,null,0,25);
  for(String grupo:List.of("LISTO","REVISION","NO_INCLUIDO")){
   var pagina=preparacion.candidatos(f.getPeriodoId(),filtro,grupo);if(!pagina.isEmpty()){var c=pagina.getContent().getFirst();if(!grupo.equals("LISTO"))throw regla(c.nombre()+": "+c.motivo());return c;}
  }
  throw regla("No hay configuración aplicable para ese empleado y periodo.");
 }
 public Vista revisarIndividual(escuela.personal.dto.PagoIndividualNominaForm f){
  var c=calculoIndividual(f);var row=new PagoEmpleadoNominaService.Fila(c.id(),c.id(),c.codigo(),c.nombre(),c.propuesto(),c.moneda(),"PENDIENTE",null);
  var vista=revisarDatos(0L,0L,c.moneda(),List.of(row),f);
  return new Vista(0L,vista.fuentes(),1,c.propuesto(),PreparacionNominaService.sello(c.sello()+"|"+vista.sello()));
 }
 @Transactional public Long pagarIndividual(escuela.personal.dto.PagoIndividualNominaForm f){
  if(!f.isConfirmado())throw regla("Confirma el pago individual antes de registrar dinero.");
  jdbc.queryForObject("SELECT id FROM periodo_nomina WHERE id=:periodo AND institucion_id=:inst FOR UPDATE",p().addValue("periodo",f.getPeriodoId()),Long.class);
  jdbc.queryForObject("SELECT id FROM empleado WHERE id=:empleado AND institucion_id=:inst FOR UPDATE",p().addValue("empleado",f.getEmpleadoId()),Long.class);
  var vista=revisarIndividual(f);if(!Objects.equals(vista.sello(),f.getSello()))throw regla("Cambió la configuración o el saldo. Consulta nuevamente antes de pagar.");
  for(var fuente:vista.fuentes().stream().sorted(Comparator.comparing(Fuente::id)).toList())jdbc.queryForObject("SELECT id FROM cuenta_financiera WHERE id=:cuenta AND institucion_id=:inst FOR UPDATE",p().addValue("cuenta",fuente.id()),Long.class);
  if(!Objects.equals(revisarIndividual(f).sello(),f.getSello()))throw regla("Cambió un saldo; revisa de nuevo. No se movió dinero.");
  var c=calculoIndividual(f);var borrador=new escuela.personal.dto.BorradorNominaForm();borrador.setPeriodoId(f.getPeriodoId());borrador.setSelloPeriodo(preparacion.contexto(f.getPeriodoId()).sello());borrador.setNombre("Pago individual · "+c.codigo());borrador.setMotivo(f.getMotivo());borrador.setSeleccion(List.of(c.token()));
  // Fotografía técnica creada y pagada en una sola transacción; no exige borrador previo al usuario.
  Long id=preparacion.guardar(borrador);f.setVersion(preparacion.obtener(id).version());f.setFilaId(null);f.setSello(revisar(id,f).sello());pagar(id,f);return id;
 }
 public List<escuela.admin.dto.OpcionAutocompletado> buscarCuentas(String q){
  var texto=OrganizacionLaboralService.texto(q,200);if(!texto.isBlank()&&texto.length()<3)return List.of();
  return jdbc.query("SELECT c.id,c.codigo,c.nombre,c.tipo FROM cuenta_financiera c JOIN institucion i ON i.id=c.institucion_id WHERE c.institucion_id=:inst AND c.activo AND c.plantel_id IS NULL AND c.tipo IN ('CAJA','BANCO') AND c.moneda=i.moneda_predeterminada AND strpos(lower(c.codigo||' '||c.nombre),lower(:q))>0 ORDER BY c.id LIMIT 11",p().addValue("q",texto),(r,n)->new escuela.admin.dto.OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),r.getString("tipo")));
 }
 public List<escuela.admin.dto.OpcionAutocompletado> buscarMotivos(String q){
  var texto=OrganizacionLaboralService.texto(q,200);if(!texto.isBlank()&&texto.length()<3)return List.of();
  return jdbc.query("SELECT id,codigo,nombre FROM motivo_financiero WHERE institucion_id=:inst AND activo AND naturaleza IN ('EGRESO','AMBOS') AND strpos(lower(codigo||' '||nombre),lower(:q))>0 ORDER BY id LIMIT 11",p().addValue("q",texto),(r,n)->new escuela.admin.dto.OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),"Pago de nómina"));
 }
 private void estado(Long id,String motivo){jdbc.update("UPDATE nomina_borrador SET estado=(SELECT CASE WHEN count(ni.id)=0 THEN 'AUTORIZADA' WHEN count(ni.id)=count(*) THEN 'PAGADA' ELSE 'PARCIALMENTE_PAGADA' END FROM nomina_borrador_empleado ne LEFT JOIN nomina_pago_intento ni ON ni.fila_id=ne.id AND ni.estado='PAGADO' WHERE ne.borrador_id=:id AND ne.revision=nomina_borrador.revision_actual),motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:id AND institucion_id=:inst",p().addValue("id",id).addValue("motivo",motivo));}
 private LocalDateTime ahora(){return LocalDateTime.now(ZoneId.of(jdbc.queryForObject("SELECT zona_horaria FROM institucion WHERE id=:inst",p(),String.class))).withSecond(0).withNano(0);}
}
