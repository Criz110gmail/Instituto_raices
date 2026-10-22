package escuela.personal.dto;

import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;
import escuela.common.exception.ReglaNegocioException;
import escuela.personal.service.OrganizacionLaboralService;

public record FiltroConceptoEmpleado(String q,String estado,Long filtroEmpleadoId,String filtroEmpleadoTexto,
 Long filtroConceptoId,String filtroConceptoTexto,String filtroNaturaleza,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta,Integer pagina,Integer tamanio) {
 public FiltroConceptoEmpleado normalizado(){return new FiltroConceptoEmpleado(OrganizacionLaboralService.texto(q,200),
  Set.of("ACTUAL","PROXIMA","ANTERIOR","CANCELADA").contains(Objects.toString(estado,""))?estado:"TODAS",filtroEmpleadoId,OrganizacionLaboralService.texto(filtroEmpleadoTexto,450),filtroConceptoId,OrganizacionLaboralService.texto(filtroConceptoTexto,450),
  Set.of("PERCEPCION","DEDUCCION","APORTACION_PATRONAL").contains(Objects.toString(filtroNaturaleza,""))?filtroNaturaleza:"",desde,hasta,Math.max(0,pagina==null?0:pagina),Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25);}
 public void validar(){
  if(filtroEmpleadoId==null&&filtroEmpleadoTexto!=null&&!filtroEmpleadoTexto.isBlank())throw regla("Selecciona el empleado de la lista o limpia el filtro.");
  if(filtroConceptoId==null&&filtroConceptoTexto!=null&&!filtroConceptoTexto.isBlank())throw regla("Selecciona el concepto de la lista o limpia el filtro.");
  if(desde!=null&&hasta!=null&&desde.isAfter(hasta))throw regla("El inicio del rango no puede ser posterior al fin. El rango busca vigencias que coincidan con esas fechas.");
 }
 public FiltroConceptoEmpleado pagina(int p,int n){return new FiltroConceptoEmpleado(q,estado,filtroEmpleadoId,filtroEmpleadoTexto,filtroConceptoId,filtroConceptoTexto,filtroNaturaleza,desde,hasta,p,n);}
 public String url(String ruta){var f=normalizado();return UriComponentsBuilder.fromPath(ruta).queryParam("q",f.q()).queryParam("estado",f.estado()).queryParam("filtroNaturaleza",f.filtroNaturaleza()).queryParam("pagina",f.pagina()).queryParam("tamanio",f.tamanio())
  .queryParamIfPresent("filtroEmpleadoId",Optional.ofNullable(f.filtroEmpleadoId())).queryParamIfPresent("filtroConceptoId",Optional.ofNullable(f.filtroConceptoId())).queryParamIfPresent("desde",Optional.ofNullable(f.desde())).queryParamIfPresent("hasta",Optional.ofNullable(f.hasta())).build().encode().toUriString();}
 private static ReglaNegocioException regla(String m){return new ReglaNegocioException(m);}
}
