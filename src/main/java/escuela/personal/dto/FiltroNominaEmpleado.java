package escuela.personal.dto;

import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;
import escuela.common.exception.ReglaNegocioException;
import escuela.personal.service.OrganizacionLaboralService;

public record FiltroNominaEmpleado(String q,String estado,String naturaleza,String frecuencia,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta,Integer pagina,Integer tamanio,Long filtroConceptoId,String filtroConceptoTexto) {
 public FiltroNominaEmpleado(String q,String estado,String naturaleza,String frecuencia,LocalDate desde,LocalDate hasta,Integer pagina,Integer tamanio){this(q,estado,naturaleza,frecuencia,desde,hasta,pagina,tamanio,null,"");}
 public static FiltroNominaEmpleado inicial(){return new FiltroNominaEmpleado("","TODAS","","",null,null,0,25);}
 public FiltroNominaEmpleado normalizado(){return new FiltroNominaEmpleado(OrganizacionLaboralService.texto(q,200),
  Set.of("ACTUAL","PROXIMA","ANTERIOR","CANCELADA").contains(Objects.toString(estado,""))?estado:"TODAS",
  Set.of("PERCEPCION","DEDUCCION","APORTACION_PATRONAL").contains(Objects.toString(naturaleza,""))?naturaleza:"",
  Set.of("MENSUAL","QUINCENAL","SEMANAL","UNICA","LEGADO").contains(Objects.toString(frecuencia,""))?frecuencia:"",
  desde,hasta,Math.max(0,pagina==null?0:pagina),Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25,filtroConceptoId,OrganizacionLaboralService.texto(filtroConceptoTexto,450));}
 public void validar(){if(desde!=null&&hasta!=null&&desde.isAfter(hasta))throw new ReglaNegocioException("La fecha Desde no puede ser posterior a Hasta.");if(filtroConceptoId==null&&filtroConceptoTexto!=null&&!filtroConceptoTexto.isBlank())throw new ReglaNegocioException("Selecciona el concepto de la lista o limpia el filtro.");}
 public FiltroNominaEmpleado pagina(int p,int n){return new FiltroNominaEmpleado(q,estado,naturaleza,frecuencia,desde,hasta,p,n,filtroConceptoId,filtroConceptoTexto).normalizado();}
 public String url(Long empleado,String sufijo){var f=normalizado();return UriComponentsBuilder.fromPath("/admin/empleados/"+empleado+"/nomina"+sufijo)
  .queryParam("q",f.q()).queryParam("estado",f.estado()).queryParam("naturaleza",f.naturaleza()).queryParam("frecuencia",f.frecuencia())
  .queryParam("pagina",f.pagina()).queryParam("tamanio",f.tamanio()).queryParamIfPresent("filtroConceptoId",Optional.ofNullable(f.filtroConceptoId())).queryParamIfPresent("desde",Optional.ofNullable(f.desde())).queryParamIfPresent("hasta",Optional.ofNullable(f.hasta())).build().encode().toUriString();}
}
