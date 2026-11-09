-- Confirmación editorial: no modifica expedientes ni archivos.
UPDATE guia_proceso_paso SET
 instrucciones='1. En Alumnos busca al alumno y abre Editar → Expediente documental. 2. Localiza el documento aprobado y pulsa Abrir documento. 3. Comprueba que se abre en otra pestaña y corresponde al archivo enviado. 4. En el portal familiar consulta Solicitudes enviadas para revisar el estado y la respuesta.',
 resultado='El usuario confirmó que el documento aprobado aparece en el expediente del alumno y que abre correctamente desde ahí.',
 precaucion='Consulta el archivo correcto. Abrirlo no modifica la solicitud ni el expediente.'
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='propuesta-documento-familiar') AND numero=3;
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-11-09',
 ejemplo='El usuario confirmó el envío y aprobación de un documento familiar, su presencia en el expediente oficial y su apertura correcta desde Alumnos → Expediente documental.'
WHERE slug='propuesta-documento-familiar';
