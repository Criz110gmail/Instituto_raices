# Préstamo compensado mediante nómina con neto cero

Estado: **CONFIRMADO por el usuario el 06/11/2026**, con los límites de evidencia
indicados al final. Excel complementario también confirmado por el usuario.

Disponible además en **Guía de procesos → Préstamos: entrega, liquidación y neto
cero**, pasos 9–13, versión de contenido 5. V159 editorial publicada el 06/11/2026;
no modifica préstamos, pagos, saldos ni permisos.

Este ejemplo ya se realizó. Los pasos conservan la captura utilizada para la guía;
**no recrear a Sofía, volver a entregar dinero ni repetir o cancelar el cierre**.
El usuario realizó las operaciones; el agente no hizo pagos ni capturas por herramientas.

## Resultado confirmado

| Dato | Resultado |
| --- | --- |
| Empleado | Sofía Prueba Neto Cero · EMP-PREST-CERO-01 |
| Préstamo | PRE-000002 |
| Principal | $100.00 |
| Interés | $0.00 |
| Programación | Versión 1 · ALTA · Quincenal · Una cuota |
| Periodo utilizado | NOV-2026-CIERRE-TEST · 16/11/2026 al 30/11/2026 |
| Cálculo del ejemplo | Percepción $100.00 − cuota $100.00 = neto $0.00 |
| Estado final | LIQUIDADO, capital e interés pendientes $0.00 |
| CAJA | $315.11 |
| BBVA_COBROS | $174.29 |
| CAJA-CORTE-TEST | $1,150.00 |

El folio de nómina no se aportó en la conversación: consultar el real en Nóminas,
sin inventar un BOR. Un cierre cero no genera pago NPI ni distribución por cuentas.

## 1. Preparación del empleado

En Empleados se creó EMP-PREST-CERO-01, Sofía Prueba Neto Cero, con actividad laboral
vigente e ingreso propuesto 06/11/2026. Sin sueldo base ni perfil docente requerido:
se usó una percepción extraordinaria para aislar el préstamo. No duplicar el empleado.

Antes del ejemplo: CAJA $415.11, BBVA $174.29 y caja de corte $1,150.00.
Tomás PRE-000001 ya estaba liquidado y no se modificó para esta prueba.

## 2. Programación sin entregar dinero

Personal y nómina → Préstamos → Nuevo préstamo:

| Campo | Captura usada en el ejemplo |
| --- | --- |
| Empleado | Sofía, seleccionada de las sugerencias |
| Deducción | PREST-TEST, existente |
| Importe que se prestará | $100.00 |
| Modalidad / porcentaje | Sin interés nuevo / 0 |
| Frecuencia | Quincenal · 15 y último día del mes |
| Número de cuotas | 1 |
| Primer descuento | 30/11/2026 |
| Descontar mediante nómina | Marcado |
| Motivo | Préstamo ficticio a Sofía para probar liquidación mediante nómina con neto cero. |

Revisar programación, comprobar capital $100.00/interés $0.00/total $100.00;
confirmar y guardar. La programación sola queda BORRADOR y no entrega dinero.
El procedimiento propuesto incluyó probar Cancelar en los modales; no se atribuye
aceptación individual de cada botón sin una confirmación específica del usuario.

## 3. Entrega original

En ese préstamo → Gestión → Entregar dinero al empleado:

| Campo | Captura propuesta para la prueba realizada |
| --- | --- |
| Cuenta | CAJA |
| Motivo financiero | OTROS_EGRESOS · Otros egresos |
| Referencia | ENTREGA-SOFIA-CERO-01 |
| Motivo | Entrega ficticia de 100 a Sofía para comprobar recuperación mediante neto cero. |
| Confirmación de recepción | Marcada |

Revisar y confirmar una sola entrega de $100.00: CAJA pasa de $415.11 a $315.11.
El préstamo queda ACTIVO y debe $100.00. El egreso original PRESTAMO se conserva.
Las referencias propuestas no se presentaron en evidencia posterior: no tratarlas
como folios verificados por consulta técnica.

## 4. Percepción única

Empleados → Sofía → Configuración de nómina → Conceptos configurados → Agregar:

