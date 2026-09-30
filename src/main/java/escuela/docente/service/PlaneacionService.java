package escuela.docente.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import escuela.academico.entity.*;
import escuela.academico.repository.GrupoRepository;
import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.common.exception.*;
import escuela.docente.dto.*;
import escuela.docente.entity.*;
import escuela.docente.repository.*;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor @Transactional
public class PlaneacionService {
    private final PlaneacionSemanalRepository planeaciones;
    private final PlaneacionHistorialRepository historiales;
    private final PlaneacionVersionPublicadaRepository versiones;
    private final MaestroRepository maestros;
    private final AsignacionMaestroRepository asignaciones;
    private final GrupoRepository grupos;
    private final UsuarioRepository usuarios;
    private final AlcanceDatosService alcance;
    private final RegistroAuditoriaService auditoria;
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    public Long crear(UsuarioPrincipal principal, PlaneacionRequest request){
        Maestro maestro=maestro(principal);Grupo grupo=grupo(request.grupoId());
        PlaneacionSemanal p=new PlaneacionSemanal();p.setInstitucion(maestro.getInstitucion());p.setMaestro(maestro);p.setGrupo(grupo);p.setCicloEscolar(grupo.getCicloEscolar());p.setEstado(EstadoPlaneacion.BORRADOR);
        validarYCopiar(p,request,maestro,true);p=planeaciones.saveAndFlush(p);historial(p,null,EstadoPlaneacion.BORRADOR,"Planeación creada");return p.getId();
    }

    public void actualizar(UsuarioPrincipal principal,Long id,PlaneacionRequest request){
        Maestro maestro=maestro(principal);PlaneacionSemanal p=bloquearPropia(id,maestro);verificar(p,request.version(),"Planeación");exigirEditable(p);
        validarYCopiar(p,request,maestro,false);planeaciones.saveAndFlush(p);
    }

    public void enviar(UsuarioPrincipal principal,Long id,Long version){
        Maestro maestro=maestro(principal);PlaneacionSemanal p=bloquearPropia(id,maestro);verificar(p,version,"Planeación");
        if(!Set.of(EstadoPlaneacion.BORRADOR,EstadoPlaneacion.REQUIERE_AJUSTES,EstadoPlaneacion.REABIERTA).contains(p.getEstado()))throw new ReglaNegocioException("La planeación no está disponible para envío");
        validarCompleta(p);transicion(p,EstadoPlaneacion.ENVIADA,null,AccionAuditoria.PLANEACION_ENVIADA);p.setEnviadaEn(Instant.now());
    }

    public void descartar(UsuarioPrincipal principal,Long id,Long version,String motivo){
        Maestro maestro=maestro(principal);PlaneacionSemanal p=bloquearPropia(id,maestro);verificar(p,version,"Planeación");
        if(!Set.of(EstadoPlaneacion.BORRADOR,EstadoPlaneacion.REQUIERE_AJUSTES,EstadoPlaneacion.REABIERTA).contains(p.getEstado()))throw new ReglaNegocioException("Sólo una planeación editable puede descartarse");
        transicion(p,EstadoPlaneacion.DESCARTADA,limpiar(motivo),AccionAuditoria.PLANEACION_DESCARTADA);
    }

