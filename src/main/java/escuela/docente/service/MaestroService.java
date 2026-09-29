package escuela.docente.service;

import escuela.academico.entity.*;
import escuela.academico.repository.*;
import escuela.common.exception.*;
import escuela.docente.dto.*;
import escuela.docente.entity.*;
import escuela.docente.repository.*;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;
import static escuela.common.mapper.NormalizacionTexto.*;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor @Transactional
public class MaestroService {
    private final MaestroRepository maestros;
    private final AsignacionMaestroRepository asignaciones;
    private final InstitucionRepository instituciones;
    private final GrupoRepository grupos;
    private final MateriaRepository materias;
    private final MateriaGradoRepository planesMateria;

    public MaestroResponse crear(MaestroRequest r){
        Institucion i=instituciones.findById(r.institucionId()).orElseThrow(()->new RecursoNoEncontradoException("la institución",r.institucionId()));
        if(!i.isActivo()) throw new ReglaNegocioException("La institución debe estar activa");
        validarNumero(r.institucionId(),r.numeroEmpleado(),0L);
        Maestro m=new Maestro();m.setInstitucion(i);copiar(m,r);return respuesta(maestros.saveAndFlush(m));
    }
    public MaestroResponse actualizar(Long id,MaestroRequest r){
        Maestro m=buscar(id);verificar(m,r.version(),"Maestro");
        if(!m.getInstitucion().getId().equals(r.institucionId())) throw new ReglaNegocioException("No se puede cambiar la institución del maestro");
        validarNumero(r.institucionId(),r.numeroEmpleado(),id);copiar(m,r);return respuesta(maestros.saveAndFlush(m));
    }
    public void desactivar(Long id,Long version){Maestro m=buscar(id);verificar(m,version,"Maestro");m.setActivo(false);if(m.getUsuario()!=null)m.getUsuario().setEstado(escuela.seguridad.entity.EstadoUsuario.INACTIVO);asignaciones.findAllByMaestroIdOrderByActivoDescFechaInicioDescIdDesc(id).forEach(a->a.setActivo(false));}
    @Transactional(readOnly=true) public MaestroResponse obtener(Long id){return respuesta(buscar(id));}
    @Transactional(readOnly=true) public List<AsignacionMaestroResponse> asignaciones(Long id){buscar(id);return asignaciones.findAllByMaestroIdOrderByActivoDescFechaInicioDescIdDesc(id).stream().map(this::respuesta).toList();}
    public AsignacionMaestroResponse asignar(Long maestroId,AsignacionMaestroRequest r){
        Maestro m=buscar(maestroId);if(!m.isActivo())throw new ReglaNegocioException("El maestro debe estar activo");
        Grupo g=grupos.findById(r.grupoId()).orElseThrow(()->new RecursoNoEncontradoException("el grupo",r.grupoId()));
        Materia materia=materias.findById(r.materiaId()).orElseThrow(()->new RecursoNoEncontradoException("la materia",r.materiaId()));
        validarRelacion(m,g,materia,r.fechaInicio(),r.fechaFin());
        if(asignaciones.existeTraslape(maestroId,g.getId(),materia.getId(),r.fechaInicio(),r.fechaFin(),0L)) throw new RecursoDuplicadoException("El maestro ya tiene esa materia asignada al grupo durante esas fechas");
        AsignacionMaestro a=new AsignacionMaestro();a.setMaestro(m);a.setGrupo(g);a.setMateria(materia);a.setFechaInicio(r.fechaInicio());a.setFechaFin(r.fechaFin());return respuesta(asignaciones.saveAndFlush(a));
    }
    public void desactivarAsignacion(Long maestroId,Long id,Long version){
        AsignacionMaestro a=asignaciones.findByIdAndMaestroId(id,maestroId).orElseThrow(()->new RecursoNoEncontradoException("la asignación",id));
        verificar(a,version,"Asignación docente");a.setActivo(false);
    }
    private void validarRelacion(Maestro m,Grupo g,Materia materia,LocalDate inicio,LocalDate fin){
        if(inicio==null)throw new ReglaNegocioException("La fecha inicial es obligatoria");
        if(fin!=null&&fin.isBefore(inicio))throw new ReglaNegocioException("La fecha final no puede ser anterior a la inicial");
        Long institucion=m.getInstitucion().getId();
        if(!g.getPlantel().getInstitucion().getId().equals(institucion)||!materia.getInstitucion().getId().equals(institucion))throw new ReglaNegocioException("El maestro, grupo y materia deben pertenecer a la misma institución");
        if(!g.isActivo()||!materia.isActivo())throw new ReglaNegocioException("El grupo y la materia deben estar activos");
        if(!planesMateria.existsByMateriaIdAndGradoIdAndActivoTrue(materia.getId(),g.getGrado().getId()))throw new ReglaNegocioException("La materia no está configurada como activa para el grado de este grupo");
        if(inicio.isBefore(g.getCicloEscolar().getFechaInicio())||(fin!=null&&fin.isAfter(g.getCicloEscolar().getFechaFin())))throw new ReglaNegocioException("La vigencia de la asignación debe quedar dentro del ciclo escolar del grupo");
    }
    private void copiar(Maestro m,MaestroRequest r){m.setNumeroEmpleado(codigo(r.numeroEmpleado()));m.setNombres(limpiar(r.nombres()));m.setPrimerApellido(limpiar(r.primerApellido()));m.setSegundoApellido(limpiar(r.segundoApellido()));m.setEmail(email(r.email()));m.setTelefono(limpiar(r.telefono()));m.setActivo(true);}
    private void validarNumero(Long i,String n,Long id){if(maestros.existsByInstitucionIdAndNumeroEmpleadoIgnoreCaseAndIdNot(i,codigo(n),id))throw new RecursoDuplicadoException("Ya existe un maestro con ese número de empleado");}
    private Maestro buscar(Long id){return maestros.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el maestro",id));}
    private MaestroResponse respuesta(Maestro m){var u=m.getUsuario();return new MaestroResponse(m.getId(),m.getInstitucion().getId(),m.getInstitucion().getNombre(),m.getNumeroEmpleado(),m.getNombres(),m.getPrimerApellido(),m.getSegundoApellido(),nombre(m.getNombres(),m.getPrimerApellido(),m.getSegundoApellido()),m.getEmail(),m.getTelefono(),m.isActivo(),u==null?null:u.getId(),u==null?null:u.getUsername(),u==null?null:u.getEstado(),m.getVersion());}
    private AsignacionMaestroResponse respuesta(AsignacionMaestro a){return new AsignacionMaestroResponse(a.getId(),a.getGrupo().getId(),a.getGrupo().getNombre(),a.getGrupo().getPlantel().getNombre(),a.getGrupo().getCicloEscolar().getNombre(),a.getMateria().getId(),a.getMateria().getNombre(),a.getFechaInicio(),a.getFechaFin(),a.isActivo(),a.getVersion());}
    private String nombre(String...p){return Stream.of(p).filter(x->x!=null&&!x.isBlank()).reduce((a,b)->a+" "+b).orElse("");}
}
