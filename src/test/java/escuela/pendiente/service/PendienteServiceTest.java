package escuela.pendiente.service;

import escuela.pendiente.dto.*;
import escuela.pendiente.entity.*;
import escuela.pendiente.repository.*;
import escuela.seguridad.entity.*;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.institucion.entity.Institucion;
import escuela.common.exception.ReglaNegocioException;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class PendienteServiceTest {
 final PendienteRepository repo=mock(PendienteRepository.class);
 final PendienteCambioRepository cambios=mock(PendienteCambioRepository.class);
 final UsuarioRepository usuarios=mock(UsuarioRepository.class);
 final PendienteService s=new PendienteService(repo,cambios,usuarios);
 UsuarioPrincipal principal(long id){return new UsuarioPrincipal(id,1L,Set.of(),true,false,"qa","NO-EXPONER",List.of(new SimpleGrantedAuthority("PORTAL_MAESTRO_ACCEDER")));}
 Usuario usuario(TipoCuentaUsuario tipo){var i=new Institucion();i.setId(1L);i.setZonaHoraria("America/Mexico_City");var u=new Usuario();u.setId(7L);u.setInstitucion(i);u.setEstado(EstadoUsuario.ACTIVO);u.setTipoCuenta(tipo);when(usuarios.findById(7L)).thenReturn(Optional.of(u));return u;}
 PendientePersonal propio(){usuario(TipoCuentaUsuario.ADMINISTRATIVO);var e=new PendientePersonal();e.setId(3L);e.setVersion(2L);e.setTitulo("Sólo mío");e.setFechaCompromiso(LocalDate.now().minusDays(2));when(repo.findByIdAndUsuarioIdAndUsuarioInstitucionId(3L,7L,1L)).thenReturn(Optional.of(e));return e;}
 PendienteForm form(){var f=new PendienteForm();f.setTitulo(" Preparar clase ");f.setDescripcion("Nota privada");f.setFechaCompromiso(LocalDate.now().plusDays(1));return f;}
 @Test void crearObtienePropietarioSoloDeSesionYAuditaEnHistorialPrivado(){
  var u=usuario(TipoCuentaUsuario.ADMINISTRATIVO);when(repo.saveAndFlush(any())).thenAnswer(a->{PendientePersonal e=a.getArgument(0);e.setId(3L);e.setVersion(0L);return e;});
  assertThat(s.guardar(principal(7),false,null,form())).isEqualTo(3L);
  var captor=org.mockito.ArgumentCaptor.forClass(PendientePersonal.class);verify(repo).saveAndFlush(captor.capture());
  assertThat(captor.getValue().getUsuario()).isSameAs(u);assertThat(captor.getValue().getTitulo()).isEqualTo("Preparar clase");verify(cambios).save(any(PendienteCambio.class));
 }
 @Test void otroUsuarioNoPuedeLeerEditarCambiarEstadoNiHistorial(){
  propio();var otro=usuario(TipoCuentaUsuario.ADMINISTRATIVO);otro.setId(8L);when(usuarios.findById(8L)).thenReturn(Optional.of(otro));
  assertThatThrownBy(()->s.obtener(principal(8),false,3L)).isInstanceOf(AccessDeniedException.class);
  assertThatThrownBy(()->s.guardar(principal(8),false,3L,form())).isInstanceOf(AccessDeniedException.class);
  assertThatThrownBy(()->s.estado(principal(8),false,3L,2L,EstadoPendiente.REALIZADO)).isInstanceOf(AccessDeniedException.class);
  assertThatThrownBy(()->s.historial(principal(8),false,3L,0)).isInstanceOf(AccessDeniedException.class);
  verify(repo,never()).findById(anyLong());verify(repo,never()).saveAndFlush(any());verifyNoInteractions(cambios);
 }
 @Test void familiaRecuperacionInactivoYOtraInstitucionNoTienenAgenda(){
  var u=usuario(TipoCuentaUsuario.PORTAL_TUTOR);
  assertThatThrownBy(()->s.actor(principal(7),false)).isInstanceOf(AccessDeniedException.class);
  assertThatThrownBy(()->s.actor(principal(7),true)).isInstanceOf(AccessDeniedException.class);
  u.setTipoCuenta(TipoCuentaUsuario.ADMINISTRATIVO);u.setEstado(EstadoUsuario.INACTIVO);
  assertThatThrownBy(()->s.actor(principal(7),false)).isInstanceOf(AccessDeniedException.class);
  u.setEstado(EstadoUsuario.ACTIVO);u.getInstitucion().setId(2L);
  assertThatThrownBy(()->s.actor(principal(7),false)).isInstanceOf(AccessDeniedException.class);
  assertThatThrownBy(()->s.actor(new UsuarioPrincipal(null,null,Set.of(),true,true,"bootstrap","x",List.of()),false)).isInstanceOf(AccessDeniedException.class);
 }
 @Test void maestroSoloEnSuPortalYConDerechoVigente(){
  usuario(TipoCuentaUsuario.PORTAL_MAESTRO);assertThat(s.actor(principal(7),true).getId()).isEqualTo(7L);
  assertThatThrownBy(()->s.actor(principal(7),false)).isInstanceOf(AccessDeniedException.class);
  var p=new UsuarioPrincipal(7L,1L,Set.of(),true,false,"qa","x",List.of());assertThatThrownBy(()->s.actor(p,true)).isInstanceOf(AccessDeniedException.class);
 }
 @Test void completarCancelarYReabrirConservanRegistroYFechaDeRealizacion(){
  var e=propio();s.estado(principal(7),false,3L,2L,EstadoPendiente.REALIZADO);assertThat(e.getRealizadoEn()).isNotNull();
  assertThat(PendienteVista.de(e,LocalDate.now()).vencido()).isFalse();
  s.estado(principal(7),false,3L,2L,EstadoPendiente.CANCELADO);assertThat(e.getRealizadoEn()).isNull();
  s.estado(principal(7),false,3L,2L,EstadoPendiente.PENDIENTE);assertThat(PendienteVista.de(e,LocalDate.now()).vencido()).isTrue();
  verify(repo,never()).delete(any(PendientePersonal.class));verify(cambios,times(3)).save(any());
 }
 @Test void versionObsoletaNoSobrescribeNiCambiaEstado(){
  propio();var f=form();f.setVersion(1L);assertThatThrownBy(()->s.guardar(principal(7),false,3L,f)).isInstanceOf(ReglaNegocioException.class);
  assertThatThrownBy(()->s.estado(principal(7),false,3L,1L,EstadoPendiente.REALIZADO)).isInstanceOf(ReglaNegocioException.class);verify(repo,never()).saveAndFlush(any());
 }
 @Test void filtrosPaginadosConLimitesYFechasValidas(){
  usuario(TipoCuentaUsuario.ADMINISTRATIVO);when(repo.findAll(any(Specification.class),any(Pageable.class))).thenReturn(Page.empty());
  s.listar(principal(7),false,new PendienteFiltro("texto","VENCIDOS",null,null,-1,999));
  var cap=org.mockito.ArgumentCaptor.forClass(Pageable.class);verify(repo).findAll(any(Specification.class),cap.capture());
  assertThat(cap.getValue().getPageNumber()).isZero();assertThat(cap.getValue().getPageSize()).isEqualTo(25);assertThat(cap.getValue().getSort().getOrderFor("fechaCompromiso").isAscending()).isTrue();
  assertThatThrownBy(()->s.listar(principal(7),false,new PendienteFiltro("","TODOS",LocalDate.of(2026,10,20),LocalDate.of(2026,10,1),0,25))).isInstanceOf(ReglaNegocioException.class);
 }
 @Test void avisosSoloAbiertosConTopCincoYLaboranEnZonaInstitucional(){
  usuario(TipoCuentaUsuario.ADMINISTRATIVO);var hoy=s.hoy(principal(7),false);assertThat(hoy).isEqualTo(LocalDate.now(ZoneId.of("America/Mexico_City")));
  when(repo.findAll(any(Specification.class),any(Pageable.class))).thenReturn(Page.empty());
  when(repo.countByUsuarioIdAndEstadoInAndFechaCompromisoBefore(eq(7L),anyCollection(),eq(hoy))).thenReturn(2L);
  when(repo.countByUsuarioIdAndEstadoInAndFechaCompromisoBetween(eq(7L),anyCollection(),eq(hoy),eq(hoy))).thenReturn(1L);
  when(repo.countByUsuarioIdAndEstadoInAndFechaCompromisoBetween(eq(7L),anyCollection(),eq(hoy.plusDays(1)),eq(hoy.plusDays(7)))).thenReturn(3L);
  var avisos=s.avisos(principal(7),false);assertThat(avisos.total()).isEqualTo(6);
  verify(repo).countByUsuarioIdAndEstadoInAndFechaCompromisoBefore(7L,List.of(EstadoPendiente.PENDIENTE,EstadoPendiente.EN_PROCESO),hoy);
  verify(repo).findAll(any(Specification.class),argThat((Pageable p)->p.getPageSize()==5));
 }
 @Test void validacionDeServicioNoConfiaSoloEnHtml(){usuario(TipoCuentaUsuario.ADMINISTRATIVO);var f=form();f.setTitulo("  ");assertThatThrownBy(()->s.guardar(principal(7),false,null,f)).isInstanceOf(ReglaNegocioException.class);verifyNoInteractions(repo,cambios);}
 @Test @SuppressWarnings("unchecked") void consultaSiempreIncluyeUsuarioEInstitucionAunqueFiltroPidaTodos(){
  jakarta.persistence.criteria.Root<PendientePersonal> raiz=mock(jakarta.persistence.criteria.Root.class,RETURNS_DEEP_STUBS);
  var b=mock(jakarta.persistence.criteria.CriteriaBuilder.class);
  PendienteService.especificacion(principal(7),new PendienteFiltro("","TODOS",null,null,0,25),LocalDate.now()).toPredicate(raiz,null,b);
  verify(b).equal(raiz.get("usuario").get("id"),7L);
  verify(b).equal(raiz.get("usuario").get("institucion").get("id"),1L);
 }
}
