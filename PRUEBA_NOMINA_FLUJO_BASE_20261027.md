# Ensayo del flujo ordinario de nómina sin calendarios

Estado: **propuesto; pendiente de confirmación manual del usuario**. No repetir las
capturas ya aprobadas. Este ensayo no implica timbrado ni dispersión bancaria real.

## Retoma actual — paso5, 28/10/2026

El usuario ya avanzó a **Autorización y pagos por empleado**. Verificado:
**BOR-000002 · JUL-2027-RED-TEST · Borrador · 1 empleado (Diana) · $14,187.15**.
No repetir preparación, Incluir ni guardar otro borrador. El apartado histórico
siguiente describe el punto anterior. Continuar directamente en **5. Revisar fuentes
y registrar el pago**, desde Preparar pago colectivo. Usar banco10,000/caja4,187.15,
agregando la segunda cuenta; todavía no consta pago confirmado. BOR-000001 de enero intacto.

## Punto exacto para retomar mañana — confirmado el 27/10/2026

Ya existe julio `JUL-2027-RED-TEST` y Diana tiene el concepto único EXTRA250 asignado.
No repetir los apartados 2–3. El usuario llegó a la vista previa, filtró a Diana y
confirmó **14,000 + 630 − 442.85 = $14,187.15**. Su fila decía **No incluido**.
Todavía no confirmó Incluir, guardar borrador, aplicar ni pagar esta nómina.

1. Iniciar sesión con el administrador persistido, no el usuario restringido `prueba`.
2. Entrar a **Personal y nómina → Nóminas** y comprobar primero si ya existe un
   borrador de julio con Diana. Si existe, abrirlo y revisar; no crear otro.
3. Si no existe, **Nuevo borrador → Periodo JUL-2027-RED-TEST → Opciones de selección
   → Elegir empleados específicos → Preparar nómina del periodo**.
   La vista previa anterior puede haber caducado; no reutilizar a ciegas su URL UUID.
4. En el filtro Empleado seleccionar a Diana. Filtrar no la incluye automáticamente.
5. Pulsar **Incluir** en su fila. Comprobar **1 empleado y $14,187.15** globales;
   sueldo14,000, percepciones630, deducciones442.85. Los demás no deben estar incluidos.
6. Guardar el borrador siguiendo los pasos restantes del apartado 4, probar Cancelar
   en su confirmación y confirmar después. Guardar no mueve dinero.
7. Seguir apartados 5 en adelante para revisar fuentes/pago/cancelación. Anotar saldos
   actuales de cuentas antes de pagar: los saldos viejos escritos aquí no son garantía.
8. **No aplicar tratamientos a este ensayo de julio**: su referencia permanece en
   $14,187.15. Las nuevas decisiones se prueban aparte con
   `PRUEBA_TRATAMIENTOS_NOMINA_20261027.md` después de cerrar este caso ordinario.

## Alcance y límites de esta entrega

Periodos independientes, cálculo proporcional, fotografía del borrador, pago ordinario
colectivo/individual desde varias cuentas y cancelación con devolución exacta. No exige
calendarios. Las tablas de calendarios antiguas permanecen sólo para conservar históricos.

No dar por terminado todo el plan: tratamientos **No aplicar/Posponer por concepto** y
obligaciones adicionales, liquidaciones neto cero con contrapartida contable, recibos
nuevos y permisos de operación separados requieren cierre posterior. Los netos cero
no generan egresos ficticios. Chequeras y rediseño de préstamos quedaron expresamente
fuera de esta entrega; un pago multicuenta con cuotas de préstamo se bloquea sin afectar
dinero. Las rutas históricas anteriores no equivalen a esas funciones nuevas.

## 1. Datos que ya existen en esta computadora

Consulta acotada del 27/10/2026. El usuario confirmó sus capturas; no crearlas otra vez:

- Diana Prueba Puestos, `EMP-PUESTOS-TEST-01` (empleado6).
- Puesto40h/$10,000 del01/06/2027 al15/06/2027;48h/$14,000 desde16/06/2027.
- `TRANS-RED-TEST`:50mensuales hasta30/06/2027 y80 desde01/07/2027.
- `DED-RED-TEST`: deducción100semanales desde01/06/2027.
- `QUIN-RED-TEST`: percepción150quincenales desde01/06/2027.
- Catálogo `EXTRA-RED-TEST` activo, percepción ocasional; todavía sin asignación única.
- No existía julio2027 al consultar. Reutilizarlo si el usuario lo creó después.

