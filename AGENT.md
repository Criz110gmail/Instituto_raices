# Continuidad del proyecto — Nexo Escolar

## Cierre humano de Posponer, cancelar y reponer80 — 29/10/2026

Usuario confirma todos los controles del punto9 del ensayo: Bruce/noviembre2026,
BOR-000004 original, BOR-000005 liquidación; NPI-5 cancelado y nuevo intento pagado
(no asumir su número). Banco4274.29→4354.29→4274.29; cajas12762.85/1150 intactas.
Sueldo12000 no cancelado; pendiente80 pagado y configuración mensual80 conservada
01/11–30/11. NO repetir ni cancelar pagos para documentar. Se conserva intento y
compensación. La lista de conceptos pospuestos se consulta desde BOR-000004,
no desde BOR-000005 (la liquidación no tiene otros pendientes derivados).
Listado Nóminas ahora distingue estado con CSS acotado: borrador azul, pendiente
ámbar, parcial violeta, pagada verde, aplicada histórica gris azulado, cancelada rojo.
Etiquetas y estados internos/filtros/exportación sin cambio. Falta publicar guía
integrada de estos tratamientos; continuidad y manual sí registran aceptación.

## Retoma Posponer transporte de Bruce — 29/10/2026

Usuario confirma que los puntos1–5 del ensayo Posponer coinciden y está en punto6,
Conceptos pospuestos de noviembre2026. No repetir el egreso inmediato12000.
Pendiente entregar los80 de transporte por separado (todavía NO pagados/aceptados).
Ver pagos por empleado del Resumen usa nomina-primary-action turquesa. Filtro de
pendientes incorpora autocompletado compartido, focus10 y búsqueda>=3; endpoint
acotado a nómina/institución y empleados con conceptos pospuestos, no catálogo global.
ID/etiqueta separados con FiltroPendienteNomina: tabla/Excel/paginación usan mismo
ID, conservan etiqueta/estado; búsqueda textual previa compatible. Sin migración.

## Separación de decisión y motivo — 29/10/2026

Usuario confirma historial Bruce/noviembre: NO_APLICAR29/10 09:39 y APLICAR09:43
para conceptos:8; Excel actual Transporte80 y sueldo12000 ambos APLICAR. No repetir
operaciones. Falta confirmar explícitamente neto12080/configuración/cuentas para
cerrar todos los controles; Posponer sigue pendiente. Corrige solape visual con
wrapper nomina-treatment-decision (flex vertical, gap10px, motivo en flujo normal).
Historial usa nomina-cell para separar fecha/usuario y destino/acción. No modificar
datos ni cálculos ni exportación por este ajuste.

## Regla de guías y acciones de decisiones — 29/10/2026

Antes de entregar o publicar una guía, verificar los nombres visibles, rutas y botones
en las plantillas/controladores actuales. No confundir IDs técnicos con etiquetas:
Nóminas → Ver → Detalle por empleado → Ver desglose → Decisiones y pendientes de
este periodo. No se llama Empleados la pestaña del detalle. Los tres accesos de
decisiones (abrir, decidir todo el empleado y Excel) reutilizan nomina-primary-action
turquesa; el desglose incorpora nomina-detail para activar el estilo compartido.
El selector de alcance conserva su comportamiento previo: puede cambiar antes de
confirmar y el sello valida el alcance revisado. No se bloqueó ni cambió el cálculo.
Ensayo humano actual: Bruce/noviembre2026/No aplicar transporte80, aún SIN confirmar.
No publicar ese caso como probado ni ejecutar decisiones/pagos con herramientas.

## Pago individual de septiembre confirmado — 29/10/2026

CIERRE HUMANO: Diana/SEP-2027-IND-TEST (01–15/09/2027), BOR-000003/NPI-3 generado
sin borrador previo. Egreso banco6975.71 a08:44; BBVA_COBROS23330→16354.29.
CAJA12762.85 y CAJA-CORTE-TEST1150 intactas. Usuario confirmó también bloqueo al
consultar mismo empleado/fechas. No repetir septiembre ni cancelar NPI-3.
V142 agrega guía CONFIRMADA9pasos con desglose/revisión/edición/modal/movimiento y
duplicación. No marca como probado No aplicar/Posponer ni concurrencia.
Mensaje CANDIDATOS distingue pago vigente ya registrado de reserva en borrador/nómina
pendiente. EXISTS con mismos empleado/fechas/activo/ignorar y pago PAGADO; primero
aviso pagado, luego reservado, ambos NO_INCLUIDO y no aplican en simulación. Misma
regla de bloqueo, sin cambiar saldos ni elegibilidad. Cancelación restaura aviso de
pendiente, no afirmar que hay pago vigente cancelado. Próximo: No aplicar concepto
en agosto según manual de tratamientos (comprobar periodo/reservas antes).

QA:4Java/package con PostgreSQL17 temporal: distingue reserva/pagado/cancelado;
HTTP de pago duplicado devuelve aviso claro sin formulario de pago ni otro movimiento.
V142 guía CONFIRMADA9pasos/6permisos. QA ficticia retirada; sólo app/contenido
editorial actualizados, sin pagos/roles/.env/commit/push operativos.

## Desglose del cálculo individual antes de pagar — 28/10/2026

Usuario confirma consulta SEP-2027-IND-TEST/Diana neto6975.71 pero pide partidas
visibles. Nueva tabla ¿Cómo se calcula este pago? debajo de neto: sueldo7000,
transporte40, apoyoquincenal150, deducción−214.29; totalpercepciones7190,
deducciones214.29 y neto6975.71. Código/nombre/frecuencia, periodo y fechas; salario
se identifica Sueldo base proporcional. Aportación patronal separada si existe,
sin disminuir neto. En móvil filas apiladas, importe visible sin scroll horizontal.
PreparacionNominaService.partidasIndividuales proyecta JSON de Candidato ya calculado
(sueldos/conceptos/ocasionales/préstamos), no recalcula fórmulas ni consulta importes
de referencia para sustituirlos. Autoriza lectura salarial y empleado; JSON interno,
no parámetro HTTP. Controller carga tabla/periodo del mismo candidato antes de exponer
resultado. Se muestra durante captura y revisión directa. Colectivo y pagos previos
sin cambio. No repetir julio ni declarar pagada septiembre: reanudar revisión de
desglose y después continuar pago individual, todavía pendiente de aceptación humana.

QA:4Java/package con PostgreSQL17 temporal: candidato y4partidas reales exactas
7000/40/150/214.29, total7190/neto6975.71; consulta HTTP no crea borradores/movimientos.
Cierre3Java/package,140JS y16vistas Chrome4anchos/2temas (fixture y render SQL),
sin campos editables en tabla/overflow/POST. QA temporal retirada. Sólo app
actualizada; sin SQL de migración ni pagos/datos operativos/roles/.env/commit/push.

## Pago individual: ayudas y alineación — 28/10/2026

Usuario está en selección de Pago individual, ya creó SEP-2027-IND-TEST (01–15/09/2027)
y todavía no confirmó el pago. Caso esperado Diana7000+40+150−214.29=6975.71,
BBVA_COBROS23330→16354.29, cajas intactas. No crear otro periodo ni repetir julio.
Corrige UI de periodo/empleado: bloque propio nomina-direct-selection y grid2columnas
iguales (1móvil), etiquetas y inputs alineados. Se sobreescribe alto fijo de botones
de sugerencias de .filters sólo en esta ayuda; texto multilínea/altura auto/scroll.
Body nomina-direct libera overflow de form-card para no recortar dropdown antes del
cálculo. Sin cambiar endpoints, focus10/query>=3, parámetros GET ni reglas/pagos.
Retomar seleccionando periodo existente y Diana → Consultar cálculo, luego ensayo
de pago individual propuesto. No declarar ese ensayo confirmado aún.

QA:3Java/package,140JS y16vistas ficticias Chrome4anchos/2temas, antes/después de
calcular. Anchos/alturas alineados, listas multilínea sin recortes/solapes, focus10
y query>=3, selección por ID y GET de consulta preservados, sin overflow ni POST.
Sólo app actualizada; sin SQL ni operaciones financieras ni roles/.env/commit/push.

## Cancelación y nuevo pago de julio cerrados — 28/10/2026

CONFIRMACIÓN HUMANA: NPI-1 recuperado a las15:32 (banco10,000/caja4,187.15) y NPI-2
pagado a las16:41, mismo BOR-000002/JUL-2027-RED-TEST/Diana. Egresos nuevos10,000 y
4,187.15; saldos finales BBVA_COBROS23,330 y CAJA12,762.85. CAJA-CORTE-TEST1,150
sin cambios. Total vigente14,187.15; no repetir cancelación ni pago. Usuario dice todo
correcto. V141 añade guía CONFIRMADA de8pasos, corrige paso8 de guía inicial V140
mediante UPDATE editorial (nunca editar migración previa) y deja otros casos pendientes.
Manual retoma apartado7: pago individual sin borrador en periodo independiente; primero
revisar disponibilidad y cálculo, no usar julio. Después No aplicar/Posponer según manual
de tratamientos. Falta de fondos/concurrencia/núcleos0/negativo no aceptados por esta
confirmación. No ejecutar operaciones financieras ni publicar esos ensayos como probados.

QA:4Java/package con integración PostgreSQL17 ficticia. V141 produce guía confirmada
8pasos/6permisos y guía inicial versión2 con8pasos; no datos financieros operativos.
Test del encabezado de revisión en integración alineado al texto actual. Sólo app
y contenido editorial actualizados; sin roles/.env/commit/push. QA temporal retirada.

## Revisión de pago protegida y edición explícita — 28/10/2026

Usuario autoriza separación captura/revisión. nomina-pago-multiple compartida por
colectivo, pago por fila y directo: tras Revisar distribución sólo muestra tarjetas
con cuenta/medio/saldo anterior/importe/saldo posterior, referencia/motivo y botones
Cancelar, Editar distribución, Confirmar y registrar pagos. No inputs visibles ni
altas/bajas de fuente durante revisión; selector de empleado/periodo directo oculto.
Campos hidden conservan payload y filtros. POST /editar en ambos controladores vuelve
a captura, conserva fuentes/datos/solicitud/versión/fila/periodo/empleado, e invalida
sello/confirmado. Requiere revisar otra vez. No usar Atrás del navegador.
Modal toma cuenta/importe de snapshot visible data-payment-review-source; Cancelar
no paga. Sello/transacciones/bloqueos/idempotencia/saldos siguen intactos en servidor.
Errores al confirmar vuelven a captura conservada. No cambia motor ni SQL.
Re-pago Diana aún pendiente de aceptación humana; no ejecutarlo por agente.

QA:1031Java/package y140JS correctos. Tests de POST editar colectivo/fila y directo:
conservan datos/filtros, invalidan revisión y no invocan pago.8vistas Chrome ficticias
4anchos/2temas: ningún input editable en revisión, modal con ambas cuentas/importes,
Cancelar sin POST y Editar/Revisar conservan captura. Sólo app actualizada; sin DB,
operaciones financieras ni roles/.env/commit/push.

## Botón de pago multicuenta y revisión por aclarar — 28/10/2026

Pagar desde una o varias cuentas en Registrar resultado ahora usa nomina-primary-action
turquesa y encabezado flex alineado con título/ayuda; móvil apila sin desbordar.
Usuario pregunta si cuentas/importes deberían editarse tras Revisar distribución.
No modificar todavía ese flujo: actualmente formulario sigue editable y Volver a revisar
recalcula sello; confirmar coteja captura/saldos y bloquea si cambiaron. Recomendación:
revisión sólo lectura con Editar distribución que regrese a captura preservada, no
dependencia del Atrás del navegador. Pendiente autorización de esa UX. Re-pago Diana
sigue sin confirmación; no registrar dinero ni marcar prueba completa.

QA:3Java/package y8vistas ficticias Chrome4anchos/2temas: botón turquesa, enlace/fila
conservados, sin solaparse con texto, overflow ni POST. Sólo app actualizada.

## Ubicación de pantallas e intento cancelado — 28/10/2026

Usuario confirma cancelación de NPI-1 de Diana: ambas cuentas restauradas; nómina
BOR-000002 Pendiente de pago correcto. Historial interno REVERTIDO, fuentes originales
47/48 y recuperaciones49/50. Nuevo pago aún NO confirmado; no ejecutarlo por agente.
Sólo mejorar presentación: Intento.estadoTexto muestra Pago cancelado (BD REVERTIDO
sin cambiar), distintivo ámbar, explicación y tarjeta de recuperación con referencia,
motivo, fecha/usuario separados. No afirmar deuda actual en cada fila histórica si
después se vuelve a pagar. Encabezado ubicacion compartido en15pantallas de Nóminas,
preparación, desglose, autorización/pagos, multifuente, intentos, decisiones y pendientes.
Multifuente distingue Preparar pago colectivo / Preparar pago por empleado. Main de
autorización dice Autorización y pagos por empleado. Sin modificar rutas/permisos ni
comportamiento financiero. Continuar prueba desde nuevo pago de Diana, no cancelar otra vez.

QA:1026Java/package,139JS, cierre focal3Java/package; test de cobertura de las15
pantallas y etiqueta sin cambiar REVERTIDO.32vistas ficticias Chrome4anchos/2temas
para autorización, multifuente, intento cancelado y decisiones; sin overflow ni POST.
Sólo app actualizada; sin migración/DB/operaciones ni roles/.env/commit/push.

## Julio de Diana pagado y confirmado por usuario — 28/10/2026

CIERRE HUMANO: confirma simulador14000+630−442.85=14187.15 y egresos NOMINA del
28/10/2026 11:32, BOR-000002/JUL-2027-RED-TEST, NPI-1. BBVA_COBROS33,330−10,000=
23,330; CAJA16,950−4,187.15=12,762.85. CAJA-CORTE-TEST1,150 no participa.
No repetir pago ni preparación. V140 sólo agrega guía CONFIRMADA de8pasos detallados,
con permisos de consulta y advertencias; no marca cancelación/re-pago/concurrencia
como probados. Manual retoma apartado6: cancelación y nuevo pago pendientes. Revisar
saldos antes de ensayo: otros movimientos posteriores pueden modificar la referencia.
Botón Configuración de nómina del expediente adopta referencia aprobada turquesa con
texto centrado y flecha en segunda línea, sin cambiar permisos ni ruta.

QA:4Java focales/package incluyen integración PostgreSQL17 ficticia con V140;
guía insertada CONFIRMADA,8pasos y6permisos. Cierre3Java/package,8vistas Chrome
4anchos/2temas: botón turquesa, navegación y sin overflow/POST. En móvil ocupa fila
completa para no cortar palabras. Retirado contenedor ficticio/tmpfs. Sólo app y
contenido de guía actualizados; sin cambios de pagos/saldos/roles/.env/commit/push.

## Configuración de nómina independiente y organizada — 28/10/2026

Por petición del usuario, el acceso Configuración de nómina del expediente abre
/admin/empleados/{id}/nomina, ya no incrusta toda la consulta en la pestaña del
expediente. Conserva condición de permisos. Pantalla independiente con navegación
existente: Conceptos configurados (filtros, tabla, Excel, paginación), Simulador por
fechas (rango y resultado) y Puesto y sueldo (consulta e historial). Cambiar pestaña
no guarda ni paga. GET del simulador vuelve a su pestaña por hash; filtros a conceptos.
Cada formulario conserva los parámetros de la otra consulta; el cálculo sigue usando
sólo fechas. Inputs alineados, tres columnas/ dos/ una según espacio. Sugerencias con
altura automática corrigen solapamiento por altura fija heredada de .filters button.
No se cambió fórmula ni configuración operativa. La comprobación humana del pago de
julio de Diana sigue pendiente: no repetir ni darla por aceptada. Manual actualizado
para entrar a Simulador por fechas y pulsar Simular nómina.

QA de esta reorganización:1024Java/package y139JS correctas.24vistas ficticias de
Chrome (con resultado, sin fechas y cálculo bloqueado;4anchos y2temas): pestañas,
hash, selección/focus10/búsqueda>=3, sugerencias sin solaparse, sin overflow ni POST.
Sólo contenedor app actualizado; no reinicio de BD ni cambios operativos/.env/Git.

## Simulador por fechas sustituye totales de referencia — 28/10/2026

CORRECCIÓN de intención confirmada: usuario no quiere sumas de referencias, sino
importe estimado como nómina completa del empleado. Retirados ResumenReferencias,
resumir y tarjetas por frecuencia. Nueva simulación GET desde fechas desde/hasta,
misma pestaña de empleado; sin fechas pide rango. Tras Aplicar filtros ancla a
#simulacion-nomina. Otros filtros afectan sólo tabla, explícito: no quitar sueldo
o conceptos al simular por buscar un nombre/tipo. Sueldo+adicionales=percepciones;
deducciones y neto destacados. CasoDiana julio14000+630−442.85=14187.15.

PreparacionNominaService.simularEmpleado reutiliza CANDIDATOS y función proporcional,
no copia fórmulas. Param interno simulacion=true sólo en lectura efímera; helper de
TODAS las operaciones reales fija false: no eludir reservas al preparar/pagar. Se
ignora ocupación por nómina/ préstamo reservado únicamente para estimar configuración
actual, aun con cobertura ya pagada. Resto de elegibilidad/moneda/vigencias intacto.
No exponer Candidato/token como orden de pago. Requiere autorización de configuración
de nómina y empleado/institución. No writes/locks financieros ni registro de periodo.

Reconoce periodo PROGRAMADO con mismas fechas para incluir única/ocasionales. Si no
hay periodo exacto y hay conceptos ligados a periodo, explica que seleccione fechas
completas; no dar total parcial engañoso. Sin conceptos ligados permite rango libre
con contexto sólo en memoria. Máximo366días; invertido/incompleto explicado. Estado
inactivo/configuración incompatible/neto negativo muestra motivo sin importe falso.
Estimación con configuración actual, no saldo pendiente ni reconstrucción histórica.
Manual de ensayo actualizado, no aceptación humana ni duplicar julio.

QA:1024Java/package,139JS;1IT PostgreSQL17 aislada adicional: caso exacto14000+630−
442.85=14187.15 (incluye única250), simular cobertura usada sin habilitar candidatos
de pago, rangos sin periodo/únicas parciales, fechas inválidas, permisos y sin nuevas
filas de nómina/movimientos.6Java focales/package final.40vistas ficticias en4tamaños/
2temas para resultado/sin fechas/bloqueado/expediente/resultado SQL real: sin overflow,
erroresJS, POST ni textos encimados. Sólo app actualizada; sin pagos/datos operativos,
SQL/roles/.env/commit/push. Retirado nexo-simulador-qa ficticio/tmpfs y Dockerfile temporal.

## Totales filtrados en configuración de nómina del empleado — 28/10/2026

Usuario especifica Empleados→Configuración de nómina. Fragmento compartido muestra
tarjetas Total de percepciones y Total de deducciones entre filtros y tabla; aportación
patronal histórica separada sólo si existe. SUM/count en BD con el MISMO WHERE/parámetros
de listar, sin LIMIT/OFFSET: incluye todas las páginas y respeta concepto/búsqueda/
estado/naturaleza/frecuencia/fechas/empleado/institución y permisos privados.

No sumar frecuencias ni monedas incompatibles: desglose por frecuencia y moneda;
LEGADO conserva grupo anterior. Son importes de referencia del listado, NO neto ni
simulación del periodo; sueldo base aparte. Versiones canceladas/anteriores cuentan
si los filtros las incluyen, explícito en ayuda. Sin resultados muestra0 y aviso.
Ambos controladores cargan resumen: expediente inicial y listado filtrado independiente.
CSS en forms.css, compartida por ambas vistas; no cargar nómina.css global al expediente.
Nota obsoleta de motor futuro reemplazada por explicación de preparación ya existente.
Manual explica julio:80mensual/150quincenal/250única y100semanal de referencia, distintos
de630/442.85 calculados. No confirmar aún prueba humana ni duplicar pago de julio.

QA:1024Java/package,139JS;1IT PostgreSQL17 aislada adicional valida SUM por frecuencia,
filtros/fechas vacíos e invertidos y total independiente de página.24vistas Chrome
ficticias4tamaños/2temas (con importes/sin registros/expediente real sintético) sin
overflow ni erroresJS, ambas rutas renderizan. Sólo app actualizada, sin pagos/datos
operativos/roles/.env/commit/push. Retirado nexo-totales-config-qa ficticio/tmpfs.

## Autocompletado de empleados incluidos en nómina — 28/10/2026

Buscar empleado en Pagos por empleado reutiliza ayuda focus10/búsqueda>=3, limitada
por nómina/institución/revisión actual (fuentes históricas, no catálogo global).
Nuevo GET /admin/preparacion-nomina/{id}/pagos/empleados/buscar valida acceso salarial,
consulta máximo11, devuelve10+hayMas/noStore. Selección usa empleadoPagoId, no etiqueta
concatenada; texto e ID se conservan en URL/página/detalle/volver y capturas. Sin
selección aún admite texto libre; Limpiar restaura lista. FiltroPagoNomina mantiene
constructor4argumentos compatible y añade ID/texto. Tabla y Excel aplican mismo ID,
estado y ámbito; resumen global no cambia. No ampliar alcance a otros empleados.
Manual añade foco→Dia→seleccionar→Pagado→Aplicar filtros→Ver pago e intentos.
Usuario sigue comprobando pago de julio/Diana, no aceptación financiera integral aún.

QA:1024Java/package y1IT PostgreSQL17 aislada adicional con filtro por ID/etiqueta,
scoping de nómina, búsqueda corta y Excel de un empleado;139JS.8vistas4tamaños/2temas
validan foco10,2caracteres sin consulta/3con consulta, selección/limpiar/ID y sin
overflow/erroresJS; altura automática de opciones evita superposición móvil. Cierre
focal6Java/package correcto. Sólo app actualizada; sin pagos/datos operativos/SQL/
roles/.env/commit/push. Retirado nexo-empleado-pagos-qa ficticio/tmpfs.

## Pagos por empleado organizado por pestañas — 28/10/2026

Usuario está comprobando el resultado de julio y solicita ordenar Autorización y pagos
por empleado. Pantalla nomina-pagos usa student-tabs existente: Resumen (totales/CTA),
Pagos por empleado (filtros/tabla/Excel/enlace intentos), Decisiones y pendientes
(importes/pospuestos/autorizar sin dinero), Cancelación de pagos sólo con permiso y
pagados>0. Sin inventar historial global: el historial por persona continúa en Ver pago
e intentos. Colores/controles/modales existentes, responsive y navegación por teclado.

Filtros/paginación/Limpiar vuelven a #empleados; volver desde detalle conserva filtros
y ese hash. POST de decisiones fuerza pestaña decisiones y conserva motivo; cancelación
fallida fuerza cancelacion conservando captura, éxito vuelve resumen porque puede
desaparecer la pestaña. No cambios de cálculo, SQL, pagos ni permisos. El importe de
decisiones ahora se etiqueta Importe incluido en la revisión (no implica deuda tras pago).
Manual añade navegación; no declarar aceptación financiera integral sin confirmación
del usuario ni repetir borrador/pago de julio. Continúa comprobación de Diana14,187.15.

QA:1022Java/package,139JS y40vistas sintéticas (borrador/pagada/lectura/filtros/error,
4tamaños/2temas) correctas; pestaña activa única, hashes/teclado compartido, permisos,
filtros y modal Cancelar sin POST comprobados; sin overflow/erroresJS. Sólo app
actualizada; no pagos ni cambios de datos/SQL/roles/.env/commit/push.

## Medio de pago automático por fuente — 28/10/2026

Usuario pidió retirar selector redundante y luego precisó mostrar el medio automático
sin permitir editarlo. Fuente sólo captura cuenta e importe; medio readonly informativo:
caja→Efectivo, banco→Transferencia. Se actualiza al seleccionar y se limpia al borrar
cuenta. Autocomplete conserva detalle del resultado en hidden.dataset.detalle y usa
su evento change existente; lógica específica aislada en preparacion-nomina.js.
Endpoints de cuentas devuelven tipo CAJA/BANCO como detalle. Servidor ignora el medio
enviado y siempre lo deriva de cuenta_financiera.tipo al revisar/confirmar, conservando
medio histórico por fuente. No implementar cheques ni permitir cambiar cuenta/tipo.
Manual actualizado; retoma sigue paso5 BOR-000002/Diana14,187.15, sin pago humano aún.

Verificación:5Java/package inclIT PostgreSQL17 aislada con caja y banco, medio enviado
incorrectamente ignorado y sustituido por tipo real;139JS;8vistas focus/cambio/limpieza/
readonly y24de diseño en4tamaños/2temas sin erroresJS ni overflow. Sólo app actualizada,
saludUP18080; sin pagos operativos ni cambios de datos/roles/.env/commit/push.
Retirados sólo contenedor temporal nexo-medio-qa (ficticio/tmpfs) y Dockerfile temporal.

## Fuentes dinámicas y cuentas de plantel — 28/10/2026

Preparar pago colectivo/individual inicia con una fuente. Agregar otra cuenta conserva
captura (máximo20); Quitar cuenta elimina sólo esa fila y renumera, mínimo1. Ambas
acciones invalidan sello/confirmación y exigen nueva revisión; no registran movimientos.
Se reutiliza template, permisos, CSRF y diseño responsive con botones module-action.

Causa comprobada de lista vacía: todas las cuentas operativas tienen plantel_id=1 y
las búsquedas/validaciones de nómina exigían plantel_id IS NULL. Se permiten cuentas
de plantel o generales de la MISMA institución, activas, caja/banco y misma moneda.
Se conserva autorización institucional salarial/financiera; MovimientoManualService
valida alcance y deriva plantel de la cuenta en egresos y compensaciones. No cambiar
alcance de roles ni cuentas operativas. Autocomplete compartido ya consultaba al focus;
no requería modificar JS global. Primeras10, búsqueda>=3, paginado de sugerencias vigente.

Pruebas unitarias de1/20fuentes, quitar intermedia/conservar campos/invalidar revisión;
IT PostgreSQL17 aislada verifica endpoint sin texto, cuenta de plantel, agregar/quitar
con binding/CSRF sin egresos y pago/cancelación con plantel contable correcto. Chrome
ficticio:8combinaciones4tamaños/2temas,10sugerencias al focus/búsqueda3/selección y sin
overflow/erroresJS. Manual actualizado: paso5 BOR-000002/Diana14,187.15, agregar segunda
cuenta banco10,000/caja4,187.15. No consta pago humano; no repetir borrador ni conceptos.

Verificación final:1022Java/package correctas (más1IT aislada explícita),138JS,
24vistas de pago/revisión/listado sin overflow/erroresJS, además8de autocompletado.
Actualizada sólo app, saludUP18080; sin cambios de datos operativos, roles, .env,
commit/push. Retirado únicamente contenedor temporal nexo-fuentes-qa con datos ficticios
y Dockerfile temporal; PostgreSQL operativo no se reinició.

## Botones de decisiones homologados — 28/10/2026

Usuario pidió mismo estilo de Preparar pago colectivo en Ver conceptos pospuestos y
Confirmar decisiones sin mover dinero. Ambos usan nomina-primary-action, encabezado
separado de la captura, texto explícito sin salida de dinero y adaptación móvil. Se
conservan rutas/permisos/motivo/modal con Cancelar y lógica de congelación intactos.
13Java focalizadas/package,5JS de confirmaciones y8vistas4tamaños/2temas sin errores
ni overflow. Sólo app actualizada, sin datos/SQL/pagos/roles/commit/push. Ensayo sigue
en BOR-000002/julio/Diana14,187.15, paso5 Preparar pago colectivo. Estos botones de
pendientes/congelación no son necesarios para ese caso ordinario.

## Ensayo en pago colectivo y diseño — 28/10/2026

Usuario avanzó a Autorización y pagos por empleado, paso5 del ensayo. Consulta acotada
confirmó BOR-000002/JUL-2027-RED-TEST,estadoBORRADOR,1empleado Diana6,total14,187.15.
No repetir Incluir/guardar ni crear otro borrador. BOR-000001 de enero se conserva.
Falta elegir Preparar pago colectivo, bancoBBVA_COBROS10,000 yCAJA4,187.15, tercera
fuente vacía, revisar y confirmar con modal. No consta pago ni aceptación financiera.

Cambio solicitado sólo visual: CTA Preparar pago colectivo usa nomina-primary-action
igual a Autorización y pagos; tarjeta de siguiente paso separa explicación y acción.
Formulario multicuenta incorpora employee-card (variables de tema), controles uniformes
incluidos selects, importes, referencia y motivo, fuentes en tarjetas con etiquetas
alineadas, ayudas sin invadir el texto y respuesta móvil. Gestión de pagos también
uniforma sus textarea/selects. Sin cambios de montos/reglas/BD/permisos o rutas.
QA:13Java focalizadas/package,138JS y24vistas de listado/captura/revisión en
1440/768/390/320 claro/oscuro sin overflow ni erroresJS. Controles fuente1 alineados
(misma coordenada superior),44px,borde10px y fondo temático uniforme. Sólo app
actualizada; no capturas/pagos por herramientas ni commit/push.

## Guardar borrador sin selección — 28/10/2026

Usuario reportó que Guardar borrador revisado no abría modal. Consulta operativa
acotada confirmó vistas ABIERTA/vigentes,versión0,0incluidos,total0: no había pulsado
Incluir. No se guardó ni pagó nada. Se conserva bloqueo sin selección y se añade
aviso junto al botón: filtrar no incluye, pulsar Incluir en fila y verificar1empleado/
14,187.15; enlace Ir a seleccionar empleados. Modal titula Borrador de nómina, no
mensual; texto ya no niega el cálculo proporcional que la vista sí realizó.
Render distingue0/1incluidos.13Java focalizadas/package y138JS correctos. Chrome
sintético verificó0sin selección,modal con14,187.15 al incluir,Cancelar/Escape0POST,
confirmación1POST interceptado (sin base real). Retomar julio Incluir→guardar,
no repetir periodo/conceptos ni afirmar aceptación humana del guardado/pago.

## Cierre técnico V139 y retoma sin duplicar — 28/10/2026

V139 aplicada correctamente; comparación posterior confirmó huellas idénticas de
borradores(1), movimientos(46), cuentas(3) y conceptos_empleado(7). Tablas nuevas
de tratamientos/pendientes operativos en0: no se registró nómina/pago con herramientas.
Se verificó respaldo legible y se retiró sólo nexo-tratamientos-qa (datos ficticios
en tmpfs), además del Dockerfile temporal. No tocar contenedores ajenos ni volúmenes.

Suite general/package:1019Java; integración PostgreSQL aislada adicional1IT correcta
con decisiones/restauración, sello/versión, pospuesto completo/complementario, pago,
cancelación/reapertura, no duplicidad, CSRF, Excel filtrado, lectura/sin escritura y
rollback de decisión que dejaría neto0.138JS sin fallos.32vistas1440/768/390/320 en
claro/oscuro sin overflow/erroresJS; corrección final del total monetario en una línea
en320px y ocultación de Preparar colectivo cuando no hay importe inmediato. Último
ajuste sólo visual:13Java focalizadas/package correctos, app recreada sin PostgreSQL.

Retoma: NO repetir julio ni EXTRA250. Usuario dejó Diana «No incluido», neto14,187.15.
Seguir PRUEBA_NOMINA_FLUJO_BASE_20261027.md desde su bloque inicial; nuevos escenarios
en PRUEBA_TRATAMIENTOS_NOMINA_20261027.md DESPUÉS. No aceptación humana integrada aún.
Recuperaciones complementarias netas cero/negativas siguen pendientes explícitas;
recibos nuevos, permisos segregados, cheques y préstamos no se declaran terminados.
No commit/push ni lectura de .env.

## Retoma humana de julio y tratamientos V139 — 27/10/2026

Usuario autorizó implementar No aplicar/Posponer y conservar la prueba para mañana.
NO continuar otra entrega automáticamente. Primero retomar julio de Diana: el usuario
confirmó $14,187.15 en vista previa y «No incluido»; falta Incluir y guardar borrador.
Julio y EXTRA250 ya existen; no repetir conceptos ni cambiar sus importes. La vista
UUID puede caducar: recalcular sólo si no hay borrador/reserva de julio. Manual ordinario
PRUEBA_NOMINA_FLUJO_BASE_20261027.md actualizado al inicio; los tratamientos se prueban
DESPUÉS, aparte, con PRUEBA_TRATAMIENTOS_NOMINA_20261027.md. No aceptación humana aún.

V139 aditiva: nomina_tratamiento append-only por fila/clave/acción/motivo/actor/fecha;
vista efectiva superpuesta a snapshot original inmutable. Decidir sólo en borrador
actual con versión/confirmación. Todo empleado o conceptos adicionales; sueldo base y
cuotas protegidos. No mezclar decisiones globales y por concepto; restablecer a Aplicar
antes de recalcular para no descartarlas silenciosamente. Configuración no se modifica.
Confirmar autorización materializa nomina_pendiente de conceptos pospuestos; origen,
importe firmado y copia quedan fijos. No se suman a otro colectivo. Pospuesto completo
se paga por fila original individual; percepción complementaria positiva usa liquidación
técnica separada mismo empleado/periodo, sin reserva duplicada ni repetir sueldo. Fuentes
y cancelaciones exactas existentes; cancelar pago complementario reabre pendiente y
actualiza estado del origen. Nueva pantalla de decisiones, captura separada/modal y
seguimiento paginado/Excel; filtros de Pagos añaden Pospuesto/No aplicar y resumen separado.

Límite explícito: obligaciones complementarias netas cero/negativas se conservan como
recuperación/liquidación especializada pendiente; no convertirlas a egresos ni sumarlas
a otro mes. Cuotas de préstamos no editables desde tratamientos. Contabilidad neto0,
recibos nuevos, permisos de operación segregados, cheques y rediseño préstamos continúan
pendientes. No declarar cerrado todo el rediseño. Se reutilizan permisos existentes,
sin grants ni cambios de roles. Modales Cancelar/Escape no escriben operación final.

Respaldo privado ignorado: .respaldos-locales/tratamientos-v139.IqjLKl/antes.dump;
imagen previa nexo-pre-tratamientos-v139:20261027. Huellas previas:1borrador,
46movimientos,3cuentas,7conceptos_empleado; comparar tras despliegue, sin capturar pagos.
QA en PostgreSQL17 temporal nexo-tratamientos-qa, base EXACTA nexo_flujo_nomina_qa;
nunca dropdb/reset en el contenedor operativo. No leer .env, commit ni push.

## Alineación visual de nómina y total de Diana — 27/10/2026

Usuario ya consultó julio para Diana y confirma sueldo $14,000, percepciones $630,
deducciones $442.85 y propuesto $14,187.15. Figura «No incluido»: filtrar no selecciona;
siguiente acción humana es Incluir, verificar un empleado/$14,187.15 y guardar borrador.
Todavía NO consta nómina guardada/aplicada ni aceptación del flujo de pago.
Se alinean filtros de Nóminas/Periodos y consulta de empleados, controles de formularios,
Limpiar con module-action y tarjeta compartida de importe neto en ambos desgloses.
Sin cambios de cálculo, esquema o datos. QA aislada: 13 pruebas Java, 135 JS y 20 vistas
1440/390 claro/oscuro sin desbordamientos/errores JS; fixture ficticio en target.
Desplegado únicamente app; actuator UP. Sin cambios de datos ni pagos por herramientas.

## Ensayo manual en curso y botón Limpiar — 27/10/2026

Usuario confirmó tabla de Diana con EXTRA-RED-TEST250único JUL-2027-RED-TEST,
TRANS80desdejulio/50hasta junio, QUIN150 y DED100; todas Próximas por fechas futuras.
No duplicar esas capturas. Siguiente: Nóminas→Nuevo borrador→julio→Opciones de
selección→Elegir empleados específicos→Preparar→seleccionar sólo Diana y revisar
14000+630−442.85=14187.15. Todavía no consta cálculo guardado ni pago humano.
Se corrige Limpiar de listado de periodos añadiendo module-action, mismo borde/
tema que Empleados/Puestos; sin lógica ni BD. Prueba de contrato JS específica.

## Despliegue parcial del flujo ordinario V137/V138 — 27/10/2026

Estado más reciente, supera “en desarrollo/no desplegado” de la nota siguiente:
V137/V138 desplegadas sólo en app, saludUP18080. Periodos nuevos sin calendario,
motor proporcional por días/prefijos, borrador, pago ordinario colectivo/individual
multicuenta, individual sin borrador previo del usuario y cancelación individual o
completa con fuentes exactas. No declarar TODO el rediseño terminado: faltan
tratamientos No aplicar/Posponer por concepto y obligaciones complementarias,
liquidación neto0 con contrapartida contable, recibos nuevos y permisos segregados.
Chequeras/préstamos expresamente para después. Los casos no soportados se bloquean,
no se inventan egresos0 ni recuperaciones de préstamos desde multicuenta.

Package integrado1019pruebas Java(1018generales+1IT PG17),134JS. IT ampliada después
pasó también colectivo2empleados/uno sin sueldo y rollback de cancelación del segundo
empleado; /tmp/nexo-flujo-dos-empleados-it.log. VisualQA20combinaciones1440/390 y
claro/oscuro, sin overflow ni erroresJS (log /tmp/nexo-flujo-visual-final.log).
QA temporal/tmpfs y Dockerfile temporal retirados; PostgreSQL operativo intacto.
Respaldo validado .respaldos-locales/flujo-nomina-v137.DTjlKn/antes.dump,
imagen anterior nexo-pre-flujo-v138:20261027. Huellas de movimientos/cuentas/conceptos/
borradores/salarios idénticas antes/después;46movimientos,3cuentas,6conceptos_empleado,
1borrador,8salarios. Nuevas tablas de fuentes operativas vacías; no pagos por herramientas.
No .env, cambios de roles, commit/push ni alteración de contenedores ajenos.

Ensayo siguiente PRUEBA_NOMINA_FLUJO_BASE_20261027.md: retomar creando julio SIN
calendario y único250para Diana; NO repetir sus recurrentes ya capturados. Julio:
14000sueldo+80transporte+300quincenal+250único−442.85semanal=14187.15.
Usar selección específica y sólo Diana para no pagar otros empleados de ensayo.
Fuentes propuestas banco10000/caja4187.15; consulta real antes de ensayo:
BBVA_COBROS33330,CAJA16950. No consta aceptación humana del nuevo flujo; no publicarlo
en guía confirmada todavía. El manual antiguo entrega2 remite al nuevo ensayo.

## Trabajo en curso: flujo base sin calendarios — 27/10/2026

Usuario confirmó entrega2 pasos1–5 (Diana: transporte50hasta30/06,80desde01/07;
deducción100semanal; percepción150quincenal). Se detuvo antes de crear julio porque
el formulario todavía pedía calendario. Autorizó completar flujo base hasta pago
multicuenta/cancelación; chequeras y rediseño de préstamos DESPUÉS. También autoriza
retirar tablas de calendario si es necesario, pero se conservan referencias legadas
para no destruir revisiones. No marcar estas funciones como aceptadas manualmente.

EN DESARROLLO, NO DESPLEGADO: V137 periodos nuevos sin calendario y motor por días
con prefijos HALF_UP deterministas; V138 fuentes exactas por pago/empleado, egresos
multicuenta y compensaciones. Servicios nuevos PagoMultipleNominaService y formulario,
PagoIndividualNominaController permite confirmar pago sin borrador previo del usuario:
crea fotografía técnica y paga atómicamente. Falta cerrar QA/alcance antes de declarar
terminado el flujo. No habilitar netos0 fingiendo un egreso ni cuotas de préstamos
multicuenta: bloqueados por depender de contabilidad/entrega posterior. Excepciones por
concepto/obligaciones pospuestas y recibos nuevos NO se declaran implementados.

Base operativa SIGUE V136,46movimientos. Respaldo validado privado:
.respaldos-locales/flujo-nomina-v137.DTjlKn/antes.dump. No se leyó .env. Sólo cambios
locales de código, no commit/push ni migración operativa todavía. Contenedor QA temporal
aislado nexo-flujo-nomina-qa (Postgres17 tmpfs), DB EXACTA nexo_flujo_nomina_qa;
no tocar chatgpt_proyectos-postgres-1 con dropdb/reset. QA explícita:
QA_FLUJO_NOMINA_JDBC=jdbc:postgresql://127.0.0.1:5432/nexo_flujo_nomina_qa.
NominaProporcionalPostgresIT pasó motor+fuentes+fallo forzado segunda fuente/rollback+
cancelación exacta+directo sin borrador+duplicado; último log /tmp/nexo-flujo-directo-it.log.
Se agregaron después cancelación completa/UI y faltan repetir. Suite general:
1018 pruebas,1fallo de contrato antiguo que exigía calendario (NominaEtapaTresTest);
ya adaptado a motor nuevo, falta repetir paquete. Últimos cambios NO package verificado.

Precaución: al generar parches con Node, usar SIEMPRE replace(patron,()=>texto) si hay
expresiones Thymeleaf con dólar/comilla. Se corrigieron duplicaciones accidentales en
nomina-pago-multiple.html y nomina-pago-empleado.html; repetir render/QA visual antes de
entregar. MockMvc limpia SecurityContext; IT lo restaura antes de llamadas a servicios.
No afirmar que la nueva aplicación ya corre: seguir pruebas, backup/huellas/despliegue
sólo app cuando esté seguro, y escribir manual actualizado retomando paso6 sin duplicar
los pasos1–5. Chequeras/préstamos quedan pendientes explícitos, no incluidos por inferencia.

## Privacidad salarial aprobada; siguiente ensayo entrega2 — 27/10/2026

Usuario **prueba** con Empleados + Datos laborales y sin Salarios confirmó menú
sólo Empleados, pestañas01/02/03/04/05/06, sin Ver puesto e historial, y denegación
de /admin/empleados/6/puesto y /admin/asignaciones/excel?empleadoId=6&estado=TODAS.
Caso aislado de privacidad salarial/URL/Excel aprobado. No volver a pedirlo.
Pruebas manuales viables del cierre entrega1 terminadas: positivos con **pruebas**,
negativos con **prueba**, concurrencia con Elena y selector de fechas. No afirmar
POST negativo manual ni sólo lectura; separación Consultar/Administrar pendiente.
Siguiente: PRUEBA_REDISENO_NOMINA_ENTREGA2.md. Habilitar Empleados — Configuración
de nómina al rol administrativo de ensayo autorizado (QA_SALARIOS_CONSULTA si se
usa **pruebas**), no a PRUEBA_PERSONAL_BASE/usuario **prueba** negativo. Renovar
sesión. No nuevas funciones ni duplicar empleados/puestos existentes por herramientas.

## Acceso salarial positivo y Excel confirmados — 27/10/2026

Usuario con **pruebas** confirmó menú correcto, pestañas03Datos laborales/04Planteles,
Diana15/06/2027→40h/$10,000;16/06/2027→48h/$14,000 y Excel coincidente. Registrar
como prueba positiva de acceso salarial, NO sólo consulta (paquetes completos).
Con **prueba** se confirmó ocultación sin Laboral/Salarios y ruta salarial denegada;
privacidad aislada con Laboral permitido/Salarios omitido y Excel directo no está
confirmada. Separar Consultar/Administrar requiere autorización propia, pendiente.
Entrega2 aún sin ensayo humano. Para empezar se habilita módulo Empleados —
Configuración de nómina al rol administrativo autorizado y se renueva sesión;
seguir PRUEBA_REDISENO_NOMINA_ENTREGA2.md sin duplicar puestos de Diana.

## Causa identificada: prueba y pruebas son cuentas distintas — 27/10/2026

Resumen de sesión proporcionado por usuario: username **prueba** (singular),
administrativo/Toda la institución. SQL acotado confirma prueba→PRUEBA_PERSONAL_BASE:
sólo EMPLEADO_LEER/ADMINISTRAR activos, Laboral desactivado y sin Salarios.
**pruebas** (plural)→QA_SALARIOS_CONSULTA tiene los6permisos activos de Empleados,
Laboral y Salarios. Investigación previa se hizo sobre la cuenta plural, no la
sesión real. Las cuatro pestañas y denegación observadas son coherentes con el rol
de prueba; no es evidencia de fallo de carga/caché. No se modificaron permisos.
Siguiente iniciar como pruebas para el ensayo de acceso laboral/salarial, sin tocar
roles de prueba ni administrador. Sólo consulta sigue no disponible por paquetes;
no declararla aprobada. Esperar confirmación de pestañas/acceso con cuenta correcta.

## Diagnóstico pendiente de sesión efectiva — 27/10/2026

Usuario confirmó acceso denegado en /admin/empleados/6/puesto pese a los tres paquetes
seleccionados. Último acceso de pruebas es POSTERIOR a actualización de permisos:
descartado recomendar otra vez renovar sesión como solución suficiente. SQL confirma
usuario9 administrativo activo/institución1, rol activo INSTITUCION y seis permisos
activos; empleado6 institución1. Código no permite concluir causa sin sesión real.
Chrome no se pudo inspeccionar: permisos de Computer Use no concedidos, no evadir
esa restricción. Siguiente comprobar /admin/perfil/resumen existente con usuario de
prueba: pedir sólo usuario/acceso/alcance, nunca contraseña, cookies o tokens.
No modificar grants ni ampliar acceso para ocultar el fallo. Prueba bloqueada, no
marcar permisos aprobados ni asumir caché/rol faltante sin evidencia.

## Diagnóstico de permisos durante cierre entrega1 — 27/10/2026

Usuario ve sólo menú Empleados y pestañas01/02/05/06. Inspección de sólo lectura:
QA_SALARIOS_CONSULTA tiene los6permisos LEER/ADMINISTRAR de Empleados/Laboral/Salarios
activos; asignación activa al usuario pruebas, administrativo activo e INSTITUCION.
La sesión/cuenta visible no refleja esos permisos; pedir cerrar sesión y renovar,
no agregar grants para corregir una sesión antigua. No se han cambiado permisos.
Corrección importante del ejemplo: Roles asigna paquetes completos en
RolServiceImpl.sincronizarPermisos y login ModuloPermiso.expandir. No existe selección
efectiva sólo lectura por módulo. No afirmar que el rol QA es de consulta ni aprobar
ese caso. Manual de pendientes actualizado con advertencia; separar operaciones
requiere autorización e implementación propias, no cambiar seguridad a escondidas.

## Retoma manual: concurrencia de asignaciones aprobada — 27/10/2026

Usuario confirmó segunda pestaña rechazada con recarga y UNA asignación en historial
de Elena: APOYO_TECNICO_48,48h,14000mensuales,desde01/06/2027,sin fin,Próxima.
No repetir ni cancelar esa fila. Observó también selector bloqueado antes de hoy;
es evidencia de interfaz, no prueba manual del servidor. Documentado en ambos
manuales entrega1. Siguiente: bloque3 de PRUEBAS_PENDIENTES_NOMINA_ENTREGA1.md,
permisos de lectura salarial/sin salario con cuentas de ensayo. Entrega2 todavía
no probada por el usuario; no comenzar otro desarrollo sin autorización.

## Cierre de implementación entrega2 y retoma manual — 27/10/2026

Implementación terminada/desplegada: sólo app actualizada, saludUP18080 y V136success.
1018pruebas generales+1IT PostgreSQL17 aislada (1019total),132JS, package correcto.
IT cubre configuración/fechas/únicos/periodoobsoleto/POST-CSRF/reenvío/rollback tras
cierre previo/IDOR/permisos/POI/motor anterior bloqueado; no afirmar que se repitieron
las demás IT históricas. Chrome1440/390 claro/oscuro, sin desbordamiento, ayudas y
modal Cancelar con ceroPOST; fechas únicas protegidas; filtros alineados4porfila.
Huellas de9tablas operativas comparadas iguales (se excluyen columnas aditivas),
46movimientos y saldos conservados; nuevos permisos con0asignaciones a roles.
Respaldo privado validado y foto anterior de imagen nexo-pre-entrega2-v136:20261026.
Dockerfile temporal retirado y contenedor QA/tmpfs nexo-conceptos-entrega2-qa retirado;
PostgreSQL operativo/volúmenes intactos, sin .env, capturas por herramientas, commit/push.

Usuario pidió terminar pendientes y recibir pruebas antiguas antes de probar entrega2.
Seguir PRUEBAS_PENDIENTES_NOMINA_ENTREGA1.md: empleado auxiliar Elena
EMP-PUESTOS-SEG-TEST, ingreso01/10/2026; dos intentos inválidos sin guardar y dos
pestañas para dejar UNA asignación48h/14000desde01/06/2027. No alterar Diana.
Permisos con roles/cuentas de ensayo separados, no rol del administrador real.
Ninguno de los tres bloques se ha confirmado aún; entrega2 tampoco tiene aceptación
humana. Siguiente entrega3 sólo con autorización: periodos sin calendario/motor por
tramos/centavos y clasificación de preparación (sin puesto/configuración/listos),
no presentar esos filtros o cálculo como ya implementados por esta entrega.

## PRIORIDAD ACTUAL — Rediseño de nómina, entrega2 — 26/10/2026

Usuario autorizó avanzar con configuración individual y retirar submenús antiguos.
Leer PRUEBA_REDISENO_NOMINA_ENTREGA2.md. No reanudar el ensayo V134 ni generar/pagar
nómina con la configuración nueva: motor proporcional y periodos sin calendario
siguen siendo entrega3. Los tres bloques manuales de entrega1 (fechas anteriores,
dos pestañas asignando puesto y permisos) quedan para27/10/2026 por decisión expresa.
Selección desactualizada de puesto y cierre sin nuevas asignaciones sí confirmados.

Implementación entrega2: V136 aditiva sobre concepto_empleado (fuente canónica).
LEGADO conserva importe por nómina; V2 añade frecuencia mensual/quincenal/semanal,
recurrente o única con periodo/snapshot exacto. No convertir los dos conceptos
anteriores ni modificar salarios, borradores, pagos o cuotas. Nueva pestaña privada
Configuración de nómina en Empleados, tabla/filtros/paginación/ayuda10/3/Excel POI,
formularios separados y modales de alta/cambio/terminación/cancelación. Sueldo base
protegido desde la asignación vigente hoy; si futura muestra aviso y enlace a fechas.
Programar cambio crea vigencia, cierra anterior día previo y conserva importe viejo;
cancelar última futura restaura fin anterior. Terminar vigente incluye último día.
Único conserva cobertura del periodo; no implementa aún consumo ni pago. Bloqueos/
versiones/traslapes, periodo obsoleto, protección contra duplicar Sueldo base/Con saldo
y captura ocasional anterior. Aportaciones patronales legadas no se reinterpretan.

Permisos nuevos EMPLEADO_NOMINA_LEER/ADMINISTRAR, visibles en Roles como
Empleados — Configuración de nómina; exigen además paquete Empleados/laboral/salarial.
NO se conceden automáticamente: rol autorizado debe habilitarlos y renovar sesión.
Sin permiso no pestaña/contexto/Excel ni acceso por URL; lectura no administra.

Menú Puestos sin Jornadas/Plantillas; se conserva información y rutas antiguas.
Configuración de nómina deja de ser entrada separada, ahora está en Empleados.
Periodos aún conserva compatibilidad de calendarios hasta entrega3, no afirmar que
ya quedó rediseñado. Motor mensual anterior clasifica V2 como Revisión sin importe
pagable; resumen anterior sólo suma LEGADO. Rutas históricas no se borran.

Inventario previo local:6empleados,3puestos,7condiciones,2conceptos legados,
46movimientos,1borrador. No crear datos de ejemplo con herramientas. Respaldo privado
ignorado validado: .respaldos-locales/entrega2-v136.rHzHze/antes-v136.dump. Huellas
de9tablas previas tomadas para comprobar despliegue; no mostrar datos del respaldo.
Estilo module-action aprobado por usuario y agregado a regla de AGENTS.md para nuevas
pantallas/correcciones, no refactor global de todos los módulos. Ensayo humano de
entrega2 pendiente; aceptación automatizada no sustituye confirmación del usuario.

## Concurrencia del catálogo confirmada — 26/10/2026

Usuario confirmó rechazo de segunda pestaña editando Puestos: «El puesto cambió
desde que lo abriste. Recarga antes de guardar». Caso de versión de catálogo
aprobado en manual entrega1. No confundir con concurrencia de asignaciones o con
selección obsoleta del puesto; esos casos y permisos no están aún confirmados.
No presumir restauración de la descripción original sin confirmación.

## Validaciones de horas y sueldo confirmadas — 26/10/2026

Usuario confirmó todos los intentos del ejemplo: campos vacíos, horas0/169,
sueldo0 rechazados y salida sin crear VALIDACION-PUESTO-TEST. Manual entrega1
actualizado. Concurrencia y permisos todavía requieren confirmación manual.

## Código de puesto duplicado — 26/10/2026

Usuario confirmó rechazo al registrar APOYO_TECNICO_40 nuevamente. Caso manual de
duplicidad aprobado e incorporado al manual entrega1. No dar por aprobadas las
validaciones restantes de horas/sueldo, concurrencia o permisos.

## Puesto inactivo validado manualmente — 26/10/2026

Usuario confirmó desactivar APOYO_TECNICO_40, ausencia en ayuda para asignaciones,
conservación del historial de Diana40h/$10,000 y reactivación final. Manual entrega1
actualizado; no presumir comprobados otros casos negativos, concurrencia o permisos.


## Confirmación adicional del ensayo entrega1 — 26/10/2026

Usuario confirmó consultas de Diana15/06/2027(40h/$10,000) y16/06/2027(48h/$14,000),
Excel de ambas asignaciones y rechazo al intercalar otra desde10/06/2027 sin alterar
historial. Registrado en PRUEBA_REDISENO_NOMINA_ENTREGA1.md. Restan verificaciones
manuales negativas, concurrencia y permisos; no asumir cierre completo ni implementar
entrega2 sin autorización. Esta actualización es documental, no despliegue/migración.

## Estilo de acciones de filtros y tablas — 26/10/2026

- Usuario confirmó el flujo principal de puestos hasta los dos registros de historial
  (40 horas hasta15/06/2027,48 horas desde16/06/2027). No inferir aceptación de
  pruebas negativas o de seguridad no reportadas. El rediseño sigue en entrega1.
- Se creó el patrón reutilizable `module-action`/`module-filter-actions`: acciones
  secundarias discretas con borde, sin gradiente ni sombra; Aplicar filtros azul.
  Adopción piloto en Empleados y Puestos, pendiente de aprobación visual del usuario
  antes de extenderlo al resto de módulos. No afirmar homologación global realizada.
- Cuatro filtros en una fila de escritorio, dos en tablet y uno en móvil. Acciones
  Ver expediente/Editar usan el mismo patrón. Datos laborales presenta tarjeta de
  puesto y sueldo con acción **Ver puesto e historial**, sin cambiar rutas/permisos.
- Cambio sólo de presentación; no migraciones, cálculos ni capturas operativas.
- Verificación: package correcto,1014Java/128JS sin fallos; fixtures en Chrome
 1440/390 sin desbordamiento y cuatro controles alineados (44px). Revisados temas
 claro/oscuro y tarjeta laboral. Sólo app actualizada, saludUP18080; sin commit/push.

## Orden del menú y texto residual — 26/10/2026

- Usuario pidió ordenar Personal y nómina según el proceso: Puestos laborales,
  Conceptos de nómina, Empleados, Periodos de nómina, Configuración de nómina,
  Nóminas. Orden coherente en enum del servidor y navigation.js; sin nuevos módulos,
  permisos, rutas, eliminación de menús ni cambios de datos.
- Se retiró texto literal `undefined` de puestos.html y empleado-form.html,
  introducido por la edición anterior; no era un valor de la BD. Regresión recorre
  todas las plantillas y verifica ausencia de línea residual, además de render de
  Puestos y orden/permisos del menú. Ejemplo entrega1 sigue pendiente de aceptación.
- Cierre: compilación limpia/package correcto,1013Java/128JS sin fallos. Fixture de
  Puestos en Chrome390/1366 confirma orden y ausencia de texto residual. Sólo app
  actualizada/recreada, saludUP18080; sin nueva migración ni cambios de puestos,
  sueldos, permisos o cuentas. PostgreSQL/volúmenes intactos, sin commit/push.

## Rediseño de nómina: entrega 1 — 26/10/2026

- Usuario autorizó implementar entrega1 y solicitó ejemplo manual. Leer
  `PRUEBA_REDISENO_NOMINA_ENTREGA1.md` (pendiente de aceptación humana) y
  `PLAN_REDISENO_NOMINA_20261026.md`. No continuar automáticamente el ensayo V134.
- V135 aditiva: puesto_laboral.horas_semanales/sueldo_mensual y fotografía en
  condicion_salarial.horas_puesto/puesto_version. Puestos legados quedan NULL/
  Pendiente de completar, sin inferencia ni backfill; condiciones previas intactas.
  Sueldo canónico sigue condicion_salarial, no otra tabla duplicada. ACTIVO laboral
  requiere ingreso, ya no texto de puesto; conserva reglas de baja/historial.
- Puestos: creación/edición separada con horas positivas hasta168 (capacidad técnica,
  no norma legal), sueldo mensual positivo, validación, modal y Excel con filtros.
  Nombre puede repetirse por variantes; código único institucional normalizado.
- Empleados → Datos laborales → Ver puesto, asignar o cambiar e historial. Ya no
  seleccionar puesto textual allí y recapturar sueldo en otra pantalla. Primero
  confirmar ingreso/actividad; nueva acción selecciona puesto una vez, consulta
  horas/sueldo protegidos, Aplicar desde y motivo. Cierre previo día anterior,
  snapshot de catálogo/versiones y revisión salarial en transacción. Sólo hoy/futuro,
  no antes del ingreso ni intercalado antes de una programación posterior existente.
- GET/POST/contexto de /admin/empleados/{id}/puesto protegidos por paquete salarial
  además de alcance/empleado laboral; sin grants nuevos. Catálogo editado no cambia
  snapshots; puesto obsoleto/inactivo/incompleto se rechaza. Ruta manual anterior no
  acepta sueldo separado para puestos completos. Compatibilidad anterior preservada
  en Configurar nómina bajo detalle secundario; aún no se implementa entrega2.
- Resumen con fecha y tabla histórica paginada/Excel; expediente muestra sueldo
  actual autorizado. OrganizacionLaboral/ConfiguracionNomina leen horas snapshot
  primero y luego horas históricas de esquema legado. No inventar jornada nueva.
- QA aislada PostgreSQL17 verificó V1–135, dos variantes, vigencias, inmutabilidad,
  rollback deliberado tras cierre previo, cancelación futura/restauración, dos
  pestañas, alcance/permisos, Excel, render GET y POST con CSRF/importe manipulado/
  repetición. Sin dinero ni nómina creada. Fixture visual390/1366 claro/oscuro con
  autocompletado y modal/Cancelar. No atribuir como ejecutadas las demás IT históricas.
- Inventario de esta base antes de V135:5empleados (Bruce3,Ana5),1puesto legado,
  5condiciones salariales y46movimientos. Respaldo privado ignorado y catálogo
  validado: `.respaldos-locales/entrega1-v135.bPaKCX/antes-v135.dump`. Huellas de
  7tablas previas guardadas para comparar al desplegar, sin mostrar contenido.
- Ejemplo propuesto usa Diana/EMP-PUESTOS-TEST-01 y dos puestos nuevos APOYO_TECNICO_40
  /48. No crear esos registros por herramientas ni cambiar Bruce/Ana. No probar aún
  nómina proporcional: cálculo por tramos pertenece a entrega3; esta entrega no paga.
- Cierre técnico: package offline correcto,1011Java/128JS y1IT PostgreSQL17 aislada
  correctos. Sólo app recreada, saludUP18080 y V135success; huellas antes/después de
  7tablas idénticas,46movimientos. Imagen anterior conservada como
  nexo-pre-entrega1-v135:20261026. Sin .env, permisos concedidos, commit ni push.
  Contenedor de QA desechable retirado, Dockerfile temporal eliminado; PostgreSQL
  operativo y volúmenes intactos. Prueba humana nueva todavía pendiente.

## PRIORIDAD: rediseño de nómina acordado y documentado — 26/10/2026

- Leer primero `PLAN_REDISENO_NOMINA_20261026.md`: especificación funcional vigente
  como DESTINO. Sustituye el destino de los planes anteriores, no declara funciones
  construidas. Usuario pidió sólo documentar; NO implementar/migrar/desplegar ni
  borrar tablas por esta solicitud. No reiniciar el ensayo anterior automáticamente.
- Flujo: puestos con horas/sueldo mensual → asignación única en empleado/conceptos
  por frecuencia y vigencia → periodo sin calendario obligatorio → borrador/pago.
  Cálculo por días reales/tramos, semana lunes–domingo y quincenas1–15/16–fin,
  centavos controlados. Sin puesto puede calcular conceptos con advertencia;
  negativo bloqueado y visible; neto0 conserva liquidación/contabilidad sin cuenta.
- Pago colectivo/individual no se duplican. Borrador reserva persona/cobertura;
  pendiente sólo se paga individualmente en periodo original, jamás se mezcla con
  otro colectivo. Individual sin borrador previo; ordinario/pendiente/extraordinario.
  No aplicar/Posponer con motivo; cancelación compensa cuenta/abonos y conserva
  obligación original, sin fingir devolución bancaria. Aplicada inmutable.
- Varias fuentes/medios, saldo insuficiente con disponible/solicitado/faltante,
  transacciones e idempotencia. Chequeras en cuentas bancarias, no otra cuenta con
  saldo. Préstamos separados con saldo real y reestructuración sólo hacia adelante.
- Pendiente resolver detalles técnicos contables de neto0, centavos fuera de orden,
  corrección de cálculo aplicado y migración segura; no funciones ya implementadas.
  Plan de7entregas y21casos de aceptación en especificación. Guía sólo tras ensayo.
- Usuario confirmó calendario de Bruce y Ana9000/NOM-MENSUAL-TEST consultada al
  15/06/2027. No constan nuevas confirmaciones de periodo junio/préstamo/pagos.
  Lo anterior es reporte humano, no consulta nueva de DB; releer inventario en otra
  computadora. V134/fase5 sigue sin aceptación integrada final.
- Esta actualización sólo documentación. Siguiente propuesto: entrega1 de puestos y
  asignación única, con inventario/diseño técnico de transición y autorización antes
  de implementar. Conservar cambios previos del usuario y del ajuste de puesto.

## Propuesta de puesto en el primer sueldo — 26/10/2026

- A petición del usuario, preparar configuración integrada propone ID y código/nombre
  del puesto del expediente sólo si no existe historial salarial y hay una coincidencia
  exacta y única activa en la misma institución. El expediente legado conserva nombre,
  no ID; no inferir por coincidencias parciales ni elegir entre nombres duplicados.
- No activa cambiarSueldo, no copia importes, no registra asignaciones al abrir y no
  sincroniza/reemplaza el puesto laboral ni los salarios existentes. Se mantienen
  permiso, revisión de concurrencia, validación del catálogo y modal al confirmar.
- Once pruebas Java focalizadas y una JS correctas; cinco nuevas cubren propuesta,
  alcance institucional, ambigüedad, ausencia de coincidencia/historial/nombre.
- Suite completa y package offline correctos:1009Java/126JS sin fallos. Sin migración,
  cambios de datos ni commit/push. Imagen local reconstruida; Dockerfile temporal retirado.
- Sólo app recreada; salud UP18080. PostgreSQL y sus volúmenes permanecieron intactos.
- Ana puede continuar en Configurar nómina → Puesto y sueldo: recargar antes de
  capturar para ver propuesta, activar sección,9000mensual desde01/06/2027 sin fin,
  calendario NOM-MENSUAL-TEST y sin conceptos adicionales. Si ya registró sueldo,
  consultarlo al15/06/2027 y no duplicar. Ensayo fase5 sigue pendiente.

## Ajuste visual de configuración de nómina — 26/10/2026

- Separación explícita entre Configurar nómina y su ayuda; Calendario usa el mismo
  contenedor form-grid que los demás campos. Captura integrada con controles uniformes,
  autocompletados, fechas, sólo lectura y motivo en temas claro/oscuro y móvil.
- Sin cambios de cálculo, permisos, vigencias ni registros. Seis pruebas Java focalizadas
  y una JS de confirmación correctas; fixture visual con CSS/fragmentos reales revisado
  en 390/1280px y ambos temas, sin desbordamiento ni solapamiento. No es aceptación
  humana del flujo ni una prueba autenticada del portal.
- Package offline correcto; imagen local actualizada y sólo app recreada. Salud UP
  en18080, sin reiniciar PostgreSQL/volúmenes; Dockerfile temporal retirado.
- Para continuar: Resumen por empleado, Bruce/DOC-PRUEBA-02, fecha de consulta
  15/06/2027 y Aplicar filtros antes de Ver configuración. Sueldo12000 y bono500
  existen desdeenero2027; no duplicarlos. Sólo asignar NOM-MENSUAL-TEST desde
  Configurar nómina → Calendario. Aún pendiente ensayo humano de fase5.

## Retoma local V134 desde V131 — 26/10/2026

- Usuario pidió leer continuidad, revisar esta base antes del ensayo fase5 y autorizó
  respaldar/actualizar sólo app. Código Git9f21177 estaba limpio; DB y app locales
  seguían V131. No confundir con datos de otra computadora del24/10.
- Aquí hay Bruce Wayne/DOC-PRUEBA-02 activo, ingreso01/10/2026, sueldo12000mensual
  desde01/01/2027 sin fin y bono BON-PUNT-TEST500 desdeenero sin fin; programaciones
 13000/600 canceladas conservadas. Calendario NOM-MENSUAL-TEST activo pero sin
  asignaciones a empleados. Enero2027 PROGRAMADO/febrero CANCELADO. Carlos/Ana no
  existen. BOR-000001 está BORRADOR/rev1, NO APLICADA; no pagar/cancelar para retomar.
  Tres cuentas existentes contienen otros ensayos; usar otra exclusiva de capacitación.
- Respaldo privado ignorado .respaldos-locales/retoma-v134-20261026.Jft5P6/antes-v134.dump,
  catálogo pg_restore validado. Huellas antes/después de14tablas previas idénticas
  (empleados/laboral/salarios/conceptos/calendarios/periodos/borrador/revisiones/filas/
  aplicación/cuentas/movimientos); no fueron sólo conteos. Imagen anterior conservada
  como nexo-retoma-pre134:20261026-Jft5P6. No leer/publicar contenido de respaldo/.env.
- Compilación limpia offline/package BUILD SUCCESS,1003Java y126JS correctos en esta
  computadora; no volver a atribuir los4IT anteriores como ejecutados hoy. App sola
  recreada26/10 a10:17México, saludUP18080 y V132/133/134success. JAR en ejecución
  contiene las tres migraciones; PostgreSQL/volúmenes conservados. Sin limpieza Docker.
- Ocasionales/préstamos/entregas/autorizaciones/intentos/reversas/abonos/compensaciones
  consultados0;46movimientos como antes, BOR000001 BORRADOR/rev1 intacto. No entregas,
  pagos, reversas ni modificación de datos operativos por herramientas. Defaults en
  fuente empaquetada entrega=true/consolidada=false, sin cambiar properties o roles.
  Dockerfiles temporales retirados; sin cambios Java/SQL ni commit/push.
- Siguiente: prueba humana PRUEBA_SIMPLIFICACION_NOMINA_ETAPA5.md adaptada a ESTA base.
  Reutilizar Bruce en vez de Carlos si se acuerda; conservar12000+500, asignar calendario,
  crear Ana9000, periodo nuevo junio2027, cuenta exclusiva30000 y concepto Deducción/
  Con saldo para préstamo3000/cuota500 desde01/06/2027. No crear registros por el agente.
  Resultados propuestos conservan21000total/12000Bruce/9000Ana. Guía integrada sigue
  pendiente de aceptación humana; V135 disponible, no nueva funcionalidad autorizada.


## Nómina: pagos individuales y abonos — fase5,24/10/2026

- Usuario autoriza fase5. V134 aditiva; NO dividir aplicaciones anteriores. BOR000001
  APLICADA/rev1/mov10/12500/saldo posterior7500 permanece histórica. Nuevas nóminas:
  BORRADOR→AUTORIZADA→PARCIALMENTE_PAGADA/PAGADA según pagos vigentes. Autorización
  congela revisión/empleados sin dinero. No cancelarla como borrador ni recalcularla
  al cambiar configuración. Neto0 requiere resolver configuración; no egreso cero.
- Detalle → Autorización y pagos por empleado. Lista/estado/búsqueda/paginación/Excel;
  empleado con Resumen/Registrar resultado/Historial. Captura con retorno/contexto,
  cuenta/motivo10/3, importe histórico no editable, revisión sellada, confirmaciones
  detalladas y Cancelar. Nuevo intento de formulario/versiones tras operación exitosa.
- Pagar: un egreso NOMINA individual por intento y abonos exactos de sus préstamos,
  en misma transacción. Rechazado SIN salida no crea movimiento ni abona; pendiente.
  Reversa exige confirmar devolución real a cuenta original, ingreso NOMINA_REVERSO
  con reversa_de_id, motivos/referencia/fecha/actor privados y compensación de abonos;
  sólo ese empleado vuelve pendiente. Reintento nuevo, sin editar original. No depósito
  bancario automatizado/timbrado ni pago parcial del neto individual.
- Préstamos: min(parcialidad,saldo) por mes desde inicio. Reserva cuota al autorizar,
  no abono; otra nómina de ese préstamo exige resolver pago pendiente antes de reservar.
  Saldo deriva entregas menos abonos de PAGADOS. Liquidación automática y reapertura
  al revertir; otras nóminas pagadas no se recalculan. Historial → Abonos/compensaciones
  paginado con saldos históricos y enlace al pago. No duplicar deducción habitual.
- application.properties: entrega-habilitada=true ahora que abonos conectados;
  consolidada-habilitada=false. Servicio entrega conserva default false. Compatibilidad
  consolidada sólo activada explícitamente en fixtures IT antiguas, NO en operación.
  No abonos externos/suspensiones/intereses/devolución del principal/refinanciamiento.
- Permisos existentes sin grants; salario privado para autorizar/consultar, más registro
  financiero para pagar y más MOVIMIENTO_FINANCIERO_REVERTIR para reversa. Backend/CSRF/
  IDOR y cuentas institucionales/moneda/saldo/estado/versiones selladas. Locks de pago
  nómina→empleado→préstamos→cuenta; autorización conserva periodo→nómina→empleados.
  Tabla intentos con índice único pago vigente, reversa única, abonos/compensaciones
  inmutables y triggers de correspondencia financiera/fotografía/estado. No PII salarial
  en libro global: sólo folios. Ocasionales congelados también desde autorización.
- 1003Java suite+4IT PostgreSQL17 aislados,126JS correctos. Executor IT7 incluye3unitarios
  ya en suite. Última cuota250 de1250/500, liquidación/reapertura, rechazo/concurrencia/
  reversa/reintento/otro empleado intacto; POST reales, permisos/CSRF/IDOR/Excel/abonos.
  Fallo SQL deliberado DESPUÉS de egreso al abonar: rollback dinero/intento/abono, reintento
  correcto. Error inicial de sintaxis CASE corregido en V134 aún no publicada; fixture
  restablece contexto después de MockMVC. Sin problemas ni cambios sobre datos reales.
- PRUEBA_SIMPLIFICACION_NOMINA_ETAPA5.md: ejemplo humano propuesto, no confirmado.
  Guía integrada pendiente de aceptación. Siguen pruebas humanas de flujo simplificado,
  expedientes familiares/timeout de sesión y requisitos de liberación generales.
  Respaldo privado previo .respaldos-locales/nomina-etapa5-v134-20261024/antes-v134.dump,
  catálogo verificado. No leer/publicar .env ni respaldos, no commit/push ni pagos reales.
- Publicada24/10/2026 a16:09México, saludUP8080/Flyway134. Comparación de huellas de
  aplicación histórica y fuentes de empleados BOR000001 idénticas antes/después,
  estadoAPLICADA/rev1/mov10/12500/saldo posterior7500 intactos;10 movimientos/0préstamos.
  Tablas nuevas autorización/intentos/reversas/abonos/compensaciones vacías en operación.
  No se hicieron pagos, entregas, abonos o reversas reales por herramientas. Entrega
  true y consolidada false en fuente empaquetada. QA visual ficticia escritorio/móvil,
  claro/oscuro, modal/Cancelar sin enviar; evidencia ignorada target/qa-nomina-etapa5/.
  DB tmpfs/ejecutor/etiqueta imagen QA retirados, servidor/tab cerrados, viewport/tema
  restaurados. No volúmenes operativos ni imágenes previas de respaldo eliminados.
  Siguiente: prueba humana fase5 propuesta; no repetir aceptación ya confirmada ni
  dar por aceptado el flujo nuevo por pasar pruebas técnicas. Guía integrada pendiente.

## Préstamos: base técnica de etapa4, entregas protegidas — 24/10/2026

- Usuario responde «continúa» a confirmación de propuesta SIN INTERESES. Implementada
  captura de préstamos desde Configuración → empleado → Conceptos → Préstamos, sin
  séptimo menú. Listado/filtros/paginación/Excel, Nuevo/Editar aparte, detalle con tabs
  Resumen/Entrega/Historial. Origen/filtros/página y captura en error conservados.
  Diseño compartido claro/oscuro/móvil, ayudas10/3 y modales Cancelar/confirmación única.
- V133: prestamo_empleado, prestamo_entrega e historial inmutable; catálogo CON_SALDO
  sólo DEDUCCION; clase financiera PRESTAMO.
  Snapshot concepto/nombre/versión/moneda, UUID único de solicitud, importe y parcialidad,
  primer descuento día01 de mes actual/futuro, motivo, versiones e historial privado.
  Empleado activo laboral/ingreso compatible/calendario mensual; no iniciar antes de
  nóminas ya aplicadas. Borrador saldo0, no mueve dinero; sólo éste admite Editar/Cancelar.
- Entrega implementada pero NO habilitada operativamente: application.properties y
  servicio app.nomina.prestamos.entrega-habilitada=false. NO activar para ejemplo humano:
  faltan pagos/abonos individuales. Backend también bloquea, no sólo botón oculto.
  QA aislada sí habilita: revisión sellada/cuenta/saldo/motivo/referencia, confirmación
  real, egreso único PRESTAMO y entrega en misma transacción. Permisos salario+finanzas,
  cuenta caja/banco institucional, moneda/saldo/estado revalidados y locks ordenados.
  Libro global no revela empleado ni referencia privada. No transferencia bancaria.
- No abonos/liquidación/suspensión/correcciones de entrega operativos aún. Saldo actual
  deriva sólo de entrega; no inferir pagos por meses transcurridos. Cuota mínima de
  saldo/parcialidad como contrato, NO afirmar amortización automática implementada.
  ACTIVO no admite cancelación/edición ni reversa manual genérica. Etapa5 requiere
  nueva migración de abonos/pagos/compensaciones y transición LIQUIDADO segura antes
  de habilitar entregas; NO basta cambiar la propiedad ni dividir aplicaciones viejas.
- Generador no cambia candidatos sin préstamo; añade fuentes sólo si activo y ya
  corresponde descuento. Clasifica REVISION hasta fase5; no omitir deuda al aplicar.
  Captura BORRADOR no afecta selección ni cálculo. Aplicadas previas conservadas.
- 1000Java suite +3IT PostgreSQL17 aislados,122JS correctos: concurrencia egreso único,
  rollback/sello alterado/sin saldo, privacidad/CSRF/IDOR, captura y revisión web,
  Excel filtrado multipágina, doble solicitud e historia inmutable. Primera aserción
  de captura corregida para decodificar entidad HTML &eacute;, no pérdida de datos.
  PRUEBA_SIMPLIFICACION_NOMINA_ETAPA4.md: ejemplo humano sólo borradores, pendiente
  aceptación; no publicar guía integrada confirmada todavía. Sigue fase5.
- Respaldo privado ignorado .respaldos-locales/nomina-etapa4-v133-20261024/antes-v133.dump,
  catálogo pg_restore verificado. Antes: V132/BOR000001APLICADA/rev1/mov10/12500/
  saldo posterior7500/10 movimientos. No repetir ni probar dinero sobre esta base.
  Sin .env/commit/push ni grants. Pendientes de sesión y expedientes familiares intactos.
- Publicada24/10/2026 a15:24México: saludUP8080, Flyway133 success. BOR000001
  conservaAPLICADA/rev1/mov10/12500/saldo posterior7500;10 movimientos como antes.
  Tablas de préstamos/entrega/historial vacías en base operativa, sin préstamos reales
  ni alteración de saldo. Propiedad de entrega false. QA visual ficticia1280/390,
  oscuro/claro: autocomplete, contexto, modal y Cancelar, pestañas/gestión protegida;
  corregida separación de ayudas y tipografía de textarea en captura móvil.
  Contenedores temporales QA y etiqueta de imagen retirados, servidor/tab cerrados;
  no se eliminaron volúmenes operativos ni imágenes previas de respaldo. Capturas
  ficticias ignoradas en target/qa-nomina-etapa4. No dar aceptación humana por concluida.

## Simplificación de nómina: etapa3 — 24/10/2026

- Implementada generación por calendario asignado, vigencias habituales y conceptos
  ocasionales para UN periodo. Leer PRUEBA_SIMPLIFICACION_NOMINA_ETAPA3.md y plan.
  Configuración → empleado → Conceptos → Sólo un periodo: listado/filtros/paginación/
  Excel, alta separada, detalle/historial/cancelación con motivo y modal Cancelar.
  Autocomplete10/3, dinero compartido, fechas dd/MM/yyyy, diseño claro/oscuro/móvil.
- V132 crea concepto_empleado_periodo e historial inmutable; no backfill/grants.
  Importe, concepto y periodo originales se conservan. Cancelar sólo antes de incluir
  en revisión aplicada, protección servidor/BD. Preparar/guardar no consume ni paga.
  Identidad institucional y empleado fijados en servidor; permisos salariales/CSRF.
  Calendario actual debe asignarse explícitamente: sin calendario REVISION, otro
  calendario NO_INCLUIDO. No convertirlo en historial de calendarios por vigencias.
- Generador agrega calendario_empleado/ocasionales al JSON original y marcador
  CALENDARIO_VIGENCIAS_1 al sello de periodo. Borrador legado sólo cambia mediante
  actualización explícita/nueva revisión; no omitir empleados bloqueados. Cambios
  posteriores invalidan propuesta, no reescriben snapshots. Aplicadas intactas.
  Sueldo sigue UNA fuente condicion_salarial; mensual completo exclusivamente.
  Locks compatibles calendario SHARE/periodo/empleado, versiones y contexto revalidados.
- 997Java suite +2IT PostgreSQL17 aislados (Preparacion y EtapaTres),118JS correctos;
  incluyen junio/diciembre, calendario ausente/distinto, ocasionales, concurrencia,
  revisiones previas/legadas, aplicación ficticia, permisos/CSRF/Excel multipágina.
  QA visual ficticia, sin conexión operativa, escritorio/móvil y modal Cancelar.
  Aceptación humana pendiente: no publicar guía confirmada todavía.
- Respaldo previo privado ignorado en .respaldos-locales/nomina-etapa3-v132-20261024/
  antes-v132.dump; catálogo pg_restore verificado. No leer/publicar .env ni respaldos.
  Antes de publicación: V131, BOR-000001/APLICADA/rev1/movimiento10/$12500/
  saldo posterior7500;10 movimientos totales y1 asignación calendario. No repetir pago.
- Publicada24/10/2026 a14:51México, saludUP8080/V132. Comprobación posterior:
  aplicación/revisión/movimiento/importe/saldo intactos,10 movimientos y1 calendario
  como antes; tablas nuevas sin filas operativas. Base de QA ficticia en tmpfs,
  ejecutor e imagen QA retirados; servidor y pestaña de revisión cerrados. No se
  eliminaron volúmenes operativos, imágenes previas de respaldo ni otros proyectos.
- Sigue etapa4 préstamos con saldo; etapa5 pagos/reversa individual pendiente.
  No fórmulas fiscales, prorrateos ni frecuencias nuevas. Prueba de timeout de sesión
  y pruebas de expedientes del portal familiar siguen pendientes; no resueltas aquí.
  Usuario ya liberó espacio Docker: aviso histórico de falta de espacio no implica
  necesidad de limpieza global actual. No commit/push ni datos operativos de prueba.

## Simplificación de nómina: etapa2 — 24/10/2026

- Usuario autoriza continuar etapa2. Captura separada en Configuración de nómina →
  empleado → Configurar nómina (`/{id}/editar`), con pestañas Puesto y sueldo,
  Calendario y Conceptos adicionales. Cambiar sueldo/calendario por selección explícita;
  hasta20 conceptos con vigencias propias, motivo común y modal detallado con Cancelar.
  Quitar fila sólo quita captura, no cancela registros guardados. No generar otro empleado.
- Orquestador transaccional reutiliza CondicionesSalarialesService.programar,
  ConfiguracionNominaService.guardarCalendario y ConceptoEmpleadoService.asignar.
  Empleado y laboral bloqueados, versiones/revisión salarial/calendario/conceptos
  revalidadas; segundo envío u otra pestaña rechazan. Si falla cualquier fila, rollback
  incluye sueldo/calendario/conceptos e historiales. Permisos institucionales salariales
  existentes, CSRF; no nuevas concesiones. Empleado se fija por ruta en servidor.
- Sueldo es percepción base desde condicion_salarial, UNA fuente, sin crear concepto
  duplicado ni exigir jornada/plantilla. Captura directa NO hereda jornada/horas de una
  asignación previa; se conserva ésta históricamente. Herramientas avanzadas compatibles.
  Programar cambio de concepto desde el empleado conserva anteriorId/version, valida
  pertenencia/concepto y cierra vigencia anterior sin modificar importe histórico.
- Detalle incluye todas las vigencias futuras/anteriores de conceptos y condiciones,
  con paginación; resumen/Excel conservan su fecha y filtros. El estado de conceptos
  se refiere a hoy; condiciones indican estado en fecha consultada. Captura/errores/
  cancelar/retorno mantienen contexto. Autocomplete dinámico comparte componente10/3,
  con inicialización idempotente; dinero y ayudas globales reutilizados.
- Calendario sigue referencia ACTUAL organizativa, no vigencia futura ni restricción
  del generador: etapa3 pendiente. No cálculo semanal/quincenal/proporcional/fiscal;
  no préstamos/saldos ni pagos/reversa individual (etapas4–5). No migración:V131/V132 libre.
- Verificado993 Java de suite +1 IT Configuración PostgreSQL17 aislado ampliado,
  115JS; incluye rollback tras fallo de concepto, duplicación, junio, cambio de importe,
  GET/POST/render reales, CSRF, lectura sin editar y retorno. QA visual ficticia
  escritorio1280/móvil390, claro/oscuro, autocompletado dinámico y modal con Cancelar.
  Prueba propuesta PRUEBA_SIMPLIFICACION_NOMINA_ETAPA2.md, aceptación humana pendiente.
  No publicar guía integrada confirmada todavía, no repetir BOR-000001/egreso12500.
  App sola publicada24/10 a12:20México, saludUP8080; sin migración nueva.
  Primer arranque sin espacio /tmp en Docker: retirados tres IDs exactos de caché de
  compilación de esta etapa (~418MB, regenerables), no imágenes previas de respaldo.
  Contenedor DB ficticia en tmpfs, ejecutor e imagen QA retirados; servidor/tab de vista
  ficticia cerrados. No hubo limpieza global ni borrado de volúmenes operativos.
  Consulta limitada confirma BOR-000001/rev1/movimiento10/12500/saldo posterior7500
  intactos. Sin .env/commit/push ni cambios de datos reales. Espacio Docker sigue siendo
  pendiente de mantenimiento autorizado antes de más compilaciones grandes.


## Simplificación de nómina autorizada: etapa1 — 24/10/2026

- Propietario aprueba seis módulos y configuración anual/vigencias, deducciones que
  empiecen después (ejemplo préstamo junio) y reversa de pago de UN empleado sin cancelar
  todo. Leer PLAN_SIMPLIFICACION_NOMINA.md íntegro. Etapas:1navegación/consulta,
  2configuración única,3generación por vigencias/periodo,4préstamos,5pagos/reversas
  individuales. NO afirmar2–5 implementadas. No cálculo fiscal automático ni timbrado.
- Etapa1: menú principal Empleados/Puestos/Conceptos/Periodos/Configuración/Nóminas.
  Interceptor postHandle sólo organiza modelos de navegación ya autorizados, sin alterar
  visibleCon ni servicios/permisos. Jornadas/esquemas quedan en Puestos; asignaciones/
  conceptos por empleado en Configuración; calendarios en Periodos. Nav interna clara,
  rutas y formularios anteriores compatibles. Capturas aún no fusionadas: avisos explícitos.
- Nóminas muestra estado/importe/empleados arriba, pestañas Resumen/Detalle por empleado/
  Aplicación/Historial; Gestión sólo borrador. Aplicación destaca cuenta, monto, fecha,
  actor, motivo e ID de movimiento. Enlace financiero requiere permiso existente y filtra
  movimientoId exacto más institución/alcance/clase; filtro conservado en página/Excel.
  DTO sobrecarga anterior compatible. No No autorizado en Aplicada; no cambia estados.
- Evidencia humana nueva en esta base: Carlos EMP-NOM-TEST-01, BOR-000001/revisión1
  APLICADA24/10 a10:54 según usuario, egreso12500 en BANCO-NOMINA-TEST/cuenta6,
  antes20000/después7500, no permite aplicar otra vez. Consulta acotada de sólo lectura
  confirma aplicación/movimientoID10 (NO secuencia #1). No repetir operación ni pedir
  Bruce de otra computadora. Usuario pospone expresamente guía integrada hasta simplificar.
- Sin SQL/migración ni escritura de datos operativos. V131 vigente/V132 libre. Mantener
  cambios previos sin commit de autocomplete Puesto. Verificación: 991 Java de suite
  más 3 IT PostgreSQL aislados (Preparación, Programación y Configuración), 114 JS,
  package BUILD SUCCESS y diff --check correcto. Render real prueba Aplicada/Borrador/
  Cancelada, revisión histórica, permisos, filtro exacto y Excel. QA visual con datos
  ficticios en escritorio 1280 y móvil 390, claro/oscuro: pestañas adaptables, filtros
  alineados, Nuevo/Excel legibles. Recursos Docker temporales de QA retirados, sin tocar
  volúmenes operativos. PRUEBA_SIMPLIFICACION_NOMINA_ETAPA1.md propone sólo consulta.
  Lo siguiente es etapa2, después de entregar1: fuente salarial única y captura integrada,
  no crear Sueldo como segundo importe ni dividir retrospectivamente el egreso aplicado.
- Publicada sólo app el24/10 a11:59México, saludUP8080 y Flyway131 sin migración nueva.
  Primer arranque falló por No space left on device de Docker en /tmp; retirados sólo
  tres registros identificados de caché de estas compilaciones (~418MB), no limpieza
  global ni volúmenes. Reinicio posterior correcto. Caché regenerable, sin datos perdidos.
  Docker conserva muchas imágenes/caché previas: revisar espacio antes de otra compilación,
  no borrar recursos de otros proyectos sin autorización. Aceptación visual humana pendiente.

## Puesto de referencia de Empleados desde catálogo — 24/10/2026

- Propietario trabaja en otra computadora con base distinta y sin empleados; ejemplo
  nuevo Carlos Prueba Nómina/EMP-NOM-TEST-01, no asumir Bruce ni transportar datos con Git.
  No ha confirmado guardados. Pide sustituir puesto libre laboral por ayuda de catálogo.
- Datos laborales usa autocomplete compartido10 al foco/desde3, nombre en entrada y
  código en detalle. Endpoint /admin/empleados/{id}/puestos/buscar sólo laboral de
  edición + Empleados institucional, sin salario; SQL activo/institución/LIMIT11 y
  no-store. No ampliar permisos salariales ni exponer importes. Texto nuevo/cambiado
  exige ID/nombre válidos, activo/institución bajo FOR SHARE; conserva nombre histórico.
- Sin migración/FK nueva: puesto sigue referencia textual, no segunda condición salarial.
  Texto anterior sin cambio se conserva aunque no esté catalogado/activo; no backfill
  por nombre ni cambios retroactivos al editar catálogo. ID sólo valida selección de
  captura, no se presenta como vínculo persistente. Historial/versiones/CSRF/modales
  y validaciones locales existentes conservados. Nada de sueldos/egresos por este cambio.
- PRUEBA_PUESTO_EMPLEADO_AUTOCOMPLETADO.md documenta orden corregido: crear/reutilizar
  puesto ANTES de confirmar laboral; luego ejemplo mensual Carlos12000+bono500 enero.
  No repetir altas ni aplicar dinero. Guía confirmada pendiente de prueba humana.
- Verificación:985 Java de suite y1 IT PostgreSQL17 aislado correctos,114 JS y package
  BUILD SUCCESS. IT con131 migraciones y datos ficticios: ayuda11/10+hayMas, mínimo3,
  búsqueda literal/código, inactivo/ajeno, selección y nombre alterados, snapshot tras
  renombrar/desactivar, CSRF, POST real válido/error con captura, historial y cero
  condiciones/movimientos nuevos. Correcciones de fixture (email/contexto tras MVC)
  antes del resultado final; no fueron cambios de datos operativos.
  Sin cambios CSS ni QA visual nueva: componente compartido y render real verificados,
  aceptación móvil/claro/oscuro del propietario pendiente. QA DB/volumen/etiqueta
  temporal retirados; sin .env, cambios operativos ni commit/push.
  V131 última migración/V132 libre; otros pendientes de nómina intactos.
- App sola recreada24/10 a09:48México; saludUP8080 y Flyway131 validada sin nuevas
  migraciones. PostgreSQL/volúmenes conservados. Recargar/iniciar sesión y continuar
  Carlos desde el paso efectivamente guardado, creando puesto antes de elegirlo.

## V131 — Configuración de nómina por empleado — 23/10/2026

- Verificación final:985 Java correctas (981suite+PreparacionNominaIT y cuatro IT
  aislados: nuevo Configuración, ConceptoNomina, ConceptoEmpleado y Programación),
  114JS, package BUILD SUCCESS. PostgreSQL17 ficticio comprobó31 empleados en dos
  páginas, fecha futura/fin de bono, fuentes sin duplicar, Excel31/1filas, formulario
  validación/CSRF/retorno con filtro de calendario distinto del campo a guardar,
  preselección de empleado, calendario inactivo/otra institución, etiqueta alterada,
  versiones/carrera dos pestañas, historial inmutable y permisos/portales. Sin egresos.
  Navegador: autocompletado al foco/modal/cancelar, escritorio claro y móvil390 oscuro
  sin desborde; tarjetas agrupan identidad/estado y acciones completas, no icono＋.
- Respaldo privado ignorado configuracion-nomina-v131-20261023/antes-v131.dump validado.
  App sola recreada23/10 a10:19México, saludUP/Flyway131success; tablas nuevas0/0.
  Conteos anteriores conservados:4 condiciones,2 conceptos,1BORRADOR y46 movimientos.
  No asignaciones reales de calendario ni modificación de configuraciones/pagos por
  herramientas. QA temporal eliminada, visor/pestaña cerrados, tema/viewport restaurados,
  Dockerfiles retirados. Sin .env, commit/push; aceptación humana y guía pendientes.

- Primera entrega autorizada después del análisis de flujo: menú propio Configuración
  de nómina, listado25/filtros/autocompletado10 al foco/desde3, fecha de consulta,
  Excel POI por bloques con mismos filtros y detalle Resumen/Conceptos/Historial.
  Reutiliza fuentes canónicas salariales y conceptos: sin segunda captura/snapshot
  salarial aquí. Accesos a Asignaciones laborales y Conceptos por empleado originales;
  nueva asignación de concepto desde empleado preselecciona identidad, no duplica bono.
  Enlace privado desde Empleados → Condiciones salariales. Diseño compartido sin CSS
  paralelo, ayudas de módulo y modal con Cancelar antes de guardar calendario.
- V131 agrega empleado_calendario_nomina (una referencia actual, no vigencias) y su
  historial inmutable antes/después/motivo/actor/fecha. Sin backfill ni datos inventados.
  FK institucionales, sin DELETE/traslado de empleado, bloqueo/versiones/calendario
  activo/nombre revalidado e institución activa. Mismos permisos privados salariales
  +Empleados/Laboral, sin grants ni acceso docente/familias/recuperación. Lectura no
  ofrece formularios/escrituras; CSRF y doble pestaña protegidos.
- Fecha consultada sólo afecta sueldo/conceptos vigentes ese día, NO elegibilidad de
  periodo completo; calendario siempre actual. Esta asociación es organizativa en
  primera entrega: no filtra ni altera aún el generador mensual V129/V130, no convierte
  sueldo según calendario ni recalcula borradores existentes. Aviso explícito si falta
  sueldo/calendario inactivo/frecuencias distintas. No movimientos financieros.
- PRUEBA_CONFIGURACION_NOMINA_V131.md pendiente humano: reutilizar Bruce/enero al
  15/01/2027 (12000+bono500, jornada histórica30), no repetir datos ni cancelar su
  borrador previo. Asignar NOM-MENSUAL-TEST, cancelar modal, confirmar/historial,
  filtros/Excel y cambio alterno con dos pestañas/restauración opcionales. Corregir
  nombres de ruta/módulo en explicación si el usuario pregunta; no aplicar nómina.
- Pendientes siguientes: conceptos ocasionales por periodo, porcentajes/incrementos,
  reglas semanales/quincenales/proporcionales, integración funcional de calendario
  asignado con compatibilidad/backfill explícitos, pagos multicuenta y reversa.
  No dar por terminado cálculo fiscal, préstamos o toda nómina. Próxima migración V132.
  V129/V130 sigue pendiente de ensayo humano, no publicar guía confirmada aún.


## V129/V130 — Nómina global y aplicación financiera autorizadas — 22/10/2026

- Verificación final:981 Java correctas (978 suite general/IT nuevo + tres IT previos
  aislados en DB limpias),113 JS, package BUILD SUCCESS. PostgreSQL17 ficticio probó
  140 elegibles/un borrador, paginación25, exclusiones persistentes, Excel global vs
  filtro, revisión140 sin truncar, sellos/fuentes/duplicados/IDOR/caducidad, permisos,
  saldo insuficiente y aplicación simultánea: exactamente un egreso, monto/saldos
  correctos, APLICADA inmutable y reversa manual rechazada. Periodo vacío da validación
  legible. QA visual claro/oscuro/escritorio/móvil390: sin desborde, tarjetas/modal
  y Cancelar comprobados, fixtures ficticias y todos los POST bloqueados en visor.
- Respaldo privado ignorado .respaldos-locales/nomina-global-v129-v130-20261022/
  antes-v129-v130.dump validado antes de publicar. Sólo app recreada22/10 a17:57México;
  saludUP y Flyway129/130success. Tablas preview/filas/aplicación0; se conserva único
  BORRADOR real y46 movimientos anteriores, sin aplicación ni cambios financieros
  operativos del agente. QA temporal eliminada, visor/pestaña cerrados, tema/viewport
  restaurados y Dockerfiles retirados. Sin .env, commit/push. Falta prueba humana.

- Propietario pidió un borrador para todo el periodo, revisar total/empleados antes de
  aplicar y elegir cuenta para reflejar el egreso. Se implementan V129 global y V130
  aplicación interna, NO transferencia bancaria ni nómina fiscal. Sustituye máximo100
  y sólo borrador del flujo principal V128; mantener sus antecedentes y datos aceptados.
- /admin/preparacion-nomina/nuevo: periodo, Todos los elegibles por defecto, opción
  individual vacía. Instantánea privada UUID/usuario/institución en BD, caduca2h;
  tabla25 y agregados SQL globales, exclusiones persistentes entre páginas/filtros.
  Excel POI por bloques: Resumen global de selección y Consulta con mismos filtros.
  Sin lista completa en navegador ni límite100 global. /seleccion-manual conserva
  compatibilidad anterior, no usarla para truncar actualizaciones.
- Guardar todos en UN borrador, revalidando fuentes/sellos/versiones bajo bloqueos.
  Actualización global conserva todos los originales y crea otra revisión inmutable,
  también >100. Si alguno deja de ser elegible bloquea todo: no cero ni actualización
  parcial. No agrega nuevos empleados silenciosamente. Preview/descarte no mueve dinero.
- Gestión → Revisar y aplicar nómina: cuenta CAJA/BANCO activa institucional/misma
  moneda, motivo Egreso/Ambos y motivo administrativo. Previa saldos/total/cuenta,
  confirmación independiente con cancelar. Paquete salarial privado más
  MOVIMIENTO_FINANCIERO_REGISTRAR, sin grants. Revalida fuentes, versión/saldo/motivo;
  bloquea periodo/empleados/cuenta, registra UN EGRESO clase NOMINA, clave NOMINA:{id},
  nomina_aplicacion inmutable y APLICADA en una transacción. Dos pestañas no duplican.
  Fecha/hora actual institucional, NO programación futura ni dispersión bancaria.
- Aplicada sólo consulta: no actualizar/cancelar/desactivar filas ni revertir como
  movimiento manual. Reversa especializada pendiente. Aportaciones patronales aparte,
  no incluidas en egreso al personal; impuestos/préstamos/incidencias/prorrateos/
  porcentajes/recibos pendientes. Total cero bloquea egreso. Desglose privado en nómina;
  concepto financiero sin nombres/sueldos individuales.
- PRUEBA_NOMINA_GLOBAL_V129_V130.md: ensayo humano/guía confirmada pendientes.
  Conservar Bruce DOC-PRUEBA-02 enero/revisión1/12500 aceptado12000+500; ya incluido
  debe salir No incluido en otra preparación coincidente, no cancelar/recrear para
  forzar ensayo. No asumir ID/folio ni usar febrero Cancelado. No operaciones reales
  de dinero por herramientas. Próxima migración V131. Publicación/QA final arriba;
  no equivale a aceptación humana ni cierra toda la etapa de nómina.


## Ver revisión repetida: navegación de pestañas — 22/10/2026

- Propietario reporta Ver revisión1 funciona primera vez, no segunda al volver a
  Historial. Causa: primera visita cambia query/carga documento; consultas sucesivas
  de misma revisión sólo cambian #historial→#empleados y student-tabs.js atendía
  DOMContentLoaded, no hashchange. No problema de importes/servidor/Excel.
- Componente compartido ahora reconoce hashchange válido, activa panel y enfoca
  pestaña correspondiente; mantiene carga normal si cambia revisionConsulta, error
  inicial/prioridad y botones/teclado. Hash ajeno no oculta contenido. Dos regresiones
  nuevas (repetición/atrás/adelante/hash desconocido);111 JS completas correctas.
  Navegador ficticio comprobó dos ciclos misma query/revisión, aria-selected true.
  Sin Java/SQL/migración/datos reales; V129 libre. Resto prueba V128 pendiente.
- Package BUILD SUCCESS y sólo app recreada a17:22México. Visor/pestaña/Dockerfiles
  retirados, sin .env, commit/push o modificación de borradores. Repetir consulta
  de revisión1 después de recargar/iniciar sesión si la sesión se perdió al recrear app.

## Detalle del borrador: acciones discretas y avance de prueba — 22/10/2026

- Propietario confirmó guardado Bruce, revisión1 Borrador, sueldo12000+bono500,
  propuesta12500, enero01–31/01/2027/pago31/01, calendario NOM-MENSUAL-TEST. Confirmó
  desglose sueldo12000 y BON-PUNT-TEST500 desde01/01 sin fin, Excel correcto y auditoría
  Registro1 por criz22/10/2026 17:10 con motivo original. NO confirmó aún pulsar
  Ver revisión1, duplicados, actualización o cancelación; no dar por cerrado ni publicar guía.
- Sólo presentación nomina-detalle.html/nomina.css scoped: Excel, Ver desglose,
  Ver revisión y acceso de actualización con acciones secundarias compactas sin gradiente
  intenso. Revisión en tarjeta con título/fecha/usuario fuera del enlace, indicador
  En consulta, botón corto. Mantiene rutas/ID/revisionConsulta/filtros/Excel/CSRF/datos.
  Contenedor algo más amplio para no comprimir tabla y acciones sin saltos arbitrarios.
  11Java dirigidas y4JS correctas; escritorio claro/móvil390oscuro revisados sin
  desborde (scrollWidth390). No Java/SQL/migración; V129 libre. Datos reales intactos.
- Package BUILD SUCCESS; sólo app recreada22/10 a17:19México. Sin .env, cambios BD,
  commit/push. QA temporal eliminada, visor/pestaña/Dockerfiles retirados y tema/
  viewport restaurados. Excel sigue sin cambio de lógica; seguir prueba desde revisión1.

## Ajuste visual de Nuevo borrador mensual — 22/10/2026

- Propietario está en punto3 de prueba V128: consultó enero/Bruce y confirmó que se
  visualizan12500. Aún NO guardó ni confirmó el resto; no marcar prueba completa ni
  publicar guía. Pide ordenar botones/formularios/tabla recortada, mismo diseño.
- Causa visual: fieldset sin min-width0 conservaba el ancho intrínseco de ocho columnas,
  desbordaba contenedor y quedaba recortado por form-card. Sólo nomina-nuevo.html y
  CSS scoped nomina-capture: ancho máximo1120, min-width0/grid minmax, cinco columnas
  (incluir,empleado/revisión,sueldo,desglose conceptos,propuesto destacado). Mantiene
  todos los importes/motivos, selección/tokens/CSRF/filtros y exportaciones intactos.
  Clasificaciones como navegación discreta con conteos; exportar y selección como
  botones secundarios neutros. Periodo y acciones alineados, barra selección separada.
  Móvil reutiliza tarjetas del sistema; sin paleta paralela ni cambios globales.
-11 Java dirigidas correctas,4JS de selección/modales correctas. Revisión con datos
  ficticios en escritorio oscuro y móvil390: scrollWidth igual clientWidth, tabla
  dentro de fieldset y total visible. Sin cambios Java/BD/migración/negocio; V129 libre.
  Retomar prueba punto3 consultando enero/Bruce; después seleccionar y revisar borrador.
- Package BUILD SUCCESS y app sola recreada a17:05México; sin migración/DB/.env,
  commit/push ni cambios a los registros del propietario. Tamaños1280oscuro/1024claro/
  390oscuro sin desborde comprobados, totales y selección visibles. QA temporal
  desechable, visor/pestaña y Dockerfiles locales retirados, viewport/tema restaurados.

## V128 — Preparación de nómina mensual autorizada — 22/10/2026

- Verificación final:979 pruebas Java correctas (976 de suite general con IT nuevo,
  más tres regresiones PostgreSQL aisladas de conceptos/programación),109 JS y
  package BUILD SUCCESS. IT nuevo adicional final verifica conteos HTML, POST real
  con CSRF/retorno, Excel filtrado y por revisión, snapshots inmutables, fuentes
  obsoletas, selección repetida, carrera entre pestañas, permisos/maestro/otra institución,
  actualización/cancelación sin tocar movimientos/sueldos/conceptos. Fixtures requieren
  DB limpia por IT: ConceptoNominaPostgresIT comprueba ausencia global de sueldos;
  no ejecutar juntos con fixture salarial nuevo en una misma DB sin aislarlos.
  AyudaContextualInterfazTest verifica carga del theme también mediante head compartido.
  Render revisado escritorio claro, móvil390 oscuro, tarjetas/nombres/códigos y modal.
- Respaldo privado ignorado .respaldos-locales/preparacion-nomina-v128-20261022/
  antes-v128.dump validado. Sólo app actualizada22/10 a16:38México; saludUP y
  Flyway128success; cuatro tablas nuevas vacías comprobadas, sin datos reales de ensayo.
  PostgreSQL/volúmenes conservados. Sin leer .env, modificar Bruce/periodos/dinero,
  commit ni push. Prueba humana y guía confirmada de esta etapa siguen pendientes.

- Usuario autorizó implementar el siguiente paso y continuar. Nuevo módulo propio
  Administración → Personal y nómina → Preparación de nómina, /admin/preparacion-nomina.
  Sólo borradores: periodo Programado/calendario activo, mes gregoriano completo,
  sueldo base/frecuencia Mensual y conceptos habituales fijos vigentes toda la cobertura.
  Propuesto = sueldo + percepciones − deducciones; aportaciones patronales separadas.
  No es neto fiscal ni nómina autorizada: sin impuestos, prorrateos, incidencias,
  préstamos, porcentajes, autorizaciones, pagos ni movimientos financieros.
- SQL clasifica antes de paginar: LISTO, REVISION, NO_INCLUIDO y motivo individual.
  Parciales/cambios dentro del mes no se resuelven asignando cero: importes null,
  selección bloqueada. Cambios de frecuencia tampoco se convierten automáticamente.
  Filtros de listado y consulta de candidatos separados; ayudas10 al foco/desde3,
  mismos criterios en Excel POI por bloques, retorno de filtros, formularios aparte,
  confirmaciones Cancelar/Confirmar para guardar/actualizar/cancelar y diseño compartido.
  Selección de página actual explícita, máximo100 por borrador; se permiten otros
  borradores del mismo periodo para empleados distintos, no duplicaciones coincidentes.
- Migración V128 crea nomina_borrador/revision/empleado/historial. Copias JSON+SHA256,
  fechas/importes/códigos/nombres guardados, revisiones inmutables. Actualizar exige
  previa y motivo, conserva empleados originales, incrementa revisión/version; si uno
  dejó de ser elegible bloquea toda actualización. Cancelar libera inclusión sin borrar
  copias ni tocar configuraciones. Historial20/revisiones20/empleados25 por página.
  Las revisiones anteriores muestran sus propios metadatos y exportan su desglose.
- FK institucionales/exclusión GiST por empleado/fechas y unicidad por periodo;
  institución/calendario/periodo/empleados bloqueados y sellos revalidados, versión
  optimista en actualizar/cancelar. Reutiliza permisos privados salariales+Empleados+
  Laboral y administrador institucional persistido activo, sin grants/portal/recuperación.
- PRUEBA_PREPARACION_NOMINA_V128.md deja recorrido detallado aún NO confirmado:
  usar enero existente ENE-2027-TEST y Bruce, no repetir altas ni cambiar fechas.
  Si únicamente tiene sueldo12000 y bono500, propuesta12500; comprobar configuración
  real antes de exigir cifra. No cancelar bono ni asignación aceptados. Guía confirmada
  de esta entrega pendiente de prueba humana. Próxima migración V129; primero aceptar
  este recorrido y después acordar incidencias/revisión/autorización, sin asumir pagos.
- Las notas de V127 de sólo analizar quedan superadas por esta autorización;
  aceptación exacta de enero/febrero y registros anteriores se preserva.

## V127 — Guía de calendarios y periodos confirmados — 22/10/2026

- Verificado:974 Java completas/105 JS correctas y package BUILD SUCCESS. PostgreSQL17
  desechable comprobó migración, render de guía12etapas/cuatro acciones, tres permisos
  privados, acceso denegado sin paquete y Excel12filas con filtro exclusivo del caso.
  El título también aparece en referencia de guía anterior: búsqueda general por
  contenido puede incluir ambas, por eso ensayo Excel usa TRASLAPE-2027-TEST.
  Revisión ficticia escritorio claro/móvil390 oscuro sin desborde; mismo diseño.
- Respaldo privado ignorado .respaldos-locales/guia-periodos-v127-20261022/antes-v127.dump
  validado. Sólo app actualizada22/10 a15:36México; saludUP/Flyway127success y guía
  CONFIRMADA/12 comprobadas. QA eliminada, visor/tab cerrados, tema/viewport restaurados,
  Dockerfiles temporales retirados. Sin .env, cambios de personal/periodos/dinero,
  commit ni push. Se preservan cambios previos V126 y tema que estaban sin confirmar.

- Propietario confirmó todos los pasos principales y compartió tabla final: calendario
  NOM-MENSUAL-TEST · Nómina mensual de prueba Mensual Activo. Enero ENE-2027-TEST,
  nombre Enero de 2027 — revisión A,01/01–31/01/2027,31días,pago31/01/2027,fecha
  opcional incidencias28/01/2027,Programado/Próximo. Febrero FEB-2027-TEST,01/02–28/02,
  28días,pago28/02/2027,incidenciasSin definir,Cancelado/No aplica. RevisiónB bloqueada,
  traslape rechazado, filtros/Excel/retorno, edición/historial y desactivar/reactivar
  confirmados. No repetir operaciones ni borrar la fecha opcional de enero.
- Usuario autoriza incorporar a Guía. V127 sólo editorial agrega Personal:
  Calendarios y periodos de nómina: programar, revisar y cancelar, slug
  calendarios-periodos-nomina-confirmado,12etapas/cuatro acciones con módulos/botones/
  capturas/resultados/precauciones. Reutiliza diseño/servicio/permisos privados de guía
  y paquete Empleados/Laboral/Salarios, sin grants. Enlaza desde cierre V124; no cambia
  calendarios/periodos/sueldos/cuentas. Acta V125 actualizada; notas de pendiente abajo
  quedan como antecedentes superados para el recorrido reportado, no auditoría exhaustiva.
- Próxima migración V128. Siguiente ANALIZAR preparación de nómina, no implementar
  aún: elegibilidad por vigencias, frecuencia de calendario vs salario, sueldo/conceptos,
  revisión explícita de parciales/cambios dentro de periodo y vista previa antes de
  autorizar. No asumir prorrateos, impuestos, préstamos, ocasionales ni pagos.

## V126 — Fecha opcional de incidencias — 22/10/2026

- Package BUILD SUCCESS, respaldo privado ignorado
  .respaldos-locales/incidencias-opcionales-v126-20261022/antes-v126.dump validado.
  Sólo app actualizada a15:07México: salud UP, Flyway126success, fecha_corte nullableYES.
  PostgreSQL/volúmenes preservados, QA eliminada, visor/tab cerrados, tema/viewport
  restaurados, Dockerfiles temporales retirados. Sin .env ni commit/push.

- Propietario en punto3 de ensayo V125 pidió simplificar término Corte de novedades.
  Aprobó hacerlo opcional en Configuración avanzada y cambiar etiqueta exactamente a
  Fecha límite para recibir incidencias de nómina. Principal: inicio/fin/pago previsto.
  Ayuda faltas/bonos/descuentos que podrían afectar pago, no editar datos personales.
  Sólo referencia: no bloquea capturas ni aplica cambios; incidencias reales pendientes.
- V126 relaja NOT NULL de fecha_corte y reemplaza CHECK de fechas: fin>=inicio,
  pago>=inicio y, si hay opcional, inicio<=fecha_corte<=pago. No backfill ni UPDATE
  de registros previos; no modificar V125 aplicada. Mantiene permisos/historial/versiones
  y rangos. Detalle/tabla/modal/Excel nulos muestran Sin definir, etiqueta actualizada.
  Nuevo desplegable cerrado sin fecha; abierto si hay dato o error. Diseño compartido,
  CSS sólo16px de separación scoped al desplegable, móvil390 sin desborde.
- 972 Java completas/105 JS correctas; PostgreSQL17 temporal valida null en alta,
  edición añadir/quitar fecha, historial, reglas opcionales, POST con campo vacío,
  render/Excel, protección y regresiones. Comparación visual escritorio claro/móvil oscuro.
  PRUEBA_CALENDARIOS_PERIODOS_NOMINA_V125.md actualizada: continuar punto3 con fecha
  opcional vacía, no repetir calendario ni registros Bruce/BON-PUNT-TEST aceptados.
  Guía confirmada sigue pendiente del ensayo humano; próxima migración V127.
- Propietario confirmó causa de colores anteriores: extensión del navegador que
  modifica tema (Dark Reader), no fallo de tabla. Se conserva ajuste preventivo previo
  theme.js, sin revertirlo. No seguir investigando ese caso salvo nuevo reporte.

## Tema consistente en primera carga — 22/10/2026

- Propietario muestra dos capturas de Empleados tras reinicio con colores distintos
  que se normalizan al recargar. /admin abre primer módulo autorizado (Empleados)
  por diseño; no se cambió navegación ni permisos. Cabeceras reales CSS/JS ya usan
  no-cache/no-store: no atribuir el caso a caché sin evidencia.
- theme.js ahora aplica preferencia válida light/dark y color-scheme only light/dark
  sincrónicamente antes de insertar componentes, para declarar paleta propia al navegador
  y evitar adaptación automática adicional. Mantiene colores existentes. Lee/escribe
  localStorage con tolerancia a bloqueo; enlaza botones también con DOM ya cargado.
  Referencia técnica: https://developer.chrome.com/blog/auto-dark-theme
- 105 JS y7 Java dirigidas login/reinicio correctas; package BUILD SUCCESS. Comparación
  DOM en vista ficticia con CSS reales: fondo/estados/bordes idénticos antes/después
  de recargar en claro y oscuro; only dark soportado. Chrome real no pudo inspeccionarse
  por falta de permisos Computer Use: causa específica del navegador original no
  reproducida ni demostrada. Solicitar confirmación humana tras actualizar, no prometer
  haber verificado extensiones/flags. Visor/tab cerrados y tema ficticio restaurado.
- Sin migración: siguiente V126, ensayo V125 sigue pendiente. Sólo app actualizada,
  sin editar DB/datos/.env ni commit/push. Dockerfiles temporales retirados.

## V125 — Calendarios y periodos de nómina — 22/10/2026

- Verificado:971 Java completas/101 JS correctas y package BUILD SUCCESS. PostgreSQL17
  desechable validó migración, fechas inclusivas/traslapes, dos transacciones, versiones,
  snapshots, cancelación/historial inmutable, permisos/tenant/CSRF, render8 pantallas,
  filtros y Excel. Revisión ficticia escritorio claro/móvil390 oscuro con ayudas y
  modales, sin CSS nuevo ni guardados reales. Primera regresión se interrumpió por
  parada de contenedores temporales; repetición completa final sin fallos.
- Respaldo privado ignorado .respaldos-locales/programacion-nomina-v125-20261022/antes-v125.dump
  validado. Sólo app actualizada22/10 a12:30México; salud UP, V125 success y catálogos
  nuevos vacíos(0/0). QA eliminada, visor/pestaña cerrados, tema claro/viewport restaurados
  y Dockerfiles temporales retirados. No .env, cambios a sueldos/cuentas, commit o push.

- Propietario aprobó implementar sólo organización de fechas, separada de calendario
  escolar y configuración salarial: Calendarios de nómina y Periodos de nómina.
  No preparar/calcular nómina, prorratear, asignar empleados ni generar pagos/cuentas.
- Calendario código/nombre/descripción/frecuencia mensual-quincenal-semanal/activo;
  periodo calendario histórico/código/nombre/cobertura inclusiva/corte/pago previsto/
  motivos, PROGRAMADO o CANCELADO terminal. Programado no equivale a pagado; situación
  Próximo/En curso/Transcurrido sólo describe fechas según zona institucional.
- V125 usa FK compuesta institucional, exclusión GiST de traslapes programados por
  calendario, versiones/bloqueos e historial privado inmutable antes/después con actor.
  Fin>=inicio, corte>=inicio, pago>=corte; corte puede quedar después de fin. Fechas
  explícitas sin duración automática. Frecuencia protegida tras cualquier periodo,
  incluso cancelado. Cancelar libera fechas, no reutiliza código ni borra/reabre.
- Mismos permisos privados Empleados/Laboral/Salarios y admin institucional persistido
  activo, sin grants. Mismo diseño claro/oscuro/móvil, módulos separados tabla-formulario,
  detalle Resumen/Historial/Gestión, ayudas10 al foco/desde3, SQL paginado, Excel POI
  por bloques con mismos filtros y retorno conservado, confirmaciones Cancelar/Confirmar.
- Prueba humana PENDIENTE: PRUEBA_CALENDARIOS_PERIODOS_NOMINA_V125.md. No registrar
  ensayos por el propietario. Bruce DOC-PRUEBA-02 y BON-PUNT-TEST500 aceptados intactos.
  No declarar V125 confirmada ni publicar guía hasta su reporte. Próxima migración V126.
  Después de aceptación, documentar Guía y diseñar preparación/elegibilidad/vigencias/
  parciales con revisión explícita, sin asumir prorrateo o impuestos/préstamos/pagos.
  Las notas inferiores que pedían analizar calendarios quedan como antecedentes.

## V124 — Guía de Conceptos por empleado confirmada — 22/10/2026

- Verificación:965 Java completas/96 JS correctas. Tras pulir legibilidad,3 pruebas
  dirigidas y package BUILD SUCCESS; PostgreSQL17 desechable comprobó migración,
  diez etapas/cuatro acciones, render, Excel10 filas y cuenta sin paquete denegada.
  Reutiliza plantilla/estilos de guía existentes, sin cambios visuales o de negocio.
- Respaldo privado ignorado .respaldos-locales/guia-conceptos-empleado-v124-20261022/antes-v124.dump
  validado sin mostrar contenido. Sólo app actualizada22/10 a11:55México; salud UP,
  V124 success y guía CONFIRMADA/10 comprobadas. Base QA eliminada y Dockerfiles
  temporales retirados. No .env, operaciones de personal/dinero, commit ni push.

- Propietario reporta todas las pruebas correctas y comparte cierre: Bruce Wayne
  DOC-PRUEBA-02, BON-PUNT-TEST Percepción/Habitual;500MXN desde01/01/2027 sin fin,
  Próxima;600 desde01/02/2027 sin fin, Cancelada. Restauración confirmada. No repetir
  altas/cambios/cancelaciones ni cancelar500. Aceptación no equivale a auditoría exhaustiva.
- Usuario autoriza incorporar a Guía y decir siguiente paso. V124 agrega sólo contenido
  editorial Personal: Conceptos por empleado: asignar, cambiar y cancelar un bono;
  slug conceptos-por-empleado-bono-confirmado. Diez etapas/cuatro acciones numeradas,
  módulos/botones/capturas/resultados/precauciones,10/3/filtros/Excel/retorno, modales,
  cambio con fecha vacía explícita01/02 vs02/01, cierre previo y restauración posterior.
- Reutiliza permisos de guía y salariales/laborales existentes, sin concederlos.
  Enlaza desde cierre de guía de catálogo V122. No modifica conceptos, asignaciones,
  sueldos, cuentas ni datos de ensayo. PRUEBA_CONCEPTOS_EMPLEADO_V123.md confirmada.
  Las notas inferiores de aceptación pendiente son antecedentes superados para este caso.
- Próxima migración V125. Siguiente: ANALIZAR calendarios y preparación de nómina,
  periodos/frecuencias/cortes, sueldo y conceptos por vigencia, parciales/elegibilidad
  y revisión antes de autorización. No implementar ni asumir impuestos, préstamos,
  porcentajes, ocasionales o pagos sin definirlos con el propietario.


## Corrección visual y fecha explícita al programar concepto — 22/10/2026

- Verificado:6 Java dirigidas (PostgreSQL17 desechable/render/fecha nueva vacía/
  referencias anteriores y reglas existentes),96 JS correctas y package BUILD SUCCESS.
  Revisión ficticia escritorio claro/móvil390 oscuro: aviso23px debajo de los inputs,
  sin superposición; sin CSS nuevo. Sólo app actualizada22/10 a11:37México, salud UP.
  Base/archivos/fechas guardadas intactos; QA eliminada, servidor/tab cerrados,
  tema/viewport restaurados y Dockerfiles temporales retirados.

- Propietario reporta en punto6 mensaje contextual encima de inputs al cargar cambio.
  Causa: p.field-help fuera de form-grid heredaba margen -8px. Contexto, errores y
  notas ahora están en filas wide de la cuadrícula con small.field-help (margen5px),
  usando el mismo diseño común; no se modifica CSS global ni importes/registro anterior.
- Cambio ya NO propone automáticamente el siguiente día válido: inicio nuevo vacío,
  obligatorio, etiqueta Inicio del nuevo importe, mínimo posterior a hoy y al inicio
  previo. Tarjeta de referencia muestra importe/inicio/fin anteriores dd/MM/yyyy.
  Esto evita confundir02/01/2027 (2enero) con01/02/2027 (1febrero) del ejemplo.
  Nuevo registro conserva fecha inicial hoy; reglas de servicio sin cambios.
- Ensayo V123 sigue pendiente en punto6: elegir600 e inicio01/02/2027 para terminar
  los500 el31/01/2027. No guardar ni corregir registros reales por el agente.
  Sin migración: siguiente V124. Se preserva corrección anterior de autocomplete y
  cambios del propietario. Sin .env, commit ni push.


## Corrección del autocompletado de Concepto habitual — 22/10/2026

- Sólo app actualizada22/10 a11:23México; salud UP y recurso JavaScript corregido
  servido comprobados. Base/archivos conservados; Dockerfiles temporales retirados.

- En prueba humana V123, punto2, el propietario informa ayuda sin opciones. Causa:
  autocomplete.js concatenaba siempre '?' aunque el endpoint ya tenía ?captura=true;
  enviaba captura=true?q= y Spring rechazaba el booleano. No era ausencia de catálogos.
- Se separa ruta/consulta fija y se combinan con URLSearchParams.set. Conserva
  captura=true y q vacío al foco o texto desde3; selección/limpiar y helpers existentes
  sin cambios de diseño, permisos o elegibilidad. Sólo habituales activos para alta.
- Nueva prueba de regresión reproduce endpoint real con parámetro fijo y exige una
  sola '?', captura exactamente true, q independiente, opciones al foco, mínimo3 y
  reapertura al limpiar.96 JS correctas. Package BUILD SUCCESS; Java sin cambios
  funcionales (empaquetado con skipTests; no se repitió suite Java en esta corrección).
- Sin migración; siguiente sigue V124. No datos de prueba operativos ni cambios de
  catálogos/salarios/cuentas/permisos. Retomar punto2 seleccionando BON-PUNT-TEST;
  DESC-TEST ocasional continúa excluido. Ensayo humano pendiente, no documentar como
  confirmado todavía. Sin .env, commit ni push.


## V123 — Conceptos por empleado — 22/10/2026

- Verificación:962 pruebas Java completas y95 JavaScript correctas. Tras ajustes finales,
  5 pruebas dirigidas y package BUILD SUCCESS; PostgreSQL17 desechable verificó vigencias,
  duplicados concurrentes, snapshots/auditoría inmutable, baja/inactivo, versión laboral,
  aislamiento/permisos/CSRF, render de listado/formulario/detalle/cambio y POST válido
  con filtros separados; Excel filtrado y paginación DB. Sin cambios de salarios/dinero.
- Revisión visual ficticia escritorio claro y móvil390 oscuro: ayudas al foco, campos
  monetarios y fechas, modales Cancelar/Escape y pestañas. Indicadores01–03 usan la
  composición compartida y evitan títulos comprimidos en móvil; sin estilos propios.
- Respaldo privado ignorado .respaldos-locales/conceptos-empleado-v123-20261022/antes-v123.dump
  validado sin mostrar contenido. Sólo app actualizada22/10 a10:30México; salud UP,
  V123 success y tablas operativas nuevas vacías comprobadas. Contenedor QA eliminado,
  servidor/tab ficticios cerrados, tema/viewport restaurados. Sin .env, datos de ensayo
  operativos, commit ni push. Catálogos/salarios existentes conservados.


- Usuario autoriza importes fijos habituales y exige filtros con ayudas10 al foco y
  búsqueda desde3 caracteres. Menú Personal y nómina → Conceptos por empleado,
  /admin/conceptos-empleado; tabla paginada, alta aparte, detalle por pestañas
  Resumen/Historial/Gestión, POI por bloques y mismos filtros/retorno. Diseño compartido
  claro/oscuro, campos monetarios texto y modales Cancelar/Confirmar, sin CSS nuevo.
- Filtros independientes empleado/concepto por ID, tipo histórico, estado de vigencia,
  texto y rango de vigencias coincidentes. Ayudas limitadas por institución; catálogo
  inactivo consultable en filtros pero captura sólo habituales activos. Índices trigram
  para búsquedas literales; no carga colecciones completas ni filtra etiquetas concatenadas.
- ConceptoEmpleado congela código/nombre/tipo/aplicación/versión del catálogo, importe
  fijo Decimal19,2 por nómina elegible, moneda institucional, fechas inclusivas y motivos.
  No es mensual para prorratear; elegibilidad/prorrateo se diseñarán con preparación de
  nómina. Sueldo base sigue sólo en CondicionSalarial. No cálculos/impuestos/pagos/dinero.
- Alta sólo empleado activo con ingreso/situación ACTIVO, institución activa y fechas
  hoy/futuro no anteriores a ingreso. Contexto revalida laboral/catálogo/etiquetas bajo
  bloqueos. Exclusión DB impide superposición pareja empleado/concepto aun en carreras.
- Programar cambio crea otra vigencia y cierra anterior día previo sin alterar importe;
  no intercala posteriores. Cancelar sólo última futura restaura fin previo en transacción.
  Terminar vigente incluye día elegido hoy/futuro, sin extender fin ni borrar pasado.
  Historial privado inmutable antes/después/actor/fecha/motivo, versiones optimistas.
- Mismos permisos salariales/laborales, sin concesiones nuevas; administrativo activo
  persistido institucional, no recuperación/docente/familia/plantel. Ocasionales y
  porcentajes pendientes para preparación, no se automatizan aquí.
- Prueba humana PENDIENTE: PRUEBA_CONCEPTOS_EMPLEADO_V123.md. No añadir como confirmada
  a Guía hasta aceptación. No repetir catálogos confirmados ni cambiar salarios de Bruce.
  Próxima migración V124. Siguiente: ensayo bono500 enero/cambio600 febrero/cancelar
  futuro y restaurar, filtros/Excel/modales; después calendarios/preparación de nómina.


## V122 — Guía de Conceptos de nómina confirmados — 21/10/2026

- Usuario confirma todas las pruebas del catálogo, filtros/Excel y dos pestañas;
  aclara que probó activar/desactivar DESC-TEST y lo dejó Activo al terminar. Los
  tres conceptos BON-PUNT-TEST, DESC-TEST y APORT-TEST quedaron Activos. No repetir
  altas ni desactivar para documentar. Aceptación funcional no es auditoría exhaustiva.
- Autoriza incorporar a Guía de procesos y después decir siguiente paso. V122 agrega
  sólo contenido editorial: categoría Personal, Conceptos de nómina: crear, filtrar
  y proteger cambios; slug conceptos-nomina-catalogo-confirmado;10 etapas con cuatro
  acciones numeradas, módulos/botones, captura/resultado y precauciones. Incluye
  Cancelar modal vs formulario, tipos/efectos, filtros/Excel, edición, desactivación/
  reactivación, duplicados y protección entre pestañas; no cálculos ni operaciones.
- Reutiliza permisos salariales/laborales y Guía existentes, sin concederlos ni
  cambiar conceptos/sueldos/cuentas. Enlaza desde cierre de guía laboral V120.
  Acta PRUEBA_CONCEPTOS_NOMINA_V121.md actualizada. Próxima migración V123.
- Siguiente: analizar con usuario Conceptos por empleado (importe y vigencia),
  sin duplicar sueldo base CondicionSalarial ni implementar nómina/pagos todavía.
- Verificación V122:957 pruebas Java y91 JavaScript correctas; PostgreSQL17
  desechable comprobó migración, diez etapas, render, Excel10 filas y permisos.
  Respaldo privado ignorado .respaldos-locales/guia-conceptos-v122-20261021/antes-v122.dump
  validado sin mostrar contenido. Sólo app actualizada21/10 a17:33México;
  V122 success, guía CONFIRMADA/10 y salud UP. Base QA eliminada. No .env,
  operaciones de personal/dinero, commit ni push; plantillas/estilos sin cambios.

## V121 — Catálogo de Conceptos de nómina — 21/10/2026

- Usuario aprueba análisis e implementación sólo del catálogo; reitera diseño común,
  filtros/ayudas, formularios separados ordenados y modales. Se agrega menú Personal
  y nómina → Conceptos de nómina, /admin/conceptos-nomina. Listado/filtros/paginación/
  Excel y Nuevo/Editar aparte; modal compartido con Cancelar y resumen de efecto,
  código/nombre/tipo/aplicación/descripción/estado. Nada de importes ni empleados.
- Tipos PERCEPCION/DEDUCCION/APORTACION_PATRONAL, aplicación HABITUAL/OCASIONAL;
  efecto descriptivo, no cálculo. Costo patronal no resta al empleado. Código único
  normalizado por institución, activo y versión; desactivar sin eliminar. Auditoría
  privada antes/después por trigger en concepto_nomina_historial, inmutable; no DELETE
  ni cambio de identidad/institución. Listado por ID, sin campo de orden capturable.
- Reutiliza paquete salarial V116 y Empleados/Laboral; autorización institucional
  administrativa persistida activa en servicio, no recuperación/docente/familia/plantel.
  Lectura no permite editar/alta. Sin concesiones ni permisos nuevos. Enum/menú,
  acceso HTTP, alcance genérico denegado y redirecciones de compatibilidad actualizados.
- Ayuda del filtro diez al foco/clic, mínimo3, SQL LIMIT11, por institución e incluye
  inactivos identificados; selección exacta por ID/etiqueta canónica. Tipo/aplicación/
  estado/texto se combinan; Excel en bloques100 con mismos filtros. Parámetros
  filtroNaturaleza/filtroAplicacion evitan colisionar con campos del formulario.
  Volver/Cancelar/guardar conservan filtros/página sin URLs externas ni sesión global.
- Prueba humana PENDIENTE en PRUEBA_CONCEPTOS_NOMINA_V121.md; no publicar como
  confirmada en Guía aún. No se crearon conceptos en base operativa por el agente.
  Siguiente: ejecutar ejemplo del catálogo con usuario; después analizar Conceptos
  por empleado (importes/vigencias), sin duplicar CondicionSalarial. Nómina/préstamos/
  impuestos/pagos siguen fuera de esta entrega. Próxima migración V122.
- Verificado:955 Java completas con IT PostgreSQL17 desechable (aislamiento, código
  duplicado, validación/versiones, auditoría inmutable, institución inactiva, permisos,
  CSRF, plantilla/paginación/filtros/Excel y POST sin colisión). Tras ajuste visual,
  5 pruebas dirigidas y package BUILD SUCCESS;91 JS, modal Cancelar/Escape y envío
  único. Revisión visual ficticia escritorio claro y móvil390 oscuro, ayudas10 al foco,
  formulario alineado y modal final; sin estilos propios nuevos para este módulo.
- Respaldo privado ignorado .respaldos-locales/conceptos-nomina-v121-20261021/antes-v121.dump
  validado sin mostrar contenido. App actualizada21/10 a17:06México, salud UP, V121
  success y catálogo/historial operativos vacíos comprobados. Sólo app recreada;
  base QA eliminada, servidor/tab cerrados y tema/viewport restaurados. Sin .env,
  conceptos reales, sueldos, movimientos, commit ni push.

## V120 — Guía de asignaciones laborales confirmadas — 21/10/2026

- Usuario autoriza incorporar el recorrido a Guía de procesos y después revisar
  Conceptos de nómina, sin implementarlos todavía. V120 sólo agrega contenido
  editorial y enlaza desde el cierre de la guía salarial V118; no reescribe migraciones.
- Guía Personal: Puesto, jornada y sueldo: asignar, programar cambios y cancelar el
  futuro; slug asignaciones-laborales-configuracion-historica.12 etapas con cuatro
  acciones cada una: acceso/previos, puesto, jornada, configuración, enero, resumen,
  catálogo sin retroactividad, febrero, filtros/Excel, cancelación, restauración y cierre.
  Códigos/fechas de Bruce, modales Cancelar/Confirmar, módulos/botones explícitos,
  valores esperados, distinción catálogo/asignación y Cancelada con fin vacío.
- CONFIRMADA corresponde al recorrido principal y Excel reportados; no afirma todas
  las pruebas independientes. No repetir operaciones del ensayo. Reutiliza permisos
  Empleados + laboral leer + salarial administrar y GUIA_PROCESOS_CONSULTAR existente;
  no concede roles/permisos ni modifica salarios, cuentas, planteles o datos de personal.
- Siguiente migración V121. Próximo trabajo: revisar diseño de Conceptos de nómina
  con el propietario; no crear catálogo, cálculos fiscales o pagos sin autorización.
- Verificación:951 Java completas (incluye PostgreSQL17 desechable),90 JS correctas;
  tras pulir texto,3 pruebas dirigidas (12 etapas/4 acciones y migración/render/Excel/
  permisos) y package BUILD SUCCESS. Guía12 pasos, Excel12 filas y cuenta limitada
  denegada. Revisión ficticia escritorio claro y móvil390 oscuro, diseño sin cambios.
- Respaldo privado ignorado .respaldos-locales/guia-v120-20261021/antes-v120.dump,
  archivo válido comprobado sin mostrar contenido. App actualizada21/10 a16:40México,
  salud UP; V120 success y guía CONFIRMADA/12 pasos comprobados en base operativa.
  Sólo app recreada; base QA eliminada, servidor/tab cerrados y tema/viewport
  restaurados. No .env/commit/push ni operaciones de personal o dinero.

## Resumen de asignación y recorrido V119 confirmado — 21/10/2026

- Propietario confirmó puntos5–7: cambiar catálogos25h/13000 preservó enero30h/12000;
  febrero4 registró25h/13000 e hizo terminar enero3 el31/01/2027; canceló febrero4
  y enero3 recuperó fin vacío con30h/12000. También confirmó Excel con mismos filtros.
  Recorrido principal cerrado; no repetir operaciones ni cancelar enero. Las pruebas
  independientes no confirmadas (permisos exhaustivos/saldos) no se dan por hechas.
- Pide ordenar pestaña Resumen de Asignaciones: sueldo bruto y jornada destacados,
  base y frecuencia separadas, puesto/configuración histórica y vigencia en tarjetas,
  motivo aparte, estado explicado y enlace de expediente alineado. Cancelada aclara
  que no aplica aunque fin sea vacío. Sin modificar datos, reglas ni otras pestañas.
  Reutiliza salarial-amount y variables/estilos comunes; CSS acotado assignment-summary
  en salarial.css, dos columnas escritorio/una móvil y temas existentes.
- Acta en PRUEBA_ORGANIZACION_LABORAL_V119.md actualizada. Guía integrada de este
  recorrido sigue pendiente de incorporación editorial; no crear migración sin tarea.
  V120 libre. Sin .env, permisos, operaciones reales, commit ni push.
- Verificación de esta mejora:948 Java unitarias/render (sin ejecutar IT PostgreSQL
  esta vez),90 JavaScript correctas; package BUILD SUCCESS. Render cubre Próxima,
  Vigente, Anterior, Cancelada y condición individual sin jornada. Revisión visual
  ficticia escritorio claro y móvil390 oscuro. App actualizada21/10 a16:24México,
  salud UP; sólo app recreada. Servidor/tab QA cerrados y tema/viewport restaurados.

## Nombre laboral y punto4 confirmado — 21/10/2026

- Esquemas de remuneración cambia sólo su nombre visible a Puestos y sueldos
  configurados; botón Nueva configuración, ayuda Configuración de puesto y sueldo,
  detalle/Excel Configuración histórica. Menú/navegación, captura, mensajes, modal,
  resumen de Empleados y filtros mantienen mismo vocabulario y componentes visuales.
  No cambiar enum técnico, /admin/esquemas, campos/JSON, IDs, códigos ESQ, permisos,
  snapshots, reglas salariales ni migraciones. V120 libre; .env no se lee.
- Propietario confirma punto4: Bruce DOC-PRUEBA-02, empleado3; asignación3 inicia
  01/01/2027 sin fin,12000MXN/30h. Condición1 de12000 inicia01/11/2026 y termina
  31/12/2026; condición2 de13000 inicia01/01/2027 y sigue Cancelada. Próximas es
  correcto según fecha actual. No repetir punto4 ni crear registros por él.
- Siguiente: punto5 PRUEBA_ORGANIZACION_LABORAL_V119.md: editar jornada25h y
  configuración13000, verificar enero conserva30h/12000; después programar febrero
  y cancelar sólo última futura. Aceptación completa V119 y guía siguen pendientes.
- Verificado:948 pruebas Java (incluye PostgreSQL aislado) y90 JavaScript sin fallos,
  package BUILD SUCCESS. Revisión visual ficticia escritorio/móvil390 claro con nuevo
  nombre, sin cambiar el diseño. App actualizada21/10 a15:42México, salud UP;
  sólo recreada app. Base QA eliminada y servidor/tab visual cerrados, viewport
  restaurado; sin .env/commit/push ni operaciones sobre asignaciones reales.

## Filtros independientes de catálogos de Personal — 21/10/2026

- Usuario detenido en punto4 de prueba V119 pide ayudas por código/nombre en todos
  los módulos de Personal/nómina. Puestos incorpora Puesto; Jornadas, Jornada;
  Esquemas, Esquema/Puesto/Jornada; Asignaciones combina Empleado/Puesto/Jornada/Esquema.
  Consulta individual anterior de condiciones incorpora Puesto. Empleados conserva
  ayuda de identidad anterior. Texto adicional queda libre y explícitamente opcional.
- Componente compartido filtro-personal usa autocomplete existente, diez al foco/clic,
  mínimo tres caracteres, selección por ID y etiqueta canónica reconstruida en servidor.
  FiltrosCatalogosPersonal usa prefijos para no colisionar con campos de captura.
  Se conservan IDs en paginación, ver/editar, formularios, regreso y exportación de
  los listados de organización. Excel aplica las mismas selecciones en cada bloque.
  Texto sin seleccionar muestra validación; no consulta todos inadvertidamente.
- Endpoint /admin/personal/filtros/{puestos|jornadas|esquemas}/buscar exige permisos
  salariales/laborales institucionales; SQL acotado por institución y LIMIT11 para
  mostrar diez+hayMas. Identifica inactivos para historial; captura sigue activos.
  Búsqueda libre de Asignaciones ahora incluye esquema_codigo y jornada_codigo/nombre
  históricos, no sólo nombre del esquema. No cambia snapshots ni reglas salariales.
- Consulta operativa de sólo lectura confirmó ESQ-DOC-MAT-TEST id1 activo en
  institución1. No se duplicó ni asignó. Para punto4 usar Asignaciones → Nueva
  asignación, no buscar un catálogo no asignado en la tabla de asignaciones.
- Sin migración; V120 sigue libre. Prueba humana V119 pendiente; continuar donde
  indica PRUEBA_ORGANIZACION_LABORAL_V119.md, actualizado con ayudas independientes.
  No hay nómina, movimientos financieros, cambios de permisos ni datos operativos
  realizados por el agente. No leer .env ni hacer commit/push.
- Verificación final:946 pruebas Java (incluye integración PostgreSQL desechable),
  90 JavaScript, cero fallos; package BUILD SUCCESS. Render/SQL/Excel/identidad,
  permisos, mínimo3, límite10+hayMas, inactivos y selecciones cubiertos. Revisión
  visual ficticia escritorio claro y móvil390 oscuro, sin cambiar datos reales.
  App local actualizada21/10 a15:27México, salud UP. Sólo app recreada, sin migración;
  base QA sin puertos/volúmenes eliminada y servidor/tab visual cerrados, viewport
  restaurado. No commit/push ni lectura de .env.

## Búsqueda de empleado con ayuda compartida — 21/10/2026

- Antes de iniciar ejemplo V119, usuario pide empleados10 al foco y buscar desde3
  caracteres. Nueva asignación ya lo tenía; faltaba en filtros. Se añade selector
  independiente Empleado en Empleados y Asignaciones laborales, con componente/tema
  compartidos y texto libre adicional separado (puesto/esquema o código/correo).
  Puestos/Jornadas/Esquemas no reciben filtro de empleado: son catálogos reutilizables.
- /admin/empleados/buscar exige sólo autorización institucional de identidad; devuelve
  código/nombre, sin salarios o laborales. Ayuda /admin/asignaciones/empleados/buscar
  conserva autorización salarial y delega la consulta general. SQL por institución,
  mínimo3 salvo vacío, LIMIT11 para mostrar10 y hayMas; no cargar todos los empleados.
- Filtros por empleadoId exacto, etiqueta visible reconstruida por servidor; nunca
  usar código+nombre concatenados como búsqueda libre. Texto sin selección muestra
  validación y no consulta todos por accidente; Excel rechaza misma captura incompleta.
  Excel de empleados propaga ID en cada bloque, asignaciones ya filtraba por ID.
- Selección persiste en enlaces/página/Excel. Retorno explícito de Empleados usa
  interceptor compartido de cobranza ampliado a ruta local fija /admin/empleados;
  URLs externas/otros módulos/fragmentos rechazados, no sesión/localStorage. POSTs
  conservan contexto mediante script compartido y volver/cancelar usan destino validado.
- Filtros alineados3 columnas escritorio/2 o1 móvil con CSS acotado employee-filters,
  sin paleta nueva ni cambios globales al autocomplete. Ejemplo/README actualizados.
  No dominio/migración/cambios reales/salarios/roles/.env/commit/push. V120 libre.
  Prueba humana V119 sigue pendiente; no repetir prueba V116 ni crear datos por usuario.
- Verificación943 Java completas correctas, incluida IT PostgreSQL17 desechable de
  búsqueda inicial10/hayMas, mínimo3, nombre compuesto/ID, Excel1 fila, alcance y
  lectura general vs salarial.90 JS correctas; tras alineación final16 dirigidas y
  tras retorno URI final4 (IT+retorno) correctas, paquete BUILD SUCCESS. QA ficticia
  desktop oscuro y móvil390 claro, lista10/foco/3 letras/Quitar, campos alineados.
- App local actualizada21/10 a14:05México, salud UP; sólo app recreada, sin migración
  ni cambios a DB/volúmenes/.env/datos/roles. QA DB, visor, pestaña y Dockerfiles
  temporales retirados; viewport restaurado. Sin commit/push; V120 sigue libre.

## V118/V119 — Guía salarial y organización laboral — 21/10/2026

- Propietario autoriza incorporar prueba V116 a Guía y continuar reorganización.
  V118 publica Personal → Puestos y sueldos: registrar, programar aumento y cancelar
  el futuro,8 etapas de4 acciones con módulos/botones/datos/resultados/precauciones.
  Mantiene captura individual compatible en expediente → Condiciones salariales →
  desplegar Condiciones individuales y compatibilidad → Abrir condiciones salariales.
  Actualiza referencia de guía de permisos sin tocar migraciones aplicadas.
- V119 agrega jornadas, esquemas y vínculo inmutable de asignación a condición salarial.
  Menús Administración → Personal y nómina: Empleados/Puestos/Jornadas laborales/
  Esquemas de remuneración/Asignaciones laborales. Tabla/filtros/paginación/Excel por
  bloques y Nuevo/Editar separados; asignación no se sobrescribe, tiene Ver y Gestión
  para nuevo cambio o cancelar última futura. Detalle Resumen/Historial/Gestión con nav.
- Jornada: horas semanales Decimal5,2 positivas hasta168 y descripción de distribución
  de referencia. No agenda por días/descansos estructurados, control de asistencia,
  cálculo de horas pagables ni validación legal. Esquema combina puesto+jornada+bruto/
  moneda/base/frecuencia. Asignar carga datos autoritativos; no recaptura salario.
- CondicionSalarial sigue única fuente de sueldo/vigencia/estado; nuevo snapshot guarda
  esquema/jornada/código/nombre/descripción/horas/versiones y FK única a condición.
  Listado global también muestra condiciones individuales previas sin inventar jornada.
  Catálogos editables no cambian snapshots autorizados. Mismo bloqueo empleado/revisión
  salarial/versión laboral, catálogos bloqueados y versiones verificadas; operación
  atómica revierte cierre/sueldo si falla snapshot. Cancelar reutiliza servicio V116 y
  restaura fin anterior. Auditoría privada inmutable para los3 tipos nuevos.
- Reutiliza permisos V116: paquete Puestos y condiciones salariales, Empleados y
  Datos laborales de empleados; no concesiones automáticas. Rutas/ayudas/Excel/UI
  exigen administrativo persistido institucional activo, sin soporte/docente/familia/
  recuperación. Horas/esquemas privados bajo mismo permiso salarial en esta entrega.
- Expediente resume condición aplicable hoy y enlaza al listado filtrado. Planteles
  siguen en EmpleadoPlantel sin duplicación ni cambios; clases/usuario/foto intactos.
  No nóminas, pagos, conversiones, impuestos, egresos ni nuevas condiciones reales
  creadas por el agente. Próxima migración V120. No reescribir V116/V117.
- Prueba humana V119 PENDIENTE en PRUEBA_ORGANIZACION_LABORAL_V119.md: reutilizar
  Bruce/Puesto, Jornada JOR-MAT-TEST30h, Esquema ESQ-DOC-MAT-TEST12000, asignar01/01/2027;
  editar catálogos25h/13000 sin cambiar enero; asignar01/02/2027, cancelar febrero y
  comprobar restauración. No repetir V116 ni cancelar12000 como paso pendiente.
  Nuevo caso no se publica CONFIRMADA antes de aceptación humana. Guía V118 sí confirmada.
- Verificación final941 Java correctas (incluye IT salarios anterior y nuevo PostgreSQL17
  desechable con migraciones1–119),90 JS correctas. Snapshot tras editar catálogos,
  fallo deliberado/rollback completo, dos confirmaciones/una ganadora, restauración,
  privacidad/lectura/CSRF/Excel/render10 rutas, guía8 pasos y Excel8 filas. QA visual
  ficticia claro/oscuro desktop1280 y móvil390; ayuda foco, carga autoritativa, modal
  Cancelar sin envío, formulario separado y sin overflow horizontal. No operaciones reales.
- Respaldo privado ignorado .respaldos-locales/organizacion-v119-20261021/antes-v118-v119.dump.
  App local actualizada21/10 a11:10México, salud UP; V118/V119 success, guía8 pasos,
  jornadas0/esquemas0/asignaciones0 (no poblar por usuario). Sólo app recreada, DB/
  volúmenes conservados; base desechable, visor, pestaña y Dockerfiles temporales
  retirados, viewport restaurado. Sin .env/commit/push. No restaurar backup por defecto.

## Prueba salarial confirmada y siguiente organización — 21/10/2026

- Propietario confirma recorrido completo V116 correcto. Aporta resultado final:
  DOC-PRI-TEST Docente de primaria,13000MXN mensual/mensual desde01/01/2027 sin fin,
  CANCELADA, motivo aumento programado;12000MXN mensual/mensual desde01/11/2026
  sin fin, PRÓXIMA, motivo condición inicial. Correcta restauración del fin anterior.
  Próxima corresponde a inicio futuro, no falta de activación. No pulsar Cancelar
  programación de12000: ese botón es una acción disponible, no prueba pendiente.
- Cierra aceptación humana del recorrido indicado (registro/modal/consulta/aumento/
  historial/Excel/cancelación futura y comprobaciones finales según confirmación global).
  No convertir esa aceptación en prueba exhaustiva de concurrencia/seguridad o nómina.
  PRUEBA_CONDICIONES_SALARIALES_V116.md y PLAN_PERSONAL_Y_NOMINA actualizados.
  Pendiente incorporar recorrido a Guía de procesos integrada; todavía no publicado.
- Siguiente acordado tras cerrar ejemplo: reorganizar navegación/catálogos visibles:
  Empleados, Puestos, Jornadas laborales, Esquemas de remuneración (puesto+jornada+
  bruto/base/frecuencia) y Asignaciones laborales (empleado+esquema+vigencias).
  Cada menú tabla/filtros/paginación/Excel y Nuevo/Editar separados. Expediente ofrece
  resumen y enlace filtrado, no capturas anidadas duplicadas. Propuesta, NO implementada.
- Reutilizar salarios/historial y adscripciones actuales sin borrar/recrear. Asignación
  conserva valores/versiones autorizados; editar catálogo no cambia sueldos existentes.
  Horas semanales no son distribución horaria ni horas pagables; horario laboral no
  es horario académico. Excepciones individuales con permiso/motivo/confirmación por
  definir, sin aumentos automáticos al cambiar jornada. Revisar modelo antes de ampliar.
- Sólo documentación en esta confirmación: sin código, migración, despliegue, cambios
  de datos/permisos ni .env/commit/push. V118 sigue libre. No repetir prueba salarial.

## Situación laboral visible en Empleados — 21/10/2026

- Durante punto2 de prueba salarial, propietario muestra historial de Bruce ACTIVO,
  ingreso01/10/2026, sin baja. Pide situación/fechas visibles sin interpretar historial.
- Listado agrega columna privada Situación laboral (Activo verde/Baja laboral/Por
  confirmar, ingreso y baja cuando existe). Expediente muestra tarjeta guardada
  encima de pestañas con estado, ingreso/baja dd/MM/yyyy y aclaración: expediente,
  perfil docente y situación laboral son independientes. No usa captura no guardada.
- Resumen sólo con EMPLEADO_LABORAL_LEER/ADMINISTRAR además de autorización de
  empleado/institución/cuenta existente. Listado consulta proyección mínima en un
  bloque por página (IDs e institución), sin cargar historial ni nuevos N+1 laborales.
  Sin permiso no consulta ni incluye resumen en DTO ni columna/tarjeta en HTML.
- Sin cambios de datos, historial, Excel existente, permisos o migraciones; V118
  libre. Prueba salarial sigue pendiente en punto2, continuar después de verificar
  tarjeta/listado, luego crear/reutilizar DOC-PRI-TEST. No repetir baja de María.
- Verificación938 Java correctas, incluye render de3 estados/con/sin permiso en
  ambas vistas, consulta por bloque y DTO privado. QA visual ficticia escritorio
  oscuro/móvil390px claro, sin modificar empleados. App actualizada21/10 a09:55
  México, salud UP; sólo app recreada. Visor/pestaña/Dockerfiles temporales retirados,
  viewport restaurado; sin .env, commit/push ni nuevas pruebas JS (no cambió JS).

## Puestos: listado y formularios separados — 20/10/2026

- Por petición del usuario, /admin/puestos abre únicamente catálogo con menú,
  filtros, tabla paginada, Excel y acciones autorizadas. Nuevo puesto abre /nuevo;
  Editar abre /{id}/editar, ambos en puesto-form.html, sin captura debajo del listado.
- Guardar, Cancelar y Volver conservan búsqueda, estado, página y tamaño. Errores
  mantienen datos en formulario; alta/edición exigen permiso también en servidor.
  Enlaces antiguos con ?id redirigen al formulario separado conservando filtros.
- Regla permanente documentada en AGENTS.md: mismo patrón listado/alta/edición y
  diseño compartido para nuevas implementaciones/correcciones; no refactor masivo.
- Verificación:935 pruebas Java y87 JS correctas; render real de listado, alta,
  edición, errores, filtros y sólo lectura. QA visual con datos ficticios en desktop
  claro y móvil390px oscuro; no se crearon puestos ni condiciones reales.
- README y ejemplo V116 actualizados. Prueba humana salarial sigue pendiente;
  no darla por aceptada. Sin cambios de dominio, migraciones (V118 libre) ni permisos.
- App local actualizada20/10 a17:33México, salud UP. Sólo app recreada, sin tocar
  base/volúmenes/.env ni commit/push. Dockerfiles temporales retirados.

## Puestos laborales en menú principal — 20/10/2026

- Usuario aprueba entrada propia para organizar catálogo. ModuloCatalogo.PUESTOS
  aparece debajo de Empleados en Administración → Personal y nómina; navigation.js
  usa el mismo grupo/desplegable existente, sin duplicar categorías ni catálogos.
  Reutiliza /admin/puestos y su lista/filtros/Excel; enlace desde captura salarial sigue.
- Visibilidad requiere Empleados + Laboral + Salarial, LEER o ADMINISTRAR por cada
  área, igual que el servicio existente. No se asignan permisos ni se modifican
  registros. Sin cambios a montos, vigencias, schema o migraciones (V118 libre).
  Alias /admin/catalogos/puestos y Excel remiten al controlador privado existente.
- Pruebas de enum/permisos completos/incompletos/docente, ruta/sección/orden y render
  Thymeleaf del menú (presente/ausente, un encabezado), redirecciones y Excel filtrado.
  No nueva QA visual: no cambia componentes/estilos, sólo entrada compartida.
- Prueba salarial humana sigue pendiente en PRUEBA_CONDICIONES_SALARIALES_V116.md;
  actualizado acceso directo desde menú en README/ejemplo. No dar sueldos por aceptados.
- 929 Java completas y87 JS correctas; tras alias Excel final35 dirigidas correctas,
  paquete BUILD SUCCESS. App actualizada20/10 a17:20México, salud UP; sólo app recreada,
  sin migración/DB/volúmenes/.env/cuentas/roles/commit/push. Dockerfiles temporales
  retirados. No repetir IT de vigencias para un cambio de navegación sin dominio nuevo.

## V116/V117 — Puestos y condiciones salariales implementados — 20/10/2026

- Propietario autoriza caso base y pide continuar. Implementados puestos institucionales
  y condiciones privadas desde Empleados → Ver expediente → Condiciones salariales →
  Abrir condiciones salariales. Página con Consulta/Registrar/Historial; catálogo
  /admin/puestos desde Registrar → Administrar puestos. Sin nuevo menú académico ni
  cambios de identidad/cuentas/grupos. Puesto textual laboral previo se conserva como
  referencia, no se importa ni sobrescribe; sueldo congela código/nombre del catálogo.
- Permiso separado CONDICION_SALARIAL_LEER/ADMINISTRAR en paquete Roles «Puestos y
  condiciones salariales», sin asignarlo. Requiere además Empleados y lectura laboral,
  administrativo persistido activo/alcance institucional. Rutas/Excel/ayuda/historial
  rechazan sin autorización y otra institución; docente/familia/soporte/recuperación
  no heredan salario. Rol UI paquete completo, no selección sólo lectura.
- Bruto DECIMAL19,2 positivo, moneda institucional no editable, base mensual/semanal/
  diaria/hora y frecuencia semanal/quincenal/mensual descriptivas sin conversión.
  Sólo actividad laboral confirmada/ingreso, expediente e institución activos. Nuevas
  vigencias hoy/futuras y no antes del ingreso; exclusión inclusiva por empleado.
  Otra vigencia cierra anterior al día previo si se cruzan; no intercalar antes de
  programaciones posteriores. Sólo última futura cancelable con motivo/versión y
  restauración atómica del fin original, preservando fila/historial. No borrar vigentes.
- JDBC transaccional con bloqueo JPA de empleado, revisión de historial/versión laboral
  y puesto revalidado antes de cerrar. Trigger conserva antes/después/actor/instante
  privados e inmutables; no vuelca sueldos a bitácoras globales. Filtros/paginación BD,
  ayuda10/foco/3 letras, Excel POI100 por bloque y mismos filtros; money text sin spinner,
  modales detallados/Cancelar/Escape. No nóminas, pagos, impuestos, préstamos o egresos.
- Suite926 Java completa correcta incluyendo IT PostgreSQL17 con migraciones1–116,
  rollback deliberado, dos confirmaciones concurrentes (una ganadora), cierre/restaurar,
  instantánea de puesto, permisos/lectura/IDOR/CSRF/Excel y cero movimientos. Tras
  protección contra puesto renombrado,14 dirigidas correctas y87 JS; test editorial
  V117 y servicio nuevamente correctos. Último cambio sólo CSS de filtros empaquetado
  sin repetir todo Java; QA ficticia claro/oscuro, escritorio1280 y móvil390, ayuda,
  modal/cancelar, tarjeta/importes y Excel legible. Sin operaciones sobre datos reales.
- V117 sólo corrige referencia de guía de permisos (contenido versión2) para no decir
  que aún no existe captura salarial. No agrega guía salarial CONFIRMADA; aceptación
  humana pendiente. Ejemplo completo PRUEBA_CONDICIONES_SALARIALES_V116.md; README,
  PLAN_PERSONAL_Y_NOMINA y modelo actualizados. No repetir bajas/planteles/permisos viejos.
- Respaldos privados ignorados .respaldos-locales/personal-salarios-v116-20261020/
  antes-v116.dump y antes-v117.dump; no mostrar contenido. App local final20/10 a16:56
  México, salud UP, V116/V117 success. Contadores reales puestos0/condiciones0/permisos
  salariales otorgados0. Sólo app recreada, PostgreSQL/volúmenes conservados. QA DB/
  volumen/visor/tab y Dockerfiles temporales retirados, viewport restaurado. Runtime QA
  nexo-salarios-runtime-qa conserva dependencias para futuros ensayos. No .env/commit/push.
- Siguiente humano: activar3 módulos autorizados (Empleados, Laboral, Salariales),
  confirmar rol y nueva sesión; Bruce sólo si actividad laboral/ingreso confirmados,
  no reactivar a María. Puesto DOC-PRI-TEST, bruto mensual12000 desde01/11/2026, pago
  mensual; luego13000 desde01/01/2027, verificar12000 hasta31/12 e historial/Excel,
  sin cambios de banco. Cancelación futura opcional separada. No dar esta prueba por
  aceptada; después incorporarla a guía y definir conceptos/calendarios antes de nómina.
  Próxima migración V118. No automatizar cálculos legales ni asistencia escolar laboral.

## V115 — Guía integrada de permisos laborales — 20/10/2026

- Usuario pide incorporar caso y siguiente ejemplo. Publicada Guía de procesos →
  Personal → Permisos de empleados: proteger datos laborales y verificar Excel,
  slug empleado-permisos-laborales, CONFIRMADA,8 etapas con4 acciones cada una.
  Rol/cuenta capacitación, instituciones/alcance, sesiones separadas, modal/Cancelar,
  sin/con permiso/retiro, URL del expediente y Excel de siete columnas. No se atribuye
  auditoría exhaustiva de endpoints/POST ni sólo lectura seleccionable.
- Requiere permisos existentes GUIA_PROCESOS_CONSULTAR (servicio), EMPLEADO_LEER,
  ROL_ADMINISTRAR y USUARIO_ADMINISTRAR. No requiere laboral para leer instrucciones
  ni otorga permisos a roles. V115 sólo tablas editoriales, actualiza referencia de
  cierre de guía baja (versión2), sin reescribir migraciones anteriores.
- 923 Java/83 JS correctas, incluido render Thymeleaf y Excel de ocho pasos. SQL en
  PostgreSQL17 desechable confirma8 etapas y visibilidad0 sin Usuarios/1 con los tres
  permisos editoriales; autorización general de Guía cubierta por servicio existente.
  Sin nueva QA visual: sólo contenido sobre componentes compartidos y render real.
- Respaldo privado ignorado .respaldos-locales/guia-permisos-v115-20261020/antes-v115.dump.
  App actualizada20/10 a16:23México, salud UP, V115 success y8 pasos confirmados por
  lectura editorial. Sólo app recreada, DB/volúmenes conservados. QA y Dockerfiles
  temporales retirados; sin .env, operaciones de personal/cuentas/roles o commit/push.
- Recorrido funcional de privacidad documentado: no repetirlo ni baja/cambios de
  planteles. Siguiente es DISEÑO de puestos y condiciones salariales, NO otra prueba
  capturable hoy. No hay entidades/formularios de sueldos/nómina. PLAN_PERSONAL_Y_NOMINA
  actualizado con caso FUTURO propuesto: empleado activo autorizado, base mensual
  bruta12000MXN desde01/11, pago mensual, cambio13000 desde01/01/2027, sin egresos.
  No reactivar a María en baja; Bruce sólo si situación laboral confirmada/autorizada.
  Reglas/formularios pendientes de aprobación e implementación separada. V116 libre.

## Privacidad de Empleados — Excel y navegación confirmados — 20/10/2026

- Propietario aporta Excel filtrado de EMP-ADM-PRUEBA-01: una fila, columnas Código
  laboral, Nombre, Correo de contacto, Teléfono, Expediente, Código docente y Perfil
  docente. Sin puesto, situación/fechas/motivo/historial laboral ni adscripciones.
  Expediente Activo y Sin perfil son datos generales, no situación laboral.
- También confirma pegar URL del expediente sin permiso y no ver secciones ocultas.
  Esto acredita navegación/ocultación, no pruebas exhaustivas de endpoints o POST.
  Concesión/retiro y nueva sesión ya aceptados. No repetir bajas ni permisos por él.
- Recorrido funcional manual de privacidad concluido con esos límites. Pendiente
  incorporar este caso a guía integrada; después definir puestos/condiciones salariales
  antes de nómina. V115 libre. Sólo documentación, sin código, despliegue, permisos
  reales, .env ni commit/push. Entradas anteriores de Excel/URL pendientes superadas.

## Permisos laborales — recorrido concesión/retiro confirmado — 20/10/2026

- Propietario confirma también retiro del módulo y nueva sesión: Datos laborales y
  Planteles vuelven a ocultarse. Quedan aceptados los tres estados del recorrido:
  sin permiso ocultos, al conceder visibles con datos previos y al retirar ocultos.
  No repetir concesiones/retiros ni baja. Esta acta supera los pendientes del recorrido
  mencionados en entradas anteriores. No equivale a aceptar Excel o rutas directas.
- Roles concede paquetes completos; sólo lectura no es seleccionable en la UI.
  Antes de cerrar toda la prueba de privacidad falta confirmar Excel filtrado sin
  columnas laborales y acceso directo sin revelación. Después definir puestos y
  condiciones salariales antes de implementar nómina. Incorporación de este recorrido
  a guía integrada aún pendiente; acta actual sólo documental, sin migración/despliegue.

## Aceptación parcial de permisos laborales — 20/10/2026

- Confirmación posterior: propietario también realizó la prueba inversa con permiso
  habilitado y nueva sesión; Datos laborales y Planteles aparecen correctamente con
  sus datos anteriores. No repetir esta concesión ni guardar otra baja por la prueba.
- Siguiente pendiente: retirar módulo laboral del rol de pruebas, confirmar y renovar
  sesión para verificar ocultación nuevamente. No afirmar retiro probado ni Excel/rutas
  directas confirmados. El siguiente humano indicado abajo queda actualizado por esta nota.
- Propietario confirma prueba SIN Datos laborales de empleados: expediente muestra
  sólo Ficha del empleado, Datos personales, Información docente y Acceso (01/02/05/06).
  Datos laborales y Planteles (03/04) ocultos, resultado correcto. También declara
  funcionamiento correcto de la prueba tras incorporación del modal de Roles.
- No extender esa evidencia a exportación sin campos privados, acceso directo,
  habilitar/retirar permiso o modo sólo lectura. La pantalla de Roles concede paquetes
  completos por módulo, no separa lectura/escritura: no pedirle configurar sólo lectura.
- Siguiente humano: marcar Datos laborales de empleados en el rol de pruebas,
  guardar/confirmar, cerrar e iniciar sesión en cuenta de pruebas; verificar pestañas
  privadas con información previa sin guardar movimientos. Después retirar permiso,
  reiniciar sesión y comprobar ocultación. No modificar administrador principal.
- Sólo acta documental, sin código, despliegue, migración ni cambios de usuarios.

## Roles — confirmación antes de enviar cambios — 20/10/2026

- Durante prueba pendiente de permisos privados, usuario pide modal en Roles.
  Crear/guardar y desactivar usan modal compartido money-confirm, sin alert/confirm
  del navegador ni nuevo estilo. Resumen de código/nombre, institución, descripción,
  estado y lista de módulos; desactivar informa conservación de asignaciones/historial.
- Cancelar o Escape cierra sin enviar y mantiene captura. Confirmar vuelve a validar
  y envía el formulario original con CSRF/versión y autorización existente. No cambia
  paquetes de permisos ni crea modalidad sólo lectura en pantalla. Explica efecto
  sobre cuentas asignadas y suma de otros roles. No se modificaron cuentas/roles reales.
- Pruebas JS para alta/edición/desactivación: resumen actualizado, módulos marcados,
  cero seleccionados, Cancelar/Escape sin envío y confirmación única. Verificación
  Java dirigida a controlador/servicio de roles; mismo componente visual existente.
- Prueba humana de permisos laborales sigue pendiente; no documentar como confirmada
  por entregar el ejemplo. V115 continúa libre, sin migración editorial en este cambio.
- Verificación final:83 JS y6 Java de roles correctas, paquete BUILD SUCCESS.
  App actualizada20/10 a14:10México, salud UP; sólo app recreada, sin DB/volúmenes,
  .env, roles reales ni commit/push. Dockerfiles temporales retirados. No nueva QA
  visual en navegador: se reutiliza el modal compartido, pruebas DOM de comportamiento.

## V114 — Baja confirmada y estados verdes de Empleados — 20/10/2026

- Propietario confirma baja, historial, cancelar dentro del modal sin guardar y
  conservación de foto/perfil/acceso/planteles. El texto temporal de13:24 se guardó
  porque pulsó Confirmar y guardar, no por un fallo de Cancelar; conservar historial.
  Ingreso01/10/2026 y baja20/10/2026 comprobados por él. No repetir operaciones.
- Activo en listado de Empleados reutiliza status positivo verde del sistema, tanto
  expediente como perfil docente. Inactivo neutro; nombre/código y contacto separados.
  No cambia filtros, datos ni situación laboral: expediente activo no implica ACTIVO laboral.
- V114 incorpora guía Personal «Baja laboral: confirmación e historial sin borrar el
  expediente»,8 etapas/4 acciones cada una, con permisos existentes. Actualiza referencias
  de guías anteriores (versión de contenido2). Sólo contenido editorial, sin otorgar permisos.
  No declarar probados por el humano todos los negativos ni baja con cuenta docente activa.
- 920 Java y79 JS correctas; tras últimos ajustes editoriales, paquete/pruebas dirigidas
  de render y contenido correctos. SQL probado en PostgreSQL17 aislado. QA visual ficticia
  escritorio claro/oscuro: verde compartido, inactivos neutros y textos separados.
- Respaldo privado ignorado .respaldos-locales/guia-baja-v114-20261020/antes-v114.dump.
  App actualizada20/10 a13:37México; salud UP, V114 exitosa, guías Personal7/8/8 etapas.
  Sólo app recreada, sin operaciones reales de personal, .env, roles, commit/push.
- Siguiente: probar permisos de información laboral privada (consulta general sin
  datos privados; lectura laboral sin edición). Después puestos/condiciones salariales
  y nómina. Próxima migración V115. Notas previas de baja pendiente quedan superadas.

## Fechas laborales visibles al recargar — 20/10/2026

- Usuario detenido en punto1 de prueba de baja: fecha de ingreso parece no guardarse.
  Lectura acotada de EMP-ADM-PRUEBA-01 confirma ACTIVO, ingreso actual20/10/2026,
  historial anterior01/10. No se tocaron registros ni se hizo baja por el propietario.
- Diagnóstico con MVC/Thymeleaf real y datos ficticios: LaboralForm sin formato
  explícito renderizaba date value01/10/26(es-MX) o10/1/26(en-US), inválidos para HTML
  date. POST ISO sí enlaza correctamente; problema confirmado de visualización, no
  pérdida del registro. DateTimeFormat ISO.DATE en fechaIngreso y fechaBaja corrige
  render/binding sin cambiar LocalDate, servicio, validaciones o esquema.
- 917 Java/79 JS correctas. Dos pruebas nuevas GET/POST en es-MX/en-US, ingreso/baja
  ISO y baja vacía; calendario recibe valor válido y no invierte día/mes. Sin cambios
  de diseño/JS ni nueva QA visual; prueba usa plantilla real. Diagnóstico temporal en
  target/diagnostico, no aplicación ni base real. PRUEBA_EMPLEADOS_V111 actualizado.
- App local actualizada20/10 a13:13México, sólo app recreada; PostgreSQL/volúmenes
  conservados, sin migración (V113/V114 libre), .env, datos reales, roles, commit/push.
  Dockerfiles temporales retirados; salud en CONTEXTO. No ejecutar fechas por él.
- Siguiente humano: volver a Datos laborales, revisar fecha guardada; para ejemplo
  capturar01/10/2026 y Guardar información laboral/Confirmar y guardar, recargar y
  confirmar que permanece antes de continuar baja. No reprogramar planteles21/10
  ni declarar prueba de baja aceptada. Guías V113 mantienen casos anteriores cerrados.

## V113 — Casos de Empleados y Planteles en Guía de procesos — 20/10/2026

- Propietario confirma que el aviso quedó bien y pide documentar. Se cierra el
  recorrido principal probado de empleado no docente y planteles/cambio programado.
  No volver a crear EMP-ADM-PRUEBA-01 ni repetir/cancelar su cambio21/10: ya guardado.
  No anticipar aceptación humana del paso de día, baja, continuidad desmarcada,
  consulta privada entre cuentas ni todas las variantes/concurrencias por tests.
- V113 añade2 guías CONFIRMADA, categoría Personal: Registrar un empleado
  administrativo sin perfil docente (7 etapas) y Varios planteles y cambio programado
  de principal (8). Slugs empleado-administrativo-sin-perfil-docente y
  empleado-planteles-cambio-principal. Módulos/botones/ejemplos/resultados/precauciones,
  fechas inclusivas, mensaje ya principal y aviso actual/futuro; Excel filtrado.
  Sólo tablas de guía, sin operaciones de personal ni permisos a roles.
- Visibilidad con GUIA_PROCESOS_CONSULTAR y EMPLEADO_LEER + LABORAL_ADMINISTRAR;
  alta requiere además EMPLEADO_ADMINISTRAR. Reutiliza autorizaciones existentes.
  Filtro de categoría antes sólo Cobranza; ahora DISTINCT de guías autorizadas,
  acotado100 y mismo filtro de permisos, sin exponer módulos ajenos. UI sin nuevo diseño.
- 915 Java/79 JS correctas, incluyendo render de2 guías/15 pasos y Excel15 filas,
  filtro Personal y categorías restringidas. SQL validado PostgreSQL17 temporal:
  7+8 pasos, sin permiso laboral0 visibles/con permisos2. Sin nueva QA visual por
  contenido/filtro existentes; render verifica selección. Datos y escenarios no ensayados
  quedan diferenciados. Actualizadas README y ambas pruebas de Personal.
- V113 aplicada localmente20/10 a12:44México, salud UP,2 guías/15 pasos confirmados
  por lectura limitada. Sólo app recreada; DB/volúmenes reales intactos. Respaldo privado
  ignorado .respaldos-locales/guia-personal-v113-20261020; no mostrar contenido.
  QA contenedor/volumen y Dockerfiles temporales retirados. Sin .env/commit/push.
- Guía integrada de estos casos ya NO pendiente; notas anteriores superadas. Siguiente
  prueba acordable: baja laboral con fecha/motivo/historial, separada de disponibilidad
  de cuentas/perfil y adscripciones; después permisos privados. No ejecutarla por el
  propietario. Puestos/condiciones salariales y nómina se definen después de esta base.
  Próxima migración disponible V114.

## Aviso de principal actual y programado — 20/10/2026

- Propietario intenta elegir A para21/10 y obtiene ya es principal. Consulta limitada
  de sólo lectura de EMP-ADM-PRUEBA-01 confirma6 filas: originales hasta19/10,
  B principal/A adicional20/10 y Parian(A) principal/B adicional desde21/10. El cambio
  YA está guardado; no repetirlo, cancelarlo ni pedir que lo vuelva a confirmar.
- Usuario pide aclarar por qué las tarjetas muestran B hoy20/10. Aviso nuevo antes
  de las tarjetas: principal actual con fecha de hoy y próximo con fecha efectiva;
  aclara que aún no está vigente y no necesita confirmar otra vez. Sin futuro muestra
  Principal vigente hoy, no un cambio ficticio. Si no hay asignaciones no aparece aviso.
- ResumenPrincipal consulta sólo1 actual/1 próximo, con autorización laboral y fecha
  institucional, independiente de página consultada. Presentación con tokens actuales,
  claro/oscuro/móvil. Sin cambios a vigencias, motor, permisos, auditoría ni esquema.
- 910 Java/79 JS correctas; nuevas pruebas de consultas limitadas/permiso/vacío y render
  con/sin futuro. QA ficticia escritorio oscuro/móvil390 claro y oscuro, sin mutaciones.
  No se repite IT PostgreSQL de cambio (ya verificada en entrega previa); sólo lectura/UI.
- App local actualizada20/10 a12:35México; sólo app recreada, sin migración (V112),
  .env, registros reales, roles, commit/push. DB/volúmenes intactos. Servidor/tab QA
  cerrados, viewport restaurado y Dockerfiles temporales retirados. Salud en CONTEXTO.
- Siguiente: propietario revisar aviso, comprobar Auditoría/identidad sin otra
  operación. Documentar casos de personal en guía integrada (aún pendiente); después
  prueba de baja laboral y permisos privados antes de puestos/salarios/nómina.

## Planteles ordenados y cambio atómico de principal — 20/10/2026

- Usuario confirma cuatro asignaciones del ejemplo manual A/B hasta19/10 y B/A
  desde20/10; pide organización y asistente. No repetir las cuatro operaciones ni
  modificar sus registros. Aceptación del nuevo asistente todavía pendiente.
- Planteles separa actuales (principal primero), próximas e históricas, cerrando las
  dos últimas inicialmente. Estado por fecha institucional, límites inclusivos;
  tres consultas paginadas de20, sin cargar todas las vigencias. Navegar páginas abre
  la sección correspondiente incluso al volver a página0. Tarjetas y acción Finalizar
  independientes; grupos/roles y las demás pestañas no cambian.
- Cambiar plantel principal permite plantel activo institucional (ayuda diez/3letras),
  fecha hoy/futura posterior al inicio de las etapas afectadas, conservar anterior
  como adicional y motivo obligatorio. Vista previa GET sin escritura, modal resumen,
  Cancelar/Escape/fondo sin POST ni perder campos; confirmar usa CSRF y versiones.
  JSON obsoleto se ignora, cambios de captura invalidan resumen y hay guarda dobleclic.
- CambioPrincipalEmpleadoService autoriza EMPLEADO_LEER + LABORAL_ADMINISTRAR con
  cuenta administrativa institucional real, bloquea empleado, revalida versiones y
  cruces/programaciones. Cierra filas antes de insertar, flush en transacción única;
  fallo revierte todo. Mantiene fechas fin previstas y tipos históricos. Auditoría
  EMPLEADO_PRINCIPAL_CAMBIADO con actor/motivo/fecha efectiva/planteles; enum varchar
  existente, sin SQL ni migración nueva. V112 vigente/V113 libre.
- 908 Java incluyendo integración PostgreSQL temporal y79 JS aprobadas. IT con
  aplicación/filtros reales, migraciones, GET resumen/detalle, fallo inducido tras cierre
  (rollback total), dos confirmaciones concurrentes (una sola aplica/audita), páginas
  históricas20+3 y exclusión DB. QA ficticia claro escritorio/móvil390 claro/oscuro,
  resumen y cancelación; corregidos aliases scoped de colores para evitar modal
  transparente. Nunca se confirmaron operaciones reales del propietario.
- App local actualizada20/10 a12:24México, salud UP, sólo app recreada; DB/volúmenes
  reales intactos, sin .env/roles/commit/push. Contenedores/volúmenes temporales QA,
  servidor/tab y Dockerfiles temporales retirados; viewport restaurado.
- Procedimiento nuevo en PRUEBA_PLANTELES_EMPLEADOS.md y README. Siguiente humano:
  empleado existente → Planteles → Cambiar plantel principal → desde21/10 si hoy20/10,
  elegir A, conservar B adicional, motivo; visualizar/Cancelar y luego confirmar si
  correcto. Futuras quedan en Próximas hasta fecha efectiva. No repetir cambio manual.
  Incorporar casos no docente/multiplantel a guía integrada tras aceptación del asistente;
  todavía pendiente. No cerrar nómina ni otros escenarios de personal por estos tests.

## Empleado no docente confirmado y tarjeta de plantel corregida — 20/10/2026

- Propietario confirma todos los pasos del ejemplo administrativo no docente con un
  plantel; única observación es diseño de tarjeta de adscripción. Acta en
  PRUEBA_EMPLEADOS_V111.md. No repetir alta/foto/laboral ni habilitar perfil docente.
  No equivale a aceptación de multiplantel, bajas, solapamientos o permisos privados.
- Planteles asignados ahora usa tarjetas employee-campus-* de forms.css: nombre,
  Principal/Adicional, inicio/fin separados, acción Finalizar vigencia en zona aparte.
  Tokens actuales para claro/oscuro, rejilla adaptable móvil, nombres largos, foco
  visible y calendario legible en ambos temas. Endpoint/POST/versión/modal/permisos
  intactos; acción sólo para asignación sin fin y usuario con edición laboral.
- Pruebas899 Java/75 JS aprobadas; render con principal/adicional/finalizado, fechas,
  sólo lectura y fixture ficticia. QA visual escritorio claro/oscuro y móvil390oscuro
  correcta; falta aceptación visual del propietario. App local actualizada20/10 a11:55
  México, salud UP, sin migración (V112 vigente/V113 disponible). PostgreSQL/volúmenes
  intactos, sin .env, registros reales, roles, commit/push. Servidor/tab QA cerrados.
- Siguiente: aceptar diseño y documentar caso no docente en Guía de procesos integrada
  (todavía NO añadido; V112 es planeaciones); después ejemplo de multiplantel y
  validaciones. No asumir finalizados personal completo ni nómina.

## V112 — Planeaciones incorporadas a la Guía de procesos — 19/10/2026

- Usuario pide agregar el caso a la guía integrada para cerrar la prueba. V112 añade
  sólo contenido editorial: Planeaciones docentes: captura, revisión y versiones PDF,
  categoría Gestión académica, slug planeaciones-revision-versiones-pdf, CONFIRMADA.
  Diez etapas detalladas: preparación, captura/borrador, envío, revisión/ajustes,
  corrección/reenvío, publicación1, reapertura con motivo, nueva publicación y PDF1/2.
- Consulta /admin/guias/planeaciones-revision-versiones-pdf con permisos existentes
  GUIA_PROCESOS_CONSULTAR + PLANEACION_LEER. No otorga roles ni acceso docente al admin.
  Reutiliza UI responsive/temas y Excel filtrado del módulo. Sin operaciones docentes,
  publicaciones, datos laborales/financieros ni aceptación de otros escenarios.
- V112 aplicada localmente19/10 a17:30México; salud UP y10 pasos confirmados en base.
  Sólo app recreada; PostgreSQL/volúmenes conservados. Respaldo privado ignorado en
  .respaldos-locales/guia-v112-20261019; no mostrar contenido. Sin .env/commit/push.
  Pruebas897 Java/75 JS completas correctas, luego9 dirigidas correctas con render y
  Excel de los diez pasos reales. SQL definitivo probado en PostgreSQL17 aislado.
- README, GUIA_PLANEACIONES_DOCENTES y PRUEBA_EMPLEADOS_V111 actualizados: integración
  ya NO pendiente. Notas anteriores son historial superado. Prueba principal cerrada;
  no pedir repetirla ni reabrir/publicar otra vez. Descarte/concurrencia/aislamiento,
  Empleados no docente/laboral/multiplantel, agenda/familias/sesiones y Compras siguen
  pendientes independientes. Próxima migración disponible V113.

## Cierre humano del flujo principal de planeaciones — 19/10/2026

- Usuario confirma «sí todo coincide» al comparar Versiones1/2 y comprobar Publicada,
  consulta PDF y ausencia de edición docente. Aceptado el recorrido principal completo:
  captura→envío→revisión→ajustes/corrección/reenvío→publicación1→reapertura con motivo→
  edición/reenvío/revisión→publicación2, con conservación del contenido de versión1.
  NO volver a pedirle esta comparación ni repetir publicaciones/reaperturas/altas.
- GUIA_PLANEACIONES_DOCENTES.md contiene procedimiento detallado, módulos/pestañas/
  botones/modales/ejemplo/resultados/FAQ y límites. PRUEBA_EMPLEADOS_V111.md tiene acta
  VALIDADO y pendientes históricos relacionados corregidos. Documentado en README y
  CONTEXTO. Esta aceptación no cierra todo el sistema ni todo V111.
- Sólo documentación este turno: no código, SQL, migraciones, builds, despliegues,
  sesiones, datos reales, .env, commit o push. NO afirmar que ya se añadió el caso al
  catálogo de Guía de procesos dentro de la aplicación: la guía en repositorio está
  lista; esa incorporación sigue como pendiente documental explícito.
- Siguiente acordable: incorporar este caso validado al catálogo integrado o escoger
  escenario independiente pendiente (p.ej. descarte). No asumir autorización para
  descartar esta planeación ni ampliar roles. Empleados no docente, laboral/multiplantel,
  agenda, casos familiares, sesiones5m aplazadas y Compras documental siguen pendientes.

## Detalle administrativo por pestañas y versiones PDF identificables — 19/10/2026

- Usuario ve2 publicaciones tras reabrir/corregir/publicar; se explicó que son copias
  históricas, no duplicados. Pidió rótulos Ver PDF · Versión1 — Publicación inicial /
  Versión2 — Nueva publicación, fecha/hora discreta, y evitar scroll al final del historial.
  Pendiente comparar ambos PDF con usuario; no asumir que confirmó su contenido.
- Detalle admin ahora reutiliza student-tabs/forms: Planeación, Revisión, Versiones PDF,
  Historial. Barra fija al desplazar contenido, cuatro opciones visibles (dos filas
  móvil), paneles separados. Revisión inicial; publicar exitoso fuerza Versiones PDF,
  error fuerza Revisión. PDF actual sigue arriba y archivos abren otra pestaña inline.
- En Versiones PDF tarjetas/enlaces con rótulo explícito, fecha debajo dd/MM/yyyy · HH:mm,
  vacíos explicados. Nueva VersionPlaneacionFila sólo presentación, convierte Instant
  a zona de la institución sin alterar timestamps/JSON/orden/revisiones guardados.
  Métodos/permiso de archivo histórico intactos; no migración ni nuevas publicaciones.
- student-tabs incorpora scroll opcional sólo para nav data-scroll-to-tabs, respeta
  movimiento reducido; otros módulos sin ese atributo conservan comportamiento.
  Soporta hash/teclado, retorno a pestaña y no reconstruye formularios/motivos.
- Estado de pruebas/publicación en CONTEXTO. Siguiente humano: abrir mismo detalle →
  Versiones PDF → Ver PDF Versión1 y2, comprobar anterior/nuevos materiales, fecha
  local y nueva navegación/modales. No reabrir otra vez sólo para comprobar los PDF.

## Modales de acciones de planeación en ambos portales — 19/10/2026

- Propietario confirmó Publicada/PDF histórico/bloqueo docente tras corrección del
  serializador. Dice haber pulsado Reabrir; falta confirmar motivo visible, edición
  y nueva publicación/revisión2. No repetir reapertura ni crear otro registro por él.
- Solicita confirmaciones administrativas y docentes. Admin: Iniciar revisión,
  Publicar, Solicitar ajustes, Reabrir. Docente: Guardar (nuevo y existente) y Descartar;
  Enviar conserva su modal ya validado. Cada acción tiene texto/resumen/confirmar/
  cancelar, fechas dd/MM/yyyy y motivo capturado cuando aplica. No añade permisos.
- Fragmento/CSS/JS nuevos planeacion-confirmacion, dialog nativo y paletas actuales.
  Cancelar/Escape/fondo cierran sin POST y restauran foco; confirmar requestSubmit
  conserva endpoint/CSRF/versión/submitter, evita doble envío y revalida. Guardar sólo
  abre tras validación completa, confirma captura actual sin publicar/enviar.
  Vistas soporte sin nuevos botones/atributos/JS ni modal de mutación.
- planeacion-vista coordina guardas: abrir/cancelar modal no marca salida aceptada;
  captura pendiente sigue avisando al salir y bloquea envío/descarte. Ayudas de materia
  excluidas del resumen (se toma texto, no icono i). Todo sin cambios de servidor/
  esquema/datos reales. Ver CONTEXTO para verificación y publicación.
- Siguiente: misma planeación Reabierta → docente editar/guardar y confirmar→enviar;
  admin iniciar revisión/confirmar, Publicar/Cancelar primero y después confirmar.
  Esperar Revisión2 y Revisión1 intacta. Descartar NO parte de esta prueba.

## Corrección de publicación de planeaciones — 19/10/2026

- Prueba humana de Publicar falló con500 a22:19:44UTC (16:19México). Log confirma
  InvalidDefinitionException de Jackson2: LocalDate de PlaneacionDocumento.fechaInicio
  sin módulo jsr310 al serializar versión histórica. No es error de captura ni PDF.
- PlaneacionService usaba com.fasterxml ObjectMapper.findAndRegisterModules sin
  módulo de fechas presente; Boot4 trae Jackson3 y Jackson2 queda por Jasper.
  Cambiado mapper privado a tools.jackson JsonMapper con fechas ISO explícitas;
  soporte nativo LocalDate/Instant y lectura del mismo DTO/snapshot. Sin dependencias
  nuevas, esquemas o cambios de proceso. No modificar Jasper ni configuración JSONglobal.
- Regresiones nuevas publican/leen snapshot con fechas de semana/actividad/Instant,
  generan PDF desde snapshot, conservan revisión1 al reabrir/publicar2, rechazan
  estado/versión inválida y leen fechas numéricas legadas. Render administrativo
  prueba redirección/GET con versión publicada y enlace PDF/reapertura.
- Sin publicar ni reparar manualmente datos del propietario. Ver CONTEXTO para
  cifras/estado operativo. Pendiente reabrir mismo registro y reintentar Publicar;
  no crear otra planeación ni marcar publicación/reapertura humana como aceptada.

## Ajustes corregidos y reenviados: aceptación humana — 19/10/2026

- Propietario confirma que vio la tarjeta, corrigió, guardó y reenvió; ya no permite
  editar. Aceptado ese recorrido, no publicación/reapertura todavía. Siguiente prueba:
  administración → Planeaciones → misma semana/grupo → Revisar → Iniciar revisión,
  comprobar cambios y Publicar planeación; después comprobar bloqueo/PDF docente.
- Pide simplificar tarjeta: quitar Ir a captura porque el menú ya está debajo.
  Retirado botón/listener, tarjeta conserva motivo/fecha/ayuda que refiere al menú.
  La prueba de aceptación se actualiza para usar Captura → Recursos y evaluación.
  Las entradas históricas que citan CTA describen la versión anterior, ya superada.

## Correcciones administrativas visibles al docente — 19/10/2026

- Aceptación humana: modal Cancelar/Confirmar envío funciona; docente envió,
  administrador inició revisión y solicitó ajustes. Maestro ve Requiere ajustes,
  pero no veía el motivo. Confirmado faltante: guardado en historial admin sin
  exposición docente. No confundir con observaciones generales del formulario.
- Tarjeta ámbar destacada sobre las pestañas, en detalle/edición/POST con errores:
  Correcciones solicitadas por administración, fecha institucional dd/MM/yyyy HH:mm,
  texto íntegro multilínea escapado y botón Ir a captura. Paleta clara/oscura/móvil,
  no rojo de errores; permanece al guardar hasta reenviar. Al reenviar deja de
  marcar solicitud pendiente; historial administrativo original se conserva.
- Nueva lectura observacionRevision verifica maestro activo, dueño e institución
  ANTES de consultar historial. Sólo REQUIERE_AJUSTES/REABIERTA: último evento del
  estado actual (findFirst orden fecha/id), no todo el historial ni comentarios
  antiguos como pendientes. Reapertura usa título propio; sin motivo no inventa.
  Soporte autorizado ve misma tarjeta sin botón/acciones de escritura.
- No cambios de esquema, estados, permisos, PDF ni operaciones reales para QA.
  Estado de verificación/publicación en CONTEXTO. Pendiente humano: ver tarjeta,
  corregir recursos/evaluación, guardar y reenviar misma planeación; luego publicar.

## Confirmación antes de enviar planeación — 19/10/2026

- Propietario confirmó que ya ve el PDF en la navegación unificada. Pidió agregar
  modal a Enviar a revisión antes de realizar la prueba de envío; no enviarla por él.
- En Resumen y envío, submit abre dialog nativo estilizado con tema del portal,
  maestro/grupo/plantel/semana dd/MM/yyyy/materias y aviso de bloqueo de edición.
  Confirmar envío realiza requestSubmit al mismo endpoint/CSRF/versión; Cancelar,
  Escape o fondo cierran sin POST y restauran foco. Confirmar evita doble clic y
  revalida cambios pendientes; no aparece para soporte ni estados no editables.
- No se modificó servidor/dominio ni se guardaron/enviaron planeaciones reales de QA.
  Estado de verificación/publicación en CONTEXTO. Pendiente humano: probar Cancelar,
  luego Confirmar envío con la misma planeación y revisión desde administración.

## Planeación en una sola vista — 19/10/2026

- Propietario reportó guardar su planeación y confundirse con «¿Necesitas descartarla?»;
  se explicó que no era validación sino acción opcional. Solicita evitar volver del
  formulario al detalle para abrir PDF. No hay todavía aceptación de envío/revisión.
- Detalle editable y /editar ahora comparten formulario y navegación principal:
  Captura (sus cinco secciones), Resumen y envío, Documento PDF y Gestión. Guardar
  redirige a la misma vista en Resumen; /editar abre Captura. Volver va al listado.
  Sin formulario nuevo dentro de otro: guardar, enviar y descartar son forms hermanos.
- PDF tiene su pestaña y enlace al endpoint privado inline existente, abre otra pestaña
  sin descarga automática ni perder captura. Aclara que PDF/resumen usan lo guardado,
  no texto pendiente. No se creó una copia pública ni se cambió el generador de PDF.
- JS de vista conserva DOM/texto al navegar, teclado/aria y tema móvil. Detecta cambios
  pendientes, avisa y bloquea envío/descarte hasta guardar; también si el servidor
  rechazó un POST (capturaPendiente). Aviso de salida del navegador para no perder texto.
  Descartar sólo en Gestión con motivo, nunca como paso obligatorio de guardar.
- Enviada/En revisión/Publicada/Descartada conservan consulta sin editor/acciones de
  borrador; /editar redirige al detalle. Soporte mantiene sólo lectura y su PDF privado,
  sin botones de escritura ni JS de mutación. Foto/reglas de servicio/CSRF/versiones
  permanecen. Sin migración ni datos guardados de QA. Estado operativo en CONTEXTO.
- Pendiente humano: recargar, abrir misma planeación, Captura→PDF→Captura conserva
  texto; guardar y enviar desde Resumen y envío. Después prueba de revisión/ajustes.

## Captura de planeaciones por pestañas y validación visible — 19/10/2026

- Propietario confirmó que bruce.wayne SÍ entra por /maestros/acceso; estaba usando
  /familias. Acceso humano docente confirmado, no faltaban roles ni contraseña. Las
  entradas previas de rechazo pendiente son historial superado para ESTE login.
  Sigue pendiente prueba humana de guardar/enviar/revisar/publicar planeación.
- Nueva solicitud implementada: cinco pestañas de captura (grupo/semana, objetivos,
  alineación, actividades, recursos/evaluación) con teclado, Anterior/Siguiente y
  guardar visible. Paleta/controles portal, responsive, sin tocar diseño administrativo.
  CSS nuevo planeacion-form.css y UI propia planeacion-form-ui.js; no usa estilos admin.
- Obligatorios marcados*, errores rojos locales incluidos TODOS los campos anidados,
  resumen enlazado/cuenta por pestaña y apertura del primer error. Corrige mensajes
  Bean Validation en español y pinta borde/aria-invalid/describedby; cliente evita
  enviar incompletos/espacios/fecha fuera de semana/duración inválida, servidor conserva
  @Valid y datos. No relajar borrador: requiere propósito/materia/actividad completa.
  Alineación opcional, pero cada tarjeta agregada requiere sus3 campos; quitar vacía.
- Filas dinámicas ahora reindexan names/id al quitar/agregar, conservan catálogos
  iniciales y limitan selects a materias marcadas. Consultas con aborto/secuencia
  evitan respuestas viejas; no cambia permisos/elegibilidad del servicio. Las reglas
  de vigencia, semana única, edición y publicación permanecen en backend.
- Regresar ahora botón con borde/foco/tema. Foto en nueva/editar/POSTconerror/detalle:
  faltaba fotoPerfil en preparar/detalle; se usa la misma fotografía privada docente,
  no otro archivo ni URL pública. Soporte conserva captura deshabilitada y sin envío;
  pestañas sí navegan en sólo lectura. Sin migraciones nuevas ni nómina.
- Regla de UX: todo formulario con listas anidadas debe mostrar validación por campo
  también en filas ya renderizadas, no sólo en plantillas JS; pestañas deben llevar
  al error y conservar captura. Ayudas no son errores; rojo sólo requeridos/validación.
- Verificado879 Java/62 Node y QA ficticia escritorio/móvil390px/claro/oscuro: guardar
  incompleto, enlaces de error, corrección8→7, foto/cabecera/regresar, añadir/quitar/
  reindexar actividad, seleccionar materia y captura completa que supera cliente.
  POST de fixture bloqueado405 deliberadamente; no es error de app real. Pruebas MVC
  invalidan sin llamar crear; válida llama crear una vez y redirige a detalle. Lectura
  soporte pasó. Estado operativo final en CONTEXTO; no crear datos reales para probar.

## Rechazo docente persiste: falta identificar petición real — 19/10/2026

- Propietario sigue viendo mensaje contextual docente en acceso-denegado. No asumir
  que los arreglos de navegación resolvieron su login. Se preguntó si al pulsar Ir al
  portal de maestros vuelve al rechazo o abre; respuesta aún pendiente en este punto.
- PortalDocenteCompletoIT ampliado con Java HttpClient contra Tomcat real/puerto
  aleatorio, CookieManager, CSRF+comprobar+POSTlogin con origen maestros y redirects
  reales: termina en /maestros200 con Mis planeaciones e identidad ficticia. Además
  sigue pasando MockMvc. Prueba aislada1/1 sin fallos, DB/usuario/clave ficticios;
  sin claves/sesiones reales, ni mutaciones de usuarios/roles/datos de instalación.
- Sólo test/documentación cambiados en este seguimiento. Sin publicación/reinicio ni
  cambio de código productivo para no seguir suponiendo causas. QA DB/red retiradas.
  Obtener resultado del botón y URL siguiente antes de afirmar causa o nueva solución.

## Diagnóstico ampliado del acceso docente — 19/10/2026

- Propietario sigue reportando acceso denegado para cuenta docente de la prueba y
  aporta URL /acceso-denegado. No ha confirmado desde qué formulario entró ni ventana
  privada; NO dar por identificado/resuelto ese recorrido humano exacto.
- Sólo lectura: tipo docente/activo, vínculo/perfil/institución correctos,1 asignación,
  acceso reciente e intentos0; sin colisión de nombre con recuperación (sólo booleano,
  nunca imprimir configuración), no se leyó contraseña/hash ni se cambió la cuenta.
- PortalDocenteCompletoIT ejecuta Spring Boot/Flyway/JPA/servicios/filtros/Thymeleaf y
  BCrypt reales sobre PostgreSQL desechable con usuario/empleado/maestro ficticios9100.
  POSTlogin→/maestros y GETmaestros200 con ficha/nombre propios; recursos admin403.
  Primer ensayo pasó SIN tocar permisos/código de autenticación. No basta probar302.
- Encontrado y corregido bucle de navegación verificable: /admin no tenía regreso
  docente si no había módulos y acceso-denegado enlazaba siempre /admin. Ahora entrada
  genérica retorna al portal autorizado, error (forward/controlador o excepción) da
  enlace contextual y ayuda sin pedir roles administrativos. Inicio público incluye
  Entrar como maestro. DestinoPortalSesion sólo elige rutas internas, no da permisos.
  Las restricciones a módulos/datos y origen explícito incompatible siguen intactas.
- IT ampliado y pruebas dirigidas14 pasan; estado final en CONTEXTO. No reproducir el
  acceso usando credenciales/sesiones reales del propietario ni cambiar claves/roles.
  Pedir comprobación en /maestros/acceso después de publicar; si persiste, capturar
  pantalla de entrada (sin contraseña) y recorrido/URL anteriores para ubicar el salto.

## Asignación docente y entrada después de activar cuenta — 19/10/2026

- Propietario probó DOC-PRUEBA-02 (empleado/perfil creado), no se ha aceptado aún el
  flujo docente completo. Reportó500 al asignar materia y acceso denegado tras activar.
  Log18:15UTC confirma PostgreSQL42883 date<=text en coalesce(:fechaFin,:fechaInicio).
  Reemplazado por consulta nativa con cast DATE y fin nulo como rango abierto; evita
  también omitir cruces con asignaciones futuras. No requiere migración ni corregir
  registros: el fallo ocurrió antes del INSERT.
- Lectura limitada confirmó perfil activo/cuenta PORTAL_MAESTRO ACTIVO/credencial
  configurada, cero asignaciones; no se consultó hash/password ni se cambiaron permisos.
  Activación llevaba a /login y login general enviaba siempre a /admin. Ahora la
  autoridad exclusiva de docente/familia dirige a su portal aunque use login general;
  entrada explícita incompatible sigue denegada y bootstrap/multiportal conserva admin.
  Mensaje de activación ya no dice que el docente requiera roles administrativos.
- PostgreSQL aislado (temporal, sin puertos, datos ficticios): AsignacionPostgresIT
  ejecuta la consulta extraída del repositorio y parámetros DATE/nulos reales; prueba
  vigencia abierta, futuro, límites inclusivos, adyacencia, otro maestro, inactivo e
  ID excluido. Login HTTP prueba docente/familia desde general y administración403,
  sin permisos extra (FACTOR_PASSWORD es autoridad técnica del framework).
- Estado final de pruebas/publicación en CONTEXTO. No invitar de nuevo, cambiar clave
  o dar roles a la cuenta de prueba para resolver esto. Pendiente propietario reintentar
  asignar y entrar en /maestros/acceso, preferir ventana privada si admin sigue abierto.

## Corrección visual de Empleados — 19/10/2026

- Foto: selector nativo accesible estilizado con botón/borde/tema y ayudas separadas;
  código docente dentro de form-grid compartido (antes fuera, no heredaba diseño).
  Pestañas del expediente en rejilla3 columnas/escritorio,2/móvil, títulos completos
  sin elipsis. CSS acotado a employee-card/panel; no cambia otras pestañas ni dominio.
-16 pruebas Java relacionadas pasan, render de empleado sin perfil agregado para
  cubrir campo de código; inspección visual ficticia móvil390px/claro/oscuro.
  Cambio exclusivamente de plantilla/CSS/tests/documentación; sin migración, usuarios,
  permisos u operaciones reales. Revisar CONTEXTO para publicación. Aceptación humana
  sigue pendiente; no interpretar estas mejoras como prueba completa de personal.

## Entrega V111: empleados y perfil docente — 19/10/2026

- Usuario autorizó implementar la primera entrega. Empleados está en Administración →
  Personal y nómina → Empleados (/admin/empleados), con filtros/paginación JPA y
  Excel POI streaming con los mismos filtros. Expediente por pestañas: ficha/foto,
  personales, laborales, planteles, información docente y acceso. Tema y controles
  compartidos, ayudas/autocompletado10 al foco/3 caracteres y modales de confirmación.
- V111 crea empleado, empleado_laboral, empleado_laboral_historial y empleado_plantel.
  Convierte uno a uno por maestro.id/procedencia, sin fusión por nombre/email, conserva
  Maestro.id/código/usuario/actividad/FK/foto. Identidad canónica sólo en Empleado;
  columnas personales legadas de Maestro quedan snapshot técnico, no fuente editable.
  Actualizados JPQL/Criteria/orden/soporte/horarios; no basta delegación Java.
- Laboral inicial POR_CONFIRMAR, sin inferir ingreso/puesto/baja/planteles/principal
  ni sueldo. Multiplantel con rangos inclusivos y un principal simultáneo; historial
  laboral privado por trigger inmutable, versión/actor/fecha. No borrado de expedientes
  ni traslado de un perfil docente a otro empleado. Editar no reactiva perfil/cuenta.
  Deshabilitar docente bloquea asignaciones/horarios vigentes y planeaciones pendientes;
  no desactiva cuentas ni grupos en cascada. Baja laboral independiente del perfil/acceso.
- Nuevos permisos SIN asignación automática a roles: EMPLEADO_LEER/ADMINISTRAR y
  EMPLEADO_LABORAL_LEER/ADMINISTRAR. Laboral requiere también lectura de Empleados.
  Sólo administrativo activo/persistido, alcance institucional. Permisos de Maestros,
  familias, docente, soporte o recuperación no abren expedientes laborales. No ampliar
  roles/cuentas del propietario para probar. Asignar manualmente desde Roles y permisos.
- Alta de maestros ahora comienza en Empleados → Información docente; gestión de
  cuenta/grupos/materias permanece en Maestros con MAESTRO_ADMINISTRAR. Rutas antiguas
  de escritura personal niegan cambios; foto académica requiere también EMPLEADO_ADMINISTRAR.
  Cuenta administrativa/docente mixta pendiente: no convertir tipoCuenta ni crear cuentas
  al guardar personal. Nómina, sueldos, préstamos y pagos NO implementados en esta entrega.
- Validaciones técnicas: SQL V1–111 en PostgreSQL17 aislado, conversión ficticia con
  maestros homónimos/contacto repetido, conservación de cuenta/foto/actividad, alta nueva
  sin columnas legadas, exclusiones/IDOR de plantel e historial/vínculo inmutables.
  Hibernate/repositorios arrancaron con esquema nuevo y salud UP en entorno aislado.
  Pruebas de servicio, permisos/CSRF HTTP, vistas Thymeleaf, Excel101 filas/texto literal
  y modales Node. QA visual con datos ficticios, móvil390px/claro y oscuro, pestañas y
  Cancelar sin envío; no aceptación humana ni prueba de nómina.
- Estado operativo final y cifras de pruebas: ver CONTEXTO_PROYECTO.md (entrada V111).
  Base local encontrada V98, distinta de la V110 documentada en la otra computadora;
  NO asumir que un pull aplica migraciones. Respaldo privado ignorado en
  .respaldos-locales/personal-v111-20261019; excluido de Git y contexto Docker, fuera de
  target para no perderlo con clean. Nunca mostrar su contenido. Respaldos
  externos/cifrado/retención/restauración de archivos siguen pendientes operativos.
- Siguiente: prueba humana de identidad canónica, empleado no docente, perfil nuevo,
  laboral/multiplantel y regresión de portal/soporte/asignaciones. Después definir etapa
  de puestos/condiciones salariales antes de nómina. Pendientes previos intactos: agenda,
  ocho casos familiares y sesiones5m aplazadas (mantener30m), Compras documental/respaldos.
  No reabrir traspasos/reversas ni dar por cerrado todo el sistema.

Las entradas siguientes son historial previo; «no implementada/V111 libre» describe
el inventario anterior y queda superado por esta entrega.

## Inventario y propuesta de integración Empleado/Maestro — 19/10/2026

- Usuario autorizó comenzar por definir relaciones/fuente de datos antes de tablas o
  formularios. Se creó DISENO_INTEGRACION_EMPLEADOS_MAESTROS.md, propuesta técnica
  NO implementada: inventario de Maestro/FK/consultas/foto/accesos/alcance/portal/
  soporte/reportes/agenda, relaciones, conversión, permisos y matriz de regresiones.
- Empleado fuente de identidad personal; Maestro conserva ID, código docente,
  usuario y actividad, con vínculo único a Empleado. Un empleado por maestro actual
  por ID/procedencia, jamás fusión por nombre/correo. EmpleadoPlantel confirmado con
  vigencia/principal; grupos académicos no acreditan fecha laboral ni principal.
  No copiar fechas técnicas como contratación ni inventar sueldo/estado laboral.
- Riesgos comprobados: MaestroService.desactivar también desactiva usuario y todas
  asignaciones; copiar reactiva. Criteria/JPQL usan campos personales directos; no
  basta delegar getters. Foto tiene reemplazo/retiro privado; mantener mismo Archivo.
  MAESTROS exige administración institucional; adscripción laboral no da permisos.
  Usuario.tipoCuenta es exclusivo (admin/tutor/maestro), también validado por agenda:
  elegir acceso mixto o cuentas separadas antes de ofrecer ese flujo. Sin convertir
  tipos, reasignar cuentas, agendas o permisos automáticamente.
- Propuesta aditiva: conservar columnas personales legadas sólo técnicamente sin
  segunda fuente funcional, resolver sus NOT NULL para altas nuevas, cambiar todas
  lecturas y consultas a canónica antes de editar. No borrar columnas en esta entrega.
  No dar de baja laboral mediante el desactivar académico existente. Sueldos/nóminas/
  préstamos quedan fuera de esta primera implementación. Ver documento para detalles.
- Verificación sólo estática/diff; no certifica datos o esquema aplicado de esta
  instalación. Git base c391be1, sin cambios locales al iniciar; última migración
  presente V110/V111 libre. No se ejecutaron SQL, pruebas, builds, reinicios ni deploy;
  no .env, usuarios, operaciones reales o commit. Sólo documentación actualizada.
- Siguiente: presentar/revisar propuesta y después implementar entrega autorizada;
  no interpretar este documento como integración ya construida. Pendientes previos
  (agenda humana, ocho escenarios familiares, sesiones aplazadas, Compras documental,
  respaldos) intactos. No reabrir traspaso/reversa cerrado.

## Próxima etapa: empleados, maestros y nómina — 2026-10-18

- Propietario incorpora nómina al alcance futuro y aprueba Empleados como captura única
  con perfil docente opcional y varios planteles por institución. Empleado, maestro y
  usuario son distintos; acceso independiente, sin permisos automáticos por asignación.
  Conservar IDs, usuarios, relaciones e historial de maestros; nunca borrar/recrear.
- LEER ÍNTEGRO `PLAN_PERSONAL_Y_NOMINA.md` antes de ampliar: acuerdos, modelo conceptual,
  fases, integración compatible y reglas aún por cerrar. Primera etapa: inventariar
  dependencias y presentar estrategia aditiva; implementar empleados/perfil docente/
  multi-plantel, no toda la nómina. No fusionar por nombre ni inventar datos laborales.
- Plan incluye bruto/base/frecuencia/vigencia, conceptos, préstamos, distribución de
  costos, nómina/pagos/reversas y recibo NO fiscal. IMSS/Infonavit/ISR inicialmente
  manuales con soporte y revisión contable, NO cálculo legal automático ni timbrado.
- Hoy sólo documentación para retomar19/10 o próxima sesión; sin código, migraciones,
  reinicios, operaciones reales ni commit. V110 último estado documentado/V111 libre,
  verificar al retomar. No declarar implementada la integración ni nómina.
- Siguen aceptación humanaV110, ocho escenarios familiares y sesiones aplazadas30m.
  Traspaso/reversa cerrado. Exclusiones históricas de nómina superadas por este acuerdo;
  inventarios/emisión fiscal siguen fuera. Compras documental y respaldos independientes.

## Pendientes personales de administración y docentes — 2026-10-17

- Solicitud aprobada: agenda PRIVADA por usuario, no compartida ni con administradores.
 Acceso intrínseco como el perfil: ADMINISTRATIVO activo/persistido en /admin/pendientes;
 PORTAL_MAESTRO activo/persistido con acceso docente en /maestros/pendientes. No requiere
 asignar un módulo a roles; no se cambian permisos ni usuarios. Niega familias,
 recuperación/bootstrap y consultas de agenda del maestro desde soporte administrativo.
 Todas las lecturas/escrituras/Excel/historial/avisos filtran o verifican usuario e
 institución de la sesión. No se admite propietario en formulario, parámetro o cuerpo.
- V110 crea pendiente_personal (usuario, título180, descripción4000, fecha, estado,
 realizadoEn y auditoría/versiones base) y pendiente_personal_cambio, auditoría PRIVADA
 de acciones/estado/instante/versión. Nunca registrar notas privadas en Auditoría global.
 Triggers bloquean cambiar propietario, borrar pendientes y modificar/borrar historial.
 Migraciones anteriores intactas; modelo de entidades actualizado antes de ampliar.
- Estados Pendiente/En proceso/Realizado/Cancelado; vencido es derivado. Editar, completar,
 cancelar y reabrir sin borrar historial; realizadoEn sólo en Realizado. Fecha local
 institucional, sin horas de compromiso; historial fecha local dd/MM/yyyy HH:mm.
 Listado JPA paginado, búsqueda título, estado/abiertos/vencidos/hoy/próximos y rango.
 Excel POI streaming100 con los mismos filtros, fechas numéricas dd/mm/yyyy y texto
 literal (sin fórmulas desde captura). Historial paginado20, no colección completa.
- Campana/enlace Mis pendientes en encabezado admin y docente real, contador de vencidos,
 hoy y próximos7días. Tarjeta no modal en consultas con workspace/topbar o inicio docente;
 top5 ordenado porfecha/id. No avisa realizados/cancelados ni manda email/push. Carga
 al abrir pantalla, sin sondeo que mantenga sesión activa. Soporte docente oculta el enlace.
- Formularios claros con ayudas propias, errores locales y modales compartidos al
 guardar/editar y cambiar estado: detalle vigente y Cancelar/Escape/fondo sin guardar,
 foco restaurado y confirmación única. Acciones visibles al desplazar textos largos.
 Tema claro/oscuro y filtros uniformes; nuevos CSS limitados a agenda/header propio.
- SQL y triggers validados con datos ficticios dentro de BEGIN/ROLLBACK; sin crear
 pendientes en cuenta del propietario ni persistir datos de QA. Pruebas técnicas NO
 equivalen a aceptación humana de la agenda. No modificar .env ni timeout actual30m.
 Sesiones5m/restauración30m sigue pendiente por separado. Después de revisar agenda,
 retomar los OCHO escenarios acordados de actualización de expedientes familiares;
 no volver a abrir pruebas de traspaso/reversa ya cerradas.
- Verificado855 Java/57 Node sin fallos, diff correcto. Pruebas: IDOR lectura/edición/
 estado/historial, tipo de cuenta/activo/institución, pertenencia en JPA incluso Todos,
 CSRF/anonimato/soporte, datos JSON sin principal, controles de versión, completar/
 cancelar/reabrir y fecha de realización, avisos sólo abiertos/top5, Excel101 filas
 en dos páginas/fecha numérica/texto sin fórmulas y seis vistas reales de ambos portales.
 QA navegador con seis fixtures ficticias y POST bloqueado: escritorio claro y390×844
 oscuro con estilos globales/ayudas/reloj, filtros y capturas legibles; Cancelar y modales
 de guardar/estado sin mutaciones. Se corrigió contraste de tarjetas y botones en oscuro.
 Resultados técnicos, no prueba humana de agenda ni carga de millones de pendientes.
- Despliegue: V110 aplicada y Hibernate/repositorios arrancan sin errores; salud UP8080.
 Publicación17/10 a19:00México (18/10 a01:00UTC), con aviso de posible cierre de sesión.
 Verificación SQL sólo lectura: pendientes0/cambios0;38 guías existentes intactas.
 Endpoint de avisos sin sesión401. PostgreSQL/volúmenes intactos; no .env, usuarios,
 roles, movimientos financieros ni commit. Timeout30m conservado; próxima V111.
 Servidor/pestaña/contenedor y etiqueta QA retirados, viewport restaurado. Sólo archivos
 ficticios ignorados en target/qa-pendientes; sin copias de documentos reales.

## Cierre humano de pruebas de traspaso/reversa — 2026-10-17

- Propietario aportó reconsulta posterior a la reversa del corte Tesorería16/10/2026:
 pantalla/PDF y Excel COMPLETO coinciden con el histórico previo. BBVA1mov/ingreso500;
 origen3mov/ingreso406/egreso406/neto0/traspasos0entrada-300salida; destino1mov/operación0,
 traspasos300entrada-0salida/MXN. PDF apertura4650/ingresos906/egresos406/cierre5150,
 netos de traspasos0/-300/+300. Compensaciones17/10 no alteraron este corte anterior.
- Captura adjunta muestra formulario de transferencia1 ya revertida, cuentas correctas,
 importe300, fecha original16/10 a20:01 y aviso Esta operación ya fue revertida / No es
 posible publicar una segunda reversa; tarjeta termina sin campos ni botón de envío.
 Bloqueo EN PANTALLA aceptado, no afirmar ensayo humano de POST forzado o concurrencia.
- V109 sólo UPDATE de guías existentes: reemplaza paso7 de reversa y pendientes de
 resúmenes/paso6 de guías anteriores; no crea guías duplicadas ni modifica V1–V108.
 Ya no quedan las dos comprobaciones humanas pendientes de ESTE ejemplo financiero.
 No equivale a aceptación de todos los módulos/variantes. Sigue pendiente por separado
 revisión de sesiones (propuesta5m temporal/restaurar30m), sin tocar properties actual30m.
 SQL validado BEGIN/ROLLBACK; sin operaciones financieras, .env, roles ni commit.
- Verificado840 Java/52 Node sin fallos y diff correcto. V109 aplicada:38 guías/315
 etapas, sin duplicados; versiones transferencia4/reportes3/reversa2. App recreada y
 salud UP8080,17/10 a18:29México; se avisó que publicación puede cerrar sesión. Base y
 volúmenes conservados. Properties30m sin cambios; próxima migración V110. No ejecutar
 nuevas pruebas financieras hasta acordar siguiente caso; este bloque ya está cerrado.

## Sesiones aplazadas por el propietario; continuar pruebas financieras — 2026-10-17

- Propietario aclaró que el cierre ocurrió después de publicar modal, cuando hizo
 su prueba y escribió; no dio hora/contador exactos. NO atribuirlo al reinicio16:08
 ni declararlo corregido. A petición explícita, dejar diagnóstico/prueba pendientes.
- Propuesta para ensayo futuro controlado: cambiar temporalmente en properties
 server.servlet.session.timeout de30m a5m, publicar ANTES de iniciar prueba y no
 reiniciar durante ella. Comprobar aviso, vencimiento, renovación y retorno al login
 propio; registrar horas/contador/actividad. Restaurar30m, publicar y confirmar antes
 de cerrar la tarea. No se autorizó ejecutarlo ahora: sigue30m, no se cambió properties.
- En este turno sólo documentación local; sin compilación/despliegue/reinicio,
 migración ni operaciones financieras. V108 vigente/V109 libre. Continúan dos pruebas
 financieras pendientes: reconsulta histórica16/10 tras reversa y bloqueo de segunda
 reversión por pantalla. El ensayo de sesiones es un pendiente separado, no incluido
 en las dos comprobaciones restantes de este bloque financiero.

## Revisión de cierre antes de30 minutos — 2026-10-17

- Propietario reportó un cierre que considera anterior a30 minutos mientras se
 documentaba reversa. Diagnóstico sólo: NO cambiar seguridad ni persistencia sin
 causa confirmada. application.properties fija server.servlet.session.timeout=30m,
 no hay otra definición en application*, ni SERVER_SERVLET_SESSION_TIMEOUT en
 contenedor. Filtro/estado usan1800s; son30 minutos desde última petición útil, no
 desde la última tecla. Sondeo GET/estado cada5s NO renueva; acciones del servidor y
 POST/renovar sí. Contador usa reloj servidor/performance.now, no reloj local.
- En revisión antes de publicar V108 el contenedor arrancó17/10/2026 a22:08:17UTC
 (16:08:17 México), app lista16:08:23; corresponde a publicar modal. Sesiones locales
 se pierden al recrear app; no demuestra por sí solo causa del incidente sin hora
 del propietario. RestartCount0 no excluye recreación (el contenedor es nuevo).
 Se preguntó cuándo cerró, tiempo visible y actividad; respuesta pendiente.
- Prueba adicional noCierraAntesDeLosTreintaMinutosExactos comprueba sesión válida
 tras29:59.999; pruebas existentes invalidan a30:00 y renovación1800s. Sin cambios
 funcionales de sesiones ni promesa de incidente corregido. Para prueba humana usar
 tramo sin publicaciones/reinicios y anotar inicio, última acción y hora de cierre.

## Reversa del traspaso y reportes aceptados — 2026-10-17

- Propietario confirmó reversión tras publicar modal. TRASPASO-TEST-001/transferencia1,
 original16/10 a20:01; reversa17/10/2026 a16:10. Cuentas: origen BANCO-COMPRAS-TEST
 saldo actual2000/inicial2000; destino BANCO-TRASPASO-TEST actual0/inicial0. Libro:
 ingreso REVERSO300 al origen, antes1700/después2000, Compensa #6, secuencia #4;
 egreso REVERSO300 del destino, antes300/después0, Compensa #7, secuencia #2. No se
 proporcionaron IDs de los nuevos movimientos ni cabecera de reversa: NO inventarlos.
- Tesorería diaria17/10, todas autorizadas: dos filas/un movimiento cada una,
 ingresos/egresos/neto operativo0. Origen traspasos entrada300/salida0; destino0/300.
 Excel coincide. PDF apertura5150/operación0/cierre5150 y netos traspaso+300/-300.
- Estado mensual2026/Octubre origen: cuatro filas (reversa300, traspaso300, reversa
 compra406 y compra406), apertura0, inicial registrado2000, ingresos706/egresos706,
 cierre2000. Destino: dos filas (reversa egreso300 y traspaso ingreso300), apertura0,
 inicial0, ingresos300/egresos300/cierre0. Pantalla/PDF/Resumen y detalle Excel de AMBAS
 cuentas confirmados. Primera copia repetida del origen fue error humano reconocido,
 NO fallo de filtro; después aportó destino correcto. Historial de originales intacto.
- V108 editorial: nueva guía reversa-traspaso-reportes-confirmados, CONFIRMADA,
 siete etapas/cuatro acciones. Actualiza resumen/paso6 de guías V106/V107 y añade
 advertencia histórica a pasos3–5 de V107: sus tres/una filas eran ANTES de reversa,
 no se deben esperar hoy al consultar todo octubre. V1–V107 SQL sin modificar.
 SQL validado BEGIN/ROLLBACK; sólo tablas guia_proceso*, sin mover dinero/otorgar roles.
- Quedan DOS comprobaciones humanas de este bloque: volver a consultar corte16/10
 después de revertir (debe conservar valores anteriores) y comprobar pantalla de
 transferencia ya revertida sin formulario ni segunda reversión. No forzar POST.
 Cancelar/Escape/fondo del nuevo modal siguen con evidencia técnica, no humana separada;
 motivo exacto guardado no mostrado. No declarar aceptadas estas variantes ni todo el
 sistema. Próximo: consulta histórica, después bloqueo de segunda reversión.
- Verificado838 Java/52 Node sin fallos; diff correcto. V108 aplicada:38 guías/315
 etapas, nueva guía CONFIRMADA/v1, transferencia v3 y reportes previos v2. App recreada
 y salud UP8080; publicación17/10 a23:39UTC (17:39México), se avisó que cerraría sesiones.
 PostgreSQL/volúmenes intactos, sin .env, operaciones financieras, roles ni commit.
 Próxima migración V109. Incidente de sesión anterior sigue sin causa individual
 confirmada; empezar prueba humana sin reinicios después de esta publicación.

## Confirmación previa de reversa trazable — 2026-10-17

- A petición del propietario, Confirmar reversa trazable ahora abre el modal compartido
 money-input, kind reversion-financiera, antes del POST. Supera la nota anterior de
 envío directo. Cancelar del formulario tiene clase detail-close-button y conserva
 navegación al libro. Plantilla compartida cubre transferencia y operación manual.
- Modal: ID/tipo de operación, recorrido original o cuenta/dirección, importe/moneda,
 fecha original, fecha de reversa dd/MM/yyyy HH:mm, motivo vigente y compensación
 esperada (dos movimientos inversos para transferencia, uno para manual). Advierte
 devolución destino→origen, fondos suficientes, historial y que no envía dinero al banco.
 Cancelar/Escape/fondo sólo cierran y conservan captura/foco. Confirmar y registrar
 reversa reutiliza POST/CSRF/versión/idempotencia originales, sin modificar servicio.
- Assets versionados20261017-reversion. Acciones pegadas al pie al desplazar, móvil y
 oscuro.835 Java/52 Node correctos: render real transferencia/manual/revertida, moneda,
 escape HTML, validación previa, motivo/fecha actualizados, cancelación/Escape/fondo y
 confirmación única. QA navegador con fixture ficticia y todo POST bloqueado: escritorio
 claro y390×844 oscuro legibles, Cancelar conserva campos. No operación financiera real.
- Sin migración (V107 vigente/V108 libre), .env, usuarios/roles ni commit. Propietario
 todavía NO ha revertido transferencia1/TRASPASO-TEST-001: último saldo aceptado origen
1700/destino300. Recargar página tras publicación; probar Cancelar primero, después
 volver a abrir y confirmar una vez para continuar ensayo humano pendiente.
- App reconstruida/recreada, salud UP8080;107 migraciones validadas sin nuevas.
 PostgreSQL/volúmenes intactos. Servidor/pestaña/contendor y etiqueta QA retirados,
 viewport restaurado. No se pulsó confirmación sobre registros de la instalación.

## Reportes del traspaso aceptados por propietario — 2026-10-17

- Evidencia humana nueva: Tesorería diaria16/10/2026, todas las cuentas autorizadas:
 BBVA_COBROS1mov/ingreso500; BANCO-COMPRAS-TEST3mov/ingreso406/egreso406/neto0,
 traspasos entrada0/salida300; BANCO-TRASPASO-TEST1mov/operación0/entrada300/salida0.
 Excel coincide. Balanza PDF apertura4650/ingresos906/egresos406/cierre5150; traspasos
 netos0,-300,+300 por fila. No confundir compra/reversa406 con transferencia300.
- Estado por cuenta usa corte Mensual/año2026/mesOctubre, NO Desde/Hasta. Origen:
 tres movimientos #3traspaso egreso300, #2reversa ingreso406, #1compra egreso406.
 PDF/Resumen Excel: apertura0, saldo inicial registrado en periodo2000 (no ingreso),
 ingresos406, egresos706, cierre1700. Detalle Excel coincide. Destino: una entrada300
 claseTRASPASO, referenciaTRASPASO-TEST-001, apertura0, ingresos300/egresos0/cierre300,
 inicial registrado0; pantalla/PDF/ambas hojas Excel confirmados. Secuencias no son IDs.
- V107 sólo editorial crea reportes-traspaso-confirmados, CONFIRMADA, seis etapas de
 cuatro acciones, permiso REPORTE_FINANCIERO_CONSULTAR sin conceder roles. Actualiza
 resumen y paso6 de guía V106 para retirar pendiente de reportes, sin editar V106.
 No declara aceptación visual separada de logo ni reversa. SQL validado BEGIN/ROLLBACK.
- Verificado832 Java/49 Node sin fallos, diff correcto. V107 aplicada por Flyway:
 guía nueva CONFIRMADA/v1, transferencia previa CONFIRMADA/v2;37 guías/308 etapas.
 Sólo app reconstruida/recreada, salud UP8080, PostgreSQL/volúmenes intactos. Etiqueta
 QA retirada. Sin .env, usuarios/roles, datos financieros ni commit. Siguiente V108.
- Próximo ensayo humano: reversión de transferencia1 desde libro con enlace
 /admin/reversiones/transferencias/1/nueva. Identificar referencia/cuentas/300, fecha
 real no futura ni anterior a original; motivo obligatorio. Pantalla actual NO tiene
 modal adicional: Confirmar reversa trazable envía la operación. No prometer modal.
 Si no hay otras operaciones, origen1700→2000, destino300→0, dos movimientos REVERSO
 ligados a originales conservados. Fecha de reversa actual puede caer el17, no el16:
 reportes diarios del16 NO deben cambiar. Nunca aplicar por herramientas.

## Cabecera de Excel con título centrado — 2026-10-17

- Los cuatro exportadores de Reportes financieros y sus seis hojas conservan logo
 izquierdo proporcional/ancla absoluta. Se agrega título específico del reporte en
 azul,18pt, centrado horizontal/vertical en bloque combinado a la derecha del logo.
 Las cuatro filas existentes llevan relleno blanco sólido: oculta cuadrícula sólo
 en cabecera, sin alterar preferencia de cuadrícula del resto de cada hoja. Concentrado
 conserva su preferencia anterior sin cuadrícula global. PDF y recurso PNG intactos.
- Sin mover tablas, resúmenes, fechas/importes, filtros, congelación, paginación ni
 permisos.830 Java/49 Node correctos; prueba nueva guarda/reabre XLSX streaming de301
 filas y comprueba título, combinación, fondo blanco limitado y cuadrícula preservada.
 Habilidad de hojas de cálculo aplicada a revisión de sólo lectura de seis hojas
 ficticias; títulos legibles, sin cubrir imagen/datos. No sustituye aceptación humana:
 descargar Excel nuevo. Muestras técnicas ignoradas en target/qa-logo-financiero.
- Sin .env, cambios financieros, roles, commit ni migración;106 vigente/V107 libre.
 App reconstruida/recreada únicamente, salud UP en8080 y106 migraciones validadas.
 PostgreSQL/volúmenes intactos. Contenedor y etiqueta temporales QA retirados.
 Continúan pendientes reportes operativos del traspaso y posterior ensayo de reversa.

## Excel: logo mayor y ancla absoluta contra deformación — 2026-10-17

- Propietario reportó logo estirado al abrir Excel: NO considerar aceptada la apariencia
 anterior sólo por las muestras renderizadas. Ajuste exclusivo a Excel; PDF y PNG fuente
 permanecen sin cambios. Debe descargar un archivo nuevo, los anteriores no se actualizan.
- MarcaReporteFinanciero reserva las mismas cuatro filas, ahora30pt cada una. Imagen
 máxima192×128px (antes aprox96×64), calculada por dimensiones nativas para conservar
 proporción y dar margen a logos futuros. Tablas siguen en las mismas filas que tras
 la cabecera anterior: no cambian datos, filtros ni congelación/autofiltro.
- Se reemplaza twoCellAnchor/editAs=oneCell con dos offsets dentro de la misma celda
 (incluido extremo fuera de su altura) por xdr:absoluteAnchor real. Posición y tamaño
 explícitos en EMU, mismo tamaño en transformación gráfica y bloqueo noChangeAspect;
 se retira el ancla provisional. No depende del ancho de columnas ni altura de filas.
 Se conserva copia única del PNG por libro, válida con SXSSF y XSSF.
- Verificado:829 Java/49 Node sin fallos, diff correcto. Prueba nueva guarda/reabre un
 XLSX después de cambiar anchuras/alturas y verifica ancla absoluta, tamaño/proporción,
 bloqueo y transformación coherente. Exportaciones y datos anteriores conservados.
 Habilidad de hojas de cálculo usada para QA de sólo lectura: las seis hojas ficticias
 importadas/renderizadas con logo mayor, proporcionado y sin cubrir tablas; XML final
 inspeccionado. Esto NO sustituye aceptación en el Excel del propietario, aún pendiente.
- App reconstruida/recreada, salud UP en8080;106 migraciones, V107 libre. PostgreSQL/
 volúmenes intactos; sin .env, cambios financieros, usuarios/roles ni commit. Contenedor
 y etiqueta temporales retirados; muestras ignoradas en target/qa-logo-financiero.
 Próximo humano: descargar de nuevo el Excel y confirmar tamaño/proporción. Prueba
 operativa de reportes del traspaso y su reversa continúan pendientes.

## Logo provisional en PDF y Excel de Reportes financieros — 2026-10-17

- Solicitud del propietario: usar el logo provisional existente y poder sustituirlo
 después. Se usa reports/assets/logo-institucion-ejemplo.png (libro azul/turquesa con
 hojas doradas, ya utilizado por fichas/comprobantes); NO static/images/logo.png
 (escudo verde/marrón de Instituto Raíces). Ninguna imagen fuente fue modificada.
 Referencia única para finanzas en MarcaReporteFinanciero.RECURSO. Para cambiarlo:
 reemplazar ese PNG conservando nombre/ruta y reconstruir/recrear app; recurso y caché
 forman parte del JAR. Otros reportes que usan ese mismo logo de respaldo lo compartirán.
- Cuatro PDF Jasper: Tesorería/balanza, Estado de cuenta por cuenta, Estado del alumno
 y Concentrado de cobranza. Imagen conservando proporción en bloque blanco izquierdo
 de la cabecera, textos a la derecha; mantiene institución/título/subtítulo/resúmenes.
 Sin consultas, alcances, permisos, filtros, totales o transacciones nuevos.
- Cuatro Excel y sus seis hojas (incluidos ambos Resumen) llevan el mismo PNG nativo.
 Reserva cuatro filas superiores sin texto decorativo ni combinaciones; tablas/datos
 originales desplazados cuatro filas, con congelación y autofiltro ajustados. Logo de
 tamaño fijo/proporcional y una sola copia binaria por libro, reutilizada entre hojas.
 Conserva hojas/orden/hoja inicial, streaming por100, números/fechas y estados vacíos.
 No se amplía a compras, proveedores u otros Excel fuera de Reportes financieros.
- Verificado:828 Java y49 Node sin fallos, diff correcto. Pruebas de imagen PDF/Excel,
 proporción/reutilización y conservación de fechas/importes/filtros/paginación/vacíos;
 tests existentes ajustan sólo posiciones por cabecera. Habilidades PDF/hojas de cálculo
 usadas para QA de muestras ficticias, no para sustituir Jasper/POI. PDFBox renderizó
 los cuatro PDF y Artifact Tool importó/renderizó las seis hojas sin reescribir archivos.
 Revisión visual final sin logo sobre datos; muestras ignoradas en target/qa-logo-financiero.
- App reconstruida/recreada y salud UP en8080;106 migraciones validadas, sin nueva
 migración (V107 libre). PostgreSQL/volúmenes intactos, sin .env, usuarios, roles,
 operaciones financieras ni commit. Contenedores y etiquetas temporales de pruebas
 retirados, no datos operativos. Logo nuevo y aceptación visual humana aún pendientes.
- Próximo: propietario descarga PDF/Excel de Reportes financieros para revisar logo.
 Continúa pendiente probar el traspaso confirmado en reportes con datos de instalación;
 estas muestras son técnicas/ficticias, NO aceptación de reportes ni de reversa del caso.

## Traspaso aceptado, guía V106 y foto ampliable de sesión — 2026-10-16

- Propietario confirmó modal y aplicación de300 MXN, referencia TRASPASO-TEST-001,
 16/10/2026 a20:01 institucional. BANCO-COMPRAS-TEST/cuenta4 pasó2000→1700;
 BANCO-TRASPASO-TEST/cuenta5 pasó0→300. Dos movimientos institucionales: EGRESO TRASPASO
 desde origen e INGRESO TRASPASO en destino. Ambos enlaces de reversión apuntan a
 transferencia1. #3/#1 junto a fecha son secuencias por cuenta; no inventar IDs de los
 movimientos. No repetir la transferencia ni revertirla sin acordar el siguiente ensayo.
- V106 sólo editorial añade transferencia-entre-cuentas-confirmada, CONFIRMADA, seis
 etapas/cuatro acciones cada una. Tres permisos asociados limitan su lectura sin
 otorgar derechos. Documenta exactamente evidencia anterior; reversa y reportes de
 este traspaso NO están aceptados. Cancelar/Escape del resumen tienen verificación
 técnica, no confirmación humana separada. V1–V105 intactas. Guía36/302 etapas totales,
 V106 aplicada; siguiente migración V107. Supera notas previas de traspaso pendiente.
- Solicitud nueva: clic en foto dentro de la ficha rápida abre una segunda vista previa
 nativa, amplia y de sólo lectura, sin recortar (object-fit contain). Reutiliza la ruta
 privada de fotografía propia y avatar genérico existentes. Botón de foto con lupa,
 foco visible y aria-expanded; assets del fragmento versionados 20261016-foto.
 Cerrar vista previa/Escape/fondo vuelven al perfil con sus datos y foco en la foto,
 sin cerrar la sesión, navegar ni modificarla. Si cierra la ficha principal también
 cierra la foto. Error de imagen muestra aviso local y conserva cierre. Sin endpoints,
 carga de archivos, cambios de usuario, nuevos permisos ni almacenamiento adicional.
- Verificado:826 Java y49 Node sin fallos; render real de ambos dialogs, seguridad
 previa conservada, reapertura/cierre/Escape/fondo, foto privada propia, fallo de imagen
 y conservación del perfil. Navegador ficticio claro escritorio y390×844 oscuro:
 imagen completa, sin desbordamiento, cierre visible y foco restaurado. Referencia
 visual ignorada en target/qa-perfil/foto-ampliada.png. No implica aceptación humana
 de la nueva ampliación. SQL V106 validado primero en BEGIN/ROLLBACK y luego aplicado
 por Flyway. Sólo app recreada, salud UP en8080; PostgreSQL/volúmenes intactos.
 Limpieza pendiente de QA anterior completada; contenedor/etiqueta actuales y servidor/
 pestaña temporales retirados, viewport restaurado. Sin .env, cambios operativos ni commit.
- Próximo humano: recargar, abrir Sesión iniciada como → pulsar foto → Cerrar vista
 previa para comprobar retorno. Después revisar este traspaso en Tesorería y estados
 de cuenta/exportaciones, separando entradas/salidas de TRASPASO de operación. Origen
 también tiene compra/reversa históricas: no atribuirlas a este traspaso ni afirmar
 ingresos/egresos operativos cero para toda la cuenta. Reversa será ensayo independiente.

## Transferencias entre cuentas: confirmación previa — 2026-10-16

- Propietario confirmó saldo2000 de BANCO-COMPRAS-TEST y que creó la cuenta destino
 del ejemplo. No se ha aplicado ni aceptado un traspaso; no crear otra cuenta ni mover
 dinero por herramientas. Antes de confirmar300, comprobar que destino parte de0.
- Aplicar transferencia abre modal común money-input (transferencia-cuentas), con
 institución, origen/salida, destino/entrada, importe monetario, fecha dd/MM/yyyy HH:mm,
 referencia y observaciones vigentes. Cancelar/Escape/fondo sólo cierran y conservan
 captura; Confirmar y aplicar transferencia usa el POST original con CSRF y clave de
 idempotencia, sin cambiar servicios, alcance ni transacciones. Resumen usa textContent.
- Cancelar al lado de Aplicar transferencia tiene estilo secundario común
 detail-close-button, conservando su enlace al libro. Assets monetarios versionados en
 esta pantalla. Acciones del modal de traspaso permanecen visibles al desplazar y abre
 desde el inicio: excepción local al modal común para evitar que el foco inicial en
 Cancelar esconda el título en teléfonos. Los demás tipos de confirmación no cambian.
- Verificaciones:824 Java/47 Node sin fallos, git diff --check. Render real Thymeleaf,
 contrato th:action/CSRF, resumen vigente, datos literales, validación, campos opcionales,
 Cancelar/Escape sin envío y confirmación única. Navegador ficticio con POST bloqueado
 en escritorio claro y390×844 oscuro: modal legible, sin desborde, título al abrir,
 acciones visibles, cierre conserva captura/foco. Referencia ignorada en
 target/qa-transferencia/confirmacion.png. Prueba operativa permanece pendiente.
- Próximo humano: recargar Transferir entre cuentas; origen BANCO-COMPRAS-TEST2000,
 destino BANCO-TRASPASO-TEST0, monto300, referencia TRASPASO-TEST-001 y fecha/hora real
 no futura ni anterior a aperturas. Abrir resumen y Cancelar primero: no cambia dinero.
 Reabrir, revisar y confirmar UNA vez. Esperado1700 origen y300 destino, dos movimientos
 ligados de TRASPASO. Pedir evidencia antes de incorporar a guía como confirmado.

## Ficha rápida del usuario desde su foto de sesión — 2026-10-16

- Petición previa a probar transferencias: clic en foto/nombre de «Sesión iniciada como»
 abre perfil de sólo lectura, sin navegar ni perder la captura. Implementado en fragmento
 session-actions compartido por listados y formularios administrativos. No modifica los
 encabezados propios de familias/maestros. Único botón del modal: Cerrar; Escape y fondo
 también cierran, devuelven foco y no cierran sesión ni envían formularios.
- Diseño aislado en session-profile.css/js: foto112px con marco, portada azul tinta y
 turquesa, usuario/correo/institución/tipo de acceso/alcance en tarjeta; dos columnas
 escritorio y una móvil, temas claro/oscuro. Modal nativo dialog (foco contenido), ancho
 máximo520 y altura limitada al viewport, desplazamiento interno y pie fijo al fondo.
 Loading/error visibles con cierre disponible; aborta al cerrar y descarta respuestas
 atrasadas al reabrir. Datos escritos como texto, no HTML; no guarda perfil en caché local.
- GET /admin/perfil/resumen consulta sólo el usuario del principal autenticado, nunca
 recibe un ID seleccionable. Servicio transaccional readonly y DTO explícito de cinco
 textos: no devuelve entidades, password/hash, tokens, lista de permisos ni archivos.
 Valida institución y estado activo; acceso de recuperación usa presentación limitada
 sin consultar BD. No exige USUARIO_ADMINISTRAR para ver la propia ficha. Alcance limitado
 muestra cantidad de planteles autorizados sin cargar catálogos. Foto conserva endpoint
 privado existente /admin/perfil/fotografia y avatar genérico cuando no está configurada.
- Verificado:823 Java/45 Node sin fallos; pruebas de DTO, alcance, recuperación, rechazo
 de usuario ausente/inactivo/otra institución, HTTP sólo propio y no-store, parámetros ID
 ignorados, render Thymeleaf real con nombre escapado, carga bajo demanda, cierre/Escape/
 fondo, error/redirección y carreras de respuestas. Navegador con fixture ficticia en
1280 escritorio,390×844 oscuro/textos largos y320×568 claro: sin desborde horizontal,
 un solo botón Cerrar visible, foco restaurado y error local sin navegación. Captura
 de referencia ignorada en target/qa-perfil/perfil-vista.png. Aceptación humana pendiente.
- Sólo app reconstruida/recreada, salud UP en8080; consulta real anónima JSON devuelve401.
 Flyway valida105 migraciones, sin nueva migración; V106 libre. PostgreSQL/volúmenes no
 reiniciados, sin .env, roles, operaciones financieras ni commit. Servidor, pestaña,
 contenedor y etiqueta de imagen temporales retirados; viewport restaurado.
- Siguiente propietario: recargar una pantalla administrativa, pulsar foto/nombre y
 comprobar foto/datos/Cerrar sin perder captura. Después ensayo propuesto, NO ejecutado:
 banco institucional BANCO-COMPRAS-TEST con2000 → banco institucional nuevo
 BANCO-TRASPASO-TEST con apertura0, misma institución/MXN, transferencia300 una sola vez
 (referencia TRASPASO-TEST-001). Esperado origen1700, destino300, dos movimientos de
 TRASPASO relacionados, total combinado2000 y sin ingreso/gasto operativo consolidado.
 Usar fecha real no futura y no anterior a la apertura; no reutilizar cajas, BBVA_COBROS
 ni repetir compra/reversa. Si las cuentas tienen otro saldo, acordar el ejemplo antes.
 No documentar la transferencia como confirmada hasta recibir evidencia del propietario.

## Compras aceptadas, Excel con formato y guía V105 — 2026-10-16

- Propietario confirmó que filtros/diseño funcionan correctamente y compartió Excel:
 COM-00000001, Papelería Prueba Raíces, RFC vacío, Plantel prueba de permisos,
 Cuenta de prueba de compras, TICKET-PRUEBA-001, total406, Cancelada, Movimiento4.
 Esto acepta contenido y filtros del archivo anterior; NO confundir con aceptación
 humana del formato nuevo, pendiente de volver a descargarlo. Estas notas prevalecen
 sobre las entradas anteriores que aún indican filtros/Excel pendientes.
- ExcelCompraService conserva filtros, páginas de100 y streaming; Fecha pasa de texto
 ISO a celda numérica nativa con dd/mm/yyyy hh:mm, usando la zona de la institución.
 Total sigue numérico y usa "$"#,##0.00; anchos23 y20 para fecha/total y sólo dos
 estilos compartidos, sin crecer por registro. Ejemplo16/10/2026 17:58 y $406.00;
 también probado $1,000,000.50. Sin cambios de fechas operativas ni lógica financiera.
- V105 añade sólo contenido editorial de la guía compras-confirmacion-reversa:
 «Compra de prueba: egreso, cancelación controlada y consulta del historial», categoría
 Tesorería, CONFIRMADA, ocho etapas con cuatro acciones cada una. Se limita por once
 permisos de los módulos involucrados; no concede roles ni crea datos operativos.
 Documenta preparación, borrador350+56=406, egreso original #4 de2000 a1594,
 modal de cancelación, reverso406/Compensa #4 de1594 a2000, filtros y Excel.
 Cuenta BANCO-COMPRAS-TEST es Institucional y operación del Plantel prueba de permisos.
 Ese plantel YA tiene actividad, aunque V104 describe su estado vacío anterior.
 ID del reverso desconocido: #1/#2 son secuencias por cuenta, no inventar el ID.
 La guía distingue proceso/contenido aceptados de nuevo formato verificado técnicamente.
- Verificado:815 Java y41 Node sin fallos, git diff --check; pruebas del archivo real
 con POI comprueban tipos numéricos, fecha/moneda, cambio de día UTC→zona institucional,
 dos páginas con mismos filtros, estilos reutilizados y consulta vacía. Habilidad de
 hojas de cálculo usada para importar/renderizar sólo fixture ficticia: fecha y total
 visibles sin recorte. Sin acceso a .env ni lectura de registros financieros para QA.
 V105 validada primero en BEGIN/ROLLBACK y aplicada después por Flyway al recrear sólo
 app; salud UP en8080. Consulta editorial confirma35 guías/296 etapas. PostgreSQL y
 volúmenes intactos. Contenedor/etiqueta temporal de pruebas retirados; fixtures quedan
 ignoradas en target/qa-compras. Sin commit. Próxima migración disponible V106.
- Siguiente: propietario descarga otra vez Excel de la compra cancelada y confirma
 presentación de fecha/moneda, sin repetir compra ni reversa. Después se propone probar
 Movimientos financieros → Transferir entre cuentas, con dos cuentas aisladas y monto
 controlado (ejemplo300), comprobando egreso/ingreso ligados, saldos y traspaso neto cero
 en reportes; acordar primero el ejemplo y saldo de destino. Aún NO probado ni registrado.

## Revisión de alineación y nombres largos en filtros — 2026-10-16

- Propietario reportó que filtros seguían desalineados y opciones encimadas; no dar la
 corrección visual anterior por aceptada. Se refuerza contrato local del historial:
 cuatro campos principales uniformes, controles44px/títulos con altura común, segunda
 fila de estado/fechas/texto/tamaño y acciones aparte. Dos columnas intermedias y una
 en teléfono. Reglas acotadas a data-purchase-filters, no cambian Proveedores ni captura.
- Opciones ahora usan columna flexible con altura automática explícita y tipografía/
 interlineado separados para nombre y detalle, envolviendo texto largo sin elipsis.
 Sobrescribe sólo los !important necesarios del contrato global; no aplica alturas de
 botones de Consultar ni cuadrículas heredadas. Panel permite desplegar sin recorte y
 CSS de listado tiene versión nueva para cargar el ajuste sin depender de caché anterior.
- Verificado:810 Java/41 Node sin fallos, y navegador ficticio con nombres/detalles
 largos de proveedor/cuenta. Métricas reales: controles principales iguales en1280
 (194.25px de ancho/44px de alto y mismo top), en900 (362.5px) y390 (287px), sin
 desbordamiento. Se corrigió específicamente la prioridad de span3 heredada en móvil
 que creaba columnas implícitas. Diez opciones por lista: nombre y detalle separados,
 altura sin recorte; desplegable380px escritorio (Cuenta alineado a derecha) y ancho
 del control en móvil. Claro/oscuro revisados. Fechas tienen ancho completo, sin tapar
 texto con calendario. Sólo app reconstruida/recreada, salud UP, PostgreSQL/volúmenes
 intactos; QA temporal retirado y viewport restaurado. Sin cambios de consultas,
 seguridad, filtros, Excel, dinero ni migraciones ni commit. COM-00000001 sigue
 cancelada; no se repiten operaciones. Propietario debe recargar Compras y aceptar diseño.

## Filtros de Compras con autocompletado remoto — 2026-10-16

- Proveedor y Cuenta del historial reemplazan select por input/buscador común: foco/clic
 vacío consulta diez iniciales; desde tres caracteres busca en servidor con página11
 para indicar más resultados. Sin listas completas ni carga de motivos en listado.
 Controlador recupera sólo etiquetas seleccionadas por ID para consulta/paginación;
 exportación conserva IDs/filtros originales, sin enviar el texto visible como identidad.
- Endpoints propios /admin/autocompletado/compras/filtros/proveedores y /cuentas exigen
 COMPRA_LEER, no captura ni módulos externos. Autocompletados de captura conservan
 COMPRA_ADMINISTRAR y sólo activos. Historial incluye inactivos identificados para
 localizar registros antiguos. Institución y alcance se validan en servidor; Cuenta
 con Todos los planteles incluye todas las autorizadas, con plantel incluye las de ese
 plantel y compartidas. No confundirlo con captura institucional (sólo compartidas).
- Campos vacíos significan Todos/Todas; escribir sin elegir una opción bloquea Consultar
 hasta seleccionar o borrar, evitando aplicar otro filtro sin avisar. Cambiar institución
 limpia ambas selecciones; cambiar plantel limpia cuenta y descarta respuestas atrasadas.
- Verificado:810 Java y41 Node sin fallos; seguridad HTTP lectura/captura, alcance,
 límites, inactivos, búsquedas/foco/cambios de alcance y controlador real con render de
 selección y conservación de criterios en paginación/exportación. QA ficticio muestra
 diez opciones por filtro y textos sin encimarse; resultados anclados al input y estilos
 acotados al módulo para no heredar altura fija de botones de filtros. Sólo app
 reconstruida/recreada, salud UP; PostgreSQL/volúmenes intactos y QA temporal retirado.
 Sin migración, .env, operaciones financieras ni commit.
 Mantener la prueba manual de filtros/Excel pendiente y no repetir compra/reversa aceptadas.
 La observación previa del formato ISO/moneda del Excel sigue pendiente, no se cambia aquí.

## Reversa aceptada; motivo debajo y siguiente ensayo de filtros — 2026-10-16

- Propietario confirmó funcionamiento del modal y cancelación COM-00000001. Evidencia:
 estado Cancelada; cuenta BANCO-COMPRAS-TEST saldo $2,000.00; ingreso REVERSO406 desde
1594 a2000 con Compensa #4; egreso original406 conservado como Revertido. No repetir
cancelación ni revertir desde Movimientos. #1/#2 de las filas son secuencias por cuenta,
no el ID de la reversa; el propietario no proporcionó ese ID y no debe inventarse.
- Ajuste solicitado: Motivo obligatorio debajo de explicación de cancelación, alineado
a la izquierda en escritorio y teléfono. Una columna, textarea de al menos120px y ancho
completo con box-sizing; botón debajo a la izquierda, ancho completo en móvil. Mantiene
Gestión, validaciones, modal, POST/version/CSRF y comportamiento financiero existentes.
- Verificado:805 Java y39 Node sin fallos; QA ficticio en escritorio y móvil390×844,
 motivo debajo, alineado y sin desbordar; botón completo en móvil. Sólo app reconstruida/
 recreada, salud UP; PostgreSQL/volúmenes intactos y QA temporal retirado. Sin nueva
 migración, operaciones financieras ni commit.
V105 sigue libre. Próximo humano: probar filtros/Excel del historial sin crear datos:
institución instituto Raices, Plantel prueba de permisos, proveedor Papelería Prueba Raíces,
cuenta de prueba, estado Cancelada, 16/10/2026–16/10/2026 y folio COM-00000001 → una
fila406/Cancelada; Excel coincidente. Estado Confirmada mismo folio → vacío. Todas y
referencia TICKET-PRUEBA-001 → recuperar la fila. No documentar estos filtros/Excel
como aceptados antes de recibir respuesta. La compra cancelada ya no ofrece capturar
motivo; no crear otra compra/egreso sólo para ver el cambio visual, probado en QA ficticio.
- Observación de código para siguiente revisión de exportación: ExcelCompraService aún
 escribe fecha ISO y total numérico sin estilo monetario. No afirmar homologación de
 formato dd/MM/yyyy/pesos ni aceptación del Excel hasta revisar/corregir ese pendiente.

## Confirmación previa a cancelar una compra — 2026-10-16

- A petición del propietario, Cancelar mediante reversa ahora abre modal antes de enviar:
 folio, proveedor, cuenta destino del reintegro, plantel, referencia, movimiento original,
 importe y motivo vigente. Volver sin cancelar y Escape sólo cierran, conservando el
 motivo y sin enviar POST. Confirmar cancelación usa el POST original con versión/CSRF;
 la reversa financiera y sus validaciones transaccionales no cambian.
- No se ha cancelado COM-00000001 ni ejecutado otra operación por herramientas. La
 aceptación de reversa sigue pendiente del propietario; egreso id4/$406.00 y saldo
 $1,594.00 continúan siendo la última confirmación humana. No duplicar la reversa desde
 Movimientos financieros. Sin nueva migración ni commit; V105 libre.
- Verificado:805 Java y39 Node sin fallos; render condicionado por estado/permiso,
 resumen literal, motivo actualizado, cierre/Escape sin POST y confirmación única.
 Navegador ficticio comprobó modal de406 y Volver conservando motivo/estado. Sólo app
 reconstruida/recreada, salud UP; PostgreSQL/volúmenes intactos y temporales QA retirados.
 Próximo humano: recargar detalle → Gestión, capturar
 motivo, abrir modal y probar Volver sin cancelar; después reabrir y confirmar una sola
 vez. Esperado CANCELADA, ingreso compensatorio406 y saldo2000 si no hubo otros movimientos.

## Compra confirmada por propietario; navegación y acciones — 2026-10-16

- Aceptación humana: COM-00000001, Papelería Prueba Raíces, TICKET-PRUEBA-001,
 movimiento financiero id4, egreso único de $406.00; BANCO-COMPRAS-TEST (cuenta id4)
 pasó de apertura $2,000.00 a saldo $1,594.00. No repetir confirmación ni crear otro
 egreso. Cancelación/reversa todavía NO probada ni aceptada.
- DATO REAL DEL ENSAYO, distinto de la propuesta: cuenta de alcance Institucional y
 movimiento en Plantel prueba de permisos, no Parian. Conservarlo, no corregir por
 herramientas. El segundo plantel ya NO está vacío financieramente: la limitación de
 V104 describe su momento histórico, no el estado actual para futuras pruebas.
- Captura/edición ahora separa Datos de la compra y Productos y servicios. Detalle
 separa Resumen, Productos y servicios y Gestión. Confirmación/cancelación quedan en
 Gestión sin depender de la longitud de partidas. Reutiliza student-tabs.js: hash,
 flechas, panel con errores y accesos directos. Validación HTML abre el panel del primer
 campo inválido; errores del servidor en partidas abren su pestaña.
- Cancelar en captura de compra/proveedor y Ver compra del listado tienen presentación
 de botón común, responsiva, con foco y tema oscuro. No se cambian rutas, permisos,
 cálculos, movimientos ni comportamiento financiero. Guardar sigue sólo en borrador.
- Verificado:805 Java y38 Node sin fallos, render real de captura y detalle en tres
 estados/con y sin permiso. Navegador ficticio confirmó pestañas, Gestión accesible,
 botones y validación de descripción oculta abriendo/focalizando Productos. Cancelación
 distribuida en dos columnas y botón sin texto apilado, móvil en una columna. Sólo app
 reconstruida/recreada; PostgreSQL/volúmenes intactos. Sin nueva migración ni commit; V105 libre.
 Próximo: propietario revisa diseño y abre Gestión de la compra ya confirmada. Después
 ensayo guiado de cancelación con motivo, esperando reversa $406.00 y saldo $2,000.00
 si no hubo otros movimientos. No ejecutarlo ni documentarlo como aceptado por herramientas.

## Detalle de compra: contador legible y confirmación del egreso — 2026-10-16

- Propietario guardó el borrador del ejemplo y llegó al detalle con dos partidas;
 todavía NO confirmó ni registró egreso. No atribuir aceptación de afectación bancaria.
- Contador de partidas usa clase propia con contraste explícito en claro/oscuro y
 etiqueta accesible. Conserva el número de partidas, no suma cantidades de productos.
- Confirmar y registrar egreso abre resumen común con folio, proveedor, cuenta,
 plantel, categoría, referencia, partidas y subtotal/impuestos/total. Cancelar o Escape
 sólo cierran el modal; aceptar envía el POST original con versión y CSRF. El servicio
 mantiene validaciones transaccionales; no se registran movimientos por herramientas.
- Verificado:805 Java y36 Node sin fallos, plantilla real en borrador/confirmada con y
 sin permiso, modal con importes/partidas y Cancelar/Escape sin envío. Navegador ficticio
 confirma contador legible claro/oscuro y resumen completo. Sólo app reconstruida/recreada;
 PostgreSQL/volúmenes intactos, temporales QA retirados. Sin migración nueva, .env ni commit. Próximo humano:
 recargar detalle, revisar contador y abrir/cancelar el resumen; sólo después continuar
 confirmación guiada de $406.00 en la cuenta de prueba (esperado $2,000.00 → $1,594.00).

## Compras: folio de alta, búsquedas remotas y confirmación — 2026-10-16

- El propietario inició el ejemplo de compra ficticia de $406.00 y reportó restricción
 de integridad al guardar. Logs: SQLState22001, value too long for character varying(40).
 Causa: TEMP- más UUID con guiones medía41; ahora usa UUID completo sin guiones (37),
 conserva unicidad y después asigna COM- más ID. No se amplía ni modifica V50 aplicada.
- Captura/edición de compras usa tres autocompletados remotos: proveedores activos,
 cuentas activas compatibles con institución/plantel y motivos EGRESO/AMBOS. Diez
 resultados al enfocar y búsqueda desde tres caracteres, orden estable y página11 para
 detectar más coincidencias. No carga catálogos completos en el formulario; edición y
 errores recuperan sólo las etiquetas seleccionadas mediante IDs autorizados. El listado
 conserva sus filtros existentes. Endpoints propios exigen COMPRA_ADMINISTRAR y alcance.
- Proveedores (alta/edición) y borrador de compra muestran confirmación común con resumen
 actualizado, Cancelar y guardar explícito. Compras incluye proveedor/cuenta/categoría,
 fecha dd/MM/yyyy, referencia, notas, partidas, subtotal, impuestos y total; deja claro
 que guardar NO mueve dinero. Se mantienen POST/CSRF y validación del servidor.
- Cantidades conservan hasta tres decimales por recomendación para horas, metros o kilos;
 ayuda visible distingue enteros para piezas. Cálculo visual por partida usa centavos
 exactos y el mismo redondeo HALF_UP del servidor, sin diferencias de suma de impuestos.
- Verificado: 804 pruebas Java y35 Node sin fallos, render Thymeleaf real de alta y
 edición, permisos HTTP de los tres endpoints, alcance/paginación y cálculos/resúmenes.
 QA visual en navegador con formularios ficticios: modales claro/oscuro y Cancelar
 conserva captura. Sólo app reconstruida/recreada, salud UP y104 migraciones validadas;
 PostgreSQL/volúmenes intactos.
 No publicar aún la prueba operativa como confirmada: no se crean proveedores, compras,
 cuentas ni movimientos por herramientas. No se lee .env ni se hace commit. Sin
 migración nueva: V105 continúa disponible. Temporales de QA retirados; fixtures en target.
- Próximo propietario: recargar y guardar el borrador del ejemplo, probar Cancelar en
 ambos resúmenes y búsquedas/foco/cambio de plantel. Esperado subtotal350, impuestos56,
 total406 y cuenta de prueba todavía2000. Después confirmar/cancelar de forma guiada;
 no repetir altas ya guardadas ni tocar BBVA_COBROS, cajas o el segundo plantel vacío.

## Guía confirmada de permisos y alcance de reportes — V104 — 2026-10-16

- Propietario repitió URLs completas del concentrado para institución1/plantel2 con
 prueba.reportes.parian: Excel y PDF ya muestran No tienes acceso a esta información,
 no500. Después confirmó caso permitido de Parian, Concepto de cobro y corte16/10/2026:
 pantalla, Resumen y Concentrado del Excel y PDF coinciden. Tres cargos, importe
 $2,100.00, cobrado $500.00, por cobrar $1,600.00 y vencido $0.00, MXN; dos filas:
 ANTICIPO-TEST-02 (2/$1,600.00 pendiente) y MATERIAL (1/$500.00 pagado).
- V104 incorpora guía reportes-permisos-alcance CONFIRMADA con nueve etapas: preparación
 controlada, cuenta sin módulo, cuenta limitada, filtro/consulta, rechazo de exportaciones
 y caso autorizado en pantalla/Excel/PDF. Conserva V1–V103 y actualiza referencia desde
 concentrado. Sólo contenido editorial; no crea ni modifica usuarios, roles, planteles,
 cargos, pagos ni movimientos. Los permisos asociados limitan lectura de la guía al
 administrador que puede seguirla; no se otorgan derechos a ningún rol.
- Verificado SQL con BEGIN/ROLLBACK, 796 pruebas Java y diff sin errores. App
 reconstruida/recreada, salud UP y V104 aplicada: guía CONFIRMADA, nueve etapas,
 34 guías / 288 etapas totales. PostgreSQL no reiniciado; sin lectura de .env,
 cambios operativos ni commit automático. Siguiente migración disponible V105.
- Limitación explícita: segundo plantel vacío. Aceptación de filtro y rechazo por ID,
 no separación de registros financieros de dos planteles con movimientos, otros reportes,
 otras instituciones o grandes volúmenes. Los errores400 por enlaces incompletos y
 el500 inicial son antecedentes, NO resultados aceptados. El reensayo correcto sí lo es.
- Próximo: el propietario consulta la guía con su administrador; no necesita volver
 a crear cuentas ni repetir operaciones. Acordar después el siguiente ensayo adicional;
 no generar datos en el segundo plantel automáticamente. Conservar pruebas y cambios
 de SalidaArchivoHttp pendientes de commit del propietario.

## Permisos por plantel: exportaciones rechazadas terminaban en 500 — 2026-10-16

- Propietario confirmó prueba.sinreportes: menú sólo Guía de procesos y rechazo de
 Tesorería directa y Excel/PDF del concentrado sin permiso del módulo. No se cambió
 su cuenta por herramientas. Después creó prueba.reportes.parian con Reportes
 financieros y alcance sólo Parian, y el plantel de prueba CAMPUS-PRUEBA-02 (id2),
 institución id1. No hay datos financieros del segundo plantel; no atribuir prueba
 de separación de registros reales entre planteles, sólo filtro y rechazo explícito.
- Confirmado por propietario: filtro sólo ofrece Parian; consulta manipulada con
 plantelId=2 rechaza derechos. Primeras URLs Excel/PDF incompletas devolvieron400
 por faltar institucionId: NO son prueba de alcance. URLs completas con institución1,
 plantel2 y corte 16/10/2026 devolvieron500; repetir después de esta corrección.
- Causa comprobada en logs: getOutputStream() has already been called for this response.
 El controlador abría la salida binaria y fijaba cabeceras antes de que el servicio
 validara alcance; al denegar, ManejadorGlobalExcepciones intentaba renderizar HTML
 mediante getWriter. La autorización no se relaja: se corrige el transporte del error.
- SalidaArchivoHttp abre stream/cabeceras sólo al primer byte. Las ocho exportaciones
 de ReporteFinancieroAdminController lo usan; errores previos conservan HTML/403 sin
 Content-Disposition ni descarga. Escritura directa al servlet, sin acumular el archivo
 completo en memoria; flush/close sin bytes no abren respuesta. Excel autorizado sigue
 attachment y PDF inline, no-store; servicios mantienen validaciones y paginación.
- Regresión HTTP con Tomcat/seguridad/plantilla/advice reales y servicios simulados:
 los ocho rechazos deben ser HTML403 sin descarga, los ocho casos autorizados conservan
 tipo/disposición/cuerpo. Usuarios sólo en memoria, sin consultas a BD ni datos reales.
- Verificado 794 Java sin fallos, incluida la regresión HTTP de los dieciséis casos,
 y git diff --check. Sólo app reconstruida/recreada, salud UP; PostgreSQL no reiniciado.
 Sin migración, cambios financieros, lectura de .env ni commit. V104 sigue disponible.
- Próximo propietario: con prueba.reportes.parian, repetir Excel y PDF del concentrado
 con institucionId=1, plantelId=2, agrupacion=CONCEPTO, fechaCorte=2026-10-16. Ambos
 deben mostrar No tienes acceso a esta información, no500 ni archivo. Después volver
 a consultar/exportar Parian para comprobar acceso permitido. No modificar permisos
 ni crear pagos/cargos para completar el ensayo. Guía integrada pendiente hasta terminar
 el ejemplo por petición del propietario; no publicar aceptación aún no recibida.

## Primer acceso confirmado; preparación verificada y Usuarios con pestañas — 2026-10-16

- ACEPTACIÓN MÁS RECIENTE: después de publicar la preparación verificada, el propietario
 respondió «si ya se corrigio» al ciclo recargar/entrar/cerrar/volver a entrar una vez.
 Esto sustituye el pendiente del primer acceso de las entradas anteriores. No confundir
 con aceptación de permisos financieros por URL, que todavía no ha realizado.

- Propietario volvió a confirmar fallo al primer login después de logout; segundo
 funciona. La corrección de la entrada anterior NO quedó aceptada. No atribuir ahora
 causa definitiva al navegador sin su secuencia real: el ciclo aislado sí funcionó.
- Añadido LoginCookieHttpTest: Tomcat y cookies HTTP reales, seguridad/plantillas
 reales y cuentas sólo en memoria; nueve ciclos por tres portales. Instancia opcional
 qa.browser únicamente de prueba en localhost:18082, sin PostgreSQL ni .env. Navegador
 aislado reprodujo salida y primer acceso correctamente incluso antes del refuerzo nuevo.
- Nuevo POST /acceso/comprobar público pero protegido por CSRF, 204 sólo al pasar
 su filtro; token inválido/cookie ausente o caducada devuelve409/no-store, sin crear
 sesión ni autenticar. login-session.js hace GET de token y esta comprobación ANTES
 de enviar credenciales. Ante409 sólo repite preparación, hasta tres intentos; nunca
 reenvía usuario/contraseña. Envío final nativo único sin segundo evento submit,
 conservando validación HTML; script versionado en los tres accesos.
- Diagnóstico del rechazo sólo registra booleanos: fase, cookie de sesión presente,
 sesión válida/existente, campo/cabecera CSRF presentes. No valores de tokens/cookies,
 identificadores de sesión, nombres, contraseñas ni cuerpo del formulario. Si sigue
 fallando, pedir al propietario un ciclo y revisar esas líneas acotadas, no logs con
 credenciales ni desactivar CSRF. No dar por resuelto hasta aceptación humana.
- Usuarios ahora tiene pestañas Identidad, Autorización y Estado y activación, mediante
 student-tabs.js y estilos comunes. Paso3 separa tarjetas de estado y contraseña/
 invitación, con márgenes, ajuste móvil y enlace en bloque aparte. Sin acciones
 superpuestas. Alta mantiene identidad primero; roles e invitación requieren registro.
 Errores de asignación abren Autorización; gestión/errores de cuenta abren Activación.
 Hashes conservan sección tras asignar rol o generar invitación/recuperación.
- Verificado 791 Java / 28 Node, cookies HTTP reales y navegador aislado: acceso y
 primer reingreso con cuenta ficticia en memoria correctos. QA visual final en claro/
 oscuro y móvil390/escritorio1280: tres pestañas visibles, tarjetas y enlace sin solapar;
 se corrigieron caché de CSS y prioridad de estilos móviles, sin ocultar navegación.
 App reconstruida/recreada y salud UP; temporales QA retirados. Sin migración, V104
 libre; no lectura de .env, escrituras financieras, usuarios/permisos reales ni commit.
- Próximo humano: recargar Usuarios y revisar pestañas/paso3. Después continuar rutas
 financieras prohibidas con prueba.sinreportes; menú restringido confirmado, rutas no.
 Si vuelve a fallar el login, capturar diagnóstico de fase sin pedir contraseñas.

## Logout y primer acceso: respuesta privada atrasada; orden de Usuarios — 2026-10-16

- Propietario creó prueba.sinreportes, ingresó y confirmó que únicamente ve Guía de
 procesos. NO confirmó rechazo por rutas directas/Excel/PDF ni alcance multiplantel.
 No modificar roles ni usuarios por herramientas para completar esa prueba.
- Reportó que cerrar sesión y volver a entrar sigue produciendo accesoActualizado
 en el primer intento, aunque el segundo funciona. Las correcciones anteriores no
 bastaron. Ciclo HTTP secuencial con datos ficticios no falló; no confundirlo con
 reproducción exacta de su navegador. Se reprodujo una carrera adicional: solicitud
 privada autorizada antes de logout, sesión invalidada antes de renderizar th:action,
 token CSRF diferido crea OTRA sesión desde la respuesta atrasada. Regresión falló
 con el repositorio predeterminado; los filtros de entrada no cubrían esta fase.
- SesionCsrfTokenRepository conserva tokens en sesión, generación aleatoria estándar,
 enmascarado y comprobación normales de Spring Security. Sólo GET/acceso/csrf,
 /activar-cuenta y /restablecer-password pueden crear una sesión anónima para CSRF.
 Una respuesta privada que pierde su sesión nunca pide otra al guardar el token.
 Revocar tokens no crea sesiones. No omite CSRF ni reenvía contraseñas.
- NullRequestCache evita que peticiones privadas antiguas creen sesiones para guardar
 destinos; successHandler ya determina admin/familias/maestros. No afecta sus destinos.
 session-clock.js detiene sondeos/renovación al submit de logout, salida del modal y
 pagehide; aborta consulta en curso e ignora respuesta posterior. pageshow persisted
 reanuda consulta para comprobar sesión al volver. Mantener redirección del portal propio.
- Usuarios conserva alta en dos tiempos: crear identidad, después asignar roles/alcance.
 Orden de edición: Paso 1 Identidad → Paso 2 Autorización → Paso 3 Estado y activación.
 Generar invitación/recuperación y sus enlaces quedan al final, no antes de los roles.
 Redirect tras generar termina #activacion para no perder el enlace. Se preservan
 validaciones, POST/CSRF, permisos, estados y expiraciones; no se autoasignan roles
 ni se impone nueva regla de negocio que bloquee invitaciones sin roles.
- Regresiones: vista privada invalidada durante render no crea sesión y el siguiente
 login válido entra con un POST; nueve ciclos login/logout en tres portales con usuarios
 en memoria, rotación de ID y consulta401 sin sesión; repositorio y JS abortado/salida.
 Plantilla Usuarios renderizada INVITADO/ACTIVO/INACTIVO, orden y enlaces comprobados.
 QA visual de datos ficticios en escritorio/móvil390 y claro/oscuro; no usuarios reales.
- Verificado 789 Java / 28 Node sin fallos y diff limpio. App reconstruida/recreada,
 salud UP; HTTP real con usuario inexistente, tres ciclos: primer POST llega a error
 de credenciales esperado, no accesoActualizado. Esto NO confirma login humano válido.
 Flyway permanece V103, V104 libre. PostgreSQL no reiniciado, sin operaciones financieras,
 lectura de .env ni commit. Servidor/contenedor/etiqueta de QA temporal retirados;
 pantallas ficticias conservadas en target ignorado. Contraste oscuro del enlace mejorado.
- Pendiente propietario: recargar login y vistas privadas para cargar scripts nuevos;
 entrar, cerrar sesión, volver a entrar una sola vez (repetir un par de ciclos).
 La aceptación humana del primer intento aún NO está confirmada. Si persiste,
 capturar secuencia/portal/pestañas sin contraseñas: no dar por resuelto sólo porque
 el segundo intento funciona. Después retomar prueba de enlaces financieros prohibidos.

## Cortes históricos de alumno y cobranza confirmados — V103 — 2026-10-16

- Propietario confirmó estado de ALUM-2026-002 con Situación Todas y corte 15/10/2026
 en pantalla, Excel y PDF: únicamente noviembre/diciembre, dos cargos de $800.00,
 importe/saldo $1,600.00 y aplicado $0.00. Original $1,000.00 y ajuste -$200.00 por
 cargo; emisión 10/10, vencimientos 20/11 y 20/12, fechas dd/mm/yyyy. Material emitido
 el 16/10 excluido. No repetir pagos, ajustes ni cancelaciones para recrear el ejemplo.
- Confirmó concentrado histórico por Concepto de cobro al 15/10/2026, institución
 instituto Raices y filtro Plantel Todos autorizados: una fila ANTICIPO-TEST-02,
 2 cargos, importe/por cobrar $1,600.00, cobrado/vencido $0.00, MXN. Coinciden pantalla,
 PDF y las dos hojas Excel Resumen/Concentrado; MATERIAL no aparece.
- V103 agrega cuatro etapas: alumno 6/7 (guía versión 2, siete etapas) y concentrado
 5/6 (versión 3, seis etapas), conservando CONFIRMADA. Actualiza referencias en guías
 de Tesorería y Excel. V1–V102 se preservan; sólo contenido editorial, sin cambios
 de permisos, dominio ni operaciones financieras.
- Verificado SQL con BEGIN/ROLLBACK, 782 pruebas Java y diff sin errores. Sólo app
 reconstruida/recreada, salud UP y V103 aplicada: 33 guías / 279 etapas. Ambas guías
 siguen CONFIRMADAS con las versiones/etapas indicadas. Sin lectura de .env ni commit;
 PostgreSQL no reiniciado. Siguiente migración disponible V104.
- Ronda básica de reportes revisada: Tesorería actual/histórica, cuenta sin movimientos,
 alumno con filtros y corte histórico, concentrado por tres agrupaciones al 16 y por
 concepto al 15. No atribuir aceptación humana de corte histórico por plantel/ciclo,
 permisos restringidos, grandes volúmenes o estados vacíos fuera del ensayo descrito.
- Próxima ronda PROPUESTA: permisos y alcance. Con una cuenta de prueba SIN módulo
 Reportes financieros verificar menú oculto y rechazo de rutas directas/Excel/PDF.
 Después, si hay cuentas y datos de varios planteles, comprobar el alcance de una
 cuenta limitada a un plantel. No retirar permisos al administrador principal ni
 crear usuarios/asignar roles automáticamente. Solicitar al propietario qué cuenta
 controlada puede usar; nunca pedir contraseñas. Primer login tras reinicio continúa
 sin confirmación humana explícita. No publicar la ronda de permisos como confirmada.

## Tesorería histórica confirmada — V102 — 2026-10-16

- Propietario confirmó Tesorería BBVA_COBROS, Plantel de operación Todos, agrupación
 MENSUAL y rango 01/10/2026–15/10/2026 en pantalla, Excel y Balanza PDF: 2 movimientos,
 ingresos/egresos $1,440.00 cada uno, neto/traspasos $0.00, saldo inicial/final $1,000.00.
 El cobro de $500.00 del día 16 queda excluido sin modificar el saldo actual de la cuenta.
- V102 agrega etapas 7 y 8 a reportes-cuentas-tesoreria (versión 2), conserva CONFIRMADA
 y actualiza las referencias que decían pendiente la consulta histórica de Tesorería.
 No extiende esa aceptación al corte histórico del estado del alumno o concentrado.
 V1–V101 permanecen intactas. Sólo guía, sin permisos nuevos ni operaciones financieras.
- Verificado SQL con BEGIN/ROLLBACK, 780 pruebas Java sin fallos y diff sin errores.
 Sólo app reconstruida/recreada y salud UP; V102 aplicada, guía CONFIRMADA versión 2
 con 8 etapas, 33 guías / 275 etapas totales. Sin lectura de .env ni commit automático.
 PostgreSQL no reiniciado; siguiente migración disponible V103.
- Próxima prueba PROPUESTA: Estado de cuenta del alumno ALUM-2026-002, Situación Todas,
 mismo alcance autorizado y Fecha de corte 15/10/2026 → Generar estado. Por los datos
 del ensayo esperamos sólo noviembre/diciembre $800.00 cada uno: 2 cargos, importe y
 saldo $1,600.00, aplicado $0.00. Material de $500.00 tiene emisión 16/10 y no debe
 aparecer al corte del 15. Comparar pantalla y archivos nuevos Excel/PDF antes de
 documentarlo como confirmado; si difiere, revisar fechas efectivas de ajustes/abonos.
 No registrar pagos, cambiar fechas ni cancelar operaciones para forzar cifras.
- El Excel del concentrado por las tres agrupaciones sigue confirmado por V101.
 Primer login real tras reinicio aún sin confirmación explícita. Las notas anteriores
 se conservan debajo como historial y no sustituyen este siguiente paso.

## Excel del concentrado aceptado — V101 — 2026-10-16

- El propietario confirmó pantalla y las dos hojas del Excel (Resumen/Concentrado)
 por concepto, plantel y ciclo escolar. Corte 16/10/2026, institución instituto Raices
 y filtro Plantel Todos autorizados. No afirmar que seleccionó un plantel individual:
 Plantel parian es la fila agrupada del resultado, no el valor del filtro utilizado.
- Las tres agrupaciones coinciden: 3 cargos, importe $2,100.00, cobrado $500.00,
 por cobrar $1,600.00, vencido $0.00, MXN. Concepto: dos filas (colegiatura 2/$1,600.00
 pendiente y material 1/$500.00 pagado). Plantel parian y Ciclo Escolar 2026-2027:
 una fila cada uno, mismos totales.
- V101 confirma reporte-concentrado-excel, versión 2, conservando sus tres etapas.
 Actualiza requisitos, ejemplo, acciones/resultados y referencias desde agrupaciones
 y guías de caja. V100 y sus pruebas se preservan como historial; no editar migraciones
 ya aplicadas. Sólo contenido editorial; sin cambios de dominio, permisos o dinero.
- Verificado: SQL en BEGIN/ROLLBACK, 778 pruebas Java sin fallos y git diff --check.
 Sólo app reconstruida/recreada, salud UP y V101 aplicada: guía CONFIRMADA versión 2,
 totales conservados de 33 guías / 273 etapas. PostgreSQL no reiniciado; sin lectura
 de .env, operaciones financieras ni commit automático. Siguiente migración libre V102.
- Siguiente prueba PROPUESTA, todavía no confirmada: Tesorería, cuenta BBVA_COBROS,
 Plantel de operación Todos, Agrupación MENSUAL, Desde 01/10/2026, Hasta 15/10/2026.
 Esperado por el ensayo previo: dos movimientos (pago/cancelación), ingresos y egresos
 $1,440.00 cada uno, neto $0.00, apertura/cierre $1,000.00; excluir cobro $500.00 del 16.
 Contrastar pantalla, Excel y Balanza PDF. La cuenta actual conserva $1,500.00 si no
 hubo otras operaciones; no confundir saldo actual con saldo al corte histórico.
 No crear pagos, modificar fechas ni repetir cancelaciones para forzar la prueba.
- La aceptación humana de login tras reinicio sigue sin confirmación explícita.
 Las entradas siguientes se conservan como historial; el Excel ya NO está pendiente.

## Reportes confirmados y Excel del concentrado — 2026-10-16

- Estado vigente: propietario confirmó fechas dd/mm/yyyy y filtros PAGADO/PENDIENTE
 en pantalla, Excel y PDF del estado del alumno. ALUM-2026-002: tres cargos por
 $2,100.00, aplicado $500.00, saldo $1,600.00. Material pagado $500.00; noviembre y
 diciembre pendientes $800.00 cada uno. No repetir el cobro de material ni la cancelación.
- Concentrado confirmado en pantalla/PDF al 16/10/2026: concepto dos filas (colegiatura
 2/$1,600.00 sin cobro y material 1/$500.00 cobrado); plantel y ciclo una fila cada uno.
 Totales: 3 cargos, importe $2,100.00, cobrado $500.00, por cobrar $1,600.00, vencido $0.00.
- V100 agrega cuatro guías (18 etapas): cuentas/tesorería, estado del alumno y
 agrupaciones del concentrado CONFIRMADAS; Excel del concentrado PENDIENTE de revisión
 humana. Actualiza enlaces de continuidad de las guías de caja sin repetir operaciones.
 Sólo contenido editorial, permisos de consulta existentes; no crea movimientos ni
 otorga derechos a roles. Los datos del ensayo NO viajan con git pull.
- BBVA_COBROS: apertura $1,000.00, pago y cancelación $1,440.00 cada uno; después
 cobro vigente $500.00. Tesorería 01/10–16/10/2026 confirmada pantalla/Excel/PDF:
 tres movimientos, ingresos $1,940.00, egresos $1,440.00, neto $500.00 y cierre $1,500.00.
 DIARIA dos filas; MENSUAL una. Cuenta CAJA-SOBRANTE-TEST sin movimientos: cierre
 $1,150.00, ingresos/egresos cero, apertura del periodo cero y aviso de inicio dentro
 del mes. El sobrante justificado de $1 no generó ingreso automático.
- Nuevo GET /admin/reportes-financieros/concentrado-cobranza/excel y botón Exportar Excel.
 Conserva institución, plantel, agrupación y corte, con los mismos permisos/alcance.
 ExcelConcentradoCobranzaService usa transacción de sólo lectura REPEATABLE_READ,
 SXSSF por bloques de 100 y estilos reutilizados; exporta todas las páginas, no sólo
 la visible. Hoja inicial Resumen con filtros/totales; Concentrado con filas agrupadas,
 filtros de columna y cabecera fija. Fechas reales dd/mm/yyyy, importes numéricos en
 pesos con centavos; sin cartera conserva resumen y mensaje, no libro vacío.
- Verificaciones: pruebas Java de las tres agrupaciones, más de una página, sin datos,
 acceso denegado, endpoint y render de enlace con filtros; prueba de contenido V100.
 SQL validado con BEGIN/ROLLBACK; XLSX ficticios reabiertos e inspeccionados/renderizados
 con la guía de hojas de cálculo, sin recortes en las tres agrupaciones.
- Compilación completa: 776 pruebas Java sin fallos. Sólo app reconstruida/recreada;
 salud HTTP UP y PostgreSQL no reiniciado. V100 aplicada; 33 guías / 273 etapas.
 No se leyó .env ni se efectuaron operaciones financieras; sin commit automático.
- Siguiente prueba del propietario: recargar Concentrado, corte 16/10/2026 y mismo plantel;
 descargar Excel por CONCEPTO (dos filas), PLANTEL y CICLO (una fila cada uno).
 Comparar Resumen y Concentrado con 3 / $2,100.00 / $500.00 / $1,600.00 / $0.00.
 Sólo tras aceptación humana confirmar la guía Excel mediante una migración posterior.
 La reconstrucción con corte histórico anterior al nuevo pago sigue sin ensayarse.
 La aceptación humana del login tras la última corrección tampoco fue explícita;
 no inferirla de haber retomado reportes. Las notas anteriores son historial, no pendientes vigentes.

## Login público atrasado también reemplazaba cookies — 2026-10-16

- Usuario volvió a admin y sí fue redirigido al acceso al recuperar visibilidad, pero
 primer login SIGUIÓ mostrando accesoActualizado. No confundir detección de caducidad
 confirmada con inicio de sesión aceptado. Chrome mostró ERR_INVALID_RESPONSE al abrir
 /sesion/estado; HTTP sin sesión devuelve401 vacío y appUP, no demuestra caída del servicio.
- NUEVA reproducción exacta con datos ficticios: obtener token y sesión nuevos,
 GET/login?sesionExpirada con cookie antigua genera OTRA sesión por th:action/CSRF
 de la plantilla; su cookie pisa la nueva y POST/login falla accesoActualizado. La
 corrección de GET/POST privados no cubría estas cargas de accesos públicos.
- EXCEPCIÓN deliberada SOLO en login, login-familias y login-maestros: acción se resuelve
 con th:attr (action=@{/login}), no th:action, para no materializar CSRF/sesión al render.
 Campo _csrf vacío explícito y botón inicialmente disabled; login-session.js registra
 controlador y habilita botón. Al Entrar obtiene token actual con GET/acceso/csrf y
 envía una vez por POST nativo. Servidor conserva CSRF normal; token vacío se rechaza.
 JavaScript requerido, noscript lo explica; no enviar credenciales si falla obtención
 del token. No extrapolar excepción a formularios de negocio: éstos usan th:action.
- LoginRenderSinSesionTest ahora usa plantillas/controlador REALES con cadena de
 seguridad: cinco rutas de acceso antiguo no crean sesión, y token permite un solo
 login correcto en cada portal con usuarios sólo en memoria. Mantiene origen y rutas.
 Actualizado contrato estático docente por excepción justificada, no relajación de CSRF.
- Verificado768Java/28Node completos sin fallos. HTTP real confirma cinco accesos200
 sin Set-Cookie tras obtener token; primerPOST con usuario ficticio llega a error de
 credenciales esperado, no accesoActualizado. Navegador real de prueba desde
 login?sesionExpirada verificó habilitación JS y un envío con el mismo resultado.
 No se usaron contraseñas/usuarios reales ni .env. No atribuir aceptación humana todavía.
- Sólo app reconstruida/recreada, saludUP; sin migración/operaciones financieras,V100
 libre. Preservar cambio de fechas del estado de alumno aún sin commit del propietario.
- Próximo: usuario RECARGA login con Cmd+Shift+R e intenta entrar una vez; informar
 si realmente llega a admin. No seguir marcando acceso resuelto sólo por contador o
 redirección. Después volver a fechas/reporte del alumno; no repetir pago500.

## Estado del alumno: fechas dd/MM/yyyy — 2026-10-16

- Usuario confirmó Estado de cuenta de Martha Martinez Rodriguez:3cargos, total2100,
 aplicado500 y pendiente1600. Material500 pagado; noviembre/diciembre800 cada uno,
 sin abonos después de cancelar PAG-20261009-000002. PDF/Excel coinciden numéricamente.
 Solicitó uniformar fechas: Excel mostraba ISO y pantalla mezclaba ISO/año corto.
- Pantalla ahora formatea fecha de corte/emisión/vencimiento explícitamente dd/MM/yyyy.
 Mantiene ISO para input type=date y parámetros HTTP, sin cambiar interpretación de fechas.
- ExcelReporteFinancieroService.estadoCuenta exporta emisión/vencimiento como fechas
 reales de Excel con estilo dd/mm/yyyy (año4dígitos), no cadenas ISO. Reutiliza estilo
 y mantiene streaming/bloques100/importes/filtros/permisos. Otros reportes no modificados.
- PDF ya usaba dd/MM/yyyy; no se cambió Jasper. Regresión comprueba corte/vencimientos,
 totales y PNG del PDF generado con datos ficticios revisado sin recortes.
- Verificado767Java completos sin fallos: fechas XLSX reabiertas/valores y visualización
 en dos bloques, render de pantalla e ISO de transporte, PDF real. No operaciones
 financieras ni consultas de credenciales/.env. Sin migración; V100 sigue libre.
- App reconstruida/recreada/salud UP; temporales Docker QA retirados. Próximo usuario:
 recargar Estado de cuenta del alumno y descargar Excel nuevo; ver16/10/2026,
 20/10/2026,20/11/2026,20/12/2026. Confirmación visual del cambio pendiente. No repetir
 cobro500 ni cancelar de nuevo; luego acordar siguiente ensayo/actualización de guía.

## Reinicio: redirección automática y carrera de cookies reproducida/corregida — 2026-10-16

- Usuario reportó que la vista privada seguía abierta tras reiniciar y que el primer
 login devolvía Acceso actualizado. La corrección anterior no cubría solicitudes
 privadas atrasadas; no dar ese caso por confirmado por sólo ver cambio de interfaz.
- REPRODUCIDO HTTP con datos ficticios: GET/acceso/csrf crea sesión/token nuevos;
 GET/admin con cookie anterior emitía otra JSESSIONID desde redirigir(getSession(true));
 al sobrescribir la cookie nueva, POST/login rechazaba el token con accesoActualizado.
 Se retiró creación/borrado de cookies en redirecciones caducadas. No basta reintentar
 login ni borrar cookies desde esas respuestas; podrían llegar después de otro acceso.
- SesionInvalidaPostFilter antes de CsrfFilter rechaza envíos con ID inválido y sin
 sesión existente, sin crear otra cookie. Login viejo redirige al acceso propio;
 renovar caducada devuelve401. Sesiones existentes siguen por CSRF normal; no concede
 autenticación ni permite registrar acciones. SesionInvalidaFilter conserva GET privado
 y público como antes. Logout válido sigue invalidando y borrando cookies.
- session-clock.js consulta cada5s con pestaña visible, al foco/volver; límite4s para
 no quedar bloqueado si servidor no responde. No renueva actividad por sondeo.
 Confirma401/redirect con otro GET actual antes de salir, y verifica mensajes de cierre
 de otra pestaña en vez de cerrar una sesión nueva por un aviso viejo. Nunca repite
 POST/credenciales; evita redirecciones/broadcast duplicados. Mientras servidor esté
 caído muestra sin conexión; al volver y confirmar401 regresa al acceso de ese portal.
- Verificado764Java/28Node sin fallos, incluidas solicitudes privadas GET/POST sin
 creación de sesión, login único, CSRF/fijación/logout, pérdida sin clic cada5s,
 desconexión/reconexión y mensajes/respuestas anteriores a una nueva sesión.
- QA navegador aislado con script real y backend ficticio confirmó regreso automático
 /admin→/login, /portal→/familias, /maestros→/maestros/acceso, con sesionExpirada.
 Login real abierto antes del reinicio, envío de usuario inexistente una vez llegó
 a error de credenciales (esperado para datos ficticios), no accesoActualizado.
 HTTP real repitió carreraGET/POST:302 sin Set-Cookie y primer login llegó a validar
 credenciales. No se usaron usuarios/contraseñas reales ni se leyó .env.
- App reconstruida/recreada/salud UP; servidor de QA detenido. Sin migración ni
 operaciones financieras, V100 libre. Preservar cambios de filtro Alumno pendientes.
- Próximo usuario: RECARGAR UNA VEZ para cargar detector nuevo (las vistas ya abiertas
 conservan JS anterior), probar reinicio y acceso normal en un intento. Detección
 periódica, no push instantáneo; servidor debe responder y pestaña activa o retomada.
 Después retomar Pagos recibidos→Martha→Aplicar filtros y estado del alumno; no repetir500.

## Pagos recibidos: filtro por alumno y pruebas de tesorería — 2026-10-16

- Propietario confirmó Tesorería de CAJA-SOBRANTE-TEST sin movimientos en pantalla/
 Excel/PDF. También BBVA_COBROS,01/10/2026–16/10/2026: apertura1000, pago1440 y
 cancelación PAG-20261009-000002/1440, cierre1000. Después registró por sí mismo nuevo
 pago500 para Martha Martinez Rodriguez, cuenta1500. Confirmó Tesorería diaria2filas
 y mensual1fila/3movimientos/ingresos1940/egresos1440/neto500/traspasos0; Excel/PDF
 coinciden, PDF apertura1000/cierre1500 y $0.00 de traspasos completo, sin recorte.
 FechaHasta16 fue filtro elegido, no defecto. No confundir pagos con movimientos:
 cancelación es movimiento reverso del mismo pago, no un segundo pago recibido.
- Solicitó filtro Alumno en Pagos recibidos antes de seguir revisando su estado de
 cuenta. Implementado autocompletado por nombre/matrícula, selección ID y botón quitar;
 foco hasta10 sugerencias, desde3 caracteres hasta20, más1 para detectar hayMas. Usa
 columna generada/indexada de V8, sólo lectura, sin COUNT ni cargar todos los alumnos.
- Endpoint /admin/autocompletado/alumnos-pagos exige PAGO_LEER (no permisos de alumnos
 ni reportes). AlcanceDatosService limita sugerencias a alumnos con pagos consultables
 de la institución/planteles de la sesión; incluye alumnos inactivos con historial.
- FiltroAlumnoPago usa EXISTS sobre solicitudes, aplicaciones históricas y cargos de
 anticipos. Incluye pendientes/cancelados según Estado seleccionado; un pago de varios
 alumnos aparece una vez por consulta, conservando su importe total. No se atribuye
 todo el importe al alumno ni se filtra sólo por tutor. Mantiene alcance normal de Pagos.
- alumnoId/alumnoTexto se conservan en listado/normalización/paginación/Excel por bloques
 y retorno de detalle. Si escriben nombre sin seleccionar sugerencia, error visible,
 no consulta silenciosa de todos los pagos. Ayuda contextual específica y diseño
 claro/oscuro responsive; mensaje vacío no excluye por error a alumnos inactivos.
- Verificado764Java/28Node, incluidos EXISTS, límites/escape, alcance institucional/
 plantel, permiso PAGO_LEER, render/paginación/retorno y Excel multipágina. QA visual
 de página renderizada con datos ficticios en escritorio/móvil390 y claro/oscuro; no
 sesión autenticada ni pruebas de búsqueda sobre registros reales por herramientas.
 App reconstruida/recreada, salud UP; temporales Docker QA retirados. Sin migración,
 cambios financieros ni lectura .env. V100 libre; el usuario realiza commit/push.
- Próximo usuario: Pagos recibidos → Alumno → escribir Martha, seleccionar sugerencia
 y Aplicar filtros, revisar detalle/volver/paginación/Excel. No arrastrar cobertura anual
 o concepto de otro ensayo: usar Todos/todas las coberturas si busca su historial completo.
 Después retomar Estado de cuenta del alumno Martha para verificar aplicación del nuevo
 pago500 y efecto del pago cancelado en sus adeudos; aún no confirmado ese reporte.
 No repetir cobro500 ni publicar guía adicional automáticamente.

## Acceso tras reiniciar: cookie antigua y token CSRF — 2026-10-16

- Propietario reportó primer intento Acceso actualizado y después La sesión volvió a
 cambiar durante el acceso. No asumir contraseña incorrecta ni usuario bloqueado.
- El invalidSessionStrategy global interceptaba incluso GET públicos (login, JS/CSS,
 /acceso/csrf). HTTP con cookie ficticia anterior reprodujo302 en /acceso/csrf; scripts
 o token podían recibir HTML de login en lugar de su contenido. Además redirigir borraba
 JSESSIONID aunque CSRF ya hubiera generado una sesión anónima nueva.
- Sustituido por SesionInvalidaFilter después de AnonymousAuthenticationFilter: deja
 continuar accesos públicos GET/HEAD y recursos; las páginas privadas con cookie inválida
 siguen redirigiendo al acceso de su portal con sesionExpirada. Reloj caducado responde401
 sin crear sesión. La autorización de Spring Security y CSRF siguen activos.
- Redirigir establece/conserva sesión anónima nueva en vez de borrarle la cookie; no
 recupera autenticación previa. Logout conserva POST/CSRF, invalidación y borrado de cookies.
 Login JS conserva actualización previa de token y un solo envío de credenciales; no
 hay reenvío automático de contraseñas ni relajación de permisos.
- Regresión con cadena de seguridad completa y usuarios exclusivamente en memoria:
 tres portales, cookie antigua, token200/no-store, un intento de autenticación, rotación
 del identificador de sesión, rechazo sin CSRF y logout.756Java y28Node aprobados.
- App reconstruida/recreada, salud UP. HTTP real con cookie FICTICIA: tres logins,
 JS/CSS/token200; /admin, /portal y /maestros302; /sesion/estado401. No se usaron
 credenciales reales ni se leyó .env; sin migración ni operaciones financieras.
- Pendiente usuario: dejar acceso abierto, reiniciar la app, capturar credenciales y
 pulsar Entrar una sola vez (repetir en portales que use). No atribuir aceptación humana
 todavía. Después retomar revisión visual de reportes y pruebas financieras acordadas.
 V100 libre; conservar cambios previos de reportes/exportaciones y documentación.

## Navegación de reportes y aviso de apertura — 2026-10-16

- Propietario confirmó que Excel y PDF de Estado de cuenta por cuenta ya muestran la
 información del caso CAJA-SOBRANTE-TEST/octubre2026 sin movimientos. Esta aceptación
 sustituye la pendiente de la entrada anterior; no implica validar reportes con movimientos.
- Los cuatro reportes comparten ahora el fragmento reportes-navegacion: título visible
 Selecciona un reporte, cuatro opciones con descripción, bordes y selección destacada,
 aria-current de página y foco visible. Conserva rutas y permisos; no son paneles JS.
 Dos columnas en pantallas estrechas, sin ocultar opciones por desplazamiento horizontal.
- Aviso Cuenta iniciada en este periodo alineado con las métricas y el panel, icono
 informativo no interactivo, título separado del texto e importe destacado. Claro/oscuro.
 No altera cálculo ni trata inicial1150 como ingreso.
- Verificado755 pruebas Java sin fallos, incluida renderización de los cuatro estados de
 navegación; revisión visual de muestras estáticas con CSS real en escritorio, móvil390
 y oscuro, no sesión autenticada. Sólo app reconstruida/recreada, salud UP.
 Sin migración ni cambios financieros, V100 sigue libre. Preservar cambios previos de
 exportaciones. Próximo: usuario recarga reportes y confirma navegación/aviso; después
 acordar ensayo de reporte de una cuenta con movimientos existentes y comparar pantalla,
 Excel/PDF. No crear movimientos, reabrir cortes ni publicar guía adicional automáticamente.

## Estado de cuenta sin movimientos: Excel y PDF — 2026-10-16

- Propietario encontró el reporte desde la pestaña superior de Reportes financieros;
 la ruta directa /admin/reportes-financieros/estado-cuenta-cuenta también funciona.
 Confirmó en pantalla cierre1150/ingresos0/egresos0 para CAJA-SOBRANTE-TEST, pero reportó
 Excel/PDF vacíos. El ensayo de comparación de exportaciones sigue pendiente de aceptación.
- Regresión reprodujo que Excel abría Estado de cuenta aunque Resumen estuviera primero:
 setSheetOrder no cambia la hoja activa. Ahora activa/selecciona Resumen explícitamente,
 detalle vacío explica Sin movimientos registrados en este periodo, fechas dd/MM/yyyy
 e importes numéricos con formato de pesos. Conserva consulta/filtros y streaming por bloques.
- Jasper compartido ahora imprime cabecera/resumen/pie sin filas (ALL_SECTIONS_NO_DETAIL)
 y un estado vacío explícito. Estado por cuenta añade explicación del saldo inicial
 registrado dentro del periodo:1150 no es ingreso por movimiento; apertura de octubre0
 y cierre1150 son compatibles con una cuenta iniciada16/10/2026. No alterar saldos.
- Verificado754Java completos sin fallos, incluido XLSX reabierto/hoja activa/cifras y
 PDF real de una página con periodo/cuenta/importes/estado vacío. PNG revisado visualmente
 sin recortes. Temporales Docker de revisión retirados; sin QA visual autenticada.
- Sólo presentación/exportación, sin migración ni operaciones financieras. V100 libre,
 app reconstruida/recreada y salud UP; no se modificaron PostgreSQL ni volúmenes.
 preservar V1–V99. Próximo: recargar reporte y descargar NUEVOS Excel/PDF de octubre;
 contrastar cierre1150/ingresos0/egresos0 y resumen de apertura, sin crear movimientos.
 No publicar la prueba de reportes como CONFIRMADA hasta que el propietario compruebe ambos.

## ESTADO VIGENTE — sobrante justificado confirmado, guía V99 — 2026-10-16

- Propietario confirmó rechazo sin justificación y cierre al capturarla; también confirmó
 saldo actual de CAJA-SOBRANTE-TEST1150 después del cierre. Lectura de esta BD verifica
 corte1 CERRADO, inicial/esperado1150, declarado1151, diferencia+1, cero movimientos,
 ingresos/egresos0 y motivo conservado. Caso CONCLUIDO: no repetir apertura/cierre ni
 registrar ingreso1. No confundir con CAJA-CORTE-TEST ni sus registros de otra computadora.
- V99 publica corte-caja-sobrante-1 CONFIRMADA8etapas/4acciones y5 permisos existentes
 requeridos sin otorgar roles. Incluye preparación de cuenta nueva1150, usuario admin
 persistido (no recuperación), autocompletado foco10/búsqueda3, apertura/modal/pestañas,
 rechazo, corrección, cierre y cuenta sin ajuste automático. Cancelar modal/Excel son
 controles recomendados, no nuevas aceptaciones humanas atribuidas.
- Actualiza referencias de faltante, cierre sin diferencia y seguridad mediante V99;
 no editar V1–V98. Sólo contenido editorial, sin escrituras financieras por herramientas.
- Verificación final:753Java completos sin fallos, diff limpio y SQL editorial validado
 BEGIN/ROLLBACK. Sólo app reconstruida/recreada, salud UP; lectura confirma V99 aplicada,
 guía CONFIRMADA8etapas y29guías/255etapas. Corte1 conserva CERRADO/1150/1151/+1/0mov.
 No QA visual autenticada ni operaciones financieras. Próxima V100; preservar V1–V99.
- SIGUIENTE PROPUESTO: Reportes financieros → Estado de cuenta por cuenta, misma caja,
 Mensual/octubre2026. Comparar pantalla/Excel/PDF: cierre1150, ingresos/egresos0,
 movimientos0; sobrante del corte no es ingreso. Revisar saldo inicial registrado en
 periodo por separado si la cuenta abrió dentro de octubre. No crear movimientos para
 este primer control. Después contrastar tesorería/agrupaciones y casos con movimientos,
 efectivo frente a banco/tarjeta y reportes de alumno/cobranza. Aún NO confirmados.

## Retoma local y buscador de cajas — 2026-10-16

- Se levantó la versión descargada con volúmenes existentes: V98 aplicada, salud UP,
 750Java/27Node aprobados en esta computadora. No existía CAJA-CORTE-TEST ni sus cortes;
 no asumir que Git transporta los datos de la otra computadora.
- Propietario creó CAJA-SOBRANTE-TEST, tipo CAJA activa, institución1/plantel1,
 saldo inicial1150, sin corte abierto al consultar. Ensayo de sobrante pendiente:
 abrir corte sin movimientos, declarar1151 sin justificación→rechazo; luego justificar,
 cerrar con diferencia+1 y cuenta1150, sin ingreso automático. No operar por herramientas.
- Lista vacía: propietario confirmó sesión criz; comprobación booleana identifica ese
 usuario como recuperación, que no puede operar cortes. En esta BD chris es el usuario
 administrativo institucional; no cambiar permisos ni leer/exponer credenciales.
- Apertura y filtro de cortes optan por refresh-on-focus del autocompletado compartido:
 foco/clic consultan hasta10 cajas permitidas, reabren al volver y buscan desde3 letras,
 sin duplicar petición foco+clic ni perder institución/selección. Sin ampliar alcance.
- Apertura informa explícitamente si la sesión es de recuperación y deshabilita captura
 y envío; evita presentar ese caso como una caja inexistente. Backend conserva controles.
- Sin migración, V99 sigue libre. Sobrante aún NO confirmado ni publicado como guía.
- Verificación final del cambio:751Java/28Node aprobados, incluye render de apertura
 normal/recuperación y DOM de foco/clic/reapertura/institución/búsqueda3. Sólo app
 reconstruida/recreada; salud UP. Sin QA visual autenticada ni operaciones financieras.

## V98 — guía de faltante y recorridos con pestañas — 2026-10-15

- Verificado750Java completos/27Node sin fallos, diff limpio. SQL editorial validado
  BEGIN/ROLLBACK; sólo app recreada/salud UP. Lectura confirma V98 aplicada,
  faltanteCONFIRMADA8etapas y sin-diferencia versión2/10etapas,28guías/247etapas.
  Temporales retirados, sin QA visual autenticada ni operaciones financieras.
  Próxima V99; no editar V1–V98. Corrección de numeración evita interpretar el50
  final de una observación como otra acción de la guía.

- Autorizado actualizar guía: V98 publica corte-caja-faltante-1 CONFIRMADA8etapas/
  4acciones por etapa,4permisos existentes requeridos sin otorgar roles. Relata caso
  confirmado: nuevo corte1150/sin movimientos/declarado1149 sin motivo→rechazo;
  motivo obligatorio→cerrado/diferencia−1/cuenta1150. Saldo inicial de CUENTA sigue1000,
  saldo inicial del NUEVO CORTE1150. No repetir operaciones; no egreso1 automático.
- Actualiza seis etapas de corte-caja-sin-diferencia y dos precauciones por nav
  Resumen/Apertura/Conteo y cierre o Cierre y conciliación, modal Confirmar y abrir
  corte y footer. Quita referencias a faltante pendiente en esa guía y seguridad.
  Cancelar modal/Excel siguen controles recomendados, no aceptación humana adicional.
  Sólo tablas guía; no modifica migraciones anteriores ni finanzas. Próxima V99
  tras aplicar V98. Siguiente PROPUESTO no ejecutado: sobrante1 y después reportes.

## Detalle del corte con pestañas y faltante confirmado — 2026-10-15

- Verificado15Java específicos y27Node sin fallos, diff limpio. Render de página
  completa abierto/cerrado con/sin permiso, error operacional/tab, cero movimientos;
  DOM compartido cubre resumen/hash/teclado/acceso/errores. Sólo app recreada/salud UP,
  temporales retirados; sin QA visual autenticada ni operaciones financieras.

- Usuario confirmó rechazo sin justificación y, tras instrucciones, cierre correcto
  con justificación: esperado1150/declarado1149/diferencia−1/cuenta sigue1150. Caso
  faltante1 CONCLUIDO según respuesta, no repetir cierre ni registrar un egreso1.
  Falta publicar su guía y adaptar pasos de la V97 a pestañas/modal de apertura;
  V98 disponible. Sobrante/reportes siguen sin confirmar.
- Detalle ahora usa exactamente student-tablist/payment-record-tabs + student-tabs.js:
  Resumen inicial con estado/caja/saldo inicial y, cerrado, esperado/declarado/diferencia;
  Apertura con fecha/actor/saldo/folio/notas; Conteo y cierre si abierto o Cierre y
  conciliación si cerrado, con formulario o registro/ingresos/egresos/justificación.
  Nav/hash/teclado/acceso desde Resumen compartidos, sin JS paralelo. Error operativo
  fuerza pestaña cierre y errores de campos la activan automáticamente.
- Footer historial/movimientos fuera de paneles para ambos estados; permisos/formulario/
  POST/CSRF/version/idempotencia/modal se conservan. Abierto no expone esperado/diferencia.
  Cero movimientos muestra Sin movimientos registrados durante este corte en lugar de
  rango invertido. Estilos son contenedores/espaciado locales con variables/temas/móvil
  compartidos, sin cambios de reglas financieras, permisos ni migración.

## Modal antes de abrir corte — 2026-10-15

- Verificado14Java específicos/26Node sin fallos, sintaxis/diff limpios. Render
  conserva POST/clave/campos/escape y botón. DOM valida cancelar/Escape/resumen
  actualizado/confirmación única. Sólo app recreada/salud UP, temporales retirados;
  sin QA visual autenticada ni operaciones financieras por herramientas.

- Abrir corte usa confirmación compartida apertura-caja: institución, caja seleccionada
  y observaciones, Cancelar conserva captura sin POST y Confirmar y abrir corte envía
  una vez. No precalcula saldo de apertura en navegador: servidor toma saldo/folio
  vigentes al confirmar. Validaciones, alcance, usuario, unicidad e idempotencia intactas.
- Cancelar exterior lleva detail-close-button común; navega al historial, no abre ni
  cierra cortes. Sin migración ni finanzas. V98 disponible; V97 aplicada no se edita.
- Prueba de faltante1 en pausa ANTES de nueva apertura; no hay segundo corte confirmado.
  Recargar/iniciar sesión si procede, misma CAJA-CORTE-TEST saldo1150, abrir otro corte
  sólo si no existe ya abierto; luego declarar1149 sin motivo → debe rechazar. No
  repetir movimientos200/50. La guía V97 conserva recorrido de apertura; próxima
  actualización editorial podrá detallar nuevo modal junto al ensayo de diferencia.

## V97 — corte sin diferencia confirmado y guía — 2026-10-15

- Verificado745Java completos/25Node sin fallos, diff limpio. SQL editorial validado
  BEGIN/ROLLBACK; sólo app recreada/salud UP. Lectura confirma V97 aplicada y guía
  CONFIRMADA10etapas;27guías/239etapas. Temporales retirados, sin QA visual autenticada
  ni operaciones financieras por herramientas. Próxima V98; no editar V1–V97.

- Propietario confirmó también Saldo actual1150 en Cuentas financieras. Caso completo
  inicial1000/+200/−50/dos movimientos/declarado1150/diferencia0/cuenta1150 CONCLUIDO.
  No repetir ingreso/egreso/cierre ni seguir solicitando su comprobación pendiente.
- V97 publica guía CONFIRMADA corte-caja-sin-diferencia de10etapas/4acciones y8permisos
  existentes requeridos (sin otorgar roles). Preparación caja/motivos/apertura previa/
  movimientos/modales/conteo/historial/consulta cuenta. Cancelar modal y Excel son
  comprobaciones adicionales recomendadas, no nuevas aceptaciones humanas atribuidas.
  Actualiza precaución final de guía de seguridad; sólo tablas guía, sin finanzas.
- Próximo ensayo PROPUESTO no ejecutado: abrir OTRO corte en misma caja1150, sin nuevos
  movimientos, declarar1149 sin justificación → rechazo/corte abierto; después motivo
  obligatorio → cierre con esperado1150/declarado1149/diferencia−1, cero movimientos,
  cuenta sigue1150 (cierre NO ajusta saldo). No modificar saldo inicial ni registrar
  egreso1 para forzar prueba. Faltante/sobrante/reportes no confirmados aún.
  Próxima migración tras V97: V98; preservar V1–V96 y V97 una vez aplicada.

## Etiqueta de diferencia en historial de cortes — 2026-10-15

- Verificado13Java sin fallos (incluye render cero/positivo/negativo/pendiente),
  diff limpio; sólo app recreada/salud UP, temporales retirados. Sin QA visual
  autenticada ni operaciones financieras por herramientas.

- Columna Resultado pasa a Diferencia del cierre (también data-label móvil).
  Cero muestra Sin diferencia · $0.00; mantiene Esperado debajo. Abierto continúa
  Pendiente, sin revelar esperado; importes y reglas intactos. Sin migración, V97 libre.
  Usuario reiteró resultado del ejemplo correcto; no repetir cierre ni movimientos.

## Resultado del corte cerrado y aceptación humana — 2026-10-15

- Verificado: 12 pruebas Java específicas y 25 Node aprobadas, diff limpio.
  Render cubre diferencia0/faltante/sobrante, importes/notas escapadas/enlaces y
  ausencia del bloque si abierto. Sólo app recreada/salud UP, temporales retirados;
  sin QA visual autenticada ni operaciones financieras por herramientas.

- Usuario confirmó cierre correcto: dos movimientos/folios1–2, ingresos200,
  egresos50, esperado1150, declarado1150, diferencia0, por criz15oct2026 15:53,
  observaciones Prueba de cierre sin diferencia. No repetir cierre ni movimientos.
  No afirmó aún revisión separada del saldo actual en Cuentas financieras.
- Causa visual: detail-grid anidado fuera del selector directo form-card, sin estilos.
  Resultado ahora usa payment-detail-grid/article compartidos: esperado/declarado/
  diferencia destacados, explicación sin diferencia/faltante/sobrante, registro de
  cierre/movimientos/ingreso/egreso separados y notas en detail-section.
  Botones Volver al historial y Ver movimientos de la caja usan estilos comunes,
  responsive/temas heredados y sólo navegación GET. Sin cambios financieros/migración,
  V97 libre; guía de corte aún no publicada. Próximo verificar cuenta1150 y luego
  diferencia controlada1/reportes, sin operar por herramientas.

## Confirmación de cierre de caja — 2026-10-15

- Verificado: 11 pruebas Java de cierre/render/formulario y 25 Node sin fallos,
  sintaxis/diff limpios. Sólo app recreada, salud UP y temporales retirados.
  Sin QA visual autenticada ni cierre/movimientos financieros por herramientas.

- Conciliar y cerrar usa modal compartido cierre-caja: corte/caja/plantel, efectivo
  declarado, justificación y observaciones capturadas. Confirmar y cerrar caja envía
  una vez; Cancelar/Escape conservan captura sin POST ni cierre. Respeta conteo ciego:
  no expone saldo esperado/diferencia antes del cierre; servidor conserva validación,
  justificación obligatoria ante diferencia, permisos, versión e idempotencia.
- Cerrar después es enlace al historial con detail-close-button del sistema, no
  registra ni cancela el corte. Sin cambio financiero/migración, V97 disponible.
- Prueba humana de corte pendiente: reutilizar misma caja/corte, no repetir ingresos
  o egresos ya registrados. Con saldo inicial1000 e ingreso200/egreso50, declarar1150,
  probar Cancelar modal y después confirmar; esperar diferencia0/cuenta1150.

## Motivos remotos y confirmación de movimiento manual — 2026-10-15

- Verificación: suite completa de 739 pruebas Java aprobada; 8 pruebas Java finales
  específicas y 24 pruebas Node aprobadas. Diff sin errores; sólo app actualizada,
  salud UP y temporales retirados. Sin QA visual autenticada ni operaciones financieras.

- Usuario pidió sustituir select largo de Motivo por primeras10/foco y búsqueda3,
  modal en Registrar movimiento conCancelar/resumen y Cancelar formulario como botón.
  Nuevo MotivoMovimientoBusquedaService/endpoint motivos-movimiento: permiso existente
  MOVIMIENTO_FINANCIERO_REGISTRAR+alcance institución, sólo activos compatibles
  INGRESO/EGRESO oAMBOS, busca código/nombre/categoría, DBpage11→10+hayMas, orden
  nombre/ID;2caracteres no consulta. No trae catálogo completo ni crea permisos.
- Form usa autocomplete compartido con data-extra-input=direccion y opt-in refresh
  focus/clic; aborta resultados antiguos y limpia motivo al cambiar institución/tipo.
  IDoculto y texto visible, exige opción válida; tras error servidor reconstruye
  etiqueta de motivo activo/coherente en lugar de confiar texto capturado. Ya no
  llama listarActivos en preparación del formulario. Backendregistrar mantiene
  validación activa/institución/naturaleza e idempotencia existentes.
- Confirmación compartida movimiento-manual resume institución/cuenta/tipo/motivo/
  monto/fecha dd/MM/yyyy/concepto/referencia/tercero y efectoingreso/egreso;Cancelar
  conserva captura sinPOST y confirmar envía una vez. Cancelar exterior usa
  detail-close-button del sistema. Sin migración/finanzas, V97 libre.
- Ejemplo corte1000→+200−50→1150 aún pendiente de confirmar movimientos/cierre.
  Recargar formulario (iniciar sesión si procede), seguir con ingreso200/egreso50
  en misma caja y corte abierto; NO abrir otro corte ni repetir movimientos ya guardados.

## Botón Contar y cerrar en listado de cortes — 2026-10-15

- Verificado10Java de corte/render y22Node sin fallos, diff limpio; render cubre
  abierto/cerrado con/sin permiso. Sólo app recreada/salud UP, temporales retirados;
  sin QA visual autenticada ni operaciones financieras por herramientas.

- Usuario pidió diseño de acción de cortes accesible desde Movimientos financieros.
  Contar y cerrar ahora usa payment-list-button (botón administrativo azul, flecha,
  altura42, foco visible); Ver detalle queda secundario con borde/colores compartidos.
  Conserva href y condición ABIERTO+puedeAdministrar; no ejecuta cierre al pulsar,
  sólo abre detalle. Responsive común sin cambiar permisos ni datos.
- Prueba de caja1000→ingreso200→egreso50→cierre1150 sigue en curso, sin confirmación
  de movimientos/cierre. No cerrar ni abrir otro corte por herramientas. V97 libre.

## Prueba de corte en apertura y observaciones de consulta — 2026-10-15

- Verificado9Java de corte/render y22Node sin fallos, diff limpio. Render observa
  texto escapado/sin formulario y ausencia connull. Sólo app recreada/salud UP,
  temporales retirados, sin QA visual autenticada ni operaciones financieras.

- Usuario aceptó ejemplo de corte: caja nueva CAJA-CORTE-TEST saldo inicial1000,
  motivosING/EGR de prueba, abrir corte ANTES de movimientos, luego ingreso manual200
  y egreso50 → saldo1150; declarar1150/cerrar →2movimientos, ingresos200, egresos50,
  diferencia0, cuenta sigue1150. No duplicar pago alumno200 anterior para este ingreso
  manual; ensayo independiente y exclusivamente datos de prueba. Falta confirmar
  ejecución de movimientos y cierre; no publicar guíaCONFIRMADA ni dar por realizado.
- Usuario está en detalle del corte abierto y pidió diseño de Observaciones de
  apertura. Bloque usa sección/título/tarjeta readonly, márgenes/padding/colores del
  sistema, texto con saltos y ajuste móvil, escapedThymeleaf; no modifica contenido.
- Aclarado: Conteo físico y cierre queda vacío por ahora; primero registrar200/50,
  después declarar1150 y Conciliar y cerrar. No abrir otro corte al recargar ni
  cerrar antes de movimientos. La diferencia controlada1 y reportes quedan después.
  Sin finanzas/migración, V97 libre.

## ESTADO VIGENTE — guía de seguridad V96 y ronda concluida — 2026-10-15

- Verificado734Java/22Node sin fallos, diff limpio; SQL editorial BEGIN/ROLLBACK.
  Sólo app recreada/salud UP. Lectura confirma V96 aplicada, guíaCONFIRMADA12etapas,
  26guías/229etapas. Temporales retirados; sin QA visual autenticada ni operaciones
  financieras por herramientas. Siguiente migración V97, preservar V1–V96 aplicadas.

- Usuario autorizó publicar tres pruebas confirmadas: selección duplicada bloqueada,
  Cancelar modal sin crear pago y mismo folio validado desde dos pestañas con sólo
  un ingreso/abono200 y aviso ámbar. NO seguir marcándolas pendientes ni repetir pagos.
- V96 agrega seguridad-registro-validacion-pagos CONFIRMADA,12etapas/4acciones cada
  una/9permisos existentes requeridos sin asignar roles. Recorrido completo concepto/
  cuota/cargo200/duplicado/modalCancelar/pagoúnico/dospestañas/aviso/cajaS+200.
  Diferencia casos humanosDUP-TEST yDUP-TEST-02 de nuevo códigoilustrativoSEG-PAGO-TEST;
  no inventa folio del segundo ensayo. Actualiza referencias editoriales de siete
  guías previas vía nueva migración, sin tocar V1–V95 ni registros financieros.
- Esta ronda de cobranza queda concluida; no afirmar cobertura de toda combinación.
  SIGUIENTE PROPUESTO, no autorizado/ejecutado aún: validar corte/cierre de caja y
  reportes financieros (efectivo separado de banco/tarjeta, saldo esperado vs contado,
  diferencia controlada y coincidencia con Excel/PDF). Esperar elección del propietario
  antes de realizar nuevas capturas. No implementar más módulos por inferencia.

## Tres pruebas de seguridad confirmadas y aviso ámbar — 2026-10-15

- Verificado23Java de render/controller/validación y22Node sin fallos, diff limpio;
  contraste calculado texto/fondo claro≥6.2 y oscuro≥9.7. Sólo app recreada/salud UP,
  temporales retirados; sin QA visual autenticada ni movimientos financieros.

- Propietario confirmó nueva pruebaDUP-TEST-02: segundo intento mostró Este pago ya
  estaba validado / No se realizó ningún movimiento adicional y cuenta sólo+200.
  Antes ya confirmó duplicado no seleccionable y Cancelar modal sin pago. Las TRES
  pruebas humanas de esta ronda están concluidas, no seguir pidiéndolas pendientes.
  Falta incorporarlas a guía publicada (V96 disponible); no se creó migración en
  este ajuste de diseño. No asumir folio del segundo ensayo ni cancelar pagos200.
- Solicitud visual: aviso repetido cambia azul por ámbar, borde izquierdo destacado,
  icono y textos legibles, variante oscura específica y móvil existente. Sin lógica,
  montos o permisos. Este mensaje es informativo, no otro éxito ni error financiero.

## Validación repetida: aviso sin repetir movimientos — 2026-10-15

- Verificado732Java/22Node sin fallos, diff limpio; pruebas primera/segunda bajo
  mismo lock con un ingreso/abono/save/auditoría y controller sin flash de éxito
  repetido, render aviso condicional role=status. Sólo app recreada/salud UP,
  temporales retirados. Sin QA visual autenticada ni operaciones financieras.

- Usuario confirmó cargo duplicado bloqueado y Cancelar modal sin registrar pago.
  Después validó mismo folio desde dos pestañas y ambas mostraron éxito. Lectura
  acotada deDUP-TEST200 confirmaPAG-20261015-000029 VALIDADO, UN movimiento200 y
  UNA aplicación200. No doble ingreso. No cancelar/reparar/revertir ese pago.
- Causa: servicio bajo lock devolvía pago yaVALIDADO idempotente y controller siempre
  mostraba éxito. Nuevo ResultadoValidacion(pago,yaValidado) se decide tras el mismo
  bloqueo; validar anterior mantiene contrato idempotente para otros consumidores.
  Controller usa validarConResultado: primera mensaje habitual, repetición aviso
  Este pago ya fue validado desde otra pestaña o por otro usuario. No se realizó
  ningún movimiento adicional. Flashinformativo con diseño propio/temas/móvil, no rojo.
- No cambios a montos/abonos/roles/esquema. V96 libre. Finanzas prueba dos pestañas
  comprobadas por lectura; aviso nuevo pendiente de confirmación humana. No publicar
  guía de seguridad completa comoCONFIRMADA todavía. Si ya no queda pestaña antigua
  pendiente, para repetir haría falta OTRO ensayo200; no reutilizar ni cancelar pago29.

## Evitar cargos duplicados durante captura de pagos — 2026-10-15

- Verificado727Java completos y15Java finales de buscador/saldo/exclusiones,22Node
  sin fallos; DOM cubre excluir21/filtrar respuesta/liberar/rechazar opción vieja
  sin aumentar200. Servicio prueba permisos/deduplicación/límite, SQLbase igual.
  JS/diff limpios, sólo app recreada/salud UP, temporales retirados. Sin QA visual
  autenticada ni operaciones financieras. V96 libre.

- Usuario probóDUP-TEST200 y sí podía seleccionar mismo cargo dos veces; pidió
  corregir antes de guardar. No hay pago/validación nueva confirmada por esta prueba.
- Pago-form envía excluirIds de cargos ya seleccionados; endpoint opcional recibe
  lista y servicio deduplica/omite null con máximo200. Repositorio excluyeNOT IN antes
  de ORDER/LIMIT, conserva institución/tutor/vínculo/estado/saldo; sin exclusiones usa
  consulta previa. Constante SQL base compartida evita divergencia de reglas.
- UI filtra también respuesta y revalida al pulsar una opción antigua; avisa Este
  adeudo ya está incluido en el pago sin asignar importe ni aumentar total. Limpiar/
  quitar fila libera ID para próximas consultas. Validación backend de guardar ya
  existente permanece. Sin migración/finanzas, V96 libre; pruebas humanas pendientes.
- Seguir: recargar Nuevo registro, mismoDUP-TEST200 una vez, Agregar cargo y buscarlo
  otra vez: ya no debe aparecer. Quitar fila vacía y dejar sólo200; después probar
  Cancelar modal sin guardar y usar un único pago200 para validación dos pestañas.

## Confirmación al crear conceptos de cobro — 2026-10-15

- Verificado726Java/22Node sin fallos, JS/diff limpios. DOM prueba resumen y reglas,
  cancelar sin guardar, corregir/reabrir y confirmar una vez. Sólo app recreada,
  salud UP, temporales retirados; sin QA visual autenticada ni creación de conceptos
  o movimientos financieros por herramientas. V96 libre.

- Antes de continuar DUP-TEST, propietario pidió modal en Crear concepto.
  Alta usa data-money-confirm=concepto-cobro y motor/estilo money-input compartido.
  Resumen vivo: institución/código/nombre/categoría/descripción/permitebeca/descuento/
  recargo/activo, Sí/No para reglas. Cancelar conserva captura sinPOST; Confirmar y
  crear concepto envía una vez. textContent evita interpretar texto comoHTML.
  Aclara que concepto no genera adeudos/pagos/ajustes ni fija importe individual.
- Edición y desactivación no cambian; endpoints/backend/permisos intactos. Sin
  migración/finanzas, V96 libre. Pruebas humanas DUP-TEST/confirmcancel/dospestañas
  siguen pendientes; no se creó concepto por herramientas.

## ESTADO VIGENTE — devoluciones05 confirmadas y guía V95 — 2026-10-14

- Verificado725Java/21Node sin fallos, diff limpio. SQL editorial BEGIN/ROLLBACK;
  sólo app recreada/salud UP. Lectura confirma V95 aplicada, guíaCONFIRMADA10etapas,
  25guías/217etapas. Temporales retirados, sin QA visual autenticada ni movimientos
  financieros por herramientas. Siguiente V96; preservar V1–V95 aplicadas.

- Propietario confirmó ambas: parcial100 sólo noviembre → abono600/deuda100,
  diciembre700/deuda0, beneficio100 por mes conservado, cuenta−100, portal100.
  Los400 del portal eran otro adeudo del otro hijo; no error ni efecto del ensayo.
  Después restante1300(600+700) → devuelto acumulado1400, cuenta otros−1300,
  acuerdo cancelado, beneficio100 por mes reversado, beca200 por mes conservada,
  sin abonos vigentes, adeudos800+800 y portal igual. No repetir ni tratar pendiente.
- V95 guía CONFIRMADA devolver-acuerdo-anticipado de10 etapas/4acciones y5permisos
  existentes requeridos, sin otorgar roles. Localización por concepto, cuentaS,
  selección/consentimiento/modales, parcial y restante, vista de abonos liberados vs
  reversión adicional de beneficio, banco/adeudos/portal/historial y otro hermano400.
  Actualiza referencias editoriales de guías previas con nueva migración, sin tocar
  V1–V94 ni registros financieros. No inferir folio/método/saldo absoluto.
- SIGUIENTE: tres ensayos de seguridad pendientes: cargo duplicado, cancelar modal
  sin guardar y validar MISMO folio desde dos pestañas sin duplicar ingreso/abonos.
  Usar otro cargo/controladoDUP-TEST200, no el anual ni pagar deudas800 para ensayarlos.

## Regreso a listados de cobranza conserva contexto — 2026-10-14

- Verificación:723Java completos sin fallos y3 pruebas finales render/interceptor,
  21Node aprobados (incluye hiddenPOST local/sin duplicación). Diff limpio, sólo app
  recreada/salud UP; temporales retirados, sin QA visual autenticada ni finanzas.

- Ver/Editar desde Pagos/Adeudos/Cuotas lleva retorno explícito codificado con q,
  conceptoCobro, estado, fechas/tipo, cobertura/año, página y tamaño. Backlinks del
  detalle y Cancelar cuota usan ese destino, sin modificar diseño. Abrir en otra
  pestaña conserva origen; no guardar búsquedas compartidas en sesión/localStorage.
- RetornoListadoCobranza genera URL y sólo admite ruta relativa exacta del listado
  correspondiente; rechaza externos, otra categoría, fragmentos/rutas codificadas.
  Interceptor aplica a los tres módulos, añade modelo en detalles/errores y conserva
  contexto en redirects de acciones. Guardar cuota que vuelve al listado devuelve
  filtros originales. Script inserta hidden retorno sólo en formulariosPOST locales.
  Entradas directas sin retorno siguen con enlaces predeterminados; no afecta permisos.
- Sin migración ni finanzas, V95 libre. Devoluciones05/seguridad pendientes.

## Concepto de cobro en Cuotas y Adeudos — 2026-10-14

- Verificación721Java/20Node sin fallos: render/enlaces de tres módulos, Excel dos
  bloques de Cuotas/Adeudos, criterios vacíos/literales, SQL PostgreSQL real generado
  por Hibernate para select/conteo de Cuotas/Adeudos/Pagos e interceptado antes de
  conectar. Diff limpio; sólo app recreada/salud UP, temporales retirados. Sin QA
  visual autenticada ni operaciones financieras. V95 libre.

- Se extiende filtro conceptoCobro a CUOTAS_ALUMNO y CARGOS: código/nombre parcial
  sin distinguir mayúsculas, caracteres%/_/backslash literales. Se combina mediante
  AND con Buscar (alumno/matrícula), estado, fechas y alcance vigente, antes de paginar.
  Filtro vacío no limita. Relación directa conceptoCobro evita duplicar filas.
- DTO/controladores/enlaces/Excel ya compartían conceptoCobro; ambos módulos ahora
  muestran input en fila propia, arriba Buscar/Estado/Por página alineados44px/gap18,
  clase local filters-billing, colores compartidos y adaptación tablet/móvil. Pagos
  mantiene su diseño y otros módulos no reciben filtros nuevos. Sin migración ni
  movimientos, V95 libre; devoluciones05/seguridad siguen pendientes.

## Selección de abonos y autorización de devoluciones — 2026-10-14

- Verificación:12Java específicos/20Node sin fallos, diff limpio; sólo app recreada
  y salud UP, temporales retirados. Sin QA visual autenticada ni operaciones financieras.

- Diseño solicitado antes de prueba05: checkbox reales24px con marca clara, tarjeta
  seleccionada con borde/fondo del tema y etiqueta Seleccionado junto al monto.
  Autorización de beneficio usa refund-benefit-consent en tarjeta con texto principal
  y ayuda secundaria. Mantiene required/name/th:field/datos de abonos, sin alterar
  cálculos ni consentimiento. Teclado/foco visible y fallback forced-colors nativo;
  móvil pone importe debajo del alumno. Estilos locales, no cambia otros checks.
- Devoluciones05 y seguridad siguen pendientes; no marcar prueba confirmada ni
  ejecutar finanzas por herramientas. Sin migración/permisos, V95 libre.

## Filtro independiente por concepto en Pagos recibidos — 2026-10-14

- Verificado:716Java/20Node sin fallos, incluye criterio tres EXISTS/vacío/literales,
  render filtro/enlaces sólo PAGOS y Excel dos bloques con mismo concepto. Diff limpio;
  sólo app recreada, salud UP, temporales retirados. Sin QA visual autenticada ni
  operaciones financieras por herramientas. V95 libre.

- Propietario no encontraba ANTICIPO-TEST-05: búsqueda general sólo consulta folio,
  pagador/tutor, referencia y plantel. Se agrega campo Concepto de cobro por código
  o nombre, independiente/combinable con búsqueda general/estado/fechas/cobertura/año.
- FiltroCatalogo incorpora conceptoCobro con constructores previos compatibles,
  normalización y conPagina preservándolo. Controller listado/Excel, enlaces de
  paginación/exportación y bloques POI conservan el valor. Input sólo en PAGOS,
  fila ancha propia con controles/colores compartidos y responsive existente.
- FiltroConceptoPago usa tres EXISTS correlacionados: solicitudes, aplicaciones
  (incluye historial) y cargos del acuerdo anticipado (incluye bonificados0). Código/
  nombre sin distinguir mayúsculas, porcentaje/guion bajo/backslash literales.
  Consulta antes de paginar con alcance existente, sin cargar todos los pagos ni
  multiplicar filas por mensualidad. Filtro vacío no agrega subconsultas.
- Sin cambio financiero/esquema/permisos; V95 libre. Para probar: Pagos recibidos →
  Limpiar → Concepto de cobro ANTICIPO-TEST-05 → Aplicar filtros. Confirmar folio del
  pago1400 antes de devolver100; las devoluciones05 y seguridad siguen pendientes.

## ESTADO VIGENTE — pago anual confirmado y guía V94 — 2026-10-13

- Verificación final:713Java/20Node sin fallos, diff limpio. SQL editorial validado
  BEGIN/ROLLBACK; sólo app recreada y salud UP. Lectura confirma V94 aplicada y guía
  CONFIRMADA/12etapas;24guías/207etapas. Temporales de empaquetado retirados, sin QA
  visual autenticada ni operaciones financieras. Próxima migración V95; no editar V94.

- Propietario confirmó terminado el ejemplo enero–diciembre2027:12 originales1000,
  sin beca, diciembre bonificado1000/pago0, enero–noviembre1000 cada uno, un pago11000,
  cuenta B+11000, doce saldos0, Cobertura anual LIQUIDADO/11 pagados/1bonificado y
  comprobante familiar disponible. No seguir tratándolo como pendiente ni repetir pago.
  No suponer folio, alumno, método o saldo absoluto que no comunicó.
- V94__guia_pago_anual_diciembre_bonificado.sql agrega guía CONFIRMADA de12 etapas,
  cuatro acciones cada una y10 permisos existentes requeridos, sin conceder roles.
  Recorrido por inscripciones/ciclos, familia, cuentaB, concepto, cuotas, generación
  seleccionada, acuerdo, vista previa/modales, pago único, Gestión/validación, Resumen/
  Distribución/Cobertura anual, banco/adeudos/portal/PDF. Consulta de filtros/Excel como
  paso complementario, no atribuida como nueva prueba humana confirmada.
- Fechas/plazo/código del ensayo son ilustrativos; ciclo hasta15/06/2027 no cubre todo
  año. Puede atravesar inscripciones compatibles sin falsear fechas. Año de pago y año
  cubierto se distinguen. Diciembre0 no crea abono ni ingreso ficticio. No tocar V1–V93.
- SIGUIENTE: retomar devoluciones del MISMO pago1400 de ANTICIPO-TEST-05, primero100
  parcial sólo noviembre700 → abono600/deuda100, diciembre700/deuda0, bancoB+1300.
  Después devolver restante1300 (600+700) → devuelto1400, bancoB, beca200 por mes
  conservada, beneficio100 por mes reversado, deudas800 por mes/acuerdo cancelado.
  Confirmar folio/estado/cuenta actuales antes de orientar, no asumir IDs de otra BD.
  No cancelar ese pago ni ejecutar finanzas por herramientas. Seguridad duplicados/
  modal/validación MISMO folio desde dos pestañas también sigue pendiente.

## Alineación interna de Resumen de pago — 2026-10-13

- Ajuste CSS exclusivo de panel-pago-resumen: margen común34px escritorio/20px móvil
  para intro, explicación del estado, sección del acuerdo y accesos anuales. Antes
  explicación y acceso anual quedaban pegados al borde, mientras intro/acuerdo tenían
  padding. Párrafos con margen y altura de línea uniformes; botones de acuerdo/año
  con tipografía compartida, alineación izquierda, separación vertical y ancho móvil.
- Sin cambio de datos/acciones ni otros paneles.3 pruebas Java de render y1Node de
  pestañas sin fallos, diff limpio. Sólo app actualizada, salud UP; no QA visual
  autenticada. Temporales retirados. V94 libre; pruebas humanas pendientes intactas.

## Expediente financiero integrado en Resumen — 2026-10-13

- Autorizado organizar detalle de Pagos recibidos: arriba del navegador sólo folio,
  estado e importe recibido registrado. Encabezado compacto adaptable con importe
  alineado a la derecha en escritorio y debajo en móvil, conservando temas compartidos.
- Expediente financiero, tutor/plantel, explicación de la resolución y aviso/enlace
  del acuerdo anticipado quedan DENTRO de Resumen, no fuera del menú. No se crea
  otra pestaña redundante. Datos, cuenta y movimiento siguen en Resumen; Distribución,
  Gestión, Devoluciones, Comprobantes y Cobertura anual permanecen independientes.
- Regresión renderiza Resumen y encabezado para cuatro estados, con/sin acuerdo,
  y protege orden del navegador.11 pruebas Java específicas/1Node de pestañas sin
  fallos, diff limpio. Sólo app recreada y salud UP; sin QA visual autenticada.
  No cambios financieros, permisos o migración. V94 libre; prueba anual humana y
  devoluciones05/seguridad siguen pendientes. Temporales de empaquetado retirados.

## Cobertura anual en pestaña propia — 2026-10-13

- Autorizado: mover sección extensa al menú nav existente del detalle de pagos.
  Pestaña Cobertura anual sólo si existen grupos anuales, panel-pago-anual y hash
  #cobertura-anual-pago. Resumen conserva accesos compactos año/estado/alumno/concepto
  con data-open-student-tab=anual; activan panel y transfieren foco al tab para teclado.
  No panel/tab/acceso cuando lista anual vacía. Panel empieza hidden y script común
  controla selección; no dejar información anual extensa encima del expediente.
- Detalle por año conserva estado,12/12 meses, meses pagados/bonificados, beneficio
  pactado y abonos vigentes. Distribución mantiene desglose mensual, incluido0bonificado.
  No cambia clasificación DB, filtros/Excel, roles, pagos o saldos. V94 sigue libre.
- Pruebas render de tarjeta/nav/panel/resumen con y sin anual y DOM de acceso/hash/
  flechas/foco:710Java/20Node sin fallos, JS/diff limpios. Sólo app recreada, saludUP
  y CSS servido. Sin QA visual autenticada ni cambios financieros. Anual pendiente de validar
  y confirmar para guía. Devoluciones05 y seguridad siguen pendientes.

## Filtros de Pagos recibidos alineados — 2026-10-13

- Solicitud de diseño: se mueve Por página antes del fieldset ancho de cobertura
  (antes quedaba aislado debajo). Sólo PAGOS usa filters-payments: fila Buscar/Estado/
  Por página, bloque cobertura(tipo2columnas/año1), bloque fechas3columnas y acciones
  separadas. Campos44px, títulos/gaps uniformes, bordes directos de fecha/año y tema
  compartido. Tablet2columnas/móvil1, sin afectar filtros de otros módulos.
- No cambia valores, consultas, paginación ni filtros de Excel, permisos o migraciones.
  V94 libre; anual humano sigue pendiente y devoluciones05/seguridad pendientes.
  CatalogoRangosRenderTest sin fallos, confirma orden y clase sólo en PAGOS, preserva
  parámetros Excel/paginación. Diff limpio; sólo app recreada, salud UP y CSS servido.
  Sin QA visual autenticada ni operaciones financieras por herramientas.

## Login: recuperación automática de cookie caducada — 2026-10-13

- Se reprodujo GET/acceso/csrf con cookie JSESSIONID ficticia caducada:302 al login
  con sesionExpirada, en vez deJSON. fetch sigue la redirección y el JS anterior la
  trataba como fallo de conexión aunque el servidor ya limpiara cookie/renovara acceso.
- login-session.js ahora reconoce sólo redirect del MISMO origen con sesionExpirada y
  reintenta consultaGET/token UNA vez (máximo2). Luego reemplaza _csrf y envía el login
  una sola vez. NO reintenta POST/credenciales ni desactiva CSRF. Redirect externo/u otro
  motivo, fallos de servidor o red no reciben reintento automático ni envían credenciales.
  Avisos distinguen sesión que vuelve a cambiar, servicio no disponible y conexión.
- Aplica a los tres portales, mantiene origen y controles compartidos. Sin backend,
  migración o cambios financieros; V94 libre.16 pruebas Java de sesión/CSRF y19Node
  sin fallos; JS/diff limpios. Sólo app recreada, salud UP y script servido. HTTP con
  cookie FICTICIA reprodujo302/login y segundoGET/token válido sin enviar credenciales;
  cookie de QA/temporales retirados, no QA visual autenticada ni operaciones financieras.
  Anual humano sigue pendiente de validar/confirmar; devoluciones05/seguridad pendientes.

## ESTADO VIGENTE — identificación y filtros de cobertura anual V93 — 2026-10-13

- Propietario autorizó identificar año completo y filtrar quién pagó todo el año.
  Clasificación DERIVADA, no un campo editable: mismo pago/alumno/concepto/plantel/
  moneda y año,12 cargos de COLEGIATURA/12 meses distintos enero–diciembre. No mezcla
  meses de hermanos, conceptos, años ni meses duplicados. Puede atravesar inscripciones
  compatibles del mismo alumno. Incluye cargo bonificado0 desde detalles del acuerdo,
  aunque no tenga solicitud/aplicación monetaria. También reconoce pago ordinario que
  cubra los12 meses, sin obligar a que provenga de acuerdo.
- V93 crea sólo vista pago_cobertura_anual y función estable pago_coincide_cobertura.
  SQL agrupa fuentes DISTINCT de solicitudes/aplicaciones/detalles; lee neto de ajustes
  y abonos DEL MISMO pago. LIQUIDADO exige validado/acuerdo aplicado (si existe), cargos
  emitidos y cada mes cubierto por abonos propios/bonificación, con alguna aplicación
  monetaria positiva. Pendiente, cancelado/rechazado o no liquidado se distinguen; una
  devolución que libera deuda quita LIQUIDADO. Devolver excedente sin reabrir cargos no
  falsea su cobertura. Bonificaciones pactadas históricas no se presentan como vigentes.
- Pagos recibidos agrega columnas Cobertura por alumno/año y Beneficio anual pactado.
  Filtros: Todos/Sin anual/Anual/Anual liquidado/pendiente/no vigente/no liquidado,
  Año cubierto opcional1900–9999 (no fijo a un año). Año2027 puede pagarse en2026;
  rango Desde/Hasta sigue siendo fecha del pago, no año cubierto. Filtros en DB antes
  de paginar, alcance existente por institución/plantel; paginación y Excel conservan
  criterios. No se consulta toda la información para filtrar en Java.
- Detalle de pago muestra tarjeta por alumno/concepto/año con12/12, estado, meses
  pagados completos/bonificados pactados, beneficio y abonos vigentes (no otro ingreso).
  Formulario/filtros y temas compartidos. Excel POI en bloques100, beneficio numérico
  y columna de cobertura con ajuste de texto. Desglose se carga en lote por página,
  dePago valida alcance; no hay endpoint familiar ni nuevo permiso/rol.
- No se modifican pagos/cargos/becas/movimientos; sin aplicaciones0 ficticias. Regresión
  SQL con10 escenarios FICTICIOS en PostgreSQL temporal sin red/volúmenes, ya retirado:
  pendiente, validado11+bonificado1, devolución parcial, cancelación, devolución total,
  mezcla hermanos/conceptos/años/duplicado y pago ordinario completo. SQL real deDDL
  validado en BEGIN/ROLLBACK sin operaciones financieras. Verificación final709Java/
 19Node sin fallos, diff limpio. Sólo app recreada y salud UP; lectura confirma V93
 aplicada y cobertura2027 PENDIENTE/12 meses/1 bonificado, sin validar el pago. Guías
 siguen23/195. Temporales de empaquetado/QA retirados; no QA visual autenticada.
- Ejemplo anual humano sigue antes de validar y pendiente de confirmar, no publicar
  guía anual confirmada. Devoluciones05/seguridad también pendientes. Próxima migración
  V94; no editar V1–V93 aplicadas. Guías existentes23/195 no cambian.

## Ejemplo anual en revisión y periodos legibles — 2026-10-13

- Propietario pidió ejemplo enero–diciembre2027 con diciembre bonificado, sin beca:
  12x1000=12000, beneficio1000, pago11000. Se advirtió ciclo existente hasta15/06/2027
  no cubre todo2027: cada cargo requiere inscripción/ciclo que cubra sus fechas, sin
  falsear vigencias; año calendario puede atravesar varias inscripciones/ciclos.
  Ahora está revisando Distribución antes de validar; no confirmó aún el resultado
  anual. No publicar guía anual CONFIRMADA ni suponer pago validado/ids/banco.
- Periodo de AcuerdoAnticipadoService pasa de ISO a dd/MM/uuuu — dd/MM/uuuu; se refleja
  en Distribución (alumno/mensualidad), detalle de acuerdo (alumno/concepto/periodo),
  selección/vista previa y Excel del acuerdo. Sólo presentación, no modifica fechas ni
  cálculos. Regresión incluye enero/diciembre2027 y febrero bisiesto2028.
- Propuesta de mejora para analizar con propietario, NO implementada: identificación
  Año completo2027 por alumno y concepto con12 meses distintos enero–diciembre,
  incluyendo bonificado0; no basta contar12 cargos porque pueden ser otros hermanos/
  conceptos/años. Distinguir Cobertura anual propuesta de Pago anual validado;
  cancelaciones/devoluciones no deben presentarse como anual vigente liquidado.
  Etiqueta en listado/detalle de pagos, filtros tipo cobertura/año cubierto y reporte
  tutor/alumno/año/meses/beneficio/importe/estado, paginado y Excel mismos filtros.
  Año del pago real puede ser2026 y año cubierto2027; no confundir ambos filtros.
- V93 sigue libre. Devoluciones05 y seguridad pendientes; ejemplo anual en revisión.
  Formato verificado:703 Java/19 Node sin fallos, diff limpio; sólo app recreada,
  salud UP. Sin movimientos financieros, migración ni QA visual autenticada por herramientas.

## ESTADO VIGENTE — tres variantes confirmadas y guías V92 — 2026-10-13

- Propietario confirmó completo05: dos originales1000/beca200 por mes, conservar beca
  y beneficio fijo200TOTAL (100 por mes), un pago1400, abonos700+700, ambos saldos0,
  banco+1400, beca20% activa y comprobante familiar.03 (bonificar diciembre/pago800)
  y04 (sustituir beca20% por beneficio10%/pago1800) ya estaban confirmadas.
  No repetir los tres pagos ni seguir pidiendo sus comprobaciones como pendientes.
- V92__guias_variantes_pago_anticipado.sql publica TRES guías CONFIRMADAS independientes:
  anticipo-mensualidad-bonificada, anticipo-sustituir-beca y anticipo-beneficio-fijo,
 10 etapas/4 acciones cada una,9 permisos existentes requeridos por guía sin asignar roles.
  Preparación completa por alumno/concepto/beca/cuota/generación, selección aislada,
  plazo, política, vista previa, modales, pago, validación, desglose y banco/portal/recibo.
 03 explica diciembre0 sin aplicación ficticia;04 compensación de beca sólo meses elegidos;
 05 fijo200TOTAL, no por mes. Actualiza referencias editoriales pendientes en V90/V91
  mediante nueva migración, sin modificar archivos aplicados ni datos financieros.
- SQL comprobado BEGIN/ROLLBACK: tres guías10 etapas/9 permisos. Verificación final:
 701 Java/19 Node sin fallos, diff limpio; sólo app recreada, salud UP. Lectura confirma
 V92 aplicada, tres guías CONFIRMADAS/10 etapas cada una,23 guías/195 etapas totales.
 Sin operaciones financieras, cambios de volúmenes/.env ni QA visual autenticada por
 herramientas. Temporales de empaquetado retirados. Próxima V93; no editar V1–V92.
- SIGUIENTE: devolución parcial100 del MISMO pago1400 de05, manteniendo beneficio,
  seleccionar sólo abono de noviembre700 → queda abono600/deuda100, diciembre700/deuda0,
  banco B+1300 (B anterior al pago05). Luego devolver restante1300 de ese mismo pago,
  seleccionando abonos vigentes600+700, devuelve acumulado1400 y restaura banco B,
  beca200 por mes y deudas800 por mes, beneficio100 reversado/acuerdo cancelado.
  NO se han realizado ni confirmado estas devoluciones; no publicarlas como guías
  confirmadas ni ejecutarlas por herramientas. Confirmar folio/estado/saldo actual de05
  antes de orientar; no asumir ids o saldo absoluto de otra BD. No cancelar el pago05.
  DUP-TEST200/duplicados/modal y validación del MISMO folio desde dos pestañas siguen
  pendientes; cancelar modal sin guardar durante05 no confirma idempotencia en dos pestañas.

## Distribución03: dos meses visibles también después de validar — 2026-10-13

- Propietario precisó Pagos recibidos → Ver → Distribución, pago800 de03, sólo veía
  una fila. Causa encontrada: desglose se había incluido dentro de la sección exclusiva
  de PENDIENTE_VALIDACION. Se mueve al panel común, visible en validados y como historia
  en cancelados. No volver a probar solamente el fragmento aislado: regresión renderiza
  paymentDistribution completa con pago VALIDADO/anticipoAPLICADO y CANCELADO.
- Lista principal ahora incluye abono real de noviembre800 y tarjeta de diciembre0
  etiquetada mensualidad bonificada, sin dinero recibido; bonificación800 separada.
  No INSERT de aplicación0 ni movimiento bancario. Totales siguen aplicados800, banco800.
  Tarjeta bonificada sólo con acuerdoAPLICADO/pagar0; historia no finge beneficio vigente.
- Sin cambios financieros ni migración; V92 libre. Dos pruebas Java de render
  (panel completo validado/cancelado y acuerdo) sin fallos, diff limpio; JAR empaquetado,
  sólo app recreada y salud UP. Sin QA visual autenticada ni operaciones financieras.

## ESTADO VIGENTE — prueba04 confirmada, acceso entre pestañas y desglose bonificado — 2026-10-13

- Propietario confirmó completo ANTICIPO-TEST-04: sustitución de beca20% sólo en
  noviembre/diciembre por beneficio10%, base2000/beneficio200/pago1800,900 por mes,
  ambos saldos0, banco+1800, beca general activa y comprobante/portal correctos.
  No repetir pago04. Guías03 y04 pendientes de incorporación editorial; V92 sigue libre.
- Próximo ejemplo05: conservar beca20% + cantidad fija200TOTAL,100 por mes, pago1400;
  después devolución parcial100 y restante1300, y seguridad duplicados/modal/dos pestañas.
  No confirmar esos escenarios sin prueba humana.
- Corrección del acceso: logout en una pestaña deja login de otro portal con tokenCSRF
  viejo aunque cookie ya sea nueva/válida o no exista. Nuevo GET /acceso/csrf público
  sólo devuelve token protegido de sesión actual, sin credenciales, Cache-Control:no-store.
  Los tres logins usan login-session.js: consulta antes de enviar y reemplaza _csrf,
  conserva origen/contexto; fallo no envía credenciales. CSRF sigue habilitado.
  Si falla CSRF POST/login, handler redirige al acceso propio con accesoActualizado
  sin borrar cookie válida ni reenviar al error POST; aviso usa diseño de cada portal.
  Permisos y errores ordinarios fuera de login mantienen protección existente.
- En pago03 Distribución había un abono800 sólo para noviembre: CORRECTO, diciembre
  bonificado no recibe dinero y no se debe inventar aplicación0. Nuevo desglose histórico
  Mensualidades incluidas en el acuerdo muestra AMBOS meses/base/beneficio/pago acordado,
  diciembre Bonificada, sin abono cuando aplicado; pendiente/cancelado etiqueta condiciones
  históricas sin fingir ingreso o saldo actual. Abonos reales permanecen separados.
- No se modifican pagos, aplicaciones, saldos ni migraciones con estas correcciones.
  Regresión con filtroCSRF real, acceso de tres portales, JS único envío, recuperación
  sincookie/cookieválida y render bonificado/histórico.699 Java/19 Node sin fallos,
  sintaxis/diff limpios. Sólo app recreada, salud UP, endpoint/token y login familiar
  verificados por HTTP sin exponer token. POST/login sin credenciales con token viejo
  redirige302 a acceso propio, sin Whitelabel. Sin QA visual autenticada ni operaciones
  financieras por herramientas; confirmar intercambio real de sesiones con propietario.

## ESTADO VIGENTE — mensualidad bonificada confirmada — 2026-10-13

- Propietario confirmó prueba completa ANTICIPO-TEST-03: noviembre/diciembre originales
  1000 con beca200 cada mes; conservar beca y bonificar diciembre800; pago completo800.
  Noviembre abono800/saldo0; diciembre sin abono, descuento800/saldo0; banco+800,
  beca20% conservada, acuerdo aplicado y comprobante/portal correctos según confirmación.
  Folio comunicado PAG-20261013-000025; no asumir id ni saldo absoluto de otra BD.
- Se generaron seis cargos (dos por concepto03/04/05);04 y05 siguen destinados a sus
  pruebas,800 por mes antes de acuerdos. No incluirlos en acuerdo03 ni repetir pago03.
- Ajuste solicitado: botón Ver pago separado de totales y leyenda mediante bloque
  anticipation-payment-followup, espaciado/borde y texto fluido móvil, botón compartido.
  Verificado AnticipoRenderTest/18Node sin fallos, diff limpio; sólo app recreada,
  salud UP y CSS servido por HTTP. Sin QA visual autenticada ni operaciones financieras.
  Sin cambios de cálculos, permisos ni movimientos. Guía de03 aún pendiente de incorporar;
  no se crea migración en esta corrección. V92 sigue libre, no editar V1–V91 aplicadas.
- Siguiente ensayo: ANTICIPO-TEST-04, SUSTITUIR beca sólo noviembre/diciembre +10%.
  Original1000/beca previa200/base acordada1000/beneficio100/pago900 por mes; total1800.
  La beca general sigue activa; al validar se compensa200 y descuenta100 por cargo.
  Anotar saldo bancario B real antes, esperado B+1800 y ambos saldos0. No confirmado aún.
  Después05 conservar beca+cantidad fija200total (100 por mes), pago1400 y devoluciones
  parcial100/restante1300; bloque seguridad duplicados/modal/dos pestañas pendiente.

## ESTADO VIGENTE — cancelación anticipada confirmada y guía V91 — 2026-10-09

- Propietario confirmó el ensayo nuevo ANTICIPO-TEST-02: dos mensualidades originales
 1000, beca200 y beneficio80 por mes; validó1440 y comprobó banco, adeudos0, beca activa,
 acuerdo aplicado y comprobante familiar. Después canceló el MISMO pago y confirmó todos
 los controles: banco resta1440, acuerdo/pago cancelados, beneficio reversado, beca20%
 conservada y portal con noviembre800/diciembre800. Caso CONCLUIDO, no repetirlo.
- Consulta de sólo lectura confirma PAG-20261009-000002 CANCELADO1440 y acuerdo
 ANT-20261010-000002 CANCELADO. Primer intento incompleto720 también cancelado;
 se conservan ambos historiales. No confundir con ids/folios del ensayo de otra BD.
- V91 publica guía CONFIRMADA de12 etapas/cuatro acciones por etapa y10 permisos
 existentes requeridos, sin otorgar roles. Incluye captura completa, apertura de cuenta,
 selección de ambos meses, pago único1440, beneficio condicionado, validación, cancelación
 y verificaciones admin/familia/banco. Actualiza referencias editoriales de V90 mediante
 la migración nueva; no modifica V1–V90 ni registros financieros.
- Historial de ajustes: Ver del catálogo reutiliza new-button como Pagos; Ver cargo
 del detalle usa detail-close-button de forms.css. Sin estilos paralelos ni cambio de rutas.
- Verificación final:693 pruebas Java aprobadas; SQL editorial comprobado en BEGIN/ROLLBACK.
 Sólo app reconstruida/recreada, salud UP; lectura confirma V91 aplicada, guía CONFIRMADA
 de12 etapas y20 guías/165 etapas totales. Ambos pagos cancelados720/1440 permanecen
 intactos. Sin QA visual autenticada ni escrituras financieras por herramientas.
 Próxima migración V92; no editar V1–V91 aplicadas. Cambios sin commit/push del agente.
- Próximos ensayos humanos: mensualidad bonificada, sustitución de beca/cantidad fija,
 devoluciones anticipadas y DUP-TEST200/validación del mismo pago desde dos pestañas.
 No dar estas variantes por confirmadas ni realizar operaciones financieras con herramientas.

## Vista previa de pagos anticipados corregida — 2026-10-09

- El propietario inició un ensayo nuevo ANTICIPO-TEST-02 con dos mensualidades1000,
  beca20% y beneficio adicional10%. En Pagos recibidos, aún pendiente de validación,
  reportó saldo800/pago720/restante80 y una advertencia propia de pagos ordinarios.
- PagoMapper ahora consulta la relación de sólo lectura con el acuerdo existente:
  para pagos pendientes con propuesta activa calcula saldo + beca sustituida - beneficio
  anticipado - abono. Conservando beca:800-80-720=0; sustituyendo:800+200-80-920=0.
  Los pagos ordinarios conservan su cálculo; pagos resueltos y acuerdos cancelados no
  proyectan beneficios. La vista previa no publica ajustes ni modifica cargos.
- Distribución explica el beneficio condicionado al pago completo y vigente, muestra
  su importe y, cuando aplica, la beca que se sustituirá. Omite la advertencia genérica
  de diferencia para estas filas del acuerdo. La validación financiera sigue revalidando
  todas las condiciones y no cambió.
- Verificación:691 pruebas Java sin fallos, incluida regresión de cálculo y render real
  de pago pendiente anticipado. Sólo app reconstruida/recreada; salud UP y BD local V90.
  No requiere migración, V91 sigue disponible. No se validaron/cancelaron pagos mediante
  herramientas ni se publicó una guía nueva como confirmada.
- Continuar el MISMO pago pendiente del ensayo: recargar, comprobar beneficio80/saldo
  estimado0 por mes, validar desde pantalla y comprobar resultados. Luego cancelar ese
  mismo pago por el flujo de Gestión y confirmar banco/saldos/beca/historial antes de
  documentar la guía de cancelación. Los folios del nuevo ensayo aún no se comunicaron.

## ESTADO VIGENTE — prueba anticipada confirmada y guía V90 — 2026-10-09

- Este bloque prevalece sobre el punto de traslado anterior (histórico). Propietario
  confirmó TODOS los puntos finales: ambos adeudos saldo0, beca200 y descuento80 por mes,
  pago1440/comprobante familiar, sin esas deudas pendientes y beca20% aún activa.
  Banco confirmado19730 desde18290. Caso conservar beca20% + beneficio10% CONCLUIDO.
  No repetir registros ni continuar pidiendo estas comprobaciones como pendientes.
- V90__guia_pago_anticipado_beca.sql publica sólo contenido editorial: guía CONFIRMADA
  pago-anticipado-beca-porcentaje con10 etapas,4 acciones cada una,9 permisos existentes
  requeridos sin otorgar roles. Rutas/módulos/botones, fechas, modales, cálculo por mes,
  banco/comprobante/portal y precauciones de no duplicar. Validada BEGIN/ROLLBACK.
  No modifica tablas financieras ni los pagos existentes. No editar V1–V90 aplicadas.
- Verificación final:687 Java/18 Node sin fallos; diff limpio. Sólo app recreada,
  salud UP; lectura confirma V90 aplicada, guía CONFIRMADA/10 etapas y19 guías/153 etapas.
  Pago27 permanece VALIDADO1440 y banco19730; sin operaciones financieras por herramientas,
  QA visual autenticada ni cambios de volúmenes/.env. Temporales de empaquetado retirados.
- Guía publicada desde Seguridad y soporte → Guía de procesos.
  Próxima migración V91. Prueba siguiente propuesta: cancelar el MISMO pago validado
  id27/PAG-20261009-000024 desde Pagos recibidos → Ver → Gestión → Cancelar pago
  registrado por error, motivo y modal (NO cancelar cargo, NO devolución). Esperado
  banco18290, deuda800 por mes, beneficio80 reversado y beca200 conservada, acuerdo1
  cancelado; trazabilidad completa. Todavía NO realizada ni confirmada por propietario.
  No ejecutar operaciones financieras por herramientas ni publicar esa cancelación
  como escenario confirmado. Mensualidad gratis, sustitución/fijo y DUP-TEST200/two-tabs
  permanecen pendientes. Traslado/Git/BD conserva precauciones del bloque anterior.

## PUNTO EXACTO PARA RETOMAR EN OTRA COMPUTADORA — 2026-10-09

Esta sección es el estado vigente y prevalece sobre los siguientes pasos históricos
de las entradas anteriores. Petición actual: guardar continuidad; sólo documentación,
sin registrar pagos, cancelar, devolver, desplegar ni publicar una guía confirmada.

### Prueba actual: beca20% + beneficio adicional10% por pago anticipado completo

- Propietario usa alumno ya inscrito desde01/10/2026, sin fecha final de inscripción;
  ciclo escolar01/10/2026–15/06/2027. Se explicó que el fin de ciclo limita la vigencia
  cuando inscripción no tiene fecha final. No hace falta modificar inscripción.
- Concepto del ejemplo ANTICIPO-TEST-01, Colegiatura prueba pago anticipado, categoría
  COLEGIATURA, permite beca/descuento, sin recargo, activo. Propietario confirmó concepto
  y beca20% registrados. Ejercicio indicado: cuota1000MXN/mes, noviembre y diciembre2026,
  día límite20, vigencia01/11/2026–31/12/2026, automática; generar sólo esos dos cargos
  con corte31/12/2026. Beca200 y saldo800 por mes antes del acuerdo.
- Acuerdo conservar beca + porcentaje adicional10%, base1600, beneficio160, pago1440.
  Fecha límite indicada20/10/2026; si fecha de nueva prueba cambia, revisar plazo vigente.
  Propuesta no afecta banco/cargos; beneficio sólo al validar pago completo desde acuerdo.
- Propietario ya guardó propuesta, registró y VALIDÓ pago completo con tarjeta/cuenta
  bancaria. Lectura real de BD confirmó acuerdo id1, ANT-20261009-000001, APLICADO,
  total_base1600, total_beneficio160, total_pagar1440; pago id27,
  PAG-20261009-000024, VALIDADO, monto1440, cuenta_destino_id1.
- Cuenta id1 Cuenta principal BBVA Raices: movimiento id36 INGRESO1440, secuencia24,
  saldo_anterior18290, saldo_posterior19730. Suma saldo inicial+movimientos19730,
  sin saltos de saldo. Propietario inicialmente anotó18890 y creyó ingreso840; consulta
  no encontró saldo histórico18890 ni movimiento600 en esta cuenta. Posteriormente
  confirmó19730 en Cuentas financieras y reconoció posible error al anotar saldo previo.
  NO existe justificación para crear ajuste600, repetir pago o alterar movimientos.
- Lo comprobado por herramientas fue sólo lectura; todas las operaciones de esta
  prueba fueron hechas por propietario. No tomar resultados esperados como confirmados.

### Próximo paso inmediato, antes de cancelar o devolver

El último usuario preguntó dónde comprobar el saldo0 y desglose de beca/beneficio.
Se le indicó Operación escolar → Cobranza escolar → Adeudos de alumnos:
1. Buscar alumno y ANTICIPO-TEST-01; estado Todos o Liquidado para ver ya saldados.
2. Abrir Ver de noviembre → Resumen: original1000; Descuentos y becas280;
   Total del adeudo con ajustes720; Abonos vigentes720; Falta por pagar0.
3. Historial → Historial de ajustes: BECA disminución200 y DESCUENTO por acuerdo80.
4. Repetir con diciembre: mismos importes. Sólo consultar; NO aplicar ajuste manual.
5. Confirmar también acuerdo Beneficio aplicado, beca20% aún activa, y Portal familiar
   → Pagos sin esas mensualidades pendientes y con comprobante del pago1440.
FALTA confirmación del propietario de estos puntos. No declarar caso completo ni
publicar todavía su guía en el sistema (guías existentes18/143 de V88).

Cuando confirme, documentar el caso según autorización y proponer prueba separada de
cancelación del MISMO pago: esperado banco18290, cada mensualidad nuevamente800 por
pagar, reversa del descuento80, beca200 conservada, acuerdo cancelado e historial intacto.
No ejecutar por herramientas; guiar al propietario. Luego probar mensualidad bonificada,
sustitución de beca y cantidad fija con nuevos cargos. DUP-TEST200/duplicados/modal Cancelar/
validación mismo pago en dos pestañas siguen pausados hasta terminar estos escenarios.

### Estado técnico y traslado

- V89 aplicada en esta BD; V90 es la próxima migración libre. No editar V1–V89 aplicadas.
- Implementación y documentación tienen archivos modificados y NUEVOS sin seguimiento;
  no se hicieron commit/push. Usuario maneja Git manualmente: debe incluir ambos tipos,
  en especial V89, servicios/templates/scripts/pruebas nuevos y documentación actual.
- Git NO transporta registros de PostgreSQL, volúmenes, archivos privados subidos ni .env.
  Folios/ids anteriores corresponden a esta BD: en otra computadora verificar si está
  usando la misma BD/restauración; si faltan datos, pedir dirección, no recrear pagos
  automáticamente ni asumir que git pull restaura la prueba. Nunca leer/exponer .env.
- Última suite completa de implementación685Java/17Node; correcciones visuales después
  verificadas con AnticipoRenderTest +18Node, sintaxis/diff limpios. Sólo app recreada,
  salud UP. No QA visual autenticada por herramientas. Dockerfile normal sin cambios;
  DockerHub504 previo se sorteó con imagen QA/runtime local, temporales retirados.
- UI vigente: tarjetas de mensualidades con alumno/concepto/periodo/saldo, borde completo
 2px y fondo sólido claro/oscuro; Quitar sólo selección. Visualizar acuerdo baja y enfoca
 resumen automáticamente tras carga, respeta movimiento reducido. Modales de propuesta,
 pago y cancelación; controles responsivos y diseño común obligatorio.

## Ajuste visual del acuerdo anticipado — 2026-10-09

- Corrección solicitada: tarjeta con borde completo de2px, no sólo acento superior.
  Fondo sólido con variable --white/fallback y fondo oscuro explícito, sin depender
  de --surface sin definición en esta pantalla. Prueba CSS protege borde y ambos temas.

- Petición del propietario: mensualidades seleccionadas como tarjetas y desplazamiento
  automático a Así quedaría el acuerdo al visualizar. Tarjetas responsivas con tema común,
  alumno/concepto/periodo/saldo separados y Quitar alineado; mismo contenido para selección
  dinámica y render servidor. Vista previa con foco accesible, desplazamiento tras carga
  y respeto de prefers-reduced-motion; sin desplazamiento en formulario inicial.
- Sin cambios de cálculos, saldos, permisos ni migraciones. V90 sigue disponible.
- Verificado render real AnticipoRenderTest y18 pruebas Node sin fallos; JS/diff limpios.
  JAR empaquetado con QA local, sólo app recreada; recursos nuevos servidos y salud UP.
  No QA visual autenticada ni movimientos financieros por herramientas.
- Prueba humana de pago anticipado avanzó hasta validación1440 y banco19730 confirmado.
  Falta revisión humana de ambos adeudos/portal/beca; ver PUNTO EXACTO al inicio.

## V89 — acuerdos por pago anticipado — 2026-10-09

- Usuario autorizó beneficio exclusivamente por pago completo, una mensualidad/porcentaje/
  monto fijo, conservar o sustituir beca sólo en mensualidades seleccionadas, fecha límite,
  responsables/motivos, vista previa, modales y diseño común en TODOS los controles.
- Acceso: Operación escolar → Cobranza escolar → Convenios de pago → Acuerdos por pago
  anticipado (/admin/convenios-pago/anticipados). No cambia convenios ordinarios ni
  condonaciones anteriores. Propuesta propia por tutor/plantel/moneda, hasta100 mensualidades
  sin abonos ni recargos activos, colegiaturas cuyo concepto admite descuento. Los demás
  conceptos siguen por los procesos ordinarios. Mensualidades futuras se preparan con
  Generar automáticos y sus claves existentes, sin crear cargos duplicados desde el acuerdo.
- Propuesta no cambia cargos/becas. Vista previa y confirmación servidor con huella de
  selección/importes/beca/versiones; motivo y cuenta administrativa identificable obligatorios.
  Conservar suma beneficio sobre base neta; sustituir compensa la beca únicamente en las
  filas seleccionadas, sin cancelar BecaAlumno ni modificar otros meses. Otros ajustes
  previos permanecen y se muestran separados. Porcentaje/monto se reparten sin perder
  centavos; mensualidad bonifica la fila explícitamente elegida. Total siempre positivo.
- Registrar pago completo desde detalle administrativo: monto/distribución calculados,
  efectivo/caja o tarjeta/transferencia/banco; transferencias requieren comprobante.
  Pago pendiente no concede beneficio. Al validar se revalida contrato, plazo del pago,
  alcance/responsabilidad/snapshots y distribución; publica ajustes y abonos atómicamente
  con un único ingreso. Si administración verifica recibido mayor, cumple el pago completo
  y el excedente conserva el saldo a favor V87; no aumenta el beneficio.
- Cancelar pago validado restaura beneficio y beca de origen con reversas, no borra historia.
  Devolución exige revisión administrativa explícita/motivo/modal: parcial conserva
  beneficio; devolución acumulada total revierte beneficio y restaura beca. Cambios/abonos
  posteriores bloquean restauración para revisión. Aplicados protegen ajustes/cancelación
  de cargo/convenio sustitutivo. Propuesta puede cancelarse sin banco sólo tras resolver
  cualquier pago pendiente; vencida no admite nuevos pagos con beneficio.
- Listado/filtros de institución, estado real y texto paginados en DB; Excel POI con el
  mismo filtro y bloques100, desglose y formato monetario. Ayuda foco10/texto3 excluye
  ya seleccionados ANTES de LIMIT para encontrar meses11/12. Controles/tema/móvil compartidos;
  modales independientes de propuesta, registro y cancelación. Validación/cancelación/
  devolución del pago reutilizan sus modales y añaden contexto del beneficio.
- V89 crea acuerdo_anticipado/cargo, folio por secuencia, relaciones Pago/AjusteCargo,
  snapshots y unicidad parcial de cargo reservado/pago pendiente-validado; no otorga roles.
  Sólo CONVENIO_PAGO_ADMINISTRAR administra propuestas; registrar también PAGO_REGISTRAR,
  validar/cancelar/devolver mantienen permisos financieros. Recuperación no autoriza dinero.
- Prueba humana del beneficio anual pendiente; NO publicar guía confirmada todavía.
  Guías18/143 de V88 intactas. Próximo caso sugerido: dos mensualidades1000 con beca20%,
  conservar +10% adicional: base1600/beneficio160/pago1440; antes de validar no cambia
  deuda1600, después ambos720 aplicados y banco+1440; cancelar restaura800 por mes/bancoB.
  Luego mensualidad bonificada y sustitución/fijo en otros cargos, sin repetir anteriores.
- Docker Hub respondió504 en construcción normal. Se reutiliza imagen local de QA con
  Maven/JDK21; monta sólo src/pom de lectura y target generado, nunca .env. El empaquetado
  final usa temporalmente la imagen vigente como base y el JAR verificado; Dockerfile
  normal permanece sin cambios para otras computadoras. Verificación final:685 pruebas
  Java/17 Node sin fallos, sintaxis JS y diff limpios. DDL/consulta final verificados con
  EXPLAIN en BEGIN/ROLLBACK. Sólo app recreada; salud UP, V89 aplicada y scripts nuevos
  disponibles por HTTP. Al desplegar inicialmente había cero acuerdos; después propietario
  creó/validó el acuerdo de prueba (ver continuidad al inicio). Sin operaciones financieras
  por herramientas,
  cambios de volúmenes ni QA visual autenticada. Archivos temporales de empaquetado
  retirados. Siguiente migración V90. DUP-TEST200/two-tabs siguen pendientes.

## V88 — guía de saldo a favor entre hermanos confirmada — 2026-10-09

- Propietario confirmó reporte300/recibido400/aplicación300 y saldo100; aplicación100
  al hermano, reversión individual y reaplicación final. Banco no cambia por esas
  operaciones, ambos adeudos terminan0 y disponible0. No repetir capturas existentes.
- V88 publica guía CONFIRMADA de diez etapas/cuatro acciones cada una: preparación,
  cuotas, selección/generación, reporte familiar, importe real validado, aplicación,
  comprobación, deshacer individual y reaplicación. Incluye filtros sin efecto financiero,
  motivos/modales, saldo familiar y banco B+400. No confundir reversión con Devoluciones.
- Sólo INSERT de contenido y siete permisos existentes requeridos, sin otorgar roles
  ni modificar registros financieros. SQL validado BEGIN/ROLLBACK: diez etapas/siete
  permisos. La etapa familiar conserva /familias en instrucciones, ruta editorial NULL
  porque los enlaces de guía sólo permiten rutas administrativas. V87 no se modifica.
- Verificación:671 pruebas Java y17 Node sin fallos. Sólo app reconstruida/recreada;
  salud UP y lectura confirma V88 aplicada, guía CONFIRMADA/diez etapas y18 guías/143
  etapas. Sin operaciones financieras, .env ni cambios de PostgreSQL/volúmenes.
  Sin QA visual autenticada. Siguiente migración V89.
- SIGUIENTE: analizar beneficio por pago anual (12 mensualidades/pagar11), todavía
  NO implementado ni autorizado en detalle. Definir selección de mensualidades,
  relación con becas/descuentos, pago parcial/cancelación y trazabilidad antes de codificar.
  DUP-TEST200/selección duplicada/modal Cancelar/validación del MISMO pago en dos pestañas
  permanece pausado hasta terminar los escenarios acordados. No prometer prueba anual
  con una funcionalidad no existente. No operaciones financieras mediante herramientas.

## Ajuste visual de saldo a favor — 2026-10-09

- Propietario confirmó aplicación100 al hermano: adeudo desapareció, banco sin cambio
  y portal sin disponible. Caso principal confirmado, incorporación editorial a guía
  todavía pendiente. Ahora prueba reversión individual desde historial, NO Devoluciones.
- Historial separa filtros de tarjetas por registro: alumno/concepto/monto/fecha/autor/
  motivo y operación. Filtro sólo consulta; Aplicación/Reversión no significa revisión.
  Deshacer esta aplicación despliega motivo y botón de reversión/modal para UN registro;
  no borra historia, no revierte todos los resultados ni toca banco. Controles sólo
  con permiso, aplicación reversible y pago validado, protección backend conservada.
- Verificado ajuste del historial:669 Java/17 Node sin fallos; render cubre aplicación
  reversible, aplicación histórica y reversión, sólo una acción individual con endpoint
  propio. No se cambiaron permisos, saldos ni migraciones.

- Corrección adicional: MoneyValues transforma credit-amount en hidden y crea
  money-entry visible sin esa clase; el CSS ahora estiliza también money-entry
  de la tarjeta seleccionada (borde, fondo del tema, altura44 y foco). No cambia
  formato/envío numérico. Modal existente saldo-favor añade total a aplicar;
  pruebas DOM verifican Cancelar sin guardar y Confirmar un envío. Prueba humana
  de reversión sigue pendiente, no aplicar crédito mediante herramientas.

- Propietario confirmó que ve los100 disponibles del pago PAG-20261009-000023 en
  Tus pagos del portal familiar; después confirmó aplicación al hermano, como arriba.
- Distribución muestra tarjeta de saldo a favor con cifra destacada y acción alineada;
  gestión incorpora buscador al form-grid/autocompletado compartido, controles con
  variables del tema, importes/filas responsivas y Cancelar con borde. Sin cambios
  de cálculo, permisos, movimientos o migraciones; V88 sigue disponible.
- Verificado:669 pruebas Java y17 Node sin fallos; render real del detalle validado
  y gestión verifica tarjeta/acción y buscador compartido. Sólo app reconstruida/recreada;
  salud UP y HTTP sirve estilos nuevos, sin cambios de PostgreSQL/volúmenes/.env.
  revisión visual autenticada queda a cargo del propietario. Incorporación editorial
  del caso principal pendiente; no se ejecutaron aplicaciones por herramientas.

## V87 — saldo a favor administrativo — 2026-10-09

- Implementación: el dinero disponible conserva la fórmula de Pago menos aplicaciones
  netas menos devoluciones ejecutadas; NO existe una segunda tabla de saldos ni otro
  ingreso al aplicar crédito. En Pagos recibidos → Ver → Distribución se abre
  **Gestionar saldo a favor e historial**.
- PAGO_VALIDAR permite repartir saldo entre cargos de hermanos del mismo tutor,
  institución, plantel y moneda, con responsabilidad financiera vigente, motivo obligatorio,
  vista previa y confirmación. Bloquea importes excesivos, cargos duplicados y pagos en revisión.
  PAGO_CANCELAR permite revertir una aplicación con motivo y confirmación, recuperando
  deuda/disponible sin mover banco; no aplica sobre cargos cancelados o incluidos en convenio.
- Al validar un pago pendiente, administración puede declarar un importe REAL mayor con
  motivo; se conserva monto_reportado y se publica una sola entrada por el monto real.
  Familia nunca edita su importe ni aplica saldo; sólo consulta su resumen familiar paginado.
- Autocompletado primeras diez opciones al foco/clic; búsqueda desde tres caracteres.
  Historial administrativo paginado y Excel POI respeta filtro de operación. Formularios,
  modales y tablas reutilizan admin.css/forms.css y estilos familiares, con temas y móvil.
- La migración V87 añade datos originales del reporte y metadatos auditables/idempotentes
  de aplicaciones; devoluciones/cancelaciones conservan su procedencia. No altera los
  registros financieros del propietario ni publica como confirmada una prueba no realizada.
- SIGUIENTE prueba humana: crear cargo nuevo300 para hijo A, reportar transferencia300
  con comprobante de400; admin valida recibido400, motivo obligatorio, banco B+400,
  cargo A liquidado y saldo a favor100. Crear cargo100 del hermano B mismo plantel/moneda,
  aplicar100 desde el pago original: B liquidado, disponible0, banco sigue B+400.
  Verificar portal familiar, comprobante, historial/Excel y reversión administrativa opcional.
  El excedente100 de EXC-TEST ya se devolvió: no está disponible para esta prueba.
- Pendiente posterior: analizar beneficio por pago anual (12 mensualidades/pagar11).
  La prueba DUP-TEST200 y validación desde dos pestañas queda pausada hasta terminar ambos
  escenarios. Actualizar guía sólo cuando el propietario confirme resultados humanos.
- Verificado:669 pruebas Java y17 pruebas Node sin fallos; render real de saldo a favor,
  validación y sección familiar; contratos de foco/búsqueda, centavos, duplicados,
  confirmación/cancelación. PostgreSQL validó migración y consultas con BEGIN/ROLLBACK.
  Sólo app reconstruida/recreada, salud UP y lectura confirma V87 aplicada.
  No hubo QA visual autenticada ni operaciones financieras por herramientas.
  Siguiente migración V88. Guías siguen17/133; nueva prueba humana sigue pendiente.


## V86 — devolución del excedente confirmada — 2026-10-08

- Propietario confirmó todo el caso EXC-TEST300: recibido400/TARJETA/banco,
  aplicado300/disponible100; devolución PARCIAL100 conserva abono300/saldo0, disponible0,
  banco neto B+300 y deuda anterior400 intacta. Caso pendiente de V85 queda CONCLUIDO.
- V86 publica guía CONFIRMADA de ocho etapas/cuatro acciones con conceptos/cuota/
  selección/registro/validación/devolución/modal/verificaciones y precaución contra Total.
  Sólo contenido editorial, ocho permisos existentes requeridos sin otorgar roles.
  SQL validado BEGIN/ROLLBACK: ocho etapas; ninguna operación financiera por herramientas.
- Siguiente ejemplo propuesto, aún NO confirmado: cargo nuevo DUP-TEST200 sin ajustes;
  intentar seleccionar dos veces en Nuevo pago y comprobar rechazo de duplicados sin
  guardar ingreso; quitar segunda fila, registrar único200 y cancelar modal primero;
  abrir mismo pago pendiente en dos pestañas, validar primera y reintentar validación
  desde vista vieja. Un pago/abono/ingreso200, banco B+200; otros cargos intactos.
- Evidencia: PagoServiceImpl rechaza IDs repetidos en solicitudes; ValidacionPagoServiceImpl
  bloquea pago y devuelve sin nuevo ingreso si ya VALIDADO. Modal confirma un envío.
  No prometer deduplicación universal de dos formularios nuevos independientes ni referencias
  repetidas; probar el MISMO folio. No eludir una ayuda que ya excluya el cargo seleccionado.
- Siguiente migración V87; no cambios de negocio ni .env/volúmenes en esta petición.
- Verificado:661 pruebas Java sin fallos; Node de modal compartido confirma Cancelar
  sin enviar y un envío al confirmar. Sólo app reconstruida/recreada; sin QA visual
  autenticada ni pruebas financieras realizadas por herramientas.
- Salud UP; lectura confirma V86 aplicada, guía CONFIRMADA/ocho etapas y17 guías/133
  etapas. Ninguna migración anterior editada; sólo nuevo contenido editorial.

## V85 — dos cargos del mismo alumno confirmados — 2026-10-08

- Propietario confirmó único pago1000 de INS-DOBLE-TEST600/MENS-DOBLE-TEST400 del
  mismo alumno: cuenta aumentó1000, ambos pendientes desaparecieron en portal y adeudo
  anterior400 permanece. No repetir capturas ni pagar el original anterior por tools.
- Nueva guía CONFIRMADA, ocho etapas/cuatro acciones, campos reales, selección explícita,
  modal de cuota/generación/registro/validación y verificaciones de distribución/portal/
  banco/comprobante/Excel. Sólo contenido editorial, siete permisos existentes requeridos.
  SQL validado BEGIN/ROLLBACK: ocho etapas; no otorgar roles ni modificar finanzas.
- Pendiente humano de dos cargos del mismo alumno queda CONCLUIDO. Siguiente ejemplo
  propuesto: otro cargo EXC-TEST300, recibido400/distribuido300/disponible100; validar
  banco B+400; devolver parcialmente100 desde mismo banco, conserva abono300/saldo0,
  banco B+300. Seleccionar abono300 en devolución PARCIAL100 permite vista300/0;
  servicio usa disponible100 antes de liberar abonos. No marcar ese caso confirmado.
- No habilitar aplicación posterior de anticipo que no tenga flujo existente. No cambiar
  servicios ni diseño en esta petición editorial. Siguiente migración V86.
- Verificado:659 pruebas Java sin fallos; Node devolución y cálculo disponible100/
  devolver100/aplicado300/deuda0 aprobados. Sólo app reconstruida/recreada, salud UP;
  lectura confirma V85 aplicada, guía CONFIRMADA/ocho etapas,16 guías/125 etapas.
  Sin QA visual autenticada ni operaciones financieras por herramientas.

## V84 — condonación y reversión con abono previo confirmadas — 2026-10-08

- Propietario confirmó pago parcial100 con TARJETA/cuenta bancaria sobre original500,
  condonación400 y cancelación del convenio: portal vuelve a exigir400, conserva pago100
  validado y comprobante, banco no cambia por condonar/cancelar. No fue pago en caja.
- V84 publica guía CONFIRMADA Condonar y reactivar $400 conservando un pago previo de $100,
  nueve etapas, cuatro acciones por etapa, menús/campos/modales y referencia bancaria B.
  Diferencia dinero sin asignar0 de deuda400 e importes históricos400/0/400 del acuerdo
  cancelado de deuda vigente. Actualiza referencia pendiente en guía V83 sin editarla.
- Sólo contenido editorial y permisos existentes requeridos, sin otorgar roles ni
  operaciones financieras. SQL validado BEGIN/ROLLBACK: nueve etapas/cinco permisos.
- Pendiente humano de condonación con abono de los bloques anteriores queda CONCLUIDO.
  No repetir pago/convenio ya cancelado; deuda400 queda abierta para otra prueba.
- Próximas pruebas propuestas (no confirmadas): dos cargos del mismo alumno en un pago;
  dinero adicional sin asignar y devolución del excedente; doble envío/selección y alcance
  de tutor ajeno. Más adelante conciliación interna de caja/retiros, compras y portales
  académicos, sin inferir que correcciones o pruebas automáticas sean aceptación humana.
- Siguiente migración V85. No cambios en .env, PostgreSQL/volúmenes o reglas de pagos.
- Verificado:657 pruebas Java sin fallos; sólo app reconstruida/recreada, salud UP.
  No revisión visual autenticada ni operaciones financieras por herramientas.
- Lectura tras despliegue confirma V84 aplicada, nueva guía CONFIRMADA/nueve etapas,
  quince guías/117 etapas. Permisos sólo requisitos de lectura de guía, no otorgados.

## Saldo restante destacado en revisión del pago — 2026-10-08

- Punto5 de Nuevo pago separa saldo actual, importe de este pago y Quedará por pagar
  en tres tarjetas; saldo restante tiene borde/fondo del tema y cifra mayor, adaptable
  a móvil. Explica que el saldo es estimado después de validar, no dinero sin asignar.
- Cálculos, permisos, servicio, importes enviados y modal conservados. Ejemplo500/100/400
  cubierto en DOM; no registrar operaciones financieras mediante herramientas.
- Prueba humana de abono100 antes de condonar400 sigue pendiente. Sin migración, V84 libre.
- Verificado:655 pruebas Java y16 Node aprobadas; sólo app reconstruida/recreada.
  Sin QA visual autenticada ni cambios de saldos, .env o volúmenes.

## Captura de pagos ordenada y revisión previa — 2026-10-08

- Propietario autorizó una pantalla con1 Origen y responsable,2 Adeudos que se van
  a pagar,3 Datos del pago,4 Comprobantes y observaciones,5 Revisa y registra.
  PagoForm conserva IDs/nombres/POST multipart/CSRF, importes y asociaciones.
  Quien entrega dinero pasa a Datos y observaciones a Comprobantes; total distribuido
  se ve al finalizar selección, antes de capturar método/cuenta/fecha/referencia.
- Total recibido se calcula con cargos/abonos y queda protegido en flujo normal.
  Alternativa desplegable explícita permite capturar total distinto para dinero adicional
  o sin cargos. Checkbox sólo UI, no se envía: habilita total, preserva recibido manual
  al cambiar distribución; desactivarlo vuelve al total distribuido. En devolución del
  formulario con monto mayor al distribuido se recupera alternativa abierta/activada.
  Backend y sus validaciones financieras no cambian.
- Revisión final sólo consulta: cada cargo/alumno, saldo actual, importe de este pago,
  saldo estimado restante, tutor/método/cuenta/fecha dd/MM/yyyy y archivos. Texto literal,
  no HTML dinámico. Muestra Sin seleccionar si cuenta/tutor no tienen ID seleccionado.
  Usa requested-list/payment-detail-grid/form-notice y estilos globales, móvil/temas;
  CSS adicional sólo distribución local, sin otro diseño.
- Registrar como pendiente YA tenía modal data-money-confirm=registro-pago y se
  conserva: resumen, Cancelar sin guardar, Confirmar y registrar como pendiente.
  No crear otro modal ni cambiar servicio. No modificar ayuda primeras10/saldo previo
  a paginación ni pago rápido con primer cargo fijo (sigue sin opción parcial).
- Verificado: 654 pruebas Java sin fallos y16 Node. Render Thymeleaf real normal/rápido,
  contrato orden/IDs/confirmación, DOM abono500→100/saldo400/revisión, dinero extra150/
  distribución100 y retorno a automático al desactivar; modal compartido sigue probado.
  Sólo app recreada, salud UP y HTTP sirve revisión/alternativa. Sin operaciones de
  pagos/adeudos, .env, volúmenes ni migración; V84 disponible,14 guías/108 etapas intactas.
  Revisión visual autenticada no realizada por limitación de permisos previa.
- Prueba humana pendiente sigue: Pagos recibidos → Nuevo registro → tutor → seleccionar
  cargo500 → Registrar pago parcial100 → efectivo/caja → confirmar/validar. Verificar
  pagado100/saldo400/caja+100 antes de condonar400 y revertir manteniendo100. No publicar
  todavía ese escenario como guía confirmada.


## Causa real de ayuda vacía: saldo filtrado después de LIMIT — 2026-10-08

- Propietario volvió a reportar lista vacía aunque foco/clic JS ya estaba desplegado.
  Lectura de base confirma institución1/tutor2:14 cargos EMITIDO, sólo1 con deuda;
  cero pendientes entre primeros10 ordenados por vencimiento/id. Repository paginaba
  emitidos incluyendo pagados y servicio descartaba saldos0 DESPUÉS: búsqueda vacía
  no veía el único pendiente; escribir3 ampliaba tamaño20/afinaba búsqueda.
- CargoRepository.buscarParaSolicitudPago ahora filtra saldo positivo en SQL ANTES
  de ORDER/LIMIT: total=max(original+ajustes firmados,0) menos aplicaciones firmadas.
  Becas/descuentos/recargos/reversas/devoluciones contemplados con subconsultas
  independientes; no cambiar estados EMITIDO, permisos, responsabilidad financiera
  ni orden. Servicio conserva comprobación final de saldo y alcance como defensa.
  Consulta compartida de selección en convenios también deja de paginar liquidados.
- Verificado: consulta real sólo lectura retorna1 opción pendiente entre primeras10
  (antes0). Fixture CTE ficticia prueba12 pagados, descuento total, un pendiente y
  otro con beca200/recargo80/abono880/devolución100: sólo IDs14/15, no cancelado/convenido.
  Prefijo fixture en src/test/resources/sql/cargos_pago_saldo_fixture.sql se completa
  con query real del Repository y LIMIT; ejecución READ ONLY/ROLLBACK, sin inserts reales.
- 652 pruebas Java sin fallos, nuevo contrato SQL conserva alcance y saldo previo a
  paginación;16 Node sin fallos. Sólo app recreada, salud UP. Sin migración, movimientos,
  lectura .env ni cambios de volúmenes; V84 disponible, catorce guías/108 etapas.
- Intento de revisión en Chrome bloqueado por permisos de Computer Use; no acceso
  autenticado ni sesión prestada. Evidencia es SQL de datos reales y pruebas, no afirmar
  QA visual autenticada. Corrección JS previa sola no resolvía esta causa de servidor.
- Propietario debe recargar formulario y seleccionar institución/tutor → Agregar cargo
  → cargo500 → Registrar pago parcial100, guardar/validar luego si coincide.
  Caso abono100/condonar400/revertir400 conserva100 sigue sin confirmación humana.
  No registrar pagos vía herramientas ni actualizar guía como confirmada todavía.


## Ayuda de cargos en Pagos recibidos al recibir foco/clic — 2026-10-08

- Usuario prueba abono100 sobre adeudo500 reactivado: desde Pagos recibidos → Nuevo
  registro → Agregar cargo la fila dinámica no reabría opciones hasta escribir3.
  JS sólo oía focus y descartaba apertura con resultados almacenados ocultos; el clic
  de Agregar además podía cerrar la ayuda nueva al burbujear hasta document.
- Componente local pago-form ahora consulta primeras opciones con texto vacío en foco
  y clic (backend ya limita a10 disponibles, exige institución/tutor/alcance); búsqueda
  escrita mantiene mínimo3 y debounce280ms. Permite reabrir lista oculta/limpiar en
  input ya enfocado. Guarda de burbujeo mantiene abierta ayuda de nueva fila enfocada.
- Evita duplicar petición pendiente de foco+clic por URL/contexto, aborta y versiona;
  respuestas/opciones viejas no cambian selección al cambiar tutor/institución/método.
  Fuera del control cierra; importes protegidos y acciones parciales no cambian.
- Verificado: 651 pruebas Java sin fallos y16 Node. Nuevo DOM simula Agregar/foco/clic/
  burbujeo/primeras10/búsqueda3/reapertura/limpiar/contexto y selección500 → parcial100,
  total100/saldo400. Sólo app recreada; salud UP; HTTP sirve eventos nuevos y guardas.
  Sin cambios de datos, .env, volúmenes ni migración; V84 sigue disponible.
- IMPORTANTE para orientar prueba: pago rápido desde Adeudos → Registrar pago oculta
  parcial para primer cargo fijo (HTML y JS); no se habilitó en esta tarea. Abono se
  registra desde Administración → Finanzas → Pagos recibidos → Nuevo registro:
  institución/plantel/tutor → Agregar cargo → seleccionar500 → Registrar pago parcial100
  → guardar/validar. No buscar adeudo en tabla de pagos ni pedir tutor pagarlo primero.
- Prueba humana de condonación con abono100 previo sigue pendiente. No incorporarla
  aún como guía confirmada; catorce guías/108 etapas permanecen.


## V83 — cancelación de condonación total confirmada — 2026-10-08

- Propietario confirmó cancelación del convenio y que el adeudo volvió a aparecer
  para pagar en portal familiar. V83 agrega guía CONFIRMADA Cancelar una condonación
  total: recuperar el adeudo de $500, siete etapas/cuatro acciones consecutivas:
  identificar convenio de prueba, referencia bancoB, original/portal antes,
  motivo y modal, historial/adeudo después, regreso familiar y cuenta/Excel.
- Documenta mismo original restaurado, no cancelar el cargo, ausencia de cargos nuevos,
  ni pagos/devoluciones/movimientos. Importe histórico condonado del acuerdo cancelado
  no representa condonación activa. Si está vencido, autorización de transferencia
  vencida sigue siendo independiente. No repetir operación ya cancelada.
- Confirmación humana específica: cancelación y reaparición familiar. Banco sin
  movimientos y Excel son verificaciones esperadas detalladas, no nuevas pruebas
  ejecutadas por herramientas. Abonos previos100/saldo400 quedan como escenario humano
  pendiente, aunque preservación/restauración esté cubierta automáticamente.
- V83 sólo INSERT/UPDATE editorial en tablas guía y tres permisos existentes requeridos,
  sin otorgar roles. Homologa precauciones etapas8/9 de guía de condonación V82 y aumenta
  su versión de contenido2, sin modificar migraciones anteriores. SQL probado
  BEGIN/ROLLBACK: siete etapas y ambas referencias actualizadas.
- Verificado: 651 pruebas Java sin fallos, acciones numeradas/semántica/sólo contenido.
  Sólo app recreada, salud UP; V83 aplicada, lectura confirma nueva guía CONFIRMADA y
  catorce guías/108 etapas. .env, PostgreSQL/volúmenes y registros financieros preservados.
- Siguiente migración V84. Cancelación de condonación500 sin abonos ya confirmada;
  no seguir declarándola pendiente. Próximo caso opcional: condonación con abono previo
  (original500, abono100, condonar400 y revertir400 conservando100), pedir prueba humana
  antes de publicar ese escenario. No tocar convenios reales/liquidados.


## Botones compactos también en cuotas y adeudos — 2026-10-08

- Cuotas por alumno y Adeudos de alumnos reutilizan exactamente el grupo flex de
  Aplicar filtros/Limpiar de Pagos recibidos: ancho natural, mismos controles/colores,
  adaptación móvil y tema heredado. Resto de módulos conserva su distribución.
  Sin cambios de filtros, consultas, fechas, saldos, permisos ni rutas.
- Verificado: 649 pruebas Java sin fallos, render real de los tres módulos y contrato
  de tamaño natural; imagen reconstruida, sólo app recreada y salud UP. Sin migración,
  operaciones sobre datos, lectura de .env ni cambios de volúmenes. Diff limpio.
  Revisión visual autenticada pendiente del propietario.
- V83 sigue disponible; trece guías/101 etapas de V82 sin modificaciones. Siguiente
  prueba pendiente: reversión de otra condonación total, no repetir caso ya confirmado.


## V82 — guía de condonación total confirmada y filtros compactos — 2026-10-08

- Propietario confirmó prueba de condonación total: deuda ya no exigible en admin ni
  portal familiar y saldo bancario sin cambio. V82 agrega guía CONFIRMADA
  Condonar totalmente un adeudo mediante convenio: $500 a $0, nueve etapas detalladas
  y cuatro acciones por etapa: reutilización/preparación opcional, cuota/generación
  selectiva, referencia bancoB, selección, modalidad/motivo, modal, historial y portal.
- Usa nombres/menús actuales, filtros Con saldo pendiente/Incluido en convenio,
  monto0 protegido, ausencia de nuevos cargos/pagos/movimientos y autorización.
  No presentar condonación como Pagado ni duplicar ajuste. Instrucciones incluyen
  consulta/exportación sin afirmar aceptación humana específica de cada subpaso.
  Cancelación/reapertura de esta condonación sigue pendiente de ensayo humano.
- Cinco permisos existentes requeridos por guía, sin asignación de roles; sólo
  INSERT editorial en tablas guía. SQL probado en BEGIN/ROLLBACK (nueve etapas).
  Se corrigió referencia paso4. que el parser interpretaba como acción adicional.
- Pagos recibidos agrupa Aplicar filtros/Limpiar en fila flex al final de filtros:
  botón con ancho natural, sin estirarse a columna; misma altura/colores/borde globales.
  CSS localizado, flex-wrap móvil/tema heredado, otros módulos no cambian.
- Verificado: 649 pruebas Java sin fallos y15 Node; contrato nueve etapas/captura
  explícita, sólo contenido y botón compacto. Render de tres módulos comprueba
  grupo de acciones únicamente en Pagos. Sólo app recreada, salud UP; V82 aplicada,
  lectura confirma guía CONFIRMADA/nueve etapas y trece guías/101 etapas totales.
  .env, volúmenes y operaciones financieras preservados; sin QA visual autenticada.
- Siguiente migración V83. Prueba principal V80 ya confirmada, no repetirla como pendiente.
  Próximo caso propuesto: cancelar otra condonación total con motivo/modal para verificar
  restauración del saldo sin movimientos y conservación de abonos si existen; solicitar
  prueba al propietario antes de publicar esa reversión como confirmada.


## V81 — rangos de fechas en cuotas, adeudos y pagos — 2026-10-08

- Cuotas por alumno filtra por fecha límite prevista: única usa vencimiento exacto;
  mensual busca algún vencimiento en rango usando día del mes limitado a meses cortos
  y vigencia parcial, igual a planificar de CargoServiceImpl. No genera ni consulta
  solamente cargos emitidos: muestra configuraciones. Función PostgreSQL inmutable
  cuota_vencimiento_en_rango calcula en tiempo constante (máximo dos meses candidatos).
- Adeudos permite Fecha límite de pago (predeterminada) o Fecha de registro del cargo.
  Pagos usa fecha_pago local según zona de institución, no fecha de validación; ambos
  extremos incluidos con todas las horas del último día. Desde/Hasta independientes.
- DTO conserva fecha puntual de Asistencia y constructores existentes. Rango invertido
  se valida cliente/servidor: listado conserva captura y muestra ayuda sin consultar;
  Excel valida antes de generar respuesta. Texto, situación, alcance, COUNT y paginación
  siguen en base. Excel conserva rango/tipo y todos los criterios en bloques100.
- Tres pantallas usan mismos estilos de controles: sección Filtrar por fechas responsiva,
  accesos Este mes/Mes anterior/Este año/Rango personalizado (calendario México servidor,
  no UTC del navegador). Selección no envía sola; Aplicar filtros queda al final.
  Limpiar quita todo; links de paginación/Excel conservan rango y tipo.
- Verificado: 646 pruebas Java y15 Node sin fallos. Hibernate/PostgreSQL compila Criteria
  para tres entidades; render Thymeleaf real de tres pantallas conserva fechas/links.
  DTO/controlador comprueban rango invertido, captura/paginación; POI dos páginas.
  SQL de casos12 + límites locales probado con función en BEGIN/ROLLBACK, y repetido
  sólo lectura tras deploy: febrero28/29, vigencias parciales, bordes exactos/abiertos.
- Sólo app recreada; salud UP, V81 aplicada, doce guías/92 etapas preservadas.
  Sin operaciones sobre datos financieros, sin lectura de .env ni cambios de volúmenes.
  Revisión visual autenticada pendiente del propietario; no presumir prueba humana.
- Siguiente migración V82. Sigue pendiente prueba humana de condonación total500→0
  de V80 y publicación de esa guía sólo tras confirmación. Conservar casos anteriores.


## Filtros por situación real del adeudo — 2026-10-08

- Adeudos de alumnos reemplaza selector Emitido por Situación del adeudo:
  Todos, Con saldo pendiente, Pendiente sin abonos (no vencido), Pago parcial,
  Vencido, Liquidado — saldo $0, Incluido en convenio y Cancelado.
  Parcial vencido aparece en ambos filtros y tabla como Parcial · Vencido.
  Liquidado cubre pagos o ajustes, no implica dinero recibido.
- SituacionAdeudo mantiene estados persistidos sin modificaciones; filtra emitidos
  usando original + ajustes firmados (becas/descuentos/recargos/reversas), total mínimo0,
  menos aplicaciones firmadas (abonos/devoluciones/cancelaciones). Subconsultas
  independientes evitan multiplicar importes al cruzar colecciones. Cancelado/convenido
  no entra en filtros de deuda o liquidado aunque conserve importes históricos.
- Criteria aplica situación, texto y alcance antes de COUNT y paginación en PostgreSQL.
  Excel POI usa la misma consulta/filtro en bloques100 y mismas etiquetas. Enlaces
  antiguos EMITIDO siguen admitidos, pero no se ofrece esa opción en nuevo selector.
  Valor desconocido no devuelve registros. Conserva diseño compartido y ayuda neutra.
- Verificado: 640 pruebas Java sin fallos y14 pruebas Node. Nuevas pruebas cubren
  estados/beca-recargo/devolución/ajuste/liquidación, SQL compilado con Hibernate y dialecto
  PostgreSQL sin acceder a datos (COUNT/LIMIT/subconsultas) y Excel con dos páginas.
  No prueba de operaciones reales ni revisión visual autenticada. Sólo app recreada,
  salud UP; sin cambios de datos, migraciones, .env ni volúmenes.
- Sin migración: V81 sigue disponible. Prueba humana de condonación total V80 sigue
  pendiente. Revisar estos filtros con casos existentes y continuar convenio500→0;
  no publicar condonación como confirmada ni tocar convenio800 liquidado.


## V80 — condonación total mediante convenio — 2026-10-08

- Propietario autorizó modalidad explícita en Convenios de pago: Tipo de convenio
  permite Convenio con monto por pagar o Condonación total del saldo pendiente.
  Total fija monto por pagar0 y fecha de efecto igual al acuerdo; calcula condonación
  desde saldo pendiente bloqueado en servidor, nunca desde el importe enviado.
- Conserva motivo/condiciones, administrador persistido identificado (ID/nombre/fecha),
  autorización y auditoría. Acceso de recuperación no puede condonar. Usa permisos
  existentes, alcance institucional/tutor/cargos y bloqueos; sin asignaciones de roles.
- Originales quedan CONVENIDO, acuerdo CONDONADO_TOTAL (Condonado totalmente).
  No crea cargos0, pagos, comprobantes ni movimientos financieros; abonos previos
  permanecen. Estado diferenciado de Pagado, filtro/Excel POI paginado con mismos
  criterios y montos0/condonado. Detalle de pagos previos no presenta saldo convenido
  como deuda exigible.
- Confirmación compartida resume saldo/monto0/condonación/motivo/fecha/sin movimiento;
  Cancelar/Escape/fondo no envían. Gestión permite cancelar con motivo y modal,
  restaurando saldo pendiente original sin alterar abonos ni cuentas. Si originales
  cambiaron importes/abonos después del acuerdo se bloquea reversión para revisión.
- V80 agrega modalidad/autorización/restricciones; asociación original conserva historia
  con activo e índice único parcial (un acuerdo activo por cargo, permite reutilizar tras
  cancelar). Trigger impide cargos nuevos bajo condonación total. Modelo y README
  actualizados. DDL probado previamente con BEGIN/ROLLBACK.
- Verificado: imagen construida con 633 pruebas Java sin fallos y 14 pruebas Node;
  render real, DTO/servicio, cancelación, pago previo, autorización, doble envío y Excel.
  Sólo app recreada, salud UP; lectura confirma V80 aplicada, dos convenios históricos
  MONTO_ACORDADO y doce guías/92 etapas. No operaciones financieras por herramientas,
  sin lectura de .env ni borrado de datos/volúmenes. Sin QA visual autenticada.
- Siguiente migración V81. Siguiente paso: prueba humana500 pendiente→condonación500/
  por pagar0, autorización visible, portal sin deuda y cuenta sin cambios; probar
  Cancelar del modal primero. Luego reversión opcional→500 exigible sin movimiento.
  Abono100 previo sobre500 debe condonar sólo400; probado automáticamente, pendiente
  ensayo humano. No publicar ninguno como guía confirmada hasta resultado del propietario.
  No tocar convenio800 liquidado ni casos anteriores confirmados.
- Los bloques V79 y anteriores son historial: propuesta antes pendiente ya autorizada
  e implementada por esta V80. No repetir la propuesta como pendiente.


## V79 — cancelación de convenio sin pagos confirmada — 2026-10-08

- Propietario confirmó ejemplo completo y reiteró que originales300/200 reaparecen en
  el portal familiar tras Cancelar y reactivar adeudos. Caso nuevo400/condonado100/
  cargos240/160 queda documentado, sin tocar convenio800 anterior ya liquidado.
- V79 agrega guía CONFIRMADA Cancelar un convenio sin pagos: volver a adeudos de $300
  y $200, ocho etapas detalladas: conceptos, cuotas300/200, generación500, bancoB,
  acuerdo400, revisión sin pagar, modal cancelar/repetir/confirmar, verificación admin
  y familiar. Explica cancelar acuerdo no condona originales y no registra movimientos.
- Cinco permisos existentes requeridos sin asignar roles; sólo contenido editorial.
  Verificado: 622 pruebas Java sin fallos; SQL probado BEGIN/ROLLBACK (ocho etapas).
  V79 aplicada, salud UP, lectura confirma guía CONFIRMADA y doce guías/92 etapas.
  Sólo app recreada, sin pagos/cancelaciones/movimientos vía herramientas; .env y
  volúmenes preservados, diff limpio. Confirmación funcional proviene del propietario.
- Siguiente migración V80. Nueva consulta del propietario: convenio sobre deuda500,
  acordado0/condonado500 rechazado. No se implementó cambio: formulario DecimalMin0.01,
  servicio acordado>0 y restricciones DB de convenio y cargos nuevos exigen positivos.
  Condonar totalmente ya es posible mediante descuento manual del saldo si concepto
  permite descuentos y total no queda debajo de abonos; no equivale a cancelar convenio.
- Propuesta pendiente de decisión, NO implementada/autorizada: opción explícita
  Condonación total dentro del flujo de convenio con motivo/auditoría y sin crear
  nuevos adeudos0. Requiere diseñar estados, reversión, permisos y consistencia financiera,
  no sólo bajar mínimo del input. No declarar escenario500/0 probado ni publicarlo
  como guía confirmada. Siguiente conversación: elegir si usar ajuste existente o
  implementar modalidad explícita en convenios.


## Confirmación para cancelar convenio sin pagos — 2026-10-08

- Convenios → Ver → Gestión → Cancelar y reactivar adeudos abre modal compartido
  money-input (cancelacion-convenio), no ejecuta al primer clic. Resume folio/tutor,
  originales por cargo/alumno/saldo a reactivar y nuevos por cargo/alumno/importe a
  cancelar; totales de origen/acuerdo/condonación, motivo literal y ningún efecto bancario.
- Cancelar/Escape/fondo cierran sin enviar y conservan motivo/foco. Sólo Confirmar
  cancelación del convenio usa requestSubmit original con validación, POST/version/CSRF
  y aprobación de un envío. No se modifica servicio: pagos aplicados/estados/version/
  alcance/bloqueos siguen siendo revalidados por el servidor. Metadata visual no autoriza.
- Datos se renderizan desde DTO con atributos escapados y spans hidden sin nombres de
  envío; no confiar en metadata recibida. CSS/JS del modal común, sin diseño paralelo.
  Convenio cancelado no muestra formulario. Actualizado contrato estático de cuota por
  nueva acción Cancelar, sin cambiar comportamiento de cuotas.
- Verificado: 620 pruebas Java y13 suites Node sin fallos. Render Thymeleaf real cubre
  metadata/escape/ruta/version y ausencia en cancelado; DOM300/200→240/160 con500/400/100
  comprueba resumen, texto literal, cancelar/Escape/fondo/validación sin envío, foco y
  confirmación única. App reconstruida/recreada, salud UP, diff limpio. Sin operación
  financiera por herramientas ni QA visual autenticada; .env/base/volúmenes intactos.
- Sin migración, V79 sigue disponible. Once guías/84 etapas; no publicar cancelación como
  confirmada todavía. Propietario espera en paso7 de OTRO convenio sin pagos:
  revisar primero Cancelar del modal y luego confirmar para comprobar originales300/200
  reactivados, nuevos240/160 cancelados, bancoB y convenioCancelado/motivo. No tocar
  convenio800 previamente liquidado. Documentar caso completo sólo tras resultado humano.


## V78 — etiqueta clara del concepto del convenio — 2026-10-08

- Formulario y modal usan Concepto de cobro del convenio en lugar de Concepto del nuevo
  cargo. Placeholder Código o nombre del concepto de cobro; ayuda permanente field-help
  vinculada con aria-describedby explica identifica nuevos adeudos, ejemplo Convenio de pago.
  Ayuda separada del autocomplete-status para que búsquedas no la borren.
- Conserva conceptoCobroId, autocompletado, alcance, permisos, selección y reglas financieras.
  V78 sólo homologa nombre en una etapa editorial de la guía confirmada del convenio800;
  no modificar migración V76 aplicada ni publicar cancelación como probada.
- Verificado: 619 pruebas Java y13 suites Node sin fallos; SQL probado BEGIN/ROLLBACK,
  V78 aplicada y lectura confirma etiqueta actualizada. Sólo app recreada, salud UP,
  diff limpio. .env/volúmenes preservados y sin operaciones financieras por herramientas.
- Siguiente V79. Propietario llegó al paso5 de cancelar OTRO convenio sin pagos:
  originales300/200 → convenio400/condonado100/nuevos240/160. Elegir CONV-ACUERDO-TEST
  como concepto del convenio y CONV-CANCEL-TEST identifica sólo los originales.
  Cancelación todavía pendiente; no tocar convenio800 liquidado ni regenerar cuotas.


## V77 — navegación separada de generación y botones de selección — 2026-10-08

- Generar automáticos → vista previa cambia Cancelar generación por ← Volver a adeudos
  de alumnos: enlace GET fuera del formulario POST, debajo de confirmación y con divisor/
  ayuda Volver al listado no genera adeudos ni modifica tus cuotas. No cancela movimientos.
- Seleccionar todos/Desmarcar todos usan borde/fondo tintado, altura44, hover y foco visibles
  con variables comunes y adaptación móvil. CSS limitado a charge-generation-preview;
  sin cambiar recargos, selección, paginación, modal, token, permisos ni reglas de generación.
- V77 editorial homologa el nombre del regreso en seis guías existentes, sin publicar
  prueba de cancelación de convenio como confirmada. Once guías/84 etapas conservadas.
- Verificado: 619 pruebas Java sin fallos, contrato comprueba enlace fuera/después de POST
  y estilos. SQL probado BEGIN/ROLLBACK (seis guías), V77 aplicada, salud UP y lectura
  confirma cero nombres anteriores. App reconstruida/recreada, sin operaciones financieras
  ni cambios de volúmenes/.env; diff limpio. Revisión visual autenticada pendiente.
- Siguiente migración V78. Prueba humana de cancelar OTRO convenio sin pagos en curso:
  dos cuotas originales300/200 con CONV-CANCEL-TEST ya visibles en paso3. Después generar
  únicamente esos dos, acuerdo400/condonado100/nuevos240/160; cancelar sin pagar debe
  restaurar originales300/200, cancelar nuevos y conservar bancoB.
  No tocar convenio anterior800 ya liquidado. Tras recrear app, renovar vista previa/
  login si es necesario: selección previa ligada a sesión puede dejar de ser válida.


## V76 — convenio familiar liquidado confirmado y botón Ver de pagos — 2026-10-08

- Propietario confirmó que reportó y validó los dos cargos nuevos480/320 y todo funcionó
  correctamente. Caso de convenio de originales600/400, acuerdo800, condonación200,
  pago familiar único800 queda confirmado por el propietario, no ejecutado con tools.
- Pagos recibidos → acción Ver usa new-button común mediante clase condicional sólo
  para PAGOS. Mantiene ruta /admin/pagos/{id}/editar, permisos y texto; otros módulos
  conservan edit-link. No cambia botones del detalle, servicios ni reglas financieras.
- V76 agrega guía CONFIRMADA Convenio de dos hijos y liquidación familiar: $480 + $320,
  diez etapas con origen menú/módulo/filtros/campos y modales de cuota/generación/convenio/
  reporte familiar/validación. Incluye bancoB+800, nuevos saldos0, originales Convenido,
  convenio Cumplido y comprobantes; fechas ejemplo08/10 y20/10 adaptables al ciclo.
  Sólo tablas de contenido y siete permisos existentes, sin asignaciones de rol.
- Verificado: SQL editorial probado BEGIN/ROLLBACK; 618 pruebas Java y13 suites Node
  sin fallos; app reconstruida/recreada, salud UP. V76 aplicada; lectura confirma guía
  CONFIRMADA/diez etapas y once guías/84 etapas totales. Diff limpio. Sin pagos ni
  movimientos mediante herramientas, base/volúmenes/.env preservados. Diseño del botón
  a confirmar visualmente por propietario, no revisión autenticada por tools.
- Siguiente migración V77. Siguiente prueba propuesta: cancelar OTRO convenio antes de
  cualquier pago; debe cancelar sus cargos nuevos y reactivar originales sin mover cuenta.
  No cancelar el convenio ya liquidado ni publicar cancelación como confirmada todavía.
  Añadir instrucciones del modal familiar a otros recorridos existentes en actualización
  editorial posterior; esta guía nueva ya incorpora Confirmar y enviar a revisión.


## Confirmar reporte familiar de transferencia antes de enviar — 2026-10-08

- Portal familiar → Reportar transferencia → Enviar transferencia a revisión usa el
  modal compartido money-input con acción transferencia-familiar. Resume cada cargo/
  alumno/importe, total, cuenta, fecha dd/MM/yyyy/hora, referencia, pagador/observaciones
  cuando existen y nombres de los comprobantes. Texto literal mediante textContent.
- Cancelar/Escape/fondo no envían y conservan captura/archivos, restaurando foco.
  Confirmar y enviar a revisión usa requestSubmit original, validación HTML, multipart
  y CSRF; autorización del modal vale sólo un envío y doble clic de confirmación no repite.
  Guardas contra cargos duplicados siguen ejecutándose antes del modal y antes del POST.
- Aclara que reportar no ejecuta una transferencia bancaria: la escuela debe validar
  antes de aplicar a adeudos y cuenta. Sin cambios de servicio, importes ni reglas.
  CSS usa card/soft/mint/line del portal familiar, compatible con temas y móvil, sin
  reutilizar colores administrativos fijos en esa pantalla. Soporte comparte formulario.
- Verificado: 615 pruebas Java sin fallos y13 suites Node. DOM480/320 total800, fecha/
  referencia/cuenta, archivos y texto literal, cancelar/Escape preservados y un envío.
  Contrato conserva POST/multipart/CSRF y estilo; actualizado contrato estático de cuota
  por ampliación de acciones Cancelar sin cambiar comportamiento de cuotas.
  App reconstruida/recreada, salud UP, diff limpio. Sin QA visual autenticada ni reportes
  reales mediante herramientas; propietario debe confirmar modal en su prueba en curso.
- Sin migración; V76 disponible. .env/base/volúmenes/saldos intactos. Convenio800 sigue
  pendiente de reporte familiar y validación. Antes de publicar guía completa confirmar
  resultado con propietario; incorporar paso de este modal en recorridos familiares.


## Contador discreto sólo para administración y maestros — 2026-10-08

- Portal familiar no adjunta el contador al DOM: mantiene reloj interno, aviso al quedar
  dos minutos, Continuar sesión y Cerrar sesión. No se cambia duración ni lógica servidor.
- Admin/maestros insertan indicador dentro de form-session-actions/teacher-user, antes
  del control de tema; cierre de sesión sigue último y al extremo derecho. Si no hay
  grupo compartido, inserta antes del último bloque de cabecera, no después de Salir.
- Presentación compacta tipo cápsula, icono de reloj, texto tenue/tabular, borde suave,
  foco visible y advertencia ámbar sólo cerca del vencimiento. Respeta claro/oscuro,
  selectores docentes existentes y adaptación móvil. Modal y validación CSRF intactos.
- Verificado: 614 pruebas Java y13 suites Node sin fallos. Prueba DOM comprueba ausencia
  de indicador familiar y aviso/renovación conservados, indicador admin/docente y cierre
  último. App reconstruida/recreada y salud UP; diff limpio. Revisión visual autenticada
  pendiente del propietario; sin operaciones financieras ni cambios de datos/volúmenes.
- Sin migración; V76 disponible. Convenio800 pendiente de pago familiar y validación
  antes de publicar guía completa. .env no leído. Recargar portales para revisar ajuste.


## Contador y aviso de sesión con renovación explícita — 2026-10-08

- application.properties fija server.servlet.session.timeout=30m. Componente global en
  pantallas admin/familia/maestros (no login) muestra Sesión · mm:ss en cabecera, incluidos
  formularios. A los120 segundos abre modal común, responsivo y claro/oscuro con tiempo
  restante, Continuar sesión y Cerrar sesión. Renovar no recarga ni guarda formularios.
- GET /sesion/estado devuelve reloj/vencimiento/duración y CSRF con no-store; no crea sesión
  ni renueva actividad. POST /sesion/renovar exige autenticación y CSRF y renueva desde
  ahora por la duración real de la sesión. Una sesión ya caducada nunca se resucita.
- El contenedor toca lastAccess al consultar: filtro de seguridad posterior a CSRF mantiene
  actividad lógica independiente y exige su plazo en TODAS las peticiones autenticadas.
  Sondeos/renovación no se contabilizan automáticamente; sólo POST aceptado renueva.
  Recursos css/js/favicon no renuevan; peticiones operativas siguen contando como actividad.
  No registrar este filtro además como componente servlet, para evitar doble ejecución.
- Contador usa diferencias de tiempo del servidor y performance.now; sincroniza fetch y
  pestañas con BroadcastChannel, ignora respuestas antiguas y consulta al recuperar foco/
  visibilidad o BFCache. Sondeo30s no renueva. Sin conexión no presume éxito ni reinicia
  contador: permite reintentar; si servidor confirma caducidad va al acceso propio.
- Cerrar sesión conserva POST/CSRF/origen, invalidación y redirecciones existentes.
  Soporte dentro de rutas admin permanece administrativo. No se agregó autoguardado:
  planeaciones conservan sus borradores existentes; no tocar cobranza ni saldos.
- Verificado: 614 pruebas Java sin fallos y13 suites Node, incluyendo reloj/aviso2min/
  renovación30min, expiración lógica pese al sondeo, respuestas viejas y acceso propio.
  App reconstruida/recreada, salud UP; GET sin sesión401, POST sin CSRF redirige al acceso
  sin ejecutar renovación. Todas las pantallas completas cargan theme.js. Diff limpio.
  Sin revisión visual autenticada ni espera real30min; propietario debe confirmar diseño.
- Sin migración ni operaciones financieras, .env no leído, PostgreSQL/volúmenes intactos.
  V76 disponible, diez guías/74 etapas conservadas; convenio800 sigue pendiente de pago
  familiar y validación administrativa antes de publicar su guía como confirmada.


## Sesión y cierre de sesión por portal — 2026-10-08

- Caducidad y cierre voluntario redirigen directamente a /familias, /maestros/acceso
  o /login según portal; usan sesionExpirada o logout respectivamente. POST con CSRF
  caducado y /logout con origen familiar/docente conservan el acceso correspondiente.
- Consultas AJAX familiares de cargos/cuentas y autocomplete compartido reconocen los
  accesos propios, no fuerzan /login administrativo. Context path conservado; cookie
  caducada eliminada con HttpOnly y Secure en HTTPS. Errores reales de permisos siguen403.
- Login familiar ya no muestra enlace al acceso administrativo. Esto no vuelve secreta
  la URL: POST de autenticación sigue /login; la seguridad sigue basada en permisos.
- No se cambia duración: sin override en configuración del proyecto, Spring Boot usa
  30 minutos de inactividad. Peticiones renuevan tiempo; escribir/mover mouse no lo hace.
- Verificado: 604 pruebas Java y doce suites Node sin fallos; app reconstruida/recreada,
  salud UP. Sin migración ni movimientos financieros; V76 disponible. Prueba visual de
  cierre/caducidad con usuarios reales pendiente del propietario. No se leyó .env.
- Convenio de dos hijos sigue pendiente de pago familiar800 y validación administrativa;
  no publicar guía completa aún. Diez guías/74 etapas conservadas.


## Portal familiar sin cargos repetidos en un reporte — 2026-10-08

- Ayudas del reporte filtran por ID los cargos elegidos en otras filas, sin excluir la
  selección de la propia fila. Si queda vacía la consulta por selecciones previas, explica
  que ya fueron agregados y cómo buscar otros; no dice que no exista deuda.
- Guarda en clic: una opción de lista desactualizada no puede repetir cargo; mensaje
  Este adeudo ya está agregado al pago y no modifica ID/importe/total/cuenta. Limpiar
  una selección permite volver a ofrecerla al consultar. Mantiene foco/principio10 y búsqueda3.
- Guarda adicional antes de submit bloquea duplicados restaurados/manipulados, muestra
  aviso en fila con aria-invalid y la lleva a la vista. Backend mantiene HashSet y su
  validación Un cargo sólo puede seleccionarse una vez; no se cambia servicio ni saldos.
- Verificado: 601 pruebas Java sin fallos; contrato protege tres guardas y control servidor.
  Doce suites Node pasan: prueba DOM320/480 → total800, selección propia, limpieza/liberación,
  lista desactualizada y envío repetido. Sólo app recreada, salud UP y JS servido correcto.
  Diff limpio; sin operaciones financieras ni cambios de base/volúmenes. Revisión visual
  autenticada pendiente del propietario; no se reportó ni validó transferencia con tools.
- Sin migración, V76 sigue disponible. Diez guías/74 etapas conservadas. Convenio creado
  pendiente de pago familiar800 y validación, no publicar guía completa todavía.
- Propietario recarga Reportar transferencia, selecciona cargo320 y en otra fila480;
  el320 no debe ofrecerse en la segunda. No guardar/reportar/validar pagos mediante tools.

## Detalle de convenio por pestañas y botones comunes — 2026-10-08

- Propietario confirmó creación del convenio con importes del ejemplo; liquidación y
  cancelación del convenio siguen pendientes. No publicar su guía como completa todavía.
- Listado Convenios → Ver usa new-button, mismo borde/espaciado del sistema; detalle tiene
  botón de volver común y navegador Resumen/Adeudos/Gestión del patrón de alumnos/pagos.
  Folio/tutor/situación y métricas globales permanecen fuera de paneles.
- Resumen presenta fechas/motivo/condiciones; Adeudos separa originales conservados y nuevos
  por alumno; Gestión aloja cancelación y motivo histórico. Formulario de cancelar usa
  entity-form/form-section/form-grid, textarea wide, ayuda neutra, validación requerida y
  botón danger-button; permisos, rutas, CSRF y reglas de operación se mantienen.
- Errores de captura/negocio abren Gestión y conservan motivo. Cancelación exitosa vuelve a
  Gestión con mensaje/estado. Componente compartido conserva teclado/hash/temas/móvil.
  Sólo cambio de presentación/controlador; sin migración, V76 sigue disponible.
- Verificado: 600 pruebas Java sin fallos, incluido render Thymeleaf real vigente/cancelado,
  campos/escape, pestañas y selección servidor, más contrato de botón/listado y errores.
  Sólo app recreada, salud UP; diff limpio. Sin migración/operaciones financieras ni cambios
  de PostgreSQL/volúmenes. Revisión visual autenticada a cargo del propietario.
- Prueba en curso: volver al detalle del convenio → Adeudos, comprobar nuevos480/320; luego
  reportar ambos desde portal familiar800 y validar → bancoB+800/saldos0/convenioCumplido.
  No cancelar este convenio antes de liquidarlo; la cancelación se prueba con otro caso.
  No ejecutar convenios/pagos/cancelaciones por herramientas.

## V75 — confirmación de generación y captura clara de convenio — 2026-10-08

- Adeudos → Generar automáticos → vista previa: botón Cancelar generación vuelve al listado
  sin emitir; Confirmar y generar adeudos seleccionados abre modal con selección global de
  todas las páginas/cantidad/importe y fecha corte. Cancelar en modal conserva selección;
  sólo Confirmar y generar adeudos envía. Token/alcance/caducidad/revalidaciones intactos.
- generation-selection prepara inputs en primer submit pero no bloquea botón hasta el envío
  aprobado por modal. money-input marca ese evento sin mantener aprobación para envíos futuros.
  Cancelar no deja Generando selección ni botón deshabilitado. Recargos sin modal conservan flujo.
- Convenio separa Resultados disponibles (no incluidos) y Cargos agregados al convenio en
  tarjetas con títulos/ayudas y contador; selección usa acento menta común, variante oscura,
  tarjetas de superficie compartida y estado vacío. Agregar/quitar mantienen los IDs y montos.
- Confirmar convenio abre resumen de tutor/concepto/originales/saldo seleccionado/nuevo monto/
  condonación/fechas/descripción/motivo/condiciones. Cancelar/Escape no crean ni limpian captura;
  Confirmar y crear convenio usa validación/CSRF/requestSubmit originales. Crear no mueve fondos.
- V75 sólo actualiza seis pasos editoriales de guías confirmadas sobre generación, sin publicar
  convenio como confirmado. Diez guías / 74 etapas. No se modifican servicios ni reglas financieras.
- Verificado: 597 pruebas Java sin fallos, SQL probado BEGIN/ROLLBACK; once suites Node
  correctas, incluyendo selección sin bloquear antes de modal, cancelación preservada,
  envío aprobado y resumen de convenio $1000/$800/$200. Pruebas estructurales agregadas.
  V75 aplicada, diez guías/74 etapas conservadas; sólo app recreada, salud UP; base/volúmenes
  intactos, sin operaciones financieras. Diff limpio; revisión visual autenticada pendiente.
- Siguiente V76. Propietario sigue preparando convenio de dos hijos: originales600/400,
  acordado800, condonado200, nuevos480/320. Primero confirmar creación y luego pago familiar800
  validado/bancoB+800/saldos0/convenioCumplido. Cancelación del convenio se prueba después en
  otro caso. No crear convenios/cargos/pagos por herramientas.

## V74 — tres guías de cancelación confirmadas — 2026-10-07

- Propietario confirmó la secuencia completa sobre $600: pendiente cancelado sin cambiar
  caja/deuda; pago nuevo validado caja B+600/deuda0, luego cancelado caja B/deuda600;
  finalmente adeudo cancelado conservando histórico600/abonos0/saldo0, cajaB y sin pendiente
  familiar. Pagos anteriores conservados Cancelados. Confirmación humana, no por herramientas.
- V74 agrega tres guías CONFIRMADA: Cancelar un pago pendiente registrado por error: $600
  (siete etapas), Cancelar un pago validado registrado por error: $600 (cinco) y Cancelar
  un adeudo sin abonos vigentes: $600 (cinco). Total diez guías / 74 etapas.
- Cada recorrido precisa menú/módulo/búsqueda/filtro/fila/pestaña/campos/modales/confirmación
  y resultados. Primera prepara concepto/cuota/selección600/cajaB; segunda reutiliza deuda
  y registra otro pago; tercera retira obligación después de corregir pagos. No repetir al
  consultar, no mezclar devolución/descuento ni regenerar cuota única cancelada.
- Sólo tablas editoriales/requisitos de permisos existentes; no asigna roles ni ejecuta
  cuotas, cargos, pagos, cancelaciones, ajustes o movimientos. Mismo diseño/filtros/paginación
  y Excel del módulo existente. Perfil autorizado debe contar con permisos de cada proceso.
- Verificado: 594 pruebas Java sin fallos, contenido/numeración/botones y límites editoriales.
  SQL probado con BEGIN/ROLLBACK, tres guías/17 etapas y 16 relaciones con permisos existentes.
  V74 aplicada; lectura confirma tres nuevas CONFIRMADA y diez guías/74 etapas totales.
  Sólo app recreada, salud UP; base/volúmenes preservados, sin operaciones financieras ni
  QA visual autenticada. Diff limpio; presentación de guías a revisar por propietario.
- Siguiente migración V75. Consultar las guías en Administración → Seguridad y soporte →
  Guía de procesos. Los pendientes de cancelaciones descritos anteriormente quedan concluidos
  para estos escenarios exactos; no declarar probados convenios, saldo insuficiente,
  concurrencia, cancelación con devoluciones u otras variantes. Próximo caso a acordar.

## Botones de gestiones opcionales con estilo común — 2026-10-07

- Al separar Transferencia de cargo vencido y Corregir y reemplazar cargo, sus enlaces
  quedaron fuera de entity-form/cargo-detail-actions, únicos ancestros incluidos en
  el selector del botón. No era falta de clase HTML: faltaba alcance CSS del patrón.
- forms.css extiende la misma regla detail-close-button a charge-tab-panel: inline-flex,
  borde/fondo de variables de tema, radio, espaciado, altura44, ancho máximo y texto centrado.
  Foco de teclado visible común; sin paleta paralela, rutas/acciones/permisos sin cambios.
- Verificado: 591 pruebas Java sin fallos; prueba estructural protege selector y enlaces
  fuera del formulario. Sólo app recreada, salud UP; CSS servido contiene selector nuevo,
  diff limpio. Sin migración ni operaciones financieras; base/volúmenes intactos. Revisión
  visual autenticada pendiente. V74 sigue disponible; preservar V73 y cambios anteriores.
- Recargar Adeudos → Ver → Gestión → Otras gestiones opcionales para revisar presentación.
  Prueba de cancelación del adeudo600 sigue pendiente; no ejecutarla mediante herramientas.

## Modal para cancelar un adeudo — 2026-10-07

- Gestión → Cancelar cargo al alumno abre Confirmar cancelación del adeudo, con número,
  alumno, concepto, total ajustado, abonos vigentes, saldo actual y motivo. Explica deja de
  ser exigible, conserva historial, no mueve caja/bancos ni es devolución/cancelación de pago.
- Cancelar/Escape/fondo no envían y conservan motivo; Confirmar cancelación del adeudo usa
  requestSubmit con validación/CSRF y autorización sólo para ese envío. Backend conserva
  bloqueo/versión y prohibición si existen abonos netos; el resumen no sustituye esa regla.
  Advierte que cancelar no permite regenerar automáticamente la cuota de origen.
- Gestión presenta Cancelar adeudo separado y primero, con explicación de que no requiere
  habilitar transferencias ni corregir. Los otros botones van en Otras gestiones opcionales ·
  no son pasos para cancelar, cerrado inicialmente, con tarjetas explicando cada finalidad.
  Reutiliza form-section/student-section-intro/form-grid/form-notice/cuota-advanced y
  detail-close-button; sin CSS paralelo ni cambios de permisos/rutas/condiciones operativas.
- V73 sólo actualiza navegación de tres pasos de guías existentes para localizar ese bloque.
  Guía específica de cancelaciones aún pendiente tras confirmar cancelación del adeudo;
  siete guías/57 etapas, sin nuevos escenarios publicados por este cambio.
- Verificado: 590 pruebas Java sin fallos; render Thymeleaf comprueba atributos, separación
  de acciones y ausencia de formulario en cancelado. Once suites Node pasan, incluidas
  $600/abonos0, cierre sin enviar, Escape y confirmación única. Diff limpio.
  V73 editorial probada con BEGIN/ROLLBACK y aplicada; siete guías/57 etapas conservadas.
  Sólo app recreada, salud UP; base/volúmenes intactos, sin operaciones financieras ni QA
  visual autenticada. Próxima migración V74; confirmar presentación/prueba con propietario.
- Propietario debe recargar Adeudos → Ver → Gestión, motivo de retirar cobro de prueba600,
  Cancelar cargo al alumno → revisar resumen → Confirmar cancelación del adeudo; comprobar
  estado cancelado/saldo0/cajaB y ausencia en pendientes familiares. No ejecutarlo con tools.

## Resumen de adeudo comprensible — V72 — 2026-10-07

- Propietario confirmó pago600 validado aumentó caja y posterior cancelación devolvió la
  cuenta a B; adeudo sigue pendiente con pagado neto0/saldo600. Resultado humano confirmado,
  no ejecutado por herramientas. No cancelar el adeudo aún; esa prueba requiere revisar
  las restricciones por historial de aplicaciones. Guía específica de cancelación aún pendiente.
- Resumen conserva importes/cálculos y usa Total del adeudo con ajustes, Abonos vigentes
  y Falta por pagar, últimos dos destacados en tarjetas distribution-total existentes.
  Ayuda explica dinero actualmente aplicado, excluye cancelados/devueltos y conserva historial.
  Sin abonos/saldo positivo aclara que cancelar un pago no cancela la obligación.
  Liquidado informa sin pendiente; cancelado/convenido diferencia saldo no exigible de recibido.
- V72 homologa etiquetas en instrucciones/resultados existentes (cinco pasos), sin publicar
  nuevo caso ni modificar pagos/cargos/saldos. Siete guías/57 etapas.
- Verificado: 590 pruebas Java sin fallos, con render real de pendiente sin abonos, parcial,
  liquidado y cancelado. SQL probado con BEGIN/ROLLBACK; V72 aplicada, siete guías/57 etapas.
  Sólo app recreada, salud UP, sin operaciones financieras ni cambios de base/volúmenes.
  Revisión visual autenticada pendiente; diff limpio. Siguiente migración V73.
- Continuación: propietario recarga Adeudos → Ver → Resumen y confirma claridad del caso600.
  Antes de cancelar este adeudo, revisar regla vigente sobre pagos históricos; no prometer
  que la cancelación está permitida ni ejecutar operaciones financieras por herramientas.

## Confirmación del registro administrativo de pago — V71 — 2026-10-07

- Adeudos de alumnos → Ver → Resumen → Registrar pago abre formulario compartido; su botón
  Registrar como pendiente muestra Confirmar registro del pago antes de guardar. También
  aplica al alta general desde Pagos recibidos, sin cambiar vínculo/origen del flujo rápido.
- Resumen: tutor, total recibido, método, cuenta declarada, fecha/hora dd/MM/yyyy, referencia,
  cada cargo/alumno e importe, total distribuido/dinero sin asignar y nombres de archivos.
  Sumas exactas BigInt/centavos; datos literales con textContent, sin cambiar montos enviados.
- Cancelar/Escape/fondo conservan captura/archivos sin envío. Confirmar y registrar como
  pendiente usa requestSubmit/validación/CSRF/multipart originales, un envío autorizado.
  Aclara que registrar no modifica cuentas/adeudos hasta validar. Servicios sin cambios.
- V71 actualiza dos pasos editoriales de guías que registran pago administrativo y versiones;
  siete guías/57 etapas. La prueba de cancelación sigue pendiente de confirmación humana.
- Verificado: 588 pruebas Java sin fallos, diff limpio; SQL probado en BEGIN/ROLLBACK,
  V71 aplicada, siete guías/57 etapas conservadas. Sólo app recreada, salud UP y base/volúmenes
  intactos; sin operaciones financieras ni revisión visual autenticada. Próxima migración V72.
  Once suites Node pasan incluyendo
  resumen600, cambio a parcial300, archivo literal conservado al cancelar y un solo envío.
- Continuar prueba del propietario: formulario de pago nuevo $600 del mismo adeudo, revisar
  modal, confirmar pendiente, validar → B+600/deuda0, cancelar pago validado → B/deuda600.
  No crear pagos ni ejecutar decisiones financieras por herramientas.

## Confirmaciones de decisiones de pago — V70 — 2026-10-07

- Pagos recibidos → Ver → Gestión: cada acción muestra confirmación específica, reutilizando
  dialog nativo/estilos comunes: Confirmar validación del pago, Confirmar rechazo del pago
  y Confirmar cancelación del pago. Cada una incluye folio/titular/importe; validación añade
  cuenta destino y motivo de cambio si existe; rechazo/cancelación muestran motivo literal.
- Botón Cancelar en las tres confirmaciones cierra sin envío y conserva captura; Escape/fondo
  también. Botones finales distintos: Confirmar validación, Confirmar rechazo, Confirmar
  cancelación. Conserva validación requerida, CSRF, requestSubmit y aprobación de un único envío.
- Cancelación pendiente indica sin cambios de cuenta/adeudos. Validada identifica cuenta
  del ingreso original, egreso compensatorio por importe del pago y liberación de abonos/deuda.
  No equivale a devolución real; conserva historial. Rechazo explica motivo visible al tutor.
  Reglas, permisos, transacciones y servicios financieros no se modifican.
- V70 actualiza textos de validación/rechazo en guías ya confirmadas; no publica la prueba
  de cancelación que el propietario todavía está preparando. Siete guías / 57 etapas.
- Verificado: 586 pruebas Java sin fallos, diff limpio; SQL editorial probado con BEGIN/ROLLBACK;
  V70 aplicada por Flyway, siete guías/57 etapas conservadas. Sólo app recreada, salud UP;
  PostgreSQL/volúmenes preservados, sin validar/rechazar/cancelar mediante herramientas.
  Revisión visual autenticada pendiente del propietario. Siguiente migración V71.
  Once suites Node pasan
  con escenarios validación/rechazo/cancelación pendiente/validada, cancelar/Escape sin envío,
  motivo literal, datos propios sin mezclar acciones y confirmación única.
- Próxima prueba humana: recargar Pagos recibidos → Ver → Gestión del pendiente $600, usar
  Cancelar pago registrado por error (no Rechazar), revisar modal, primero Cancelar sin guardar
  y luego Confirmar cancelación; cuenta B/deuda600. Después pago nuevo validado y cancelación
  recupera B/deuda600. No ejecutar estas operaciones mediante herramientas.

## Confirmación al crear cuotas — V69 — 2026-10-07

- Crear cuota y Preparar cobro del alta asistida abren Confirmar nueva cuota con el modal
  compartido: institución, plantel, alumno/inscripción, concepto, importe único o por mes,
  frecuencia, vencimiento o meses/día límite, vigencia, estado, generación automática y si
  se creará el cargo ahora. Moneda exacta del campo y fechas dd/MM/yyyy; texto literal seguro.
- Cancelar/Escape/fondo cierran sin guardar, conservando captura y foco. Confirmar y crear
  cuota usa requestSubmit con validación/CSRF y autorización sólo para ese envío. No modifica
  servicios, reglas de cuotas ni edición existente. No registra pagos ni fondos.
- V69 actualiza sólo instrucciones/versiones de cinco pasos de guías que configuran cuotas;
  siete guías / 57 etapas, sin declarar la cancelación de pagos como confirmada.
- Pruebas DOM: cuota única/mensual/asistida, resumen recalculado al corregir, texto literal,
  Cancelar/Escape sin envío, confirmación única y no afectar botones de los otros modales.
  Verificado: 584 pruebas Java sin fallos y once suites Node correctas; diff limpio.
  SQL editorial probado con BEGIN/ROLLBACK, V69 aplicada por Flyway y lectura confirma
  cinco pasos actualizados, siete guías/57 etapas. Sólo app recreada, salud UP; base/volúmenes
  preservados, sin cuotas/pagos por herramientas. Revisión visual autenticada pendiente.
  Siguiente migración V70.
- Próxima prueba humana: recargar Crear cuota, revisar resumen de $600 de TEST-CANCELACION,
  pulsar Cancelar, comprobar datos conservados y volver a confirmar; continuar la prueba
  de cancelación pendiente/validada indicada antes. No crear esa cuota por herramientas.

## V68 — devolución parcial seguida de descuento confirmada — 2026-10-07

- Propietario confirmó el caso separado: cargo original $400, pago validado $400,
  devolución $100 y descuento manual $100 → total ajustado $300, pagado acumulado $300
  y saldo $0. Historial correcto, cuenta sin otro movimiento al descontar y sin pendiente
  familiar. Esta confirmación humana concluye el pendiente descrito en los bloques anteriores.
- V68 publica Devolver $100 y ajustar la deuda con un descuento en nueve etapas:
  concepto que admite descuento, cuota, emisión seleccionada, reporte familiar, validación
  y saldo S, devolución, comprobación previa, ajuste con modal y verificación en ambos perfiles.
  S ya incluye el pago: devolución deja S−100; descuento conserva ese saldo. No volver a
  pagar y descontar los mismos $100; otra guía explica el nuevo pago sobre otro caso.
- Se enlaza la alternativa confirmada en la última precaución de la guía de devolución
  más nuevo pago, incrementando su versión. Sólo tablas editoriales; sin modificar
  roles, cargos, pagos, ajustes ni movimientos. Siete guías / 57 etapas tras aplicar V68.
- Verificado: 582 pruebas Java sin fallos; contenido/numeración/recorridos/botones cubiertos.
  SQL probado dentro de BEGIN/ROLLBACK, nueve etapas y ocho requisitos de permisos existentes.
  V68 aplicada por Flyway; lectura confirma siete guías / 57 etapas y nuevo caso CONFIRMADA.
  Sólo app recreada, salud UP; PostgreSQL/volúmenes intactos. No se realizaron operaciones
  financieras ni revisión visual autenticada; confirmación funcional proviene del propietario.
- Siguiente migración V69. Próxima prueba propuesta: cancelación de un pago capturado
  por error (no devolución real), primero pendiente sin afectar cuenta y después validado
  con anulación de ingreso y recuperación de deuda. Usar casos nuevos controlados; no
  cancelar el pago con devolución de este ejemplo. No publicar esos escenarios como
  confirmados hasta que el propietario verifique los resultados. No operar dinero con tools.

## Detalle de adeudos con pestañas y vista previa — V67 — 2026-10-07

- Adeudos de alumnos → Ver usa mismas pestañas/clases de alumnos y pagos: Resumen,
  Transferencias, Ajustar saldo, Historial y Gestión. Alumno/inscripción y saldo pendiente
  destacado permanecen fuera de paneles. Registrar pago sigue en Resumen; habilitar
  transferencia vencida, reemplazar y cancelar se separan en Gestión. Se conserva historial,
  trazabilidad, permisos/condiciones de acciones y estado no editable para cargos cerrados.
- Ajustar saldo muestra Así quedaría el cargo, usando tarjeta de revisión existente:
  total actual, ajuste, nuevo total, pagado y saldo posterior. BigInt/centavos, sin redondeo;
  Descuento fuerza disminución, Recargo aumento y Corrección permite elegir. Si total
  posterior <0 o <pagado no se envía; backend sigue revalidando bajo bloqueo sus reglas.
  Modal de confirmación incluye nuevo total/pagado/saldo. No se envían esos cálculos como
  importes autoritativos ni se registran fondos por mostrar vista previa.
- Errores de validación/negocio abren Ajustar saldo conservando formulario; cancelación
  fallida abre Gestión con motivo. Ajuste exitoso redirige a Historial con aviso y saldos
  recalculados. Atributo opcional force-active-tab prevalece al hash; otras páginas sin él
  conservan su funcionamiento, incluyendo prioridad de errores y teclado.
- V67 sólo actualiza navegación en guías existentes (Gestión/Historial/Transferencias),
  sin nuevos escenarios confirmados; seis guías / 48 etapas.
- Verificado: 579 pruebas Java sin fallos, incluyendo render real Thymeleaf de cinco
  pestañas, valores de preview, cargo cancelado y captura escapada; errores/conservación
  y flash de Historial. Once suites Node correctas: preview BigInt, DOM de efecto/límites,
  prioridad de pestañas/teclado y regresiones monetarias. SQL editorial probado en rollback;
  V67 aplicada, lectura confirma cinco pasos actualizados y seis guías/48 etapas. Sólo app
  recreada, salud UP; sin operaciones financieras ni cambios de PostgreSQL/volúmenes.
  Revisión visual autenticada y prueba humana siguen a cargo del propietario.
- Siguiente V68. Prueba humana de devolución más descuento sigue pendiente: nuevo cargo
  $400, pagar400, devolver100, descuento100 → ajustado300/pagado300/saldo0. Al llegar al
  detalle usar pestaña Ajustar saldo; después de confirmar revisar Historial y Resumen.

## Foco de captura monetaria sólo al clicar el campo — 2026-10-07

- Causa de foco al clicar arriba: activación nativa de label envolvente/asociado. Componente
  evita esa activación por mouse/tacto en título/espacio del label, sólo para importes;
  clic directo en input y Tab/foco asistido siguen funcionando. Botones/enlaces/controles
  de ayuda dentro de label no se interceptan. Al clicar título de campo ya activo se quita
  foco y reaparece moneda; readonly conserva moneda incluso al recibir foco.
- Clase del input visible cambia a money-entry: money-input ya pertenecía a un contenedor
  de forms.css con grid de dos columnas. No reutilizar nombres de clases sin comprobar su
  uso previo. Valores canónicos, límites, modales, cálculos y envío no cambian.
- Verificado: 576 pruebas Java y ocho suites Node sin fallos. Simulación DOM cubre label
  asociado/envolvente, clic título bloqueado, clic campo permitido, desenfoque/formato,
  activación asistida y botones de ayuda; regresiones monetarias previas correctas.
  Sólo app recreada, salud UP y JS servido actualizado; sin migración ni cambios de fondos
  o volúmenes. No se hizo inspección visual autenticada real; confirmar foco con propietario.
  V67 libre;
  prueba de devolución más descuento y captura/scroll/formato aún a cargo del propietario.

## Captura monetaria segura y confirmaciones — V66 — 2026-10-07

- 14 formularios administrativos de importes usan input text/data-money y teclado decimal:
  cuotas, cargos, ajustes, pagos/distribución, devoluciones, becas fijas, recargos fijos/tope,
  cuentas, movimientos, retiros, traspasos, corte, convenio y compras/precios (incluidas filas
  nuevas). Porcentajes, cantidades, horas, fechas y calificaciones siguen sus controles.
  Portal familiar ya tenía monto calculado protegido y decimal oculto; no se vuelve editable.
- money-input.js conserva el input original como hidden canónico (mismos IDs/nombres y
  valores numéricos para scripts/servicio) y crea visible text sin name. Al foco muestra
  decimal, al blur moneda; original.value programático actualiza presentación, eventos
  del visible notifican input/change originales. readonly/disabled/min/max/required y
  setCustomValidity/focus/check/report se reflejan. Filas dinámicas se inicializan una vez,
  renumeración de nombres sigue sobre original, reset vuelve al valor inicial.
- Parsing exacto por cadenas/BigInt, sin redondear entrada; hasta 2 decimales, pega formato
  $1,400.50 o decimal 1400.50, permite coma decimal no ambigua. Rechaza 1,400 sin símbolo ni
  punto decimal, notación científica, negativos, exceso de precisión y texto inválido;
  no convertirlos silenciosamente en cero ni enviar $/separadores. Tope híbrido conserva
  precisión histórica 4 decimales: sólo muestra moneda para MONTO_FIJO, porcentaje sin $.
  Servidor mantiene BigDecimal/Bean Validation, autoridad y reglas de negocio existentes.
- Modal nativo compartido confirma Validar y publicar, Ejecutar devolución y publicar egreso
  y Aplicar ajuste al saldo: importe, cuenta/titular o cargo/alumno/efecto según operación,
  deuda recuperada y abono en devolución. Volver a revisar/Escape/fondo no envían; foco y
  teclado nativos, modo oscuro/móvil, texto literal textContent; Confirmar operación usa
  requestSubmit conservando handlers/CSRF y aprobación sólo para ese envío, no futuros.
- V66 sólo actualiza pasos editoriales que mencionan esos botones y su versión; seis guías
  / 48 etapas, sin publicar la prueba aún pendiente de devolución más descuento.
- Verificado: 575 pruebas Java sin fallos; ocho suites Node incluidas captura/parser exacto,
  simulación DOM (foco/blur, validación, bloqueos, filas nuevas/tope híbrido, cancelar y
  confirmar una sola vez) y regresiones anteriores. Binding BigDecimal y reglas financieras
  existentes aprobados. SQL editorial probado con rollback; V66 aplicada por Flyway,
  lectura confirma seis guías / 48 etapas / nueve pasos con confirmación y versiones una
  sola vez incrementadas. Sólo app recreada, salud UP y JS nuevo servido; sin operaciones
  financieras ni cambios de PostgreSQL/volúmenes. No se hizo QA visual autenticado real;
  captura/formato en navegador/móvil y desenlace descuento pendientes del propietario.
- Durante preparación editorial se ejecutó V66 directamente una vez: se restauraron sólo
  texto añadido/versiones a 4/6/3/2/1/1, con fecha previa idéntica; luego se probó en rollback
  y se aplicó exclusivamente por Flyway. No dejó incrementos duplicados ni tocó finanzas.
  Validar migraciones SQL siempre con BEGIN/ROLLBACK explícito, nunca psqlear archivo directo.
- Siguiente V67. Prueba humana pendiente: concepto
  DEV-DESC-TEST permite descuento, nueva cuota/cargo $400 → pagar/validar $400 → devolver
  $100 → descontar $100 → ajustado $300, pagado $300, saldo $0; descuento no mueve cuenta.
  Incluir al capturar cuota: teclear 400, usar rueda con foco, salir/ver $400.00, volver/ver
  400.00, guardar y verificar cuota400/vista previa400. No reutilizar el cargo liquidado.

## V65 — devolución parcial y nuevo pago confirmados — 2026-10-07

- Propietario confirmó devolución $100 del pago validado $400: historial devuelto $100,
  cargo original $400, saldo recuperado $100 y cuenta disminuyó $100. Luego reportó y pagó
  esos $100 en portal familiar, validó en administración, cuenta aumentó $100 y confirmó
  saldo final $0 y desaparición del pendiente familiar. Operaciones hechas por propietario,
  nunca por herramientas. Pendientes humanos de devolución/nuevo pago anteriores concluidos.
- V65 agrega guía Devolver $100 y pagar nuevamente el saldo en ocho etapas explícitas:
  identificar pago, saldo S, devolución, comprobaciones, portal, reporte, validación y
  resultados. Conserva cuenta protegida/botón/motivo condicional y vista previa diferenciada.
  Explica original $400 menos devolución $100 más pago nuevo $100 = neto $400; no descuento.
- Seis guías confirmadas / 48 etapas; sólo contenido editorial y permisos requeridos de
  guía, sin conceder roles ni alterar datos financieros.
- Verificado: 571 pruebas Java sin fallos; Node devolución correcto; SQL editorial validado
  con rollback antes de despliegue, V65 aplicada por Flyway. Lectura confirmó seis guías,
  48 etapas y ocho nuevas. Sólo app recreada, salud UP; PostgreSQL/volúmenes preservados.
  Sin devolución/pago/ajuste por herramientas ni validación visual autenticada del navegador.
- Siguiente migración V66. Siguiente prueba: otro concepto que permita descuento, otro
  cargo $400 sin beca/recargo → pagar $400 → devolver $100 → descuento manual $100 → total
  ajustado $300 / abono $300 / saldo $0. No volver a pagar esos $100 ni usar el cargo ya
  liquidado del caso confirmado. Descuento no mueve fondos; validar recepción y devolución
  sí. Documentar como confirmado sólo cuando el propietario compruebe esa otra vertiente.

## Vista previa de devolución diferenciada — 2026-10-07

- Así quedaría la devolución usa tarjeta azul/lavanda, borde destacado y etiqueta Vista
  previa · No necesitas capturar aquí. Explica que todavía no se ha devuelto dinero.
  Importes conservan tarjetas y saldo recuperado en menta; tema oscuro tiene variante
  equivalente. Cambio sólo HTML/CSS, sin modificar cálculos, IDs, permisos o operaciones.
- Verificado: 568 pruebas Java y Node del flujo de devolución correctos; diff limpio.
  Sólo app recreada, salud UP; sin migración ni cambios de fondos/volúmenes. Revisión
  visual autenticada pendiente del propietario. La prueba humana de devolución $100 del pago $400
  sigue pendiente; no se ejecutó devolución por herramientas ni se confirmó el caso en guía.

## Botón de cambio de cuenta en devolución — 2026-10-07

- Se reemplaza el checkbox visible por botón Devolver desde otra cuenta con diseño
  existente, estado accesible aria-pressed/expanded y área táctil mínima 44px. Activo
  cambia a Conservar cuenta original; volver restaura cuenta, oculta motivo y lo limpia.
  Estado booleano se envía por campo oculto; las validaciones financieras no cambian.
- Se corrige CSS: regla del formulario display:block anulaba hidden del apartado de
  motivo. Regla específica hidden ahora garantiza que no aparezca mientras no se active.
- Verificado: 568 pruebas Java sin fallos; Node cubre activación por botón, estado oculto,
  motivo condicional, restauración y cantidades. Sólo app recreada, salud UP; sin migración
  ni cambios de saldos/volúmenes. Revisión visual autenticada a cargo del propietario.
  Sin ejecutar devolución. Prueba humana $400 → devolver
  $100 todavía pendiente de confirmación; no añadirla como confirmada a la guía.

## Cuenta protegida en devoluciones — 2026-10-07

- Pago recibido → Ver → Devoluciones precarga y protege la cuenta donde se validó el
  ingreso. Devolver desde otra cuenta habilita la ayuda existente; desactivarlo restaura
  ID/nombre originales, limpia motivo y cancela búsqueda pendiente. Autocompletado respeta
  readonly para no consultar, borrar ni seleccionar opciones mientras esté protegido.
- Si se elige otra cuenta, motivo obligatorio hasta 500 caracteres, validado en servicio
  contra la cuenta real del pago (no un ID original enviado por el navegador). No basta
  alterar el input/POST. Se mantienen institución, moneda, alcance, saldo, fechas, actor,
  concurrencia, idempotencia y límites de la devolución. Pagos sin cuenta original requieren
  selección explícita y motivo; no se inventa una cuenta histórica.
- Motivo del cambio se añade con etiqueta al motivo de la devolución visible en historial
  (límite conjunto 2000); auditoría registra cuenta original, cuenta de egreso y motivo por
  campos. Se usan tablas existentes, sin migración (siguiente V65), roles ni cambios de datos.
- Resumen indica Se descontarán [importe] de [cuenta], con diseño existente claro/oscuro.
  Verificado: 568 pruebas Java sin fallos; Node cubre cuenta protegida, cambio/motivo,
  restauración y vista $100/$300/$100; flujo familiar anterior correcto y diff limpio.
  Sólo app recreada, salud UP. Sin migración ni devolución por herramientas; PostgreSQL
  y volúmenes intactos. Revisión visual autenticada pendiente del propietario.
- Pendiente humano: pago de prueba corregido/validado $400 → devolución parcial $100 desde
  misma cuenta → cuenta −$100, abono activo $300, deuda $100. No publicar aún como guía
  confirmada; esperar resultado del propietario. Cinco guías confirmadas / 40 etapas.

## V64 — rechazo y reenvío corregido de $400 confirmado — 2026-10-07

- Propietario confirmó completar la prueba, validar el reporte corregido, aumento de $400 en
  cuenta y comprobante visible en portal familiar. Es confirmación humana; el agente no
  rechazó ni validó pagos. El pendiente de reenvío descrito debajo queda concluido.
- V64 añade Guía de procesos → Rechazar un pago y corregir el reporte: $400, diez etapas
  detalladas: concepto, cuota, emisión seleccionada, saldo B, primer reporte, rechazo con
  motivo, consulta del motivo/modal, reporte corregido, validación y comprobación final.
  En cada etapa: menú/módulo, búsqueda/fila, botones/pestañas, campos, guardado y resultado.
- Ejemplo de $400 sin ajustes: rechazo mantiene deuda de $400 y cuenta B; segundo validado
  deja saldo $0, un ingreso de $400 y banco B + $400, no $800. Primer folio conserva rechazo/comprobante.
  Referencias TEST ilustrativas: corregir archivo no crea otra referencia bancaria real
  ni exige una segunda transferencia. Modal de motivo se explica desde historial del hijo.
- Nueva guía sólo visible con permisos requeridos, sin concederlos; mismos filtros,
  paginación, diseño y Excel. V64 inserta exclusivamente contenido editorial; no afecta
  cargos, cuotas, pagos, movimientos o roles. Ahora cinco guías y 40 etapas.
- Verificado: 562 pruebas Java sin fallos; Node del modal y flujo de transferencia correctos;
  SQL editorial validado con rollback y V64 aplicada por Flyway. Consulta de sólo lectura
  confirmó cinco guías / 40 etapas / diez etapas nuevas. Sólo app recreada, salud UP;
  PostgreSQL y volúmenes preservados. Sin validación visual autenticada en navegador.
- Próxima migración V65. Siguiente prueba recomendada:
  devolución parcial de un pago validado, comprobando egreso, saldo de cuenta y recuperación
  de deuda. Ya se probó devolución antes, pero confirmar/reconstruir caso con valores exactos
  antes de publicarlo en guía. No ejecutar devolución real por herramientas ni reutilizar
  un pago del propietario sin que él decida cuál usar y autorice su propia prueba.

## Motivo de rechazo visible en historial familiar — 2026-10-07

- Durante prueba de rechazo/reenvío, propietario reportó que sólo veía Rechazado.
  PortalPagoFila no incluía el motivo en la consulta del historial. Se agrega motivoRechazo
  desde pago.motivo_rechazo_cancelacion únicamente para estado RECHAZADO, conservando
  filtros por usuario/institución/alumno, paginación y contrato de constructores anteriores.
- Tus pagos y comprobantes → Estado Rechazado → Ver motivo del rechazo abre dialog nativo
  con folio, motivo literal, saltos de línea y orientación. Respeta temas/móvil y vista de
  soporte; no muestra botón en pagos de otros estados. Cierre por botón, Escape o fondo;
  foco dentro del modal y retorno al botón, fondo sin desplazamiento. Texto escapado en
  HTML y escrito con textContent, nunca innerHTML; no hay datos de otros tutores.
- Si no existe motivo histórico se solicita aclaración con administración, sin inventarlo.
  Mensaje explica que rechazo no reduce deuda y que corregir reporte no exige otra
  transferencia bancaria si el dinero ya salió. No cambia validación/rechazo ni cuentas.
- Sin migración; V64 sigue disponible. Caso de reenvío aún pendiente de confirmación:
  tutor debe recargar Tus pagos, consultar motivo de TEST-RECHAZO-400, reportar nuevamente
  con TEST-CORREGIDO-400 y comprobar validación400, saldo0 y un solo ingreso400. No publicar
  ese caso como confirmado todavía.
- Verificación: 559 pruebas Java sin fallos, render real de filas rechazadas/validadas y
  soporte, escape de texto y modal; Node prueba folio, motivo literal con saltos, fallback,
  cierre, fondo y restauración de foco, además del flujo de reporte previo. Sólo app
  desplegada, salud UP; sin migraciones ni cambios de pagos. Revisión autenticada del
  motivo específico pendiente del propietario al recargar su historial y continuar paso5.

## V63 — recorridos explícitos de las cuatro guías — 2026-10-07

- Se revisan las cuatro guías actuales y sus 30 etapas: dos hijos 8, beca/recargo 8,
  pagos parciales 7 y transferencia vencida 7. Cada etapa comienza desde menú/perfil y
  describe categoría, módulo, búsqueda, Aplicar filtros, fila/Acciones, botón, campos,
  guardado y comprobación; las transiciones entre módulos y portal son explícitas.
- Cargo vencido: Operación escolar → Cobranza escolar → Adeudos de alumnos → buscar
  alumno → Aplicar filtros → Ver en fila correcta → Transferencia de cargo vencido →
  checkbox → motivo → Guardar autorización. Alternativa general detalla Administración →
  Configuración escolar → Planteles → buscar → Editar → opción → Guardar cambios.
- Se corrigen referencias antiguas que excluían todos los vencidos del portal. Se mantienen
  ejemplos, resultados, permisos, estados confirmados, precauciones de selección/fechas y
  el diseño. V63 sólo actualiza tablas editoriales y versión/fecha; no cambia operaciones.
- Regla permanente de guías: no escribir Abre el cargo, Busca el pago, Ve a Ver o Revisa
  la cuenta sin precisar desde qué menú/módulo se llega, cómo se identifica el registro y
  qué botón/pestaña/acción se usa. Cada etapa debe poder seguirse sin recordar la pantalla
  anterior; explicar autocompletado, captura, errores, guardado, resultado y retorno.
  Revisar nombres exactos contra HTML/rutas; distinguir guardar pendiente de validar.
- Verificación: 558 pruebas Java aprobadas, incluyendo cobertura de las 30 etapas,
  rutas/botones de autorización y numeración sin confundir importes con acciones. SQL
  de actualización validado primero con ROLLBACK; V63 aplicada y sólo app desplegada,
  salud UP. SQL confirma cuatro guías revisadas07/10 y30 etapas con origen explícito.
  No se cambiaron operaciones ni permisos. Siguiente migración V64; revisión de claridad
  en pantalla a cargo del propietario. No se editó ninguna migración previamente aplicada.

## Confirmación de transferencia vencida y claridad visual — 2026-10-07

- Propietario confirmó validación del cargo19 y aumento850 en caja. Después creó una
  cuota única vencida05/10, emitió el cargo500, lo vio en el portal familiar, reportó el
  comprobante y validó: confirmó incremento500 en la cuenta bancaria. No fueron operaciones
  ejecutadas por herramientas. No atribuir a esta prueba revocaciones ni concurrencia.
- Tus pagos presenta confirmación en tarjeta menta con icono, Comprobante recibido y
  estado En revisión. Explica que el saldo sólo cambia al validar y dónde consultar el estado.
  Se mantiene el mensaje del servidor escapado, role=status y no desaparece por temporizador.
- Reportar transferencia → 5 · Revisa y envía usa la variante lavanda existente y etiqueta
  Sólo revisión · no necesitas capturar nada aquí. Resumen automático sin inputs; para
  corregir se usan las secciones anteriores. Mantiene tema claro/oscuro y diseño móvil.
- V62 incorpora el caso confirmado500 a Guía de procesos en siete pasos, con cuota única,
  generación seleccionada, autorización, reporte, validación y resultados. El ejemplo no
  crea cargos ni dinero; su captura de cuota no usa la excepción de fecha del cargo manual.
- Verificación: 555 pruebas Java aprobadas (incluido render real del aviso, escape del
  mensaje y resumen sin inputs), prueba Node del flujo familiar y git diff --check correctos.
  Sólo app desplegada, salud UP y V62 aplicada: guía CONFIRMADA con siete pasos. Próxima
  migración V63. No se modificaron pagos, roles, vencimientos o importes. Revisión visual
  autenticada, tema oscuro y móvil a cargo del propietario; no se envió un pago de prueba.
  El primer arranque detectó enlace familiar incompatible con la restricción de guías;
  V62 se revirtió y no quedó aplicada. Se corrigió el enlace editorial a NULL (instrucciones
  conservan /familias), se añadió regresión y el despliegue final aplicó V62 correctamente.

## V61 — transferencias de cargos vencidos — 2026-10-06

- Planteles → Editar: Permitir reportar transferencias de cargos vencidos. Inicialmente
  desactivado para conservar la configuración; el agente no habilita planteles reales.
- Adeudos de alumnos → Ver → Transferencia de cargo vencido permite autorizar/revocar una
  excepción individual con motivo, versión y administrador persistido. Regla del plantel
  y excepción se combinan con OR: quitar la excepción no anula la regla general del plantel.
  Cada cambio conserva actor/instante/motivo/antes-después en la bitácora inmutable existente.
  El cambio general del plantel también queda en Auditoría. No modifica fechas ni saldos.
- Selector, cuenta y POST comparten la regla en PostgreSQL: vínculo financiero vigente,
  tutor/alumno activos, institución, cargo emitido, saldo positivo, fecha vigente o permiso
  de vencido, y ningún pago pendiente de validación. Se filtra antes de limitar 10/20 filas;
  cancelados, convenidos, liquidados y pagos en revisión no consumen opciones del selector.
  Fecha actual calculada en la zona institucional, no en la fecha UTC del contenedor.
- Reportar bloquea cargos en orden ID y conserva bloqueos hasta guardar para evitar dos
  reportes concurrentes del mismo cargo. Monto/total protegidos; si cambió el saldo respecto
  de la selección, actualiza el formulario y exige revisar y adjuntar de nuevo, sin guardar.
- Desglose al seleccionar y en revisión final: original, becas/descuentos, recargos/ajustes
  aplicados, abonos y saldo; distingue vencido habilitado. No simula recargos futuros.
  Cuenta bancaria, comprobantes privados, revisión administrativa y PDF inline se conservan.
- Un reporte ya guardado NO se revaloriza: conserva su monto; administración ve aviso si
  la deuda actual es distinta. Validación aplica hasta saldo y conserva excedente disponible
  o deuda restante, sin inflar el ingreso. No se implementó cobro automático ni tarjeta online.
- V61 actualiza precauciones editoriales de las guías existentes. El propietario confirmó
  la selección del recargo único con tope50 y registro efectivo850 del ejemplo 1000−200+50;
  esa prueba no fue ejecutada por herramientas. SQL posterior confirma cargo19 con ajuste
  neto−150, abonos0 y un pago en revisión: falta validar/rechazar antes de otro reporte para
  ese cargo; no declararlo liquidado todavía. Próxima migración V62.
- Prueba del propietario: usar otro cargo vencido con saldo; habilitar sólo ese cargo con
  motivo, entrar con su tutor, revisar desglose, reportar comprobante, comprobar que queda
  en revisión y que no admite otro reporte. Validar desde administración y comprobar saldo,
  ingreso y PDF. Revisar claro/oscuro y móvil. No cambiar vencimiento para habilitar el portal.
- Verificación: 552 pruebas Java aprobadas, sintaxis Node y prueba del flujo familiar correctas.
  PostgreSQL ejecutó la consulta con 11 escenarios en CTE en transacción READ ONLY: incluyó
  vigente y vencidos habilitados, excluyó pagado/cancelado/convenido/en revisión/ajeno/inactivo.
  Consulta sobre esquema real correcta; V61 aplicada, imagen app desplegada y salud UP.
  Cero planteles habilitados y cero excepciones reales. No se crean pagos ni movimientos
  por herramientas. Prueba operativa nueva y revisión visual autenticada pendientes del dueño.

## V60 — selección explícita de adeudos y recargos — 2026-10-06

- Vistas previas de Generar automáticos y Generar recargos incluyen casillas inicialmente
  marcadas, Seleccionar/Desmarcar todos y selección de página. En adeudos la clave pertenece
  a cuota + periodo, no a toda la cuota; en recargos pertenece al cargo con sus periodos de mora.
- Resumen vivo global de registros/importe seleccionados arriba y antes de confirmar.
  Tarjetas originales mantienen totales de toda la vista, con aclaración. Desmarcar sólo omite
  esa ejecución; no cancela cuotas, adeudos ni políticas, y vuelven a ofrecerse en nueva vista.
  Confirmación deshabilitada con selección vacía o sin script/almacenamiento de sesión.
- Metadatos privados V60: generacion_seleccion + items (claves, versiones, importe y fecha;
  no nombres ni expedientes). Captura en lotes de100, lista paginada y estado cliente en
  sessionStorage por UUID; no se envía toda la lista al navegador. Selección vinculada al
  usuario persistido, institución, plantel, corte y tipo; caduca2h y se consume una vez.
- Backend valida pertenencia, versión, importe, vencimiento y cantidad seleccionada; no
  incorpora registros añadidos después de visualizar. Cambio detectado exige nueva vista
  y revierte la transacción completa. El importe real del cargo emitido se verifica después
  de aplicar beca. Máximo5000 cambios explícitos por solicitud; modo Todos menos excluidos
  o modo Sólo incluidos permite seleccionar un registro aun con muchas páginas.
- V60 actualiza instrucciones/precauciones de guías existentes; siguiente V61. Selección
  funcional nueva pendiente de confirmar por el propietario. No se generaron cargos,
  recargos ni pagos de prueba por herramientas. Mantener permisos/CSRF/diseño común.
- Continuación pendiente: Políticas → Generar recargos → corte06/10/2026 → Visualizar →
  Desmarcar todos → marcar solamente cargo19 de PRUEBA-TOPE → resumen1 registro/$50 →
  Confirmar y generar recargos seleccionados → cargo19 debe quedar1000−200+50=850.
  Probar automáticamente dos filas: desmarcar una, confirmar otra y comprobar que la omitida
  sigue disponible al volver a visualizar. No confundir repetición con cuota cancelada.
- Pendiente antes de producción: acordar retención/depuración de metadatos de vistas caducadas;
  su expiración impide usarlos, pero esta entrega no borra esos metadatos ni registros financieros.
- Verificación y despliegue: 536 pruebas Java aprobadas y Node verifica selección global,
  exclusiones entre páginas, sólo un registro, montos, vacío y fallos de almacenamiento.
  Render de filas marcado por defecto y guardas de token/usuario/corte/importe/version.
  V60 aplicada, imagen `app` actualizada y salud `UP`. SQL confirma cargo19 sin recargo
  (original1000/beca200); no se prepararon vistas reales ni se generaron cargos por herramientas.

## Diagnóstico de cuotas cerrado por defecto — 2026-10-06

- El propietario confirmó que la vista principal no tenía cuotas pendientes y confundió
  la lista diagnóstica con cargos por generar. Cuotas no incluidas se oculta en un bloque
  desplegable común: Consultar cuotas no incluidas · sólo información, no se generarán cargos.
  Cerrado al visualizar inicialmente; conserva apertura al paginar explícitamente dentro
  del diagnóstico. Mantiene paginación, alcance y Excel; no cambia generación ni datos.
- Cargo #18 permanece cancelado. La prueba continúa desde su corrección con reemplazo,
  no desde generación automática. Sin migración, V60 sigue disponible.
- Verificado: 523 pruebas Java aprobadas, incluida apertura/cierre del diagnóstico renderizado.
  Imagen desplegada sólo en `app`, salud `UP`. No se cambiaron cuotas, cargos ni pagos.

## V59 — corrección controlada de vencimientos — 2026-10-06

- Cuota única con cualquier cargo generado (también cancelado) protege importe, moneda,
  frecuencia y fechas en interfaz y servicio. Mensuales permiten cambiar futuras emisiones;
  frecuencia protegida y aviso explícito de que los cargos emitidos no cambian.
- Generar automáticos añade Cuotas no incluidas y motivo: activación, concepto, inscripción,
  inicio posterior al corte o periodos ya generados. Consulta institucional/plantel por bloques
  de 100, página de 25 y Excel POI con el mismo alcance/corte; no extrae todo en memoria.
- Adeudos → detalle → Corregir y reemplazar cargo (`/admin/cargos/{id}/corregir`) corrige sólo
  el vencimiento de un cargo proveniente de cuota, con confirmación, motivo obligatorio,
  administrador persistido y permiso CARGO_ADMINISTRAR. Valida alcance y versión; bloquea
  pagos históricos, pagos en revisión, convenios, ajustes distintos de beca y reemplazo previo.
- V59 enlaza nuevo cargo con anterior mediante FK única reemplaza_cargo_id y motivo_reemplazo.
  Original cancelado permanece intacto; si estaba emitido se cancela en la misma transacción.
  Conserva alumno, concepto, importe, moneda, periodo y fecha de registro original. Recalcula
  beca vigente; no traslada ajustes manuales ni recargos. No modifica la cuota ni cuenta alguna.
  Actor/instante de creación identifica la corrección. Enlaces anterior/siguiente en detalle.
- Registro de pagos bloquea el cargo al comprobar solicitudes para serializar con reemplazos.
  La clave automática original permanece reservada: cancelar no regenera. La generación única
  inmediata localiza el último reemplazo y rechaza un último cargo cancelado/convenido.
- V59 actualiza precauciones y versiones de las guías existentes, sin declarar confirmado el
  nuevo caso. Próxima migración V60. No se reemplazó el cargo del propietario por herramientas.
- Prueba pendiente del propietario: cargo #18 cancelado (10/10/2026), cuota #13 corregida
  (05/10/2026). Abrir el cargo, corregir con vencimiento 05/10, motivo de captura incorrecta,
  confirmar. Nuevo cargo debe tener original1000, beca200 y saldo800; recargo corte06/10 debe
  proponer50, saldo850. Confirmar sólo el alcance revisado. Original conserva10/10 y cancelación.
- Verificación y despliegue: 522 pruebas Java y pruebas Node de cuota protegida/recargos
  aprobadas; formulario Thymeleaf renderizado con validación de confirmación. Sólo `app`
  recreado, V59 aplicada con éxito y salud `UP`. Cargo18 sigue cancelado, original1000/beca200,
  sin reemplazo creado por herramientas. Revisión visual autenticada pendiente del propietario.

## Corrección del ejemplo de recargos — 2026-10-06

- El propietario reportó la tabla ilustrativa vacía incluso al configurar el recargo.
  No se atribuye una causa de navegador confirmada: no fue posible inspeccionar su sesión.
  Se versionan script/estilos para evitar recursos anteriores, se refuerza inicio antes o
  después de DOMContentLoaded y lectura de campos por id o nombre. Actualiza con input,
  change y botón Actualizar ejemplo (type button; nunca guarda ni genera movimientos).
- La tabla ya no queda sin explicación: informa el porcentaje o tope faltante o los valores
  inválidos. Se conserva el ejemplo 1000−200, 10% mensual, tope150 → 80/70/0, deuda950.
  Se distingue la simulación con acento violeta, aviso NO MODIFICA DATOS, temas claro/oscuro
  y tabla móvil común. Prueba de renderizado real verifica los campos de Thymeleaf.
- Sin migración ni cambio de reglas financieras. Próxima migración V59. Pendiente confirmar
  visualmente en la sesión del propietario; no se registraron recargos ni pagos de prueba.
- Verificación: 506 pruebas Java sin fallos, prueba Node aprobada, imagen final desplegada
  sólo en `app` y salud `UP`. No requiere reconstrucción adicional para revisar el ejemplo.

## Claridad del tope de recargos — 2026-10-06

- Políticas de recargo separa Tope de recargos, Ejemplo calculado y Política habilitada.
  Opciones: Sin tope adicional, Hasta un monto máximo y Hasta un porcentaje del cargo
  original. Etiquetas dinámicas y ayudas distinguen acumulado por cargo, base con becas
  y porcentaje del tope sobre original. No incluye ajustes manuales ni comparte tope familiar.
- Ejemplo ilustrativo no persistido y sin consultas de alumnos: original 1000 y descuentos
  200, modalidad 10%, mensual y tope 150 muestran 80/70/0 y deuda 950. Un recargo único
  también se limita: tope 50 permite sólo 50 sobre base 800, deuda 850. No cambia reglas.
- Se preservan valores y validaciones de políticas existentes. Guardar no genera recargos;
  es obligatorio abrir Políticas → Generar recargos, visualizar y confirmar explícitamente.
- V58 actualiza el paso 5 de la guía de beca/recargo y su versión a 2. No reescribe V57,
  no crea ajustes y no registra pruebas. Próxima migración V59.
- Verificación: 505 pruebas Java sin fallos y simulación Node aprobada. Imagen reconstruida
  y desplegada en `app`, V58 aplicada y salud `UP`. No se generaron movimientos de prueba.
  La revisión visual autenticada y el caso funcional quedan a cargo del propietario.
- La prueba funcional de topes aún no está confirmada por el propietario. Prueba inmediata
  segura: cuota 1000, beca 200, recargo 10% único y tope de 50, fecha límite pasada válida
  dentro de inscripción/ciclo; vista previa espera 50 y saldo 850. Mensual con tope150
  espera 80/70/0 en los tres meses de mora; para generar todos hoy requiere fechas pasadas
  válidas que cubran esos meses, o esperar su transcurso. No confirmar cortes futuros en
  datos operativos. La simulación de pantalla permite probarlo sin esperar ni guardar.

## V57 — guías de becas/recargos y pagos parciales — 2026-10-06

- Se incorporan dos casos confirmados por el propietario a Guía de procesos, manteniendo
  la guía de dos hijos. Beca/recargo: original $1,000, beca 20% ($200), exigible $800,
  recargo único 10% sobre $800 ($80), pago $880 y saldo cero. Parciales: cargo $1,000,
  pagos independientes $300/$500/$200, saldos $700/$200/$0.
- Ocho etapas para beca/recargo y siete para parciales, con requisitos, fechas válidas,
  ejemplo, resultados, precauciones y enlaces exactos. Recargos se abre en Políticas de
  recargo. Para abonos se usa Pagos recibidos → Nuevo registro; el flujo rápido tiene
  protegida su fila inicial. La transferencia familiar liquida el saldo completo restante;
  cargos vencidos no se ofrecen en su selector, por lo que se pagan desde administración.
- V57 modifica sólo tablas editoriales de guías y sus requisitos de permisos. No crea
  alumnos, cuotas, cargos, pagos, movimientos ni permisos de rol. No altera V56 aplicada.
  La búsqueda, paginación, detalle y Excel existentes incorporan el contenido nuevo.
- Mantener visibles sólo guías compatibles con permisos del usuario. El caso de beca
  requiere además Tipos de beca, Becas, Políticas de recargo e Historial de ajustes.
  Fechas de ejemplo se adaptan al ciclo; no forzar el reloj ni hacer operaciones reales
  para leer una guía. Tasas/topes alternativos no se declaran probados por este caso.
- El propietario aprobó la claridad de los pasos de la primera guía V56; no confundir
  esa aprobación con una revisión visual exhaustiva en todos los dispositivos/temas.
- Próxima migración V58. Revisar las dos nuevas guías y continuar documentando casos
  confirmados; conservar pendientes de capturas y guías específicas de portales.
- Despliegue confirmado: 502 pruebas, V57 exitosa, tres guías con 23 pasos y salud `UP`.
  Se verificó en SQL la exclusión de la guía de becas al faltar ese permiso. El propietario
  puede recargar Guía de procesos; no requiere reconstruir otra vez para consultar.

## V56 — Guía de procesos y casos prácticos — 2026-10-06

- Primera entrega administrativa en Seguridad y soporte → Guía de procesos (`/admin/guias`).
  Asignar `GUIA_PROCESOS_CONSULTAR` desde Roles y permisos y reiniciar sesión; la migración
  no lo otorga automáticamente. Exige usuario persistido y permisos de los módulos del
  proceso para listar, abrir o exportar. La guía no concede permisos ni usa datos operativos.
- V56 guarda contenido editorial global, requisitos de permisos y pasos en tablas de guía.
  Son ejemplos ficticios versionados, no expedientes de personas. Sin edición operativa;
  posteriores revisiones de contenido se publican mediante nuevas migraciones controladas,
  actualizando versión/fecha. Listado filtrado/paginado en PostgreSQL; Excel POI por bloques
  comparte búsqueda, categoría, estado y restricciones de permisos.
- Primera guía confirmada: transferencia $1,000 para cargos $600/$400 de dos hijos.
  Ocho etapas: vínculos/inscripciones, concepto, cuotas individuales, vista previa/generación,
  saldo B de referencia, reporte familiar, validación y comprobación por hijo/comprobante.
  Cada etapa incluye perfil, módulo, instrucciones numeradas, ejemplo, resultado y precaución.
- El detalle reutiliza el diseño administrativo, temas y acciones; índice de pasos,
  secciones desplegables y enlaces GET a módulos en otra pestaña. No registra pagos ni
  marca operaciones realizadas al avanzar. Para probar con tutor desde administración,
  usar una sesión privada separada; la guía no suplanta cuentas familiares.
- Las capturas de pantalla reales y el acceso directo a guías desde portales familiar/docente
  quedan para ampliaciones posteriores con permisos y contenido propios; no se inventan
  capturas ni se exponen guías administrativas a esos perfiles.
- Próxima migración disponible V57. Verificar como propietario asignación del módulo,
  búsqueda, pasos, Excel, tema oscuro y móvil. Continuar añadiendo sólo casos confirmados.
- Despliegue confirmado: 500 pruebas, V56 aplicada, ocho pasos, servicio `UP` y filtros de
  permisos comprobados en PostgreSQL. No se cambiaron roles ni datos operativos. El propietario
  debe asignar Guía de procesos a su rol y reiniciar sesión para la revisión visual final.

## Confirmación funcional del propietario — transferencia de $1,000 — 2026-10-06

- El propietario confirmó que validó el pago, la cuenta financiera aumentó $1,000,
  el portal familiar dejó de mostrar esa cantidad como deuda y apareció el comprobante.
  Estos resultados son confirmación humana, no una operación ejecutada por el agente.
- El flujo probado se conserva para la guía: Portal familiar → Tus pagos → Reportar
  transferencia → seleccionar cargos → cuenta → datos y comprobante → enviar a revisión;
  administración → Pagos recibidos → Ver → Gestión → validar y publicar; después comprobar
  saldo de la cuenta y regresar al portal para revisar pendientes y comprobante oficial.
- Ejemplo acordado: una transferencia $1,000 para dos hijos, con cargos $600 y $400.
  El propietario confirmó también la comprobación final: Distribución $600 y $400,
  consulta individual con saldo de esos cargos $0 e importe propio en el historial.
  Caso de una transferencia para dos hijos aprobado funcionalmente por el propietario.
  Es el primer caso confirmado para incorporar al módulo Guía de procesos y casos prácticos.
- No marca aprobados otros escenarios ni la revisión móvil/oscura. No cambia código,
  permisos, cuotas, cargos o pagos. Este caso ya se incorporó a la guía inicial V56.

## Transferencia familiar en una sola pantalla — 2026-10-06

- Reportar transferencia sigue el flujo: total calculado encima de cargos → seleccionar
  cargos → cuenta destino habilitada tras consultar por cargo → fecha/referencia/datos →
  comprobantes → resumen final y Enviar transferencia a revisión. No usa un asistente
  con pantallas ni botones Siguiente. En celular el total permanece visible al desplazarse.
- Revisión final reúne cada cargo e importe, total, cuenta, fecha en dd/MM/yyyy, referencia
  y archivos seleccionados; cambia inmediatamente al editar. El selector permite reemplazar
  los archivos elegidos y muestra nombres/tamaños. No altera validaciones ni publica pagos.
- Guía ilustrativa: dos cargos $600 y $400 actualizan el total a $1,000; seleccionar la
  cuenta bancaria del plantel, fecha/hora del comprobante, referencia y archivo; revisar
  al final y enviar. Queda en revisión y no reduce saldos hasta validación administrativa.
- Mantiene campos/CSRF/endpoints y diseño familiar claro/oscuro. Cuenta bloqueada sin cargo
  o durante la consulta, con explicación visible. Para grupos de hijos en planteles distintos
  sigue requiriendo una transferencia separada. Sin migración; V56 continúa disponible.
- Prueba JavaScript: `node src/test/js/portal-pago-flujo.test.cjs`, con DOM simulado y
  consultas ficticias; no usa sesión ni registra operaciones reales.

## Claridad de pagos familiares — 2026-10-06

- Tus pagos separa Resumen de tu cuenta, Lo que falta por pagar y Tus pagos y comprobantes.
  La lista consulta `POR_PAGAR` (saldo positivo) en PostgreSQL antes de contar y paginar:
  conserva pendientes, parciales y vencidos, excluye liquidados, cancelados y convenidos.
  El resumen sigue usando TODOS para no perder abonos históricos ni alterar cálculos.
- La tabla pendiente muestra concepto/periodo, total con ajustes, abonos efectivos, saldo,
  fecha límite y estado. La deuda vencida se identifica como parte del saldo, no otro total.
  Historial aclara importe destinado al alumno y transferencias en revisión sin efecto aún.
- Guía: Familias → seleccionar hijo → Tus pagos → revisar saldo y cargos pendientes →
  Reportar transferencia si corresponde → revisar estado en historial; el saldo sólo baja
  al validar. Ejemplo ilustrativo: cargo $1,000, abonos $300, falta $700; una transferencia
  de $200 en revisión no reduce todavía esos $700. Sin saldo aparece Estás al corriente.
  No confundir una página fuera de rango con ausencia de deuda.
- Mantiene diseño familiar, claro/oscuro, tarjetas móviles, filtros de historial y ambos
  paginadores. Soporte comparte la vista de consulta. No hubo migración ni movimientos.
  El ingreso total, resultado familiar y desglose por hijo están confirmados,
  según la confirmación funcional al inicio de este documento.

## Vista previa de adeudos con becas — 2026-10-06

- Antes de confirmar la generación automática, la tabla desglosa importe original,
  beca por aplicar (monto, nombre y porcentaje/modalidad) y total estimado por cobrar.
  Sin beca aplicable muestra $0.00. El resumen suma todas las páginas, no sólo la visible.
- La consulta reutiliza la selección por inscripción, concepto y vigencia, el redondeo
  y el límite del monto fijo de la emisión. No genera cargos ni ajustes; mantiene
  paginación y procesamiento por bloques. No estima ajustes manuales ni recargos futuros.
- Procedimiento actualizado: Adeudos de alumnos → Generar automáticos → seleccionar
  alcance y corte → Visualizar cuotas por aplicar → verificar original menos beca y
  totales por alumno → confirmar sólo si son correctos. Si se cambia una beca, volver
  a consultar: la confirmación recalcula según los datos vigentes.
- El ingreso de $1,000, saldo familiar, comprobante y reparto por hijo están confirmados.
  Mantener las pruebas de otros escenarios bajo control del propietario;
  no generar sus adeudos ni registrar pagos mediante herramientas de desarrollo.

## Regla acordada — guía viva por procesos probados — 2026-10-06

- La fuente principal de ayuda será un módulo integrado **Guía de procesos y casos
  prácticos**, no un PDF estático. Se implementará progresivamente sobre funcionalidades
  estables; no esperar a que termine todo el sistema ni describir funciones aún inexistentes.
- Cada proceso probado y confirmado por el propietario debe incorporarse a la guía con
  escenario, requisitos y permisos, ejemplo ficticio completo, menú/módulo/botón exactos,
  campos y valores de cada paso, resultados antes/después, saldos y estados esperados,
  lugares de comprobación, errores frecuentes, correcciones y variantes relacionadas.
- Distinguir siempre pruebas pendientes de escenarios confirmados; una implementación o
  prueba automatizada no sustituye la confirmación funcional del propietario. Si la guía
  aún no existe, conservar esos procedimientos en documentación de continuidad para su
  incorporación posterior. Nunca inventar resultados de una prueba no terminada.
- Al modificar una función estable, actualizar simultáneamente su procedimiento, ejemplo
  e imágenes. Esto forma parte de la definición de terminado. La guía tendrá búsqueda por
  situaciones, navegación por proceso/perfil, escenarios vinculados y versión/fecha de revisión;
  cada perfil sólo verá guías acordes a sus permisos. Mantener el diseño y los temas del sistema.
- Los ejemplos se marcarán ilustrativos: la guía no ejecutará registros ni pagos, y marcar
  un paso revisado no significa que el sistema haya realizado la operación. Utilizar datos
  controlados autorizados para las pruebas; no mezclar ejemplos con información operativa real.
- Al cierre del proyecto se hará una revisión integral. El PDF podrá derivarse del mismo
  contenido como complemento, no como fuente paralela. Primer caso aprobado para la guía:
  un pago de $1,000 para dos hijos ($600 y $400), confirmado el 06/10/2026.
- La decisión inicial sólo actualizó documentación; posteriormente V56 implementó el
  módulo administrativo y el primer caso confirmado. Ampliar progresivamente el contenido.

## Corrección visual de búsqueda de cargos en convenios — 2026-10-06

- Nuevo convenio incorpora `form-grid` al buscador de cargos pendientes para reutilizar
  los inputs, bordes, foco, tipografía y tema oscuro comunes. Se mantiene el layout
  responsivo de `agreement-charge-search` y la búsqueda existente. No requiere migración;
  V56 continúa disponible.

## V55 — soporte del portal docente — 2026-10-05

- Seguridad y soporte incorpora **Soporte del portal de maestros** bajo
  `/admin/portal-maestros-soporte`. V55 crea `PORTAL_MAESTRO_SOPORTE` sin concederlo
  automáticamente a roles existentes. El propietario debe asignar el módulo desde Roles y
  permisos y volver a iniciar sesión. Exige administrador persistido con alcance institucional.
- Selección paginada de maestros activos con cuenta docente activa de la misma institución,
  búsqueda por nombre, empleado o usuario y Excel Apache POI con los mismos filtros por bloques.
- Reutiliza el portal real: planeaciones y filtros, horario, alumnos asignados, fotos,
  ficha médica, detalle de planeación, captura original protegida y PDF privado inline.
  No cambia el SecurityContext ni inicia sesión como el docente. Cada consulta de portal
  registra al administrador real en auditoría sin almacenar datos médicos ni credenciales.
- Sólo admite consulta: no crear, editar, enviar ni descartar; la captura no carga su script
  de edición y sus campos están deshabilitados. Un banner identifica siempre al maestro y
  permite cambiarlo o volver a administración. Los enlaces permanecen dentro del contexto.
- La siguiente migración disponible es V56. Queda la prueba visual y funcional autenticada
  del propietario con dos maestros de prueba, incluyendo aislamiento entre sus planeaciones
  y alumnos. No se crearán datos docentes reales sólo para verificar.
- Docker aprobó 483 pruebas, incluidos renderizado compartido, captura protegida, portal
  normal, permisos y aislamiento institucional. No se modificó ningún expediente docente.
- Se desplegó sólo `app`; PostgreSQL confirmó V55 y el permiso nuevo, y salud respondió
  `UP`. Los volúmenes permanecieron intactos. No requiere otra reconstrucción para probar.

## Captura guiada de cuotas — 2026-10-05

- Cuotas por alumno separa **Una sola vez** (importe y Fecha límite de pago) de **Cada mes**
  (primer/último mes y día límite). La vigencia técnica queda en Configuración adicional,
  con sugerencias autorizadas según la intersección entre inscripción y ciclo escolar.
- El servidor completa fechas ausentes de una cuota única y deriva las fechas mensuales
  desde los meses elegidos. Conserva rangos parciales existentes al editar el mismo mes;
  no modifica cuotas históricas ni cargos ya emitidos. Se mantienen las validaciones de
  vigencia, solapamientos y claves de generación idempotentes.
- El resumen explica que un cobro único no se repite y que guardar la configuración no es
  recibir dinero. Generación automática continúa siendo una ejecución explícita desde
  Adeudos de alumnos. No requiere migración; V55 permanece disponible.
- La revisión visual autenticada queda para el propietario: el navegador de verificación
  llegó al login por sesión caducada. No se consultaron credenciales ni se crearon cuotas.
- Docker aprobó 474 pruebas y la verificación JavaScript comprobó alternancia de campos,
  fechas mensuales y resumen monetario. No se hicieron operaciones de cobranza de prueba.

Este archivo permite continuar el desarrollo desde otra computadora o una conversación
nueva sin perder las decisiones tomadas. Antes de modificar código, lee también
`CONTEXTO_PROYECTO.md`, `README.md` y `modelo_entidades_sistema_escolar_v1.txt`.

## Objetivo y forma de trabajar

Construir un sistema administrativo escolar profesional, multiinstitución y
multiplantel. La comunicación con el propietario del proyecto debe ser en español.
Trabaja exclusivamente dentro de la carpeta del repositorio y conserva los cambios
existentes. No elimines datos, volúmenes Docker ni archivos sin autorización explícita.

Cuando el usuario diga «continúa» o «siguiente paso», retoma la sección **Siguiente
paso acordado** de este documento. Primero inspecciona el estado real del repositorio;
no vuelvas a implementar componentes que ya existan.

## Repositorio y estado confirmado

- Repositorio privado: `Criz110gmail/Instituto_raices`.
- Remoto esperado: `https://Criz110gmail@github.com/Criz110gmail/Instituto_raices.git`.
- Rama principal: `main`.
- Confirma siempre `git status` y `git log` antes de continuar; el propietario realiza
  manualmente los commits y el `push`.
- Estado más reciente verificado el 02/10/2026: V54 implementa **Convenios de pago**.
  Un convenio selecciona mediante búsqueda hasta 100 cargos pendientes de uno o varios
  hijos del mismo tutor, conserva pagos previos, marca los originales como `CONVENIDO` y
  distribuye proporcionalmente el nuevo monto en un cargo por inscripción. Bloquea cargos
  con transferencias pendientes y la cancelación sólo procede si los cargos nuevos aún no
  tienen pagos; entonces cancela los nuevos y reactiva los originales. El módulo tiene un
  único permiso funcional, alcance institucional, listado paginado, filtros, Excel y fechas
  `dd/MM/yyyy`. V54 se aplicó correctamente, Docker aprobó 454 pruebas y el servicio
  respondió `UP`. La siguiente migración
  disponible es V55.
- Ajuste visual verificado el 03/10/2026: la navegación administrativa se ordena por el
  proceso real en dos áreas, **Operación escolar** y **Administración**, subdivididas en
  Control escolar, Gestión académica, Cobranza escolar, Comunicación, Finanzas, Compras y
  proveedores, Configuración escolar y Seguridad y soporte. Los nombres visibles son
  **Periodos de evaluación**, **Familiares del alumno**, **Adeudos de alumnos**, **Pagos
  recibidos** y **Soporte del portal familiar**. La pantalla de Roles usa exactamente los
  mismos nombres. No cambiaron slugs, URLs, permisos técnicos, entidades ni tablas. Docker
  compiló 719 fuentes principales y 129 de prueba y aprobó 456 pruebas. No hubo migración;
  V55 continúa disponible.
- La edición de Alumnos quedó dividida en
  seis pestañas —Ficha del alumno, Información del alumno, Contacto y domicilio, Notas
  administrativas, Expediente documental y Ficha médica—. Las tres secciones editables
  principales conservan un solo formulario y una sola actualización de la entidad; los
  errores abren automáticamente la sección correspondiente. La ficha general y la ficha
  médica cuentan con PDF Jasper institucional `inline`, logo de la escuela, fotografía
  del alumno cuando existe, paginación y formato de fechas `dd/MM/yyyy`. No hubo migración;
  Docker compiló 703 fuentes principales y 126 de prueba, aprobó 449 pruebas y el servicio
  desplegado respondió `UP`. La siguiente migración disponible continúa siendo V54.
- La edición de Tutores aplica el mismo patrón en seis pestañas y abre por defecto
  **Identificación oficial**; continúan Ficha del tutor, Información del tutor, Contacto y
  domicilio, Información laboral y Portal de familias. Los tres bloques editables comparten
  un solo formulario y los errores abren la pestaña correcta. Identificación ofrece una
  ficha Jasper PDF privada con logo institucional, identificación vigente cuando es imagen,
  datos personales, contacto, domicilio, trabajo y estado del portal. Docker compiló 704
  fuentes principales y 127 de prueba, aprobó 451 pruebas y el servicio desplegado respondió
  `UP`. No hubo migración; V54 sigue disponible.
- La interfaz distingue **Adeudos de alumnos** (obligaciones y saldos) de **Pagos recibidos** (dinero
  recibido). En Pagos, la cuenta declarada es obligatoria para cualquier método. El ajuste
  de un cargo permanece separado porque aumenta o disminuye la deuda sin registrar un
  ingreso. El detalle de pago usa estados y cancelación con diseño completo en temas claro
  y oscuro. Al validar, la cuenta declarada queda precargada y protegida; cambiarla es una
  corrección explícita con motivo obligatorio y auditoría de ambas cuentas. Este ajuste no
  requirió migración; la siguiente disponible continúa siendo V53.
- V51 incorpora **Tarjeta** como método de pago administrativo. No se crea una cuenta
  financiera por terminal: el pago se declara y valida contra la cuenta bancaria o de
  inversión donde la terminal deposita; efectivo continúa usando caja. El pago rápido
  iniciado desde una inscripción protege institución, plantel, tutor titular y moneda,
  manteniendo editables sólo los datos propios de la operación. En la distribución, cargo
  y saldo completo quedan protegidos por defecto; un abono menor requiere activar
  explícitamente **Registrar pago parcial**. V52 crea la secuencia global de base de datos
  para el folio interno del pago: se asigna al guardar como
  `PAG-AAAAMMdd-consecutivo`, nunca lo captura el usuario y no se reutilizan números aunque
  una transacción se revierta. El folio permanece en consultas y comprobantes. Las ayudas
  informativas usan un estilo neutro diferenciado de los errores rojos. La siguiente
  migración disponible es V53.
- V50 implementa Proveedores y Compras. El catálogo de proveedores es multiinstitución;
  cada compra se captura con partidas, proveedor, plantel opcional, cuenta de caja/banco
  y categoría financiera. Guardar conserva un borrador sin afectar saldos, confirmar crea
  el egreso en Movimientos financieros y cancelar conserva el egreso original y publica
  una reversa compensatoria. Ambos listados son paginados, filtrables y exportables a
  Excel con los mismos criterios.
- La mejora posterior a V50 incorpora cobranza asistida sin acoplar los módulos: desde
  la edición de una inscripción se consulta su cuota e historial de pagos por cobrar, se
  prepara una cuota única con el contexto precargado, se puede emitir inmediatamente su
  cargo idempotente y abrir Pagos con cargo, saldo y tutor financiero precargados. No
  requirió migración; V51 continúa disponible.
- V49 implementa la Actualización familiar de expedientes. Un tutor con vínculo activo
  y autorización puede proponer documentos o una ficha médica completa desde su portal;
  cada envío conserva el consentimiento y no modifica el expediente oficial hasta que
  administración lo aprueba. La bandeja administrativa es paginada, filtrable y exportable
  a Excel. La siguiente migración disponible es V50.
- V48 implementa el Calendario escolar detallado por ciclo, con días inhábiles,
  vacaciones, eventos académicos y variaciones de horario. Cada registro puede aplicar
  a toda la institución, a un plantel, a un nivel o a la combinación plantel–nivel.
  Incluye seguridad por alcance, validación de fechas y cruces de suspensiones,
  listado paginado, filtros y Excel con los mismos criterios.
- Las fotografías nuevas de alumnos, maestros y usuarios aceptan JPEG, PNG, HEIC y
  HEIF hasta 20 MB. ImageMagick/libheif corrige orientación, elimina metadatos,
  redimensiona a un máximo de 1920 px y guarda JPEG calidad 85; esta optimización no
  modifica retroactivamente fotografías históricas.
- V47 implementa Horarios y clases con bloques recurrentes por asignación docente,
  validación de cruces de maestro, grupo y aula, vigencia, permisos, listado paginado,
  filtros y Excel. El maestro consulta «Mi horario» y la familia consulta el horario
  vigente del alumno.
  Incluye cuentas de maestro separadas, asignaciones maestro–grupo–materia, captura
  estructurada, revisión administrativa, versiones publicadas, PDF y Excel.
- Nunca guardes tokens de GitHub, contraseñas o el contenido real de `.env` en Git.
- En equipos con varias cuentas de GitHub, conserva la configuración de credenciales
  a nivel local del repositorio y usa `credential.useHttpPath=true`.

## Tecnología y arquitectura

- Java 21, Maven y Spring Boot 4.1.1.
- Spring MVC con Thymeleaf; no es una SPA.
- PostgreSQL 17 mediante Docker Compose y volumen persistente `postgres_data`.
- Flyway administra exclusivamente el esquema; Hibernate usa `ddl-auto=validate`.
- Los archivos privados se guardan en el volumen Docker `private_files`; PostgreSQL
  conserva únicamente metadatos, checksum y relaciones.
- Para respaldar o mover una instalación se necesitan juntos `postgres_data` y
  `private_files`; nunca versionar las fotografías en Git.
- Git transporta código y migraciones, pero no transporta PostgreSQL ni los archivos
  de los volúmenes. Clonar el repositorio en otra computadora crea una instalación sin
  los datos operativos anteriores, salvo que se restauren ambos respaldos.
- Spring Data JPA, Bean Validation, Spring Security, Lombok, Actuator y Spring Mail.
- Apache POI 5.4.1 con `SXSSFWorkbook` para exportaciones Excel de bajo consumo de
  memoria.
- Paquete base: `escuela`.
- Módulos principales: `escuela.institucion`, `escuela.academico`, `escuela.seguridad`,
  `escuela.alumno`, `escuela.tutor`, `escuela.inscripcion`, `escuela.calificacion`, `escuela.cobranza`,
  `escuela.finanzas`, `escuela.comunicacion`, `escuela.auditoria`, `escuela.admin`,
  `escuela.config` y `escuela.common`.
- Cada entidad usa identificador `Long`, auditoría y versión para concurrencia optimista.
- Los controladores y formularios usan DTO; nunca deben enlazarse directamente con
  entidades JPA.
- Los registros históricos se desactivan; no se eliminan físicamente.

## Modelo implementado en la primera etapa

El núcleo institucional y académico contiene ocho entidades:

1. `Institucion`
2. `Plantel`
3. `NivelEducativo`
4. `PlantelNivel` (oferta educativa)
5. `Grado`
6. `CicloEscolar`
7. `PeriodoAcademico`
8. `Grupo`

Para las ocho entidades ya existen migración Flyway, entidades, repositorios, DTO de
entrada y salida, mappers, servicios transaccionales y reglas de negocio. La migración
actual es `V1__crear_nucleo_institucional_academico.sql`.

Reglas importantes ya aplicadas:

- Los códigos, país, moneda y correo se normalizan antes de persistir o comparar.
- No se reasignan relaciones propietarias durante una actualización.
- Toda actualización exige la versión recibida y valida concurrencia optimista.
- Un ciclo predeterminado desmarca el anterior dentro de una transacción con bloqueo.
- Los periodos no pueden salir del ciclo, solaparse, duplicarse ni modificarse si el
  ciclo está cerrado.
- Un grupo sólo opera con institución, plantel, nivel y grado activos, ciclo abierto y
  una oferta educativa activa para ese plantel y nivel.
- La situación académica vigente de un alumno se deriva de `Inscripcion` y
  `AsignacionGrupo`; no debe duplicarse como otra fuente de verdad.

## Interfaz terminada

- Consola administrativa disponible bajo `/admin`.
- Navegación lateral para los ocho catálogos.
- La navegación lateral desplaza únicamente la lista de módulos cuando supera la
  altura disponible; marca, encabezado y estado de servicios permanecen visibles. El
  módulo activo se centra automáticamente dentro del área desplazable.
- La navegación sólo incluye módulos para los que la sesión tenga permiso `*_LEER` o
  `*_ADMINISTRAR`; Roles y Usuarios requieren su permiso administrativo. `/admin`
  redirige al primer módulo autorizado y el controlador vuelve a validar el permiso
  antes de consultar o exportar. Los cambios de rol se reflejan al iniciar una sesión
  nueva, porque las autoridades se cargan durante el login.
- Diseño profesional en azul tinta y cian, totalmente responsivo para escritorio,
  tableta y móvil.
- Tema claro/oscuro seleccionable y persistido en el dispositivo.
- Login personalizado de Nexo Escolar; se sustituyó el formulario predeterminado de
  Spring Security.
- Listados de los ocho módulos con filtros ejecutados en PostgreSQL, estados vacíos y
  paginación real de 10, 25, 50 o 100 filas.
- Las pantallas nunca deben recuperar el catálogo completo para mostrar un listado.
- Los ocho módulos exportan Excel con Apache POI. La exportación reutiliza el mismo
  `FiltroCatalogo` que la pantalla y recorre los datos filtrados por bloques.
- Mantenimiento completo —alta, edición y desactivación lógica— para:
  `Institucion`, `Plantel`, `NivelEducativo`, `PlantelNivel` y `Grado`.
- `CicloEscolar` cuenta con alta, edición, estados y selección transaccional del ciclo
  predeterminado; no usa desactivación porque su ciclo de vida se expresa por estado.
- `PeriodoAcademico` cuenta con alta y edición de ciclo, nivel, tipo, fechas, estado y
  observaciones, validando rangos, duplicados, solapamientos y ciclos cerrados.
- `Grupo` cuenta con alta, edición y desactivación lógica, con selectores dependientes y
  validación de oferta educativa activa.
- Al crear grupos, los grados se consultan bajo demanda para el plantel seleccionado,
  sin caché ni carga completa inicial. La pantalla informa claramente si está
  consultando, si encontró grados, si falta oferta educativa activa o si ocurrió un
  error de conexión.
- `Alumno` y `Tutor` cuentan con expediente institucional, alta, edición,
  desactivación lógica, filtros, paginación y Excel. El tutor puede vincularse de
  manera opcional con una cuenta de usuario de su misma institución.
- Los formularios incluyen Bean Validation, CSRF, mensajes de resultado y versión
  optimista, manteniendo los temas y el diseño responsivo.
- Los errores esperables al crear, actualizar o desactivar se muestran en el mismo
  formulario sin perder la captura: reglas de negocio, concurrencia y restricciones de
  integridad. Nunca se presentan SQL ni detalles internos de PostgreSQL al usuario.
- Las relaciones con catálogos de alto volumen usan autocompletado remoto, nunca un
  `<select>` con todos los registros. La búsqueda inicia con tres caracteres, respeta
  permisos y alcance institucional y devuelve como máximo 20 coincidencias.

Archivos clave de la interfaz:

- `src/main/java/escuela/admin/controller/CatalogoAdminController.java`
- `src/main/java/escuela/admin/service/CatalogoConsultaService.java`
- `src/main/java/escuela/admin/service/ExcelCatalogoService.java`
- `src/main/java/escuela/admin/dto/FiltroCatalogo.java`
- `src/main/java/escuela/admin/dto/ModuloCatalogo.java`
- `src/main/resources/templates/admin/catalogo.html`
- `src/main/resources/static/css/admin.css`
- `src/main/resources/static/css/forms.css`
- `src/main/resources/static/js/theme.js`

Los controladores y formularios de mantenimiento ya terminados sirven como patrón:

- `InstitucionAdminController` + `InstitucionForm` + `institucion-form.html`
- `PlantelAdminController` + `PlantelForm` + `plantel-form.html`
- `NivelEducativoAdminController` + `NivelEducativoForm` + `nivel-form.html`
- `PlantelNivelAdminController` + `PlantelNivelForm` + `oferta-form.html`
- `GradoAdminController` + `GradoForm` + `grado-form.html`
- `CicloEscolarAdminController` + `CicloEscolarForm` + `ciclo-form.html`
- `PeriodoAcademicoAdminController` + `PeriodoAcademicoForm` + `periodo-form.html`
- `GrupoAdminController` + `GrupoForm` + `grupo-form.html`

## Requisitos no negociables para módulos nuevos

- Todos los enlaces de volver de encabezados administrativos comparten en `admin.css` el
  botón con borde de Pagos recibidos, incluidos los detalles de Planeaciones y Actualizaciones
  familiares. Mantener esa regla común en módulos nuevos, con tema oscuro y foco visible.

Todo módulo que se construya debe incluir desde su primera entrega:

1. Diseño profesional e innovador, accesible y 100 % responsivo.
2. Compatibilidad completa con temas claro y oscuro.
3. Consultas paginadas en base de datos; nunca cargar todos los registros para un
   listado.
4. Filtros enviados desde la pantalla y aplicados en la consulta JPA.
5. Exportación `.xlsx` con Apache POI usando exactamente los mismos filtros.
6. Exportación por bloques/streaming para soportar crecimiento futuro.
7. DTO de formulario separado de la entidad JPA.
8. Bean Validation, CSRF, reglas de negocio, auditoría y control optimista de versión.
9. Desactivación lógica cuando el dominio la permita; no borrado físico.
10. Pruebas proporcionales al riesgo y verificación real en Docker antes de entregar.
11. Errores de negocio, concurrencia e integridad mostrados dentro del mismo formulario.
12. Toda relación con un catálogo de alto volumen debe usar autocompletado remoto con
    consulta indexada, alcance de seguridad, mínimo de tres caracteres y resultado
    acotado; nunca cargar la colección completa al abrir el formulario.
13. Todo PDF, imagen o documento consultable debe contar con un endpoint privado con
    autorización por alcance, `Content-Disposition: inline` y un enlace con
    `target="_blank"`; sólo las exportaciones Excel se descargan automáticamente.
14. La consistencia visual es obligatoria y forma parte de la definición de terminado:
    cada módulo nuevo o modificado debe reutilizar la misma composición, espaciado,
    tipografía, tarjetas, filtros, controles, botones, ayudas, estados vacíos, mensajes y
    formularios de los módulos administrativos consolidados. Debe verificarse en escritorio
    y móvil, con tema claro y oscuro. No se considera terminada una funcionalidad si sólo
    funciona técnicamente pero sus filtros o formulario se ven distintos, sin estructura o
    desalineados. Antes de crear CSS especializado, reutilizar `admin.css`, `forms.css`,
    `navigation.css` y los patrones existentes; cualquier excepción visual debe justificarse.

## Correcciones y ampliaciones V44–V45

- Los PDF financieros y los documentos del expediente se visualizan en otra pestaña;
  ya no fuerzan una descarga al abrirlos.
- Roles y permisos reconoce el módulo Asistencia, evitando el fallo producido por el
  permiso técnico sin módulo funcional.
- El reporte familiar de transferencias muestra al enfocar los primeros diez cargos
  vigentes, excluye vencidos/cancelados/pagados y completa el saldo seleccionado.
- La ayuda contextual ya no invade los controles de carga de archivos.
- “Oferta educativa” se presenta al usuario como “Niveles por plantel” y los periodos
  académicos admiten el tipo anual.
- Los filtros de estado muestran exclusivamente los estados válidos de cada módulo;
  Asistencia incorpora fecha y la conserva al paginar y exportar.
- Boletas usa autocompletado de alumnos al enfocar y busca remotamente desde tres
  caracteres.
- Maestro admite fotografía privada en su ficha administrativa. El portal docente
  muestra “Mis alumnos” con acceso acotado a grupos vigentes, fotografía, datos
  generales y ficha médica.
- La edición de planeaciones vacía primero las colecciones dependientes antes de
  reconstruirlas, evitando falsos duplicados por restricciones únicas.

## Seguridad y variables de entorno

- `.env` existe sólo localmente y está ignorado por `.gitignore`.
- `.env.example` sí se versiona, pero contiene únicamente valores descriptivos.
- `compose.yaml` exige todas las variables; no contiene contraseñas de respaldo.
- `application.yml` exige `DB_PASSWORD` y tampoco conserva una contraseña predeterminada.
- Variables necesarias:
  `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `SPRING_PROFILES_ACTIVE`,
  `APP_PORT`, `ADMIN_BOOTSTRAP_USERNAME` y `ADMIN_BOOTSTRAP_PASSWORD`.
- Nunca copies al repositorio las credenciales del equipo anterior. En una computadora
  nueva crea `.env` a partir de `.env.example` y genera contraseñas nuevas.
- Los usuarios activos de PostgreSQL ya pueden iniciar sesión. El administrador de
  `.env` se conserva temporalmente como acceso de recuperación controlado.
- Una sesión caducada redirige a `/login?sesionExpirada` y muestra un aviso explícito.
  Esto cubre navegación, envíos `POST` cuyo token CSRF venció y consultas asíncronas;
  los errores de permisos reales conservan la pantalla 403. El cierre normal elimina
  la cookie `JSESSIONID` para no producir falsos avisos de caducidad.

## Arranque en una computadora nueva

```bash
git clone https://github.com/Criz110gmail/Instituto_raices.git
cd Instituto_raices
cp .env.example .env
```

Edita `.env` localmente, define contraseñas seguras y selecciona un puerto disponible;
el proyecto se verificó usando `APP_PORT=18080`. Después ejecuta:

```bash
docker compose up --build -d
docker compose ps
curl http://localhost:18080/actuator/health
```

La imagen Docker compila el proyecto y ejecuta las pruebas Maven, por lo que Maven no
es obligatorio en el host. Docker sí debe estar instalado y en ejecución.

## Archivos privados, respaldo y decisión pendiente de producción

- La configuración actual debe conservarse durante desarrollo: el volumen nombrado
  `private_files` se monta como `/data/nexo-escolar` y la aplicación recibe esa ruta en
  `FILE_STORAGE_ROOT`. El volumen sobrevive a reinicios, recreaciones de contenedores y
  a `docker compose down` sin opciones destructivas.
- Nunca ejecutar `docker compose down -v` ni eliminar volúmenes sin autorización y un
  respaldo comprobado. Esa opción elimina `postgres_data` y `private_files`.
- Un respaldo recuperable debe capturar en el mismo corte PostgreSQL y los archivos;
  la base guarda la relación entre alumno y clave física. Los dos artefactos deben
  copiarse fuera de Docker y fuera de la computadora, cifrados y con acceso limitado.
- Comandos manuales de referencia, ejecutados desde la raíz en una ventana de
  mantenimiento. Se detiene sólo la aplicación para impedir cargas durante el corte;
  PostgreSQL y los volúmenes no se eliminan:

```bash
mkdir -p ../respaldos_instituto_raices
docker compose stop app
docker compose exec -T postgres sh -c \
  'pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB"' \
  > ../respaldos_instituto_raices/base_datos.sql
docker compose run --rm --no-deps app \
  tar -C /data/nexo-escolar -czf - . \
  > ../respaldos_instituto_raices/imagenes_privadas.tar.gz
docker compose start app
tar -tzf ../respaldos_instituto_raices/imagenes_privadas.tar.gz | head
```

- La carpeta `../respaldos_instituto_raices` queda fuera del repositorio, pero todavía
  está en la misma computadora: luego debe copiarse a un destino privado externo. Los
  `.sql`, los archivos comprimidos y cualquier fotografía real deben
  permanecer fuera de Git. Antes de confiar en el mecanismo se debe documentar y probar
  también la restauración en una instalación aislada.
- Decisión pendiente antes de producción: para una sola instancia puede mantenerse el
  volumen con respaldo automatizado externo. Un bind mount sólo se usará si la operación
  necesita una ruta del host para su agente de respaldo; será una ruta dedicada como
  `/srv/nexo-escolar/private-files`, nunca una carpeta dentro del repositorio. Si se
  despliegan varias instancias, se evaluará almacenamiento de objetos privado compatible
  con S3/MinIO. Cambiar de backend no debe alterar la abstracción de almacenamiento ni
  guardar rutas físicas en PostgreSQL.
- Pendiente operativo: automatizar respaldos, definir retención/cifrado/destino externo,
  comprobar restauraciones y acordar RPO/RTO antes de usar datos reales en producción.

## Base de seguridad implementada

- La migración `V2__crear_seguridad_roles_permisos.sql` crea `Usuario`, `Rol`,
  `Permiso`, `RolPermiso`, `UsuarioRol` e `InvitacionUsuario` de forma aditiva.
- Existen entidades, repositorios, DTO, mappers y servicios transaccionales para crear y
  modificar usuarios invitados y roles, asignar permisos, asignar roles con alcance y
  activar usuarios mediante invitaciones.
- Se preserva la separación entre instituciones en usuarios, roles y planteles.
- V2 siembra 18 permisos técnicos. Las invitaciones sólo guardan SHA-256 del token, son
  de un solo uso y la contraseña se codifica con BCrypt.
- `UsuarioSistemaDetailsService` autentica usuarios activos de PostgreSQL con su hash
  BCrypt y obtiene autoridades de asignaciones, roles y permisos activos.
- Un username único puede usarse directamente. Si se repite entre instituciones, el
  identificador es `CODIGO_INSTITUCION\usuario`.
- El usuario temporal de `.env` sigue disponible como recuperación y recibe todos los
  permisos técnicos existentes. No retirar este puente hasta comprobar la operación y
  recuperación con usuarios persistidos.
- `Roles y permisos` ya es un módulo visible en la consola. Incluye filtros en base de
  datos, paginación, Excel, alta, edición, desactivación lógica y selección de los 18
  permisos técnicos con errores dentro del mismo formulario.
- La migración V3 agrega `activo` a `RolPermiso` para conservar el historial al retirar
  o volver a conceder permisos; V2 y V3 ya fueron aplicadas y no deben editarse.
- `Usuarios` ya es visible en Seguridad con filtros PostgreSQL, paginación, Excel, alta
  de invitados, edición, estados, asignaciones de rol y alcances. Las asignaciones se
  desactivan y reactivan lógicamente.
- `/activar-cuenta` es público y permite consumir una invitación de 48 horas para fijar
  una contraseña BCrypt. El enlace se muestra una sola vez al administrador y la base
  conserva únicamente SHA-256 del token.
- El principal autenticado conserva usuario, institución y planteles asignados. Los
  listados, Excel, selectores y operaciones por ID aplican ese alcance en servidor.
- El alcance institucional ve sólo su institución; el de plantel restringe planteles,
  oferta y grupos a los asignados. Los catálogos académicos compartidos permanecen
  limitados a la institución. `VINCULOS_TUTOR` no concede acceso administrativo; con
  permiso de lectura sólo permite consultar relaciones propias, activas y vigentes.
- La creación de instituciones queda reservada al acceso de recuperación, y administrar
  roles o usuarios exige alcance institucional para impedir escalamiento de privilegios.
- Los permisos o alcances insuficientes muestran una pantalla 403 propia, responsiva y
  compatible con los temas claro y oscuro.
- Spring Data Auditing obtiene el actor desde `UsuarioPrincipal`: las altas guardan
  `creado_por_id` y los cambios guardan `actualizado_por_id`. El acceso de recuperación
  y los procesos sin usuario persistido conservan actor nulo mediante un listener que
  evita conservar por error el actor anterior.
- Los eventos de autenticación registran `ultimo_acceso_en`, reinician el contador al
  entrar correctamente e incrementan sólo contraseñas erróneas de cuentas identificables.
- Cinco fallos consecutivos producen un bloqueo automático de 15 minutos. Al vencer,
  un acceso correcto reactiva la cuenta; un bloqueo administrativo sin fecha no vence.
- Usuarios desconocidos, ambiguos, invitados, inactivos y el acceso de recuperación no
  acumulan intentos. La pantalla conserva un mensaje de error uniforme.
- La migración V4 crea `RecuperacionPassword`, separada de las invitaciones. Un
  administrador puede generar para un usuario activo un enlace de 30 minutos que se
  muestra una sola vez; otro enlace revoca el anterior y la base sólo conserva SHA-256.
- `/restablecer-password` permite fijar una nueva contraseña BCrypt, consume el token,
  limpia intentos y bloqueos temporales, pero nunca retira un bloqueo administrativo.
- Todavía no existe entrega por correo: no hay SMTP configurado y los correos actuales
  pueden no ser reales. El administrador entrega el enlace por un canal verificado.
- La migración V5 crea `Alumno` y agrega los permisos `ALUMNO_LEER` y
  `ALUMNO_ADMINISTRAR`. No concede permisos nuevos automáticamente a roles existentes.
- `Alumnos` ya tiene aislamiento institucional, listado filtrado y paginado en
  PostgreSQL, Excel por bloques, alta, edición y desactivación lógica.
- El propietario confirmó en el navegador que `criz110` ya puede ver y operar el
  módulo Alumnos con los permisos asignados a su rol.
- Matrícula y CURP —cuando existe— son únicas por institución. No se guarda plantel,
  grado o grupo actual: esa trayectoria se derivará de las inscripciones futuras.
- Los campos `LocalDate` de alumnos, ciclos y periodos declaran formato ISO explícito
  para que los controles HTML de fecha carguen correctamente al editar.
- La migración V6 crea `Tutor` y agrega `TUTOR_LEER` y `TUTOR_ADMINISTRAR` sin
  concederlos automáticamente a roles existentes. Una cuenta de usuario sólo puede
  vincularse con un tutor y debe pertenecer a la misma institución.
- `Tutores` incluye aislamiento institucional, filtros y paginación en PostgreSQL,
  Excel por bloques, alta, edición y desactivación lógica. Los errores permanecen en
  el formulario y la fecha de nacimiento usa formato ISO.
- El propietario confirmó que `criz110` ya tiene los permisos y puede ver Tutores.
- La migración V7 crea `AlumnoTutor` y agrega `VINCULO_TUTOR_LEER` y
  `VINCULO_TUTOR_ADMINISTRAR` sin concederlos automáticamente. Administra parentesco,
  contacto principal, responsabilidad financiera, autorizaciones y vigencias sin
  borrar el historial.
- El propietario confirmó que `criz110` ya tiene los permisos y validó el módulo de
  vínculos. Flyway V8 agrega búsquedas indexadas con `pg_trgm` para alumnos, tutores y
  usuarios.
- Flyway V9 crea `Archivo`, `AlumnoFotografia` y `fotografia_archivo_id`. El expediente
  permite cargar JPEG/PNG de hasta 5 MB, reemplazar o retirar la foto y consultar el
  historial sin exponer el almacenamiento privado.
- Al editar un alumno, la pantalla inicia con una ficha técnica visual: fotografía
  destacada, nombre, matrícula, institución, estado y datos rápidos. La carga, retiro e
  historial de fotografías forman parte de esa cabecera; debajo permanece el
  formulario completo del expediente.
- Los listados y los trece formularios administrativos muestran `Cerrar sesión`. El
  botón envía `POST /logout` con CSRF, invalida la sesión y regresa al inicio.
- Flyway V12 agrega `fotografia_archivo_id` a `Usuario`. La edición de usuario permite
  cargar o reemplazar JPEG/PNG de hasta 5 MB y restaurar el avatar genérico; los bytes
  permanecen en `private_files` y sólo se sirven mediante rutas autenticadas.
- El encabezado compartido muestra siempre la fotografía —personalizada o genérica— y
  el nombre del usuario autenticado. En móvil conserva ambos datos mediante un segundo
  renglón responsivo y funciona en los temas claro y oscuro.
- Flyway V10 crea `Inscripcion` y `AsignacionGrupo`, y agrega los permisos
  `INSCRIPCION_LEER` e `INSCRIPCION_ADMINISTRAR` sin concederlos automáticamente.
- `Inscripciones` ya administra la trayectoria por alumno, plantel, ciclo y grado.
  Los traslados o promociones cierran el registro anterior y crean uno nuevo; los
  cambios de grupo cierran la asignación vigente sin sobrescribir el historial.
- El módulo valida institución, oferta activa, ciclo, vigencias, solapamientos y
  capacidad bajo bloqueo transaccional. Su listado pagina y filtra en PostgreSQL y la
  exportación XLSX reutiliza exactamente esos filtros y recorre los datos por bloques.
- El detalle de inscripción muestra una ficha técnica compacta del alumno con su
  fotografía privada actual, matrícula, nacimiento, CURP, plantel, ciclo y grado. La
  fotografía se entrega mediante la inscripción después de validar su alcance.
- Flyway V11 crea `ConceptoCobro` y `CuotaAlumno`, con cuatro permisos técnicos sin
  autoasignación. Los conceptos definen categorías y ajustes permitidos; no contienen
  un precio global.
- Cada cuota pertenece a una inscripción y, por tanto, a un solo alumno. Configura
  importe, moneda, frecuencia única o mensual, vigencia, vencimiento, estado y si la
  generación futura de cargos será automática o manual.
- Flyway V13 crea `Cargo` y los permisos `CARGO_LEER` y `CARGO_ADMINISTRAR`, sin
  concederlos automáticamente a roles existentes. Un cargo emitido pertenece siempre
  a una inscripción/alumno y conserva importe, descripción, periodo y vencimiento.
- Los cargos extraordinarios se emiten manualmente. El generador automático recorre
  cuotas por bloques, respeta institución/plantel y usa una clave única por cuota y
  periodo con `ON CONFLICT DO NOTHING`; reintentos y ejecuciones concurrentes no
  duplican obligaciones. Los cargos no se editan: sólo se cancelan con versión y motivo.
- Un pago futuro podrá cubrir varios hijos, pero deberá conservar aplicaciones separadas
  por cada cargo/alumno. Nunca se administrará un saldo familiar indistinto.
- Flyway V14 crea `TipoBeca`, `BecaAlumno` y `AjusteCargo`, además de seis permisos sin
  autoasignación. Cada beca pertenece a una inscripción y concepto, utiliza porcentaje
  o monto fijo y no puede solaparse con otra beca activa equivalente.
- Al emitir un cargo se aplica la beca vigente con redondeo `HALF_UP` a dos decimales y
  se congela su base, porcentaje y monto como ajuste histórico. Cambiar la beca sólo
  afecta cargos futuros. Los ajustes manuales y sus reversas son movimientos separados;
  nunca se sobrescribe ni elimina el original.
- Flyway V15 crea `PoliticaRecargo` y enlaza cada recargo automático con su política y
  una clave de generación única. Existe una sola política editable por concepto.
- El administrador elige porcentaje o monto fijo, días completos de gracia,
  periodicidad única o mensual, límite sin tope, por monto o por porcentaje del cargo,
  y puede pausar tanto la política como su generación automática.
- El recargo es simple: se calcula sobre el importe original más o menos ajustes que no
  sean recargos, por lo que nunca genera interés sobre recargos anteriores. Empieza el
  día posterior al vencimiento y a los días de gracia completos configurados.
- El generador procesa cargos por bloques de 100, admite corte institucional o por
  plantel y usa `ON CONFLICT DO NOTHING`; repetirlo no duplica un periodo. Los ajustes
  emitidos conservan importe, base, porcentaje, fecha y política históricos.
- Una reversa resta del acumulado usado para el límite, pero la clave histórica impide
  volver a cobrar automáticamente el mismo periodo.
- Flyway V16 crea `CuentaFinanciera` y dos permisos sin autoasignación. Cada cuenta
  pertenece a una institución y puede ser compartida o limitarse a un plantel.
- Los tipos iniciales son `CAJA`, `BANCO` e `INVERSION`. Caja no admite datos bancarios;
  banco e inversión requieren institución financiera y al menos número o CLABE.
- La moneda se limita por ahora a la predeterminada de la institución. El saldo inicial
  no puede ser negativo y su fecha no puede estar en el futuro. Mientras no existan
  movimientos, estos valores pueden corregirse con versión optimista.
- Los listados y Excel muestran únicamente los cuatro últimos caracteres de cuenta o
  CLABE. Los valores completos sólo aparecen en el formulario administrativo autorizado.
- Una cuenta institucional compartida sólo puede administrarse con alcance institucional;
  una cuenta de plantel también respeta los planteles asignados al usuario.
- Flyway V17 crea `Pago`, `ComprobantePago` y `SolicitudAplicacionPago`, además de
  `PAGO_LEER` y `PAGO_REGISTRAR` sin conceder permisos automáticamente.
- Efectivo y transferencia se registran exclusivamente en `PENDIENTE_VALIDACION`.
  La transferencia exige al menos un comprobante privado; el efectivo lo admite de
  forma opcional. Se aceptan hasta cinco JPEG, PNG o PDF de 10 MB cada uno, validados
  por firma y guardados fuera de las rutas públicas.
- Un pago pertenece a un tutor y puede distribuirse de forma solicitada entre cargos de
  varios hijos sobre los que conserve responsabilidad financiera vigente. Cada cargo
  aparece una sola vez, usa la moneda del pago y no puede solicitar más que su saldo
  actual; la suma tampoco puede superar el pago.
- La distribución solicitada no modifica cargos ni crea aplicaciones. La cuenta
  declarada es opcional y se limita a caja para efectivo o banco/inversión para
  transferencia. Folio e idempotencia son únicos por institución.
- Pagos cuenta con filtros y paginación PostgreSQL, Excel por bloques con los mismos
  filtros, alta responsiva, resumen en tiempo real y detalle/descarga protegidos.
- Flyway V18 crea `AplicacionPago`, `MotivoFinanciero` y `MovimientoFinanciero`, y agrega
  `PAGO_VALIDAR` sin concederlo automáticamente a los roles existentes.
- Sólo una cuenta administrativa persistida e identificable puede validar o rechazar;
  el acceso temporal de recuperación no autoriza movimientos de dinero.
- Validar bloquea primero pago y cuenta, y después los cargos en orden de identificador.
  Recalcula el saldo de cada cargo, reduce automáticamente una solicitud si otro pago
  consumió parte del adeudo y conserva el resto como monto disponible del pago.
- La validación publica un único ingreso por el total del pago, con secuencia y saldos
  de cuenta protegidos por bloqueo. Pago, aplicaciones y movimiento se confirman o se
  revierten juntos; las restricciones por pago, solicitud e idempotencia impiden dobles
  ingresos o aplicaciones al reintentar.
- Rechazar exige motivo y conserva comprobantes sin afectar cargos ni cuentas. Los
  saldos de Cargo, su situación pagada/parcial y los autocompletados ya descuentan las
  aplicaciones efectivas. Un cargo con aplicaciones no puede cancelarse y una cuenta
  con movimientos ya no permite cambiar moneda, fecha ni saldo inicial.
- Flyway V19 agrega `MOVIMIENTO_FINANCIERO_LEER`, índices de consulta y el libro
  inmutable paginado. Permite filtrar por cuenta mediante autocompletado, plantel,
  dirección, clase y fechas; muestra el saldo vigente de la cuenta y exporta a Excel
  exactamente los filtros visibles.
- Flyway V20 agrega `MOTIVO_FINANCIERO_LEER`, `MOTIVO_FINANCIERO_ADMINISTRAR` y
  `MOVIMIENTO_FINANCIERO_REGISTRAR`, sin concederlos automáticamente a roles.
- Motivos financieros es un catálogo institucional de ingreso, egreso o ambos. El
  motivo `COBROS_ESCOLARES` es reservado: no puede desactivarse, renombrarse ni cambiar
  a egreso porque sostiene la publicación de pagos validados.
- El registro manual publica únicamente movimientos `OPERACION`, usa una clave de
  idempotencia por formulario, bloquea la cuenta, toma la última secuencia, calcula
  saldo anterior/posterior y rechaza egresos que producirían saldo negativo.
- La cuenta se busca por autocompletado. El plantel, la institución, el motivo activo,
  la fecha de apertura, la zona horaria y el actor persistido se validan antes de
  guardar. El acceso de recuperación no puede publicar dinero.
- Los movimientos son inmutables: V20 no permite editarlos ni eliminarlos. Las
  correcciones se resolverán mediante reversas en una etapa posterior.
- Flyway V21 crea `TransferenciaCuenta`, agrega la relación desde los dos movimientos
  `TRASPASO`, el motivo técnico `TRASPASO_INTERNO` y el permiso
  `TRANSFERENCIA_CUENTA_REGISTRAR`, sin asignarlo automáticamente a roles.
- Una transferencia exige cuentas distintas, activas, autorizadas, de la misma
  institución y moneda. La fecha debe ser válida para ambas aperturas y la cuenta de
  origen debe conservar saldo no negativo.
- El servicio bloquea las dos cuentas por identificador ascendente, aunque origen y
  destino vengan en orden contrario. Crea en una sola transacción la transferencia, un
  egreso de origen y un ingreso de destino, cada uno con secuencia y saldo propios.
- La transferencia tiene idempotencia institucional y cada lado es único en la base de
  datos. El acceso de recuperación no puede mover fondos. V21 no implementa todavía la
  reversa; `REVERTIDA` queda reservado para una etapa posterior.
- Flyway V22 crea `DevolucionPago`, enlaza sus ajustes de aplicaciones y su movimiento
  único de egreso, y agrega `PAGO_DEVOLVER` sin asignarlo automáticamente a roles.
- Una devolución sólo parte de un pago validado, no cambia ese estado y nunca se modela
  como cancelación. Valida fecha, moneda, alcance, saldo de cuenta, límite total del pago,
  versión e idempotencia institucional. El acceso de recuperación no puede ejecutarla.
- El monto disponible se calcula como pago menos aplicaciones netas menos devoluciones
  ejecutadas. Si no basta, el usuario selecciona aplicaciones vigentes: cada una se
  revierte completa y, cuando sólo se necesita liberar una parte, se crea una nueva
  aplicación por el remanente. El original nunca se edita ni elimina.
- La devolución, los ajustes de cargos y un solo movimiento `DEVOLUCION/EGRESO` se
  confirman en la misma transacción. `DEVOLUCION_PAGO` es un motivo técnico reservado.
- El expediente muestra saldos, historial y formulario responsivo. La cuenta se busca
  por autocompletado remoto; cualquier validación o restricción vuelve a la misma
  pantalla conservando los datos capturados.
- Flyway V23 crea `ReversionFinanciera`, enlaza sus movimientos compensatorios y agrega
  `MOVIMIENTO_FINANCIERO_REVERTIR` sin asignarlo automáticamente a roles existentes.
- Sólo las operaciones manuales y transferencias aplicadas se revierten por este flujo.
  Cobros, devoluciones, aplicaciones de pagos y otros movimientos técnicos conservan
  sus procesos específicos. El original nunca se edita ni elimina.
- Una operación manual produce un `REVERSO` con dirección contraria, mismo importe y
  relación única con el original. Si deshace un ingreso, la cuenta debe conservar saldo.
- Una transferencia se revierte completa: bloquea ambas cuentas por ID ascendente,
  exige fondos en la receptora, publica el egreso de retorno y el ingreso en el origen
  dentro de una sola transacción, y cambia la transferencia a `REVERTIDA`.
- La cabecera de reversa conserva tipo, objetivo, fecha, motivo, actor e idempotencia.
  No existe segunda reversa. La fecha no puede ser futura ni anterior al original o a
  la apertura de las cuentas. El acceso de recuperación no puede mover fondos.
- El libro muestra acciones únicamente para objetivos elegibles, estado revertido y el
  movimiento original compensado. El formulario es responsivo y conserva errores.
- Flyway V24 crea `CorteCaja` y agrega `CORTE_CAJA_LEER` y
  `CORTE_CAJA_ADMINISTRAR` sin asignarlos automáticamente a roles existentes.
- Sólo las cuentas de tipo `CAJA` pueden abrir un corte. La apertura captura hora del
  servidor, usuario persistido, último folio y saldo vigente; una restricción parcial
  impide dos cortes abiertos en la misma caja. Una caja inactiva no abre cortes nuevos,
  pero sí permite cerrar el que ya estuviera pendiente.
- El cierre bloquea primero la cuenta y después el corte, toma el folio final y resume
  ingresos y egresos por secuencia contable. Por ello, un movimiento capturado después
  no se pierde aunque su fecha operativa sea anterior a la apertura.
- El usuario realiza un conteo ciego: la pantalla no revela el saldo esperado antes de
  declarar el efectivo. Al cerrar conserva saldo esperado, efectivo declarado,
  diferencia, responsables y observaciones; cualquier diferencia exige justificación.
- Abrir o cerrar no crea movimientos monetarios. El cierre es inmutable, usa versión
  optimista y claves de idempotencia separadas para soportar reintentos seguros.
- El módulo tiene listado filtrado y paginado en PostgreSQL, búsqueda remota de cajas,
  Excel por bloques, detalle y formularios responsivos. Los usuarios de plantel sólo
  ven cajas y cortes de sus planteles; una caja institucional exige alcance institucional.
- Flyway V25 no crea saldos duplicados: agrega el permiso
  `REPORTE_FINANCIERO_CONSULTAR` y tres índices de apoyo para ajustes, aplicaciones y
  movimientos. El permiso no se asigna automáticamente a roles existentes.
- Reportes financieros ofrece dos vistas administrativas derivadas. El estado de cuenta
  reconstruye por alumno los cargos, ajustes y aplicaciones efectivos hasta una fecha
  de corte, y clasifica pendiente, parcial, pagado, vencido o cancelado. Un cargo parcial
  ya vencido se presenta como vencido y los cancelados conservan historial sin saldo.
- Tesorería agrupa por día, mes o año los ingresos y egresos operativos por cuenta. Los
  traspasos internos y sus reversas no inflan el flujo operativo y se muestran en
  columnas separadas. La fecha se interpreta en la zona horaria institucional.
- Los saldos de apertura y cierre sólo aparecen cuando se consultan cuentas completas.
  Al filtrar por plantel de operación se muestran flujos atribuidos, pero no se inventa
  una división del saldo de cuentas compartidas. El alcance de plantel tampoco expone
  cuentas institucionales.
- Ambos reportes filtran y paginan en PostgreSQL, usan autocompletado remoto para
  alumnos/cuentas y exportan los mismos filtros a XLSX por bloques. Son consultas de
  solo lectura: no publican movimientos ni modifican cargos, pagos o saldos.
- Flyway V26 crea `EventoEscolar` y `DestinatarioEvento`, además de
  `EVENTO_ESCOLAR_LEER` y `EVENTO_ESCOLAR_ADMINISTRAR`; los permisos no se asignan
  automáticamente a roles existentes.
- Un evento nace como `BORRADOR`, sólo el borrador se edita, la publicación es una
  acción explícita y la cancelación conserva fecha y motivo. No existe eliminación
  física. Los eventos se clasifican como junta, festival, suspensión, actividad u otro.
- El alcance puede ser institucional, de plantel o una selección. La selección admite
  nivel, grado, grupo y alumno con semántica de unión, valida pertenencia, oferta,
  inscripción vigente y ciclo, impide duplicados y exige al menos un destinatario.
- Las fechas se capturan en horario local y se guardan como instantes usando la zona
  horaria institucional; deben permanecer dentro del ciclo y no se admiten ciclos
  cerrados. Los usuarios de plantel no pueden administrar eventos institucionales.
- El módulo incluye listado filtrado y paginado en PostgreSQL, exportación XLSX por
  bloques, formulario, ficha, publicación, cancelación y autocompletado remoto de
  destinatarios. Los errores esperables se muestran en la misma pantalla. V26 todavía
  no envía correo, WhatsApp ni notificaciones y aún no es el portal del tutor.
- Flyway V27 agrega `PORTAL_TUTOR_ACCEDER` y un índice parcial para la agenda publicada;
  no asigna el permiso automáticamente ni crea copias de expedientes o saldos.
- `/portal` es una experiencia familiar separada de `/admin`. Una cuenta persistida debe
  corresponder a un tutor activo y sólo obtiene alumnos activos mediante vínculos activos
  y vigentes. Elegir otro identificador o solicitar su fotografía vuelve a validar el
  vínculo; el acceso de recuperación nunca entra al portal.
- La ficha muestra matrícula, parentesco, plantel, ciclo, grado, grupo y fotografía
  actual. Si no existe inscripción vigente se informa sin inventar una situación
  académica. El selector de hijos y toda la pantalla son responsivos y admiten tema
  claro u oscuro.
- La experiencia visual del portal es deliberadamente distinta de administración:
  lenguaje cotidiano, colores cálidos, jerarquía centrada en la fotografía y el nombre
  del hijo, agenda tipo línea de tiempo, pagos resumidos sin vocabulario contable y
  navegación inferior en móvil. El nombre institucional aparece sólo como contexto.
- `/familias` es la entrada dedicada para madres, padres y tutores: conserva el mismo
  motor de autenticación y las mismas credenciales seguras, pero muestra una bienvenida
  propia, dirige al portal después de autenticar y regresa allí al cerrar sesión o al
  equivocarse de contraseña. `/login` permanece como acceso del personal.
- El estado de cuenta se deriva de cargos, ajustes y aplicaciones existentes y sólo se
  muestra cuando el vínculo marca simultáneamente responsabilidad financiera y permiso
  para ver finanzas. La consulta queda limitada al alumno validado y se pagina en bloques
  de diez; el portal no registra pagos ni altera saldos.
- La agenda sólo muestra eventos `PUBLICADO` aplicables al alumno por ciclo y alcance
  institucional, plantel, nivel, grado, grupo o alumno. Incluye próximos eventos y los
  finalizados durante los últimos 30 días, con paginación. No expone borradores ni
  cancelados.
- Flyway V28 crea `Aviso` y agrega `AVISO_LEER` y `AVISO_ADMINISTRAR` sin asignarlos
  automáticamente a roles. Un aviso nace como borrador, sólo el borrador se edita, la
  publicación es explícita y el retiro exige motivo y conserva el historial.
- El alcance inicial de Avisos es institución o plantel. Los avisos generales exigen
  alcance institucional; los de plantel respetan los planteles asignados. El vencimiento
  es opcional y debe ser posterior a la publicación.
- La administración incluye filtros y paginación PostgreSQL, Excel por bloques, alta,
  edición, detalle, publicación y retiro con errores integrados en la pantalla.
- El portal familiar muestra por hijo sólo avisos publicados, ya vigentes, no vencidos
  y aplicables a toda la institución o a su plantel actual, paginados de diez en diez.
  V28 no crea notificaciones internas ni envía correo o WhatsApp.
- Flyway V29 crea `NotificacionUsuario` para la bandeja interna del portal. La
  sincronización ocurre al abrir `/portal`, genera como máximo una fila por usuario y
  origen mediante una clave única, e incorpora eventos publicados de los últimos 30
  días y avisos publicados vigentes que correspondan a cualquiera de sus hijos.
- Sólo se generan notificaciones para vínculos activos, vigentes y con
  `puede_recibir_notificaciones=true`. Eventos respeta institución, plantel, ciclo,
  nivel, grado, grupo o alumno; Avisos respeta institución o plantel.
- La campana muestra pendientes y la bandeja pagina diez filas en PostgreSQL, primero
  las no leídas. Al abrir una notificación se bloquea la fila, se comprueba que sea de
  la cuenta autenticada y se revalida que el evento o aviso siga accesible antes de
  marcarla como leída. No se borran filas ni se generan datos ficticios.
- V29 usa el permiso existente `PORTAL_TUTOR_ACCEDER`; no agrega permisos y no envía
  correo, WhatsApp ni notificaciones push.
- Flyway V30 amplía `NotificacionUsuario` con `pago_id` y los tipos
  `PAGO_VALIDADO` y `PAGO_RECHAZADO`. Al abrir el portal sincroniza resultados de los
  últimos 90 días para el tutor titular del pago, con clave única por usuario, pago y
  resultado; un reintento no duplica filas.
- Las notificaciones financieras exigen tutor, alumno y vínculo activos y vigentes,
  además de responsabilidad financiera, permiso para ver finanzas y permiso para
  recibir notificaciones. La lectura vuelve a validar usuario, institución, estado del
  pago y acceso financiero, y dirige a `#cuenta`.
- V30 no crea pagos, movimientos ni permisos, no cambia el proceso atómico de validación
  o rechazo y continúa sin correo, WhatsApp o push.
- Flyway V31 crea `Auditoria`, el permiso `AUDITORIA_CONSULTAR`, cuatro índices de
  consulta y un trigger PostgreSQL que rechaza cualquier `UPDATE` o `DELETE`. El permiso
  no se asigna automáticamente a roles existentes.
- `/admin/auditoria` permite consultar por institución, fechas, acción, tipo e ID de
  entidad, actor o correlación; pagina en PostgreSQL y exporta exactamente los mismos
  filtros a XLSX por bloques. Sólo admite alcance institucional.
- La bitácora captura en la misma transacción validación/rechazo de pagos,
  devoluciones, transferencias, reversas, cambios de permisos y roles, y
  publicación/cancelación o retiro de eventos y avisos. Los reintentos idempotentes no
  duplican registros.
- Cada petición recibe `X-Correlation-ID`; el registro atribuye el usuario persistido,
  el acceso de recuperación o un proceso de sistema. Nunca conserva contraseñas, hashes,
  tokens, cuentas bancarias, comprobantes ni contenido de archivos.
- Flyway V32 agrega `PAGO_CANCELAR` sin autoasignarlo a roles; registra actor y fecha de
  cancelación, permite la clase de movimiento `ANULACION` con relación única al ingreso
  original y amplía las notificaciones a `PAGO_CANCELADO`.
- Un pago pendiente se puede cancelar con motivo sin mover saldos. Un pago validado por
  error requiere saldo suficiente en la misma cuenta, no puede tener devoluciones
  ejecutadas y se cancela revirtiendo todas sus aplicaciones activas y publicando un
  solo egreso compensatorio. Pago, aplicaciones, movimiento y auditoría se confirman
  juntos. No se borran registros ni se reusa el flujo de devolución real.
- La cancelación exige usuario persistido, `PAGO_CANCELAR`, versión optimista y alcance
  de plantel/cuenta; un reintento con el mismo motivo no duplica movimientos. El pago
  rechazado no se cancela. El portal conserva las notificaciones anteriores como
  historial, pero deja de mostrar las que ya no coinciden con el estado vigente y
  presenta una nueva notificación de cancelación al tutor autorizado.
- La conciliación bancaria se descarta por decisión del propietario. No se importan
  estados de cuenta externos. El estado de cuenta por cuenta financiera es un reporte
  interno mensual o anual construido sólo con movimientos de Nexo Escolar; muestra
  apertura, ingresos, egresos, cierre y detalle paginado, con Excel y PDF equivalentes.
  Reutiliza `REPORTE_FINANCIERO_CONSULTAR` y no requiere Flyway V33.
- V33 registra retiros externos con destinatario, motivo, concepto y referencia de
  comprobante. Cada retiro enlaza un solo `OPERACION/EGRESO`, conserva el actor y una
  auditoría inmutable; no sustituye traspasos entre cuentas ni devoluciones de pagos.
  Usa permisos propios, listado paginado y Excel con los mismos filtros. Una reversa del
  movimiento lo muestra como revertido, sin borrar su expediente.
- V34 permite al tutor con acceso financiero reportar una transferencia desde el portal,
  adjuntar comprobantes privados y distribuir el importe exacto entre varios cargos
  autorizados, incluso de distintos hijos. El pago queda pendiente de validación; la
  administración puede validarlo o rechazarlo con motivo y se conservan origen,
  reportante e historial. Las etiquetas del autocompletado incluyen concepto,
  descripción e ID para distinguir varios cargos del mismo alumno.
- V35 agrega `PORTAL_TUTOR_SOPORTE` sin autoasignarlo. El soporte administrativo del
  portal es estrictamente de sólo lectura: permite localizar al tutor y observar la
  misma experiencia y alcance que él, pero no reportar pagos ni ejecutar mutaciones.
- La administración de roles presenta 31 módulos asignables con nombres amigables, no
  los 65 permisos técnicos. Cada módulo es todo o nada: seleccionar cualquiera de sus
  permisos históricos concede internamente todas sus acciones; guardar el rol normaliza
  sus relaciones al paquete completo. Los permisos técnicos siguen protegiendo cada
  endpoint y esa simplificación no requirió una migración. Los cambios de sesión requieren salir y
  volver a entrar.
- V36 crea `TutorIdentificacion` como documento privado opcional del expediente. Admite
  INE, licencia, pasaporte u otra identificación oficial en PDF, JPEG o PNG de hasta
  10 MB. Sólo puede existir una vigente por tutor; reemplazar o retirar conserva el
  historial y el archivo privado. La descarga revalida institución y acceso al módulo
  Tutores, responde sin caché y los bytes permanecen en `private_files`, no en PostgreSQL.
- V37 agrega `Usuario.tipoCuenta` con valores `ADMINISTRATIVO` y `PORTAL_TUTOR`. La
  migración reclasifica como portal las cuentas ya vinculadas desde `Tutor.usuario_id`.
  El catálogo y las rutas de Usuarios sólo administran cuentas administrativas; las
  cuentas familiares se crean, activan, recuperan, desactivan y reactivan exclusivamente
  desde la ficha del tutor. No reciben roles: su única autoridad se deriva del tipo de
  cuenta y es `PORTAL_TUTOR_ACCEDER`.
- Al crear acceso desde Tutores se propone un username normalizado `nombre.apellido`, se
  permite editarlo y se valida su unicidad. La cuenta nace `INVITADO` y se genera un enlace
  de activación de 48 horas; al establecer contraseña pasa a `ACTIVO`. Una cuenta activa
  puede recibir un enlace de recuperación de 30 minutos. Desactivar conserva credencial,
  relaciones e historial; reactivar regresa a `ACTIVO` si ya tenía contraseña o a
  `INVITADO` si nunca la configuró.
- El listado de Tutores muestra username y situación del acceso, y permite filtrar por
  `SIN_CUENTA`, `CUENTA_ACTIVA`, `CUENTA_PENDIENTE` o `CUENTA_INACTIVA`, además del estado
  activo/inactivo del expediente.
- V38 crea `AlumnoDocumento` y `FichaMedicaAlumno` dentro de la ficha técnica del alumno.
  Los documentos admiten PDF/JPEG/PNG de hasta 10 MB, categoría, descripción, fecha y
  vigencia; retirar conserva el historial y el archivo privado. La ficha médica única
  registra sangre, alergias, condiciones, medicamentos, apoyos, restricciones, servicio
  médico y contacto/autorización de emergencia con auditoría y versión optimista.
- V38 reutiliza los permisos y el alcance de Alumnos; no agrega permisos ni menú y no
  expone información médica al portal familiar. Los bytes permanecen en `private_files`.
- V39 agrega el módulo `Materias` y configura por grado el tipo de evaluación numérica o
  cualitativa, escala, mínima aprobatoria, decimales, orden, horas semanales e inclusión
  en boleta. El grado usa autocompletado remoto y no se cargan catálogos completos.
- `MATERIA_LEER` y `MATERIA_ADMINISTRAR` respetan el modelo de acceso completo por
  módulo y no se asignan automáticamente a roles existentes. Los conflictos de código,
  grado, orden o versión permanecen en el formulario con un mensaje comprensible.
- V40 agrega una calificación por `Inscripcion`, `MateriaGrado` y `PeriodoAcademico`. La
  pantalla selecciona grupo, periodo y materia mediante autocompletados remotos y carga
  sólo los alumnos asignados al grupo durante las fechas del periodo.
- La captura admite borradores parciales. Publicar exige resultado para todos los alumnos
  mostrados, fija el estado y los datos del actor, y vuelve el bloque inmutable hasta que
  un administrador lo reabra explícitamente. Al reabrir, todo el bloque vuelve a borrador
  y deja de ser visible para la familia hasta una nueva publicación.
- Cada registro conserva una copia de tipo de evaluación, escala, mínima aprobatoria y
  decimales del plan vigente; cambios posteriores en Materias no reescriben el historial.
  El portal familiar y el soporte de portal sólo consultan resultados publicados.
- `CALIFICACION_LEER` y `CALIFICACION_ADMINISTRAR` no se asignan automáticamente a roles
  existentes. El módulo respeta alcance institucional/plantel, filtros, paginación y XLSX;
  errores de regla, integridad o concurrencia permanecen dentro de la captura.

## Verificación confirmada

- Compilación correcta de 573 archivos Java de producción.
- 357 pruebas Maven sin fallos, errores ni omisiones.
- Flyway V1 a V40 validados y aplicados correctamente sobre el volumen existente.
- Hibernate validó el esquema y detectó 51 repositorios.
- PostgreSQL y la aplicación iniciaron correctamente con credenciales tomadas de `.env`.
- `/actuator/health` respondió `UP`.
- Los formularios autenticados de instituciones, planteles, niveles, oferta educativa y
  grados respondieron HTTP 200.
- El listado filtrado, el formulario y la exportación de Alumno–Tutor respondieron
  autenticados; el libro comenzó con firma XLSX válida.
- No se crearon vínculos de prueba. El propietario confirmó los permisos y el
  funcionamiento del módulo con `criz110`.
- PostgreSQL confirmó `pg_trgm` y los tres índices GIN. Los formularios autenticados
  mostraron los tres autocompletados y sus endpoints respondieron JSON HTTP 200.
- El volumen `private_files` quedó escribible sólo desde la aplicación, el panel de
  fotografía respondió autenticado y la salud permaneció `UP`. La verificación no
  cargó fotografías ni modificó alumnos existentes.
- La edición autenticada del alumno renderizó la ficha técnica antes del formulario;
  una prueba del controlador fija la etiqueta de institución mostrada en la cabecera.
- El cierre de sesión respondió HTTP 302 y la misma cookie fue redirigida al login al
  intentar regresar a `/admin`.
- El listado y el formulario de inscripciones respondieron autenticados, y la
  exportación filtrada comenzó con firma XLSX `504b0304`. No se crearon inscripciones
  ni asignaciones durante la verificación.
- Los dos listados y formularios de cobranza respondieron autenticados; la exportación
  filtrada de cuotas comenzó con firma XLSX `504b0304`. Las tablas de conceptos y cuotas
  permanecieron vacías y PostgreSQL confirmó 30 permisos técnicos totales.
- La última instancia local verificada quedó en `http://localhost:8080`.
- PostgreSQL confirmó V24 exitosa, los dos permisos de corte y cero cortes de prueba;
  la verificación automática no generó movimientos ni alteró saldos.
- PostgreSQL confirmó V25 exitosa, sus tres índices y el permiso de reportes. También
  analizó correctamente las consultas nativas de estado de cuenta, tesorería agrupada y
  saldos; la base verificada no contenía filas operativas para esos filtros y no se
  modificaron datos monetarios.
- PostgreSQL confirmó V26 exitosa, las tablas `evento_escolar` y
  `destinatario_evento`, y los dos permisos de eventos. La imagen reconstruida inició
  con salud `UP`; no se crearon eventos ni destinatarios ficticios.
- PostgreSQL confirmó V27, `PORTAL_TUTOR_ACCEDER` y el índice parcial de agenda. Las
  consultas reales de vínculos y segmentación de eventos se ejecutaron en modo lectura
  con identificadores inexistentes; no se crearon ni modificaron datos escolares.
- El nombre visible de sesión se publica al modelo Thymeleaf mediante
  `IdentidadSesionAdvice`; no usar `#authentication`, porque el dialecto de seguridad
  no forma parte de las dependencias actuales. Una prueba de regresión cubre presencia
  y ausencia de autenticación.
- Se corrigió la sintaxis Thymeleaf de los tamaños de página en Tesorería, Estado de
  cuenta, Cortes de caja y Eventos. La expresión compacta `${{10,25,50,100}}` se
  interpretaba como SpEL inválido y cortaba el HTML; una prueba de regresión protege
  las cuatro plantillas.
- PostgreSQL confirmó V28, la tabla `aviso` vacía y los permisos `AVISO_LEER` y
  `AVISO_ADMINISTRAR`. La imagen reconstruida inició con salud `UP`; no se crearon
  avisos ficticios ni se modificaron datos escolares.
- PostgreSQL confirmó V29 exitosa y la tabla `notificacion_usuario` inicialmente vacía.
  La imagen Docker inició por completo y las pruebas nuevas cubren sincronización,
  página normalizada, lectura propia y rechazo de una notificación de otra cuenta.
- PostgreSQL confirmó V30 exitosa, `pago_id`, sus restricciones e índice, y cero
  notificaciones iniciales. Las pruebas nuevas cubren lectura de pago validado y rechazo
  de lectura cuando ya no existe acceso financiero.
- PostgreSQL confirmó V31 exitosa, el permiso `AUDITORIA_CONSULTAR`, cuatro índices y
  `tg_auditoria_inmutable` para impedir cambios o eliminaciones. La tabla permaneció
  vacía: no se inventaron acciones sensibles para verificarla.
- PostgreSQL confirmó V32 exitosa, el permiso `PAGO_CANCELAR`, las columnas de actor y
  fecha y las restricciones de cancelación y movimiento. No se cancelaron pagos reales
  para verificarla; la suite de pruebas cubre los caminos sensibles.
- Para V33, Docker compiló el módulo y pasó 298 pruebas. Flyway aplicó V1–V33 en un
  PostgreSQL 17 temporal y vacío, Hibernate detectó 45 repositorios, `/actuator/health`
  respondió `UP` y las dos plantillas nuevas renderizaron completas. Tras autorización
  explícita del propietario, se desplegó la imagen nueva en la instancia habitual.
  Flyway confirmó el esquema en V33, ambos permisos existen, la app respondió `UP` y
  `retiro_fondo` conservó cero filas; no hubo retiros en datos reales.
- Corrección posterior del formulario V33: el `POST` de retiro no generaba el campo
  CSRF porque usaba `action` HTML en vez de `th:action`; ahora lo incluye. La ayuda
  de autocompletado tiene color neutro y se limpia al seleccionar, junto con el
  error previo de cuenta. La suite pasó 299 pruebas. En PostgreSQL temporal, un
  usuario administrativo de prueba registró un retiro de 25 sobre saldo 100; quedó
  saldo 75 y un solo egreso incluso tras reintentar la misma clave. La imagen
  corregida está desplegada en el entorno habitual y respondió `UP`; no se creó
  ningún retiro de prueba en sus cuentas reales.
- Corrección posterior de Pagos: `/admin/pagos/nuevo` fallaba al evaluar
  `errorOperacion or #fields.hasErrors('*')` cuando `errorOperacion` no existía. El
  formulario ahora comprueba explícitamente `errorOperacion != null` y usa
  `th:action` para incluir CSRF en el `POST`. Docker compiló y pasó 300 pruebas;
  una instancia aislada autenticada respondió HTTP 200 y mostró el token CSRF.
- Corrección posterior de Cargos: emisión manual y generación programada usaban
  `action` HTML y omitían el CSRF del `POST`; ambos usan `th:action`. La ayuda de
  descripción histórica explica que se conserva aunque se renombre el concepto.
  Docker pasó 301 pruebas; en una app y PostgreSQL temporales ambos formularios
  abrieron y los envíos vacíos regresaron HTTP 200 con validaciones, sin crear cargos.
  La imagen corregida se desplegó en la instancia habitual con salud `UP`.
- Verificación posterior de guardado válido: en otra base PostgreSQL temporal se
  prepararon institución, plantel, grado, ciclo, alumno, inscripción activa y concepto
  ficticios. `POST /admin/cargos` respondió 302 al listado; quedó exactamente un cargo
  `EMITIDO` de 250.00 MXN con la descripción capturada, visible en el listado. No hubo
  excepciones ni se creó ningún cargo en los datos reales. No hizo falta otro cambio de
  código ni un nuevo despliegue.
- El listado antes llamado “Ajustes de cargos” se presenta como “Historial de ajustes”
  en el menú y encabezado. Explica que los ajustes manuales se registran desde el
  detalle de un cargo. No cambiar el slug `ajustes-cargo`, rutas, permisos ni el
  comportamiento de registro/reversa al continuar. Docker pasó 302 pruebas, una
  instancia aislada renderizó título y ayudas completos y la imagen se desplegó en
  la instancia habitual con salud `UP`.
- Los autocompletados de cuentas/movimientos y de Pagos ocultan la ayuda al elegir una
  sugerencia; el componente compartido también limpia el error anterior del campo.
  Al borrar o cambiar el alcance se restablece sólo la indicación pertinente y se
  cancelan búsquedas pendientes en los flujos propios de Pagos. Docker pasó 302 pruebas,
  los JavaScript superaron la comprobación de sintaxis y la instancia habitual respondió
  `UP` después de desplegar. No se alteraron datos financieros.
- El formulario de alta manual en Movimientos financieros no incluía el token CSRF al
  usar `action` HTML. Se cambió a `th:action` y se añadió una prueba de regresión.
  Docker pasó 303 pruebas y la imagen quedó desplegada con salud `UP`; no se generaron
  movimientos reales. Queda pendiente un guardado manual con cuenta controlada.
- El formulario de transferencias tenía el mismo defecto: el `POST` no incluía CSRF y
  era rechazado antes de tocar las cuentas. Ahora usa `th:action` y cuenta con prueba de
  regresión. Docker pasó 304 pruebas y la instancia quedó `UP`; no se ejecutaron
  transferencias ni se alteraron saldos durante la verificación.
- La apertura de corte de caja también omitía CSRF por usar `action` HTML; el cierre ya
  era correcto. La apertura usa ahora `th:action` y una prueba protege ambos envíos.
  Docker pasó 305 pruebas y la imagen quedó desplegada con salud `UP`; no se abrió ni
  cerró ningún corte real.
- La estabilización de V34/V35 pasó las 312 pruebas. Las pruebas nuevas cubren cargos
  distinguibles del mismo alumno, reparto exacto de una transferencia entre tres hijos,
  origen y reportante del portal, rechazo por suma incorrecta, rechazo de tutor sin
  vínculo financiero y soporte sin ningún `POST` ni ruta administrativa para reportar
  pagos. Flyway avanzó el volumen
  habitual de V32 a V35 y `/actuator/health` respondió `UP`; no se crearon pagos ni se
  modificaron saldos.
- Docker Compose ya no publica PostgreSQL en el puerto 5432 del host; la aplicación se
  conecta por la red interna a `postgres:5432`. Esto evita conflictos con otro
  PostgreSQL local sin cambiar el volumen persistente ni las credenciales de `.env`.

## Siguiente paso acordado

Prioridad actual: revisar las guías V57 de beca/recargo y abonos desde administración;
la claridad de la guía V56 de dos hijos fue aprobada por el propietario. Revisar búsqueda/Excel,
legibilidad móvil y tema oscuro. Incorporar nuevos escenarios conforme el propietario los
confirme. El módulo no ejecuta capturas y no sustituye las pruebas operativas pendientes.
La siguiente migración libre es V58. Los pendientes anteriores se conservan debajo.

El último módulo implementado es V55 **Soporte del portal de maestros**. El propietario
debe asignar ese módulo al rol administrativo institucional, cerrar sesión y volver a
entrar. Probar dos maestros con cuentas activas, filtros y paginación de planeaciones,
captura protegida, PDF en nueva pestaña, horario, alumnos y ficha médica; comprobar que
cambiar maestro o volver a administración conserva la sesión administrativa y que todas
las consultas de portal aparecen en Auditoría. V56 queda disponible. La prueba integral
de cobranza con dos hijos ya confirmó ingreso total, saldo familiar y comprobante;
también quedó confirmado el reparto individual de $600 y $400. Incorporarlo a la guía.

La reorganización del menú y los nombres funcionales quedó implementada. El propietario
debe revisar visualmente con un rol amplio y otro limitado que la sección activa se abra,
que el orden resulte natural en escritorio y móvil y que Roles muestre **Periodos de
evaluación**, **Familiares del alumno**, **Adeudos de alumnos** y **Pagos recibidos** sin
perder selecciones existentes. Las rutas internas conservan `/admin/catalogos/cargos`,
`/admin/catalogos/pagos` y los permisos `CARGO_*`/`PAGO_*`.

La etapa anterior V54 **Convenios de pago** conserva pendiente la prueba funcional del
propietario: asignar/confirmar el permiso `Convenios de pago`, seleccionar dos o más cargos
de hermanos, verificar saldo anterior, monto acordado y condonación, confirmar que el portal
familiar sólo presenta los cargos nuevos, registrar un pago parcial y comprobar que la
cancelación queda bloqueada. En un segundo convenio sin pagos, cancelar y verificar que los
cargos originales reaparecen. Revisar también filtros, paginación, Excel, tema oscuro y
móvil. No usar datos personales reales sólo para probar.

También quedó terminada la reorganización del expediente de Tutores y su ficha Jasper. La
prueba funcional controlada debe recorrer las seis pestañas, guardar Información, Contacto
y Laboral, provocar validaciones en cada bloque, comprobar Identificación y Portal, y abrir
el PDF con una identificación de imagen y otra de tipo PDF. Cuando la identificación sea
PDF, el reporte muestra sus metadatos y deja vacía la vista previa; no convierte ni expone
el documento dentro del reporte.

El expediente administrativo de Pagos usa navegación por pestañas basada en `student-tabs.js`:
Resumen, Distribución, Gestión, Devoluciones condicional y Comprobantes. Los errores de las
operaciones deben conservar la pestaña activa y el botón de regreso debe mantener su diseño
responsivo y compatible con tema oscuro. No volver a colocar todas las secciones en una sola vista.

El acceso bootstrap/de recuperación no puede autorizar movimientos financieros porque carece de
un `usuario_id` persistido al cual atribuirlos. En el detalle de un pago pendiente se ocultan las
acciones de validación y rechazo para ese acceso y se explica que debe usarse una cuenta creada en
**Seguridad → Usuarios**. No relajar esta protección en futuros cambios.

V50 de Proveedores y Compras quedó implementada, compilada y desplegada; Docker ejecutó
417 pruebas sin fallos, Flyway aplicó V50, Hibernate validó el esquema y salud respondió
`UP`. Antes de probarla, el propietario debe asignar los módulos `Proveedores` y `Compras`
al rol administrativo correspondiente, cerrar sesión e ingresar de nuevo. El paquete de
Compras concede lectura, captura de borradores, confirmación y cancelación; si se requiere
separación de funciones, ajustar directamente los permisos técnicos `COMPRA_*`.

Con datos controlados se debe verificar: crear/editar proveedor; guardar y editar una compra
con varias partidas sin cambio de saldo; confirmar y comprobar el egreso en Movimientos
financieros y en el saldo de la cuenta; cancelar con motivo y comprobar la reversa; filtros,
paginación, Excel, tema oscuro y presentación móvil. No confirmar una compra operativa real
sólo para verificar. La siguiente migración disponible es V51 y el siguiente módulo aún debe
acordarse con el propietario.

La **corrección visual de Calendario escolar** quedó implementada el 03/10/2026 sin
modificar la funcionalidad V48. El listado ahora usa encabezado, resumen, filtros en tarjeta,
acciones, resultados y estados con la jerarquía común; el formulario usa cabecera de sesión,
errores descriptivos, secciones numeradas, guía de alcance y acciones consistentes. Incluye
ayuda contextual propia, tema claro/oscuro y puntos de quiebre para escritorio, tableta y
móvil. Docker compiló 719 fuentes de producción y 129 de prueba y ejecutó 456 pruebas sin
fallos. La aplicación quedó desplegada con salud `UP`; la revisión automática en navegador
llegó al login porque la sesión había caducado, por lo que el propietario debe hacer la última
revisión visual autenticada sin necesidad de crear o modificar datos.

Queda documentado el siguiente pendiente que no debe perderse al cambiar de etapa:

- **Expediente documental y fiscal de Compras:** permitir varios adjuntos opcionales por
  compra: PDF, JPEG, PNG, HEIC/HEIF optimizados y XML CFDI. Los documentos se almacenan
  de forma privada y se visualizan en otra pestaña. El XML original debe conservarse y
  sus datos normalizarse en tablas de encabezado, conceptos, impuestos y relaciones CFDI,
  incluyendo UUID, emisor, receptor, moneda, fechas y totales. Deben detectarse UUID
  duplicados y diferencias contra proveedor/compra. La compra puede confirmarse sin CFDI:
  `SIN_COMPROBANTE` exige justificación, ticket/recibo admite archivo opcional y CFDI usa
  XML con PDF opcional. Las diferencias fiscales muestran advertencia y justificación;
  un XML inválido o UUID duplicado bloquea su vinculación. No sustituir partidas de la
  compra automáticamente: ofrecer importación con vista previa y confirmación humana.

V49 de Actualización familiar de expedientes también conserva pendiente su prueba funcional.
El propietario debe asignar `ACTUALIZACION_EXPEDIENTE_LEER` y/o
`ACTUALIZACION_EXPEDIENTE_REVISAR` a un rol administrativo, cerrar sesión e ingresar de
nuevo. Con datos controlados debe verificar: tutor autorizado, propuesta de PDF e imagen
HEIC/JPEG/PNG, propuesta médica, rechazo con respuesta, aprobación y reflejo en el
expediente oficial, Excel filtrado, tema oscuro y presentación móvil. Un tutor cuyo vínculo
no tenga `puedeAutorizar` no debe poder enviar propuestas.

V43 de portal docente y planeaciones semanales quedó implementada, compilada y
desplegada. El propietario debe hacer la prueba funcional con datos controlados:
asignar los módulos `Maestros` y `Planeaciones` a un rol administrativo, crear un
maestro y su cuenta, asignarle varias materias/grupos, iniciar en `/maestros/acceso`,
guardar un borrador, enviarlo y completar el flujo administrativo de revisión, ajustes,
publicación, reapertura con motivo, edición y nueva publicación. También debe comprobar
PDF, Excel, tema oscuro y presentación móvil.

No crear datos docentes operativos sólo para verificar. Después de aprobar V43, definir
con el propietario el alcance de V44 antes de agregar dominio o migraciones. La primera
mejora candidata es cerrar observaciones surgidas de la prueba del portal docente; no se
ha acordado todavía un módulo nuevo.

V41 y V42 también están implementadas y verificadas automáticamente. Permanecen sus
pruebas funcionales controladas: Asistencia requiere revisar captura masiva, historial
y Excel; Boletas administrativas requiere asignar `BOLETA_CONSULTAR` y comprobar filtros,
PDF individual/colectivo y Excel. No crear datos operativos reales sólo para verificar.

También sigue pendiente la prueba funcional controlada de V34/V35: usar un tutor
autorizado con varios hijos y varios cargos —incluidos dos del mismo alumno—, distribuir
una sola transferencia, adjuntar comprobante y confirmar el historial. Después, desde
administración, probar por separado rechazo con motivo y validación. No usar un pago
operativo real.

Para V35 se debe asignar el módulo `Soporte del portal familiar` al rol adecuado, iniciar
una sesión nueva y comprobar que soporte ve exactamente el alcance del tutor, pero no
muestra el botón para reportar transferencias ni dispone de acciones de escritura.

Para V36 se debe editar un tutor controlado y probar opcionalmente un PDF o imagen sin
datos personales reales: cargar, abrir, reemplazar, revisar el historial y retirar la
identificación vigente. Confirmar que un archivo falso o mayor de 10 MB muestra el error
en el mismo formulario. No cargar una identificación real sólo para verificar.

Después de V36 se unificó la presentación monetaria de administración y portal familiar:
todo importe visible usa `FormatoMoneda` y se muestra como `$150.52` o `$1,000,000.50`,
con dos decimales y separadores. Los campos numéricos editables y porcentajes conservan
su formato de captura. El menú administrativo usa ahora fondo sólido azul noche, contraste
alto, acento turquesa y una tarjeta clara para el módulo activo; no cambió rutas ni permisos.
La edición de Tutores también usa una ficha técnica equivalente a la de Alumnos: muestra
primero la identificación oficial —con vista previa para imágenes o tarjeta para PDF—,
nombre, estado y datos clave; la carga, reemplazo, retiro e historial permanecen en ese
encabezado y el formulario completo continúa debajo.

Las pruebas manuales controladas de V32 y V33 continúan pendientes, pero no bloquean
esta estabilización. La conciliación bancaria permanece descartada: no se importarán
CSV bancarios. Correo y WhatsApp también siguen fuera de alcance hasta diseñar
consentimiento, proveedor, reintentos y trazabilidad.

V37 está implementada, compilada, aplicada y verificada automáticamente. El propietario debe hacer
una prueba funcional controlada: confirmar que el módulo
Usuarios sólo muestra personal administrativo; en Tutores probar los cuatro filtros de
cuenta; elegir un tutor controlado sin acceso, aceptar o editar el username sugerido,
crear la cuenta, copiar el enlace de activación, establecer contraseña e ingresar desde
`/familias`. Después generar un enlace para cambiar contraseña, desactivar el acceso,
confirmar que ya no inicia sesión y reactivarlo. No usar correos ni identificaciones
reales sólo para probar.

Después se implementó la etapa de reportes Jasper descrita abajo. La estrategia definitiva
de almacenamiento privado sigue pendiente para producción, pero no bloquea las pruebas
funcionales actuales.

### Reportes financieros Jasper posteriores a V37

- Se incorporó JasperReports `7.0.8` mediante `jasperreports-pdf`; el controlador ya no
  usa el generador PDFBox manual del estado por cuenta. PDFBox permanece como dependencia
  de prueba para extraer texto y renderizar regresiones.
- Tesorería exporta una **Balanza de movimientos y saldos** en PDF; no se presenta como
  balanza contable porque el dominio todavía no tiene catálogo contable ni pólizas de
  partida doble. El PDF sólo se habilita sin plantel operativo, pues un saldo de cuenta
  no debe dividirse artificialmente por la atribución de un movimiento.
- El estado por cuenta y el estado del alumno exportan PDF Jasper con el mismo periodo,
  filtros, alcance y datos paginados de la pantalla. El estado por cuenta conserva
  traspasos y reversas para cuadrar apertura y cierre.
- Se agregó `Concentrado de cobranza`, consultable y paginado por concepto, plantel o
  ciclo escolar, con importe exigible, cobrado, por cobrar y vencido al corte. Los cargos
  cancelados conservan historia, pero aportan cero a los importes.
- Jasper no ejecuta SQL ni recibe una conexión: usa los servicios existentes y un
  `JRDataSource` paginado de 100 filas. Así mantiene validaciones, alcance de sesión y
  evita cargar millones de registros en memoria. No se agregó migración; Flyway continúa
  en V37 y la siguiente disponible sigue siendo V38.
- La prueba Jasper genera un PDF real en Linux/Alpine, extrae su contenido y renderiza la
  primera página. Se usa la fuente lógica `SansSerif`, ya que Helvetica no estaba
  instalada en la imagen. La inspección visual confirmó encabezado, resúmenes, tabla,
  moneda, pie y paginación sin recortes.
- La compilación final ejecutó 333 pruebas sin fallos. La consulta agrupada se analizó y
  ejecutó en PostgreSQL real en modo lectura (la instalación no tenía cargos que listar),
  y la imagen final quedó desplegada con `/actuator/health` en `UP`.

### Navegación separada del portal familiar posterior a Jasper

- `/portal` quedó como inicio sencillo: saludo, hijo seleccionado, ficha breve y menú de
  opciones. Ya no contiene avisos, agenda, pagos ni notificaciones desplegados uno debajo
  de otro.
- Cada opción abre una pantalla independiente: `/portal/notificaciones`,
  `/portal/avisos`, `/portal/agenda` y `/portal/pagos`. Todas conservan el hijo mediante
  `alumnoId`, tienen botón **Regresar**, paginación propia y navegación móvil entre
  secciones. El formulario `/portal/pagos/reportar` continúa separado.
- Abrir una notificación ahora lleva a la página correspondiente; las financieras usan
  `pagos` en vez del antiguo ancla `cuenta`. Después de reportar una transferencia se
  vuelve a `/portal/pagos`.
- El soporte administrativo conserva la misma separación en
  `/admin/portal-soporte/{tutorId}/avisos`, `/agenda` y `/pagos`, siempre en modo de sólo
  lectura y sin la acción para reportar transferencias. La bandeja personal de
  notificaciones no se simula desde soporte.
- No hubo cambio de esquema: Flyway permanece en V37 y V38 sigue disponible. Docker
  compiló y ejecutó 334 pruebas sin fallos; la imagen actual inició con 46 repositorios,
  validó las 37 migraciones y `/actuator/health` respondió `UP`.
- La comprobación visual automatizada alcanzó correctamente la pantalla de acceso porque
  no había una sesión familiar conservada. No se leyó `.env`. El propietario debe entrar
  con una cuenta familiar controlada y recorrer Inicio → Notificaciones, Avisos, Agenda y
  Pagos, verificando Regresar, cambio de hijo, paginación y navegación móvil.

### Ayuda contextual global posterior a la navegación familiar

- `theme.js` carga el componente global `contextual-help.js` y su hoja de estilos en
  todas las pantallas que ya utilizan los temas. No fue necesario duplicar modal ni
  textos en cada plantilla.
- Cada control visible dentro de una etiqueta —formularios y filtros incluidos— recibe
  un botón de información junto al nombre. Se excluyen campos ocultos técnicos como
  CSRF, versiones e identificadores que el usuario no captura.
- El modal explica para qué sirve el campo y presenta un ejemplo. Existe un glosario
  semántico para códigos, relaciones, fechas, importes, finanzas, seguridad, personas y
  comunicación; las combinaciones especiales como el código de Institución tienen una
  explicación propia. Un texto de respaldo basado en tipo y etiqueta cubre campos nuevos
  hasta que se agregue una definición especializada.
- Un observador incorpora también controles creados dinámicamente, como nuevas filas en
  la distribución de transferencias. Las etiquetas externas vinculadas mediante `for`
  reciben el mismo tratamiento.
- Cada módulo muestra arriba la opción **¿Qué hace este módulo?**, con propósito general
  y un ejemplo práctico. Se cubren los 31 módulos administrativos, sus operaciones
  especiales y el portal familiar. Login, acceso de familias, activación y recuperación
  conservan sólo la ayuda de sus campos para no alterar la composición de esas pantallas.
- El componente es responsivo, compatible con tema claro y oscuro, no envía formularios,
  no modifica valores y puede cerrarse con botón, clic exterior o `Escape`. Mantiene el
  foco dentro del modal y lo devuelve al icono que lo abrió.
- Los filtros usan iconos de 17 px, transparentes y de menor contraste hasta recibir foco
  o puntero. Las etiquetas conservan su ancho completo para que el icono nunca suba el
  control a la misma línea. Los modales administrativos usan azul noche y cian; el portal
  familiar mantiene su variante crema y verde menta.
- No hubo cambio de esquema: Flyway continúa en V37. Docker compiló 531 fuentes y ejecutó
  336 pruebas sin fallos. La revisión en navegador confirmó los iconos, el contenido del
  ejemplo, la semántica de diálogo y el contraste; no se usaron credenciales ni `.env`.
- El acceso familiar reutiliza la geometría redondeada del login administrativo —ancho,
  proporciones, radio y espaciado— sin abandonar su paleta crema, coral y menta. Los
  formularios `.filters`, `.ledger-filters` y `.event-filters` se presentan como tarjetas
  redondeadas responsivas, con iconos de ayuda circulares de 20 px y variante oscura, sin
  cambiar sus filtros ni consultas.
  Docker ejecutó 337 pruebas sin fallos y la revisión pública confirmó una tarjeta de
  1080 px, dos columnas y radio de 28 px; la revisión autenticada de filtros queda manual.

### Punto exacto de reanudación en otra computadora

1. El cambio local posterior a V42 implementa V43 funcional: boletas PDF dentro del
   portal familiar y de la vista de soporte. Preservarlo y no reconstruir V1–V43.
2. Crear el `.env` local desde `.env.example`; nunca pedir, leer ni copiar el contenido
   real del otro equipo. Levantar con `docker compose up --build -d` y comprobar salud.
3. Si se necesita conservar alumnos y fotografías del equipo anterior, Git no basta:
   restaurar base y archivos como una pareja sólo con autorización y con un procedimiento
   probado. No improvisar una restauración sobre datos existentes.
4. Flyway V1–V42 ya existen y están aplicadas al volumen local; nunca editarlas. V43
   funcional no agregó SQL, por lo que la siguiente migración disponible será V43.
5. Probar las cuatro descargas Jasper con datos controlados y distintos tamaños de
   resultado. Ejecutar también la prueba manual de V37 y las pendientes descritas para V34, V35 y V36. Mantener
   además
  en la lista las pruebas pendientes de cancelaciones V32 y retiros V33; no crear
  movimientos operativos reales sólo para verificar.

### Antecedente — manual integral y complemento PDF

- La estrategia del 06/10/2026 reemplaza el PDF estático como fuente principal por una guía
  integrada progresiva. Los requisitos de detalle siguientes se conservan para esa guía.
  No implementar ahora el módulo ni generar un PDF sin una solicitud específica.
- El posible complemento PDF se derivará de la guía verificada; la explicación será
  sencilla, directa e intuitiva, pensada para una persona sin conocimientos técnicos.
- El manual explicará de forma exhaustiva el flujo integral del sistema y cada operación
  disponible, indicando desde qué menú se inicia, requisitos previos, datos que deben
  capturarse, significado de cada campo, botones que deben presionarse, resultado esperado,
  validaciones, estados posteriores, errores comunes y cómo corregirlos.
- Cada procedimiento será realmente paso a paso, numerado y verificable. No se omitirán
  pasos por considerarlos evidentes ni se asumirirá que el lector conoce el sistema.
- Se documentarán por separado los perfiles y permisos: administración, personal con
  permisos limitados, maestros, tutores/familias y cualquier otro portal o rol que exista
  al cierre. El manual explicará qué puede ver y hacer cada perfil.
- Debe cubrir la secuencia recomendada de configuración inicial, operación cotidiana y
  procesos relacionados entre módulos, además de consultas, filtros, exportaciones Excel,
  documentos PDF, archivos, estados, aprobaciones, cancelaciones, reversas y auditoría.
- Siempre que sea posible se incorporarán **capturas reales de las pantallas finales**,
  señalando visualmente menús, pestañas, campos y botones relevantes. Las capturas deberán
  incluir escritorio y, cuando aporte claridad, la experiencia móvil; nunca deberán mostrar
  contraseñas, tokens, datos sensibles ni información personal real.
- Antes de entregarlo se verificará que las rutas, nombres de módulos, permisos, textos,
  imágenes y pasos coincidan con la versión final desplegada. El PDF tendrá índice,
  capítulos por proceso, referencias cruzadas, glosario, solución de problemas frecuentes
  y control de versión/fecha.
- El documento deberá revisarse visualmente página por página para asegurar legibilidad,
  imágenes nítidas, ausencia de recortes y navegación clara. La aceptación exige que una
  persona nueva pueda completar cada tarea siguiendo únicamente el manual.
6. Mantener el patrón completo: permisos sin autoasignación, alcance, filtros y
   paginación en PostgreSQL, exportación XLSX por bloques con los mismos filtros,
   formularios responsivos, temas y validación transaccional.
7. No generar movimientos monetarios reales de prueba sin autorización expresa. Para
   validar la interfaz, el propietario debe usar importes y cuentas controlados.

## Disciplina de cambios y entrega

V34–V43 funcional, los reportes financieros Jasper, la navegación separada del portal
familiar y la ayuda contextual global están implementados localmente. V43 compiló,
ejecutó sus pruebas y arrancó; falta que el propietario pruebe Boletas con una cuenta
familiar y datos controlados. También siguen pendientes las pruebas funcionales
enumeradas de etapas anteriores.

- Inspecciona `git status` antes de editar y preserva cambios ajenos.
- Usa migraciones Flyway nuevas para cambios de esquema; nunca edites una migración ya
  aplicada.
- No uses operaciones destructivas de Git ni elimines el volumen PostgreSQL.
- Actualiza `README.md`, `CONTEXTO_PROYECTO.md` y este archivo cuando cambie el estado o
  el siguiente paso.
- Antes de subir, confirma que `.env` no aparece en `git status`.
- No hagas `push` ni cambies credenciales remotas sin autorización del usuario.
- La respuesta final debe indicar qué se implementó, cómo se verificó y cuál es el
  siguiente paso lógico.

### V42 — boletas académicas derivadas

- Se agregó el módulo `Boletas`, protegido por `BOLETA_CONSULTAR` y sin asignación
  automática a roles. Sólo lista inscripciones que tienen resultados publicados en
  materias configuradas con inclusión en boleta.
- La consulta se filtra por institución, ciclo, plantel, grupo histórico y alumno; pagina
  en PostgreSQL. El autocompletado muestra los primeros diez grupos al recibir foco y
  busca desde tres caracteres, respetando ciclo y alcance.
- El PDF individual y colectivo se genera con JasperReports desde servicios Java, sin SQL
  dentro del reporte. Cada alumno inicia una página y conserva tipo de evaluación, escala
  congelada y observaciones. No se guarda promedio ni copia de resultados.
- Excel usa Apache POI en modo streaming y tanto Excel como el PDF colectivo procesan
  bloques de 100 alumnos con exactamente los mismos filtros de la pantalla. Los PDF se
  abren en otra pestaña para permitir decidir si se descargan.
- Flyway V42 incorpora el permiso y un índice parcial para la consulta de inscripciones;
  no crea una entidad de boleta. Docker compiló 593 fuentes y 93 fuentes de prueba,
  ejecutó 363 pruebas sin fallos, aplicó V42 y respondió `UP`.
- Falta la prueba visual y funcional autenticada del propietario con calificaciones
  controladas, así como asignar `Boletas` al rol correspondiente. La siguiente migración
  libre es V43 y su alcance debe decidirse después de aprobar esta etapa.

### V43 funcional — boletas PDF en el portal familiar

- El tutor puede abrir `/portal/boletas` para consultar, por cada hijo autorizado, las
  inscripciones con calificaciones publicadas e incluidas en boleta. La consulta pagina
  en PostgreSQL y nunca recupera el historial completo.
- Cada tarjeta resume ciclo, plantel, grado, grupo, materias y periodos disponibles. El
  PDF oficial se abre en otra pestaña mediante `Content-Disposition: inline`; el tutor
  decide desde el navegador si desea descargarlo.
- La autorización valida institución, tutor, vínculo vigente, hijo e inscripción. No se
  aceptan identificadores de otro alumno y nunca se muestran borradores.
- La vista de soporte administrativo ofrece exactamente la misma consulta y PDF para
  reproducir la experiencia familiar, conservando su permiso propio y la auditoría.
- El diseño reutiliza la identidad crema, coral y menta, temas claro/oscuro, tarjetas,
  selector de hijos, navegación móvil y estados vacíos del portal familiar.
- No hubo migración: Flyway permanece en V42 y el nombre V43 sigue libre para el próximo
  cambio de esquema. Docker compiló 596 fuentes principales y 94 de prueba; ejecutó
  365 pruebas sin fallos, desplegó la imagen y `/actuator/health` respondió `UP`.

### Pendiente acordado — portal de maestros y planeaciones semanales

- Después de cerrar V43 se desarrollará un acceso propio para maestros, separado del
  portal familiar y de la consola administrativa. Cada maestro tendrá una cuenta y sólo
  podrá trabajar con los grupos que tenga asignados.
- El maestro podrá cargar su planeación por grupo cada semana y el administrador podrá
  consultarla desde el sistema, con historial, seguridad y almacenamiento privado.
- El propietario entregará el formato real de planeación cuando comience esa etapa. No
  definir campos, migración, validaciones ni flujo de revisión antes de analizar dicho
  formato; primero acordar si se llena dentro del sistema o se adjunta como documento.
- Al diseñarla se deben resolver expresamente asignación maestro–grupo, semana/ciclo,
  unicidad, reemplazos o versiones, estados de revisión, comentarios, permisos,
  auditoría, tipos y tamaños de archivo, y experiencia responsiva del portal docente.

### Estabilización posterior a V43 — 2026-09-29

- El portal docente y las planeaciones ya están implementados; el bloque anterior queda
  como antecedente histórico, no como trabajo pendiente.
- Se corrigieron las hojas de Calificaciones y Asistencia: la consulta de alumnos ahora
  usa `exists` en lugar de `distinct` con orden por columnas asociadas, evitando el error
  PostgreSQL `42P10` y manteniendo una sola inscripción por alumno.
- Horas semanales de Materias admite centésimas y valores enteros como `5`; se eliminó la
  combinación inválida `min=0.01` con `step=0.25` que hacía que el navegador sugiriera
  únicamente `4.76` o `5.01`.
- La imagen final usa JDK 21 porque los reportes Jasper construidos dinámicamente necesitan
  `javac`. La prueba de boleta ahora recorre explícitamente la exportación colectiva.
- El formulario administrativo de maestros se reorganizó con `entity-form`, secciones,
  acciones y adaptación móvil comunes. El login docente reutiliza el diseño principal de
  Nexo y fue revisado visualmente en escritorio y 390×844.
- Docker ejecutó 373 pruebas sin fallos, la boleta se abrió, leyó y renderizó, Flyway
  validó 43 migraciones y la aplicación desplegada respondió `UP`. No se leyó `.env` ni
  se crearon datos académicos de prueba.
- Pendiente del propietario: probar con datos controlados el guardado real de una hoja de
  calificaciones, una de asistencia, el PDF colectivo y el recorrido completo de maestro.
  La siguiente migración disponible continúa siendo V44.

### Navegación administrativa agrupada — 2026-09-29

- El menú lateral fusiona por nombre las categorías repetidas después de aplicar los
  permisos. Cada categoría se presenta como un botón desplegable accesible; la del módulo
  actual siempre queda abierta y las demás recuerdan su estado en el navegador.
- El orden visible es Estructura, Personas, Trayectoria, Cobranza, Finanzas, Comunicación
  y Seguridad. Trayectoria aparece una sola vez y reúne, cuando el usuario tiene permiso,
  Inscripciones, Calificaciones, Asistencia, Boletas y Planeaciones.
- La transformación es común para las 14 pantallas administrativas que ya cargan
  `navigation.js`; no duplica reglas en cada HTML ni altera la seguridad del servidor.
  Docker ejecutó 374 pruebas sin fallos y la interacción se revisó visualmente.

### Ajuste visual del formulario administrativo de maestros — 2026-09-29

- Las secciones **Portal de maestros** y **Grupos y materias asignadas** ya no heredan el
  diseño compacto anterior. Ahora usan encabezados, tarjetas de resumen, estados, acciones,
  formulario, listado y estado vacío coherentes con el resto de formularios administrativos.
- El ajuste incluye temas claro y oscuro y adaptación específica para móvil. Se revisó en
  navegador a tamaño escritorio y 390×844, Docker ejecutó 374 pruebas sin fallos y la
  aplicación desplegada respondió `UP`.

### Estabilización de capturas, boletas y ayudas — 2026-09-29

- Calificaciones y Asistencia ya consultan directamente el identificador de institución
  del grupo; no navegan relaciones JPA perezosas fuera de sesión al abrir una hoja.
- El PDF colectivo de boletas fallaba aun con registros porque `javac` no veía las
  dependencias anidadas del JAR de Spring Boot. Se incorporó `jasperreports-jdt` 7.0.8 y
  ECJ para compilar expresiones mediante el classloader de la aplicación.
- El acceso familiar del tutor tiene un panel administrativo profesional y responsivo. Los
  filtros de Planeaciones usan la geometría común y los iconos de ayuda tienen una regla
  global protegida contra estilos particulares de Inscripciones, Maestros y otros módulos.
- La revisión visual cubrió escritorio y 390×844. Docker ejecutó 377 pruebas sin fallos,
  validó V1–V43 y la aplicación desplegada respondió `UP` sin errores de arranque.
- Los autocompletados tienen un contrato visual global que prevalece sobre estilos de
  formularios especializados. Sus resultados, opciones y botón para limpiar no pueden
  convertirse en botones blancos, heredar el rojo de validación ni perder el tema oscuro;
  tampoco producen desplazamiento horizontal en móvil. Aplica también a **Nueva
  asignación** dentro del expediente administrativo de Maestros.
- **Abrir captura** de Calificaciones y **Abrir lista** de Asistencia comparten un espacio
  académico profesional: encabezado, selector, resumen contextual, tabla, campos y acciones
  consistentes. La misma regla cubre temas claro/oscuro y convierte cada alumno en una
  tarjeta legible en móvil, sin depender del desplazamiento horizontal.
- Publicar resultados y reabrir un bloque ya no usan alertas nativas del navegador. Ambas
  acciones muestran un modal propio, accesible y responsivo, con explicación específica,
  tema oscuro, cierre por Escape, control de foco y confirmación antes del mismo POST.

### Expedientes de maestros y tutores por pestañas — 2026-09-30

- La edición administrativa de Maestros reutiliza el navegador de pestañas de Alumnos:
  separa **Ficha del maestro**, **Portal de maestros** y **Grupos y materias**.
- La edición de Tutores separa **Ficha del tutor**, **Identificación oficial** y **Portal
  de familias**, evitando una pantalla continua extensa.
- El componente común admite hashes configurables, teclado, tema oscuro y desplazamiento
  horizontal controlado en móvil. Tras errores o acciones de cuenta, identificación y
  asignaciones se conserva la pestaña correspondiente.
- Docker ejecutó 388 pruebas sin fallos, desplegó la imagen y `/actuator/health` respondió
  `UP`. La revisión autenticada queda para el propietario porque no se leyó `.env`.

### Login docente e historial tabular de planeaciones — 2026-09-30

- `/maestros/acceso` usa una identidad exclusiva violeta, coral y dorada con marca **Nexo
  Docente**, distinta del azul administrativo. Conserva temas, accesibilidad, ayudas y
  respuesta móvil; se revisó visualmente en escritorio y 390×844.
- `/maestros` reemplazó las tarjetas semanales por una tabla ordenada con semana, grupo,
  plantel, materias, estado y acción. En móvil cada fila se convierte en tarjeta etiquetada.
- Los filtros por fecha inicial, fecha final y estado se aplican mediante `Specification`
  sobre PostgreSQL. La consulta pagina 20 registros y conserva los filtros al navegar.
- **Editar** aparece sólo para Borrador, Requiere ajustes o Reabierta; los demás estados
  conducen a **Ver detalle**. Un rango invertido muestra una validación clara y no consulta
  información fuera del maestro autenticado.
- Docker ejecutó 390 pruebas sin fallos y desplegó la imagen. No hubo migración ni lectura
  de `.env`.

### Filtros independientes de planeaciones administrativas — 2026-09-30

- `/admin/planeaciones` separa la búsqueda en **Maestro**, **Grupo** y **Propósito**. Ya no
  existe un texto ambiguo que busque simultáneamente en los tres campos.
- Maestro y Grupo usan el autocompletado transversal: al recibir foco muestran los primeros
  10 registros permitidos y, desde tres caracteres, consultan coincidencias en PostgreSQL.
  La selección conserva el identificador exacto, aunque la etiqueta visible incluya número
  de empleado, grado, plantel o ciclo escolar.
- Propósito permanece como texto libre y sólo consulta el propósito de la planeación. Los
  filtros se combinan entre sí y con estado, fechas, institución y plantel; se conservan en
  paginación y la exportación Excel Apache POI usa exactamente los mismos criterios.
- Las sugerencias de Maestro y Grupo respetan el alcance de datos del usuario. El diseño
  tiene variantes clara/oscura y disposición responsiva de seis, dos o una columna.
- Docker ejecutó 392 pruebas sin fallos, desplegó la imagen y `/actuator/health` respondió
  `UP`. No hubo migración ni lectura de `.env`.

### Orden técnico automático en catálogos — 2026-09-30

- Los formularios de Niveles educativos, Grados, Periodos de evaluación y Materias por grado
  ya no muestran ni reciben el campo **Orden**. También dejó de presentarse en los listados
  de Niveles/Grados y en las ayudas de materias.
- V46 alineó el `orden` existente con el `id` en `nivel_educativo`, `grado`,
  `periodo_academico` y `materia_grado`. Cuatro triggers asignan automáticamente el ID como
  orden en cada alta nueva.
- La columna se conserva y la aplicación no la sobrescribe al editar. Un ajuste excepcional
  hecho directamente en BD permanece vigente y continúa determinando la ordenación.
- La verificación de datos devolvió cero diferencias en las cuatro tablas y confirmó los
  cuatro triggers. Docker ejecutó 393 pruebas sin fallos y desplegó la imagen. La siguiente
  migración disponible es V47.

### Comprobante oficial de pago y nombre claro de cobranza — 2026-09-30

- Al validar un pago, Administración y el Portal de familias pueden abrir un comprobante
  oficial Jasper PDF en una pestaña nueva. La respuesta usa disposición `inline`; la
  descarga queda como una decisión posterior del usuario desde el visor del navegador.
- El comprobante incluye institución, identidad fiscal, plantel, folio, importe, tutor,
  pagador, método, referencia, cuenta receptora, fecha y usuario de validación, movimiento,
  distribución por alumno y resumen aplicado/devuelto/disponible.
- Se intenta usar el logotipo configurado de la institución y, si no existe o no puede
  leerse, se usa `reports/assets/logo-institucion-ejemplo.png` como respaldo provisional.
- El tutor sólo puede consultar comprobantes validados que pertenezcan a su propia cuenta.
  El modo Portal tú aplica la misma regla simulando al tutor seleccionado y registra la
  consulta en auditoría. Administración conserva el control por alcance y permisos de pago.
- El módulo administrativo antes presentado como **Cargos** ahora se llama **Pagos de
  alumnos** en navegación, permisos, ayuda y formularios. Las rutas, permisos técnicos,
  tablas y entidades conservan sus nombres internos para evitar cambios incompatibles.
- La muestra Jasper fue leída y renderizada en A4: una página, sin recortes ni traslapes.
  Docker ejecutó 399 pruebas sin fallos. No hubo migración ni despliegue de contenedores;
  la siguiente migración disponible continúa siendo V47.

### Expediente de inscripción por pestañas — 2026-09-30

- Al editar una inscripción, la pantalla usa el mismo navegador de pestañas accesible de
  Alumnos, Maestros y Tutores. **Ficha de inscripción** reúne la fotografía, identidad del
  alumno y datos académicos; **Grupo y trayectoria** reúne historial, nueva asignación y
  traslado o promoción.
- La pestaña puede seleccionarse con teclado, conserva su hash en la URL y se adapta a tema
  claro, oscuro y móvil. Los errores de asignación mantienen abierta Grupo y trayectoria;
  al asignar o finalizar un grupo se vuelve directamente a esa pestaña.
- La creación inicial continúa como formulario directo, porque aún no existe historial que
  separar. Docker ejecutó 400 pruebas sin fallos. No hubo migración ni despliegue; V47
  continúa disponible.

### Cobranza asistida desde la inscripción — 2026-10-01

- La edición de Inscripciones agrega la pestaña **Cobranza**, sin fusionar ni duplicar las
  responsabilidades de Cuotas, Adeudos de alumnos y Pagos recibidos. Presenta un resumen del avance,
  cuotas configuradas y cargos emitidos para ese alumno.
- **Preparar cobro** abre una cuota individual de frecuencia única con inscripción,
  institución, plantel, moneda y vigencia precargados. El usuario sólo elige concepto,
  importe y vencimiento; opcionalmente crea el cargo exigible en la misma operación.
- La emisión inmediata usa la misma clave idempotente del generador automático. Repetir la
  acción devuelve el cargo existente y nunca cobra dos veces la misma cuota única.
- Desde la pestaña o desde el detalle de un cargo pendiente/parcial se abre **Registrar
  pago**. El formulario precarga cargo, saldo pendiente, institución, plantel, moneda y el
  tutor responsable financiero vigente; si hay varios o ninguno, lo informa sin ocultar
  la posibilidad de corregir la selección.
- El autocompletado ordinario de cargos también completa el importe solicitado y el total
  del pago con el saldo vigente seleccionado. Las opciones pagadas se excluyen.
- El flujo respeta por separado `CUOTA_ALUMNO_ADMINISTRAR`, `CARGO_ADMINISTRAR` y
  `PAGO_REGISTRAR`, además del alcance institucional. No hubo migración: V51 sigue libre.
  Docker compiló 697 fuentes principales y 121 de prueba y ejecutó 422 pruebas sin fallos.

#### Pendiente acordado — pago anticipado desde la inscripción

- Agregar una acción **Preparar pago anticipado** para el caso presencial en que el padre
  cubra inscripción y varias mensualidades en una sola entrega.
- El administrador elegirá los cargos y periodos a adelantar; el sistema mostrará una vista
  previa con concepto, periodo, vencimiento e importe, diferenciando cargos existentes de
  los que se generarían. Nada se escribirá antes de confirmar.
- La generación será individual para esa inscripción e idempotente; no debe disparar cargos
  masivos para otros alumnos. El pago resultante será uno solo, pero mantendrá una aplicación
  independiente por inscripción, mensualidad y demás cargos seleccionados.
- Efectivo deberá ingresar a una caja y transferencia a una cuenta compatible. La validación
  seguirá siendo el momento que aplica los cargos y publica un único movimiento financiero
  por el total. Se conservarán los permisos y alcances actuales.
- Este apartado documenta trabajo futuro. Al 2026-10-02 no existe implementación, migración
  ni cambio funcional asociado.

### Vista previa obligatoria de recargos — 2026-10-01

- **Generar recargos** ya no aplica cambios desde la primera pantalla. El administrador
  selecciona institución, plantel opcional y fecha de corte y pulsa **Visualizar recargos**.
- La misma pantalla presenta un resumen y una tabla paginada con alumno, matrícula,
  plantel, concepto, vencimiento, días de atraso, política, periodos nuevos, saldo actual,
  recargo y nuevo saldo. Sólo entonces aparece **Confirmar y generar recargos**.
- La vista previa es de sólo lectura. La confirmación vuelve a calcular los datos vigentes
  para evitar aplicar una simulación desactualizada y conserva las claves idempotentes para
  impedir duplicados.
- El cálculo de vista previa y el definitivo comparten la misma planificación. Los cargos
  completamente pagados, cancelados, sin saldo, sin política automática o sin un periodo
  nuevo no aparecen ni reciben recargo.
- La pantalla reutiliza el diseño administrativo, tiene temas claro/oscuro y convierte la
  tabla en tarjetas legibles en móvil. No requirió migración; V51 continúa disponible.
  Docker compiló 699 fuentes principales y 122 de prueba, ejecutó 425 pruebas sin fallos y
  el despliegue respondió `UP`. La revisión autenticada queda para el propietario porque la
  sesión del navegador había caducado y no se consultó `.env`.

### Vista previa de pagos automáticos y confirmaciones — 2026-10-01

- **Adeudos de alumnos → Generar automáticos** también usa un flujo obligatorio de dos
  pasos. El primer formulario sólo ofrece **Visualizar cuotas por aplicar**; todavía no
  modifica la cobranza.
- La vista previa muestra, en una lista paginada, cada pago faltante por alumno, matrícula,
  plantel, concepto, frecuencia, periodo, vencimiento e importe. El resumen indica cuotas
  con pendientes, cantidad de adeudos por generar e importe total programado.
- Sólo después de revisar aparece **Confirmar y generar pagos faltantes**. Al confirmar se
  recalculan cuotas y periodos para evitar datos obsoletos; las claves `AUTO` continúan
  impidiendo duplicados ante ejecuciones repetidas o simultáneas.
- La previsualización y la generación comparten la misma planificación, por lo que una
  mensualidad ya existente no aparece. Ambos generadores usan ahora botones de confirmación
  con diseño explícito, accesible, responsivo y compatible con tema oscuro.
- Después de pulsar **Visualizar cuotas por aplicar** o **Visualizar recargos**, la pantalla
  lleva automáticamente al usuario hasta el resultado y enfoca su título. Mientras se
  prepara, el botón queda deshabilitado y muestra **Preparando vista…** para evitar dobles
  envíos y dejar claro que la solicitud está en curso.
- Se corrigieron los formularios finales de ambos generadores para usar `th:action`. Es
  obligatorio conservarlo en operaciones `POST`: así Thymeleaf incorpora el token CSRF y
  la confirmación no termina rechazada como un método HTTP no soportado.
- No hubo migración; V51 permanece disponible. Docker compiló 701 fuentes principales y
  122 de prueba, ejecutó 426 pruebas sin fallos y el despliegue respondió `UP`.

### Reporte familiar con importes protegidos — 2026-10-02

- **Portal familiar → Tus pagos → Reportar transferencia** dejó de usar el `datalist`
  nativo para los cargos. La ayuda propia abre siempre los primeros diez cargos vigentes
  al recibir foco y consulta desde tres caracteres, incluso después de una validación.
- Cada cargo seleccionado carga su saldo pendiente completo. Tanto **Saldo a pagar** como
  **Total a transferir** son de sólo lectura; al agregar varios hijos o cargos, el total se
  suma automáticamente.
- La protección no depende del navegador: antes de guardar, el servidor comprueba que los
  cargos continúen vigentes, pertenezcan a la cuenta familiar, no estén repetidos y vuelve
  a calcular cada saldo. Un importe alterado en la petición es reemplazado por el vigente.
- No requiere migración. Se agregó una prueba de interfaz y las pruebas del servicio cubren
  la normalización autoritativa de importes.

### Historial familiar de pagos por alumno — 2026-10-02

- **Portal familiar → Tus pagos** presenta los pagos en una tabla paginada con fecha,
  folio y referencia, método, importe aplicado al alumno, estado y comprobante oficial.
- Los filtros de mes y año se ejecutan en PostgreSQL y se conservan al cambiar de página.
  El selector de año sólo ofrece años que realmente tienen pagos para el hijo consultado.
- La consulta exige simultáneamente el tutor autenticado, su institución y una aplicación
  de pago ligada a la inscripción del hijo seleccionado. Un tutor con varios hijos no ve
  mezclados sus historiales; en pagos compartidos se muestra sólo el importe distribuido al
  alumno actual.
- Los comprobantes validados continúan abriéndose como PDF en otra pestaña. Los estados en
  revisión, rechazados o cancelados explican claramente por qué no hay comprobante oficial.
- La misma experiencia quedó disponible en **Portal tú** para soporte administrativo. La
  tabla se convierte en tarjetas en móvil y respeta temas claro y oscuro. No hubo migración;
  Docker compiló 701 fuentes principales y 123 de prueba y aprobó 441 pruebas. La imagen se
  construyó, pero no se desplegó.

### Captura simplificada del periodo de un cargo — 2026-10-02

- **Adeudos de alumnos → Nuevo registro** ya no obliga al usuario a interpretar siempre las
  fechas técnicas de inicio y fin. Ahora permite elegir **Mes completo**, **Fecha
  específica** o **Rango personalizado**.
- Mes completo solicita únicamente mes y año y calcula el primer y último día. Fecha
  específica usa el mismo día como inicio y fin. El rango conserva la flexibilidad anterior
  dentro de una opción explícitamente avanzada.
- **Fecha de vencimiento** se renombró visualmente a **Fecha límite para pagar** y explica
  que después el cargo aparecerá vencido y podrá recibir recargos configurados.
- Un resumen vivo presenta qué periodo cubre el cargo y hasta cuándo debe pagarse. La
  derivación de fechas también se realiza en Java antes de crear el cargo, por lo que no
  depende de que el navegador envíe campos técnicos calculados.
- El diseño es responsivo y compatible con tema oscuro. Se añadieron pruebas para los tres
  modos y el contrato visual. Docker compiló 702 fuentes principales y 124 de prueba,
  aprobó 444 pruebas, desplegó la imagen y el servicio respondió `UP`. No hubo migración.
  La revisión autenticada del navegador quedó limitada porque la sesión administrativa
  disponible había caducado; no se consultaron credenciales ni `.env`.

### Fecha de registro protegida en cargos — 2026-10-02

- **Adeudos de alumnos → Nuevo registro** muestra la fecha institucional actual como
  **Fecha de registro del cargo**. El campo permanece bloqueado y el servidor vuelve a
  imponer esa fecha aunque una petición intente alterarla.
- La opción **Registrar con una fecha diferente** habilita una excepción explícita. En
  ese caso son obligatorios tanto la nueva fecha como el motivo; no se permiten fechas
  futuras. La fecha actual se calcula con la zona horaria configurada en la institución.
- V53 agrega `motivo_fecha_registro_diferente` a `cargo`. El motivo queda trazable y se
  muestra en el detalle administrativo cuando el cargo fue registrado con fecha anterior.
- La tarjeta de captura, el interruptor, las ayudas y el resumen vivo conservan el diseño
  responsivo y los temas claro/oscuro del sistema. Docker compiló 702 fuentes principales
  y 124 de prueba, aprobó 445 pruebas, aplicó V53 y el servicio desplegado respondió `UP`.
  La siguiente migración disponible es V54.

### Ayuda contextual fuera de la navegación con Tab — 2026-10-02

- Los iconos informativos de formularios, filtros, accesos y portales, así como el botón
  **¿Qué hace este módulo?**, son ayudas opcionales activadas únicamente con clic y usan
  `tabindex="-1"`.
- Al recorrer una captura con `Tab`, el foco avanza directamente entre `input`, `select`,
  `textarea` y los demás controles operativos; ya no se detiene en los botones de ayuda.
- Si se abre con clic la ayuda asociada a un campo, al cerrar el modal el foco regresa a
  ese campo para conservar la continuidad de la captura. Los controles internos del modal
  continúan administrando su propio foco mientras está abierto.
- La regla se implementó una sola vez en `contextual-help.js`, por lo que también cubre
  campos agregados dinámicamente. No agregó migración. `node --check` aprobó la sintaxis y
  `docker compose build app` compiló 702 fuentes principales y 124 de prueba y ejecutó 445
  pruebas sin fallos. La imagen se desplegó; en esta base Flyway aplicó las migraciones
  pendientes V41–V53 y Spring Boot inició correctamente. La siguiente migración disponible
  continúa siendo V54.

### Claridad visual de recargos y fechas de becas — 2026-10-02

- La explicación de **Días de gracia** en políticas de recargo ahora usa la clase semántica
  `field-help` y color neutro. El rojo queda reservado para errores de validación.
- La validación del periodo de una beca ya no expone fechas ISO. El rango de inscripción se
  presenta como `dd/MM/yyyy`, por ejemplo `29/09/2026 a 15/06/2027`.
- Regla permanente: toda fecha visible para el usuario —incluidos mensajes de negocio— debe
  usar `dd/MM/yyyy`. ISO `yyyy-MM-dd` queda limitado a controles HTML, persistencia e
  integraciones técnicas.
- No hubo migración. Docker compiló 702 fuentes principales y 125 de prueba y aprobó 447
  pruebas sin fallos. La imagen se desplegó y `/actuator/health` respondió `UP`. La
  siguiente migración disponible continúa siendo V54.

### Antecedente del rediseño de topes — implementado el 06/10/2026

El cambio descrito al inicio de este documento resuelve este pendiente de captura.
Conservar el tope también en Una sola vez: puede reducir una aplicación al máximo permitido.
La aceptación funcional del nuevo ejemplo de tope sigue pendiente del propietario.

- El propietario confirmó durante la prueba funcional que **Tipo de límite** y **Valor del
  límite** no resultan comprensibles para un usuario administrativo sin explicación previa.
- Antes de dar por cerrada la experiencia de cobranza se deberá rediseñar esa parte del
  formulario con nombres orientados a la operación, ayudas visibles en lenguaje sencillo y
  ejemplos calculados con el importe original del cargo. La pantalla debe explicar claramente
  la diferencia entre **sin tope**, **tope máximo en dinero** y **tope como porcentaje del
  importe original**, así como que el límite controla el recargo acumulado y no el importe de
  cada mensualidad.
- También se deberá evaluar ocultar o simplificar el límite cuando la periodicidad sea
  **Una sola vez**, evitando pedir decisiones que no aporten valor al caso más sencillo. Este
  punto queda documentado únicamente como pendiente; todavía no se modificó código.

### Pago parcial con total recibido sincronizado — 2026-10-05

- La devolución administrativa distingue **Total** (importe calculado y protegido a partir
  de abonos seleccionados más dinero disponible sin aplicar) y **Parcial** (importe capturado
  dentro de ese máximo). La vista previa informa dinero a devolver, importe seleccionado
  que seguirá aplicado, deuda recuperada y saldo por cargo tras devolver. Usa primero el
  dinero sin aplicar y respeta el orden de aplicaciones del servicio; las validaciones
  transaccionales del servidor se mantienen.

- **Cuentas financieras** incluye saldo actual en tabla, Excel y edición/detalle como dato
  de consulta. Usa el saldo posterior del último movimiento por secuencia, o el saldo inicial
  si todavía no hay movimientos; no vuelve a sumar el saldo inicial. El saldo consultado
  no forma parte de los campos editables.

- En **Pagos recibidos → Nuevo registro**, al habilitar un pago parcial y modificar el
  importe que se aplicará a un cargo, **Total recibido** se sincroniza inmediatamente con
  la suma distribuida. Con varios cargos utiliza la suma de todos; el administrador aún
  puede modificar después el total si realmente recibió dinero que desea conservar sin
  asignar.
- Las etiquetas distinguen ahora **Total recibido**, **Distribuido entre cargos** y
  **Dinero pendiente de asignar**. Este último representa dinero recibido sin destino, no
  el saldo que todavía debe el alumno.
- Durante la captura parcial se muestra el saldo estimado que conservará el cargo después
  de validar. El importe tampoco puede superar desde la interfaz el saldo completo usado
  como referencia; las reglas de servidor permanecen como protección definitiva.
- El detalle de un pago pendiente muestra por cada distribución el saldo actual del cargo
  y el saldo estimado después de validar; por ejemplo, `$1,000` actual, `$300` distribuido
  y `$700` que seguirá debiendo el alumno.
- El listado de **Adeudos de alumnos** identifica como **Parcial** un cargo con abonos y
  saldo pendiente. Su detalle presenta **Pagado acumulado** junto al saldo exigible para
  que el usuario pueda conciliar ambos importes sin acudir al expediente del pago.
- **Pagos recibidos** distingue en su tabla total recibido, total de cargos vinculados y
  saldo actual por pagar. Para varios cargos suma cada cargo una sola vez. El detalle de
  un pago validado presenta por abono el total del cargo, el importe de ese pago y el saldo
  actual restante; este último cambia con los pagos posteriores y no es una fotografía
  histórica del saldo al momento de validar.
- No requirió migración. `node --check` aprobó `pago-form.js` y Docker compiló 719 fuentes
  principales y 130 de prueba y ejecutó 462 pruebas sin fallos.

### Convenios de pago auditables — V54 — 2026-10-02

- El módulo **Cobranza → Convenios de pago** formaliza acuerdos familiares sin borrar ni
  cancelar manualmente cada mensualidad. Usa un solo permiso asignable:
  `CONVENIO_PAGO_ADMINISTRAR`.
- La captura selecciona tutor y concepto con autocompletado y busca cargos pendientes en
  bloques de máximo 20; nunca carga miles de alumnos o cargos en un `select`.
- Un acuerdo puede incluir cargos de varios hijos del mismo tutor responsable financiero.
  Se conserva el importe y pago previo de cada original, su estado cambia a `CONVENIDO` y
  su saldo exigible se vuelve cero en catálogos y reportes.
- El monto acordado se distribuye proporcionalmente por inscripción, con corrección del
  último centavo. Cada hijo recibe un cargo nuevo independiente con clave idempotente
  `CONVENIO:{convenioId}:{inscripcionId}`; caja, tarjeta y transferencia siguen usando el
  flujo normal de Pagos.
- La transacción rechaza cargos no emitidos, sin saldo, de otra institución/moneda, sin
  responsabilidad financiera vigente o con una transferencia pendiente de validación.
- Cancelar conserva el convenio y los cargos nuevos. Sólo procede antes de cualquier pago:
  marca los nuevos como cancelados y reactiva los originales. Si existe una aplicación,
  obliga a tratarla primero mediante los flujos financieros existentes.
- El listado es paginado y filtrable en PostgreSQL y su Excel recorre exactamente los mismos
  criterios. La situación se deriva como `VIGENTE`, `VENCIDO`, `CUMPLIDO` o `CANCELADO`.
- Docker compiló 719 fuentes principales y 129 de prueba y aprobó 454 pruebas. Flyway aplicó
  V54 sobre PostgreSQL 17 y Spring Boot inició en el puerto 8080. La siguiente migración
  disponible es V55.

### Navegación administrativa por proceso — 2026-10-03

- El menú lateral dejó de agruparse por categorías técnicas aisladas. Ahora presenta dos
  áreas principales: **Operación escolar** y **Administración**, cada una con subsecciones
  plegables ordenadas según el flujo habitual de trabajo.
- Operación escolar reúne Control escolar, Gestión académica, Cobranza escolar y
  Comunicación. Administración reúne Finanzas, Compras y proveedores, Configuración
  escolar y Seguridad y soporte. La subsección del módulo activo siempre permanece abierta.
- Los nombres visibles se homologaron en navegación, formularios, mensajes, ayuda contextual
  y Roles: **Periodos de evaluación**, **Familiares del alumno**, **Adeudos de alumnos**,
  **Pagos recibidos** y **Soporte del portal familiar**.
- La generación automática habla de adeudos, no de pagos, porque todavía no existe recepción
  de dinero. Dentro del expediente financiero se conserva la palabra cargo cuando identifica
  técnicamente la obligación a la que se aplica un pago.
- No se renombraron código, entidades, tablas, rutas ni autoridades. La compatibilidad de
  `PeriodoAcademico`, `Cargo`, `/admin/cargos`, `CARGO_*` y `PAGO_*` permanece intacta y los
  roles existentes conservan sus asignaciones.
- `node --check` aprobó ambos JavaScript modificados y Docker compiló 719 fuentes principales
  y 129 de prueba; las 456 pruebas pasaron sin fallos. No se creó migración y V55 sigue libre.

### Reporte familiar de transferencias y ayudas semánticas — 2026-10-03

- El portal familiar ya no pregunta el plantel al reportar una transferencia. La cuenta
  destino se consulta a partir del primer cargo autorizado y el servidor deriva de nuevo el
  plantel desde los cargos; nunca confía en un plantel enviado por el navegador.
- Una transferencia puede distribuirse entre varios cargos e hijos del mismo plantel. Si los
  cargos pertenecen a planteles distintos, se conserva el formulario y se solicita reportar
  una transferencia separada por plantel, porque `pago.plantel_registro_id` representa un solo
  origen contable.
- Los saldos individuales y el total se muestran como pesos, bloqueados para edición. Sus
  valores autoritativos se recalculan en servidor antes de registrar el pago. El cargo muestra
  también plantel y vencimiento en `dd/MM/yyyy`.
- El autocompletado abre las primeras diez opciones tanto al recibir foco como al hacer clic,
  incluso después de una validación. La cuenta seleccionada se restaura si continúa siendo
  válida para el cargo recuperado.
- Los iconos de ayuda de campo dejaron de ser botones HTML dentro de `label`: ahora son
  activadores no etiquetables, excluidos de Tab y únicamente responden al clic directo. Esto
  evita que pulsar otra parte del campo abra accidentalmente el modal.
- Todas las plantillas marcan explícitamente los mensajes `th:errors` con `field-error`. Las
  leyendas normales de formularios y autocompletados usan color neutro; el rojo queda
  reservado para validaciones reales.
- No requiere migración y V55 sigue libre. `node --check` y `git diff --check` finalizaron sin
  errores. Maven local compiló 719 fuentes principales y 129 de prueba; la suite completa pasó
  con 459 pruebas, cero fallos y cero errores. Docker Desktop devolvió un error interno de
  escritura en `metadata_v2.db` y `meta.db`, por lo que no se desplegó una imagen nueva.

### Ayuda contextual desplegada y verificada — 2026-10-04

- Se recuperó el acceso a Docker. El contenedor anterior aún servía el JavaScript que creaba
  botones dentro de las etiquetas; se reprodujo que un clic fuera del icono abría el modal.
- Se reconstruyó la imagen y se recreó únicamente el servicio `app` con
  `docker compose up -d --no-deps app`; PostgreSQL y los volúmenes permanecieron intactos.
- La compilación aprobó las 459 pruebas (cero fallos y errores) y `/actuator/health` respondió
  `UP`. El JavaScript servido ahora crea `span.context-help-trigger`.
- En el navegador se comprobó en ambos accesos, administrativo y familiar, que pulsar la
  etiqueta fuera del icono no abre la ayuda, pulsar el icono sí la abre y Tab pasa del usuario
  a la contraseña. El comportamiento se implementa en el componente global de ayuda.
- No hubo cambios de código ni migraciones en esta verificación; V55 continúa libre.

### Homologación visual administrativa y revisión de planeaciones — 2026-10-05

- Los rótulos **Operación escolar** y **Administración** del menú lateral ahora son
  separadores visuales no interactivos. Usan icono, línea y colores distintos —turquesa y
  ámbar— para evitar que se confundan con las subsecciones desplegables.
- Actualizaciones familiares, Horarios y clases, Convenios de pago, Proveedores y Compras
  reutilizan directamente la base visual existente de `admin.css` y `forms.css` para filtros,
  acciones y formularios; no existe una hoja paralela que cambie sus colores. El botón
  Limpiar queda como acción secundaria reconocible y el tema oscuro usa los selectores
  globales ya establecidos.
- Los formularios de Proveedores y Compras agrupan sus datos principales en las mismas
  secciones `form-section` del sistema. Horarios usa ahora `form-top`, `form-intro` y
  `entity-form`, sin alterar campos, validaciones ni comportamiento de negocio.
- Planeaciones presenta **Revisar** y **Regresar al listado** como botones consistentes con
  las acciones administrativas, en tema claro, oscuro y móvil.
- Se corrigió la apertura de **Revisar planeación**. La plantilla consultaba la propiedad
  histórica `estadoNuevo`, mientras el DTO sólo exponía el componente `nuevo`; se añadió un
  accesor de compatibilidad probado para que Thymeleaf resuelva el estado sin error.
- No se agregó migración y V55 sigue disponible. Docker compiló 719 fuentes principales y
  130 de prueba; las 461 pruebas pasaron sin fallos. Se recreó únicamente `app`, Spring Boot
  inició correctamente, validó las 54 migraciones existentes y `/login` y la nueva hoja de
  estilos respondieron HTTP 200.
