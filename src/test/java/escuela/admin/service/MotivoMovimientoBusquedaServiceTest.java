package escuela.admin.service;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.MotivoFinancieroRepository;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class MotivoMovimientoBusquedaServiceTest {
    @Test void primerasDiezPaginadasSinCargarCatalogoYConAlcance() {
        var repo=mock(MotivoFinancieroRepository.class);var alcance=mock(AlcanceDatosService.class);
        var motivos=java.util.stream.LongStream.rangeClosed(1,11).mapToObj(id->{var m=new MotivoFinanciero();m.setId(id);m.setCodigo("M-"+id);m.setNombre("Motivo"+id);m.setCategoria("Pruebas");m.setNaturaleza(NaturalezaMotivoFinanciero.AMBOS);return m;}).toList();
        when(repo.findAll(any(Specification.class),any(Pageable.class))).thenReturn(new PageImpl<>(motivos,PageRequest.of(0,11),11));
        var r=new MotivoMovimientoBusquedaService(repo,alcance).buscar(1L,DireccionMovimiento.INGRESO,"");
        assertThat(r.resultados()).hasSize(10);assertThat(r.hayMas()).isTrue();
        assertThat(r.resultados().getFirst().titulo()).isEqualTo("M-1 · Motivo1");
        verify(alcance).validarInstitucion(1L);verify(repo).findAll(any(Specification.class),argThat((Pageable p)->p.getPageSize()==11&&p.getPageNumber()==0));
        verify(repo,never()).findAll();
    }
    @Test void consultaCortaNoCargaBaseYPermisoSeCompruebaAntes() {
        var repo=mock(MotivoFinancieroRepository.class);var alcance=mock(AlcanceDatosService.class);
        assertThat(new MotivoMovimientoBusquedaService(repo,alcance).buscar(1L,DireccionMovimiento.EGRESO,"ab").resultados()).isEmpty();
        verify(alcance).validarInstitucion(1L);verifyNoInteractions(repo);
    }
    @Test void criterioRestringeInstitucionActivoNaturalezaYBuscaTresCampos() {
        var repo=mock(MotivoFinancieroRepository.class);var alcance=mock(AlcanceDatosService.class);
        when(repo.findAll(any(Specification.class),any(Pageable.class))).thenAnswer(i->{
            Specification<MotivoFinanciero> spec=i.getArgument(0);
            var root=mock(jakarta.persistence.criteria.Root.class,RETURNS_DEEP_STUBS);
            var cb=mock(jakarta.persistence.criteria.CriteriaBuilder.class,RETURNS_DEEP_STUBS);
            spec.toPredicate(root,null,cb);
            verify(cb).equal(root.get("institucion").get("id"),1L);verify(cb).isTrue(root.get("activo"));
            verify(root.get("naturaleza")).in(NaturalezaMotivoFinanciero.AMBOS,NaturalezaMotivoFinanciero.EGRESO);
            verify(cb,times(3)).like(any(),eq("%lim\\_50\\%%"),eq('\\'));
            return Page.empty();
        });
        new MotivoMovimientoBusquedaService(repo,alcance).buscar(1L,DireccionMovimiento.EGRESO,"LIM_50%");
    }
}
