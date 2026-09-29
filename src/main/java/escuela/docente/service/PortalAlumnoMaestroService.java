package escuela.docente.service;

import escuela.alumno.service.*;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.docente.dto.*;
import escuela.docente.entity.Maestro;
import escuela.docente.repository.MaestroRepository;
import escuela.inscripcion.entity.AsignacionGrupo;
import escuela.inscripcion.repository.AsignacionGrupoRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Locale;
import java.util.stream.Stream;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class PortalAlumnoMaestroService {
 private final MaestroRepository maestros;private final AsignacionGrupoRepository asignaciones;private final FichaMedicaAlumnoService fichas;private final FotografiaAlumnoService fotos;
 public Page<AlumnoMaestroFila> listar(UsuarioPrincipal p,String q,int pagina){Maestro m=maestro(p);String t=q==null?"":q.trim().toLowerCase(Locale.ROOT);return asignaciones.alumnosDelMaestro(m.getId(),LocalDate.now(),t,PageRequest.of(Math.max(0,pagina),12,Sort.by("id").descending())).map(this::fila);}
 public AlumnoMaestroDetalle detalle(UsuarioPrincipal p,Long alumnoId){AsignacionGrupo ag=asignacion(p,alumnoId);var a=ag.getInscripcion().getAlumno();return new AlumnoMaestroDetalle(a.getId(),a.getMatricula(),nombre(a.getNombres(),a.getPrimerApellido(),a.getSegundoApellido()),a.getFechaNacimiento(),a.getSexo(),a.getTelefono(),a.getEmail(),ag.getGrupo().getPlantel().getNombre(),ag.getGrupo().getGrado().getNombre(),ag.getGrupo().getNombre(),a.getFotografiaArchivo()!=null,fichas.obtener(a.getId()));}
 public ArchivoDescarga fotografia(UsuarioPrincipal p,Long alumnoId){asignacion(p,alumnoId);var actual=fotos.actual(alumnoId);if(actual==null)throw new RecursoNoEncontradoException("la fotografía del alumno",alumnoId);return fotos.descargar(alumnoId,actual.id());}
 private AsignacionGrupo asignacion(UsuarioPrincipal p,Long alumnoId){Maestro m=maestro(p);return asignaciones.alumnoDelMaestro(m.getId(),alumnoId,LocalDate.now()).stream().findFirst().orElseThrow(()->new AccessDeniedException("El alumno no pertenece a un grupo asignado al maestro"));}
 private Maestro maestro(UsuarioPrincipal p){if(p==null||p.usuarioId()==null)throw new AccessDeniedException("Cuenta docente requerida");return maestros.findByUsuarioId(p.usuarioId()).filter(Maestro::isActivo).orElseThrow(()->new AccessDeniedException("Cuenta docente no vinculada"));}
 private AlumnoMaestroFila fila(AsignacionGrupo ag){var a=ag.getInscripcion().getAlumno();return new AlumnoMaestroFila(a.getId(),a.getMatricula(),nombre(a.getNombres(),a.getPrimerApellido(),a.getSegundoApellido()),ag.getGrupo().getPlantel().getNombre(),ag.getGrupo().getGrado().getNombre(),ag.getGrupo().getNombre(),a.getFotografiaArchivo()!=null);}
 private String nombre(String...v){return Stream.of(v).filter(x->x!=null&&!x.isBlank()).reduce((a,b)->a+" "+b).orElse("");}
}
