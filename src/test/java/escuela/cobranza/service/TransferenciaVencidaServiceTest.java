package escuela.cobranza.service;
import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.CargoRepository;
import escuela.institucion.entity.*;
import escuela.inscripcion.entity.Inscripcion;
import escuela.alumno.entity.Alumno;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class TransferenciaVencidaServiceTest {
    CargoRepository cargos=mock(CargoRepository.class);
    RegistroAuditoriaService auditoria=mock(RegistroAuditoriaService.class);
    TransferenciaVencidaService service=new TransferenciaVencidaService(cargos,auditoria);
    Cargo cargo;
    @BeforeEach void preparar() {
        cargo=new Cargo();cargo.setId(1L);cargo.setVersion(0L);cargo.setImporteOriginal(new BigDecimal("850.00"));
        cargo.setFechaVencimiento(LocalDate.of(2026,10,5));
        var institucion=new Institucion();institucion.setId(1L);
        var plantel=new Plantel();plantel.setId(2L);plantel.setInstitucion(institucion);
        var alumno=new Alumno();alumno.setInstitucion(institucion);
        var inscripcion=new Inscripcion();inscripcion.setAlumno(alumno);inscripcion.setPlantel(plantel);cargo.setInscripcion(inscripcion);
        when(cargos.findByIdForUpdate(1L)).thenReturn(Optional.of(cargo));
        autenticar(7L,1L,true,false,Set.of(),"CARGO_ADMINISTRAR");
    }
    @AfterEach void limpiar(){SecurityContextHolder.clearContext();}
    void autenticar(Long usuario,Long institucion,boolean institucional,boolean recuperacion,Set<Long> planteles,String permiso) {
        var autoridades=List.of(new SimpleGrantedAuthority(permiso));
        var p=new UsuarioPrincipal(usuario,institucion,planteles,institucional,recuperacion,"admin","",autoridades);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p,"",autoridades));
    }
    void guardar(boolean permiso,String motivo){service.configurar(1L,0L,permiso,motivo);}
    @Test void autorizaYRevocaConAuditoriaSinModificarDeudaNiFecha() {
        guardar(true,"Familia pagará a distancia");assertThat(cargo.isTransferenciaVencidaAutorizada()).isTrue();
        guardar(false,"Revocar excepción");assertThat(cargo.isTransferenciaVencidaAutorizada()).isFalse();
        assertThat(cargo.getImporteOriginal()).isEqualByComparingTo("850");
        assertThat(cargo.getFechaVencimiento()).isEqualTo(LocalDate.of(2026,10,5));
        verify(auditoria,times(2)).registrar(eq(1L),eq(AccionAuditoria.TRANSFERENCIA_VENCIDA_AUTORIZADA),eq("CARGO"),eq(1L),anyString(),anyMap());
    }
    @Test void exigeMotivoYVersionActual(){assertThatThrownBy(()->guardar(true," ")).hasMessageContaining("motivo obligatorio");cargo.setVersion(1L);assertThatThrownBy(()->guardar(true,"Razón")).hasMessageContaining("modificado por otro proceso");verifyNoInteractions(auditoria);}
    @Test void bloqueaLiquidadosCanceladosYConvenidos(){cargo.setImporteOriginal(BigDecimal.ZERO);assertThatThrownBy(()->guardar(true,"Razón")).hasMessageContaining("saldo pendiente");cargo.setImporteOriginal(BigDecimal.TEN);for(var estado:List.of(EstadoRegistroCargo.CANCELADO,EstadoRegistroCargo.CONVENIDO)){cargo.setEstadoRegistro(estado);assertThatThrownBy(()->guardar(true,"Razón")).hasMessageContaining("emitido");}verifyNoInteractions(auditoria);}
    @Test void bloqueaOtraInstitucionOtroPlantelYFaltaDePermiso(){autenticar(7L,3L,true,false,Set.of(),"CARGO_ADMINISTRAR");assertThatThrownBy(()->guardar(true,"Razón")).hasMessageContaining("alcance");autenticar(7L,1L,false,false,Set.of(9L),"CARGO_ADMINISTRAR");assertThatThrownBy(()->guardar(true,"Razón")).hasMessageContaining("alcance");autenticar(7L,1L,true,false,Set.of(),"CARGO_LEER");assertThatThrownBy(()->guardar(true,"Razón")).hasMessageContaining("administrador identificado");verifyNoInteractions(auditoria);}
    @Test void noPermiteRecuperacionNiSesionAusente(){autenticar(null,1L,true,true,Set.of(),"CARGO_ADMINISTRAR");assertThatThrownBy(()->guardar(true,"Razón")).hasMessageContaining("administrador identificado");SecurityContextHolder.clearContext();assertThatThrownBy(()->guardar(true,"Razón")).hasMessageContaining("administrador identificado");}
    @Test void reintentoSinCambioNoDuplicaAuditoria(){cargo.setTransferenciaVencidaAutorizada(true);guardar(true,"Reintento");verifyNoInteractions(auditoria);verify(cargos,never()).saveAndFlush(any());}
    @Test void accesoPorPlantelPermitido(){autenticar(7L,1L,false,false,Set.of(2L),"CARGO_ADMINISTRAR");guardar(true,"Razón");assertThat(cargo.isTransferenciaVencidaAutorizada()).isTrue();}
}
