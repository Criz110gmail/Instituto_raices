-- Sólo guía de casos confirmados. No crea ni modifica nóminas, pagos o cuentas.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('nomina-decisiones-y-pospuestos-confirmados','Nómina: no aplicar, posponer y pagar conceptos por separado','Personal',
'Excluye un concepto sólo del periodo, restablécelo, pospón su entrega y conserva la trazabilidad al pagar, cancelar y volver a pagar el pendiente.',
'Administrador institucional identificado con permisos privados laborales, configuración salarial, registro financiero y cancelación. La guía no concede permisos. Casos concluidos: consultar su evidencia, no repetir pagos. Cancelar sólo si los fondos no salieron o fueron recuperados realmente. Las rutas parten del menú Personal y nómina; las cuentas y movimientos están en Administración → Finanzas.',
'Confirmado por el usuario el 29/10/2026: Bruce Wayne, NOV-2026-DEC-TEST del 01/11/2026 al 30/11/2026, sueldo $12,000.00 y transporte $80.00. BOR-000004 es la nómina original; BOR-000005 es la liquidación del transporte. No aplicar redujo a $12,000.00; restablecer Aplicar recuperó $12,080.00. Posponer conservó $80.00 y permitió pagar $12,000.00 primero. El transporte se pagó por separado como NPI-5, se canceló y se volvió a pagar como NPI-6. BBVA_COBROS terminó en $4,274.29, CAJA en $12,762.85 y CAJA-CORTE-TEST en $1,150.00. No hay dispersión bancaria automática ni timbrado fiscal.',1,DATE '2026-10-29','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_LEER','EMPLEADO_NOMINA_LEER','CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER')
WHERE g.slug='nomina-decisiones-y-pospuestos-confirmados';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Identificar al empleado y la evidencia ya confirmada','Personal y nómina → Empleados → Ver expediente',
'1. Inicia sesión con tu administrador autorizado y busca Bruce Wayne en Personal y nómina → Empleados. 2. Abre Ver expediente y consulta Datos laborales: sueldo mensual histórico de $12,000.00 aplicable en noviembre de 2026 según su vigencia. 3. En Configuración de nómina → Conceptos configurados comprueba transporte $80.00 mensual del 01/11/2026 al 30/11/2026. 4. Como este ensayo ya concluyó, consulta BOR-000004 y BOR-000005 en Nóminas antes de intentar cualquier registro nuevo.',
'El bono de $500 empieza el 01/01/2027; no corresponde a noviembre. El bono de $600 quedó cancelado.',
'Empleado, sueldo y vigencias identificados sin cambiar su configuración.',
'No crear otro transporte ni modificar su vigencia para repetir la prueba. No repetir el sueldo ni el pendiente ya pagados.','/admin/empleados'),
(2,'Entender la configuración usada por el ejemplo','Empleados → Bruce → Ver expediente → Configuración de nómina → Conceptos configurados',
'1. Para comprender un ensayo nuevo autorizado, abre Agregar concepto desde Conceptos configurados. 2. Selecciona TRANS-RED-TEST · Transporte de prueba de rediseño e Importe de referencia 80.00. 3. Elige Se repite durante su vigencia, Este importe corresponde a Un mes, Aplicar desde 01/11/2026 y Último día incluido 30/11/2026, con motivo de prueba. 4. Revisar y guardar concepto permite comprobar y confirmar su modal; en este caso ya confirmado sólo consulta el registro existente, sin volver a guardarlo.',
'Transporte mensual de $80 únicamente durante noviembre. El sueldo base proviene del puesto y no se agrega como concepto adicional.',
'Configuración independiente de la decisión temporal de la nómina.',
'Agregar o cambiar configuración no significa pagar. Esta guía no autoriza duplicar conceptos existentes.','/admin/empleados'),
(3,'Identificar el periodo independiente','Personal y nómina → Periodos de nómina',
'1. Busca NOV-2026-DEC-TEST en Periodos de nómina y abre su consulta o edición sin guardar cambios. 2. Comprueba Inicio del periodo 01/11/2026 y Fin del periodo 30/11/2026. 3. Comprueba Fecha prevista de pago 30/11/2026; no necesitas calendario. 4. En otro ensayo autorizado se usaría Nuevo periodo → Revisar periodo → modal de confirmación, pero no vuelvas a crear ni cambiar el periodo ya utilizado por Bruce.',
'Periodo completo de noviembre de 2026. Los folios y fechas son evidencia de esta base, no valores obligatorios para todos los usuarios.',
'Cobertura de la nómina identificada sin duplicación.',
'No cambiar fechas para eludir reservas. Un periodo registrado no selecciona empleados ni mueve dinero.','/admin/periodos-nomina'),
(4,'Consultar el borrador original y su desglose','Personal y nómina → Nóminas → Ver → Detalle por empleado',
'1. En Nóminas localiza Noviembre — decisiones de Bruce, BOR-000004, y pulsa Ver. 2. Abre la pestaña Detalle por empleado y en Bruce pulsa Ver desglose. 3. La copia original incluye sueldo $12,000.00 y transporte $80.00, total $12,080.00. 4. Abre Decisiones y pendientes de este periodo para distinguir lo originalmente calculado de las decisiones posteriores; no prepares otro borrador.',
'En un ensayo nuevo se usaría Nuevo borrador → elegir periodo → Opciones de selección → Elegir empleados específicos → Preparar nómina del periodo, incluir sólo Bruce y Guardar borrador revisado.',
'Original histórico y acceso real a decisiones identificados.',
'La pestaña visible se llama Detalle por empleado, no Empleados. No confundir desglose histórico con pendiente de pago actual.','/admin/preparacion-nomina'),
(5,'No aplicar transporte únicamente en este periodo','Nóminas → Ver original → Detalle por empleado → Ver desglose → Decisiones y pendientes de este periodo',
'1. Para un borrador editable autorizado, en Transporte pulsa Cambiar decisión y conserva Transporte · $80 en Empleado o concepto, no Todo el pago del empleado. 2. Selecciona No aplicar en este periodo y motivo No otorgar transporte en noviembre de prueba; conservar la configuración original. 3. Pulsa Revisar decisión: el inmediato debe ser $12,000.00; Confirmar tratamiento abre modal y Cancelar lo cierra sin guardar. 4. Tras revisar y confirmar cuando corresponda, verifica No aplicar y el motivo; en este ensayo terminado consulta Historial de decisiones para la evidencia del 29/10/2026 a las 09:39.',
'No aplicar excluye $80 sin crear una percepción pendiente; no elimina el transporte del empleado.',
'Exclusión limitada al periodo, con auditoría y sin salida de dinero.',
'No intentar repetir decisiones sobre una nómina ya pagada. El selector permite cambiar alcance antes de confirmar: revisa que siga siendo Transporte.','/admin/preparacion-nomina'),
(6,'Restablecer Aplicar sin duplicar el concepto','Nóminas → Ver original → Detalle por empleado → Ver desglose → Decisiones y pendientes de este periodo',
'1. En el mismo borrador editable abre Cambiar decisión de Transporte y elige Aplicar / incluir pago. 2. Escribe Restablecer transporte para continuar con la prueba de Posponer, revisa y confirma el modal. 3. El inmediato vuelve a $12,080.00; consulta los dos eventos y Exportar decisiones a Excel, donde transporte de $80 y sueldo de $12,000 están en APLICAR. 4. En Empleados → Bruce → Ver expediente → Configuración de nómina comprueba que transporte de $80 y su vigencia continúan intactos; para el caso concluido consulta el evento del 29/10/2026 a las 09:43.',
'El historial conserva NO_APLICAR y APLICAR; el Excel refleja la decisión vigente, no dos transportes que deban sumarse.',
'Restablecimiento confirmado sin duplicar configuración ni pagar.',
'No sumar ambos eventos históricos como si fueran importes adicionales.','/admin/preparacion-nomina'),
(7,'Posponer transporte conservando la obligación','Nóminas → Ver original → Detalle por empleado → Ver desglose → Decisiones y pendientes de este periodo',
'1. En un borrador editable, pulsa Cambiar decisión de Transporte y conserva el concepto de $80 como alcance. 2. Selecciona Posponer · conservar pendiente y explica Pagar los 80 de transporte de noviembre posteriormente, conservando el pendiente del periodo original. 3. Pulsa Revisar decisión y comprueba $12,000.00 inmediatos; prueba Cancelar del modal antes de confirmar cuando corresponda. 4. Comprueba A pagar ahora $12,000.00, Pospuesto $80.00 y No aplicado $0.00; no se ha movido dinero por esta decisión.',
'A diferencia de No aplicar, Posponer conserva los $80 para entregarlos después en noviembre, no dentro de otra nómina colectiva.',
'Sueldo inmediato separado del transporte pendiente.',
'No posponer todo el empleado en este caso. Tras pagar o autorizar la revisión ya no se cambian sus decisiones.','/admin/preparacion-nomina'),
(8,'Registrar sólo el pago inmediato del sueldo','Nóminas → Ver original → Autorización y pagos por empleado → Resumen',
'1. Comprueba $12,000.00 inmediatos y pulsa Preparar pago colectivo para el único empleado incluido. 2. En Cuenta de origen selecciona BBVA_COBROS, importe 12000, Motivo financiero de egreso para nómina, referencia NOV-BRUCE-INMEDIATO-TEST y motivo que explique los 80 pospuestos. 3. Pulsa Revisar distribución: desde saldo $16,354.29 debe quedar $4,354.29; corrige mediante Editar distribución si hace falta. 4. Confirmar y registrar pagos abre modal con Cancelar; confirma una sola vez cuando proceda y verifica un egreso de $12,000.00, no $12,080.00.',
'Este pago ya se realizó y no debe repetirse. Las cajas permanecieron $12,762.85 y $1,150.00.',
'Sueldo pagado, transporte de $80 todavía pendiente del periodo original.',
'No usar Confirmar decisiones sin mover dinero para intentar pagar; esa acción sólo congela decisiones. Un registro interno no envía transferencias bancarias.','/admin/preparacion-nomina'),
(9,'Localizar el transporte en la nómina original','Nóminas → Ver BOR-000004 → Autorización y pagos por empleado → Decisiones y pendientes',
'1. Desde BOR-000004 pulsa Ver conceptos pospuestos, no desde la liquidación BOR-000005. 2. En Buscar empleado enfoca para hasta diez opciones o escribe Bru desde tres caracteres y selecciona Bruce Wayne. 3. Selecciona Estado del pendiente Pendiente y Aplicar filtros para un caso aún no liquidado; en este caso final usa Pagado o Todos. 4. Comprueba transporte de $80 del periodo NOV-2026-DEC-TEST y Exportar Excel: debe coincidir empleado, periodo, importe y estado.',
'La selección usa identificador, no el texto concatenado del nombre. Limpiar elimina el filtro de empleado y estado.',
'Pendiente e historial localizados en el registro que los originó.',
'La liquidación no tiene otros conceptos pospuestos derivados: su lista vacía no significa que se borró el pendiente de BOR-000004.','/admin/preparacion-nomina'),
(10,'Pagar por separado únicamente los 80 pospuestos','Original → Ver conceptos pospuestos → Preparar pago individual',
'1. En un pendiente de $80 todavía sin pagar pulsa Preparar pago individual, prueba Cancelar del modal y después confirma cuando corresponda. 2. Se abre una liquidación separada con Total a pagar $80.00; en este ensayo se generó BOR-000005. 3. Captura BBVA_COBROS e importe 80, motivo financiero de nómina, referencia NOV-BRUCE-TRANSPORTE-80 y motivo Entrega posterior de los 80 de transporte pospuestos de noviembre de Bruce. 4. Revisar distribución debe mostrar $4,354.29 antes y $4,274.29 después; revisa el modal de Confirmar y registrar pagos y confirma una vez.',
'Pago original del transporte NPI-5, egreso de $80 del 29/10/2026 a las 10:22. No se volvió a pagar el sueldo.',
'Liquidación del pendiente en su periodo original, con cuentas y evidencia propias.',
'NPI-5 ya fue cancelado después y sustituido por NPI-6. No reproducir ese pago. Si aparece un importe distinto de80, detenerse.','/admin/preparacion-nomina'),
(11,'Comprobar el primer cierre y la configuración intacta','Administración → Finanzas → Movimientos financieros y Cuentas financieras; Empleados → Configuración de nómina',
'1. Consulta los egresos del sueldo de $12,000 y transporte de $80 desde BBVA_COBROS, sin duplicaciones. 2. Comprueba el saldo de ese momento $4,274.29, CAJA $12,762.85 y CAJA-CORTE-TEST $1,150.00. 3. Desde la nómina original → Autorización y pagos por empleado → Decisiones y pendientes → Ver conceptos pospuestos consulta el estado Pagado del transporte. 4. En Empleados → Bruce → Ver expediente → Configuración de nómina → Conceptos configurados verifica transporte de $80 mensual del 01/11/2026 al 30/11/2026.',
'Total entregado $12,080. El pago no borra ni cancela la configuración del empleado.',
'Pago separado y configuración confirmados por el usuario.',
'Los saldos son evidencia del ejemplo; movimientos posteriores pueden cambiarlos. No ajustar cuentas manualmente para igualarlos.','/admin/cuentas-financieras'),
(12,'Cancelar sólo el pago del transporte recuperado','Original → Ver conceptos pospuestos → Ver liquidación → Pagos por empleado → Ver pago e intentos → Registrar resultado',
'1. Desde la fila de $80 de BOR-000004 abre Ver liquidación y verifica BOR-000005; en Pagos por empleado → Ver pago e intentos → Historial de intentos identifica NPI-5 por $80. 2. Abre Registrar resultado → Cancelar sólo este pago y selecciona Motivo financiero de ingreso compatible, Referencia de devolución RECUP-NOV-BRUCE-80 y motivo Recuperación de los 80 de transporte para probar reapertura y pago posterior. 3. Marca recuperación real sólo si corresponde y pulsa Revisar cancelación individual; prueba Cancelar del modal, que no debe cambiar el pago ni la cuenta. 4. Revisa y confirma cuando proceda: el banco recupera $80, de $4,274.29 a $4,354.29, conservando NPI-5 como Pago cancelado.',
'El sueldo de $12,000 sigue vigente. Se recupera únicamente el importe de la liquidación, no toda la nómina original.',
'Intento cancelado y movimiento compensatorio conservados.',
'No cancelar todos los pagos ni usar BOR-000004 para devolver el sueldo. NPI-5 ya está cancelado; sólo consultar su evidencia.','/admin/preparacion-nomina'),
(13,'Comprobar la reapertura del mismo pendiente','Nóminas → Ver BOR-000004 → Autorización y pagos por empleado → Decisiones y pendientes → Ver conceptos pospuestos',
'1. Tras una cancelación válida vuelve a BOR-000004, no a la lista de pospuestos de BOR-000005. 2. Selecciona Bruce, estado Pendiente y pulsa Aplicar filtros: el mismo pendiente de $80 vuelve a estar disponible, sin crear otro duplicado. 3. En Movimientos financieros comprueba un ingreso de $80 vinculado al egreso de NPI-5 y en Cuentas financieras el saldo de ese momento $4,354.29. 4. En Ver liquidación → Pagos por empleado → Ver pago e intentos → Historial de intentos consulta Pago cancelado y su motivo; en el caso final ya repagado usa Todos o Pagado para consultar la fila.',
'Pendiente describe la obligación actual; Pago cancelado describe el intento histórico, que no se borra.',
'Misma obligación reabierta sin afectar el sueldo ni otro periodo.',
'No crear un nuevo concepto, periodo o borrador para reponer el transporte.','/admin/preparacion-nomina'),
(14,'Volver a pagar el mismo pendiente sin duplicar sueldo','Original → Ver conceptos pospuestos → Preparar pago individual',
'1. En la fila de $80 aún pendiente pulsa Preparar pago individual y confirma su modal; reutiliza BOR-000005. 2. Captura BBVA_COBROS e importe 80, motivo financiero de nómina, referencia REPOSICION-NOV-BRUCE-80 y motivo Nuevo pago del transporte después de cancelar el intento anterior; conservar ambos intentos. 3. Pulsa Revisar distribución y comprueba total de $80, saldo anterior $4,354.29 y posterior $4,274.29; usa Editar distribución para corregir. 4. Pulsa Confirmar y registrar pagos y confirma el modal una vez; el sistema crea un intento nuevo, NPI-6 en esta base.',
'La liquidación conserva periodo, empleado e importe; sólo cambia el intento y su referencia.',
'Nuevo egreso de $80 y pago vigente sin eliminar NPI-5 cancelado.',
'El nuevo folio es automático. No tiene que ser NPI-6 en otra base. No repetir este caso ya concluido.','/admin/preparacion-nomina'),
(15,'Comprobar el cierre definitivo de los tres escenarios','Nómina original y liquidación → Historial; Administración → Finanzas',
'1. Desde BOR-000004 → Autorización y pagos por empleado → Decisiones y pendientes → Ver conceptos pospuestos → Pagado comprueba transporte de $80 y Ver liquidación. 2. En BOR-000005 → Pagos por empleado → Ver pago e intentos → Historial de intentos comprueba NPI-5 cancelado y NPI-6 pagado. 3. En Cuentas financieras confirma banco $4,274.29 y cajas $12,762.85/$1,150.00, considerando movimientos posteriores; el ciclo de reposición suma ingreso de $80 y egreso de $80. 4. Verifica sueldo de $12,000 vigente y configuración de transporte intacta; conserva Excel y auditoría y no vuelvas a pagar ni cancelar nada.',
'Usuario confirmó todos estos controles. No aplicar/restablecer, Posponer/pago separado y cancelar/repagar quedaron validados.',
'Tres escenarios confirmados, con historial íntegro y sin duplicación de salida neta.',
'Fondos insuficientes y concurrencia requieren ensayos independientes. Esta guía no los declara confirmados.','/admin/preparacion-nomina')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='nomina-decisiones-y-pospuestos-confirmados';

-- Actualización editorial: no modificar migraciones anteriores ni sus checksums.
UPDATE guia_proceso_paso SET precaucion='La consulta bloqueada no altera saldos. No crear otro periodo superpuesto. No aplicar/Posponer se documentan en la guía Nómina: no aplicar, posponer y pagar conceptos por separado, con su propio ensayo confirmado de Bruce.'
WHERE numero=9 AND guia_id=(SELECT id FROM guia_proceso WHERE slug='nomina-pago-individual-sin-borrador-confirmado');
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-29'
WHERE slug='nomina-pago-individual-sin-borrador-confirmado';
