-- Aceptación humana del 06/11: sólo contenido de la guía existente, sin operaciones.
UPDATE guia_proceso SET
 titulo='Préstamos: entrega, liquidación y neto cero',
 resumen='Casos confirmados: Tomás recuperó $220 de un préstamo de $200 con interés y liquidación anticipada. Sofía liquidó $100 mediante una nómina con neto cero, sin salida adicional de dinero. Historial y exportaciones Excel comprobados.',
 requisitos=replace(requisitos,
 'El caso de esta base ya terminó: consultar PRE-000001, BOR-000017 y BOR-000018. No repetir capturas, entregas, pagos ni cancelaciones. Neto cero con préstamo y comprobaciones manuales complementarias se prueban por separado.',
 'Los ejemplos ya terminaron: Tomás PRE-000001 y Sofía PRE-000002. Consultar sus registros; no repetir capturas, entregas, pagos ni cierres. Excel confirmado el 06/11/2026. La prueba manual de rol sólo consulta sigue pendiente; esta guía no concede permisos.'),
 ejemplo=ejemplo||' Caso adicional confirmado el 06/11/2026: Sofía Prueba Neto Cero, EMP-PREST-CERO-01, PRE-000002. Una entrega de $100 desde CAJA, sin intereses; una cuota de $100 el 30/11/2026. En NOV-2026-CIERRE-TEST, percepción única $100 menos cuota $100: neto $0.00. La compensación dejó el préstamo LIQUIDADO, capital e interés pendientes $0.00, sin NPI ni salida adicional al cerrar nómina. CAJA final $315.11; BBVA_COBROS $174.29 y CAJA-CORTE-TEST $1,150.00, sin cambios. Repetir el cálculo informa que ya existe un neto cero confirmado para fechas coincidentes. El egreso original de entrega se conserva. Exportaciones Excel aceptadas por el usuario.',
 version_contenido=version_contenido+1,revisada_el=DATE '2026-11-06'
 WHERE slug='prestamos-programados-nomina';

