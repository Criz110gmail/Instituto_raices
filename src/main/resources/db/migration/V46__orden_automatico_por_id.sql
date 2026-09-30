-- El orden deja de ser una captura del usuario. Se conserva como dato técnico para que
-- pueda ajustarse directamente en BD cuando la institución necesite una excepción.

DROP INDEX ux_nivel_institucion_orden;
UPDATE nivel_educativo SET orden = id::INTEGER;
CREATE UNIQUE INDEX ux_nivel_institucion_orden
    ON nivel_educativo (institucion_id, orden);

DROP INDEX ux_grado_nivel_orden;
UPDATE grado SET orden = id::INTEGER;
CREATE UNIQUE INDEX ux_grado_nivel_orden
    ON grado (nivel_educativo_id, orden);

ALTER TABLE periodo_academico DROP CONSTRAINT uq_periodo_orden;
UPDATE periodo_academico SET orden = id::INTEGER;
ALTER TABLE periodo_academico
    ADD CONSTRAINT uq_periodo_orden
    UNIQUE (ciclo_escolar_id, nivel_educativo_id, orden);

DROP INDEX ux_materia_grado_orden_activo;
UPDATE materia_grado SET orden = id::INTEGER;
CREATE UNIQUE INDEX ux_materia_grado_orden_activo
    ON materia_grado (grado_id, orden) WHERE activo = TRUE;

CREATE OR REPLACE FUNCTION asignar_orden_desde_id()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.id > 2147483647 THEN
        RAISE EXCEPTION 'El identificador excede la capacidad del campo orden';
    END IF;
    NEW.orden := NEW.id::INTEGER;
    RETURN NEW;
END;
$$;

CREATE TRIGGER tr_nivel_orden_desde_id
BEFORE INSERT ON nivel_educativo
FOR EACH ROW EXECUTE FUNCTION asignar_orden_desde_id();

CREATE TRIGGER tr_grado_orden_desde_id
BEFORE INSERT ON grado
FOR EACH ROW EXECUTE FUNCTION asignar_orden_desde_id();

CREATE TRIGGER tr_periodo_orden_desde_id
BEFORE INSERT ON periodo_academico
FOR EACH ROW EXECUTE FUNCTION asignar_orden_desde_id();

CREATE TRIGGER tr_materia_grado_orden_desde_id
BEFORE INSERT ON materia_grado
FOR EACH ROW EXECUTE FUNCTION asignar_orden_desde_id();
