package escuela.cobranza.service;
import escuela.cobranza.dto.*;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.*;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.common.exception.*;
import escuela.finanzas.repository.SolicitudAplicacionPagoRepository;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static escuela.cobranza.support.CalculoCargo.*;
import escuela.seguridad.service.UsuarioPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ConvenioPagoService {
 private final ConvenioPagoRepository convenios; private final ConvenioPagoCargoOriginalRepository originales;
 private final ConvenioPagoCargoNuevoRepository nuevos; private final CargoRepository cargos;
 private final TutorRepository tutores; private final ConceptoCobroRepository conceptos;
 private final InstitucionRepository instituciones; private final AlumnoTutorRepository vinculos;
 private final SolicitudAplicacionPagoRepository solicitudes; private final AlcanceDatosService alcance;

 public Page<ConvenioPagoFila> listar(Long institucionId,EstadoConvenioPago estado,String texto,int pagina,int tamanio){
  Specification<ConvenioPago>s=alcance.especificacion(escuela.admin.dto.ModuloCatalogo.CONVENIOS_PAGO);
  if(institucionId!=null)s=s.and((r,q,cb)->cb.equal(r.get("institucion").get("id"),institucionId));
  if(estado!=null)s=s.and((r,q,cb)->cb.equal(r.get("estado"),estado));
  if(texto!=null&&!texto.isBlank()){String p="%"+texto.trim().toLowerCase(Locale.ROOT)+"%";s=s.and((r,q,cb)->cb.or(cb.like(cb.lower(r.get("folio")),p),cb.like(cb.lower(r.get("descripcion")),p),cb.like(cb.lower(r.get("tutor").get("nombres")),p),cb.like(cb.lower(r.get("tutor").get("primerApellido")),p)));}
  return convenios.findAll(s,PageRequest.of(Math.max(0,pagina),Math.min(100,Math.max(10,tamanio)),Sort.by(Sort.Direction.DESC,"fechaAcuerdo","id"))).map(this::fila);
 }
 public List<CargoConvenioOpcion> buscarCargos(Long institucionId,Long tutorId,String texto){
  alcance.validarInstitucion(institucionId); alcance.validarRecurso(escuela.admin.dto.ModuloCatalogo.TUTORES,tutorId);
  return cargos.buscarParaSolicitudPago(institucionId,tutorId,texto==null?"":texto.trim(),PageRequest.of(0,20)).stream()
    .filter(c->saldo(c).signum()>0).map(this::opcion).toList();
 }
 public List<CargoConvenioOpcion> cargosSeleccionados(List<Long> ids){if(ids==null||ids.isEmpty())return List.of();return ids.stream().distinct().map(cargos::findById).flatMap(Optional::stream).map(this::opcion).toList();}
 public String tutorEtiqueta(Long id){if(id==null)return "";return tutores.findById(id).map(t->nombre(t.getNombres(),t.getPrimerApellido(),t.getSegundoApellido())+" · "+t.getTelefonoPrincipal()).orElse("");}
 public String conceptoEtiqueta(Long id){if(id==null)return "";return conceptos.findById(id).map(c->c.getCodigo()+" · "+c.getNombre()).orElse("");}
 @Transactional public Long crear(ConvenioPagoForm f){
  if(f.getModalidad()==ModalidadConvenioPago.CONDONACION_TOTAL)f.setFechaVencimiento(f.getFechaAcuerdo());
  validarFormulario(f); alcance.validarInstitucion(f.getInstitucionId());
  var tutor=tutores.findByIdForUpdate(f.getTutorId()).orElseThrow(()->new RecursoNoEncontradoException("el tutor",f.getTutorId()));
  alcance.validarRecurso(escuela.admin.dto.ModuloCatalogo.TUTORES,tutor.getId());
  var concepto=conceptos.findById(f.getConceptoCobroId()).orElseThrow(()->new RecursoNoEncontradoException("el concepto de cobro",f.getConceptoCobroId()));
  var institucion=instituciones.findById(f.getInstitucionId()).orElseThrow(()->new RecursoNoEncontradoException("la institución",f.getInstitucionId()));
  if(!tutor.getInstitucion().getId().equals(institucion.getId())||!concepto.getInstitucion().getId().equals(institucion.getId())||!concepto.isActivo())throw new ReglaNegocioException("El tutor y el concepto deben estar activos y pertenecer a la institución seleccionada");
  List<Long> ids=f.getCargoIds().stream().filter(Objects::nonNull).distinct().toList();
  if(ids.isEmpty())throw new ReglaNegocioException("Selecciona al menos un adeudo pendiente válido");
  if(solicitudes.existePendiente(tutor.getId(),ids))throw new ReglaNegocioException("Uno de los cargos tiene una transferencia pendiente de validación; valídala o recházala antes de crear el convenio");
  List<Cargo> seleccionados=ids.stream().sorted().map(id->cargos.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("el cargo",id))).toList();
  BigDecimal saldoTotal=BigDecimal.ZERO;
  for(Cargo c:seleccionados){validarCargo(c,tutor.getId(),institucion.getId(),f.getFechaAcuerdo());saldoTotal=saldoTotal.add(saldo(c));}
  String moneda=seleccionados.getFirst().getMoneda();if(seleccionados.stream().anyMatch(c->!moneda.equals(c.getMoneda())))throw new ReglaNegocioException("Todos los cargos del convenio deben usar la misma moneda");
  boolean condonacion=f.getModalidad()==ModalidadConvenioPago.CONDONACION_TOTAL;
  UsuarioPrincipal actor=condonacion?actorPersistido():null;
  saldoTotal=dinero(saldoTotal); BigDecimal acordado=condonacion?new BigDecimal("0.00"):dinero(f.getMontoAcordado());
  if(!condonacion&&(acordado.signum()<=0||acordado.compareTo(saldoTotal)>0))throw new ReglaNegocioException("En un convenio con monto por pagar indica al menos $0.01 sin superar el saldo pendiente de "+saldoTotal);
  ConvenioPago cv=new ConvenioPago();cv.setInstitucion(institucion);cv.setTutor(tutor);cv.setConceptoCobro(concepto);cv.setFolio(folio(f.getFechaAcuerdo()));cv.setFechaAcuerdo(f.getFechaAcuerdo());cv.setFechaVencimiento(f.getFechaVencimiento());cv.setDescripcion(f.getDescripcion().trim());cv.setMotivo(f.getMotivo().trim());cv.setCondiciones(limpio(f.getCondiciones()));cv.setMoneda(moneda);cv.setSaldoOriginalTotal(saldoTotal);cv.setMontoAcordadoTotal(acordado);cv.setMontoCondonadoTotal(dinero(saldoTotal.subtract(acordado)));
  cv.setModalidad(f.getModalidad());
  if(condonacion){cv.setEstado(EstadoConvenioPago.CONDONADO_TOTAL);cv.setAutorizadoPorId(actor.usuarioId());cv.setAutorizadoPorNombre(actor.username());cv.setAutorizadoEn(Instant.now());}
  convenios.saveAndFlush(cv);
  for(Cargo c:seleccionados){ConvenioPagoCargoOriginal d=new ConvenioPagoCargoOriginal();d.setConvenio(cv);d.setCargo(c);d.setImporteTotalSnapshot(total(c));d.setMontoAplicadoSnapshot(aplicado(c));d.setSaldoIncluido(saldo(c));originales.save(d);c.setEstadoRegistro(EstadoRegistroCargo.CONVENIDO);}
  if(condonacion)return cv.getId();
  Map<Long,List<Cargo>> porInscripcion=new LinkedHashMap<>();seleccionados.forEach(c->porInscripcion.computeIfAbsent(c.getInscripcion().getId(),x->new ArrayList<>()).add(c));
  BigDecimal asignado=BigDecimal.ZERO;int indice=0;
  for(List<Cargo> grupo:porInscripcion.values()){indice++;BigDecimal saldoGrupo=dinero(grupo.stream().map(c->saldo(c)).reduce(BigDecimal.ZERO,BigDecimal::add));BigDecimal parte=indice==porInscripcion.size()?dinero(acordado.subtract(asignado)):dinero(acordado.multiply(saldoGrupo).divide(saldoTotal,2,RoundingMode.HALF_UP));if(parte.signum()<=0)throw new ReglaNegocioException("El monto acordado es demasiado pequeño para distribuirlo entre todos los alumnos seleccionados");asignado=asignado.add(parte);Cargo base=grupo.getFirst();Cargo nuevo=new Cargo();nuevo.setInscripcion(base.getInscripcion());nuevo.setConceptoCobro(concepto);nuevo.setClaveGeneracion("CONVENIO:"+cv.getId()+":"+base.getInscripcion().getId());nuevo.setDescripcion(f.getDescripcion().trim());nuevo.setPeriodoCobroInicio(grupo.stream().map(Cargo::getPeriodoCobroInicio).min(LocalDate::compareTo).orElse(f.getFechaAcuerdo()));nuevo.setPeriodoCobroFin(grupo.stream().map(Cargo::getPeriodoCobroFin).max(LocalDate::compareTo).orElse(f.getFechaAcuerdo()));nuevo.setFechaEmision(f.getFechaAcuerdo());nuevo.setFechaVencimiento(f.getFechaVencimiento());nuevo.setImporteOriginal(parte);nuevo.setMoneda(cv.getMoneda());nuevo.setEstadoRegistro(EstadoRegistroCargo.EMITIDO);cargos.save(nuevo);ConvenioPagoCargoNuevo d=new ConvenioPagoCargoNuevo();d.setConvenio(cv);d.setCargo(nuevo);d.setInscripcion(base.getInscripcion());d.setSaldoOriginalGrupo(saldoGrupo);d.setMontoAcordado(parte);nuevos.save(d);}
  return cv.getId();
 }
 public ConvenioPagoDetalle detalle(Long id){alcance.validarRecurso(escuela.admin.dto.ModuloCatalogo.CONVENIOS_PAGO,id);return detalleDe(convenios.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el convenio",id)));}
 @Transactional public void cancelar(Long id,Long version,String motivo){
  ConvenioPago c=convenios.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("el convenio",id));alcance.validarRecurso(escuela.admin.dto.ModuloCatalogo.CONVENIOS_PAGO,id);
  if(!Objects.equals(c.getVersion(),version))throw new ReglaNegocioException("El convenio fue modificado por otro usuario; recarga la pantalla");
  if(motivo==null||motivo.isBlank()||motivo.length()>2000)throw new ReglaNegocioException("Captura el motivo de cancelación, máximo 2000 caracteres");
  if(c.getEstado()==EstadoConvenioPago.CANCELADO)throw new ReglaNegocioException("El convenio ya está cancelado");
  if(c.getModalidad()==ModalidadConvenioPago.CONDONACION_TOTAL)actorPersistido();
  for(var d:c.getCargosNuevos()){Cargo cargo=cargos.findByIdForUpdate(d.getCargo().getId()).orElseThrow();if(aplicado(cargo).signum()>0)throw new ReglaNegocioException("No se puede cancelar porque uno de los cargos del convenio ya tiene pagos aplicados");if(cargo.getEstadoRegistro()!=EstadoRegistroCargo.EMITIDO)throw new ReglaNegocioException("Uno de los cargos generados ya no está vigente");cargo.setEstadoRegistro(EstadoRegistroCargo.CANCELADO);cargo.setCanceladoEn(Instant.now());cargo.setMotivoCancelacion("Cancelación del convenio "+c.getFolio()+": "+motivo.trim());}
  for(var d:c.getCargosOriginales()){Cargo cargo=cargos.findByIdForUpdate(d.getCargo().getId()).orElseThrow();if(cargo.getEstadoRegistro()!=EstadoRegistroCargo.CONVENIDO)throw new ReglaNegocioException("Uno de los cargos originales cambió de estado");
   if(c.getModalidad()==ModalidadConvenioPago.CONDONACION_TOTAL&&(total(cargo).compareTo(d.getImporteTotalSnapshot())!=0||aplicado(cargo).compareTo(d.getMontoAplicadoSnapshot())!=0))throw new ReglaNegocioException("Los abonos o ajustes del adeudo original cambiaron; revisa el historial antes de restaurar la deuda condonada");
   cargo.setEstadoRegistro(EstadoRegistroCargo.EMITIDO);d.setActivo(false);}
  c.setEstado(EstadoConvenioPago.CANCELADO);c.setCanceladoEn(Instant.now());c.setMotivoCancelacion(motivo.trim());
 }
 private void validarFormulario(ConvenioPagoForm f){if(f.getFechaAcuerdo()!=null&&f.getFechaVencimiento()!=null&&f.getFechaVencimiento().isBefore(f.getFechaAcuerdo()))throw new ReglaNegocioException("La fecha de vencimiento no puede ser anterior a la fecha del acuerdo");if(f.getCargoIds()==null||f.getCargoIds().isEmpty())throw new ReglaNegocioException("Selecciona al menos un cargo pendiente");}
 private void validarCargo(Cargo c,Long tutorId,Long institucionId,LocalDate fecha){alcance.validarRecurso(escuela.admin.dto.ModuloCatalogo.CARGOS,c.getId());if(c.getEstadoRegistro()!=EstadoRegistroCargo.EMITIDO||saldo(c).signum()<=0)throw new ReglaNegocioException("Uno de los cargos ya no tiene saldo disponible para convenio");if(!c.getInscripcion().getAlumno().getInstitucion().getId().equals(institucionId)||!vinculos.tieneResponsabilidadFinancieraVigente(c.getInscripcion().getAlumno().getId(),tutorId,fecha))throw new ReglaNegocioException("El tutor no es responsable financiero vigente de uno de los alumnos seleccionados");}
 private String folio(LocalDate fecha){return "CV-"+fecha.format(DateTimeFormatter.BASIC_ISO_DATE)+"-"+String.format("%06d",convenios.siguienteFolio());}
 private CargoConvenioOpcion opcion(Cargo c){var a=c.getInscripcion().getAlumno();return new CargoConvenioOpcion(c.getId(),c.getInscripcion().getId(),nombre(a.getNombres(),a.getPrimerApellido(),a.getSegundoApellido()),a.getMatricula(),c.getConceptoCobro().getNombre(),c.getDescripcion(),c.getFechaVencimiento(),total(c),aplicado(c),c.getEstadoRegistro()==EstadoRegistroCargo.EMITIDO?saldo(c):BigDecimal.ZERO,c.getMoneda());}
 private ConvenioPagoFila fila(ConvenioPago c){return new ConvenioPagoFila(c.getId(),c.getFolio(),nombre(c.getTutor().getNombres(),c.getTutor().getPrimerApellido(),c.getTutor().getSegundoApellido()),c.getFechaAcuerdo(),c.getFechaVencimiento(),c.getSaldoOriginalTotal(),c.getMontoAcordadoTotal(),c.getMontoCondonadoTotal(),c.getMoneda(),situacion(c));}
 private ConvenioPagoDetalle detalleDe(ConvenioPago c){return new ConvenioPagoDetalle(c.getId(),c.getVersion(),c.getFolio(),c.getInstitucion().getNombre(),nombre(c.getTutor().getNombres(),c.getTutor().getPrimerApellido(),c.getTutor().getSegundoApellido()),c.getFechaAcuerdo(),c.getFechaVencimiento(),c.getDescripcion(),c.getMotivo(),c.getCondiciones(),c.getMoneda(),c.getSaldoOriginalTotal(),c.getMontoAcordadoTotal(),c.getMontoCondonadoTotal(),c.getEstado(),situacion(c),c.getMotivoCancelacion(),c.getCargosOriginales().stream().map(this::opcionOriginal).toList(),c.getCargosNuevos().stream().map(x->opcion(x.getCargo())).toList(),c.getAutorizadoPorId(),c.getAutorizadoPorNombre(),c.getAutorizadoEn()==null?null:DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of(c.getInstitucion().getZonaHoraria()==null?"America/Mexico_City":c.getInstitucion().getZonaHoraria())).format(c.getAutorizadoEn()));}
 private CargoConvenioOpcion opcionOriginal(ConvenioPagoCargoOriginal d){Cargo c=d.getCargo();var a=c.getInscripcion().getAlumno();return new CargoConvenioOpcion(c.getId(),c.getInscripcion().getId(),nombre(a.getNombres(),a.getPrimerApellido(),a.getSegundoApellido()),a.getMatricula(),c.getConceptoCobro().getNombre(),c.getDescripcion(),c.getFechaVencimiento(),d.getImporteTotalSnapshot(),d.getMontoAplicadoSnapshot(),d.getSaldoIncluido(),c.getMoneda());}
 private String situacion(ConvenioPago c){if(c.getEstado()==EstadoConvenioPago.CANCELADO)return "CANCELADO";if(c.getModalidad()==ModalidadConvenioPago.CONDONACION_TOTAL)return "CONDONADO_TOTAL";BigDecimal pendiente=c.getCargosNuevos().stream().map(x->saldo(x.getCargo())).reduce(BigDecimal.ZERO,BigDecimal::add);if(pendiente.signum()==0)return "CUMPLIDO";return c.getFechaVencimiento().isBefore(LocalDate.now())?"VENCIDO":"VIGENTE";}
 private UsuarioPrincipal actorPersistido(){var a=SecurityContextHolder.getContext().getAuthentication();if(a!=null&&a.isAuthenticated()&&a.getPrincipal() instanceof UsuarioPrincipal p&&!p.accesoRecuperacion()&&p.usuarioId()!=null)return p;throw new ReglaNegocioException("La condonación requiere una cuenta administrativa identificable; el acceso de recuperación no puede autorizarla");}
 private String nombre(String n,String p,String s){return (n+" "+p+(s==null?"":" "+s)).trim();}private String limpio(String s){return s==null||s.isBlank()?null:s.trim();}private BigDecimal dinero(BigDecimal n){return n.setScale(2,RoundingMode.HALF_UP);}
}
