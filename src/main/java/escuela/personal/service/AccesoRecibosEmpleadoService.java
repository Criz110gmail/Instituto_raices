package escuela.personal.service;

import escuela.personal.dto.AccesoRecibosForm;
import escuela.personal.entity.Empleado;
import escuela.personal.repository.EmpleadoRepository;
import escuela.docente.repository.MaestroRepository;
import escuela.seguridad.entity.*;
import escuela.seguridad.repository.*;
import escuela.seguridad.service.*;
import escuela.common.exception.*;
import escuela.common.service.ValidacionVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.*;
import java.util.*;
import static escuela.common.mapper.NormalizacionTexto.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class AccesoRecibosEmpleadoService {
 private final EmpleadoService personal;
 private final EmpleadoRepository empleados;
 private final MaestroRepository maestros;
 private final UsuarioRepository usuarios;
 private final InvitacionUsuarioService invitaciones;
 private final RecuperacionPasswordService recuperaciones;
 private final InvitacionUsuarioRepository invitacionRepository;
 private final RecuperacionPasswordRepository recuperacionRepository;
 private final escuela.auditoria.service.RegistroAuditoriaService auditoria;
 public record Vista(Long empleadoId,String nombre,String codigo,Long version,Long usuarioId,String username,String email,String estado,boolean habilitado,boolean compartida){}
 public record Enlace(String token,Instant expira,boolean activacion){}
 public record Identidad(Long empleadoId,Long institucionId,String nombre,String codigo,boolean docente){}
 public Vista obtener(Long id){personal.autorizarSalarios(false);return vista(personal.autorizar(id,false,false));}
 private Usuario cuenta(Empleado e){
  var m=maestros.findByEmpleadoId(e.getId()).orElse(null);var u=e.getUsuarioPortal();
  if(u==null&&m!=null)u=m.getUsuario();
  if(u!=null&&(!u.getInstitucion().getId().equals(e.getInstitucion().getId())||!Set.of(TipoCuentaUsuario.PORTAL_EMPLEADO,TipoCuentaUsuario.PORTAL_MAESTRO).contains(u.getTipoCuenta())))throw new AccessDeniedException("Cuenta incompatible con el empleado");
  if(u!=null&&m!=null&&m.getUsuario()!=null&&!u.getId().equals(m.getUsuario().getId()))throw new ReglaNegocioException("Revisa las cuentas vinculadas antes de continuar");
  return u;
 }
 private Vista vista(Empleado e){var u=cuenta(e);return new Vista(e.getId(),e.nombre(),e.getCodigo(),e.getVersion(),u==null?null:u.getId(),u==null?sugerencia(e):u.getUsername(),u==null?e.getEmail():u.getEmail(),u==null?"SIN_CUENTA":u.getEstado().name(),e.isPortalRecibosHabilitado(),u!=null&&u.getTipoCuenta()==TipoCuentaUsuario.PORTAL_MAESTRO);}
 private String sugerencia(Empleado e){String base=java.text.Normalizer.normalize(e.getNombres().split("\\s+")[0]+"."+e.getPrimerApellido().split("\\s+")[0],java.text.Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]","");base=base.substring(0,Math.min(70,base.length()));String candidato=base;int n=2;while(usuarios.existsByInstitucionIdAndUsernameIgnoreCaseAndIdNot(e.getInstitucion().getId(),candidato,0L))candidato=base+n++;return candidato;}
 @Transactional public Enlace gestionar(Long id,AccesoRecibosForm f){
  personal.autorizarSalarios(true);personal.autorizar(id,false,true);
  var e=empleados.bloquear(id).orElseThrow();ValidacionVersion.verificar(e,f.getVersion(),"Acceso del empleado");
  if(!f.isConfirmado()||!Set.of("HABILITAR","DESHABILITAR","ENLACE").contains(Objects.toString(f.getAccion(),"")))throw new ReglaNegocioException("Confirma una operación válida");
  var u=cuenta(e);boolean nueva=false;
  if(f.getAccion().equals("HABILITAR")){
   if(!e.isActivo())throw new ReglaNegocioException("Activa primero el expediente del empleado");
   if(u==null){String nombre=limpiar(f.getUsername()),correo=email(f.getEmail());if(nombre==null||!nombre.matches("[a-zA-Z0-9._-]{3,80}")||correo==null||correo.length()>254||!correo.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))throw new ReglaNegocioException("Indica un usuario de 3 a 80 letras, números, puntos o guiones y un correo válido");
    if(usuarios.existsByInstitucionIdAndUsernameIgnoreCaseAndIdNot(e.getInstitucion().getId(),nombre,0L)||usuarios.existsByInstitucionIdAndEmailIgnoreCaseAndIdNot(e.getInstitucion().getId(),correo,0L))throw new ReglaNegocioException("El usuario o correo ya pertenece a otra cuenta. No se fusionan cuentas por nombre o correo");
    u=new Usuario();u.setInstitucion(e.getInstitucion());u.setUsername(nombre);u.setEmail(correo);u.setTipoCuenta(TipoCuentaUsuario.PORTAL_EMPLEADO);u.setEstado(EstadoUsuario.INVITADO);u=usuarios.saveAndFlush(u);nueva=true;
   }else if(u.getTipoCuenta()==TipoCuentaUsuario.PORTAL_EMPLEADO&&u.getEstado()==EstadoUsuario.INACTIVO){u.setEstado(u.getPasswordHash()==null?EstadoUsuario.INVITADO:EstadoUsuario.ACTIVO);usuarios.saveAndFlush(u);}
   e.setUsuarioPortal(u);e.setPortalRecibosHabilitado(true);empleados.saveAndFlush(e);
   auditar(e,"HABILITAR");return nueva?emitir(u):null;
  }
  if(u==null||e.getUsuarioPortal()==null)throw new ReglaNegocioException("Habilita primero el acceso a recibos");
  if(f.getAccion().equals("ENLACE")){
   if(!e.isPortalRecibosHabilitado())throw new ReglaNegocioException("El acceso a recibos está desactivado");
   if(u.getTipoCuenta()==TipoCuentaUsuario.PORTAL_MAESTRO&&!personal.permitido("MAESTRO_ADMINISTRAR"))throw new AccessDeniedException("Se requiere administrar Maestros para cambiar la contraseña compartida");
   var enlace=emitir(u);auditar(e,"ENLACE");return enlace;
  }
  e.setPortalRecibosHabilitado(false);
  if(u.getTipoCuenta()==TipoCuentaUsuario.PORTAL_EMPLEADO){
   u.setEstado(EstadoUsuario.INACTIVO);usuarios.saveAndFlush(u);
   var ahora=Instant.now();var inv=invitacionRepository.findAllByUsuarioIdAndUsadoEnIsNullAndRevocadoEnIsNull(u.getId());inv.forEach(x->x.setRevocadoEn(ahora));invitacionRepository.saveAll(inv);
   var rec=recuperacionRepository.findAllByUsuarioIdAndUsadoEnIsNullAndRevocadoEnIsNull(u.getId());rec.forEach(x->x.setRevocadoEn(ahora));recuperacionRepository.saveAll(rec);
  }
  empleados.saveAndFlush(e);auditar(e,"DESHABILITAR");return null;
 }
 private void auditar(Empleado e,String operacion){auditoria.registrar(e.getInstitucion().getId(),escuela.auditoria.entity.AccionAuditoria.EMPLEADO_ACCESO_RECIBOS,"EMPLEADO",e.getId(),"Gestión de acceso personal a recibos",Map.of("operacion",operacion,"usuarioId",e.getUsuarioPortal().getId(),"habilitado",e.isPortalRecibosHabilitado()));}
 private Enlace emitir(Usuario u){if(u.getEstado()==EstadoUsuario.INVITADO){var r=invitaciones.emitir(u.getId(),Duration.ofHours(48));return new Enlace(r.token(),r.expiraEn(),true);}var r=recuperaciones.emitir(u.getId(),Duration.ofMinutes(30));return new Enlace(r.token(),r.expiraEn(),false);}
 public Identidad identidadActual(){
  var a=SecurityContextHolder.getContext().getAuthentication();if(a==null||!(a.getPrincipal() instanceof UsuarioPrincipal p)||p.usuarioId()==null||p.accesoRecuperacion())throw new AccessDeniedException("Acceso personal no disponible");
  var e=empleados.findByUsuarioPortalIdAndInstitucionId(p.usuarioId(),p.institucionId()).orElseThrow(()->new AccessDeniedException("Acceso a recibos no habilitado"));
  var u=e.getUsuarioPortal();if(u==null||!Objects.equals(u.getInstitucion().getId(),p.institucionId())||!e.isActivo()||!e.isPortalRecibosHabilitado()||!e.getInstitucion().isActivo()||u.getEstado()!=EstadoUsuario.ACTIVO||!Set.of(TipoCuentaUsuario.PORTAL_EMPLEADO,TipoCuentaUsuario.PORTAL_MAESTRO).contains(u.getTipoCuenta()))throw new AccessDeniedException("Acceso a recibos no disponible");
  boolean docente=maestros.findByEmpleadoId(e.getId()).filter(m->m.isActivo()&&m.getUsuario()!=null&&m.getUsuario().getId().equals(u.getId())).isPresent();
  return new Identidad(e.getId(),p.institucionId(),e.nombre(),e.getCodigo(),docente);
 }
}
