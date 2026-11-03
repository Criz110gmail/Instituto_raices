package escuela.finanzas.dto;

import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;

public record FiltroCheque(String q,String estado,Long folio,Long chequeraId,String chequeraTexto,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta,Integer pagina,Integer tamanio) {
 public FiltroCheque normalizado(){return new FiltroCheque(FiltroChequera.texto(q,200),Set.of("EMITIDO","CANCELADO").contains(Objects.toString(estado,""))?estado:"TODOS",folio,chequeraId,FiltroChequera.texto(chequeraTexto,200),desde,hasta,Math.min(100000,Math.max(0,pagina==null?0:pagina)),Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25);}
 public FiltroCheque pagina(int p){return new FiltroCheque(q,estado,folio,chequeraId,chequeraTexto,desde,hasta,p,tamanio);}
 public String url(String ruta){var f=normalizado();return UriComponentsBuilder.fromPath(ruta).queryParam("q",f.q).queryParam("estado",f.estado).queryParam("pagina",f.pagina).queryParam("tamanio",f.tamanio).queryParam("chequeraTexto",f.chequeraTexto).queryParamIfPresent("chequeraId",Optional.ofNullable(f.chequeraId)).queryParamIfPresent("folio",Optional.ofNullable(f.folio)).queryParamIfPresent("desde",Optional.ofNullable(f.desde)).queryParamIfPresent("hasta",Optional.ofNullable(f.hasta)).build().encode().toUriString();}
}
