-- Sólo evidencia editorial del propietario: no ejecuta ni repite la reversión.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES ('reversa-traspaso-reportes-confirmados',
$texto$Revertir un traspaso y comprobar sus reportes$texto$,'Tesorería',
$texto$Reversión de TRASPASO-TEST-001 confirmada el 17/10/2026 a las 16:10: origen recupera $2,000.00 y destino queda en $0.00. Libro de movimientos, Tesorería y estados mensuales con PDF y Excel coinciden. Pendientes adicionales: corte anterior del 16/10 después de la reversa y bloqueo de segunda reversión.$texto$,
$texto$Administrador con Movimientos financieros, Cuentas financieras y Reportes financieros, alcance institucional compatible. Reutilizar transferencia 1 del ensayo; ya está revertida y no debe repetirse. Los IDs pertenecen a esta instalación y Git no transporta datos. La guía no concede derechos ni registra dinero.$texto$,
$texto$Transferencia original TRASPASO-TEST-001 de $300.00: 16/10/2026 a las 20:01 desde BANCO-COMPRAS-TEST hacia BANCO-TRASPASO-TEST. Antes de revertir: $1,700.00 y $300.00. Reversa registrada por el propietario el 17/10/2026 a las 16:10: ingreso 300 al origen y egreso 300 del destino. Las filas compensan originales #6 y #7; #4/#2 junto a fecha son secuencias por cuenta, NO IDs de los nuevos movimientos. No se proporcionó el ID de la cabecera de reversión.$texto$,
1,DATE '2026-10-17','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p
ON p.codigo=ANY(ARRAY['CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER','MOVIMIENTO_FINANCIERO_REVERTIR','REPORTE_FINANCIERO_CONSULTAR'])
WHERE g.slug='reversa-traspaso-reportes-confirmados';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,v.ruta
FROM guia_proceso g JOIN (VALUES
(1,$texto$Identificar el traspaso ya aplicado$texto$,$texto$Cuentas y Movimientos financieros$texto$,
$texto$1. Identifica BANCO-COMPRAS-TEST y BANCO-TRASPASO-TEST, ambas institucionales y MXN. 2. Reconoce los saldos previos al ensayo de reversa: origen $1,700.00 y destino $300.00; hoy el caso ya está revertido y no debes restaurar esos valores. 3. En el libro localiza las dos filas originales del 16/10/2026 a 20:01, referencia TRASPASO-TEST-001 e importe $300.00. 4. Ambos enlaces de Revertir transferencia correspondían a transferencia 1; se usó el de la fila de ingreso del destino, pero cualquiera identifica la misma operación conjunta.$texto$,
$texto$Enlace del ensayo: /admin/reversiones/transferencias/1/nueva. No se revierte cada lado por separado.$texto$,
$texto$Operación y saldos previos identificados sin crear otra transferencia.$texto$,
$texto$Caso histórico ya revertido. No crear otra transferencia para volver a obtener esos saldos ni editar aperturas.$texto$,
$texto$/admin/movimientos-financieros$texto$),
(2,$texto$Revisar el formulario y confirmar mediante el modal$texto$,$texto$Reversión trazable$texto$,
$texto$1. Para un ensayo nuevo autorizado, revisa en la pantalla operación, cuentas e importe antes de capturar la fecha y hora real, no futura ni anterior al original o a las aperturas. 2. Captura un motivo de corrección explicativo; la fecha aceptada de este caso es 17/10/2026 a 16:10, no debe copiarse para registrar hoy otra operación. 3. Confirmar reversa trazable abre el modal con ID, recorrido original, importe, fecha original, fecha de reversa, motivo y resultado; Cancelar sólo cierra y conserva la captura. 4. Sólo Confirmar y registrar reversa envía la operación: el propietario confirmó la reversión y posteriormente mostró sus saldos y movimientos.$texto$,
$texto$Motivo sugerido durante el ensayo: Prueba controlada: reversión del traspaso TRASPASO-TEST-001 por $300.00 para comprobar devolución entre cuentas y conservación del historial. El texto exacto guardado no fue mostrado; no se da por verificado.$texto$,
$texto$Aplicación de reversa confirmada por el propietario tras publicar el modal. Cancelar/Escape/fondo y confirmación única tienen pruebas técnicas, sin aceptación humana separada.$texto$,
$texto$La reversa cambia saldos internos, no envía dinero al banco. No ejecutar otra reversa sólo por leer esta guía. Cancelar del formulario vuelve al libro; Cancelar del modal conserva el formulario.$texto$,
$texto$/admin/movimientos-financieros$texto$),
(3,$texto$Comprobar saldos y dos compensaciones$texto$,$texto$Cuentas financieras y libro de movimientos$texto$,
$texto$1. En Cuentas financieras comprueba BANCO-COMPRAS-TEST con saldo inicial y actual $2,000.00 y BANCO-TRASPASO-TEST con inicial y actual $0.00. 2. En el libro identifica17/10/2026 a 16:10, referencia TRASPASO-TEST-001: ingreso REVERSO $300.00 al origen, antes $1,700.00 y después $2,000.00, Compensa #6. 3. Identifica el egreso REVERSO $300.00 del destino, antes $300.00 y después $0.00, Compensa #7. 4. Distingue las secuencias #4 del origen y #2 del destino de los IDs de los originales compensados; los estados mensuales posteriores confirman que se conservan los originales.$texto$,
$texto$Total de ambas cuentas: $2,000.00 antes y después de la reversión. No hubo ingreso externo ni eliminación de movimientos.$texto$,
$texto$Saldos y dos movimientos compensatorios confirmados por el propietario.$texto$,
$texto$La observación Compensa #6/#7 identifica originales, no los IDs nuevos. La comprobación humana de bloqueo de segunda reversión aún está pendiente.$texto$,
$texto$/admin/movimientos-financieros$texto$),
(4,$texto$Comparar Tesorería del día de reversión con PDF y Excel$texto$,$texto$Reportes financieros → Tesorería$texto$,
$texto$1. Selecciona instituto Raices, Plantel Todos autorizados, todas las cuentas y agrupación Diaria. 2. Captura Desde 17/10/2026 y Hasta 17/10/2026 y genera: origen un movimiento, operación $0.00 y traspasos entrada/salida $300.00/$0.00; destino un movimiento, operación $0.00 y traspasos $0.00/$300.00. 3. Descarga Excel y compara ambas filas, tipos BANCO, alcance Institucional, neto operativo 0 y moneda MXN. 4. Descarga Balanza PDF: saldo inicial $5,150.00, ingresos operativos $0.00, egresos operativos $0.00, cierre $5,150.00 y netos de traspaso $300.00/-$300.00.$texto$,
$texto$Aunque las filas del libro son REVERSO, al compensar una transferencia Tesorería las clasifica como traspasos, no como ingresos o gastos operativos.$texto$,
$texto$Pantalla, PDF y Excel de Tesorería confirmados para 17/10/2026.$texto$,
$texto$El saldo consolidado incluye todas las cuentas autorizadas, no sólo las dos del ensayo. No atribuir otra operación posterior a la reversión.$texto$,
$texto$/admin/reportes-financieros/tesoreria$texto$),
(5,$texto$Comparar el estado mensual del origen en todas las salidas$texto$,$texto$Estado de cuenta por cuenta → Origen$texto$,
$texto$1. Selecciona BANCO-COMPRAS-TEST, corte Mensual, año 2026 y Octubre, y genera; no hay campos Desde/Hasta en este formulario. 2. Comprueba cuatro filas: reversa de traspaso ingreso 300 del 17 a 16:10; traspaso egreso 300, reversa de compra ingreso 406 y compra egreso 406 del 16/10/2026. 3. En PDF verifica periodo 01/10/2026 al 31/10/2026, apertura $0.00, ingresos $706.00, egresos $706.00, cierre $2,000.00 y saldo inicial registrado en periodo $2,000.00 separado de ingresos. 4. En Excel compara Resumen con cuatro movimientos y esos totales; en Estado de cuenta verifica las cuatro filas, clases, referencias y moneda MXN.$texto$,
$texto$$0.00 + $2,000.00 + $706.00 - $706.00 = $2,000.00. Secuencias4/3/2/1 y referencia TICKET-PRUEBA-001 para compra/reversa 406; TRASPASO-TEST-001 para transferencia/reversa 300.$texto$,
$texto$Pantalla, PDF y ambas hojas Excel del origen confirmados, con cuatro movimientos conservados.$texto$,
$texto$Estado de cuenta incluye traspasos en ingresos/egresos; Tesorería los separa de operación. Apertura cero es al inicio del mes: la cuenta se abrió dentro del periodo.$texto$,
$texto$/admin/reportes-financieros/estado-cuenta-cuenta$texto$),
(6,$texto$Comparar el estado mensual del destino en todas las salidas$texto$,$texto$Estado de cuenta por cuenta → Destino$texto$,
$texto$1. Elimina la selección de cuenta origen, busca y selecciona la sugerencia BANCO-TRASPASO-TEST, conservando Mensual, año 2026 y Octubre, y vuelve a generar. 2. Comprueba dos filas: egreso REVERSO $300.00 del 17/10/2026 a 16:10, secuencia 2, y entrada TRASPASO $300.00 del 16/10/2026 a 20:01, secuencia 1, ambas con TRASPASO-TEST-001. 3. Descarga un PDF nuevo y revisa el encabezado de cuenta destino, apertura $0.00, ingresos $300.00, egresos $300.00 y cierre $0.00. 4. En Excel revisa Resumen con dos movimientos, esos totales e inicial registrado $0.00; en Estado de cuenta compara ambas filas y MXN.$texto$,
$texto$El propietario primero volvió a copiar el origen por error y después aportó la cuenta correcta. Sólo la última evidencia de BANCO-TRASPASO-TEST confirma el destino.$texto$,
$texto$Pantalla, PDF y ambas hojas Excel del destino confirmados. Original y compensación permanecen visibles.$texto$,
$texto$Escribir el nombre no basta: seleccionar la sugerencia, generar y descargar de nuevo. No reutilizar archivos de la otra cuenta ni afirmar un fallo del filtro por la primera copia equivocada.$texto$,
$texto$/admin/reportes-financieros/estado-cuenta-cuenta$texto$),
(7,$texto$Separar aceptación y dos comprobaciones adicionales pendientes$texto$,$texto$Reportes y control de reversión$texto$,
$texto$1. Conserva confirmados saldos, movimientos, Tesorería del 17 y estados mensuales de octubre en pantalla, PDF y Excel. 2. Mantén pendiente volver a consultar el corte de Tesorería 16/10/2026 después de la reversión para confirmar que conserva los valores anteriores; la evidencia anterior a revertir no confirma este reensayo. 3. Mantén pendiente comprobar que transferencia 1 ya revertida no ofrece otra reversión, sólo revisando su pantalla: no forzar un POST ni intentar crear compensaciones adicionales. 4. Documenta cada comprobación adicional sólo cuando el propietario aporte el resultado, sin marcarla como aceptada por pruebas automáticas.$texto$,
$texto$Esperado para el corte16: apertura4650, ingresos906, egresos406, cierre5150 y salida300 del origen/entrada300 al destino. Esperado para bloqueo: aviso de operación ya revertida sin formulario de confirmación. Ambos son expectativas, NO resultados humanos confirmados.$texto$,
$texto$Reversión y reportes confirmados; dos pruebas adicionales pendientes explícitas.$texto$,
$texto$No repetir la reversión para verificar bloqueo. Una guía CONFIRMADA no convierte en aceptadas las comprobaciones que esta etapa marca pendientes.$texto$,
$texto$/admin/movimientos-financieros$texto$)
) v(numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion,ruta) ON true
WHERE g.slug='reversa-traspaso-reportes-confirmados';

-- Supersede pendientes editoriales anteriores sin modificar V106/V107 ya aplicadas.
UPDATE guia_proceso SET resumen=$texto$Transferencia TRASPASO-TEST-001 de $300.00 y reportes confirmados. Reversa del 17/10/2026 a 16:10 también confirmada: origen $2,000.00 y destino $0.00, con Tesorería, PDF y Excel; consultar Revertir un traspaso y comprobar sus reportes. Pendientes adicionales: reconsulta del corte16 tras revertir y bloqueo de segunda reversión.$texto$,
version_contenido=version_contenido+1,revisada_el=DATE '2026-10-17'
WHERE slug IN ('transferencia-entre-cuentas-confirmada','reportes-traspaso-confirmados');
UPDATE guia_proceso_paso SET titulo=$texto$Consultar la reversión posterior ya confirmada$texto$,
instrucciones=$texto$1. Conserva los resultados anteriores como evidencia previa a la reversión, no como saldo mensual actual. 2. Consulta Revertir un traspaso y comprobar sus reportes para la reversión del 17/10/2026 a 16:10, ya aceptada por el propietario. 3. Distingue los saldos anteriores $1,700.00/$300.00 de los posteriores $2,000.00/$0.00 y conserva todos los movimientos. 4. Mantén pendientes únicamente la reconsulta del corte16 después de revertir y el bloqueo humano de otra reversión; no repetir operaciones.$texto$,
ejemplo=$texto$Guía posterior: reversa-traspaso-reportes-confirmados. Origen mensual: cuatro movimientos e ingresos/egresos706 cada uno; destino dos movimientos e ingresos/egresos300 cada uno.$texto$,
resultado=$texto$Transferencia, reversión y reportes confirmados; dos comprobaciones adicionales aún pendientes.$texto$
WHERE guia_id IN (SELECT id FROM guia_proceso WHERE slug IN ('transferencia-entre-cuentas-confirmada','reportes-traspaso-confirmados')) AND numero=6;
UPDATE guia_proceso_paso SET precaucion=precaucion || $texto$ Resultado histórico ANTERIOR a la reversión del 17/10/2026: al consultar octubre después de revertir, origen tiene cuatro movimientos y cierre $2,000.00, destino dos y cierre $0.00. Ver la guía de reversión confirmada; no repetir el traspaso para reproducir la fotografía anterior.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='reportes-traspaso-confirmados') AND numero IN (3,4,5);
