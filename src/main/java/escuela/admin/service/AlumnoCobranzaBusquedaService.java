package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.alumno.entity.Alumno;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.entity.CuotaAlumno;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.persistence.EntityManager;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class AlumnoCobranzaBusquedaService {
    private final EntityManager em;
    private final AlcanceDatosService alcance;
    public ResultadoAutocompletado buscar(String consulta, boolean cargos) {
        String texto=consulta==null?"":consulta.trim().toLowerCase(Locale.ROOT);
        if ((!texto.isEmpty() && texto.length()<3)||texto.length()>150) return ResultadoAutocompletado.vacio();
        int limite=texto.isEmpty()?10:20;
        var cb=em.getCriteriaBuilder(); var query=cb.createQuery(Alumno.class); var alumno=query.from(Alumno.class);
        var relacionados=query.subquery(Integer.class);
        if(cargos) {
            var entidad=relacionados.from(Cargo.class);
            relacionados.select(cb.literal(1)).where(cb.equal(entidad.get("inscripcion").get("alumno"),alumno),alcance.<Cargo>especificacion(ModuloCatalogo.CARGOS).toPredicate(entidad,query,cb));
        } else {
            var entidad=relacionados.from(CuotaAlumno.class);
            relacionados.select(cb.literal(1)).where(cb.equal(entidad.get("inscripcion").get("alumno"),alumno),alcance.<CuotaAlumno>especificacion(ModuloCatalogo.CUOTAS_ALUMNO).toPredicate(entidad,query,cb));
        }
        String patron="%"+texto.replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%";
        query.select(alumno).where(cb.exists(relacionados),cb.like(alumno.get("busquedaAutocomplete"),patron,'\\'));
        query.orderBy(cb.asc(alumno.get("primerApellido")),cb.asc(alumno.get("nombres")),cb.asc(alumno.get("id")));
        var alumnos=em.createQuery(query).setMaxResults(limite+1).getResultList();
        return new ResultadoAutocompletado(alumnos.stream().limit(limite).map(a->new OpcionAutocompletado(a.getId(),a.getMatricula()+" · "+a.getNombres()+" "+a.getPrimerApellido()+(a.getSegundoApellido()==null?"":" "+a.getSegundoApellido()),(cargos?"Con adeudos relacionados":"Con cuotas relacionadas")+(a.isActivo()?"":" · Alumno inactivo"))).toList(),alumnos.size()>limite);
    }
}
