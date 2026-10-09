package escuela.cobranza.service;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.*;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.PagoRepository;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.entity.Usuario;
import escuela.common.exception.*;
import escuela.admin.dto.ModuloCatalogo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.util.*;
import static escuela.cobranza.support.CalculoCargo.*;

@Service @RequiredArgsConstructor @Transactional
public class AnticipoLifecycleService {
 private final AcuerdoAnticipadoRepository acuerdos;
 private final CargoRepository cargos;
 private final AjusteCargoRepository ajustes;
 private final AlumnoTutorRepository vinculos;
 private final AlcanceDatosService alcance;
 private final escuela.auditoria.service.RegistroAuditoriaService auditoria;
 public static BigDecimal beca(Cargo c) {
  return c.getAjustes().stream().filter(a->a.getTipo()==TipoAjusteCargo.BECA&&a.getEfecto()==EfectoAjusteCargo.DISMINUCION&&a.getReversaDe()==null&&a.getReversa()==null)
   .map(AjusteCargo::getMonto).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2);
 }
 public static boolean tieneRecargo(Cargo c) {
  return c.getAjustes().stream().anyMatch(a->a.getTipo()==TipoAjusteCargo.RECARGO&&a.getEfecto()==EfectoAjusteCargo.AUMENTO&&a.getReversaDe()==null&&a.getReversa()==null);
 }
 public void validarCargo(Cargo c,Long institucion,Long tutor,LocalDate hoy) {
  alcance.validarRecurso(ModuloCatalogo.CARGOS,c.getId());
  if(!c.getInscripcion().getAlumno().getInstitucion().getId().equals(institucion)||!c.getInscripcion().getAlumno().isActivo()
    ||!Set.of("ACTIVA","PREINSCRITA").contains(c.getInscripcion().getEstado().name())
    ||c.getEstadoRegistro()!=EstadoRegistroCargo.EMITIDO||saldo(c).signum()<=0||aplicado(c).signum()!=0)
   throw new ReglaNegocioException("Selecciona mensualidades vigentes sin abonos; si ya hubo pagos usa el convenio ordinario");
  if(!c.getConceptoCobro().getCategoria().name().equals("COLEGIATURA")||!c.getConceptoCobro().isPermiteDescuento())
   throw new ReglaNegocioException("El acuerdo anticipado sólo admite colegiaturas cuyo concepto permite descuentos");
  if(tieneRecargo(c))throw new ReglaNegocioException("La mensualidad tiene recargos activos; resuélvelos por separado antes del acuerdo anticipado");
  if(!YearMonth.from(c.getPeriodoCobroInicio()).equals(YearMonth.from(c.getPeriodoCobroFin())))throw new ReglaNegocioException("Selecciona colegiaturas desglosadas por mes, no un cargo que agrupa varios meses");
  if(!vinculos.tieneResponsabilidadFinancieraVigente(c.getInscripcion().getAlumno().getId(),tutor,hoy))throw new ReglaNegocioException("El tutor no es responsable financiero vigente del alumno");
 }
 public void comprobarSnapshots(AcuerdoAnticipado a,LocalDate hoy,Long pagoPermitido) {
  for(var d:a.getCargos().stream().sorted(Comparator.comparing(x->x.getCargo().getId())).toList()) {
   Cargo c=cargos.findByIdForUpdate(d.getCargo().getId()).orElseThrow();
   validarCargo(c,a.getInstitucion().getId(),a.getTutor().getId(),hoy);
   if(cargos.tienePagoEnRevisionDistinto(c.getId(),pagoPermitido))throw new ReglaNegocioException("Una mensualidad tiene otro pago en revisión; resuélvelo antes de aplicar el beneficio");
   if(!c.getInscripcion().getPlantel().getId().equals(a.getPlantel().getId())||!c.getMoneda().equals(a.getMoneda())
      ||total(c).compareTo(d.getTotalActual())!=0||beca(c).compareTo(d.getBeca())!=0||c.getImporteOriginal().compareTo(d.getOriginal())!=0)
    throw new ReglaNegocioException("Las becas o importes de una mensualidad cambiaron. Cancela la propuesta y prepara una nueva vista previa");
  }
 }
 public void aplicar(Pago pago,Usuario actor,List<SolicitudAplicacionPago> solicitudes) {
  if(pago.getAcuerdoAnticipadoId()==null)return;
  AcuerdoAnticipado a=acuerdos.bloquear(pago.getAcuerdoAnticipadoId()).orElseThrow(); validarPropietario(a,pago);
  LocalDate hoy=LocalDate.now(ZoneId.of(a.getInstitucion().getZonaHoraria()));
  if(!"PROPUESTO".equals(a.getEstado()))throw new ReglaNegocioException("El beneficio ya no está disponible para este pago");
  if(pago.getFechaPago().atZone(ZoneId.of(a.getInstitucion().getZonaHoraria())).toLocalDate().isAfter(a.getFechaLimite()))throw new ReglaNegocioException("El pago se realizó después de la fecha límite del acuerdo; registra un abono normal sin beneficio");
  if(pago.getMonto().compareTo(a.getTotalPagar())<0)throw new ReglaNegocioException("El beneficio exige pagar completo al menos "+a.getTotalPagar()+"; para un importe menor registra un pago normal sin beneficio");
  comprobarSnapshots(a,hoy,pago.getId());
  Map<Long,BigDecimal> esperadas=new HashMap<>();a.getCargos().stream().filter(d->d.getPagar().signum()>0).forEach(d->esperadas.put(d.getCargo().getId(),d.getPagar()));
  if(solicitudes.size()!=esperadas.size()||solicitudes.stream().anyMatch(s->!esperadas.containsKey(s.getCargo().getId())||s.getMontoSolicitado().compareTo(esperadas.get(s.getCargo().getId()))!=0))throw new ReglaNegocioException("La distribución del pago no corresponde a las mensualidades del acuerdo");
  for(var d:a.getCargos()) {
   Cargo c=d.getCargo();c.getAjustes().size();c.getAplicaciones().size();
   if("SUSTITUIR".equals(a.getPoliticaBeca())&&d.getBeca().signum()>0) crearAjuste(a,c,d.getBeca(),EfectoAjusteCargo.AUMENTO,TipoAjusteCargo.CORRECCION,actor,"Sustitución de beca sólo para esta mensualidad");
   if(d.getBeneficio().signum()>0)crearAjuste(a,c,d.getBeneficio(),EfectoAjusteCargo.DISMINUCION,TipoAjusteCargo.DESCUENTO,actor,"Beneficio por pago anticipado completo");
  }
  a.setEstado("APLICADO");a.setAplicadoEn(Instant.now());acuerdos.saveAndFlush(a);
  auditoria.registrar(a.getInstitucion().getId(),escuela.auditoria.entity.AccionAuditoria.ANTICIPO_APLICADO,"ACUERDO_ANTICIPADO",a.getId(),a.getMotivo(),Map.of("pagoId",pago.getId(),"beneficio",a.getTotalBeneficio(),"totalPagado",pago.getMonto()));
 }
 public void deshacer(Pago pago,Usuario actor,String motivo) {
  if(pago.getAcuerdoAnticipadoId()==null)return;
  AcuerdoAnticipado a=acuerdos.bloquear(pago.getAcuerdoAnticipadoId()).orElseThrow();validarPropietario(a,pago);
  if(!"APLICADO".equals(a.getEstado()))return;
  for(var d:a.getCargos().stream().sorted(Comparator.comparing(x->x.getCargo().getId())).toList()) {
   Cargo c=cargos.findByIdForUpdate(d.getCargo().getId()).orElseThrow();
   if(c.getEstadoRegistro()!=EstadoRegistroCargo.EMITIDO||aplicado(c).signum()!=0)throw new ReglaNegocioException("Hay cambios o abonos posteriores en las mensualidades; revísalos antes de restaurar el beneficio");
  }
  var originales=ajustes.findAllByAcuerdoAnticipadoIdAndReversaDeIsNullOrderByIdAsc(a.getId());
  for(var o:originales) {
   if(o.getReversa()!=null)throw new ReglaNegocioException("Un ajuste del beneficio ya cambió; revisa el acuerdo antes de cancelarlo");
   Cargo c=o.getCargo();c.getAjustes().size();var r=new AjusteCargo();r.setCargo(c);r.setAcuerdoAnticipadoId(a.getId());r.setTipo(TipoAjusteCargo.CORRECCION);r.setEfecto(o.getEfecto()==EfectoAjusteCargo.AUMENTO?EfectoAjusteCargo.DISMINUCION:EfectoAjusteCargo.AUMENTO);r.setMonto(o.getMonto());r.setReversaDe(o);r.setAutorizadoPor(actor);r.setFechaEfectiva(LocalDate.now(ZoneId.of(a.getInstitucion().getZonaHoraria())));r.setMotivo("Reversión del acuerdo "+a.getFolio()+" por cancelación/devolución del pago "+pago.getFolio()+"; motivo conservado en ambos expedientes");ajustes.save(r);o.setReversa(r);c.getAjustes().add(r);
  }
  a.setEstado("CANCELADO");a.setCanceladoEn(Instant.now());a.setMotivoCancelacion(motivo);a.getCargos().forEach(d->d.setActivo(false));acuerdos.saveAndFlush(a);
  auditoria.registrar(a.getInstitucion().getId(),escuela.auditoria.entity.AccionAuditoria.ANTICIPO_CANCELADO,"ACUERDO_ANTICIPADO",a.getId(),motivo,Map.of("pagoId",pago.getId(),"condicionesRestauradas",true));
 }
 private void crearAjuste(AcuerdoAnticipado a,Cargo c,BigDecimal monto,EfectoAjusteCargo efecto,TipoAjusteCargo tipo,Usuario actor,String descripcion) {
  var j=new AjusteCargo();j.setCargo(c);j.setAcuerdoAnticipadoId(a.getId());j.setTipo(tipo);j.setEfecto(efecto);j.setMonto(monto);j.setAutorizadoPor(actor);j.setFechaEfectiva(LocalDate.now(ZoneId.of(a.getInstitucion().getZonaHoraria())));j.setMotivo(descripcion+" · "+a.getFolio()+"; motivo y condiciones conservados en el acuerdo");j=ajustes.save(j);c.getAjustes().add(j);
 }
 private void validarPropietario(AcuerdoAnticipado a,Pago p) {
  if(!a.getInstitucion().getId().equals(p.getInstitucion().getId())||!a.getTutor().getId().equals(p.getTutor().getId())||!a.getPlantel().getId().equals(p.getPlantelRegistro().getId())||!a.getMoneda().equals(p.getMoneda()))throw new ReglaNegocioException("El pago no pertenece al tutor/plantel/moneda del acuerdo");
 }
}
