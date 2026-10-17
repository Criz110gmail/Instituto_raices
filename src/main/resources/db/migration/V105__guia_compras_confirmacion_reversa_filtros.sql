-- Contenido editorial: no crea proveedores, compras, cuentas ni movimientos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES ('compras-confirmacion-reversa',
$texto$Compra de prueba: egreso, cancelación controlada y consulta del historial$texto$,'Tesorería',
$texto$Proceso confirmado por el propietario: compra COM-00000001 por $406.00, egreso original #4, cancelación mediante modal, saldo restaurado y filtros/Excel coincidentes. El formato nuevo de fecha y moneda del Excel tiene verificación técnica; queda pendiente volver a descargarlo para aceptación visual del propietario.$texto$,
$texto$Usuario administrativo persistido con Proveedores, Compras, Motivos financieros, Cuentas financieras, Movimientos financieros y Guía de procesos, autorizado para la institución y plantel. Reutilizar los datos existentes. Las capturas y confirmaciones descritas ya se realizaron: para consultar esta instalación no volver a generar egresos ni cancelar otra vez. En otra instalación reproducir únicamente con autorización y datos aislados. Git no transporta registros operativos.$texto$,
$texto$Ensayo del 16/10/2026: instituto Raices; operación en Plantel prueba de permisos. Proveedor Papelería Prueba Raíces, sin RFC. Cuenta institucional BANCO-COMPRAS-TEST · Cuenta de prueba de compras, saldo inicial $2,000.00. Compra COM-00000001, documento TICKET-PRUEBA-001: subtotal $350.00, impuesto $56.00 y total $406.00. Saldo tras egreso $1,594.00; tras reversa $2,000.00. El plantel de prueba ya tiene actividad de compras, no está vacío. No se conoce el ID de la reversa; no confundir secuencias #1/#2 de la cuenta con identificadores de movimiento.$texto$,
1,DATE '2026-10-16','CONFIRMADA');

