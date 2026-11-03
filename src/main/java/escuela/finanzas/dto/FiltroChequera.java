package escuela.finanzas.dto;

import java.util.*;
import org.springframework.web.util.UriComponentsBuilder;

public record FiltroChequera(String q,String estado,Long folio,Long chequeraId,String chequeraTexto,Integer pagina,Integer tamanio) {
 public FiltroChequera normalizado(){return new FiltroChequera(texto(q,200),Set.of("ACTIVO","INACTIVO").contains(Objects.toString(estado,""))?estado:"TODOS",folio,chequeraId,texto(chequeraTexto,200),Math.min(100000,Math.max(0,pagina==null?0:pagina)),Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25);}
 public FiltroChequera pagina(int p){return new FiltroChequera(q,estado,folio,chequeraId,chequeraTexto,p,tamanio);}
 public String url(String ruta){var f=normalizado();return UriComponentsBuilder.fromPath(ruta).queryParam("q",f.q).queryParam("estado",f.estado).queryParam("pagina",f.pagina).queryParam("tamanio",f.tamanio).queryParam("chequeraTexto",f.chequeraTexto).queryParamIfPresent("folio",Optional.ofNullable(f.folio)).queryParamIfPresent("chequeraId",Optional.ofNullable(f.chequeraId)).build().encode().toUriString();}
 public static String texto(String x,int max){return x==null?"":x.strip().substring(0,Math.min(x.strip().length(),max));}
}
