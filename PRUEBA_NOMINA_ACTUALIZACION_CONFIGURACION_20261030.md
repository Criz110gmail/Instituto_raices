# Actualización explícita y propuesta desactualizada

Estado: **confirmada por el usuario el 30/10/2026**. Publicada mediante V146.
No repetir pagos, altas temporales ni crear otra revisión de BOR-000009.

## Evidencia

- Luis Prueba Posposición, EMP-POSP-TOTAL-TEST, empleado 8.
- BOR-000009, nombre real Preparación · DIC-2026-FONDOS-TEST, estado Borrador.
- Periodo 01/12/2026–15/12/2026. Revisión 1: sueldo/neto $483.87.
- Revisión 2: sueldo $483.87 + transporte $38.71 = $522.58.
- Transporte 9 activo: $80 mensuales del 01/12/2026 al 31/12/2026.
- EXTRA-RED-TEST de $10: asignaciones 10 y 11 canceladas, conservando auditoría.
- Pestaña A: propuesta preparada ANTES del apoyo temporal agregado desde B.
  Sin recargar, Confirmar nueva revisión y modal rechazaron:
  «Cambió la configuración o inclusión de uno o más empleados. No se guardó nada:
  vuelve a preparar y revisar el periodo.»
- Sólo DESPUÉS del rechazo se canceló la asignación temporal 11 desde B.
- Historial conserva únicamente revisiones 1 y 2. Ambas propuestas vuelven a $522.58.
- CAJA $3,762.85; BBVA_COBROS $274.29; CAJA-CORTE-TEST $1,150.00, intactas.
- Retorno desde revisión al historial y desglose a revisión comprobado por el usuario.

El primer intento se canceló antes de intentar A; no contaba como prueba del rechazo.
El segundo sí respetó el orden y fue confirmado. No equivale a una carrera de
transacciones simultáneas ni a duplicación de pagos. El aviso actual es general,
no identifica los empleados cambiados por nombre.

## Ubicación y regreso

Empleados → Luis → Ver expediente → Configuración de nómina → Conceptos configurados
→ Ver → Resumen / Gestión / Historial. El detalle, alta y cambio muestran la ruta,
el empleado y sección actual. ← Volver a configuración conserva los filtros y
paginación. El detalle no modifica permisos ni permite borrar historial.

## Ensayo posterior también confirmado

Actualización 30/10/2026: No aplicar TODO se ejecutó y confirmó en BOR-000010,
sin pagos ni pendientes y cuentas intactas. Ver PRUEBA_NOMINA_NO_APLICAR_EMPLEADO_20261030.md.
Los permisos también están confirmados en V148. La propuesta original siguiente
se conserva como referencia, no como una instrucción de volver a ejecutarla:

No aplicar TODO el pago de Luis en la segunda parte de diciembre, reusando
DIC-2026-DOS-PESTANAS-TEST (16–31/12/2026), disponible para él aunque Bruce ya lo pagó.
No tocar BOR-000009 ni el pago de noviembre. Neto previsto $557.42:
sueldo $516.13 + transporte $41.29, sin deducciones y sin apoyo extraordinario.
Preparar borrador sólo Luis, guardar y decidir Todo el pago del empleado → No aplicar
en este periodo. Probar Cancelar del modal antes de confirmar. A pagar0, pospuesto0,
excluido557.42. Confirmar decisiones sin mover dinero conserva la exclusión con
motivo; no registra pago/egreso ni pendiente. El listado puede indicar Aplicada · sin
pendientes por cierre de decisiones: no significa dinero pagado. Revisar fila de
Luis No aplicar, historial, Excel, cuentas intactas y configuración original igual.
La aceptación humana ya se recibió; no repetirlo. Para continuar usar
RETOMAR_NOMINA_20261030.md.
