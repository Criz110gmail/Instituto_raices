package escuela.cobranza.service;

import escuela.cobranza.entity.Cargo;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AcuerdoAnticipadoPeriodoTest {
 @Test void muestraPeriodoMensualConDiaMesAnio(){
  var cargo=new Cargo();cargo.setPeriodoCobroInicio(LocalDate.of(2027,1,1));cargo.setPeriodoCobroFin(LocalDate.of(2027,1,31));
  assertThat(AcuerdoAnticipadoService.periodo(cargo)).isEqualTo("01/01/2027 — 31/01/2027");
 }
 @Test void diciembreYFebreroBisiestoConservanLasFechasExactas(){
  var cargo=new Cargo();cargo.setPeriodoCobroInicio(LocalDate.of(2027,12,1));cargo.setPeriodoCobroFin(LocalDate.of(2027,12,31));
  assertThat(AcuerdoAnticipadoService.periodo(cargo)).isEqualTo("01/12/2027 — 31/12/2027");
  cargo.setPeriodoCobroInicio(LocalDate.of(2028,2,1));cargo.setPeriodoCobroFin(LocalDate.of(2028,2,29));
  assertThat(AcuerdoAnticipadoService.periodo(cargo)).isEqualTo("01/02/2028 — 29/02/2028");
 }
}
