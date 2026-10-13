package escuela.admin.dto;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class FiltroCoberturaPagoTest {
 @Test void normalizaYConservaCoberturaEnCadaPagina(){
  var f=new FiltroCatalogo(" Ana ","validado",null,0,25,null,null,"REGISTRO","anual_liquidado",2027).normalizado();
  f.validarCobertura();assertThat(f.cobertura()).isEqualTo("ANUAL_LIQUIDADO");
  assertThat(f.conPagina(2).anioCobertura()).isEqualTo(2027);assertThat(f.conPagina(2).cobertura()).isEqualTo("ANUAL_LIQUIDADO");
 }
 @Test void rechazaEstadoInventadoYAnioInvalido(){
  assertThatThrownBy(()->new FiltroCatalogo("","TODOS",null,0,25,null,null,"REGISTRO","INVENTADO",2027).normalizado().validarCobertura()).hasMessageContaining("cobertura");
  assertThatThrownBy(()->new FiltroCatalogo("","TODOS",null,0,25,null,null,"REGISTRO","ANUAL",10000).normalizado().validarCobertura()).hasMessageContaining("año");
 }
}
