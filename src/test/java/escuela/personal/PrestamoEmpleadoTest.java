package escuela.personal;
import escuela.personal.dto.*;
import escuela.personal.service.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class PrestamoEmpleadoTest {
 @Test void filtroNoColisionaConOrigenYConservaUnicode(){var origen=new FiltroConfiguracionNomina("Prueba con espacio",null,"",null,"","TODOS",LocalDate.of(2030,6,1),2);var f=new FiltroPrestamoEmpleado(" Carlos ","ACTIVO",-1,1000).normalizado();assertThat(f.paginaPrestamo()).isZero();assertThat(f.tamanioPrestamo()).isEqualTo(25);assertThat(f.pagina(3).url(origen,"/admin/configuracion-nomina/1/prestamos")).contains("q=Prueba%20con%20espacio","pagina=2","paginaPrestamo=3","estadoPrestamo=ACTIVO").doesNotContain("%25","empleadoId=");}
 @Test void contratoNoPermiteCapturarSaldoEstadoOPropietario(){var nombres=Arrays.stream(PrestamoEmpleadoForm.class.getDeclaredFields()).map(java.lang.reflect.Field::getName).toList();assertThat(nombres).doesNotContain("empleadoId","institucionId","saldo","estado","moneda");try(var factory=jakarta.validation.Validation.buildDefaultValidatorFactory()){var v=factory.getValidator();assertThat(v.validate(new PrestamoEmpleadoForm())).isNotEmpty();var f=new ConceptoNominaForm();f.setCodigo("PRE");f.setNombre("Préstamo");f.setNaturaleza("DEDUCCION");f.setAplicacion("CON_SALDO");assertThat(v.validate(f)).isEmpty();}}
 @Test void ultimaCuotaSeLimitaASaldoNoAMesesTranscurridos(){var p=new PrestamoEmpleadoService.Prestamo(1L,1L,1L,0L,"PRE · Préstamo",new BigDecimal("3000"),new BigDecimal("500"),"MXN",LocalDate.of(2030,6,1),"ACTIVO","Prueba","Prueba",1L,UUID.randomUUID(),new BigDecimal("125.50"));assertThat(p.cuota()).isEqualByComparingTo("125.50");assertThat(p.folio()).isEqualTo("PRE-000001");}
}
