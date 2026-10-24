package escuela.personal.service;

import escuela.personal.dto.*;
import escuela.common.exception.ReglaNegocioException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ConfiguracionEmpleadoNominaService {
 private final EmpleadoService empleados;
 private final CondicionesSalarialesService salarios;
 private final ConceptoEmpleadoService conceptos;
 private final ConfiguracionNominaService configuracion;
 private final NamedParameterJdbcTemplate jdbc;
 private final Validator validator;

 public long revisionConceptos(Long id) {
  empleados.autorizarSalarios(false);empleados.autorizar(id,true,false);
  return jdbc.queryForObject("SELECT coalesce(max(h.id),0) FROM concepto_empleado_historial h JOIN concepto_empleado c ON c.id=h.asignacion_id WHERE c.empleado_id=:id",
    new MapSqlParameterSource("id",id),Long.class);
 }
 public ConfiguracionEmpleadoNominaForm preparar(Long id,Long cambioConcepto) {
  empleados.autorizarSalarios(true);
  var e=configuracion.obtener(id,configuracion.hoy());var f=new ConfiguracionEmpleadoNominaForm();
  f.setVersionLaboral(empleados.laboral(id).getVersion());f.setRevisionConceptos(revisionConceptos(id));
  f.getSueldo().setRevision(salarios.revision(id));f.getSueldo().setVersionLaboral(f.getVersionLaboral());
  f.getSueldo().setFechaInicio(configuracion.hoy());
  f.getCalendario().setVersion(e.version());f.getCalendario().setCalendarioId(e.calendarioId());f.getCalendario().setCalendarioTexto(e.calendario());
  // No preseleccionar el sueldo: evita registrar otra condición por guardar un concepto.
  if(cambioConcepto!=null) {
   var anterior=conceptos.obtener(cambioConcepto);
   if(!Objects.equals(anterior.empleadoId(),id))throw new org.springframework.security.access.AccessDeniedException("Concepto de otro empleado");
   if(!anterior.modificable())throw regla("Este concepto sólo admite consulta; revisa su estado y vigencia.");
   var contexto=conceptos.contexto(id,anterior.conceptoId());var c=new ConceptoEmpleadoForm();
   c.setConceptoId(anterior.conceptoId());c.setConceptoTexto(contexto.concepto());c.setConceptoVersion(contexto.conceptoVersion());
   c.setAnteriorId(anterior.id());c.setAnteriorVersion(anterior.version());c.setImporte(anterior.importe());c.setFin(anterior.fin());
   f.getConceptos().add(c);
  }
  return f;
 }
 /** Una sola transacción: si falla un concepto, tampoco se cambia sueldo o calendario. */
 @Transactional public void guardar(Long id,ConfiguracionEmpleadoNominaForm f) {
  var actor=empleados.autorizarSalarios(true);empleados.autorizar(id,true,false);
  var p=new MapSqlParameterSource("id",id).addValue("inst",actor.institucionId());
  jdbc.queryForObject("SELECT id FROM empleado WHERE id=:id AND institucion_id=:inst FOR UPDATE",p,Long.class);
  Long laboral=jdbc.queryForObject("SELECT version FROM empleado_laboral WHERE empleado_id=:id FOR UPDATE",p,Long.class);
  if(!Objects.equals(laboral,f.getVersionLaboral())||!Objects.equals(revisionConceptos(id),f.getRevisionConceptos())
    ||!Objects.equals(salarios.revision(id),f.getSueldo().getRevision())
    ||!Objects.equals(configuracion.obtener(id,configuracion.hoy()).version(),f.getCalendario().getVersion()))
   throw regla("La configuración cambió en otra pestaña. Recarga y revisa antes de confirmar; no se guardó ningún cambio.");
  if(f.getConceptos()==null||f.getConceptos().size()>20)throw regla("Agrega como máximo 20 conceptos por confirmación.");
  if(!f.isCambiarSueldo()&&!f.isCambiarCalendario()&&f.getConceptos().isEmpty())throw regla("Selecciona un cambio o agrega un concepto antes de guardar.");
  if(f.getMotivo()==null||f.getMotivo().isBlank()||f.getMotivo().length()>2000)throw regla("Explica el motivo de estos cambios (máximo 2000 caracteres).");
  if(f.isCambiarSueldo()) {
   f.getSueldo().setMotivo(f.getMotivo());f.getSueldo().setVersionLaboral(f.getVersionLaboral());validar(f.getSueldo(),"Puesto y sueldo");
  }
  if(f.isCambiarCalendario()) {f.getCalendario().setMotivo(f.getMotivo());validar(f.getCalendario(),"Calendario");}
  var empleado=empleados.obtener(id);Set<Long> seleccionados=new HashSet<>();
  for(var c:f.getConceptos()) {
   if(c==null)throw regla("Revisa las filas de conceptos; no pueden quedar vacías.");
   c.setEmpleadoId(id);c.setEmpleadoTexto(empleado.codigo()+" · "+empleado.nombre());c.setLaboralVersion(f.getVersionLaboral());c.setMotivo(f.getMotivo());
   validar(c,"Concepto");
   if(!seleccionados.add(c.getConceptoId()))throw regla("No repitas un concepto en la misma confirmación; registra una vigencia a la vez.");
   if(c.getAnteriorId()!=null&&!Objects.equals(conceptos.obtener(c.getAnteriorId()).empleadoId(),id))
    throw new org.springframework.security.access.AccessDeniedException("Concepto de otro empleado");
  }
  if(f.isCambiarSueldo())salarios.programar(id,f.getSueldo());
  if(f.isCambiarCalendario())configuracion.guardarCalendario(id,f.getCalendario());
  for(var c:f.getConceptos())conceptos.asignar(c);
 }
 private void validar(Object f,String seccion) {
  var errores=validator.validate(f);
  if(!errores.isEmpty())throw regla(seccion+": "+errores.stream().sorted(Comparator.comparing(x->x.getPropertyPath().toString())).map(x->x.getMessage()).findFirst().orElse("Revisa los datos."));
 }
 private static ReglaNegocioException regla(String texto){return new ReglaNegocioException(texto);}
}
