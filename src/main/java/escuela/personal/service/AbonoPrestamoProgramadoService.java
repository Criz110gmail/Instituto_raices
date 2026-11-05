package escuela.personal.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.namedparam.*;
import escuela.common.exception.ReglaNegocioException;
import java.math.BigDecimal;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class AbonoPrestamoProgramadoService {
 private final NamedParameterJdbcTemplate jdbc;private final EmpleadoService empleados;
 private MapSqlParameterSource p(Long id,Long fila){var a=empleados.autorizarSalarios(true);return new MapSqlParameterSource("inst",a.institucionId()).addValue("actor",a.usuarioId()).addValue("id",id).addValue("fila",fila);}
 public void validar(Long id,Long fila,boolean bloquear){var par=p(id,fila);if(bloquear)jdbc.query("SELECT p.id FROM prestamo_empleado p JOIN nomina_fila_efectiva ne ON ne.empleado_id=p.empleado_id AND ne.institucion_id=p.institucion_id WHERE ne.id=:fila AND ne.borrador_id=:id AND p.institucion_id=:inst ORDER BY p.id FOR UPDATE OF p",par,(org.springframework.jdbc.core.RowCallbackHandler)r->{});
  long invalidas=jdbc.queryForObject("SELECT count(*) FROM nomina_fila_efectiva ne CROSS JOIN LATERAL jsonb_array_elements(ne.fuentes->'prestamos')x LEFT JOIN prestamo_empleado p ON p.id=(x->>'id')::bigint AND p.empleado_id=ne.empleado_id AND p.institucion_id=ne.institucion_id LEFT JOIN prestamo_saldo s ON s.id=p.id WHERE ne.id=:fila AND ne.borrador_id=:id AND ne.institucion_id=:inst AND (p.id IS NULL OR s.saldo<(x->>'cuota')::numeric OR p.moneda<>ne.moneda OR (p.programado AND ((SELECT coalesce(sum((q->>'capital')::numeric+(q->>'interes')::numeric),0) FROM jsonb_array_elements(x->'cuotas_programadas')q)<>(x->>'cuota')::numeric OR EXISTS(SELECT 1 FROM jsonb_array_elements(x->'cuotas_programadas')q WHERE NOT EXISTS(SELECT 1 FROM prestamo_cuota_pendiente c JOIN prestamo_plan pl ON pl.id=c.plan_id WHERE pl.prestamo_id=p.id AND c.plan_id=(q->>'plan_id')::bigint AND c.numero=(q->>'numero')::integer AND c.capital=(q->>'capital')::numeric AND c.interes=(q->>'interes')::numeric)))))",par,Long.class);
  if(invalidas>0)throw new ReglaNegocioException("El saldo o las cuotas del préstamo cambiaron o ya fueron abonadas. Revisa la nómina antes de confirmar; no se movió dinero.");
 }
 @Transactional public void abonar(Long id,Long fila,Long pago){validar(id,fila,true);var par=p(id,fila).addValue("pago",pago);var cuotas=jdbc.queryForList("SELECT p.id,s.saldo,(x->>'cuota')::numeric cuota,p.programado FROM nomina_fila_efectiva ne CROSS JOIN LATERAL jsonb_array_elements(ne.fuentes->'prestamos')x JOIN prestamo_empleado p ON p.id=(x->>'id')::bigint AND p.empleado_id=ne.empleado_id JOIN prestamo_saldo s ON s.id=p.id WHERE ne.id=:fila AND ne.borrador_id=:id AND ne.institucion_id=:inst ORDER BY p.id",par);
  for(var q:cuotas){Long prestamo=((Number)q.get("id")).longValue();par.addValue("prestamo",prestamo).addValue("saldo",q.get("saldo")).addValue("cuota",q.get("cuota"));jdbc.update("INSERT INTO prestamo_abono_nomina(pago_id,prestamo_id,importe,saldo_anterior,saldo_posterior) VALUES(:pago,:prestamo,:cuota,:saldo,:saldo-:cuota)",par);
   if(Boolean.TRUE.equals(q.get("programado")))jdbc.update("INSERT INTO prestamo_cuota_abono(pago_id,plan_id,numero,capital,interes) SELECT :pago,(q->>'plan_id')::bigint,(q->>'numero')::integer,(q->>'capital')::numeric,(q->>'interes')::numeric FROM nomina_fila_efectiva ne CROSS JOIN LATERAL jsonb_array_elements(ne.fuentes->'prestamos')x CROSS JOIN LATERAL jsonb_array_elements(x->'cuotas_programadas')q WHERE ne.id=:fila AND ne.borrador_id=:id AND ne.institucion_id=:inst AND (x->>'id')::bigint=:prestamo",par);
   estado(prestamo,"Abono de nómina NPI-"+pago,par);
  }
 }
 @Transactional public void compensar(Long pago,Long reversa){var a=empleados.autorizarSalarios(true);var par=new MapSqlParameterSource("inst",a.institucionId()).addValue("actor",a.usuarioId()).addValue("pago",pago).addValue("rev",reversa);jdbc.update("INSERT INTO prestamo_abono_compensacion(reversa_id,pago_id,prestamo_id,importe) SELECT :rev,a.pago_id,a.prestamo_id,a.importe FROM prestamo_abono_nomina a JOIN prestamo_empleado p ON p.id=a.prestamo_id WHERE a.pago_id=:pago AND p.institucion_id=:inst",par);for(Long prestamo:jdbc.queryForList("SELECT a.prestamo_id FROM prestamo_abono_nomina a JOIN prestamo_empleado p ON p.id=a.prestamo_id WHERE a.pago_id=:pago AND p.institucion_id=:inst ORDER BY p.id",par,Long.class))estado(prestamo,"Cancelación de abono NPI-"+pago,par);}
 private void estado(Long id,String motivo,MapSqlParameterSource par){jdbc.update("UPDATE prestamo_empleado SET estado=CASE WHEN (SELECT saldo FROM prestamo_saldo WHERE id=:prestamo)=0 THEN 'LIQUIDADO' ELSE 'ACTIVO' END,motivo_operacion=:motivo,actualizado_por_id=:actor,actualizado_en=CURRENT_TIMESTAMP,version=version+1 WHERE id=:prestamo AND institucion_id=:inst",par.addValue("prestamo",id).addValue("motivo",motivo));}
}
