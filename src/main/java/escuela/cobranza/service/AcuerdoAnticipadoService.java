package escuela.cobranza.service;
import escuela.cobranza.dto.AnticipoForm;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.*;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
import escuela.finanzas.service.PagoService;
import escuela.finanzas.dto.request.*;
import escuela.institucion.repository.InstitucionRepository;
import escuela.tutor.repository.TutorRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.*;
import escuela.seguridad.entity.Usuario;
import escuela.common.exception.*;
import escuela.admin.dto.ModuloCatalogo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import java.math.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.security.*;
import static escuela.cobranza.support.CalculoCargo.*;
import static escuela.cobranza.support.CalculoAnticipo.beneficios;
import static escuela.cobranza.service.AnticipoLifecycleService.beca;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class AcuerdoAnticipadoService {
 private final AcuerdoAnticipadoRepository acuerdos;
 private final AcuerdoAnticipadoCargoRepository detalles;
 private final CargoRepository cargos;
 private final TutorRepository tutores;
 private final InstitucionRepository instituciones;
 private final UsuarioRepository usuarios;
 private final PagoRepository pagos;
 private final PagoService pagoService;
 private final AnticipoLifecycleService lifecycle;
 private final AlcanceDatosService alcance;
 private final escuela.auditoria.service.RegistroAuditoriaService auditoria;
 public record Fila(Long cargoId,String alumno,String concepto,String periodo,BigDecimal original,BigDecimal beca,BigDecimal totalActual,BigDecimal base,BigDecimal beneficio,BigDecimal pagar,String moneda){public BigDecimal otrosAjustes(){return totalActual.subtract(original).add(beca);}}
 public record VistaPrevia(List<Fila> filas,BigDecimal base,BigDecimal beneficio,BigDecimal pagar,String moneda,String huella){}
 public record Vista(Long id,Long version,String folio,String tutor,String plantel,String moneda,LocalDate limite,String politicaBeca,String tipoBeneficio,String motivo,String autorizadoPor,String estado,BigDecimal base,BigDecimal beneficio,BigDecimal pagar,List<Fila> filas,Long pagoId,String pagoFolio,String motivoCancelacion,Long institucionId,Long plantelId,String beneficioDescripcion,boolean pagoEnRevision){}
 public record PagoForm(@jakarta.validation.constraints.NotNull Long version,@jakarta.validation.constraints.NotNull MetodoPago metodo,@jakarta.validation.constraints.NotNull Long cuentaId,@jakarta.validation.constraints.NotNull @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha,@jakarta.validation.constraints.Size(max=150)String referencia){}

 public List<Fila> buscar(Long institucion,Long tutor,String texto,List<Long> excluir) {
  alcance.validarInstitucion(institucion);alcance.validarRecurso(ModuloCatalogo.TUTORES,tutor);
  String q=texto==null?"":texto.trim();if(!q.isEmpty()&&q.length()<3)return List.of();
  var inst=instituciones.findById(institucion).orElseThrow();
  boolean total=alcance.alcanceInstitucionalActual(institucion);var planteles=alcance.plantelesActuales(institucion);
  if(excluir!=null&&excluir.size()>100)throw new ReglaNegocioException("La selección admite hasta100 mensualidades");var omitidos=excluir==null?List.<Long>of():excluir.stream().filter(Objects::nonNull).distinct().toList();
  return cargos.buscarParaAnticipo(institucion,tutor,total,planteles.isEmpty()?Set.of(-1L):planteles,LocalDate.now(ZoneId.of(inst.getZonaHoraria())),q,omitidos.isEmpty(),omitidos.isEmpty()?Set.of(-1L):omitidos,PageRequest.of(0,q.isEmpty()?10:20)).stream()
   .map(c->fila(c,total(c),BigDecimal.ZERO,total(c))).toList();
 }
 public VistaPrevia previsualizar(AnticipoForm f) {return calcular(f,false);}
 public List<Fila> seleccionadas(List<Long> ids){if(ids==null)return List.of();return ids.stream().filter(Objects::nonNull).distinct().limit(100).map(id->{alcance.validarRecurso(ModuloCatalogo.CARGOS,id);var c=cargos.findById(id).orElseThrow();return fila(c,total(c),BigDecimal.ZERO,total(c));}).toList();}
 private VistaPrevia calcular(AnticipoForm f,boolean bloquear) {
  alcance.validarInstitucion(f.getInstitucionId());alcance.validarRecurso(ModuloCatalogo.TUTORES,f.getTutorId());
  var inst=instituciones.findById(f.getInstitucionId()).orElseThrow();LocalDate hoy=LocalDate.now(ZoneId.of(inst.getZonaHoraria()));
  if(f.getFechaLimite()==null||f.getFechaLimite().isBefore(hoy))throw new ReglaNegocioException("La fecha límite debe ser hoy o una fecha posterior");
  if(!Set.of("CONSERVAR","SUSTITUIR").contains(f.getPoliticaBeca()))throw new ReglaNegocioException("Selecciona cómo combinar la beca vigente");
  if(f.getCargoIds()==null||f.getCargoIds().isEmpty()||f.getCargoIds().size()>100||f.getCargoIds().stream().anyMatch(id->id==null||id<=0)||new HashSet<>(f.getCargoIds()).size()!=f.getCargoIds().size())throw new ReglaNegocioException("Selecciona entre1 y100 mensualidades válidas sin repetir cargos");
  var cs=f.getCargoIds().stream().sorted().map(id->(bloquear?cargos.findByIdForUpdate(id):cargos.findById(id)).orElseThrow(()->new RecursoNoEncontradoException("el cargo",id))).toList();
  var tutor=tutores.findById(f.getTutorId()).orElseThrow();if(!tutor.isActivo()||!tutor.getInstitucion().getId().equals(f.getInstitucionId()))throw new ReglaNegocioException("El tutor no pertenece a esta institución o está inactivo");
  Long plantel=cs.getFirst().getInscripcion().getPlantel().getId();String moneda=cs.getFirst().getMoneda();
  List<BigDecimal> bases=new ArrayList<>();int mes=-1;StringBuilder huella=new StringBuilder(f.getInstitucionId()+":"+f.getTutorId()+":"+f.getFechaLimite()+":"+f.getPoliticaBeca()+":"+f.getTipoBeneficio()+":"+f.getValor()+":"+f.getCargoBonificadoId());
  for(int i=0;i<cs.size();i++) {
   Cargo c=cs.get(i);lifecycle.validarCargo(c,f.getInstitucionId(),f.getTutorId(),hoy);
   if(!plantel.equals(c.getInscripcion().getPlantel().getId())||!moneda.equals(c.getMoneda()))throw new ReglaNegocioException("Selecciona mensualidades del mismo plantel y moneda; pueden ser de hermanos");
   if(detalles.existsByCargoIdAndActivoTrue(c.getId()))throw new ReglaNegocioException("Una mensualidad ya tiene un acuerdo activo. Cancela su propuesta anterior antes de incluirla nuevamente");
   if(cargos.tienePagoEnRevision(c.getId()))throw new ReglaNegocioException("Una mensualidad tiene un pago en revisión; resuélvelo primero");
   bases.add(total(c).add("SUSTITUIR".equals(f.getPoliticaBeca())?beca(c):BigDecimal.ZERO));
   if(c.getId().equals(f.getCargoBonificadoId()))mes=i;
   huella.append(':').append(c.getId()).append(':').append(c.getVersion()).append(':').append(total(c)).append(':').append(beca(c));
  }
  var bs=beneficios(bases,f.getTipoBeneficio(),f.getValor(),mes);List<Fila> filas=new ArrayList<>();
  for(int i=0;i<cs.size();i++)filas.add(fila(cs.get(i),bases.get(i),bs.get(i),bases.get(i).subtract(bs.get(i))));
  BigDecimal base=bases.stream().reduce(BigDecimal.ZERO,BigDecimal::add),beneficio=bs.stream().reduce(BigDecimal.ZERO,BigDecimal::add);
  if(base.compareTo(new BigDecimal("999999999999.99"))>0)throw new ReglaNegocioException("El total supera el importe admitido para un acuerdo; divídelo en acuerdos independientes");
  String hash;try{hash=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(huella.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}
  return new VistaPrevia(List.copyOf(filas),base,beneficio,base.subtract(beneficio),moneda,hash);
 }
 @Transactional public Long crear(AnticipoForm f) {
  Usuario actor=actor(f.getInstitucionId());var v=calcular(f,true);
  if(!Objects.equals(f.getHuella(),v.huella()))throw new ReglaNegocioException("Actualiza la vista previa antes de confirmar: la selección, fechas o importes cambiaron");
  var primero=cargos.findById(v.filas().getFirst().cargoId()).orElseThrow();var a=new AcuerdoAnticipado();a.setInstitucion(primero.getInscripcion().getAlumno().getInstitucion());a.setPlantel(primero.getInscripcion().getPlantel());a.setTutor(tutores.findById(f.getTutorId()).orElseThrow());a.setAutorizadoPor(actor);a.setFolio("ANT-"+LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)+"-"+String.format("%06d",acuerdos.siguienteFolio()));a.setFechaLimite(f.getFechaLimite());a.setPoliticaBeca(f.getPoliticaBeca());a.setTipoBeneficio(f.getTipoBeneficio());a.setMotivo(f.getMotivo().trim());a.setMoneda(v.moneda());a.setTotalBase(v.base());a.setTotalBeneficio(v.beneficio());a.setTotalPagar(v.pagar());
  a.setValorBeneficio("MENSUALIDAD".equals(f.getTipoBeneficio())?null:f.getValor());a.setCargoBonificadoId("MENSUALIDAD".equals(f.getTipoBeneficio())?f.getCargoBonificadoId():null);acuerdos.saveAndFlush(a);
  for(var r:v.filas()) {var d=new AcuerdoAnticipadoCargo();d.setAcuerdo(a);d.setCargo(cargos.findById(r.cargoId()).orElseThrow());d.setOriginal(r.original());d.setTotalActual(r.totalActual());d.setBeca(r.beca());d.setBase(r.base());d.setBeneficio(r.beneficio());d.setPagar(r.pagar());detalles.save(d);a.getCargos().add(d);}
  auditoria.registrar(a.getInstitucion().getId(),escuela.auditoria.entity.AccionAuditoria.ANTICIPO_PROPUESTO,"ACUERDO_ANTICIPADO",a.getId(),a.getMotivo(),Map.of("beneficio",a.getTotalBeneficio(),"pagar",a.getTotalPagar(),"beca",a.getPoliticaBeca()));
  return a.getId();
 }
 public Vista obtener(Long id){return vista(cargar(id));}
 public Vista obtenerPorPago(Long pagoId){alcance.validarRecurso(ModuloCatalogo.PAGOS,pagoId);var p=pagos.findById(pagoId).orElseThrow();return p.getAcuerdoAnticipadoId()==null?null:obtener(p.getAcuerdoAnticipadoId());}
 private AcuerdoAnticipado cargar(Long id) {
  var a=acuerdos.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el acuerdo",id));alcance.validarInstitucion(a.getInstitucion().getId());alcance.validarPlantel(a.getPlantel().getId());return a;
 }
 public Page<Vista> listar(Long inst,String estado,String texto,int pagina,int tamanio) {
  alcance.validarInstitucion(inst);if(!Set.of("","PROPUESTO","APLICADO","CANCELADO","VENCIDO").contains(estado))throw new ReglaNegocioException("Selecciona un estado válido");
  var institucion=instituciones.findById(inst).orElseThrow();var hoy=LocalDate.now(ZoneId.of(institucion.getZonaHoraria()));boolean total=alcance.alcanceInstitucionalActual(inst);var ps=alcance.plantelesActuales(inst);
  Specification<AcuerdoAnticipado> s=(r,q,cb)->cb.and(cb.equal(r.get("institucion").get("id"),inst),total?cb.conjunction():(ps.isEmpty()?cb.disjunction():r.get("plantel").get("id").in(ps)));
  if(!estado.isBlank())s=s.and((r,q,cb)->"VENCIDO".equals(estado)?cb.and(cb.equal(r.get("estado"),"PROPUESTO"),cb.lessThan(r.get("fechaLimite"),hoy)):"PROPUESTO".equals(estado)?cb.and(cb.equal(r.get("estado"),estado),cb.greaterThanOrEqualTo(r.get("fechaLimite"),hoy)):cb.equal(r.get("estado"),estado));
  if(texto!=null&&!texto.isBlank()){String p="%"+texto.trim().toLowerCase(Locale.ROOT)+"%";s=s.and((r,q,cb)->cb.or(cb.like(cb.lower(r.get("folio")),p),cb.like(cb.lower(r.get("tutor").get("nombres")),p),cb.like(cb.lower(r.get("motivo")),p)));}
  return acuerdos.findAll(s,PageRequest.of(Math.max(0,pagina),Math.min(100,Math.max(10,tamanio)),Sort.by(Sort.Direction.DESC,"id"))).map(this::vista);
 }
 @Transactional public Long registrarPago(Long id,PagoForm f,List<MultipartFile> archivos) {
  var a=cargar(id);actor(a.getInstitucion().getId());
  a=acuerdos.bloquear(id).orElseThrow();verificarVersion(a,f.version());
  LocalDate hoy=LocalDate.now(ZoneId.of(a.getInstitucion().getZonaHoraria()));
  if(!a.getEstado().equals("PROPUESTO")||hoy.isAfter(a.getFechaLimite()))throw new ReglaNegocioException("La propuesta está vencida o ya no admite pago; registra un pago normal sin beneficio");
  if(pagos.existsByAcuerdoAnticipadoIdAndEstadoIn(id,List.of(EstadoPago.PENDIENTE_VALIDACION,EstadoPago.VALIDADO)))throw new ReglaNegocioException("Este acuerdo ya tiene un pago pendiente o validado; consulta su folio, no registres otro");
  lifecycle.comprobarSnapshots(a,hoy,null);
  if(a.getCargos().stream().anyMatch(d->cargos.tienePagoEnRevision(d.getCargo().getId())))throw new ReglaNegocioException("Una mensualidad tiene otro pago en revisión");
  var solicitudes=a.getCargos().stream().filter(d->d.getPagar().signum()>0).map(d->new SolicitudAplicacionPagoRequest(d.getCargo().getId(),d.getPagar())).toList();
  var request=new PagoRequest(a.getInstitucion().getId(),a.getPlantel().getId(),a.getTutor().getId(),null,f.fecha().atZone(ZoneId.of(a.getInstitucion().getZonaHoraria())).toInstant(),a.getTotalPagar(),a.getMoneda(),f.metodo(),f.cuentaId(),f.referencia(),"Pago completo del acuerdo "+a.getFolio(),solicitudes);
  Long pago=pagoService.registrarAnticipado(request,archivos,id).id();a.setActualizadoEn(Instant.now());acuerdos.saveAndFlush(a);return pago;
 }
 @Transactional public void cancelar(Long id,Long version,String motivo) {
  var a=cargar(id);actor(a.getInstitucion().getId());a=acuerdos.bloquear(id).orElseThrow();verificarVersion(a,version);
  if(!a.getEstado().equals("PROPUESTO"))throw new ReglaNegocioException("Sólo se cancela aquí una propuesta. Si ya fue aplicada, cancela el pago desde Pagos recibidos para revertir también su beneficio");
  if(pagos.existsByAcuerdoAnticipadoIdAndEstadoIn(id,List.of(EstadoPago.PENDIENTE_VALIDACION,EstadoPago.VALIDADO)))throw new ReglaNegocioException("Primero rechaza o cancela el pago pendiente asociado");
  if(motivo==null||motivo.isBlank()||motivo.length()>2000)throw new ReglaNegocioException("Captura un motivo obligatorio de máximo2000 caracteres");
  a.setEstado("CANCELADO");a.setMotivoCancelacion(motivo.trim());a.setCanceladoEn(Instant.now());a.getCargos().forEach(d->d.setActivo(false));acuerdos.saveAndFlush(a);
  auditoria.registrar(a.getInstitucion().getId(),escuela.auditoria.entity.AccionAuditoria.ANTICIPO_CANCELADO,"ACUERDO_ANTICIPADO",a.getId(),motivo,Map.of("soloPropuesta",true));
 }
 private Vista vista(AcuerdoAnticipado a) {
  String estado=a.getEstado().equals("PROPUESTO")&&a.getFechaLimite().isBefore(LocalDate.now(ZoneId.of(a.getInstitucion().getZonaHoraria())))?"VENCIDO":a.getEstado();
  var p=pagos.findFirstByAcuerdoAnticipadoIdOrderByIdDesc(a.getId()).orElse(null);
  return new Vista(a.getId(),a.getVersion(),a.getFolio(),a.getTutor().getNombres()+" "+a.getTutor().getPrimerApellido(),a.getPlantel().getNombre(),a.getMoneda(),a.getFechaLimite(),a.getPoliticaBeca(),a.getTipoBeneficio(),a.getMotivo(),a.getAutorizadoPor().getUsername(),estado,a.getTotalBase(),a.getTotalBeneficio(),a.getTotalPagar(),a.getCargos().stream().map(d->new Fila(d.getCargo().getId(),d.getCargo().getInscripcion().getAlumno().getNombres()+" · "+d.getCargo().getInscripcion().getAlumno().getMatricula(),d.getCargo().getConceptoCobro().getNombre(),periodo(d.getCargo()),d.getOriginal(),d.getBeca(),d.getTotalActual(),d.getBase(),d.getBeneficio(),d.getPagar(),a.getMoneda())).toList(),p==null?null:p.getId(),p==null?null:p.getFolio(),a.getMotivoCancelacion(),a.getInstitucion().getId(),a.getPlantel().getId(),descripcionBeneficio(a),p!=null&&p.getEstado()==EstadoPago.PENDIENTE_VALIDACION);
 }
 private String descripcionBeneficio(AcuerdoAnticipado a){return "MENSUALIDAD".equals(a.getTipoBeneficio())?"Una mensualidad bonificada":"PORCENTAJE".equals(a.getTipoBeneficio())?a.getValorBeneficio().stripTrailingZeros().toPlainString()+" % adicional":"Cantidad fija: "+a.getValorBeneficio().setScale(2).toPlainString()+" "+a.getMoneda();}
 private Fila fila(Cargo c,BigDecimal base,BigDecimal beneficio,BigDecimal pagar){return new Fila(c.getId(),c.getInscripcion().getAlumno().getNombres()+" · "+c.getInscripcion().getAlumno().getMatricula(),c.getConceptoCobro().getNombre(),periodo(c),c.getImporteOriginal(),beca(c),total(c),base,beneficio,pagar,c.getMoneda());}
 private String periodo(Cargo c){return c.getPeriodoCobroInicio()+" — "+c.getPeriodoCobroFin();}
 private void verificarVersion(AcuerdoAnticipado a,Long v){if(!Objects.equals(a.getVersion(),v))throw new ReglaNegocioException("El acuerdo cambió; recarga la pantalla antes de continuar");}
 private Usuario actor(Long inst){var auth=SecurityContextHolder.getContext().getAuthentication();if(auth==null||!(auth.getPrincipal() instanceof UsuarioPrincipal p)||p.accesoRecuperacion()||p.usuarioId()==null)throw new ReglaNegocioException("El acuerdo requiere una cuenta administrativa identificable");if(auth.getAuthorities().stream().noneMatch(x->x.getAuthority().equals("CONVENIO_PAGO_ADMINISTRAR")))throw new AccessDeniedException("No tienes permiso para autorizar este acuerdo");var u=usuarios.findById(p.usuarioId()).orElseThrow();if(!u.getInstitucion().getId().equals(inst))throw new AccessDeniedException("La cuenta administrativa no pertenece a la institución");return u;}
}