No utilizar la cuenta restringida `prueba`. Iniciar con administrador persistido con
Empleados, Datos laborales, Puestos/salarios, Configuración de nómina, registro de
movimientos y permiso de revertir para la cancelación. El acceso de recuperación no
autoriza movimientos financieros.

## 2. Crear julio, sin calendario

1. Menú **Administración → Personal y nómina → Periodos de nómina**.
2. Buscar `JUL-2027-RED-TEST`. Si ya existe, abrir Ver y revisar fechas; no duplicarlo.
3. Si no existe, pulsar **Nuevo periodo** y capturar:

| Campo | Valor |
|---|---|
| Código | JUL-2027-RED-TEST |
| Inicio | 01/07/2027 |
| Fin | 31/07/2027 |
| Fecha prevista de pago | 31/07/2027 |

4. Pulsar **Revisar periodo**. El modal no debe pedir calendario.
5. Probar **Cancelar**: sólo cierra el resumen y conserva la captura.
6. Revisar nuevamente y confirmar. Consultar tabla, detalle y Excel:31días y fechas
   correctas. Guardar el periodo no mueve dinero.
7. Un periodo utilizado por nómina o concepto único ya no permite reescribir sus fechas.

## 3. Completar el concepto único de250

1. Menú **Personal y nómina → Empleados**; buscar Diana por código.
2. **Ver expediente → Configuración de nómina → Agregar concepto**.
3. Seleccionar `EXTRA-RED-TEST`, importe250, modalidad **Una sola vez, en un periodo**.
4. Seleccionar julio desde las sugerencias. Las fechas deben quedar01/07–31/07/2027.
5. Motivo: `Percepción única de250 para la prueba completa de julio2027.`
6. Revisar modal, probar Cancelar y después confirmar. No agregar otra vez transporte,
   deducción semanal ni percepción quincenal. Esta captura todavía no paga nada.

## 4. Calcular sólo a Diana en un borrador de ensayo

1. **Personal y nómina → Nóminas → Nuevo borrador**.
2. Seleccionar `JUL-2027-RED-TEST` en Periodo.
3. Abrir **Opciones de selección → Elegir empleados específicos**. Es importante:
   Todos incluye también a otros empleados elegibles de esta base.
4. Pulsar **Preparar nómina del periodo**.
5. En la vista previa, buscar Diana y seleccionar únicamente su fila. La búsqueda no
   desmarca automáticamente a personas seleccionadas previamente.
6. Comprobar el resumen global: **1empleado** y el desglose siguiente:

| Partida | Importe julio |
|---|---:|
| Sueldo mensual | $14,000.00 |
| Transporte mensual | $80.00 |
| Apoyo quincenal:150×2 | $300.00 |
| Percepción única | $250.00 |
| Total de percepciones adicionales | $630.00 |
| Deducción semanal proporcional | $442.85 |
| Neto a pagar | **$14,187.15** |

Semanal significa lunes a domingo. Julio2027 empieza jueves: el importe corresponde
a partes de las semanas de sus extremos y tres semanas completas. Los centavos se
distribuyen con diferencias de acumulados redondeados de cada semana; no es una
división mensual entre cuatro semanas. Cada semana completa suma exactamente100 y
calcular fuera de orden no modifica el resultado.

7. Abrir el desglose y exportar Excel con el filtro de Diana. No debe aparecer sueldo
   base duplicado. En julio no se utiliza el transporte anterior de50.
8. Guardar el borrador con nombre `Nómina julio2027 Diana prueba integral` y motivo
   `Revisión del sueldo, tres frecuencias y concepto único antes de pagar.`
9. Abrir Resumen, Empleados y desglose. Deben conservar los mismos importes. Aún no
   cambió ningún saldo. Si modificas una configuración relevante, se exige revisar y
   actualizar el borrador antes de aplicar; no se paga desde una vista obsoleta.

Como comprobación separada, junio2027 de Diana debe calcular sueldo12,000 por sus dos
puestos, otras percepciones350, deducción428.57 y neto **11,921.43**. Consultar junio
no exige pagarlo ni mezclarlo con julio.

## 5. Revisar fuentes y registrar el pago

1. Antes de pagar, entrar a **Cuentas financieras** y anotar los saldos actuales.
2. Al consultar esta base: `BBVA_COBROS`33,330; `CAJA`16,950. Si cambian por otras
   operaciones, usar el nuevo saldo como base; no modificar saldo inicial para forzar
   este ensayo. No usar `CAJA-CORTE-TEST` para esta prueba.
