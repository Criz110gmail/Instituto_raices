package escuela.compras.service;

import escuela.compras.dto.*;
import escuela.compras.entity.*;
import escuela.compras.repository.*;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
import escuela.finanzas.service.*;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.*;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompraServiceTest {
    @Test void guardaBorradorConFolioTemporalDentroDelLimiteYTotalesExactosSinMoverDinero() throws Exception {
        var compras=mock(CompraRepository.class);var proveedores=mock(ProveedorRepository.class);
        var instituciones=mock(InstitucionRepository.class);var cuentas=mock(CuentaFinancieraRepository.class);
        var motivos=mock(MotivoFinancieroRepository.class);var movimientos=mock(MovimientoManualService.class);
        var i=new Institucion();i.setId(1L);i.setZonaHoraria("America/Mexico_City");
        var p=new Proveedor();p.setInstitucion(i);p.setActivo(true);
        var c=new CuentaFinanciera();c.setInstitucion(i);c.setActivo(true);
        var m=new MotivoFinanciero();m.setInstitucion(i);m.setActivo(true);m.setNaturaleza(NaturalezaMotivoFinanciero.EGRESO);
        when(instituciones.findById(1L)).thenReturn(Optional.of(i));when(proveedores.findById(2L)).thenReturn(Optional.of(p));
        when(cuentas.findById(3L)).thenReturn(Optional.of(c));when(motivos.findById(4L)).thenReturn(Optional.of(m));
        var folios=new ArrayList<String>();
        int limite=Compra.class.getDeclaredField("folio").getAnnotation(jakarta.persistence.Column.class).length();
        when(compras.saveAndFlush(any(Compra.class))).thenAnswer(inv->{Compra x=inv.getArgument(0);
            folios.add(x.getFolio());assertThat(x.getFolio()).hasSizeLessThanOrEqualTo(limite);x.setId(7L);return x;});
        var service=new CompraService(compras,proveedores,instituciones,mock(PlantelRepository.class),cuentas,motivos,
            mock(MovimientoFinancieroRepository.class),mock(ReversionFinancieraRepository.class),mock(UsuarioRepository.class),movimientos,mock(ReversionFinancieraService.class),mock(AlcanceDatosService.class));
        var f=new CompraForm();f.setInstitucionId(1L);f.setProveedorId(2L);f.setCuentaId(3L);f.setMotivoFinancieroId(4L);
        f.setFechaOperacion(LocalDateTime.of(2026,10,16,12,0));f.setPartidas(List.of(partida("2","100"),partida("3","50")));
        assertThat(service.crear(f)).isEqualTo(7L);
        assertThat(folios).hasSize(2);assertThat(folios.getFirst()).startsWith("TEMP-");assertThat(folios.getLast()).isEqualTo("COM-00000007");
        verify(compras,times(2)).saveAndFlush(argThat(x->x.getEstado()==EstadoCompra.BORRADOR
            &&x.getSubtotal().compareTo(new BigDecimal("350.00"))==0&&x.getImpuesto().compareTo(new BigDecimal("56.00"))==0
            &&x.getTotal().compareTo(new BigDecimal("406.00"))==0&&x.getMovimientoEgreso()==null));
        verifyNoInteractions(movimientos);
    }
    private CompraPartidaForm partida(String cantidad,String precio){var p=new CompraPartidaForm();p.setDescripcion("Prueba");p.setCantidad(new BigDecimal(cantidad));p.setPrecioUnitario(new BigDecimal(precio));p.setPorcentajeImpuesto(new BigDecimal("16"));return p;}
}
