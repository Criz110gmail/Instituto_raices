package escuela.personal.dto;
import java.util.Set;
public record FiltroEmpleado(Long institucionId,String q,String estado,int pagina,int tamanio,Long empleadoId,String empleadoTexto){
 public FiltroEmpleado(Long institucionId,String q,String estado,int pagina,int tamanio){this(institucionId,q,estado,pagina,tamanio,null,"");}
 public FiltroEmpleado normalizado(){return new FiltroEmpleado(institucionId,q==null?"":q.strip().substring(0,Math.min(200,q.strip().length())),Set.of("TODOS","ACTIVO","INACTIVO","DOCENTE","SIN_PERFIL_DOCENTE").contains(estado==null?"":estado)?estado:"TODOS",Math.max(0,pagina),Set.of(10,25,50,100).contains(tamanio)?tamanio:25,empleadoId,escuela.personal.service.OrganizacionLaboralService.texto(empleadoTexto,450));}
 public FiltroEmpleado pagina(int p){return new FiltroEmpleado(institucionId,q,estado,p,tamanio,empleadoId,empleadoTexto);}
 public void validarSeleccion(){if(empleadoId==null&&empleadoTexto!=null&&!empleadoTexto.isBlank())throw new escuela.common.exception.ReglaNegocioException("Selecciona un empleado de la lista o limpia el campo para consultar todos.");}
}
