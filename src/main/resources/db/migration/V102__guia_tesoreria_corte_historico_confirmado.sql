-- Consulta histórica confirmada por el propietario el 16/10/2026.
-- Sólo contenido editorial: no modifica fechas, cargos, pagos, cuentas ni movimientos.
UPDATE guia_proceso SET
    resumen=$texto$Casos confirmados en pantalla, Excel y PDF: cuenta sin movimientos, pago cancelado, nuevo cobro vigente y Tesorería con fecha final anterior a ese cobro. Distinguir saldo actual, saldo al corte y flujos del periodo.$texto$,
    ejemplo=$texto$CAJA-SOBRANTE-TEST conserva $1,150.00 sin movimientos. En BBVA_COBROS, del 01/10/2026 al 15/10/2026: dos movimientos, ingresos y egresos $1,440.00 cada uno, neto $0.00 y saldo inicial/final $1,000.00. Al incluir el 16/10/2026: tres movimientos, ingresos $1,940.00, egresos $1,440.00 y cierre $1,500.00. El cobro de $500.00 del día 16 se excluye del primer rango.$texto$,
    estado='CONFIRMADA',version_contenido=version_contenido+1,revisada_el=DATE '2026-10-16'
WHERE slug='reportes-cuentas-tesoreria';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,
    '/admin/reportes-financieros/tesoreria'
FROM guia_proceso g JOIN (VALUES
(7,$texto$Consultar Tesorería antes del cobro del día 16$texto$,$texto$Tesorería → Filtros$texto$,
$texto$1. Entra a Administración → Finanzas → Reportes financieros → Tesorería. 2. Selecciona la institución del ensayo, busca BBVA_COBROS y elige su sugerencia; conserva Plantel de operación Todos y Agrupación MENSUAL. 3. Captura Desde 01/10/2026 y Hasta 15/10/2026, y pulsa Generar reporte. 4. Comprueba una fila mensual con dos movimientos, ingresos $1,440.00, egresos $1,440.00, neto $0.00 y traspasos de entrada/salida $0.00; no debe incluir el cobro de $500.00 del 16/10/2026.$texto$,
$texto$La fila mensual identifica octubre con el primer día del mes; no significa que ambos movimientos ocurrieran el 01/10/2026.$texto$,
$texto$Pantalla de Tesorería histórica confirmada por el propietario con el cobro posterior excluido.$texto$,
$texto$Modificar Hasta filtra la consulta, no cambia la fecha del pago ni el saldo actual. No registrar dinero, cancelar de nuevo ni cambiar fechas del sistema para reproducir el ejemplo.$texto$),
(8,$texto$Comparar el corte histórico en Excel y Balanza PDF$texto$,$texto$Tesorería → Exportaciones$texto$,
$texto$1. Con BBVA_COBROS y rango 01/10/2026–15/10/2026 ya generado, pulsa Exportar Excel y abre la descarga nueva. 2. Comprueba dos movimientos, ingresos operativos $1,440.00, egresos operativos $1,440.00, neto $0.00, traspasos $0.00 y moneda MXN. 3. Pulsa Balanza PDF: encabezado Del 01/10/2026 al 15/10/2026, saldo inicial $1,000.00, ingresos $1,440.00, egresos $1,440.00 y saldo final $1,000.00; su tabla debe coincidir con pantalla y Excel. 4. Distingue este corte histórico del rango hasta el 16/10/2026 ya confirmado, que incluye el nuevo ingreso de $500.00 y cierre $1,500.00; conserva los registros existentes sin repetir operaciones.$texto$,
$texto$Corte al 15/10: $1,000.00 + $1,440.00 - $1,440.00 = $1,000.00. Corte al 16/10: agrega $500.00 y cierra en $1,500.00.$texto$,
$texto$Consulta histórica confirmada en pantalla, Excel y PDF por el propietario el 16/10/2026.$texto$,
$texto$Esta aceptación corresponde a Tesorería y al rango indicado. No confirma aún el corte histórico del estado del alumno, del concentrado ni la reconstrucción de convenios/cancelaciones de cargos. Los archivos anteriores no se actualizan al cambiar filtros.$texto$)
) v(numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion) ON true
WHERE g.slug='reportes-cuentas-tesoreria';

UPDATE guia_proceso_paso SET precaucion=$texto$No cancelar de nuevo ni cambiar fechas/datos para forzar el antecedente. La consulta histórica de Tesorería hasta el 15/10/2026 ya fue confirmada en pantalla, Excel y PDF; seguir las etapas finales de esta misma guía.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='reportes-cuentas-tesoreria') AND numero=3;

UPDATE guia_proceso_paso SET precaucion=$texto$La aceptación de este Excel corresponde al alcance y corte del concentrado descritos. Tesorería hasta el 15/10/2026 también está confirmada en la guía Revisar cuentas y tesorería con un pago cancelado y otro vigente. No confundirla con aceptación de corte histórico del alumno o del concentrado, otros planteles, paginación masiva ni datos vacíos como pruebas humanas. No cambiar fechas de registros ni del sistema para probar cortes.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='reporte-concentrado-excel') AND numero=3;
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-16'
WHERE slug='reporte-concentrado-excel';
