-- Sólo etiqueta editorial; no modifica conceptos, convenios ni adeudos operativos.
WITH pasos AS (
 UPDATE guia_proceso_paso
 SET instrucciones=replace(instrucciones,'Concepto del nuevo cargo','Concepto de cobro del convenio')
 WHERE instrucciones LIKE '%Concepto del nuevo cargo%'
 RETURNING guia_id
)
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-08'
WHERE id IN (SELECT DISTINCT guia_id FROM pasos);
