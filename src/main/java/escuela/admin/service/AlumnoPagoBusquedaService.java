package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.alumno.entity.Alumno;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class AlumnoPagoBusquedaService {
    private final EntityManager em;
    private final AlcanceDatosService alcance;

    public ResultadoAutocompletado buscar(String consulta) {
        String texto = consulta == null ? "" : consulta.trim().toLowerCase(Locale.ROOT);
        if (!texto.isEmpty() && texto.length() < 3) return ResultadoAutocompletado.vacio();
        if (texto.length() > 150) return ResultadoAutocompletado.vacio();
        int limite = texto.isEmpty() ? 10 : 20;
        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(Alumno.class);
        var alumno = query.from(Alumno.class);
        String patron = "%" + texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
        query.select(alumno).where(alcance.alumnosConPagos().toPredicate(alumno, query, cb),
                cb.like(alumno.get("busquedaAutocomplete"), patron, '\\'));
        query.orderBy(cb.asc(alumno.get("primerApellido")), cb.asc(alumno.get("nombres")), cb.asc(alumno.get("id")));
        // Sólo 11/21 filas, sin COUNT global ni cargar catálogos completos.
        var alumnos = em.createQuery(query).setMaxResults(limite + 1).getResultList();
        return new ResultadoAutocompletado(alumnos.stream().limit(limite).map(a -> new OpcionAutocompletado(
                a.getId(), a.getMatricula() + " · " + a.getNombres() + " " + a.getPrimerApellido()
                        + (a.getSegundoApellido() == null ? "" : " " + a.getSegundoApellido()),
                "Con pagos relacionados" + (a.isActivo() ? "" : " · Alumno inactivo"))).toList(), alumnos.size() > limite);
    }
}
