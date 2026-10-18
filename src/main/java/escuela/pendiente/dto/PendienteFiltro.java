package escuela.pendiente.dto;

import escuela.common.exception.ReglaNegocioException;
import escuela.pendiente.entity.EstadoPendiente;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.Set;

public record PendienteFiltro(String q,String estado,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta,Integer pagina,Integer tamanio) {
 public PendienteFiltro normalizado(){
  String texto=q==null?"":q.strip();if(texto.length()>180)texto=texto.substring(0,180);
  String e=estado==null||estado.isBlank()?"ABIERTOS":estado;
  if(!Set.of("ABIERTOS","TODOS","VENCIDOS","HOY","PROXIMOS","PENDIENTE","EN_PROCESO","REALIZADO","CANCELADO").contains(e))throw new ReglaNegocioException("Selecciona un estado válido");
  return new PendienteFiltro(texto,e,desde,hasta,Math.max(0,pagina==null?0:pagina),Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25);
 }
 public void validar(){if(desde!=null&&hasta!=null&&hasta.isBefore(desde))throw new ReglaNegocioException("La fecha final debe ser igual o posterior a la inicial");}
 public PendienteFiltro pagina(int p,int t){return new PendienteFiltro(q,estado,desde,hasta,p,t);}
}
