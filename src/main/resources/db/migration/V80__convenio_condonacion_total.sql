-- Modalidad explícita: no crea pagos, movimientos ni cargos de importe cero.
ALTER TABLE convenio_pago ADD COLUMN modalidad VARCHAR(30) NOT NULL DEFAULT 'MONTO_ACORDADO';
ALTER TABLE convenio_pago ADD COLUMN autorizado_por_id BIGINT REFERENCES usuario(id);
ALTER TABLE convenio_pago ADD COLUMN autorizado_en TIMESTAMPTZ;
ALTER TABLE convenio_pago ADD COLUMN autorizado_por_nombre VARCHAR(180);
ALTER TABLE convenio_pago DROP CONSTRAINT ck_convenio_montos;
ALTER TABLE convenio_pago DROP CONSTRAINT ck_convenio_estado;
ALTER TABLE convenio_pago DROP CONSTRAINT ck_convenio_cancelacion;
ALTER TABLE convenio_pago ADD CONSTRAINT ck_convenio_montos CHECK (
 saldo_original_total>0 AND monto_condonado_total=saldo_original_total-monto_acordado_total AND
 ((modalidad='MONTO_ACORDADO' AND monto_acordado_total>0 AND monto_acordado_total<=saldo_original_total AND estado IN ('VIGENTE','CANCELADO')) OR
  (modalidad='CONDONACION_TOTAL' AND monto_acordado_total=0 AND estado IN ('CONDONADO_TOTAL','CANCELADO')
   AND autorizado_por_id IS NOT NULL AND autorizado_en IS NOT NULL AND autorizado_por_nombre IS NOT NULL AND btrim(autorizado_por_nombre)<>'')));
ALTER TABLE convenio_pago ADD CONSTRAINT ck_convenio_estado CHECK (estado IN ('VIGENTE','CANCELADO','CONDONADO_TOTAL'));
ALTER TABLE convenio_pago ADD CONSTRAINT ck_convenio_cancelacion CHECK (
 (estado IN ('VIGENTE','CONDONADO_TOTAL') AND cancelado_en IS NULL AND motivo_cancelacion IS NULL)
 OR (estado='CANCELADO' AND cancelado_en IS NOT NULL AND motivo_cancelacion IS NOT NULL AND btrim(motivo_cancelacion)<>''));
ALTER TABLE convenio_pago ADD CONSTRAINT ck_convenio_motivo CHECK (btrim(motivo)<>'');

-- Un adeudo puede tener acuerdos históricos cancelados, pero un único acuerdo activo.
ALTER TABLE convenio_pago_cargo_original ADD COLUMN activo BOOLEAN NOT NULL DEFAULT true;
UPDATE convenio_pago_cargo_original o SET activo=false FROM convenio_pago c WHERE c.id=o.convenio_pago_id AND c.estado='CANCELADO';
ALTER TABLE convenio_pago_cargo_original DROP CONSTRAINT convenio_pago_cargo_original_cargo_id_key;
CREATE UNIQUE INDEX uq_convenio_original_activo ON convenio_pago_cargo_original(cargo_id) WHERE activo;

CREATE FUNCTION validar_nuevo_cargo_convenio() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
 IF (SELECT modalidad FROM convenio_pago WHERE id=NEW.convenio_pago_id)<>'MONTO_ACORDADO' THEN
  RAISE EXCEPTION 'Una condonación total no genera cargos nuevos';
 END IF;
 RETURN NEW;
END;
$$;
CREATE TRIGGER trg_convenio_nuevo_modalidad BEFORE INSERT OR UPDATE ON convenio_pago_cargo_nuevo
FOR EACH ROW EXECUTE FUNCTION validar_nuevo_cargo_convenio();
