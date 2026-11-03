# Prueba aprobada: neto cero desde Pago individual

## Qué vamos a comprobar

Diego tiene $200 de percepciones y $200 de deducciones: no hay dinero que entregar.
Queremos dejar registrado que se revisó ese periodo y quedó resuelto, con sus
conceptos y un motivo, sin elegir cuentas ni sacar dinero. No es excluirlo ni
posponerlo. No tienes que crear un borrador antes.

Estado: **CONFIRMADA por el usuario el 03/11/2026**.
No confundir con los cierres ya aprobados de noviembre y del 1–15 de diciembre.

## Resultado confirmado

El usuario realizó todos los pasos y confirmó el bloqueo al intentar repetirlos.
Consulta de sólo lectura identifica **BOR-000015**, revisión1, empleado12/fila20,
con cierre cero y motivo conservado. Estado interno PAGADA; etiqueta visible
**Confirmada sin pagos**. Conceptos19/20, EXTRA-RED-TEST200 y DESC-TEST200,
continúan activos para el periodo7. No hay salida de dinero por el cierre.

Al intentar nuevamente Pago individual aparece:

> Este empleado ya tiene un neto cero confirmado para fechas coincidentes.

Saldos finales confirmados: CAJA **$1,195.11**, BBVA_COBROS **$274.29** y
CAJA-CORTE-TEST **$1,150.00**, sin cambios. **No repetir ni cancelar BOR-000015**
para reproducir esta guía. Los siguientes pasos conservan el ejemplo realizado,
no son instrucciones para volver a capturarlo en esta base.

## 1. Usuario y datos de partida

Usa el administrador con permisos de configuración y operación financiera.
Datos comprobados en esta computadora mediante consulta de sólo lectura:

- Diego: empleado 12, sin sueldo base, sin cuotas de préstamos.
- Ya existe `DIC-2026-DOS-PESTANAS-TEST`, del **16/12/2026 al 31/12/2026**.
- Antes de iniciar, Diego no tenía conceptos ni cierre para ese intervalo.
  Ahora sí existen: consultar BOR-000015 y no recapturar.
- CAJA **$1,195.11**; BBVA_COBROS **$274.29**; CAJA-CORTE-TEST **$1,150.00**.

No cambies el periodo ni los registros de otros empleados. Si ya avanzaste en esta
prueba, consulta primero los conceptos para no agregarlos dos veces. Si hubo otros
movimientos desde esta lectura, anota los saldos actuales como referencia.

## 2. Configurar una percepción de $200

Entra a **Personal y nómina → Empleados → Diego → Ver expediente →
Configuración de nómina**. Abre la configuración y su pestaña de conceptos; pulsa
**Agregar concepto**.

| Campo | Valor |
| --- | --- |
| Percepción o deducción | `EXTRA-RED-TEST · Apoyo extraordinario de prueba` |
| Importe de referencia | **200.00** |
| ¿Cuándo se aplica? | **Una sola vez, en un periodo** |
| Periodo de aplicación | `DIC-2026-DOS-PESTANAS-TEST` |
| Aplicar desde | **16/12/2026**, completado por el periodo |
| Último día incluido | **31/12/2026**, completado por el periodo |
| Motivo obligatorio | `Percepción de prueba para confirmar neto cero directo en la segunda quincena de diciembre.` |

Pulsa **Revisar y guardar concepto**, revisa el modal y confirma.

## 3. Configurar la deducción de $200

Pulsa **Agregar concepto** otra vez:

| Campo | Valor |
| --- | --- |
| Percepción o deducción | `DESC-TEST · Descuento autorizado de prueba` |
| Importe de referencia | **200.00** |
| ¿Cuándo se aplica? | **Una sola vez, en un periodo** |
| Periodo de aplicación | `DIC-2026-DOS-PESTANAS-TEST` |
| Aplicar desde / Último día incluido | **16/12/2026 — 31/12/2026**, automáticos |
| Motivo obligatorio | `Deducción de prueba por el mismo importe para comprobar un cierre sin salida de dinero.` |