    public void iniciarRevision(Long id,Long version){PlaneacionSemanal p=bloquearAdmin(id);verificar(p,version,"Planeación");if(p.getEstado()!=EstadoPlaneacion.ENVIADA)throw new ReglaNegocioException("Sólo una planeación enviada puede pasar a revisión");p.setRevisionIniciadaEn(Instant.now());transicion(p,EstadoPlaneacion.EN_REVISION,null,AccionAuditoria.PLANEACION_REVISION_INICIADA);}
    public void solicitarAjustes(Long id,Long version,String motivo){PlaneacionSemanal p=bloquearAdmin(id);verificar(p,version,"Planeación");if(p.getEstado()!=EstadoPlaneacion.EN_REVISION)throw new ReglaNegocioException("La planeación debe estar en revisión");if(limpiar(motivo)==null)throw new ReglaNegocioException("Escribe las correcciones solicitadas");transicion(p,EstadoPlaneacion.REQUIERE_AJUSTES,limpiar(motivo),AccionAuditoria.PLANEACION_AJUSTES_SOLICITADOS);}
    public void publicar(Long id,Long version){
        PlaneacionSemanal p=bloquearAdmin(id);verificar(p,version,"Planeación");if(p.getEstado()!=EstadoPlaneacion.EN_REVISION)throw new ReglaNegocioException("La planeación debe estar en revisión antes de publicarse");validarCompleta(p);
        int revision=p.getNumeroRevision()+1;Instant ahora=Instant.now();Usuario actor=actor();p.setNumeroRevision(revision);p.setPublicadaEn(ahora);p.setPublicadaPor(actor);p.setEstado(EstadoPlaneacion.PUBLICADA);
        PlaneacionDocumento documento=documento(p,revision,ahora);PlaneacionVersionPublicada v=new PlaneacionVersionPublicada();v.setPlaneacion(p);v.setNumeroRevision(revision);v.setContenidoJson(serializar(documento));v.setPublicadaEn(ahora);v.setPublicadaPor(actor);versiones.save(v);
        historial(p,EstadoPlaneacion.EN_REVISION,EstadoPlaneacion.PUBLICADA,null);auditoria(p,AccionAuditoria.PLANEACION_PUBLICADA,null);
    }
    public void reabrir(Long id,Long version,String motivo){PlaneacionSemanal p=bloquearAdmin(id);verificar(p,version,"Planeación");if(p.getEstado()!=EstadoPlaneacion.PUBLICADA)throw new ReglaNegocioException("Sólo una planeación publicada puede reabrirse");if(limpiar(motivo)==null)throw new ReglaNegocioException("El motivo de reapertura es obligatorio");transicion(p,EstadoPlaneacion.REABIERTA,limpiar(motivo),AccionAuditoria.PLANEACION_REABIERTA);}

    @Transactional(readOnly=true) public PlaneacionDocumento detalleMaestro(UsuarioPrincipal principal,Long id){Maestro m=maestro(principal);PlaneacionSemanal p=detalle(id);if(!p.getMaestro().getId().equals(m.getId()))throw new org.springframework.security.access.AccessDeniedException("Planeación no autorizada");return documento(p,p.getNumeroRevision(),p.getPublicadaEn());}
    @Transactional(readOnly=true) public PlaneacionDocumento detalleAdmin(Long id){alcance.validarRecurso(escuela.admin.dto.ModuloCatalogo.PLANEACIONES,id);PlaneacionSemanal p=detalle(id);return documento(p,p.getNumeroRevision(),p.getPublicadaEn());}
    @Transactional(readOnly=true) public Long versionEntidad(Long id){return detalle(id).getVersion();}
    @Transactional(readOnly=true) public EstadoPlaneacion estado(Long id){return detalle(id).getEstado();}
    @Transactional(readOnly=true) public List<HistorialPlaneacionFila> historial(Long id){detalleAdmin(id);return historiales.findAllByPlaneacionIdOrderByOcurridoEnDescIdDesc(id).stream().map(h->new HistorialPlaneacionFila(h.getEstadoAnterior(),h.getEstadoNuevo(),h.getMotivo(),h.getActor()==null?"Sistema":h.getActor().getUsername(),h.getOcurridoEn())).toList();}
    @Transactional(readOnly=true) public List<PlaneacionVersionPublicada> versiones(Long id){detalleAdmin(id);return versiones.findAllByPlaneacionIdOrderByNumeroRevisionDesc(id);}
    @Transactional(readOnly=true) public PlaneacionDocumento version(Long id,int revision,boolean docente,UsuarioPrincipal principal){if(docente)detalleMaestro(principal,id);else detalleAdmin(id);PlaneacionVersionPublicada v=versiones.findByPlaneacionIdAndNumeroRevision(id,revision).orElseThrow(()->new RecursoNoEncontradoException("la versión publicada",(long)revision));return deserializar(v.getContenidoJson());}

