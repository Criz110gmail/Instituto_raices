package escuela.personal;
import escuela.personal.dto.*;
import jakarta.validation.Validation;
import java.math.BigDecimal;
import java.nio.file.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class PuestosEntregaUnoTest {
 @Test void formularioExigeHorasYSueldoMensualConLimites(){
  try(var factory=Validation.buildDefaultValidatorFactory()){
   var v=factory.getValidator();var f=new PuestoForm();f.setCodigo("P40");f.setNombre("Apoyo técnico");
   assertThat(v.validate(f)).extracting(e->e.getPropertyPath().toString()).contains("horasSemanales","sueldoMensual");
   f.setHorasSemanales(new BigDecimal("40"));f.setSueldoMensual(new BigDecimal("10000"));assertThat(v.validate(f)).isEmpty();
   f.setHorasSemanales(new BigDecimal("169"));assertThat(v.validate(f)).isNotEmpty();
   f.setHorasSemanales(new BigDecimal("40.123"));assertThat(v.validate(f)).isNotEmpty();
   f.setHorasSemanales(new BigDecimal("40"));f.setSueldoMensual(BigDecimal.ZERO);assertThat(v.validate(f)).isNotEmpty();
  }
 }
 @Test void migracionEsAditivaSinBackfillNiPermisosConcedidos()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V135__puestos_con_sueldo_y_asignacion_unica.sql"));
  assertThat(sql).contains("ADD COLUMN horas_semanales","ADD COLUMN sueldo_mensual","ADD COLUMN horas_puesto","ck_empleado_activo_ingreso");
  assertThat(sql).doesNotContain("DROP TABLE","DELETE FROM","UPDATE puesto_laboral","UPDATE condicion_salarial","INSERT INTO rol_permiso","INSERT INTO movimiento_financiero");
 }
}
