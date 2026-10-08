-- Sólo pasos editoriales; no genera adeudos ni publica casos de convenio no confirmados.
WITH pasos AS (
 UPDATE guia_proceso_paso
 SET instrucciones=instrucciones || ' Confirmar y generar adeudos seleccionados abre Confirmar generación de adeudos. Revisa cantidad e importe de la selección de todas las páginas y fecha de corte. Cancelar conserva la selección sin generar; sólo Confirmar y generar adeudos ejecuta la solicitud. Cancelar generación vuelve al listado sin emitir cargos.'
 WHERE instrucciones LIKE '%Confirmar y generar adeudos seleccionados%'
   AND guia_id IN (SELECT id FROM guia_proceso WHERE estado='CONFIRMADA')
 RETURNING guia_id
)
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-08'
WHERE id IN (SELECT DISTINCT guia_id FROM pasos);
