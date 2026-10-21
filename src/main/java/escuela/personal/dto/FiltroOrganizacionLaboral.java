package escuela.personal.dto;
import java.time.LocalDate;import org.springframework.format.annotation.DateTimeFormat;
public record FiltroOrganizacionLaboral(String q,String estado,Integer pagina,Integer tamanio,Long empleadoId,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate desde,@DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate hasta,String empleadoTexto){
 public FiltroOrganizacionLaboral(String q,String estado,Integer pagina,Integer tamanio,Long empleadoId,LocalDate desde,LocalDate hasta){this(q,estado,pagina,tamanio,empleadoId,desde,hasta,"");}
 public FiltroOrganizacionLaboral normalizado(boolean asignaciones){return new FiltroOrganizacionLaboral(escuela.personal.service.OrganizacionLaboralService.texto(q,200),asignaciones?(java.util.Set.of("ACTUAL","PROXIMA","ANTERIOR","CANCELADA").contains(java.util.Objects.toString(estado,""))?estado:"TODAS"):escuela.personal.service.OrganizacionLaboralService.estado(estado),Math.max(0,pagina==null?0:pagina),java.util.Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25,empleadoId,desde,hasta,escuela.personal.service.OrganizacionLaboralService.texto(empleadoTexto,450));}
 public void validarSeleccion(){if(empleadoId==null&&empleadoTexto!=null&&!empleadoTexto.isBlank())throw new escuela.common.exception.ReglaNegocioException("Selecciona un empleado de la lista o limpia el campo para consultar todos.");}
}
