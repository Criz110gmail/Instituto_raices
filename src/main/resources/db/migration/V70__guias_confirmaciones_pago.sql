-- Sólo contenido editorial; no valida, rechaza ni cancela pagos.
WITH pasos AS (
 UPDATE guia_proceso_paso
 SET instrucciones=replace(instrucciones,'Confirmar operación','Confirmar validación') ||
   ' Al pulsar Validar y publicar, revisa Confirmar validación del pago: folio, titular, importe y cuenta destino. Cancelar cierra sin publicar; sólo Confirmar validación registra la operación.'
 WHERE instrucciones LIKE '%Validar y publicar%'
   AND guia_id IN (SELECT id FROM guia_proceso WHERE estado='CONFIRMADA')
 RETURNING guia_id
)
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-07'
WHERE id IN (SELECT DISTINCT guia_id FROM pasos);

UPDATE guia_proceso_paso
SET instrucciones=instrucciones || ' Rechazar sin afectar saldos abre Confirmar rechazo del pago con folio, importe y motivo. Cancelar no ejecuta el rechazo y conserva la captura. Para registrar la decisión pulsa Confirmar rechazo.'
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='pago-rechazado-reenvio-400') AND numero=6;
