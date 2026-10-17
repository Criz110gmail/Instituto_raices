package escuela.compras.service;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.service.MotivoMovimientoBusquedaService;
import escuela.compras.entity.Proveedor;
import escuela.compras.repository.ProveedorRepository;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.*;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.stream.LongStream;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompraBusquedaServiceTest {
    @Test void filtroProveedorIncluyeInactivosYDevuelveDiezSinCargarTodo() {
        var datos=LongStream.rangeClosed(1,11).mapToObj(id->{var p=new Proveedor();p.setId(id);p.setRazonSocial("Proveedor "+id);p.setActivo(false);return p;}).toList();
        when(proveedores.findAll(any(Specification.class),any(Pageable.class))).thenAnswer(inv->{
            Specification<Proveedor> spec=inv.getArgument(0);var root=mock(jakarta.persistence.criteria.Root.class,RETURNS_DEEP_STUBS);var cb=mock(jakarta.persistence.criteria.CriteriaBuilder.class,RETURNS_DEEP_STUBS);
            spec.toPredicate(root,null,cb);verify(cb).equal(root.get("institucion").get("id"),1L);verify(cb,never()).isTrue(any());
            return new PageImpl<>(datos);
        });
        var r=service.proveedoresFiltro(1L,"");assertThat(r.resultados()).hasSize(10);assertThat(r.hayMas()).isTrue();assertThat(r.resultados().getFirst().detalle()).contains("Inactivo");
        verify(proveedores).findAll(any(Specification.class),argThat((Pageable p)->p.getPageSize()==11));
    }
    @Test void filtroCuentaTodosNoExigePlantelNiActivoPeroConservaAlcance() {
        when(alcance.<CuentaFinanciera>especificacion(ModuloCatalogo.CUENTAS_FINANCIERAS)).thenReturn((r,q,cb)->cb.conjunction());
        when(cuentas.findAll(any(Specification.class),any(Pageable.class))).thenAnswer(inv->{
            Specification<CuentaFinanciera> spec=inv.getArgument(0);var root=mock(jakarta.persistence.criteria.Root.class,RETURNS_DEEP_STUBS);var cb=mock(jakarta.persistence.criteria.CriteriaBuilder.class,RETURNS_DEEP_STUBS);
            spec.toPredicate(root,null,cb);verify(cb).equal(root.get("institucion").get("id"),1L);verify(cb,never()).isTrue(any());verify(cb,never()).isNull(any());return Page.empty();
        });
        service.cuentasFiltro(1L,null,"");verify(alcance).especificacion(ModuloCatalogo.CUENTAS_FINANCIERAS);verify(cuentas).findAll(any(Specification.class),argThat((Pageable p)->p.getPageSize()==11));
    }
    @Test void filtrosRechazanConsultaCortaSinCargarCatalogos() {
        assertThat(service.proveedoresFiltro(1L,"ab").resultados()).isEmpty();assertThat(service.cuentasFiltro(1L,2L,"a").resultados()).isEmpty();verifyNoInteractions(proveedores,cuentas);
    }
    private final ProveedorRepository proveedores=mock(ProveedorRepository.class);
    private final CuentaFinancieraRepository cuentas=mock(CuentaFinancieraRepository.class);
    private final MotivoFinancieroRepository motivos=mock(MotivoFinancieroRepository.class);
    private final AlcanceDatosService alcance=mock(AlcanceDatosService.class);
    private final MotivoMovimientoBusquedaService categorias=mock(MotivoMovimientoBusquedaService.class);
    private final CompraBusquedaService service=new CompraBusquedaService(proveedores,cuentas,motivos,categorias,alcance);

    @Test void proveedoresDevuelveDiezConOrdenEstableYLimiteEnBase() {
        var datos=LongStream.rangeClosed(1,11).mapToObj(id->{var p=new Proveedor();p.setId(id);p.setRazonSocial("Proveedor "+id);return p;}).toList();
        when(proveedores.findAll(any(Specification.class),any(Pageable.class))).thenReturn(new PageImpl<>(datos));
        var r=service.proveedores(1L,"");assertThat(r.resultados()).hasSize(10);assertThat(r.hayMas()).isTrue();
        verify(alcance).validarInstitucion(1L);
        verify(proveedores).findAll(any(Specification.class),argThat((Pageable p)->p.getPageSize()==11&&p.getSort().getOrderFor("id")!=null));
    }
    @Test void cuentasUsaAlcancePlantelYPaginaLimitada() {
        Specification<CuentaFinanciera> base=(r,q,cb)->cb.conjunction();
        when(alcance.<CuentaFinanciera>especificacion(ModuloCatalogo.CUENTAS_FINANCIERAS)).thenReturn(base);
        when(cuentas.findAll(any(Specification.class),any(Pageable.class))).thenReturn(Page.empty());
        service.cuentas(1L,2L,"");verify(alcance).validarInstitucion(1L);verify(alcance).validarPlantel(2L);
        verify(cuentas).findAll(any(Specification.class),argThat((Pageable p)->p.getPageSize()==11));
    }
    @Test void consultaCortaNoConsultaCatalogosYMotivosExigenEgreso() {
        assertThat(service.proveedores(1L,"ab").resultados()).isEmpty();assertThat(service.cuentas(1L,2L,"a").resultados()).isEmpty();
        verifyNoInteractions(proveedores,cuentas);service.motivos(1L,"papel");verify(categorias).buscar(1L,DireccionMovimiento.EGRESO,"papel");
    }
    @Test void cuentasFiltraInstitucionActivoYPlantelSinPerderElAlcance() {
        when(alcance.<CuentaFinanciera>especificacion(ModuloCatalogo.CUENTAS_FINANCIERAS)).thenReturn((r,q,cb)->cb.conjunction());
        when(cuentas.findAll(any(Specification.class),any(Pageable.class))).thenAnswer(inv->{
            Specification<CuentaFinanciera> spec=inv.getArgument(0);
            var root=mock(jakarta.persistence.criteria.Root.class,RETURNS_DEEP_STUBS);
            var cb=mock(jakarta.persistence.criteria.CriteriaBuilder.class,RETURNS_DEEP_STUBS);
            spec.toPredicate(root,null,cb);verify(cb).equal(root.get("institucion").get("id"),1L);verify(cb).isTrue(root.get("activo"));
            verify(cb).equal(root.get("plantel").get("id"),2L);verify(cb).isNull(root.get("plantel"));return Page.empty();
        });service.cuentas(1L,2L,"banco");
    }
    @Test void proveedorFiltraInstitucionActivoYEscapeDeComodines() {
        when(proveedores.findAll(any(Specification.class),any(Pageable.class))).thenAnswer(inv->{
            Specification<Proveedor> spec=inv.getArgument(0);
            var root=mock(jakarta.persistence.criteria.Root.class,RETURNS_DEEP_STUBS);
            var cb=mock(jakarta.persistence.criteria.CriteriaBuilder.class,RETURNS_DEEP_STUBS);
            spec.toPredicate(root,null,cb);verify(cb).equal(root.get("institucion").get("id"),1L);verify(cb).isTrue(root.get("activo"));
            verify(cb,times(3)).like(any(),eq("%pap\\_50\\%%"),eq('\\'));return Page.empty();
        });service.proveedores(1L,"PAP_50%");
    }
}
