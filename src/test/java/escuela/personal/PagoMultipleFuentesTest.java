package escuela.personal;

import escuela.personal.dto.PagoMultipleNominaForm;
import escuela.personal.dto.PagoIndividualNominaForm;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class PagoMultipleFuentesTest {
 @Test void iniciaConUnaFuenteTambienEnIndividual(){
  assertThat(new PagoMultipleNominaForm().getFuentes()).hasSize(1);
  assertThat(new PagoIndividualNominaForm().getFuentes()).hasSize(1);
 }
 @Test void agregaYQuitaSinPerderCapturaEInvalidaLaRevision(){
  var f=new PagoMultipleNominaForm();var banco=f.getFuentes().getFirst();
  banco.setCuentaId(12L);banco.setCuentaTexto("Banco de prueba");banco.setImporte(new BigDecimal("10000"));
  f.setReferencia("Referencia conservada");f.setMotivo("Motivo conservado");
  f.setSello("revision-anterior");f.setConfirmado(true);
  assertThat(f.agregarFuente()).isTrue();assertThat(f.getSello()).isNull();assertThat(f.isConfirmado()).isFalse();
  var caja=f.getFuentes().get(1);caja.setCuentaId(13L);caja.setImporte(new BigDecimal("4187.15"));
  assertThat(f.agregarFuente()).isTrue();var ultima=f.getFuentes().get(2);
  f.setSello("otra-revision");f.setConfirmado(true);
  assertThat(f.quitarFuente(1)).isTrue();assertThat(f.getFuentes()).containsExactly(banco,ultima);
  assertThat(banco.getImporte()).isEqualByComparingTo("10000");assertThat(f.getReferencia()).isEqualTo("Referencia conservada");
  assertThat(f.getMotivo()).isEqualTo("Motivo conservado");assertThat(f.getSello()).isNull();assertThat(f.isConfirmado()).isFalse();
  assertThat(f.quitarFuente(0)).isTrue();assertThat(f.getFuentes()).containsExactly(ultima);
  assertThat(f.quitarFuente(0)).isFalse();assertThat(f.quitarFuente(-1)).isFalse();assertThat(f.quitarFuente(50)).isFalse();
 }
 @Test void noPermiteMasDeVeinteFuentes(){
  var f=new PagoMultipleNominaForm();for(int i=1;i<20;i++)assertThat(f.agregarFuente()).isTrue();
  assertThat(f.agregarFuente()).isFalse();assertThat(f.getFuentes()).hasSize(20);
 }
 @Test void editarInvalidaSoloLaRevisionYConservaLaCaptura(){
  var f=new PagoIndividualNominaForm();f.setPeriodoId(3L);f.setEmpleadoId(6L);f.setVersion(7L);f.setFilaId(2L);f.setMotivoPagoId(4L);f.setReferencia("REPAGO");f.setMotivo("Motivo conservado");
  var solicitud=f.getSolicitud();var fuente=f.getFuentes().getFirst();fuente.setCuentaId(1L);fuente.setImporte(new BigDecimal("14187.15"));f.setSello("sello-anterior");f.setConfirmado(true);
  f.invalidarRevision();
  assertThat(f.getSello()).isNull();assertThat(f.isConfirmado()).isFalse();assertThat(f.getSolicitud()).isEqualTo(solicitud);
  assertThat(f.getPeriodoId()).isEqualTo(3L);assertThat(f.getEmpleadoId()).isEqualTo(6L);assertThat(f.getFilaId()).isEqualTo(2L);assertThat(f.getVersion()).isEqualTo(7L);assertThat(f.getMotivoPagoId()).isEqualTo(4L);assertThat(f.getReferencia()).isEqualTo("REPAGO");assertThat(f.getMotivo()).isEqualTo("Motivo conservado");assertThat(f.getFuentes()).containsExactly(fuente);assertThat(fuente.getImporte()).isEqualByComparingTo("14187.15");
 }
}
