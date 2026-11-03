# Nómina de todos los elegibles, incluido un neto cero

**Confirmada por el usuario el 03/11/2026. No repetir los pagos.**

## En palabras simples

El sistema tomó automáticamente a quienes correspondían al periodo. Pagó a las
tres personas que tenían dinero por recibir y conservó a Diego, cuyo resultado
era cero. No lo excluyó ni lo dejó pendiente y no inventó un egreso de $0.

## Evidencia de esta computadora

- BOR-000014: Diciembre — todos los elegibles con neto cero, revisión1.
- DIC-2026-FONDOS-TEST: 01/12/2026–15/12/2026.
- Selección: Todos los elegibles del periodo; cuatro incluidos automáticamente.
- Sueldos967.74, otras percepciones500, deducciones200, neto1267.74.

| Empleado | Resultado | Neto | Registro |
|---|---|---:|---|
| Marta | Pagado | $483.87 | NPI-14 |
| Pablo | Pagado | $483.87 | NPI-15 |
| Sofía | Pagado | $300.00 | NPI-16 |
| Diego | Neto cero confirmado | $0.00 | Cierre de fila19, sin NPI |

Estado final: **Aplicada · sin pendientes**. Lectura posterior confirma pagos
vigentes y cierre de Diego. No se modificó su configuración para registrar el pago.

## Cuentas

Una fuente CAJA, referencia DIC-TODOS-CERO-01. Tres egresos, total1267.74.

| Cuenta | Antes | Después |
|---|---:|---:|
| CAJA | $2,462.85 | **$1,195.11** |
| BBVA_COBROS | $274.29 | **$274.29** |
| CAJA-CORTE-TEST | $1,150.00 | **$1,150.00** |

## Cómo se recorrió el proceso

1. Se usaron los sueldos existentes de Marta y Pablo ($1,000 mensual) sin cambiar
   sus asignaciones; correspondieron483.87 por persona en esta primera quincena.
2. En Empleados → Configuración de nómina → Agregar concepto se registraron los
   apoyos únicos de Sofía300 y Diego200, más deducción200 de Diego, para diciembre.
   Las capturas y pagos anteriores de noviembre se conservaron.
3. Nóminas → Nuevo borrador → periodo → Opciones de selección → Todos los elegibles
   del periodo → Preparar nómina del periodo. Los cuatro quedaron incluidos.
   Bruce/Luis quedaron fuera por reserva coincidente, sin duplicarse.
4. Se revisó desglose y total, después Guardar borrador revisado y su confirmación.
5. Autorización y pagos por empleado → Resumen → Preparar pago colectivo.
   CAJA1267.74, motivo NOMINA, referencia DIC-TODOS-CERO-01 y justificación.
6. Revisar distribución → Confirmar y registrar pagos, con Cancelar disponible.
   La revisión distingue tres personas por pagar y un cierre cero.
7. Se confirmaron resultados, tres egresos, saldos y detalle conservado. El Excel
   de revisión contiene a los cuatro, incluido Diego con0.

## Limpieza visual solicitada al cerrar

- Detalle de nómina conserva sólo **Exportar revisión a Excel** arriba del menú.
  Se retira el duplicado de Detalle por empleado; ambos usaban la misma ruta y
  revisión. No cambia el archivo ni la revisión histórica seleccionada.
- Periodos de nómina no repite un botón que lleva al mismo listado.
- Resumen de nómina utiliza estadoTexto(), como el listado: ya no muestra Cancelado
  para estados PAGADA/AUTORIZADA/PARCIALMENTE_PAGADA. Era un error de etiqueta,
  no una cancelación real. No se reescriben estados ni movimientos.

También disponible en Guía de procesos: **Nómina: preparar a todos y conservar al
empleado con neto cero** (V150,8pasos,6permisos de consulta).

## Límites y siguiente paso

Los tres ejemplos pendientes originales y este recorrido Todos ya tienen
confirmación humana. No equivale a una prueba manual de veinte empleados ni de
concurrencia simultánea; esos controles tienen pruebas técnicas aisladas.
Pendiente real para completar todas las entradas del flujo: neto cero en captura
directa Pago individual (sin borrador previo). Funciona ya desde borrador/colectivo.
Después, chequeras y rediseño de préstamos siguen como entregas separadas.
