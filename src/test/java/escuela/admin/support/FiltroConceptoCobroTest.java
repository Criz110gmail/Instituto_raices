package escuela.admin.support;

import escuela.admin.dto.FiltroCatalogo;
import escuela.cobranza.entity.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class FiltroConceptoCobroTest {
    @Test void vacioNoRestringeConceptos() {
        var cb=mock(CriteriaBuilder.class);var root=mock(Root.class);
        FiltroConceptoCobro.especificacion(new FiltroCatalogo("Alumno","TODOS",0,25)).toPredicate(root,mock(CriteriaQuery.class),cb);
        verify(cb).conjunction();verifyNoInteractions(root);
    }
    @Test void codigoONombreCombinableConAlumnoYLiteralesEnAmbasEntidades() {
        for(var entidad:new Class[]{Cargo.class,CuotaAlumno.class}) {
            var cb=mock(CriteriaBuilder.class,RETURNS_DEEP_STUBS);
            var root=mock(Root.class,RETURNS_DEEP_STUBS);var query=mock(CriteriaQuery.class);
            var f=new FiltroCatalogo("Alumno","TODOS",null,0,25,null,null,"VENCIMIENTO","TODOS",null," TEST_05% ");
            FiltroConceptoCobro.especificacion(f).toPredicate(root,query,cb);
            verify(cb).like(cb.lower(root.get("conceptoCobro").get("codigo")),"%test\\_05\\%%",'\\');
            verify(cb).like(cb.lower(root.get("conceptoCobro").get("nombre")),"%test\\_05\\%%",'\\');
            verifyNoInteractions(query);
        }
    }
}
