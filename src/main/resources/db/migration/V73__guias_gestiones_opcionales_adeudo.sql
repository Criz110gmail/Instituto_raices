-- Sólo navegación editorial de gestiones ya existentes; sin nuevos casos confirmados.
WITH pasos AS (
 UPDATE guia_proceso_paso
 SET instrucciones=instrucciones || ' Para localizar Transferencia de cargo vencido o Corregir y reemplazar cargo, entra en la pestaña Gestión del adeudo y despliega Otras gestiones opcionales · no son pasos para cancelar. Son acciones independientes y no se requieren para cancelar el adeudo.'
 WHERE (instrucciones LIKE '%Transferencia de cargo vencido%' OR instrucciones LIKE '%Corregir y reemplazar cargo%')
   AND guia_id IN (SELECT id FROM guia_proceso WHERE estado='CONFIRMADA')
 RETURNING guia_id
)
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-07'
WHERE id IN (SELECT DISTINCT guia_id FROM pasos);
