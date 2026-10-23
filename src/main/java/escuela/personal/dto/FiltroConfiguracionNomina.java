package escuela.personal.dto;

import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;
import escuela.common.exception.ReglaNegocioException;
import escuela.personal.service.OrganizacionLaboralService;

public record FiltroConfiguracionNomina(String q,Long empleadoId,String empleadoTexto,
 Long filtroCalendarioId,String filtroCalendarioTexto,String estado,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fecha,Integer pagina) {
 public FiltroConfiguracionNomina normalizado(LocalDate hoy){return new FiltroConfiguracionNomina(
  OrganizacionLaboralService.texto(q,200),empleadoId,OrganizacionLaboralService.texto(empleadoTexto,450),
  filtroCalendarioId,OrganizacionLaboralService.texto(filtroCalendarioTexto,450),
  Set.of("CON_CALENDARIO","SIN_CALENDARIO","SIN_SUELDO").contains(Objects.toString(estado,""))?estado:"TODOS",
  fecha==null?hoy:fecha,Math.max(0,pagina==null?0:pagina));}
 public void validar(){if(empleadoId==null&&empleadoTexto!=null&&!empleadoTexto.isBlank())throw new ReglaNegocioException("Selecciona el empleado de la ayuda o limpia el filtro.");if(filtroCalendarioId==null&&filtroCalendarioTexto!=null&&!filtroCalendarioTexto.isBlank())throw new ReglaNegocioException("Selecciona el calendario de la ayuda o limpia el filtro.");}
 public FiltroConfiguracionNomina pagina(int p){return new FiltroConfiguracionNomina(q,empleadoId,empleadoTexto,filtroCalendarioId,filtroCalendarioTexto,estado,fecha,p);}
 public String url(String ruta){return UriComponentsBuilder.fromPath(ruta).queryParam("q",Objects.toString(q,""))
  .queryParam("estado",Objects.toString(estado,"TODOS")).queryParam("pagina",pagina==null?0:pagina)
  .queryParamIfPresent("empleadoId",Optional.ofNullable(empleadoId)).queryParamIfPresent("filtroCalendarioId",Optional.ofNullable(filtroCalendarioId))
  .queryParamIfPresent("fecha",Optional.ofNullable(fecha)).build().encode().toUriString();}
}
