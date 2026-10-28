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
 private List<Fuente> fuentes=new ArrayList<>(List.of(new Fuente()));
 public boolean agregarFuente(){
  if(fuentes==null||fuentes.isEmpty()||fuentes.size()>=20)return false;
  fuentes.add(new Fuente());invalidarRevision();return true;
 }
 public boolean quitarFuente(int indice){
  if(fuentes==null||fuentes.size()<=1||indice<0||indice>=fuentes.size())return false;
  fuentes.remove(indice);invalidarRevision();return true;
 }
 public void invalidarRevision(){sello=null;confirmado=false;}
 @Getter @Setter public static class Fuente {
  private Long cuentaId;
  private String cuentaTexto;
  // Sólo presentación; al revisar/pagar se deriva nuevamente del tipo de cuenta en BD.
  private String medio;
  private BigDecimal importe;
 }
}
