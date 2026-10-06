package escuela.docente.service;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.docente.entity.Maestro;
import escuela.docente.repository.MaestroRepository;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.*;
import escuela.seguridad.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SoportePortalMaestroServiceTest {
    private final MaestroRepository maestros = mock(MaestroRepository.class);
    private final AlcanceDatosService alcance = mock(AlcanceDatosService.class);
    private final RegistroAuditoriaService auditoria = mock(RegistroAuditoriaService.class);
    private final SoportePortalMaestroService service = new SoportePortalMaestroService(maestros, alcance, auditoria);

    private UsuarioPrincipal admin(boolean institucional, String permiso) {
        return new UsuarioPrincipal(1L, 2L, Set.of(), institucional, false, "administrador", "",
                List.of(new SimpleGrantedAuthority(permiso)));
    }
    private Maestro maestro() {
        var institucion = new Institucion(); institucion.setId(2L);
        var usuario = new Usuario(); usuario.setId(9L); usuario.setInstitucion(institucion);
        usuario.setEstado(EstadoUsuario.ACTIVO); usuario.setTipoCuenta(TipoCuentaUsuario.PORTAL_MAESTRO);
        usuario.setUsername("docente");
        var m = new Maestro(); m.setId(7L); m.setInstitucion(institucion); m.setUsuario(usuario);
        m.setNumeroEmpleado("M-1"); m.setNombres("Ana"); m.setPrimerApellido("Prueba");
        return m;
    }

    @Test void contextoConservaLaSesionYAuditaSinAsignarPermisosAdministrativosAlDocente() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        when(maestros.findById(7L)).thenReturn(Optional.of(maestro()));
        var docente = service.contexto(admin(true, "PORTAL_MAESTRO_SOPORTE"), 7L, "ALUMNOS");
        assertThat(docente.usuarioId()).isEqualTo(9L);
        assertThat(docente.password()).isEmpty();
        assertThat(docente.authorities()).extracting(a -> a.getAuthority()).containsExactly("PORTAL_MAESTRO_ACCEDER");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(autenticacion);
        verify(auditoria).registrar(eq(2L), eq(AccionAuditoria.PORTAL_MAESTRO_SOPORTE), eq("MAESTRO"), eq(7L), anyString(), eq(Map.of("seccion", "ALUMNOS")));
    }

    @Test void rechazaOtraInstitucionSinAuditarUnaConsultaAutorizada() {
        var m = maestro(); var otra = new Institucion(); otra.setId(5L); m.setInstitucion(otra);
        when(maestros.findById(7L)).thenReturn(Optional.of(m));
        assertThatThrownBy(() -> service.contexto(admin(true, "PORTAL_MAESTRO_SOPORTE"), 7L, "INICIO"))
                .isInstanceOf(AccessDeniedException.class);
        verifyNoInteractions(auditoria);
    }

    @Test void rechazaCuentaDeOtroTipoOCuentaInactiva() {
        var m = maestro(); when(maestros.findById(7L)).thenReturn(Optional.of(m));
        m.getUsuario().setTipoCuenta(TipoCuentaUsuario.ADMINISTRATIVO);
        assertThatThrownBy(() -> service.contexto(admin(true, "PORTAL_MAESTRO_SOPORTE"), 7L, "INICIO")).isInstanceOf(AccessDeniedException.class);
        m.getUsuario().setTipoCuenta(TipoCuentaUsuario.PORTAL_MAESTRO); m.getUsuario().setEstado(EstadoUsuario.INACTIVO);
        assertThatThrownBy(() -> service.contexto(admin(true, "PORTAL_MAESTRO_SOPORTE"), 7L, "INICIO")).isInstanceOf(AccessDeniedException.class);
        verifyNoInteractions(auditoria);
    }

    @Test void noAceptaPermisoFamiliarNiAlcanceDePlantel() {
        assertThatThrownBy(() -> service.listar(admin(true, "PORTAL_TUTOR_SOPORTE"), "", 0, 25)).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> service.listar(admin(false, "PORTAL_MAESTRO_SOPORTE"), "", 0, 25)).isInstanceOf(AccessDeniedException.class);
        verifyNoInteractions(maestros);
    }

    @Test void noAceptaRecuperacionNiCuentaDocenteComoAdministrador() {
        var recuperacion = new UsuarioPrincipal(null, null, Set.of(), true, true, "recuperacion", "",
                List.of(new SimpleGrantedAuthority("PORTAL_MAESTRO_SOPORTE")));
        assertThatThrownBy(() -> service.listar(recuperacion, "", 0, 25)).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> service.listar(admin(true, "PORTAL_MAESTRO_ACCEDER"), "", 0, 25)).isInstanceOf(AccessDeniedException.class);
        verifyNoInteractions(maestros);
    }

    @Test void paginaEnBaseDeDatosConTamanioAcotado() {
        when(maestros.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(maestro())));
        var resultado = service.listar(admin(true, "PORTAL_MAESTRO_SOPORTE"), "Ana", -2, 10000);
        assertThat(resultado.getContent().getFirst().usuario()).isEqualTo("docente");
        verify(maestros).findAll(any(org.springframework.data.jpa.domain.Specification.class),
                eq(PageRequest.of(0, 25, Sort.by("primerApellido", "nombres", "id"))));
    }
}
