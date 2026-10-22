-- Sólo simplifica la programación. No borra fechas ni altera registros/historial.
ALTER TABLE periodo_nomina ALTER COLUMN fecha_corte DROP NOT NULL;
DO $$ DECLARE restriccion RECORD; BEGIN
 FOR restriccion IN SELECT conname FROM pg_constraint
  WHERE conrelid='periodo_nomina'::regclass AND contype='c'
   AND pg_get_constraintdef(oid) LIKE '%fecha_fin%'
   AND pg_get_constraintdef(oid) LIKE '%fecha_corte%'
 LOOP
  EXECUTE format('ALTER TABLE periodo_nomina DROP CONSTRAINT %I',restriccion.conname);
 END LOOP;
END $$;
ALTER TABLE periodo_nomina ADD CONSTRAINT ck_periodo_nomina_fechas
 CHECK(fecha_fin>=fecha_inicio AND fecha_pago>=fecha_inicio
  AND (fecha_corte IS NULL OR (fecha_corte>=fecha_inicio AND fecha_pago>=fecha_corte)));
COMMENT ON COLUMN periodo_nomina.fecha_corte IS
 'Fecha límite opcional para recibir incidencias de nómina; sólo referencia, no bloquea capturas ni calcula pagos.';
