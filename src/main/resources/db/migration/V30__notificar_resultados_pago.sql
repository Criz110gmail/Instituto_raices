ALTER TABLE notificacion_usuario
    ADD COLUMN pago_id BIGINT REFERENCES pago(id);

ALTER TABLE notificacion_usuario
    ALTER COLUMN tipo TYPE VARCHAR(20);

ALTER TABLE notificacion_usuario
    DROP CONSTRAINT ck_notificacion_tipo,
    DROP CONSTRAINT ck_notificacion_origen;

ALTER TABLE notificacion_usuario
    ADD CONSTRAINT ck_notificacion_tipo CHECK (
        tipo IN ('EVENTO', 'AVISO', 'PAGO_VALIDADO', 'PAGO_RECHAZADO')
    ),
    ADD CONSTRAINT ck_notificacion_origen CHECK (
        (tipo = 'EVENTO' AND evento_id IS NOT NULL AND aviso_id IS NULL AND pago_id IS NULL)
        OR (tipo = 'AVISO' AND aviso_id IS NOT NULL AND evento_id IS NULL AND pago_id IS NULL)
        OR (tipo IN ('PAGO_VALIDADO', 'PAGO_RECHAZADO')
            AND pago_id IS NOT NULL AND evento_id IS NULL AND aviso_id IS NULL)
    );

CREATE INDEX ix_notificacion_pago
    ON notificacion_usuario (pago_id) WHERE pago_id IS NOT NULL;
