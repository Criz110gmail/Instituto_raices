ALTER TABLE pago
    ADD COLUMN origen_registro VARCHAR(25) NOT NULL DEFAULT 'ADMINISTRACION',
    ADD COLUMN reportado_por_id BIGINT REFERENCES usuario(id);

ALTER TABLE pago
    ADD CONSTRAINT ck_pago_origen_registro
        CHECK (origen_registro IN ('ADMINISTRACION', 'PORTAL_FAMILIAR')),
    ADD CONSTRAINT ck_pago_reportante
        CHECK (
            (origen_registro = 'ADMINISTRACION' AND reportado_por_id IS NULL)
            OR (origen_registro = 'PORTAL_FAMILIAR' AND reportado_por_id IS NOT NULL)
        );

CREATE INDEX ix_pago_reportante_estado_fecha
    ON pago (reportado_por_id, estado, fecha_pago DESC)
    WHERE reportado_por_id IS NOT NULL;
