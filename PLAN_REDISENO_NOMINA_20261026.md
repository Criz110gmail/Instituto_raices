# Rediseño funcional de nómina — acuerdo del 26/10/2026

### Seguimiento de implementación

29/10/2026: pago individual sin borrador y bloqueo de duplicación confirmados:
Diana/SEP-2027-IND-TEST/BOR-000003/NPI-3, egreso6975.71, banco16354.29 final,
cajas sin cambio. V142 guía confirmada. Sigue No aplicar/Posponer; no repetir septiembre.
Mensajes diferencian pago vigente de reserva sin cambiar reglas de cobertura.

28/10/2026: aceptación humana del ciclo de Diana/julio/BOR-000002: cálculo14,187.15,
primer pago NPI-1, cancelación con recuperación por fuente y nuevo pago NPI-2.
Saldos finales banco23,330/caja12,762.85; caja de corte1,150 sin cambios. Guías
V140/V141 confirmadas sólo para esos recorridos. No repetir julio. Próximo ensayo:
pago individual sin borrador en otro periodo; luego No aplicar/Posponer y controles
pendientes. No equiparar integración automatizada con aceptación manual del resto.

27/10/2026 V139: decisiones No aplicar/Posponer auditadas por empleado/concepto en
borrador; vista efectiva sin reescribir copias. Pendiente completo sólo individual;
complementario positivo por copia del periodo original y fuentes/cancelación exactas.
Captura separada, modales, seguimiento/Excel. Obligaciones complementarias firmadas
netas cero/negativas conservadas, pero recuperación/liquidación especializada todavía
pendiente, al igual que contabilidad neto0, recibos nuevos y permisos separados. No
declarar entregas4–7 totalmente cerradas. Prueba humana propuesta en
PRUEBA_TRATAMIENTOS_NOMINA_20261027.md, después del julio ordinario de Diana.

27/10/2026: usuario autorizó completar flujo base sin calendarios hasta pago desde
varias cuentas/cancelación; chequeras y rediseño de préstamos expresamente después.
V137/V138 desplegadas para el flujo ordinario: periodos independientes, cálculo por prefijos de referencia,
fuentes por intento y pago directo sin borrador previo del usuario. No declarar todo
el plan implementado: tratamientos por concepto/pospuestos, contabilidad neto0,
recibos nuevos y permisos separados conservan pendientes. La conservación de tablas
legadas no vuelve a exigir calendarios al usuario. Ensayo ordinario propuesto en
PRUEBA_NOMINA_FLUJO_BASE_20261027.md; aceptación humana todavía pendiente.

Entrega2 autorizada el26/10/2026: configuración por concepto dentro del empleado,
frecuencias/vigencias, nueva aplicación única y permisos privados. V136 aditiva,
sin convertir filas legadas ni habilitar cálculo/pagos nuevos. Ensayo humano propuesto
en PRUEBA_REDISENO_NOMINA_ENTREGA2.md. Usuario pidió continuar y dejar para27/10 los
tres bloques restantes de entrega1; no marcar aceptación integrada. La configuración
de periodos sin calendario y el motor proporcional corresponden todavía a entrega3.
Se retiran Jornadas/Plantillas de la navegación de Puestos y Configuración de nómina
como entrada separada; sus datos y rutas históricas se conservan.

Entrega1 autorizada después de este acuerdo: V135, puestos con horas/sueldo mensual
y asignación histórica única desde Datos laborales del empleado. No se migraron
importes legados ni se habilitó el nuevo motor de cálculo/pagos. Ensayo humano en
`PRUEBA_REDISENO_NOMINA_ENTREGA1.md`, todavía pendiente. Las afirmaciones inferiores
“sólo documentado/no iniciado” describen el momento del acuerdo original; esta nota
supera ese estado para entrega1 y la configuración de entrega2 descrita arriba.
Entregas3–7 no se declaran implementadas.

## 1. Estado, alcance y prioridad

