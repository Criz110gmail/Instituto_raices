# Prueba pendiente: pagar nómina con cheque

Estado: **Pago, cancelación, nuevo cheque y bloqueo de dos pestañas CONFIRMADOS
por el usuario el 03/11/2026**.

Resultado confirmado: BOR-000016 de Clara, cheque1001/NPI17 cancelado y cheque1002/
NPI18 emitido con pago vigente100. PestañaB bloqueada con «La nómina cambió.
Recarga antes de revisar; no se movió dinero». BBVA174.29/caja1195.11/corte1150.
El usuario ve ambos documentos en el historial. No repetir pagos ni cancelaciones
para comprobar la nueva consulta bancaria. Los pasos siguientes conservan el
ejemplo realizado, no son instrucciones para volver a capturarlo en esta base.

Nuevo listado bancario: ver PRUEBA_CONSULTA_CHEQUES_20261103.md, sólo lectura.
Las pruebas automáticas usaron datos ficticios aislados. No se emitieron cheques
ni se modificaron saldos, empleados o permisos de esta base para prepararlas.

## En palabras simples

Pagaremos $100 a una empleada de prueba con un cheque del banco BBVA. Veremos que
el dinero se descuenta una sola vez. Después cancelaremos ese pago, recuperaremos
los $100 y comprobaremos que el cheque cancelado no puede volver a utilizarse.
Finalmente pagaremos los mismos $100 con otro cheque, sin duplicar la nómina.

Esto registra la entrega internamente: **no imprime cheques, no envía instrucciones
al banco y no comprueba su cobro bancario**. En producción, la cancelación sólo
procede cuando el documento ya no puede cobrarse y los fondos están recuperados
o liberados. Aquí usaremos exclusivamente números y datos ficticios.

## 1. Datos disponibles y usuario

Usa el administrador con acceso a empleados, datos laborales, configuración salarial
y de nómina, operación financiera y cuentas. Además necesita **Chequeras - Consultar**
o **Chequeras - Administrar**. Administrar ya incluye consulta. Para cancelar necesita
también permiso financiero de reversa. No se conceden permisos automáticamente.

Lectura de esta computadora del 03/11/2026:

| Dato | Disponible |
| --- | --- |
| Banco | BBVA_COBROS, saldo **$274.29** |
| Chequera | BBVA — Chequera de prueba, activa, **1001–1050** |
| Caja | CAJA, saldo **$1,195.11** |
| Caja de corte | CAJA-CORTE-TEST, saldo **$1,150.00** |
| Periodo existente | NOV-2026-CIERRE-TEST, **16/11/2026–30/11/2026** |
| Concepto existente | EXTRA-RED-TEST · Apoyo extraordinario de prueba |

Clara todavía no existe y no hay cheques emitidos por esta nueva funcionalidad.
Si ya avanzaste al leer esto, no vuelvas a crearla ni repitas pagos: consulta su
historial primero. Si hubo otros movimientos, toma los saldos actuales como base.
No cambies el saldo inicial ni repitas los ejemplos ya aprobados de otros empleados.

## 2. Crear a Clara y registrar su actividad

En **Personal y nómina → Empleados → Nuevo empleado** captura:

| Campo | Valor |
| --- | --- |
| Institución | La misma de BBVA_COBROS |
| Código laboral | EMP-CHEQUE-TEST-01 |
| Nombre(s) | Clara |
| Primer apellido | Prueba |
| Segundo apellido | Cheque |
| Correo | clara.cheque@example.invalid |

Registra y confirma. En su expediente → **Datos laborales** selecciona
**Actividad laboral confirmada**, ingreso **03/11/2026**, sin baja. Guarda y confirma.
No le asignes puesto ni sueldo: esta prueba paga únicamente un apoyo extraordinario.
No necesita cuenta de acceso ni perfil docente.

## 3. Agregar el apoyo de una sola vez

Desde el expediente de Clara → **Configuración de nómina**, abre la configuración,
ve a **Conceptos configurados** y pulsa **Agregar concepto**:

| Campo | Valor |
| --- | --- |
| Percepción o deducción | EXTRA-RED-TEST · Apoyo extraordinario de prueba |
| Importe de referencia | **100.00** |
| ¿Cuándo se aplica? | Una sola vez, en un periodo |
| Periodo de aplicación | NOV-2026-CIERRE-TEST |
| Aplicar desde / Último día incluido | **16/11/2026 — 30/11/2026**, completados por el periodo |
| Motivo obligatorio | Apoyo único de Clara para probar el pago de nómina con cheque. |

Revisa, prueba **Cancelar en el modal** y luego confirma. Debe quedar un solo
concepto por $100. No añadas transporte ni deducciones.

## 4. Consultar el pago y capturar el cheque

En **Personal y nómina → Nóminas → Pago individual** selecciona de las sugerencias
el periodo NOV-2026-CIERRE-TEST y a Clara. Pulsa **Consultar cálculo**.
Debe mostrar sueldo $0, percepción $100, deducciones $0 y **total $100**.
El aviso **Sin sueldo base asignado** es correcto aquí. No crees un borrador previo.

En Fuente 1 captura:

