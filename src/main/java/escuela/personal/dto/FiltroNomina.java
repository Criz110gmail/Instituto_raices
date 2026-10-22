package escuela.personal.dto;

import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;
import escuela.common.exception.ReglaNegocioException;
import escuela.personal.service.OrganizacionLaboralService;

public record FiltroNomina(String q,String estado,Long filtroCalendarioId,String filtroCalendarioTexto,Long filtroPeriodoId,String filtroPeriodoTexto,
 Long filtroEmpleadoId,String filtroEmpleadoTexto,@DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate desde,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate hasta,Integer pagina,Integer tamanio) {
 public FiltroNomina normalizado(){return new FiltroNomina(OrganizacionLaboralService.texto(q,200),Set.of("BORRADOR","CANCELADO").contains(Objects.toString(estado,""))?estado:"TODOS",filtroCalendarioId,OrganizacionLaboralService.texto(filtroCalendarioTexto,450),filtroPeriodoId,OrganizacionLaboralService.texto(filtroPeriodoTexto,450),filtroEmpleadoId,OrganizacionLaboralService.texto(filtroEmpleadoTexto,450),desde,hasta,Math.max(0,pagina==null?0:pagina),Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25);}
 public void validar(){if(filtroCalendarioId==null&&filtroCalendarioTexto!=null&&!filtroCalendarioTexto.isBlank()||filtroPeriodoId==null&&filtroPeriodoTexto!=null&&!filtroPeriodoTexto.isBlank()||filtroEmpleadoId==null&&filtroEmpleadoTexto!=null&&!filtroEmpleadoTexto.isBlank())throw new ReglaNegocioException("Selecciona las ayudas de las sugerencias o limpia el texto pendiente.");if(desde!=null&&hasta!=null&&desde.isAfter(hasta))throw new ReglaNegocioException("El inicio del rango no puede ser posterior al fin.");}
 public FiltroNomina pagina(int p,int n){return new FiltroNomina(q,estado,filtroCalendarioId,filtroCalendarioTexto,filtroPeriodoId,filtroPeriodoTexto,filtroEmpleadoId,filtroEmpleadoTexto,desde,hasta,p,n);}
 public String url(String ruta){var f=normalizado();return UriComponentsBuilder.fromPath(ruta).queryParam("q",f.q()).queryParam("estado",f.estado()).queryParam("pagina",f.pagina()).queryParam("tamanio",f.tamanio()).queryParamIfPresent("filtroCalendarioId",Optional.ofNullable(f.filtroCalendarioId())).queryParamIfPresent("filtroPeriodoId",Optional.ofNullable(f.filtroPeriodoId())).queryParamIfPresent("filtroEmpleadoId",Optional.ofNullable(f.filtroEmpleadoId())).queryParamIfPresent("desde",Optional.ofNullable(f.desde())).queryParamIfPresent("hasta",Optional.ofNullable(f.hasta())).build().encode().toUriString();}
}
