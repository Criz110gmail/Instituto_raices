-- Sólo guía y contenido editorial. No genera decisiones, nóminas, pagos ni movimientos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('nomina-no-aplicar-empleado-confirmada','Nómina: no aplicar el pago de un empleado en este periodo','Personal',
'Sirve para indicar: este pago no corresponde en este periodo. Se guarda un motivo, no sale dinero y no queda pendiente para después. Si todos los empleados fueron excluidos y confirmas las decisiones, queda Confirmada sin pagos, no como borrador.',
'Administrador identificado con permisos de nómina. No aplicar excluye sólo este periodo; Posponer significa se lo pagaré después y conserva el pendiente. Usa No aplicar únicamente con una justificación válida y autorizada por la escuela. Esta función registra la decisión; no determina si existe una obligación laboral. Caso ya concluido: consulta BOR-000010 sin repetir, pagar ni cancelar. Permisos de lectura de guía no conceden facultades a roles.',
'Confirmado por el usuario el 30/10/2026: Luis Prueba Posposición, EMP-POSP-TOTAL-TEST, BOR-000010, periodo DIC-2026-DOS-PESTANAS-TEST del 16/12/2026 al 31/12/2026. Sueldo $516.13 más transporte $41.29, total original $557.42. No aplicar sobre todo el empleado y Confirmar decisiones sin mover dinero: Confirmada sin pagos, sin pagos registrados ni conceptos pospuestos. Cuentas intactas: CAJA $3,762.85, BBVA_COBROS $274.29 y CAJA-CORTE-TEST $1,150.00. Sueldo y transporte originales conservados, apoyos temporales de $10 cancelados.',1,DATE '2026-10-30','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_LEER','EMPLEADO_NOMINA_LEER','CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER')
WHERE g.slug='nomina-no-aplicar-empleado-confirmada';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Elegir entre no aplicar y pagar después','Personal y nómina → Nóminas',
'1. Identifica al empleado y el periodo que necesitas revisar. 2. Si el pago se hará después, usa Posponer para conservarlo pendiente en su periodo original. 3. Si ese pago no corresponde en este periodo y existe autorización para excluirlo, usa No aplicar y explica el motivo. 4. No borres ni cambies el sueldo o los conceptos del empleado para excluir sólo esta nómina.',
'Luis tenía calculados $557.42; la prueba excluye ese importe del periodo, no lo guarda para pagar después.',
'El usuario distingue No aplicar de Posponer antes de confirmar.',
'Registrar un motivo no cancela por sí mismo obligaciones laborales. No aplicar no debe usarse para ocultar pagos que sí corresponden.','/admin/preparacion-nomina'),
(2,'Identificar el borrador y el importe original','Personal y nómina → Nóminas → Ver BOR-000010 → Detalle por empleado',
'1. Busca BOR-000010, Preparación · DIC-2026-DOS-PESTANAS-TEST, ya existente en este caso. 2. Comprueba empleado Luis y fechas 16/12/2026 al 31/12/2026. 3. Consulta el cálculo original: sueldo $516.13 y transporte $41.29, total $557.42. 4. Para un ensayo nuevo autorizado se prepara Nuevo borrador → Opciones de selección → Elegir empleados específicos → Preparar nómina del periodo → buscar empleado → Incluir → Guardar borrador revisado y confirmar el modal.',
'La búsqueda sólo filtra; Incluir agrega al empleado. El folio nuevo lo asigna el sistema.',
'Periodo, empleado e importe identificados sin sacar dinero.',
'Este ejemplo ya terminó. No crear otro borrador para Luis con estas mismas fechas ni tocar BOR-000009.','/admin/preparacion-nomina'),
(3,'Decidir no aplicar todo el pago','Nóminas → Ver borrador → Detalle por empleado → Luis → Ver desglose → Decisiones y pendientes de este periodo',
'1. En Resumen de Decisiones sobre conceptos pulsa Decidir sobre todo el empleado. 2. En Empleado o concepto selecciona Todo el pago del empleado y en Qué hacer en este periodo selecciona No aplicar en este periodo. 3. Captura el motivo que explica la exclusión y pulsa Revisar decisión; debe mostrar pago inmediato $0.00. 4. Pulsa Confirmar tratamiento y revisa el modal: Cancelar lo cierra sin guardar; confirmar guarda la decisión con su motivo, sin mover dinero.',
'Motivo usado: Excluir el pago completo de Luis de este periodo como prueba, sin posponerlo ni modificar su configuración.',
'A pagar ahora $0.00, pospuesto $0.00 y tratamiento No aplicar.',
'No elegir únicamente Transporte si se desea excluir todo el pago. Guardar una decisión todavía no equivale a cerrar las decisiones de la nómina.','/admin/preparacion-nomina'),
(4,'Confirmar las decisiones sin pagar','Nóminas → Ver BOR-000010 → Autorización y pagos por empleado → Decisiones y pendientes',
'1. Regresa al detalle del borrador y abre Autorización y pagos por empleado → Decisiones y pendientes. 2. En Motivo para confirmar las decisiones explica que se cierra la exclusión sin pago ni posposición. 3. Pulsa Confirmar decisiones sin mover dinero y confirma su modal después de revisar. 4. No prepares pago colectivo ni selecciones cuentas: todos los pagos de este ejemplo están excluidos y no corresponde registrar una salida.',
'Cuando todos fueron excluidos, la nómina queda Confirmada sin pagos. No queda como borrador ni representa dinero entregado.',
'Decisión final conservada con fecha, usuario y motivo, sin pagos pendientes.',
'Después de confirmar las decisiones no se modifican sus conceptos. No confundir este cierre con pagar un neto de cero.','/admin/preparacion-nomina'),
(5,'Revisar el resultado y por qué se excluyó','Nóminas → Ver BOR-000010 → Autorización y pagos por empleado → Pagos por empleado',
'1. Busca Luis con la ayuda de Buscar empleado, selecciona Estado del pago No aplicar y pulsa Aplicar filtros; la vista debe permanecer junto a los filtros. 2. Comprueba que Luis diga No aplicar, no Pagado, Pendiente ni Pospuesto, y exporta la tabla a Excel si necesitas revisar el resultado. 3. Desde el desglose abre Decisiones y pendientes de este periodo → Historial de decisiones para ver quién decidió, cuándo, qué acción tomó y su motivo; Conceptos muestra las partidas y Resumen el resultado. 4. Regresa a Autorización y pagos por empleado → Decisiones y pendientes → Ver conceptos pospuestos y verifica que este caso no creó ningún registro; sus filtros también conservan la posición.',
'Historial: Todo el pago del empleado → No aplicar en este periodo, con su motivo.',
'Exclusión trazable y ausencia de un pendiente para pagar después.',
'El total original se conserva para explicar la decisión. No representa un pago realizado.','/admin/preparacion-nomina'),
(6,'Comprobar dinero y configuración sin cambios','Finanzas → Cuentas financieras y Movimientos financieros; Personal y nómina → Empleados',
'1. En Cuentas financieras confirma CAJA $3,762.85, BBVA_COBROS $274.29 y CAJA-CORTE-TEST $1,150.00 si no hubo otras operaciones. 2. En Movimientos financieros comprueba que no existe egreso de BOR-000010. 3. En Empleados → Luis → Ver expediente → Configuración de nómina verifica sueldo mensual $1,000 y transporte mensual $80 del 01/12/2026 al 31/12/2026; los dos apoyos extraordinarios de $10 siguen cancelados. 4. Conserva BOR-000010 como evidencia confirmada sin pagos y no lo repitas; los siguientes periodos usarán la configuración y vigencias que correspondan.',
'En palabras simples: este pago no se aplica, queda explicado por qué, no sale dinero y no queda para después.',
'Prueba confirmada sin afectar cuentas ni configuración de otros periodos.',
'No modificar saldos iniciales para forzar resultados ni marcar esta exclusión como un pago real.','/admin/cuentas-financieras')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='nomina-no-aplicar-empleado-confirmada';

-- Mantener las rutas de la guía anterior alineadas con las pestañas nuevas.
UPDATE guia_proceso_paso SET instrucciones=replace(replace(replace(instrucciones,
'Cambiar decisión','Conceptos → Cambiar decisión'),
'Historial de decisiones','la pestaña Historial de decisiones'),
'Exportar decisiones a Excel','Resumen → Exportar decisiones a Excel'),
modulo=modulo||' → Conceptos'
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='nomina-decisiones-y-pospuestos-confirmados')
AND numero IN (5,6,7);
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-30'
WHERE slug='nomina-decisiones-y-pospuestos-confirmados';
