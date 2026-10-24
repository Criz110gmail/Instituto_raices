# Pagos individuales y recuperación de préstamos — fase 5

24/10/2026. Ejemplo propuesto, pendiente de aceptación humana. No publica todavía
la guía integrada confirmada ni implica cálculo fiscal, timbrado o dispersión bancaria.

## Flujo disponible

Nóminas → Ver detalle → Autorización y pagos por empleado.

- BORRADOR: permite revisar/actualizar explícitamente antes de autorizar.
- AUTORIZADA: congela revisión, empleados y conceptos. No mueve dinero ni abona.
- PARCIALMENTE_PAGADA: al menos uno, pero no todos, tienen pago vigente.
- PAGADA: todos los empleados de esa revisión tienen pago vigente.
- APLICADA: exclusivamente aplicación consolidada histórica; consulta sin convertirla
  en pagos individuales ni inventar movimientos o abonos.

La autorización es una acción definitiva sobre la revisión; no se ofrece cancelarla
como borrador. Resolver pendientes pagando/rechazando o revirtiendo el pago individual.
No hay pago parcial del neto de un mismo empleado ni registro de egresos de cero:
un neto cero requiere resolver la configuración antes de autorizar.

Cada empleado: Resumen / Registrar resultado / Historial de intentos. Neto autorizado
no editable; cuenta/motivo por autocomplete10/3, referencia y justificación, revisión
de saldo y modal detallado con Cancelar. Cancelar el modal no envía ni mueve dinero.
Lista con filtros, paginación y Excel; retorno conserva contexto y errores conservan captura.

Pago real: egreso completo NOMINA por intento, abonos de sus cuotas y estado de nómina
en una transacción. Rechazo SIN salida: historial RECHAZADO, no egreso ni abonos;
sigue pendiente y puede intentarse nuevamente. No usar rechazo si el dinero sí salió.
Reversa: confirmar fondos realmente devueltos a la cuenta original, motivo financiero
de ingreso, referencia y motivo. Publica NOMINA_REVERSO ligado al egreso original,
compensa exactamente sus abonos y devuelve sólo ese empleado a pendiente. Otros
pagos/nóminas permanecen intactos. Nuevo pago genera otro intento, no sobrescribe el anterior.
Historial muestra egreso, compensación, comprobantes/motivos y actor/fecha de devolución.

## Préstamos conectados

Entrega real ahora habilitada por app.nomina.prestamos.entrega-habilitada=true en
application.properties; el servicio conserva default seguro false si no se configura.
La ruta de nuevos egresos consolidados queda deshabilitada por defecto y en properties
(app.nomina.consolidada-habilitada=false). No activar esa compatibilidad en operación;
se habilita sólo en fixtures aisladas de regresión para comprobar historia antigua.

El generador mensual incluye min(parcialidad,saldo), sólo desde el primer descuento.
No calcula intereses ni resta deuda por meses transcurridos. Autorizar reserva la cuota,
no abona; otra nómina para ese préstamo requiere resolver antes el pago pendiente para
no reservar dos veces. Un borrador previo se revalida al autorizar, nunca se sobrescribe.
Pago individual abona y puede dejar LIQUIDADO. Reversa restaura el abono y puede volver
a ACTIVO, aun si posteriormente hubo otras nóminas pagadas: no las recalcula.
Configuración → Préstamos → Historial → Ver abonos y compensaciones permite consultar
abonos vigentes/compensados con sus saldos históricos y enlace al pago.

No duplicar préstamo como deducción habitual. Monedas, cuotas, cuentas, fondos,
versiones y pertenencia institucional se validan de nuevo en servidor/transacción.
No se han implementado abonos externos manuales, intereses, suspensiones, devolución
de la entrega del principal ni refinanciamientos; la entrega no se cancela como borrador.
Semanal/quincenal/proporcionales y fórmulas legales siguen fuera del cálculo automático.

## Ejemplo manual controlado — no tocar la aplicación histórica

No repetir BOR-000001/rev1/APLICADA/$12,500.00/movimiento10 de esta computadora.
Usar cuentas y empleados exclusivamente de capacitación y datos no confundibles con reales.
Antes de capturar, comprobar cuáles ya existen y no duplicarlos.

1. Elegir un mes completo FUTURO que no esté autorizado/aplicado, ejemplo junio2027,
   calendario mensual activo, empleados de capacitación Carlos y Ana, ambos con
   ingreso laboral confirmado y calendario asignado. Una sola configuración por empleado.
