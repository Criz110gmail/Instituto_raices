package escuela.personal;

import escuela.personal.controller.*;
import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.common.exception.ReglaNegocioException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class PagoNominaEditarRevisionTest {
 private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder captura(String ruta){
  return post(ruta).param("version","7").param("filaId","2").param("periodoId","3").param("empleadoId","6")
   .param("solicitud","00000000-0000-0000-0000-000000000001").param("sello","revision-anterior").param("confirmado","true")
   .param("motivoPagoId","4").param("motivoPagoTexto","NOMINA").param("referencia","REPAGO-TEST").param("motivo","Justificación conservada")
   .param("fuentes[0].cuentaId","1").param("fuentes[0].cuentaTexto","BANCO QA").param("fuentes[0].importe","10000.00")
   .param("fuentes[1].cuentaId","2").param("fuentes[1].cuentaTexto","CAJA QA").param("fuentes[1].importe","4187.15")
   .param("q","origen").param("pagina","3");
 }
 private void conservado(PagoMultipleNominaForm f){
  assertThat(f.getSello()).isNull();assertThat(f.isConfirmado()).isFalse();assertThat(f.getVersion()).isEqualTo(7L);assertThat(f.getFilaId()).isEqualTo(2L);
  assertThat(f.getSolicitud()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));assertThat(f.getReferencia()).isEqualTo("REPAGO-TEST");assertThat(f.getMotivo()).isEqualTo("Justificación conservada");assertThat(f.getMotivoPagoId()).isEqualTo(4L);assertThat(f.getFuentes()).hasSize(2);assertThat(f.getFuentes().get(1).getImporte()).isEqualByComparingTo("4187.15");
 }
 @Test void editarPagoDeNominaVuelveACapturaSinRevisarNiPagar()throws Exception{
  var multiple=mock(PagoMultipleNominaService.class);var pagos=mock(PagoEmpleadoNominaService.class);var preparacion=mock(PreparacionNominaService.class);
  when(pagos.puedePagar()).thenReturn(true);
  var d=LocalDate.of(2027,7,1);
  when(preparacion.obtener(2L)).thenReturn(new PreparacionNominaService.Borrador(2L,3L,"Julio","AUTORIZADA",1,7L,"Motivo","JUL-TEST","Julio","Periodo independiente",d,d.plusDays(30),d.plusDays(30),"MXN","sello",1,new BigDecimal("14187.15")));
  when(pagos.fila(2L,2L)).thenReturn(new PagoEmpleadoNominaService.Fila(2L,6L,"EMP-QA","Diana",new BigDecimal("14187.15"),"MXN","PENDIENTE",null));
  var mvc=MockMvcBuilders.standaloneSetup(new PagoMultipleNominaController(multiple,pagos,preparacion)).build();
  var view=mvc.perform(captura("/admin/preparacion-nomina/2/pago-multiple/editar")).andReturn().getModelAndView();
  assertThat(view.getViewName()).isEqualTo("admin/nomina-pago-multiple");conservado((PagoMultipleNominaForm)view.getModel().get("formMultiple"));assertThat(view.getModel()).doesNotContainKey("vistaMultiple");
  assertThat(((FiltroNomina)view.getModel().get("filtro")).q()).isEqualTo("origen");verifyNoInteractions(multiple);
 }
 @Test void editarPagoDirectoConservaEmpleadoYPeriodoSinPagar()throws Exception{
  var multiple=mock(PagoMultipleNominaService.class);var empleados=mock(EmpleadoService.class);var preparacion=mock(PreparacionNominaService.class);
  when(multiple.calculoIndividual(any())).thenThrow(new ReglaNegocioException("Revisar cálculo antes de pagar"));
  var mvc=MockMvcBuilders.standaloneSetup(new PagoIndividualNominaController(multiple,preparacion,empleados)).build();
  var view=mvc.perform(captura("/admin/preparacion-nomina/pago-individual/editar")).andReturn().getModelAndView();
  var f=(PagoIndividualNominaForm)view.getModel().get("formMultiple");conservado(f);assertThat(f.getEmpleadoId()).isEqualTo(6L);assertThat(f.getPeriodoId()).isEqualTo(3L);assertThat(view.getModel()).doesNotContainKey("vistaMultiple");
  verify(empleados).autorizarSalarios(true);verify(multiple,never()).pagarIndividual(any());verify(multiple,never()).revisarIndividual(any());
 }
}
