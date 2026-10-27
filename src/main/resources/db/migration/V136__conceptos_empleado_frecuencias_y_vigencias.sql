-- Entrega 2. Conserva la semántica de las filas anteriores: importe por nómina.
ALTER TABLE concepto_empleado
 ADD COLUMN modelo VARCHAR(12) NOT NULL DEFAULT 'LEGADO',
 ADD COLUMN frecuencia_importe VARCHAR(12),
 ADD COLUMN modalidad VARCHAR(12),
 ADD COLUMN periodo_id BIGINT,
 ADD COLUMN periodo_codigo VARCHAR(80),
 ADD COLUMN periodo_inicio DATE,
 ADD COLUMN periodo_fin DATE,
 ADD COLUMN periodo_version BIGINT;
ALTER TABLE concepto_empleado ADD CONSTRAINT ck_concepto_configuracion CHECK (
 (modelo='LEGADO' AND frecuencia_importe IS NULL AND modalidad IS NULL AND periodo_id IS NULL
  AND periodo_codigo IS NULL AND periodo_inicio IS NULL AND periodo_fin IS NULL AND periodo_version IS NULL)
 OR (modelo='V2' AND naturaleza IN ('PERCEPCION','DEDUCCION') AND modalidad IS NOT NULL AND frecuencia_importe IS NOT NULL AND (
  (modalidad='RECURRENTE' AND frecuencia_importe IS NOT NULL AND frecuencia_importe IN ('MENSUAL','QUINCENAL','SEMANAL')
   AND periodo_id IS NULL AND periodo_codigo IS NULL AND periodo_inicio IS NULL AND periodo_fin IS NULL AND periodo_version IS NULL)
  OR (modalidad='UNICA' AND frecuencia_importe='UNICA' AND periodo_id IS NOT NULL AND periodo_codigo IS NOT NULL
   AND periodo_inicio IS NOT NULL AND periodo_fin IS NOT NULL AND periodo_version IS NOT NULL AND periodo_version>=0
   AND fecha_inicio=periodo_inicio AND fecha_fin=periodo_fin))));
ALTER TABLE concepto_empleado ADD CONSTRAINT fk_concepto_config_periodo
 FOREIGN KEY(periodo_id,institucion_id) REFERENCES periodo_nomina(id,institucion_id);
CREATE INDEX ix_concepto_config_periodo ON concepto_empleado(periodo_id,empleado_id) WHERE modelo='V2';
-- Snapshots y auditoría V123 incluyen las columnas nuevas automáticamente.
-- No convertir ni recalcular conceptos, salarios, borradores o pagos históricos.
INSERT INTO permiso(codigo,descripcion) VALUES
 ('EMPLEADO_NOMINA_LEER','Empleados — Consultar configuración de nómina'),
 ('EMPLEADO_NOMINA_ADMINISTRAR','Empleados — Administrar configuración de nómina');
-- Sin concesiones automáticas a usuarios/roles.
