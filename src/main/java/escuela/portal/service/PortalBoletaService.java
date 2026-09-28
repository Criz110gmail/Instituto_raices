package escuela.portal.service;

import escuela.admin.dto.BoletaCalificacionFila;
import escuela.admin.dto.BoletaDetalle;
import escuela.admin.service.BoletaDetalleService;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.portal.dto.PortalBoletaFila;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortalBoletaService {
    private static final int TAMANIO = 10;
    private final PortalTutorService portal;
    private final InscripcionRepository inscripciones;
    private final BoletaDetalleService detalles;

    public Page<PortalBoletaFila> listar(UsuarioPrincipal principal, Long alumnoId, int pagina) {
        var hijo = portal.validarHijo(principal, alumnoId);
        Page<Inscripcion> bloque = inscripciones.buscarBoletasPortal(hijo.alumnoId(), principal.institucionId(),
                PageRequest.of(Math.max(0, pagina), TAMANIO));
        Map<Long, BoletaDetalle> contenido = detalles.crear(bloque.getContent());
        return bloque.map(inscripcion -> fila(contenido.get(inscripcion.getId())));
    }

    public BoletaDetalle detalle(UsuarioPrincipal principal, Long alumnoId, Long inscripcionId) {
        var hijo = portal.validarHijo(principal, alumnoId);
        Inscripcion inscripcion = inscripciones.buscarBoletaPortal(inscripcionId, hijo.alumnoId(), principal.institucionId())
                .orElseThrow(() -> new ReglaNegocioException(
                        "La boleta solicitada no está disponible para esta cuenta familiar"));
        BoletaDetalle detalle = detalles.crear(List.of(inscripcion)).get(inscripcionId);
        if (detalle == null || detalle.calificaciones().isEmpty()) {
            throw new ReglaNegocioException("La boleta todavía no tiene resultados publicados");
        }
        return detalle;
    }

    private PortalBoletaFila fila(BoletaDetalle detalle) {
        long periodos = detalle.calificaciones().stream().map(BoletaCalificacionFila::periodo).distinct().count();
        long materias = detalle.calificaciones().stream().map(BoletaCalificacionFila::materia).distinct().count();
        return new PortalBoletaFila(detalle.inscripcionId(), detalle.ciclo(), detalle.plantel(), detalle.grado(),
                detalle.grupo(), materias, periodos);
    }
}
