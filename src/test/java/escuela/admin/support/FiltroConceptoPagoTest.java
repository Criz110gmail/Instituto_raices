package escuela.admin.support;

import escuela.admin.dto.FiltroCatalogo;
import escuela.cobranza.entity.AcuerdoAnticipadoCargo;
import escuela.finanzas.entity.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class FiltroConceptoPagoTest {
    @Test void conservaFiltroNormalizadoEnPaginasYCompatibilidadDeConstructores() {
        var f=new FiltroCatalogo("","TODOS",null,0,25,null,null,"REGISTRO","TODOS",null,"  ANTICIPO-TEST-05  ").normalizado();
        assertThat(f.conPagina(2).conceptoCobro()).isEqualTo("ANTICIPO-TEST-05");
        assertThat(new FiltroCatalogo("","TODOS",0,25).normalizado().conceptoCobro()).isEmpty();
    }
    @Test void sinConceptoNoAgregaSubconsultas() {
        var cb=mock(CriteriaBuilder.class);
        var query=mock(CriteriaQuery.class);
        FiltroConceptoPago.especificacion(new FiltroCatalogo("","TODOS",0,25)).toPredicate(mock(Root.class),query,cb);
        verify(cb).conjunction();verifyNoInteractions(query);
    }
    @Test void consultaTresFuentesConExistsSinMultiplicarPagosYEscapaComodines() {
        var cb=mock(CriteriaBuilder.class,RETURNS_DEEP_STUBS);
        var query=mock(CriteriaQuery.class);
        var pago=mock(Root.class,RETURNS_DEEP_STUBS);
        var solicitud=mock(Root.class,RETURNS_DEEP_STUBS);
        var aplicacion=mock(Root.class,RETURNS_DEEP_STUBS);
        var detalle=mock(Root.class,RETURNS_DEEP_STUBS);
        var s=mock(Subquery.class,RETURNS_SELF);var a=mock(Subquery.class,RETURNS_SELF);var d=mock(Subquery.class,RETURNS_SELF);
        when(query.subquery(Integer.class)).thenReturn(s,a,d);
        when(s.from(SolicitudAplicacionPago.class)).thenReturn(solicitud);
        when(a.from(AplicacionPago.class)).thenReturn(aplicacion);
        when(d.from(AcuerdoAnticipadoCargo.class)).thenReturn(detalle);
        var f=new FiltroCatalogo("","TODOS",null,0,25,null,null,"REGISTRO","TODOS",null,"TEST_05%");
        FiltroConceptoPago.especificacion(f).toPredicate(pago,query,cb);
        verify(cb).equal(solicitud.get("pago"),pago);verify(cb).equal(aplicacion.get("pago"),pago);
        verify(cb).equal(detalle.get("acuerdo").get("id"),pago.get("acuerdoAnticipadoId"));
        verify(cb).exists(s);verify(cb).exists(a);verify(cb).exists(d);
        verify(cb,times(6)).like(any(),eq("%test\\_05\\%%"),eq('\\'));
        verify(query,never()).distinct(anyBoolean());
    }
}
