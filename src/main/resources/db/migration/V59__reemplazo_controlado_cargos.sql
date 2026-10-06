ALTER TABLE cargo
    ADD COLUMN reemplaza_cargo_id BIGINT REFERENCES cargo(id),
    ADD COLUMN motivo_reemplazo VARCHAR(2000);
ALTER TABLE cargo ADD CONSTRAINT ux_cargo_reemplazo UNIQUE (reemplaza_cargo_id);
ALTER TABLE cargo ADD CONSTRAINT ck_cargo_reemplazo CHECK (
    (reemplaza_cargo_id IS NULL AND motivo_reemplazo IS NULL)
    OR (reemplaza_cargo_id IS NOT NULL AND reemplaza_cargo_id <> id
        AND motivo_reemplazo IS NOT NULL AND btrim(motivo_reemplazo) <> '')
);

-- Actualiza precauciones de guías existentes; no declara probado el nuevo escenario.
UPDATE guia_proceso_paso SET precaucion=precaucion ||
    ' Una cuota única ya generada tiene fechas e importe protegidos. Cambiar una cuota mensual sólo afecta emisiones faltantes. Cancelar un cargo no lo regenera: consulta Cuotas no incluidas y motivo; para corregir el vencimiento usa Corregir y reemplazar cargo, con autorización y motivo obligatorio.'
WHERE modulo IN ('Cuotas por alumno','Adeudos de alumnos');
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-06'
WHERE id IN (SELECT guia_id FROM guia_proceso_paso WHERE modulo IN ('Cuotas por alumno','Adeudos de alumnos'));
