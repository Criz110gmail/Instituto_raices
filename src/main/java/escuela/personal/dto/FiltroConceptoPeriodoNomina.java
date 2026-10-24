package escuela.personal.dto;
import java.util.*;
import org.springframework.web.util.UriComponentsBuilder;
import escuela.personal.service.OrganizacionLaboralService;
public record FiltroConceptoPeriodoNomina(String buscarOcasional,String estadoOcasional,Long periodoOcasionalId,String periodoOcasionalTexto,Integer paginaOcasional,Integer tamanioOcasional) {
 public FiltroConceptoPeriodoNomina normalizado(){return new FiltroConceptoPeriodoNomina(OrganizacionLaboralService.texto(buscarOcasional,200),Set.of("ACTIVA","CANCELADA").contains(Objects.toString(estadoOcasional,""))?estadoOcasional:"TODOS",periodoOcasionalId,OrganizacionLaboralService.texto(periodoOcasionalTexto,450),Math.max(0,paginaOcasional==null?0:paginaOcasional),Set.of(10,25,50,100).contains(tamanioOcasional==null?0:tamanioOcasional)?tamanioOcasional:25);}
 public FiltroConceptoPeriodoNomina pagina(int p){return new FiltroConceptoPeriodoNomina(buscarOcasional,estadoOcasional,periodoOcasionalId,periodoOcasionalTexto,p,tamanioOcasional);}
 public String url(FiltroConfiguracionNomina origen,String ruta){var f=normalizado();return origen.url(ruta)+"&"+UriComponentsBuilder.newInstance().queryParam("buscarOcasional",f.buscarOcasional()).queryParam("estadoOcasional",f.estadoOcasional()).queryParam("paginaOcasional",f.paginaOcasional()).queryParam("tamanioOcasional",f.tamanioOcasional()).queryParamIfPresent("periodoOcasionalId",Optional.ofNullable(f.periodoOcasionalId())).build().encode().getQuery();}
}
