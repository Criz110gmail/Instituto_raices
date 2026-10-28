package escuela.personal.dto;
import escuela.personal.service.OrganizacionLaboralService;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.*;
public record FiltroPagoNomina(String buscarPago,String estadoPago,Integer paginaPago,Integer tamanioPago,Long empleadoPagoId,String empleadoPagoTexto){
 public FiltroPagoNomina(String buscarPago,String estadoPago,Integer paginaPago,Integer tamanioPago){this(buscarPago,estadoPago,paginaPago,tamanioPago,null,"");}
 public FiltroPagoNomina normalizado(){
  Long empleado=empleadoPagoId!=null&&empleadoPagoId>0?empleadoPagoId:null;
  String texto=OrganizacionLaboralService.texto(empleadoPagoTexto,450);
  if(texto.isBlank())texto=OrganizacionLaboralService.texto(buscarPago,200);
  return new FiltroPagoNomina(empleado==null?OrganizacionLaboralService.texto(texto,200):"",Set.of("PAGADO","PENDIENTE","POSPUESTO","NO_APLICAR").contains(Objects.toString(estadoPago,""))?estadoPago:"TODOS",Math.max(0,paginaPago==null?0:paginaPago),Set.of(10,25,50,100).contains(tamanioPago==null?0:tamanioPago)?tamanioPago:25,empleado,texto);
 }
 public FiltroPagoNomina pagina(int n){return new FiltroPagoNomina(buscarPago,estadoPago,n,tamanioPago,empleadoPagoId,empleadoPagoTexto);}
 public String url(FiltroNomina origen,String ruta){var f=normalizado();return origen.url(ruta)+"&"+UriComponentsBuilder.newInstance().queryParam("buscarPago",f.buscarPago()).queryParam("estadoPago",f.estadoPago()).queryParam("paginaPago",f.paginaPago()).queryParam("tamanioPago",f.tamanioPago()).queryParam("empleadoPagoId",f.empleadoPagoId()).queryParam("empleadoPagoTexto",f.empleadoPagoTexto()).build().encode().getQuery();}
}