-- Limita quién consulta el procedimiento; no concede permisos a roles.
INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p
ON p.codigo=ANY(ARRAY['PROVEEDOR_LEER','PROVEEDOR_ADMINISTRAR','COMPRA_LEER','COMPRA_ADMINISTRAR',
'COMPRA_CONFIRMAR','COMPRA_CANCELAR','MOTIVO_FINANCIERO_LEER','MOTIVO_FINANCIERO_ADMINISTRAR',
'CUENTA_FINANCIERA_LEER','CUENTA_FINANCIERA_ADMINISTRAR','MOVIMIENTO_FINANCIERO_LEER'])
WHERE g.slug='compras-confirmacion-reversa';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,v.ruta
FROM guia_proceso g JOIN (VALUES
(1,$texto$Identificar los datos de prueba ya existentes$texto$,$texto$Proveedores y Motivos financieros$texto$,
$texto$1. En Proveedores busca Papelería Prueba Raíces y comprueba su institución y estado activo. 2. Verifica que el RFC es opcional; en este ensayo se dejó vacío. 3. En Motivos financieros identifica COMPRA-PAPEL-TEST, Papelería de prueba, naturaleza Egreso, categoría PRUEBAS y activo. 4. Si reproduces en otra instalación, crea sólo los registros faltantes autorizados y revisa el resumen del modal antes de guardar el proveedor.$texto$,
$texto$Proveedor Papelería Prueba Raíces; motivo COMPRA-PAPEL-TEST. Son ejemplos de prueba, no documentos fiscales ni proveedores reales verificados.$texto$,
$texto$Proveedor y motivo identificados sin duplicarlos.$texto$,
$texto$La prueba confirma el flujo utilizado, no todas las variantes de alta, edición, inactivación o RFC duplicado. No editar catálogos operativos para reproducirla.$texto$,
$texto$/admin/proveedores$texto$),
(2,$texto$Comprobar la cuenta de origen y el plantel$texto$,$texto$Cuentas financieras$texto$,
$texto$1. Localiza BANCO-COMPRAS-TEST · Cuenta de prueba de compras. 2. Comprueba tipo Cuenta bancaria, moneda MXN, alcance Institucional y fecha inicial 16/10/2026. 3. Distingue el saldo inicial $2,000.00 del saldo actual; tras la cancelación aceptada ambos son $2,000.00. 4. Al revisar la compra identifica Plantel prueba de permisos como plantel de la operación, aunque la cuenta sea compartida institucional.$texto$,
$texto$Banco ficticio de pruebas; cuenta institucional con $2,000.00 de apertura. No es una cuenta de otro plantel ni una conexión con un banco real.$texto$,
$texto$Cuenta compatible con el plantel de la compra y saldo de referencia conocido.$texto$,
$texto$No cambiar el saldo inicial para cuadrar una prueba. Otros movimientos posteriores pueden cambiar el saldo actual; compara también los movimientos vinculados.$texto$,
$texto$/admin/catalogos/cuentas-financieras$texto$),
(3,$texto$Revisar la captura y las dos partidas del borrador$texto$,$texto$Compras → Datos y Productos$texto$,
$texto$1. Abre la compra existente COM-00000001; en otra instalación autorizada usa Nueva compra y elige institución, Plantel prueba de permisos, proveedor y cuenta indicados. 2. En los buscadores de captura enfoca el campo para los primeros diez resultados o escribe tres caracteres y selecciona proveedor, cuenta y categoría. 3. Revisa dos paquetes de papel a $100.00 cada uno y tres cajas de lápices a $50.00 cada una, ambas partidas con impuesto 16%. 4. Comprueba documento TICKET-PRUEBA-001, subtotal $350.00, impuesto $56.00 y total $406.00 antes de aceptar el modal de guardar borrador.$texto$,
$texto$Papel: $200.00 + $32.00 = $232.00. Lápices: $150.00 + $24.00 = $174.00. Total $406.00; cantidades 2 y 3.$texto$,
$texto$El borrador conserva las partidas y todavía no produce un egreso. En esta instalación el proceso ya terminó y la compra está Cancelada.$texto$,
$texto$No guardar otra compra para revisar ésta. Las cantidades admiten decimales para servicios o medidas; aquí se usaron enteros. No confundir precio unitario con total de la partida.$texto$,
$texto$/admin/compras$texto$),
(4,$texto$Reconocer la confirmación financiera ya aceptada$texto$,$texto$Compras → Resumen y Gestión$texto$,
$texto$1. Consulta el resumen, proveedor, documento, cuenta y total antes de confirmar un borrador autorizado. 2. Al pulsar Confirmar y registrar egreso revisa el modal; su opción Cancelar sólo lo cierra sin registrar dinero. 3. Confirma únicamente si corresponde registrar el egreso real de esa compra; en el ensayo ya se aceptó esta operación una vez. 4. Comprueba la referencia Movimiento financiero #4 y el saldo posterior $1,594.00 que tuvo la cuenta tras descontar $406.00.$texto$,
$texto$$2,000.00 - $406.00 = $1,594.00. Compra COM-00000001; egreso de operación #4, documento TICKET-PRUEBA-001.$texto$,
$texto$Un egreso asociado a la compra, sin duplicar el registro al consultar su detalle.$texto$,
$texto$No volver a confirmar COM-00000001: está cancelada. El saldo $1,594.00 corresponde al momento anterior a la reversa, no al saldo actual aceptado.$texto$,
$texto$/admin/compras$texto$),
(5,$texto$Reconocer la cancelación mediante reversa y su modal$texto$,$texto$Compras → Gestión$texto$,
$texto$1. En una compra confirmada autorizada abre Gestión y localiza el motivo obligatorio debajo de la explicación. 2. Escribe una justificación real antes de pulsar Cancelar mediante reversa. 3. Revisa el modal; Volver sin cancelar sólo lo cierra y conserva el motivo, sin revertir dinero. 4. Usa Confirmar cancelación sólo cuando proceda; el propietario ya lo hizo y confirmó que COM-00000001 cambió a Cancelada.$texto$,
$texto$Se aceptó el modal de cancelación de la compra por $406.00; su motivo queda en el historial, no se borra el documento.$texto$,
$texto$Compra Cancelada conservada para consulta y saldo restituido mediante compensación.$texto$,
$texto$No repetir la cancelación ni usar Revertir operación desde Movimientos para esta misma compra. El motivo de otra instalación debe describir su caso real, no copiar una justificación ficticia.$texto$,
$texto$/admin/compras$texto$),
(6,$texto$Verificar saldo restaurado y trazabilidad del reverso$texto$,$texto$Cuentas y Movimientos financieros$texto$,
$texto$1. Consulta la cuenta BANCO-COMPRAS-TEST y comprueba el saldo actual aceptado $2,000.00. 2. En Movimientos financieros filtra esa cuenta y fecha 16/10/2026. 3. Identifica el egreso original $406.00, antes $2,000.00 y después $1,594.00, marcado Revertido. 4. Identifica el ingreso REVERSO $406.00, antes $1,594.00 y después $2,000.00, con Compensa #4.$texto$,
$texto$$2,000.00 - $406.00 + $406.00 = $2,000.00. El egreso y el reverso permanecen visibles; no se borraron movimientos.$texto$,
$texto$Reversa y saldo confirmados por el propietario, manteniendo el vínculo con el egreso original #4.$texto$,
$texto$No inventar el identificador de la reversa. Los números #1 y #2 junto a fecha pueden ser la secuencia de la cuenta, no los IDs del movimiento financiero.$texto$,
$texto$/admin/movimientos-financieros$texto$),
(7,$texto$Consultar la compra con los filtros del historial$texto$,$texto$Compras → Filtros$texto$,
$texto$1. Selecciona instituto Raices y Plantel prueba de permisos. 2. En Proveedor y Cuenta enfoca para diez opciones iniciales o escribe al menos tres caracteres y selecciona Papelería Prueba Raíces y Cuenta de prueba de compras. 3. Usa estado Cancelada, fechas 16/10/2026 a 16/10/2026 y folio COM-00000001 para localizar el registro; un buscador vacío significa todos, no texto libre sin seleccionar. 4. Pulsa Consultar y compara folio, proveedor, cuenta, documento TICKET-PRUEBA-001, total $406.00 y estado Cancelada.$texto$,
$texto$El propietario confirmó que los filtros funcionan correctamente y que la fila coincide con la exportación.$texto$,
$texto$Compra localizada sin crear datos ni modificar su estado. Opciones legibles y filtros alineados aceptados por el propietario.$texto$,
$texto$No dar por probadas manualmente todas las combinaciones, catálogos de gran volumen o paginación sólo por este caso. Cambiar institución o plantel puede limpiar selecciones incompatibles.$texto$,
$texto$/admin/compras$texto$),
(8,$texto$Comparar el Excel filtrado y confirmar el formato nuevo$texto$,$texto$Compras → Exportar Excel$texto$,
$texto$1. Conserva los filtros de la consulta y descarga Excel desde Compras. 2. Comprueba COM-00000001, proveedor, RFC vacío, plantel, cuenta, TICKET-PRUEBA-001 y estado Cancelada; la columna Movimiento debe indicar 4 (egreso original). 3. En la versión corregida comprueba fecha 16/10/2026 17:58, hora de la institución, y total $406.00; fecha e importe son celdas numéricas nativas, no texto. 4. Confirma visualmente esa nueva descarga antes de dar por aceptado el ajuste de formato; el contenido y los filtros del archivo anterior ya fueron aceptados.$texto$,
$texto$Formato de fecha dd/mm/yyyy HH:mm; moneda con centavos y miles, por ejemplo $1,000,000.50, sin alterar el importe numérico.$texto$,
$texto$Contenido exportado confirmado. Nuevo formato comprobado técnicamente con fecha, moneda, cambio de día, dos páginas y consulta vacía; aceptación visual del nuevo archivo todavía pendiente.$texto$,
$texto$Movimiento 4 sigue siendo el egreso original asociado, no el ID desconocido de su reversa. No volver a confirmar ni cancelar la compra para descargar el archivo.$texto$,
$texto$/admin/compras$texto$)
) v(numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion,ruta) ON true
WHERE g.slug='compras-confirmacion-reversa';
