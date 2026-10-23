package escuela.personal;
import escuela.personal.dto.*;
import escuela.admin.dto.ModuloCatalogo;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class ConfiguracionNominaTest {
 @Test void filtrosConservanFechaIdsYPagina(){var f=new FiltroConfiguracionNomina(" Bruce ",3L,"",4L,"","SIN_SUELDO",null,-1).normalizado(LocalDate.of(2027,1,15));f.validar();assertThat(f.fecha()).isEqualTo(LocalDate.of(2027,1,15));assertThat(f.pagina()).isZero();assertThat(f.pagina(2).url("/admin/configuracion-nomina/3")).contains("empleadoId=3","filtroCalendarioId=4","fecha=2027-01-15","pagina=2","q=Bruce");}
 @Test void textoSinSeleccionNoConsultaTodos(){assertThatThrownBy(()->new FiltroConfiguracionNomina("",null,"Bruce",null,"","TODOS",null,0).validar()).hasMessageContaining("Selecciona el empleado");assertThatThrownBy(()->new FiltroConfiguracionNomina("",null,"",null,"Mensual","TODOS",null,0).validar()).hasMessageContaining("Selecciona el calendario");}
 @Test void moduloPrivadoSinConcesiones(){var m=ModuloCatalogo.CONFIGURACION_NOMINA;assertThat(m.rutaListado()).isEqualTo("/admin/configuracion-nomina");assertThat(m.visibleCon(Set.of("EMPLEADO_LEER"))).isFalse();assertThat(m.visibleCon(Set.of("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_LEER"))).isTrue();}
}
