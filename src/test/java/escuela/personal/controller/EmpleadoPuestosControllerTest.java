package escuela.personal.controller;

import escuela.admin.dto.OpcionAutocompletado;
import escuela.personal.service.*;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.repository.PlantelRepository;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.stream.IntStream;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class EmpleadoPuestosControllerTest {
 @Test void ayudaDevuelveDiezYHayMasSinCacheNiImportes()throws Exception{
  var service=mock(EmpleadoService.class);
  var controller=new EmpleadoAdminController(service,mock(ExcelEmpleadoService.class),mock(InstitucionService.class),mock(AlcanceDatosService.class),mock(FotografiaEmpleadoService.class),mock(PlantelRepository.class),mock(CambioPrincipalEmpleadoService.class),mock(OrganizacionLaboralService.class),mock(NominaEmpleadoService.class),mock(CondicionesSalarialesService.class));
  var filas=IntStream.rangeClosed(1,11).mapToObj(n->new OpcionAutocompletado((long)n,"Puesto "+n,"COD-"+n+" · Puesto laboral")).toList();
  when(service.buscarPuestosLaborales(2L,"")).thenReturn(filas);
  var r=controller.buscarPuestos(2L,"");assertThat(r.getHeaders().getCacheControl()).contains("no-store");
  assertThat(r.getBody().resultados()).hasSize(10);assertThat(r.getBody().hayMas()).isTrue();
  var mvc=MockMvcBuilders.standaloneSetup(controller).build();
  when(service.buscarPuestosLaborales(2L,"AUX")).thenReturn(List.of(new OpcionAutocompletado(7L,"Auxiliar","AUX · Puesto laboral")));
  var response=mvc.perform(get("/admin/empleados/2/puestos/buscar").param("q","AUX")).andReturn().getResponse();
  assertThat(response.getStatus()).isEqualTo(200);assertThat(response.getHeader("Cache-Control")).contains("no-store");
  assertThat(response.getContentAsString()).contains("Auxiliar","AUX","\"id\":7").doesNotContain("importe","salario","sueldo");verify(service).buscarPuestosLaborales(2L,"AUX");
 }
}
