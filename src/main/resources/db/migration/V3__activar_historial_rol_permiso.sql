ALTER TABLE rol_permiso
    ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;

CREATE INDEX ix_rol_permiso_rol_activo ON rol_permiso (rol_id, activo);
