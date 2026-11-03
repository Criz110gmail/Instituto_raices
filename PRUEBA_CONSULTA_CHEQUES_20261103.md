# Consulta de cheques desde la cuenta bancaria

Estado: implementada, **pendiente de aceptación visual y funcional del usuario**.
Esta prueba no registra pagos, no cancela cheques y no cambia saldos.

## Qué queremos comprobar

Ver juntos los cheques de una cuenta sin buscar empleado por empleado. Los cheques
cancelados se distinguen de los emitidos y los filtros y Excel muestran lo mismo.

## 1. Abrir la cuenta

Con el administrador entra a **Cuentas financieras → BBVA_COBROS → Chequeras**.
Arriba debe haber un menú con **Datos de la cuenta**, **Chequeras** y **Cheques**.
Pulsa **Cheques**. Debe quedar marcada esa pestaña y mostrar el nombre de BBVA.
También puedes abrir **Editar** de BBVA y desde ahí usar la pestaña **Cheques**.

## 2. Comprobar los dos registros existentes

| Cheque | Pago | Beneficiario | Importe | Estado |
| --- | --- | --- | --- | --- |
| 1002 | NPI-18 | Clara Prueba Cheque | $100.00 MXN | Emitido |
| 1001 | NPI-17 | Clara Prueba Cheque | $100.00 MXN | Cancelado · No reutilizable |

El cheque1001 conserva fecha y motivo de cancelación. La chequera histórica es
**BBVA — Chequera de prueba**. Si otros usuarios emitieron cheques, pueden aparecer
más registros: no cancelarlos para reproducir la tabla.

Las tarjetas resumen **todos los resultados filtrados**, no sólo la página visible.
Sin otros cheques: emitidos1/$100 y cancelados1/$100. No son un saldo bancario ni
afirman que el banco haya cobrado el documento.

## 3. Probar filtros y Excel

1. Estado **Emitidos** → Aplicar filtros: sólo1002 y tarjeta emitidos1/$100.
2. Estado **Cancelados** → Aplicar filtros: sólo1001 con motivo de cancelación.
3. Exporta Excel con ese filtro: debe contener1001 cancelado, no1002.
4. Pulsa **Limpiar**: vuelven ambos, sin subir al inicio de la pantalla.
5. Beneficiario, chequera o pago: escribe **Clara** y filtra; deben quedar los dos.
6. Folio exacto **1002**: sólo el emitido. Limpia ese folio para continuar.
7. Emisión desde/hasta **03/11/2026**: ambos, si no cambiaste la fecha en el ejemplo.
8. Invierte las fechas: debe explicar el error, sin perder la captura.
9. Limpia y enfoca **Chequera**: muestra hasta10 sugerencias; escribe3caracteres y
   selecciona **BBVA — Chequera de prueba**. Filtra: ambos pertenecen a esa chequera.

## 4. Abrir desde una chequera específica

Pulsa **Chequeras** en el menú y luego **Ver cheques** en la fila de BBVA — Chequera
de prueba. Debe abrir el mismo listado con esa chequera seleccionada en el filtro.
Ver historial sigue consultando cambios del catálogo; Ver cheques consulta los
documentos de pago. No son la misma funcionalidad.

## 5. Permisos y saldos

**Chequeras - Consultar** permite listado, filtros y Excel de las cuentas autorizadas,
sin editar chequeras ni entrar a expedientes o desgloses de salarios. Administrar
incluye consulta. Sin acceso a la cuenta no se permite consultar sus documentos
por URL ni Excel. Las cajas no tienen esta consulta bancaria.

Al terminar, sin otras operaciones: BBVA **$174.29**, CAJA **$1,195.11** y corte
**$1,150.00**. No debe aparecer ningún movimiento financiero nuevo por consultar,
filtrar o exportar. No repetir la nómina de Clara para probar este listado.
