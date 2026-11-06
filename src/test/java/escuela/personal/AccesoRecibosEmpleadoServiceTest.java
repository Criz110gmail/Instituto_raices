package escuela.personal;
import escuela.personal.dto.AccesoRecibosForm;
import escuela.personal.entity.Empleado;
import escuela.personal.repository.EmpleadoRepository;
import escuela.personal.service.*;
import escuela.docente.entity.Maestro;
import escuela.docente.repository.MaestroRepository;
import escuela.seguridad.entity.*;
import escuela.seguridad.repository.*;
import escuela.seguridad.service.*;
import escuela.institucion.entity.Institucion;
import escuela.auditoria.service.RegistroAuditoriaService;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class AccesoRecibosEmpleadoServiceTest {
 private final EmpleadoService personal=mock(EmpleadoService.class);
 private final EmpleadoRepository empleados=mock(EmpleadoRepository.class);
 private final MaestroRepository maestros=mock(MaestroRepository.class);
 private final UsuarioRepository usuarios=mock(UsuarioRepository.class);
 private final InvitacionUsuarioService invitaciones=mock(InvitacionUsuarioService.class);
 private final RecuperacionPasswordService recuperaciones=mock(RecuperacionPasswordService.class);
 private final RegistroAuditoriaService auditoria=mock(RegistroAuditoriaService.class);
 private final AccesoRecibosEmpleadoService s=new AccesoRecibosEmpleadoService(personal,empleados,maestros,usuarios,invitaciones,recuperaciones,mock(InvitacionUsuarioRepository.class),mock(RecuperacionPasswordRepository.class),auditoria);
 private Empleado e;private Usuario u;
 @BeforeEach void preparar(){var i=new Institucion();i.setId(1L);i.setActivo(true);i.setZonaHoraria("America/Mexico_City");e=new Empleado();e.setId(7L);e.setInstitucion(i);e.setNombres("Persona");e.setPrimerApellido("Ficticia");e.setCodigo("EMP-QA");e.setVersion(0L);u=new Usuario();u.setId(9L);u.setInstitucion(i);u.setUsername("persona.ficticia");u.setTipoCuenta(TipoCuentaUsuario.PORTAL_EMPLEADO);u.setEstado(EstadoUsuario.ACTIVO);when(personal.autorizar(7L,false,false)).thenReturn(e);when(empleados.bloquear(7L)).thenReturn(Optional.of(e));when(empleados.saveAndFlush(any())).thenAnswer(x->x.getArgument(0));}
 @AfterEach void limpiar(){SecurityContextHolder.clearContext();}
 private AccesoRecibosForm form(String accion){var f=new AccesoRecibosForm();f.setVersion(0L);f.setAccion(accion);f.setConfirmado(true);return f;}
 private void sesion(){var p=new UsuarioPrincipal(9L,1L,Set.of(),false,false,"persona.ficticia","",List.of());SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p,"",List.of()));when(empleados.findByUsuarioPortalIdAndInstitucionId(9L,1L)).thenReturn(Optional.of(e));}
 @Test void crearExigeAmbasAutorizacionesYNoConcedeRoles(){var f=form("HABILITAR");f.setUsername("persona.ficticia");f.setEmail("personal@example.invalid");when(usuarios.saveAndFlush(any())).thenAnswer(x->{Usuario nuevo=x.getArgument(0);nuevo.setId(9L);return nuevo;});when(invitaciones.emitir(9L,Duration.ofHours(48))).thenReturn(new escuela.seguridad.dto.response.InvitacionEmitidaResponse("enlace-ficticio",Instant.now().plusSeconds(120)));var enlace=s.gestionar(7L,f);assertThat(enlace.activacion()).isTrue();assertThat(e.isPortalRecibosHabilitado()).isTrue();assertThat(e.getUsuarioPortal().getTipoCuenta()).isEqualTo(TipoCuentaUsuario.PORTAL_EMPLEADO);verify(personal).autorizarSalarios(true);verify(personal).autorizar(7L,false,true);verify(auditoria).registrar(eq(1L),any(),eq("EMPLEADO"),eq(7L),anyString(),anyMap());}
 @Test void reutilizaCuentaDocenteSinNuevaCuentaNiCambiarClave(){u.setTipoCuenta(TipoCuentaUsuario.PORTAL_MAESTRO);var m=new Maestro();m.setEmpleado(e);m.setUsuario(u);when(maestros.findByEmpleadoId(7L)).thenReturn(Optional.of(m));assertThat(s.gestionar(7L,form("HABILITAR"))).isNull();assertThat(e.getUsuarioPortal()).isSameAs(u);verify(usuarios,never()).saveAndFlush(any());verifyNoInteractions(invitaciones,recuperaciones);}
 @Test void desactivarRecibosNoDesactivaDocente(){u.setTipoCuenta(TipoCuentaUsuario.PORTAL_MAESTRO);e.setUsuarioPortal(u);e.setPortalRecibosHabilitado(true);s.gestionar(7L,form("DESHABILITAR"));assertThat(e.isPortalRecibosHabilitado()).isFalse();assertThat(u.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);verify(usuarios,never()).saveAndFlush(any());}
 @Test void sesionAntiguaSeBloqueaAlDesactivarPermisoPersonal(){e.setUsuarioPortal(u);e.setPortalRecibosHabilitado(true);sesion();assertThat(s.identidadActual().empleadoId()).isEqualTo(7L);e.setPortalRecibosHabilitado(false);assertThatThrownBy(s::identidadActual).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);}
 @Test void institucionAjenaNoResuelveIdentidad(){e.setUsuarioPortal(u);e.setPortalRecibosHabilitado(true);var p=new UsuarioPrincipal(9L,2L,Set.of(),false,false,"persona.ficticia","",List.of());SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p,"",List.of()));assertThatThrownBy(s::identidadActual).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);}
 @Test void sinConfirmacionNoGuardaNiEmiteEnlaces(){var f=form("HABILITAR");f.setConfirmado(false);assertThatThrownBy(()->s.gestionar(7L,f)).isInstanceOf(escuela.common.exception.ReglaNegocioException.class);verify(usuarios,never()).saveAndFlush(any());verify(empleados,never()).saveAndFlush(any());verifyNoInteractions(invitaciones,recuperaciones);}
 @Test void enlaceCompartidoExigeAdministrarMaestros(){u.setTipoCuenta(TipoCuentaUsuario.PORTAL_MAESTRO);e.setUsuarioPortal(u);e.setPortalRecibosHabilitado(true);assertThatThrownBy(()->s.gestionar(7L,form("ENLACE"))).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);verifyNoInteractions(invitaciones,recuperaciones);}
}
