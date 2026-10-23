package escuela.personal.dto;

import org.springframework.web.util.UriComponentsBuilder;

public record FiltroCandidatosNomina(String buscar,Long candidatoEmpleadoId,String candidatoEmpleadoTexto,Integer paginaCandidatos,Integer tamanioCandidatos) {
 public FiltroNomina comoFiltro(){return new FiltroNomina(buscar,"TODOS",null,null,null,null,candidatoEmpleadoId,candidatoEmpleadoTexto,null,null,paginaCandidatos,tamanioCandidatos).normalizado();}
 public String url(FiltroNomina origen,Long periodo,String grupo,int pagina){var f=comoFiltro();var o=origen.normalizado();return UriComponentsBuilder.fromPath("/admin/preparacion-nomina/seleccion-manual").queryParam("q",o.q()).queryParam("estado",o.estado()).queryParam("pagina",o.pagina()).queryParam("tamanio",o.tamanio()).queryParamIfPresent("filtroCalendarioId",java.util.Optional.ofNullable(o.filtroCalendarioId())).queryParamIfPresent("filtroPeriodoId",java.util.Optional.ofNullable(o.filtroPeriodoId())).queryParamIfPresent("filtroEmpleadoId",java.util.Optional.ofNullable(o.filtroEmpleadoId())).queryParamIfPresent("desde",java.util.Optional.ofNullable(o.desde())).queryParamIfPresent("hasta",java.util.Optional.ofNullable(o.hasta())).queryParam("periodoId",periodo).queryParam("grupo",grupo).queryParam("buscar",f.q()).queryParam("candidatoEmpleadoId",candidatoEmpleadoId==null?"":candidatoEmpleadoId).queryParam("paginaCandidatos",pagina).queryParam("tamanioCandidatos",f.tamanio()).build().encode().toUriString();}
}