    @Transactional(readOnly=true) public Page<PlaneacionFila> listarMaestro(UsuarioPrincipal principal,int pagina){return listarMaestro(principal,null,null,null,pagina);}
    @Transactional(readOnly=true) public Page<PlaneacionFila> listarMaestro(UsuarioPrincipal principal,LocalDate desde,LocalDate hasta,EstadoPlaneacion estado,int pagina){
        Maestro m=maestro(principal);
        Specification<PlaneacionSemanal>s=(r,q,c)->c.equal(r.get("maestro").get("id"),m.getId());
        if(desde!=null)s=s.and((r,q,c)->c.greaterThanOrEqualTo(r.get("fechaFin"),desde));
        if(hasta!=null)s=s.and((r,q,c)->c.lessThanOrEqualTo(r.get("fechaInicio"),hasta));
        if(estado!=null)s=s.and((r,q,c)->c.equal(r.get("estado"),estado));
        if(desde!=null&&hasta!=null&&hasta.isBefore(desde))s=s.and((r,q,c)->c.disjunction());
        return planeaciones.findAll(s,PageRequest.of(Math.max(0,pagina),20,Sort.by(Sort.Direction.DESC,"fechaInicio","id"))).map(this::fila);
    }
    @Transactional(readOnly=true) public Page<PlaneacionFila> listarAdmin(FiltroPlaneacion original){FiltroPlaneacion f=original.normalizado();Specification<PlaneacionSemanal>s=criterios(f).and(alcance.especificacion(escuela.admin.dto.ModuloCatalogo.PLANEACIONES));return planeaciones.findAll(s,PageRequest.of(f.pagina(),f.tamanio(),Sort.by(Sort.Direction.DESC,"fechaInicio","id"))).map(this::fila);}
    @Transactional(readOnly=true) public Page<PlaneacionFila> bloqueAdmin(FiltroPlaneacion original){return listarAdmin(original);}
    @Transactional(readOnly=true) public MaestroResponse perfil(UsuarioPrincipal principal){
        Maestro m=maestro(principal);
        return new MaestroResponse(m.getId(),m.getInstitucion().getId(),m.getInstitucion().getNombre(),m.getNumeroEmpleado(),m.getNombres(),m.getPrimerApellido(),m.getSegundoApellido(),nombre(m),m.getEmail(),m.getTelefono(),m.isActivo(),m.getUsuario()==null?null:m.getUsuario().getId(),m.getUsuario()==null?null:m.getUsuario().getUsername(),m.getUsuario()==null?null:m.getUsuario().getEstado(),m.getVersion());
    }
    @Transactional(readOnly=true) public List<AsignacionMaestroResponse> gruposDisponibles(UsuarioPrincipal principal,LocalDate inicio,LocalDate fin){Maestro m=maestro(principal);LocalDate i=inicio==null?LocalDate.now():inicio, f=fin==null?i.plusDays(6):fin;return asignaciones.vigentes(m.getId(),i,f).stream().collect(Collectors.toMap(a->a.getGrupo().getId(),Function.identity(),(a,b)->a,LinkedHashMap::new)).values().stream().map(a->new AsignacionMaestroResponse(a.getId(),a.getGrupo().getId(),a.getGrupo().getNombre(),a.getGrupo().getPlantel().getNombre(),a.getGrupo().getCicloEscolar().getNombre(),a.getMateria().getId(),a.getMateria().getNombre(),a.getFechaInicio(),a.getFechaFin(),a.isActivo(),a.getVersion())).toList();}
    @Transactional(readOnly=true) public List<PlaneacionDocumento.Materia> materiasDisponibles(UsuarioPrincipal principal,Long grupoId,LocalDate inicio,LocalDate fin){Maestro m=maestro(principal);if(grupoId==null||inicio==null||fin==null)return List.of();return asignaciones.materiasVigentes(m.getId(),grupoId,inicio,fin).stream().filter(a->!a.getFechaInicio().isAfter(inicio)&&(a.getFechaFin()==null||!a.getFechaFin().isBefore(fin))).map(a->new PlaneacionDocumento.Materia(a.getMateria().getId(),a.getMateria().getCodigo(),a.getMateria().getNombre())).distinct().toList();}

