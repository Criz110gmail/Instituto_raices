-- Pruebas humanas confirmadas el 16/10/2026. Sólo contenido editorial.
-- No modifica cargos, pagos, aplicaciones, movimientos ni permisos.
UPDATE guia_proceso SET
    resumen=$texto$Casos confirmados en pantalla, Excel y PDF: estado del alumno al 16/10/2026, filtros PAGADO/PENDIENTE y consulta histórica al 15/10/2026 que excluye el material emitido al día siguiente.$texto$,
    ejemplo=$texto$ALUM-2026-002 al 15/10/2026, Situación Todas: dos colegiaturas de noviembre/diciembre por $800.00 cada una, importe y saldo $1,600.00, aplicado $0.00. Cada una conserva original $1,000.00 y ajuste -$200.00. El material emitido el 16/10 no aparece; al corte del 16 ya son tres cargos por $2,100.00 y aplicado $500.00.$texto$,
    estado='CONFIRMADA',version_contenido=version_contenido+1,revisada_el=DATE '2026-10-16'
WHERE slug='reporte-alumno-filtros';

UPDATE guia_proceso SET
    resumen=$texto$Agrupaciones confirmadas en pantalla, PDF y Excel al 16/10/2026. También se confirmó el corte histórico del 15/10/2026 por concepto de cobro: una fila de colegiaturas, sin el material emitido el día 16.$texto$,
    ejemplo=$texto$Institución instituto Raices, Plantel Todos autorizados y corte 15/10/2026, agrupado por Concepto de cobro: una fila ANTICIPO-TEST-02, dos cargos, importe $1,600.00, cobrado $0.00, por cobrar $1,600.00 y vencido $0.00. Coincide en pantalla, PDF y ambas hojas del Excel. Las tres agrupaciones al corte del 16 siguen confirmadas por separado.$texto$,
    estado='CONFIRMADA',version_contenido=version_contenido+1,revisada_el=DATE '2026-10-16'
