-- Contenido editorial confirmado. No cancela, registra ni repite pagos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('nomina-cancelacion-y-nuevo-pago-confirmados','Nómina: cancelar un pago y volver a pagar desde sus cuentas','Personal',
'Cancela el pago de un empleado con recuperación completa por cuenta, conserva el intento anterior y registra un nuevo pago del mismo periodo sin crear otro borrador.',
'Administrador institucional identificado con permisos laborales, configuración y administración salarial, pagos financieros y cancelación. Para consultar, Guía de procesos y permisos privados de lectura asociados. Sólo cancelar cuando el dinero no salió o fue recuperado realmente. Este caso de prueba ya está concluido: consultar sin repetir operaciones.',
'Confirmado por el usuario el 28/10/2026: Diana Prueba Puestos, BOR-000002, JUL-2027-RED-TEST, neto $14,187.15. NPI-1 cancelado a las 15:32, recuperando $10,000.00 en BBVA_COBROS y $4,187.15 en CAJA. Nuevo pago NPI-2 a las 16:41 desde esas mismas cuentas. Saldos finales banco $23,330.00 y caja $12,762.85; CAJA-CORTE-TEST sin cambios en $1,150.00. No implica dispersión bancaria ni timbrado fiscal.',1,DATE '2026-10-28','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_LEER','EMPLEADO_NOMINA_LEER','CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER')
WHERE g.slug='nomina-cancelacion-y-nuevo-pago-confirmados';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Identificar el pago y sus cuentas originales','Personal y nómina → Nóminas → Ver → Autorización y pagos por empleado',
'1. Inicia sesión con administrador institucional autorizado y entra a Personal y nómina → Nóminas. 2. Localiza BOR-000002 del periodo JUL-2027-RED-TEST y pulsa Ver; no abras el borrador de enero ni crees uno nuevo. 3. Pulsa Autorización y pagos por empleado → Pagos por empleado, busca Diana y aplica los filtros. 4. Pulsa Ver pago e intentos y comprueba empleado, neto $14,187.15 e Historial de intentos; si ya aparece NPI-2 pagado, consulta únicamente los resultados finales de esta guía.',
'Pago original NPI-1: banco $10,000.00 y caja $4,187.15. El neto pertenece a julio de Diana.',
'Empleado, periodo e intento identificados sin repetir un pago.',
'El caso ya está confirmado. Los identificadores dependen de la base: nunca cancelar un pago sólo porque se parece al ejemplo.','/admin/preparacion-nomina'),
(2,'Preparar la cancelación de un solo empleado','Nóminas → Ver → Autorización y pagos por empleado → Pagos por empleado → Ver pago e intentos → Registrar resultado',
'1. Sólo para un nuevo ensayo autorizado, abre Registrar resultado del pago vigente que quieres cancelar. 2. Localiza Cancelar sólo este pago; no uses Cancelar todos los pagos vigentes. 3. Selecciona un Motivo financiero de ingreso autorizado y captura Referencia de devolución RECUP-NOM-RED-JUL-01. 4. En Motivo de reversa escribe Cancelación del pago de prueba de Diana de julio de 2027; recuperación de 10000 en BBVA_COBROS y 4187.15 en CAJA para comprobar la devolución a las cuentas originales.',
'Las fuentes de recuperación se toman del pago original; no se eligen cuentas distintas ni un importe parcial.',
'Justificación preparada; todavía no hay ingresos compensatorios.',
'Marca recuperación real únicamente cuando corresponda. La cancelación no debe utilizarse para ocultar una salida de dinero que no se recuperó.','/admin/preparacion-nomina'),
(3,'Confirmar recuperación y cancelar sin borrar historial','Ver pago e intentos → Registrar resultado → Cancelar sólo este pago',
'1. Confirma la casilla que indica que el dinero realmente regresó a la cuenta original. 2. Pulsa Revisar cancelación individual y verifica que el modal corresponda a Diana y $14,187.15. 3. En un nuevo ensayo prueba Cancelar dentro del modal y comprueba que no cambian pago ni saldos. 4. Vuelve a revisar y confirma únicamente cuando la captura y la recuperación real sean correctas.',
'Estado del intento anterior en pantalla: Pago cancelado. Internamente se conserva el código REVERTIDO, sin eliminar el registro.',
'La cancelación conserva referencia, motivo, fecha, usuario y recuperación por fuente.',
'El botón Cancelar del modal sólo lo cierra; no cancela un pago. No reenviar esta operación para NPI-1, que ya quedó cancelado.','/admin/preparacion-nomina'),
(4,'Comprobar fondos recuperados y pendiente correcto','Administración → Movimientos financieros y Cuentas financieras; Nóminas → Historial de intentos',
'1. Entra a Movimientos financieros y localiza la cancelación de NPI-1 del 28/10/2026 a las 15:32. 2. Comprueba INGRESO NOMINA_REVERSO por $10,000.00 en BBVA_COBROS y $4,187.15 en CAJA, vinculados a sus egresos originales. 3. En Cuentas financieras comprueba banco $33,330.00 y caja $16,950.00 en ese momento, sin otros movimientos; CAJA-CORTE-TEST permanece $1,150.00. 4. Vuelve a Nóminas → Ver BOR-000002 → Autorización y pagos por empleado → Pagos por empleado: Diana queda pendiente por $14,187.15 y el historial conserva Pago cancelado.',
'Pendiente de pago es el estado actual de la obligación; Pago cancelado identifica el intento histórico NPI-1.',
'Recuperación completa y derecho a cobrar restaurado sin borrar la nómina.',
'Los saldos son referencias del ensayo. Después del nuevo pago ya no mostrarán estos valores; consulta la secuencia de movimientos para reconstruir cada momento.','/admin/movimientos-financieros'),
(5,'Abrir el mismo pendiente para volver a pagar','Nóminas → Ver BOR-000002 → Autorización y pagos por empleado → Pagos por empleado',
'1. Quita el filtro Pagado y elige Pendiente o todos los estados; busca y selecciona Diana y aplica. 2. Pulsa Ver pago e intentos → Registrar resultado. 3. En Registrar pago completo pulsa Pagar desde una o varias cuentas y verifica Total a pagar $14,187.15. 4. Conserva el mismo periodo y borrador: el importe se toma de la fotografía histórica, no se recalcula desde la configuración actual.',
'No crear otra nómina, periodo ni sueldo para volver a pagar el pendiente restaurado.',
'Formulario del nuevo intento abierto para el mismo empleado y periodo.',
'Si no aparece Pendiente o el importe difiere, detén el ensayo y revisa el historial antes de confirmar. No canceles otra vez un pago distinto.','/admin/preparacion-nomina'),
(6,'Capturar distribución y nueva referencia','Pagar desde una o varias cuentas → Distribuir el pago de nómina',
'1. Selecciona BBVA_COBROS como primera Cuenta de origen e Importe de esta cuenta 10000.00; el medio Transferencia es automático. 2. Pulsa Agregar otra cuenta, selecciona CAJA e importe 4187.15; el medio Efectivo es automático. 3. Selecciona un Motivo financiero autorizado de pago de nómina y referencia REPAGO-NOM-RED-JUL-01. 4. Escribe el motivo Nuevo pago de prueba de Diana de julio de 2027, después de cancelar y recuperar el pago anterior. Banco 10000 y caja 4187.15.; pulsa Revisar distribución.',
'Fuentes suman exactamente $14,187.15. No es un abono parcial ni una combinación de periodos.',
'Captura completa lista para revisión, sin nuevas salidas de dinero.',
'Si una fuente no tiene fondos suficientes, corrige la distribución. No inventar ingresos ni omitir una cuenta para forzar el pago.','/admin/preparacion-nomina'),
(7,'Revisar sin editar y confirmar el nuevo intento','Distribuir el pago de nómina → Revisión protegida',
'1. Comprueba revisión de un empleado por $14,187.15, banco $10,000.00 y caja $4,187.15, con saldos posteriores $23,330.00 y $12,762.85. 2. Si necesitas corregir, pulsa Editar distribución: conserva cuentas, importes, referencia y motivo; después exige Revisar distribución nuevamente. 3. Pulsa Confirmar y registrar pagos y revisa ambas fuentes en el modal; Cancelar lo cierra sin registrar dinero. 4. Confirma sólo una vez cuando la revisión sea correcta; no uses Atrás del navegador para cambiar datos.',
'La revisión no tiene campos editables. Cambiar captura o saldos invalida la confirmación anterior y el servidor exige revisar de nuevo.',
'Nuevo intento registrado sin modificar ni eliminar el intento cancelado.',
'Un modal no realiza una transferencia bancaria real; registra el pago verificado en el sistema. No volver a confirmar el caso ya concluido NPI-2.','/admin/preparacion-nomina'),
(8,'Comprobar el cierre económico y los dos intentos','Nóminas → Ver pago e intentos → Historial de intentos; Movimientos financieros y Cuentas financieras',
'1. En la nómina de julio consulta a Diana e Historial de intentos: NPI-1 debe conservarse como Pago cancelado y NPI-2 como el nuevo pago vigente. 2. En Movimientos financieros comprueba los dos nuevos EGRESOS NOMINA de NPI-2 del 28/10/2026 a las 16:41: banco $10,000.00 y caja $4,187.15, conservando los ingresos compensatorios de NPI-1. 3. En Cuentas financieras confirma banco $23,330.00, caja $12,762.85 y CAJA-CORTE-TEST $1,150.00, siempre considerando movimientos posteriores. 4. Conserva la evidencia y no repitas operaciones; el siguiente escenario de prueba será independiente.',
'Salida neta vigente del ciclo: $14,187.15. El pago original, su recuperación y el nuevo pago permanecen trazables.',
'Cancelación y nuevo pago confirmados por el usuario, sin duplicar la salida neta de dinero.',
'Esto no confirma pagos individuales sin borrador, falta de fondos, concurrencia ni decisiones No aplicar/Posponer. Esos escenarios requieren sus propios ensayos.','/admin/preparacion-nomina')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='nomina-cancelacion-y-nuevo-pago-confirmados';

-- Mantener la guía inicial al día sin modificar su migración ni su checksum.
UPDATE guia_proceso_paso
SET instrucciones='1. Vuelve a Empleados → Diana → Configuración de nómina y confirma importes y vigencias. 2. Distingue simulación actual de nómina histórica: simular no autoriza otro pago. 3. Conserva la evidencia del pago original y abre Guía de procesos → Nómina: cancelar un pago y volver a pagar desde sus cuentas para el escenario posterior ya confirmado. 4. Consulta ese recorrido sin repetir la cancelación de NPI-1 ni el pago vigente NPI-2.',
resultado='Pago ordinario confirmado; cancelación y nuevo pago documentados en su guía independiente.',
precaucion='No repetir julio. La guía posterior conserva la evidencia de recuperación y nuevo pago; otros escenarios siguen pendientes.'
WHERE numero=8 AND guia_id=(SELECT id FROM guia_proceso WHERE slug='nomina-colectiva-dos-cuentas-confirmada');
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-28'
WHERE slug='nomina-colectiva-dos-cuentas-confirmada';