| Campo | Valor |
| --- | --- |
| Cuenta de origen | BBVA_COBROS, seleccionada de las sugerencias |
| Importe de esta cuenta | **100.00** |
| Forma de entrega | Cheque nominativo · Cuenta bancaria |
| Chequera activa | BBVA — Chequera de prueba |
| Folio del cheque | **1001**, si sigue disponible |
| Fecha de emisión | Hoy, la propuesta por el sistema |
| Beneficiario | Clara Prueba Cheque, propuesto automáticamente |
| Motivo financiero | NOMINA · Nómina |
| Referencia | CHEQUE-CLARA-1001 |
| Motivo de la operación | Pago de prueba de Clara por 100 mediante cheque 1001 de BBVA. |

Al enfocar chequera deben aparecer sugerencias, y al elegirla se propone el primer
folio libre. Consultarlo **no lo reserva**. No agregues otra cuenta para este caso.
Una caja no permite cheques. La fecha de emisión no puede ser posterior a hoy.

## 5. Revisar sin modificar y probar Cancelar

Pulsa **Revisar distribución**. Cuenta, importe y datos del cheque quedan protegidos.
Si necesitas corregirlos, usa **Editar distribución**, no el botón Atrás del navegador.

Comprueba banco, $100, folio1001, beneficiario y fecha. El saldo previsto será
**$174.29** si BBVA sigue en $274.29. Pulsa **Confirmar y registrar pagos**:
el modal debe incluir también chequera, folio, fecha y beneficiario.

Primero pulsa **Cancelar dentro del modal**. No debe emitir el cheque ni descontar
dinero. Vuelve a abrirlo y ahora confirma **una sola vez**.

## 6. Verificar el pago

- Nóminas → abre el nuevo registro de Clara → **Autorización y pagos por empleado**
  → **Pagos por empleado** → **Ver pago e intentos**.
- Clara debe aparecer pagada. Anota el folio BOR y NPI reales; no anticipes números.
- **Historial de intentos → Cheques utilizados en los pagos** debe mostrar
  cheque1001, BBVA, chequera, Clara, fecha, $100 y estado **Emitido**.
- **Movimientos financieros:** un egreso de nómina por $100, referencia al mismo NPI;
  el concepto identifica el cheque1001.
- **Cuentas financieras:** BBVA **$174.29**; CAJA **$1,195.11** y corte **$1,150.00**
  sin cambios. No debe haber otro descuento al consultar el historial.

## 7. Cancelar el pago, conservando su cheque

Dentro de ese mismo **Ver pago e intentos → Registrar resultado → Cancelar sólo este pago**:

| Campo | Valor |
| --- | --- |
| Motivo financiero de ingreso | OTROS_INGRESOS · Otros ingresos |
| Referencia de devolución | CANCELA-CHEQUE-CLARA-1001 |
| Motivo de reversa | Cancelación de prueba: cheque ficticio 1001 recuperado y anulado, fondos liberados en BBVA. |

Marca las confirmaciones de recuperación de fondos y de que el cheque ya no puede
cobrarse. En un pago real **no las marques si no tienes esa certeza**.
Pulsa **Revisar cancelación individual**, prueba Cancelar en el modal y comprueba
que todo siga igual. Revisa de nuevo y confirma.

Resultado: Clara queda pendiente de pago; BBVA vuelve a **$274.29** por un ingreso
compensatorio exacto de $100. El intento anterior aparece Cancelado y su cheque
**Cancelado · No reutilizable**. No desaparecen el egreso original ni el folio1001.

## 8. Volver a pagar con otro cheque

No vuelvas al Pago individual inicial ni crees otra nómina. Desde **el mismo registro
de Clara → Autorización y pagos por empleado → Pagos por empleado → Ver pago e intentos
→ Registrar resultado → Pagar desde una o varias cuentas** prepara los mismos $100.

Elige BBVA y Cheque, la misma chequera y trata de usar **1001**: al revisar debe
indicar que ya fue utilizado, aunque esté cancelado, sin mover dinero.
Corrige a **1002** (o al siguiente disponible si otro usuario ya lo emitió), fecha
de hoy, mismo beneficiario. Referencia CHEQUE-CLARA-1002 y motivo:
`Nuevo pago de Clara con cheque 1002; el cheque 1001 quedó cancelado.`

Revisa y confirma. BBVA vuelve a **$174.29**, con un nuevo egreso de $100 y un único
pago vigente. El historial conserva1001 cancelado y1002 emitido. La nómina queda
**Aplicada · sin pendientes**. Las cajas siguen intactas.

## 9. Seguridad y cierre

Intenta consultar otra vez el pago individual original de Clara en el mismo periodo:
debe impedir un segundo pago de la misma cobertura. No debe cambiar ningún saldo.

Para probar dos pestañas de confirmación, usa las dos revisiones **antes** de confirmar
el paso8: confirma A y luego B; B debe pedir revisar/recargar y no descontar otra vez.
No lo hagas como un tercer pago después de terminar el ejemplo.

Esta entrega admite cheque por empleado y puede combinarlo con efectivo u otro banco
en ese mismo pago. El pago colectivo sigue usando efectivo/transferencias: para
entregar cheques nominativos se entra por cada empleado. La chequera no permite
cambiar su rango después de emitir un cheque. No incluye impresión, cobro bancario,
conciliación automática ni el rediseño de préstamos.

Al confirmar los resultados humanos, documentar los folios reales y saldos sin
repetir operaciones para obtener evidencia.
