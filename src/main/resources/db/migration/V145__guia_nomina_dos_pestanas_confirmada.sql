-- Sólo documentación confirmada; no repite pagos ni altera cuentas o permisos de roles.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('nomina-dos-pestanas-confirmada','Nómina: impedir un segundo pago desde otra pestaña','Personal',
'Prepara dos revisiones del mismo borrador antes de pagar, confirma la primera y verifica que la segunda revisión obsoleta no genere otro egreso.',
'Administrador institucional identificado con permisos privados de nómina y registro financiero. Las dos pestañas usan la misma sesión y el mismo borrador, no dos nóminas diferentes. Caso concluido: consultar BOR-000007/NPI-8 sin repetir ni cancelar su pago. Los permisos de esta guía son de lectura y no conceden facultades a roles.',
'Confirmado por el usuario el 29/10/2026: Bruce Wayne, DIC-2026-DOS-PESTANAS-TEST del 16/12/2026 al 31/12/2026, BOR-000007. Sueldo proporcional $6,193.55 desde CAJA. Pestaña A registró NPI-8, referencia DIC-BRUCE-DOBLE-A, a las 15:21. Pestaña B mostró La nómina cambió. Recarga antes de revisar; no se movió dinero. Sólo un egreso de $6,193.55 y un pago vigente. CAJA $10,956.40 a $4,762.85, banco $274.29 y caja de corte $1,150.00 intactos. Es una segunda confirmación secuencial desde una revisión obsoleta, no una prueba de transacciones simultáneas.',1,DATE '2026-10-29','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_LEER','EMPLEADO_NOMINA_LEER','CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER')
WHERE g.slug='nomina-dos-pestanas-confirmada';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Identificar empleado, cálculo y saldo antes del ensayo','Personal y nómina → Empleados; Administración → Finanzas → Cuentas financieras',
'1. Busca Bruce Wayne en Empleados y consulta su sueldo histórico de $12,000 mensuales aplicable hasta el 31/12/2026. 2. Comprueba que transporte terminó en noviembre y bono empieza en enero, por lo que no corresponden al periodo del 16/12/2026 al 31/12/2026. 3. En Cuentas financieras identifica los saldos previos históricos: CAJA $10,956.40, BBVA_COBROS $274.29 y caja de corte $1,150.00. 4. Para este ensayo ya concluido consulta su historial; no ajustes las cuentas ni reproduzcas el pago para volver a obtener esos saldos.',
'Primera quincena $5,806.45 más segunda $6,193.55 suman sueldo mensual completo $12,000.00.',
'Empleado, cobertura y referencia económica identificados.',
'Las cifras previas ya cambiaron por el pago. No confundir el saldo inicial de la cuenta con el disponible actual.','/admin/cuentas-financieras'),
(2,'Identificar el periodo de la segunda parte de diciembre','Personal y nómina → Periodos de nómina',
'1. Busca DIC-2026-DOS-PESTANAS-TEST y reutiliza el registro existente. 2. Comprueba Inicio del periodo 16/12/2026, Fin del periodo 31/12/2026 y Fecha prevista de pago 31/12/2026. 3. Para un nuevo ensayo autorizado se usaría Nuevo periodo → Revisar periodo → confirmar modal, sin calendario. 4. No modifiques DIC-2026-FONDOS-TEST ni su pago de la primera quincena: son otro caso ya concluido.',
'La cobertura de este caso ya está pagada para Bruce y no debe duplicarse.',
'Periodo independiente identificado sin alterar otros pagos.',
'No cambiar fechas o código para eludir un bloqueo por cobertura ya utilizada.','/admin/periodos-nomina'),
(3,'Guardar un solo borrador con el empleado incluido','Personal y nómina → Nóminas → Nuevo borrador',
'1. Para un ensayo nuevo autorizado selecciona el periodo, Opciones de selección → Elegir empleados específicos → Preparar nómina del periodo. 2. En Detalle por empleado busca Bruce, aplica filtros y pulsa Incluir en su fila; buscar no equivale a incluir. 3. Comprueba un empleado y neto $6,193.55, sin conceptos adicionales ni deducciones. 4. Guarda como Diciembre — dos pestañas de Bruce con motivo Comprobar que dos confirmaciones del mismo borrador sólo registren un pago, revisando y confirmando el modal; en este caso consulta BOR-000007, que ya existe.',
'Se crea un solo borrador para ambas pestañas. Los folios son automáticos y pueden ser distintos en otra base.',
'Fotografía del cálculo conservada sin pago hasta confirmar la distribución.',
'No crear un borrador diferente por pestaña. No repetir BOR-000007 ya pagado.','/admin/preparacion-nomina'),
(4,'Preparar la revisión A sin confirmar el pago','Nóminas → Ver borrador → Autorización y pagos por empleado → Resumen',
'1. Abre Ver del mismo borrador en dos pestañas del navegador con la misma sesión administrativa, A y B. 2. En A pulsa Preparar pago colectivo y selecciona sólo CAJA con importe6193.55, medio Efectivo automático y motivo financiero de egreso para nómina. 3. Captura referencia DIC-BRUCE-DOBLE-A y motivo Primera confirmación de prueba del mismo borrador desde dos pestañas. 4. Pulsa Revisar distribución y comprueba total $6,193.55, saldo anterior $10,956.40 y posterior estimado $4,762.85; todavía no confirmes.',
'Revisión A lista. No ha salido dinero.',
'Primera captura preparada para comparar con otra revisión del mismo estado.',
'Abrir la segunda pestaña después de pagar no reproduce esta prueba: ambas revisiones deben existir antes de la primera confirmación.','/admin/preparacion-nomina'),
(5,'Preparar B antes de pagar en A','Mismo borrador en pestaña B → Autorización y pagos por empleado → Resumen → Preparar pago colectivo',
'1. En B conserva el mismo borrador, empleado y periodo; no uses Pago individual independiente. 2. Selecciona CAJA e importe6193.55, motivo financiero de nómina y referencia DIC-BRUCE-DOBLE-B. 3. Escribe el motivo Segunda confirmación de prueba de una revisión que quedará obsoleta y pulsa Revisar distribución. 4. Comprueba los mismos importes y saldo posterior estimado $4,762.85; deja B en revisión y regresa a A sin pagar todavía desde B.',
'Ambas revisiones se basan en la misma nómina y saldo previo, con referencias diferentes.',
'Segunda revisión preparada antes de la primera salida.',
'No guardar otra nómina ni añadir otra cuenta. El objetivo es confirmar dos veces el mismo pago, no pagar dos obligaciones distintas.','/admin/preparacion-nomina'),
(6,'Confirmar A una sola vez','Pestaña A → Revisión → Confirmar y registrar pagos',
'1. En A pulsa Confirmar y registrar pagos y revisa el modal: Bruce, CAJA y $6,193.55. 2. Confirma una sola vez cuando corresponda registrar el pago real. 3. Comprueba que la nómina quede pagada y la cuenta CAJA disminuya a $4,762.85; banco y caja de corte permanecen iguales. 4. Conserva B sin recargar ni volver a revisar para que su captura siga representando el estado anterior; en este caso concluido sólo consulta NPI-8.',
'Pago vigente confirmado NPI-8, referencia DIC-BRUCE-DOBLE-A, 29/10/2026 a las 15:21.',
'Una sola salida de $6,193.55 registrada.',
'No confirmar otra vez A ni crear un pago nuevo. Una revisión no implica que siga siendo válida después de cambiar la nómina.','/admin/preparacion-nomina'),
(7,'Comprobar el rechazo de la revisión obsoleta B','Pestaña B sin recargar → Confirmar y registrar pagos',
'1. Regresa a B sin recargar, editar ni volver a revisar la distribución. 2. Pulsa Confirmar y registrar pagos y confirma el modal para intentar usar la revisión anterior. 3. Comprueba el aviso La nómina cambió. Recarga antes de revisar; no se movió dinero. 4. Verifica que no exista otro pago ni egreso y conserva el mensaje; no corrijas saldos manualmente ni intentes eludir el bloqueo.',
'El texto puede variar en otros estados válidos de rechazo, pero nunca debe producir otra salida para esa misma obligación.',
'Segunda confirmación bloqueada por estado o versión vigente del servidor.',
'Una pantalla de error inesperada debe reportarse. Esta prueba secuencial no equivale a confirmar simultáneamente desde dos transacciones.','/admin/preparacion-nomina'),
(8,'Comprobar las tres cuentas y un solo movimiento','Administración → Finanzas → Cuentas financieras y Movimientos financieros',
'1. En Cuentas financieras confirma CAJA $4,762.85, considerando movimientos posteriores. 2. Comprueba que BBVA_COBROS conserva $274.29 y caja de corte $1,150.00. 3. En Movimientos financieros localiza exactamente un EGRESO NOMINA por $6,193.55 de BOR-000007, referencia NPI-8. 4. Comprueba saldo anterior $10,956.40 y posterior $4,762.85; no debe existir una segunda salida de la pestaña B.',
'El usuario compartió el movimiento del 29/10/2026 a las 15:21 y los tres saldos coincidentes.',
'Único egreso y conservación del dinero de las otras cuentas confirmados.',
'No contar egresos de otros borradores como duplicados. Filtra y verifica la nómina y el pago relacionados.','/admin/movimientos-financieros'),
(9,'Revisar el intento vigente y cerrar sin repetir','Nóminas → Ver BOR-000007 → Autorización y pagos por empleado → Pagos por empleado',
'1. Busca y selecciona Bruce en Buscar empleado, aplica los filtros y abre Ver pago e intentos. 2. En Historial de intentos comprueba sólo NPI-8 Pagado por $6,193.55, referencia DIC-BRUCE-DOBLE-A. 3. Comprueba que su fuente es CAJA y que el historial conserva el egreso registrado, sin un segundo pago vigente por B. 4. Conserva la evidencia y no vuelvas a pagar ni cancelar este caso; los siguientes ensayos deben usar otros registros y coberturas disponibles.',
'Confirmación humana completa: rechazo B, una salida, un pago vigente y saldos correctos.',
'Prueba de dos pestañas documentada como confirmada.',
'Posponer todo el pago de un empleado y carreras de transacciones simultáneas requieren sus propios ensayos.','/admin/preparacion-nomina')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='nomina-dos-pestanas-confirmada';

UPDATE guia_proceso_paso SET precaucion='La segunda confirmación desde una pestaña obsoleta se documenta en Nómina: impedir un segundo pago desde otra pestaña, con ensayo confirmado de Bruce. No repetir pagos. Esto no confirma carreras de transacciones simultáneas.'
WHERE numero=9 AND guia_id=(SELECT id FROM guia_proceso WHERE slug='nomina-fondos-insuficientes-confirmados');
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-29'
WHERE slug='nomina-fondos-insuficientes-confirmados';
