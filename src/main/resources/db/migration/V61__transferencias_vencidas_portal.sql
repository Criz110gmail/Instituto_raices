ALTER TABLE plantel ADD COLUMN permitir_transferencias_vencidas boolean NOT NULL DEFAULT false;
ALTER TABLE cargo ADD COLUMN transferencia_vencida_autorizada boolean NOT NULL DEFAULT false;
-- Autorizaciones y revocaciones se conservan en la bitácora inmutable existente.
-- No modifica vencimientos, saldos, pagos, roles ni activa planteles existentes.

UPDATE guia_proceso_paso SET precaucion=replace(precaucion,
 'Este cargo ya está vencido: el selector de Reportar transferencia del portal lo excluye. Usa administración para registrar ese pago; no cambies el vencimiento para hacerlo aparecer.',
 'Este cargo está vencido: puede pagarse presencialmente desde administración o reportarse por transferencia si el plantel lo permite o existe una autorización individual. Configuración escolar → Planteles → Editar → Permitir reportar transferencias de cargos vencidos; para un solo caso, Adeudos de alumnos → Ver → Transferencia de cargo vencido, con motivo obligatorio. No cambies la fecha límite para hacerlo aparecer. Un pago en revisión bloquea otro reporte del mismo cargo.')
WHERE precaucion LIKE 'Este cargo ya está vencido:%';
UPDATE guia_proceso SET requisitos=replace(requisitos,
 'Fecha límite vigente para que el último saldo aparezca al reportar en el portal.',
 'Fecha límite vigente, o transferencia vencida habilitada por el plantel o una autorización individual, para que el último saldo aparezca al reportar en el portal.'),
 version_contenido=version_contenido+1,revisada_el=DATE '2026-10-06'
WHERE slug IN ('beca-recargo-liquidacion','pagos-parciales-liquidacion');
