package escuela.personal.dto;
import escuela.personal.service.OrganizacionLaboralService;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.*;
public record FiltroPrestamoEmpleado(String buscarPrestamo,String estadoPrestamo,Integer paginaPrestamo,Integer tamanioPrestamo) {
 public FiltroPrestamoEmpleado normalizado(){return new FiltroPrestamoEmpleado(OrganizacionLaboralService.texto(buscarPrestamo,200),Set.of("BORRADOR","ACTIVO","CANCELADO","LIQUIDADO").contains(Objects.toString(estadoPrestamo,""))?estadoPrestamo:"TODOS",Math.max(0,paginaPrestamo==null?0:paginaPrestamo),Set.of(10,25,50,100).contains(tamanioPrestamo==null?0:tamanioPrestamo)?tamanioPrestamo:25);}
 public FiltroPrestamoEmpleado pagina(int p){return new FiltroPrestamoEmpleado(buscarPrestamo,estadoPrestamo,p,tamanioPrestamo);}
 public String url(FiltroConfiguracionNomina origen,String ruta){var f=normalizado();return origen.url(ruta)+"&"+UriComponentsBuilder.newInstance().queryParam("buscarPrestamo",f.buscarPrestamo()).queryParam("estadoPrestamo",f.estadoPrestamo()).queryParam("paginaPrestamo",f.paginaPrestamo()).queryParam("tamanioPrestamo",f.tamanioPrestamo()).build().encode().getQuery();}
}
