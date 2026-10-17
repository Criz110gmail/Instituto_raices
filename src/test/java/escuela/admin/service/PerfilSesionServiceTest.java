package escuela.admin.service;

import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.*;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerfilSesionServiceTest {
 private final UsuarioRepository usuarios=mock(UsuarioRepository.class);
 private final PerfilSesionService service=new PerfilSesionService(usuarios);
 private UsuarioPrincipal principal(boolean institucional,Set<Long> planteles) {
  return new UsuarioPrincipal(7L,1L,planteles,institucional,false,"usuario.prueba","hash-no-publicar",List.of());
 }
 private Usuario usuario() {
  var institucion=new Institucion();institucion.setId(1L);institucion.setNombre("Institución de prueba");
  var u=new Usuario();u.setId(7L);u.setInstitucion(institucion);u.setUsername("usuario.prueba");
  u.setEmail("usuario@example.com");u.setPasswordHash("hash-no-publicar");u.setEstado(EstadoUsuario.ACTIVO);
  when(usuarios.findById(7L)).thenReturn(Optional.of(u));return u;
 }
 @Test void consultaSoloSuIdentidadSinListarUsuariosNiExponerCredenciales() {
  usuario();var resumen=service.actual(principal(true,Set.of()));
  assertThat(resumen.usuario()).isEqualTo("usuario.prueba");
  assertThat(resumen.correo()).isEqualTo("usuario@example.com");
  assertThat(resumen.institucion()).isEqualTo("Institución de prueba");
  assertThat(resumen.acceso()).isEqualTo("Administración");
  assertThat(resumen.alcance()).isEqualTo("Toda la institución");
  assertThat(resumen.toString()).doesNotContain("hash-no-publicar");
  verify(usuarios).findById(7L);verifyNoMoreInteractions(usuarios);
 }
 @Test void alcanceLimitadoMuestraCantidadSinInventarAccesoInstitucional() {
  usuario();
  assertThat(service.actual(principal(false,Set.of(2L))).alcance()).isEqualTo("1 plantel autorizado");
  assertThat(service.actual(principal(false,Set.of(2L,3L))).alcance()).isEqualTo("2 planteles autorizados");
  assertThat(service.actual(principal(false,Set.of())).alcance()).isEqualTo("Sin planteles asignados");
 }
 @Test void recuperacionNoConsultaBaseNiExponeConfiguracionPrivada() {
  var p=new UsuarioPrincipal(null,null,Set.of(),true,true,"recuperacion","hash-no-publicar",List.of());
  var resumen=service.actual(p);
  assertThat(resumen.acceso()).isEqualTo("Acceso de recuperación");assertThat(resumen.correo()).isEmpty();
  verifyNoInteractions(usuarios);
 }
 @Test void rechazaUsuarioInactivoInexistenteOInstitucionDistinta() {
  var u=usuario();u.setEstado(EstadoUsuario.INACTIVO);
  assertThatThrownBy(()->service.actual(principal(true,Set.of()))).isInstanceOf(AccessDeniedException.class);
  u.setEstado(EstadoUsuario.ACTIVO);u.getInstitucion().setId(2L);
  assertThatThrownBy(()->service.actual(principal(true,Set.of()))).isInstanceOf(AccessDeniedException.class);
  when(usuarios.findById(7L)).thenReturn(Optional.empty());
  assertThatThrownBy(()->service.actual(principal(true,Set.of()))).isInstanceOf(AccessDeniedException.class);
 }
 @Test void rechazaPrincipalAusenteOIdentidadSinId() {
  assertThatThrownBy(()->service.actual(null)).isInstanceOf(AccessDeniedException.class);
  assertThatThrownBy(()->service.actual(new UsuarioPrincipal(null,1L,Set.of(),false,false,"qa","",List.of())))
   .isInstanceOf(AccessDeniedException.class);
  verifyNoInteractions(usuarios);
 }
}
