-- Guía de la funcionalidad nueva: pendiente de aceptación humana. Sin datos operativos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('prestamos-programados-nomina','Préstamos: entregar, descontar y reprogramar el saldo','Personal',
'Registra lo prestado y sus cuotas. Guardar no entrega dinero; la deuda baja al liquidar la nómina. Una cancelación de pago restaura la cuota y conserva el historial.',
'Cuenta administrativa activa con alcance institucional. Permisos separados Préstamos - Consultar, Administrar, Entregar y Reestructurar; entregar requiere registro financiero y acceso a la cuenta. Nómina conserva sus permisos privados y financieros. No se asigna ningún permiso automáticamente.',
'Ejemplo pendiente de prueba humana: Tomás, principal $200, interés total 10% ($20), cuatro cuotas quincenales de $55. Primera nómina: percepción única $500 menos cuota $55 = $445. Tras un pago quedan $165. Reprogramar a una cuota, sin interés nuevo, permite liquidar ese saldo en la siguiente quincena. No repetir ejemplos ya pagados de otros empleados.',1,DATE '2026-11-04','PENDIENTE');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('PRESTAMO_LEER','PRESTAMO_ADMINISTRAR','PRESTAMO_ENTREGAR','PRESTAMO_REESTRUCTURAR',
 'CONDICION_SALARIAL_ADMINISTRAR','EMPLEADO_NOMINA_ADMINISTRAR','MOVIMIENTO_FINANCIERO_REGISTRAR','MOVIMIENTO_FINANCIERO_REVERTIR','CUENTA_FINANCIERA_LEER')
WHERE g.slug='prestamos-programados-nomina';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Preparar empleado y concepto','Personal y nómina → Empleados; Conceptos de nómina',
'1. Confirma empleado activo y fecha de ingreso. 2. Crea o selecciona un concepto activo de tipo Deducción y forma Con saldo. 3. No lo asignes también como deducción habitual del empleado: la cuota del préstamo se incorpora por separado.',
'Tomás Prueba Préstamos; PREST-TEST, Recuperación de préstamo de prueba.',
'Empleado y deducción disponibles para seleccionar.',
'No necesita calendario. No crear nómina ni entregar dinero en esta preparación.','/admin/conceptos-nomina'),
(2,'Programar las cuotas sin entregar','Personal y nómina → Préstamos → Nuevo préstamo',
'1. Selecciona empleado y deducción desde las sugerencias. 2. Captura principal, modalidad y porcentaje total, frecuencia, número de cuotas, primer descuento y motivo. 3. Marca Descontar mediante nómina. 4. Pulsa Revisar programación y compara cada fecha e importe. 5. Confirmar y guardar abre el modal; Cancelar no guarda.',
'Principal 200, porcentaje total 10%, cuatro quincenas desde 30/11/2026: capital total 200 e interés total 20; cada cuota 55.',
'Borrador de préstamo con cuotas propuestas; saldo real entregado cero.',
'No es tasa mensual. Quincenal usa 15 y último día del mes. Última cuota ajusta centavos.','/admin/prestamos/nuevo'),
(3,'Registrar la entrega real','Préstamos → Ver → Gestión → Entregar el principal',
'1. Selecciona caja o banco compatible, motivo financiero de egreso y referencia. 2. Escribe el motivo y confirma la entrega real. 3. Pulsa Revisar entrega. 4. Verifica cuenta, principal y saldo posterior protegidos. 5. Usa Editar captura para corregir o Confirmar y registrar entrega para abrir el modal.',
'Se entregan 200, no 220. La deuda resultante es capital 200 más interés pactado 20.',
'Una sola salida financiera por el principal; préstamo activo.',
'No envía transferencias. Cancelar el modal no mueve dinero. Si falta saldo indica cuánto falta.','/admin/prestamos'),
(4,'Revisar y liquidar la cuota en nómina','Personal y nómina → Nóminas',
'1. Prepara el periodo o entra en Pago individual y selecciona empleado. 2. Consulta el cálculo. 3. Revisa percepciones, cuota del préstamo y neto. 4. Revisa distribución de cuentas y confirma según el flujo habitual de nómina.',
'Percepción única 500 menos primera cuota 55: neto 445. Después del pago: capital 150 e interés 15 pendientes.',
'El pago real registra la cuota una sola vez, con capital e interés separados.',
'Preparar o autorizar no abona. Neto negativo se bloquea; no se aplica un descuento parcial silencioso.','/admin/preparacion-nomina'),
(5,'Consultar saldo e historial','Personal y nómina → Préstamos → Ver',
'1. En Resumen consulta capital, interés y total pendientes. 2. En Cuotas y abonos revisa fechas, cuotas abonadas e intentos cancelados. 3. En Historial abre Ver cuotas de una programación anterior. 4. En el listado filtra por empleado o estado y exporta Excel.',
'Tras la primera cuota: 165 pendientes. Una fecha transcurrida no equivale a un abono.',
'Consulta sin cambiar configuraciones ni mover dinero.',
'Vencido con saldo no significa liquidado. No se prolongan descuentos fuera de las fechas programadas.','/admin/prestamos'),
(6,'Cancelar un pago de nómina','Nóminas → Ver → Autorización y pagos por empleado',
'1. Abre Pagos por empleado → Ver pago e intentos → Registrar resultado. 2. En Cancelar sólo este pago captura motivo financiero de ingreso, referencia y justificación. 3. Confirma que los fondos están recuperados; si hubo cheques deben quedar no cobrables. 4. Revisa y confirma. 5. Para pagar otra vez utiliza la misma fila, no otro pago individual del periodo.',
'Cancelar neto 445 devuelve 445 a sus cuentas originales y restaura la cuota 55. La deuda vuelve de 165 a 220.',
'Compensación financiera y restauración del préstamo, sin borrar el intento anterior.',
'Si reabre deuda de una programación sustituida, revisa el aviso de reprogramación antes de preparar otro descuento.','/admin/preparacion-nomina'),
(7,'Ampliar plazo o liquidar antes','Préstamos → Ver → Gestión → Reprogramar préstamo',
'1. Revisa capital e interés pendientes protegidos. 2. Selecciona frecuencia, número de cuotas, primer descuento y motivo. 3. Deja Sin interés nuevo para conservar sólo lo pendiente. 4. Revisa la propuesta. 5. Cancelar no cambia nada; confirmar conserva una nueva programación sin otra entrega.',
'Con saldo 165: dos quincenas de 82.50 o una cuota de 165. Porcentaje nuevo 0; interés anterior 15 no se vuelve a cobrar ni se convierte en capital.',
'Saldo redistribuido; programaciones y abonos previos conservados.',
'Cuotas comprometidas en una nómina autorizada pendiente bloquean la reprogramación. No modificar la cuenta para forzar saldos.','/admin/prestamos'),
(8,'Comprobar casos sin salida de dinero','Nóminas y Préstamos',
'1. Posponer o no aplicar todo el empleado conserva la deuda hasta resolver su pago. 2. Si la nómina compensa exactamente las cuotas y el neto es cero, confirma el cierre sin seleccionar cuenta. 3. Consulta la compensación en el historial del préstamo y el detalle de nómina.',
'Percepción 100 menos cuota programada 100: neto cero; deuda baja por compensación, sin NPI ni egreso bancario cero.',
'Registro auxiliar auditable de cartera y nómina, sin dinero ficticio.',
'No es un asiento de contabilidad general. No hay cancelación de cierres cero en esta entrega ni conversión automática de préstamos históricos.','/admin/prestamos')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='prestamos-programados-nomina';
