-- Homologar el nombre del enlace de navegación, sin ejecutar operaciones financieras.
WITH pasos AS (
 UPDATE guia_proceso_paso
 SET instrucciones=replace(instrucciones,
   'Cancelar generación vuelve al listado sin emitir cargos.',
   'El enlace ← Volver a adeudos de alumnos, separado debajo de la confirmación, vuelve al listado sin emitir cargos ni modificar cuotas.')
 WHERE instrucciones LIKE '%Cancelar generación vuelve al listado sin emitir cargos.%'
 RETURNING guia_id
)
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-08'
WHERE id IN (SELECT DISTINCT guia_id FROM pasos);
