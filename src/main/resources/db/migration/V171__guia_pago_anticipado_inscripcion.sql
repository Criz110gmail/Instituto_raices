-- Contenido editorial confirmado; no registra cargos, pagos ni movimientos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES ('pago-anticipado-desde-inscripcion','Preparar un pago anticipado desde la inscripción','Cobranza',
'Elige varios cobros de un alumno para preparar un solo pago, sin duplicar mensualidades.',
'Inscripción vigente, tutor responsable financiero y cuotas configuradas. Requiere permisos de Inscripciones, Adeudos de alumnos y Pagos recibidos, además del alcance correspondiente.',
'Para practicar, usa una cuota ficticia de $500 mensuales y selecciona dos meses: el total será $1,000 si no hay becas ni otros ajustes.',
1,DATE '2026-11-09','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN ('INSCRIPCION_ADMINISTRAR','CARGO_ADMINISTRAR','PAGO_REGISTRAR')
WHERE g.slug='pago-anticipado-desde-inscripcion';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.* FROM guia_proceso g CROSS JOIN (VALUES
(1,'Abrir la inscripción','Administración','Inscripciones → Editar → Cobranza',
'1. Busca la inscripción del alumno. 2. Pulsa Editar y abre Cobranza. 3. Pulsa Preparar pago anticipado.',
'Usa una inscripción vigente de prueba con tutor responsable financiero.','Se abre la pantalla para consultar los periodos del alumno.',
'Si falta el botón, revisa los permisos de Inscripciones, Adeudos de alumnos y Pagos recibidos, así como el alcance de institución y plantel.','/admin/inscripciones'),
(2,'Consultar y seleccionar','Administración','Preparar pago anticipado',
'1. Captura las fechas Desde y Hasta. 2. Pulsa Consultar cargos y mensualidades. 3. Marca los conceptos y periodos que se van a pagar. 4. Revisa el total seleccionado.',
'Dos mensualidades de $500 suman $1,000 cuando no hay ajustes.','Aparecen solo los cargos y mensualidades disponibles para pagar.',
'Los cargos ya liquidados, cancelados, sustituidos o con un pago en revisión no aparecen para seleccionar. Consultar no crea cargos ni mueve dinero.','/admin/inscripciones'),
(3,'Revisar y preparar','Administración','Preparar pago anticipado',
'1. Pulsa Generar faltantes y preparar pago. 2. Revisa los conceptos, periodos y total en la confirmación. 3. Si necesitas corregir algo, pulsa Cancelar. 4. Si todo es correcto, confirma.',
'Selecciona noviembre y diciembre para preparar un pago conjunto.','Se generan únicamente los cargos faltantes y se abre el formulario de pago con una distribución por cargo.',
'Confirmar esta selección aún no registra dinero; si abandonas el formulario posterior, los cargos generados se conservan.','/admin/inscripciones'),
(4,'Registrar el pago pendiente','Administración','Pagos recibidos → Registrar pago pendiente',
'1. Comprueba tutor, cargos, importes y total. 2. Captura la fecha, método y cuenta declarada; agrega referencia y comprobante cuando corresponda. 3. Pulsa Registrar como pendiente. 4. Revisa y confirma.',
'Efectivo en caja: dos cargos de $500 y un pago pendiente de $1,000.','Queda registrado un pago pendiente de validación con su distribución por cargo.',
'Registrar como pendiente no liquida cargos ni cambia el saldo de la cuenta. La información debe corresponder a un pago realmente recibido.','/admin/catalogos/pagos'),
(5,'Validar y comprobar','Administración','Pagos recibidos → Ver pago',
'1. Abre el pago pendiente. 2. Verifica cuenta, importe y cargos. 3. Valida el pago una sola vez. 4. Consulta los adeudos y movimientos financieros para comprobar el resultado.',
'Un pago validado de $1,000 aplica $500 a cada una de las dos mensualidades y registra un ingreso de $1,000.','Los cargos pagados dejan de ofrecerse para otro anticipo y el ingreso se refleja en la cuenta.',
'Validar sí aplica el pago y registra el ingreso. Si hay diferencias, revisa el pago antes de validarlo.','/admin/catalogos/pagos'),
(6,'Evitar pagos repetidos','Administración','Inscripciones → Cobranza → Preparar pago anticipado',
'1. Vuelve a consultar los periodos. 2. Comprueba que los meses pagados no aparezcan como disponibles. 3. Si otro usuario registró un pago o cambió un cargo mientras tu pantalla estaba abierta, vuelve a consultar antes de continuar.',
'Noviembre y diciembre pagados no se ofrecen de nuevo; enero sin pagar sigue disponible.','El sistema impide continuar con una selección desactualizada o con un pago en revisión.',
'Cancelar un pago pendiente conserva su historial y permite volver a consultar el cargo si sigue disponible.','/admin/inscripciones')
) AS v(numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
WHERE g.slug='pago-anticipado-desde-inscripcion';
