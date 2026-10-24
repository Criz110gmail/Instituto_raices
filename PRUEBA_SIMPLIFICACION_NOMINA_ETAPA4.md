# Préstamos internos sin intereses — etapa 4

24/10/2026. Desarrollo técnico, no aceptación humana ni guía confirmada.
La respuesta «continúa» se tomó como confirmación de la propuesta sin intereses.

## Disponible en esta entrega

Configuración de nómina → empleado → Conceptos recurrentes → Préstamos: entrega y saldo.
No agrega otra entrada principal. Lista con búsqueda, estado, paginación, Excel y
Nuevo préstamo; alta/edición separadas, retorno con filtros y página de origen.
Detalle: Resumen y saldo / Entrega y gestión / Historial. Confirmaciones compartidas
con detalle y Cancelar que sólo cierra el modal. Importe $3,000.00 y fechas dd/MM/yyyy.

En Conceptos de nómina se incorpora aplicación Con saldo, exclusivamente Deducción.
No la asignes también como habitual/ocasional: sería un descuento duplicado. La ayuda
del préstamo muestra diez conceptos al foco y busca desde tres caracteres, filtrados
en SQL por institución, activo, Deducción y Con saldo. Conserva versión y nombre.

El borrador guarda principal, parcialidad mensual positiva no superior al principal,
primer día del mes de descuento, motivo y solicitud única contra doble captura.
Requiere empleado laboral activo, ingreso compatible y calendario mensual activo.
Inicio actual/futuro y posterior a nóminas ya aplicadas; sin intereses ni prorrateos.
No mueve dinero ni constituye aún deuda: saldo $0.00. Sólo el borrador se edita/cancela.

## Protección operativa hasta fase 5 — no cambiar para pruebas humanas

`app.nomina.prestamos.entrega-habilitada=false` explícito en application.properties y
valor por defecto en servicio. Sin pagos/abonos individuales completos, no activar
entregas reales. El bloqueo es de servidor además de ocultar controles. Permite
preparar borradores mientras se termina el proceso de recuperación de la siguiente fase.

La entrega está implementada y comprobada exclusivamente en QA aislada con propiedad
habilitada allí: revisión sellada de préstamo/cuenta/saldo/motivo/referencia, confirmación
de entrega real y modal final. Autorizar salarios Y registrar movimientos financieros;
caja o banco institucional activo y moneda institucional, sin sobregiro. Un egreso
PRESTAMO idempotente por préstamo y entrega inmutable en la MISMA transacción. No envía
dinero al banco. El libro financiero muestra folio, no nombre/código del empleado ni
referencia privada. El detalle salarial conserva referencia, cuenta, movimiento y actor.
No editar/cancelar préstamo entregado ni revertirlo desde movimiento manual genérico.

Saldo entregado actual se deriva de la entrega, nunca de meses transcurridos. Todavía
no hay abonos, liquidación, suspensión ni corrección de entrega operativa habilitados.
Fase5 debe incorporar pagos/intentos individuales, abonos y sus compensaciones
inmutables, última cuota limitada al saldo y transición de estados con nueva migración.
No dar por completo el ciclo de préstamo ni cambiar sólo la propiedad para liberarlo.

## Ejemplo que sí puede revisar el usuario ahora — sin dinero

1. Reutilizar empleado de capacitación activo con ingreso y calendario mensual; no
   repetir ni modificar BOR-000001 aplicada, su egreso #10 o su saldo posterior7500.
2. Conceptos de nómina → Nuevo: código PRESTAMO-INTERNO-TEST, nombre «Recuperación
   de préstamo interno», tipo Deducción, aplicación Con saldo, Activo. Si existe,
   reutilizarlo; no duplicarlo ni cambiar conceptos ya asignados.
3. Configuración → empleado → Conceptos recurrentes → Préstamos → Nuevo préstamo.
4. Concepto anterior, importe $3,000.00, parcialidad mensual $500.00, primer descuento
   01/06/2027 (o primer día de otro mes futuro compatible), motivo «Capacitación de
   préstamo interno sin intereses». Revisar modal, Cancelar conserva captura sin guardar.
5. Revisar de nuevo y confirmar: Borrador/sin entregar, saldo $0.00. No esperar un egreso.
6. Editar motivo, revisar/guardar; historial conserva el cambio. Verificar filtros/Excel.
7. En Entrega y gestión debe indicar protección temporal por fase5, sin botón para
   registrar una entrega real. Cancelar borrador con motivo opcionalmente, conservando
   historial y cero movimientos. No recrear/cancelar repetidamente por documentación.

## Generador y transición

Un préstamo en BORRADOR no afecta el cálculo mensual. Si existe uno ACTIVO con primer
descuento hasta el fin del periodo, se clasifica REVISION y no se paga omitiendo el
descuento. Se incluye fotografía de préstamo en fuentes sólo cuando corresponde;
sin préstamo no cambia el sello de candidatos. No recalcula aplicaciones históricas.
Un borrador anterior al préstamo activo queda obsoleto y no puede aplicarse sin revisión.
Esta barrera es transitoria hasta los abonos por pago individual, no un cálculo salarial.

## Evidencia técnica, no dinero ni personas reales

1000 Java de suite, 3 IT PostgreSQL17 aislados (Préstamos, EtapaTres, Preparación)
y122 JS correctos. Los3 unitarios ejecutados también con IT no se vuelven a sumar.
Migraciones1–133 desde base vacía; captura/edición/cancelación, versiones, doble UUID,
entrega concurrente con un único egreso, sello alterado, saldo insuficiente, historial
inmutable, privacidad, lectura sin editar, IDOR/CSRF, alta/revisión web y Excel multipágina.
QA activa:3000 entregados sobre cuenta ficticia20000 →17000; deuda3000 sin abonos por
preparar. QA protegida: bloquear entrega en servidor y pantalla. Datos sólo en tmpfs.
Revisión visual con datos ficticios en escritorio y móvil, claro/oscuro; no enviar
formularios de confirmación final en la vista ficticia. Aceptación humana pendiente.

V133 aditiva: prestamo_empleado, prestamo_entrega, prestamo_empleado_historial, ampliación
Con saldo y clase PRESTAMO. Sin backfill, grants ni división de egresos históricos.
Próximo paso: fase5; diseñar migración de aplicaciones consolidadas antes de pagos/reversas
por empleado. Las antiguas aplicadas siguen sólo consulta, no fabricar pagos individuales.
