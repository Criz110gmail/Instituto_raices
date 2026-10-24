package escuela.personal.dto;
import escuela.personal.service.OrganizacionLaboralService;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.*;
public record FiltroPagoNomina(String buscarPago,String estadoPago,Integer paginaPago,Integer tamanioPago){
 public FiltroPagoNomina normalizado(){return new FiltroPagoNomina(OrganizacionLaboralService.texto(buscarPago,200),Set.of("PAGADO","PENDIENTE").contains(Objects.toString(estadoPago,""))?estadoPago:"TODOS",Math.max(0,paginaPago==null?0:paginaPago),Set.of(10,25,50,100).contains(tamanioPago==null?0:tamanioPago)?tamanioPago:25);}
 public FiltroPagoNomina pagina(int n){return new FiltroPagoNomina(buscarPago,estadoPago,n,tamanioPago);}
 public String url(FiltroNomina origen,String ruta){var f=normalizado();return origen.url(ruta)+"&"+UriComponentsBuilder.newInstance().queryParam("buscarPago",f.buscarPago()).queryParam("estadoPago",f.estadoPago()).queryParam("paginaPago",f.paginaPago()).queryParam("tamanioPago",f.tamanioPago()).build().encode().getQuery();}
}
