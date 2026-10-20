package escuela.personal;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.institucion.entity.*;
import escuela.institucion.repository.PlantelRepository;
import escuela.personal.dto.CambioPrincipalForm;
import escuela.personal.entity.*;
import escuela.personal.repository.*;
import escuela.personal.service.*;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class CambioPrincipalEmpleadoServiceTest {
    final EmpleadoService acceso=mock(EmpleadoService.class);
    final EmpleadoRepository empleados=mock(EmpleadoRepository.class);
    final EmpleadoPlantelRepository asignaciones=mock(EmpleadoPlantelRepository.class);
    final PlantelRepository planteles=mock(PlantelRepository.class);
    final RegistroAuditoriaService auditoria=mock(RegistroAuditoriaService.class);
    final CambioPrincipalEmpleadoService service=new CambioPrincipalEmpleadoService(acceso,empleados,asignaciones,planteles,auditoria);
    final Empleado empleado=new Empleado(); final Plantel a=new Plantel(),b=new Plantel();
    EmpleadoPlantel origen,destino;
    final LocalDate fecha=LocalDate.now(ZoneId.of("America/Mexico_City")).plusDays(2);
    @BeforeEach void preparar(){
        var institucion=new Institucion();institucion.setId(1L);institucion.setZonaHoraria("America/Mexico_City");
        empleado.setId(10L);empleado.setInstitucion(institucion);
        a.setId(20L);a.setNombre("Plantel A");a.setInstitucion(institucion);a.setActivo(true);
        b.setId(21L);b.setNombre("Plantel B");b.setInstitucion(institucion);b.setActivo(true);
        origen=asignacion(30L,a,true);destino=asignacion(31L,b,false);
        when(acceso.autorizar(10L,true,true)).thenReturn(empleado);when(empleados.bloquear(10L)).thenReturn(Optional.of(empleado));
        when(planteles.findById(21L)).thenReturn(Optional.of(b));
        var llamadas=new AtomicInteger();
        when(asignaciones.findAll(any(Specification.class),any(Pageable.class))).thenAnswer(x->new PageImpl<>(List.of(llamadas.getAndIncrement()%2==0?origen:destino)));
    }
    EmpleadoPlantel asignacion(Long id,Plantel plantel,boolean principal){var v=new EmpleadoPlantel();v.setId(id);v.setVersion(2L);v.setEmpleado(empleado);v.setPlantel(plantel);v.setFechaInicio(fecha.minusDays(10));v.setPrincipal(principal);return v;}
    CambioPrincipalForm form(boolean conservar){var f=new CambioPrincipalForm();f.setFecha(fecha);f.setPlantelId(21L);f.setOrigenId(30L);f.setVersionOrigen(2L);f.setDestinoId(31L);f.setVersionDestino(2L);f.setConservarAnterior(conservar);f.setMotivo("Cambio autorizado de sede principal");return f;}
    @Test void vistaPreviaNoEscribeYConservaLimites(){
        var v=service.visualizar(10L,fecha,21L,true);assertThat(v.origen()).isEqualTo("Plantel A");assertThat(v.cierreAnterior()).isEqualTo(fecha.minusDays(1));
        verify(asignaciones,never()).save(any());verify(asignaciones,never()).flush();verifyNoInteractions(auditoria,empleados);
    }
    @Test void cambioCierraAntesDeCrearConHistorialYAuditoria(){
        service.confirmar(10L,form(true));
        assertThat(origen.getFechaFin()).isEqualTo(fecha.minusDays(1));assertThat(destino.getFechaFin()).isEqualTo(fecha.minusDays(1));
        assertThat(origen.isPrincipal()).isTrue();assertThat(destino.isPrincipal()).isFalse();
        var captor=org.mockito.ArgumentCaptor.forClass(EmpleadoPlantel.class);verify(asignaciones,times(2)).save(captor.capture());
        assertThat(captor.getAllValues().getFirst().getPlantel()).isSameAs(b);assertThat(captor.getAllValues().getFirst().isPrincipal()).isTrue();
        assertThat(captor.getAllValues().getLast().getPlantel()).isSameAs(a);assertThat(captor.getAllValues().getLast().isPrincipal()).isFalse();
        assertThat(captor.getAllValues()).allSatisfy(v->assertThat(v.getFechaInicio()).isEqualTo(fecha));
        var orden=inOrder(empleados,asignaciones,auditoria);orden.verify(empleados).bloquear(10L);orden.verify(asignaciones).flush();orden.verify(asignaciones,times(2)).save(any());orden.verify(asignaciones).flush();
        orden.verify(auditoria).registrar(eq(1L),eq(AccionAuditoria.EMPLEADO_PRINCIPAL_CAMBIADO),eq("EMPLEADO"),eq(10L),contains("Cambio autorizado"),anyMap());
    }
    @Test void puedeFinalizarAnteriorSinRecrearloYRespetaFinPrevisto(){
        origen.setFechaFin(fecha.plusDays(12));destino.setFechaFin(fecha.plusDays(6));
        service.confirmar(10L,form(false));
        verify(asignaciones).save(argThat(v->v.isPrincipal()&&v.getFechaFin().equals(fecha.plusDays(6))));
    }
    @Test void versionObsoletaNoCierraNada(){var f=form(true);f.setVersionDestino(1L);assertThatThrownBy(()->service.confirmar(10L,f)).isInstanceOf(escuela.common.exception.ReglaNegocioException.class);assertThat(origen.getFechaFin()).isNull();verify(asignaciones,never()).save(any());verifyNoInteractions(auditoria);}
    @Test void cambioDeAsignacionDesdePreviewNoSeAplica(){var f=form(true);f.setOrigenId(300L);assertThatThrownBy(()->service.confirmar(10L,f)).hasMessageContaining("Vuelve a visualizar");assertThat(origen.getFechaFin()).isNull();}
    @Test void rechazaHistoricoMismoDiaDeInicioYSolapamiento(){
        assertThatThrownBy(()->service.visualizar(10L,fecha.minusDays(3),21L,true)).hasMessageContaining("hoy");
        origen.setFechaInicio(fecha);assertThatThrownBy(()->service.visualizar(10L,fecha,21L,true)).hasMessageContaining("día siguiente");
        origen.setFechaInicio(fecha.minusDays(1));when(asignaciones.count(any(Specification.class))).thenReturn(1L);
        assertThatThrownBy(()->service.visualizar(10L,fecha,21L,true)).hasMessageContaining("cruza");verify(asignaciones,never()).save(any());
    }
    @Test void niegaPlantelAjenoInactivoYPermisoInsuficiente(){
        var otra=new Institucion();otra.setId(8L);b.setInstitucion(otra);assertThatThrownBy(()->service.visualizar(10L,fecha,21L,true)).isInstanceOf(AccessDeniedException.class);
        b.setInstitucion(a.getInstitucion());b.setActivo(false);assertThatThrownBy(()->service.visualizar(10L,fecha,21L,true)).hasMessageContaining("activo");
        when(acceso.autorizar(10L,true,true)).thenThrow(new AccessDeniedException("denegado"));assertThatThrownBy(()->service.confirmar(10L,form(true))).isInstanceOf(AccessDeniedException.class);verifyNoInteractions(auditoria);
    }
    @Test void motivoVacioYPrincipalYaElegidoSeRechazan(){var f=form(true);f.setMotivo("   ");assertThatThrownBy(()->service.confirmar(10L,f)).hasMessageContaining("motivo");when(planteles.findById(20L)).thenReturn(Optional.of(a));assertThatThrownBy(()->service.visualizar(10L,fecha,20L,true)).hasMessageContaining("ya es principal");}
}
