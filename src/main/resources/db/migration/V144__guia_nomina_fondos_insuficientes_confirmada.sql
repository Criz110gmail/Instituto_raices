-- Contenido editorial confirmado; no reproduce pagos ni modifica cuentas.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('nomina-fondos-insuficientes-confirmados','Nómina: fondos insuficientes, cancelar revisión y pagar desde dos cuentas','Personal',
'Comprueba que una fuente insuficiente bloquee la revisión sin descontar de ninguna cuenta; corrige la distribución, cancela el modal sin pagar y confirma después una sola vez.',
'Administrador institucional identificado con permisos privados de nómina y registro financiero. La guía no concede permisos. Ensayo concluido: consultar BOR-000006 y NPI-7 sin volver a pagar. Antes de otro ensayo autorizado, verificar vigencias, coberturas disponibles y saldos reales. Los importes históricos de esta guía no deben imponerse mediante ajustes a las cuentas.',
'Confirmado por el usuario el 29/10/2026: Bruce Wayne, DIC-2026-FONDOS-TEST del 01/12/2026 al 15/12/2026. Sueldo mensual $12,000.00, proporcional $5,806.45. Primera distribución CAJA $806.45 y BBVA_COBROS $5,000.00 bloqueada: banco disponible $4,274.29, faltante $725.71; todas las cuentas intactas. Corrección CAJA $1,806.45 y banco $4,000.00, Cancelar del modal sin movimientos y posterior confirmación real. BOR-000006/NPI-7 pagado; saldos finales CAJA $10,956.40, banco $274.29 y caja de corte $1,150.00. No implica dispersión bancaria ni timbrado fiscal.',1,DATE '2026-10-29','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_LEER','EMPLEADO_NOMINA_LEER','CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER')
WHERE g.slug='nomina-fondos-insuficientes-confirmados';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Comprobar sueldo, vigencias y saldos previos','Personal y nómina → Empleados; Administración → Finanzas → Cuentas financieras',
'1. Busca Bruce Wayne en Personal y nómina → Empleados y abre Ver expediente → Datos laborales. 2. Consulta sueldo mensual histórico de $12,000.00 vigente del 01/11/2026 al 31/12/2026 y Configuración de nómina: transporte sólo en noviembre y bono desde enero. 3. En Administración → Finanzas → Cuentas financieras anota los saldos previos del ensayo: banco $4,274.29, CAJA $12,762.85 y caja de corte $1,150.00. 4. Como el caso ya terminó, consulta su historial para esos saldos previos; los actuales después del pago son distintos.',
'Bruce recibe sueldo de $12,000 por mes completo. El 01/12 al 15/12 equivale a 15 de los31 días de diciembre: $5,806.45; no se reduce su sueldo mensual.',
'Cálculo y referencias históricas identificados sin cambios.',
'No usar el saldo inicial de la cuenta como disponible actual. No modificar el sueldo ni las cuentas para hacer coincidir un ejemplo.','/admin/cuentas-financieras'),
(2,'Identificar el periodo ya utilizado','Personal y nómina → Periodos de nómina',
'1. Busca DIC-2026-FONDOS-TEST y consulta su registro existente. 2. Comprueba Inicio del periodo 01/12/2026 y Fin del periodo 15/12/2026. 3. Comprueba Fecha prevista de pago 15/12/2026, sin calendario. 4. En un nuevo ensayo autorizado se usaría Nuevo periodo → Revisar periodo → confirmar modal; para este caso no crees otro periodo ni cambies sus fechas.',
'Periodo independiente de la primera quincena de diciembre, ya pagada para Bruce.',
'Cobertura identificada sin duplicar periodos ni pagos.',
'Las fechas de registro del movimiento son diferentes de las fechas que cubre el sueldo.','/admin/periodos-nomina'),
(3,'Preparar y seleccionar expresamente al empleado','Personal y nómina → Nóminas → Nuevo borrador',
'1. Para un ensayo nuevo autorizado selecciona el periodo y Opciones de selección → Elegir empleados específicos → Preparar nómina del periodo. 2. En Detalle por empleado busca Bruce, selecciona su sugerencia y pulsa Aplicar filtros; después pulsa Incluir en su fila. 3. Comprueba Incluido, un empleado, sueldo/neto $5,806.45, percepciones adicionales cero y deducciones cero. 4. Guarda con nombre Diciembre — fondos insuficientes de Bruce y motivo Comprobar que una fuente sin fondos bloquee todo el pago sin afectar ninguna cuenta; revisa y confirma el modal. En este caso consulta BOR-000006, ya existente.',
'Buscar sólo filtra: no incluye. Guardar sin incluidos muestra un aviso con Ir a seleccionar empleados; con incluidos abre la confirmación.',
'Borrador guardado sin pagos ni egresos.',
'BOR-000006 ya está pagado. No recrear ni cancelar la nómina para repetir esta guía.','/admin/preparacion-nomina'),
(4,'Capturar dos fuentes con el banco insuficiente','Nóminas → Ver borrador → Autorización y pagos por empleado → Resumen → Preparar pago colectivo',
'1. Verifica Total a pagar $5,806.45 y selecciona CAJA en Fuente1 con importe806.45. 2. Pulsa Agregar otra cuenta, que lleva a la fuente nueva, y selecciona BBVA_COBROS con importe5000.00; sólo son dos fuentes. 3. Selecciona Motivo financiero autorizado de egreso para nómina y referencia DIC-BRUCE-SIN-FONDOS-TEST. 4. En Motivo de la operación explica que caja tiene fondos y banco no, y que no debe afectarse ninguna cuenta si falla una fuente; pulsa Revisar distribución.',
'$806.45+$5,000.00=$5,806.45: la suma es correcta. Efectivo/Transferencia se asignan automáticamente según cuenta.',
'Captura completa para validar fondos, todavía sin pagar.',
'No agregar una tercera fuente. Este es un recorrido histórico ya concluido, no una instrucción para pagar nuevamente.','/admin/preparacion-nomina'),
(5,'Leer el bloqueo y distinguir un error de captura','Preparar pago colectivo → Revisar distribución',
'1. Comprueba el aviso de BBVA_COBROS: disponible $4,274.29, solicitado $5,000.00 y faltan $725.71. 2. Verifica que termine No se movió dinero y que no abra una revisión válida para confirmar. 3. Si el solicitado aparece como $5,806.45, revisa las dos fuentes: se capturó el total completo en banco y no la distribución del ejemplo. 4. Con fuente CAJA806.45 y banco5000, conserva el mensaje correcto como evidencia y no inventes ingresos para superar la validación.',
'El usuario corrigió un primer intento mal capturado y confirmó el faltante correcto de $725.71.',
'Banco insuficiente bloquea la revisión de toda la operación.',
'Este caso verifica la validación previa. No demuestra por sí solo una carrera de saldos durante la confirmación, que requiere otro ensayo.','/admin/preparacion-nomina'),
(6,'Comprobar que tampoco se descontó de caja','Administración → Finanzas → Cuentas financieras y Movimientos financieros',
'1. En otra pestaña abre Cuentas financieras después del aviso y comprueba BBVA_COBROS $4,274.29. 2. Comprueba CAJA $12,762.85 aunque era la primera fuente válida; caja de corte conserva $1,150.00. 3. En Movimientos financieros verifica que el intento bloqueado no creó egresos para ese borrador. 4. En Nóminas comprueba que todavía era Borrador y no tenía pago registrado; el usuario confirmó que ninguna cuenta cambió en ese momento.',
'No se registra una salida parcial de caja antes de descubrir que el banco no alcanza.',
'Bloqueo previo confirmado sin movimientos en ninguna fuente.',
'Después del pago definitivo ya no verás estos saldos actuales: consulta el historial para reconstruir este momento.','/admin/movimientos-financieros'),
(7,'Corregir las dos fuentes y revisar sin volver a capturar','Mismo formulario → Cuentas de origen → Revisar distribución',
'1. Regresa al mismo formulario del borrador sin crear otro registro ni fuente. 2. Cambia CAJA a1806.45 y BBVA_COBROS a4000.00, conservando referencia y justificación. 3. Pulsa Revisar distribución: total $5,806.45, caja anterior $12,762.85 y posterior estimada $10,956.40, banco anterior $4,274.29 y posterior estimado $274.29. 4. Comprueba las tarjetas protegidas; si hay un dato incorrecto usa Editar distribución y después revisa nuevamente.',
'Dos fuentes suficientes, suma exacta. La revisión sólo muestra una propuesta de salida.',
'Distribución válida revisada, todavía sin dinero registrado.',
'Los saldos posteriores son estimados hasta confirmar. No utilizar Atrás del navegador para cambiar la revisión.','/admin/preparacion-nomina'),
(8,'Cancelar el modal y confirmar después una sola vez','Revisión → Confirmar y registrar pagos',
'1. Pulsa Confirmar y registrar pagos y verifica total $5,806.45, CAJA $1,806.45 y banco $4,000.00. 2. Pulsa Cancelar dentro del modal y comprueba que lo cierre sin pago: las cuentas permanecen en $12,762.85 y $4,274.29. 3. Cuando corresponda registrar el pago real, abre nuevamente el modal y confirma una sola vez; el usuario aclaró expresamente que hizo primero Cancelar y después Confirmar. 4. Consulta la nómina BOR-000006 y su intento NPI-7; no repitas esta confirmación porque ya está pagado.',
'Cancelar del modal no cancela la nómina ni ejecuta una salida. La confirmación posterior sí registra el pago.',
'Cancelación segura del modal y pago final comprobados por separado.',
'Si se pulsa Cancelar y cambian saldos, detener el ensayo y reportarlo; no intentar compensar manualmente.','/admin/preparacion-nomina'),
(9,'Comprobar salidas exactas y cierre de la prueba','Nóminas → Ver → Autorización y pagos por empleado; Administración → Finanzas',
'1. En BOR-000006 → Autorización y pagos por empleado → Pagos por empleado → Bruce → Ver pago e intentos → Historial de intentos comprueba NPI-7 pagado por $5,806.45. 2. En Movimientos financieros comprueba dos egresos del pago: CAJA $1,806.45 y BBVA_COBROS $4,000.00, sin egresos del intento fallido ni de Cancelar. 3. En Cuentas financieras verifica caja $10,956.40, banco $274.29 y caja de corte $1,150.00; considera movimientos posteriores. 4. Conserva evidencia y no vuelvas a pagar ni cancelar este periodo; la siguiente prueba de duplicación desde dos pestañas debe usar otra cobertura disponible.',
'El usuario confirmó las cantidades finales y la secuencia Cancelar sin movimientos → Confirmar con salida exacta.',
'Fondos insuficientes, cancelación del modal y pago multifuente confirmados.',
'La prueba de dos pestañas sigue pendiente. No marcar como validada la concurrencia por este resultado.','/admin/preparacion-nomina')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='nomina-fondos-insuficientes-confirmados';

UPDATE guia_proceso_paso SET precaucion='Fondos insuficientes se documentan en Nómina: fondos insuficientes, cancelar revisión y pagar desde dos cuentas, con ensayo independiente confirmado. Concurrencia sigue pendiente; no repetir pagos de Bruce.'
WHERE numero=15 AND guia_id=(SELECT id FROM guia_proceso WHERE slug='nomina-decisiones-y-pospuestos-confirmados');
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-29'
WHERE slug='nomina-decisiones-y-pospuestos-confirmados';