Diseño funcional acordado con el propietario durante el análisis. Este documento
documenta el destino; **no declara implementadas estas funciones** ni autoriza por sí
solo su despliegue, migración, eliminación de tablas o modificación de registros.

Sustituye, como destino funcional, la organización propuesta en
`PLAN_SIMPLIFICACION_NOMINA.md` y `PLAN_PERSONAL_Y_NOMINA.md`. Esos documentos y las
pruebas V125–V134 se conservan como antecedentes de lo realmente construido.
No confundir las cinco etapas anteriores con las entregas de este rediseño.

Objetivo: reducir capturas reales, no sólo menús. El usuario asigna un puesto una
vez, configura conceptos dentro del empleado, define fechas del periodo y revisa
una nómina antes de pagar. Calendarios, jornadas y plantillas separadas dejan de ser
requisitos del nuevo flujo; sus referencias históricas no se eliminan por ocultarlos.

Flujo acordado:

**Puestos → Empleado y conceptos → Periodo → Cálculo/borrador → Pago colectivo o
individual → Recibos, pendientes, cancelaciones e historial.**

No contempla dispersión bancaria automática, timbrado fiscal ni cálculo legal
automático de IMSS, Infonavit, impuestos o prestaciones. Registrar conceptos con
esos nombres no implementa sus fórmulas fiscales. No prometer “nunca habrá fallas”:
exigir transacciones, controles de concurrencia y recuperación verificables.

## 2. Módulos y organización

| Módulo | Responsabilidad |
|---|---|
| Puestos laborales | Código, nombre, horas semanales y sueldo mensual de referencia |
| Conceptos de nómina | Catálogo general de percepciones y deducciones |
| Empleados | Asignación de puesto y configuración individual por vigencias |
| Periodos de nómina | Cobertura y fecha prevista de pago, sin calendario obligatorio |
| Nóminas | Cálculo colectivo, borrador, excepciones y aplicación |
| Pagos de nómina | Pago individual ordinario, pendiente o extraordinario |
| Préstamos a empleados | Entrega, programación, saldo y reestructuración |
| Cuentas financieras → Chequeras | Control de cheques de cada cuenta bancaria |

Configuración de nómina se integra como pestaña privada del empleado, no como
otra captura independiente que obligue a repetir puesto y sueldo.

En catálogos: menú abre listado con tabla, filtros, paginación, Excel y acciones;
Nuevo/Editar abren formulario separado. Aplicar el diseño existente, no una paleta
nueva. Temas claro/oscuro, móvil/escritorio, filtros alineados, ayudas de información,
autocompletados con primeros diez al enfocar y búsqueda desde tres caracteres.
Conservar filtros, página y tamaño al volver. Confirmaciones con resumen y Cancelar.
Los campos monetarios mantienen captura segura y formato del sistema, sin scroll
que altere cantidades. Ayudas informativas no deben parecer errores rojos.

## 3. Puestos y asignación al empleado

### 3.1 Catálogo

Cada variante tiene código único, nombre, carga horaria semanal, sueldo base mensual
y estado. Puede repetirse el nombre si la variante es distinta:

| Código | Nombre | Horas/semana | Sueldo mensual |
|---|---|---:|---:|
| APOYO_TECNICO_40 | Apoyo técnico | 40 | $10,000.00 |
| APOYO_TECNICO_48 | Apoyo técnico | 48 | $14,000.00 |

El sueldo del puesto siempre es mensual. Las horas describen la carga contratada;
no generan por sí mismas descuentos por faltas ni horas extras.

### 3.2 Asignación única y cambios

En Datos laborales se asigna el puesto una sola vez, junto con ingreso laboral.
Mostrar tarjeta visible con puesto/código, horas, sueldo mensual y fechas aplicables.
La pestaña Configuración de nómina presenta Sueldo base como percepción protegida,
derivada de esa asignación; no solicita buscar el puesto nuevamente ni permite
agregar otro sueldo base como concepto adicional.

Editar el catálogo no cambia silenciosamente el sueldo contratado de asignaciones
existentes. Conservar copia de código/nombre/horas/sueldo y vigencia de cada asignación.

