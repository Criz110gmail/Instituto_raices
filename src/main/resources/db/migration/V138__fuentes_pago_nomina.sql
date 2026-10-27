-- Distribución exacta por empleado, intento y fuente; las filas son inmutables.
CREATE TABLE nomina_pago_fuente (
 pago_id bigint NOT NULL, ordinal integer NOT NULL CHECK(ordinal>0),
 cuenta_id bigint NOT NULL REFERENCES cuenta_financiera(id),
 cuenta_texto varchar(450) NOT NULL,
 medio varchar(15) NOT NULL CHECK(medio IN ('EFECTIVO','TRANSFERENCIA')),
 importe numeric(19,2) NOT NULL CHECK(importe>0),
 movimiento_id bigint NOT NULL UNIQUE REFERENCES movimiento_financiero(id),
 PRIMARY KEY(pago_id,ordinal),
 FOREIGN KEY(pago_id) REFERENCES nomina_pago_intento(id) DEFERRABLE INITIALLY DEFERRED
);
CREATE TABLE nomina_pago_fuente_reversa (
 pago_id bigint NOT NULL, ordinal integer NOT NULL,
 movimiento_id bigint NOT NULL UNIQUE REFERENCES movimiento_financiero(id),
 FOREIGN KEY(pago_id,ordinal) REFERENCES nomina_pago_fuente(pago_id,ordinal),
 FOREIGN KEY(pago_id) REFERENCES nomina_pago_reversa(pago_id) DEFERRABLE INITIALLY DEFERRED,
 PRIMARY KEY(pago_id,ordinal)
);
CREATE TRIGGER tr_nomina_fuente_inmutable BEFORE UPDATE OR DELETE ON nomina_pago_fuente FOR EACH ROW EXECUTE FUNCTION proteger_personal_historial();
CREATE TRIGGER tr_nomina_fuente_reversa_inmutable BEFORE UPDATE OR DELETE ON nomina_pago_fuente_reversa FOR EACH ROW EXECUTE FUNCTION proteger_personal_historial();
CREATE FUNCTION validar_fuente_nomina() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
 IF EXISTS(SELECT 1 FROM nomina_pago_intento WHERE id=NEW.pago_id) THEN
  RAISE EXCEPTION 'Un pago ya registrado conserva sus fuentes originales'; END IF;
 IF NOT EXISTS(SELECT 1 FROM movimiento_financiero m JOIN cuenta_financiera c ON c.id=m.cuenta_id
 WHERE m.id=NEW.movimiento_id AND m.cuenta_id=NEW.cuenta_id AND m.clase='NOMINA' AND m.direccion='EGRESO'
 AND m.monto=NEW.importe AND m.clave_idempotencia='NOMINA-EMP:'||NEW.pago_id||':'||NEW.ordinal
 AND ((NEW.medio='EFECTIVO' AND c.tipo='CAJA') OR (NEW.medio='TRANSFERENCIA' AND c.tipo='BANCO')))
 THEN RAISE EXCEPTION 'Fuente de nómina sin egreso y medio coincidentes'; END IF; RETURN NEW;
