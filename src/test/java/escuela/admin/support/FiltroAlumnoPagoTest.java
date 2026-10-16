package escuela.admin.support;

import escuela.admin.dto.FiltroCatalogo;
import escuela.cobranza.entity.AcuerdoAnticipadoCargo;
import escuela.finanzas.entity.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class FiltroAlumnoPagoTest {
    @Test void conservaAlumnoEnNormalizacionPaginasYRetornoYRechazaTextoSinSeleccion() {
        var f = new FiltroCatalogo("", "TODOS", null, 0, 25, null, null, "REGISTRO", "TODOS", null, "", 2L, " Martha ").normalizado();
        assertThat(f.conPagina(2).alumnoId()).isEqualTo(2L);
        assertThat(f.conPagina(2).alumnoTexto()).isEqualTo("Martha");
        assertThat(RetornoListadoCobranza.listado(escuela.admin.dto.ModuloCatalogo.PAGOS,f)).contains("alumnoId=2", "alumnoTexto=Martha");
        assertThatThrownBy(() -> new FiltroCatalogo("", "TODOS", null, 0, 25, null, null, "REGISTRO", "TODOS", null, "", null, "Martha").normalizado().validarCobertura()).hasMessageContaining("Selecciona un alumno");
        assertThat(new FiltroCatalogo("", "TODOS", 0, 25).normalizado().alumnoId()).isNull();
    }
    @Test void sinAlumnoNoAgregaSubconsultas() {
        var cb=mock(CriteriaBuilder.class);var query=mock(CriteriaQuery.class);
        FiltroAlumnoPago.especificacion(null).toPredicate(mock(Root.class),query,cb);
        verify(cb).conjunction();verifyNoInteractions(query);
    }
    @Test void tresFuentesHistoricasConExistsSinDuplicarNiFiltrarPorEstado() {
        var cb=mock(CriteriaBuilder.class,RETURNS_DEEP_STUBS);var query=mock(CriteriaQuery.class);
        var pago=mock(Root.class,RETURNS_DEEP_STUBS);var solicitud=mock(Root.class,RETURNS_DEEP_STUBS);
        var aplicacion=mock(Root.class,RETURNS_DEEP_STUBS);var detalle=mock(Root.class,RETURNS_DEEP_STUBS);
        var s=mock(Subquery.class,RETURNS_SELF);var a=mock(Subquery.class,RETURNS_SELF);var d=mock(Subquery.class,RETURNS_SELF);
        when(query.subquery(Integer.class)).thenReturn(s,a,d);
        when(s.from(SolicitudAplicacionPago.class)).thenReturn(solicitud);
        when(a.from(AplicacionPago.class)).thenReturn(aplicacion);
        when(d.from(AcuerdoAnticipadoCargo.class)).thenReturn(detalle);
        FiltroAlumnoPago.especificacion(2L).toPredicate(pago,query,cb);
        verify(cb).equal(solicitud.get("pago"),pago);verify(cb).equal(aplicacion.get("pago"),pago);
        verify(cb).equal(detalle.get("acuerdo").get("id"),pago.get("acuerdoAnticipadoId"));
        verify(cb).exists(s);verify(cb).exists(a);verify(cb).exists(d);
        verify(cb).equal(eq(solicitud.get("cargo").get("inscripcion").get("alumno").get("id")), any(Expression.class));
        verify(query,never()).distinct(anyBoolean());verify(pago,never()).get("estado");
    }
}
