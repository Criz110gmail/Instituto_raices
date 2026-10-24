package escuela.personal.service;
import escuela.personal.dto.*;
import escuela.personal.entity.*;
import escuela.personal.repository.*;
import escuela.docente.entity.Maestro;
import escuela.docente.repository.MaestroRepository;
import escuela.institucion.repository.*;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.service.*;
import escuela.common.exception.*;
import escuela.common.service.ValidacionVersion;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.*;
import java.util.*;
import static escuela.common.mapper.NormalizacionTexto.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class EmpleadoService {
 private final EmpleadoRepository empleados;private final EmpleadoLaboralRepository laborales;private final EmpleadoPlantelRepository adscripciones;
 private final MaestroRepository maestros;private final InstitucionRepository instituciones;private final PlantelRepository planteles;
 private final UsuarioRepository usuarios;private final AlcanceDatosService alcance;private final JdbcTemplate jdbc;
 public boolean permitido(String codigo){var a=SecurityContextHolder.getContext().getAuthentication();return a!=null&&a.getAuthorities().stream().anyMatch(p->p.getAuthority().equals(codigo));}
 public UsuarioPrincipal autorizarSalarios(boolean escritura){
  actor("EMPLEADO_LEER");actor("EMPLEADO_LABORAL_LEER");
  return actor(escritura?"CONDICION_SALARIAL_ADMINISTRAR":"CONDICION_SALARIAL_LEER");
 }
 private UsuarioPrincipal actor(String permiso){
  var a=SecurityContextHolder.getContext().getAuthentication();
  if(a==null||!(a.getPrincipal() instanceof UsuarioPrincipal p)||p.usuarioId()==null||p.accesoRecuperacion()||!p.alcanceInstitucional()||!(permitido(permiso)||permiso.endsWith("_LEER")&&permitido(permiso.replace("_LEER","_ADMINISTRAR"))))throw new AccessDeniedException("Sin autorización de personal institucional");
  var u=usuarios.findById(p.usuarioId()).orElseThrow(()->new AccessDeniedException("Cuenta no disponible"));
  if(u.getEstado()!=EstadoUsuario.ACTIVO||!Objects.equals(u.getInstitucion().getId(),p.institucionId())||u.getTipoCuenta()!=escuela.seguridad.entity.TipoCuentaUsuario.ADMINISTRATIVO)throw new AccessDeniedException("Cuenta administrativa activa requerida");
  alcance.validarAdministracionInstitucional(p.institucionId());return p;
 }
 public Empleado autorizar(Long id,boolean laboral,boolean escritura){
  var p=actor(escritura&&!laboral?"EMPLEADO_ADMINISTRAR":"EMPLEADO_LEER");
  if(laboral)actor(escritura?"EMPLEADO_LABORAL_ADMINISTRAR":"EMPLEADO_LABORAL_LEER");
  var e=empleados.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el empleado",id));
  if(!Objects.equals(e.getInstitucion().getId(),p.institucionId()))throw new AccessDeniedException("Empleado fuera de tu institución");return e;
 }
 public EmpleadoVista obtener(Long id){var e=vista(autorizar(id,false,false));if(!puedeResumenLaboral())return e;var l=laboral(id);return e.conResumenLaboral(new ResumenLaboral(l.getSituacion(),l.getFechaIngreso(),l.getFechaBaja()));}
 private boolean puedeResumenLaboral(){return permitido("EMPLEADO_LABORAL_LEER")||permitido("EMPLEADO_LABORAL_ADMINISTRAR");}
 public Page<EmpleadoVista> listar(FiltroEmpleado original){var p=actor("EMPLEADO_LEER");var f=original.normalizado();
  f.validarSeleccion();
  if(f.institucionId()!=null&&!Objects.equals(f.institucionId(),p.institucionId()))throw new AccessDeniedException("Institución no autorizada");
  Specification<Empleado> s=(r,q,b)->{var pred=new ArrayList<jakarta.persistence.criteria.Predicate>();pred.add(b.equal(r.get("institucion").get("id"),p.institucionId()));
   if(f.empleadoId()!=null)pred.add(b.equal(r.get("id"),f.empleadoId()));
   if(!f.q().isBlank()){String t="%"+f.q().toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%";pred.add(b.or(b.like(b.lower(r.get("codigo")),t,'\\'),b.like(b.lower(r.get("nombres")),t,'\\'),b.like(b.lower(r.get("primerApellido")),t,'\\'),b.like(b.lower(r.get("segundoApellido")),t,'\\'),b.like(b.lower(r.get("email")),t,'\\')));}
   if(f.estado().equals("ACTIVO")||f.estado().equals("INACTIVO"))pred.add(b.equal(r.get("activo"),f.estado().equals("ACTIVO")));
   if(f.estado().equals("DOCENTE")||f.estado().equals("SIN_PERFIL_DOCENTE")){var sub=q.subquery(Long.class);var m=sub.from(Maestro.class);sub.select(m.get("id")).where(b.equal(m.get("empleado").get("id"),r.get("id")));pred.add(f.estado().equals("DOCENTE")?b.exists(sub):b.not(b.exists(sub)));}
   return b.and(pred.toArray(jakarta.persistence.criteria.Predicate[]::new));};
  var pagina=empleados.findAll(s,PageRequest.of(f.pagina(),f.tamanio(),Sort.by("primerApellido","nombres","id"))).map(this::vista);
  if(!puedeResumenLaboral()||pagina.isEmpty())return pagina;
  actor("EMPLEADO_LABORAL_LEER");
  var resumenes=new HashMap<Long,ResumenLaboral>();
  laborales.resumirPagina(p.institucionId(),pagina.stream().map(EmpleadoVista::id).toList()).forEach(l->resumenes.put(l.getEmpleadoId(),new ResumenLaboral(l.getSituacion(),l.getIngreso(),l.getBaja())));
  return pagina.map(e->e.conResumenLaboral(resumenes.get(e.id())));
 }
 private EmpleadoVista vista(Empleado e){var m=maestros.findByEmpleadoId(e.getId()).orElse(null);return new EmpleadoVista(e.getId(),e.getInstitucion().getId(),e.getCodigo(),e.getNombres(),e.getPrimerApellido(),e.getSegundoApellido(),e.getEmail(),e.getTelefono(),e.isActivo(),e.getFotografiaArchivo()!=null,e.getVersion(),m==null?null:m.getId(),m!=null&&m.isActivo(),m==null?null:m.getNumeroEmpleado(),m==null||m.getUsuario()==null?null:m.getUsuario().getUsername());}
 public java.util.List<escuela.admin.dto.OpcionAutocompletado> buscarEmpleados(String q){
  var p=actor("EMPLEADO_LEER");String t=OrganizacionLaboralService.texto(q,100);if(!t.isEmpty()&&t.length()<3)return List.of();
  return jdbc.query("SELECT id,codigo,nombres||' '||primer_apellido||coalesce(' '||segundo_apellido,'') nombre FROM empleado WHERE institucion_id=? AND strpos(lower(codigo||' '||nombres||' '||primer_apellido||coalesce(' '||segundo_apellido,'')),lower(?))>0 ORDER BY primer_apellido,nombres,id LIMIT 11",(r,n)->new escuela.admin.dto.OpcionAutocompletado(r.getLong("id"),r.getString("codigo")+" · "+r.getString("nombre"),"Empleado de tu institución"),p.institucionId(),t);
 }
 @Transactional public Long guardar(Long id,EmpleadoForm f){var p=actor("EMPLEADO_ADMINISTRAR");
  if(!Objects.equals(f.getInstitucionId(),p.institucionId()))throw new AccessDeniedException("Institución no autorizada");
  var e=id==null?new Empleado():autorizar(id,false,true);if(id!=null)ValidacionVersion.verificar(e,f.getVersion(),"Empleado");
  if(empleados.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(p.institucionId(),codigo(f.getCodigo()),id==null?0L:id))throw new RecursoDuplicadoException("Ya existe un empleado con ese código");
  if(id==null){var i=instituciones.findById(p.institucionId()).orElseThrow(()->new ReglaNegocioException("Institución no disponible"));if(!i.isActivo())throw new ReglaNegocioException("Activa primero la institución");e.setInstitucion(i);}
  e.setCodigo(codigo(f.getCodigo()));e.setNombres(limpiar(f.getNombres()));e.setPrimerApellido(limpiar(f.getPrimerApellido()));e.setSegundoApellido(limpiar(f.getSegundoApellido()));e.setEmail(email(f.getEmail()));e.setTelefono(limpiar(f.getTelefono()));empleados.saveAndFlush(e);
  if(id==null){var l=new EmpleadoLaboral();l.setEmpleado(e);laborales.save(l);}return e.getId();
 }
 @Transactional public void perfil(Long id,Long version,boolean activo,String codigoDocente){var e=autorizar(id,false,true);empleados.bloquear(id).orElseThrow();ValidacionVersion.verificar(e,version,"Empleado");
  var m=maestros.findByEmpleadoId(id).orElse(null);
  if(activo){if(!e.isActivo())throw new ReglaNegocioException("Activa el expediente antes de habilitar el perfil docente");
   if(m==null){String n=codigo(codigoDocente);if(n==null||n.isBlank())n=e.getCodigo();if(maestros.existsByInstitucionIdAndNumeroEmpleadoIgnoreCaseAndIdNot(e.getInstitucion().getId(),n,0L))throw new RecursoDuplicadoException("Código docente ya registrado");m=new Maestro();m.setInstitucion(e.getInstitucion());m.setEmpleado(e);m.setNumeroEmpleado(n);}m.setActivo(true);
  }else{if(m==null)return;if(maestros.tieneDependenciasActivas(m.getId(),LocalDate.now(ZoneId.of(e.getInstitucion().getZonaHoraria()))))throw new ReglaNegocioException("Finaliza o revisa primero asignaciones, horarios y planeaciones pendientes del maestro");m.setActivo(false);}
  maestros.saveAndFlush(m);e.setActualizadoEn(java.time.Instant.now());empleados.saveAndFlush(e);
 }
 public LaboralForm laboral(Long id){autorizar(id,true,false);var l=laborales.findByEmpleadoId(id).orElseThrow();var f=new LaboralForm();f.setPuesto(l.getPuesto());f.setFechaIngreso(l.getFechaIngreso());f.setSituacion(l.getSituacion());f.setFechaBaja(l.getFechaBaja());f.setMotivoBaja(l.getMotivoBaja());f.setVersion(l.getVersion());return f;}
 public List<escuela.admin.dto.OpcionAutocompletado> buscarPuestosLaborales(Long id,String q){
  var e=autorizar(id,true,true);String t=OrganizacionLaboralService.texto(q,100);
  if(!t.isEmpty()&&t.length()<3)return List.of();
  return jdbc.query("SELECT id,codigo,nombre FROM puesto_laboral WHERE institucion_id=? AND activo AND strpos(lower(codigo||' '||nombre),lower(?))>0 ORDER BY nombre,id LIMIT 11",
   (r,n)->new escuela.admin.dto.OpcionAutocompletado(r.getLong("id"),r.getString("nombre"),r.getString("codigo")+" · Puesto laboral"),e.getInstitucion().getId(),t);
 }
 private String resolverPuestoLaboral(Empleado e,EmpleadoLaboral actual,LaboralForm f){
  String texto=limpiar(f.getPuesto());
  if(f.getPuestoId()==null){
   if(texto==null||texto.isBlank()||Objects.equals(texto,limpiar(actual.getPuesto())))return texto;
   throw new ReglaNegocioException("Selecciona el puesto de las sugerencias. Si no existe, regístralo primero en Puestos laborales.");
  }
  var nombres=jdbc.query("SELECT nombre FROM puesto_laboral WHERE id=? AND institucion_id=? AND activo FOR SHARE",
   (r,n)->r.getString("nombre"),f.getPuestoId(),e.getInstitucion().getId());
  if(nombres.isEmpty())throw new ReglaNegocioException("Selecciona un puesto activo de tu institución; vuelve a buscarlo en las sugerencias.");
  if(!Objects.equals(texto,nombres.getFirst()))throw new ReglaNegocioException("El puesto cambió o el texto no corresponde a la selección. Vuelve a seleccionarlo antes de guardar.");
  return nombres.getFirst();
 }
 @Transactional public void guardarLaboral(Long id,LaboralForm f){var e=autorizar(id,true,true);empleados.bloquear(id).orElseThrow();var l=laborales.findByEmpleadoId(id).orElseThrow();ValidacionVersion.verificar(l,f.getVersion(),"Datos laborales");
  String puesto=resolverPuestoLaboral(e,l,f);
  if(f.getFechaIngreso()!=null&&f.getFechaIngreso().isAfter(LocalDate.now(ZoneId.of(e.getInstitucion().getZonaHoraria()))))throw new ReglaNegocioException("La fecha de ingreso no puede ser futura");
  if(f.getSituacion().equals("ACTIVO")&&(f.getFechaIngreso()==null||f.getPuesto()==null||f.getPuesto().isBlank()))throw new ReglaNegocioException("Para confirmar actividad laboral indica puesto y fecha de ingreso");
  if(f.getSituacion().equals("BAJA")&&(f.getFechaBaja()==null||f.getMotivoBaja()==null||f.getMotivoBaja().isBlank()))throw new ReglaNegocioException("La baja laboral requiere fecha y motivo; no desactiva cuenta ni grupos");
  if(f.getFechaBaja()!=null&&(f.getFechaIngreso()!=null&&f.getFechaBaja().isBefore(f.getFechaIngreso())||f.getFechaBaja().isAfter(LocalDate.now(ZoneId.of(e.getInstitucion().getZonaHoraria())))))throw new ReglaNegocioException("Revisa las fechas de ingreso y baja");
  l.setPuesto(puesto);l.setFechaIngreso(f.getFechaIngreso());l.setSituacion(f.getSituacion());l.setFechaBaja(f.getSituacion().equals("BAJA")?f.getFechaBaja():null);l.setMotivoBaja(f.getSituacion().equals("BAJA")?limpiar(f.getMotivoBaja()):null);laborales.saveAndFlush(l);
 }
 public record Adscripcion(Long id,Long version,String plantel,String inicio,String fin,boolean principal){}
 public LocalDate hoyLaboral(Long id){var e=autorizar(id,true,false);return LocalDate.now(ZoneId.of(e.getInstitucion().getZonaHoraria()));}
 public record ResumenPrincipal(LocalDate hoy,String actual,String proximo,LocalDate desde){}
 public ResumenPrincipal resumenPrincipal(Long id){
  LocalDate hoy=hoyLaboral(id);
  Specification<EmpleadoPlantel> base=(r,q,b)->b.and(b.equal(r.get("empleado").get("id"),id),b.isTrue(r.get("principal")));
  var actual=adscripciones.findAll(base.and((r,q,b)->b.and(b.lessThanOrEqualTo(r.get("fechaInicio"),hoy),b.or(b.isNull(r.get("fechaFin")),b.greaterThanOrEqualTo(r.get("fechaFin"),hoy)))),PageRequest.of(0,1,Sort.by("id")));
  var proximo=adscripciones.findAll(base.and((r,q,b)->b.greaterThan(r.get("fechaInicio"),hoy)),PageRequest.of(0,1,Sort.by("fechaInicio","id")));
  return new ResumenPrincipal(hoy,actual.isEmpty()?null:actual.getContent().getFirst().getPlantel().getNombre(),proximo.isEmpty()?null:proximo.getContent().getFirst().getPlantel().getNombre(),proximo.isEmpty()?null:proximo.getContent().getFirst().getFechaInicio());
 }
 public Page<Adscripcion> plantelesGrupo(Long id,int pagina,String grupo){
  LocalDate hoy=hoyLaboral(id);
  Specification<EmpleadoPlantel> s=(r,q,b)->b.and(b.equal(r.get("empleado").get("id"),id),switch(grupo){
   case "ACTUALES" -> b.and(b.lessThanOrEqualTo(r.get("fechaInicio"),hoy),b.or(b.isNull(r.get("fechaFin")),b.greaterThanOrEqualTo(r.get("fechaFin"),hoy)));
   case "PROXIMAS" -> b.greaterThan(r.get("fechaInicio"),hoy);
   case "HISTORICAS" -> b.lessThan(r.get("fechaFin"),hoy);
   default -> throw new IllegalArgumentException("Grupo de asignaciones no disponible");});
  var sort=grupo.equals("HISTORICAS")?Sort.by("fechaFin").descending().and(Sort.by("id").descending()):Sort.by("principal").descending().and(Sort.by("fechaInicio")).and(Sort.by("id"));
  var formato=java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
  return adscripciones.findAll(s,PageRequest.of(Math.max(0,pagina),20,sort)).map(a->new Adscripcion(a.getId(),a.getVersion(),a.getPlantel().getNombre(),formato.format(a.getFechaInicio()),a.getFechaFin()==null?"Sin fin":formato.format(a.getFechaFin()),a.isPrincipal()));
 }
 public Page<Adscripcion> planteles(Long id,int pagina){autorizar(id,true,false);var fecha=java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");return adscripciones.findByEmpleadoId(id,PageRequest.of(Math.max(0,pagina),20,Sort.by("fechaInicio").descending().and(Sort.by("id").descending()))).map(a->new Adscripcion(a.getId(),a.getVersion(),a.getPlantel().getNombre(),fecha.format(a.getFechaInicio()),a.getFechaFin()==null?"Sin fin":fecha.format(a.getFechaFin()),a.isPrincipal()));}
 @Transactional public void agregarPlantel(Long id,PlantelEmpleadoForm f){var e=autorizar(id,true,true);empleados.bloquear(id).orElseThrow();var p=planteles.findById(f.getPlantelId()).orElseThrow(()->new ReglaNegocioException("Selecciona un plantel válido"));if(!p.isActivo()||!Objects.equals(p.getInstitucion().getId(),e.getInstitucion().getId()))throw new AccessDeniedException("Plantel no permitido");if(f.getFechaFin()!=null&&f.getFechaFin().isBefore(f.getFechaInicio()))throw new ReglaNegocioException("El fin no puede ser anterior al inicio");var a=new EmpleadoPlantel();a.setEmpleado(e);a.setPlantel(p);a.setFechaInicio(f.getFechaInicio());a.setFechaFin(f.getFechaFin());a.setPrincipal(f.isPrincipal());adscripciones.saveAndFlush(a);}
 @Transactional public void finalizarPlantel(Long id,Long relacion,Long version,LocalDate fecha){autorizar(id,true,true);empleados.bloquear(id).orElseThrow();var a=adscripciones.findByIdAndEmpleadoId(relacion,id).orElseThrow(()->new AccessDeniedException("Asignación no disponible"));ValidacionVersion.verificar(a,version,"Asignación laboral");if(fecha==null||fecha.isBefore(a.getFechaInicio()))throw new ReglaNegocioException("Indica fecha final igual o posterior al inicio");if(a.getFechaFin()!=null)throw new ReglaNegocioException("La vigencia ya tiene un fin; conserva su historial");a.setFechaFin(fecha);adscripciones.saveAndFlush(a);}
 public record Cambio(String fecha,Long usuario,Long version,String situacion,String puesto,String ingreso,String baja,String motivo){}
 public Page<Cambio> historial(Long id,int pagina){var e=autorizar(id,true,false);int n=Math.max(0,pagina);Long total=jdbc.queryForObject("select count(*) from empleado_laboral_historial where empleado_id=?",Long.class,id);var formato=java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of(e.getInstitucion().getZonaHoraria()));var lista=jdbc.query("select ocurrido_en,usuario_id,version,datos->>'situacion',datos->>'puesto',datos->>'fechaIngreso',datos->>'fechaBaja',datos->>'motivoBaja' from empleado_laboral_historial where empleado_id=? order by id desc limit 20 offset ?",(rs,num)->new Cambio(formato.format(rs.getTimestamp(1).toInstant()),rs.getObject(2,Long.class),rs.getLong(3),rs.getString(4),rs.getString(5),fechaHumana(rs.getString(6)),fechaHumana(rs.getString(7)),rs.getString(8)),id,(long)n*20);return new PageImpl<>(lista,PageRequest.of(n,20),total==null?0:total);}
 private String fechaHumana(String f){return f==null?"—":LocalDate.parse(f).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));}
}