    private void validarYCopiar(PlaneacionSemanal p,PlaneacionRequest r,Maestro m,boolean nueva){Grupo g=grupo(r.grupoId());if(!nueva&&!p.getGrupo().getId().equals(g.getId()))throw new ReglaNegocioException("No se puede cambiar el grupo de una planeación existente");validarFechas(g,r.fechaInicio(),r.fechaFin());if(planeaciones.existeTraslape(m.getId(),g.getId(),r.fechaInicio(),r.fechaFin(),nueva?0L:p.getId()))throw new RecursoDuplicadoException("Ya existe otra planeación de este maestro y grupo que coincide con esas fechas");List<AsignacionMaestro> vigentes=asignaciones.materiasVigentes(m.getId(),g.getId(),r.fechaInicio(),r.fechaFin()).stream().filter(a->!a.getFechaInicio().isAfter(r.fechaInicio())&&(a.getFechaFin()==null||!a.getFechaFin().isBefore(r.fechaFin()))).toList();Map<Long,Materia> permitidas=vigentes.stream().map(AsignacionMaestro::getMateria).collect(Collectors.toMap(Materia::getId,Function.identity(),(a,b)->a));LinkedHashSet<Long> ids=new LinkedHashSet<>(r.materiaIds()==null?List.of():r.materiaIds());if(ids.isEmpty())throw new ReglaNegocioException("Selecciona al menos una materia");if(!permitidas.keySet().containsAll(ids))throw new ReglaNegocioException("Una o más materias no están asignadas al maestro para todo el rango elegido");p.setFechaInicio(r.fechaInicio());p.setFechaFin(r.fechaFin());p.setProposito(limpiar(r.proposito()));p.setSituacionDidactica(limpiar(r.situacionDidactica()));p.setEjesArticuladores(limpiar(r.ejesArticuladores()));p.setConocimientos(limpiar(r.conocimientos()));p.setHabilidades(limpiar(r.habilidades()));p.setActitudes(limpiar(r.actitudes()));p.setTecnicaEvaluacion(limpiar(r.tecnicaEvaluacion()));p.setInstrumentoEvaluacion(limpiar(r.instrumentoEvaluacion()));p.setRecursos(limpiar(r.recursos()));p.setActividadesPermanentes(limpiar(r.actividadesPermanentes()));p.setAjustesRazonables(limpiar(r.ajustesRazonables()));p.setObservaciones(limpiar(r.observaciones()));if(!nueva){p.getMaterias().clear();p.getAlineaciones().clear();p.getActividades().clear();planeaciones.flush();}p.getMaterias().clear();int orden=1;for(Long id:ids){PlaneacionMateria pm=new PlaneacionMateria();pm.setPlaneacion(p);pm.setMateria(permitidas.get(id));pm.setOrden(orden++);p.getMaterias().add(pm);}copiarAlineaciones(p,r.alineaciones(),permitidas,ids);copiarActividades(p,r.actividades(),permitidas,ids,r.fechaInicio(),r.fechaFin());}
    private void copiarAlineaciones(PlaneacionSemanal p,List<PlaneacionRequest.Alineacion> filas,Map<Long,Materia> permitidas,Set<Long> seleccionadas){p.getAlineaciones().clear();int n=1;for(var f:filas==null?List.<PlaneacionRequest.Alineacion>of():filas){if(f.materiaId()!=null&&!seleccionadas.contains(f.materiaId()))throw new ReglaNegocioException("La alineación curricular usa una materia no seleccionada");PlaneacionAlineacion a=new PlaneacionAlineacion();a.setPlaneacion(p);a.setMateria(f.materiaId()==null?null:permitidas.get(f.materiaId()));a.setCampoFormativo(limpiar(f.campoFormativo()));a.setContenido(limpiar(f.contenido()));a.setProcesoDesarrollo(limpiar(f.procesoDesarrollo()));a.setOrden(n++);p.getAlineaciones().add(a);}}
    private void copiarActividades(PlaneacionSemanal p,List<PlaneacionRequest.Actividad> filas,Map<Long,Materia> permitidas,Set<Long> seleccionadas,LocalDate inicio,LocalDate fin){if(filas==null||filas.isEmpty())throw new ReglaNegocioException("Agrega al menos una actividad semanal");p.getActividades().clear();int n=1;for(var f:filas){if(!seleccionadas.contains(f.materiaId()))throw new ReglaNegocioException("Una actividad usa una materia no seleccionada");if(f.fecha().isBefore(inicio)||f.fecha().isAfter(fin))throw new ReglaNegocioException("Todas las actividades deben quedar dentro del rango semanal");PlaneacionActividad a=new PlaneacionActividad();a.setPlaneacion(p);a.setMateria(permitidas.get(f.materiaId()));a.setFecha(f.fecha());a.setTitulo(limpiar(f.titulo()));a.setInicio(limpiar(f.inicio()));a.setDesarrollo(limpiar(f.desarrollo()));a.setCierre(limpiar(f.cierre()));a.setDuracionMinutos(f.duracionMinutos());a.setTarea(limpiar(f.tarea()));a.setObservaciones(limpiar(f.observaciones()));a.setOrden(n++);p.getActividades().add(a);}}
    private void validarFechas(Grupo g,LocalDate inicio,LocalDate fin){if(inicio==null||fin==null)throw new ReglaNegocioException("Completa el rango de la semana");if(fin.isBefore(inicio)||fin.isAfter(inicio.plusDays(6)))throw new ReglaNegocioException("La planeación puede abarcar como máximo siete días");if(inicio.isBefore(g.getCicloEscolar().getFechaInicio())||fin.isAfter(g.getCicloEscolar().getFechaFin()))throw new ReglaNegocioException("La semana debe quedar dentro del ciclo escolar "+g.getCicloEscolar().getNombre());}
    private void validarCompleta(PlaneacionSemanal p){if(limpiar(p.getProposito())==null||p.getMaterias().isEmpty()||p.getActividades().isEmpty())throw new ReglaNegocioException("Completa el propósito, las materias y al menos una actividad antes de enviar");}
    private void exigirEditable(PlaneacionSemanal p){if(!Set.of(EstadoPlaneacion.BORRADOR,EstadoPlaneacion.REQUIERE_AJUSTES,EstadoPlaneacion.REABIERTA).contains(p.getEstado()))throw new ReglaNegocioException("La planeación está bloqueada mientras se encuentra "+p.getEstado().getEtiqueta().toLowerCase());}
    private void transicion(PlaneacionSemanal p,EstadoPlaneacion nuevo,String motivo,AccionAuditoria accion){EstadoPlaneacion anterior=p.getEstado();p.setEstado(nuevo);historial(p,anterior,nuevo,motivo);auditoria(p,accion,motivo);}
    private void historial(PlaneacionSemanal p,EstadoPlaneacion anterior,EstadoPlaneacion nuevo,String motivo){PlaneacionHistorial h=new PlaneacionHistorial();h.setPlaneacion(p);h.setEstadoAnterior(anterior);h.setEstadoNuevo(nuevo);h.setMotivo(motivo);h.setActor(actor());h.setOcurridoEn(Instant.now());historiales.save(h);}
    private void auditoria(PlaneacionSemanal p,AccionAuditoria accion,String motivo){auditoria.registrar(p.getInstitucion().getId(),accion,"PLANEACION_SEMANAL",p.getId(),motivo,Map.of("estado",p.getEstado().name(),"revision",p.getNumeroRevision(),"grupoId",p.getGrupo().getId()));}
    private Usuario actor(){UsuarioPrincipal p=principal();return p.usuarioId()==null?null:usuarios.getReferenceById(p.usuarioId());}
    private UsuarioPrincipal principal(){var a=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();if(a==null||!(a.getPrincipal() instanceof UsuarioPrincipal p))throw new org.springframework.security.access.AccessDeniedException("Sesión no válida");return p;}
    private Maestro maestro(UsuarioPrincipal p){if(p.usuarioId()==null)throw new org.springframework.security.access.AccessDeniedException("Cuenta docente requerida");Maestro m=maestros.findByUsuarioId(p.usuarioId()).orElseThrow(()->new org.springframework.security.access.AccessDeniedException("La cuenta no está vinculada con un maestro"));if(!m.isActivo())throw new org.springframework.security.access.AccessDeniedException("El maestro está inactivo");return m;}
    private Grupo grupo(Long id){return grupos.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el grupo",id));}
    private PlaneacionSemanal detalle(Long id){return planeaciones.findDetalleById(id).orElseThrow(()->new RecursoNoEncontradoException("la planeación",id));}
    private PlaneacionSemanal bloquearPropia(Long id,Maestro m){PlaneacionSemanal p=planeaciones.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("la planeación",id));if(!p.getMaestro().getId().equals(m.getId()))throw new org.springframework.security.access.AccessDeniedException("Planeación no autorizada");return p;}
    private PlaneacionSemanal bloquearAdmin(Long id){PlaneacionSemanal p=planeaciones.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("la planeación",id));alcance.validarRecurso(escuela.admin.dto.ModuloCatalogo.PLANEACIONES,id);return p;}
    private PlaneacionFila fila(PlaneacionSemanal p){return new PlaneacionFila(p.getId(),nombre(p.getMaestro()),p.getMaestro().getNumeroEmpleado(),p.getGrupo().getPlantel().getNombre(),p.getGrupo().getNombre(),p.getGrupo().getGrado().getNombre(),p.getFechaInicio(),p.getFechaFin(),p.getEstado(),p.getNumeroRevision(),p.getMaterias().size(),p.getVersion());}
    private PlaneacionDocumento documento(PlaneacionSemanal p,int revision,Instant publicada){return new PlaneacionDocumento(p.getId(),p.getGrupo().getId(),revision,p.getInstitucion().getNombre(),nombre(p.getMaestro()),p.getMaestro().getNumeroEmpleado(),p.getGrupo().getPlantel().getNombre(),p.getCicloEscolar().getNombre(),p.getGrupo().getGrado().getNombre(),p.getGrupo().getNombre(),p.getFechaInicio(),p.getFechaFin(),p.getEstado().getEtiqueta(),p.getSituacionDidactica(),p.getProposito(),p.getEjesArticuladores(),p.getConocimientos(),p.getHabilidades(),p.getActitudes(),p.getTecnicaEvaluacion(),p.getInstrumentoEvaluacion(),p.getRecursos(),p.getActividadesPermanentes(),p.getAjustesRazonables(),p.getObservaciones(),p.getMaterias().stream().map(x->new PlaneacionDocumento.Materia(x.getMateria().getId(),x.getMateria().getCodigo(),x.getMateria().getNombre())).toList(),p.getAlineaciones().stream().map(x->new PlaneacionDocumento.Alineacion(x.getMateria()==null?null:x.getMateria().getId(),x.getMateria()==null?"General":x.getMateria().getNombre(),x.getCampoFormativo(),x.getContenido(),x.getProcesoDesarrollo())).toList(),p.getActividades().stream().map(x->new PlaneacionDocumento.Actividad(x.getMateria().getId(),x.getMateria().getNombre(),x.getFecha(),x.getTitulo(),x.getInicio(),x.getDesarrollo(),x.getCierre(),x.getDuracionMinutos(),x.getTarea(),x.getObservaciones())).toList(),publicada);}
    private String serializar(PlaneacionDocumento d){try{return json.writeValueAsString(d);}catch(JsonProcessingException e){throw new IllegalStateException("No fue posible conservar la versión publicada",e);}}
    private PlaneacionDocumento deserializar(String s){try{return json.readValue(s,PlaneacionDocumento.class);}catch(JsonProcessingException e){throw new IllegalStateException("No fue posible leer la versión publicada",e);}}
    private String nombre(Maestro m){return java.util.stream.Stream.of(m.getNombres(),m.getPrimerApellido(),m.getSegundoApellido()).filter(x->x!=null&&!x.isBlank()).collect(Collectors.joining(" "));}
    private Specification<PlaneacionSemanal> criterios(FiltroPlaneacion f){return (r,q,c)->{List<jakarta.persistence.criteria.Predicate>p=new ArrayList<>();if(f.institucionId()!=null)p.add(c.equal(r.get("institucion").get("id"),f.institucionId()));if(f.plantelId()!=null)p.add(c.equal(r.get("grupo").get("plantel").get("id"),f.plantelId()));if(f.maestroId()!=null)p.add(c.equal(r.get("maestro").get("id"),f.maestroId()));if(f.grupoId()!=null)p.add(c.equal(r.get("grupo").get("id"),f.grupoId()));if(f.estado()!=null)p.add(c.equal(r.get("estado"),f.estado()));if(f.desde()!=null)p.add(c.greaterThanOrEqualTo(r.get("fechaFin"),f.desde()));if(f.hasta()!=null)p.add(c.lessThanOrEqualTo(r.get("fechaInicio"),f.hasta()));if(!f.texto().isBlank()){String x="%"+f.texto().toLowerCase(Locale.ROOT)+"%";p.add(c.like(c.lower(r.get("proposito")),x));}return c.and(p.toArray(jakarta.persistence.criteria.Predicate[]::new));};}
}
