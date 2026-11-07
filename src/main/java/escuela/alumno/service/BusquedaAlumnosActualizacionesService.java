package escuela.alumno.service;

import escuela.admin.dto.OpcionAutocompletado;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.alumno.entity.Alumno;
import escuela.alumno.entity.ActualizacionExpedienteFamiliar;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.persistence.EntityManager;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class BusquedaAlumnosActualizacionesService {
    private final EntityManager em;
    private final AlcanceDatosService alcance;
    public ResultadoAutocompletado buscar(Long institucionId, String q) {
        alcance.validarAdministracionInstitucional(institucionId);
        String texto=q==null?"":q.trim().toLowerCase(Locale.ROOT);
        if ((!texto.isEmpty() && texto.length()<3) || texto.length()>150) return ResultadoAutocompletado.vacio();
        int limite=texto.isEmpty()?10:20;
        var cb=em.getCriteriaBuilder();var consulta=cb.createQuery(Alumno.class);var a=consulta.from(Alumno.class);
        var existe=consulta.subquery(Long.class);var solicitud=existe.from(ActualizacionExpedienteFamiliar.class);
        existe.select(cb.literal(1L)).where(cb.equal(solicitud.get("alumno").get("id"),a.get("id")),cb.equal(solicitud.get("institucion").get("id"),institucionId));
        String patron="%"+texto.replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%";
        consulta.select(a).where(cb.equal(a.get("institucion").get("id"),institucionId),cb.exists(existe),cb.like(a.get("busquedaAutocomplete"),patron,'\\'));
        consulta.orderBy(cb.asc(a.get("primerApellido")),cb.asc(a.get("nombres")),cb.asc(a.get("id")));
        var filas=em.createQuery(consulta).setMaxResults(limite+1).getResultList();
        return new ResultadoAutocompletado(filas.stream().limit(limite).map(x->new OpcionAutocompletado(x.getId(),x.getMatricula()+" · "+x.getNombres()+" "+x.getPrimerApellido()+(x.getSegundoApellido()==null?"":" "+x.getSegundoApellido()),"Con solicitudes familiares")).toList(),filas.size()>limite);
    }
}
