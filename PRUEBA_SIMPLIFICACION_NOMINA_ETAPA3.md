# Revisión propuesta — nómina, etapa 3

24/10/2026. Pendiente de aceptación humana; no constituye prueba confirmada en la guía.
No repetir BOR-000001 aplicada ni su egreso de $12,500.00 (movimiento 10).

## Qué cambió

El calendario asignado al empleado ahora participa en la generación. Configuración
anual de sueldo y conceptos habituales se reutiliza cada mes según sus vigencias.
Sin calendario aparece revisión; otro calendario no se incluye. No se asignan
calendarios automáticamente a empleados existentes. La asignación sigue siendo
actual, no un historial de calendarios con vigencias futuras.

Configuración de nómina → empleado → Conceptos → Sólo un periodo permite registrar
bonos o deducciones fijas ocasionales. Catálogo: aplicación Ocasional. Listado,
filtros, Excel y alta separada; periodo y concepto con sugerencias10/3. El periodo
pertenece al calendario asignado y debe ser mensual completo, no terminado.
Tipo, moneda y cobertura se obtienen en servidor; no los decide el navegador.
Cada asignación conserva concepto e importe originales, motivo e historial.

Preparar/guardar no paga ni consume el concepto. Cancelarlo exige motivo y
confirmación; si un borrador lo incluía, actualizarlo explícitamente. Una asignación
incluida en la revisión aplicada no se cancela ni sobrescribe. Para corregir antes
de aplicar: cancelar y registrar de nuevo, conservando el historial.

## Ejemplo controlado, sin aplicar pagos

Usar un empleado de capacitación distinto del empleado de la nómina ya aplicada.
Elegir un año futuro y crear periodos mensuales completos mayo, junio y diciembre
del mismo calendario; asignarlo expresamente al empleado desde Configurar nómina.

1. Sueldo mensual $12,000.00 y bono habitual $500.00, vigentes todo ese año.
2. Deducción habitual $250.00 del 01/06 al 30/11 del mismo año. Es descuento fijo
   de capacitación, NO préstamo: aún no hay saldo ni amortizaciones automáticas.
3. Preparar sin aplicar: mayo $12,500.00, junio $12,250.00, diciembre $12,500.00.
4. Crear concepto de catálogo Ocasional/Percepción, asignarlo sólo a mayo por
   $1,000.00, motivo «Bono extraordinario de capacitación». Revisar modal;
   Cancelar no guarda, confirmar sí. Preparar mayo: $13,500.00; junio sin cambio.
5. Guardar borrador mayo, cancelar esa asignación con motivo. El borrador guardado
   conserva $13,500.00 y exige actualizar; una nueva revisión queda $12,500.00.
   La anterior sigue consultable. No aplicar esta prueba.
6. Comprobar filtros y Excel: fecha dd/MM/yyyy, importes numéricos con centavos,
   retorno al listado y configuración sin perder filtros/página.
7. Sin calendario debe indicar revisión; calendario diferente no se incluye.
   No cambiar configuraciones reales para probarlo. Una vigencia parcial dentro
   del mes exige revisión: no se inventa un prorrateo.

## Compatibilidad y límites

Borradores de reglas anteriores requieren actualización explícita; no recalcular
silenciosamente ni descartar empleados bloqueados para lograr aplicar. Nóminas
aplicadas conservan revisión, empleados, conceptos y movimiento originales.
Cambios de periodo/configuración después de guardar invalidan la propuesta vigente.
No cálculo semanal/quincenal/anual/proporcional/fiscal ni timbrado automático.
Préstamos con saldo (etapa4) y pagos/reversa individual (etapa5) siguen pendientes.

## Verificación técnica

997 pruebas Java de suite y 2 IT PostgreSQL17 aislados correctos; 118 JS correctos.
Incluyen calendario, vigencias de junio, ocasionales por periodo, dos capturas
concurrentes, revisión anterior inmutable, borrador legado, autorización/CSRF,
Excel paginado, aplicación ficticia y bloqueo posterior. Las cuatro pruebas unitarias
ejecutadas también en IT ya están contadas en997, no sumarlas nuevamente.
Vista ficticia sin escrituras: escritorio/móvil, autocompletados, contexto y modal.
V132 crea tablas de asignaciones e historial; no backfill ni concesión de permisos.
