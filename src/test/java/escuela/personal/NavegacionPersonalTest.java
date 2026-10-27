package escuela.personal;

import escuela.admin.dto.*;
import escuela.config.NavegacionPersonalConfig;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class NavegacionPersonalTest {
 @Test void menuSigueElProcesoAcordadoEnServidorYNavegador()throws Exception{
  var permisos=Set.of("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR");
  var view=new ModelAndView("admin/puestos");view.addObject("modulos",Arrays.stream(ModuloCatalogo.values()).filter(m->m.visibleCon(permisos)).toList());
  new NavegacionPersonalConfig().postHandle(null,null,null,view);
  assertThat(view.getModel().get("modulos")).isEqualTo(List.of(ModuloCatalogo.PUESTOS,ModuloCatalogo.CONCEPTOS_NOMINA,ModuloCatalogo.EMPLEADOS,ModuloCatalogo.PERIODOS_NOMINA,ModuloCatalogo.PREPARACION_NOMINA));
  var js=java.nio.file.Files.readString(java.nio.file.Path.of("src/main/resources/static/js/navigation.js"));
  assertThat(js).contains("'Administración · Personal y nómina': ['Puestos laborales', 'Conceptos de nómina', 'Empleados', 'Periodos de nómina', 'Nóminas']");
 }
 @Test void plantillasNoContienenRestosUndefined()throws Exception{
  try(var rutas=java.nio.file.Files.walk(java.nio.file.Path.of("src/main/resources/templates"))){
   for(var ruta:rutas.filter(p->p.toString().endsWith(".html")).toList())
    assertThat(java.nio.file.Files.readString(ruta)).as(ruta.toString()).doesNotContainPattern("(?m)^\\s*undefined\\s*$");
  }
 }
 @Test void seisEntradasSinCambiarAutorizacionNiEliminarRutas(){
  var permisos=Set.of("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR");
  var originales=Arrays.stream(ModuloCatalogo.values()).filter(m->m.visibleCon(permisos)).toList();
  assertThat(originales).contains(ModuloCatalogo.JORNADAS_LABORALES,ModuloCatalogo.CALENDARIOS_NOMINA);
  var view=new ModelAndView("admin/programacion-nomina-listado");view.addObject("modulos",originales);view.addObject("moduloActual",ModuloCatalogo.CALENDARIOS_NOMINA);
  new NavegacionPersonalConfig().postHandle(null,null,null,view);
  assertThat((List<?>)view.getModel().get("modulos")).hasSize(5);
  assertThat(view.getModel().get("moduloActual")).isEqualTo(ModuloCatalogo.PERIODOS_NOMINA);
  assertThat(view.getModel().get("moduloPersonalOrigen")).isEqualTo(ModuloCatalogo.CALENDARIOS_NOMINA);
  assertThat(ModuloCatalogo.CALENDARIOS_NOMINA.visibleCon(permisos)).isTrue();assertThat(ModuloCatalogo.JORNADAS_LABORALES.rutaListado()).isEqualTo("/admin/jornadas");
  assertThat(ModuloCatalogo.PREPARACION_NOMINA.titulo()).isEqualTo("Nóminas");
 }
 @Test void noAgregaPadresNoAutorizadosNiModificaRedirecciones(){
  var c=new NavegacionPersonalConfig();var view=new ModelAndView("admin/qa");view.addObject("modulos",List.of(ModuloCatalogo.JORNADAS_LABORALES));c.postHandle(null,null,null,view);assertThat((List<?>)view.getModel().get("modulos")).hasSize(1);
  var red=new ModelAndView("redirect:/admin/jornadas");red.addObject("modulos",List.of(ModuloCatalogo.JORNADAS_LABORALES,ModuloCatalogo.PUESTOS));c.postHandle(null,null,null,red);assertThat((List<?>)red.getModel().get("modulos")).hasSize(2);
 }
 @Test void movimientoExactoSeConservaEnNormalizacionYPaginasSinAlterarFiltrosAnteriores(){
  var f=new FiltroMovimientoFinanciero(1L,2L,"Cuenta",null,"EGRESO","NOMINA",LocalDate.of(2026,10,24),null,0,25,37L);
  assertThat(f.normalizado().movimientoId()).isEqualTo(37L);assertThat(f.conPagina(2,100).movimientoId()).isEqualTo(37L);assertThat(f.conPagina(2,100).cuentaId()).isEqualTo(2L);
  assertThat(new FiltroMovimientoFinanciero(1L,null,"",null,"TODOS","TODOS",null,null,0,25).normalizado().movimientoId()).isNull();
 }
}
