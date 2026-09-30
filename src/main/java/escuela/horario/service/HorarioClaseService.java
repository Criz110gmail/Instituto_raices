package escuela.horario.service;

import escuela.admin.dto.*;
import escuela.common.exception.*;
import escuela.docente.entity.*;
import escuela.docente.repository.*;
import escuela.horario.dto.*;
import escuela.horario.entity.HorarioClase;
import escuela.horario.repository.HorarioClaseRepository;
import escuela.seguridad.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Service @RequiredArgsConstructor
public class HorarioClaseService {
    private final HorarioClaseRepository repository;
    private final AsignacionMaestroRepository asignaciones;
    private final MaestroRepository maestros;
    private final AlcanceDatosService alcance;

    @Transactional(readOnly=true)
    public Page<HorarioClaseFila> listar(FiltroHorario entrada){
        FiltroHorario f=entrada.normalizado();
        return repository.findAll(especificacion(f),PageRequest.of(f.pagina(),f.tamanio(),
                Sort.by("diaSemana").ascending().and(Sort.by("horaInicio")).and(Sort.by("id"))))
                .map(this::fila);
    }

    @Transactional(readOnly=true)
    public Page<HorarioClaseFila> bloque(FiltroHorario f){return listar(f);}

    @Transactional(readOnly=true)
    public HorarioClaseForm formulario(Long id){
        HorarioClase h=obtener(id);alcance.validarPlantel(h.getPlantel().getId());
        HorarioClaseForm f=new HorarioClaseForm();f.setAsignacionMaestroId(h.getAsignacionMaestro().getId());
        f.setDiaSemana((int)h.getDiaSemana());f.setHoraInicio(h.getHoraInicio());f.setHoraFin(h.getHoraFin());
        f.setFechaInicio(h.getFechaInicio());f.setFechaFin(h.getFechaFin());f.setAula(h.getAula());
        f.setObservaciones(h.getObservaciones());f.setActivo(h.isActivo());f.setVersion(h.getVersion());return f;
    }

    @Transactional(readOnly=true)
    public String etiquetaAsignacion(Long id){return id==null?"":etiqueta(asignaciones.findById(id)
            .orElseThrow(()->new RecursoNoEncontradoException("la asignación docente",id)));}

    @Transactional
    public Long crear(HorarioClaseForm f){HorarioClase h=new HorarioClase();copiar(h,f,0L);return repository.saveAndFlush(h).getId();}

    @Transactional
    public void actualizar(Long id,HorarioClaseForm f){HorarioClase h=obtener(id);alcance.validarPlantel(h.getPlantel().getId());
        if(f.getVersion()==null||!f.getVersion().equals(h.getVersion()))throw new ConflictoVersionException("El horario",id);
        copiar(h,f,id);repository.saveAndFlush(h);
    }

    @Transactional(readOnly=true)
    public ResultadoAutocompletado buscarAsignaciones(String consulta){
        UsuarioPrincipal p=principal();String q=consulta==null?"":consulta.trim().toLowerCase(Locale.ROOT);
        int limite=q.length()<3?10:20;String patron="%"+q+"%";
        Specification<AsignacionMaestro> s=(r,x,cb)->cb.and(cb.isTrue(r.get("activo")),
                cb.isTrue(r.get("maestro").get("activo")),cb.isTrue(r.get("grupo").get("activo")),
                cb.or(cb.like(cb.lower(r.get("maestro").get("numeroEmpleado")),patron),
                    cb.like(cb.lower(r.get("maestro").get("nombres")),patron),
                    cb.like(cb.lower(r.get("maestro").get("primerApellido")),patron),
                    cb.like(cb.lower(r.get("grupo").get("nombre")),patron),
                    cb.like(cb.lower(r.get("grupo").get("grado").get("nombre")),patron),
                    cb.like(cb.lower(r.get("materia").get("nombre")),patron),
                    cb.like(cb.lower(r.get("grupo").get("plantel").get("nombre")),patron)));
        if(!p.accesoRecuperacion())s=s.and((r,x,cb)->cb.equal(r.get("maestro").get("institucion").get("id"),p.institucionId()));
        if(!p.accesoRecuperacion()&&!p.alcanceInstitucional())s=s.and((r,x,cb)->p.plantelIds().isEmpty()?cb.disjunction():r.get("grupo").get("plantel").get("id").in(p.plantelIds()));
        Page<AsignacionMaestro> pagina=asignaciones.findAll(s,PageRequest.of(0,limite+1,Sort.by("id").descending()));
        return new ResultadoAutocompletado(pagina.getContent().stream().limit(limite)
                .map(a->new OpcionAutocompletado(a.getId(),etiqueta(a),a.getGrupo().getPlantel().getNombre()+" · "+a.getGrupo().getCicloEscolar().getNombre())).toList(),pagina.getNumberOfElements()>limite);
    }

    @Transactional(readOnly=true)
    public List<HorarioClaseFila> horarioMaestro(UsuarioPrincipal p,LocalDate fecha){
        Maestro m=maestros.findByUsuarioId(p.usuarioId()).orElseThrow(()->new AccessDeniedException("Cuenta docente no vinculada"));
        return repository.horarioMaestro(m.getId(),fecha==null?LocalDate.now():fecha).stream().map(this::fila).toList();
    }

    @Transactional(readOnly=true)
    public List<HorarioClaseFila> horarioAlumno(Long alumnoId,LocalDate fecha){return repository.horarioAlumno(alumnoId,fecha==null?LocalDate.now():fecha).stream().map(this::fila).toList();}

