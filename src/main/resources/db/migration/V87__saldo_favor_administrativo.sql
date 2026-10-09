-- El disponible sigue derivándose de pago - aplicaciones netas - devoluciones.
ALTER TABLE pago ADD COLUMN monto_reportado NUMERIC(19,2), ADD COLUMN motivo_cambio_monto VARCHAR(2000);
ALTER TABLE pago ADD CONSTRAINT ck_pago_monto_reportado CHECK (
 (monto_reportado IS NULL AND motivo_cambio_monto IS NULL) OR
 (monto_reportado IS NOT NULL AND monto_reportado > 0 AND monto >= monto_reportado AND motivo_cambio_monto IS NOT NULL AND length(trim(motivo_cambio_monto)) > 0));
ALTER TABLE aplicacion_pago ADD COLUMN saldo_favor BOOLEAN NOT NULL DEFAULT false,
 ADD COLUMN motivo_saldo_favor VARCHAR(2000), ADD COLUMN autorizado_por_id BIGINT REFERENCES usuario(id),
 ADD COLUMN clave_saldo_favor VARCHAR(100);
ALTER TABLE aplicacion_pago ADD CONSTRAINT ck_aplicacion_saldo_favor CHECK (
 NOT saldo_favor OR (motivo_saldo_favor IS NOT NULL AND length(trim(motivo_saldo_favor)) > 0 AND autorizado_por_id IS NOT NULL));
CREATE UNIQUE INDEX ux_aplicacion_saldo_clave ON aplicacion_pago(pago_id,clave_saldo_favor) WHERE clave_saldo_favor IS NOT NULL;
CREATE INDEX ix_pago_tutor_saldo ON pago(institucion_id,tutor_id,plantel_registro_id,id) WHERE estado='VALIDADO';
