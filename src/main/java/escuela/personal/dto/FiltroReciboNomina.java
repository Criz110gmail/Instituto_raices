package escuela.personal.dto;

import escuela.common.exception.ReglaNegocioException;
import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;

public record FiltroReciboNomina(String q, String estado, Long empleadoId, String empleadoTexto,
        Long periodoId, String periodoTexto,
        @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
        @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta, Integer pagina, Integer tamanio) {
    public FiltroReciboNomina normalizado() {
        return new FiltroReciboNomina(texto(q,200), Set.of("PAGADO","CANCELADO","NETO_CERO","HISTORICO").contains(Objects.toString(estado,""))?estado:"TODOS",
                empleadoId,texto(empleadoTexto,450),periodoId,texto(periodoTexto,450),desde,hasta,
                Math.max(0,Math.min(100000,pagina==null?0:pagina)),Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25);
    }
    private static String texto(String s,int max){return s==null?"":s.strip().substring(0,Math.min(s.strip().length(),max));}
    public void validar() {
        if(desde!=null&&hasta!=null&&desde.isAfter(hasta))throw new ReglaNegocioException("La fecha Desde no puede ser posterior a Hasta.");
        if(empleadoId==null&&!empleadoTexto.isBlank())throw new ReglaNegocioException("Selecciona al empleado de las sugerencias o limpia el campo.");
        if(periodoId==null&&!periodoTexto.isBlank())throw new ReglaNegocioException("Selecciona el periodo de las sugerencias o limpia el campo.");
    }
    public FiltroReciboNomina pagina(int p){return new FiltroReciboNomina(q,estado,empleadoId,empleadoTexto,periodoId,periodoTexto,desde,hasta,p,tamanio).normalizado();}
    public String url(String ruta){var f=normalizado();return UriComponentsBuilder.fromPath(ruta).queryParam("q",f.q).queryParam("estado",f.estado)
            .queryParam("empleadoTexto",f.empleadoTexto).queryParam("periodoTexto",f.periodoTexto)
            .queryParamIfPresent("empleadoId",Optional.ofNullable(f.empleadoId)).queryParamIfPresent("periodoId",Optional.ofNullable(f.periodoId))
            .queryParamIfPresent("desde",Optional.ofNullable(f.desde)).queryParamIfPresent("hasta",Optional.ofNullable(f.hasta))
            .queryParam("pagina",f.pagina).queryParam("tamanio",f.tamanio).build().encode().toUriString();}
}
