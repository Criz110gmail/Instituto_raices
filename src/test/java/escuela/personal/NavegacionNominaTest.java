package escuela.personal;

import java.nio.file.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class NavegacionNominaTest {
 @Test void todasLasPantallasDeAccionesIdentificanSuSeccion()throws Exception{
  for(var nombre:List.of("pagos","pago-empleado","pago-multiple","inicio","periodo-previa","periodo-empleado","detalle","empleado","tratamientos","tratamiento-form","pendientes","aplicacion","aplicacion-previa","nuevo","previa"))
   assertThat(Files.readString(Path.of("src/main/resources/templates/admin/nomina-"+nombre+".html"))).as(nombre).contains("nomina-comun :: ubicacion(");
 }
 @Test void etiquetaDeCancelacionNoCambiaElCodigoHistorico(){
  var i=new escuela.personal.service.PagoEmpleadoNominaService.Intento(1L,"REVERTIDO",null,"MXN",null,null,null,null,null,null,null,null,null,null);
  assertThat(i.estado()).isEqualTo("REVERTIDO");assertThat(i.estadoTexto()).isEqualTo("Pago cancelado");
 }
}
