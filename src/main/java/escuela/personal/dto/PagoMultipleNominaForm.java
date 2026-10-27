package escuela.personal.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.*;

@Getter @Setter
public class PagoMultipleNominaForm {
 private Long version;
 private Long filaId;
 private UUID solicitud=UUID.randomUUID();
 private Long motivoPagoId;
 private String motivoPagoTexto;
 private String referencia;
 private String motivo;
 private String sello;
 private boolean confirmado;
 private List<Fuente> fuentes=new ArrayList<>(List.of(new Fuente(),new Fuente(),new Fuente()));
 @Getter @Setter public static class Fuente {
  private Long cuentaId;
  private String cuentaTexto;
  private String medio="TRANSFERENCIA";
  private BigDecimal importe;
 }
}
