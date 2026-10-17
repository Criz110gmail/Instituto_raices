-- Aceptación del propietario: sólo contenido de guía, sin operaciones financieras.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES ('transferencia-entre-cuentas-confirmada',
$texto$Transferir entre cuentas: salida y entrada por $300$texto$,'Tesorería',
$texto$Propietario confirmó el modal y la transferencia interna: origen de $2,000.00 a $1,700.00 y destino de $0.00 a $300.00, con egreso e ingreso TRASPASO y referencia TRASPASO-TEST-001. No incluye aceptación de reversa ni reportes de este traspaso.$texto$,
$texto$Administrador persistido con Cuentas financieras, Movimientos financieros y Guía de procesos, alcance institucional compatible. Dos cuentas activas, misma institución y moneda. La fecha no puede ser futura ni anterior a las aperturas y el origen debe tener saldo suficiente. Git no transporta estas cuentas ni movimientos. Reutilizar los registros del ensayo: consultar no exige transferir de nuevo.$texto$,
$texto$Caso confirmado el 16/10/2026 a las 20:01 de la institución: instituto Raices; origen institucional BANCO-COMPRAS-TEST · Cuenta de prueba de compras (cuenta4), saldo previo $2,000.00; destino institucional BANCO-TRASPASO-TEST · Cuenta destino de prueba de traspasos (cuenta5), apertura $0.00. Importe $300.00 MXN y referencia TRASPASO-TEST-001. El enlace de Revertir transferencia identifica transferencia1. Los números #3 y #1 junto a fecha son secuencias por cuenta: no se proporcionaron IDs de los dos movimientos y no deben inventarse.$texto$,
1,DATE '2026-10-16','CONFIRMADA');

