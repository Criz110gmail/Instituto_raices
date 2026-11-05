-- Aceptación humana del caso positivo; sólo contenido editorial compartido.
UPDATE guia_proceso SET estado='CONFIRMADA',
 titulo='Préstamos: entrega, cancelación y liquidación anticipada',
 resumen='Caso confirmado de Tomás: se prestaron $200 y se recuperaron $220, incluidos $20 de interés total. Cancelar el primer pago restauró la deuda; el repago y la liquidación anticipada conservaron todo el historial.',
 requisitos=requisitos||' El caso de esta base ya terminó: consultar PRE-000001, BOR-000017 y BOR-000018. No repetir capturas, entregas, pagos ni cancelaciones. Neto cero con préstamo y comprobaciones manuales complementarias se prueban por separado.',
 ejemplo='Confirmado por el usuario el 05/11/2026. Tomás Prueba Préstamos, EMP-PREST-TEST-01, PRE-000001: una entrega de $200 desde CAJA y cuatro cuotas iniciales de $55 (capital total $200 + interés total $20). BOR-000017, NOV-2026-CIERRE-TEST: percepción $500 menos cuota $55 = neto $445; NPI-19 cancelado, NPI-20 pagado. La cancelación restauró CAJA a $995.11 y deuda a $220; el repago dejó CAJA $550.11 y deuda $165. Versión 2 conserva capital $150 e interés anterior $15, sin interés nuevo; una cuota $165 el 15/12/2026. BOR-000018, DIC-2026-FONDOS-TEST: percepción $300 menos liquidación $165 = neto $135, NPI-21 pagado. Préstamo LIQUIDADO con capital/interés/saldo $0. CAJA $415.11; BBVA_COBROS $174.29 y CAJA-CORTE-TEST $1,150.00, sin cambios. Ambas programaciones y el intento cancelado se conservan.',
 version_contenido=version_contenido+1,revisada_el=DATE '2026-11-05'
 WHERE slug='prestamos-programados-nomina';

-- No presentar como aceptado el caso neto cero, que no pertenece a esta prueba humana.
UPDATE guia_proceso_paso s SET titulo='Liquidar el saldo y comprobar el cierre',
 modulo='Nóminas → Pago individual; Préstamos → Ver → Resumen',
 instrucciones='1. Este ejemplo ya fue realizado; consulta sus registros sin repetir pagos. 2. La configuración de Tomás en DIC-2026-FONDOS-TEST incluyó EXTRA-RED-TEST por $300, una sola vez, del 01 al 15/12/2026. 3. Pago individual calculó $300 menos cuota reprogramada $165: neto $135. Se revisó la distribución, se probó Cancelar y después se confirmó desde CAJA. 4. Consulta BOR-000018 y su pago NPI-21; en Préstamos verifica PRE-000001 Liquidado con capital, interés y total pendientes en cero. 5. En Historial conserva la entrega original, ambas programaciones, el intento cancelado y los abonos vigentes.',
 ejemplo='Primera nómina: abono vigente $50 de capital + $5 de interés. Última nómina: $150 de capital + $15 de interés. Recuperación total $220; una sola entrega original de $200. CAJA final $415.11, BBVA $174.29 y caja de corte $1,150.00.',
 resultado='Prueba principal aprobada: préstamo liquidado, sin interés duplicado, nueva entrega ni pérdida de historial.',
 precaucion='No repetir este ejemplo ni ajustar saldos iniciales para hacerlo coincidir. El préstamo con neto cero queda como prueba manual independiente; no confundir compensación auxiliar con contabilidad general.',
 ruta='/admin/prestamos'
 FROM guia_proceso g WHERE s.guia_id=g.id AND g.slug='prestamos-programados-nomina' AND s.numero=8;
