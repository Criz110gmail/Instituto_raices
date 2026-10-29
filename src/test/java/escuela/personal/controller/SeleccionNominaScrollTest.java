package escuela.personal.controller;

import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.common.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeleccionNominaScrollTest {
 @Test void incluirYExcluirRegresanALaFilaConLosMismosFiltrosYPagina(){
  var service=mock(PreparacionNominaService.class);var empleados=mock(EmpleadoService.class);
  var controller=new PreparacionNominaController(service,mock(ProgramacionNominaService.class),empleados,mock(AplicacionNominaService.class));
  var id=UUID.randomUUID();var origen=new FiltroNomina("Origen","TODOS",null,null,null,null,null,null,null,null,2,25);
  var consulta=new FiltroPreparacionNomina("Bruce","LISTO",3,3L,"Bruce Wayne");
  for(boolean incluir:new boolean[]{true,false}){
   var redirect=controller.seleccionarPeriodo(id,4L,3L,incluir,origen,consulta,new RedirectAttributesModelMap());
   assertThat(redirect).contains("pagina=2","paginaConsulta=3","consultaEmpleadoId=3","buscar=Bruce").endsWith("#nomina-empleado-3");
   verify(service).seleccionarPreparacion(id,4L,3L,incluir);
  }
 }
 @Test void errorNoOcultaElAvisoConUnAnclaDeExito(){
  var service=mock(PreparacionNominaService.class);var controller=new PreparacionNominaController(service,mock(ProgramacionNominaService.class),mock(EmpleadoService.class),mock(AplicacionNominaService.class));
  var id=UUID.randomUUID();doThrow(new ReglaNegocioException("La vista cambió; recarga.")).when(service).seleccionarPreparacion(id,0L,3L,true);
  var flash=new RedirectAttributesModelMap();var redirect=controller.seleccionarPeriodo(id,0L,3L,true,new FiltroNomina("","TODOS",null,null,null,null,null,null,null,null,0,25),new FiltroPreparacionNomina("","LISTO",0,null,null),flash);
  assertThat(redirect).doesNotContain("#nomina-empleado-");assertThat(flash.getFlashAttributes().get("errorOperacion")).isEqualTo("La vista cambió; recarga.");
 }
}