-- Restringe la lectura de la guía sin conceder permisos ni ampliar roles.
INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p
ON p.codigo=ANY(ARRAY['CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER','TRANSFERENCIA_CUENTA_REGISTRAR'])
WHERE g.slug='transferencia-entre-cuentas-confirmada';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,v.ruta
FROM guia_proceso g JOIN (VALUES
(1,$texto$Identificar las cuentas y el saldo previo$texto$,$texto$Cuentas financieras$texto$,
$texto$1. Localiza BANCO-COMPRAS-TEST · Cuenta de prueba de compras y comprueba que es institucional, bancaria, MXN y activa. 2. Localiza BANCO-TRASPASO-TEST · Cuenta destino de prueba de traspasos, también institucional, bancaria, MXN y activa. 3. Distingue saldo inicial de saldo actual: antes del ensayo el origen tenía $2,000.00 y el destino $0.00. 4. Reutiliza ambas cuentas; en esta instalación el traspaso ya se aplicó y sus saldos actuales aceptados son $1,700.00 y $300.00.$texto$,
$texto$Cuenta origen4 y destino5, misma institución. Aperturas 16/10/2026. Los identificadores pertenecen al ensayo, no son universales.$texto$,
$texto$Cuentas y referencia de saldos identificadas sin modificar la apertura ni crear duplicados.$texto$,
$texto$No tocar BBVA_COBROS, cajas ni compras para reproducir el caso. Si hay otros movimientos posteriores, los saldos actuales pueden variar; consulta los vinculados al traspaso.$texto$,
$texto$/admin/catalogos/cuentas-financieras$texto$),
(2,$texto$Revisar los datos de captura del ejemplo$texto$,$texto$Movimientos financieros → Transferir entre cuentas$texto$,
$texto$1. Desde Movimientos financieros abre Transferir entre cuentas sólo para una nueva prueba expresamente autorizada, no para repetir la ya aceptada. 2. Selecciona instituto Raices y las cuentas origen BANCO-COMPRAS-TEST y destino BANCO-TRASPASO-TEST mediante sus buscadores. 3. Captura monto $300.00 y referencia TRASPASO-TEST-001 únicamente al reproducir el ejemplo en datos aislados; utiliza fecha y hora reales, no futuras ni anteriores a la apertura. 4. Revisa las observaciones y distingue origen de destino antes de pulsar Aplicar transferencia.$texto$,
$texto$Origen con $2,000.00; destino con $0.00; importe $300.00. El registro aceptado se muestra el 16/10/2026 a las 20:01.$texto$,
$texto$Datos preparados para revisar, sin transferir hasta aceptar la confirmación.$texto$,
$texto$Abrir el formulario no es necesario para consultar una transferencia aplicada. No modificar fechas de cuentas para forzar validaciones ni repetir la referencia como si evitara duplicados.$texto$,
$texto$/admin/movimientos-financieros$texto$),
(3,$texto$Revisar el modal antes de confirmar$texto$,$texto$Transferir entre cuentas → Confirmación$texto$,
$texto$1. Al pulsar Aplicar transferencia revisa el modal con institución, cuenta de origen, cuenta de destino, importe, fecha, referencia y observaciones. 2. Si algo no corresponde, pulsa Cancelar para volver a la captura sin enviar; Escape y fondo también cierran el resumen. 3. Al reabrir comprueba los datos vigentes, no valores anteriores a una corrección. 4. Sólo si corresponde registrar el traspaso pulsa Confirmar y aplicar transferencia una vez; el propietario ya confirmó este modal y la operación del ejemplo.$texto$,
$texto$Resumen de $300.00 desde BANCO-COMPRAS-TEST hacia BANCO-TRASPASO-TEST; referencia TRASPASO-TEST-001.$texto$,
$texto$Confirmación y traspaso aceptados por el propietario. Cancelar/Escape y conservación de captura están verificados técnicamente, sin aceptación humana separada de esas acciones.$texto$,
$texto$El registro sólo mueve saldos internos del sistema: no envía dinero a un banco. Confirmar conserva CSRF e idempotencia; no sustituye las validaciones del servidor.$texto$,
$texto$/admin/movimientos-financieros$texto$),
(4,$texto$Comprobar los dos saldos posteriores$texto$,$texto$Cuentas financieras$texto$,
$texto$1. Regresa a Cuentas financieras y consulta BANCO-COMPRAS-TEST. 2. Comprueba saldo inicial $2,000.00 y saldo actual $1,700.00 en el momento del ensayo. 3. Consulta BANCO-TRASPASO-TEST y comprueba saldo inicial $0.00 y saldo actual $300.00. 4. Verifica que el total combinado continúa en $2,000.00, siempre que no haya otras operaciones entre las comprobaciones.$texto$,
$texto$Origen: $2,000.00 - $300.00 = $1,700.00. Destino: $0.00 + $300.00 = $300.00. Total $2,000.00.$texto$,
$texto$Ambos saldos posteriores confirmados por el propietario sin editar las aperturas.$texto$,
$texto$No confundir importe transferido con saldo inicial ni registrar otro ingreso manual en el destino: duplicaría la entrada.$texto$,
$texto$/admin/catalogos/cuentas-financieras$texto$),
(5,$texto$Comprobar salida y entrada en el libro de movimientos$texto$,$texto$Movimientos financieros$texto$,
$texto$1. Consulta la fecha 16/10/2026 y referencia TRASPASO-TEST-001 con alcance institucional. 2. En BANCO-COMPRAS-TEST identifica EGRESO TRASPASO de $300.00, antes $2,000.00 y después $1,700.00, con concepto Transferencia a Cuenta destino de prueba de traspasos. 3. En BANCO-TRASPASO-TEST identifica INGRESO TRASPASO de $300.00, antes $0.00 y después $300.00, con concepto Transferencia desde Cuenta de prueba de compras. 4. Comprueba que ambos muestran 16/10/2026 a las 20:01, la misma referencia y enlace de Revertir transferencia correspondiente a la transferencia1, sin activarlo.$texto$,
$texto$Salida y entrada institucionales relacionadas; el enlace observado termina /admin/reversiones/transferencias/1/nueva.$texto$,
$texto$Dos movimientos TRASPASO confirmados y rastreables, conservando el historial de ambas cuentas.$texto$,
$texto$#3 y #1 junto a fecha no son los IDs confirmados de los movimientos. No pulsar Revertir transferencia durante esta consulta ni intentar revertir cada movimiento por separado.$texto$,
$texto$/admin/movimientos-financieros$texto$),
(6,$texto$Delimitar lo aceptado y la siguiente comprobación$texto$,$texto$Transferencias y Reportes financieros$texto$,
$texto$1. Conserva como resultado aceptado modal de confirmación, saldos $1,700.00/$300.00 y dos movimientos TRASPASO de $300.00. 2. Mantén como pendientes la consulta de este traspaso en Tesorería, estado de cuenta y exportaciones; una prueba de compra o pago anterior no los valida. 3. En una siguiente prueba acordada revisa que los reportes separen traspasos de ingresos y egresos operativos, con neto consolidado del traspaso igual a cero. 4. Si después se acuerda una reversa, trátala como un ensayo independiente y verifica ambos saldos y compensaciones antes de documentarla.$texto$,
$texto$Este procedimiento confirma transferencia interna aplicada. No confirma reversa, reportes, exportaciones, concurrencia ni casos de saldo insuficiente.$texto$,
$texto$Límite de aceptación explícito sin atribuir resultados todavía no probados.$texto$,
$texto$No ejecutar otra transferencia ni una reversa sólo por leer la guía. La migración añade documentación: no crea cuentas, transferencias ni movimientos.$texto$,
$texto$/admin/movimientos-financieros$texto$)
) v(numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion,ruta) ON true
WHERE g.slug='transferencia-entre-cuentas-confirmada';
