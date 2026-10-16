-- Contenido editorial autorizado. No crea cargos, pagos, cuentas ni movimientos.
-- La exportación Excel nueva del concentrado tiene guía PENDIENTE independiente.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado) VALUES
('reportes-cuentas-tesoreria',$texto$Revisar cuentas y tesorería con un pago cancelado y otro vigente$texto$,'Finanzas',
$texto$Casos confirmados en pantalla, Excel y PDF: cuenta sin movimientos, pago con cancelación y un nuevo cobro vigente; distinguir saldos, flujos y agrupaciones.$texto$,
$texto$Administrador identificado con Reportes financieros, institución y cuentas autorizadas. Sólo consultar registros de prueba existentes; no crear ni repetir pagos para forzar cifras. Las cuentas y operaciones no viajan al hacer git pull. Los importes describen el ensayo local al 16/10/2026, no todas las cuentas de la institución.$texto$,
$texto$CAJA-SOBRANTE-TEST conserva $1,150.00 sin movimientos. BBVA_COBROS inició con $1,000.00; pago y cancelación por $1,440.00 se compensaron. Después de otro pago de $500.00: tres movimientos, ingresos $1,940.00, egresos $1,440.00, neto $500.00 y saldo $1,500.00.$texto$,1,DATE '2026-10-16','CONFIRMADA'),
('reporte-alumno-filtros',$texto$Revisar el estado de cuenta de un alumno y sus filtros$texto$,'Finanzas',
$texto$Caso confirmado en pantalla, Excel y PDF: tres cargos, un material pagado y dos colegiaturas pendientes; fechas completas y exportaciones que conservan situación.$texto$,
$texto$Reportes financieros autorizado y alumna de prueba ALUM-2026-002 accesible. Reutilizar el pago de material de $500.00 ya registrado; no cobrar otra vez. La cancelación del pago anterior ya existe. Los nombres, matrículas y cifras son referencias de prueba, no datos que se creen mediante esta guía.$texto$,
$texto$Material $500.00 pagado; noviembre y diciembre $800.00 cada uno, con original $1,000.00 y ajuste -$200.00. Total $2,100.00, aplicado $500.00 y saldo $1,600.00 al 16/10/2026.$texto$,1,DATE '2026-10-16','CONFIRMADA'),
('reporte-concentrado-agrupaciones',$texto$Revisar cobranza por concepto, plantel y ciclo escolar$texto$,'Finanzas',
$texto$Agrupaciones confirmadas por el propietario en pantalla y PDF con los mismos cargos y corte. La nueva exportación Excel tiene un recorrido pendiente separado.$texto$,
$texto$Reportes financieros autorizado, institución del ensayo y Plantel parian accesibles. Tres cargos existentes: material pagado y dos colegiaturas pendientes. Este reporte reúne todos los alumnos del alcance seleccionado, no filtra una sola matrícula. No crear ni modificar operaciones.$texto$,
$texto$Al 16/10/2026: tres cargos, importe $2,100.00, cobrado $500.00, por cobrar $1,600.00 y vencido $0.00. Concepto tiene dos filas; plantel y ciclo una fila cada uno en este ensayo.$texto$,1,DATE '2026-10-16','CONFIRMADA'),
('reporte-concentrado-excel',$texto$Exportar el concentrado de cobranza a Excel$texto$,'Finanzas',
$texto$Función nueva pendiente de aceptación humana. Contrastar Resumen y Concentrado con los filtros y cifras ya confirmados en pantalla y PDF.$texto$,
$texto$Versión con Exportar Excel desplegada, Reportes financieros autorizado y caso del concentrado existente. Esta guía no confirma todavía la nueva exportación por parte del propietario; sólo indica cómo verificarla. No registrar pagos ni cambiar el corte para producir resultados distintos.$texto$,
$texto$Mismo corte 16/10/2026 y Plantel parian: total de tres cargos por $2,100.00, cobrado $500.00 y pendiente $1,600.00. Excel debe abrir Resumen y conservar concepto, plantel o ciclo elegidos.$texto$,1,DATE '2026-10-16','PENDIENTE');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo='REPORTE_FINANCIERO_CONSULTAR'
WHERE g.slug IN ('reportes-cuentas-tesoreria','reporte-alumno-filtros','reporte-concentrado-agrupaciones','reporte-concentrado-excel');

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,v.ruta
FROM guia_proceso g JOIN (VALUES
('reportes-cuentas-tesoreria',1,$texto$Localizar las opciones de reporte$texto$,$texto$Reportes financieros$texto$,
$texto$1. Desde el menú lateral entra a Administración → Finanzas → Reportes financieros. 2. Identifica Selecciona un reporte y sus cuatro opciones; pulsa Estado de cuenta por cuenta, no Estado de cuenta del alumno. 3. Selecciona la institución de prueba autorizada y ubica el autocompletado de Cuenta financiera. 4. Mantén este ensayo sólo como consulta; si faltan los registros locales, no crees operaciones ni cambies saldos para reproducirlos.$texto$,
$texto$Tesorería, Estado de cuenta por cuenta, Estado de cuenta del alumno y Concentrado de cobranza.$texto$,
$texto$Navegación identificable y reporte correcto.$texto$,$texto$No confundir cuenta bancaria con saldo pendiente de un alumno. Usa únicamente el alcance que tu usuario tenga autorizado.$texto$,$texto$/admin/reportes-financieros/tesoreria$texto$),
('reportes-cuentas-tesoreria',2,$texto$Consultar una cuenta sin movimientos$texto$,$texto$Estado de cuenta por cuenta$texto$,
$texto$1. Busca y selecciona CAJA-SOBRANTE-TEST del autocompletado. 2. Selecciona Mensual, año 2026 y Octubre; pulsa Consultar. 3. Comprueba cero movimientos, ingresos y egresos $0.00 y cierre $1,150.00; apertura de octubre $0.00 porque la cuenta se inició dentro del periodo. 4. Lee Cuenta iniciada en este periodo y descarga Excel y PDF: deben conservar resumen y mensaje sin movimientos, no archivos vacíos.$texto$,
$texto$Saldo inicial registrado dentro de octubre: $1,150.00; no es ingreso por movimiento.$texto$,
$texto$Pantalla y ambas exportaciones sin movimientos confirmadas.$texto$,$texto$El sobrante de $1 del corte no produjo ingreso automático. No volver a abrir/cerrar ni registrar $1,150.00 como cobro.$texto$,$texto$/admin/reportes-financieros/estado-cuenta-cuenta$texto$),
('reportes-cuentas-tesoreria',3,$texto$Entender el pago cancelado de la cuenta bancaria$texto$,$texto$Estado de cuenta por cuenta$texto$,
$texto$1. Identifica BBVA_COBROS y su saldo de apertura de $1,000.00. 2. En el historial local reconoce Pago escolar PAG-20261009-000002 por $1,440.00 y Cancelación del mismo folio por $1,440.00. 3. Comprueba que son dos movimientos de un mismo pago, conservados como ingreso y egreso, no dos cobros vigentes. 4. Interpreta el antecedente confirmado antes del nuevo cobro: neto $0.00 y cierre $1,000.00; ahora que existe otro pago no esperes ese cierre en el mismo rango completo.$texto$,
$texto$Antes del nuevo pago: $1,000.00 + $1,440.00 - $1,440.00 = $1,000.00.$texto$,
$texto$Pago y cancelación trazables sin borrar movimientos.$texto$,$texto$No cancelar de nuevo ni cambiar fechas/datos para forzar el antecedente. Una prueba específica de reconstrucción histórica anterior al pago nuevo todavía no se ha confirmado.$texto$,$texto$/admin/reportes-financieros/estado-cuenta-cuenta$texto$),
('reportes-cuentas-tesoreria',4,$texto$Revisar el flujo diario después del cobro vigente$texto$,$texto$Tesorería$texto$,
$texto$1. Pulsa Tesorería y selecciona institución, BBVA_COBROS y Plantel de operación Todos. 2. Captura Desde 01/10/2026, Hasta 16/10/2026 y Agrupación DIARIA; pulsa Generar reporte. 3. Comprueba la fila del 09/10: dos movimientos, ingresos/egresos $1,440.00 y neto $0.00. 4. Comprueba la fila del 16/10: un movimiento de ingreso $500.00 y egresos $0.00; traspasos de ambas filas $0.00.$texto$,
$texto$Dos filas diarias que reúnen tres movimientos.$texto$,
$texto$Nuevo cobro de $500.00 distinguido del pago cancelado.$texto$,$texto$El nuevo pago ya fue registrado por el propietario. No cobrar otros $500.00 ni usar un filtro de plantel parcial para comparar saldos de la cuenta.$texto$,$texto$/admin/reportes-financieros/tesoreria$texto$),
('reportes-cuentas-tesoreria',5,$texto$Contrastar la agrupación mensual$texto$,$texto$Tesorería$texto$,
$texto$1. Conserva BBVA_COBROS, Todos y rango 01/10/2026–16/10/2026. 2. Cambia Agrupación a MENSUAL y pulsa Generar reporte; cambiar sólo el selector no vuelve a consultar. 3. Comprueba una fila de octubre con tres movimientos, ingresos $1,940.00, egresos $1,440.00 y neto $500.00. 4. Revisa apertura $1,000.00 y cierre $1,500.00, sin traspasos.$texto$,
$texto$La fecha del primer día identifica el mes agrupado, no la fecha de cada pago.$texto$,
$texto$Agrupación mensual confirmada sin duplicar movimientos.$texto$,$texto$Una fila mensual puede resumir varias operaciones. Cobrado del estado del alumno no equivale a sumar todos los ingresos históricos del banco.$texto$,$texto$/admin/reportes-financieros/tesoreria$texto$),
('reportes-cuentas-tesoreria',6,$texto$Comparar Excel y Balanza PDF$texto$,$texto$Tesorería → Exportaciones$texto$,
$texto$1. Con el resultado mensual generado pulsa Exportar Excel. 2. Pulsa Balanza PDF manteniendo cuenta y rango; el encabezado debe indicar Del 01/10/2026 al 16/10/2026. 3. Contrasta tres movimientos, ingresos $1,940.00, egresos $1,440.00, neto $500.00 y saldo final $1,500.00. 4. Comprueba traspasos $0.00 completos; en el ensayo el propietario confirmó que no estaban recortados.$texto$,
$texto$PDF: saldo inicial $1,000.00 y final $1,500.00.$texto$,
$texto$Pantalla, Excel y PDF de Tesorería coinciden.$texto$,$texto$No exigir Hasta 31/10 si se seleccionó 16/10. Los archivos descargados antes de cambiar filtros no se actualizan: descarga otros.$texto$,$texto$/admin/reportes-financieros/tesoreria$texto$),

('reporte-alumno-filtros',1,$texto$Seleccionar la alumna y el corte$texto$,$texto$Estado de cuenta del alumno$texto$,
$texto$1. En Reportes financieros pulsa Estado de cuenta del alumno. 2. Selecciona institución y busca la matrícula ALUM-2026-002 o el nombre de la alumna de prueba; elige la sugerencia. 3. Conserva planteles autorizados, Situación Todas y Fecha de corte 16/10/2026. 4. Pulsa Generar estado para consultar; no registres ni vuelvas a aplicar el pago del material.$texto$,
$texto$Alumna del ensayo con tres cargos existentes.$texto$,
$texto$Cartera individual al corte seleccionado.$texto$,$texto$El saldo bancario $1,500.00 no es la deuda del alumno. Este reporte no es un listado de todos los pagos de su tutor.$texto$,$texto$/admin/reportes-financieros/estado-cuenta$texto$),
('reporte-alumno-filtros',2,$texto$Contrastar importes, aplicaciones y fechas$texto$,$texto$Estado de cuenta del alumno → Todas$texto$,
$texto$1. Comprueba Material utilizado: original/total $500.00, aplicado $500.00 y saldo $0.00, PAGADO. 2. Revisa noviembre y diciembre: original $1,000.00 y ajustes -$200.00 por cargo, total $800.00, aplicado $0.00 y saldo $800.00, PENDIENTE. 3. Contrasta resumen: tres cargos, importe $2,100.00, aplicado $500.00 y pendiente $1,600.00. 4. Comprueba fechas completas de emisión 16/10/2026 o 10/10/2026 y vencimientos 20/10/2026, 20/11/2026, 20/12/2026; descarga Excel y PDF y compara esas cifras.$texto$,
$texto$El pago cancelado de $1,440.00 ya no reduce las dos colegiaturas.$texto$,
$texto$Importes y fechas dd/mm/yyyy confirmados en pantalla y exportaciones.$texto$,$texto$No eliminar cargos ni pagos para quitar el historial. Los dos ajustes -$200.00 siguen registrados; no volver a aplicarlos.$texto$,$texto$/admin/reportes-financieros/estado-cuenta$texto$),
('reporte-alumno-filtros',3,$texto$Filtrar únicamente adeudos pendientes$texto$,$texto$Estado de cuenta del alumno → PENDIENTE$texto$,
$texto$1. Mantén alumna y corte 16/10/2026. 2. Selecciona Situación PENDIENTE y pulsa Generar estado. 3. Comprueba que sólo aparecen noviembre y diciembre por $800.00 cada uno, sin abonos. 4. Verifica que no aparece Material utilizado y que la suma de pendientes es $1,600.00.$texto$,
$texto$Dos cargos pendientes; el pagado no aparece.$texto$,
$texto$Filtro PENDIENTE confirmado por el propietario.$texto$,$texto$Pendiente no significa vencido: noviembre y diciembre aún no vencen al corte de octubre.$texto$,$texto$/admin/reportes-financieros/estado-cuenta$texto$),
('reporte-alumno-filtros',4,$texto$Filtrar únicamente el cargo pagado$texto$,$texto$Estado de cuenta del alumno → PAGADO$texto$,
$texto$1. Mantén alumna y fecha de corte. 2. Cambia Situación a PAGADO y pulsa Generar estado. 3. Comprueba una sola fila: Material utilizado por $500.00, aplicado $500.00 y saldo $0.00. 4. Verifica que las dos colegiaturas pendientes ya no aparecen.$texto$,
$texto$Un cargo pagado con saldo cero.$texto$,
$texto$Filtro PAGADO confirmado por el propietario.$texto$,$texto$La consulta no debe efectuar otro cobro ni cambiar la situación de los cargos; sólo selecciona el historial visible.$texto$,$texto$/admin/reportes-financieros/estado-cuenta$texto$),
('reporte-alumno-filtros',5,$texto$Comprobar que las exportaciones respetan situación$texto$,$texto$Estado de cuenta del alumno → Excel / PDF$texto$,
$texto$1. Genera PAGADO y descarga Excel y PDF; ambos deben mostrar sólo material de $500.00 con saldo cero. 2. Cambia a PENDIENTE, pulsa Generar estado y descarga archivos nuevos. 3. Ambos deben mostrar sólo las dos colegiaturas de $800.00 cada una, saldo total $1,600.00, sin material. 4. Contrasta fecha de corte y fechas dd/mm/yyyy; el propietario confirmó que ambos archivos respetan los filtros.$texto$,
$texto$PAGADO: una fila. PENDIENTE: dos filas.$texto$,
$texto$Filtrado de pantalla conservado en Excel y PDF.$texto$,$texto$No reutilizar una descarga anterior como si reflejara el filtro nuevo. Corte histórico anterior al pago nuevo sigue siendo una prueba por acordar.$texto$,$texto$/admin/reportes-financieros/estado-cuenta$texto$),

('reporte-concentrado-agrupaciones',1,$texto$Agrupar los adeudos por concepto$texto$,$texto$Concentrado de cobranza → Concepto de cobro$texto$,
$texto$1. En Reportes financieros pulsa Concentrado de cobranza. 2. Selecciona institución, Plantel parian, Agrupar por Concepto de cobro y corte 16/10/2026; pulsa Generar. 3. Comprueba ANTICIPO-TEST-02 con dos cargos, importe/por cobrar $1,600.00 y cobrado/vencido $0.00. 4. Comprueba MATERIAL con un cargo de $500.00, cobrado $500.00 y por cobrar/vencido $0.00.$texto$,
$texto$Dos filas de concepto con tres cargos en total.$texto$,
$texto$Agrupación por concepto confirmada.$texto$,$texto$Si existen otros cargos del plantel, también deben incluirse; no filtra exclusivamente a la alumna del ejemplo.$texto$,$texto$/admin/reportes-financieros/concentrado-cobranza$texto$),
('reporte-concentrado-agrupaciones',2,$texto$Agrupar por plantel$texto$,$texto$Concentrado de cobranza → Plantel$texto$,
$texto$1. Mantén institución, Plantel parian y corte 16/10/2026. 2. Cambia Agrupar por a Plantel y pulsa Generar. 3. Revisa la fila Plantel parian: tres cargos, importe $2,100.00, cobrado $500.00 y por cobrar $1,600.00. 4. Comprueba vencido $0.00; los adeudos pendientes aún no vencen al corte.$texto$,
$texto$Una fila del plantel sin cambiar los totales.$texto$,
$texto$Agrupación por plantel confirmada.$texto$,$texto$Una fila agrupada no equivale a un solo cargo. No cambiar la fecha del sistema para probar vencimientos.$texto$,$texto$/admin/reportes-financieros/concentrado-cobranza$texto$),
('reporte-concentrado-agrupaciones',3,$texto$Agrupar por ciclo escolar$texto$,$texto$Concentrado de cobranza → Ciclo escolar$texto$,
$texto$1. Conserva institución, plantel y fecha de corte. 2. Selecciona Agrupar por Ciclo escolar y pulsa Generar. 3. Comprueba Ciclo Escolar 2026-2027 con tres cargos e importe $2,100.00. 4. Contrasta cobrado $500.00, por cobrar $1,600.00 y vencido $0.00, iguales a las otras agrupaciones.$texto$,
$texto$Un ciclo concentra los tres cargos del ensayo.$texto$,
$texto$Agrupación por ciclo escolar confirmada.$texto$,$texto$No sumar los resultados de concepto, plantel y ciclo entre sí: son tres vistas de la misma cartera.$texto$,$texto$/admin/reportes-financieros/concentrado-cobranza$texto$),
('reporte-concentrado-agrupaciones',4,$texto$Comparar PDF con las tres agrupaciones$texto$,$texto$Concentrado de cobranza → PDF$texto$,
$texto$1. Genera Concepto de cobro y abre PDF; debe conservar dos filas y el corte 16/10/2026. 2. Genera Plantel y abre otro PDF; debe mostrar la fila del plantel con los mismos totales. 3. Genera Ciclo escolar y abre otro PDF; encabezado y tabla deben indicar ese agrupamiento. 4. En los tres verifica tres cargos, importe $2,100.00, cobrado $500.00 y por cobrar $1,600.00; sus filas muestran vencido $0.00.$texto$,
$texto$Pantalla y PDF coinciden por concepto, plantel y ciclo.$texto$,
$texto$Tres agrupaciones y PDF confirmados por el propietario.$texto$,$texto$Excel no existía durante esa aceptación. La exportación recién agregada tiene guía PENDIENTE independiente; no presentarla como ya probada por el propietario.$texto$,$texto$/admin/reportes-financieros/concentrado-cobranza$texto$),

('reporte-concentrado-excel',1,$texto$Generar el concentrado con los filtros del ensayo$texto$,$texto$Concentrado de cobranza$texto$,
$texto$1. Recarga la pantalla después de actualizar la aplicación. 2. Selecciona institución, Plantel parian, Concepto de cobro y corte 16/10/2026. 3. Pulsa Generar y confirma las dos filas del caso existente y sus totales. 4. Identifica Exportar Excel junto a PDF y pulsa una sola vez para descargar.$texto$,
$texto$Tres cargos, importe $2,100.00, cobrado $500.00, pendiente $1,600.00.$texto$,
$texto$Descarga del nuevo archivo; pendiente de aceptación humana.$texto$,$texto$No crear otro pago 500 ni otros cargos. Si faltan registros, el archivo puede estar legítimamente vacío y debe explicarlo.$texto$,$texto$/admin/reportes-financieros/concentrado-cobranza$texto$),
('reporte-concentrado-excel',2,$texto$Contrastar las hojas Resumen y Concentrado$texto$,$texto$Concentrado de cobranza → Exportar Excel$texto$,
$texto$1. Abre el archivo y verifica que inicia en Resumen. 2. Contrasta institución, plantel, corte 16/10/2026 y agrupación Concepto de cobro. 3. Revisa totales tres/$2,100.00/$500.00/$1,600.00/vencido$0.00 y moneda MXN. 4. Abre Concentrado y comprueba las dos filas con importes numéricos y centavos; debe incluir todos los grupos filtrados, no sólo la página visible.$texto$,
$texto$Resumen global y detalle con el mismo alcance autorizado.$texto$,
$texto$Verificación esperada de datos, formato y cobertura de páginas.$texto$,$texto$No editar el archivo para forzar coincidencia ni atribuir aceptación humana hasta que el propietario lo compruebe.$texto$,$texto$/admin/reportes-financieros/concentrado-cobranza$texto$),
('reporte-concentrado-excel',3,$texto$Repetir para plantel y ciclo y registrar resultado$texto$,$texto$Concentrado de cobranza → Exportaciones$texto$,
$texto$1. Cambia Agrupar por a Plantel, pulsa Generar y descarga Excel nuevo. 2. Repite con Ciclo escolar, manteniendo institución, plantel y corte. 3. Cada archivo debe tener una fila del ensayo y conservar los mismos totales que pantalla y PDF. 4. Informa si los tres archivos coinciden para documentar aceptación humana en una actualización posterior.$texto$,
$texto$Concepto: dos filas; plantel/ciclo: una fila cada uno.$texto$,
$texto$Prueba manual nueva por realizar; no marcada confirmada.$texto$,$texto$Cambiar filtros no modifica archivos ya descargados. Corte histórico y otros alcances sin datos son pruebas adicionales, no confirmadas aquí.$texto$,$texto$/admin/reportes-financieros/concentrado-cobranza$texto$)
) v(slug,numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion,ruta) ON g.slug=v.slug;

