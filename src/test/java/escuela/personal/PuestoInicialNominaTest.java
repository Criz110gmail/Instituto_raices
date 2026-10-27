package escuela.personal;

import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.seguridad.service.UsuarioPrincipal;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PuestoInicialNominaTest {
 private final EmpleadoService empleados=mock(EmpleadoService.class);
 private final CondicionesSalarialesService salarios=mock(CondicionesSalarialesService.class);
 private final ConceptoEmpleadoService conceptos=mock(ConceptoEmpleadoService.class);
 private final ConfiguracionNominaService configuracion=mock(ConfiguracionNominaService.class);
 private final NamedParameterJdbcTemplate jdbc=mock(NamedParameterJdbcTemplate.class);
 private final ConfiguracionEmpleadoNominaService service=new ConfiguracionEmpleadoNominaService(empleados,salarios,conceptos,configuracion,jdbc,mock(Validator.class));
 private static final Map<String,Object> PUESTO=Map.of("id",7L,"codigo","DOC-PRI-TEST","nombre","Docente de primaria");
 private void preparar(String nombre,long revision,List<Map<String,Object>> encontrados){
  when(empleados.autorizarSalarios(true)).thenReturn(new UsuarioPrincipal(1L,42L,Set.of(),true,false,"qa","",List.of()));
  var laboral=new LaboralForm();laboral.setVersion(4L);laboral.setPuesto(nombre);when(empleados.laboral(3L)).thenReturn(laboral);
  when(salarios.revision(3L)).thenReturn(revision);when(configuracion.hoy()).thenReturn(LocalDate.of(2026,10,26));
  when(configuracion.obtener(eq(3L),any())).thenReturn(new ConfiguracionNominaService.Fila(3L,"EMP-QA","Ana",true,"ACTIVO",null,null,null,null,"MXN",null,null,null,null,null,false,0L,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO));
  when(jdbc.queryForObject(anyString(),any(MapSqlParameterSource.class),eq(Long.class))).thenReturn(0L);
  when(jdbc.queryForList(anyString(),any(MapSqlParameterSource.class))).thenReturn(encontrados);
 }
 @Test void proponePuestoUnicoDeLaInstitucionSinActivarNiCopiarSueldo(){
  preparar("Docente de primaria",0,List.of(PUESTO));var f=service.preparar(3L,null);
  assertThat(f.getSueldo().getPuestoId()).isEqualTo(7L);assertThat(f.getSueldo().getPuestoTexto()).isEqualTo("DOC-PRI-TEST · Docente de primaria");
  assertThat(f.isCambiarSueldo()).isFalse();assertThat(f.isCambiarCalendario()).isFalse();assertThat(f.getSueldo().getImporteBruto()).isNull();assertThat(f.getConceptos()).isEmpty();
  var sql=ArgumentCaptor.forClass(String.class);var parametros=ArgumentCaptor.forClass(MapSqlParameterSource.class);
  verify(jdbc).queryForList(sql.capture(),parametros.capture());assertThat(sql.getValue()).contains("institucion_id=:inst AND activo AND nombre=:nombre","LIMIT 2");
  assertThat(parametros.getValue().getValue("inst")).isEqualTo(42L);assertThat(parametros.getValue().getValue("nombre")).isEqualTo("Docente de primaria");
  verify(salarios,never()).programar(anyLong(),any());verify(jdbc,never()).update(anyString(),any(MapSqlParameterSource.class));
 }
 @Test void noAdivinaEntrePuestosConElMismoNombre(){
  preparar("Docente de primaria",0,List.of(PUESTO,Map.of("id",8L,"codigo","OTRO","nombre","Docente de primaria")));
  assertThat(service.preparar(3L,null).getSueldo().getPuestoId()).isNull();
 }
 @Test void sinCoincidenciaActivaNoProponeUnPuestoInvalido(){
  preparar("Nombre histórico",0,List.of());assertThat(service.preparar(3L,null).getSueldo().getPuestoId()).isNull();
 }
 @Test void conHistorialSalarialNoPreseleccionaOtraAsignacion(){
  preparar("Docente de primaria",9,List.of(PUESTO));assertThat(service.preparar(3L,null).getSueldo().getPuestoId()).isNull();
  verify(jdbc,never()).queryForList(anyString(),any(MapSqlParameterSource.class));
 }
 @Test void sinPuestoEnExpedienteNoBusca(){
  preparar(null,0,List.of());assertThat(service.preparar(3L,null).getSueldo().getPuestoId()).isNull();
  verify(jdbc,never()).queryForList(anyString(),any(MapSqlParameterSource.class));
 }
}