Acción **Cambiar puesto**: nuevo puesto, fecha efectiva y motivo. Cierra el anterior
el día previo y abre el nuevo en una sola operación, sin dos capturas. No es una baja
laboral. Conservar historial; evitar asignaciones solapadas.

Ejemplo junio de 2027: $10,000 mensuales del 1 al 15 y $14,000 del 16 al 30:
10,000/30×15 + 14,000/30×15 = **$12,000** de sueldo para junio.

La baja laboral real se trata por separado; su último día se incluye. Ingresos y
bajas a mitad del periodo sólo generan importes durante los días elegibles.

## 4. Conceptos por empleado

Tabla dentro de Configuración de nómina con concepto, naturaleza, importe,
frecuencia, aplicación, inicio, fin y acciones. Por fila:

- Frecuencia del importe: Mensual, Quincenal o Semanal; mensual por defecto.
- Recurrente durante su vigencia, o sólo una nómina/periodo seleccionado.
- Concepto único: importe íntegro para el periodo, sin repetición ni conversión
  automática como si fuera recurrente.
- Inicio de aplicación y último día incluido, opcional si no termina.

Ejemplos del catálogo: sueldo base protegido, antigüedad, transporte, horas extras,
IMSS, Infonavit, fondo de ahorro y aportación al retiro. Sus nombres no implican
cálculos legales implementados. Preservar aportaciones patronales históricas: no
convertirlas a descuentos del trabajador durante la transición.

Cambiar importe requiere **Aplicar desde**. Dejar de aplicar requiere fecha efectiva;
no borrar la historia. Ejemplo: transporte50mensual hasta30/06/2027 y80mensual
desde01/07/2027. Junio conserva50; julio usa80. Si cambia a mitad del periodo, separar
tramos. Un periodo anterior aún no calculado usa la vigencia histórica, no el último
importe de pantalla. Cancelar una captura nunca utilizada conserva auditoría.

No sumar dos veces sueldo base ni cuotas provenientes de préstamos. Cambiar sueldo
o conceptos no modifica nóminas aplicadas ni actualiza borradores silenciosamente.

Filtros de empleados: Sin puesto asignado; Sin configuración de nómina;
Configuración incompleta; Listos para calcular; además Sin sueldo base y advertencias
operativas según la consulta.

## 5. Periodos, elegibilidad y conversiones

### 5.1 Periodo

Captura principal: código, inicio, fin y fecha prevista de pago. No exigir calendario,
grupo, jornada ni plantilla adicional. La frecuencia de los conceptos determina su
referencia; no adivinar que cualquier rango de quince días es una quincena.
Ayudantes para completar fechas pueden diseñarse sin convertirlos en otro catálogo.

Cada pago individual corresponde a **un empleado y un periodo**. No combinar
periodos en una operación de pago. El colectivo agrupa empleados del mismo periodo.

### 5.2 Elegibilidad

- Empleado activo durante al menos parte de la cobertura: calcular sus días elegibles.
- Con puesto: sueldo base por vigencia más conceptos aplicables.
- Sin puesto, con conceptos: calcular sólo conceptos; advertencia visible
  **Sin sueldo base: se calcularán únicamente conceptos adicionales**.
- Sin puesto ni conceptos: Sin configuración aplicable, sin inventar sueldo cero.
- Configuración inválida/huecos o fechas inconsistentes: explicar el problema; no
  inventar tramos ni omitirlo sin señalización. Distinguir ausencia intencional de base
  de una asignación rota.
- Neto negativo: bloquear pago de ese empleado, estado y color visibles, filtro
  Neto negativo/Requieren atención. No pagar negativo ni recortar deducciones a escondidas.
  Resolver configuración o excluirlo de esa aplicación con motivo; los demás pueden
  continuar con confirmación expresa de los no incluidos.

### 5.3 Política de conversión acordada

Repartir por días del periodo de referencia, no mensual/4 semanas:

