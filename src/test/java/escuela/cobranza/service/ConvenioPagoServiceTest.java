package escuela.cobranza.service;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.cobranza.dto.ConvenioPagoForm;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.entity.*;
import escuela.finanzas.repository.SolicitudAplicacionPagoRepository;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.tutor.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ConvenioPagoServiceTest {
 private final ConvenioPagoRepository convenios=mock(ConvenioPagoRepository.class);private final CargoRepository cargos=mock(CargoRepository.class);private final AlcanceDatosService alcance=mock(AlcanceDatosService.class);
 private final ConvenioPagoService service=new ConvenioPagoService(convenios,mock(ConvenioPagoCargoOriginalRepository.class),mock(ConvenioPagoCargoNuevoRepository.class),cargos,mock(TutorRepository.class),mock(ConceptoCobroRepository.class),mock(InstitucionRepository.class),mock(AlumnoTutorRepository.class),mock(SolicitudAplicacionPagoRepository.class),alcance);
 @Test void rechazaVencimientoAnteriorAlAcuerdoSinModificarDatos(){ConvenioPagoForm f=new ConvenioPagoForm();f.setFechaAcuerdo(LocalDate.of(2026,10,2));f.setFechaVencimiento(LocalDate.of(2026,10,1));f.getCargoIds().add(1L);assertThatThrownBy(()->service.crear(f)).isInstanceOf(ReglaNegocioException.class).hasMessageContaining("vencimiento");verifyNoInteractions(convenios,cargos);}
 @Test void noCancelaConvenioCuandoElNuevoCargoYaTienePagos(){Cargo cargo=new Cargo();cargo.setId(20L);cargo.setEstadoRegistro(EstadoRegistroCargo.EMITIDO);AplicacionPago aplicacion=new AplicacionPago();aplicacion.setMonto(new BigDecimal("100.00"));aplicacion.setOperacion(OperacionAplicacionPago.APLICAR);cargo.getAplicaciones().add(aplicacion);ConvenioPago convenio=new ConvenioPago();convenio.setId(3L);convenio.setVersion(0L);convenio.setEstado(EstadoConvenioPago.VIGENTE);ConvenioPagoCargoNuevo nuevo=new ConvenioPagoCargoNuevo();nuevo.setCargo(cargo);convenio.getCargosNuevos().add(nuevo);when(convenios.findByIdForUpdate(3L)).thenReturn(Optional.of(convenio));when(cargos.findByIdForUpdate(20L)).thenReturn(Optional.of(cargo));assertThatThrownBy(()->service.cancelar(3L,0L,"Cambio de acuerdo")).isInstanceOf(ReglaNegocioException.class).hasMessageContaining("pagos aplicados");}
}
