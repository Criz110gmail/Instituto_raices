package escuela.personal;

import escuela.personal.dto.*;
import escuela.admin.dto.ModuloCatalogo;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Set;
import static org.assertj.core.api.Assertions.*;

class ConceptoEmpleadoTest {
 @Test void filtrosIndependientesNormalizadosConRetornoSeguro(){
  var f=new FiltroConceptoEmpleado(" bono ","PROXIMA",3L,"Bruce",2L,"BON","PERCEPCION",LocalDate.of(2027,1,1),LocalDate.of(2027,2,1),-2,999).normalizado();f.validar();assertThat(f.pagina()).isZero();assertThat(f.tamanio()).isEqualTo(25);
  assertThat(f.url("/admin/conceptos-empleado/5")).contains("filtroEmpleadoId=3","filtroConceptoId=2","filtroNaturaleza=PERCEPCION","desde=2027-01-01","hasta=2027-02-01").doesNotContain("empleadoId=", "Bruce");
  assertThat(f.pagina(1,100).normalizado().pagina()).isEqualTo(1);
 }
 @Test void rechazaSeleccionNoConfirmadaYRangoInvertido(){
  assertThatThrownBy(()->new FiltroConceptoEmpleado("","",null,"Bruce",null,"","",null,null,0,25).validar()).hasMessageContaining("Selecciona el empleado");
  assertThatThrownBy(()->new FiltroConceptoEmpleado("","",null,"",null,"BON","",null,null,0,25).validar()).hasMessageContaining("Selecciona el concepto");
  assertThatThrownBy(()->new FiltroConceptoEmpleado("","",null,"",null,"","",LocalDate.of(2027,2,1),LocalDate.of(2027,1,1),0,25).validar()).hasMessageContaining("rango");
 }
 @Test void beanValidationExigeMontoFijoVigenciaMotivoYContexto(){
  try(var factory=Validation.buildDefaultValidatorFactory()){
   var v=factory.getValidator();var f=new ConceptoEmpleadoForm();assertThat(v.validate(f)).isNotEmpty();
   f.setEmpleadoId(1L);f.setEmpleadoTexto("EMP · Bruce");f.setConceptoId(1L);f.setConceptoTexto("BON · Bono");f.setConceptoVersion(0L);f.setLaboralVersion(0L);f.setImporte(new BigDecimal("500.00"));f.setInicio(LocalDate.of(2027,1,1));f.setMotivo("Bono habitual autorizado");assertThat(v.validate(f)).isEmpty();
   for(String monto: new String[]{"0.00","-1.00","1.123","100000000000000000.00"}){f.setImporte(new BigDecimal(monto));assertThat(v.validate(f)).isNotEmpty();}
  }
 }
 @Test void menuPrivadoRequierePaqueteLaboralYSalarial(){
  var m=ModuloCatalogo.CONCEPTOS_EMPLEADO;assertThat(m.visibleCon(Set.of("EMPLEADO_LEER"))).isFalse();assertThat(m.visibleCon(Set.of("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_LEER"))).isTrue();assertThat(m.rutaListado()).isEqualTo("/admin/conceptos-empleado");
 }
}
