package escuela.personal.dto;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.Set;
public record FiltroSalarial(String q,String estado,@DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta,Integer pagina,Integer tamanio) {
 public FiltroSalarial normalizado(){return new FiltroSalarial(q==null?"":q.strip().substring(0,Math.min(q.strip().length(),200)),
  Set.of("ACTUAL","PROXIMA","ANTERIOR","CANCELADA").contains(estado==null?"":estado)?estado:"TODAS",desde,hasta,
  Math.max(0,pagina==null?0:pagina),Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25);}
 public FiltroSalarial pagina(int p,int n){return new FiltroSalarial(q,estado,desde,hasta,p,n);}
}
