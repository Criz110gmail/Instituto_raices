package escuela.personal;
import escuela.personal.dto.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class FiltroPendienteNominaTest {
 @Test void seleccionUsaIdYConservaEtiquetaEnPaginaYExcel(){
  var f=new FiltroPendienteNomina("etiqueta no debe buscarse","PENDIENTE",0,3L,"DOC-PRUEBA-02 · Bruce Wayne").normalizado();
  assertThat(f.buscarPendiente()).isEmpty();assertThat(f.pagina(2).normalizado().empleadoPendienteId()).isEqualTo(3L);
  var origen=new FiltroNomina("","TODOS",null,null,null,null,null,null,null,null,0,25);
  assertThat(f.pagina(2).url(origen,"/pendientes")).contains("empleadoPendienteId=3","paginaPendiente=2","estadoPendiente=PENDIENTE");
  assertThat(f.url(origen,"/pendientes/excel")).contains("empleadoPendienteId=3","Bruce%20Wayne");
 }
 @Test void permiteBusquedaLibreYParametrosAnterioresYNormaliza(){
  assertThat(new FiltroPendienteNomina("Bruce",null,-1,null,null).normalizado()).isEqualTo(new FiltroPendienteNomina("Bruce","TODOS",0,null,"Bruce"));
  var f=new FiltroPendienteNomina("","INVALIDO",null,-3L," Bruce ").normalizado();assertThat(f.buscarPendiente()).isEqualTo("Bruce");assertThat(f.empleadoPendienteId()).isNull();assertThat(f.estadoPendiente()).isEqualTo("TODOS");
 }
}
