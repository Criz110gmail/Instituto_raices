package escuela.asistencia.service.impl;

import escuela.academico.entity.Grupo;
import escuela.academico.repository.GrupoRepository;
import escuela.asistencia.dto.*;
import escuela.asistencia.entity.*;
import escuela.asistencia.repository.AsistenciaRepository;
import escuela.asistencia.service.AsistenciaService;
import escuela.common.exception.*;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor @Transactional
public class AsistenciaServiceImpl implements AsistenciaService {
    private final AsistenciaRepository repository;
    private final GrupoRepository grupos;
    private final InscripcionRepository inscripciones;

    @Override @Transactional(readOnly = true)
    public HojaAsistenciaResponse hoja(Long grupoId, LocalDate fecha) {
        Contexto contexto = contexto(grupoId, fecha);
        return respuesta(contexto, registros(contexto));
    }

    @Override
    public HojaAsistenciaResponse guardar(CapturaAsistenciaRequest request) {
        Contexto contexto = contexto(request.grupoId(), request.fecha());
        if (request.filas() == null) throw new ReglaNegocioException("No se recibieron alumnos para capturar");
        Map<Long, CapturaAsistenciaRequest.Fila> filas = request.filas().stream().collect(
                Collectors.toMap(CapturaAsistenciaRequest.Fila::inscripcionId, Function.identity(), (a,b) -> {
                    throw new ReglaNegocioException("La captura contiene un alumno repetido");
                }, LinkedHashMap::new));
        Set<Long> esperadas = contexto.inscripciones().stream().map(Inscripcion::getId).collect(Collectors.toSet());
        if (!filas.keySet().equals(esperadas)) {
            throw new ReglaNegocioException("La lista de alumnos cambió; recarga la asistencia antes de guardar");
        }
        Map<Long, Asistencia> existentes = registros(contexto);
        List<Asistencia> guardar = new ArrayList<>();
        for (Inscripcion inscripcion : contexto.inscripciones()) {
            var fila = filas.get(inscripcion.getId());
            if (fila.estado() == null) throw new ReglaNegocioException("Selecciona el estado de todos los alumnos");
            String observaciones = texto(fila.observaciones());
            if (observaciones != null && observaciones.length() > 1000) {
                throw new ReglaNegocioException("Las observaciones no pueden superar 1000 caracteres");
            }
            Asistencia asistencia = existentes.get(inscripcion.getId());
            if (asistencia == null) {
                asistencia = new Asistencia();
                asistencia.setInscripcion(inscripcion);
                asistencia.setGrupo(contexto.grupo());
                asistencia.setFecha(contexto.fecha());
            } else {
                verificar(asistencia, fila.version(), "Asistencia");
            }
            asistencia.setEstado(fila.estado());
            asistencia.setObservaciones(observaciones);
            guardar.add(asistencia);
        }
        repository.saveAllAndFlush(guardar);
        return respuesta(contexto, registros(contexto));
    }

    private Contexto contexto(Long grupoId, LocalDate fecha) {
        if (fecha == null) throw new ReglaNegocioException("Selecciona la fecha de asistencia");
        Grupo grupo = grupos.findById(grupoId).orElseThrow(() -> new RecursoNoEncontradoException("el grupo", grupoId));
        if (!grupo.isActivo()) throw new ReglaNegocioException("El grupo está inactivo");
        if (fecha.isBefore(grupo.getCicloEscolar().getFechaInicio()) || fecha.isAfter(grupo.getCicloEscolar().getFechaFin())) {
            throw new ReglaNegocioException("La fecha debe estar dentro del ciclo escolar del grupo");
        }
        List<Inscripcion> lista = inscripciones.buscarParaAsistencia(grupoId, fecha);
        if (lista.isEmpty()) throw new ReglaNegocioException("El grupo no tiene alumnos asignados en esa fecha");
        return new Contexto(grupo, fecha, lista);
    }

    private Map<Long, Asistencia> registros(Contexto contexto) {
        List<Long> ids = contexto.inscripciones().stream().map(Inscripcion::getId).toList();
        return repository.findAllByInscripcionIdInAndGrupoIdAndFecha(ids, contexto.grupo().getId(), contexto.fecha())
                .stream().collect(Collectors.toMap(a -> a.getInscripcion().getId(), Function.identity()));
    }

    private HojaAsistenciaResponse respuesta(Contexto contexto, Map<Long, Asistencia> registros) {
        List<FilaAsistenciaResponse> filas = contexto.inscripciones().stream().map(i -> {
            Asistencia a = registros.get(i.getId());
            return new FilaAsistenciaResponse(i.getId(), a == null ? null : a.getId(), i.getAlumno().getMatricula(),
                    nombre(i), a == null ? EstadoAsistencia.PRESENTE : a.getEstado(),
                    a == null ? null : a.getObservaciones(), a == null ? null : a.getVersion());
        }).toList();
        Grupo g = contexto.grupo();
        return new HojaAsistenciaResponse(g.getId(), g.getNombre(), g.getGrado().getNombre(),
                g.getPlantel().getNombre(), g.getCicloEscolar().getNombre(), contexto.fecha(), filas);
    }

    private String nombre(Inscripcion i) {
        return String.join(" ", i.getAlumno().getNombres(), i.getAlumno().getPrimerApellido(),
                Optional.ofNullable(i.getAlumno().getSegundoApellido()).orElse("")).trim();
    }
    private String texto(String valor) { return valor == null || valor.isBlank() ? null : valor.trim(); }
    private record Contexto(Grupo grupo, LocalDate fecha, List<Inscripcion> inscripciones) {}
}
