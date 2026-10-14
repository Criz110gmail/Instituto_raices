package escuela.admin.support;

import escuela.admin.dto.FiltroCatalogo;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

/** Cargo y CuotaAlumno tienen una relación directa y obligatoria con el concepto. */
public final class FiltroConceptoCobro {
    private FiltroConceptoCobro() {}

    public static <T> Specification<T> especificacion(FiltroCatalogo filtro) {
        return (root,query,cb)-> {
            String texto=filtro.conceptoCobro()==null?"":filtro.conceptoCobro().trim();
            if(texto.isEmpty())return cb.conjunction();
            String patron="%"+texto.toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%";
            var concepto=root.get("conceptoCobro");
            return cb.or(cb.like(cb.lower(concepto.get("codigo")),patron,'\\'),
                         cb.like(cb.lower(concepto.get("nombre")),patron,'\\'));
        };
    }
}
