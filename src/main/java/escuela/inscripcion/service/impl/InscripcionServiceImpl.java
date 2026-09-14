package escuela.inscripcion.service.impl;

import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.Grado;
import escuela.academico.entity.Grupo;
import escuela.academico.repository.CicloEscolarRepository;
import escuela.academico.repository.GradoRepository;
import escuela.academico.repository.GrupoRepository;
import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoRepository;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.dto.request.AsignacionGrupoRequest;
import escuela.inscripcion.dto.request.InscripcionRequest;
import escuela.inscripcion.dto.response.AsignacionGrupoResponse;
import escuela.inscripcion.dto.response.InscripcionResponse;
import escuela.inscripcion.entity.AsignacionGrupo;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.mapper.AsignacionGrupoMapper;
import escuela.inscripcion.mapper.InscripcionMapper;
import escuela.inscripcion.repository.AsignacionGrupoRepository;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.inscripcion.service.InscripcionService;
import escuela.institucion.entity.Plantel;
import escuela.institucion.repository.InstitucionRepository;
import escuela.institucion.repository.PlantelNivelRepository;
import escuela.institucion.repository.PlantelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class InscripcionServiceImpl implements InscripcionService {

    private static final EnumSet<EstadoInscripcion> ESTADOS_VIGENTES =
            EnumSet.of(EstadoInscripcion.PREINSCRITA, EstadoInscripcion.ACTIVA);

    private final InscripcionRepository repository;
    private final AsignacionGrupoRepository asignacionRepository;
    private final AlumnoRepository alumnoRepository;
    private final PlantelRepository plantelRepository;
    private final CicloEscolarRepository cicloRepository;
    private final GradoRepository gradoRepository;
    private final GrupoRepository grupoRepository;
    private final PlantelNivelRepository ofertaRepository;
    private final InstitucionRepository institucionRepository;
    private final InscripcionMapper mapper;
    private final AsignacionGrupoMapper asignacionMapper;

    @Override
    public InscripcionResponse crear(InscripcionRequest request) {
        Alumno alumno = alumnoParaActualizar(request.alumnoId());
        institucionRepository.buscarPorIdConBloqueo(alumno.getInstitucion().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", alumno.getInstitucion().getId()));
        Plantel plantel = plantel(request.plantelId());
        CicloEscolar ciclo = ciclo(request.cicloEscolarId());
        Grado grado = grado(request.gradoId());
        Inscripcion anterior = prepararAnterior(request, alumno);
        validar(request, alumno, plantel, ciclo, grado, 0L);
        return mapper.respuesta(repository.saveAndFlush(
                mapper.nueva(request, alumno, plantel, ciclo, grado, anterior)));
    }

    @Override
    public InscripcionResponse actualizar(Long id, InscripcionRequest request) {
        Inscripcion entidad = buscarParaActualizar(id);
        verificar(entidad, request.version(), "Inscripción");
        validarPropietariosInmutables(entidad, request);
        validarTransicion(entidad.getEstado(), request.estado());
        validar(request, entidad.getAlumno(), entidad.getPlantel(), entidad.getCicloEscolar(),
                entidad.getGrado(), id);
        mapper.actualizar(entidad, request);
        if (esTerminal(request.estado())) {
            cerrarAsignacionAbierta(entidad.getId(), request.fechaFin());
        }
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionResponse obtener(Long id) {
        return mapper.respuesta(repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la inscripción", id)));
    }

    @Override
    public AsignacionGrupoResponse asignarGrupo(Long inscripcionId, AsignacionGrupoRequest request) {
        Inscripcion inscripcion = buscarParaActualizar(inscripcionId);
        Grupo grupo = grupoRepository.findByIdForUpdate(request.grupoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el grupo", request.grupoId()));
        validarAsignacion(inscripcion, grupo, request.fechaInicio());

        asignacionRepository.findAbiertaForUpdate(inscripcionId).ifPresent(actual -> {
            if (actual.getGrupo().getId().equals(grupo.getId())) {
                throw new RecursoDuplicadoException("El alumno ya está asignado a ese grupo");
            }
            if (!request.fechaInicio().isAfter(actual.getFechaInicio())) {
                throw new ReglaNegocioException("El cambio de grupo debe ser posterior a la asignación vigente");
            }
            actual.setFechaFin(request.fechaInicio().minusDays(1));
            if (limpiar(request.motivo()) != null) actual.setMotivo(limpiar(request.motivo()));
        });

        long ocupados = asignacionRepository.contarSuperpuestas(grupo.getId(),
                request.fechaInicio(), inscripcion.getFechaFin());
        if (grupo.getCapacidad() != null && ocupados >= grupo.getCapacidad()) {
            throw new ReglaNegocioException("El grupo alcanzó su capacidad máxima");
        }
        return asignacionMapper.respuesta(asignacionRepository.saveAndFlush(
                asignacionMapper.nueva(request, inscripcion, grupo)));
    }

    @Override
    public void finalizarAsignacion(Long inscripcionId, Long asignacionId, Long version,
                                    LocalDate fechaFin) {
        Inscripcion inscripcion = buscarParaActualizar(inscripcionId);
        AsignacionGrupo asignacion = asignacionRepository.findByIdForUpdate(asignacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la asignación de grupo", asignacionId));
        if (!asignacion.getInscripcion().getId().equals(inscripcionId)) {
            throw new ReglaNegocioException("La asignación no pertenece a la inscripción indicada");
        }
        verificar(asignacion, version, "Asignación de grupo");
        if (fechaFin == null || fechaFin.isBefore(asignacion.getFechaInicio())) {
            throw new ReglaNegocioException("La fecha final debe ser igual o posterior al inicio de la asignación");
        }
        if (inscripcion.getFechaFin() != null && fechaFin.isAfter(inscripcion.getFechaFin())) {
            throw new ReglaNegocioException("La asignación no puede terminar después de la inscripción");
        }
        asignacion.setFechaFin(fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionGrupoResponse> listarAsignaciones(Long inscripcionId) {
        if (!repository.existsById(inscripcionId)) {
            throw new RecursoNoEncontradoException("la inscripción", inscripcionId);
        }
        return asignacionRepository.findAllByInscripcionIdOrderByFechaInicioDesc(inscripcionId)
                .stream().map(asignacionMapper::respuesta).toList();
    }

    private Inscripcion prepararAnterior(InscripcionRequest request, Alumno alumno) {
        if (request.inscripcionAnteriorId() == null) return null;
        Inscripcion anterior = buscarParaActualizar(request.inscripcionAnteriorId());
        if (!anterior.getAlumno().getId().equals(alumno.getId())) {
            throw new ReglaNegocioException("La inscripción anterior debe pertenecer al mismo alumno");
        }
        if (!ESTADOS_VIGENTES.contains(anterior.getEstado())) {
            throw new ReglaNegocioException("La inscripción anterior ya está cerrada");
        }
        if (!request.fechaInicio().isAfter(anterior.getFechaInicio())) {
            throw new ReglaNegocioException("La nueva inscripción debe iniciar después de la anterior");
        }
        LocalDate cierre = request.fechaInicio().minusDays(1);
        anterior.setFechaFin(cierre);
        anterior.setEstado(EstadoInscripcion.FINALIZADA);
        cerrarAsignacionAbierta(anterior.getId(), cierre);
        return anterior;
    }

    private void validar(InscripcionRequest request, Alumno alumno, Plantel plantel,
                         CicloEscolar ciclo, Grado grado, Long idExcluido) {
        Long institucionId = alumno.getInstitucion().getId();
        if (!institucionId.equals(plantel.getInstitucion().getId())
                || !institucionId.equals(ciclo.getInstitucion().getId())
                || !institucionId.equals(grado.getNivelEducativo().getInstitucion().getId())) {
            throw new ReglaNegocioException("Alumno, plantel, ciclo y grado deben pertenecer a la misma institución");
        }
        if (!alumno.isActivo() || !plantel.isActivo() || !plantel.getInstitucion().isActivo()
                || !grado.isActivo() || !grado.getNivelEducativo().isActivo()) {
            throw new ReglaNegocioException("Alumno, institución, plantel, nivel y grado deben estar activos");
        }
        if (ciclo.getEstado() == EstadoAcademico.CERRADO) {
            throw new ReglaNegocioException("No se pueden modificar inscripciones de un ciclo cerrado");
        }
        if (!ofertaRepository.existsByPlantelIdAndNivelEducativoIdAndActivoTrue(
                plantel.getId(), grado.getNivelEducativo().getId())) {
            throw new ReglaNegocioException("El plantel no ofrece activamente el nivel del grado");
        }
        validarFechas(request, ciclo);
        validarEstado(request);
        String numero = codigo(request.numeroInscripcion());
        if (repository.existsByAlumnoInstitucionIdAndNumeroInscripcionIgnoreCaseAndIdNot(
                institucionId, numero, idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe ese número de inscripción en la institución");
        }
        if (ESTADOS_VIGENTES.contains(request.estado())
                && !repository.buscarSuperpuestas(request.alumnoId(), idExcluido,
                ESTADOS_VIGENTES, request.fechaInicio(), request.fechaFin()).isEmpty()) {
            throw new RecursoDuplicadoException("El alumno ya tiene una inscripción vigente durante esas fechas");
        }
    }

    private void validarFechas(InscripcionRequest request, CicloEscolar ciclo) {
        if (request.fechaFin() != null && request.fechaFin().isBefore(request.fechaInicio())) {
            throw new ReglaNegocioException("La fecha de fin no puede ser anterior al inicio");
        }
        if (request.fechaInicio().isBefore(ciclo.getFechaInicio())
                || request.fechaInicio().isAfter(ciclo.getFechaFin())
                || (request.fechaFin() != null && request.fechaFin().isAfter(ciclo.getFechaFin()))) {
            throw new ReglaNegocioException("La vigencia de la inscripción debe quedar dentro del ciclo escolar");
        }
        if (request.fechaInscripcion().isAfter(request.fechaInicio())) {
            throw new ReglaNegocioException("La fecha de inscripción no puede ser posterior al inicio escolar");
        }
    }

    private void validarEstado(InscripcionRequest request) {
        if (esTerminal(request.estado()) && request.fechaFin() == null) {
            throw new ReglaNegocioException("Indica la fecha de fin para cerrar la inscripción");
        }
        boolean requiereMotivo = request.estado() == EstadoInscripcion.BAJA
                || request.estado() == EstadoInscripcion.CANCELADA;
        if (requiereMotivo && limpiar(request.motivoBajaCancelacion()) == null) {
            throw new ReglaNegocioException("Indica el motivo de la baja o cancelación");
        }
        if (!requiereMotivo && limpiar(request.motivoBajaCancelacion()) != null) {
            throw new ReglaNegocioException("El motivo sólo corresponde a una baja o cancelación");
        }
    }

    private void validarTransicion(EstadoInscripcion actual, EstadoInscripcion nueva) {
        boolean valida = actual == nueva
                || actual == EstadoInscripcion.PREINSCRITA
                    && (nueva == EstadoInscripcion.ACTIVA || nueva == EstadoInscripcion.CANCELADA)
                || actual == EstadoInscripcion.ACTIVA
                    && (nueva == EstadoInscripcion.BAJA || nueva == EstadoInscripcion.FINALIZADA);
        if (!valida) throw new ReglaNegocioException("La transición de estado solicitada no está permitida");
    }

    private void validarAsignacion(Inscripcion inscripcion, Grupo grupo, LocalDate inicio) {
        if (!ESTADOS_VIGENTES.contains(inscripcion.getEstado())) {
            throw new ReglaNegocioException("Sólo una inscripción vigente puede recibir grupo");
        }
        if (!grupo.isActivo()) throw new ReglaNegocioException("El grupo seleccionado está inactivo");
        if (!grupo.getPlantel().getId().equals(inscripcion.getPlantel().getId())
                || !grupo.getCicloEscolar().getId().equals(inscripcion.getCicloEscolar().getId())
                || !grupo.getGrado().getId().equals(inscripcion.getGrado().getId())) {
            throw new ReglaNegocioException("El grupo debe coincidir con plantel, ciclo y grado de la inscripción");
        }
        if (grupo.getCicloEscolar().getEstado() == EstadoAcademico.CERRADO) {
            throw new ReglaNegocioException("No se puede asignar un grupo de un ciclo cerrado");
        }
        if (inicio.isBefore(inscripcion.getFechaInicio())
                || (inscripcion.getFechaFin() != null && inicio.isAfter(inscripcion.getFechaFin()))) {
            throw new ReglaNegocioException("La asignación debe iniciar dentro de la vigencia de la inscripción");
        }
    }

    private void validarPropietariosInmutables(Inscripcion entidad, InscripcionRequest request) {
        if (!entidad.getAlumno().getId().equals(request.alumnoId())
                || !entidad.getPlantel().getId().equals(request.plantelId())
                || !entidad.getCicloEscolar().getId().equals(request.cicloEscolarId())
                || !entidad.getGrado().getId().equals(request.gradoId())
                || !java.util.Objects.equals(entidad.getInscripcionAnterior() == null ? null
                : entidad.getInscripcionAnterior().getId(), request.inscripcionAnteriorId())) {
            throw new ReglaNegocioException("No se pueden cambiar alumno, plantel, ciclo, grado o inscripción anterior");
        }
    }

    private void cerrarAsignacionAbierta(Long inscripcionId, LocalDate fechaFin) {
        asignacionRepository.findAbiertaForUpdate(inscripcionId).ifPresent(asignacion -> {
            if (fechaFin.isBefore(asignacion.getFechaInicio())) {
                throw new ReglaNegocioException("No se puede cerrar la inscripción antes de su asignación de grupo");
            }
            asignacion.setFechaFin(fechaFin);
        });
    }

    private boolean esTerminal(EstadoInscripcion estado) {
        return estado == EstadoInscripcion.BAJA || estado == EstadoInscripcion.FINALIZADA
                || estado == EstadoInscripcion.CANCELADA;
    }

    private Inscripcion buscarParaActualizar(Long id) {
        return repository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la inscripción", id));
    }

    private Alumno alumnoParaActualizar(Long id) {
        return alumnoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el alumno", id));
    }

    private Plantel plantel(Long id) {
        return plantelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el plantel", id));
    }

    private CicloEscolar ciclo(Long id) {
        return cicloRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el ciclo escolar", id));
    }

    private Grado grado(Long id) {
        return gradoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el grado", id));
    }
}
