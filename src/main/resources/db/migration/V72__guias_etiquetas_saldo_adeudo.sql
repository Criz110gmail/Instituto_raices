-- Homologar etiquetas del resumen; sólo contenido editorial.
WITH pasos AS (
 UPDATE guia_proceso_paso
 SET instrucciones=replace(replace(replace(instrucciones,'Pagado acumulado','Abonos vigentes'),'pagado acumulado','abonos vigentes'),'Saldo exigible','Falta por pagar'),
     resultado=replace(replace(replace(resultado,'Pagado acumulado','Abonos vigentes'),'pagado acumulado','abonos vigentes'),'Saldo exigible','Falta por pagar')
 WHERE (instrucciones || resultado) ~ '(Pagado acumulado|pagado acumulado|Saldo exigible)'
   AND guia_id IN (SELECT id FROM guia_proceso WHERE estado='CONFIRMADA')
 RETURNING guia_id
)
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-07'
WHERE id IN (SELECT DISTINCT guia_id FROM pasos);
