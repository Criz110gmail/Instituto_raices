# Préstamos integrados con nómina — 04/11/2026

Actualización 06/11/2026: caso Sofía neto cero, Excel y usuario de sólo Préstamos -
Consultar confirmados por el usuario. PRE-000002 LIQUIDADO, sin salida adicional
por cierre; rutas de modificación denegadas. Consulta de cheques también aprobada.
V160 documenta cierres en Guía de procesos. Ver PRUEBA_PRESTAMO_NETO_CERO_20261106.md
y PRUEBA_PRESTAMOS_SOLO_CONSULTA_20261106.md. Las referencias inferiores a neto0
pendiente son históricas. Siguiente: revisar recibos/consultas; no repetir ejemplos.

Implementación del flujo aprobado. Caso humano principal CONFIRMADO el05/11/2026:
PRE-000001 de Tomás liquidado, una entrega200, recuperación220 incluidos20interés,
cancelación/repago conservados y liquidación anticipada. CAJA415.11/BBVA174.29/corte1150.
Ver PRUEBA_PRESTAMOS_FLUJO_NUEVO_20261104.md. No repetir sus operaciones.
V158 sólo documenta aceptación en guía integrada, sin modificar préstamos ni dinero.
Préstamo con neto cero y verificaciones manuales complementarias siguen separados;
no se declaran aprobados por esta confirmación del flujo positivo.

## En palabras simples

Se registra cuánto se prestará y cómo se recuperará. Guardarlo no entrega dinero.
Al confirmar la entrega se descuenta únicamente el principal de una cuenta. Después,
cada nómina toma las cuotas que corresponden a sus fechas. La deuda baja cuando
se liquida esa nómina, no cuando se prepara o autoriza.

## Decisiones implementadas

- Módulo independiente Personal y nómina → Préstamos, listado, filtros 10/3,
  paginación y Excel completo filtrado. Alta/edición aparte; detalle con Resumen,
  Cuotas y abonos, Gestión e Historial. Retornos conservan filtros de origen.
- Principal y porcentaje **total**, no mensual. Interés = capital × porcentaje / 100,
  redondeado a dos decimales. Capital/interés se distribuyen por separado; última
  cuota ajusta centavos. Sin interés nuevo conserva el interés anterior pendiente.
- Semanal cada siete días; quincenal los días 15 y último del mes; mensual conserva
  el día de inicio donde exista (31 → febrero28/29 → marzo31).
- Entre 1 y 600 cuotas positivas; inicio hoy o futuro, empleado activo e ingreso
  compatible. Deducción activa Con saldo. No recapturar ese préstamo como concepto
  habitual del empleado. La programación tiene inicio y fin calculado.
- Entrega efectiva una sola vez desde caja/banco compatible y con saldo. Revisión
  protegida, confirmación con Cancelar, referencia/motivo y egreso PRESTAMO.
  No se envía una transferencia bancaria. No entrega por cheque en este formulario.
- Sólo cuotas pendientes cuya fecha está dentro del periodo de nómina. No prorratea
  una cuota ni prolonga automáticamente la programación. Vencer no elimina deuda:
  si una cuota no fue cobrada se consulta y reprograma, sin inventar abonos.
- Admite recuperación en pagos de nómina individuales o colectivos/multicuenta,
  incluyendo el flujo de cheques ya existente de nómina. Nómina y abonos son atómicos;
  fallo técnico revierte toda la transacción. Neto negativo bloqueado, sin descuento parcial.
- Cancelar un pago positivo devuelve el dinero a sus fuentes originales y restaura
  capital/interés. Intentos/cheques/programaciones previas quedan conservados.
- Neto cero con préstamo programado: compensación auxiliar auditable contra la
  nómina, sin cuenta ni egreso cero ni NPI. **No es un asiento de contabilidad general**.
  Los cierres cero siguen siendo inmutables y no se añade una acción para cancelarlos.
  Préstamos históricos del modelo anterior no se convierten ni se cierran a cero automáticamente.
- Posponer/no aplicar **todo el empleado** conserva deuda; no permite editar o
  excluir individualmente una cuota de préstamo como si fuera otro concepto.
- Reprogramar sólo capital/interés pendientes; porcentaje nuevo predeterminado 0,
  se aplica sólo al capital restante. No capitaliza interés anterior, no vuelve a
  entregar dinero y no borra abonos. Puede ampliar plazo o acelerar a 1 o 2 cuotas.
- Bloquea reprogramación con cuotas reservadas en nómina autorizada pendiente.
  Si cancelar un pago antiguo restaura deuda de una programación sustituida,
  muestra aviso de reprogramación: no oculta ni descuenta automáticamente ese saldo.
- Activar/desactivar descuento automático cambia recuperación futura sólo mediante
  nueva programación. No incorpora una caja de pagos manuales externos del préstamo.

## Seguridad e historial

Dos paquetes independientes: Préstamos - Consultar y Préstamos - Administrar.
Administrar incluye consultar, crear, editar, cancelar borradores, entregar y
reprogramar. V156 retira Entregar/Reestructurar; conserva los roles que ya tenían
Administrar, sin promover lectores ni permisos especializados aislados. Cada operación se
valida en servidor, con actor administrativo persistido activo, misma institución
y alcance institucional. Entregar requiere además registro financiero y acceso
a la cuenta; consultar préstamos no concede permisos salariales ni de pagos.

V154 es aditiva: prestamo_plan, prestamo_cuota, prestamo_cuota_abono y
prestamo_cuota_cero; mantiene prestamo_empleado, entrega, abonos/compensaciones
históricos. Planes/cuotas/abonos inmutables, importes completos y relaciones
validados por disparadores. Solicitud, versión, sellos y bloqueos previenen
duplicados; la entrega enlaza la programación inicial aceptada. V155 sólo guía
editorial pendiente, sin datos operativos ni concesiones de acceso.

## Verificación

Pruebas automáticas en PostgreSQL17 efímero, nunca sobre datos operativos:
programación sin dinero, interés/capital, entrega única, carrera real dos pestañas,
pagos multifuente, fallo de abono con rollback total, cancelación/repago,
reprogramación sin nueva entrega, reservas, cuota cero compensada y duplicado,
lector sin escritura/salarios, URL/Excel/empleado ajeno y revisión HTTP.
Regresiones separadas de proporcionalidad, neto cero y cheques.
Revisión Chrome 390/768/1440, claro/oscuro, sugerencias, menú, Cancelar/Escape.

La aprobación de pruebas automáticas **no** significa que la prueba humana terminó.
No modificar BOR9 ni los pagos previos de Clara/Diego/Luis para probar préstamos.

Activación local: sóloapp, V154/V155success y healthUP. Suitegeneral1056correctas,
ITpréstamos y tres regresiones PostgreSQL separadas correctas. Base operativa
conserva0préstamos/0entregas/0grants nuevos y72movimientos/18intentos anteriores;
saldos174.29/1195.11/1150 intactos. Sin operaciones de negocio por agente.
