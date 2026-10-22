-- Sólo contenido editorial. Recorrido del catálogo confirmado por el propietario el21/10/2026.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('conceptos-nomina-catalogo-confirmado','Conceptos de nómina: crear, filtrar y proteger cambios','Personal',
'Registra percepciones, deducciones y aportaciones patronales sin capturar importes; revisa confirmaciones, filtra y exporta, activa o desactiva y evita duplicados o sobrescrituras entre pestañas.',
'Administrador institucional identificado con Empleados, Datos laborales de empleados y Puestos y condiciones salariales; además, permiso de Guía de procesos para consultar este recorrido. No concede accesos ni ejecuta operaciones. Usa códigos de ensayo autorizados y revisa si ya existen antes de registrar. No repetir el caso ya concluido ni modificar asignaciones salariales de Bruce.',
'BON-PUNT-TEST: Bono de puntualidad de prueba, Percepción/Habitual; DESC-TEST: Descuento autorizado de prueba, Deducción/Ocasional; APORT-TEST: Aportación de la escuela de prueba, Aportación patronal/Habitual. El propietario confirmó filtros, Excel, activación/desactivación y dos pestañas; al terminar dejó los tres conceptos Activos.',1,DATE '2026-10-21','CONFIRMADA');
INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN ('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_ADMINISTRAR') WHERE g.slug='conceptos-nomina-catalogo-confirmado';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Entrar y distinguir catálogo de nómina','Administración → Personal y nómina → Conceptos de nómina',
'1. Inicia sesión como administrador institucional autorizado; si cambiaron permisos, cierra e inicia sesión. 2. Entra a Personal y nómina → Conceptos de nómina: el menú abre listado, filtros, paginación, Exportar Excel y Nuevo concepto. 3. Comprueba si ya existen BON-PUNT-TEST, DESC-TEST y APORT-TEST; reutiliza para consultar, no repitas altas. 4. Identifica el alcance: este catálogo sólo describe conceptos y su aplicación; todavía no asigna importes a empleados ni genera nóminas.',
'Percepción suma al empleado; Deducción resta; Aportación patronal es costo de la escuela y no reduce el pago del empleado. Los efectos son descriptivos, no operaciones realizadas.',
'Acceso al catálogo correcto, sin alterar el expediente ni las condiciones salariales.',
'El sueldo base sigue en sus condiciones laborales existentes. No recapturarlo ni tocar las asignaciones de enero/febrero del ensayo anterior. No usar personal real para aprender.','/admin/conceptos-nomina'),
(2,'Crear percepción y probar Cancelar del modal','Administración → Personal y nómina → Conceptos de nómina → Nuevo concepto',
'1. Desde el listado pulsa Nuevo concepto; captura código BON-PUNT-TEST, nombre Bono de puntualidad de prueba y descripción Bono de prueba para comprobar el catálogo, sin asignarlo a empleados. 2. Selecciona Tipo de concepto Percepción, Forma de aplicación Habitual y Concepto activo marcado. 3. Pulsa Crear concepto: revisa código, nombre, tipo, aplicación, efecto Suma al empleado, descripción y estado; pulsa Cancelar dentro del modal y comprueba que conserva captura sin registrar. 4. Repite Crear concepto y pulsa Confirmar y guardar; vuelve al listado con aviso de guardado y Activo en verde.',
'BON-PUNT-TEST · Bono de puntualidad de prueba; Percepción/Habitual. Habitual describe que se propondrá en periodos correspondientes cuando exista una futura asignación vigente.',
'Un único concepto guardado; Cancelar del modal no lo crea ni pierde datos.',
'Si ya existe el código no confirmes otra alta: consulta el registro. En un nuevo ensayo autorizado usa otro código coherente. No existe un importe global del bono en esta pantalla.','/admin/conceptos-nomina'),
(3,'Crear deducción ocasional','Administración → Personal y nómina → Conceptos de nómina → Nuevo concepto',
'1. Regresa al listado y pulsa Nuevo concepto. 2. Captura código DESC-TEST, nombre Descuento autorizado de prueba y descripción Clasificación de prueba; no descuenta dinero ni recupera préstamos. 3. Selecciona Tipo Deducción, Aplicación Ocasional y Activo marcado; pulsa Crear concepto. 4. En el modal comprueba Resta al empleado y Ocasional; confirma y verifica la fila en el listado, usando Limpiar si conservabas filtros que la excluyen.',
'DESC-TEST · Descuento autorizado de prueba; Deducción/Ocasional. Ocasional corresponde a una nómina específica cuando esa etapa esté implementada.',
'Concepto de deducción identificado, sin descuento aplicado.',
'No capturar porcentajes, saldo de préstamo ni retenciones legales aquí. Crear un concepto llamado préstamo no implementa entrega, recuperación ni saldo.','/admin/conceptos-nomina'),
(4,'Crear aportación sin reducir el pago del empleado','Administración → Personal y nómina → Conceptos de nómina → Nuevo concepto',
'1. En el listado pulsa Nuevo concepto. 2. Captura código APORT-TEST, nombre Aportación de la escuela de prueba y descripción Costo de referencia de la escuela; no disminuye el pago del empleado. 3. Selecciona Aportación patronal, Habitual y Activo marcado; pulsa Crear concepto. 4. Revisa que el efecto del modal y de la tabla diga Costo de la escuela; no reduce el pago del empleado; confirma y vuelve al listado.',
'APORT-TEST · Aportación de la escuela de prueba; Aportación patronal/Habitual.',
'Aportación diferenciada de una deducción del empleado.',
'Es clasificación de catálogo, no cálculo ni determinación de una obligación fiscal. No genera pago, retención o movimiento financiero.','/admin/conceptos-nomina'),
(5,'Usar ayuda y combinar filtros','Administración → Personal y nómina → Conceptos de nómina → Filtros',
'1. Pulsa Limpiar y enfoca Concepto de nómina sin escribir: aparecen hasta diez opciones; si sólo existen tres, muestra tres. 2. Escribe BON y selecciona BON-PUNT-TEST desde las sugerencias; la búsqueda comienza desde tres caracteres. 3. Elige Estado Activos, Tipo de concepto Percepción y Forma de aplicación Habitual, deja Texto adicional vacío y pulsa Aplicar filtros. 4. Comprueba que sólo aparece el bono seleccionado; la × limpia únicamente ese concepto y Limpiar restablece todos los criterios.',
'La etiqueta contiene código y nombre pero filtra por ID, no por concatenar el texto. Texto adicional es opcional para código, nombre o descripción.',
'Consulta independiente y combinable por concepto, estado, tipo y aplicación.',
'Escribir sin seleccionar una sugerencia muestra validación en vez de consultar todos inadvertidamente. Las ayudas pueden mostrar inactivos identificados para consulta histórica.','/admin/conceptos-nomina'),
(6,'Exportar los mismos resultados y conservar filtros','Administración → Personal y nómina → Conceptos de nómina → Exportar Excel y Editar',
'1. Mantén el filtro del bono BON-PUNT-TEST, Activos, Percepción y Habitual del paso anterior. 2. Pulsa Exportar Excel y abre el archivo: debe contener sólo ese concepto con código, nombre, tipo, efecto, aplicación, descripción y estado, sin importes. 3. En el listado pulsa Editar del bono y después Volver al listado sin modificar nada; deben conservarse filtros y página. 4. Pulsa Limpiar y combina Deducción/Ocasional para localizar DESC-TEST; la exportación debe respetar también esa combinación.',
'El propietario confirmó que los filtros de pantalla y Excel coincidieron. El catálogo tiene consulta paginada y exportación por bloques.',
'Excel consistente con la consulta y regreso sin perder el contexto.',
'Si un registro no aparece, revisa primero los filtros conservados; no significa que se haya borrado. No distribuir exportaciones laborales a usuarios sin autorización.','/admin/conceptos-nomina'),
(7,'Editar y diferenciar las dos opciones Cancelar','Administración → Personal y nómina → Conceptos de nómina → Editar',
'1. Selecciona BON-PUNT-TEST, aplica filtros y pulsa Editar; cambia Descripción a Descripción actualizada del bono de prueba, sin pagos ni importes. 2. Pulsa Guardar cambios y Cancelar dentro del modal: mantiene el texto capturado pero todavía no lo guarda. 3. Pulsa Cancelar del formulario, junto a Guardar cambios, y vuelve a abrir Editar: debe conservar la descripción original del registro. 4. Recaptura la descripción actualizada, pulsa Guardar cambios → Confirmar y guardar y abre Editar otra vez para comprobar que ahora sí persistió.',
'Cancelar del modal sólo cierra la confirmación; Cancelar del formulario regresa al listado sin guardar. Volver al listado tampoco registra cambios.',
'Edición confirmada, sin escrituras al cancelar y sin perder captura al cerrar el modal.',
'Un cambio del catálogo no debe modificar silenciosamente futuros registros históricos de nómina. En esta entrega todavía no existen asignaciones de estos conceptos a empleados.','/admin/conceptos-nomina'),
(8,'Desactivar, filtrar inactivos y reactivar','Administración → Personal y nómina → Conceptos de nómina → Editar y Filtros',
'1. Localiza DESC-TEST y pulsa Editar; desmarca Concepto activo, pulsa Guardar cambios, revisa Inactivo en el modal y confirma. 2. Pulsa Limpiar y filtra Estado Inactivos, Tipo Deducción y Aplicación Ocasional; aplica y verifica DESC-TEST, también en Excel. 3. Abre Editar del mismo registro, marca Concepto activo y confirma el guardado para reactivarlo. 4. Pulsa Limpiar y consulta Activos: DESC-TEST vuelve a aparecer; en el ensayo confirmado se dejó Activo al terminar.',
'Resultado final confirmado: BON-PUNT-TEST, DESC-TEST y APORT-TEST Activos. La desactivación y reactivación se probaron para validar filtros, no quedó una baja pendiente.',
'Activación/desactivación sin eliminar el registro ni su auditoría.',
'Si tras desactivar desaparece al filtrar Activos, es correcto. Para localizarlo selecciona Inactivos o Todos. No hay borrado físico del concepto.','/admin/conceptos-nomina'),
(9,'Comprobar obligatorios y código duplicado','Administración → Personal y nómina → Conceptos de nómina → Nuevo concepto',
'1. Abre Nuevo concepto e intenta Crear concepto sin completar los obligatorios: debe impedir el envío e indicar los campos necesarios. 2. Captura código bon-punt-test en minúsculas, nombre Este registro duplicado no debe guardarse, Tipo Percepción, Aplicación Habitual y Activo marcado. 3. Pulsa Crear concepto y confirma el resumen; debe rechazar el código repetido, conservar la captura y no crear otro registro. 4. Pulsa Cancelar del formulario para salir; vuelve al listado y verifica que sólo exista el bono original.',
'El código se normaliza a mayúsculas y debe ser único dentro de la institución. Letras, números, guion o guion bajo, sin espacios.',
'Validaciones claras y ningún alta duplicada.',
'Las ayudas informativas son neutrales; sólo las validaciones corresponden a errores. No resolver un duplicado sobrescribiendo otro concepto.','/admin/conceptos-nomina'),
(10,'Evitar sobrescritura entre dos pestañas y cerrar','Administración → Personal y nómina → Conceptos de nómina → Editar en dos pestañas',
'1. Abre Editar de BON-PUNT-TEST en dos pestañas A y B antes de guardar en cualquiera. 2. En A captura Descripción Cambio autorizado desde la pestaña A y guarda confirmando el modal. 3. Sin recargar B, escribe ESTE CAMBIO DE LA PESTAÑA B NO DEBE GUARDARSE e intenta guardar y confirmar: debe pedir recargar porque el registro cambió; abre Editar de nuevo desde el listado y verifica que prevalece A. 4. Conserva los tres conceptos Activos como resultado final; consulta esta guía sin repetir operaciones y después revisa con el administrador el diseño de Conceptos por empleado.',
'El propietario confirmó que la prueba de dos pestañas funcionó. Las versiones evitan sobrescribir datos de otro usuario o de una pantalla atrasada.',
'Catálogo validado y trazable; no se generaron importes por empleado, cálculos ni pagos por registrar estos conceptos.',
'La aceptación es del recorrido reportado; no equivale a auditoría exhaustiva de todos los accesos. Conceptos por empleado requiere importe y vigencia en una entrega separada; no duplicar el sueldo base existente ni generar nómina todavía.','/admin/conceptos-nomina')
)v(n,t,m,i,e,r,p,u) WHERE g.slug='conceptos-nomina-catalogo-confirmado';

UPDATE guia_proceso_paso SET precaucion='Este recorrido laboral ya está confirmado. El catálogo siguiente también tiene guía: Guía de procesos → Personal → Conceptos de nómina: crear, filtrar y proteger cambios. No repetir asignaciones o cancelaciones para consultarla. Después se analiza Conceptos por empleado, sin generar nómina todavía.'
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='asignaciones-laborales-configuracion-historica') AND numero=12;
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-21' WHERE slug='asignaciones-laborales-configuracion-historica';