- Mensual: días reales de cada mes.
- Quincenal: 1–15 y16–último día del mes; cada mitad completa recibe el importe
  quincenal íntegro, aunque tengan distinta cantidad de días.
- Semanal: lunes a domingo; semana completa recibe importe semanal íntegro.
- Periodo parcial: importe de referencia / días de referencia × días elegibles.
- Cruces de mes, quincena, semana o cambio de configuración: calcular cada tramo
  con su referencia y vigencia; no aplicar un único divisor a todo el rango.

Transporte300mensual en junio: 7días=70; cuatro tramos de7días suman280 y los2días
restantes20; mes completo300. Julio: 300/31×7=67.74. No exigir nómina extra de dos
días: una semana que cruza meses se descompone en sus tramos.
Transporte100semanal: semana completa100; tres días100/7×3=42.86.

### 5.4 Redondeo y control de cobertura

Usar aritmética decimal de precisión suficiente y resultado monetario de dos decimales.
Conservar base, divisor, días, tramos, importe antes de redondear y ajuste aplicado.
Al completar referencia, el total debe coincidir exactamente con el importe que
corresponde a esa vigencia; distribuir/ajustar centavos con regla determinista visible.

**No usar el ajuste de centavos para cobrar un importe excluido ni liquidar un
pendiente.** Diferenciar calculado, pagado, pospuesto, excluido y cancelado. No asumir
que periodos previos ya se calcularon o pagaron. Deben probarse cálculo fuera de orden,
cancelaciones y recálculos. El algoritmo exacto y modo de redondeo son detalles técnicos
a especificar con pruebas antes de implementar el motor, sin cambiar este acuerdo.

### 5.5 Evitar dobles liquidaciones

Validar empleado y días cubiertos, no sólo código de periodo. Si ya existe liquidación
ordinaria/reserva/pendiente para días superpuestos, bloquear y mostrar origen/fechas
y solución. No recortar cobertura silenciosamente. Extraordinarios no repiten base y
usan identidad propia de operación/conceptos para permitir bonos legítimos distintos.

## 6. Borrador, excepciones y reservas

Seleccionar periodo → Calcular nómina → resumen global y tabla por empleado con
percepciones, deducciones, neto y desglose. Guardar borrador nunca mueve dinero,
no recupera préstamos y no emite cheques. Es editable con revisiones y auditoría.

Totales visibles: Neto del periodo; Pendientes anteriores (para consulta, no se suman
a otro colectivo); Total a pagar ahora; Importe pospuesto; costos patronales separados
cuando correspondan. No confundir costo de la escuela con dinero neto a empleados.

### 6.1 Acciones con justificación

Empleado: Incluir pago; Posponer pago completo; Excluir de esta aplicación.
Concepto: **No aplicar en este periodo** o **Posponer**.

| Acción sobre concepto | Percepción | Deducción |
|---|---|---|
| No aplicar | No genera derecho pendiente por ese concepto, con motivo | No se descuenta; una deuda subyacente no desaparece |
| Posponer | Disminuye lo entregado ahora y conserva percepción pendiente | Aumenta neto actual; conserva recuperación pendiente |

Posponer percepción no significa perderla. Se paga individualmente en su periodo
original. Recuperación de cuotas no descontadas se administra en Préstamos, sin
duplicarla como una deuda adicional. El modal muestra el efecto sobre el neto.

### 6.2 Reserva

Empleado incluido en borrador queda reservado para ese periodo/cobertura. No se
permite pago individual paralelo. Mostrarlo bloqueado, no ocultarlo sin explicación:
“Incluido en BOR-…; abre el borrador para continuar o retirar al empleado”.
Retirar empleado o descartar borrador exige motivo/confirmación y libera reserva.
No reservar dinero por guardar borrador. Reservas vencidas por error técnico requieren
recuperación auditable, no liberación arbitraria que permita doble pago.

Aplicar con pago pospuesto libera la reserva para pagarlo individualmente; mantiene
la obligación y fotografía originales. **Nunca se suma un pospuesto a otra nómina
colectiva**, ni se combinan dos periodos en un pago.

