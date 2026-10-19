package escuela.personal.dto;
import java.util.Set;
public record FiltroEmpleado(Long institucionId,String q,String estado,int pagina,int tamanio){
 public FiltroEmpleado normalizado(){return new FiltroEmpleado(institucionId,q==null?"":q.strip().substring(0,Math.min(200,q.strip().length())),Set.of("TODOS","ACTIVO","INACTIVO","DOCENTE","SIN_PERFIL_DOCENTE").contains(estado==null?"":estado)?estado:"TODOS",Math.max(0,pagina),Set.of(10,25,50,100).contains(tamanio)?tamanio:25);}
 public FiltroEmpleado pagina(int p){return new FiltroEmpleado(institucionId,q,estado,p,tamanio);}
}
