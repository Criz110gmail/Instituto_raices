-- Entrega 1: aditiva. No inferir salarios/horas del catálogo a partir de empleados.
ALTER TABLE puesto_laboral ADD COLUMN horas_semanales NUMERIC(5,2), ADD COLUMN sueldo_mensual NUMERIC(19,2);
ALTER TABLE puesto_laboral ADD CONSTRAINT ck_puesto_condiciones_completas CHECK (
 (horas_semanales IS NULL AND sueldo_mensual IS NULL) OR
 (horas_semanales IS NOT NULL AND horas_semanales>0 AND horas_semanales<=168
  AND sueldo_mensual IS NOT NULL AND sueldo_mensual>0));
ALTER TABLE condicion_salarial ADD COLUMN horas_puesto NUMERIC(5,2), ADD COLUMN puesto_version BIGINT;
ALTER TABLE condicion_salarial ADD CONSTRAINT ck_condicion_puesto_snapshot CHECK (
 (horas_puesto IS NULL AND puesto_version IS NULL) OR
 (horas_puesto IS NOT NULL AND horas_puesto>0 AND horas_puesto<=168 AND puesto_version IS NOT NULL
  AND puesto_version>=0 AND base='MENSUAL' AND frecuencia='MENSUAL'));
-- Las protecciones/historia V116 usan to_jsonb: incluyen columnas nuevas sin reescribir historia.
-- Sueldo sigue siendo UNA fuente: condicion_salarial. No se copian importes a empleado_laboral.
-- Actividad laboral y asignación salarial son distintas; admite conceptos sin sueldo base.
ALTER TABLE empleado_laboral DROP CONSTRAINT empleado_laboral_check;
ALTER TABLE empleado_laboral ADD CONSTRAINT ck_empleado_activo_ingreso CHECK(situacion<>'ACTIVO' OR fecha_ingreso IS NOT NULL);
