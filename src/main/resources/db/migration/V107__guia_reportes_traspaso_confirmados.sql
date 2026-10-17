-- Sólo documentación aceptada por el propietario. No registra ni revierte dinero.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES ('reportes-traspaso-confirmados',
$texto$Comprobar un traspaso en Tesorería y estados de cuenta$texto$,'Tesorería',
$texto$Pantalla, PDF y Excel confirmados para TRASPASO-TEST-001: salida de $300.00, entrada de $300.00, sin ingresos o egresos operativos por el traspaso. Estados mensuales de octubre con saldos de origen $1,700.00 y destino $300.00. Reversa pendiente, no aceptada.$texto$,
$texto$Administrador con Reportes financieros y alcance compatible con instituto Raices y cuentas institucionales. Reutilizar el traspaso aplicado; no registrar otro. Los registros y sus IDs pertenecen a esta instalación, Git no transporta datos. Si existen movimientos posteriores, separar sus efectos antes de comparar los saldos.$texto$,
$texto$16/10/2026 a las 20:01: BANCO-COMPRAS-TEST · Cuenta de prueba de compras → BANCO-TRASPASO-TEST · Cuenta destino de prueba de traspasos, $300.00 MXN, referencia TRASPASO-TEST-001. Origen también tiene compra COM-00000001 por $406.00 y su reversa. BBVA_COBROS tiene un cobro independiente de $500.00. No confundir operaciones anteriores con el traspaso.$texto$,
1,DATE '2026-10-17','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo='REPORTE_FINANCIERO_CONSULTAR'
WHERE g.slug='reportes-traspaso-confirmados';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,v.ruta
FROM guia_proceso g JOIN (VALUES
(1,$texto$Consultar el día del traspaso en Tesorería$texto$,$texto$Reportes financieros → Tesorería$texto$,
$texto$1. Selecciona instituto Raices, Plantel Todos autorizados y todas las cuentas. 2. Captura Desde 16/10/2026 y Hasta 16/10/2026, agrupación Diaria; no restrinjas a Parian porque ambas cuentas del traspaso son institucionales. 3. Genera y localiza BBVA_COBROS con un movimiento, ingresos $500.00 y egresos $0.00. 4. Localiza BANCO-COMPRAS-TEST con tres movimientos, ingresos operativos $406.00, egresos operativos $406.00 y neto $0.00; su traspaso de salida es $300.00.$texto$,
$texto$Origen: traspasos entrada/salida $0.00/$300.00. Destino: un movimiento, operación $0.00 y traspasos $300.00/$0.00.$texto$,
$texto$Tres filas confirmadas; compra y reversa compensadas, traspasos separados de operación.$texto$,
$texto$No repetir la transferencia ni cambiar aperturas. La cuenta BBVA_COBROS es independiente del traspaso.$texto$,
$texto$/admin/reportes-financieros/tesoreria$texto$),
(2,$texto$Comparar Excel y PDF de Tesorería$texto$,$texto$Tesorería → Exportaciones$texto$,
$texto$1. Conserva los mismos filtros del 16/10/2026 y descarga Excel y PDF. 2. En Excel compara las tres filas con pantalla: movimientos 1/3/1, ingresos 500/406/0 y egresos 0/406/0. 3. Comprueba traspasos de salida 300 en origen y entrada 300 en destino; en PDF aparecen netos -$300.00 y $300.00. 4. Comprueba el resumen PDF: saldo inicial $4,650.00, ingresos operativos $906.00, egresos operativos $406.00 y saldo final $5,150.00.$texto$,
$texto$$4,650.00 + $906.00 - $406.00 = $5,150.00; traspasos consolidados -$300.00 + $300.00 = $0.00.$texto$,
$texto$Pantalla, Excel y PDF coinciden. El traspaso no infla ingresos ni egresos operativos.$texto$,
$texto$Saldo inicial de Tesorería incluye todas las cuentas autorizadas según filtros, no sólo las dos del ensayo. La comparación confirma contenido, no una aceptación separada del diseño del logo.$texto$,
$texto$/admin/reportes-financieros/tesoreria$texto$),
(3,$texto$Consultar el estado mensual del origen$texto$,$texto$Estado de cuenta por cuenta$texto$,
$texto$1. Abre Estado de cuenta por cuenta y selecciona BANCO-COMPRAS-TEST. 2. Selecciona corte Mensual, año 2026 y mes Octubre; este formulario no usa Desde/Hasta. 3. Genera y comprueba salida TRASPASO-TEST-001 de $300.00 a 20:01, reversa de compra con ingreso $406.00 a 18:23 y compra con egreso $406.00 a 17:58 del 16/10/2026. 4. Comprueba que se conservan las tres filas originales con clases TRASPASO, REVERSO y OPERACION, sin borrar la compra cancelada.$texto$,
$texto$Periodo 01/10/2026 al 31/10/2026; secuencias por cuenta #3/#2/#1, no IDs universales de movimientos.$texto$,
$texto$Salida del traspaso y antecedentes de compra/reversa confirmados en pantalla.$texto$,
$texto$La referencia TICKET-PRUEBA-001 corresponde a la compra y su reversa, no a TRASPASO-TEST-001.$texto$,
$texto$/admin/reportes-financieros/estado-cuenta-cuenta$texto$),
(4,$texto$Comparar PDF y ambas hojas Excel del origen$texto$,$texto$Estado de cuenta por cuenta → Exportaciones$texto$,
$texto$1. Conserva cuenta origen y corte Mensual 2026/Octubre; descarga PDF y Excel. 2. En PDF comprueba apertura $0.00, ingresos $406.00, egresos $706.00 y cierre $1,700.00, con saldo inicial registrado en el periodo $2,000.00 separado de ingresos. 3. En Excel revisa Resumen: tres movimientos y los mismos totales, moneda MXN. 4. En la hoja Estado de cuenta compara las tres filas, clases, importes y referencias con pantalla; folio de pago puede ser — porque no son pagos escolares.$texto$,
$texto$$0.00 + $2,000.00 + $406.00 - $706.00 = $1,700.00. La cuenta se abrió durante octubre; por eso apertura al inicio del mes es cero.$texto$,
$texto$Pantalla, PDF y ambas hojas Excel del origen confirmados, con apertura de cuenta diferenciada de ingresos.$texto$,
$texto$En estado de cuenta los egresos $706.00 sí incluyen compra 406 y traspaso 300; Tesorería separa traspasos de operación y no debe presentar esos706 como egreso operativo.$texto$,
$texto$/admin/reportes-financieros/estado-cuenta-cuenta$texto$),
(5,$texto$Comparar pantalla, PDF y Excel del destino$texto$,$texto$Estado de cuenta por cuenta → Destino$texto$,
$texto$1. Cambia únicamente la cuenta a BANCO-TRASPASO-TEST y conserva Mensual, año 2026, Octubre. 2. Genera y comprueba una fila del 16/10/2026 a 20:01: Transferencia desde Cuenta de prueba de compras, clase TRASPASO, referencia TRASPASO-TEST-001, ingreso $300.00 y sin egreso. 3. Descarga PDF y verifica apertura $0.00, ingresos $300.00, egresos $0.00 y cierre $300.00. 4. En Excel revisa Resumen con un movimiento y esos totales, saldo inicial registrado $0.00, y Estado de cuenta con la misma fila, secuencia1 y moneda MXN.$texto$,
$texto$Destino: $0.00 + $300.00 - $0.00 = $300.00; folio de pago — es válido para una transferencia interna.$texto$,
$texto$Pantalla, PDF, Resumen y detalle Excel del destino confirmados.$texto$,
$texto$No registrar otro ingreso en destino: duplicaría el dinero. Si existen movimientos posteriores, no atribuirlos al traspaso.$texto$,
$texto$/admin/reportes-financieros/estado-cuenta-cuenta$texto$),
(6,$texto$Cerrar la prueba sin revertir todavía$texto$,$texto$Guía de procesos y Movimientos financieros$texto$,
$texto$1. Conserva como confirmada la consulta de Tesorería y sus exportaciones del 16/10/2026. 2. Conserva como confirmados ambos estados mensuales de octubre, PDF y dos hojas Excel por cuenta, origen $1,700.00 y destino $300.00. 3. Mantén la reversa como siguiente ensayo independiente pendiente; esta guía no la ejecuta ni la declara aceptada. 4. Antes de una reversa acordada revisa saldo suficiente en destino y la transferencia exacta TRASPASO-TEST-001; después verifica las dos compensaciones y que se conserve el historial.$texto$,
$texto$Próximo ensayo propuesto: devolver300 del destino al origen mediante Revertir transferencia, no mediante dos operaciones manuales ni creando otra transferencia.$texto$,
$texto$Límite de aceptación documentado: reportes confirmados, reversa pendiente.$texto$,
$texto$No pulsar Confirmar reversa trazable sólo por leer la guía. Una reversa cambia saldos internos y no envía dinero al banco.$texto$,
$texto$/admin/movimientos-financieros$texto$)
) v(numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion,ruta) ON true
WHERE g.slug='reportes-traspaso-confirmados';

-- Actualiza el límite anterior sin reescribir la migración V106 ya aplicada.
UPDATE guia_proceso SET resumen=$texto$Transferencia TRASPASO-TEST-001 de $300.00 confirmada: origen $2,000.00→$1,700.00, destino $0.00→$300.00. También confirmados Tesorería y estados de cuenta con PDF/Excel; consultar la guía Comprobar un traspaso en Tesorería y estados de cuenta. Reversa pendiente.$texto$,
version_contenido=version_contenido+1,revisada_el=DATE '2026-10-17'
WHERE slug='transferencia-entre-cuentas-confirmada';
UPDATE guia_proceso_paso SET instrucciones=$texto$1. Conserva como aceptados modal, aplicación, saldos y dos movimientos TRASPASO. 2. Consulta la guía Comprobar un traspaso en Tesorería y estados de cuenta para la evidencia posterior confirmada en pantalla, PDF y Excel. 3. Distingue el corte diario de Tesorería del corte Mensual 2026/Octubre del estado por cuenta. 4. Mantén la reversa como ensayo independiente todavía pendiente; no repetir ni revertir al consultar estas guías.$texto$,
ejemplo=$texto$Reportes confirmados: origen $1,700.00 y destino $300.00; Tesorería separa traspasos de operación.$texto$,
resultado=$texto$Transferencia y reportes confirmados; reversa pendiente.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='transferencia-entre-cuentas-confirmada') AND numero=6;
