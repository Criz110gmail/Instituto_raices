ALTER TABLE nomina_borrador DROP CONSTRAINT nomina_borrador_estado_check;
ALTER TABLE nomina_borrador ADD CONSTRAINT nomina_borrador_estado_check CHECK(estado IN ('BORRADOR','CANCELADO','APLICADA'));
ALTER TABLE movimiento_financiero DROP CONSTRAINT ck_movimiento_clase;
ALTER TABLE movimiento_financiero ADD CONSTRAINT ck_movimiento_clase CHECK(clase IN ('COBRO','OPERACION','TRASPASO','DEVOLUCION','AJUSTE','REVERSO','ANULACION','NOMINA'));
CREATE TABLE nomina_aplicacion (
 borrador_id BIGINT PRIMARY KEY, institucion_id BIGINT NOT NULL, periodo_id BIGINT NOT NULL, revision INTEGER NOT NULL,
 movimiento_id BIGINT NOT NULL UNIQUE REFERENCES movimiento_financiero(id), cuenta_id BIGINT NOT NULL REFERENCES cuenta_financiera(id),
 motivo_financiero_id BIGINT NOT NULL REFERENCES motivo_financiero(id), cuenta_codigo VARCHAR(50) NOT NULL, cuenta_nombre VARCHAR(150) NOT NULL,
 empleados INTEGER NOT NULL CHECK(empleados>0), monto NUMERIC(19,2) NOT NULL CHECK(monto>0), moneda VARCHAR(3) NOT NULL,
 saldo_anterior NUMERIC(19,2) NOT NULL, saldo_posterior NUMERIC(19,2) NOT NULL,
 aplicado_por_id BIGINT NOT NULL REFERENCES usuario(id), aplicado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 motivo VARCHAR(2000) NOT NULL CHECK(btrim(motivo)<>''),
 FOREIGN KEY(borrador_id,institucion_id,periodo_id) REFERENCES nomina_borrador(id,institucion_id,periodo_id),
 FOREIGN KEY(borrador_id,revision) REFERENCES nomina_borrador_revision(borrador_id,numero),
 CHECK(saldo_posterior=saldo_anterior-monto AND saldo_posterior>=0)
);
CREATE TRIGGER tr_nomina_aplicacion_inmutable BEFORE UPDATE OR DELETE ON nomina_aplicacion FOR EACH ROW EXECUTE FUNCTION proteger_personal_historial();
CREATE OR REPLACE FUNCTION proteger_nomina_borrador() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
 IF TG_OP='DELETE' THEN RAISE EXCEPTION 'Conserva el borrador y sus revisiones'; END IF;
 IF TG_TABLE_NAME='nomina_borrador' THEN
  IF OLD.estado IN ('CANCELADO','APLICADA') OR (NEW.id,NEW.institucion_id,NEW.periodo_id,NEW.creado_por_id,NEW.creado_en)
    IS DISTINCT FROM (OLD.id,OLD.institucion_id,OLD.periodo_id,OLD.creado_por_id,OLD.creado_en) THEN
   RAISE EXCEPTION 'Identidad inmutable; nómina aplicada o cancelada sólo consulta';
  END IF;
 ELSIF TG_TABLE_NAME='nomina_borrador_empleado' THEN
  IF EXISTS(SELECT 1 FROM nomina_borrador WHERE id=OLD.borrador_id AND estado='APLICADA')
    OR NOT OLD.activo OR NEW.activo OR (to_jsonb(NEW)-'activo') IS DISTINCT FROM (to_jsonb(OLD)-'activo') THEN
   RAISE EXCEPTION 'Conserva la fotografía histórica del empleado';
  END IF;
 ELSE RAISE EXCEPTION 'La revisión histórica no se modifica';
 END IF;
 RETURN NEW;
END $$;
