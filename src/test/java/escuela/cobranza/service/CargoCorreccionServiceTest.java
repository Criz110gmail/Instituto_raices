package escuela.cobranza.service;
import escuela.cobranza.entity.*;
import escuela.cobranza.mapper.CargoMapper;
import escuela.cobranza.repository.*;
import escuela.cobranza.service.impl.AplicacionBecaCargoService;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.entity.*;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class CargoCorreccionServiceTest {
    CargoRepository cargos=mock(CargoRepository.class); CuotaAlumnoRepository cuotas=mock(CuotaAlumnoRepository.class);
    AplicacionBecaCargoService becas=mock(AplicacionBecaCargoService.class);
    CargoCorreccionService service=new CargoCorreccionService(cargos,cuotas,mock(CargoMapper.class),becas);
    Cargo c; CuotaAlumno q;
    @BeforeEach void preparar() {
        var permisos=List.of(new SimpleGrantedAuthority("CARGO_ADMINISTRAR"));
        var p=new UsuarioPrincipal(1L,1L,Set.of(),true,false,"admin","",permisos);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p,"",permisos));
        q=new CuotaAlumno(); q.setId(13L);q.setEstado(EstadoCuota.ACTIVA);q.setFrecuencia(FrecuenciaCuota.UNICA);
        q.setFechaVencimientoUnico(LocalDate.of(2026,10,5));
        c=new Cargo();c.setId(18L);c.setVersion(0L);c.setCuotaAlumno(q);c.setEstadoRegistro(EstadoRegistroCargo.CANCELADO);
        var i=new Inscripcion();i.setEstado(EstadoInscripcion.ACTIVA);c.setInscripcion(i);
        var alumno=new escuela.alumno.entity.Alumno();var institucion=new escuela.institucion.entity.Institucion();institucion.setId(1L);
        alumno.setInstitucion(institucion);i.setAlumno(alumno);
        var concepto=new ConceptoCobro();concepto.setActivo(true);c.setConceptoCobro(concepto);
        c.setDescripcion("Prueba");c.setPeriodoCobroInicio(LocalDate.of(2026,10,1));c.setPeriodoCobroFin(LocalDate.of(2027,6,15));
        c.setFechaEmision(LocalDate.of(2026,10,6));c.setFechaVencimiento(LocalDate.of(2026,10,10));
        c.setImporteOriginal(new BigDecimal("1000.00"));c.setMoneda("MXN");
        when(cargos.findById(18L)).thenReturn(Optional.of(c));when(cargos.findByIdForUpdate(18L)).thenReturn(Optional.of(c));
        when(cuotas.findByIdForUpdate(13L)).thenReturn(Optional.of(q));
    }
    @AfterEach void limpiar(){SecurityContextHolder.clearContext();}
    void reemplazar(){service.reemplazar(18L,0L,LocalDate.of(2026,10,5),"  Fecha capturada por error  ");}
    @Test void reemplazaCanceladoSinCambiarHistoricoYRecalculaBeca() {
        reemplazar(); var captor=ArgumentCaptor.forClass(Cargo.class);verify(cargos).saveAndFlush(captor.capture());
        var nuevo=captor.getValue();assertThat(nuevo.getReemplazaCargo()).isSameAs(c);
        assertThat(nuevo.getClaveGeneracion()).isEqualTo("REEMPLAZO:18");
        assertThat(nuevo.getFechaVencimiento()).isEqualTo(LocalDate.of(2026,10,5));
        assertThat(c.getFechaVencimiento()).isEqualTo(LocalDate.of(2026,10,10));
        assertThat(nuevo.getImporteOriginal()).isEqualByComparingTo("1000");
        assertThat(nuevo.getMotivoReemplazo()).isEqualTo("Fecha capturada por error");verify(becas).aplicar(nuevo);
    }
    @Test void cancelaEmitidoDentroDeLaMismaTransaccion(){c.setEstadoRegistro(EstadoRegistroCargo.EMITIDO);reemplazar();assertThat(c.getEstadoRegistro()).isEqualTo(EstadoRegistroCargo.CANCELADO);assertThat(c.getCanceladoEn()).isNotNull();}
    @Test void noDuplicaUnReemplazo(){when(cargos.findByReemplazaCargoId(18L)).thenReturn(Optional.of(new Cargo()));assertThatThrownBy(this::reemplazar).hasMessageContaining("ya tiene un reemplazo");verify(cargos,never()).saveAndFlush(any());}
    @Test void noReemplazaConPagoEnRevision(){when(cargos.tienePagoEnRevision(18L)).thenReturn(true);assertThatThrownBy(this::reemplazar).hasMessageContaining("pago en revisión");verify(cargos,never()).saveAndFlush(any());}
    @Test void noReemplazaConHistorialDePagos(){c.getAplicaciones().add(new escuela.finanzas.entity.AplicacionPago());assertThatThrownBy(this::reemplazar).hasMessageContaining("historial de pagos");}
    @Test void noReemplazaConRecargo(){var a=new AjusteCargo();a.setTipo(TipoAjusteCargo.RECARGO);c.getAjustes().add(a);assertThatThrownBy(this::reemplazar).hasMessageContaining("distintos de beca");}
    @Test void exigeFechaDentroDelPeriodoYMotivo(){assertThatThrownBy(()->service.reemplazar(18L,0L,LocalDate.of(2026,9,1),"Error")).hasMessageContaining("periodo de cobro");assertThatThrownBy(()->service.reemplazar(18L,0L,LocalDate.of(2026,10,5)," ")).hasMessageContaining("motivo obligatorio");}
    @Test void exigeActorIdentificado(){SecurityContextHolder.clearContext();assertThatThrownBy(this::reemplazar).isInstanceOf(ReglaNegocioException.class).hasMessageContaining("usuario administrativo");}
    @Test void permiteBecaPeroNoConvenio(){var a=new AjusteCargo();a.setTipo(TipoAjusteCargo.BECA);c.getAjustes().add(a);reemplazar();c.setEstadoRegistro(EstadoRegistroCargo.CONVENIDO);assertThatThrownBy(this::reemplazar).hasMessageContaining("convenio");}
    @Test void bloqueaOtraInstitucion(){c.getInscripcion().getAlumno().getInstitucion().setId(2L);assertThatThrownBy(this::reemplazar).hasMessageContaining("alcance administrativo");verify(cargos,never()).saveAndFlush(any());}
    @Test void bloqueaVersionAntigua(){c.setVersion(1L);assertThatThrownBy(this::reemplazar).hasMessageContaining("modificado por otro proceso");verify(cargos,never()).saveAndFlush(any());}
    @Test void bloqueaAccesoDeRecuperacion(){var permisos=List.of(new SimpleGrantedAuthority("CARGO_ADMINISTRAR"));var p=new UsuarioPrincipal(null,1L,Set.of(),true,true,"recuperacion","",permisos);SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p,"",permisos));assertThatThrownBy(this::reemplazar).hasMessageContaining("usuario administrativo");}
}
