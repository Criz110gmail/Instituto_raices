package escuela.auditoria.service;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.entity.Auditoria;
import escuela.auditoria.repository.AuditoriaRepository;
import escuela.auditoria.support.CorrelacionAuditoria;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.service.UsuarioPrincipal;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RegistroAuditoriaServiceTest {
    private final AuditoriaRepository repository = mock(AuditoriaRepository.class);
    private final EntityManager entityManager = mock(EntityManager.class);
    private final RegistroAuditoriaService service = new RegistroAuditoriaService(repository, entityManager);

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
        CorrelacionAuditoria.limpiar();
    }

    @Test
    void registraActorAutenticadoYOmiteDatosSensibles() {
        Institucion institucion = new Institucion();
        Usuario usuario = new Usuario();
        when(entityManager.getReference(Institucion.class, 3L)).thenReturn(institucion);
        when(entityManager.getReference(Usuario.class, 27L)).thenReturn(usuario);
        UsuarioPrincipal principal = new UsuarioPrincipal(27L, 3L, Set.of(), true,
                false, "operador", "hash", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
        Map<String, Object> cambios = new LinkedHashMap<>();
        cambios.put("estado", "VALIDADO");
        cambios.put("password", "no-debe-guardarse");
        cambios.put("numeroCuenta", "no-debe-guardarse");

        service.registrar(3L, AccionAuditoria.PAGO_VALIDADO, "PAGO", 91L, null, cambios);

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(repository).save(captor.capture());
        Auditoria registro = captor.getValue();
        assertThat(registro.getInstitucion()).isSameAs(institucion);
        assertThat(registro.getUsuarioActor()).isSameAs(usuario);
        assertThat(registro.getActorSistema()).isNull();
        assertThat(registro.getCambios()).isEqualTo("{\"estado\":\"VALIDADO\"}");
        assertThat(registro.getCorrelacionId()).isNotBlank();
    }

    @Test
    void identificaElAccesoDeRecuperacionSinInventarUnUsuario() {
        Institucion institucion = new Institucion();
        when(entityManager.getReference(Institucion.class, 3L)).thenReturn(institucion);
        UsuarioPrincipal principal = new UsuarioPrincipal(null, null, Set.of(), true,
                true, "recuperacion", "hash", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        service.registrar(3L, AccionAuditoria.AVISO_RETIRADO, "AVISO", 8L,
                " contenido retirado ", Map.of());

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(repository).save(captor.capture());
        Auditoria registro = captor.getValue();
        assertThat(registro.getUsuarioActor()).isNull();
        assertThat(registro.getActorSistema()).isEqualTo("ACCESO_RECUPERACION");
        assertThat(registro.getMotivo()).isEqualTo("contenido retirado");
        verify(entityManager, never()).getReference(eq(Usuario.class), anyLong());
    }
}