2. Vigencias que cubran todo junio: Carlos sueldo $12,000.00 y bono habitual $500.00;
   Ana sueldo $9,000.00 sin adicionales. No cambiar sueldos reales por esta prueba.
3. Cuenta institucional de capacitación con $30,000.00, moneda MXN y fecha inicial
   no posterior al día real del registro. Motivos financieros compatibles con egreso
   de préstamo/nómina e ingreso de devolución; pueden ser distintos.
4. Catálogo Deducción/Con saldo y préstamo de Carlos: principal $3,000.00, parcialidad
   $500.00, primer descuento 01/06/2027. Guardar sólo borrador: saldo préstamo0,
   cuenta30000. Cancelar dentro del modal no registra una entrega.
5. Sólo en el escenario de capacitación controlado: revisar y confirmar entrega.
   Cuenta27000 y préstamoACTIVO/saldo3000. No vuelve a permitir entregar ni cancelar
   como borrador. Guardar la referencia/comprobante ficticio para identificar la prueba.
6. Preparar junio: Carlos neto12000 (12000+500-500), Ana9000, total21000.
   Revisar ambos desgloses y guardar. Saldo préstamo sigue3000.
7. Autorizar con motivo: AUTORIZADA, cero pagos, cuenta27000/préstamo3000.
   No se puede editar/reemplazar la revisión ni quitar empleados autorizados.
8. Registrar rechazo de Carlos con referencia y motivo, sin salida de dinero:
   sigue pendiente, historial conserva rechazo, cuenta27000/préstamo3000.
9. Registrar pago de Carlos: revisar cuenta/motivo/referencia/neto12000 y modal;
   confirmar salida real del escenario de prueba. Cuenta15000/préstamo2500,
   PARCIALMENTE_PAGADA. Reintentar la misma confirmación no crea otro pago.
10. Registrar pago de Ana9000: cuenta6000/PAGADA. Su pago no modifica el préstamo de Carlos.
11. Desde Carlos, registrar reversa sólo después de confirmar devolución real del
    escenario de capacitación: ingreso12000 a cuenta original, saldo18000, préstamo3000,
    Carlos pendiente/Ana pagada. Historial conserva rechazo, pago revertido y compensación.
12. Volver a pagar Carlos12000: cuenta6000/préstamo2500/PAGADA; nuevo intento.
    Consultar estados, historial de abonos y Excel filtrado; no son pruebas ya confirmadas.

La prueba de liquidación con cuotas posteriores requiere fondos adicionales y periodos
posteriores preparados uno a uno; no dar por probada manualmente sin realizarla.
La aceptación técnica sí verificó última cuota250 para préstamo1250/parcialidad500,
liquidación y reapertura por reversa de un pago anterior sin modificar nóminas posteriores.

## Seguridad, historia y evidencia técnica

Permisos existentes, sin grants: autorización/lectura privada salarial; pagar exige
administración salarial y registro financiero; reversa añade revertir movimientos.
No basta ocultar botones. CSRF/IDOR, instituciones, cuenta institucional activa,
moneda, fondos y solicitud única; locks nómina→empleado→préstamos→cuenta.
Autorización conserva locks periodo→nómina→empleados y revalida las fuentes actuales.
Nómina/préstamos no se editan históricamente; aplicación antigua intacta.
El libro global usa folios opacos, no nombres/códigos de empleado ni referencias privadas.

V134: autorización inmutable, intentos con único pago vigente por empleado de la revisión,
reversa única, abonos y compensaciones inmutables, vista de saldo y triggers de historia/
estado/coincidencia. Sin backfill, grants, ni división de egresos históricos.
1003Java de suite +4IT PostgreSQL17 aislados y126JS correctos. El total7 del ejecutor
IT incluye3unitarios ya contados en1003: no sumarlos de nuevo. Pruebas de concurrencia,
última cuota/liquidación/reapertura, rechazo, devolución real confirmada, otro empleado
intacto, permisos/CSRF/IDOR, POST reales, captura/nonce renovados, Excel y abonos.
Fallo deliberado después de registrar egreso: rollback de dinero/intento/abono completo,
reintento posterior correcto con mismo formulario. No saldo parcial ni cargo huérfano.
QA visual ficticia de escritorio/móvil/claro/oscuro, confirmación/Cancelar sin enviar.

Pendiente: aceptación humana integrada y luego publicar guía de procesos confirmada.
No declarar cerrado todo el sistema: expedientes familiares, timeout de sesión y lista
de liberación general siguen pendientes fuera de esta fase.