-- Sustituir referencias antiguas de pendientes sin editar migraciones ya aplicadas.
UPDATE guia_proceso_paso SET precaucion=$texto$No existe PDF específico de corte de caja. Los reportes financieros ahora tienen guías de casos confirmados; la nueva exportación Excel del concentrado conserva una guía pendiente independiente.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='corte-caja-sobrante-1') AND numero=7;
UPDATE guia_proceso_paso SET instrucciones=replace(instrucciones,
$texto$Siguiente prueba propuesta: reportes financieros de esta misma cuenta en pantalla, Excel y PDF, empezando por su estado mensual sin movimientos.$texto$,
$texto$Continúa en la guía Revisar cuentas y tesorería con un pago cancelado y otro vigente: ya documenta el estado sin movimientos y las exportaciones confirmadas. No repetir operaciones.$texto$)
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='corte-caja-sobrante-1') AND numero=8;
UPDATE guia_proceso_paso SET precaucion=replace(precaucion,
$texto$los reportes financieros siguen pendientes$texto$,
$texto$los reportes financieros tienen guías confirmadas y el nuevo Excel del concentrado tiene su propio recorrido pendiente$texto$)
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='corte-caja-faltante-1') AND numero=8;
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-16'
WHERE slug IN ('corte-caja-sobrante-1','corte-caja-faltante-1');