END $$;
CREATE TRIGGER tr_nomina_fuente_validar BEFORE INSERT ON nomina_pago_fuente FOR EACH ROW EXECUTE FUNCTION validar_fuente_nomina();
CREATE OR REPLACE FUNCTION proteger_intento_nomina() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
 IF TG_OP='DELETE' THEN RAISE EXCEPTION 'Conserva los intentos de pago'; END IF;
 IF TG_OP='UPDATE' THEN
  IF OLD.estado<>'PAGADO' OR NEW.estado<>'REVERTIDO' OR (to_jsonb(NEW)-'estado') IS DISTINCT FROM (to_jsonb(OLD)-'estado') OR NOT EXISTS(SELECT 1 FROM nomina_pago_reversa WHERE pago_id=OLD.id) THEN RAISE EXCEPTION 'Sólo cancelación trazable del pago'; END IF;
 ELSE
  IF NEW.estado='REVERTIDO' OR NOT EXISTS(SELECT 1 FROM nomina_borrador_empleado ne JOIN nomina_autorizacion a ON a.borrador_id=ne.borrador_id AND a.revision=ne.revision WHERE ne.id=NEW.fila_id AND ne.institucion_id=NEW.institucion_id AND ne.propuesto=NEW.monto AND ne.moneda=NEW.moneda) THEN RAISE EXCEPTION 'Pago no corresponde a revisión autorizada'; END IF;
  IF NEW.estado='PAGADO' THEN
   IF EXISTS(SELECT 1 FROM nomina_pago_fuente WHERE pago_id=NEW.id) THEN
    IF NEW.monto<>(SELECT sum(importe) FROM nomina_pago_fuente WHERE pago_id=NEW.id)
    OR NOT EXISTS(SELECT 1 FROM nomina_pago_fuente WHERE pago_id=NEW.id AND movimiento_id=NEW.movimiento_id)
    OR EXISTS(SELECT 1 FROM nomina_pago_fuente f JOIN movimiento_financiero m ON m.id=f.movimiento_id JOIN cuenta_financiera c ON c.id=f.cuenta_id WHERE f.pago_id=NEW.id AND (m.institucion_id<>NEW.institucion_id OR c.moneda<>NEW.moneda))
    THEN RAISE EXCEPTION 'Las fuentes no corresponden al importe, institución o moneda del pago'; END IF;
   ELSIF NOT EXISTS(SELECT 1 FROM movimiento_financiero m WHERE m.id=NEW.movimiento_id AND m.institucion_id=NEW.institucion_id AND m.clase='NOMINA' AND m.direccion='EGRESO' AND m.monto=NEW.monto AND m.clave_idempotencia='NOMINA-EMP:'||NEW.id) THEN RAISE EXCEPTION 'Falta egreso único del pago individual'; END IF;
  END IF;
 END IF; RETURN NEW;
END $$;
CREATE OR REPLACE FUNCTION validar_reversa_nomina() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
 IF EXISTS(SELECT 1 FROM nomina_pago_fuente WHERE pago_id=NEW.pago_id) THEN
  IF NOT EXISTS(SELECT 1 FROM nomina_pago_intento WHERE id=NEW.pago_id AND estado='PAGADO' AND institucion_id=NEW.institucion_id)
  OR EXISTS(SELECT 1 FROM nomina_pago_fuente f LEFT JOIN nomina_pago_fuente_reversa r ON r.pago_id=f.pago_id AND r.ordinal=f.ordinal LEFT JOIN movimiento_financiero m ON m.id=r.movimiento_id
   WHERE f.pago_id=NEW.pago_id AND (m.id IS NULL OR m.reversa_de_id<>f.movimiento_id OR m.monto<>f.importe OR m.cuenta_id<>f.cuenta_id OR m.clase<>'NOMINA_REVERSO' OR m.direccion<>'INGRESO'))
  OR NOT EXISTS(SELECT 1 FROM nomina_pago_fuente_reversa WHERE pago_id=NEW.pago_id AND movimiento_id=NEW.movimiento_id)
  THEN RAISE EXCEPTION 'La cancelación debe compensar todas las fuentes exactas del pago'; END IF;
 ELSIF NOT EXISTS(SELECT 1 FROM nomina_pago_intento ni JOIN movimiento_financiero m ON m.id=NEW.movimiento_id WHERE ni.id=NEW.pago_id AND ni.estado='PAGADO' AND ni.institucion_id=NEW.institucion_id AND m.institucion_id=ni.institucion_id AND m.clase='NOMINA_REVERSO' AND m.direccion='INGRESO' AND m.monto=ni.monto AND m.reversa_de_id=ni.movimiento_id) THEN RAISE EXCEPTION 'Compensación no corresponde al pago individual vigente'; END IF; RETURN NEW;
END $$;
