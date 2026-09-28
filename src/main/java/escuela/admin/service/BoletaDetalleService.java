package escuela.admin.service;

import escuela.academico.entity.TipoEvaluacion;
import escuela.admin.dto.BoletaCalificacionFila;
import escuela.admin.dto.BoletaDetalle;
import escuela.calificacion.entity.Calificacion;
import escuela.calificacion.entity.EstadoCalificacion;
import escuela.calificacion.repository.CalificacionRepository;
import escuela.inscripcion.entity.AsignacionGrupo;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.AsignacionGrupoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoletaDetalleService {
    private final AsignacionGrupoRepository asignaciones;
    private final CalificacionRepository calificaciones;

    public Map<Long, BoletaDetalle> crear(List<Inscripcion> inscripciones) {
        if (inscripciones.isEmpty()) return Map.of();
        List<Long> ids = inscripciones.stream().map(Inscripcion::getId).toList();
        Map<Long, List<Calificacion>> porInscripcion = calificaciones
                .findAllByInscripcionIdInAndEstadoAndMateriaGradoIncluirBoletaTrueOrderByInscripcionIdAscPeriodoAcademicoOrdenAscMateriaGradoOrdenAscIdAsc(
                        ids, EstadoCalificacion.PUBLICADA).stream()
                .collect(Collectors.groupingBy(c -> c.getInscripcion().getId(), LinkedHashMap::new, Collectors.toList()));
        Map<Long, String> grupos = new HashMap<>();
        for (AsignacionGrupo asignacion : asignaciones.buscarHistorialParaBoleta(ids)) {
            grupos.putIfAbsent(asignacion.getInscripcion().getId(),
                    asignacion.getGrupo().getNombre() + " · " + asignacion.getGrupo().getTurno().name());
        }
        Map<Long, BoletaDetalle> resultado = new LinkedHashMap<>();
        for (Inscripcion inscripcion : inscripciones) {
            List<BoletaCalificacionFila> filas = porInscripcion.getOrDefault(inscripcion.getId(), List.of()).stream()
                    .map(this::calificacion).toList();
            var alumno = inscripcion.getAlumno();
            resultado.put(inscripcion.getId(), new BoletaDetalle(inscripcion.getId(), alumno.getInstitucion().getId(),
                    alumno.getInstitucion().getNombre(), inscripcion.getNumeroInscripcion(), alumno.getMatricula(),
                    nombre(alumno.getNombres(), alumno.getPrimerApellido(), alumno.getSegundoApellido()),
                    inscripcion.getPlantel().getNombre(), inscripcion.getCicloEscolar().getNombre(),
                    inscripcion.getGrado().getNombre(), grupos.getOrDefault(inscripcion.getId(), "Sin grupo registrado"), filas));
        }
        return resultado;
    }

    private BoletaCalificacionFila calificacion(Calificacion calificacion) {
        String resultado = calificacion.getTipoEvaluacion() == TipoEvaluacion.NUMERICA
                ? numero(calificacion.getValorNumerico(), calificacion.getDecimales())
                : calificacion.getValorCualitativo();
        String escala = calificacion.getTipoEvaluacion() == TipoEvaluacion.NUMERICA
                ? numero(calificacion.getEscalaMinima(), calificacion.getDecimales()) + "–"
                    + numero(calificacion.getEscalaMaxima(), calificacion.getDecimales()) + " · mínima "
                    + numero(calificacion.getMinimaAprobatoria(), calificacion.getDecimales())
                : "Evaluación cualitativa";
        return new BoletaCalificacionFila(calificacion.getPeriodoAcademico().getNombre(),
                calificacion.getMateriaGrado().getMateria().getNombre(), resultado, escala,
                calificacion.getObservaciones() == null ? "" : calificacion.getObservaciones());
    }

    private String numero(BigDecimal valor, int decimales) {
        return valor == null ? "" : valor.setScale(decimales, RoundingMode.HALF_UP).toPlainString();
    }

    private String nombre(String... partes) {
        return Arrays.stream(partes).filter(p -> p != null && !p.isBlank()).collect(Collectors.joining(" "));
    }
}
