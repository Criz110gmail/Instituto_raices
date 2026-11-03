-- Sólo documentación confirmada; no realiza pagos ni modifica roles o cuentas.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('nomina-cero-directo-confirmada','Nómina: confirmar un neto cero desde Pago individual','Personal',
'Si las percepciones y deducciones se compensan, registra que ese periodo quedó resuelto sin elegir cuentas ni sacar dinero. No es excluir al empleado ni posponer su pago.',
'Administrador con permisos privados de nómina y operación financiera. Esta guía no concede permisos. El ejemplo ya terminó: consultar BOR-000015, no repetir el cierre ni recapturar sus conceptos.',
'Confirmado por el usuario el 03/11/2026. Diego, DIC-2026-DOS-PESTANAS-TEST, 16/12/2026–31/12/2026. Percepción EXTRA-RED-TEST $200 y deducción DESC-TEST $200, ambas de una sola aplicación. Neto $0, BOR-000015 revisión 1, Confirmada sin pagos, sin NPI ni egreso. CAJA $1,195.11; BBVA_COBROS $274.29; CAJA-CORTE-TEST $1,150.00, sin cambios. Repetir la consulta informa que ya existe neto cero confirmado.',1,DATE '2026-11-03','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_LEER','EMPLEADO_NOMINA_LEER','CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER')
WHERE g.slug='nomina-cero-directo-confirmada';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Revisar empleado y periodo','Personal y nómina → Empleados; Periodos de nómina',
'Consulta a Diego y el periodo DIC-2026-DOS-PESTANAS-TEST del 16 al 31 de diciembre de 2026. No requiere sueldo base si sólo corresponden conceptos adicionales. En esta base el ejemplo ya está confirmado.',
'Diego: sin sueldo base, sin cuotas de préstamo.','Datos del ejemplo identificados.',
'No modificar periodos ni registros anteriores para repetir esta prueba.','/admin/periodos-nomina'),
(2,'Configurar conceptos que se compensan','Empleados → Ver expediente → Configuración de nómina → Agregar concepto',
'En un ensayo nuevo autorizado, agrega EXTRA-RED-TEST por $200 y DESC-TEST por $200, Una sola vez, en un periodo. Selecciona el mismo periodo para ambos y revisa sus fechas automáticas. Guarda cada concepto con motivo y confirmación. En este caso ya existen y sólo se consultan.',
'$200 de percepción menos $200 de deducción da $0.','Conceptos conservados en la configuración.',
'No duplicar conceptos ni agregar sueldo para forzar este resultado.','/admin/empleados'),
(3,'Consultar el cálculo sin crear borrador previo','Personal y nómina → Nóminas → Pago individual',
'Selecciona periodo y empleado desde las sugerencias y pulsa Consultar cálculo. Si el neto es cero aparece Confirmar neto cero del empleado, con desglose y justificación, sin cuenta, motivo financiero ni referencia de pago.',
'Sueldo $0, percepciones $200, deducciones $200, total $0.','Cálculo visible sin guardar ni mover dinero.',
'Un neto negativo sigue bloqueado; cero con cuotas de préstamo requiere su tratamiento específico.','/admin/preparacion-nomina/pago-individual'),
(4,'Revisar y cancelar sin guardar','Pago individual → Revisar neto cero',
'Escribe el motivo de la operación y pulsa Revisar neto cero. Revisa el desglose y motivo protegidos; Editar justificación permite corregirlo. Confirmar neto cero abre el modal. Cancelar dentro del modal vuelve a la revisión sin guardar.',
'Confirmar el neto cero de Diego del 16 al 31 de diciembre: percepción 200 y deducción 200, sin salida de dinero.','Cancelar no crea un cierre ni una nómina.',
'No confundir Cancelar dentro del modal con el enlace de salir de la pantalla.','/admin/preparacion-nomina/pago-individual'),
(5,'Confirmar y conservar el detalle','Revisión → Confirmar neto cero',
'Confirma en el modal el empleado, periodo, importe cero y motivo. El sistema conserva la fotografía de nómina y el cierre con usuario y fecha, en una sola operación, sin dejar un borrador pendiente ni generar un pago de cero.',
'BOR-000015, Confirmada sin pagos; Diego Neto cero confirmado.','Cierre histórico sin salida de dinero.',
'Esta operación ya fue realizada: consultar el registro, no repetirlo.','/admin/preparacion-nomina'),
(6,'Comprobar cuentas y bloqueo del duplicado','Nóminas → Ver → Autorización y pagos por empleado; Finanzas',
'En Pagos por empleado, Diego figura Neto cero confirmado. Ver pago e intentos → Resumen conserva fecha, responsable y motivo; el desglose conserva ambos conceptos. No hay NPI ni movimiento financiero. Repetir Pago individual con el mismo empleado y periodo informa que ya existe un neto cero confirmado para fechas coincidentes.',
'CAJA $1,195.11, BBVA_COBROS $274.29 y CAJA-CORTE-TEST $1,150.00, sin cambios.','Prueba aprobada por el usuario; sin cierre ni salida duplicados.',
'Los saldos son históricos. Si hay movimientos posteriores, consultarlos; no ajustar cuentas para hacerlos coincidir.','/admin/preparacion-nomina')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='nomina-cero-directo-confirmada';
