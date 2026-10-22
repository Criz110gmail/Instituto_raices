package escuela.personal.dto;

import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;
import escuela.common.exception.ReglaNegocioException;
import escuela.personal.service.OrganizacionLaboralService;

public record FiltroProgramacionNomina(String q,String estado,String filtroFrecuencia,Long filtroCalendarioId,String filtroCalendarioTexto,
 Long filtroPeriodoId,String filtroPeriodoTexto,@DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate desde,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate hasta,Integer pagina,Integer tamanio) {
 public FiltroProgramacionNomina normalizado(boolean periodos){
  Set<String> estados=periodos?Set.of("PROGRAMADO","CANCELADO"):Set.of("ACTIVO","INACTIVO");
  return new FiltroProgramacionNomina(OrganizacionLaboralService.texto(q,200),estados.contains(Objects.toString(estado,""))?estado:"TODOS",
   Set.of("MENSUAL","QUINCENAL","SEMANAL").contains(Objects.toString(filtroFrecuencia,""))?filtroFrecuencia:"",filtroCalendarioId,OrganizacionLaboralService.texto(filtroCalendarioTexto,450),
   periodos?filtroPeriodoId:null,periodos?OrganizacionLaboralService.texto(filtroPeriodoTexto,450):"",periodos?desde:null,periodos?hasta:null,Math.max(0,pagina==null?0:pagina),Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25);
 }
 public void validar(){
  if(filtroCalendarioId==null&&filtroCalendarioTexto!=null&&!filtroCalendarioTexto.isBlank())throw regla("Selecciona el calendario de las sugerencias o limpia el filtro.");
  if(filtroPeriodoId==null&&filtroPeriodoTexto!=null&&!filtroPeriodoTexto.isBlank())throw regla("Selecciona el periodo de las sugerencias o limpia el filtro.");
  if(desde!=null&&hasta!=null&&desde.isAfter(hasta))throw regla("El inicio del rango no puede ser posterior al fin. El rango busca periodos que coincidan con esas fechas.");
 }
 public FiltroProgramacionNomina pagina(int p,int n){return new FiltroProgramacionNomina(q,estado,filtroFrecuencia,filtroCalendarioId,filtroCalendarioTexto,filtroPeriodoId,filtroPeriodoTexto,desde,hasta,p,n);}
 public String url(String ruta){var f=normalizado(ruta.contains("periodos-nomina"));return UriComponentsBuilder.fromPath(ruta).queryParam("q",f.q()).queryParam("estado",f.estado()).queryParam("filtroFrecuencia",f.filtroFrecuencia()).queryParam("pagina",f.pagina()).queryParam("tamanio",f.tamanio())
  .queryParamIfPresent("filtroCalendarioId",Optional.ofNullable(f.filtroCalendarioId())).queryParamIfPresent("filtroPeriodoId",Optional.ofNullable(f.filtroPeriodoId())).queryParamIfPresent("desde",Optional.ofNullable(f.desde())).queryParamIfPresent("hasta",Optional.ofNullable(f.hasta())).build().encode().toUriString();}
 private static ReglaNegocioException regla(String mensaje){return new ReglaNegocioException(mensaje);}
}
