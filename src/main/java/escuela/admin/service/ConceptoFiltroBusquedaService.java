package escuela.admin.service;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.dto.OpcionAutocompletado;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.entity.CuotaAlumno;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.AcuerdoAnticipadoCargo;
import escuela.finanzas.entity.Pago;
import escuela.finanzas.entity.SolicitudAplicacionPago;
import escuela.finanzas.entity.AplicacionPago;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.security.core.context.SecurityContextHolder;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.persistence.EntityManager;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Sugerencias históricas únicamente si un cargo o cuota está dentro del alcance. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConceptoFiltroBusquedaService {
    private final EntityManager em;
    private final AlcanceDatosService alcance;

    public ResultadoAutocompletado buscar(String consulta) {
        String texto = consulta == null ? "" : consulta.trim().toLowerCase(Locale.ROOT);
        if ((!texto.isEmpty() && texto.length() < 3) || texto.length() > 150) return ResultadoAutocompletado.vacio();
        int limite = texto.isEmpty() ? 10 : 20;
        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(ConceptoCobro.class);
        var concepto = query.from(ConceptoCobro.class);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var permisos = auth.getAuthorities().stream().map(a -> a.getAuthority()).collect(java.util.stream.Collectors.toSet());
        var visibles = new ArrayList<Predicate>();
        if (permisos.contains("CARGO_LEER") || permisos.contains("CARGO_ADMINISTRAR")) {
            var cargos = query.subquery(Integer.class);
            var cargo = cargos.from(Cargo.class);
            cargos.select(cb.literal(1)).where(cb.equal(cargo.get("conceptoCobro"), concepto),
                    alcance.<Cargo>especificacion(ModuloCatalogo.CARGOS).toPredicate(cargo, query, cb));
            visibles.add(cb.exists(cargos));
        }
        if (permisos.contains("CUOTA_ALUMNO_LEER") || permisos.contains("CUOTA_ALUMNO_ADMINISTRAR")) {
            var cuotas = query.subquery(Integer.class);
            var cuota = cuotas.from(CuotaAlumno.class);
            cuotas.select(cb.literal(1)).where(cb.equal(cuota.get("conceptoCobro"), concepto),
                    alcance.<CuotaAlumno>especificacion(ModuloCatalogo.CUOTAS_ALUMNO).toPredicate(cuota, query, cb));
            visibles.add(cb.exists(cuotas));
        }
        if (permisos.contains("PAGO_LEER")) {
            var pagos = query.subquery(Integer.class);
            var pago = pagos.from(Pago.class);
            var solicitudes = pagos.subquery(Integer.class);
            var solicitud = solicitudes.from(SolicitudAplicacionPago.class);
            solicitudes.select(cb.literal(1)).where(cb.equal(solicitud.get("pago"), pago), cb.equal(solicitud.get("cargo").get("conceptoCobro"), concepto));
            var aplicaciones = pagos.subquery(Integer.class);
            var aplicacion = aplicaciones.from(AplicacionPago.class);
            aplicaciones.select(cb.literal(1)).where(cb.equal(aplicacion.get("pago"), pago), cb.equal(aplicacion.get("cargo").get("conceptoCobro"), concepto));
            var acuerdos = pagos.subquery(Integer.class);
            var acuerdo = acuerdos.from(AcuerdoAnticipadoCargo.class);
            acuerdos.select(cb.literal(1)).where(cb.equal(acuerdo.get("acuerdo").get("id"), pago.get("acuerdoAnticipadoId")), cb.equal(acuerdo.get("cargo").get("conceptoCobro"), concepto));
            pagos.select(cb.literal(1)).where(alcance.<Pago>especificacion(ModuloCatalogo.PAGOS).toPredicate(pago, query, cb), cb.or(cb.exists(solicitudes), cb.exists(aplicaciones), cb.exists(acuerdos)));
            visibles.add(cb.exists(pagos));
        }
        String patron = "%" + texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
        query.select(concepto).where(cb.or(visibles.toArray(Predicate[]::new)),
                cb.like(concepto.get("busquedaAutocomplete"), patron, '\\'));
        query.orderBy(cb.asc(concepto.get("nombre")), cb.asc(concepto.get("id")));
        var resultados = em.createQuery(query).setMaxResults(limite + 1).getResultList();
        return new ResultadoAutocompletado(resultados.stream().limit(limite)
                .map(c -> new OpcionAutocompletado(c.getId(), c.getCodigo(),
                        c.getNombre() + (c.isActivo() ? "" : " · Inactivo"))).toList(), resultados.size() > limite);
    }
}
