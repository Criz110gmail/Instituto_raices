package escuela.personal;

import escuela.personal.dto.*;
import escuela.personal.service.PreparacionNominaService;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class PreparacionNominaTest {
 @Test void vistaGlobalConservaFiltrosYNoLimitaLaSeleccionALaPagina(){var f=new FiltroPreparacionNomina(" Bruce ","INCLUIDOS",-1,3L,null).normalizado();assertThat(f.buscar()).isEqualTo("Bruce");assertThat(f.paginaConsulta()).isZero();var origen=new FiltroNomina("Origen histórico","APLICADA",null,null,null,null,null,null,null,null,2,25);assertThat(f.url(origen,"/admin/preparacion-nomina/vista/abc",3)).contains("estado=APLICADA","pagina=2","paginaConsulta=3","consultaEmpleadoId=3","grupoConsulta=INCLUIDOS").doesNotContain("%25");assertThat(new FiltroPreparacionNomina("","OTRO",0,null,null).normalizado().grupoConsulta()).isEqualTo("LISTO");}
 @Test void aplicarNoRecibeImportesDelNavegador(){assertThat(Arrays.stream(AplicarNominaForm.class.getDeclaredFields()).map(java.lang.reflect.Field::getName)).doesNotContain("monto","sueldo","total","institucionId","empleados");try(var factory=Validation.buildDefaultValidatorFactory()){assertThat(factory.getValidator().validate(new AplicarNominaForm())).isNotEmpty();}}
 @Test void estadosYFiltrosSonPropios(){var f=new FiltroNomina(" prueba ","PROGRAMADO",null,null,null,null,null,null,null,null,-5,999).normalizado();assertThat(f.estado()).isEqualTo("TODOS");assertThat(f.q()).isEqualTo("prueba");assertThat(f.pagina()).isZero();assertThat(f.tamanio()).isEqualTo(25);assertThat(PreparacionNominaService.grupo("OTRO")).isEqualTo("LISTO");}
 @Test void filtrosDeCandidatosNoSobrescribenOrigen(){var origen=new FiltroNomina("Borrador histórico","BORRADOR",1L,null,2L,null,null,null,null,null,3,25);var candidatos=new FiltroCandidatosNomina("María",4L,"María",1,10);assertThat(candidatos.url(origen,5L,"REVISION",2)).contains("estado=BORRADOR","pagina=3","filtroCalendarioId=1","periodoId=5","candidatoEmpleadoId=4","paginaCandidatos=2").doesNotContain("%25");}
 @Test void selloEsDeterministaYDetectaDiferencias(){assertThat(PreparacionNominaService.sello("configuración1")).hasSize(64).isEqualTo(PreparacionNominaService.sello("configuración1")).isNotEqualTo(PreparacionNominaService.sello("configuración2"));}
 @Test void capturaSinImportesManualesYSeleccionAcotada(){try(var factory=Validation.buildDefaultValidatorFactory()){var v=factory.getValidator();var f=new BorradorNominaForm();assertThat(v.validate(f)).isNotEmpty();f.setPeriodoId(1L);f.setSelloPeriodo("a".repeat(64));f.setNombre("Enero");f.setMotivo("Prueba");f.setSeleccion(List.of("1:"+"b".repeat(64)));assertThat(v.validate(f)).isEmpty();f.setSeleccion(Collections.nCopies(101,"1:"+"b".repeat(64)));assertThat(v.validate(f)).isNotEmpty();}assertThat(Arrays.stream(BorradorNominaForm.class.getDeclaredFields()).map(java.lang.reflect.Field::getName)).doesNotContain("sueldo","importe","cuentaId","impuesto");}
}
