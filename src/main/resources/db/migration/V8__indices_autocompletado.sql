CREATE EXTENSION IF NOT EXISTS pg_trgm;

ALTER TABLE alumno ADD COLUMN busqueda_autocomplete TEXT GENERATED ALWAYS AS (
    lower(
        coalesce(matricula, '') || ' ' || coalesce(curp, '') || ' ' ||
        coalesce(nombres, '') || ' ' || coalesce(primer_apellido, '') || ' ' ||
        coalesce(segundo_apellido, '')
    )
) STORED;

ALTER TABLE tutor ADD COLUMN busqueda_autocomplete TEXT GENERATED ALWAYS AS (
    lower(
        coalesce(nombres, '') || ' ' || coalesce(primer_apellido, '') || ' ' ||
        coalesce(segundo_apellido, '') || ' ' || coalesce(telefono_principal, '') || ' ' ||
        coalesce(telefono_secundario, '') || ' ' || coalesce(email, '')
    )
) STORED;

ALTER TABLE usuario ADD COLUMN busqueda_autocomplete TEXT GENERATED ALWAYS AS (
    lower(coalesce(username, '') || ' ' || coalesce(email, ''))
) STORED;

CREATE INDEX ix_alumno_busqueda_autocomplete
    ON alumno USING gin (busqueda_autocomplete gin_trgm_ops);
CREATE INDEX ix_tutor_busqueda_autocomplete
    ON tutor USING gin (busqueda_autocomplete gin_trgm_ops);
CREATE INDEX ix_usuario_busqueda_autocomplete
    ON usuario USING gin (busqueda_autocomplete gin_trgm_ops);