WHERE slug='reporte-concentrado-agrupaciones';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,v.ruta
FROM guia_proceso g JOIN (VALUES
('reporte-alumno-filtros',6,$texto$Consultar el alumno antes de la emisión del material$texto$,$texto$Estado de cuenta del alumno → Corte histórico$texto$,
$texto$1. Entra a Administración → Finanzas → Reportes financieros → Estado de cuenta del alumno. 2. Selecciona la institución y el alcance autorizado del ensayo; busca ALUM-2026-002 y elige la sugerencia, no dejes sólo el nombre escrito. 3. Selecciona Situación Todas y Fecha de corte 15/10/2026; pulsa Generar estado. 4. Comprueba únicamente las colegiaturas de noviembre y diciembre: cada una con original $1,000.00, ajustes -$200.00, importe $800.00, aplicado $0.00, saldo $800.00 y situación PENDIENTE; el material emitido el 16/10 no debe aparecer.$texto$,
$texto$Dos cargos, importe $1,600.00, aplicado $0.00 y saldo pendiente $1,600.00.$texto$,
$texto$Pantalla histórica del alumno confirmada por el propietario.$texto$,
$texto$El corte se compara con la fecha de emisión y las fechas efectivas de ajustes/aplicaciones. No cambiar esas fechas, registrar otros pagos ni repetir cancelaciones para forzar el resultado.$texto$,
$texto$/admin/reportes-financieros/estado-cuenta$texto$),
('reporte-alumno-filtros',7,$texto$Contrastar Excel y PDF históricos del alumno$texto$,$texto$Estado de cuenta del alumno → Exportaciones$texto$,
$texto$1. Con Situación Todas y corte 15/10/2026 ya generado, pulsa Exportar Excel y abre la descarga nueva. 2. Comprueba dos filas de colegiatura con emisión 10/10/2026, vencimientos 20/11/2026 y 20/12/2026, total/saldo $800.00 cada una y aplicado $0.00. 3. Abre PDF y verifica matrícula ALUM-2026-002, Corte 15/10/2026, dos cargos, importe $1,600.00, pagado $0.00 y saldo pendiente $1,600.00. 4. Contrasta pantalla y ambos archivos: sólo noviembre/diciembre, sin material, con fechas dd/mm/yyyy y los mismos importes.$texto$,
$texto$Pantalla, Excel y PDF conservan el corte histórico y no incluyen el material del día 16.$texto$,
$texto$Las tres salidas del estado histórico del alumno fueron confirmadas por el propietario el 16/10/2026.$texto$,
$texto$Esta aceptación cubre el ensayo descrito. No demuestra reconstrucción de cambios históricos de convenios o cancelaciones de cargos, otros alumnos ni permisos restringidos. No reutilizar un archivo descargado con corte del 16.$texto$,
$texto$/admin/reportes-financieros/estado-cuenta$texto$),
('reporte-concentrado-agrupaciones',5,$texto$Consultar cobranza antes de emitir el material$texto$,$texto$Concentrado de cobranza → Corte histórico$texto$,
$texto$1. Entra a Administración → Finanzas → Reportes financieros → Concentrado de cobranza. 2. Selecciona la institución del ensayo, Plantel Todos autorizados, Agrupar por Concepto de cobro y Fecha de corte 15/10/2026. 3. Pulsa Generar y comprueba una sola fila ANTICIPO-TEST-02 con dos cargos, importe $1,600.00, cobrado $0.00, por cobrar $1,600.00 y vencido $0.00. 4. Verifica que MATERIAL no aparece: su cargo fue emitido el día 16 y queda fuera del corte anterior.$texto$,
$texto$Una fila de concepto reúne dos cargos pendientes por $800.00 cada uno.$texto$,
$texto$Concentrado histórico por concepto confirmado en pantalla por el propietario.$texto$,
$texto$Todos autorizados es el filtro de plantel utilizado. El concentrado reúne todos los alumnos de ese alcance, no únicamente la matrícula del ensayo. Si hay otros cargos válidos también deben incluirse; no borrarlos para obtener estas cifras.$texto$,
$texto$/admin/reportes-financieros/concentrado-cobranza$texto$),
('reporte-concentrado-agrupaciones',6,$texto$Comparar las dos hojas Excel y el PDF históricos$texto$,$texto$Concentrado de cobranza → Exportaciones$texto$,
$texto$1. Con Concepto de cobro y corte 15/10/2026 generado, pulsa Exportar Excel y abre el archivo nuevo. 2. En Resumen verifica la institución, Plantel Todos autorizados, Concepto de cobro, corte 15/10/2026, dos cargos, importe $1,600.00, cobrado $0.00, por cobrar $1,600.00, vencido $0.00 y MXN. 3. En Concentrado comprueba una fila ANTICIPO-TEST-02 con esos mismos valores y sin MATERIAL. 4. Abre PDF y comprueba Agrupado por concepto de cobro, Corte 15/10/2026, dos cargos y los mismos importes de pantalla y Excel.$texto$,
$texto$Pantalla, PDF y ambas hojas del Excel coinciden y excluyen el material del día 16.$texto$,
$texto$Corte histórico del concentrado por concepto confirmado por el propietario el 16/10/2026.$texto$,
$texto$No sumar las hojas entre sí ni registrar dinero para repetir la consulta. El corte histórico por plantel/ciclo, datos vacíos, permisos restringidos y grandes volúmenes no fueron aceptados como pruebas humanas en este ensayo.$texto$,
$texto$/admin/reportes-financieros/concentrado-cobranza$texto$)
) v(slug,numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion,ruta) ON g.slug=v.slug;

UPDATE guia_proceso_paso SET precaucion=$texto$No reutilizar descargas anteriores como si reflejaran filtros nuevos. El estado histórico del alumno al 15/10/2026 ya fue confirmado en pantalla, Excel y PDF; ver las etapas finales de esta guía.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='reporte-alumno-filtros') AND numero=5;
UPDATE guia_proceso_paso SET precaucion=$texto$La consulta de Tesorería histórica está confirmada. También se confirmaron el estado del alumno y el concentrado por concepto al 15/10/2026 en sus guías respectivas. No extender esta aceptación a la reconstrucción histórica de convenios/cancelaciones de cargos. Los archivos anteriores no se actualizan al cambiar filtros.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='reportes-cuentas-tesoreria') AND numero=8;
UPDATE guia_proceso_paso SET precaucion=$texto$Además del Excel de las tres agrupaciones al 16/10/2026, el propietario confirmó ambas hojas por Concepto de cobro al 15/10/2026; consultar las etapas finales de Revisar cobranza por concepto, plantel y ciclo escolar. No implica aceptación humana de corte histórico por plantel/ciclo, otros alcances, datos vacíos o grandes volúmenes.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='reporte-concentrado-excel') AND numero=3;
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-16'
WHERE slug IN ('reportes-cuentas-tesoreria','reporte-concentrado-excel');
