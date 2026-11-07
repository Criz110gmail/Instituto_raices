-- Guía editorial de la prueba confirmada; no modifica expedientes ni permisos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('actualizacion-familiar-ficha-medica','Revisar una propuesta familiar de ficha médica','Control escolar',
'El tutor propone una actualización médica y administración decide si rechazarla o incorporarla a la ficha oficial.',
'Alumno y tutor vinculados y autorizados, cuenta familiar activa, ficha médica existente y usuario administrativo con acceso institucional a Actualizaciones familiares. Usar información ficticia para capacitación.',
'Prueba confirmada por el usuario el 06/11/2026: una propuesta rechazada se mostró en el portal sin cambiar la ficha; una segunda propuesta aprobada se mostró en el portal e incorporó la observación enviada a la ficha oficial.',
1,DATE '2026-11-06','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN ('ACTUALIZACION_EXPEDIENTE_LEER','ACTUALIZACION_EXPEDIENTE_REVISAR','ALUMNO_LEER')
WHERE g.slug='actualizacion-familiar-ficha-medica';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.* FROM guia_proceso g CROSS JOIN (VALUES
(1,'Enviar una propuesta médica','Tutor','Portal familiar → Expediente',
'1. Inicia sesión con la cuenta familiar autorizada y selecciona al alumno. 2. Abre su expediente y solicita actualizar la ficha médica. 3. Revisa todos los campos propuestos, captura el mensaje y proporciona el consentimiento solicitado. 4. Envía la propuesta.',
'En una prueba controlada usa un alumno y datos ficticios; registra una observación que puedas identificar.',
'La propuesta queda pendiente de revisión; aún no cambia la ficha oficial.',
'La ficha propuesta es completa: al aprobarla sustituirá los datos médicos vigentes. No envíes información real en una demostración.',NULL),
(2,'Revisar y rechazar','Administración','Control escolar → Actualizaciones familiares',
'1. En Actualizaciones familiares filtra la institución y selecciona al alumno en el autocompletado. 2. Abre Revisar y consulta Resumen y Propuesta. 3. En Revisión escribe el motivo, pulsa Rechazar propuesta y confirma en el modal.',
'Motivo de ejemplo: La información de prueba debe corregirse antes de incorporarse.',
'La familia consulta el estado Rechazada y la ficha médica oficial conserva sus datos anteriores; el usuario confirmó ambos resultados.',
'Cancelar en el modal cierra la confirmación sin enviar la decisión. El rechazo no aplica los datos propuestos.','/admin/actualizaciones-expediente'),
(3,'Enviar una nueva propuesta','Tutor','Portal familiar → Expediente',
'1. Después del rechazo, crea una nueva propuesta médica para el mismo alumno. 2. Conserva los demás campos y cambia una observación fácilmente identificable. 3. Envía y comprueba que siga pendiente antes de su revisión.',
'Observación ficticia: Prueba de actualización aprobada.',
'La ficha oficial conserva sus datos previos hasta que una persona autorizada apruebe.',
'No supongas que la nueva propuesta se incorpora automáticamente al enviarse.',NULL),
(4,'Aprobar y verificar','Administración y tutor','Actualizaciones familiares y ficha médica',
'1. En Actualizaciones familiares abre la nueva propuesta y revisa su contenido. 2. En Revisión escribe la respuesta para la familia, pulsa Aprobar e incorporar y confirma. 3. Verifica el estado Aprobada en el portal familiar. 4. En Alumnos abre la ficha médica oficial y comprueba la observación aprobada.',
'Respuesta ficticia: Información revisada y aprobada para la prueba.',
'El usuario confirmó que la propuesta se mostró aprobada en el portal y la observación quedó en la ficha oficial.',
'La aprobación reemplaza la información médica con la propuesta completa. Confirma todos los campos antes de aceptarla; Cancelar no aplica cambios.','/admin/actualizaciones-expediente')
) AS v(numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
WHERE g.slug='actualizacion-familiar-ficha-medica';
