-- Sólo texto editorial, sin registrar pagos ni conceder permisos.
WITH pasos AS (
 UPDATE guia_proceso_paso
 SET instrucciones=instrucciones || ' Al registrar un pago administrativo, tanto desde Adeudos de alumnos → Ver → Resumen → Registrar pago como desde Pagos recibidos → Nuevo registro, Registrar como pendiente abre Confirmar registro del pago. Revisa tutor, total, método, cuenta, fecha, referencia, distribución y archivos. Cancelar conserva la captura sin guardar; sólo Confirmar y registrar como pendiente envía el formulario. Todavía no mueve saldos: después se valida desde Gestión.'
 WHERE instrucciones LIKE '%Registrar como pendiente%'
   AND guia_id IN (SELECT id FROM guia_proceso WHERE estado='CONFIRMADA')
 RETURNING guia_id
)
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-07'
WHERE id IN (SELECT DISTINCT guia_id FROM pasos);
