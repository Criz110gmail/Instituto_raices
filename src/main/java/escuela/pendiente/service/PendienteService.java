package escuela.pendiente.service;

import escuela.pendiente.dto.*;
import escuela.pendiente.entity.*;
import escuela.pendiente.repository.*;
import escuela.seguridad.entity.*;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.common.exception.ReglaNegocioException;
import escuela.common.service.ValidacionVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.time.*;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class PendienteService {
 private final PendienteRepository pendientes;
 private final PendienteCambioRepository cambios;
 private final UsuarioRepository usuarios;
 private static final List<EstadoPendiente> ABIERTOS=List.of(EstadoPendiente.PENDIENTE,EstadoPendiente.EN_PROCESO);
 public Usuario actor(UsuarioPrincipal p,boolean docente){
  if(p==null||p.accesoRecuperacion()||p.usuarioId()==null)throw denegado();
  Usuario u=usuarios.findById(p.usuarioId()).orElseThrow(PendienteService::denegado);
  if(u.getEstado()!=EstadoUsuario.ACTIVO||!Objects.equals(u.getInstitucion().getId(),p.institucionId())
    ||u.getTipoCuenta()!=(docente?TipoCuentaUsuario.PORTAL_MAESTRO:TipoCuentaUsuario.ADMINISTRATIVO))throw denegado();
  if(docente&&p.getAuthorities().stream().noneMatch(a->a.getAuthority().equals("PORTAL_MAESTRO_ACCEDER")))throw denegado();
  return u;
 }
 public LocalDate hoy(UsuarioPrincipal p,boolean docente){return hoy(actor(p,docente));}
 private LocalDate hoy(Usuario u){return LocalDate.now(ZoneId.of(u.getInstitucion().getZonaHoraria()));}
 private static AccessDeniedException denegado(){return new AccessDeniedException("Sólo puedes consultar tus propios pendientes");}
 private PendientePersonal propio(UsuarioPrincipal p,Long id){return pendientes.findByIdAndUsuarioIdAndUsuarioInstitucionId(id,p.usuarioId(),p.institucionId()).orElseThrow(PendienteService::denegado);}
 public PendienteVista obtener(UsuarioPrincipal p,boolean docente,Long id){var u=actor(p,docente);return PendienteVista.de(propio(p,id),hoy(u));}
 public Page<PendienteVista> listar(UsuarioPrincipal p,boolean docente,PendienteFiltro original){
  var u=actor(p,docente);var f=original.normalizado();f.validar();var hoy=hoy(u);
  return pendientes.findAll(especificacion(p,f,hoy),PageRequest.of(f.pagina(),f.tamanio(),Sort.by("fechaCompromiso").ascending().and(Sort.by("id").ascending()))).map(x->PendienteVista.de(x,hoy));
 }
 static Specification<PendientePersonal> especificacion(UsuarioPrincipal p,PendienteFiltro f,LocalDate hoy){return (r,q,b)->{
  var filtros=new ArrayList<jakarta.persistence.criteria.Predicate>();
  filtros.add(b.equal(r.get("usuario").get("id"),p.usuarioId()));
  filtros.add(b.equal(r.get("usuario").get("institucion").get("id"),p.institucionId()));
  if(!f.q().isBlank()){String s=f.q().toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_");filtros.add(b.like(b.lower(r.get("titulo")),"%"+s+"%",'\\'));}
  switch(f.estado()){
   case "ABIERTOS","VENCIDOS","HOY","PROXIMOS" -> filtros.add(r.get("estado").in(ABIERTOS));
   case "TODOS" -> {} default -> filtros.add(b.equal(r.get("estado"),EstadoPendiente.valueOf(f.estado())));
  }
  if(f.estado().equals("VENCIDOS"))filtros.add(b.lessThan(r.get("fechaCompromiso"),hoy));
  if(f.estado().equals("HOY"))filtros.add(b.equal(r.get("fechaCompromiso"),hoy));
  if(f.estado().equals("PROXIMOS"))filtros.add(b.between(r.get("fechaCompromiso"),hoy.plusDays(1),hoy.plusDays(7)));
  if(f.desde()!=null)filtros.add(b.greaterThanOrEqualTo(r.get("fechaCompromiso"),f.desde()));
  if(f.hasta()!=null)filtros.add(b.lessThanOrEqualTo(r.get("fechaCompromiso"),f.hasta()));
  return b.and(filtros.toArray(jakarta.persistence.criteria.Predicate[]::new));
 };}
 @Transactional public Long guardar(UsuarioPrincipal p,boolean docente,Long id,PendienteForm f){
  var u=actor(p,docente);
  if(f.getTitulo()==null||f.getTitulo().isBlank()||f.getTitulo().strip().length()>180||f.getFechaCompromiso()==null||f.getEstado()==null||f.getDescripcion()!=null&&f.getDescripcion().length()>4000)throw new ReglaNegocioException("Revisa el título, fecha, descripción y estado del pendiente");
  var e=id==null?new PendientePersonal():propio(p,id);
  if(id!=null)ValidacionVersion.verificar(e,f.getVersion(),"Pendiente");else e.setUsuario(u);
  e.setTitulo(f.getTitulo().strip());e.setDescripcion(f.getDescripcion()==null?null:f.getDescripcion().strip());e.setFechaCompromiso(f.getFechaCompromiso());
  aplicarEstado(e,f.getEstado());pendientes.saveAndFlush(e);cambios.save(new PendienteCambio(e,id==null?"CREADO":"ACTUALIZADO"));return e.getId();
 }
 @Transactional public void estado(UsuarioPrincipal p,boolean docente,Long id,Long version,EstadoPendiente estado){
  actor(p,docente);var e=propio(p,id);ValidacionVersion.verificar(e,version,"Pendiente");
  if(estado==null)throw new ReglaNegocioException("Selecciona un estado válido");
  if(e.getEstado()==estado)return;
  aplicarEstado(e,estado);pendientes.saveAndFlush(e);cambios.save(new PendienteCambio(e,"ESTADO"));
 }
 private void aplicarEstado(PendientePersonal e,EstadoPendiente estado){
  if(estado==EstadoPendiente.REALIZADO){if(e.getEstado()!=estado)e.setRealizadoEn(Instant.now());}
  else e.setRealizadoEn(null);e.setEstado(estado);
 }
 public record Cambio(String accion,String estado,String fecha,Long version){}
 public Page<Cambio> historial(UsuarioPrincipal p,boolean docente,Long id,int pagina){var u=actor(p,docente);propio(p,id);var formato=java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of(u.getInstitucion().getZonaHoraria()));return cambios.findByPendienteIdOrderByIdDesc(id,PageRequest.of(Math.max(0,pagina),20)).map(c->new Cambio(c.getAccion(),EstadoPendiente.valueOf(c.getEstado()).getEtiqueta(),formato.format(c.getOcurridoEn()),c.getVersionPendiente()));}
 public record Avisos(long vencidos,long hoy,long proximos,long total,List<PendienteVista> primeros){}
 public Avisos avisos(UsuarioPrincipal p,boolean docente){
  var u=actor(p,docente);var hoy=hoy(u);
  long vencidos=pendientes.countByUsuarioIdAndEstadoInAndFechaCompromisoBefore(u.getId(),ABIERTOS,hoy);
  long dia=pendientes.countByUsuarioIdAndEstadoInAndFechaCompromisoBetween(u.getId(),ABIERTOS,hoy,hoy);
  long proximos=pendientes.countByUsuarioIdAndEstadoInAndFechaCompromisoBetween(u.getId(),ABIERTOS,hoy.plusDays(1),hoy.plusDays(7));
  var f=new PendienteFiltro("","ABIERTOS",null,hoy.plusDays(7),0,10).normalizado();
  var primeros=pendientes.findAll(especificacion(p,f,hoy),PageRequest.of(0,5,Sort.by("fechaCompromiso").ascending().and(Sort.by("id").ascending()))).map(x->PendienteVista.de(x,hoy)).getContent();
  return new Avisos(vencidos,dia,proximos,vencidos+dia+proximos,primeros);
 }
}
