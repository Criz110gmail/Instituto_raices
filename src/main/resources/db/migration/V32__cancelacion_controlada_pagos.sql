ALTER TABLE pago
    ADD COLUMN cancelado_en TIMESTAMPTZ,
    ADD COLUMN cancelado_por_id BIGINT REFERENCES usuario(id);

ALTER TABLE pago ADD CONSTRAINT ck_pago_cancelacion_trazable CHECK (
    (estado = 'CANCELADO' AND cancelado_en IS NOT NULL AND cancelado_por_id IS NOT NULL)
    OR (estado <> 'CANCELADO' AND cancelado_en IS NULL AND cancelado_por_id IS NULL)
);

ALTER TABLE movimiento_financiero DROP CONSTRAINT ck_movimiento_clase;
ALTER TABLE movimiento_financiero ADD CONSTRAINT ck_movimiento_clase CHECK (
    clase IN ('COBRO', 'OPERACION', 'TRASPASO', 'DEVOLUCION', 'AJUSTE', 'REVERSO', 'ANULACION')
);
ALTER TABLE movimiento_financiero ADD CONSTRAINT ck_movimiento_anulacion_pago CHECK (
    clase <> 'ANULACION' OR (
        direccion = 'EGRESO' AND reversa_de_id IS NOT NULL
        AND pago_id IS NULL AND transferencia_id IS NULL AND devolucion_pago_id IS NULL
        AND reversion_financiera_id IS NULL
    )
);

INSERT INTO permiso (codigo, descripcion) VALUES
    ('PAGO_CANCELAR', 'Cancelar pagos registrados por error y compensar su ingreso y aplicaciones');

ALTER TABLE notificacion_usuario DROP CONSTRAINT ck_notificacion_tipo;
ALTER TABLE notificacion_usuario DROP CONSTRAINT ck_notificacion_origen;
ALTER TABLE notificacion_usuario ADD CONSTRAINT ck_notificacion_tipo CHECK (
    tipo IN ('EVENTO', 'AVISO', 'PAGO_VALIDADO', 'PAGO_RECHAZADO', 'PAGO_CANCELADO')
);
ALTER TABLE notificacion_usuario ADD CONSTRAINT ck_notificacion_origen CHECK (
    (tipo = 'EVENTO' AND evento_id IS NOT NULL AND aviso_id IS NULL AND pago_id IS NULL)
    OR (tipo = 'AVISO' AND aviso_id IS NOT NULL AND evento_id IS NULL AND pago_id IS NULL)
    OR (tipo IN ('PAGO_VALIDADO', 'PAGO_RECHAZADO', 'PAGO_CANCELADO')
        AND pago_id IS NOT NULL AND evento_id IS NULL AND aviso_id IS NULL)
);