### 6.3 Cambios de configuración

Aviso con empleados y diferencias efectivas que afecten al periodo; acciones Recalcular
afectados o Conservar borrador por ahora. Conservar no permite aplicar información
desactualizada relevante sin revisión/actualización. Cambios futuros que no afectan
cobertura no bloquean innecesariamente. Recalcular conserva revisión previa y exige
revisar las excepciones afectadas. Pago individual posterior a otra consulta obliga a
revalidar reserva y totales; no pagar desde una vista antigua.

## 7. Aplicación colectiva e individual

### 7.1 Fuentes y confirmación

El usuario distribuye importe entre cuentas y medios. Diferenciar fuente de dinero
(cuenta) de medio (Efectivo/Transferencia/Cheque). Presentar resumen claro por cuenta,
por medio y total. Propuesta automática de distribución entre empleados revisable;
conservar asignación exacta empleado/pago/cuenta/medio para cancelar después.

Suma exacta contra neto a entregar, cuentas autorizadas/activas, moneda compatible y
saldo disponible validado bajo bloqueo. Sin saldo, no realizar ninguna afectación:
“Banco A: disponible10000, solicitado12000, faltan2000”. Borrador sigue sin aplicar.
No convertir insuficiencia de saldo en posposición automática.

Revisar y aplicar nómina → modal con periodo, empleados, cuentas/importes, total,
excluidos/pospuestos, cuotas y Cancelar. Confirmar registra todo o nada. El aviso
aclara que son registros internos, no transferencias enviadas al banco.
Se paga neto completo o se pospone; abonos parciales de sueldo quedan fuera de esta
primera definición. Dividir entre cuentas no equivale a pago parcial al empleado.

### 7.2 Pagos individuales

Módulo Pagos de nómina: periodo + empleado + tipo. No exige borrador previo.
Consultar cálculo → detalle → cuentas/medios → Revisar y pagar → Confirmar/Cancelar.
Tipos:

- Ordinario: cálculo nuevo del periodo, sujeto a coberturas/reservas y configuración.
- Pendiente: fotografía de liquidación ya confirmada/pospuesta/cancelada en su periodo
  original, no recálculo con configuración de hoy.
- Extraordinario: conceptos adicionales seleccionados, sin repetir sueldo base,
  motivo y folio propios; permite varias operaciones legítimas distintas.

Lo pagado individualmente no se vuelve a pagar por colectivo; sí permanece visible
en resumen e historial del periodo como Pagado individualmente. Un concepto único
consumido no se vuelve a pagar; cancelar compensa su consumo según la operación.

### 7.3 Estados visibles

- Borrador: modificable/recalculable con historial, sin dinero.
- Aplicada con pendientes: se pagó lo incluido; hay derechos pendientes expresamente
  pospuestos o reabiertos. No equivale a fallo parcial de una transacción.
- Aplicada y pagada: no quedan obligaciones vigentes pendientes.
- Liquidado sin salida de dinero: neto cero con efectos confirmados, cuando proceda.
- Pagos Cancelados y borradores Descartados conservan historial.

Aplicada queda inmutable: pagos posteriores y cancelaciones son eventos vinculados,
no edición del borrador original. El estado agregado se deriva de hechos vigentes.

### 7.4 Neto cero

Mostrar importes cero en tablas, detalles y recibos. No pedir cuenta para neto cero,
ni inventar egreso cero. Sí confirmar liquidación y generar efectos contables reales.
Ejemplo percepción500−cuota préstamo500: neto0, cuenta no cambia, préstamo abona500,
registro contable y fotografía íntegros. Si no hay conceptos/configuración no crear
una liquidación ficticia de cero para ocultar el problema.
Verificar cómo representa la arquitectura actual asientos sin flujo de efectivo;
no afirmar que un movimiento de caja cero sustituye contabilidad. Definir cuentas
contables y contrapartidas antes de implementar ese asiento.

