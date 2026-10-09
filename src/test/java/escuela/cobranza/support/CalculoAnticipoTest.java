package escuela.cobranza.support;
import org.junit.jupiter.api.Test;
import java.math.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
class CalculoAnticipoTest {
 BigDecimal n(String s){return new BigDecimal(s);}
 @Test void beca20MasMesGratisDa8800(){var bases=Collections.nCopies(12,n("800.00"));var partes=CalculoAnticipo.beneficios(bases,"MENSUALIDAD",null,11);assertThat(partes.get(11)).isEqualByComparingTo("800");assertThat(partes.stream().reduce(BigDecimal.ZERO,BigDecimal::add)).isEqualByComparingTo("800");assertThat(n("9600").subtract(partes.get(11))).isEqualByComparingTo("8800");}
 @Test void porcentajesSeCalculanSobreBaseNetaNoSeSuman(){var partes=CalculoAnticipo.beneficios(Collections.nCopies(12,n("800.00")),"PORCENTAJE",n("10"),-1);assertThat(partes).allSatisfy(p->assertThat(p).isEqualByComparingTo("80"));assertThat(n("9600").subtract(partes.stream().reduce(BigDecimal.ZERO,BigDecimal::add))).isEqualByComparingTo("8640");}
 @Test void sustituirBecaPorMesBonificadoDa11000(){var partes=CalculoAnticipo.beneficios(Collections.nCopies(12,n("1000.00")),"MENSUALIDAD",null,0);assertThat(n("12000").subtract(partes.stream().reduce(BigDecimal.ZERO,BigDecimal::add))).isEqualByComparingTo("11000");}
 @Test void montoFijoSeDistribuyeSinPerderCentavosNiSuperarBases(){var partes=CalculoAnticipo.beneficios(List.of(n("0.01"),n("0.01"),n("0.01")),"MONTO",n("0.02"),-1);assertThat(partes.stream().reduce(BigDecimal.ZERO,BigDecimal::add)).isEqualByComparingTo("0.02");assertThat(partes).allSatisfy(p->assertThat(p).isBetween(n("0.00"),n("0.01")));}
 @Test void rechazaBeneficioCeroExcesivoOTotalYSeleccionSinMes(){for(String v:List.of("0","100","101"))assertThatThrownBy(()->CalculoAnticipo.beneficios(List.of(n("800")),"PORCENTAJE",n(v),-1)).isInstanceOf(escuela.common.exception.ReglaNegocioException.class);assertThatThrownBy(()->CalculoAnticipo.beneficios(List.of(n("800")),"MENSUALIDAD",null,-1)).isInstanceOf(escuela.common.exception.ReglaNegocioException.class);assertThatThrownBy(()->CalculoAnticipo.beneficios(List.of(n("800")),"MONTO",n("1.001"),-1)).isInstanceOf(escuela.common.exception.ReglaNegocioException.class);}
}
