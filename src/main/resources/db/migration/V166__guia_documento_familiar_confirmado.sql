-- Guía editorial del recorrido confirmado; no modifica expedientes ni permisos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('propuesta-documento-familiar','Enviar y aprobar un documento familiar','Control escolar',
'La familia propone un documento para el alumno y administración lo revisa antes de incorporarlo al expediente oficial.',
'Alumno y tutor vinculados con autorización, cuenta familiar activa y personal administrativo con acceso institucional a Actualizaciones familiares y al expediente documental. Utilizar un archivo ficticio durante la capacitación.',
'El usuario confirmó el 07/11/2026 que pudo abrir el documento en la revisión administrativa, aprobarlo y verlo como aprobado en administración y en el portal familiar; también confirmó que el documento aparece en el expediente oficial.',
1,DATE '2026-11-07','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN ('ACTUALIZACION_EXPEDIENTE_LEER','ACTUALIZACION_EXPEDIENTE_REVISAR','ALUMNO_LEER')
WHERE g.slug='propuesta-documento-familiar';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.* FROM guia_proceso g CROSS JOIN (VALUES
(1,'Proponer el documento','Tutor','Portal familiar → Expediente familiar',
'1. Entra al portal familiar y selecciona al alumno. 2. Abre Expediente familiar y pulsa Proponer documento. 3. Selecciona el tipo, arrastra el archivo o haz clic para elegirlo; completa las fechas y descripción si corresponden. 4. Escribe un mensaje, acepta el consentimiento y pulsa Enviar para revisión.',
'Para capacitación usa Otro documento y un PDF ficticio con una descripción que lo identifique como prueba.',
'La solicitud se envía para revisión; la familia puede consultar su estado.',
'PDF hasta 10 MB e imágenes hasta 20 MB antes de optimizarlas. La propuesta todavía requiere revisión administrativa.',NULL),
(2,'Revisar y aprobar','Administración','Control escolar → Actualizaciones familiares',
'1. Filtra la institución y selecciona el alumno. 2. Abre la propuesta pendiente con Revisar y comprueba el resumen. 3. En Propuesta pulsa Abrir documento y revisa el archivo. 4. En Revisión captura una respuesta para la familia, pulsa Aprobar e incorporar y confirma en el modal.',
'Respuesta de ejemplo: Documento revisado y aprobado para la prueba.',
'El usuario confirmó la apertura durante la revisión y la aprobación en administración.',
'Verifica el archivo correcto antes de aprobar. Cancelar en el modal no envía la decisión.','/admin/actualizaciones-expediente'),
(3,'Comprobar expediente y portal','Administración y tutor','Alumnos → Expediente documental y portal familiar',
'1. En Alumnos busca al alumno y abre Editar → Expediente documental. 2. Localiza el documento aprobado. 3. En el portal familiar abre Expediente familiar y consulta Solicitudes enviadas para confirmar el estado y la respuesta.',
'Busca el documento ficticio enviado en el paso 1.',
'El usuario confirmó que el documento aparece en el expediente del alumno y que la solicitud figura aprobada en el portal familiar.',
'La verificación confirmada incluye la apertura en revisión administrativa y la presencia en el expediente. No se afirmó una segunda apertura desde el expediente.', '/admin/catalogos/alumnos')
) AS v(numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
WHERE g.slug='propuesta-documento-familiar';
