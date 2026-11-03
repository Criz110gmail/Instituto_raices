# Nómina: un resultado de cero también se conserva

## Qué estamos probando, en palabras simples

Diego tiene un apoyo de $200 y un descuento de $200. No hay dinero por entregar,
pero debemos dejar registrado que se revisó y confirmó su nómina. No debemos
pedir una cuenta, sacar dinero ni dejarlo pendiente de pago por tener total cero.

**Confirmación funcional recibida el 03/11/2026:** el usuario confirmó que el
cierre y los demás controles del ejemplo fueron correctos. Lectura posterior:
BOR13 confirmado, fila15 con cierre cero y motivo conservado; cuentas sin cambios.
No repetir la confirmación para probar el diseño ni crear otro borrador.

Se mejora la presentación del cierre dentro de la pestaña Resumen de Ver pago e
intentos, con importe, revisión, fecha, responsable y motivo alineados. Falta sólo
aceptación visual de ese ajuste, no otra operación de nómina.

## Punto exacto para continuar

- Empleado: Diego Prueba Validaciones, EMP-NETO-TEST, id12.
- Borrador existente BOR-000013: Noviembre — Diego neto cero, revisión1, fila15.
- Periodo NOV-2026-CIERRE-TEST, 16/11/2026–30/11/2026.
- Sueldo0; percepción200; deducción200; neto0. La deducción anterior300 está cancelada.
- Antes de esta implementación el usuario recibió el bloqueo al autorizar cero.
- No crear otro borrador, no recapturar conceptos ni usar No aplicar como atajo.

## Pasos manuales

1. Recargar y entrar con administrador a Personal y nómina → Nóminas → Ver
   Noviembre — Diego neto cero (BOR-000013).
2. Autorización y pagos por empleado → Decisiones y pendientes.
3. Revisar importe0 y capturar motivo: `Confirmar el neto cero de Diego: percepción
   de 200 y deducción de 200, sin salida de dinero.`
4. Pulsar Confirmar netos cero y decisiones. Modal: periodo y1netocero, explica
   que no solicita cuenta ni genera egreso. Probar Cancelar: vuelve sin guardar.
5. Repetir y confirmar. Debe quedar Confirmada sin pagos, no Borrador.
6. Pagos por empleado → Todos o Neto cero confirmado. Diego aparece con0 y estado
   Neto cero confirmado, no Pagado ni Pendiente.
7. Ver pago e intentos: tarjeta Neto cero confirmado · sin salida de dinero,
   revisión, motivo, fecha/usuario. No hay botón de pagar/rechazar/cancelar un pago;
   no debe inventar NPI ni movimiento. Su desglose200/200 se conserva en la nómina.
8. Excel de Pagos por empleado con filtro Neto cero confirmado conserva la fila
   y el estado; Pago vigente vacío. Excel de revisión conserva200/200/0.
9. Cuentas permanecen CAJA2462.85, BBVA_COBROS274.29, CAJA-CORTE-TEST1150 si no hubo
   otras operaciones. Cero egresos nuevos por Diego en Movimientos financieros.
10. Opcional: abrir dos pestañas ANTES de confirmar5 y confirmar primeroA; despuésB
    sin recargar. B debe rechazar versión/revisión ya confirmada, sin duplicar cierre.

Resultado funcional aprobado por el usuario. La prueba opcional de dos pestañas
no se declara realizada manualmente; existe verificación técnica de concurrencia.

## Colectivo de veinte empleados (verificación técnica aislada)

El generador en modo Todos incluye veinte empleados: diecinueve con200 y uno con
200 de percepción y200 de deducción. Resumen20empleados/3800total; fuentes cubren
3800, no4000. Al confirmar registra19pagos reales y1cierre cero, conserva20filas,
termina sin pendientes. Si falla cualquier pago, revierte todo, incluido el cierre.
Prueba automatizada, no afirmar captura humana ni aplicarla en la base real.

## Límites explícitos

- No es una transferencia bancaria ni contabilidad de partida doble.
- El cierre es registro auditable privado, no movimiento ficticio de tesorería.
- No se permite neto negativo. Cuotas de préstamo con cero siguen requiriendo
  tratamiento especializado; no se inventan abonos ni liquidación de préstamos.
- Cero confirmado no admite devolución financiera porque no hubo dinero entregado.
- Este flujo se confirma desde el borrador o junto con el colectivo. La captura
  directa Pago individual conserva su validación de importe positivo.
- El botón inferior Volver a configuración de Detalle del concepto ya utiliza el
  mismo estilo payment-back-link y flecha que el superior, sin cambiar sus filtros.
