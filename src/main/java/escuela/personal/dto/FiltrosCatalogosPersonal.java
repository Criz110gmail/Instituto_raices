package escuela.personal.dto;

import escuela.common.exception.ReglaNegocioException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.util.UriComponentsBuilder;

/** Selecciones de consulta: los prefijos evitan colisiones con los formularios de alta. */
@Getter @Setter
public class FiltrosCatalogosPersonal {
 private Long filtroPuestoId;
 private String filtroPuestoTexto;
 private Long filtroJornadaId;
 private String filtroJornadaTexto;
 private Long filtroEsquemaId;
 private String filtroEsquemaTexto;

 public void validar(){
  validar(filtroPuestoId,filtroPuestoTexto,"puesto");
  validar(filtroJornadaId,filtroJornadaTexto,"jornada");
  validar(filtroEsquemaId,filtroEsquemaTexto,"configuración de puesto y sueldo");
 }
 private static void validar(Long id,String texto,String nombre){
  if(id==null&&texto!=null&&!texto.isBlank())throw new ReglaNegocioException("Selecciona "+(nombre.equals("puesto")?"el ":"la ")+nombre+" de la lista de sugerencias o limpia el campo para consultar todos.");
 }
 public UriComponentsBuilder agregarA(UriComponentsBuilder url){
  if(filtroPuestoId!=null)url.queryParam("filtroPuestoId",filtroPuestoId);
  if(filtroJornadaId!=null)url.queryParam("filtroJornadaId",filtroJornadaId);
  if(filtroEsquemaId!=null)url.queryParam("filtroEsquemaId",filtroEsquemaId);
  return url;
 }
}
