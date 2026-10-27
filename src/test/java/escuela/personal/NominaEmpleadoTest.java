package escuela.personal;

import escuela.personal.dto.FiltroNominaEmpleado;
import escuela.personal.service.ConceptoEmpleadoService;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;

class NominaEmpleadoTest {
 @Test void filtrosConservanTodosLosParametrosSinRetornoExterno(){var f=new FiltroNominaEmpleado(" Transporte ","PROXIMA","PERCEPCION","SEMANAL",LocalDate.of(2027,6,1),LocalDate.of(2027,7,31),2,10).normalizado();f.validar();assertThat(f.url(6L,"/nuevo")).startsWith("/admin/empleados/6/nomina/nuevo?").contains("q=Transporte","estado=PROXIMA","naturaleza=PERCEPCION","frecuencia=SEMANAL","desde=2027-06-01","hasta=2027-07-31","pagina=2","tamanio=10");assertThat(f.pagina(3,100).pagina()).isEqualTo(3);}
 @Test void rangoInvertidoSeRechazaYValoresNoPermitidosNoLleganAlSql(){assertThatThrownBy(()->new FiltroNominaEmpleado("","","","",LocalDate.of(2027,7,1),LocalDate.of(2027,6,1),0,25).validar()).hasMessageContaining("Desde");var f=new FiltroNominaEmpleado(null,"SQL","SQL","SQL",null,null,-1,5000).normalizado();assertThat(f.estado()).isEqualTo("TODAS");assertThat(f.naturaleza()).isEmpty();assertThat(f.frecuencia()).isEmpty();assertThat(f.pagina()).isZero();assertThat(f.tamanio()).isEqualTo(25);}
 @Test void sueldoBaseNoSeDuplicaAunqueUseSeparadores(){for(String s:new String[]{"SUELDO_BASE","Sueldo base","SALARIO-BASE","sueldo"})assertThat(ConceptoEmpleadoService.esSueldoBase(s,"Otro")).isTrue();assertThat(ConceptoEmpleadoService.esSueldoBase("TRANS","Ayuda de transporte")).isFalse();}
 @Test void menuNoOfreceJornadasNiPlantillasYCapturaTieneModales()throws Exception{
  String nav=Files.readString(Path.of("src/main/resources/templates/admin/fragments/personal-navegacion.html"));assertThat(nav).doesNotContain("href=\"/admin/jornadas\"","href=\"/admin/esquemas\"");
  String form=Files.readString(Path.of("src/main/resources/templates/admin/empleado-nomina-form.html"));assertThat(form).contains("data-money-confirm=\"nomina-configurar\"","data-money","frecuenciaImporte","modalidad","periodoVersion");
  String detalle=Files.readString(Path.of("src/main/resources/templates/admin/empleado-nomina-detalle.html"));assertThat(detalle).contains("data-money-confirm=\"nomina-cancelar\"","data-money-confirm=\"nomina-finalizar\"");
 }
}
