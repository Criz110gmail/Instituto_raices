package escuela.personal;
import escuela.personal.dto.*;import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class FiltroEmpleadoAutocompletadoTest {
 @Test void conservaIdYPaginaSinUsarEtiquetaConcatenadaComoTextoLibre(){
  var f=new FiltroEmpleado(1L,"  correo  ","ACTIVO",2,10,42L,"EMP-42 · Nombre Apellido").normalizado();
  assertThat(f.q()).isEqualTo("correo");assertThat(f.empleadoId()).isEqualTo(42L);assertThat(f.pagina(3).empleadoId()).isEqualTo(42L);assertThat(f.pagina(3).empleadoTexto()).isEqualTo("EMP-42 · Nombre Apellido");assertThatCode(f::validarSeleccion).doesNotThrowAnyException();
 }
 @Test void textoSinSeleccionNoSeConvierteEnConsultaDeTodos(){
  var f=new FiltroEmpleado(null,"","TODOS",0,25,null,"Nombre sin seleccionar");assertThatThrownBy(f::validarSeleccion).hasMessageContaining("Selecciona un empleado");
  var a=new FiltroOrganizacionLaboral("","TODAS",0,25,null,null,null,"Nombre sin seleccionar").normalizado(true);assertThatThrownBy(a::validarSeleccion).hasMessageContaining("Selecciona un empleado");
  assertThatCode(()->new FiltroEmpleado(null,"","TODOS",0,25).validarSeleccion()).doesNotThrowAnyException();
 }
}
