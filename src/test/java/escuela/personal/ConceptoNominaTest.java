package escuela.personal;

import escuela.personal.dto.*;
import escuela.personal.service.ConceptoNominaService.Concepto;
import escuela.admin.dto.ModuloCatalogo;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ConceptoNominaTest {
 @Test void efectosSeparanPagoDelEmpleadoYCostoPatronal(){
  assertThat(new Concepto(1L,"BON","Bono","","PERCEPCION","HABITUAL",true,0L).efecto()).isEqualTo("Suma al empleado");
  assertThat(new Concepto(1L,"DES","Descuento","","DEDUCCION","OCASIONAL",true,0L).efecto()).isEqualTo("Resta al empleado");
  assertThat(new Concepto(1L,"PAT","Aportación","","APORTACION_PATRONAL","HABITUAL",true,0L).efecto()).contains("no reduce el pago");
 }
 @Test void filtrosAcotadosConservanSeleccionEnExcelYNoColisionanConCaptura(){
  var f=new FiltroConceptoNomina(" Bono ","ACTIVO","PERCEPCION","HABITUAL",7L,"BON · Bono",-8,1000).normalizado();
  assertThat(f.pagina()).isZero();assertThat(f.tamanio()).isEqualTo(25);assertThat(f.pagina(2,100).conceptoId()).isEqualTo(7L);
  assertThat(f.url("/admin/conceptos-nomina")).contains("filtroNaturaleza=PERCEPCION","filtroAplicacion=HABITUAL","conceptoId=7").doesNotContain("BON%20");
  assertThatThrownBy(()->new FiltroConceptoNomina("","TODOS","","",null,"Bono sin seleccionar",0,25).validar()).hasMessageContaining("Selecciona el concepto");
 }
 @Test void menuExigePaquetePrivadoYUsaPantallasSeparadas(){
  assertThat(ModuloCatalogo.CONCEPTOS_NOMINA.visibleCon(Set.of("EMPLEADO_LEER"))).isFalse();
  assertThat(ModuloCatalogo.CONCEPTOS_NOMINA.visibleCon(Set.of("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_LEER"))).isTrue();
  assertThat(ModuloCatalogo.CONCEPTOS_NOMINA.rutaListado()).isEqualTo("/admin/conceptos-nomina");
 }
 @Test void formValidaCamposObligatoriosYEnums(){
  try(var factory=jakarta.validation.Validation.buildDefaultValidatorFactory()){
   var validator=factory.getValidator();var form=new ConceptoNominaForm();assertThat(validator.validate(form)).hasSize(4);
   form.setCodigo("BON-PUNT");form.setNombre("Bono de puntualidad");form.setNaturaleza("PERCEPCION");form.setAplicacion("HABITUAL");assertThat(validator.validate(form)).isEmpty();
   form.setNaturaleza("PAGO");form.setAplicacion("AUTOMATICO");form.setVersion(-1L);assertThat(validator.validate(form)).hasSize(3);
  }
 }
}
