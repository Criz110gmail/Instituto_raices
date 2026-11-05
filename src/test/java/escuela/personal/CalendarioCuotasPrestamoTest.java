package escuela.personal;
import escuela.personal.service.CalendarioCuotasPrestamo;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;
class CalendarioCuotasPrestamoTest {
 @Test void conservaCapitalInteresYCentavos(){var cuotas=CalendarioCuotasPrestamo.calcular(new BigDecimal("100"),new BigDecimal("10"),3,LocalDate.of(2026,11,15),"MENSUAL");assertThat(cuotas.stream().map(c->c.capital()).reduce(BigDecimal.ZERO,BigDecimal::add)).isEqualByComparingTo("100");assertThat(cuotas.stream().map(c->c.interes()).reduce(BigDecimal.ZERO,BigDecimal::add)).isEqualByComparingTo("10");assertThat(cuotas.getLast().total()).isEqualByComparingTo("36.68");}
 @Test void quincenasRespetanFebreroYNoGeneranTresCuotas(){var q=CalendarioCuotasPrestamo.calcular(new BigDecimal("6000"),BigDecimal.ZERO,4,LocalDate.of(2028,1,31),"QUINCENAL");assertThat(q.stream().map(c->c.fecha())).containsExactly(LocalDate.of(2028,1,31),LocalDate.of(2028,2,15),LocalDate.of(2028,2,29),LocalDate.of(2028,3,15));}
 @Test void mensualNoPierdeDiaAncla(){var q=CalendarioCuotasPrestamo.calcular(new BigDecimal("300"),BigDecimal.ZERO,3,LocalDate.of(2027,1,31),"MENSUAL");assertThat(q.get(1).fecha()).isEqualTo(LocalDate.of(2027,2,28));assertThat(q.get(2).fecha()).isEqualTo(LocalDate.of(2027,3,31));assertThatThrownBy(()->CalendarioCuotasPrestamo.calcular(BigDecimal.ONE,BigDecimal.ZERO,600,LocalDate.now(),"SEMANAL")).hasMessageContaining("demasiado pequeño");}
}