### 7.5 Cancelar pago

Nombre visible **Cancelar pago**, aunque técnicamente se conserve una reversa.
Motivo obligatorio, previa y confirmación; parcial por empleado o de la aplicación
completa según autorización. Movimientos inversos a las cuentas originales, compensación
exacta de préstamos y conceptos, pago original cancelado, liquidación vuelve pendiente.
El nuevo pago tiene folio distinto y sólo puede hacerse desde Pagos de nómina.

Confirmar dinero no entregado o realmente recuperado; no restituir disponibilidad
ficticia si el empleado/banco aún lo tiene. Cancelar aplicación no recupera transferencias.
Si no hubo recuperación, mantener su seguimiento sin sumar efectivo inexistente.

Cancelar pago no cambia fórmula/importes originales ni libera fechas para duplicar
liquidación. Corrección de cálculo aplicado es una operación distinta: su protocolo
de compensación/reemplazo debe diseñarse antes de habilitarlo. No permitir editar
históricos bajo el nombre de Cancelar. Cancelar toda una aplicación requiere validar
que todos sus efectos puedan revertirse; si no, bloquear sin reversión a medias.

## 8. Préstamos a empleados

Módulo independiente con empleado, principal, condiciones, frecuencia de recuperación,
cuotas, fecha inicial/final prevista, entrega desde cuenta y saldo visible. Tarjeta
de préstamo en empleado. Calendario completo de cuotas y confirmación antes de aceptar.

Primera modalidad acordada: sin intereses o **interés fijo total expresamente
identificado**, no confundirlo con interés mensual. Porcentajes/interés sobre saldo
requieren especificación adicional; no inferir fórmulas ni validez legal automática.

Separar solicitado, entregado, capital pendiente, interés pendiente, total pendiente,
cuotas pagadas/restantes y próxima cuota. Aprobación/programación/borrador no recuperan
saldo. Descontar y abonar sólo al liquidar efectivamente la nómina (incluido neto0 con
compensación real). Evitar otra deducción habitual del mismo préstamo.

Al vencer fecha final con deuda: Vencido con saldo pendiente, no Liquidado; no
continuar descuentos automáticos fuera del plazo sin reprogramación autorizada.
La falta de neto suficiente no recorta cuota ni liquida deuda silenciosamente.

### Reestructuración

Acción Reestructurar préstamo: saldo real a fecha, plazo/frecuencia/interés nuevos,
inicio y motivo; comparar condiciones y mostrar tabla previa. Sustituye sólo la
programación pendiente; pagos anteriores se conservan. No entrega dinero ni genera
otro egreso por extender plazo.

Ejemplo sin intereses:6000/12meses=500; seis cuotas efectivamente pagadas=3000,
saldo3000. Extender saldo a12meses sin interés=250/mes. Si se acuerdan300 de interés
fijo total nuevo: capital3000+interés300=3300;12cuotas275. Intereses previos pendientes
se muestran separados; no capitalizarlos ni cobrarlos dos veces automáticamente.

Bloquear reestructuración incompatible con cuotas ya comprometidas en una nómina
confirmada pendiente de pago. Resolver primero la operación existente; no crear dos
programaciones simultáneas para recuperar la misma deuda. Cancelar pago compensa
abono y reabre el saldo correspondiente, conservando origen.

## 9. Chequeras vinculadas a cuentas bancarias

No crear cuenta financiera “Cheque” con saldo propio. Chequera pertenece a banco;
cheque es medio/documento de pago, no otra bolsa de dinero.

En Cuentas financieras → cuenta bancaria → pestaña Chequeras: nombre, rango de
folios y activa/inactiva. Listado y formulario separados coherentes con el sistema.
Pago cheque: cuenta bancaria, chequera activa, número, fecha de emisión, beneficiario
propuesto desde empleado e importe. Proponer disponible, verificable/modificable
para coincidir con documento físico. Modal muestra todos esos datos.

