package escuela.personal;

import escuela.personal.dto.FiltrosCatalogosPersonal;
import escuela.personal.dto.FiltroSalarial;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.*;

class FiltrosCatalogosPersonalTest {
 @Test void exigeSeleccionSinConvertirTextoEnConsultaDeTodos(){
  var f=new FiltrosCatalogosPersonal();f.setFiltroEsquemaTexto("ESQ-DOC-MAT-TEST");
  assertThatThrownBy(f::validar).hasMessageContaining("Selecciona la configuración de puesto y sueldo");
  f.setFiltroEsquemaId(12L);assertThatCode(f::validar).doesNotThrowAnyException();
  f.setFiltroPuestoTexto("Docente");assertThatThrownBy(f::validar).hasMessageContaining("puesto");
  f.setFiltroPuestoId(5L);f.setFiltroJornadaTexto("Matutina");assertThatThrownBy(f::validar).hasMessageContaining("jornada");
 }
 @Test void retornoSoloIncluyeIdsNuncaTextoNiUrlExterna(){
  var f=new FiltrosCatalogosPersonal();f.setFiltroPuestoId(2L);f.setFiltroJornadaId(3L);f.setFiltroEsquemaId(4L);f.setFiltroPuestoTexto("https://otro.invalid");
  assertThat(f.agregarA(UriComponentsBuilder.fromPath("/admin/esquemas")).build().toUriString()).isEqualTo("/admin/esquemas?filtroPuestoId=2&filtroJornadaId=3&filtroEsquemaId=4");
  assertThat(new FiltrosCatalogosPersonal().agregarA(UriComponentsBuilder.fromPath("/admin/puestos")).build().toUriString()).isEqualTo("/admin/puestos");
 }
 @Test void filtroSalarialConservaSeleccionEnCadaBloqueDeExcel(){
  var f=new FiltroSalarial("","TODAS",null,null,0,25,8L,"Puesto seleccionado");
  assertThat(f.normalizado().pagina(1,100).filtroPuestoId()).isEqualTo(8L);
  assertThat(f.normalizado().pagina(1,100).filtroPuestoTexto()).isEqualTo("Puesto seleccionado");
 }
}