    private Specification<HorarioClase> especificacion(FiltroHorario f){
        Specification<HorarioClase> s=(r,q,cb)->cb.conjunction();
        UsuarioPrincipal p=principal();
        if(!p.accesoRecuperacion())s=s.and((r,q,cb)->cb.equal(r.get("institucion").get("id"),p.institucionId()));
        if(!p.accesoRecuperacion()&&!p.alcanceInstitucional())s=s.and((r,q,cb)->p.plantelIds().isEmpty()?cb.disjunction():r.get("plantel").get("id").in(p.plantelIds()));
        if(f.maestroId()!=null)s=s.and((r,q,cb)->cb.equal(r.get("maestro").get("id"),f.maestroId()));
        if(f.grupoId()!=null)s=s.and((r,q,cb)->cb.equal(r.get("grupo").get("id"),f.grupoId()));
        if(f.diaSemana()!=null)s=s.and((r,q,cb)->cb.equal(r.get("diaSemana"),f.diaSemana()));
        if(f.fecha()!=null)s=s.and((r,q,cb)->cb.and(cb.lessThanOrEqualTo(r.get("fechaInicio"),f.fecha()),cb.greaterThanOrEqualTo(r.get("fechaFin"),f.fecha())));
        if(f.activo()!=null)s=s.and((r,q,cb)->cb.equal(r.get("activo"),f.activo()));return s;
    }

    private void copiar(HorarioClase h,HorarioClaseForm f,Long id){
        AsignacionMaestro a=asignaciones.findById(f.getAsignacionMaestroId()).orElseThrow(()->new RecursoNoEncontradoException("la asignación docente",f.getAsignacionMaestroId()));
        alcance.validarPlantel(a.getGrupo().getPlantel().getId());
        if(!a.isActivo()||!a.getMaestro().isActivo()||!a.getGrupo().isActivo()||!a.getMateria().isActivo())throw new ReglaNegocioException("La asignación, el maestro, el grupo y la materia deben estar activos");
        if(f.getHoraInicio()==null||f.getHoraFin()==null||!f.getHoraFin().isAfter(f.getHoraInicio()))throw new ReglaNegocioException("La hora final debe ser posterior a la hora inicial");
        if(f.getFechaInicio()==null||f.getFechaFin()==null||f.getFechaFin().isBefore(f.getFechaInicio()))throw new ReglaNegocioException("La fecha final debe ser igual o posterior a la inicial");
        LocalDate asignacionFin=a.getFechaFin()==null?a.getGrupo().getCicloEscolar().getFechaFin():a.getFechaFin();
        if(f.getFechaInicio().isBefore(a.getFechaInicio())||f.getFechaFin().isAfter(asignacionFin))throw new ReglaNegocioException("La vigencia del horario debe quedar dentro de la asignación docente ("+a.getFechaInicio()+" a "+asignacionFin+")");
        String aula=limpiar(f.getAula());
        if(f.isActivo()&&repository.existeCruce(id,f.getDiaSemana(),f.getFechaInicio(),f.getFechaFin(),f.getHoraInicio(),f.getHoraFin(),a.getMaestro().getId(),a.getGrupo().getId(),a.getGrupo().getPlantel().getId(),aula))throw new ReglaNegocioException("Ese bloque se cruza con otro horario activo del maestro, grupo o aula");
        h.setAsignacionMaestro(a);h.setMaestro(a.getMaestro());h.setGrupo(a.getGrupo());h.setMateria(a.getMateria());
        h.setPlantel(a.getGrupo().getPlantel());h.setInstitucion(a.getMaestro().getInstitucion());h.setCicloEscolar(a.getGrupo().getCicloEscolar());
        h.setDiaSemana(f.getDiaSemana().shortValue());h.setHoraInicio(f.getHoraInicio());h.setHoraFin(f.getHoraFin());h.setFechaInicio(f.getFechaInicio());h.setFechaFin(f.getFechaFin());h.setAula(aula);h.setObservaciones(limpiar(f.getObservaciones()));h.setActivo(f.isActivo());
    }

    private HorarioClase obtener(Long id){return repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el horario de clase",id));}
    private UsuarioPrincipal principal(){Object p=SecurityContextHolder.getContext().getAuthentication().getPrincipal();if(p instanceof UsuarioPrincipal u)return u;throw new AccessDeniedException("Sesión inválida");}
    private HorarioClaseFila fila(HorarioClase h){return new HorarioClaseFila(h.getId(),h.getInstitucion().getNombre(),h.getPlantel().getNombre(),h.getCicloEscolar().getNombre(),nombre(h.getMaestro()),h.getMaestro().getNumeroEmpleado(),h.getGrupo().getGrado().getNombre(),h.getGrupo().getNombre(),h.getMateria().getNombre(),(int)h.getDiaSemana(),dia(h.getDiaSemana()),h.getHoraInicio(),h.getHoraFin(),h.getFechaInicio(),h.getFechaFin(),h.getAula(),h.getObservaciones(),h.isActivo(),h.getVersion());}
    private String etiqueta(AsignacionMaestro a){return nombre(a.getMaestro())+" · "+a.getGrupo().getGrado().getNombre()+" "+a.getGrupo().getNombre()+" · "+a.getMateria().getNombre();}
    private String nombre(Maestro m){return (m.getNombres()+" "+m.getPrimerApellido()+" "+Objects.toString(m.getSegundoApellido(),"")).trim();}
    public static String dia(int d){return switch(d){case 1->"Lunes";case 2->"Martes";case 3->"Miércoles";case 4->"Jueves";case 5->"Viernes";case 6->"Sábado";case 7->"Domingo";default->"";};}
}
