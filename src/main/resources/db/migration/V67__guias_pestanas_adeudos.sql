-- Sólo navegación editorial; no altera cargos, pagos ni ajustes.
WITH pasos_actualizados AS (
 UPDATE guia_proceso_paso
 SET instrucciones = replace(replace(replace(instrucciones,
   'Transferencia de cargo vencido', 'Gestión → Transferencia de cargo vencido'),
   'Historial de ajustes', 'pestaña Historial → Historial de ajustes'),
   'Transferencias reportadas por el tutor', 'pestaña Transferencias → Transferencias reportadas por el tutor')
 WHERE guia_id IN (SELECT id FROM guia_proceso WHERE estado='CONFIRMADA')
   AND instrucciones ~ '(Transferencia de cargo vencido|Historial de ajustes|Transferencias reportadas por el tutor)'
 RETURNING guia_id
)
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-07'
WHERE id IN (SELECT DISTINCT guia_id FROM pasos_actualizados);
