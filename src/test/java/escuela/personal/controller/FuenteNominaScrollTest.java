package escuela.personal.controller;
import escuela.personal.dto.*;
import escuela.personal.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FuenteNominaScrollTest {
 private FiltroNomina filtro(){return new FiltroNomina("","TODOS",null,null,null,null,null,null,null,null,0,25);}
 @Test void colectivoEnfocaSoloNuevaFuenteYConservaCapturaSinPagar(){
  var service=mock(PagoMultipleNominaService.class);var pagos=mock(PagoEmpleadoNominaService.class);var prep=mock(PreparacionNominaService.class);
  when(pagos.puedePagar()).thenReturn(true);var fecha=LocalDate.of(2026,12,1);
  when(prep.obtener(7L)).thenReturn(new PreparacionNominaService.Borrador(7L,7L,"Prueba","BORRADOR",1,0L,"Prueba","DIC","Diciembre","Periodo independiente",fecha,fecha.plusDays(14),fecha.plusDays(14),"MXN","sello",1,new BigDecimal("5806.45")));
  var c=new PagoMultipleNominaController(service,pagos,prep);var f=new PagoMultipleNominaForm();f.getFuentes().getFirst().setCuentaId(2L);f.getFuentes().getFirst().setImporte(new BigDecimal("806.45"));f.setReferencia("PRUEBA");var solicitud=f.getSolicitud();var m=new ExtendedModelMap();m.addAttribute("formMultiple",f);
  c.agregar(7L,f,filtro(),m);
  assertThat(m.get("fuenteNueva")).isEqualTo(1);assertThat(f.getFuentes()).hasSize(2);assertThat(f.getFuentes().getFirst().getImporte()).isEqualByComparingTo("806.45");assertThat(f.getReferencia()).isEqualTo("PRUEBA");assertThat(f.getSolicitud()).isEqualTo(solicitud);verify(service,never()).pagar(anyLong(),any());
  while(f.getFuentes().size()<20)f.agregarFuente();var limite=new ExtendedModelMap();limite.addAttribute("formMultiple",f);c.agregar(7L,f,filtro(),limite);assertThat(limite).containsKey("errorOperacion").doesNotContainKey("fuenteNueva");assertThat(f.getFuentes()).hasSize(20);
 }
 @Test void individualReutilizaLaMismaAyudaSinCrearPago(){
  var service=mock(PagoMultipleNominaService.class);var c=new PagoIndividualNominaController(service,mock(PreparacionNominaService.class),mock(EmpleadoService.class));var f=new PagoIndividualNominaForm();var m=new ExtendedModelMap();f.setReferencia("IND");c.agregar(f,filtro(),m);
  assertThat(m.get("fuenteNueva")).isEqualTo(1);assertThat(f.getFuentes()).hasSize(2);assertThat(f.getReferencia()).isEqualTo("IND");verifyNoInteractions(service);
 }
}
