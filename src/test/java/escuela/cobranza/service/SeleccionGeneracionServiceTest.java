package escuela.cobranza.service;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import escuela.seguridad.service.UsuarioPrincipal;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class SeleccionGeneracionServiceTest {
    NamedParameterJdbcTemplate jdbc=mock(NamedParameterJdbcTemplate.class);
    SeleccionGeneracionService service=new SeleccionGeneracionService(jdbc);
    UUID token=UUID.randomUUID();LocalDate corte=LocalDate.of(2026,10,6);
    @BeforeEach void iniciar(){var p=new UsuarioPrincipal(1L,1L,Set.of(),true,false,"admin","",List.of(new SimpleGrantedAuthority("CARGO_ADMINISTRAR")));SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p,"",p.authorities()));}
    @AfterEach void limpiar(){SecurityContextHolder.clearContext();}
    Map<String,Object> header(){return new HashMap<>(Map.of("usuario_id",1L,"tipo","CARGOS","institucion_id",1L,"fecha_corte",java.sql.Date.valueOf(corte),"completa",true,"utilizada",false,"vigente",true));}
    void mockHeader(Map<String,Object> h){when(jdbc.queryForList(anyString(),org.mockito.ArgumentMatchers.<Map<String,?>>any())).thenReturn(List.of(h));}
    @Test void impideOtroUsuarioOtroCorteYCaducidad(){var h=header();h.put("usuario_id",2L);mockHeader(h);assertThatThrownBy(()->service.preparar(token,"CARGOS",1L,null,corte)).hasMessageContaining("tu usuario");h.put("usuario_id",1L);assertThatThrownBy(()->service.preparar(token,"CARGOS",1L,null,corte.plusDays(1))).hasMessageContaining("filtros");h.put("vigente",false);assertThatThrownBy(()->service.preparar(token,"CARGOS",1L,null,corte)).hasMessageContaining("caducó");}
    @Test void vistaConsumidaNoSePuedeRepetir(){var h=header();h.put("utilizada",true);mockHeader(h);assertThatThrownBy(()->service.confirmar(token,"CARGOS",1L,null,corte,Set.of(),null)).hasMessageContaining("confirmada");}
    @Test void seleccionVaciaSeRechazaAntesDeGenerar(){mockHeader(header());assertThatThrownBy(()->service.confirmar(token,"CARGOS",1L,null,corte,Set.of(),Set.of())).hasMessageContaining("al menos un registro");}
    @Test void claveAjenaSeRechaza(){mockHeader(header());when(jdbc.queryForObject(anyString(),org.mockito.ArgumentMatchers.<Map<String,?>>any(),eq(Long.class))).thenReturn(0L);assertThatThrownBy(()->service.confirmar(token,"CARGOS",1L,null,corte,Set.of(),Set.of("AJENA"))).hasMessageContaining("no corresponde");}
    @Test void cambiosDeImporteVersionVencimientoOCantidadExigenOtraVista(){var i=new SeleccionGeneracionService.Item("K",0L,new BigDecimal("50"),corte);assertThatThrownBy(()->service.comprobar(i,0L,new BigDecimal("80"),corte)).hasMessageContaining("Cambió");assertThatThrownBy(()->service.comprobar(i,1L,new BigDecimal("50"),corte)).hasMessageContaining("Cambió");assertThatThrownBy(()->service.comprobar(i,0L,new BigDecimal("50"),corte.plusDays(1))).hasMessageContaining("Cambió");assertThatThrownBy(()->service.comprobarCantidad(2,1)).hasMessageContaining("La lista cambió");}
    @Test void modoTodosOModoIndividualNoSeConfunden(){assertThat(SeleccionGeneracionService.solicitada("K",Set.of(),null)).isTrue();assertThat(SeleccionGeneracionService.solicitada("K",Set.of("K"),null)).isFalse();assertThat(SeleccionGeneracionService.solicitada("K",Set.of(),Set.of())).isFalse();assertThat(SeleccionGeneracionService.solicitada("K",Set.of(),Set.of("K"))).isTrue();}
    @Test void confirmaSoloCantidadRevisadaEnModoTodosMenosExcluidos(){mockHeader(header());when(jdbc.queryForObject(anyString(),org.mockito.ArgumentMatchers.<Map<String,?>>any(),eq(Long.class))).thenAnswer(i->((String)i.getArgument(0)).contains("clave IN")?1L:3L);assertThat(service.confirmar(token,"CARGOS",1L,null,corte,Set.of("K"),null)).isEqualTo(2L);}
}
