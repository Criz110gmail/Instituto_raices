-- Aceptación humana del 16/10/2026. Sólo contenido de la guía; no operaciones financieras.
UPDATE guia_proceso SET
    estado='CONFIRMADA',
    resumen=$texto$Exportación confirmada por el propietario por concepto de cobro, plantel y ciclo escolar. Las hojas Resumen y Concentrado coinciden con la pantalla al corte del ensayo.$texto$,
    requisitos=$texto$Reportes financieros autorizado, institución y alcance del ensayo accesibles. Usar los registros de prueba existentes; no volver a cobrar ni crear cargos para reproducir cifras. El filtro confirmado fue Todos autorizados, no un plantel seleccionado individualmente. Los datos operativos no viajan mediante git pull.$texto$,
    ejemplo=$texto$Institución instituto Raices, Plantel Todos autorizados y corte 16/10/2026. Las tres agrupaciones conservan tres cargos, importe $2,100.00, cobrado $500.00, por cobrar $1,600.00, vencido $0.00 y moneda MXN. Concepto tiene dos filas; Plantel parian y Ciclo Escolar 2026-2027 una fila cada uno. Ambas hojas fueron contrastadas por el propietario.$texto$,
    version_contenido=version_contenido+1,revisada_el=DATE '2026-10-16'
WHERE slug='reporte-concentrado-excel';

UPDATE guia_proceso_paso p SET instrucciones=v.instrucciones,ejemplo=v.ejemplo,
    resultado=v.resultado,precaucion=v.precaucion
FROM guia_proceso g JOIN (VALUES
(1,
$texto$1. Entra a Administración → Finanzas → Reportes financieros → Concentrado de cobranza. 2. Selecciona la institución del ensayo, Plantel Todos autorizados, Agrupar por Concepto de cobro y fecha de corte 16/10/2026. 3. Pulsa Generar y comprueba dos filas: ANTICIPO-TEST-02 con dos cargos por $1,600.00 sin cobro, y MATERIAL con un cargo de $500.00 pagado. 4. Pulsa Exportar Excel y abre el archivo recién descargado, no una descarga de filtros anteriores.$texto$,
$texto$Dos conceptos: colegiatura con $1,600.00 por cobrar y material con saldo cero.$texto$,
$texto$Pantalla y descarga por concepto de cobro confirmadas por el propietario.$texto$,
$texto$No crear otro pago de $500.00 ni repetir los ajustes o la cancelación. Este reporte reúne todos los alumnos del alcance, no sólo la matrícula del ejemplo.$texto$),
(2,
$texto$1. Abre la hoja Resumen del Excel. 2. Comprueba corte 16/10/2026, institución instituto Raices, Plantel Todos autorizados y Agrupar por Concepto de cobro. 3. Contrasta tres cargos, importe $2,100.00, cobrado $500.00, por cobrar $1,600.00, vencido $0.00 y moneda MXN. 4. Abre Concentrado y verifica ANTICIPO-TEST-02: dos cargos, importe y por cobrar $1,600.00, cobrado y vencido $0.00; MATERIAL: un cargo, importe y cobrado $500.00, por cobrar y vencido $0.00.$texto$,
$texto$Resumen global y las dos filas de detalle coinciden con la pantalla.$texto$,
$texto$Ambas hojas por concepto confirmadas por el propietario con los mismos importes y corte.$texto$,
$texto$Plantel Todos autorizados describe el filtro, mientras Agrupar por describe la organización de las filas. No sumar Resumen y Concentrado entre sí: representan la misma cartera.$texto$),
(3,
$texto$1. Conserva institución, Plantel Todos autorizados y corte 16/10/2026; cambia Agrupar por a Plantel y pulsa Generar. 2. Descarga un Excel nuevo: Resumen debe indicar Agrupar por Plantel y Concentrado debe mostrar Plantel parian con tres cargos, $2,100.00 de importe, $500.00 cobrado, $1,600.00 por cobrar y $0.00 vencido. 3. Repite Generar y Exportar Excel con Ciclo escolar: Resumen debe indicar esa agrupación y Concentrado debe tener una fila Ciclo Escolar 2026-2027 con los mismos totales y MXN. 4. Compara ambos archivos con sus pantallas; el propietario confirmó las dos hojas de las tres agrupaciones, sin necesidad de registrar dinero adicional.$texto$,
$texto$Concepto: dos filas; plantel y ciclo: una fila cada uno, con totales iguales.$texto$,
$texto$Exportación Excel confirmada por concepto, plantel y ciclo escolar el 16/10/2026.$texto$,
$texto$La aceptación corresponde a este alcance y corte. No confirma todavía reconstrucción con un corte anterior al nuevo pago, otros planteles, paginación masiva ni datos vacíos como pruebas humanas. No cambiar fechas de registros ni del sistema para probar cortes.$texto$)
) v(numero,instrucciones,ejemplo,resultado,precaucion) ON true
WHERE p.guia_id=g.id AND g.slug='reporte-concentrado-excel' AND p.numero=v.numero;

UPDATE guia_proceso SET
    resumen=$texto$Agrupaciones confirmadas por el propietario en pantalla y PDF con los mismos cargos y corte. La exportación Excel de las tres agrupaciones también está confirmada en su guía Exportar el concentrado de cobranza a Excel.$texto$,
    version_contenido=version_contenido+1,revisada_el=DATE '2026-10-16'
WHERE slug='reporte-concentrado-agrupaciones';
UPDATE guia_proceso_paso SET precaucion=$texto$El Excel se incorporó después de la aceptación inicial del PDF. El propietario ya confirmó Resumen y Concentrado por concepto, plantel y ciclo; consultar su guía Exportar el concentrado de cobranza a Excel.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='reporte-concentrado-agrupaciones') AND numero=4;

UPDATE guia_proceso_paso SET precaucion=$texto$No existe PDF específico de corte de caja. Los reportes financieros tienen guías confirmadas, incluida la exportación Excel del concentrado por concepto, plantel y ciclo. Consultarlas sin repetir los movimientos.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='corte-caja-sobrante-1') AND numero=7;
UPDATE guia_proceso_paso SET precaucion=replace(precaucion,
$texto$los reportes financieros tienen guías confirmadas y el nuevo Excel del concentrado tiene su propio recorrido pendiente$texto$,
$texto$los reportes financieros y el Excel del concentrado tienen guías confirmadas$texto$)
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='corte-caja-faltante-1') AND numero=8;
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-16'
WHERE slug IN ('corte-caja-sobrante-1','corte-caja-faltante-1');
