ALTER TABLE maestro
    ADD COLUMN fotografia_archivo_id BIGINT REFERENCES archivo(id);

CREATE UNIQUE INDEX ux_maestro_fotografia_archivo
    ON maestro (fotografia_archivo_id)
    WHERE fotografia_archivo_id IS NOT NULL;
