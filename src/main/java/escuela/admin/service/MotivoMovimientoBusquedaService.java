package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.MotivoFinancieroRepository;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.Locale;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class MotivoMovimientoBusquedaService {
    private final MotivoFinancieroRepository repository;
    private final AlcanceDatosService alcance;
    public ResultadoAutocompletado buscar(Long institucionId,DireccionMovimiento direccion,String q) {
        alcance.validarInstitucion(institucionId);
        String texto=q==null?"":q.trim();
        if(direccion==null||(!texto.isEmpty()&&texto.length()<3))return ResultadoAutocompletado.vacio();
        String patron="%"+texto.substring(0,Math.min(100,texto.length())).toLowerCase(Locale.ROOT)
            .replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%";
        Specification<MotivoFinanciero> filtro=(root,query,cb)->cb.and(
            cb.equal(root.get("institucion").get("id"),institucionId),cb.isTrue(root.get("activo")),
            root.get("naturaleza").in(NaturalezaMotivoFinanciero.AMBOS,NaturalezaMotivoFinanciero.valueOf(direccion.name())),
            cb.or(cb.like(cb.lower(root.get("codigo")),patron,'\\'),cb.like(cb.lower(root.get("nombre")),patron,'\\'),cb.like(cb.lower(root.get("categoria")),patron,'\\')));
        var pagina=repository.findAll(filtro,PageRequest.of(0,11,Sort.by("nombre").ascending().and(Sort.by("id"))));
        return new ResultadoAutocompletado(pagina.getContent().stream().limit(10)
            .map(m->new OpcionAutocompletado(m.getId(),m.getCodigo()+" · "+m.getNombre(),m.getCategoria()+" · "+m.getNaturaleza())).toList(),pagina.hasNext()||pagina.getNumberOfElements()>10);
    }
}
