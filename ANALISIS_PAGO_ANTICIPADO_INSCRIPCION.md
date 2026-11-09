# Pago anticipado desde inscripción — análisis de implementación

Estado: implementado el 09/11/2026; pendiente de aceptación manual.
Prueba detallada: PRUEBA_PAGO_ANTICIPADO_INSCRIPCION.md.

## Flujo solicitado

Desde Inscripciones → Editar → Cobranza, elegir los conceptos y meses del alumno,
ver los cargos existentes y los que faltan, confirmar la generación individual y
preparar un pago con una aplicación por cargo. El ingreso financiero se registra
al validar el pago, no durante la vista previa ni al generar cargos.

## Presentación solicitada por el usuario

Abrir la captura en una pantalla separada desde la pestaña Cobranza. Mantener los
filtros y campos alineados, explicar qué datos faltan y mostrar claramente alumno,
concepto, periodo, importe y estado del cargo. La vista previa debe distinguir los
cargos existentes de los que se van a generar y mostrar el total seleccionado.
Usar los estilos y ayudas del sistema, soporte para tema claro y oscuro y vista
móvil, y un modal de confirmación con opción Cancelar. Conservar la inscripción
de origen al regresar y la selección si hay errores de validación.

## Hallazgos del código

- `CargoServiceImpl.generar` recorre cuotas de una institución y un plantel; no es
  seguro invocarlo para crear cargos de una sola inscripción.
- `CargoServiceImpl.planificar`, `fila` e `insertar` ya calculan períodos, becas,
  vencimientos y claves idempotentes, pero son privados. Debe extraerse o exponerse
  una operación acotada por inscripción y cuota que reutilice esas reglas.
- `PagoAdminController.prepararDesdeCargo` precarga actualmente un único cargo.
  Se requiere preparar las solicitudes múltiples sin aceptar cargos de otro alumno,
  institución, plantel o moneda y verificando el tutor financiero autorizado.
- Los acuerdos de pago anticipado existentes manejan cargos ya generados y beneficios;
  no deben confundirse con esta preparación de mensualidades futuras.

## Controles necesarios

La vista previa debe ser de sólo lectura y distinguir cargo existente, cancelado,
liquidado y pendiente de generar. La confirmación debe bloquear la inscripción y las
cuotas necesarias, recalcular importes y becas, detectar cambios y rechazar una vista
obsoleta. La unicidad y concurrencia deben impedir duplicados. Generar varios cargos
debe ser transaccional y limitado a la inscripción y selección autorizadas.

El pago preparado no debe permitir que la manipulación del navegador altere su
alcance o cantidades sin validación del servidor. Deben comprobarse permisos,
alcance, estados, saldo, cuenta y medio de pago en cada operación. Al validar, las
aplicaciones se registran por separado y el movimiento financiero es único.

## Pruebas por completar

Incluir vista previa sin escrituras, cargos existentes y faltantes, becas y saldos,
dos pestañas concurrentes, repetición, selección alterada, permisos y alcance,
inscripción distinta, error transaccional, pago sin validar y validación con un único
ingreso. Verificar también filtros, navegación, confirmación con Cancelar y móvil.
