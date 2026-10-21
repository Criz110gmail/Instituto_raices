package escuela.personal.dto;
import java.time.LocalDate;import org.springframework.format.annotation.DateTimeFormat;
public record FiltroOrganizacionLaboral(String q,String estado,Integer pagina,Integer tamanio,Long empleadoId,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate desde,@DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate hasta){
 public FiltroOrganizacionLaboral normalizado(boolean asignaciones){return new FiltroOrganizacionLaboral(escuela.personal.service.OrganizacionLaboralService.texto(q,200),asignaciones?(java.util.Set.of("ACTUAL","PROXIMA","ANTERIOR","CANCELADA").contains(java.util.Objects.toString(estado,""))?estado:"TODAS"):escuela.personal.service.OrganizacionLaboralService.estado(estado),Math.max(0,pagina==null?0:pagina),java.util.Set.of(10,25,50,100).contains(tamanio==null?0:tamanio)?tamanio:25,empleadoId,desde,hasta);}
}
