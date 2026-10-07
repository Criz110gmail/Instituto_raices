package escuela.portal.support;
import escuela.admin.dto.PlantelForm;
import escuela.institucion.entity.*;
import escuela.institucion.mapper.PlantelMapper;
import escuela.institucion.repository.*;
import escuela.institucion.service.impl.PlantelServiceImpl;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.auditoria.entity.AccionAuditoria;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class PlantelTransferenciasServiceTest {
    PlantelRepository repository=mock(PlantelRepository.class);
    RegistroAuditoriaService auditoria=mock(RegistroAuditoriaService.class);
    PlantelServiceImpl service=new PlantelServiceImpl(repository,mock(InstitucionRepository.class),new PlantelMapper(),auditoria);
    Plantel plantel;PlantelForm form;
    @BeforeEach void preparar() {
        var institucion=new Institucion();institucion.setId(1L);
        plantel=new Plantel();plantel.setId(2L);plantel.setInstitucion(institucion);plantel.setVersion(0L);
        when(repository.findById(2L)).thenReturn(Optional.of(plantel));
        form=new PlantelForm();form.setInstitucionId(1L);form.setCodigo("CENTRO");form.setNombre("Centro");form.setVersion(0L);form.setPermitirTransferenciasVencidas(true);
        autenticar(false,Set.of(2L));
    }
    void autenticar(boolean recuperacion,Set<Long> planteles) {
        var permisos=List.of(new SimpleGrantedAuthority("PLANTEL_ADMINISTRAR"));
        var p=new UsuarioPrincipal(recuperacion?null:7L,1L,planteles,false,recuperacion,"admin","",permisos);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p,"",permisos));
    }
    @AfterEach void limpiar(){SecurityContextHolder.clearContext();}
    @Test void registraHabilitacionYRevocacionSinConcederPermisos() {
        service.actualizar(2L,form.request());assertThat(plantel.isPermitirTransferenciasVencidas()).isTrue();
        form.setPermitirTransferenciasVencidas(false);service.actualizar(2L,form.request());
        assertThat(plantel.isPermitirTransferenciasVencidas()).isFalse();
        verify(auditoria,times(2)).registrar(eq(1L),eq(AccionAuditoria.TRANSFERENCIA_VENCIDA_CONFIGURADA),eq("PLANTEL"),eq(2L),anyString(),anyMap());
    }
    @Test void bloqueaOtroPlantelYAccesoDeRecuperacion() {
        autenticar(false,Set.of(9L));assertThatThrownBy(()->service.actualizar(2L,form.request())).hasMessageContaining("alcance");
        autenticar(true,Set.of(2L));assertThatThrownBy(()->service.actualizar(2L,form.request())).hasMessageContaining("administrador identificado");
        assertThat(plantel.isPermitirTransferenciasVencidas()).isFalse();verifyNoInteractions(auditoria);
    }
}
