package escuela.admin.service;

import escuela.academico.entity.Grupo;
import escuela.academico.entity.TipoEvaluacion;
import escuela.academico.repository.CicloEscolarRepository;
import escuela.academico.repository.GrupoRepository;
import escuela.admin.dto.*;
import escuela.calificacion.entity.Calificacion;
import escuela.calificacion.entity.EstadoCalificacion;
import escuela.calificacion.repository.CalificacionRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.entity.AsignacionGrupo;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.AsignacionGrupoRepository;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.institucion.repository.PlantelRepository;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoletaConsultaService {
    private final InscripcionRepository inscripciones;
    private final AsignacionGrupoRepository asignaciones;
    private final CalificacionRepository calificaciones;
    private final CicloEscolarRepository ciclos;
    private final PlantelRepository planteles;
    private final GrupoRepository grupos;
    private final AlcanceDatosService alcance;

    public FiltroBoleta normalizar(FiltroBoleta original) {
        FiltroBoleta filtro = original.normalizado();
        if (filtro.institucionId() == null) return filtro;
        alcance.validarInstitucion(filtro.institucionId());
        if (filtro.cicloId() == null) return filtro;
        var ciclo = ciclos.findById(filtro.cicloId())
                .orElseThrow(() -> new ReglaNegocioException("El ciclo escolar seleccionado no existe"));
        if (!ciclo.getInstitucion().getId().equals(filtro.institucionId())) {
            throw new ReglaNegocioException("El ciclo escolar no pertenece a la institución seleccionada");
        }
        if (filtro.plantelId() != null) {
            alcance.validarPlantel(filtro.plantelId());
            var plantel = planteles.findById(filtro.plantelId())
                    .orElseThrow(() -> new ReglaNegocioException("El plantel seleccionado no existe"));
            if (!plantel.getInstitucion().getId().equals(filtro.institucionId())) {
                throw new ReglaNegocioException("El plantel no pertenece a la institución seleccionada");
            }
        }
        if (filtro.grupoId() == null && !filtro.grupoTexto().isBlank()) {
            throw new ReglaNegocioException("Selecciona un grupo de la lista de resultados o limpia ese filtro");
        }
        if (filtro.grupoId() != null) {
            alcance.validarRecurso(ModuloCatalogo.GRUPOS, filtro.grupoId());
            Grupo grupo = grupos.findById(filtro.grupoId())
                    .orElseThrow(() -> new ReglaNegocioException("El grupo seleccionado no existe"));
            if (!grupo.getCicloEscolar().getId().equals(filtro.cicloId())) {
                throw new ReglaNegocioException("El grupo no pertenece al ciclo escolar seleccionado");
            }
            if (filtro.plantelId() != null && !grupo.getPlantel().getId().equals(filtro.plantelId())) {
                throw new ReglaNegocioException("El grupo no pertenece al plantel seleccionado");
            }
        }
        return filtro;
    }

    public ResultadoBoletas consultar(FiltroBoleta original) {
        FiltroBoleta filtro = normalizar(original);
        if (filtro.institucionId() == null || filtro.cicloId() == null) {
            return new ResultadoBoletas(Page.empty(PageRequest.of(filtro.pagina(), filtro.tamanio())));
        }
        Page<BoletaDetalle> bloque = bloqueDetallado(filtro);
        return new ResultadoBoletas(bloque.map(this::filaListado));
    }

    public Page<BoletaDetalle> bloqueDetallado(FiltroBoleta original) {
        FiltroBoleta filtro = normalizar(original);
        exigirCiclo(filtro);
        PageRequest pagina = PageRequest.of(filtro.pagina(), filtro.tamanio(), Sort.by(
                Sort.Order.asc("alumno.primerApellido"), Sort.Order.asc("alumno.segundoApellido"),
                Sort.Order.asc("alumno.nombres"), Sort.Order.asc("id")));
        Page<Inscripcion> resultado = inscripciones.findAll(especificacion(filtro), pagina);
        Map<Long, BoletaDetalle> detalles = detalles(resultado.getContent());
        return resultado.map(i -> detalles.get(i.getId()));
    }

    public BoletaDetalle detalle(Long inscripcionId) {
        alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, inscripcionId);
        Inscripcion inscripcion = inscripciones.findById(inscripcionId)
                .orElseThrow(() -> new ReglaNegocioException("La inscripción solicitada no existe"));
        BoletaDetalle detalle = detalles(List.of(inscripcion)).get(inscripcionId);
        if (detalle == null || detalle.calificaciones().isEmpty()) {
            throw new ReglaNegocioException("La inscripción todavía no tiene calificaciones publicadas incluidas en boleta");
        }
        return detalle;
    }

    public FiltroBoleta exigirExportacion(FiltroBoleta filtro) {
        FiltroBoleta normalizado = normalizar(filtro);
        exigirCiclo(normalizado);
        return normalizado;
    }

    private Specification<Inscripcion> especificacion(FiltroBoleta f) {
        Specification<Inscripcion> filtro = (root, query, cb) -> cb.and(
                cb.equal(root.get("alumno").get("institucion").get("id"), f.institucionId()),
                cb.equal(root.get("cicloEscolar").get("id"), f.cicloId()),
                cb.notEqual(root.get("estado"), EstadoInscripcion.CANCELADA));
        if (f.plantelId() != null) filtro = filtro.and((r, q, cb) -> cb.equal(r.get("plantel").get("id"), f.plantelId()));
        if (!f.q().isBlank()) {
            String patron = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            filtro = filtro.and((r, q, cb) -> cb.or(
                    cb.like(cb.lower(r.get("alumno").get("matricula")), patron),
                    cb.like(cb.lower(r.get("alumno").get("nombres")), patron),
                    cb.like(cb.lower(r.get("alumno").get("primerApellido")), patron),
                    cb.like(cb.lower(r.get("alumno").get("segundoApellido")), patron),
                    cb.like(cb.lower(r.get("numeroInscripcion")), patron)));
        }
        if (f.grupoId() != null) filtro = filtro.and(grupo(f.grupoId()));
        Specification<Inscripcion> alcanceInscripciones = alcance.especificacion(ModuloCatalogo.INSCRIPCIONES);
        return alcanceInscripciones.and(filtro).and(conPublicadas());
    }

    private Specification<Inscripcion> conPublicadas() {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Calificacion> c = sub.from(Calificacion.class);
            sub.select(c.get("id")).where(cb.and(
                    cb.equal(c.get("inscripcion").get("id"), root.get("id")),
                    cb.equal(c.get("estado"), EstadoCalificacion.PUBLICADA),
                    cb.isTrue(c.get("materiaGrado").get("incluirBoleta"))));
            return cb.exists(sub);
        };
    }

    private Specification<Inscripcion> grupo(Long grupoId) {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<AsignacionGrupo> a = sub.from(AsignacionGrupo.class);
            sub.select(a.get("id")).where(cb.and(
                    cb.equal(a.get("inscripcion").get("id"), root.get("id")),
                    cb.equal(a.get("grupo").get("id"), grupoId)));
            return cb.exists(sub);
        };
    }

    private Map<Long, BoletaDetalle> detalles(List<Inscripcion> lista) {
        if (lista.isEmpty()) return Map.of();
        List<Long> ids = lista.stream().map(Inscripcion::getId).toList();
        Map<Long, List<Calificacion>> porInscripcion = calificaciones
                .findAllByInscripcionIdInAndEstadoAndMateriaGradoIncluirBoletaTrueOrderByInscripcionIdAscPeriodoAcademicoOrdenAscMateriaGradoOrdenAscIdAsc(
                        ids, EstadoCalificacion.PUBLICADA).stream()
                .collect(Collectors.groupingBy(c -> c.getInscripcion().getId(), LinkedHashMap::new, Collectors.toList()));
        Map<Long, String> grupo = new HashMap<>();
        for (AsignacionGrupo a : asignaciones.buscarHistorialParaBoleta(ids)) {
            grupo.putIfAbsent(a.getInscripcion().getId(), a.getGrupo().getNombre() + " · " + a.getGrupo().getTurno().name());
        }
        Map<Long, BoletaDetalle> resultado = new LinkedHashMap<>();
        for (Inscripcion i : lista) {
            List<BoletaCalificacionFila> filas = porInscripcion.getOrDefault(i.getId(), List.of()).stream()
                    .map(this::filaCalificacion).toList();
            var alumno = i.getAlumno();
            resultado.put(i.getId(), new BoletaDetalle(i.getId(), alumno.getInstitucion().getId(),
                    alumno.getInstitucion().getNombre(), i.getNumeroInscripcion(), alumno.getMatricula(),
                    nombre(alumno.getNombres(), alumno.getPrimerApellido(), alumno.getSegundoApellido()),
                    i.getPlantel().getNombre(), i.getCicloEscolar().getNombre(), i.getGrado().getNombre(),
                    grupo.getOrDefault(i.getId(), "Sin grupo registrado"), filas));
        }
        return resultado;
    }

    private BoletaCalificacionFila filaCalificacion(Calificacion c) {
        String resultado = c.getTipoEvaluacion() == TipoEvaluacion.NUMERICA
                ? numero(c.getValorNumerico(), c.getDecimales()) : c.getValorCualitativo();
        String escala = c.getTipoEvaluacion() == TipoEvaluacion.NUMERICA
                ? numero(c.getEscalaMinima(), c.getDecimales()) + "–" + numero(c.getEscalaMaxima(), c.getDecimales())
                    + " · mínima " + numero(c.getMinimaAprobatoria(), c.getDecimales())
                : "Evaluación cualitativa";
        return new BoletaCalificacionFila(c.getPeriodoAcademico().getNombre(),
                c.getMateriaGrado().getMateria().getNombre(), resultado, escala,
                c.getObservaciones() == null ? "" : c.getObservaciones());
    }

    private BoletaListadoFila filaListado(BoletaDetalle d) {
        long periodos = d.calificaciones().stream().map(BoletaCalificacionFila::periodo).distinct().count();
        long materias = d.calificaciones().stream().map(BoletaCalificacionFila::materia).distinct().count();
        return new BoletaListadoFila(d.inscripcionId(), d.matricula(), d.alumno(), d.plantel(),
                d.grado(), d.grupo(), periodos, materias);
    }

    private void exigirCiclo(FiltroBoleta filtro) {
        if (filtro.institucionId() == null) throw new ReglaNegocioException("Selecciona una institución");
        if (filtro.cicloId() == null) throw new ReglaNegocioException("Selecciona un ciclo escolar");
    }

    private String numero(BigDecimal valor, int decimales) {
        return valor == null ? "" : valor.setScale(decimales, RoundingMode.HALF_UP).toPlainString();
    }

    private String nombre(String... partes) {
        return Arrays.stream(partes).filter(p -> p != null && !p.isBlank()).collect(Collectors.joining(" "));
    }
}
