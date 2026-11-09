-- Guía editorial del proceso confirmado; no modifica compras ni permisos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('comprobantes-compras-confirmados','Comprobantes de compras y permisos','Compras y proveedores',
'Guarda y consulta los documentos que respaldan una compra, conserva su historial y controla quién puede administrarlos.',
'Una compra y usuarios con acceso a la institución y plantel correspondientes. Compras - Consultar permite consultar; Compras - Administrar permite gestionar documentos. Para capacitación usa un borrador y archivos ficticios.',
'Compra ficticia de dos unidades a $100 con 16% de impuesto: subtotal $200, impuesto $32 y total $232. Se prueba con PDF, fotografía y XML de ejemplo, sin confirmar egreso.',
1,DATE '2026-11-09','CONFIRMADA');
INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo='COMPRA_LEER' WHERE g.slug='comprobantes-compras-confirmados';
INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.* FROM guia_proceso g CROSS JOIN (VALUES
(1,'Abrir el expediente','Administración','Compras → Ver compra → Comprobantes',
'1. Entra a Compras y busca la compra. 2. Pulsa Ver compra. 3. Selecciona Comprobantes. 4. Revisa el estado de la compra y su respaldo.',
'Para practicar, usa un borrador ficticio por $232.','La pestaña muestra la clasificación y los documentos de esa compra.',
'Guardar comprobantes no registra un egreso. Confirmar y registrar egreso sí afecta la cuenta; no lo pulses para esta prueba.','/admin/compras'),
(2,'Clasificar el respaldo','Administración','Comprobantes → Clasificar respaldo',
'1. Pulsa Clasificar respaldo. 2. Selecciona Sin comprobante, Ticket o recibo, o Factura CFDI. 3. Captura la justificación cuando corresponda. 4. Pulsa Revisar y guardar. 5. Revisa el modal; Cancelar conserva la captura y Confirmar guarda.',
'Sin comprobante: Compra ficticia de capacitación sin comprobante fiscal.','Se muestra la clasificación y se conserva su historial.',
'Sin comprobante requiere motivo. Factura CFDI requiere un XML vigente y justificación si hay diferencias.','/admin/compras'),
(3,'Adjuntar y abrir archivos','Administración','Comprobantes → Adjuntar comprobante',
'1. Pulsa Adjuntar comprobante. 2. Selecciona PDF, Fotografía o XML CFDI. 3. Agrega una descripción y selecciona el archivo. 4. Revisa y confirma. 5. En la tabla pulsa Abrir archivo.',
'Usa archivos ficticios y una descripción que indique que son de prueba.','El archivo queda en el expediente y se abre en otra pestaña.',
'PDF hasta 10 MB, imágenes hasta 20 MB y XML hasta 2 MB. Las imágenes se optimizan.','/admin/compras'),
(4,'Consultar XML e importar partidas','Administración','Comprobantes → Ver datos CFDI',
'1. En el XML pulsa Ver datos CFDI. 2. Revisa emisor, receptor, importes, conceptos, impuestos y diferencias. 3. Si deseas sustituir las partidas de un borrador, abre Importar al borrador, revisa la vista previa, escribe el motivo y confirma.',
'Ejemplo ficticio: dos unidades a $100 más $32 de impuesto, total $232.','El XML original se conserva; la importación confirmada cambia las partidas del borrador y no crea un egreso.',
'La lectura es estructural y no verifica autenticidad ni vigencia en SAT. Los XML con descuentos, retenciones o importes no compatibles requieren captura manual.','/admin/compras'),
(5,'Retirar y encontrar un comprobante','Administración','Comprobantes → Retirar',
'1. Pulsa Retirar en el archivo, escribe el motivo y revisa. 2. Confirma el retiro. 3. En Estado del documento selecciona Retirados o Todos y aplica filtros. 4. Abre el archivo para consultar su historial.',
'Motivo: Archivo ficticio retirado para comprobar el historial.','El archivo aparece como Retirado con su motivo y continúa disponible para consulta.',
'El UUID de un XML retirado no se libera. Para retirar el único XML vigente de una clasificación CFDI, cambia antes la clasificación.','/admin/compras'),
(6,'Filtrar, exportar y comprobar permisos','Administración','Comprobantes y Roles',
'1. Combina tipo, estado y búsqueda; pulsa Aplicar filtros. 2. Exporta Excel y revisa los resultados. 3. Con un usuario de Compras - Consultar comprueba que puede ver y abrir archivos, pero no gestionar documentos. 4. Comprueba también una URL de captura y el acceso desde un plantel fuera de su alcance.',
'Un PDF retirado aparece con estado Retirados. Un usuario limitado a otro plantel no puede abrir el archivo.','El usuario confirmó filtros, Excel, consulta, bloqueo de acciones y acceso según plantel.',
'Compras - Administrar también permite confirmar y cancelar compras; asigna cada permiso según las funciones del usuario.','/admin/compras')
) AS v(numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
WHERE g.slug='comprobantes-compras-confirmados';
