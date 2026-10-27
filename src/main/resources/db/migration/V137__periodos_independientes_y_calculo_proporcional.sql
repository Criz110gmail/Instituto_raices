-- Los calendarios existentes son exclusivamente referencias históricas.
-- No se eliminan filas ni fotografías de nóminas anteriores.
ALTER TABLE periodo_nomina ALTER COLUMN calendario_id DROP NOT NULL;
ALTER TABLE periodo_nomina ALTER COLUMN calendario_codigo DROP NOT NULL;
ALTER TABLE periodo_nomina ALTER COLUMN calendario_nombre DROP NOT NULL;
ALTER TABLE periodo_nomina ALTER COLUMN calendario_version DROP NOT NULL;
ALTER TABLE periodo_nomina ALTER COLUMN frecuencia DROP NOT NULL;
ALTER TABLE periodo_nomina ADD CONSTRAINT ck_periodo_sin_calendario
 CHECK ((calendario_id IS NULL AND calendario_codigo IS NULL AND calendario_nombre IS NULL
 AND calendario_version IS NULL AND frecuencia IS NULL) OR calendario_id IS NOT NULL);
CREATE FUNCTION proteger_periodo_nomina_utilizado() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
 IF EXISTS(SELECT 1 FROM nomina_borrador WHERE periodo_id=OLD.id AND estado<>'CANCELADO')
 OR EXISTS(SELECT 1 FROM concepto_empleado WHERE periodo_id=OLD.id AND estado='ACTIVA') THEN
  RAISE EXCEPTION 'El periodo ya tiene nóminas o conceptos únicos asociados. Conserva sus fechas e historial';
 END IF;
 RETURN NEW;
END $$;
CREATE TRIGGER tr_periodo_utilizado BEFORE UPDATE ON periodo_nomina FOR EACH ROW EXECUTE FUNCTION proteger_periodo_nomina_utilizado();
CREATE UNIQUE INDEX ux_periodo_independiente_codigo ON periodo_nomina(institucion_id,codigo)
 WHERE calendario_id IS NULL;
ALTER TABLE nomina_borrador_revision ALTER COLUMN calendario_id DROP NOT NULL;
DO $$ DECLARE c record; BEGIN
 FOR c IN SELECT conname FROM pg_constraint WHERE conrelid='nomina_borrador_revision'::regclass
 AND contype='c' AND pg_get_constraintdef(oid) LIKE '%date_trunc%'
 LOOP EXECUTE format('ALTER TABLE nomina_borrador_revision DROP CONSTRAINT %I',c.conname); END LOOP;
END $$;
ALTER TABLE nomina_borrador_revision ADD CONSTRAINT ck_revision_cobertura CHECK(fecha_fin>=fecha_inicio);
ALTER TABLE nomina_borrador_empleado DROP CONSTRAINT nomina_borrador_empleado_sueldo_check;
ALTER TABLE nomina_borrador_empleado ADD CONSTRAINT nomina_borrador_empleado_sueldo_check CHECK(sueldo>=0);
DO $$ DECLARE c record; BEGIN
 FOR c IN SELECT conname FROM pg_constraint WHERE conrelid='nomina_preparacion_empleado'::regclass
 AND contype='c' AND pg_get_constraintdef(oid) LIKE '%sueldo >%'
 LOOP EXECUTE format('ALTER TABLE nomina_preparacion_empleado DROP CONSTRAINT %I',c.conname); END LOOP;
END $$;
ALTER TABLE nomina_preparacion_empleado ADD CONSTRAINT ck_preparacion_importes
 CHECK((grupo='LISTO' AND sueldo>=0 AND percepciones>=0 AND deducciones>=0 AND aportaciones>=0
 AND propuesto>=0 AND propuesto=sueldo+percepciones-deducciones)
 OR (grupo<>'LISTO' AND sueldo IS NULL AND percepciones IS NULL AND deducciones IS NULL
 AND aportaciones IS NULL AND propuesto IS NULL));

-- Centavos deterministas por prefijos del periodo de referencia. R(b)-R(a-1)
-- garantiza aditividad, incluso al calcular fuera de orden, sin depender de pagos.
CREATE FUNCTION nomina_importe_proporcional(importe numeric, frecuencia text,
 desde date, hasta date, vigente_desde date, vigente_hasta date) RETURNS numeric
 LANGUAGE plpgsql IMMUTABLE AS $$
DECLARE dia date; fin date; ref_inicio date; ref_fin date; tramo_fin date;
 divisor integer; total numeric := 0;
BEGIN
 IF importe IS NULL OR importe<0 OR frecuencia IS NULL OR frecuencia NOT IN ('MENSUAL','QUINCENAL','SEMANAL')
 OR desde IS NULL OR hasta IS NULL OR vigente_desde IS NULL THEN
  RAISE EXCEPTION 'Referencia de cálculo proporcional inválida';
 END IF;
 dia := greatest(desde,vigente_desde); fin := least(hasta,coalesce(vigente_hasta,hasta));
 WHILE dia<=fin LOOP
  IF frecuencia='MENSUAL' THEN
   ref_inicio:=date_trunc('month',dia)::date; ref_fin:=(ref_inicio+interval '1 month - 1 day')::date;
  ELSIF frecuencia='QUINCENAL' THEN
   ref_inicio:=date_trunc('month',dia)::date;
   IF extract(day FROM dia)<=15 THEN ref_fin:=ref_inicio+14;
   ELSE ref_inicio:=ref_inicio+15; ref_fin:=(date_trunc('month',dia)+interval '1 month - 1 day')::date; END IF;
  ELSE ref_inicio:=date_trunc('week',dia)::date; ref_fin:=ref_inicio+6; END IF;
  divisor:=ref_fin-ref_inicio+1; tramo_fin:=least(fin,ref_fin);
  total:=total+round(importe*(tramo_fin-ref_inicio+1)/divisor,2)-round(importe*(dia-ref_inicio)/divisor,2);
  dia:=tramo_fin+1;
 END LOOP;
 RETURN total;
END $$;
