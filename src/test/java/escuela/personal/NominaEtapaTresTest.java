package escuela.personal;
import escuela.personal.dto.*;
import escuela.personal.service.PreparacionNominaService;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;
class NominaEtapaTresTest {
 @Test void filtroOcasionalNoPierdeOrigenNiDuplicaCodificacion(){var origen=new FiltroConfiguracionNomina("José",null,"",null,"","SIN_SUELDO",java.time.LocalDate.of(2030,5,15),2);var f=new FiltroConceptoPeriodoNomina("Bono único","ACTIVA",7L,"",1,10);assertThat(f.url(origen,"/admin/configuracion-nomina/3/ocasionales")).contains("q=Jos%C3%A9","buscarOcasional=Bono%20%C3%BAnico","pagina=2","paginaOcasional=1","periodoOcasionalId=7","tamanioOcasional=10").doesNotContain("%25");}
 @Test void ocasionalNoRecibeFechasTipoEmpleadoNiInstitucionDelNavegador(){assertThat(java.util.Arrays.stream(ConceptoPeriodoNominaForm.class.getDeclaredFields()).map(java.lang.reflect.Field::getName)).doesNotContain("empleadoId","institucionId","naturaleza","inicio","fin","pagado");try(var v=Validation.buildDefaultValidatorFactory()){assertThat(v.getValidator().validate(new ConceptoPeriodoNominaForm())).isNotEmpty();}}
 @Test void generacionTieneCalendarioSnapshotEIdentificaRevisionLegacy()throws Exception{var s=Files.readString(Path.of("src/main/java/escuela/personal/service/PreparacionNominaService.java"));assertThat(s).contains("CALENDARIO_VIGENCIAS_1","calendario_asignado IS NULL","calendario_asignado<>:calendarioActual","'calendario_empleado',to_jsonb(ec)","o.periodo_id=:periodoActual","o.sello_periodo=:selloActual","jsonb_array_elements(ne.fuentes->'ocasionales')","if(b.estado().equals(\"APLICADA\"))return \"\"");}
 @Test void migracionNoRellenaCalendariosNiReescribeNominas()throws Exception{var s=Files.readString(Path.of("src/main/resources/db/migration/V132__conceptos_nomina_por_periodo.sql"));assertThat(s).contains("CREATE TABLE concepto_empleado_periodo","ux_concepto_empleado_periodo_activo","nomina_aplicacion","proteger_personal_historial").doesNotContain("UPDATE nomina_borrador","INSERT INTO empleado_calendario_nomina","INSERT INTO rol_permiso");}
}
