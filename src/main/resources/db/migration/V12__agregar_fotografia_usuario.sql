ALTER TABLE usuario
    ADD COLUMN fotografia_archivo_id BIGINT REFERENCES archivo(id);

CREATE UNIQUE INDEX ux_usuario_fotografia_archivo
    ON usuario (fotografia_archivo_id)
    WHERE fotografia_archivo_id IS NOT NULL;
