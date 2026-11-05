# Prueba de préstamos y nómina

Estado: **CASO PRINCIPAL CONFIRMADO por el usuario el 05/11/2026**.

Tomás completó entrega, primera cuota, cancelación y repago, reprogramación y
liquidación anticipada. PRE-000001 está LIQUIDADO con capital e interés en $0.
CAJA terminó en $415.11, BBVA en $174.29 y caja de corte en $1,150.00.

**Este ejemplo ya se realizó: consulta su historial, no repitas capturas, entregas
ni pagos.** Los pasos siguientes conservan el procedimiento que se probó.
El agente sólo verificó por lectura; las operaciones las confirmó el usuario.

## Resultado guardado y confirmado

| Operación | Evidencia |
| --- | --- |
| Préstamo | PRE-000001 · Tomás · EMP-PREST-TEST-01 |
| Entrega | Una sola entrega de $200 desde CAJA |
| Primera programación | Capital $200 + interés total $20; cuatro cuotas de $55 |
| Nómina de noviembre | BOR-000017 · NOV-2026-CIERRE-TEST |
| Primer pago | NPI-19 por $445, cancelado; restauró la cuota $55 y CAJA |
| Repago | NPI-20 por $445, vigente; abonó capital $50 + interés $5 |
| Nueva programación | Versión 2: capital restante $150 + interés anterior $15, sin interés nuevo; una cuota $165 el 15/12/2026 |
| Nómina de diciembre | BOR-000018 · DIC-2026-FONDOS-TEST |
| Pago final | NPI-21 por $135, vigente; abonó capital $150 + interés $15 |
| Cierre | LIQUIDADO; capital $0, interés $0, total pendiente $0 |

En palabras simples: se prestaron $200 y se recuperaron $220, incluidos $20 de
interés total. Cancelar el primer pago no perdió el historial ni dejó un descuento
duplicado. Adelantar la liquidación no entregó más dinero ni volvió a cobrar interés.

Los filtros/Excel y el rol de sólo consulta del apartado 11 son comprobaciones
complementarias; no se marcan aceptadas manualmente sin confirmación específica.
También queda separado el caso especial de préstamo con neto cero en nómina.

## Qué vamos a comprobar

Prestaremos $200 a Tomás. Con 10% de interés **total**, debe devolver $220.
Programaremos cuatro descuentos de $55. Pagaremos su primera nómina, cancelaremos
ese pago para comprobar que vuelve la deuda y después lo pagaremos otra vez.
Por último adelantaremos los $165 restantes a una sola quincena.

No es 10% cada mes. Los $20 de interés se calculan una sola vez.

## 1. Usuario y datos de esta computadora

Usa tu administrador con alcance de toda la institución. En **Roles** hay sólo
dos permisos de préstamos:

- **Préstamos - Consultar**.
- **Préstamos - Administrar**.

Consultar permite ver, filtrar y exportar. **Administrar incluye consulta, crear,
editar, cancelar borradores, entregar y reprogramar**. Para hacer este ejemplo
necesitas Administrar; marcar ambos también es válido. Si ya tenías los cuatro
permisos anteriores, se conserva Administrar y desaparecen Entregar/Reestructurar.

Para el ejemplo completo conserva también Empleados, Datos laborales de empleados,
Configuración de nómina, Puestos y condiciones salariales, Conceptos de nómina,
Periodos de nómina, Nóminas y permisos de cuentas/registro y reversa financieros.
Cierra sesión y vuelve a entrar. No se concede administración a lectores ni a
roles sin ese permiso. Entregar sigue requiriendo registro financiero y cuenta autorizada.

Lectura del 04/11/2026:

| Dato | Valor inicial |
| --- | --- |
| CAJA | **$1,195.11** |
| BBVA_COBROS | **$174.29** |
| CAJA-CORTE-TEST | **$1,150.00** |
| Préstamos | Ninguno |
| Periodo de primera prueba | **NOV-2026-CIERRE-TEST**, 16–30/11/2026 |
| Periodo de liquidación adelantada | **DIC-2026-FONDOS-TEST**, 01–15/12/2026 |
| Percepción existente | **EXTRA-RED-TEST · Apoyo extraordinario de prueba** |

Si ya hiciste operaciones nuevas, anota los saldos actuales: no cambies saldos
iniciales para hacerlos coincidir. Si Tomás ya existe, consulta lo guardado antes
de repetir algo. No canceles ni pagues otra vez nóminas de otros ejemplos.

## 2. Crear el concepto del préstamo

En **Personal y nómina → Conceptos de nómina → Nuevo concepto**:

| Campo | Valor |
| --- | --- |
| Código | **PREST-TEST** |
| Nombre | Recuperación de préstamo de prueba |
| Tipo | **Deducción** |
| Aplicación | **Con saldo** |
| Descripción | Recuperación automática del préstamo de prueba; no duplicar como deducción habitual. |
| Estado | Activo |

Revisa, pulsa Cancelar dentro del modal y comprueba que no se guardó. Vuelve a
revisar y confirma. Si el código ya existe, usa ese registro activo, no otro igual.

