package escuela.personal.dto;
import escuela.finanzas.dto.FiltroChequera;
import java.util.*;
import org.springframework.web.util.UriComponentsBuilder;
public record FiltroPrestamoPrograma(String q,String estado,Long empleadoId,String empleadoTexto,Integer pagina,Integer tamanio){
 public FiltroPrestamoPrograma normalizado(){return new FiltroPrestamoPrograma(FiltroChequera.texto(q,200),Set.of("BORRADOR","ACTIVO","LIQUIDADO","CANCELADO","VENCIDO").contains(Objects.toString(estado,""))?estado:"TODOS",empleadoId,FiltroChequera.texto(empleadoTexto,300),Math.max(0,Math.min(100000,pagina==null?0:pagina)),Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25);}
 public FiltroPrestamoPrograma pagina(int p){return new FiltroPrestamoPrograma(q,estado,empleadoId,empleadoTexto,p,tamanio);}
 public String url(String ruta){var f=normalizado();return UriComponentsBuilder.fromPath(ruta).queryParam("q",f.q).queryParam("estado",f.estado).queryParam("empleadoTexto",f.empleadoTexto).queryParamIfPresent("empleadoId",Optional.ofNullable(f.empleadoId)).queryParam("pagina",f.pagina).queryParam("tamanio",f.tamanio).build().encode().toUriString();}
}
