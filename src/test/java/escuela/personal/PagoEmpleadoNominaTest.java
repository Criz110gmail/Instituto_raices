package escuela.personal;
import escuela.personal.dto.*;
import escuela.personal.service.PagoEmpleadoNominaService;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class PagoEmpleadoNominaTest {
 @Test void capturaNoAceptaImporteEmpleadoInstitucionOMovimiento(){assertThat(Arrays.stream(PagoEmpleadoNominaForm.class.getDeclaredFields()).map(java.lang.reflect.Field::getName).toList()).doesNotContain("monto","importe","empleadoId","institucionId","movimientoId");try(var f=jakarta.validation.Validation.buildDefaultValidatorFactory()){assertThat(f.getValidator().validate(new PagoEmpleadoNominaForm())).isNotEmpty();}}
 @Test void resumenNoMezclaPagosRevertidosConVigentes(){var r=new PagoEmpleadoNominaService.Resumen(2,1,new BigDecimal("1500"),new BigDecimal("1000"));assertThat(r.pendiente()).isEqualByComparingTo("500");}
 @Test void filtrosConservanOrigenYNoCodificanDosVeces(){var origen=new FiltroNomina("Origen con espacio","AUTORIZADA",null,null,null,null,null,null,null,null,2,25);var f=new FiltroPagoNomina(" Carlos ","PENDIENTE",-1,300).normalizado();assertThat(f.tamanioPago()).isEqualTo(25);assertThat(f.pagina(3).url(origen,"/admin/preparacion-nomina/1/pagos")).contains("q=Origen%20con%20espacio","pagina=2","paginaPago=3","estado=AUTORIZADA").doesNotContain("%25");}
 @Test void empleadoSeleccionadoSeFiltraPorIdNoPorEtiquetaConcatenada(){
  var f=new FiltroPagoNomina("texto anterior","PAGADO",0,25,6L,"EMP-06 · Diana de prueba").normalizado();
  assertThat(f.buscarPago()).isEmpty();assertThat(f.empleadoPagoId()).isEqualTo(6L);assertThat(f.empleadoPagoTexto()).isEqualTo("EMP-06 · Diana de prueba");
  var origen=new FiltroNomina("","TODOS",null,null,null,null,null,null,null,null,0,25);
  assertThat(f.pagina(2).url(origen,"/pagos")).contains("empleadoPagoId=6","empleadoPagoTexto=EMP-06","paginaPago=2");
  assertThat(f.normalizado()).isEqualTo(f);
 }
 @Test void textoLibreSinSeleccionConservaBusquedaYLimpiarNoFiltra(){
  var libre=new FiltroPagoNomina(null,"TODOS",0,25,null," Diana ").normalizado();assertThat(libre.buscarPago()).isEqualTo("Diana");
  assertThat(new FiltroPagoNomina(null,null,null,null,null,"").normalizado().buscarPago()).isEmpty();
 }
}
