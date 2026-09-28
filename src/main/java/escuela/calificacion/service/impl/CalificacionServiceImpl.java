package escuela.calificacion.service.impl;

import escuela.academico.entity.Grupo;
import escuela.academico.entity.MateriaGrado;
import escuela.academico.entity.PeriodoAcademico;
import escuela.academico.entity.TipoEvaluacion;
import escuela.academico.repository.GrupoRepository;
import escuela.academico.repository.MateriaGradoRepository;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.calificacion.dto.*;
import escuela.calificacion.entity.Calificacion;
import escuela.calificacion.entity.EstadoCalificacion;
import escuela.calificacion.repository.CalificacionRepository;
import escuela.calificacion.service.CalificacionService;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class CalificacionServiceImpl implements CalificacionService {
    private final CalificacionRepository repository;
    private final InscripcionRepository inscripcionRepository;
    private final GrupoRepository grupoRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final MateriaGradoRepository materiaGradoRepository;

    @Override
    @Transactional(readOnly = true)
    public HojaCalificacionesResponse hoja(Long grupoId, Long periodoId, Long materiaGradoId) {
        Contexto contexto = contexto(grupoId, periodoId, materiaGradoId);
        return respuesta(contexto, calificaciones(contexto.inscripciones(), materiaGradoId, periodoId));
    }

    @Override
    public HojaCalificacionesResponse guardar(CapturaCalificacionRequest request, boolean publicar) {
        Contexto contexto = contexto(request.grupoId(), request.periodoId(), request.materiaGradoId());
        if (!contexto.plan().isActivo()) {
            throw new ReglaNegocioException("El plan de la materia está inactivo y no admite nuevas capturas");
        }
        Map<Long, CapturaCalificacionRequest.Fila> recibidas = validarFilas(request, contexto);
        Map<Long, Calificacion> existentes = calificaciones(contexto.inscripciones(),
                request.materiaGradoId(), request.periodoId());
        if (existentes.values().stream().anyMatch(c -> c.getEstado() == EstadoCalificacion.PUBLICADA)) {
            throw new ReglaNegocioException("Las calificaciones ya están publicadas; reabre el bloque antes de corregirlo");
        }

        List<Calificacion> guardadas = new ArrayList<>();
        for (Inscripcion inscripcion : contexto.inscripciones()) {
            CapturaCalificacionRequest.Fila fila = recibidas.get(inscripcion.getId());
            Calificacion calificacion = existentes.get(inscripcion.getId());
            if (calificacion == null && vacia(fila) && !publicar) continue;
            if (calificacion == null) {
                calificacion = nueva(inscripcion, contexto.plan(), contexto.periodo());
            } else {
                verificar(calificacion, fila.version(), "Calificación");
            }
            copiar(calificacion, fila, contexto.plan());
            if (publicar) publicar(calificacion);
            guardadas.add(calificacion);
        }
        if (publicar && guardadas.size() != contexto.inscripciones().size()) {
            throw new ReglaNegocioException("Completa la calificación de todos los alumnos antes de publicar");
        }
        repository.saveAllAndFlush(guardadas);
        return respuesta(contexto, calificaciones(contexto.inscripciones(),
                request.materiaGradoId(), request.periodoId()));
    }

    @Override
    public HojaCalificacionesResponse reabrir(Long grupoId, Long periodoId, Long materiaGradoId) {
        Contexto contexto = contexto(grupoId, periodoId, materiaGradoId);
        Map<Long, Calificacion> registros = calificaciones(contexto.inscripciones(), materiaGradoId, periodoId);
        if (registros.isEmpty() || registros.values().stream().noneMatch(
                c -> c.getEstado() == EstadoCalificacion.PUBLICADA)) {
            throw new ReglaNegocioException("No hay calificaciones publicadas para reabrir");
        }
        registros.values().forEach(calificacion -> {
            calificacion.setEstado(EstadoCalificacion.BORRADOR);
            calificacion.setPublicadoEn(null);
            calificacion.setPublicadoPorId(null);
        });
        repository.saveAllAndFlush(registros.values());
        return respuesta(contexto, registros);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PortalCalificacionResponse> publicadasAlumno(Long alumnoId, int pagina, int tamanio) {
        int paginaSegura = Math.max(0, pagina);
        int tamanoSeguro = Math.min(100, Math.max(10, tamanio));
        return repository.buscarPortal(alumnoId, EstadoCalificacion.PUBLICADA,
                        PageRequest.of(paginaSegura, tamanoSeguro))
                .map(this::portal);
    }

    private Contexto contexto(Long grupoId, Long periodoId, Long materiaGradoId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el grupo", grupoId));
        PeriodoAcademico periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el periodo académico", periodoId));
        MateriaGrado plan = materiaGradoRepository.findById(materiaGradoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el plan de materia", materiaGradoId));
        if (!periodo.getCicloEscolar().getId().equals(grupo.getCicloEscolar().getId())
                || !periodo.getNivelEducativo().getId().equals(
                grupo.getGrado().getNivelEducativo().getId())) {
            throw new ReglaNegocioException("El periodo no corresponde al ciclo y nivel del grupo");
        }
        if (!plan.getGrado().getId().equals(grupo.getGrado().getId())) {
            throw new ReglaNegocioException("La materia no está configurada para el grado del grupo");
        }
        if (!plan.getMateria().getInstitucion().getId().equals(
                grupo.getPlantel().getInstitucion().getId())) {
            throw new ReglaNegocioException("La materia y el grupo deben pertenecer a la misma institución");
        }
        List<Inscripcion> inscripciones = inscripcionRepository.buscarParaCalificaciones(
                grupoId, periodo.getFechaInicio(), periodo.getFechaFin());
        if (inscripciones.isEmpty()) {
            throw new ReglaNegocioException("El grupo no tiene alumnos inscritos durante este periodo");
        }
        return new Contexto(grupo, periodo, plan, inscripciones);
    }

    private Map<Long, CapturaCalificacionRequest.Fila> validarFilas(
            CapturaCalificacionRequest request, Contexto contexto) {
        if (request.filas() == null) throw new ReglaNegocioException("No se recibieron alumnos para capturar");
        Map<Long, CapturaCalificacionRequest.Fila> filas = request.filas().stream()
                .collect(Collectors.toMap(CapturaCalificacionRequest.Fila::inscripcionId,
                        Function.identity(), (a, b) -> {
                            throw new ReglaNegocioException("La captura contiene un alumno repetido");
                        }, LinkedHashMap::new));
        Set<Long> permitidas = contexto.inscripciones().stream().map(Inscripcion::getId)
                .collect(Collectors.toSet());
        if (!filas.keySet().equals(permitidas)) {
            throw new ReglaNegocioException("La lista de alumnos cambió; recarga la captura antes de guardar");
        }
        return filas;
    }

    private Map<Long, Calificacion> calificaciones(List<Inscripcion> inscripciones,
                                                    Long materiaGradoId, Long periodoId) {
        List<Long> ids = inscripciones.stream().map(Inscripcion::getId).toList();
        return repository.findAllByInscripcionIdInAndMateriaGradoIdAndPeriodoAcademicoId(
                        ids, materiaGradoId, periodoId).stream()
                .collect(Collectors.toMap(c -> c.getInscripcion().getId(), Function.identity()));
    }

    private Calificacion nueva(Inscripcion inscripcion, MateriaGrado plan, PeriodoAcademico periodo) {
        Calificacion calificacion = new Calificacion();
        calificacion.setInscripcion(inscripcion);
        calificacion.setMateriaGrado(plan);
        calificacion.setPeriodoAcademico(periodo);
        calificacion.setEstado(EstadoCalificacion.BORRADOR);
        copiarEscala(calificacion, plan);
        return calificacion;
    }

    private void copiar(Calificacion calificacion, CapturaCalificacionRequest.Fila fila,
                        MateriaGrado plan) {
        if (calificacion.getId() == null) copiarEscala(calificacion, plan);
        String cualitativa = texto(fila.valorCualitativo());
        String observaciones = texto(fila.observaciones());
        if (cualitativa != null && cualitativa.length() > 100) {
            throw new ReglaNegocioException("La valoración cualitativa no puede superar 100 caracteres");
        }
        if (observaciones != null && observaciones.length() > 1000) {
            throw new ReglaNegocioException("Las observaciones no pueden superar 1000 caracteres");
        }
        if (calificacion.getTipoEvaluacion() == TipoEvaluacion.NUMERICA) {
            validarNumero(fila.valorNumerico(), calificacion);
            calificacion.setValorNumerico(fila.valorNumerico());
            calificacion.setValorCualitativo(null);
        } else {
            calificacion.setValorNumerico(null);
            calificacion.setValorCualitativo(cualitativa);
        }
        calificacion.setObservaciones(observaciones);
    }

    private void validarNumero(BigDecimal valor, Calificacion calificacion) {
        if (valor == null) return;
        if (valor.compareTo(calificacion.getEscalaMinima()) < 0
                || valor.compareTo(calificacion.getEscalaMaxima()) > 0) {
            throw new ReglaNegocioException("Una calificación está fuera de la escala permitida");
        }
        int escalaReal = Math.max(0, valor.stripTrailingZeros().scale());
        if (escalaReal > calificacion.getDecimales()) {
            throw new ReglaNegocioException("Una calificación usa más decimales de los configurados");
        }
    }

    private void publicar(Calificacion calificacion) {
        boolean completa = calificacion.getTipoEvaluacion() == TipoEvaluacion.NUMERICA
                ? calificacion.getValorNumerico() != null
                : texto(calificacion.getValorCualitativo()) != null;
        if (!completa) throw new ReglaNegocioException(
                "Completa la calificación de todos los alumnos antes de publicar");
        calificacion.setEstado(EstadoCalificacion.PUBLICADA);
        calificacion.setPublicadoEn(Instant.now());
        calificacion.setPublicadoPorId(actorActual());
    }

    private void copiarEscala(Calificacion calificacion, MateriaGrado plan) {
        calificacion.setTipoEvaluacion(plan.getTipoEvaluacion());
        calificacion.setEscalaMinima(plan.getEscalaMinima());
        calificacion.setEscalaMaxima(plan.getEscalaMaxima());
        calificacion.setMinimaAprobatoria(plan.getMinimaAprobatoria());
        calificacion.setDecimales(plan.getDecimales());
    }

    private HojaCalificacionesResponse respuesta(Contexto contexto, Map<Long, Calificacion> registros) {
        List<FilaCalificacionResponse> filas = contexto.inscripciones().stream().map(inscripcion -> {
            Calificacion c = registros.get(inscripcion.getId());
            return new FilaCalificacionResponse(inscripcion.getId(), c == null ? null : c.getId(),
                    inscripcion.getAlumno().getMatricula(), nombre(inscripcion),
                    c == null ? null : c.getValorNumerico(), c == null ? null : c.getValorCualitativo(),
                    c == null ? null : c.getObservaciones(),
                    c == null ? EstadoCalificacion.BORRADOR : c.getEstado(),
                    c == null ? null : c.getVersion());
        }).toList();
        boolean publicada = !filas.isEmpty() && filas.stream()
                .allMatch(f -> f.calificacionId() != null && f.estado() == EstadoCalificacion.PUBLICADA);
        Grupo g = contexto.grupo();
        MateriaGrado p = contexto.plan();
        return new HojaCalificacionesResponse(g.getId(), g.getNombre(), g.getPlantel().getNombre(),
                g.getCicloEscolar().getNombre(), g.getGrado().getNombre(),
                contexto.periodo().getId(), contexto.periodo().getNombre(), p.getId(),
                p.getMateria().getNombre(), p.getTipoEvaluacion(), p.getEscalaMinima(),
                p.getEscalaMaxima(), p.getMinimaAprobatoria(), p.getDecimales(), publicada, filas);
    }

    private PortalCalificacionResponse portal(Calificacion c) {
        return new PortalCalificacionResponse(c.getPeriodoAcademico().getCicloEscolar().getNombre(),
                c.getPeriodoAcademico().getNombre(), c.getMateriaGrado().getMateria().getNombre(),
                c.getTipoEvaluacion(), c.getValorNumerico(), c.getValorCualitativo(),
                c.getMinimaAprobatoria(), c.getDecimales(), c.getObservaciones(), c.getPublicadoEn());
    }

    private boolean vacia(CapturaCalificacionRequest.Fila fila) {
        return fila.valorNumerico() == null && texto(fila.valorCualitativo()) == null
                && texto(fila.observaciones()) == null;
    }

    private String nombre(Inscripcion i) {
        return String.join(" ", i.getAlumno().getNombres(), i.getAlumno().getPrimerApellido(),
                Optional.ofNullable(i.getAlumno().getSegundoApellido()).orElse("")).trim();
    }

    private String texto(String valor) {
        if (valor == null || valor.isBlank()) return null;
        return valor.trim();
    }

    private Long actorActual() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        return autenticacion != null && autenticacion.getPrincipal() instanceof UsuarioPrincipal principal
                && !principal.accesoRecuperacion() ? principal.usuarioId() : null;
    }

    private record Contexto(Grupo grupo, PeriodoAcademico periodo, MateriaGrado plan,
                            List<Inscripcion> inscripciones) {}
}