| Campo | Captura del ejemplo |
| --- | --- |
| Concepto | EXTRA-RED-TEST · Apoyo extraordinario de prueba |
| Importe de referencia | $100.00 |
| Aplicación | Una sola vez, en un periodo |
| Periodo | NOV-2026-CIERRE-TEST |
| Fechas | 16/11/2026 al 30/11/2026 |
| Motivo propuesto | Percepción única de 100 para compensar la cuota del préstamo de Sofía y comprobar neto cero. |

No agregar PREST-TEST como deducción habitual ni ocasional: el préstamo aporta la
cuota automáticamente y una segunda captura duplicaría el descuento.

## 5. Cierre de nómina sin salida adicional

Nóminas → Pago individual → mismo periodo y Sofía → Consultar cálculo.
Percepción $100.00, descuento de préstamo $100.00, neto $0.00. Consultar no abona.

Confirmar neto cero del empleado → Motivo de la operación:

> Confirmación de neto cero de Sofía: percepción de 100 compensada con cuota de préstamo de 100, sin salida adicional de dinero.

Revisar neto cero → Confirmar neto cero y confirmar en el modal. No seleccionar
cuenta ni registrar pago ficticio de $0.00. La recuperación es compensación auxiliar
de préstamo contra nómina, no un ingreso bancario ni un asiento de contabilidad general.
El cierre cero es inmutable; no se ofrece cancelación.

Usuario confirmó los saldos finales y el préstamo liquidado. Historial compartido:

| Fecha / hora del 06/11/2026 | Estado | Evidencia |
| --- | --- | --- |
| 09:50 | BORRADOR | Creación de la programación |
| 09:51 | ACTIVO | Entrega registrada |
| 09:55 | LIQUIDADO | Compensación de préstamo en nómina con neto cero |

Programaciones conservadas muestra versión 1 ALTA, capital $100.00, interés anterior
$0.00 y nuevo $0.00, con actor y motivo. La tabla individual de cuotas no se pegó
por separado; no afirmar que se verificó visualmente cada columna de esa tabla.

## 6. Protección contra repetición

Al consultar otra vez Pago individual, mismo empleado y fechas, el usuario obtuvo:

> Sofía Prueba Neto Cero: Este empleado ya tiene un neto cero confirmado para fechas coincidentes. Consulta el detalle en Personal y nómina → Nóminas. No se registró otro cierre ni salida de dinero.

No repetir el cierre ni intentar eludir la validación con otra nómina.
«Sin movimientos» se refiere al cierre de neto cero, no a borrar ni omitir el
egreso previo de entrega del préstamo.

## 7. Filtros y Excel complementarios

Usuario confirmó «todo el excel pasó la prueba» después de este recorrido propuesto:

1. Sofía + LIQUIDADO: PRE-000002, principal $100.00 y pendientes $0.00, sin Tomás.
2. Mismo filtro exportado a Excel: sólo Sofía y datos coincidentes.
3. Sofía + ACTIVO: consulta y Excel sin registros, encabezados permitidos.
4. Limpiar + LIQUIDADO: Sofía y Tomás, sin duplicados; pendientes $0.00 ambos.
5. Excel de ambos liquidados: principal Sofía $100.00 y Tomás $200.00.
6. Búsqueda PRE-000002, entrar y volver al listado conservando búsqueda.
7. Consultar resumen, programaciones e historial sin cambiar deuda ni cuentas.

La aceptación explícita cubre las exportaciones; no extenderla a todas las
interacciones de navegación, paginación masiva o permisos sólo por ese comentario.
El historial y estados indicados arriba sí fueron compartidos expresamente.

## Pendientes y continuación

- Prueba humana de rol sólo Préstamos - Consultar: **CONFIRMADA el 06/11/2026**.
  Lectura y Excel permitidos; nuevo, editar y reprogramar denegados por URL.
  Ver [prueba de sólo consulta](PRUEBA_PRESTAMOS_SOLO_CONSULTA_20261106.md).
- Tabla individual de cuotas y navegación/retorno pueden verificarse adicionalmente
  sin nuevas operaciones financieras si se desea evidencia visual específica.
- Recibos y consultas integrados siguen sujetos al diseño acordado pendiente; este
  documento no los implementa ni acredita su aceptación.
- La guía integrada conserva a Tomás e incorpora a Sofía en pasos 9–13 (V159).
  V160 incorpora el cierre de permisos en el paso 14, sólo contenido editorial.
- Sesiones y expedientes del portal familiar conservan sus pendientes independientes.

No se ejecutaron operaciones sobre préstamos, nómina, roles o saldos al documentar.
