ALTER TABLE cargo
    ADD COLUMN motivo_fecha_registro_diferente VARCHAR(1000);

ALTER TABLE cargo
    ADD CONSTRAINT ck_cargo_motivo_fecha_registro
    CHECK (motivo_fecha_registro_diferente IS NULL
        OR btrim(motivo_fecha_registro_diferente) <> '');
