package escuela.personal.dto;

import escuela.common.exception.ReglaNegocioException;
import escuela.personal.service.OrganizacionLaboralService;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.*;

public record FiltroConceptoNomina(String q,String estado,String filtroNaturaleza,String filtroAplicacion,Long conceptoId,String conceptoTexto,Integer pagina,Integer tamanio) {
 public FiltroConceptoNomina normalizado(){return new FiltroConceptoNomina(OrganizacionLaboralService.texto(q,200),OrganizacionLaboralService.estado(estado),Set.of("PERCEPCION","DEDUCCION","APORTACION_PATRONAL").contains(Objects.toString(filtroNaturaleza,""))?filtroNaturaleza:"",Set.of("HABITUAL","OCASIONAL").contains(Objects.toString(filtroAplicacion,""))?filtroAplicacion:"",conceptoId,OrganizacionLaboralService.texto(conceptoTexto,450),Math.max(0,pagina==null?0:pagina),Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25);}
 public void validar(){if(conceptoId==null&&conceptoTexto!=null&&!conceptoTexto.isBlank())throw new ReglaNegocioException("Selecciona el concepto de la lista o limpia el campo para consultar todos.");}
 public FiltroConceptoNomina pagina(int p,int n){return new FiltroConceptoNomina(q,estado,filtroNaturaleza,filtroAplicacion,conceptoId,conceptoTexto,p,n);}
 public String url(String ruta){var f=normalizado();return UriComponentsBuilder.fromPath(ruta).queryParam("q",f.q()).queryParam("estado",f.estado()).queryParam("filtroNaturaleza",f.filtroNaturaleza()).queryParam("filtroAplicacion",f.filtroAplicacion()).queryParam("pagina",f.pagina()).queryParam("tamanio",f.tamanio()).queryParamIfPresent("conceptoId",Optional.ofNullable(f.conceptoId())).build().encode().toUriString();}
}
