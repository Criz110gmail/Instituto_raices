# Consulta de cheques desde la cuenta bancaria

Estado al 06/11/2026: **CONFIRMADA por el usuario**, incluida la repetición de
fechas invertidas y scroll después de la corrección. El usuario respondió
«listo pasó la prueba». Esto no acredita una prueba con otro rol de sólo consulta.
Esta prueba no registra pagos, no cancela cheques y no cambia saldos.

La guía integrada V160 incorpora este caso en **Guía de procesos → Cheques:
consulta, filtros y Excel sin mover dinero**. Consulta de administrador confirmada;
no atribuir aceptación de un rol lector de cheques por el ensayo separado de Préstamos.

El usuario confirmó que los puntos anteriores hicieron lo esperado y aportó saldos
BBVA $174.29, CAJA $315.11 y corte $1,150.00 después del caso de Sofía. El saldo de
CAJA $1,195.11 al final del procedimiento original es histórico del 03/11, no el
saldo actual esperado. No ajustar cuentas para repetir ese valor.

## Retomar únicamente el problema visual

Se corrigió el 06/11/2026 la restauración de scroll doble (load/pageshow) y se
movió el aviso dentro de los filtros, con captura conservada. Una interacción manual
de rueda/táctil/puntero o teclas de desplazamiento cancela un ajuste tardío pendiente.
Publicado sin migración ni cambios de datos, con 1061 pruebas Java y 142 JS correctas
y revisión visual sintética escritorio/móvil en ambos temas. Recorrido ya confirmado;
se conserva como referencia, no es necesario repetirlo:

1. Recarga la pantalla Cheques; si hubo reinicio entra como INSTITUTO_RAICES\criz.
2. Filtra desde 04/11/2026 hasta 03/11/2026 y aplica.
3. Debe verse un único aviso Revisa los filtros junto a la captura, conservando fechas.
4. Desplaza arriba/abajo: no debe saltar de vuelta al aviso o al botón automáticamente.
5. Corrige a 03/11/2026–04/11/2026 y aplica: desaparece el aviso.
6. Pulsa Limpiar: sin aviso, sin perder el lugar de trabajo ni mover dinero.

No acreditar permisos de usuario sólo consulta por probar con administrador;
no volver a emitir ni cancelar los cheques 1001/1002 para revisar presentación.

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
