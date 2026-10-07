-- Sólo instrucciones editoriales: no crea cuotas ni cargos ni cambia permisos.
WITH pasos AS (
 UPDATE guia_proceso_paso
 SET instrucciones = instrucciones || ' Antes de guardar una cuota nueva, revisa el modal Confirmar nueva cuota: alumno, concepto, importe, frecuencia, fechas, estado y generación. Cancelar cierra sin guardar y conserva la captura; corrige lo necesario y vuelve a pulsar Crear cuota. Sólo Confirmar y crear cuota envía el formulario. Si vienes de la inscripción con Preparar cobro, revisa también Crear cargo al confirmar: crear un cargo no registra dinero recibido.'
 WHERE modulo='Cuotas por alumno'
   AND guia_id IN (SELECT id FROM guia_proceso WHERE estado='CONFIRMADA')
 RETURNING guia_id
)
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-07'
WHERE id IN (SELECT DISTINCT guia_id FROM pasos);