## 3. Crear a Tomás

En **Empleados → Nuevo empleado**:

| Campo | Valor |
| --- | --- |
| Código laboral | **EMP-PREST-TEST-01** |
| Nombre(s) | Tomás |
| Primer apellido | Prueba |
| Segundo apellido | Préstamos |
| Correo | tomas.prestamo@example.invalid |

Guarda. En **Datos laborales**, confirma actividad laboral con ingreso
**04/11/2026**, sin baja. Guarda y confirma. No necesita usuario ni perfil docente.
No asignes puesto: el ejemplo usa una percepción extraordinaria para aislar el
préstamo. El aviso Sin sueldo base asignado es correcto en esta prueba.

## 4. Programar sin entregar dinero

En **Personal y nómina → Préstamos → Nuevo préstamo**:

| Campo | Valor |
| --- | --- |
| Empleado | Tomás Prueba Préstamos, seleccionado de las sugerencias |
| Deducción del préstamo | PREST-TEST |
| Importe que se prestará | **200.00** |
| Modalidad | **Porcentaje total · No mensual** |
| Porcentaje total nuevo | **10** |
| Frecuencia | **Quincenal · 15 y último día del mes** |
| Número de cuotas | **4** |
| Primer descuento | **30/11/2026** |
| Descontar mediante nómina | Marcado |
| Motivo obligatorio | Préstamo ficticio de 200, interés total 10%, recuperación en cuatro quincenas. |

Pulsa **Revisar programación**. Debe mostrar capital $200, interés nuevo $20 y
total $220, distribuidos así:

| Cuota | Fecha | Capital | Interés | Total |
| --- | --- | --- | --- | --- |
| 1 | 30/11/2026 | $50 | $5 | **$55** |
| 2 | 15/12/2026 | $50 | $5 | **$55** |
| 3 | 31/12/2026 | $50 | $5 | **$55** |
| 4 | 15/01/2027 | $50 | $5 | **$55** |

Pulsa **Confirmar y guardar programación**. Primero Cancelar dentro del modal;
después vuelve a abrirlo y confirma una sola vez.

Debe quedar **BORRADOR**. En Resumen el **saldo pendiente real es $0** porque
todavía no se entregaron los $200. Las cuotas son la propuesta. Las cuentas no
cambian y no hay movimiento financiero. Anota el folio PRE real.

## 5. Entregar los $200

En el detalle del mismo préstamo → **Gestión → Entregar dinero al empleado**.
Dentro de Gestión verás tres secciones: Borrador del préstamo, Entregar dinero al
empleado y Cancelar borrador. Abre la segunda para continuar, sin crear otro préstamo:

| Campo | Valor |
| --- | --- |
| Cuenta de origen | **CAJA**, seleccionada de las sugerencias |
| Motivo financiero de egreso | **OTROS_EGRESOS · Otros egresos** |
| Referencia | **ENTREGA-PREST-TOMAS-01** |
| Motivo | Entrega ficticia del principal de 200 a Tomás para probar recuperación por nómina. |
| Confirmo que el empleado recibió el dinero prestado | Marcado |

Pulsa **Revisar entrega**. Debe mostrar principal $200 y saldo posterior de CAJA
**$995.11**. No entregas $220: el interés se recuperará después.

Pulsa **Confirmar y registrar entrega**, prueba Cancelar en el modal y verifica
que las cuentas no cambiaron. Luego confirma una sola vez.

Resultado: **ACTIVO**, capital pendiente $200, interés pendiente $20, total $220.
CAJA queda en **$995.11**. Movimientos financieros tiene un egreso PRESTAMO de
$200; consultar otra vez no crea una segunda entrega. No uses BBVA para entregar
$200: su saldo actual de $174.29 no alcanza.

## 6. Configurar la percepción de noviembre

**Empleados → Tomás → Configuración de nómina → abrir configuración →
Conceptos configurados → Agregar concepto**:

| Campo | Valor |
| --- | --- |
| Percepción o deducción | EXTRA-RED-TEST |
| Importe de referencia | **500.00** |
| ¿Cuándo se aplica? | **Una sola vez, en un periodo** |
| Periodo de aplicación | **NOV-2026-CIERRE-TEST** |
| Fechas | 16/11/2026–30/11/2026, completadas por el periodo |
| Motivo obligatorio | Apoyo único de Tomás para comprobar el descuento del préstamo en noviembre. |

Revisa y confirma. **No agregues PREST-TEST en esta tabla**: el préstamo ya aporta
su cuota automáticamente; hacerlo otra vez duplicaría una deducción.

## 7. Consultar y pagar la primera cuota

Ve a **Personal y nómina → Nóminas → Pago individual**. Selecciona
**NOV-2026-CIERRE-TEST** y a **Tomás**, luego **Consultar cálculo**.

| Partida | Importe |
| --- | --- |
| Sueldo base | $0.00 |
| Apoyo extraordinario | $500.00 |
| Cuota de préstamo | − $55.00 |
| **Total a pagar** | **$445.00** |

