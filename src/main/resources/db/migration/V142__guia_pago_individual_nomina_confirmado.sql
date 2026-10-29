-- Sólo documentación confirmada; no calcula ni paga nómina.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('nomina-pago-individual-sin-borrador-confirmado','Nómina: pago individual sin borrador previo y bloqueo de duplicados','Personal',
'Consulta un empleado y periodo, revisa sueldo y conceptos proporcionales, paga desde una cuenta y comprueba que no pueda pagarse de nuevo la misma cobertura.',
'Administrador institucional identificado con permisos privados laborales, administración salarial y registro financiero. La guía requiere sus permisos de lectura y Guía de procesos; no concede accesos. Diana ya tiene pagado septiembre: no repetir la captura ni cancelar NPI-3. Para otro ensayo, elegir un periodo y cobertura disponibles; nunca cambiar fechas para eludir un bloqueo.',
'Confirmado por el usuario el 29/10/2026: Diana Prueba Puestos, SEP-2027-IND-TEST del 01/09/2027 al 15/09/2027. Sueldo $7,000.00 + transporte $40.00 + apoyo quincenal $150.00 - deducción $214.29 = $6,975.71. Se generaron BOR-000003 y NPI-3 sin preparar previamente un borrador. Egreso en BBVA_COBROS a las 08:44, saldo $23,330.00 a $16,354.29; CAJA conserva $12,762.85 y CAJA-CORTE-TEST $1,150.00. La segunda consulta para Diana y las mismas fechas fue bloqueada. No implica dispersión bancaria ni timbrado fiscal.',1,DATE '2026-10-29','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_LEER','EMPLEADO_NOMINA_LEER','CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER')
WHERE g.slug='nomina-pago-individual-sin-borrador-confirmado';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Comprobar empleado, periodo y saldos antes del ensayo','Personal y nómina → Empleados; Administración → Cuentas financieras',
'1. Inicia sesión con administrador institucional autorizado y consulta a Diana Prueba Puestos en Personal y nómina → Empleados. 2. En Ver expediente → Configuración de nómina consulta puesto de $14,000 mensuales, transporte $80 mensuales, apoyo $150 quincenales y deducción $100 semanales, con vigencias aplicables a septiembre. 3. En Cuentas financieras anota los saldos previos del ensayo: BBVA_COBROS $23,330.00, CAJA $12,762.85 y CAJA-CORTE-TEST $1,150.00. 4. Revisa si septiembre y su pago ya existen: este caso está concluido, por lo que debes consultar sus resultados en lugar de volver a pagar.',
'Julio conserva NPI-2 y no se modifica. El apoyo único de $250 estaba ligado a julio, no a septiembre.',
'Datos de referencia identificados sin modificar configuración ni movimientos.',
'No comparar el saldo actual con el inicial de esta guía si después hubo otros movimientos. No crear conceptos nuevos sólo para repetir el ejemplo.','/admin/empleados'),
(2,'Registrar o reutilizar el periodo sin crear borrador','Personal y nómina → Periodos de nómina → Nuevo periodo',
'1. En Periodos de nómina busca SEP-2027-IND-TEST y reutilízalo si existe. 2. Sólo para un ensayo nuevo autorizado, pulsa Nuevo periodo y captura ese código, Inicio del periodo 01/09/2027, Fin del periodo 15/09/2027 y Fecha prevista de pago 15/09/2027. 3. Pulsa Revisar periodo, comprueba fechas y prueba Cancelar del modal antes de revisar y confirmar cuando corresponda. 4. Comprueba el periodo registrado y no pulses Nuevo borrador: el objetivo es pagar directamente a un empleado.',
'Periodo independiente de la primera quincena; no requiere calendario.',
'Periodo identificado sin preparar nómina colectiva ni mover dinero.',
'SEP-2027-IND-TEST ya está pagado para Diana. Nunca duplicar el periodo o cambiar sus fechas para intentar otro pago de la misma cobertura.','/admin/periodos-nomina'),
(3,'Consultar directamente el cálculo de Diana','Personal y nómina → Nóminas → Pago individual',
'1. Entra a Nóminas y pulsa Pago individual, no Nuevo borrador. 2. En Periodo enfoca para consultar opciones o escribe SEP desde tres caracteres y selecciona SEP-2027-IND-TEST. 3. En Empleado escribe Dia y selecciona Diana Prueba Puestos desde las sugerencias. 4. Pulsa Consultar cálculo; en un ensayo todavía no pagado debe mostrar Total a pagar $6,975.71, mientras que en este caso ya concluido debe bloquear la duplicación.',
'Seleccionar por identificador evita buscar por una etiqueta concatenada. Los dos inputs están alineados y las sugerencias tienen altura variable.',
'Cálculo consultado sin crear borradores ni movimientos.',
'No cambiar de empleado o fechas sólo para ocultar una validación. Si se indica reserva en una nómina pendiente, abrir ese registro y continuar allí.','/admin/preparacion-nomina/pago-individual'),
(4,'Revisar todas las partidas calculadas','Pago individual → Consultar cálculo → ¿Cómo se calcula este pago?',
'1. En una consulta disponible revisa el nombre de Diana y la cobertura 01/09/2027 al 15/09/2027. 2. Comprueba sueldo base proporcional $7,000.00, transporte $40.00 y apoyo quincenal $150.00. 3. Comprueba Total de percepciones $7,190.00, deducción semanal proporcional $214.29 y Total a pagar $6,975.71. 4. Revisa códigos, tipos y frecuencias de cada partida; si cualquier importe difiere, detente antes de capturar el pago.',
'Los importes ya corresponden a los días y vigencias consultados. No sumes importes mensuales o semanales de referencia como si fueran pagos completos de la quincena.',
'Desglose y neto coincidentes con el cálculo del sistema.',
'No debe aparecer el apoyo único de julio por $250. El desglose no representa deuda adicional ni un nuevo movimiento financiero.','/admin/preparacion-nomina/pago-individual'),
(5,'Capturar una cuenta y justificar el pago','Pago individual → Cuentas de origen y Referencia y justificación',
'1. Sólo para un nuevo pago autorizado, selecciona BBVA_COBROS como Cuenta de origen e Importe de esta cuenta 6975.71. 2. Comprueba medio automático Transferencia y no agregues otra cuenta, porque esta prueba usa sólo el banco. 3. Selecciona el Motivo financiero autorizado de nómina y captura Referencia IND-DIANA-SEP2027-Q1. 4. Escribe Motivo de la operación Pago individual de prueba de Diana del 1 al 15 de septiembre de 2027, sin preparar previamente un borrador colectivo. Pago completo desde BBVA_COBROS.; pulsa Revisar distribución.',
'El importe de una sola fuente debe cubrir exactamente el neto; no se trata de un abono parcial de sueldo.',
'Captura lista para revisión, todavía sin registrar dinero.',
'No repetir este paso sobre NPI-3, que ya fue pagado. Si no hay fondos, revisar el disponible y corregir antes de confirmar.','/admin/preparacion-nomina/pago-individual'),
(6,'Probar edición y revisar sin alterar datos protegidos','Pago individual → Revisión de distribución',
'1. Comprueba un empleado, cuenta BBVA_COBROS, importe $6,975.71, saldo previo $23,330.00 y saldo posterior $16,354.29. 2. Verifica que cuentas, importes y justificación se presenten en sólo lectura. 3. Pulsa Editar distribución y comprueba que conserva periodo, empleado, fuente, importe, referencia y motivo. 4. Sin cambiar las cantidades del ejemplo, pulsa Revisar distribución nuevamente para obtener una revisión válida.',
'Editar invalida la revisión anterior pero no elimina la captura. No se necesita Atrás del navegador.',
'Datos revisados y preservados, sin generar pago.',
'Si cambió la configuración o el saldo, el servidor exige consultar o revisar otra vez antes de confirmar; no se permite pagar silenciosamente con datos distintos.','/admin/preparacion-nomina/pago-individual'),
(7,'Confirmar el pago individual y entender el registro generado','Pago individual → Confirmar y registrar pagos',
'1. Pulsa Confirmar y registrar pagos y comprueba en el modal el total y la cuenta. 2. Para un ensayo nuevo prueba Cancelar dentro del modal y verifica que no cambien saldos ni aparezca un pago nuevo. 3. Revisa de nuevo y confirma una sola vez cuando corresponda registrar el pago real verificado. 4. En Nóminas localiza el registro generado automáticamente para ese empleado y periodo; abre Ver → Autorización y pagos por empleado → Pagos por empleado → Ver pago e intentos → Historial de intentos.',
'En el caso confirmado se generaron BOR-000003 y NPI-3. Los folios son automáticos y serán distintos en otro ensayo.',
'El sistema guarda y paga la fotografía histórica en la misma transacción, sin exigir borrador previo al usuario.',
'Que aparezca una nómina después de pagar es normal. No significa que debas volver a autorizar ni pagar el mismo importe.','/admin/preparacion-nomina'),
(8,'Comprobar egreso y cuentas sin afectar las cajas','Administración → Movimientos financieros y Cuentas financieras',
'1. En Movimientos financieros localiza el EGRESO NOMINA de BOR-000003, referencia NPI-3, del 29/10/2026 a las 08:44. 2. Comprueba importe $6,975.71, saldo anterior $23,330.00 y posterior $16,354.29 en BBVA_COBROS. 3. En Cuentas financieras verifica banco $16,354.29, CAJA $12,762.85 y CAJA-CORTE-TEST $1,150.00, considerando cualquier movimiento posterior. 4. Comprueba que julio y NPI-2 conservan su historial y no se realizó otro egreso desde caja.',
'Una sola fuente: un egreso por $6,975.71. Las dos cajas permanecieron sin cambios.',
'Pago individual y afectación financiera confirmados por el usuario.',
'Los saldos son evidencia del ensayo, no valores fijos del sistema. No ajustar cuentas manualmente para hacerlas coincidir con esta guía.','/admin/movimientos-financieros'),
(9,'Verificar que la misma cobertura no pueda pagarse dos veces','Personal y nómina → Nóminas → Pago individual',
'1. Regresa a Nóminas → Pago individual y selecciona el mismo periodo SEP-2027-IND-TEST y a Diana. 2. Pulsa Consultar cálculo y comprueba que el sistema bloquee un nuevo pago para las mismas fechas. 3. El aviso debe explicar que ya tiene un pago registrado y orientar a consultar su nómina; si sólo existe una reserva pendiente, debe explicarlo de forma distinta. 4. Verifica que no aparezca otro egreso y el banco conserve $16,354.29 si no hubo más movimientos; conserva NPI-3 como pago vigente y no repitas la operación.',
'Usuario confirmó el bloqueo original con el aviso Ya incluido en un borrador vigente para fechas coincidentes.; se aclara ahora según exista pago vigente o sólo reserva.',
'Pago individual sin borrador y bloqueo de duplicación confirmados; un único pago de esa cobertura.',
'La consulta bloqueada no altera saldos. No crear otro periodo superpuesto para eludirla. No aplicar/Posponer y otros escenarios siguen pendientes de ensayo independiente.','/admin/preparacion-nomina/pago-individual')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='nomina-pago-individual-sin-borrador-confirmado';