- Sólo bancos para cheque, no Caja.
- Rangos no superpuestos en la cuenta; folio único por cuenta aun entre chequeras.
- Confirmación reserva/consume folio concurrentemente; no duplicar por dos pestañas.
- Operación fallida no deja afectaciones ni reservas técnicas huérfanas.
- Cheque cancelado conserva número/motivo y no se reutiliza automáticamente.
- Emitir descuenta disponibilidad interna una vez; no afirma cobro bancario real.
- Cancelar exige certeza de que no puede cobrarse o recuperación real pertinente.
- No reintroducir conciliación bancaria descartada como requisito del flujo.

Ejemplo: cuenta20000, cheque5000 → disponible15000; chequera no mantiene otro saldo.

## 10. Consulta, recibos, permisos y seguridad

Detalle de nómina por pestañas Resumen, Empleados, Pagos e Historial. Recibo interno
por empleado/pago colectivo o individual, folio, escuela/logo, periodo/tipo, puestos
y vigencias, percepciones/deducciones/neto, cuentas identificadas de forma segura,
medio/cheque, fecha/estado. Cancelados claramente marcados. No CFDI/timbrado.

PDF y documentos con endpoint autorizado de visualización inline en pestaña nueva;
descarga sólo por decisión del usuario. Excel Apache POI usa exactamente los filtros
de pantalla, exporta por bloques sin cargar todo y respeta permisos/privacidad.
Consultas históricas por periodo/empleado y filtros independientes: estado, tipo,
medio y rango de fechas según módulo; ayudas10/3 y paginación servidor.

Permisos legibles, separados por operación:

- Empleados — Consultar configuración de nómina / Administrar configuración de nómina.
- Nóminas — Preparar borradores / Aplicar pago colectivo.
- Pagos de nómina — Consultar / Registrar pago individual / Cancelar pagos.
- Préstamos — Consultar / Administrar / Entregar / Reestructurar.
- Cuentas financieras — acceso a chequeras sujeto a autorización específica a definir.

Sin permiso de configuración, no ver pestaña ni salarios por rutas, autocompletados,
Excel o documentos. Autorización en servidor, alcance institucional/plantel, sin
concesiones automáticas por reorganizar menú. Recuperación/bootstrap no autoriza
operaciones financieras donde se requiere usuario administrativo identificable.

Cada aplicación/pago/cancelación/entrega/abono usa identidad idempotente, bloqueos,
versiones y restricciones de unicidad apropiadas. Revalidar datos y saldos al confirmar;
doble clic, reintento/red perdida y dos pestañas no duplican efectos. Auditoría con
usuario, fecha, motivo, relación de origen, importes antes/después y folios.
No confundir un segundo bono válido con un duplicado técnico.

## 11. Transición y entregas propuestas

El usuario acepta rehacer pruebas y reorganizar tablas si es necesario. **No es una
orden de borrar ahora tablas, historiales, cuentas ni movimientos.** Antes de migrar:
inventario real de esta computadora, respaldo verificable, dependencias, estrategia
de compatibilidad, pruebas aisladas y validación de saldos. No alterar .env ni
publicar respaldos/credenciales. No dividir egresos consolidados antiguos inventando
pagos individuales. Mantener sus consultas históricas y recibos compatibles.

Entregas propuestas, cada una con permisos/diseño/modales/Excel/pruebas pertinentes:

1. Puestos con horas/sueldo mensual y asignación única por vigencias en empleado.
2. Configuración del empleado por concepto, frecuencias, vigencias e historial.
3. Periodos sin calendario obligatorio y motor proporcional con redondeo/borradores.
4. Obligaciones, reservas, excepciones y pagos colectivos/individuales/cancelaciones.
5. Chequeras y cheques integrados con las mismas reglas financieras.
6. Préstamos/interés fijo/reestructuración y conexión exacta a pagos y compensaciones.
7. Cierre integrado de recibos/consultas y guía de procesos tras aceptación humana.

