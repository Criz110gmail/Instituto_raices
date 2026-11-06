# Recibos internos de nómina — etapa 1

Estado: presentación del PDF con conceptos aceptada por el usuario el 06/11/2026.
No atribuir aceptación humana de controles que no se hayan probado expresamente.
Sólo consulta; no volver a
pagar, cancelar cheques, entregar préstamos ni cambiar saldos para probarla.

## 1. Entrada y filtros

Con tu administrador autorizado abre **Personal y nómina → Nóminas → Recibos
internos**. No requiere crearle usuario a ningún empleado.

Al enfocar Empleado o Periodo histórico se ofrecen hasta diez registros con historia;
desde tres caracteres se busca. No son todos los empleados activos del catálogo.
Busca a Sofía o su código **EMP-PREST-CERO-01**, selecciona la sugerencia y aplica.
El cierre confirmado del caso anterior debe verse como **Neto cero confirmado**,
con importe **$0.00**. No exige cuenta ni genera NPI.

Las fechas Desde/Hasta filtran el periodo CUBIERTO por la nómina, no la fecha de
pago/registro. Rango sin cobertura: lista vacía. Rango invertido: aviso dentro de
filtros sin perder captura. Limpiar elimina filtros. Ver recibo y Volver conservan
filtros, tamaño y página. Excel debe respetar todos los filtros, no sólo la página.

## 2. Sofía y PDF sin salida de dinero

En **Ver recibo** revisa las tres pestañas y **Ver / imprimir PDF**:

- Resumen: datos históricos, neto $0.00; percepción $100.00 y deducción $100.00.
- Conceptos: cuota PRE-000002; capital $100.00/interés $0.00 si esa fotografía lo
  conserva. El desglose del préstamo NO se suma otra vez como deducción.
- Origen: sin cuenta de salida; justificación del cierre conservada.
- PDF para compartir: logo e institución, folio/estado, empleado/código/puesto,
  fechas dd/MM/yyyy, percepción por nombre e importe $100.00 y deducción por su
  nombre e importe $100.00. Después, total de percepciones $100.00, total de
  deducciones $100.00 y neto $0.00. La deducción de préstamo sólo muestra el nombre
  del concepto y su importe aplicado: no folioPRE, saldo, capital/intereses ni
  programación de cuotas.
  No cuentas, cheques, responsable, fecha operativa, códigos BOR/periodo ni notas.
  No CFDI, indicación de neto cero. Debe abrir otra pestaña e imprimir.
  Si el empleado tiene sueldo base y otras percepciones, el TOTAL de percepciones
  incluye ambos, y el sueldo se muestra una sola vez dentro de Percepciones.
  No se incluyen aportaciones patronales ni partidas pospuestas/noaplicadas en0.
  Nombres largos o muchos conceptos pueden continuar en otra página sin perder
  el último concepto ni los totales. En foto antigua sin partidas, debe decir
  sin desglose histórico disponible en vez de inventar nombres. La trazabilidad
  privada sigue sólo en las pestañas administrativas.

Préstamo continúa LIQUIDADO. Los saldos confirmados al cerrar ese caso siguen
CAJA $315.11, BBVA_COBROS $174.29, CAJA-CORTE-TEST $1,150.00 si no hubo operaciones
humanas posteriores. La consulta no debe cambiarlos.

## 3. Intentos, cheques y legado disponibles

Quita el filtro de empleado. Si existen documentos de Clara, filtra **Pagos
cancelados** y revisa el intento ya cancelado del cheque1001: importe ORIGINAL,
estado sin vigencia, justificación y folio de cheque cancelado/no reutilizable.
El pago vigente con cheque1002 es otro recibo, no reemplaza el historial.
El PDF cancelado mantiene la advertencia de no vigencia, pero NO el folio de
cheque, las cuentas ni la justificación interna; esos datos sólo aparecen en admin.
No canceles ni vuelvas a pagar para crear ejemplos. Si alguno no está en esta
base, anótalo como no disponible, no como fallo automático del reporte.

Una aplicación antigua consolidada se etiqueta **Aplicación histórica**: permite
ver el empleado y desglose congelado, sin afirmar un pago individual nuevo. Los
rechazados sin salida y borradores no deben aparecer como comprobantes de pago.
No sumar todos los recibos (cancelados + reintentos + legado) como gastos vigentes.

## 4. Permisos y exportación

Con usuario separado de sólo Préstamos - Consultar, intentar la ruta
`/admin/recibos-nomina` y `/admin/recibos-nomina/excel` debe negar acceso. PDF y
autocompletado tampoco deben exponer nómina. No quitar permisos a criz para probar.
Regresa a administrador y comprueba PDF/Excel del filtro autorizado. Confirmar
fechas/decimales/nombres/estados, incluido cero numérico; no datos de otra institución.

## Alcance pendiente

Etapa 2: cuenta opcional del empleado, reutilización docente y consulta únicamente
de sus recibos. No implementado en esta entrega. Tampoco CFDI, timbrado ni órdenes
bancarias. Cuando el humano confirme esta revisión se podrá incorporar a Guía de
procesos dentro del sistema como aceptación; no atribuirla anticipadamente.
