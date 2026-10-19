package escuela.docente.support;

import escuela.docente.entity.*;
import escuela.docente.repository.*;
import escuela.docente.service.PlaneacionService;
import escuela.institucion.entity.Institucion;
import escuela.academico.repository.GrupoRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.*;
import escuela.auditoria.service.RegistroAuditoriaService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObservacionRevisionPlaneacionTest {
 private final PlaneacionSemanalRepository planes=mock(PlaneacionSemanalRepository.class);
 private final PlaneacionHistorialRepository historial=mock(PlaneacionHistorialRepository.class);
 private final MaestroRepository maestros=mock(MaestroRepository.class);
 private final PlaneacionService service=new PlaneacionService(planes,historial,mock(PlaneacionVersionPublicadaRepository.class),maestros,mock(AsignacionMaestroRepository.class),mock(GrupoRepository.class),mock(UsuarioRepository.class),mock(AlcanceDatosService.class),mock(RegistroAuditoriaService.class));
 private final UsuarioPrincipal principal=new UsuarioPrincipal(9L,1L,Set.of(),false,false,"docente.qa","",List.of());
 private PlaneacionSemanal preparar(EstadoPlaneacion estado){
  var institucion=mock(Institucion.class);when(institucion.getId()).thenReturn(1L);when(institucion.getZonaHoraria()).thenReturn("America/Mexico_City");
  var maestro=mock(Maestro.class);when(maestro.getId()).thenReturn(7L);when(maestro.isActivo()).thenReturn(true);when(maestro.getInstitucion()).thenReturn(institucion);when(maestros.findByUsuarioId(9L)).thenReturn(Optional.of(maestro));
  var plan=mock(PlaneacionSemanal.class);when(plan.getMaestro()).thenReturn(maestro);when(plan.getInstitucion()).thenReturn(institucion);when(plan.getEstado()).thenReturn(estado);when(planes.findDetalleById(22L)).thenReturn(Optional.of(plan));return plan;
 }
 private void motivo(EstadoPlaneacion estado,String texto){var h=new PlaneacionHistorial();h.setMotivo(texto);h.setOcurridoEn(Instant.parse("2026-10-19T22:00:00Z"));when(historial.findFirstByPlaneacionIdAndEstadoNuevoOrderByOcurridoEnDescIdDesc(22L,estado)).thenReturn(Optional.of(h));}
 @Test void devuelveSoloSolicitudVigenteConFechaInstitucional(){
  preparar(EstadoPlaneacion.REQUIERE_AJUSTES);motivo(EstadoPlaneacion.REQUIERE_AJUSTES,"Agregar evaluación.\nIndicar evidencia.");
  var resultado=service.observacionRevision(principal,22L);assertThat(resultado.titulo()).contains("Correcciones solicitadas");assertThat(resultado.motivo()).isEqualTo("Agregar evaluación.\nIndicar evidencia.");assertThat(resultado.solicitadaEn()).isEqualTo(LocalDateTime.of(2026,10,19,16,0));
  verify(historial).findFirstByPlaneacionIdAndEstadoNuevoOrderByOcurridoEnDescIdDesc(22L,EstadoPlaneacion.REQUIERE_AJUSTES);verify(historial,never()).findAllByPlaneacionIdOrderByOcurridoEnDescIdDesc(any());
 }
 @Test void reaperturaTieneSuPropioMotivo(){preparar(EstadoPlaneacion.REABIERTA);motivo(EstadoPlaneacion.REABIERTA,"Corregir recursos.");assertThat(service.observacionRevision(principal,22L).titulo()).contains("reapertura");}
 @Test void solicitudViejaNoSePresentaComoPendienteDespuesDeEnviarOPublicar(){
  for(var estado:List.of(EstadoPlaneacion.BORRADOR,EstadoPlaneacion.ENVIADA,EstadoPlaneacion.EN_REVISION,EstadoPlaneacion.PUBLICADA,EstadoPlaneacion.DESCARTADA)){preparar(estado);assertThat(service.observacionRevision(principal,22L)).isNull();}verifyNoInteractions(historial);
 }
 @Test void otraPlaneacionNoExponeMotivos(){var plan=preparar(EstadoPlaneacion.REQUIERE_AJUSTES);var otro=mock(Maestro.class);when(otro.getId()).thenReturn(8L);when(plan.getMaestro()).thenReturn(otro);assertThatThrownBy(()->service.observacionRevision(principal,22L)).isInstanceOf(AccessDeniedException.class);verifyNoInteractions(historial);}
 @Test void institucionDistintaNoExponeMotivos(){var plan=preparar(EstadoPlaneacion.REQUIERE_AJUSTES);var otra=mock(Institucion.class);when(otra.getId()).thenReturn(2L);when(plan.getInstitucion()).thenReturn(otra);assertThatThrownBy(()->service.observacionRevision(principal,22L)).isInstanceOf(AccessDeniedException.class);verifyNoInteractions(historial);}
 @Test void sinHistorialOMotivoNoInventaCorrecciones(){preparar(EstadoPlaneacion.REQUIERE_AJUSTES);assertThat(service.observacionRevision(principal,22L)).isNull();motivo(EstadoPlaneacion.REQUIERE_AJUSTES,"  ");assertThat(service.observacionRevision(principal,22L)).isNull();}
}