3. Si falta un motivo apropiado, ir a **Motivos financieros → Nuevo registro**:
   código `NOMINA-RED-TEST`, nombre `Pago y cancelación de nómina de prueba`, naturaleza
   Ambos, categoría Personal, activo. Reutilizarlo si ya existe.
4. Volver a **Nóminas → Ver** del borrador → **Pagos por empleado** y pulsar
   **Preparar pago colectivo**. No hace falta una autorización previa separada.
5. Se muestra **una sola fuente**. Al enfocar **Cuenta de origen** aparecen hasta diez
   cuentas activas de la institución (incluidas las vinculadas a planteles); al escribir
   tres caracteres se filtran las sugerencias. Completar la primera fuente y pulsar
   **Agregar otra cuenta** para capturar la segunda. Si sobra una fuente, pulsar
   **Quitar cuenta** en su tarjeta: conserva las demás y exige revisar de nuevo. Siempre
   debe quedar al menos una fuente. Completar así:

El **Medio de pago · automático** es informativo y está bloqueado: no se captura.
Al elegir caja muestra Efectivo y al elegir banco muestra Transferencia.

| Fuente | Cuenta | Medio automático (no editar) | Importe |
|---|---|---|---:|
| 1 | BBVA_COBROS | Transferencia · banco | $10,000.00 |
| 2 | CAJA | Efectivo · caja | $4,187.15 |

6. Seleccionar el motivo financiero anterior. Referencia `NOM-RED-JUL-2027`.
   Motivo: `Pago íntegro de Diana para julio2027 desde banco y caja.`
7. Pulsar **Revisar distribución**. Verificar1empleado, total14,187.15 y los dos saldos
   posteriores. Se puede volver a revisar sin registrar dinero.
8. Pulsar Confirmar y registrar pagos. Probar **Cancelar dentro del modal**: cero
   egresos. Revisar y confirmar sólo después de comprobar las cifras.
9. Resultado esperado: empleado Pagado, dos fuentes guardadas y dos egresos en
   **Movimientos financieros**, total14,187.15. Con los saldos iniciales anteriores:
   banco23,330 y caja12,762.85. No se envió una transferencia bancaria real.
10. **Pagos por empleado → Ver pago e intentos → Historial** muestra cada cuenta,
    importe y movimiento de origen. Los filtros/Excel no alteran totales globales.
11. Reenviar o confirmar desde una pestaña antigua no debe duplicar el pago.

Si una cuenta no alcanza, el mensaje indica disponible, solicitado y faltante;
ninguna cuenta cambia. No compensar el faltante automáticamente con otra fuente.

### Navegación de Autorización y pagos por empleado

La pantalla se organiza en pestañas, con el mismo diseño del sistema:

- **Resumen:** empleados pagados, importe pagado y pendiente global. Antes de pagar,
  aquí aparece **Preparar pago colectivo**; después ya no se ofrece si no corresponde.
- **Pagos por empleado:** enfocar **Buscar empleado** para ver hasta diez empleados
  incluidos en esa nómina; escribir `Dia` para buscar desde tres caracteres y seleccionar
  la sugerencia de Diana. Elegir **Pagado**, pulsar **Aplicar filtros** y después
  **Ver pago e intentos → Historial de intentos** para comprobar las dos cuentas.
  Si sólo está Diana, la ayuda sólo muestra a Diana, no todo el catálogo de empleados.
  Excel respeta la selección por identificador y el estado. Al volver del detalle se
  conservan selección, filtros y pestaña. Quitar la selección con × permite buscar a
  otro empleado; **Limpiar** quita los filtros. También se admite texto sin seleccionar.
- **Decisiones y pendientes:** consultar conceptos pospuestos y decisiones de esta
  revisión. No hace falta confirmar decisiones nuevamente para este caso ya pagado.
- **Cancelación de pagos:** sólo aparece con permisos de cancelación y pagos vigentes.
  No ejecutarla mientras estés comprobando el resultado; corresponde al ensayo siguiente.

Cambiar pestañas no guarda datos ni realiza movimientos financieros.

### Simular el importe a pagar desde el expediente del empleado

1. Entra a **Personal y nómina → Empleados**.
2. Busca **Diana Prueba Puestos** y abre su expediente.
3. Pulsa **Configuración de nómina** en el expediente. Se abre una pantalla independiente del mismo empleado; entra a **Simulador por fechas**.
4. En **Vigencia desde**, captura **01/07/2027**.
5. En **Vigencia hasta**, captura **31/07/2027**.
6. Pulsa **Simular nómina**. Permaneces en esa pestaña y el resultado aparece debajo de las fechas.
7. Comprueba:

