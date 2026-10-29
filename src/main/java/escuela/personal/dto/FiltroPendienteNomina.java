package escuela.personal.dto;

import escuela.personal.service.OrganizacionLaboralService;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.*;

/** Etiqueta visible independiente del identificador usado en tabla y Excel. */
public record FiltroPendienteNomina(String buscarPendiente,String estadoPendiente,Integer paginaPendiente,
 Long empleadoPendienteId,String empleadoPendienteTexto) {
 public FiltroPendienteNomina normalizado(){
  Long id=empleadoPendienteId!=null&&empleadoPendienteId>0?empleadoPendienteId:null;
  String texto=OrganizacionLaboralService.texto(empleadoPendienteTexto,450);
  if(texto.isBlank())texto=OrganizacionLaboralService.texto(buscarPendiente,200);
  return new FiltroPendienteNomina(id==null?OrganizacionLaboralService.texto(texto,200):"",
   Set.of("PENDIENTE","PAGADO").contains(Objects.toString(estadoPendiente,""))?estadoPendiente:"TODOS",
   Math.max(0,paginaPendiente==null?0:paginaPendiente),id,texto);
 }
 public FiltroPendienteNomina pagina(int pagina){return new FiltroPendienteNomina(buscarPendiente,estadoPendiente,pagina,empleadoPendienteId,empleadoPendienteTexto);}
 public String url(FiltroNomina origen,String ruta){var f=normalizado();return origen.url(ruta)+"&"+UriComponentsBuilder.newInstance()
  .queryParam("buscarPendiente",f.buscarPendiente()).queryParam("estadoPendiente",f.estadoPendiente())
  .queryParam("paginaPendiente",f.paginaPendiente()).queryParam("empleadoPendienteId",f.empleadoPendienteId())
  .queryParam("empleadoPendienteTexto",f.empleadoPendienteTexto()).build().encode().getQuery();}
}