Consultar todavía debe dejar la deuda en $220. Captura una sola fuente **CAJA por
$445**, motivo financiero **NOMINA · Nómina**, referencia **NOM-PREST-TOMAS-NOV-01**
y motivo `Pago de noviembre de Tomás con primera cuota de préstamo de 55.`

Pulsa **Revisar distribución**. Revisa los datos protegidos, abre la confirmación,
prueba Cancelar y después confirma una sola vez. Anota los folios BOR/NPI reales.

Comprueba:

- CAJA **$550.11**; BBVA **$174.29** y caja de corte **$1,150.00**, sin cambios.
- Un egreso de nómina por **$445**, no $500 ni un segundo egreso por $55.
- Préstamos → Tomás → Resumen: capital **$150**, interés **$15**, total **$165**.
- **Cuotas y abonos**: primera cuota abonada por $50 de capital + $5 de interés.
- El préstamo aparece también como resumen dentro de Datos laborales del empleado,
  si tienes permisos de consulta de préstamos y datos laborales.

## 8. Cancelar y volver a pagar esa misma nómina

**Nóminas → Ver el BOR anotado → Autorización y pagos por empleado → Pagos por
empleado → Tomás → Ver pago e intentos → Registrar resultado → Cancelar sólo este pago**.

Selecciona **OTROS_INGRESOS · Otros ingresos**, referencia **DEV-PREST-TOMAS-NOV-01**,
motivo `Cancelación ficticia para comprobar devolución de 445 y restauración de la cuota de 55.`
Marca que el dinero está recuperado. Prueba Cancelar en el modal y después confirma.

Debe regresar **CAJA a $995.11** y **el préstamo a $220** (capital $200 + interés $20).
El pago anterior se conserva cancelado; no se borra el abono histórico.

En **esa misma fila** usa **Pagar desde una o varias cuentas**. CAJA $445,
referencia **REPAGO-PREST-TOMAS-NOV-01**, motivo de repago, revisión y confirmación.
No empieces otro pago individual directo del mismo periodo.

Resultado: CAJA **$550.11**, préstamo **$165**, un intento cancelado y uno pagado.

## 9. Adelantar la liquidación

**Préstamos → Tomás → Gestión → Reprogramar préstamo**. El capital pendiente
protegido debe ser **$150**; interés anterior **$15**.

Primero revisa una propuesta con **2 cuotas**, Quincenal, primer descuento
**15/12/2026**, **Sin interés nuevo / 0%**, motivo `Probar liquidación en dos quincenas.`
Debe mostrar **$82.50 el 15/12 y $82.50 el 31/12**. No confirmes: usa **Editar captura**.

Ahora selecciona **1 cuota**, misma fecha **15/12/2026**, 0% y motivo
`Liquidar el saldo restante completo en la próxima quincena, sin interés nuevo.`
Revisa: una cuota de **$165**, capital $150 + interés $15. Prueba Cancelar en el modal
y después confirma.

Debe conservar saldo $165 y la primera programación en Historial. No hay otra
entrega ni otro movimiento financiero. CAJA permanece en $550.11.

## 10. Pagar la última cuota y cerrar

En la configuración de nómina de Tomás agrega **EXTRA-RED-TEST por $300**, Una sola
vez, periodo **DIC-2026-FONDOS-TEST**, 01–15/12/2026, con motivo de prueba.
La percepción de noviembre no debe repetirse en diciembre.

En **Nóminas → Pago individual** consulta ese periodo y Tomás:

| Partida | Importe |
| --- | --- |
| Apoyo extraordinario de diciembre | $300.00 |
| Liquidación del préstamo | − $165.00 |
| **Total a pagar** | **$135.00** |

Paga desde **CAJA por $135**, motivo financiero de egreso, referencia
**CIERRE-PREST-TOMAS-DIC-01** y motivo `Liquidación anticipada del préstamo de Tomás, sin nueva entrega.`
Revisa y confirma.

Resultado final esperado si no hubo otros movimientos:

| Dato | Resultado |
| --- | --- |
| CAJA | **$415.11** |
| BBVA_COBROS | **$174.29**, sin cambios |
| CAJA-CORTE-TEST | **$1,150.00**, sin cambios |
| Préstamo | **LIQUIDADO**, capital $0 + interés $0 |
| Historial | Dos programaciones, entrega original, abono cancelado y abonos vigentes |
| Siguiente periodo | No vuelve a descontar un préstamo liquidado |

## 11. Filtros y seguridad

En Préstamos filtra por Tomás y LIQUIDADO; exporta Excel y compara folio, principal
$200 y pendientes $0. Aplicar filtros/Limpiar mantienen la posición de la consulta.
Consulta Historial → Ver cuotas para revisar la programación original sin cambiarla.

Si pruebas un rol con **sólo Préstamos - Consultar**, debe poder consultar/filtrar/
exportar, pero no crear, entregar ni reprogramar. No debe conceder permisos salariales.
No quites permisos a tu administrador para ensayar: usa un rol/usuario separado.

El usuario ya confirmó el caso principal de los apartados 2–10. Los registros
se conservan para consulta. No repetir pagos para revisar formato o documentación.
