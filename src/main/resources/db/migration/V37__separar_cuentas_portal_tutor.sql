ALTER TABLE usuario
    ADD COLUMN tipo_cuenta VARCHAR(20) NOT NULL DEFAULT 'ADMINISTRATIVO';

UPDATE usuario u
SET tipo_cuenta = 'PORTAL_TUTOR'
WHERE EXISTS (SELECT 1 FROM tutor t WHERE t.usuario_id = u.id);

ALTER TABLE usuario
    ADD CONSTRAINT ck_usuario_tipo_cuenta
        CHECK (tipo_cuenta IN ('ADMINISTRATIVO', 'PORTAL_TUTOR'));

CREATE INDEX ix_usuario_institucion_tipo_estado
    ON usuario (institucion_id, tipo_cuenta, estado);