| Dato del simulador | Resultado esperado |
|---|---:|
| Sueldo base proporcional | $14,000.00 |
| Percepciones adicionales | $630.00 |
| Total de percepciones | **$14,630.00** |
| Total de deducciones | **$442.85** |
| Total estimado a pagar | **$14,187.15** |

Se reconoce el periodo **JUL-2027-RED-TEST** por sus fechas exactas, incluyendo el
concepto único de250. No agregarlo nuevamente. El simulador usa la configuración actual
aplicable a las fechas y el mismo cálculo proporcional/redondeo de la preparación.
Concepto, búsqueda, estado y frecuencia sólo filtran la tabla, no recortan la nómina
simulada. No equivale a un histórico guardado ni a dinero pendiente de pagar.
Si modificaste configuración posteriormente, la estimación puede diferir del histórico.

Sin ambas fechas se solicita un rango; fechas invertidas o rangos mayores a366días
muestran explicación sin importe inventado. Si un rango parcial cruza un concepto
de un periodo específico, solicita las fechas completas en lugar de omitirlo sin aviso.

**No genera periodos, borradores, reservas ni movimientos.** Puede consultarse aunque
julio ya esté pagado: eso no autoriza otro pago ni modifica su recibo/histórico.

## 6. Cancelar y volver a pagar el mismo periodo

Sólo para ensayo autorizado y cuando el dinero realmente no salió o fue recuperado.

1. **Nóminas → Ver → Pagos por empleado → Ver pago e intentos → Gestión**.
2. En **Cancelar sólo este pago**, elegir motivo financiero de ingreso/Ambos,
   referencia `RECUP-NOM-RED-JUL`, motivo
   `Recuperación íntegra del pago de prueba en sus dos cuentas de origen.`
3. Confirmar recuperación real; revisar modal, probar Cancelar y luego confirmar.
4. Deben existir dos ingresos compensatorios:10,000al banco y4,187.15a caja.
   Cada cuenta vuelve exactamente a su saldo anterior. El intento original queda
   cancelado/trazable; el empleado vuelve Pendiente en el mismo periodo.
5. La acción **Cancelar todos los pagos vigentes**, en Pagos por empleado, sirve
   para toda la aplicación. Sólo usarla si hay pagos vigentes; exige recuperación,
   justificación y modal. Cualquier fallo revierte toda esa cancelación.
6. Para volver a pagar a Diana, abrir su mismo pendiente y pulsar **Pagar desde una
   o varias cuentas**. Se usa la fotografía original, no la configuración actual.
7. Capturar las fuentes y una nueva referencia, revisar y confirmar. Debe existir
   un folio nuevo, por14,187.15, sin editar ni borrar el intento cancelado.
8. Un pago cancelado no se reincorpora automáticamente a otro colectivo.

## 7. Pago individual sin borrador previo

No intentar pagar otra vez julio ya liquidado: se bloqueará por cobertura reservada
o pagada. Para un ensayo independiente, preparar otro periodo no superpuesto y
consultar sus importes primero; julio no se combina con él.

1. **Personal y nómina → Nóminas → Pago individual**.
2. Seleccionar un periodo programado y un empleado elegible; **Consultar cálculo**.
3. Revisar el neto obtenido. Seleccionar fuentes que sumen exactamente ese importe,
   motivo financiero, referencia y justificación.
4. **Revisar distribución → Confirmar y registrar pagos → modal**.
5. Cancelar el modal no crea nómina ni mueve dinero. Confirmar crea una fotografía
   histórica técnica y paga en la misma transacción; el usuario no preparó un borrador.
6. Esa persona/cobertura ya no debe estar disponible para otro pago colectivo o
   individual. Si aparece reservada en un borrador previo, resolver ese borrador en
   lugar de crear otro periodo para eludir el bloqueo.

## Aceptación pendiente

El ensayo automatizado aislado verificó proporciones, sumas de centavos fuera de
orden, borrador sin dinero, pago multicuenta, fallo forzado de segunda fuente con
rollback completo, doble confirmación, cancelación exacta e individual sin borrador.
Eso **no sustituye** la confirmación del usuario ni publica estos casos como guía
confirmada. Incorporar a la guía integrada sólo después de validar las capturas.
