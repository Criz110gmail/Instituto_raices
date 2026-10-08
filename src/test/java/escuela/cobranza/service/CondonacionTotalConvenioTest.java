package escuela.cobranza.service;
import escuela.cobranza.dto.*;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.*;
import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.inscripcion.entity.Inscripcion;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.SolicitudAplicacionPagoRepository;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import escuela.seguridad.service.*;
import escuela.common.exception.ReglaNegocioException;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CondonacionTotalConvenioTest {
 final ConvenioPagoRepository convenios=mock(ConvenioPagoRepository.class);
 final ConvenioPagoCargoOriginalRepository originales=mock(ConvenioPagoCargoOriginalRepository.class);
 final ConvenioPagoCargoNuevoRepository nuevos=mock(ConvenioPagoCargoNuevoRepository.class);
 final CargoRepository cargos=mock(CargoRepository.class);
 final TutorRepository tutores=mock(TutorRepository.class);
 final ConceptoCobroRepository conceptos=mock(ConceptoCobroRepository.class);
 final InstitucionRepository instituciones=mock(InstitucionRepository.class);
 final AlumnoTutorRepository vinculos=mock(AlumnoTutorRepository.class);
 final SolicitudAplicacionPagoRepository solicitudes=mock(SolicitudAplicacionPagoRepository.class);
 final ConvenioPagoService service=new ConvenioPagoService(convenios,originales,nuevos,cargos,tutores,conceptos,instituciones,vinculos,solicitudes,mock(AlcanceDatosService.class));
 Cargo original; ConvenioPago guardado;
 @BeforeEach void preparar(){
  var ins=new Institucion();ins.setId(1L);ins.setZonaHoraria("America/Mexico_City");ins.setNombre("Escuela");
  var tutor=new Tutor();tutor.setId(2L);tutor.setInstitucion(ins);tutor.setNombres("Tutor");tutor.setPrimerApellido("Prueba");
  var concepto=new ConceptoCobro();concepto.setId(3L);concepto.setInstitucion(ins);concepto.setActivo(true);concepto.setNombre("Acuerdo");
  var alumno=new Alumno();alumno.setId(4L);alumno.setInstitucion(ins);alumno.setNombres("Ana");alumno.setPrimerApellido("Prueba");
  var matricula=new Inscripcion();matricula.setId(5L);matricula.setAlumno(alumno);
  original=new Cargo();original.setId(10L);original.setInscripcion(matricula);original.setConceptoCobro(concepto);original.setEstadoRegistro(EstadoRegistroCargo.EMITIDO);original.setImporteOriginal(new BigDecimal("500.00"));original.setMoneda("MXN");original.setFechaVencimiento(LocalDate.of(2026,10,20));
  when(tutores.findByIdForUpdate(2L)).thenReturn(Optional.of(tutor));when(conceptos.findById(3L)).thenReturn(Optional.of(concepto));when(instituciones.findById(1L)).thenReturn(Optional.of(ins));when(cargos.findByIdForUpdate(10L)).thenReturn(Optional.of(original));
  when(vinculos.tieneResponsabilidadFinancieraVigente(eq(4L),eq(2L),any())).thenReturn(true);
  when(convenios.saveAndFlush(any())).thenAnswer(i->{guardado=i.getArgument(0);guardado.setId(20L);guardado.setVersion(0L);return guardado;});
  when(originales.save(any())).thenAnswer(i->{ConvenioPagoCargoOriginal o=i.getArgument(0);o.getConvenio().getCargosOriginales().add(o);return o;});
  when(convenios.findByIdForUpdate(20L)).thenAnswer(i->Optional.of(guardado));when(convenios.findById(20L)).thenAnswer(i->Optional.of(guardado));
  actor(false);
 }
 void actor(boolean recuperacion){var p=new UsuarioPrincipal(7L,1L,Set.of(),true,recuperacion,"admin.prueba","",List.of());SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(p,"",List.of()));}
 @AfterEach void limpiar(){SecurityContextHolder.clearContext();}
 ConvenioPagoForm form(){var f=new ConvenioPagoForm();f.setInstitucionId(1L);f.setTutorId(2L);f.setConceptoCobroId(3L);f.setCargoIds(List.of(10L));f.setModalidad(ModalidadConvenioPago.CONDONACION_TOTAL);f.setFechaAcuerdo(LocalDate.of(2026,10,8));f.setFechaVencimiento(LocalDate.of(2026,10,20));f.setDescripcion("Condonación de prueba");f.setMotivo("Acuerdo autorizado");f.setMontoAcordado(new BigDecimal("999.00"));return f;}
 @Test void condonaSaldoSinCrearCargosNiAplicacionesConActorYFecha(){
  assertThat(service.crear(form())).isEqualTo(20L);
  assertThat(guardado.getMontoAcordadoTotal()).isEqualByComparingTo("0");assertThat(guardado.getMontoCondonadoTotal()).isEqualByComparingTo("500");assertThat(guardado.getEstado()).isEqualTo(EstadoConvenioPago.CONDONADO_TOTAL);
  assertThat(guardado.getAutorizadoPorId()).isEqualTo(7L);assertThat(guardado.getAutorizadoPorNombre()).isEqualTo("admin.prueba");assertThat(guardado.getAutorizadoEn()).isNotNull();assertThat(guardado.getFechaVencimiento()).isEqualTo(guardado.getFechaAcuerdo());
  assertThat(original.getEstadoRegistro()).isEqualTo(EstadoRegistroCargo.CONVENIDO);verify(cargos,never()).save(any());verifyNoInteractions(nuevos);assertThat(original.getAplicaciones()).isEmpty();
  assertThat(service.detalle(20L).situacion()).isEqualTo("CONDONADO_TOTAL");assertThat(service.detalle(20L).autorizadoPorNombre()).isEqualTo("admin.prueba");
 }
 @Test void conservaAbonoYAlCancelarSoloRestauraSaldoCondonado(){
  var a=new AplicacionPago();a.setMonto(new BigDecimal("100.00"));a.setOperacion(OperacionAplicacionPago.APLICAR);original.getAplicaciones().add(a);
  service.crear(form());assertThat(guardado.getMontoCondonadoTotal()).isEqualByComparingTo("400");assertThat(guardado.getCargosOriginales().getFirst().getMontoAplicadoSnapshot()).isEqualByComparingTo("100");
  service.cancelar(20L,0L,"Reabrir deuda autorizada");assertThat(original.getEstadoRegistro()).isEqualTo(EstadoRegistroCargo.EMITIDO);assertThat(original.getAplicaciones()).containsExactly(a);assertThat(guardado.getEstado()).isEqualTo(EstadoConvenioPago.CANCELADO);assertThat(guardado.getCargosOriginales().getFirst().isActivo()).isFalse();
  assertThatThrownBy(()->service.cancelar(20L,0L,"Otra vez")).isInstanceOf(ReglaNegocioException.class).hasMessageContaining("cancelado");
 }
 @Test void noRestauraUnaDeudaCuyosAbonosCambiaron(){service.crear(form());var a=new AplicacionPago();a.setMonto(new BigDecimal("100"));a.setOperacion(OperacionAplicacionPago.APLICAR);original.getAplicaciones().add(a);assertThatThrownBy(()->service.cancelar(20L,0L,"Restaurar")).isInstanceOf(ReglaNegocioException.class).hasMessageContaining("cambiaron");assertThat(original.getEstadoRegistro()).isEqualTo(EstadoRegistroCargo.CONVENIDO);}
 @Test void noPermiteAutorizarDesdeRecuperacion(){actor(true);assertThatThrownBy(()->service.crear(form())).isInstanceOf(ReglaNegocioException.class).hasMessageContaining("recuperación");verify(convenios,never()).saveAndFlush(any());}
 @Test void rechazaTransferenciaPendiente(){when(solicitudes.existePendiente(2L,List.of(10L))).thenReturn(true);assertThatThrownBy(()->service.crear(form())).isInstanceOf(ReglaNegocioException.class).hasMessageContaining("pendiente");verifyNoInteractions(originales,nuevos);}
 @Test void montoCeroNoEsValidoParaConvenioNormal(){var f=form();f.setModalidad(ModalidadConvenioPago.MONTO_ACORDADO);f.setMontoAcordado(BigDecimal.ZERO);assertThatThrownBy(()->service.crear(f)).isInstanceOf(ReglaNegocioException.class).hasMessageContaining("$0.01");verify(convenios,never()).saveAndFlush(any());}
 @Test void formAdmiteCeroParaCondonacionPeroNuncaNegativos()throws Exception{var validator=jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();var f=form();f.setMontoAcordado(BigDecimal.ZERO);assertThat(validator.validate(f)).isEmpty();f.setMontoAcordado(new BigDecimal("-1"));assertThat(validator.validate(f)).isNotEmpty();}
 @Test void confirmarDosVecesNoCondonaDosVeces(){service.crear(form());assertThatThrownBy(()->service.crear(form())).isInstanceOf(ReglaNegocioException.class).hasMessageContaining("saldo disponible");verify(convenios,times(1)).saveAndFlush(any());}
 @Test void historialDelPagoPrevioNoMuestraLaDeudaCondonadaComoExigible(){
  var a=new AplicacionPago();a.setMonto(new BigDecimal("100.00"));a.setOperacion(OperacionAplicacionPago.APLICAR);a.setCargo(original);original.getAplicaciones().add(a);service.crear(form());
  escuela.finanzas.dto.response.AplicacionPagoResponse dto=org.springframework.test.util.ReflectionTestUtils.invokeMethod(new escuela.finanzas.mapper.PagoMapper(),"aplicacion",a);
  assertThat(dto.monto()).isEqualByComparingTo("100");assertThat(dto.saldoCargoActual()).isZero();
  service.cancelar(20L,0L,"Restaurar");
  dto=org.springframework.test.util.ReflectionTestUtils.invokeMethod(new escuela.finanzas.mapper.PagoMapper(),"aplicacion",a);assertThat(dto.saldoCargoActual()).isEqualByComparingTo("400");
 }
}