UPDATE guia_proceso_paso s SET
 precaucion='No repetir el ejemplo de Tomás ni ajustar saldos iniciales. Los saldos de este paso son históricos del 05/11/2026; después de la entrega de Sofía, CAJA quedó en $315.11. El caso de préstamo con neto cero está confirmado y se consulta en los pasos siguientes; no es contabilidad general.'
 FROM guia_proceso g WHERE s.guia_id=g.id AND g.slug='prestamos-programados-nomina' AND s.numero=8;

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(9,'Sofía: consultar el préstamo sin intereses','Personal y nómina → Préstamos → Ver',
'1. Este caso ya terminó: busca PRE-000002 o selecciona a Sofía desde las sugerencias, sin crear otro empleado. 2. En Resumen identifica EMP-PREST-CERO-01 y principal $100.00, sin intereses. 3. En Historial consulta la programación Versión 1, ALTA, Quincenal, una cuota prevista para el 30/11/2026. 4. Conserva el ejemplo anterior de Tomás como consulta independiente.',
'Sofía Prueba Neto Cero: PREST-TEST, capital $100.00, interés $0.00, una cuota de $100.00. La programación se guardó primero como BORRADOR, sin entregar dinero.',
'Condiciones originales conservadas, sin duplicar préstamo ni configuración.',
'El caso está LIQUIDADO. No volver a entregar, reprogramar ni agregar PREST-TEST como otra deducción habitual: la cuota ya la aporta el préstamo.','/admin/prestamos'),
(10,'Distinguir la entrega del cierre sin dinero','Préstamos → Ver → Resumen e Historial; Finanzas',
'1. Consulta la entrega original de $100.00 desde CAJA. 2. Esa entrega dejó el préstamo ACTIVO y redujo CAJA de $415.11 a $315.11. 3. Conserva el egreso PRESTAMO original: confirmar la nómina de neto cero no lo borra ni crea otra salida. 4. No registrar un ingreso bancario ficticio para representar la recuperación por nómina.',
'La entrega fue registrada el 06/11/2026 a las 09:51. Es una salida de $100.00, no un pago de nómina adicional.',
'Entrega y recuperación se entienden como operaciones distintas.',
'Los saldos son evidencia histórica, no valores para editar cuentas. No repetir la entrega para comprobar el diseño.','/admin/prestamos'),
(11,'Consultar el cierre de Sofía con neto cero','Personal y nómina → Nóminas → Pago individual',
'1. Consulta el detalle histórico de la nómina de Sofía para NOV-2026-CIERRE-TEST, del 16 al 30/11/2026. 2. El ejemplo utilizó EXTRA-RED-TEST por $100.00, Una sola vez en ese periodo; no recapturarlo. 3. El cálculo fue sueldo base $0.00, percepción $100.00 y cuota $100.00: total a pagar $0.00. 4. El procedimiento realizado fue Revisar neto cero, revisar el desglose y la justificación y Confirmar neto cero en el modal, sin cuenta ni distribución de dinero. 5. Consulta el cierre guardado; no intentar confirmarlo otra vez.',
'Motivo propuesto: Confirmación de neto cero de Sofía: percepción de 100 compensada con cuota de préstamo de 100, sin salida adicional de dinero.',
'Cierre de nómina y compensación auxiliar del préstamo, sin NPI ni egreso de cero.',
'No es un asiento de contabilidad general. El cierre cero es inmutable y no tiene cancelación. Si se ensaya otro caso futuro, usar datos nuevos autorizados; no reutilizar este cierre.','/admin/preparacion-nomina'),
(12,'Comprobar liquidación y bloqueo de repetición','Préstamos → Ver → Resumen e Historial; Nóminas',
'1. En PRE-000002 verifica LIQUIDADO, capital pendiente $0.00, interés pendiente $0.00 y total pendiente $0.00. 2. En Gestiones del préstamo conserva BORRADOR del 06/11/2026 a las 09:50, ACTIVO a las 09:51 y LIQUIDADO a las 09:55, con motivo Compensación de préstamo en nómina con neto cero. 3. El usuario comprobó que consultar otra vez Pago individual, Sofía y las mismas fechas, bloquea un segundo cierre. 4. Conserva los registros de Tomás y Sofía para consulta, sin repetir ninguna operación.',
'Mensaje confirmado: Este empleado ya tiene un neto cero confirmado para fechas coincidentes. No se registró otro cierre ni salida de dinero. CAJA $315.11, BBVA_COBROS $174.29 y CAJA-CORTE-TEST $1,150.00.',
'Préstamo liquidado y duplicado rechazado, sin salida adicional por el cierre.',
'Una fecha vencida no liquida la deuda. La recuperación de este caso está respaldada por el cierre y su historial, no sólo por la fecha programada.','/admin/prestamos'),
(13,'Consultar filtros y Excel de préstamos','Personal y nómina → Préstamos',
'1. Filtra Sofía y LIQUIDADO: consulta y exporta PRE-000002, principal $100.00 y pendientes $0.00. 2. Cambia a Sofía y ACTIVO: no debe incluir préstamos en la consulta ni en Excel, aunque el archivo conserve encabezados. 3. Limpia el empleado y filtra LIQUIDADO: en la base del ejemplo aparecen Sofía y Tomás, sin duplicados. 4. Exporta ambos y compara principal $100.00 y $200.00, con pendientes cero. 5. La búsqueda PRE-000002 permite localizar a Sofía para consultar Resumen e Historial, sin nuevas capturas.',
'El usuario confirmó el 06/11/2026 que todas las exportaciones Excel propuestas pasaron la prueba. PRE-000001 y PRE-000002 siguen LIQUIDADOS.',
'Exportaciones aceptadas por el usuario, con información correspondiente a los filtros.',
'No generar pagos para probar exportaciones. Esta aceptación no acredita permisos de rol sólo consulta, concurrencia ni paginación masiva; esas comprobaciones son independientes.','/admin/prestamos')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='prestamos-programados-nomina';