Revisa y confirma. No agregues sueldo ni modifiques sus conceptos anteriores.
Al ser una aplicación única, se consideran los $200 íntegros de cada concepto;
no son importes mensuales que deban convertirse a una quincena.

## 4. Consultar el cálculo directo

Entra a **Personal y nómina → Nóminas → Pago individual**:

1. Selecciona `DIC-2026-DOS-PESTANAS-TEST` de las sugerencias de Periodo.
2. Selecciona a Diego de las sugerencias de Empleado.
3. Pulsa **Consultar cálculo**.

| Resultado | Importe |
| --- | --- |
| Sueldo base | **$0.00** |
| Percepciones | **$200.00** |
| Deducciones | **$200.00** |
| Total a pagar | **$0.00** |

Debe verse **Confirmar neto cero del empleado**, con desglose y justificación.
No debe pedir cuenta, motivo financiero ni referencia de pago.
El aviso de que no tiene sueldo base es correcto para Diego.

## 5. Revisar y probar Cancelar

1. En Motivo de la operación escribe:
   `Confirmar el neto cero de Diego del 16 al 31 de diciembre: percepción 200 y deducción 200, sin salida de dinero.`
2. Pulsa **Revisar neto cero**. Se abre la revisión protegida del detalle y motivo.
3. Si quieres corregir el texto, usa **Editar justificación** y vuelve a revisar.
4. Pulsa **Confirmar neto cero** para abrir el modal final.
5. Comprueba empleado, periodo, total $0.00 y justificación.
6. Pulsa **Cancelar dentro del modal**.

Debe cerrarse el modal sin guardar un cierre, crear una nómina ni mover dinero.
Permanece en la revisión. No confundas ese Cancelar con el enlace de salir de la
pantalla.

## 6. Confirmar una sola vez

Vuelve a pulsar **Confirmar neto cero** y ahora confirma dentro del modal.

Debe abrir la autorización del registro resultante con mensaje de neto cero
confirmado sin salida de dinero. El sistema conserva un folio de nómina para el
histórico: no te exige crear ni dejar un borrador pendiente antes de confirmar.
No anticipar el número del folio: se asigna al guardar.

## 7. Comprobar el resultado

1. **Nóminas:** el nuevo registro figura como **Confirmada sin pagos**, no Cancelada
   ni Pendiente de pago.
2. **Autorización y pagos por empleado → Pagos por empleado:** Diego muestra
   **Neto cero confirmado**, sin pendiente.
3. **Ver pago e intentos → Resumen:** detalle del cierre con fecha, usuario, motivo
   e importe cero. No hay NPI ni distribución por cuentas para este cierre.
4. **Detalle por empleado → Ver desglose:** conserva percepción200/deducción200.
5. **Movimientos financieros:** no hay egreso ni ingreso por esta operación.
6. **Cuentas financieras:** los tres saldos permanecen iguales a los anotados.
7. **Configuración del empleado:** permanecen sus conceptos; confirmar no los elimina.

## 8. Comprobar que no se duplica

Vuelve a **Nóminas → Pago individual**, selecciona el mismo periodo y Diego y
pulsa **Consultar cálculo**.

Debe informar que ya tiene un neto cero confirmado para fechas coincidentes e
indicar que consultes su nómina. No debe permitir otro cierre o pago, crear otro
registro ni modificar cuentas. Tampoco debe incluirlo nuevamente en el cálculo
colectivo para esas mismas fechas.

Prueba terminada con aceptación manual. No acredita pruebas humanas de concurrencia
simultánea; éstas sólo se verificaron técnicamente. Neto cero con cuotas de préstamos
queda bloqueado y fuera de este ejemplo. Siguiente entrega: chequeras ligadas a cuentas
bancarias, separada del rediseño posterior de préstamos.
