package escuela.admin.service;

import escuela.alumno.entity.Alumno;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class AlumnoPagoBusquedaServiceTest {
    @Test void consultaCortaNoCargaCatalogos() {
        var em=mock(EntityManager.class);var alcance=mock(AlcanceDatosService.class);
        assertThat(new AlumnoPagoBusquedaService(em,alcance).buscar("Ma").resultados()).isEmpty();
        verifyNoInteractions(em,alcance);
    }
    @Test void busquedaIndexadaEscapaComodinesLimitaResultadosEIncluyeHistoricoInactivo() {
        var em=mock(EntityManager.class);var cb=mock(CriteriaBuilder.class,RETURNS_DEEP_STUBS);
        var query=mock(CriteriaQuery.class,RETURNS_SELF);var alumno=mock(Root.class,RETURNS_DEEP_STUBS);
        var typed=mock(TypedQuery.class,RETURNS_SELF);var alcance=mock(AlcanceDatosService.class);
        when(em.getCriteriaBuilder()).thenReturn(cb);when(cb.createQuery(Alumno.class)).thenReturn(query);
        when(query.from(Alumno.class)).thenReturn(alumno);when(em.createQuery(query)).thenReturn(typed);
        when(alcance.alumnosConPagos()).thenReturn((r,q,b)->b.conjunction());
        var martha=new Alumno();martha.setId(2L);martha.setMatricula("ALUM-002");martha.setNombres("Martha");martha.setPrimerApellido("Martinez");martha.setActivo(false);
        when(typed.getResultList()).thenReturn(java.util.Collections.nCopies(21,martha));
        var resultado=new AlumnoPagoBusquedaService(em,alcance).buscar(" MARTHA_% ");
        assertThat(resultado.resultados()).hasSize(20);assertThat(resultado.hayMas()).isTrue();
        assertThat(resultado.resultados().getFirst().detalle()).contains("inactivo");
        verify(typed).setMaxResults(21);verify(cb).like(alumno.get("busquedaAutocomplete"),"%martha\\_\\%%",'\\');
        verify(alumno,never()).get("activo");verify(alcance).alumnosConPagos();
    }
}
