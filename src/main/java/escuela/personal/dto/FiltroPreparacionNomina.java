package escuela.personal.dto;

import escuela.personal.service.OrganizacionLaboralService;
import escuela.personal.service.PreparacionNominaService;
import escuela.common.exception.ReglaNegocioException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public record FiltroPreparacionNomina(String buscar,String grupoConsulta,Integer paginaConsulta,Long consultaEmpleadoId,String consultaEmpleadoTexto) {
 public FiltroPreparacionNomina normalizado(){return new FiltroPreparacionNomina(OrganizacionLaboralService.texto(buscar,200),PreparacionNominaService.grupoPreparacion(grupoConsulta),Math.max(0,paginaConsulta==null?0:paginaConsulta),consultaEmpleadoId,OrganizacionLaboralService.texto(consultaEmpleadoTexto,450));}
 public void validar(){if(consultaEmpleadoId==null&&consultaEmpleadoTexto!=null&&!consultaEmpleadoTexto.isBlank())throw new ReglaNegocioException("Selecciona al empleado de las sugerencias o limpia la ayuda.");}
 public String url(FiltroNomina origen,String ruta,int pagina){var f=normalizado();return origen.url(ruta)+"&buscar="+URLEncoder.encode(f.buscar(),StandardCharsets.UTF_8)+"&grupoConsulta="+f.grupoConsulta()+"&paginaConsulta="+Math.max(0,pagina)+(f.consultaEmpleadoId()==null?"":"&consultaEmpleadoId="+f.consultaEmpleadoId());}
}