No habilitar una entrega financiera si faltan sus dependencias (por ejemplo, descuento
de préstamo requiere compensación de cancelación; cheque requiere folios seguros).
La secuencia técnica puede ajustar esas dependencias sin cambiar el flujo acordado.
La guía se incorpora por proceso probado, no se espera a finalizar todo. El manual
exhaustivo y demás pendientes generales del sistema no se eliminan por este plan.

### Puntos técnicos que deben resolverse antes de habilitar su entrega

- Modelo/compatibilidad exactos contra las tablas actuales y estados históricos.
- Algoritmo de centavos, referencias parciales, cálculo fuera de orden y cancelaciones.
- Representación contable y contrapartidas de liquidaciones neto0 sin caja/banco.
- Corrección de cálculo ya aplicado, distinta de cancelar y volver a pagar lo mismo.
- Seguimiento de dinero no recuperado y cheques aún cobrables sin restitución ficticia.
- Correspondencia exacta de distribución por cuenta/empleado y sus cancelaciones.

No presentarlos como funciones implementadas o pruebas aceptadas por documentarlos.

## 12. Matriz mínima de aceptación del rediseño

Cada caso requiere ejemplo capturable con módulos/botones, datos, totales esperados,
movimientos/cuentas/préstamos antes/después y confirmación humana antes de marcar guía.

1. Variantes mismo nombre40h/10000 y48h/14000; sueldo único al asignar puesto.
2. Cambio puesto a mitad junio:12000 esperado del ejemplo; historial intacto.
3. Ingreso/baja parcial; sin puesto con conceptos y advertencia; sin configuración.
4. Concepto mensual/semanal/quincenal, cruces, febrero bisiesto y meses30/31.
5. Referencias completas coinciden; centavos visibles sin rescatar exclusiones.
6. Transporte50→80 por vigencia; captura futura no altera históricos ni borradores.
7. Concepto único/extraordinario sin repetir base ni consumir dos veces.
8. Neto negativo evidente/bloqueado; neto0 con asiento y préstamo sin cuenta financiera.
9. No aplicar/Posponer para empleado y concepto, justificación y pendientes separados.
10. Reserva borrador bloquea individual con explicación; retiro/descarte la libera.
11. Configuración cambió: identificar empleado/tramo; recalcular afectados con historial.
12. Pago individual sin borrador y colectivo: jamás duplicar persona/cobertura.
13. Pospuesto sólo pago individual del periodo original; prohibir mezclar periodos.
14. Varias cuentas/medios, suma exacta; insuficiencia muestra disponible/faltante y rollback.
15. Cancelar pago individual/aplicación, compensar y volver a pagar sin perder historia.
16. Préstamo6000/saldo3000 tras pagos reales; reestructurar sin nueva salida de dinero.
17. Cuota vencida/insuficiente, interés fijo total separado, abono y cancelación exactos.
18. Chequeras/rangos/folio duplicado/cancelación; no crear otra cuenta con saldo.
19. Dos pestañas/doble clic/reintento en pago, cancelación, entrega, reserva y cheque.
20. Permisos directos/menú/pestañas/Excel/PDF, paginación/filtros y temas/móvil.
21. Migración: comparar saldos, históricos/egresos previos, permisos y recibos.

## 13. Punto de retoma

Documentado a petición del usuario el26/10/2026; **siguiente trabajo propuesto:
entrega1, previo inventario y diseño técnico de transición**. Implementación todavía
no iniciada ni autorizada por esta solicitud de documentación.

Prueba V134 anterior no concluida: usuario confirmó asignación de calendario de
Bruce y configuración de Ana9000mensual/NOM-MENSUAL-TEST sin adicionales, consultada
al15/06/2027. Es confirmación del usuario, no inventario nuevo de la base.
No consta confirmación posterior de creación del periodo junio ni pagos/préstamo
de ese ejemplo. No repetir registros, no declarar completada la fase5 ni continuar
ciegamente su flujo de calendarios: ahora el destino acordado es este rediseño.
En otra computadora volver a verificar datos disponibles con consultas acotadas,
sin leer .env, antes de proponer nuevas capturas o migraciones.
