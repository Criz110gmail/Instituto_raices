# Contexto del proyecto

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

## V67 — navegación y claridad del ajuste de adeudos (2026-10-07)

- Detalle separado en Resumen/Transferencias/Ajustar saldo/Historial/Gestión con diseño
  compartido de alumnos/pagos y saldo/alumno siempre fuera de paneles. Resumen conserva
  Registrar pago; Gestión contiene autorizaciones, reemplazo y cancelación; historial intacto.
- Vista previa exacta por centavos del ajuste, sin mover fondos. Caso $400 con $300 pagados
  y descuento $100 muestra nuevo total $300 y saldo $0. Disminuciones por debajo de pagado
  o de cero se advierten/bloquean; reglas del servicio, autorización y concurrencia intactas.
- Errores de ajuste conservan captura y abren su pestaña; cancelación errónea abre Gestión;
  éxito abre Historial con aviso. Override inicial del hash opcional en student-tabs no
  cambia navegación de otras pantallas sin atributo. Modal también muestra resultados.
- V67 editorial actualiza recorridos de guías; sin nuevos casos confirmados ni finanzas.
  Verificado: 579 pruebas Java sin fallos, con render real de cinco pestañas/preview/cancelado
  y errores/flash; once suites Node, cálculo exacto y DOM/pestañas/teclado correctos.
  SQL editorial validado con rollback, V67 aplicada; cinco pasos actualizados, seis guías /
  48 etapas. Sólo app recreada, salud UP; sin operaciones financieras ni reinicio/borrado
  de PostgreSQL/volúmenes. Revisión visual autenticada pendiente. Próxima V68.
- Pendiente humano: caso separado devolver100 + descuento100 sobre cargo400 pagado400,
  comprobar ajustado300/pagado300/saldo0 y cuenta no cambia al descontar, antes de publicarlo.

## Ajuste de foco en campos monetarios (2026-10-07)

- Label nativo activaba campo al clicar título/espacio superior. Se evita activación por
  puntero fuera del input sólo para importes; mantiene Tab, nombre accesible, activación
  asistida y botones de ayuda. Si estaba activo, clic en título lo desenfoca/formatea.
- Clase money-entry separada del contenedor money-input ya existente; readonly conserva
  presentación monetaria. No cambia valor, parsing, negocio, modal ni datos de servidor.
- Verificado: 576 pruebas Java y ocho suites Node correctos, incluyendo label asociado y
  envolvente, título vs input, desenfoque, activación asistida y ayuda. Sólo app recreada,
  salud UP y JS nuevo servido; sin migración, fondos o volúmenes modificados. Prueba
  visual real y devolución más descuento siguen pendientes; próxima migración V67.

## V66 — captura monetaria sin scroll y revisión antes de publicar (2026-10-07)

- Text/teclado decimal en importes de 14 formularios; foco decimal y blur formato moneda.
  Componente separa visible sin name de hidden original canónico, conserva scripts/IDs,
  BigDecimal, bloqueos, límites, validación y filas dinámicas. No se envían $ ni agrupaciones.
  Texto inválido/ambiguo o precisión extra se rechaza, no se redondea ni convierte a cero.
  Cantidades/horas/calificaciones/porcentajes no se cambian; tope híbrido conserva 4 decimales
  y sólo se presenta moneda en MONTO_FIJO. Portal familiar sigue calculado y protegido.
- Confirmación nativa con diseño existente para validar, devolver y ajustar; resumen exacto
  de operación. Cancelar/Escape/fondo no envían; Confirmar operación mantiene validación,
  handlers y CSRF, sin aprobación permanente. Métodos del input original delegan al visible.
- V66 actualiza sólo contenido de guías para mencionar modal/Confirmar operación. Seis
  guías confirmadas, 48 etapas; segunda vertiente no confirmada todavía. Siguiente V67.
- Verificado: 575 pruebas Java y ocho suites Node sin fallos; simulación DOM de formato,
  canonical/binding decimal, límites, readonly, filas nuevas, tope, cancelación y tres tipos
  de confirmación. No se hizo revisión visual autenticada en navegador real.
- V66 aplicada, salud UP, sólo app recreada; lectura confirma seis guías/48 etapas y nueve
  pasos con confirmación. JS nuevo servido. No se ejecutaron movimientos financieros.
- Preparación SQL editorial: ejecución directa temporal de V66 se restauró exactamente
  (texto añadido y versiones originales 4/6/3/2/1/1; fecha no varió), antes de prueba con
  rollback y aplicación por Flyway. Resultado sin duplicar versiones ni confirmaciones;
  no hubo modificaciones de fondos. En siguientes validaciones usar BEGIN/ROLLBACK explícito.
  Prueba humana pendiente: monto cuota400 y scroll/formato/envío, pagar400, devolver100,
  descuento100 en cargo separado → original400/ajustado300/pagado300/saldo0, sin ingreso
  ni egreso adicional al aplicar descuento. No repetir operaciones del caso ya confirmado.

## V65 — devolución $100 y liquidación posterior confirmada (2026-10-07)

- Confirmación humana: historial devuelto $100, cuenta −$100, cargo $400 con saldo $100;
  nuevo pago familiar $100 validado por propietario, cuenta +$100, adeudo final $0 y portal
  sin pendiente. Se cierra prueba de devolución/nuevo pago; no fue ejecutada por herramientas.
- Guía nueva Devolver $100 y pagar nuevamente el saldo, ocho etapas con origen de menú,
  fila, campos, botones, resultados/precauciones. Saldo base S ya incluye pago original;
  S−100+100=S. Preserva ambos pagos y devolución, no aplica descuento. Seis guías / 48 etapas.
- V65 sólo contenido editorial; sin fondos/roles/operaciones. Verificado: 571 pruebas Java,
  Node devolución y diff correctos; SQL validado con rollback, V65 aplicada. Consulta de
  sólo lectura: seis guías / 48 etapas / ocho nuevas. Sólo app recreada, salud UP; sin
  reinicio de PostgreSQL/volúmenes. Sin operación financiera ni revisión visual autenticada.
- Otra vertiente pendiente: caso separado $400 con concepto que permita descuento, pagar,
  devolver $100 y ajustar DESCUENTO/DISMINUCION $100. Total ajustado $300, aplicado $300,
  saldo $0 y cuenta no cambia al descontar. Esperar confirmación para añadirlo a guía.
  Próxima migración V66. No reutilizar cargo liquidado ni cobrar y descontar el mismo saldo.

## Claridad de vista previa en devoluciones (2026-10-07)

- Tarjeta Así quedaría la devolución cambia a azul/lavanda con etiqueta de sólo revisión,
  importes destacados y deuda recuperada en menta. Variante oscura y distribución móvil
  existentes; aviso aclara que no se devolvió dinero antes de confirmar. Sin cambios de lógica.
- Verificado: 568 pruebas Java sin fallos, Node de devolución correcto y diff limpio.
  Sólo app recreada, salud UP; sin migración ni cambios de fondos/volúmenes. Sin ejecutar
  devoluciones; revisión visual autenticada y prueba humana pendientes del propietario.

## Botón profesional y motivo condicional en devolución (2026-10-07)

- Cambio de cuenta pasa de checkbox a botón con diseño existente y estados accesibles.
  Devolver desde otra cuenta abre selección/motivo; Conservar cuenta original los restaura
  y oculta/limpia motivo. Booleano oculto conserva contrato y restricciones del servidor.
- Causa del motivo visible estando inactivo: CSS display:block del formulario superaba
  hidden. Se agrega regla específica para ocultarlo, cubierta en regresión.
- Verificado: 568 pruebas Java sin fallos; Node botón/motivo/estado/restauración correctos.
  Sólo app recreada, salud UP; sin migraciones ni cambios de saldos/volúmenes. Revisión
  visual autenticada pendiente del propietario; no se ejecutó devolución.

## Devoluciones: cuenta protegida y cambio explícito (2026-10-07)

- Propietario pidió proteger cuenta de origen durante prueba parcial $400 → devolver $100.
  Cuenta original readonly; opción Devolver desde otra cuenta permite seleccionar otra,
  motivo obligatorio si difiere. Desactivar restaura cuenta y limpia motivo/búsquedas.
- Servicio exige cambio explícito y motivo contra cuenta persistida del pago; conserva
  restricciones financieras. Historial muestra motivo del cambio dentro del motivo de
  devolución; auditoría conserva IDs original/nuevo y motivo separados. Sin migración.
- Vista previa nombra cuenta e importe de egreso. Misma apariencia del formulario, temas
  y controles. Autocompletado ahora respeta readonly y descarta respuestas tardías.
- Verificado: 568 pruebas Java sin fallos y Node cuenta/cambio/motivo/restauración/vista
  $100/$300/$100; flujo familiar previo correcto y diff limpio. Sólo app recreada, salud UP;
  sin migración ni reinicio de PostgreSQL/volúmenes. Revisión visual autenticada pendiente.
  No se ejecutó devolución ni se registraron fondos.
  Esperar confirmación humana de cuenta −$100 / aplicado $300 / adeudo $100 antes de añadir
  ese escenario confirmado a guía. Próxima migración V65; cinco guías / 40 etapas.

## V64 — guía del rechazo y reenvío confirmado (2026-10-07)

- Dueño confirmó validación del reenvío de $400, incremento de $400 en cuenta y comprobante familiar.
  Se cierra el pendiente humano de este proceso; no fue una operación hecha por el agente.
- Nueva guía confirmada Rechazar un pago y corregir el reporte: $400, diez etapas con menús
  exactos, búsquedas, selección, botones, campos, resultados y precauciones. Incluye motivo
  visible en modal, deuda de $400 mientras está rechazado/en revisión y un solo ingreso de $400 al
  validar. Conserva primer folio rechazado y explica que reenvío no es nueva transferencia.
- V64 sólo añade guía/pasos/requisitos de permisos, sin conceder roles ni registrar pagos.
  Cinco guías, 40 etapas; consultas/filtros/paginación/Excel/diseño existentes se conservan.
- Verificado: 562 pruebas Java sin fallos; Node modal/transferencia correctos; SQL validado
  con rollback. V64 aplicada por Flyway; lectura confirmó cinco guías / 40 etapas / diez
  nuevas. Sólo app recreada, salud UP; sin cambios de pagos por herramientas ni reinicio
  de PostgreSQL/volúmenes. No se realizó validación visual autenticada en navegador.
- Siguiente V65. Continuar con caso exacto de devolución
  parcial, bajo operación manual del propietario, antes de documentarlo como confirmado.

## Motivo del rechazo consultable en pagos familiares (2026-10-07)

- Causa: consulta/DTO del historial devolvían estado sin motivo. Se incluye motivo existente
  sólo de pagos RECHAZADOS, limitado al usuario, institución e hijo autorizado y la página
  actual. No se cambian estados, pagos ni saldos ni se consulta todo el historial.
- Botón Ver motivo del rechazo junto al estado abre modal nativo con folio/motivo y ayuda
  para corregir reporte. Conserva saltos, escapa HTML/textContent, temas y móvil, Escape,
  botón/fondo, foco y restauración; también disponible en vista de soporte autorizada.
- Sin migración (V64 libre), sin rechazar/validar pagos desde herramientas. Prueba en curso
  del propietario: rechazo y reenvío400 todavía no confirmado de extremo a extremo.
  No confundir nuevo reporte con nueva transferencia real.
- Verificado: 559 pruebas Java aprobadas, render de motivo escapado en tutor/soporte y sin
  botón en validados. Node cubre contenido literal, cierre, foco, fondo y motivo ausente;
  flujo de transferencia anterior correcto. Sólo app actualizada, salud UP; sin migración.
  Pendiente comprobar el motivo real al recargar el portal y terminar el caso de reenvío400.

## V63 — detalle de navegación y captura de todas las guías (2026-10-07)

- El propietario pidió evitar indicaciones como Abre Ver del nuevo cargo sin mencionar el
  módulo. Se reescriben las 30 etapas de las cuatro guías actuales con rutas de menú exactas,
  buscador/filtros/fila, acciones, controles, guardado, mensajes y resultados. Transiciones
  entre módulos y /familias explícitas; autorización por cargo y por plantel separadas.
- Revisión preserva ejemplos/resultados ya confirmados, permisos y precauciones. Corrige
  exclusión histórica de vencidos, describe selección de filas y diferencia entre cuota,
  adeudo, pago pendiente y validación. Versiona contenido y fecha con V63, sin tocar migraciones
  previas ni datos financieros. Misma pantalla, filtros, paginación y Excel existentes.
- Regla para nuevas guías: cada etapa debe especificar desde dónde empezar y cada cambio
  de módulo, registro a buscar, botón/pestaña, campos, guardar y resultado; no asumir que el
  lector sabe dónde está. Comparar nombres con formularios reales.
- Verificación: 558 pruebas Java sin fallos; regresión cubre todas las etapas y marcadores
  numerados (los importes no deben partir una acción). Actualización SQL validada con
  ROLLBACK antes de publicar. V63 exitosa, sólo app desplegada y salud UP. Cuatro guías,
  30 pasos con inicio explícito y versiones incrementadas, revisadas07/10/2026. No se
  registraron pagos ni cambiaron saldos/permisos; siguiente V64. Revisar claridad con el dueño.

## Confirmación funcional y mensajes del portal (2026-10-07)

- Propietario confirmó validación del cargo19 e ingreso850 en caja, y nueva transferencia
  del cargo vencido500 desde portal familiar, validada con aumento bancario500. No fueron
  operaciones del agente. No se declaran probadas revocaciones, duplicados ni concurrencia.
- Se da diseño al resultado del envío: tarjeta menta Comprobante recibido, estado En revisión,
  mensaje escapado y explicación de revisión antes de descontar deuda. Accesible role=status,
  responsivo, sin temporizador. Paso5 se distingue con lavanda, etiqueta Sólo revisión y
  explicación de que el resumen se llena automáticamente; conserva los controles anteriores.
- V62 agrega contenido editorial confirmado500 a Guía de procesos, siete pasos con rutas,
  ejemplo, precauciones y resultados. No crea pagos ni cambia permisos. Siguiente V63.
- Verificación: 555 pruebas Java sin fallos y prueba Node del flujo correctas; render real
  del aviso comprueba estado, mensaje escapado y ausencia si no hay confirmación. Paso5
  verificado sin controles de captura. Sólo app desplegada, salud UP; V62 aplicada y guía
  confirmada con siete pasos. Revisión visual autenticada pendiente del propietario.
  Primer intento de V62 se revirtió por enlace /familias no admitido en la guía administrativa;
  se dejó enlace NULL y texto de acceso, sin ampliar la restricción. Migración final confirmada.

## V61 — reporte de transferencias vencidas (2026-10-06)

- Implementado control general por plantel (inicialmente desactivado) y excepción por cargo
  con motivo obligatorio, versión, alcance y actor persistido. Autorizar/revocar conserva
  cambios en Auditoría; no borra recargos ni modifica vencimiento. Plantel habilitado OR
  excepción permite el cargo vencido; revocar excepción no impide una regla general activa.
- PostgreSQL aplica misma elegibilidad para búsqueda/cuentas/POST, filtra saldo positivo
  antes de límite, excluye cancelados/convenidos/liquidados y pagos en revisión, usa fecha
  de zona institucional. Reportar bloquea cargos por ID hasta guardar y evita duplicados.
- Selección muestra original, becas/descuentos, aumentos aplicados, abonos y total protegido.
  Cambio de saldo durante captura exige revisar monto actualizado y adjuntar nuevamente.
  No genera recargos implícitos. Monto de reporte ya guardado permanece intacto; aviso en
  administración señala diferencia con saldo actual, sin inventar un nuevo ingreso.
- V61 ajusta guías existentes; nuevo escenario aún no se declara confirmado. Propietario
  confirmó recargo con tope50 y registro efectivo850; SQL posterior conserva cargo19 con
  original1000, ajustes netos−150, abonos0 y un pago en revisión. Falta validar/rechazar;
  no considerarlo liquidado ni enviar otro reporte para el mismo cargo mientras siga pendiente.
- Verificación: 552 pruebas Java sin fallos y prueba Node del formulario correctas; SQL en
  CTE sólo lectura cubre 11 escenarios de elegibilidad, consulta real válida. V61 aplicada,
  imagen app desplegada y salud UP. Cero planteles/excepciones habilitados. Sin pagos ni
  movimientos de prueba o permisos de rol por herramientas. Siguiente migración V62;
  revisión visual autenticada y prueba operativa nueva pendientes del propietario.

## V60 — selección por fila antes de generar (2026-10-06)

- El propietario pidió omitir pruebas no deseadas al confirmar ambas generaciones.
  Nuevas casillas por cargo (recargo) o cuota-periodo (adeudo), todas marcadas inicialmente;
  selección de página y botones globales Seleccionar/Desmarcar todos. Importe/cantidad de
  selección separados de totales globales; estado conservado entre páginas por UUID.
- Snapshot privado de claves/versión/importe/vencimiento, en DB por lotes100, asociado al
  administrador y alcance/corte/tipo. No guarda nombres ni expedientes. Caduca2h, confirma
  una vez, bloquea selección vacía/ajena y registros nuevos fuera de la vista revisada.
  Cambios detectados de versión, importe, vencimiento o cantidad exigen volver a visualizar;
  se revierte toda la operación. Confirma importe realmente emitido tras la beca.
- Estado cliente sessionStorage y máximo5000 cambios; sólo incluidos o todos menos excluidos.
  Sin script o persistencia cliente se bloquea la confirmación, evitando generar todo por error.
  Desmarcar no cancela: vuelve a aparecer en una nueva vista; no genera cargos al visualizar.
  La consulta sí registra metadatos privados de preparación, no movimientos financieros.
- V60 actualiza procedimientos editoriales; siguiente V61. Nuevo caso pendiente del dueño:
  desmarcar todos los recargos y marcar sólo19, esperado50 y saldo850. No ejecutado con datos
  reales por herramientas. Pendiente acordar depuración de snapshots caducados antes de producción.
- Verificación final: 536 pruebas Java sin fallos; Node comprueba preservación de selección
  entre páginas y resumen global, modos Todos/Sólo incluidos y bloqueo si no hay selección
  o no funciona el almacenamiento. V60 aplicada con éxito, `app` desplegado y salud `UP`.
  Cargo19 continúa emitido, vencimiento05/10, original1000, beca200 y recargo0. No se ejecutó
  ninguna selección real desde herramientas. Revisión visual y caso funcional del dueño pendientes.

## Ocultar diagnóstico de cuotas hasta solicitarlo (2026-10-06)

- Cuotas no incluidas y motivo queda cerrado por defecto en un details/summary con estilos
  comunes y leyenda explícita de que no genera cargos. Abrirlo sólo muestra el diagnóstico;
  al paginar dentro se mantiene abierto. Excel y filtros de alcance/corte no cambian.
- No se modificó cargo18, cuota13 ni reglas de generación; sin migración (siguiente V60).
  El propietario confirmó la confusión visual, no un fallo de cálculo de generación.
- Verificación: 523 pruebas Java sin fallos; imagen desplegada sólo en `app`, salud `UP`.
  Prueba de render confirma diagnóstico cerrado inicialmente y apertura explícita al paginar.

## V59 — protección de cuotas y reemplazo de vencimiento (2026-10-06)

- Se diagnosticó cargo #18 cancelado y cuota #13 activa corregida: AUTO:1:13:UNICA sigue
  reservada, por eso cancelar no lo ofrece de nuevo. La indicación anterior de cancelar y
  regenerar fue incorrecta. Se conserva la idempotencia; no borrar el histórico ni su clave.
- Cuotas únicas generadas protegen importes/moneda/frecuencia/fechas en UI y servidor.
  Mensuales conservan edición para futuras emisiones, con aviso y frecuencia protegida.
  Generación incluye motivos de exclusión paginados y Excel POI por bloques y mismo alcance.
- Corrección explícita desde detalle con motivo y confirmación crea reemplazo vinculado único,
  conserva registro previo, cancela emitido atómicamente y recalcula beca vigente. Sólo cambia
  fecha límite dentro del periodo. Actor administrativo identificado, versión y alcance;
  bloquea historial de pagos, pagos en revisión, convenios, ajustes no BECA y reemplazo previo.
  No modifica cuentas ni cuotas. Registro de pagos bloquea cargo para evitar carreras.
- V59 incorpora FK/motivo y precauciones editoriales en guías existentes; siguiente V60.
  Caso de sustitución aún pendiente de confirmación humana. No se ejecutaron reemplazos,
  recargos ni pagos de prueba. Pasos: cargo18 → Corregir y reemplazar → 05/10/2026 y motivo →
  comprobar nuevo cargo1000−200=800 → corte06/10 con recargo10% y tope50 → saldo850.
- Verificación final: 522 pruebas Java aprobadas, incluidas aislamiento, versión, pago en
  revisión, historial de pagos, recargos previos, duplicados, beca y render del formulario.
  Node confirma protección de vigencia y ejemplo de topes. Imagen desplegada sólo en `app`,
  V59 exitosa y salud `UP`. SQL confirma cargo18 sin reemplazo y sin cambio de importe/beca.
  El propietario debe confirmar diseño en sesión y resultado funcional antes de añadir ese caso
  como confirmado en Guía de procesos.

## Tabla ilustrativa de recargos y distinción visual (2026-10-06)

- Ante reporte de tabla vacía se refuerza inicialización del script, lectura por id/nombre,
  eventos input/change y acción Actualizar ejemplo sin submit. Recursos versionados evitan
  reutilizar el script previo. Sin acceso al navegador autenticado no se confirma una causa
  específica de caché. Renderizado real verifica los campos usados por el cálculo.
- Sin valores válidos aparece una fila explicativa (porcentaje/tope faltante o valores
  inválidos), no un tbody vacío. Panel violeta diferenciado, aviso de simulación y temas
  claro/oscuro; reutiliza tabla responsiva común. Node cubre arranque tardío, campos por
  nombre, recálculo manual y resultado 80/70/0. No cambia datos ni reglas financieras.
- Sin migración, V59 disponible. Revisión visual en sesión del propietario pendiente.
- Verificación final: 506 pruebas Java aprobadas y Node correcto. Se reconstruyó y desplegó
  únicamente `app`; salud `UP`. No se generaron ajustes ni movimientos de prueba.

## Rediseño explicativo de topes de recargo (2026-10-06)

- Se resuelve el pendiente visual Límite y operación: opciones claras de tope y campo
  necesario, simulación independiente no persistida y activación separada. Mantiene el
  diseño común claro/oscuro y las reglas transaccionales existentes; no cambia valores
  iniciales ni políticas registradas. Tope porcentual sobre original; recargo sobre base
  sin recargos. El acumulado sólo corresponde a esa política/cargo, no a todos los alumnos.
- Ejemplo dinámico 1000−200, 10% mensual, tope150: aplicaciones 80/70/0, deuda950 sin
  pagos. Único con tope50: aplicación50, deuda850. No se consulta ni escribe ningún cargo.
- Node verifica dinero/porcentaje del tope, mensual/único, monto fijo, base cero e inválidos.
  La prueba Java verifica generación 80/70 sobre base800 sin exceder150.
- V58 actualiza la guía confirmada de becas al nuevo vocabulario, versión2. La prueba
  funcional de topes queda pendiente del propietario; no publicar un caso nuevo confirmado
  hasta su validación. Con fechas válidas pasadas puede probar un tope único de50 ahora;
  para dos recargos mensuales esperar el segundo periodo o usar fechas de prueba pasadas
  dentro de una inscripción/ciclo que realmente los cubra. No forzar reloj ni cortes futuros.
- Próxima migración V59. No se generaron recargos, pagos ni movimientos durante el cambio.
- Verificación final: 505 pruebas Java aprobadas, simulación Node correcta, imagen de `app`
  reconstruida y desplegada, V58 aplicada y salud `UP`. Pendiente revisión visual autenticada
  y confirmación del ejemplo por el propietario; no se ejecutaron transacciones de prueba.

## V57 — ampliación editorial de cobranza (2026-10-06)

- Nuevas guías confirmadas: beca del 20% y recargo único del 10% ($1,000 − $200 + $80 =
  $880, liquidación), y abonos $300/$500/$200 sobre $1,000 (saldos $700/$200/$0).
- Ocho y siete etapas respectivamente, con instrucciones numeradas, ejemplos, resultados,
  restricciones de fechas y estados. Recargos se ejecuta explícitamente en Políticas de
  recargo; no hay incremento diario implícito. El pago vencido de $880 se registra desde
  administración, no desde el selector de cargos vigentes del portal.
- Parciales parte de Pagos recibidos → Nuevo registro para habilitar Registrar pago parcial.
  Primeros dos abonos ilustrados en efectivo a caja y últimos $200 transferidos al banco:
  caja aumenta $800 y banco $200 después de validar. No se modifica un pago anterior.
- V57 añade únicamente guías/pasos/permisos requeridos de contenido, no datos operativos
  ni asignaciones a roles. Reutiliza sin cambios diseño, filtros, páginas y Excel V56.
  La primera guía de dos hijos conserva sus ocho etapas. Próxima migración V58.
- Propietario confirmó claridad de la primera guía; nuevas guías requieren revisión de
  instrucciones. Otros porcentajes, topes, devoluciones y reversas siguen siendo casos distintos.
- Verificación: Docker aprobó 502 pruebas, incluidas comprobaciones del contenido, rutas,
  importes, saldos y ausencia de inserts operativos en V57. La imagen se desplegó sólo en
  `app`; salud `UP` y PostgreSQL confirmó V57 exitosa, tres guías y 23 pasos (8/8/7).
  Con todos los permisos salvo BECA_ALUMNO_LEER se ofrecen sólo las otras dos guías.
  No se crearon cuotas, pagos ni movimientos de prueba; no se alteraron permisos de roles.

## V56 — guía integrada inicial (2026-10-06)

- Módulo administrativo Guía de procesos con permiso propio sin autoasignación. Contenido
  editorial compartido sin alumnos reales, cuentas o transacciones: guía, permisos y pasos.
  Consulta por búsqueda de situaciones/textos, categoría y estado, paginada en PostgreSQL.
  Excel Apache POI recorre bloques y aplica los mismos filtros y acceso que la pantalla.
- Una guía disponible: caso confirmado de una transferencia para dos hijos, $600 y $400.
  Conserva ocho etapas desde concepto/cuotas hasta validación, saldo B+$1,000 y comprobante.
  Instrucciones numeradas, perfil responsable, requisitos, captura de ejemplo, resultados
  esperados y errores/precauciones. Versionada y fechada; ejemplos nunca se ejecutan solos.
- Listar, abrir por slug y exportar sólo admiten usuario persistido con permiso de guía y
  permisos requeridos del proceso. No hay POST ni gestión de pagos desde este módulo.
  Navegación común y diseño claro/oscuro, índice, secciones desplegables y enlaces a módulos.
- Asignar Guía de procesos desde Roles y permisos y volver a entrar. El primer contenido
  requiere módulos de Alumnos, Tutores, Familiares del alumno, Inscripciones, Conceptos,
  Cuotas, Adeudos, Pagos (incluida validación) y Cuentas financieras. Permisos insuficientes
  producen listado sin la guía y bloquean detalle directo; exportación respeta lo mismo.
- Pendientes de ampliación: capturas verificadas sin datos sensibles, guías adaptadas a los
  portales y más escenarios confirmados. No se implementó un manual PDF paralelo ni progreso
  de operaciones. Nuevos contenidos/revisiones requieren migración nueva y actualización
  de fecha/versión y procedimientos al cambiar funciones. Próxima migración V57.
- Verificación: Docker aprobó 500 pruebas sin fallos, incluidas consultas con permisos,
  página/filtros, acceso directo, normalización, instrucciones decimales, renderizado real
  de listado/detalle y lectura del Excel con filtros. Node aprobó los scripts.
- La primera comprobación de arranque detectó una FK por código de permiso incompatible
  con la restricción existente. V56 no se aplicó y PostgreSQL revirtió la transacción;
  se corrigió antes de aplicar usando `permiso_id`. No se editó una migración aplicada.
- Despliegue final: sólo `app`, salud `UP`, V56 exitosa, una guía, ocho pasos y diez
  permisos requeridos. La consulta real dio cero guías teniendo sólo el permiso de guía
  y una con todos los permisos. No se otorgaron permisos ni se tocaron pagos o alumnos.
- La revisión visual autenticada de claro/oscuro y móvil queda para el propietario;
  no había sesión administrativa de verificación y no se consultaron credenciales.

## Resultado funcional comunicado por el propietario (2026-10-06)

- Tras validar una transferencia de $1,000, confirmó un aumento de $1,000 en la cuenta,
  que ese importe dejó de aparecer como pendiente en el portal familiar y que el
  comprobante oficial ya está disponible. Se registra como confirmado por el propietario.
- Procedimiento para la futura guía: familia selecciona cargos y reporta transferencia
  con cuenta, fecha/referencia y comprobante; queda en revisión; administración entra a
  Pagos recibidos → Ver → Gestión y valida/publica; consulta cuenta financiera y portal
  familiar para verificar saldo pendiente y comprobante. La validación es la que produce
  el ingreso y las aplicaciones, no el envío del comprobante.
- El ejemplo acordado reparte $1,000 entre cargos $600 y $400 de dos hijos. El resultado
  total y la comprobación final de Distribución y consulta individual de ambos hijos
  quedaron confirmados por el propietario. Caso aprobado: ingreso $1,000, aplicaciones
  $600/$400, cargos liquidados, historiales por alumno y comprobante disponible.
  Incorporarlo como primer caso confirmado de la guía integrada; no extender esta
  aprobación automáticamente a otros escenarios.
- Sólo se actualizó documentación. El agente no registró ni validó pagos, y esta
  confirmación no sustituye las pruebas de otros escenarios ni del diseño móvil/oscuro.

## Captura familiar de transferencia en orden de operación (2026-10-06)

- Una sola pantalla ordena cargos, cuenta, fecha/referencia y comprobantes con cinco
  secciones numeradas. Total protegido antes de los cargos, fijo durante desplazamiento
  móvil, y revisión al final para no regresar arriba. No se modificaron reglas financieras.
- La cuenta se habilita al obtener opciones para el primer cargo; explica bloqueo, carga,
  falta de cuentas o error. Resumen dinámico: cargos/importes, total, cuenta, fecha/hora,
  referencia y archivos. Los nombres se incorporan como texto, no HTML.
- El adjunto muestra archivos y tamaños; volver a elegir reemplaza la selección y actualiza
  la revisión. Se conservan cinco PDF/JPEG/PNG de 10 MB, multipart y protección CSRF.
- Procedimiento ilustrativo: seleccionar cargos $600/$400 → comprobar total $1,000 →
  cuenta del plantel → fecha y referencia reales del comprobante → adjuntar → revisar
  resumen → enviar a revisión. El ingreso total y resultado familiar ya fueron confirmados;
  la comprobación individual del reparto también quedó confirmada posteriormente.
- Node verifica suma, cuenta bloqueada/habilitada, resumen, fecha y reemplazo de archivos
  con DOM y respuestas simulados. No se enviaron transferencias ni se alteraron saldos.
- Docker construyó correctamente y aprobó 494 pruebas sin fallos. Se actualizó únicamente
  `app`. La inspección visual autenticada en celular y tema oscuro queda para el propietario;
  se reutilizó la base visual familiar y no se consultaron credenciales para acceder.

## Tus pagos familiar organizado por propósito (2026-10-06)

- Tres bloques: Resumen de tu cuenta, Lo que falta por pagar y Tus pagos y comprobantes.
  El listado filtra saldo positivo en SQL antes de paginar; conserva deuda vencida y parcial,
  no presenta registros liquidados/cancelados/convenidos con cero. El resumen completo se
  mantiene independiente y los pagos históricos no se borran ni se ocultan por este filtro.
- Tabla pendiente: total con becas/ajustes, abonos efectivos y falta por pagar, periodo,
  fecha límite y estado. La porción vencida se explica como incluida en el saldo pendiente.
  Historial distingue importes destinados al hijo de aplicaciones efectivas y aclara que
  las transferencias en revisión sólo descuentan al validarlas. Mantiene mes/año y PDF inline.
- Procedimiento ilustrativo actualizado: seleccionar hijo → Tus pagos → saldo/cargos →
  reportar transferencia → consultar historial → esperar validación. Cargo $1,000 menos
  $300 efectivo deja $700; un reporte de $200 pendiente no cambia todavía ese saldo.
- Soporte reutiliza la misma presentación. No se ejecutaron operaciones financieras;
  el resultado total y reparto individual de dos hijos están confirmados por el propietario.
- Verificación técnica: construcción Docker exitosa y 493 pruebas sin fallos. Incluye
  renderizado de abonos y transferencia en revisión, cuenta sin deuda con historial,
  página fuera de rango y soporte; verifica consulta SQL con filtro antes de paginación,
  alcance por alumno/institución y resumen independiente. La revisión visual autenticada
  en escritorio/móvil y temas queda para el propietario: no había sesión familiar disponible
  en el navegador de verificación y no se consultaron credenciales.
- Se recreó únicamente `app` y `/actuator/health` respondió `UP`. No requiere reconstrucción
  adicional para probar; recargar el portal (volver a iniciar sesión si el reinicio la cerró).

## Vista previa de generación automática con becas (2026-10-06)

- La tabla ahora presenta importe original, beca por aplicar en dinero y total por cobrar;
  identifica la beca y su modalidad, o indica que no existe beca aplicable.
- Resumen global de originales, becas y netos de todos los periodos pendientes,
  conservando paginación y lectura por bloques. El cálculo comparte la misma selección
  y redondeo que la generación, sin persistir ajustes en la consulta.
- La vista aclara que no incluye ajustes manuales ni recargos posteriores y que confirmar
  recalcula las becas vigentes. Antes de confirmar, volver a visualizar si cambian las becas.
- Caso de dos hijos: confirmó dos filas en la vista previa y posteriormente validó el pago
  de $1,000 con efecto en cuenta y portal; falta confirmar explícitamente el reparto por hijo.
- Verificación técnica: `docker compose build app` exitoso, 488 pruebas sin fallos;
  aplicación actualizada con `docker compose up -d --no-deps app`, arranque correcto,
  esquema V55 sin nuevas migraciones. No se ejecutaron operaciones de cobranza.

## Estrategia de guía integrada progresiva (2026-10-06)

- El propietario prioriza una guía viva por procesos y casos prácticos sobre un PDF estático.
  Cada prueba funcional que confirme deberá documentarse con requisitos, permisos, ejemplo
  completo, pasos de menú y botones, datos de captura, resultados esperados, comprobaciones,
  errores y variantes. No declarar aprobado un escenario todavía pendiente.
- Si aún no existe el módulo, guardar el procedimiento confirmado en documentación de
  continuidad para incorporarlo después. Los cambios de funcionalidad exigirán actualizar
  la guía asociada. Implementar primero procesos estables y revisar todo al cierre del sistema.
- La guía ofrecerá búsqueda por situaciones, navegación por procesos y permisos, ejemplos
  ilustrativos e imágenes sin datos sensibles. No generará operaciones automáticamente.
  El PDF será un complemento derivado del mismo contenido, no documentación paralela.
- Primer caso confirmado para la guía: pago único para dos hijos, $600 y $400.
  Esta actualización es únicamente documental; no se implementó el módulo ni se registraron pagos.

## Buscador de cargos en Nuevo convenio (2026-10-06)

- El input de búsqueda carecía de estilo porque su contenedor no incluía `form-grid`,
  clase requerida por las reglas compartidas de campos. Ahora reutiliza el mismo diseño
  en claro/oscuro, foco y móvil, sin otra paleta ni cambios de consulta o validación.
- Una prueba protege el uso de la clase común y el layout responsivo. No hay migración;
  la siguiente disponible sigue siendo V56.
- La construcción Docker aprobó 484 pruebas sin fallos ni errores.
- Se desplegó sólo `app` y salud respondió `UP`; no se modificaron datos ni volúmenes.

## V55 — soporte del portal de maestros (2026-10-05)

- Se agregó un módulo propio en Seguridad y soporte, con permiso `PORTAL_MAESTRO_SOPORTE`
  independiente del acceso familiar y de la administración de Maestros. Requiere cuenta
  administrativa persistida y alcance institucional; no autoriza recuperación ni cuenta docente.
- La selección filtra maestros y cuentas activos de la misma institución antes de paginar;
  Excel Apache POI reutiliza la búsqueda y procesa bloques de 100 registros.
- El soporte comparte plantillas y servicios con el portal real, conservando el principal
  administrativo de sesión y pasando una identidad docente temporal exclusivamente a
  consultas. Todas las rutas revalidan cuenta y alcance; las fichas de alumnos siguen limitadas
  a las asignaciones docentes y las planeaciones a su propietario.
- Banner persistente, cambio de maestro y regreso a administración; filtros y paginación
  conservan el contexto. La captura puede consultarse con campos protegidos, incluso con
  grupo histórico sin asignación vigente, pero no se guarda ni envía. PDF y fotos responden
  inline y sin caché, y la auditoría atribuye las consultas al administrador real.
- V55 registra sólo el permiso nuevo, sin asignarlo a roles. Próxima migración: V56.
- Docker compiló 722 fuentes principales y aprobó 483 pruebas. Las pruebas de renderizado
  recorrieron portal compartido con paginación, captura protegida y portal normal del maestro;
  comprobaron que sólo el portal real muestra edición y cierre de sesión docente. Las pruebas
  de servicio cubrieron permiso propio, alcance, cuenta activa, otra institución y principal
  de consulta sin cambiar la autenticación original. Queda la revisión visual autenticada
  del propietario, sin crear datos personales ni planeaciones operativas para verificar.
- Se recreó únicamente `app`; PostgreSQL confirmó V55 exitosa y `PORTAL_MAESTRO_SOPORTE`,
  y `/actuator/health` respondió `UP`. No se asignaron permisos a roles ni se cambiaron
  maestros, cuentas o datos académicos durante el despliegue.

## Captura guiada del calendario de cuotas (2026-10-05)

- Una sola vez muestra Fecha límite de pago y despliega la vigencia sólo en Configuración
  adicional. Cada mes solicita primer mes, último mes y día límite. Un resumen vivo explica
  importe, repetición y generación; se reutilizan los controles y temas del sistema.
- La sugerencia de vigencia consulta una sola inscripción autorizada y usa su intersección
  con el ciclo escolar. El servidor también completa la vigencia de cuota única cuando no
  se envía y deriva rangos mensuales; conserva fechas históricas parciales del mismo mes.
- Las validaciones transaccionales originales y la generación sin duplicados no cambian.
  No hubo migración ni se modificaron cargos o cuotas existentes para verificar.
- Se añadieron pruebas de sugerencia autorizada, meses HTML, rangos parciales, preservación
  de fechas, meses fuera del ciclo y contrato de interfaz. La revisión en navegador llegó
  al login por sesión caducada; queda la comprobación visual autenticada del propietario.
- Docker compiló 720 fuentes principales y aprobó 474 pruebas sin fallos. Node verificó
  sintaxis y el comportamiento de campos, resumen y derivación de fechas mensuales.
- Se recreó únicamente `app` y `/actuator/health` respondió `UP`. La base de datos y los
  volúmenes permanecieron intactos; no es necesario reconstruir otra vez para probar.

## Expediente de tutor segmentado y ficha Jasper (2026-10-02)

- La edición administrativa de Tutores abre en **Identificación oficial** y continúa con
  **Ficha del tutor**, **Información del tutor**, **Contacto y domicilio**, **Información
  laboral** y **Portal de familias**. La navegación reduce el desplazamiento y conserva
  hashes directos para cada sección.
- Información, contacto y trabajo siguen siendo campos de la misma tabla y se envían en un
  solo formulario. Los botones por sección actualizan el expediente íntegro, conservan la
  versión optimista y los errores activan automáticamente la pestaña del campo afectado.
  Identificación y acceso familiar mantienen formularios y operaciones independientes.
- Identificación oficial incorpora una ficha Jasper PDF privada con logo institucional,
  resumen del tutor, documento vigente, información personal, contacto, domicilio, datos
  laborales y estado del portal. Si la identificación vigente es una imagen aparece en el
  encabezado; si es PDF se incluyen tipo, nombre y fecha, sin incrustar el documento.
- El endpoint exige permiso de Tutores y alcance institucional, usa `no-store` y abre el
  reporte `inline`. La prueba generó, leyó y renderizó sus dos páginas; la inspección visual
  no encontró recortes, traslapes ni problemas de jerarquía.
- No hubo migración. Docker compiló 704 fuentes principales y 127 de prueba, ejecutó 451
  pruebas sin fallos, desplegó la imagen y `/actuator/health` respondió `UP`. La siguiente
  migración disponible continúa siendo V54.

## Expediente de alumno segmentado y reportes Jasper (2026-10-02)

- La edición administrativa de Alumnos usa seis pestañas: **Ficha del alumno**,
  **Información del alumno**, **Contacto y domicilio**, **Notas administrativas**,
  **Expediente documental** y **Ficha médica**. Sólo se presenta una sección a la vez para
  reducir el desplazamiento; expediente documental y ficha médica conservaron sus flujos.
- Información, contacto y notas siguen perteneciendo a la misma entidad y al mismo
  formulario HTML. Cada sección ofrece su acción Guardar, pero todas actualizan el alumno
  de forma íntegra, conservan versión optimista y muestran los errores en la pestaña del
  campo afectado. El alta nueva permanece como un formulario continuo.
- La ficha principal permite abrir en otra pestaña un PDF Jasper con identidad escolar,
  datos personales, contacto, domicilio y observaciones. La ficha médica ofrece otro PDF
  privado con datos médicos, cuidados y actuación en emergencias. Ambos usan el logo de la
  institución —o el provisional—, fotografía cuando existe, fechas `dd/MM/yyyy`, pie con
  paginación y contenido extensible a varias páginas sin recortes.
- Los endpoints validan permiso de Alumnos y alcance institucional, responden
  `Content-Disposition: inline` y no almacenan copias públicas. La revisión automatizada
  leyó y renderizó todas las páginas de ambos documentos; la inspección visual confirmó
  jerarquía, espaciado, saltos de página y ausencia de traslapes.
- No hubo migración. Docker compiló 703 fuentes principales y 126 de prueba, ejecutó 449
  pruebas sin fallos, desplegó la imagen y `/actuator/health` respondió `UP`. La siguiente
  migración disponible continúa siendo V54.

## Expediente de pago con navegación por pestañas

- La acción **Ver** de Pagos presenta el expediente con la misma navegación tipo navegador
  usada en Alumnos: **Resumen**, **Distribución**, **Gestión**, **Devoluciones** cuando aplica y
  **Comprobantes**. Sólo permanece visible la sección elegida para evitar desplazamientos largos.
- Resumen reúne los datos, movimiento financiero y observaciones; Distribución muestra solicitudes
  o aplicaciones; Gestión concentra validación, rechazo y cancelación; Devoluciones separa su
  historial y captura; Comprobantes agrupa el PDF oficial y los archivos privados.
- Los errores reabren automáticamente Gestión o Devoluciones según la operación. Ambos accesos de
  regreso al listado tienen diseño consistente, temas claro/oscuro y adaptación móvil.
- Gestión usa una sola superficie continua: el encabezado de control, las decisiones, la resolución
  y la corrección excepcional comparten alineación y separadores; no deben dividirse en paneles
  superpuestos ni resolverse con márgenes negativos.
- Docker compiló 701 fuentes y ejecutó 439 pruebas sin fallos. La imagen quedó desplegada y salud
  respondió `UP`. La sesión del navegador había caducado, por lo que no se usaron credenciales ni
  se leyó `.env` para forzar la revisión autenticada. No hubo migración.

## Claridad del acceso temporal en validación financiera

- El usuario bootstrap configurado por entorno sigue siendo exclusivamente un acceso de
  recuperación: no representa una cuenta persistida y no puede validar, rechazar ni autorizar
  movimientos de dinero porque la operación quedaría sin un responsable administrativo.
- El detalle de pago ahora detecta ese acceso antes del envío, oculta las decisiones financieras
  y muestra una explicación visible para cerrar sesión e ingresar con un usuario creado en
  **Seguridad → Usuarios**. Cambiar y después restaurar la cuenta destino no altera esta regla.
- Docker compiló 701 fuentes y ejecutó 438 pruebas sin fallos. La imagen quedó desplegada y
  `/actuator/health` respondió `UP`; no se agregó una migración.

## Ajuste posterior a V52 — cuenta precargada durante la validación

- La validación de un pago precarga como destino la cuenta declarada durante el registro y
  la mantiene protegida. El operador puede publicar el ingreso sin volver a buscarla.
- **Cambiar cuenta destino** habilita la búsqueda como corrección excepcional. Si el destino
  seleccionado es diferente, el motivo es obligatorio tanto en interfaz como en servicio.
  Volver a **Conservar cuenta declarada** restaura identificador y etiqueta originales.
- La auditoría de `PAGO_VALIDADO` conserva cuenta declarada, cuenta destino, indicador de
  modificación y motivo, además del usuario que ya identifica cada registro de auditoría.
- Docker compiló 701 fuentes y ejecutó 436 pruebas sin fallos. La imagen quedó desplegada
  y `/actuator/health` respondió `UP`. No hubo migración; la siguiente disponible continúa
  siendo V53.

## Ajuste posterior a V52 — separación visual entre cargos y pagos

- El nombre visible de las obligaciones cambió finalmente a **Adeudos de alumnos** en navegación,
  permisos, formularios, generación automática y ayudas. Las rutas y permisos internos
  se conservaron para no romper integraciones: un cargo representa lo que el alumno debe;
  un pago representa dinero recibido.
- La cuenta declarada ahora es obligatoria también en el formulario administrativo y en
  el servicio. Debe señalar la caja o cuenta bancaria donde se recibió el dinero.
- El ajuste manual se presenta como **Ajustar saldo del cargo**, con una explicación neutra:
  descuentos y recargos modifican la obligación, pero no registran un ingreso. Los estados
  validado, rechazado y cancelado, sus motivos y la cancelación excepcional recibieron un
  diseño responsivo consistente en temas claro y oscuro.
- Docker compiló 701 fuentes y ejecutó 433 pruebas sin fallos. El servicio desplegado
  respondió `UP`. No hubo cambios de esquema; la siguiente migración disponible continúa
  siendo V53.

## Verificación V52 — folio interno secuencial de pagos

- El folio dejó de formar parte de los formularios y solicitudes del administrador y del
  portal familiar. `PagoServiceImpl` lo asigna dentro de la operación de guardado, por lo
  que ningún cliente puede proponerlo ni modificarlo.
- V52 crea la secuencia global `seq_pago_folio`. El formato es
  `PAG-AAAAMMdd-consecutivo`, usando la fecha de registro en la zona horaria de la
  institución y un consecutivo con al menos seis dígitos. La secuencia evita repeticiones
  entre instituciones y no reutiliza valores; los saltos por transacciones revertidas son
  normales. Una verificación adicional evita colisionar con folios históricos.
- El folio se conserva como referencia interna en consultas, movimientos, auditoría,
  comprobantes y reportes. Docker compiló 701 fuentes y ejecutó 432 pruebas sin fallos;
  Flyway aplicó V52 y el servicio respondió `UP`. La siguiente migración disponible es V53.

## Verificación posterior a V50 — pagos administrativos con tarjeta y contexto protegido

- V51 amplía los métodos administrativos con **Tarjeta**. Para evitar duplicar saldos,
  una terminal no se modela como cuenta financiera: el usuario selecciona la cuenta
  bancaria o de inversión donde se deposita la venta; una caja no es válida para tarjeta.
- El autocompletado de cuenta responde al método: efectivo muestra cajas y transferencia
  o tarjeta muestran banco/inversión. El formulario adapta referencia y comprobante al
  método elegido, y el comprobante oficial presenta el nombre correcto.
- Cuando Registrar pago se abre desde un pago de alumno/inscripción, institución, plantel,
  tutor titular y moneda quedan visibles pero protegidos. Se conserva editable el nombre
  de quien materialmente entrega, transfiere o presenta la tarjeta porque puede ser una
  persona distinta del tutor responsable.
- Cada asignación muestra cargo/alumno e importe en una tarjeta responsiva. Al seleccionar
  el cargo se carga y protege su saldo completo. En el flujo rápido no puede sustituirse;
  en el alta general puede cambiarse el cargo y sólo **Registrar pago parcial** desbloquea
  deliberadamente el importe. El saldo de referencia se conserva al volver de una
  validación para que **Usar saldo completo** siempre restaure el valor correcto.
- El selector de cargo/alumno ya tiene fondo, borde y contraste propios en temas claro y
  oscuro. Las explicaciones bajo los campos se identifican con icono y color informativo;
  sólo las validaciones reales permanecen rojas. Esta verificación usaba todavía un folio
  visible generado al abrir la captura; V52 lo sustituyó por el folio interno secuencial.
- Se unificó el diseño responsivo y oscuro del formulario y de **Cerrar detalle**,
  **Registrar pago** y **Aplicar ajuste**. Docker compiló 701 fuentes y ejecutó 431
  pruebas sin fallos. Flyway aplicó V51 y el servicio desplegado respondió `UP`; la
  siguiente migración disponible es V52.

## Verificación posterior a V50 — reporte familiar de transferencia protegido

- La búsqueda de **Cargo vigente del alumno** usa un desplegable propio, responsivo y
  compatible con tema oscuro. Cada foco consulta los primeros diez cargos; desde tres
  caracteres filtra en servidor y conserva el comportamiento tras errores de validación.
- El saldo de cada cargo y el total a transferir se completan automáticamente y son de sólo
  lectura. Seleccionar varios cargos suma sus saldos completos sin captura manual.
- El servidor no confía en esos campos: valida propiedad, vigencia, duplicados y saldo,
  reemplazando cualquier importe enviado por el valor vigente antes de crear la solicitud.
- No hubo migración. La suite aumentó a 427 pruebas.

## Verificación posterior a V50 — vista previa de pagos automáticos

- La generación de pagos programados desde cuotas ya no permite escribir en el primer
  paso. **Visualizar cuotas por aplicar** muestra cada periodo faltante y sólo entonces
  habilita **Confirmar y generar pagos faltantes**.
- La tabla paginada identifica alumno, matrícula, plantel, concepto, frecuencia, periodo,
  vencimiento e importe. El encabezado resume cuotas afectadas, pagos nuevos e importe
  programado total.
- Vista previa y confirmación reutilizan la misma planificación. La confirmación recalcula
  el corte y las claves idempotentes `AUTO` evitan crear un periodo que ya exista.
- Se corrigió además el diseño de los botones finales de los generadores de pagos y
  recargos mediante un componente visual explícito para escritorio, móvil y tema oscuro.
- Ambos formularios llevan automáticamente la vista al resultado recién calculado, enfocan
  un encabezado accesible y muestran **Preparando vista…** durante el envío, evitando que
  la lista pase inadvertida debajo del formulario o que se envíe dos veces.
- Se corrigió el error al confirmar la generación: las acciones `POST` de pagos faltantes
  y recargos ahora se procesan con `th:action`, permitiendo que Thymeleaf agregue la
  protección CSRF. Las pruebas de interfaz verifican esta condición para evitar regresiones.
- No hubo migración; V51 sigue disponible. Docker ejecutó 426 pruebas sin fallos, validó
  los repositorios al arrancar y el servicio desplegado respondió `UP`.

## Verificación posterior a V50 — vista previa obligatoria de recargos

- La generación manual de recargos se convirtió en un flujo seguro de dos pasos. Primero
  se ejecuta una vista previa de sólo lectura y después se habilita la confirmación.
- La vista presenta totales y una lista paginada con alumno, concepto, plantel, vencimiento,
  atraso, política, cantidad de periodos, saldo, recargo y nuevo saldo. El diseño responde
  en escritorio y móvil y cuenta con variantes clara y oscura.
- Previsualización y confirmación utilizan el mismo plan de cálculo. Antes de escribir, la
  confirmación recalcula el corte; las claves únicas siguen evitando duplicados.
- Los pagos completamente liquidados se excluyen expresamente tanto de la vista como de la
  generación. También se omiten cargos cancelados, sin política automática, sin saldo o sin
  nuevos periodos aplicables.
- No hubo migración y V51 sigue libre. Docker ejecutó 425 pruebas sin fallos, Spring validó
  los repositorios y la aplicación desplegada respondió `UP`. La sesión de navegador estaba
  expirada, por lo que la prueba visual autenticada quedó para el propietario sin consultar
  credenciales ni `.env`.

## Verificación posterior a V50 — cobranza asistida desde la inscripción

- Se conservaron desacoplados Cuotas individuales, Adeudos de alumnos y Pagos recibidos, pero la
  inscripción ahora los enlaza mediante una tercera pestaña **Cobranza**. La pantalla
  resume cuotas, cargos y estado del proceso sin recuperar catálogos completos.
- El alta asistida precarga el contexto de la inscripción y permite crear una cuota única
  y, si el usuario tiene permiso, emitir inmediatamente su cargo. La generación reutiliza
  la clave idempotente `AUTO` de la cuota; repetirla no produce duplicados.
- Un cargo pendiente o parcial puede abrir el formulario de pago precargado. Se trasladan
  cargo, saldo, institución, plantel, moneda y responsable financiero vigente. Seleccionar
  un cargo desde el autocompletado común también rellena importe solicitado y total.
- Cada operación conserva permisos y alcance independientes. La relación cuota–inscripción
  se comprueba antes de emitir el cargo y la creación conjunta es transaccional.
- No se cambió el esquema; V51 sigue disponible. Docker compiló el paquete y ejecutó 422
  pruebas sin fallos. Falta únicamente la prueba funcional controlada del propietario con
  una inscripción y un importe de prueba.

## Decisiones pendientes posteriores a V50

- Queda pendiente incorporar **Preparar pago anticipado** dentro de la pestaña Cobranza de
  una inscripción. Permitirá seleccionar el cargo de inscripción y uno o varios periodos
  mensuales futuros —por cantidad de mensualidades o por periodos explícitos— y mostrará
  una vista previa antes de escribir. La confirmación generará únicamente los cargos
  faltantes de esa inscripción, reutilizando claves idempotentes, y distinguirá cuáles ya
  existían. Después abrirá un solo pago administrativo precargado con aplicaciones
  independientes para cada cargo. Al validarlo se publicará un único ingreso en caja o
  cuenta, conservando alumno, concepto y periodo por separado. No deberá ejecutar el
  generador masivo de otros alumnos ni aceptar importes sin identificar. Respetará los
  permisos separados de cuotas, cargos y pagos, el alcance institucional y la validación
  financiera actual. Este punto es sólo diseño pendiente; no se implementó código ni
  migración en esta revisión.

- La decisión inicial de manual PDF se actualizó el 06/10/2026: la guía integrada será
  la fuente principal y se construirá progresivamente con procesos probados. Un PDF podrá
  derivarse del mismo contenido como complemento. La ayuda explicará con lenguaje sencillo y
  procedimientos numerados cada configuración, captura, consulta, aprobación, exportación
  y operación disponible para administración, maestros, familias y demás perfiles finales.
  Incluirá requisitos previos, significado de campos, resultados esperados, errores comunes,
  permisos y relaciones entre módulos. Siempre que sea posible utilizará capturas reales de
  las pantallas finales —sin datos sensibles— con indicaciones visuales de botones, menús y
  pestañas. Tendrá índice, glosario, solución de problemas, control de versión y revisión
  visual completa. Al concluir el sistema se revisarán todos los recorridos y textos definitivos.

- La consistencia visual pasa a ser un requisito de aceptación: todo módulo debe reutilizar
  los patrones consolidados de listados, filtros, formularios, ayudas, acciones y estados,
  y revisarse en escritorio/móvil y en temas claro/oscuro. La deuda visual de Calendario
  escolar fue atendida el 03/10/2026 sin modificar su funcionalidad V48; queda únicamente
  la revisión visual autenticada del propietario en escritorio y móvil.
- Compras tendrá posteriormente un expediente documental opcional con PDF, fotografías
  optimizadas y XML CFDI. Un gasto de caja puede confirmarse sin documento fiscal; si se
  marca sin comprobante exigirá justificación. Ticket/recibo y CFDI serán clasificaciones
  separadas y no condicionarán indebidamente el movimiento financiero.
- El CFDI conservará el XML original privado y además normalizará en PostgreSQL encabezado,
  conceptos, impuestos y relaciones. Se validarán UUID duplicado, emisor/proveedor, receptor,
  moneda y totales. La importación de conceptos tendrá vista previa y confirmación; nunca
  reemplazará automáticamente las partidas administrativas.

## Verificación V50 — proveedores y compras

- Se agregó el catálogo multiinstitución de proveedores, con RFC único cuando se captura,
  datos de contacto, estado activo/inactivo, concurrencia optimista, filtros, paginación y
  Excel con los mismos criterios de la pantalla.
- Una compra conserva proveedor, plantel opcional, cuenta financiera, categoría de egreso,
  fecha, referencia y hasta 100 partidas con cantidad, precio e impuesto. Los totales se
  calculan en servidor y también se presentan dinámicamente en el formulario.
- Guardar o editar mantiene estado `BORRADOR` y no cambia el saldo. Confirmar utiliza el
  servicio financiero existente para publicar un `EGRESO` idempotente en la cuenta elegida;
  por ello respeta fecha de apertura, saldo disponible, alcance y bloqueo de cuenta.
- Cancelar una compra confirmada exige motivo y genera una reversa financiera en la misma
  cuenta. El egreso original no se elimina; se conservan revisor, fecha y motivo.
- Los permisos son `PROVEEDOR_LEER`, `PROVEEDOR_ADMINISTRAR`, `COMPRA_LEER`,
  `COMPRA_ADMINISTRAR`, `COMPRA_CONFIRMAR` y `COMPRA_CANCELAR`. Flyway V50 protege estados,
  totales y vínculos con movimiento/reversa mediante restricciones de PostgreSQL.
- Docker ejecutó 417 pruebas sin fallos, Flyway aplicó V50, Hibernate validó el esquema y
  `/actuator/health` respondió `UP` después del despliegue. La siguiente migración disponible
  es V51.

## Verificación V49 — actualización familiar de expedientes

- El portal familiar incorpora **Expediente familiar** por alumno. Sólo un tutor activo con
  vínculo vigente y `puedeAutorizar` puede proponer documentos o información médica.
- Cada envío exige consentimiento explícito y conserva su versión, texto y fecha. La
  propuesta permanece separada del expediente oficial en estado pendiente.
- Administración dispone de bandeja con filtros en PostgreSQL, paginación real, detalle,
  visualización inline de archivos y Excel con los mismos criterios. Aprobar o rechazar
  exige una respuesta para la familia y registra usuario, fecha y resolución.
- Aprobar un documento crea el registro oficial de `AlumnoDocumento`; aprobar información
  médica sustituye la ficha oficial en una transacción. Rechazar no modifica el expediente.
- Se admiten PDF de hasta 10 MB e imágenes JPEG, PNG, HEIC o HEIF de hasta 20 MB; las
  imágenes se normalizan y optimizan antes del almacenamiento privado.
- Los permisos son `ACTUALIZACION_EXPEDIENTE_LEER` y
  `ACTUALIZACION_EXPEDIENTE_REVISAR`. Flyway V49 crea el dominio y sus restricciones; la
  siguiente migración disponible es V50.
- Docker ejecutó 412 pruebas sin fallos, Flyway aplicó V49, Hibernate validó el esquema y
  `/actuator/health` respondió `UP` después del despliegue.

## Verificación posterior a V48 — optimización de fotografías móviles

- Se centralizó el procesamiento de fotografías de alumnos, maestros y usuarios.
- Las cargas nuevas aceptan JPEG, PNG, HEIC y HEIF hasta 20 MB; la firma real se valida
  sin confiar en el nombre o MIME enviados por el navegador.
- ImageMagick con libheif aplica orientación, elimina metadatos, limita el lado mayor a
  1920 píxeles y genera JPEG calidad 85 para reducir el almacenamiento sin afectar la
  presentación normal de la ficha.
- El proceso tiene límites de memoria, disco, dimensiones, tiempo y concurrencia. Sólo
  la versión optimizada se entrega al almacenamiento privado y su tamaño/checksum son
  los registrados en PostgreSQL.
- El cambio no necesita migración y no procesa retroactivamente fotografías históricas.

## Verificación V48 — calendario escolar detallado

- Se agregó `calendario_escolar_detalle`, asociado a institución y ciclo escolar, con
  alcance opcional por plantel, nivel o ambos.
- Los tipos disponibles son día inhábil, vacaciones, evento académico y variación de
  horario. Días inhábiles y vacaciones suspenden clases automáticamente.
- Las fechas se validan contra el ciclo, las variaciones exigen un rango horario y no
  se permiten suspensiones activas superpuestas con el mismo alcance.
- Administración cuenta con formulario adaptable a tema claro/oscuro, catálogos
  dependientes de la institución, filtros en base de datos, paginación y exportación
  Excel que reutiliza exactamente los criterios de la pantalla.
- Se agregaron los permisos `CALENDARIO_ESCOLAR_LEER` y
  `CALENDARIO_ESCOLAR_ADMINISTRAR`; los registros institucionales requieren alcance
  institucional y los de plantel respetan el alcance asignado al usuario.
- Flyway V48 fue aplicada en la instalación local y la aplicación inició validando el
  esquema. La siguiente migración disponible es V49.

## Homologación visual posterior a V48 — 03/10/2026

- Se conservó intacta la consulta, paginación, exportación, permisos y edición de V48.
- El listado incorporó encabezado y contador legibles, filtros agrupados en tarjeta,
  acciones consistentes, resumen de resultados, estados diferenciados y vacío accionable.
- El formulario ahora reutiliza la cabecera de sesión, mensajes de error descriptivos,
  secciones numeradas, guía visual de alcance, controles y acciones del diseño compartido.
- Se agregó una descripción contextual propia del módulo y se cubrieron tema oscuro y
  puntos de quiebre para escritorio, tableta y móvil en la hoja dedicada.
- Docker compiló 719 fuentes de producción y 129 de prueba; las 456 pruebas terminaron
  sin fallos y la aplicación desplegada respondió `UP`. El navegador local llegó al login
  por sesión caducada, así que permanece como comprobación manual abrir listado y formulario
  con una sesión administrativa y revisar claro/oscuro y escritorio/móvil sin guardar datos.

## Verificación V47 — horarios y clases

- Se agregó `horario_clase`, vinculado con la asignación maestro–grupo–materia y con
  vigencia, día, horas, aula, estado y auditoría.
- PostgreSQL y la capa de servicio impiden empalmes para maestro, grupo o aula.
- Administración cuenta con formulario, filtros independientes, paginación y Excel
  por bloques usando los mismos criterios de la pantalla.
- El portal docente incorpora «Mi horario» y el portal familiar muestra el horario
  vigente del grupo del alumno, incluyendo la vista de soporte administrativo.
- Los permisos nuevos son `HORARIO_CLASE_LEER` y `HORARIO_CLASE_ADMINISTRAR`.

## Verificación V44–V45 — correcciones transversales y ficha docente

- Se agregó `V44__agregar_fotografia_maestro.sql` y se aplicó correctamente sobre la
  instalación local, conservando los archivos en almacenamiento privado.
- `V45__permitir_periodo_academico_anual.sql` amplía la restricción de PostgreSQL para
  aceptar periodos de tipo `ANUAL`.
- Roles, pagos del portal familiar, estados por módulo, fecha de asistencia,
  autocompletado de boletas, visualización inline de documentos y edición de
  planeaciones fueron corregidos.
- El portal docente ahora expone solamente los alumnos de grupos asignados y vigentes,
  con ficha general, fotografía y ficha médica en modo consulta.
- Docker construyó la imagen y ejecutó 380 pruebas sin fallos. Flyway validó 45
  migraciones, Hibernate validó el esquema y `/actuator/health` respondió `UP`.

## Decisiones confirmadas — primera etapa

- Proyecto Maven `sistema-administrativo-escolar`, paquete base `escuela`.
- Java 21 y Spring Boot 4.1.1.
- Interfaz web con Thymeleaf.
- PostgreSQL es la base de datos y usa un volumen persistente en Docker Compose.
- Flyway es el único responsable del esquema; Hibernate usa `ddl-auto=validate`.
- Spring Data JPA, Spring Security, Bean Validation, Lombok, Spring Mail y Actuator.
- Credenciales y secretos se reciben por variables de entorno.
- La imagen Docker compila el proyecto para no depender de Maven en el equipo.
- Código organizado por módulo funcional; las entidades no se expondrán directamente
  en futuros formularios o respuestas.
- La primera etapa se limita al núcleo institucional y académico: Institucion,
  Plantel, NivelEducativo, PlantelNivel, Grado, CicloEscolar, PeriodoAcademico y Grupo.
- Los identificadores son `Long`; todas las entidades tienen fechas y actores de
  auditoría, además de versión para concurrencia optimista.
- `creado_por_id` y `actualizado_por_id` son anulables y no tendrán FK hasta crear
  el módulo Usuario.
- PeriodoAcademico subdivide un ciclo por nivel y no representa meses de cobro.
- El plantel, grado o grupo actual de un alumno se derivará de su inscripción vigente;
  no se duplicará como fuente principal cuando se implemente ese módulo.
- La seguridad inicial deja públicas la portada y la salud; las demás rutas quedan
  protegidas hasta implementar usuarios, roles y permisos.
- No se generan todavía las demás entidades del modelo.

## Verificación de la etapa

- La construcción multietapa de Docker compila con Java 21 y ejecuta las pruebas Maven.
- Resultado: 2 pruebas ejecutadas, sin fallos ni errores.
- Flyway validó y aplicó `V1__crear_nucleo_institucional_academico.sql` en PostgreSQL 17.
- Hibernate inicializó correctamente con `ddl-auto=validate` y detectó 8 repositorios JPA.
- El endpoint Actuator respondió `UP` y la portada Thymeleaf respondió correctamente.
- Limitación del entorno verificado: Maven no está instalado en el host y el puerto 8080
  ya estaba ocupado. La instancia comprobada quedó levantada mediante Docker Compose en
  `http://localhost:18080`, usando `APP_PORT=18080`.

## Decisiones — capa de aplicación del núcleo

- Cada una de las ocho entidades del núcleo tiene DTO de entrada, DTO de respuesta,
  mapper, interfaz de servicio e implementación transaccional.
- Los DTO de entrada son records inmutables y aplican Bean Validation; las respuestas
  usan identificadores para las relaciones y no exponen entidades JPA.
- Códigos, país, moneda y correo se normalizan antes de comparar y persistir.
- Las relaciones propietarias (institución, plantel, nivel, ciclo y grado) no pueden
  reasignarse mediante una actualización; se crea un registro nuevo cuando el historial
  requiere otra relación.
- Las actualizaciones exigen la versión recibida por el cliente y usan el control
  optimista de JPA. Los servicios hacen flush antes de construir la respuesta.
- Los registros históricos no se eliminan físicamente; las entidades que lo admiten
  se desactivan.
- La selección de ciclo predeterminado bloquea la institución y desmarca el anterior
  dentro de la misma transacción.
- No se permiten periodos fuera del ciclo, solapados, duplicados o modificados dentro
  de ciclos cerrados. Reducir las fechas de un ciclo no puede dejar periodos fuera.
- Un grupo sólo puede operar si plantel, institución, nivel y grado están activos, el
  ciclo no está cerrado y existe una oferta educativa activa para su nivel.
- Las excepciones de negocio, ausencia, duplicidad y conflicto de versión tienen un
  manejador MVC global y una vista segura común.

## Verificación de la capa de aplicación

- Compilación Docker correcta de 73 archivos Java de producción.
- 7 pruebas ejecutadas sin fallos: portada, ciclo predeterminado y reglas de periodos.
- Spring inició correctamente, validó Flyway V1 y `ddl-auto=validate`.
- El endpoint de salud permaneció en estado `UP` en el puerto externo 18080.

## Decisiones — interfaz, paginación y exportación

- Todo módulo nuevo debe incluir desde su primera pantalla: filtros ejecutados en base
  de datos, paginación real y exportación Excel con los mismos filtros.
- Los listados del núcleo consultan páginas de 10, 25, 50 o 100 registros mediante
  Spring Data `Pageable` y `JpaSpecificationExecutor`; nunca recuperan el catálogo
  completo para mostrar una pantalla.
- La exportación usa Apache POI `SXSSFWorkbook` y recorre el resultado filtrado en
  bloques, evitando mantener todo el archivo o todos los registros en memoria.
- El objeto `FiltroCatalogo` es compartido por pantalla y exportador para impedir que
  el Excel difiera de la consulta visible.
- Se creó una consola administrativa común para las ocho entidades actuales, con
  búsqueda textual, estado, tamaño de página, navegación y estados vacíos.
- Dirección visual: centro de control académico en azul tinta y cian, alta legibilidad,
  tabla de escritorio, tarjetas móviles y navegación lateral adaptable.
- La lista de módulos de la navegación lateral tiene desplazamiento vertical propio en
  escritorio y móvil; la marca y el estado de servicios quedan fijos y el módulo activo
  se lleva automáticamente a una posición visible.
- El menú filtra módulos con las autoridades de la sesión: basta lectura o
  administración para catálogos operativos, mientras Roles y Usuarios exigen
  administración. El ingreso `/admin` abre el primer módulo autorizado; las consultas
  y exportaciones revalidan el permiso en el controlador además de Spring Security.
  Las autoridades se actualizan para el usuario en su siguiente inicio de sesión.
- La interfaz contempla escritorio, tablet, móvil y ampliación de texto, sin depender
  de JavaScript para la navegación responsiva.
- Las credenciales administrativas temporales se reciben mediante variables de entorno
  y se retirarán cuando se implemente el módulo definitivo de usuarios y roles.

## Verificación de interfaz y exportación

- Compilación de 80 archivos Java y 7 pruebas sin fallos.
- Catálogo autenticado renderizado con filtros y tamaño de página en PostgreSQL real.
- Exportación filtrada generada con respuesta HTTP 200 y libro XLSX válido.
- Aplicación y PostgreSQL permanecen saludables en `http://localhost:18080`.

## Decisiones — temas, acceso y primer mantenimiento

- La consola y el acceso permiten elegir tema claro u oscuro; la preferencia se guarda
  localmente en el dispositivo y, si aún no existe, respeta el tema del sistema operativo.
- Se reemplazó el login predeterminado por una pantalla propia de Nexo Escolar,
  responsiva, accesible y consistente con la consola administrativa.
- El flujo de autenticación sigue bajo Spring Security y redirige a la consola después
  de un acceso correcto.
- Se implementó el mantenimiento completo de Institucion: alta, edición y desactivación
  lógica, con Bean Validation, protección CSRF, versión optimista y mensajes de resultado.
- Institucion es el primer formulario porque es la raíz obligatoria para planteles,
  niveles y ciclos. El mismo patrón visual y técnico se reutilizará en esos catálogos.

## Verificación de esta iteración

- Compilación de 83 archivos Java y 7 pruebas sin fallos.
- Login público, selector de tema y formulario autenticado renderizados correctamente.
- Aplicación y PostgreSQL permanecen saludables en el puerto externo 18080.

## Decisiones — configuración segura y mantenimiento estructural

- Existe un `.env` local, excluido del control de versiones, que concentra las
  credenciales de PostgreSQL y del administrador temporal, además del perfil y puerto.
- Docker Compose exige explícitamente esas variables y ya no contiene contraseñas de
  respaldo; si falta una variable sensible, el despliegue falla de forma inmediata.
- La contraseña del rol PostgreSQL del volumen existente se migró para coincidir con
  `.env`, sin recrear ni eliminar los datos persistentes.
- Se completó el mantenimiento de Plantel, NivelEducativo y PlantelNivel: alta, edición,
  desactivación lógica, validación, protección CSRF y control optimista de versión.
- Los formularios conservan el diseño responsivo y los temas claro/oscuro. Los listados
  siguen paginados y cada módulo mantiene su exportación Apache POI con los mismos filtros.

## Verificación de esta iteración

- Compilación de 89 archivos Java y 7 pruebas sin fallos.
- PostgreSQL y la aplicación reiniciaron usando exclusivamente las variables de `.env`.
- Salud `UP`; formularios de planteles, niveles y oferta respondieron HTTP 200.
- La exportación filtrada de oferta educativa respondió HTTP 200 con un XLSX válido.

## Decisiones — mantenimiento de grados

- Se completó el mantenimiento de Grado con alta, edición y desactivación lógica.
- El formulario selecciona primero la institución y filtra los niveles educativos que
  le pertenecen, pero el dominio conserva únicamente la relación Grado-NivelEducativo.
- En edición, la institución y el nivel propietario se muestran bloqueados para evitar
  la reasignación de relaciones históricas.
- El controlador comprueba en servidor que el nivel seleccionado pertenezca a la
  institución indicada, además de delegar las reglas de unicidad y actividad al servicio.
- El listado de grados habilitó las acciones **Nuevo registro** y **Editar** sin modificar
  su paginación, filtros o exportación existentes.

## Verificación del mantenimiento de grados

- Compilación Docker correcta de 91 archivos Java de producción.
- 11 pruebas ejecutadas sin fallos ni errores, incluidas 4 del controlador de grados.
- PostgreSQL quedó saludable y `/actuator/health` respondió `UP`.
- El formulario autenticado de grado respondió HTTP 200 y renderizó correctamente.
- La exportación filtrada de grados respondió HTTP 200 con firma XLSX válida.

## Decisiones — errores integrados en formularios

- Los cinco mantenimientos disponibles capturan errores esperables al crear, actualizar
  y desactivar: reglas de negocio, concurrencia optimista e integridad de base de datos.
- El mismo formulario se vuelve a renderizar con los valores capturados y un mensaje
  accesible, responsivo y compatible con los temas claro y oscuro.
- Los mensajes específicos del dominio se conservan. Las restricciones SQL se traducen
  según su categoría sin mostrar consultas, nombres internos ni detalles de PostgreSQL.
- Los recursos inexistentes y errores inesperados continúan en el manejador global para
  no ocultar fallos de programación o navegación inválida.
- Este comportamiento es obligatorio para los mantenimientos futuros de ciclos,
  periodos de evaluación y grupos.

## Verificación del manejo de errores

- Compilación Docker correcta de 92 archivos Java de producción.
- 16 pruebas ejecutadas sin fallos ni errores.
- Se verificó que un duplicado mantiene el mismo formulario y los datos introducidos.
- Se probaron las traducciones seguras de unicidad SQL y concurrencia optimista.
- PostgreSQL permaneció saludable y `/actuator/health` respondió `UP`.

## Decisiones — mantenimiento de ciclos escolares

- Se completó el mantenimiento de CicloEscolar con alta y edición de institución,
  código, nombre, fechas, estado y selección predeterminada.
- La institución propietaria queda bloqueada durante la edición y se conserva la versión
  para concurrencia optimista.
- El ciclo usa sus estados `PLANIFICADO`, `ABIERTO` y `CERRADO`; no tiene desactivación
  lógica porque su ciclo de vida se representa mediante esos estados.
- Al marcar un ciclo como predeterminado, el servicio bloquea la institución y desmarca
  el anterior dentro de la misma transacción.
- Los errores de código duplicado, fechas inválidas, periodos fuera del nuevo rango,
  concurrencia o integridad permanecen en el mismo formulario.

## Verificación del mantenimiento de ciclos escolares

- Compilación Docker correcta de 94 archivos Java de producción.
- 20 pruebas ejecutadas sin fallos ni errores.
- El formulario autenticado de ciclo escolar respondió HTTP 200 y renderizó sus estados.
- La exportación filtrada de ciclos respondió HTTP 200 con firma XLSX válida.
- PostgreSQL permaneció saludable y `/actuator/health` respondió `UP`.

## Decisiones — mantenimiento de periodos de evaluación

- Se completó el mantenimiento de PeriodoAcademico con alta y edición de ciclo, nivel,
  código, nombre, tipo, orden, fechas, estado y observaciones.
- La institución auxiliar filtra ciclos y niveles, pero no se duplica como relación del
  dominio. En edición se bloquean institución, ciclo y nivel.
- La pantalla limita las fechas al rango del ciclo como ayuda; el servicio conserva la
  validación definitiva de rango, solapamientos, duplicados y ciclo cerrado.
- Todos los errores esperables se muestran dentro del formulario conservando la captura.

## Verificación del mantenimiento de periodos de evaluación

- Compilación Docker correcta de 96 archivos Java de producción.
- 24 pruebas ejecutadas sin fallos ni errores.
- El formulario autenticado respondió HTTP 200 y renderizó tipos, estados y rango.
- La exportación filtrada respondió HTTP 200 con firma XLSX válida.
- PostgreSQL permaneció saludable y `/actuator/health` respondió `UP`.

## Decisiones — mantenimiento de grupos y cierre de catálogos

- Se completó el mantenimiento de Grupo con alta, edición y desactivación lógica.
- La institución filtra planteles y ciclos; el plantel limita los grados a niveles de su
  oferta activa. Los ciclos cerrados y relaciones inactivas no se ofrecen en altas.
- En edición se bloquean plantel, ciclo y grado para conservar relaciones propietarias.
- Nombre, turno, código, aula, capacidad y estado activo permanecen editables.
- El servidor revalida institución, oferta activa, ciclo, grado, turno, capacidad y
  unicidad; los errores esperables permanecen en el mismo formulario.
- Con este módulo, los ocho catálogos del núcleo institucional y académico cuentan con
  mantenimiento conforme a su ciclo de vida, además de filtros, paginación y Excel.
- El selector de grado del alta de grupos dejó de depender de la fotografía inicial de
  la página: consulta sin caché únicamente los grados activos ofrecidos por el plantel
  seleccionado. Un panel accesible y compatible con ambos temas distingue los estados
  de espera, carga, disponibilidad, ausencia de oferta y error.

## Verificación del mantenimiento de grupos

- Compilación Docker correcta de 99 archivos Java de producción.
- 28 pruebas ejecutadas sin fallos ni errores.
- El formulario autenticado de grupo respondió HTTP 200 y renderizó sus selectores.
- La exportación filtrada respondió HTTP 200 con firma XLSX válida.
- PostgreSQL permaneció saludable y `/actuator/health` respondió `UP`.

## Decisiones — base definitiva de usuarios, roles y permisos

- Flyway V2 agrega `Usuario`, `Rol`, `Permiso`, `RolPermiso`, `UsuarioRol` e
  `InvitacionUsuario` sin modificar la migración V1 ni los datos existentes.
- Los usuarios y roles pertenecen a una institución; sus nombres, correos y códigos se
  comparan normalizados dentro de ese límite institucional.
- Los permisos son un catálogo técnico global. V2 siembra 18 permisos para el núcleo
  actual y para administrar usuarios y roles.
- Las asignaciones de rol tienen alcance `INSTITUCION`, `PLANTEL` o
  `VINCULOS_TUTOR`. El plantel es obligatorio exclusivamente para el alcance de plantel
  y debe pertenecer a la institución del usuario y del rol.
- Las invitaciones son revocables, vencen como máximo en siete días y son de un solo
  uso. Sólo se persiste SHA-256 del token; la contraseña se persiste codificada con
  BCrypt y nunca se envía ni almacena en texto plano.
- Se mantienen auditoría, concurrencia optimista y desactivación lógica. El login sigue
  usando temporalmente las credenciales de `.env` para evitar un bloqueo administrativo
  antes de crear y comprobar el primer usuario real.

## Verificación de la base de seguridad

- Compilación Docker correcta de 130 archivos Java de producción.
- 35 pruebas ejecutadas sin fallos ni errores; seis cubren alcances multiinstitución,
  token protegido, contraseña codificada y consumo único de invitación, y una protege
  el acceso del administrador temporal al coexistir con BCrypt.
- Flyway validó dos migraciones y aplicó V2 sobre el volumen existente.
- PostgreSQL 17 contiene las seis tablas nuevas y los 18 permisos sembrados.
- Hibernate validó el esquema, detectó 14 repositorios y la aplicación respondió `UP`
  en `http://localhost:8080`.
- Se verificó una autenticación HTTP real con las credenciales cargadas internamente en
  el contenedor, sin imprimirlas; el acceso respondió correctamente.

## Decisiones — módulo visible de roles y permisos

- `Roles y permisos` se agregó como noveno módulo de la consola administrativa y como
  primera sección visible de Seguridad.
- El listado filtra código, nombre y descripción directamente en PostgreSQL, pagina sin
  cargar el catálogo completo y exporta un XLSX con los mismos filtros.
- El formulario crea y edita roles por institución, permite seleccionar los 18 permisos
  técnicos y muestra validaciones, duplicados, concurrencia e integridad sin salir de la
  pantalla.
- Los roles se desactivan lógicamente. Flyway V3 agregó el indicador `activo` a
  `RolPermiso`, de modo que retirar o volver a conceder permisos conserva la relación y
  su auditoría en lugar de eliminarla.

## Verificación del módulo de roles y permisos

- Compilación Docker correcta de 133 archivos Java de producción.
- 39 pruebas ejecutadas sin fallos ni errores, incluidas las reglas de sincronización
  histórica y el controlador del formulario de roles.
- Flyway validó y aplicó V3 sobre el volumen existente; Hibernate validó el esquema.
- Listado y formulario respondieron autenticados y el formulario presentó los permisos
  técnicos disponibles.
- La exportación filtrada respondió correctamente con firma XLSX válida y la aplicación
  permaneció `UP` en `http://localhost:8080`.

## Decisiones — módulo visible de usuarios y asignaciones

- `Usuarios` se agregó a la sección Seguridad con búsqueda por usuario, correo o
  institución, filtro de estado, paginación real y exportación Excel por bloques.
- El alta siempre crea usuarios `INVITADO`; institución, username y correo se validan y
  no se permite reasignar la institución al editar.
- La pantalla de edición administra estados y roles con alcance `INSTITUCION`,
  `PLANTEL` o `VINCULOS_TUTOR`. Usuario, rol y plantel se validan contra la misma
  institución y las asignaciones se desactivan o reactivan sin borrado físico.
- La administración puede generar una invitación de 48 horas. Una nueva invitación
  revoca la anterior, el enlace se muestra una sola vez y sólo se persiste su hash.
- `/activar-cuenta` es una pantalla pública protegida por CSRF que valida confirmación y
  longitud, consume el token una vez y guarda la contraseña con BCrypt.
- El administrador temporal de `.env` continúa siendo el único usuario autenticable
  hasta crear, activar y verificar el primer administrador persistido.

## Verificación del módulo de usuarios

- Compilación Docker correcta de 139 archivos Java de producción.
- 47 pruebas ejecutadas sin fallos ni errores, incluidas duplicidad, estados,
  aislamiento de asignaciones, controlador administrativo y activación.
- Listado y formulario de alta respondieron autenticados; la pantalla de activación
  respondió sin sesión y la exportación produjo una firma XLSX válida.
- Se corrigió y volvió a probar una condición nula de Thymeleaf detectada durante la
  verificación real.
- PostgreSQL permaneció en Flyway V3, la aplicación respondió `UP` y la tabla `usuario`
  siguió vacía: no se dejaron registros de prueba.

## Decisiones — autenticación de usuarios persistidos

- `UsuarioSistemaDetailsService` reemplazó el proveedor exclusivamente temporal y
  autentica usuarios `ACTIVO` mediante el hash BCrypt guardado al consumir la
  invitación.
- Un username único se resuelve directamente. Cuando existe en más de una institución,
  el acceso exige `CODIGO_INSTITUCION\usuario` para eliminar ambigüedad.
- Las autoridades se derivan únicamente de asignaciones activas, roles activos y
  permisos activos. Las rutas administrativas exigen su permiso técnico correspondiente.
- El administrador de `.env` permanece como acceso de recuperación y recibe todos los
  permisos técnicos, sin guardar ni publicar su contraseña.
- La cuenta real `criz110` quedó activa, con credencial BCrypt y rol administrador de
  alcance institucional; la contraseña en texto plano no se leyó ni se conservó.

## Verificación de la autenticación persistida

- Compilación Docker correcta de 140 archivos Java de producción.
- 50 pruebas ejecutadas sin fallos ni errores; tres cubren usuario persistido, permisos,
  usernames multiinstitución y acceso temporal de recuperación.
- Spring registró `usuarioSistemaDetailsService` como proveedor global de autenticación.
- Flyway validó V1, V2 y V3, la aplicación inició correctamente y salud respondió HTTP
  200. El acceso de recuperación respondió HTTP 200 sin imprimir sus credenciales.
- Falta únicamente que el propietario confirme en el navegador su contraseña elegida
  para `criz110`; el sistema sólo conserva el hash y no puede reconstruirla.

## Decisiones — aislamiento multiinstitución y multiplantel

- El principal de Spring Security conserva el identificador del usuario, su institución,
  la presencia de alcance institucional y los planteles de sus asignaciones activas.
- Los diez listados y sus exportaciones Excel agregan en PostgreSQL la especificación de
  alcance, además de los filtros de búsqueda y estado existentes.
- Formularios, selectores, altas, ediciones, cambios de estado y desactivaciones validan
  el alcance en servidor. Alterar manualmente un ID o una relación no permite cruzar de
  institución o usar un plantel no asignado.
- Un alcance `PLANTEL` puede consultar catálogos académicos compartidos de su institución,
  pero planteles, oferta y grupos quedan limitados a sus planteles. `VINCULOS_TUTOR` no
  habilita módulos administrativos.
- Administrar usuarios y roles requiere alcance institucional. Crear otra institución
  queda reservado al acceso de recuperación para evitar escalamiento entre inquilinos.
- Se agregó una pantalla 403 propia, accesible, responsiva y compatible con tema claro
  y oscuro para permisos o alcances insuficientes.

## Verificación del aislamiento de datos

- Compilación Docker correcta de 142 archivos Java de producción.
- 55 pruebas ejecutadas sin fallos ni errores; cinco cubren aislamiento institucional,
  planteles permitidos, acceso directo denegado, tutores y recuperación global.
- La aplicación inició con PostgreSQL saludable, Flyway V3 vigente y el proveedor de
  usuarios persistidos activo. Salud, recuperación y la pantalla 403 respondieron.
- La base de datos no fue recreada ni se modificaron los datos existentes.

## Decisiones — actor de auditoría autenticado

- `EntidadAuditable` usa `@CreatedBy` y `@LastModifiedBy` además de las fechas que ya
  administraba Spring Data Auditing.
- `AuditorAware<Long>` obtiene el identificador directamente de `UsuarioPrincipal`, sin
  consultar de nuevo la base ni utilizar username como clave de auditoría.
- El acceso temporal de recuperación y una operación sin usuario persistido devuelven
  actor vacío. `ActorAuditoriaEntityListener` también limpia explícitamente el actor al
  persistir o actualizar para no conservar por error una atribución anterior.
- No fue necesaria una migración porque `creado_por_id` y `actualizado_por_id` ya
  existían como columnas anulables en todas las tablas auditables.

## Verificación del actor de auditoría

- Compilación Docker correcta de 143 archivos Java de producción.
- 59 pruebas ejecutadas sin fallos ni errores; cuatro cubren usuario persistido,
  recuperación, sesión anónima y limpieza de un actor anterior.
- La aplicación inició correctamente, validó Flyway V3 y salud respondió HTTP 200.
- No se generaron registros artificiales ni se modificaron datos para la verificación.

## Decisiones — intentos fallidos y bloqueo temporal

- Eventos de éxito y credenciales incorrectas de Spring Security delegan el registro a
  un servicio transaccional; no se modificó el formulario ni su mensaje uniforme.
- Un acceso correcto de usuario persistido actualiza `ultimo_acceso_en`, limpia
  `intentos_fallidos` y retira un bloqueo temporal vencido.
- Cada contraseña incorrecta incrementa el contador bajo bloqueo pesimista. El quinto
  fallo cambia el estado a `BLOQUEADO` y fija `bloqueo_hasta` a 15 minutos.
- Un bloqueo con `bloqueo_hasta = null` es administrativo y no vence automáticamente.
  Tras vencer un bloqueo temporal, un fallo nuevo comienza otra vez desde uno.
- No se modifican cuentas desconocidas, usernames ambiguos, invitados, inactivos ni el
  usuario de recuperación. Esto evita filtrar la existencia o estado de una cuenta.

## Verificación de protección de acceso

- Compilación Docker correcta de 146 archivos Java de producción.
- 65 pruebas ejecutadas sin fallos ni errores; seis nuevas cubren éxito, quinto fallo,
  expiración, bloqueo administrativo, usuario desconocido y autenticación tras vencer.
- La aplicación inició correctamente con PostgreSQL saludable, Flyway V3 vigente y el
  proveedor `usuarioSistemaDetailsService` activo.
- No se provocaron fallos deliberados sobre `criz110` ni se alteraron sus credenciales.

## Decisiones — recuperación segura de contraseña

- Flyway V4 agrega `RecuperacionPassword` como flujo independiente de
  `InvitacionUsuario`; no cambia una cuenta activa al estado `INVITADO`.
- Desde la edición de un usuario activo, la administración genera un enlace de 30
  minutos que se muestra una sola vez. Emitir otro revoca cualquier enlace pendiente.
- El token aleatorio tiene 256 bits y la base conserva exclusivamente su SHA-256. La
  contraseña nueva se guarda con BCrypt y el token queda consumido en la misma
  transacción.
- Completar la recuperación limpia intentos fallidos y bloqueos automáticos temporales.
  Un bloqueo administrativo permanente nunca puede retirarse mediante el enlace.
- La pantalla pública `/restablecer-password` valida longitud y confirmación, conserva
  los errores dentro del formulario y funciona con los temas claro y oscuro.
- La entrega actual es administrada: el proyecto no tiene SMTP configurado y no se
  presupone que los correos capturados sean reales. No se envían contraseñas en texto
  plano.

## Verificación de recuperación de contraseña

- Compilación Docker correcta de 154 archivos Java de producción.
- 74 pruebas ejecutadas sin fallos ni errores; las nuevas cubren hash del token,
  revocación, consumo único, BCrypt, bloqueo temporal, bloqueo administrativo,
  validación del formulario y construcción del enlace administrativo.
- Flyway validó cuatro migraciones y aplicó V4 sobre el volumen existente sin eliminar
  datos. Hibernate validó el esquema y detectó 15 repositorios.
- La aplicación quedó `UP` en `http://localhost:8080` y la pantalla pública de
  restablecimiento respondió HTTP 200.
- No se generó un token real ni se modificaron credenciales de `criz110` durante las
  pruebas del despliegue.

## Decisiones — módulo de alumnos

- Flyway V5 agrega `Alumno` y los permisos técnicos `ALUMNO_LEER` y
  `ALUMNO_ADMINISTRAR` de forma aditiva, sin editar migraciones anteriores.
- El expediente pertenece a una institución y conserva identidad, matrícula, CURP
  opcional, nacimiento, ingreso, contacto, domicilio, observaciones y estado activo.
- Matrícula y CURP normalizadas son únicas por institución. Las fechas no pueden ser
  futuras y el ingreso no puede preceder al nacimiento.
- No se duplican plantel, grado o grupo actual; esos datos se derivarán de
  `Inscripcion` y `AsignacionGrupo` cuando se implementen.
- El listado filtra matrícula, nombres, apellidos, CURP y correo en PostgreSQL, pagina
  realmente y exporta los mismos resultados a XLSX por bloques.
- Alta, edición y desactivación lógica aplican alcance institucional, DTO de formulario,
  CSRF, auditoría, versión optimista y errores integrados en la misma pantalla.
- La interfaz agrega la sección Personas y conserva diseño responsivo y temas claro y
  oscuro. Los roles existentes no reciben permisos nuevos automáticamente.

## Verificación del módulo de alumnos

- Compilación Docker correcta de 163 archivos Java de producción.
- 86 pruebas ejecutadas sin fallos ni errores; once nuevas cubren normalización,
  duplicados, fechas, institución inmutable, desactivación, controlador y aislamiento.
- Flyway validó cinco migraciones y aplicó V5 sobre el volumen existente. Hibernate
  validó el esquema y detectó 16 repositorios.
- Listado, formulario y exportación respondieron HTTP 200; el archivo comenzó con firma
  XLSX válida y la aplicación quedó `UP` en `http://localhost:8080`.
- No se crearon alumnos de prueba. Posteriormente el propietario asignó los permisos
  correspondientes y confirmó que `criz110` ya puede ver el módulo Alumnos.
- Se corrigió la edición de fechas: Spring renderizaba `LocalDate` con formato regional
  y el navegador descartaba ese valor en controles `type=date`. Alumnos, ciclos y
  periodos ahora declaran ISO `yyyy-MM-dd`; se verificaron ambas fechas de un alumno
  existente sin modificarlo.

## Decisiones — cierre de sesión visible

- Los listados y todos los formularios administrativos reutilizan un control de sesión
  común con selector de tema y botón `Cerrar sesión`.
- El cierre usa exclusivamente `POST /logout` con el token CSRF agregado por Thymeleaf;
  no depende de navegar manualmente a una ruta GET.
- Spring invalida la sesión y redirige al inicio. Se comprobó que la cookie anterior ya
  no puede abrir `/admin` y termina nuevamente en el login.
- Las sesiones caducadas ahora redirigen al login con un aviso específico, incluso
  cuando el primer síntoma es un token CSRF vencido al guardar un formulario. Las
  consultas asíncronas de autocompletado y grados también detectan la redirección.
- Los errores de permisos continúan en la pantalla 403 y el cierre normal elimina
  `JSESSIONID` para evitar falsos positivos. Se verificaron solicitudes `GET` y `POST`
  con una sesión inválida: ambas respondieron 302 hacia `/login?sesionExpirada`; la
  suite completa ejecutó 155 pruebas sin fallos y la aplicación quedó `UP`.

## Decisiones — módulo de tutores

- Flyway V6 agrega `Tutor` y los permisos técnicos `TUTOR_LEER` y
  `TUTOR_ADMINISTRAR` de forma aditiva; los roles existentes no reciben privilegios
  nuevos automáticamente.
- El expediente de tutor pertenece a una institución y conserva identidad, teléfonos,
  correo, nacimiento opcional, domicilio, ocupación, contacto laboral y estado activo.
- Un tutor puede vincularse opcionalmente a un `Usuario` activo o invitado de su misma
  institución. Cada cuenta sólo puede representar a un tutor.
- El listado busca por nombre, apellidos, teléfono y correo directamente en PostgreSQL,
  pagina realmente y exporta los mismos filtros a XLSX por bloques.
- Alta, edición y desactivación lógica aplican alcance institucional, DTO de formulario,
  CSRF, auditoría, versión optimista y errores integrados en la misma pantalla.
- La creación automática de una cuenta invitada se pospuso hasta `AlumnoTutor`, donde
  podrá decidirse si el tutor tendrá acceso a alumnos concretos mediante
  `VINCULOS_TUTOR`.

## Verificación del módulo de tutores

- Compilación Docker correcta de 172 archivos Java de producción.
- 99 pruebas ejecutadas sin fallos ni errores; trece nuevas cubren dominio, cuenta
  opcional, aislamiento, controlador, fechas y desactivación lógica.
- Flyway validó seis migraciones y aplicó V6 sobre el volumen existente. Hibernate
  validó el esquema y detectó 17 repositorios.
- El listado y el formulario respondieron autenticados; la exportación comenzó con la
  firma XLSX `504b0304` y la aplicación quedó disponible en `http://localhost:8080`.
- La tabla `tutor` permaneció vacía durante esta verificación. Posteriormente el
  propietario asignó ambos permisos y confirmó que `criz110` ya ve Tutores.
- El siguiente módulo acordado es `AlumnoTutor`; después se agregarán archivos y
  fotografía al historial del alumno, y posteriormente las inscripciones.

## Decisiones — vínculos entre alumnos y tutores

- Flyway V7 agrega `AlumnoTutor` y los permisos `VINCULO_TUTOR_LEER` y
  `VINCULO_TUTOR_ADMINISTRAR` sin modificar migraciones anteriores ni ampliar roles de
  forma automática.
- La relación conserva parentesco, contacto principal, responsabilidad financiera,
  autorización de trámites y recogida, consulta financiera, notificaciones,
  observaciones, vigencia y estado activo.
- Alumno y tutor deben pertenecer a la misma institución y estar activos para mantener
  un vínculo activo. No pueden reasignarse al editar un registro histórico.
- No se permiten periodos superpuestos para la misma pareja ni dos contactos
  principales con vigencias superpuestas. La validación bloquea pesimistamente al
  alumno para evitar altas concurrentes incompatibles.
- Revocar desactiva la relación sin eliminarla. Una cuenta con alcance
  `VINCULOS_TUTOR` sólo puede consultar sus propios vínculos activos y vigentes; no
  recibe acceso administrativo ni conserva acceso después de la revocación o vigencia.
- El listado busca por matrícula y nombres de alumno o tutor en PostgreSQL, pagina los
  resultados y exporta los mismos filtros a XLSX por bloques.
- El formulario busca alumnos y tutores mediante autocompletado remoto por institución,
  bloquea ambas personas en edición y mantiene reglas, integridad y concurrencia dentro
  de la misma pantalla.

## Verificación de vínculos entre alumnos y tutores

- Compilación Docker correcta de 182 archivos Java de producción.
- 116 pruebas ejecutadas sin fallos ni errores; diecisiete nuevas cubren dominio,
  solapamientos, contacto principal, revocación, controlador, fechas y alcance del
  tutor.
- Flyway validó siete migraciones y aplicó V7 sobre el volumen existente. Hibernate
  validó el esquema y detectó 18 repositorios.
- El listado con filtro relacional y el formulario respondieron autenticados; la
  exportación comenzó con firma XLSX `504b0304`.
- La tabla `alumno_tutor` permaneció vacía y se confirmaron los dos permisos nuevos; no
  se alteraron alumnos, tutores, usuarios ni roles existentes.
- El siguiente paso acordado es la base privada de archivos y la fotografía del alumno;
  después se implementarán inscripciones.

## Decisión posterior: autocompletado para catálogos de alto volumen

- Se corrigió el formulario de vínculos para que alumno y tutor se seleccionen mediante
  búsqueda remota, y el formulario de tutor usa el mismo componente para la cuenta de
  usuario opcional. Ninguno carga ya las tablas completas al abrirse.
- La búsqueda comienza con tres caracteres, espera 280 ms mientras se escribe, cancela
  solicitudes anteriores y devuelve como máximo 20 elementos sin ejecutar un conteo
  total. Incluye teclado, estados ARIA, botón para limpiar y texto insertado de forma
  segura.
- Los endpoints aplican los permisos y el alcance institucional del servidor. Al buscar
  una cuenta para tutor también se excluyen cuentas asignadas a otro tutor.
- Flyway V8 habilita `pg_trgm`, incorpora columnas generadas normalizadas y crea índices
  GIN para alumnos, tutores y usuarios. La regla del proyecto es utilizar este patrón en
  toda relación futura con un catálogo potencialmente masivo.
- Docker compiló 186 fuentes de producción y ejecutó 120 pruebas sin fallos. Flyway
  avanzó a V8, se confirmaron los tres índices y los tres endpoints devolvieron JSON
  HTTP 200. La aplicación quedó disponible en `http://localhost:8080`.
- La continuidad funcional no cambia: sigue la base privada de archivos y fotografía
  del alumno; posteriormente se implementarán inscripciones.

## Decisiones — archivos privados y fotografía del alumno

- Flyway V9 crea `Archivo`, agrega `fotografia_archivo_id` a `Alumno` y registra cada
  asignación en `AlumnoFotografia`. Una fotografía reemplazada o retirada deja de ser
  la actual, pero su relación histórica y su contenido no se eliminan.
- PostgreSQL guarda institución, clave aleatoria, nombre original saneado, MIME,
  tamaño, SHA-256, estado y auditoría; nunca guarda los bytes del archivo.
- El contenido vive fuera de los recursos web en el volumen Docker privado
  `private_files`. Las claves son generadas por el servidor y el almacenamiento bloquea
  cualquier ruta que intente salir de su raíz.
- La etapa inicial admite únicamente JPEG y PNG de hasta 5 MB y 25 millones de píxeles.
  Se validan firma binaria, lector real, dimensiones y checksum, sin confiar en la
  extensión ni en el MIME enviado por el navegador.
- Cargar, reemplazar y retirar exige `ALUMNO_ADMINISTRAR` y alcance institucional. La
  visualización exige `ALUMNO_LEER` o `ALUMNO_ADMINISTRAR`, vuelve a comprobar el
  alcance por alumno, usa `no-store` y nunca revela una ruta física.
- La edición del alumno incluye vista previa, estado vacío, carga accesible, retiro e
  historial privado. Los errores de archivo, tipo, tamaño o persistencia permanecen en
  el mismo formulario.

## Verificación de archivos y fotografía

- Docker compiló 197 fuentes Java de producción y ejecutó 130 pruebas sin fallos ni
  errores. Diez pruebas nuevas cubren imagen válida, contenido falso, tamaño, historial,
  retiro, bloqueo, rutas privadas y errores integrados en el controlador.
- Flyway aplicó V9 sobre el volumen existente, Hibernate validó el esquema y detectó 20
  repositorios. Las tablas `archivo` y `alumno_fotografia` quedaron disponibles.
- El volumen `private_files` se creó con permisos para el usuario no privilegiado de la
  aplicación. El formulario autenticado mostró el panel multipart y Actuator respondió
  `UP` en `http://localhost:8080`.
- No se cargaron archivos reales ni se modificaron alumnos durante la verificación.
- El siguiente paso acordado es `Inscripcion` y `AsignacionGrupo`; todavía no se deben
  implementar cobros ni tesorería.

## Mejora visual posterior — ficha técnica del alumno

- La edición del alumno ahora abre con una ficha de perfil antes del formulario:
  fotografía destacada, nombre completo, matrícula, institución, estado, nacimiento,
  ingreso, CURP y contacto.
- Cargar, reemplazar, retirar y consultar el historial fotográfico quedó integrado en
  la cabecera. El diseño responde en móvil y conserva los temas claro y oscuro.
- Docker compiló las 197 fuentes de producción y ejecutó 131 pruebas sin fallos ni
  errores. La página autenticada confirmó que la ficha se renderiza antes del
  formulario multipart.
- Esta mejora no altera el dominio ni el siguiente paso: continúan `Inscripcion` y
  `AsignacionGrupo`.

## Decisión operativa pendiente — respaldo de archivos privados

- En desarrollo se conserva el volumen Docker `private_files`; es persistente respecto
  del contenedor, pero no sustituye un respaldo y no viaja mediante Git.
- Para recuperar o trasladar una instalación deben respaldarse juntos PostgreSQL y
  `/data/nexo-escolar`, porque la base conserva metadatos y relaciones mientras el
  volumen contiene los bytes.
- Nunca usar `docker compose down -v` sin autorización y respaldo verificado. Los
  respaldos deben quedar fuera del repositorio y copiarse cifrados a otro equipo o
  servicio privado.
- Antes de producción se definirá automatización, retención, restauración probada y
  destino externo. Para una instancia se admite volumen respaldado; un bind mount debe
  apuntar a una ruta dedicada fuera del repositorio. Para varias instancias se evaluará
  almacenamiento de objetos S3/MinIO.

## Decisiones — inscripciones y asignaciones de grupo

- Flyway V10 crea `Inscripcion` y `AsignacionGrupo`, junto con los permisos
  `INSCRIPCION_LEER` e `INSCRIPCION_ADMINISTRAR`; no modifica roles existentes.
- La inscripción es la fuente de verdad de plantel, ciclo y grado. Un traslado o una
  promoción cierra la trayectoria anterior y crea otra enlazada; una nueva asignación
  de grupo cierra la anterior y conserva ambas.
- El servicio valida misma institución, relaciones activas, oferta del plantel, ciclo
  abierto, fechas dentro del ciclo, ausencia de solapamientos y transiciones de estado.
- La asignación de grupo exige coincidencia exacta de plantel, ciclo y grado, y protege
  la capacidad con bloqueo pesimista para evitar sobrecupo concurrente.
- La consola ofrece alta, edición, cambio/finalización de grupo y continuidad académica,
  con autocompletado remoto de alumnos, alcance institucional o por plantel, temas y
  diseño responsivo.
- El listado filtra y pagina en PostgreSQL. La exportación Apache POI aplica el mismo
  filtro y procesa los resultados por bloques, sin cargar toda la tabla en memoria.

## Verificación de inscripciones y asignaciones

- Docker compiló 213 fuentes Java de producción y ejecutó 138 pruebas sin fallos ni
  errores. Las pruebas del servicio cubren creación, aislamiento, continuidad,
  capacidad, cambio de grupo y estados terminales; el alcance por plantel también se
  verifica de forma aislada.
- Flyway aplicó V10 sobre el volumen existente, Hibernate validó el esquema y detectó
  22 repositorios. Actuator respondió `UP` en `http://localhost:18080`.
- El listado, el formulario y una exportación filtrada respondieron autenticados; el
  archivo comenzó con firma XLSX `504b0304`. No se crearon inscripciones ni
  asignaciones reales durante la verificación.
- El siguiente paso es acordar meses cobrables, verano, vencimientos y actualización
  de cuotas; después iniciar Flyway V11 con `ConceptoCobro` y `CuotaAlumno`. Pagos, caja
  y tesorería permanecen fuera de esta etapa.

## Política confirmada — control individual y pagos familiares

- Toda cuota y todo cargo pertenecen a una inscripción y, por consecuencia, a un solo
  alumno. No existe un saldo familiar que oculte cuánto corresponde a cada hijo.
- Un tutor podrá realizar en una etapa futura un único pago para varios hijos. Ese pago
  conservará un solo ingreso, pero el administrador distribuirá el importe mediante
  aplicaciones independientes a los cargos de cada alumno.
- El administrador determina importe, periodo y vencimiento. Una cuota mensual usa un
  día de vencimiento de 1 a 31 y en meses más cortos se ajustará al último día; una cuota
  única usa una fecha exacta.
- Las cuotas pueden configurarse con generación manual o automática. Los conceptos de
  servicio, material u otro permiten modelar cobros extraordinarios sin mezclarlos con
  la colegiatura.
- Cambiar una cuota sólo afectará emisiones futuras. Los cargos ya emitidos conservarán
  su descripción, importe y vencimiento históricos.

## Decisiones — conceptos de cobro y cuotas por alumno

- Flyway V11 crea `ConceptoCobro` y `CuotaAlumno` de forma aditiva y agrega cuatro
  permisos técnicos sin concederlos a roles existentes.
- Un concepto pertenece a la institución, tiene código único, categoría y reglas para
  permitir beca, descuento o recargo; no guarda un precio común para todos los alumnos.
- La cuota guarda el importe individual, moneda, frecuencia `UNICA` o `MENSUAL`, rango,
  vencimiento, estado y `generacionAutomatica`. No pueden existir cuotas activas
  superpuestas para el mismo concepto e inscripción.
- Sólo conceptos e inscripciones vigentes admiten cuotas activas. Una cuota suspendida
  o finalizada no puede generar automáticamente y una finalizada queda inmutable.
- Conceptos se administran con alcance institucional. Cuotas respetan el plantel de la
  inscripción. Las búsquedas de inscripción y concepto usan autocompletado remoto,
  resultados acotados e índices GIN `pg_trgm`.
- Ambos módulos incluyen formularios responsivos con temas claro/oscuro, filtros y
  paginación en PostgreSQL y Excel por bloques reutilizando los mismos filtros.

## Verificación de conceptos y cuotas

- Docker compiló 234 fuentes Java de producción y ejecutó 151 pruebas sin fallos ni
  errores. Trece verificaciones adicionales cubren conceptos, cuotas individuales,
  vencimientos, solapamientos, estados, automatización, alcance y autocompletados.
- Flyway aplicó V11 sobre el volumen existente, Hibernate validó el esquema y detectó
  24 repositorios. Actuator respondió `UP`.
- Listados, formularios y exportación filtrada respondieron autenticados; el XLSX empezó
  con `504b0304`. Las tablas `concepto_cobro` y `cuota_alumno` quedaron vacías y el
  catálogo alcanzó 30 permisos técnicos; no se asignaron permisos a roles.
- El siguiente paso era Flyway V12 con `Cargo`, pero una mejora solicitada de identidad
  de sesión ocupó esa versión de forma aditiva. `Cargo` continúa ahora en Flyway V13,
  con alta manual y generación automática
  idempotente desde cuotas. Pagos, aplicaciones, caja y tesorería siguen fuera de esta
  etapa.

## Decisiones — identidad visible y fotografía de usuario

- Flyway V12 agrega una referencia opcional desde `Usuario` hacia `Archivo`; no altera
  roles, permisos ni credenciales.
- La administración de usuarios permite cargar o reemplazar JPEG/PNG de hasta 5 MB y
  volver al avatar genérico. Se valida firma, formato, dimensiones y SHA-256 antes de
  relacionar el archivo; la fotografía sustituida se marca como retirada sin eliminar
  físicamente el archivo privado.
- El encabezado compartido muestra el nombre de usuario autenticado y obtiene su foto
  actual mediante una ruta autenticada sin caché. La cuenta temporal de recuperación y
  los usuarios sin fotografía reciben siempre el avatar genérico.
- El diseño funciona en temas claro/oscuro y, en móvil, distribuye las acciones en un
  segundo renglón para mantener visible el nombre de usuario.

## Verificación de identidad de sesión

- Docker compiló 239 fuentes Java y ejecutó 167 pruebas sin fallos ni errores. Ocho
  pruebas nuevas cubren validación real de imagen, reemplazo, retiro, almacenamiento,
  integración del controlador, entrega del avatar genérico y publicación segura del
  nombre de sesión, incluso cuando no existe autenticación.
- Flyway validó doce migraciones y aplicó V12 sobre el volumen existente; Hibernate
  validó el esquema y la aplicación inició correctamente en `http://localhost:18080`.
- La revisión pública confirmó el arranque y el flujo de sesión caducada. No se usaron
  credenciales del `.env`, no se cargaron fotografías y no se modificaron usuarios.
- Una revisión posterior al login detectó que el dialecto Thymeleaf no proporcionaba
  `#authentication`; se sustituyó por `IdentidadSesionAdvice`, evitando el error de
  renderizado sin agregar otra dependencia.

## Decisiones — cargos individuales y generación idempotente

- Flyway V13 crea `Cargo` y agrega `CARGO_LEER` y `CARGO_ADMINISTRAR` sin modificar
  roles existentes. Cada obligación pertenece a una inscripción y, por consecuencia,
  conserva el alumno y plantel al que corresponde.
- El alta manual permite registrar cobros extraordinarios con concepto, descripción
  histórica, periodo, emisión, vencimiento, importe y periodo de evaluación opcional.
- El generador consulta por bloques de 100 únicamente cuotas activas, automáticas, con
  concepto activo e inscripción vigente. Las cuotas mensuales se convierten en un cargo
  por mes y el día 29, 30 o 31 se recorta al último día del mes cuando corresponde.
- La clave `AUTO:<institución>:<cuota>:<periodo>` tiene restricción única y se inserta
  mediante `ON CONFLICT DO NOTHING`; repetir o ejecutar concurrentemente el proceso no
  duplica cargos. Una cuota única se emite una sola vez.
- Los cargos emitidos son inmutables. Sólo pueden cancelarse con bloqueo, versión
  optimista y motivo obligatorio; la fila y sus importes históricos permanecen.
- El catálogo filtra y pagina en PostgreSQL, aplica alcance institucional o por plantel
  y exporta con Apache POI exactamente el mismo filtro en bloques. Las pantallas de alta,
  generación y detalle/cancelación son responsivas y compatibles con tema claro/oscuro.
- El autocompletado opcional de periodo de evaluación se acota por la inscripción y usa un
  índice GIN `pg_trgm`; no carga todos los periodos en el formulario.

## Verificación de cargos

- Docker compiló 253 fuentes Java y ejecutó 175 pruebas sin fallos ni errores. Las
  pruebas nuevas cubren meses cortos, separación por inscripción, idempotencia,
  aislamiento institucional y cancelación histórica.
- Flyway validó trece migraciones y aplicó V13 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 25 repositorios y la aplicación inició correctamente.
- Actuator respondió `UP` en `http://localhost:18080`. No se generaron cargos reales ni
  se modificaron cuotas existentes durante la verificación.
- Se corrigió el enlace del formulario de cuota única: el DTO descarta siempre el día
  mensual cuando la frecuencia es `UNICA`, y descarta la fecha exacta cuando es
  `MENSUAL`. Esto evita que un valor visual predeterminado produzca una validación falsa.
- La validación de vigencia de cuota informa ahora cuál fecha quedó fuera y muestra el
  rango exacto permitido por la intersección entre inscripción y ciclo escolar.
- El expediente de inscripción incorpora una ficha visual del alumno con fotografía
  actual o iniciales de respaldo, matrícula, nacimiento, CURP, plantel, ciclo y grado.
  La descarga de la foto valida primero el acceso a la inscripción y usa `no-store`.
- El siguiente paso es `TipoBeca`, `BecaAlumno` y `AjusteCargo` en Flyway V14. Pagos,
  aplicaciones, caja y tesorería continúan fuera de esta etapa.

## Decisiones — becas individuales y ajustes históricos

- Flyway V14 crea `TipoBeca`, `BecaAlumno` y `AjusteCargo`, junto con seis permisos
  técnicos que no se conceden automáticamente a roles existentes.
- Una beca se asigna a una inscripción y a un solo concepto. Puede ser porcentaje o
  monto fijo, tiene vigencia y estado, y no puede solaparse con otra beca activa para
  el mismo alumno y concepto.
- Al emitir un cargo manual o automático se localiza la beca vigente y se registra un
  ajuste separado con base, porcentaje y monto congelados. El porcentaje se redondea
  `HALF_UP` a dos decimales y el monto fijo nunca reduce el cargo por debajo de cero.
- Modificar, suspender o finalizar una beca sólo afecta cargos futuros. Un concepto no
  puede retirar la autorización de becas mientras conserve asignaciones activas.
- Los ajustes manuales admiten descuento, recargo y corrección según la política del
  concepto. Son inmutables y se corrigen creando una reversa igual y de efecto opuesto.
- Los tres módulos filtran y paginan en PostgreSQL, exportan XLSX por bloques con los
  mismos filtros, respetan alcance institucional/plantel y conservan temas claro/oscuro.

## Verificación de becas y ajustes

- Docker compiló 285 fuentes Java y ejecutó 181 pruebas sin fallos ni errores. Las seis
  pruebas nuevas cubren redondeo, monto fijo, idempotencia, límites, política del
  concepto y reversas históricas.
- Flyway validó catorce migraciones y aplicó V14 sobre PostgreSQL 17. Hibernate validó
  el esquema y detectó 28 repositorios; Actuator respondió `UP`.
- No se crearon becas, cargos ni ajustes reales durante la verificación.
- El siguiente paso requiere confirmar porcentaje o monto, días de gracia, periodicidad
  y límite de los recargos automáticos. Hasta entonces sólo se permiten manualmente.

## Decisiones — políticas de recargo automático

- Flyway V15 crea `PoliticaRecargo`, agrega su referencia y una clave idempotente a
  `AjusteCargo`, y suma `POLITICA_RECARGO_LEER` y
  `POLITICA_RECARGO_ADMINISTRAR` sin concederlos a roles existentes.
- Cada concepto admite una política configurable: porcentaje o monto fijo, de 0 a 365
  días completos de gracia, aplicación única o mensual y límite opcional por monto o
  porcentaje del importe original.
- El primer recargo se genera después de la fecha del cargo y de todos los días de
  gracia. En recurrencia mensual se conserva el día equivalente y se ajusta al final
  de los meses cortos.
- El cálculo es simple y no capitaliza recargos. Usa el importe original ajustado por
  becas, descuentos y correcciones ajenas a mora; si esa base queda en cero, no genera.
- Cada periodo usa la clave `RECARGO:<política>:<cargo>:<periodo>` y se inserta con
  `ON CONFLICT DO NOTHING`. Una reversa reduce el acumulado usado para el límite, pero
  no habilita el cobro duplicado del mismo periodo.
- La consola incluye alta, edición y desactivación de políticas, generación por fecha de
  corte e institución/plantel, listado filtrado y paginado y Excel por bloques con los
  mismos filtros. El concepto no puede desactivarse ni dejar de permitir recargos si
  conserva una política activa.

## Verificación de recargos automáticos

- Docker compiló 299 fuentes Java y ejecutó 185 pruebas sin fallos ni errores. Las
  pruebas nuevas cubren porcentaje, base descontada, días de gracia, meses cortos,
  tope prorrateado, idempotencia y saldo base cero.
- Flyway validó quince migraciones y aplicó V15 sobre PostgreSQL 17. Hibernate validó
  el esquema y las consultas, detectó 29 repositorios y Actuator respondió `UP`.
- PostgreSQL confirmó 40 permisos técnicos. `politica_recargo` y los ajustes vinculados
  a políticas permanecieron vacíos; no se modificaron datos operativos.
- El siguiente paso es acordar e implementar primero `CuentaFinanciera` y después la
  recepción, validación y aplicación de pagos. Un pago familiar podrá distribuirse entre
  varios hijos, pero cada aplicación quedará ligada al cargo y alumno correspondiente.

## Decisiones — cuentas financieras para recepción de pagos

- Flyway V16 crea `CuentaFinanciera` y agrega `CUENTA_FINANCIERA_LEER` y
  `CUENTA_FINANCIERA_ADMINISTRAR` sin ampliar roles existentes.
- Una cuenta pertenece a una institución y opcionalmente a un plantel; sin plantel es
  compartida y sólo puede administrarse con alcance institucional. Los usuarios de
  plantel administran únicamente cuentas de sus planteles autorizados.
- Los tipos iniciales son caja, banco e inversión. Caja no conserva banco, número ni
  CLABE; banco e inversión requieren nombre de la institución financiera y al menos un
  identificador. La moneda coincide con la predeterminada de la institución.
- El saldo inicial es no negativo y su fecha no puede ser futura. Podrá editarse mientras
  todavía no haya movimientos; una etapa posterior deberá bloquear moneda y apertura
  después del primer movimiento y realizar correcciones mediante movimientos trazables.
- Número de cuenta y CLABE completos sólo se muestran en el formulario administrativo.
  El listado y el Excel reutilizan los mismos filtros paginados y muestran únicamente
  los cuatro últimos caracteres.
- La pantalla es responsiva, compatible con tema claro/oscuro, filtra planteles por
  institución y oculta los campos bancarios al seleccionar caja.

## Verificación de cuentas financieras

- Docker compiló 309 fuentes Java y ejecutó 199 pruebas sin fallos ni errores. Las
  pruebas nuevas cubren normalización, duplicados, plantel ajeno, reglas de caja/banco,
  moneda, fecha, desactivación, formulario y alcance institucional/plantel.
- Flyway validó dieciséis migraciones y aplicó V16 sobre PostgreSQL 17. Hibernate validó
  el esquema, detectó 30 repositorios y Actuator respondió `UP`.
- PostgreSQL confirmó 42 permisos técnicos y cero cuentas financieras. No se crearon ni
  modificaron datos operativos durante la verificación.
- El navegador confirmó que la ruta administrativa exige sesión y conserva el aviso de
  sesión caducada; no se utilizaron credenciales para crear registros de prueba.
- El siguiente paso es V17 con registro de `Pago`, comprobantes privados y solicitudes
  de distribución. Permanecerán pendientes la validación, las aplicaciones que afectan
  saldos y la generación del movimiento financiero único por pago.

## Decisiones — pagos pendientes, comprobantes y distribución solicitada

- Flyway V17 crea `Pago`, `ComprobantePago` y `SolicitudAplicacionPago`, además de los
  permisos `PAGO_LEER` y `PAGO_REGISTRAR` sin modificar los roles existentes.
- El alta registra efectivo o transferencia exclusivamente como `PENDIENTE_VALIDACION`.
  Folio e idempotencia son únicos por institución; fecha futura, moneda ajena, tutor,
  plantel o cuenta incompatibles se rechazan antes de persistir.
- Una transferencia requiere al menos un comprobante. Se aceptan como máximo cinco
  archivos JPEG, PNG o PDF de 10 MB cada uno, comprobando su firma binaria, nombre,
  tamaño y SHA-256. Los bytes viven en `private_files` y la descarga autorizada usa
  `no-store` y disposición de archivo adjunto.
- Efectivo puede declarar una cuenta CAJA y transferencia una cuenta BANCO o INVERSION;
  la cuenta es opcional hasta que la validación elija el destino real.
- Un pago puede proponer distribución para varios hijos del mismo tutor. Cada cargo se
  valida contra responsabilidad financiera vigente, institución, moneda, estado y saldo;
  la suma solicitada no supera el pago y el remanente permanece visible sin asignar.
- Ni el pago pendiente ni sus solicitudes modifican saldos o cargos. Todavía no existen
  `AplicacionPago`, movimientos, devoluciones, cortes ni retiros.
- El listado filtra y pagina en PostgreSQL y exporta exactamente los mismos filtros con
  Apache POI por bloques. El alta y detalle son responsivos, compatibles con ambos temas
  y usan búsquedas remotas acotadas para tutores, cuentas y cargos.

## Verificación de pagos pendientes

- Docker compiló 328 fuentes Java de producción y ejecutó 209 pruebas sin fallos ni
  errores. Diez pruebas nuevas cubren pago para varios hijos, remanente, comprobantes,
  firma falsa, autorización financiera, tipo de cuenta, formulario y redirección.
- Flyway validó diecisiete migraciones y aplicó V17 sobre PostgreSQL 17. Hibernate validó
  el esquema, detectó 33 repositorios y Actuator respondió `UP`.
- No se registraron pagos, comprobantes ni distribuciones operativas durante la
  verificación. La siguiente etapa es V18: validar o rechazar pagos, crear aplicaciones
  por cargo y publicar un solo movimiento de ingreso por pago validado, todo de manera
  atómica e idempotente.

## Decisiones — validación, aplicaciones e ingreso de pagos

- Flyway V18 crea `AplicacionPago`, `MotivoFinanciero` y `MovimientoFinanciero`, y suma
  `PAGO_VALIDAR` sin asignarlo automáticamente a roles existentes.
- Sólo una cuenta de usuario persistida puede decidir sobre dinero. El acceso de
  recuperación puede consultar, pero no validar ni rechazar pagos.
- Validar exige una cuenta activa, accesible para el plantel, con la moneda y tipo
  compatibles. El pago y la cuenta se bloquean antes de procesar los cargos en orden
  estable para proteger la secuencia y evitar aplicaciones concurrentes excesivas.
- Cada solicitud se vuelve a comprobar contra el saldo y la responsabilidad financiera
  vigentes. Si el saldo disminuyó, se aplica sólo el importe disponible; la diferencia y
  cualquier parte no solicitada permanecen como monto disponible del pago.
- Se publica exactamente un movimiento `COBRO/INGRESO` por el importe total del pago.
  La clave `COBRO:PAGO:<id>`, la relación única con pago y las aplicaciones ligadas a su
  solicitud protegen los reintentos. Toda la operación comparte una transacción.
- Rechazar exige motivo y no crea movimientos ni aplicaciones. Los comprobantes se
  conservan. Los cálculos y pantallas de Cargo ya muestran saldo y estado considerando
  aplicaciones; no se puede cancelar un cargo pagado ni modificar la apertura de una
  cuenta que ya tenga movimientos.
- El expediente de pago ofrece una consola responsiva de decisión, búsqueda remota de
  cuenta destino, distribución efectiva, remanente y resumen del movimiento, compatible
  con temas claro y oscuro.

## Verificación de validación de pagos

- Docker compiló 345 fuentes Java y ejecutó 216 pruebas sin fallos ni errores.
- Flyway validó dieciocho migraciones y aplicó V18 sobre PostgreSQL 17. Hibernate validó
  el esquema y detectó 36 repositorios; Actuator respondió `UP`.
- PostgreSQL confirmó 45 permisos técnicos. Las tablas de pagos, aplicaciones y
  movimientos permanecieron vacías; no se crearon ni modificaron datos operativos.
- El siguiente paso es V19 con libro paginado de movimientos y saldo actual por cuenta,
  filtros y Excel equivalente. Operaciones manuales, traspasos, devoluciones y reversos
  siguen fuera de esta entrega.

## Decisiones — libro financiero y operaciones manuales

- Flyway V19 incorpora el permiso `MOVIMIENTO_FINANCIERO_LEER`, índices para las
  consultas frecuentes y un libro inmutable con paginación PostgreSQL. La vista filtra
  por cuenta, plantel, dirección, clase y fechas, calcula totales de página, muestra el
  saldo vigente de una cuenta seleccionada y exporta los mismos filtros por bloques.
- Flyway V20 incorpora `MOTIVO_FINANCIERO_LEER`,
  `MOTIVO_FINANCIERO_ADMINISTRAR` y `MOVIMIENTO_FINANCIERO_REGISTRAR`; ningún permiso
  nuevo se asigna automáticamente. También siembra motivos comunes por institución.
- El catálogo de motivos admite naturaleza `INGRESO`, `EGRESO` o `AMBOS`. El motivo
  técnico `COBROS_ESCOLARES` permanece reservado y activo para no romper la publicación
  de pagos validados.
- Una operación manual exige cuenta activa por autocompletado, motivo activo compatible,
  fecha no futura ni anterior a la apertura, institución/plantel autorizados y usuario
  persistido. El acceso de recuperación no puede registrar dinero.
- El servicio bloquea la cuenta, obtiene la última secuencia, calcula saldos y confirma
  con idempotencia. No admite saldo negativo. Los movimientos `OPERACION` no se editan
  ni eliminan; una corrección futura utilizará un movimiento reverso relacionado.

## Verificación de V19 y V20

- Docker compiló 364 fuentes Java y ejecutó 223 pruebas sin fallos ni errores. Las siete
  pruebas nuevas cubren catálogo reservado, normalización, saldo secuencial, fondos
  insuficientes, naturaleza incompatible y bloqueo del acceso de recuperación.
- Flyway validó veinte migraciones y aplicó V20 sobre PostgreSQL 17. Hibernate validó
  el esquema, detectó 36 repositorios y `/actuator/health` respondió `UP`.
- No se creó ningún movimiento monetario durante la verificación automática.
- El propietario debe conceder los permisos de V20 y probar la interfaz. Después, el
  siguiente paso es V21 con transferencias atómicas entre cuentas; devoluciones,
  reversas generales, cortes y retiros permanecen fuera de alcance.

## Decisiones — transferencias entre cuentas

- Flyway V21 crea `TransferenciaCuenta`, agrega `transferencia_id` al libro financiero,
  protege con unicidad los lados ingreso/egreso y suma el permiso
  `TRANSFERENCIA_CUENTA_REGISTRAR` sin modificar roles existentes.
- Una transferencia sólo admite cuentas distintas, activas, de la misma institución y
  moneda. Debe respetar ambas fechas de apertura y no puede dejar negativo el origen.
- Las cuentas se buscan mediante autocompletado. El servicio las bloquea siempre por ID
  ascendente para evitar interbloqueos y valida el alcance institucional o de plantel.
- La entidad principal, el egreso de origen y el ingreso de destino se publican en una
  única transacción. Cada movimiento conserva su propia secuencia, saldos y una clave
  derivada de la transferencia; la operación principal también es idempotente.
- `TRASPASO_INTERNO` es un motivo técnico reservado con naturaleza `AMBOS`. El traspaso
  no representa ingreso ni gasto operativo y una comisión bancaria se registra aparte.
- V21 sólo registra transferencias aplicadas. El estado `REVERTIDA` prepara una futura
  reversa conjunta, pero no existe acción de reversión en esta entrega.

## Verificación de transferencias

- Docker compiló 373 fuentes Java y ejecutó 229 pruebas sin fallos ni errores. Las seis
  pruebas añadidas cubren ambos movimientos, saldos, bloqueo ascendente, fondos,
  monedas, cuenta repetida, acceso de recuperación y motivo técnico reservado.
- Flyway validó 21 migraciones y aplicó V21 sobre PostgreSQL 17. Hibernate validó el
  esquema y detectó 37 repositorios. No se generaron transferencias operativas.
- El propietario debe asignar `TRANSFERENCIA_CUENTA_REGISTRAR`, volver a iniciar sesión
  y probar con datos controlados. La siguiente etapa recomendada es V22 con devoluciones
  de pagos; las reversas generales quedan separadas.

## Decisiones — devoluciones de pagos

- Flyway V22 crea `DevolucionPago`, relaciona los ajustes de `AplicacionPago` y agrega
  un vínculo único desde `MovimientoFinanciero`. También crea `PAGO_DEVOLVER` y el motivo
  técnico reservado `DEVOLUCION_PAGO`, sin ampliar roles existentes.
- Sólo se devuelve un pago `VALIDADO`. El pago conserva ese estado porque la devolución
  representa salida real de dinero, no cancelación. Fecha, cuenta, moneda, alcance,
  fondos, monto acumulado, versión e idempotencia se validan dentro de la transacción.
- El disponible es monto del pago menos aplicaciones netas menos devoluciones ejecutadas.
  Si resulta insuficiente, el usuario selecciona aplicaciones vigentes. Para mantener
  historial inmutable, una aplicación se revierte completa y se reaplica su remanente
  cuando la liberación requerida es parcial.
- La entidad de devolución, los ajustes de cargos y exactamente un movimiento
  `DEVOLUCION/EGRESO` se publican juntos. El formulario permanece en el expediente ante
  errores y usa autocompletado acotado para la cuenta de origen.
- El expediente de pago muestra aplicado, devuelto, disponible e historial. El acceso
  de recuperación no puede devolver fondos y se requiere una cuenta de usuario persistida.

## Verificación de devoluciones

- Docker compiló 382 fuentes Java y ejecutó 235 pruebas sin fallos ni errores. Las seis
  pruebas añadidas cubren egreso único, devolución parcial aplicada, selección
  insuficiente, límite acumulado, fondos de cuenta y motivo técnico reservado.
- Flyway validó 22 migraciones y aplicó V22 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 38 repositorios y `/actuator/health` respondió `UP`.
- No se ejecutaron devoluciones ni otros movimientos monetarios durante la verificación.
- El propietario debe asignar `PAGO_DEVOLVER`, iniciar una sesión nueva y probar con
  datos controlados. Después, la etapa recomendada es V23 para reversas trazables de
  operaciones manuales y transferencias; cancelaciones, cortes y retiros quedan separados.

## Decisiones — reversas financieras

- Flyway V23 crea `ReversionFinanciera`, agrega la relación desde los movimientos
  compensatorios y suma `MOVIMIENTO_FINANCIERO_REVERTIR` sin modificar roles existentes.
- El flujo general sólo acepta `OPERACION` manual y transferencias `APLICADA`. Cobros,
  devoluciones y aplicaciones de pago mantienen sus procesos propios; ningún original
  se edita o elimina y una restricción impide una segunda reversa.
- La reversa manual publica un movimiento `REVERSO` opuesto con el mismo importe, motivo
  financiero, alcance y referencia. Deshacer un ingreso requiere saldo actual suficiente.
- La reversa de transferencia bloquea ambas cuentas por ID ascendente y publica de forma
  atómica el retorno al origen y la salida del destino. La cuenta receptora debe conservar
  el importe y la transferencia cambia a `REVERTIDA` en la misma transacción.
- La cabecera conserva objetivo, fecha, motivo, actor e idempotencia. Se validan cuenta
  activa, alcance, fecha original, aperturas y actor persistido; recuperación no autoriza.
- El libro identifica movimientos revertidos y reversos compensatorios, y presenta la
  acción sólo cuando corresponde. Los errores permanecen en el formulario.

## Verificación de reversas financieras

- Docker compiló 392 fuentes Java y ejecutó 242 pruebas sin fallos ni errores. Las siete
  pruebas nuevas cubren reversa simple, pareja inversa, saldos, clases no admitidas,
  fondos gastados en la receptora, acceso de recuperación y reintento idempotente.
- Flyway validó 23 migraciones y aplicó V23 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 39 repositorios y `/actuator/health` respondió `UP`.
- PostgreSQL confirmó cero cabeceras y cero movimientos de reversa; no se movió dinero
  real durante la verificación automática.
- El propietario debe asignar `MOVIMIENTO_FINANCIERO_REVERTIR`, iniciar una sesión nueva
  y probar con datos controlados. Después se recomienda V24 para cortes de caja y
  conciliación; cancelaciones de pagos y retiros especializados quedan separados.

## Decisiones — cortes de caja

- Flyway V24 crea `CorteCaja` y agrega `CORTE_CAJA_LEER` y
  `CORTE_CAJA_ADMINISTRAR` sin modificar roles existentes. El flujo aplica únicamente a
  cuentas `CAJA`; no incluye bancos, inversiones, cancelaciones de pagos ni retiros.
- La apertura captura el momento del servidor, actor persistido, saldo vigente y último
  folio de la cuenta. Existe un solo corte abierto por caja y claves institucionales de
  idempotencia distintas para apertura y cierre.
- El cierre bloquea la cuenta y el corte, resume por secuencia los movimientos posteriores
  a la apertura y conserva folio final, cantidad, ingresos, egresos y saldo esperado. La
  secuencia evita perder movimientos capturados tarde con una fecha operativa anterior.
- El efectivo se declara mediante conteo ciego; el saldo esperado se muestra sólo tras
  cerrar. La diferencia es declarado menos esperado y, cuando no es cero, la justificación
  es obligatoria tanto en servicio como en PostgreSQL.
- El cierre conserva responsables y observaciones, es inmutable y no genera movimientos
  monetarios. Una caja inactiva puede cerrar un corte pendiente, pero no abrir otro.
- El listado filtra y pagina en PostgreSQL, exporta los mismos filtros por bloques y usa
  autocompletado remoto limitado a cajas activas. El alcance de plantel nunca expone cajas
  institucionales ni cortes de otros planteles.

## Verificación de cortes de caja

- Docker compiló 409 fuentes Java y ejecutó 249 pruebas sin fallos ni errores. Las siete
  pruebas nuevas cubren apertura, exclusividad, cálculo por folios, diferencia obligatoria,
  idempotencia de cierre, tipo de cuenta y bloqueo del acceso de recuperación.
- Flyway validó 24 migraciones y aplicó V24 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 40 repositorios y `/actuator/health` respondió `UP`.
- PostgreSQL confirmó V24 exitosa, dos permisos nuevos y cero cortes. No se crearon cortes
  ficticios ni movimientos monetarios durante la verificación.
- El propietario debe asignar ambos permisos, iniciar una sesión nueva y probar apertura,
  movimientos, conteo, cierre con y sin diferencia y Excel. Después se recomienda V25
  para estados de cuenta y reportes financieros operativos.

## Decisiones — estados de cuenta y reportes financieros

- Flyway V25 agrega `REPORTE_FINANCIERO_CONSULTAR` y tres índices de apoyo; no crea
  entidades de saldo ni asigna el permiso a roles existentes. Los reportes se reconstruyen
  desde los libros inmutables de cargos, ajustes, aplicaciones y movimientos.
- El estado de cuenta administrativo exige un alumno por autocompletado y admite corte,
  plantel y situación. Calcula importe original, ajustes efectivos, aplicaciones netas,
  saldo y vencimiento. Un adeudo parcial vencido se clasifica como `VENCIDO`; un cargo
  cancelado permanece visible pero no suma al exigible.
- Tesorería admite cuenta, plantel de operación, periodo y agrupación diaria, mensual o
  anual. Usa `fechaOperacion` interpretada en la zona horaria institucional. Los
  traspasos y reversas de transferencia se excluyen de ingresos/egresos operativos y se
  reportan aparte para evitar doble conteo.
- Apertura y cierre se calculan desde saldo inicial y movimientos sólo cuando el filtro
  representa cuentas completas. Un filtro por plantel de operación no prorratea cuentas
  institucionales ni inventa un saldo atribuible.
- Los usuarios con alcance de plantel sólo consultan cargos y cuentas de sus planteles;
  no reciben cuentas compartidas institucionales. Ambos reportes pagan el costo de la
  consulta en PostgreSQL, tienen paginación y exportan XLSX por bloques con los mismos
  filtros. No publican ni modifican movimientos monetarios.
- Identificadores inexistentes o filtros de negocio inválidos muestran el error dentro
  de la pantalla. Las vistas son responsivas y compatibles con temas claro y oscuro.

## Verificación de reportes financieros

- Docker compiló 424 fuentes Java y ejecutó 254 pruebas sin fallos ni errores. Las cinco
  pruebas nuevas cubren fecha local institucional, estado vacío, pertenencia del alumno,
  periodo invertido y conversión de fechas a instantes en la zona institucional.
- Flyway validó 25 migraciones y aplicó V25 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 40 repositorios y la aplicación inició en el puerto 8080.
- PostgreSQL confirmó V25, los tres índices y el permiso nuevo, y aceptó las consultas
  nativas de estado de cuenta, agrupación de tesorería y saldo de apertura. La validación
  fue de solo lectura y no creó movimientos, cargos ni saldos ficticios.
- El propietario debe asignar `REPORTE_FINANCIERO_CONSULTAR`, iniciar una sesión nueva y
  revisar ambas pestañas y sus Excel con datos existentes. Después se recomienda acordar
  V26 para eventos escolares y destinatarios como base del futuro portal del tutor;
  avisos y notificaciones quedan para una etapa posterior.

## Decisiones — eventos escolares

- Flyway V26 crea `EventoEscolar` y `DestinatarioEvento`, y agrega
  `EVENTO_ESCOLAR_LEER` y `EVENTO_ESCOLAR_ADMINISTRAR` sin modificar roles existentes.
- Los estados son `BORRADOR`, `PUBLICADO` y `CANCELADO`. Sólo se edita un borrador;
  publicar es explícito y cancelar exige motivo, conserva el historial y nunca elimina
  el registro. Los tipos iniciales son junta, festival, suspensión, actividad y otro.
- El alcance puede ser institución, plantel o selección. Una selección puede combinar
  niveles, grados, grupos y alumnos con semántica de unión; se validan institución,
  oferta, ciclo, plantel, inscripción vigente y ausencia de duplicados.
- Las fechas locales se convierten a `Instant` con la zona horaria institucional y deben
  quedar dentro de un ciclo no cerrado. La administración institucional exige alcance
  institucional y el usuario de plantel queda limitado a sus planteles.
- El listado filtra y pagina en PostgreSQL y exporta XLSX por bloques. La selección usa
  autocompletado remoto indexado y acotado, no listas completas. Crear y actualizar
  conservan la captura ante errores; publicación y cancelación muestran los errores en
  la ficha del evento.
- V26 constituye la fuente administrativa de eventos para el futuro portal del tutor.
  No implementa todavía el portal, avisos libres, notificaciones internas, correo ni
  WhatsApp.

## Verificación de eventos escolares

- Docker compiló 447 fuentes Java y ejecutó 261 pruebas sin fallos ni errores. Las siete
  pruebas nuevas cubren zona horaria, rango invertido, selección vacía, grupo de otro
  ciclo, publicación única y cancelación histórica con motivo normalizado.
- Flyway validó 26 migraciones y aplicó V26 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 41 repositorios y `/actuator/health` respondió `UP`.
- PostgreSQL confirmó la migración V26, sus dos tablas y sus dos permisos. No se crearon
  eventos ni destinatarios ficticios durante la verificación.
- El propietario debe asignar ambos permisos, iniciar una sesión nueva y probar creación,
  edición, publicación, cancelación, filtros, autocompletado y Excel. Tras confirmar y
  subir V26, debe acordarse V27 para la primera entrega del portal del tutor con hijos
  vinculados, estado de cuenta derivado y eventos publicados aplicables.

## Decisiones — primer portal del tutor

- Flyway V27 agrega `PORTAL_TUTOR_ACCEDER` y un índice parcial para eventos publicados;
  no crea tablas duplicadas ni asigna el permiso a roles existentes.
- El portal vive en `/portal`, separado de administración. La cuenta debe estar enlazada
  con un tutor activo y cada consulta limita alumnos y fotografías a vínculos activos y
  vigentes. El acceso de recuperación y los identificadores ajenos se rechazan.
- El selector familiar muestra matrícula, parentesco, fotografía y situación académica
  vigente derivada de inscripción y asignación de grupo. La ausencia de inscripción se
  presenta explícitamente.
- Finanzas se habilita sólo si el vínculo es responsable financiero y además permite ver
  finanzas. El estado de cuenta reutiliza la reconstrucción de cargos, ajustes y abonos,
  con paginación, sin persistir saldos ni permitir operaciones monetarias.
- La agenda resuelve únicamente eventos publicados del mismo ciclo que alcancen al alumno
  por institución, plantel, nivel, grado, grupo o destino individual. Presenta próximos
  eventos y hasta 30 días de historial reciente, paginados de diez en diez.
- La vista es responsiva, compatible con tema claro y oscuro, incorpora fotografía
  privada y cierre de sesión. Un usuario sólo de portal que entra por `/admin` es enviado
  al portal en lugar de recibir una pantalla de acceso denegado.
- El diseño se separa intencionalmente del sistema administrativo: usa una identidad
  familiar cálida, lenguaje directo, selector visual de hijos, accesos rápidos, agenda
  cronológica, resumen simple de pagos y una barra inferior tipo aplicación en móvil.
  No expone términos técnicos como expediente, libros contables o permisos al tutor.
- Se incorporó `/familias` como acceso visual exclusivo para tutores. El formulario
  reutiliza la autenticación segura existente, pero los aciertos, errores y cierres de
  sesión conservan el recorrido familiar; `/login` continúa reservado visualmente para
  el personal. Una visita sin sesión a `/portal` se dirige a `/familias`.

## Verificación del primer portal del tutor

- Docker compiló 453 fuentes Java y ejecutó 270 pruebas sin fallos ni errores. Las siete
  pruebas nuevas cubren recuperación, tutor no vinculado, familia vacía, restricción y
  autorización financiera, alumno ajeno y protección de fotografía.
- Dos pruebas adicionales fijan las vistas independientes de acceso para personal y
  familias.
- Flyway validó 27 migraciones y aplicó V27 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 41 repositorios y `/actuator/health` respondió `UP`.
- PostgreSQL confirmó el permiso y el índice; además ejecutó las consultas completas de
  vínculos e intersección de eventos con identificadores inexistentes, sólo en lectura.
  No se crearon tutores, vínculos, cargos o eventos ficticios.
- La imagen posterior al rediseño conservó salud `UP`; `/familias` respondió HTTP 200
  y una solicitud anónima a `/portal` respondió HTTP 302 hacia `/familias`.
- Durante la revisión manual de Tesorería se detectó que Thymeleaf rechazaba la lista
  compacta de tamaños de página y dejaba la respuesta HTML incompleta. Se corrigió la
  sintaxis en Tesorería, Estado de cuenta, Cortes de caja y Eventos; la compilación
  posterior ejecutó 271 pruebas, incluida una regresión para las cuatro plantillas.

## Decisiones — avisos escolares y lectura familiar

- Flyway V28 crea `Aviso` y los permisos `AVISO_LEER` y `AVISO_ADMINISTRAR`, sin
  asignarlos automáticamente a roles existentes.
- Un aviso pertenece a una institución y opcionalmente a un plantel. Nace como
  `BORRADOR`, sólo el borrador se edita, la publicación es explícita y `RETIRADO`
  conserva fecha y motivo. No existe eliminación física.
- El vencimiento es opcional y se interpreta en la zona horaria institucional. Un aviso
  vencido no puede publicarse y uno publicado deja de aparecer automáticamente cuando
  vence, sin perder historial administrativo.
- El módulo aplica aislamiento institucional y de plantel, listado filtrado y paginado
  en PostgreSQL, exportación XLSX por bloques y errores dentro de formularios y detalle.
- El portal resuelve avisos por el hijo seleccionado: muestra sólo publicaciones
  vigentes de toda la institución o del plantel de una inscripción actual. No incluye
  correo, WhatsApp ni notificaciones internas por usuario.

## Verificación de avisos escolares

- Docker compiló 469 fuentes Java y ejecutó 274 pruebas sin fallos ni errores. Tres
  pruebas nuevas cubren publicación vigente, rechazo de vencimiento y retiro histórico.
- Flyway aplicó V28 correctamente; PostgreSQL confirmó la tabla `aviso`, ambos permisos
  y cero avisos ficticios. Hibernate validó el esquema y la aplicación respondió `UP`.
- V28 fue revisada por el propietario y quedó confirmada en Git como `b2779ad`.

## Decisiones — notificaciones internas del portal familiar

- Flyway V29 crea `notificacion_usuario` con origen exclusivo de Evento o Aviso, estado
  de lectura, auditoría, índices para bandeja y una clave única por usuario y origen.
- La sincronización es perezosa y transaccional al abrir `/portal`; `ON CONFLICT DO
  NOTHING` permite reintentos seguros y evita duplicados sin necesitar un proceso en
  segundo plano en esta etapa.
- Sólo participan tutores, alumnos y vínculos activos y vigentes cuyo indicador
  `puede_recibir_notificaciones` esté habilitado. Eventos aplica ciclo, plantel y los
  destinatarios de nivel, grado, grupo o alumno; Avisos aplica institución o plantel.
- La bandeja es independiente del hijo seleccionado porque reúne todo lo dirigido a la
  familia, pagina diez filas en PostgreSQL, ordena pendientes primero y muestra un
  contador en la campana.
- Marcar como leída usa bloqueo pesimista, valida que la fila pertenezca a la cuenta y
  vuelve a comprobar acceso al evento o aviso. Un identificador ajeno o contenido ya
  inaccesible no se abre ni se marca.
- La etapa reutiliza `PORTAL_TUTOR_ACCEDER`, no agrega permisos, no elimina historial y
  no incluye correo, WhatsApp o push.

## Verificación de notificaciones internas

- Docker compiló 476 fuentes Java y ejecutó 277 pruebas sin fallos ni errores. Las tres
  pruebas nuevas cubren sincronización/paginación, lectura autorizada y rechazo de una
  fila perteneciente a otra cuenta.
- Flyway validó 29 migraciones y aplicó V29 sobre PostgreSQL 17. Hibernate detectó 43
  repositorios y la aplicación completó el arranque en el puerto 8080.
- PostgreSQL confirmó V29 exitosa y cero filas iniciales en `notificacion_usuario`; no
  se generaron tutores, eventos, avisos ni notificaciones ficticias.
- V29 fue revisada y confirmada en Git por el propietario como `829b2d2`.

## Decisiones — resultados de pagos en la bandeja familiar

- Flyway V30 agrega `pago_id`, amplía el tipo a 20 caracteres, incorpora
  `PAGO_VALIDADO` y `PAGO_RECHAZADO`, y endurece la restricción para que cada fila tenga
  exactamente un origen entre evento, aviso o pago.
- La sincronización perezosa considera cambios de estado de los últimos 90 días. La
  clave `PAGO_<ESTADO>:<ID>` por usuario evita duplicados y permite distinguir el
  resultado sin acoplar la transacción financiera a la capa de portal.
- El destinatario es la cuenta vinculada al tutor titular del pago. Además debe existir
  al menos un vínculo activo y vigente con alumno activo que combine responsabilidad
  financiera, permiso para ver finanzas y permiso para recibir notificaciones.
- El mensaje presenta folio, importe, moneda y resultado; un rechazo incluye su motivo.
  La vista escapa el contenido y usa el mismo contador y paginación de la bandeja.
- Al abrir se bloquea la notificación y se revalidan cuenta, institución, estado exacto
  del pago y acceso financiero. Si algo fue revocado permanece sin leer; si es válido se
  dirige a `#cuenta`.
- V30 no altera validación/rechazo, no publica movimientos adicionales, no crea permisos
  y no incorpora correo, WhatsApp o push.

## Verificación de resultados de pagos

- Docker compiló 476 fuentes Java y ejecutó 279 pruebas sin fallos ni errores. Dos
  pruebas nuevas cubren pago validado accesible y pago rechazado cuyo acceso financiero
  ya no está disponible.
- Flyway validó 30 migraciones y aplicó V30 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 43 repositorios y la aplicación completó el arranque en el puerto
  8080.
- PostgreSQL confirmó la columna `pago_id`, el índice parcial, los cuatro tipos y la
  exclusividad del origen. La tabla conservó cero filas; no se crearon ni modificaron
  pagos, movimientos o notificaciones de prueba.
- Falta la prueba manual con un pago controlado. Después de revisar y subir V30, el
  siguiente bloque recomendado es V31 para la bitácora central e inmutable de auditoría.

## Decisiones — bitácora central e inmutable de auditoría

- Flyway V31 crea `auditoria` y `AUDITORIA_CONSULTAR` sin conceder el permiso a roles
  existentes. La fila identifica institución, actor persistido o actor de sistema,
  acción, entidad, instante, motivo, cambios permitidos y correlación.
- La tabla es de sólo inserción. Además de `@Immutable`, PostgreSQL ejecuta
  `tg_auditoria_inmutable` antes de `UPDATE` o `DELETE` y rechaza la operación. Cuatro
  índices cubren institución/fecha, entidad, actor y correlación.
- `RegistroAuditoriaService` exige una transacción existente, de modo que una operación
  sensible y su evidencia se confirman o revierten juntas. Las claves idempotentes de
  los servicios evitan registrar otra acción cuando un reintento sólo devuelve el
  resultado anterior.
- Se registran validación y rechazo de pagos, devolución, transferencia, reversa de
  movimiento o transferencia, concesión de permiso a rol, asignación o retiro de rol de
  usuario, publicación/cancelación de eventos y publicación/retiro de avisos.
- Los detalles usan una lista permitida y excluyen claves de contraseñas, hashes,
  tokens, secretos, cuentas o CLABE, comprobantes y contenido de archivos. No se guarda
  el cuerpo completo de entidades ni información binaria.
- Un filtro de petición crea `X-Correlation-ID`, lo comparte durante la operación y
  limpia el contexto al terminar. El acceso de recuperación queda identificado como tal
  sin inventar un usuario de base de datos.
- `/admin/auditoria` exige el permiso nuevo y alcance institucional. Consulta y pagina
  en PostgreSQL por fechas, acción, tipo/ID de entidad, actor y correlación; el Excel
  recorre por bloques exactamente el mismo filtro. No tiene alta, edición ni borrado.

## Verificación de la bitácora central

- Docker compiló 487 fuentes Java y ejecutó 282 pruebas sin fallos ni errores. Tres
  pruebas nuevas cubren atribución del actor, exclusión de secretos, acceso de
  recuperación y ciclo de vida de la correlación.
- Flyway validó 31 migraciones y aplicó V31 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 44 repositorios y Spring Boot completó el arranque en el puerto 8080.
- PostgreSQL confirmó el permiso, los cuatro índices y la definición del trigger para
  impedir actualizaciones y eliminaciones. `auditoria` conservó cero filas porque no se
  simularon acciones sensibles sobre los datos operativos.
- La prueba funcional pendiente es asignar `AUDITORIA_CONSULTAR`, abrir una sesión
  nueva, ejecutar una acción controlada y revisar listado, filtros, detalle y Excel.
  Después de confirmar V31, la recomendación es diseñar V32 para cancelación controlada
  de pagos, separada de las devoluciones.

## Decisiones — cancelación controlada de pagos

- Flyway V32 agrega `PAGO_CANCELAR` sin asignarlo a roles existentes. Añade
  `cancelado_en` y `cancelado_por_id` a Pago con restricción que exige ambos cuando
  el estado es `CANCELADO` y los prohíbe en los demás estados.
- Cancelar es corregir un registro creado por error, no devolver dinero realmente
  recibido. Se aceptan sólo `PENDIENTE_VALIDACION` y `VALIDADO`; un pago rechazado no
  se vuelve a resolver. Se exige motivo, versión y usuario administrativo persistido;
  el acceso de recuperación no ejecuta cancelaciones.
- Un pendiente se cancela sin afectar cuentas o cargos. Un validado no puede tener
  devoluciones ejecutadas ni un ingreso ya compensado. La operación bloquea pago,
  cuenta y cargos, comprueba saldo suficiente y revierte cada aplicación vigente
  mediante una fila histórica `REVERTIR` de igual importe y con referencia al original.
- V32 amplía las clases de movimiento con `ANULACION`: publica un egreso de igual
  monto y en la misma cuenta del `COBRO`, con secuencia, saldos y referencia única al
  ingreso original. El movimiento previo, el pago y los abonos históricos permanecen.
  Un reintento con el mismo motivo no crea otra operación.
- La bitácora V31 registra `PAGO_CANCELADO` en la misma transacción. El expediente
  administrativo muestra motivo y permite la acción sólo con `PAGO_CANCELAR`; los
  errores esperables regresan a la misma ficha.
- V32 agrega `PAGO_CANCELADO` a la bandeja familiar. Una notificación previa de
  validación se conserva en la base, pero deja de mostrarse al no coincidir con el
  estado actual; se crea una notificación de cancelación para el tutor que mantenga
  vínculo y autorización financiera/de notificaciones. Su apertura revalida acceso.

## Verificación de cancelación de pagos

- Docker compiló 490 fuentes Java y ejecutó 289 pruebas sin fallos ni errores. Las
  pruebas nuevas cubren pendiente sin movimiento, validado con reversas y un egreso,
  bloqueo por devolución, saldo insuficiente, reintento y notificación familiar.
- Flyway validó 32 migraciones y aplicó V32 sobre PostgreSQL 17. Hibernate validó el
  esquema y detectó 44 repositorios; la aplicación completó el arranque.
- PostgreSQL confirmó permiso, columnas y restricciones nuevos; había cero pagos en
  estado `CANCELADO`. No se ejecutó ninguna operación de cancelación en datos reales.
- Falta prueba funcional manual con pagos y cuentas controlados por el propietario.
  Después de su revisión y commit, el siguiente bloque recomendado es definir V33 de
  conciliación bancaria, sin alterar el libro inmutable.

## Decisión posterior — estado de cuenta interno mensual y anual

- El propietario descartó la conciliación bancaria: no se importarán PDFs de bancos ni
  se comparará el libro contra movimientos externos. Los PDFs que descarga del banco
  no forman parte del flujo del sistema.
- En su lugar, el reporte por cuenta financiera ofrece cortes mensuales o anuales con
  los movimientos capturados en Nexo Escolar, saldo de apertura y cierre y exportación
  del mismo periodo a Excel y PDF. Es un documento interno, no bancario.
- El cálculo incluye ingresos, egresos, traspasos, reversas y anulaciones. Si una cuenta
  inició dentro del corte, el saldo inicial se identifica aparte; no se presenta como
  ingreso. El alcance de la cuenta sigue el permiso `REPORTE_FINANCIERO_CONSULTAR` y
  las reglas de reportes existentes. La consulta no altera el libro ni necesita V33.
- Maven compiló 495 fuentes y ejecutó 292 pruebas sin fallos. Se verificaron las
  celdas del Excel, la extracción de texto del PDF y una página renderizada del PDF.
  El empaquetado terminó correctamente. Una instancia temporal aislada en el puerto
  18081, conectada al PostgreSQL existente, respondió `UP`; la ruta nueva sin sesión
  respondió HTTP 401. No se crearon movimientos ni se alteraron saldos.
- Falta la revisión funcional autenticada con cuentas y movimientos controlados del
  propietario: comparar pantalla, Excel y PDF del mismo mes y año, incluyendo una
  cuenta abierta dentro del periodo y un traspaso. La instancia habitual no se reemplazó.

## V33 — retiros externos de fondos

- El propietario confirmó que el retiro especializado es una salida hacia persona o
  destino externo. Banco a caja registrada sigue siendo `TransferenciaCuenta`, y la
  entrega de dinero de un pago al tutor sigue siendo `DevolucionPago`. No se implementa
  conciliación bancaria.
- V33 crea `retiro_fondo` con referencia única al movimiento financiero publicado,
  destinatario, motivo, concepto, referencia de comprobante, usuario autorizante y
  clave idempotente. Agrega `RETIRO_FONDO_LEER` y `RETIRO_FONDO_REGISTRAR` sin asignarlos
  automáticamente. El retiro reutiliza `MovimientoManualService` para publicar un
  único `OPERACION/EGRESO`: bloqueo de cuenta, fecha institucional, saldo no negativo,
  alcance y actor persistido. Toda la operación y su bitácora son transaccionales.
- Los motivos técnicos de cobros, traspasos y devoluciones se rechazan. El historial
  permanece incluso si el movimiento se revierte: el estado se deriva de la reversa.
  El listado usa filtros y paginación de PostgreSQL; el Excel usa idénticos filtros por
  bloques. El folio/referencia es obligatorio; no se adjunta un archivo en esta etapa.
- Docker compiló el módulo y pasó 298 pruebas. En PostgreSQL 17 temporal y vacío,
  Flyway aplicó 33 migraciones, Hibernate validó 45 repositorios, la app respondió `UP`
  y listado/formulario autenticados devolvieron HTML completo; el Excel filtrado tuvo
  firma XLSX válida. Tras autorización explícita del propietario, la imagen nueva se
  desplegó en la instancia habitual. Flyway validó 33 migraciones y reportó el esquema
  en V33; PostgreSQL confirmó los dos permisos nuevos y cero filas en `retiro_fondo`.
  `/actuator/health` respondió `UP`. No se generaron movimientos monetarios durante el
  despliegue. Falta revisión funcional con una cuenta y retiro de prueba controlados.
- Corrección del guardado: el formulario de retiro omitía el token CSRF al usar
  `action` en vez de `th:action`; ahora el HTML autenticado lo genera y el `POST`
  entra al controlador. La ayuda del autocompletado ya no aparece roja ni permanece
  tras seleccionar una cuenta; también se limpia el error anterior de ese campo.
  Maven pasó 299 pruebas. En una app y PostgreSQL descartables se registró un retiro
  de 25 sobre saldo 100: un único movimiento dejó saldo 75; repetir la misma clave
  no duplicó el egreso. Se desplegó la imagen corregida en la instancia habitual y
  `/actuator/health` respondió `UP`. No se hicieron retiros de prueba en datos reales.

## Corrección del formulario de nuevo pago

- El log de la aplicación habitual identificó una excepción de Thymeleaf en
  `admin/pago-form.html`, línea 16: `errorOperacion` era nulo al entrar a Nuevo pago
  y la expresión `or` intentaba convertirlo a booleano. Se cambió a una comprobación
  explícita de nulidad. El formulario también pasó a usar `th:action`, para que el
  envío multipart genere el campo CSRF.
- Docker compiló y ejecutó 300 pruebas sin fallos. Una instancia temporal con
  PostgreSQL vacío y credenciales ficticias abrió `/admin/pagos/nuevo` autenticada
  con HTTP 200; el HTML contenía el formulario y su token CSRF. No se registraron
  pagos de prueba ni se tocaron saldos reales.

## Corrección del guardado de Cargos

- Los formularios de emisión manual y generación programada usaban `action` HTML,
  por lo que Thymeleaf no agregaba el token CSRF a sus envíos. Ambos usan ahora
  `th:action`. La ayuda de descripción histórica se redactó de forma explícita:
  el texto del cargo no cambia aunque se renombre después el concepto del catálogo.
- La compilación Docker pasó 301 pruebas. En una app autenticada y PostgreSQL
  descartables, ambos formularios mostraron su token CSRF y los envíos vacíos
  regresaron HTTP 200 con validaciones; `cargo` permaneció con cero filas. Se
  desplegó la imagen en la instancia habitual, que respondió `UP`. No se emitieron
  cargos ni se modificaron saldos reales para esta prueba.
- A petición del propietario se completó después una prueba válida de punta a punta
  en otra instalación descartable. Se prepararon institución, plantel, grado, ciclo,
  alumno, inscripción activa y concepto de cobro ficticios. El formulario autenticado
  envió un cargo de 250.00 MXN para septiembre de 2026; el servidor respondió 302 al
  listado y PostgreSQL confirmó exactamente una fila `EMITIDO` con la descripción
  histórica esperada. El listado mostró matrícula, descripción e importe y el log no
  registró excepciones durante el envío. No se necesitaron cambios adicionales de
  código; la instancia habitual no recibió ningún cargo de prueba.

## Nombre visible del historial de ajustes

- A petición del propietario, el listado “Ajustes de cargos” pasa a llamarse
  “Historial de ajustes” en el menú y encabezado. La pantalla aclara que los ajustes
  manuales se registran desde el detalle de Cargos; también distingue el estado
  vacío. El enlace de regreso desde el detalle vuelve al historial.
- Es un cambio de presentación: se conserva el slug `ajustes-cargo`, la ruta, los
  permisos y la lógica de registro/reversa de ajustes.
- Docker ejecutó 302 pruebas sin fallos. En una app con PostgreSQL descartables, el
  historial autenticado renderizó el nuevo título, “Movimientos registrados” y la
  guía del estado vacío. Se desplegó la imagen en la instancia habitual y salud
  respondió `UP`; no se modificaron cargos ni ajustes operativos para verificarlo.

## Corrección de ayudas de autocompletado financiero

- En los selectores de cuentas usados por movimientos y otros formularios, al elegir
  una sugerencia se ocultan la ayuda y un error de validación previo del mismo campo.
  Las indicaciones vuelven únicamente cuando se escribe, limpia o cambia el alcance.
- Los autocompletados propios de Pagos (tutor, cuenta y cargo) y de validación de pago
  ya no dejan el texto “Seleccionado” después de elegir. Al limpiar, cancelan búsquedas
  pendientes y restablecen una indicación contextual.
- La sintaxis de los tres JavaScript se verificó con Node. Docker compiló y ejecutó
  302 pruebas sin fallos; la imagen se desplegó en la instancia habitual y salud
  respondió `UP`. La verificación no generó pagos ni movimientos financieros.

## Corrección del guardado de movimientos manuales

- El formulario de `/admin/movimientos-financieros/nuevo` enviaba el `POST` con un
  atributo `action` HTML, por lo que Thymeleaf no incorporaba el token CSRF y Spring
  Security rechazaba el envío antes de llegar al controlador. Ahora usa `th:action`.
- Se añadió una prueba de regresión sobre la plantilla. Docker compiló y pasó 303
  pruebas; se desplegó la imagen en la instancia habitual y `/actuator/health`
  respondió `UP`. No se registraron movimientos ni se alteraron saldos para verificar.
- La prueba manual pendiente debe hacerse con una cuenta e importe controlados. Tras
  actualizar, es necesario abrir nuevamente el formulario para cargar el token.

## Corrección del guardado de transferencias

- El formulario de `/admin/transferencias/nueva` también usaba un atributo `action`
  HTML y no generaba el token CSRF. El servidor rechazaba el `POST` antes de ejecutar
  el servicio atómico; los registros mostraron el rechazo y ninguna cuenta cambió.
- Se cambió a `th:action` y se añadió una prueba de regresión específica. Docker pasó
  304 pruebas, la imagen se desplegó en la instancia habitual y salud respondió `UP`.
  No se hizo una transferencia de prueba ni se modificaron saldos existentes.
- Para comprobarlo manualmente se debe volver a abrir el formulario actualizado y usar
  dos cuentas e importe controlados; ambas cuentas deben tener la misma moneda.

## Corrección de la apertura de cortes de caja

- El formulario de `/admin/cortes-caja/nuevo` usaba `action` HTML y no generaba el
  token CSRF. El `POST` era rechazado antes de ejecutar el servicio, por lo que no se
  llegó a abrir ningún corte. El formulario de cierre ya utilizaba `th:action`.
- La apertura ahora usa `th:action` y una prueba de regresión protege tanto apertura
  como cierre. Docker pasó 305 pruebas, la imagen se desplegó en la instancia habitual
  y salud respondió `UP`; no se abrieron ni cerraron cortes durante la verificación.

## V34 — reporte de transferencias desde el portal familiar

El tutor con acceso financiero puede reportar una transferencia desde `/portal`, adjuntar
comprobantes y distribuir el importe entre cargos autorizados. El pago queda pendiente
de validación administrativa; se conserva el origen portal, el usuario reportante y un
historial paginado.

## Estabilización confirmada de V34 y V35

- V34 distingue de forma inequívoca los cargos del autocompletado con alumno, concepto,
  descripción e ID. Esto evita seleccionar el primer resultado por error cuando un mismo
  alumno tiene varios cargos.
- La suite cubre una transferencia distribuida entre cargos de tres hijos autorizados,
  además del rechazo cuando la suma no coincide o el tutor carece de vínculo financiero
  activo.
- V35 queda limitado a soporte visual de sólo lectura. El controlador administrativo no
  contiene `POST` y la vista de soporte oculta el botón para reportar transferencias; el
  personal no puede suplantar al tutor para registrar un pago.
- Docker compiló 514 fuentes de producción y ejecutó 312 pruebas sin fallos. PostgreSQL
  validó y aplicó V33, V34 y V35 sobre el volumen que estaba en V32; Hibernate detectó
  45 repositorios y `/actuator/health` respondió `UP`.
- Se retiró la publicación del puerto PostgreSQL 5432 hacia el host en `compose.yaml`.
  La aplicación continúa conectándose internamente a `postgres:5432`, sin modificar ni
  eliminar el volumen persistente.
- Esta verificación no creó pagos, comprobantes ni movimientos y no alteró saldos. Falta
  la prueba manual controlada con un tutor de varios hijos, distribución entre cargos,
  validación/rechazo administrativo y revisión del soporte de sólo lectura.

## Simplificación de derechos por módulo

- La pantalla de Roles ya no expone por separado acciones como leer, registrar,
  administrar, validar o cancelar. Presenta 31 módulos asignables con el mismo nombre usado en la
  interfaz, cada uno mediante una sola casilla de acceso completo.
- Los 65 permisos técnicos continúan existiendo y protegiendo los endpoints. La capa de
  autenticación expande cualquier permiso histórico al paquete completo de su módulo;
  por ello los roles existentes adoptan la regla todo o nada en su siguiente sesión.
- Al guardar un rol se activan todas las relaciones técnicas de los módulos elegidos y se
  desactivan las de los módulos no elegidos, conservando el historial de `RolPermiso`.
- La simplificación de permisos no requirió una migración. Una validación impide omitir
  silenciosamente permisos técnicos futuros que todavía no hayan sido clasificados en
  un módulo. V36 se creó después y corresponde exclusivamente a la identificación del tutor.
- `Soporte del portal familiar` es un módulo independiente de sólo lectura y ya no exige
  conceder también administración de Roles. Los cambios de acceso requieren cerrar sesión
  y volver a entrar.

## V36 — identificación oficial opcional del tutor

- El expediente editable del tutor permite cargar de forma opcional INE, licencia de
  conducir, pasaporte u otra identificación oficial. Acepta PDF, JPEG y PNG válidos de
  hasta 10 MB; no confía únicamente en el nombre o MIME declarado por el navegador.
- Los bytes se guardan bajo `private_files` y PostgreSQL conserva metadatos, checksum,
  tipo y relación. Sólo existe una identificación vigente por tutor; reemplazar o retirar
  conserva el historial privado y no elimina físicamente documentos anteriores.
- La consulta y descarga revalidan el tutor y el alcance institucional del módulo, usan
  respuesta sin caché y no exponen rutas físicas. Un tutor inactivo no admite nuevas cargas.
- Docker compiló 521 fuentes y ejecutó 324 pruebas sin fallos. Flyway aplicó V36, Hibernate
  detectó 46 repositorios y salud respondió `UP`. `tutor_identificacion` quedó con cero
  filas: no se cargó ningún documento real para verificar.
- Prueba manual pendiente: editar un tutor controlado, cargar un archivo ficticio válido,
  abrirlo, reemplazarlo, consultar el historial y retirar el vigente. Después confirmar
  que un archivo falso o demasiado grande mantiene el error dentro del formulario.

## Mejora transversal posterior a V36 — formato monetario y navegación administrativa

- Se creó `FormatoMoneda` como regla visual única para presentar pesos con símbolo, dos
  decimales y separadores: `$150.52` y `$1,000,000.50`. Se aplica en catálogos,
  autocompletados, mensajes, cargos, pagos, devoluciones, movimientos, tesorería, estados
  de cuenta, cortes, retiros y portal familiar. Porcentajes y campos numéricos editables
  no se transforman.
- El menú administrativo dejó el degradado y ahora usa un fondo sólido azul noche,
  textos de alto contraste, acento turquesa y una tarjeta clara para el módulo activo.
  La mejora es únicamente visual: no altera módulos, permisos ni rutas.
- Docker compiló 522 fuentes y ejecutó 327 pruebas sin fallos, incluida una prueba de
  integración del formatter como bean dentro de una expresión Thymeleaf. No se requirió
  migración; Flyway continúa en V36.
- La pantalla de edición de Tutores se reorganizó como ficha técnica, siguiendo el patrón
  visual de Alumnos. La identificación oficial aparece primero: muestra vista previa si es
  imagen, una tarjeta documental si es PDF o el estado sin documento. Las acciones y el
  historial se integraron en el encabezado; la lógica privada de archivos no cambió.

## V37 — separación de usuarios administrativos y accesos de tutores

- `usuario.tipo_cuenta` distingue `ADMINISTRATIVO` de `PORTAL_TUTOR`. La migración V37
  conserva los datos y reclasifica como portal toda cuenta ya referenciada por un tutor.
- Usuarios muestra, exporta y administra únicamente cuentas administrativas. Las rutas
  directas del controlador también rechazan cuentas del portal, y la asignación de roles
  impide conceder permisos administrativos a una cuenta familiar.
- La autenticación de una cuenta `PORTAL_TUTOR` ignora cualquier rol histórico y concede
  exclusivamente `PORTAL_TUTOR_ACCEDER`. Una cuenta administrativa no recibe ese acceso
  aunque exista una relación histórica con el permiso técnico.
- La ficha de Tutores reemplaza el selector de cuentas preexistentes por un bloque propio:
  propone un username normalizado `nombre.apellido`, permite editarlo, crea y vincula la
  cuenta de forma transaccional y genera un enlace de activación de 48 horas. Para cuentas
  activas genera recuperación de 30 minutos; también permite desactivar y reactivar sin
  eliminar la cuenta ni sus relaciones.
- Una reactivación conserva la contraseña: regresa a `ACTIVO` si ya estaba configurada o
  a `INVITADO` si seguía pendiente. La edición ordinaria del tutor ya no puede vincular,
  sustituir ni retirar usuarios administrativos.
- El catálogo de Tutores presenta username y estado de acceso. Agrega filtros en base de
  datos para `SIN_CUENTA`, `CUENTA_ACTIVA`, `CUENTA_PENDIENTE` y `CUENTA_INACTIVA`, además
  de los estados del expediente.
- Docker compiló 528 fuentes y ejecutó 333 pruebas sin fallos. Flyway validó 37
  migraciones y aplicó V37 sobre el volumen habitual; la aplicación respondió `UP`.
  PostgreSQL confirmó una cuenta `ADMINISTRATIVO`, una `PORTAL_TUTOR` y cero tutores
  vinculados a un tipo incorrecto. No se crearon cuentas ni se cambiaron contraseñas
  durante la verificación.
- La comprobación visual automatizada se detuvo en el login porque no había sesión
  conservada; por política no se consultó `.env`. Queda pendiente la prueba manual del
  propietario: listado de Usuarios, filtros de Tutores y ciclo crear–activar–ingresar–
  desactivar–reactivar con un tutor controlado.

## Reportes financieros con JasperReports posteriores a V37

- Se agregó JasperReports 7.0.8 para generar cuatro documentos PDF con una presentación
  común: balanza de movimientos y saldos, estado de cuenta financiera, estado de cuenta
  del alumno y concentrado de cobranza.
- Los reportes no incorporan SQL interno de Jasper. Reciben un `JRDataSource` que solicita
  bloques de 100 filas a los servicios, por lo que conservan permisos, alcance por
  institución/plantel, filtros y paginación en PostgreSQL sin cargar el resultado completo.
- El concentrado de cobranza es una nueva consulta por concepto, plantel o ciclo al corte;
  resume cargos, importe exigible, aplicado, saldo y vencido. Un cargo cancelado sigue en
  el conteo histórico, pero suma cero a los importes.
- Tesorería se rotula como balanza financiera, no como balanza de comprobación contable:
  aún no existen cuentas contables ni partida doble. La exportación se bloquea cuando hay
  filtro de plantel operativo para no inventar saldos parciales de caja o banco.
- La primera prueba Jasper falló correctamente en Alpine porque Helvetica no existía.
  Se cambió a la fuente lógica portable `SansSerif`; después el PDF se generó, PDFBox
  extrajo los textos esperados y renderizó la primera página a 144 DPI. La revisión visual
  no encontró textos cortados, traslapes ni columnas ilegibles.
- La compilación final pasó 333 pruebas. PostgreSQL aceptó y ejecutó en sólo lectura la
  consulta agrupada (cero filas en los datos actuales), y la imagen final quedó desplegada
  con salud `UP`.
- Esta etapa no cambia el esquema: Flyway permanece en V37 y V38 sigue libre. La prueba
  funcional con datos reales controlados y las cuatro rutas PDF queda a cargo del
  propietario; no crear movimientos monetarios únicamente para probar un documento.

## Navegación separada del portal familiar posterior a JasperReports

- La portada `/portal` dejó de ser una página larga con anclas. Ahora funciona como un
  inicio breve con la ficha del hijo y accesos claros a cada función.
- Notificaciones, avisos, agenda y pagos se consultan respectivamente en
  `/portal/notificaciones`, `/portal/avisos`, `/portal/agenda` y `/portal/pagos`. Cada
  pantalla conserva `alumnoId`, pagina sus propios resultados y ofrece **Regresar**.
- Las notificaciones abiertas redirigen a la sección independiente que corresponde. Los
  resultados de pago llevan a `/portal/pagos`, y una transferencia reportada con éxito
  también regresa a esa pantalla.
- El modo de soporte administrativo replica Avisos, Agenda y Pagos con rutas bajo
  `/admin/portal-soporte/{tutorId}`, sin habilitar escrituras. Las notificaciones no se
  muestran en soporte porque son una bandeja personal cuyo estado de lectura no debe
  alterarse ni simularse.
- No se agregó migración. La imagen Docker pasó 334 pruebas, arrancó con Flyway V37 y
  salud `UP`. La revisión visual sin credenciales llegó al login esperado; queda la
  prueba manual con una cuenta familiar controlada para validar contenido, regreso,
  cambio de hijo, paginación y experiencia móvil.

## Ayuda contextual global posterior a la navegación familiar

- Se agregó un componente único que coloca un icono de información junto al nombre de
  todos los campos visibles de formularios y filtros. Los campos ocultos técnicos no se
  presentan como ayuda de captura.
- El modal explica el uso del campo y da un ejemplo. Un glosario común cubre relaciones,
  fechas, importes, seguridad, finanzas, personas y comunicación, mientras que los casos
  dependientes del módulo —como la nomenclatura de cada código— tienen textos propios.
- Los controles agregados dinámicamente y las etiquetas externas enlazadas mediante
  `for` también reciben ayuda. Los futuros campos cuentan con una explicación de respaldo
  según su etiqueta y tipo hasta que se especialice el glosario.
- En la parte superior de cada módulo se muestra **¿Qué hace este módulo?**, con una
  descripción general y un caso de uso. Incluye consola administrativa, operaciones
  especiales y portal familiar. Las pantallas de acceso y contraseña conservan su diseño
  original y muestran únicamente ayuda junto a sus campos.
- La ventana es accesible y responsiva: declara diálogo modal, conserva el foco, responde
  a `Escape`, funciona en claro y oscuro y no altera valores ni envía formularios.
- Después de la revisión visual se redujeron los iconos dentro de filtros, se mantuvieron
  las etiquetas como bloques de ancho completo y se aisló la paleta: azul noche/cian en
  administración y crema/menta en el portal familiar. Esto corrigió el grid del login.
- No hay migración. Docker compiló 531 fuentes y ejecutó 336 pruebas sin fallos. La
  validación en navegador comprobó dos ayudas de campo y el modal en la pantalla pública;
  la comprobación autenticada completa queda para el propietario y no requiere capturar
  datos reales.

## Unificación visual de accesos y filtros

- El login del portal familiar conserva sus colores crema, coral y menta, pero ahora usa
  las mismas proporciones, radio exterior de 28 px y espaciado del acceso administrativo.
- Los filtros generales, financieros y de eventos tienen contenedor propio con borde
  redondeado, fondo sutil, sombra discreta, controles de 13 px e iconos informativos
  circulares de 20 px. En móvil reducen márgenes y radio; en modo oscuro usan la superficie
  azul noche del sistema.
- Es un cambio exclusivamente visual: no modifica rutas, criterios, paginación ni esquema.
- Docker ejecutó 337 pruebas sin fallos. La revisión en navegador confirmó el acceso
  familiar en dos columnas, radio de 28 px e iconos de ayuda circulares de 22 px. Los
  filtros autenticados conservan pendiente únicamente la aprobación visual del propietario.

## V38 — expediente documental y ficha médica del alumno

- La edición del alumno integra dos secciones privadas dentro de su ficha técnica; no se
  crearon módulos de menú ni permisos nuevos. Todas las operaciones reutilizan
  `ALUMNO_ADMINISTRAR`, el alcance institucional y la validación de recurso existente.
- `alumno_documento` clasifica acta, CURP, comprobante de domicilio, constancia o
  certificado, autorización, documento médico y otros. Conserva descripción, fecha del
  documento, vigencia opcional, auditoría y retiro lógico individual.
- Los documentos aceptan PDF, JPEG o PNG de hasta 10 MB y se validan mediante su firma
  real. Los bytes se guardan bajo `private_files` en la ruta privada de la institución y
  PostgreSQL conserva sólo metadatos, checksum y relaciones. Descargar revalida el
  alumno y responde sin caché; retirar no borra el archivo y lo deja en el historial.
- `ficha_medica_alumno` mantiene una sola ficha actual por alumno con tipo sanguíneo,
  alergias, padecimientos, medicamentos, discapacidad o apoyos, restricciones físicas y
  alimentarias, servicio médico, afiliación, médico tratante, contacto de emergencia,
  observaciones y constancia de autorización para atención de emergencia.
- La ficha médica usa bloqueo del alumno y versión optimista. Los alumnos inactivos se
  pueden consultar con su historia, pero no reciben documentos nuevos ni modificaciones
  médicas. Las validaciones permanecen en la misma pantalla.
- La información no se expone al portal familiar en esta etapa. Un eventual flujo para
  que el tutor entregue o corrija datos requerirá consentimiento, revisión administrativa
  y reglas de privacidad antes de habilitarse.
- Docker compiló 546 fuentes y ejecutó 344 pruebas sin fallos. Flyway validó 38
  migraciones, aplicó V38 sobre el volumen habitual y la aplicación respondió `UP`.
  Falta la prueba visual y funcional autenticada del propietario con datos ficticios.

## V39 — materias y plan de evaluación por grado

- Se agregó `materia` como catálogo institucional y `materia_grado` como configuración
  académica por grado. Cada plan define evaluación numérica o cualitativa, escala y mínima
  aprobatoria cuando corresponda, decimales, orden, horas semanales, inclusión en boleta,
  estado, auditoría y versión optimista.
- La base impide repetir código dentro de una institución, asociar dos veces una materia
  al mismo grado y repetir el orden entre materias activas del grado. El servicio valida
  además institución, nivel/grado activos, coherencia de escala y concurrencia.
- El módulo administrativo `Materias` incluye alta, edición y desactivación lógica de la
  materia y sus planes. La búsqueda de grado usa autocompletado PostgreSQL de tres
  caracteres y máximo 20 resultados. Los errores de negocio, integridad y versión se
  muestran en el mismo formulario.
- Se agregaron `MATERIA_LEER` y `MATERIA_ADMINISTRAR` sin asignación automática. El
  catálogo respeta alcance institucional, filtros, paginación y exportación XLSX global.
- Docker compiló 561 fuentes y ejecutó 350 pruebas sin fallos. Flyway validó 39
  migraciones y aplicó V39 sobre el volumen habitual; Hibernate detectó 50 repositorios
  y `/actuator/health` respondió `UP`. No se crearon materias de prueba.
- Falta la prueba funcional del propietario y asignar el nuevo módulo al rol deseado. Al
  aprobarla, continúa V40 con calificaciones por inscripción, materia y periodo en estados
  borrador/publicada; después V41 asistencia diaria y V42 boletas Jasper derivadas.

## V40 — calificaciones por periodo

- Se agregó `calificacion` con unicidad por inscripción, plan de materia y periodo. Cada
  fila conserva el tipo de evaluación, escala, mínima aprobatoria y decimales vigentes al
  crearla, además de estado, actor/fecha de publicación, auditoría y versión optimista.
- La captura administrativa trabaja por grupo, periodo y materia mediante tres
  autocompletados remotos. Obtiene sólo inscripciones con asignación al grupo durante las
  fechas del periodo; permite borradores parciales y valida escala numérica, decimales y
  longitudes cualitativas sin cargar catálogos completos.
- Publicar exige una calificación para todos los alumnos visibles y se ejecuta en una sola
  transacción. Un bloque publicado no admite cambios hasta reabrirlo; la reapertura vuelve
  todas sus filas a borrador y las oculta del portal hasta una nueva publicación.
- El módulo `Calificaciones` incorpora consulta paginada, filtros Borrador/Publicada,
  alcance institucional o de plantel y exportación XLSX. Sus permisos
  `CALIFICACION_LEER` y `CALIFICACION_ADMINISTRAR` no se asignan automáticamente.
- El portal familiar tiene una pantalla independiente de Calificaciones y el soporte
  administrativo reutiliza la misma consulta de sólo lectura. Ambos muestran únicamente
  resultados publicados del alumno autorizado.
- Los formularios POST usan acciones Thymeleaf para incluir CSRF; reglas de negocio,
  integridad y concurrencia regresan a la misma captura. Una prueba de regresión protege
  las acciones de guardar/publicar y reabrir.
- Docker compiló 573 fuentes y 89 fuentes de prueba; ejecutó 357 pruebas sin fallos.
  Flyway validó 40 migraciones y aplicó V40 sobre el volumen habitual; Hibernate inició
  con 51 repositorios y `/actuator/health` respondió `UP`. No se crearon calificaciones
  de prueba.
- Falta la prueba funcional autenticada del propietario y asignar el módulo al rol
  correspondiente. Al aprobar V40 continúa V41 con asistencia diaria por inscripción;
  V42 generará boletas Jasper derivadas de resultados publicados.

## V41 — asistencia diaria por grupo

- Se agregó `asistencia` con una fila por inscripción y fecha, vinculada también al grupo
  histórico. Admite los estados Presente, Ausente, Retardo y Justificada, observación
  opcional, auditoría y versión optimista; la base impide duplicar al alumno en el día.
- La captura administrativa trabaja por grupo y fecha. Obtiene únicamente inscripciones
  cuya asignación al grupo y vigencia académica incluyen ese día, propone Presente para
  registros nuevos y guarda la hoja completa dentro de una sola transacción.
- El servicio rechaza grupos inactivos, fechas fuera del ciclo, listas incompletas o
  desactualizadas, alumnos ajenos y estados faltantes. Una corrección posterior reutiliza
  la misma fila y verifica su versión para evitar sobrescrituras concurrentes.
- El módulo `Asistencia` incluye historial paginado, búsqueda y filtros por estado,
  alcance institucional o de plantel y exportación XLSX con exactamente los mismos
  criterios. La ayuda contextual, el menú, los temas y el diseño responsivo siguen los
  componentes comunes del sistema.
- Se agregaron `ASISTENCIA_LEER` y `ASISTENCIA_ADMINISTRAR` sin asignación automática.
  El autocompletado de grupos queda disponible para el permiso de administración y no se
  incorporaron horarios, clases ni docentes fuera del alcance acordado.
- Docker compiló 584 fuentes y 91 fuentes de prueba; ejecutó 361 pruebas sin fallos.
  Flyway validó 41 migraciones y avanzó el volumen local de V35 a V41; Hibernate inició
  con 52 repositorios y `/actuator/health` respondió `UP`. No se crearon asistencias de
  prueba.
- Falta la prueba funcional autenticada del propietario y asignar el módulo al rol
  correspondiente. Al aprobar V41 continúa V42 con boletas Jasper derivadas únicamente
  de calificaciones publicadas.

## V42 — boletas académicas derivadas (2026-09-28)

- Se implementó `/admin/boletas` con filtros por institución, ciclo, plantel, grupo
  histórico y alumno, además de paginación de base de datos y alcance institucional o de
  plantel.
- La fuente única son filas `calificacion` en estado `PUBLICADA` cuyo plan
  `materia_grado.incluir_boleta` está activo. No se creó tabla de boleta ni se persistieron
  promedios, totales o copias de resultados.
- Se agregó PDF Jasper individual y colectivo. Cada alumno inicia página, el documento se
  abre en otra pestaña y conserva periodo, materia, resultado, escala congelada y
  observaciones. Jasper usa un `JRDataSource` de servicios sin SQL propio.
- Se agregó Excel streaming con Apache POI. PDF colectivo y Excel reutilizan exactamente
  los filtros de pantalla y obtienen alumnos en bloques de 100.
- El autocompletado de grupos acepta históricos del ciclo seleccionado, muestra diez al
  enfocar y busca desde tres caracteres. El permiso nuevo `BOLETA_CONSULTAR` no se asigna
  automáticamente.
- `V42__boletas_academicas.sql` agregó el permiso y un índice parcial de apoyo, sin nueva
  entidad operativa. Docker compiló 593 fuentes y 93 fuentes de prueba; 363 pruebas
  pasaron. La prueba V42 generó y leyó el XLSX con POI, creó el PDF real, extrajo sus
  textos y renderizó la primera página.
- La imagen se desplegó; Flyway validó 42 migraciones, avanzó de V41 a V42 y
  `/actuator/health` respondió `UP`. No se crearon datos académicos de prueba ni se leyó
  `.env`.
- Pendiente del propietario: asignar Boletas al rol elegido y probar con calificaciones
  controladas los filtros, PDF individual, PDF colectivo y Excel. V43 queda libre y debe
  definirse después de aprobar esta etapa.

## V43 funcional — boletas PDF en el portal familiar (2026-09-28)

- Se incorporó `/portal/boletas`, accesible con el permiso ya existente
  `PORTAL_TUTOR_ACCEDER`. La pantalla pagina en base de datos las inscripciones del hijo
  seleccionado que tienen resultados `PUBLICADA` en materias incluidas en boleta.
- La interfaz muestra tarjetas por ciclo con plantel, grado, grupo, cantidad de materias
  y periodos. Respeta la identidad visual, los temas, el selector de hijos, estados
  vacíos, paginación y navegación móvil del portal familiar.
- El PDF reutiliza el generador Jasper oficial de V42 y se sirve en línea, en otra
  pestaña. Antes de generarlo se valida que tutor, vínculo, alumno, institución e
  inscripción correspondan a la sesión; no se exponen resultados en borrador.
- Soporte administrativo puede recorrer la misma sección y abrir el mismo PDF bajo
  `/admin/portal-soporte/{tutorId}`, con permiso y auditoría propios.
- Se extrajo un servicio común de detalle de boleta para evitar reglas divergentes entre
  administración y familia. No se crearon tablas ni copias de calificaciones.
- Docker compiló 596 fuentes principales y 94 fuentes de prueba; las 365 pruebas pasaron.
  La imagen se desplegó, Flyway validó V1–V42 y Actuator respondió `UP`. La prueba visual
  autenticada queda a cargo del propietario porque no se leyeron credenciales de `.env`.
- Aunque la etapa funcional se llama V43, no existe migración V43: ese número continúa
  disponible para el siguiente cambio de esquema.

## V43 — portal docente y planeaciones semanales (2026-09-28)

- Se agregó el expediente de maestro, una cuenta `PORTAL_MAESTRO` separada y asignaciones
  vigentes por maestro, grupo y materia. Administración las mantiene desde
  `/admin/maestros`; una materia sólo puede asignarse si está activa y configurada para
  el grado del grupo.
- El acceso propio `/maestros/acceso` conduce a un portal responsivo con temas claro y
  oscuro. Cada maestro sólo lista y modifica sus propias planeaciones y únicamente puede
  elegir grupos y materias asignados durante todo el rango capturado.
- La planeación es una captura estructurada basada en los formatos de preescolar y
  primaria: propósito, situación didáctica, ejes, conocimientos, habilidades, actitudes,
  alineación curricular, actividades por fecha/materia, recursos, evaluación, ajustes y
  observaciones. Admite un rango flexible máximo de siete días.
- La base evita traslapes para un mismo maestro y grupo mientras la planeación no esté
  descartada. Una misma semana puede tener planeaciones distintas por grupo y combinar
  sólo el subconjunto de materias que realmente se trabajará.
- El flujo es Borrador → Enviada → En revisión → Publicada. Administración puede marcar
  Requiere ajustes; sólo entonces vuelve a ser editable. Una publicación queda bloqueada
  y sólo se reabre con motivo obligatorio, actor y auditoría. Cada nueva publicación
  conserva una instantánea JSON inmutable para consultar el PDF histórico.
- `/admin/planeaciones` pagina y filtra en PostgreSQL. Su Excel streaming con Apache POI
  reutiliza los mismos filtros; los PDF actual e históricos se abren en otra pestaña.
  Los permisos nuevos no se asignan automáticamente a roles existentes.
- `V43__portal_maestros_planeaciones.sql` agregó el dominio, restricciones e índices.
  Docker ejecutó 372 pruebas sin fallos; Flyway validó 43 migraciones, Hibernate inició
  con 57 repositorios y `/actuator/health` respondió `UP`. El acceso público docente
  respondió HTTP 200 con token CSRF. No se crearon maestros ni planeaciones de prueba y
  no se leyó `.env`.
- Pendiente del propietario: asignar permisos a un rol y recorrer con datos controlados
  alta, cuenta, asignaciones, borrador, envío, revisión, ajustes, publicación, reapertura,
  segunda publicación, versiones PDF, filtros, Excel, tema oscuro y móvil. La siguiente
  migración disponible es V44; su alcance todavía no está acordado.

## Estabilización académica y visual posterior a V43 (2026-09-29)

- Se diagnosticó en los registros reales PostgreSQL `42P10` al cargar Calificaciones:
  `select distinct` ordenaba por apellidos que no estaban en la selección. Calificaciones
  y Asistencia comparten ahora una consulta correlacionada con `exists`, sin duplicados y
  compatible con PostgreSQL.
- El control **Horas semanales** cambió de paso `0.25` a `0.01`; acepta `5`, `4.5` y
  otras cantidades positivas representables por la columna decimal.
- El PDF colectivo fallaba únicamente en la imagen de producción porque Jasper invocaba
  un compilador inexistente en el JRE. La etapa final de Docker usa JDK 21 y se comprobó
  `javac 21.0.12.1` dentro del contenedor.
- El formulario de Maestros quedó dividido en identidad institucional, datos personales,
  credenciales y asignaciones usando el patrón visual común. El acceso docente reutiliza
  el login principal y se corrigió la interferencia de la ayuda contextual con su rejilla.
- Las 373 pruebas pasaron. La prueba del PDF usa la ruta colectiva, valida contenido,
  renderiza la primera página y su revisión visual confirmó encabezado, alumno, materias,
  resultados, observaciones y pie. La imagen se desplegó, V1–V43 quedó validado y salud
  respondió `UP`.

## Menú administrativo por categorías (2026-09-29)

- `navigation.js` convierte las etiquetas y enlaces ya autorizados por el servidor en
  secciones plegables. Las categorías repetidas se fusionan sin mostrar módulos para los
  que el usuario no tiene permiso.
- La categoría del módulo activo se abre automáticamente. El usuario puede abrir o cerrar
  cualquier categoría y la selección se conserva entre pantallas mediante almacenamiento
  local; si éste está bloqueado, el acordeón continúa funcionando sin persistencia.
- Trayectoria quedó unificada y ordenada como Inscripciones, Calificaciones, Asistencia,
  Boletas y Planeaciones. Personas se ordena como Alumnos, Tutores, Vínculos y Maestros.
- La prueba automatizada nueva elevó el total a 374. La revisión en navegador confirmó una
  sola etiqueta Trayectoria, apertura por clic, módulo activo visible y persistencia tras
  recargar.

## Diseño de cuenta y asignaciones del maestro (2026-09-29)

- Al editar un maestro, **Portal de maestros** presenta la cuenta en tarjetas legibles,
  distingue el estado y jerarquiza activación, recuperación y desactivación.
- **Grupos y materias asignadas** usa el mismo patrón visual: formulario separado, contador,
  estados vigente/finalizada, acciones y estado vacío compacto. Ambos bloques responden en
  móvil y tienen variantes para tema oscuro.
- Se añadió cobertura estructural a `PlaneacionInterfazTest`; las 374 pruebas pasaron, la
  vista se comprobó en escritorio y 390×844, y la imagen final quedó desplegada con salud
  `UP`.

## Correcciones transversales administrativas (2026-09-29)

- Los errores al abrir Calificaciones y Asistencia eran `LazyInitializationException` al
  recorrer grupo → plantel → institución fuera de sesión. `GrupoRepository` devuelve ahora
  el ID de institución mediante una proyección directa y ambos controladores tienen pruebas
  de regresión.
- El PDF colectivo no fallaba por ausencia de registros: los logs mostraron compilación de
  expresiones Jasper sin acceso a las librerías anidadas. `jasperreports-jdt` y ECJ quedaron
  incluidos en el JAR ejecutable; la prueba genera, lee y renderiza el PDF.
- El acceso del tutor al Portal de familias fue rediseñado con tarjetas, estados, acciones,
  modo oscuro y móvil. Planeaciones alineó sus acciones de filtro y la ayuda contextual
  impone globalmente su geometría y colores sobre cualquier formulario especializado.
- La validación final ejecutó 377 pruebas sin fallos, revisó la interfaz en escritorio y
  390×844 y desplegó la imagen con salud `UP`.
- El autocompletado de **Nueva asignación** en Maestros dejó de heredar el fondo blanco y
  los bordes de los botones del formulario. La protección quedó definida como regla global
  para resultados, opciones, texto informativo y control de limpieza, con variantes clara
  y oscura; el rojo queda reservado para errores reales y la lista no desborda en móvil.
- Las pantallas operativas de Calificaciones y Asistencia usan ahora el mismo lenguaje
  visual administrativo al seleccionar y al abrir una hoja: hero, panel de parámetros,
  resumen en tarjetas, tabla de captura, acciones, modo oscuro y tarjetas por alumno en
  móvil. No se modificaron sus rutas, validaciones ni flujo de publicación/guardado.
- Las confirmaciones de publicación y reapertura de Calificaciones son modales del sistema,
  no `confirm()` de JavaScript. Conservan el envío original con CSRF, explican el efecto
  sobre el portal familiar y admiten teclado, foco contenido, tema oscuro y móvil.

## Expedientes de maestros y tutores por pestañas (2026-09-30)

- El formulario de edición de Maestros se dividió en Ficha, Portal docente y Grupos y
  materias; el de Tutores en Ficha, Identificación oficial y Portal familiar.
- Ambos reutilizan `student-tabs.js` y el lenguaje visual de Alumnos, con navegación por
  teclado, hashes propios, diseño responsivo y variantes clara/oscura.
- Los controladores abren la pestaña correcta después de crear o administrar una cuenta,
  actualizar identificación, gestionar asignaciones o devolver errores de validación.
- Se agregó cobertura estructural de las dos plantillas. Docker ejecutó 388 pruebas sin
  fallos, desplegó la imagen y salud respondió `UP`. La sesión del navegador de validación
  estaba expirada; no se leyeron credenciales ni `.env` para forzar una revisión visual.

## Login docente e historial tabular de planeaciones (2026-09-30)

- El login docente dejó de compartir la identidad azul del administrador. Una hoja propia
  aplica violeta, coral y dorado, composición editorial y marca Nexo Docente en tema claro
  y oscuro. Se revisó en escritorio y 390×844 sin desbordamientos.
- El inicio del portal docente presenta las planeaciones en tabla, ordenadas por semana y
  paginadas en bloques de 20. En pantallas pequeñas las filas se convierten en tarjetas.
- El maestro puede filtrar por rango de fechas que se traslapa con la semana planeada y por
  estado. Los criterios se ejecutan en PostgreSQL, se conservan en la paginación y siempre
  incluyen el identificador del maestro autenticado.
- La acción abre directamente la edición sólo en estados editables; en los demás abre el
  detalle. Se agregaron pruebas del controlador, rango inválido y contrato visual.
- Docker ejecutó 390 pruebas sin fallos y desplegó la imagen. No se requirió migración ni
  se leyó `.env`.

## Filtros independientes de planeaciones administrativas (2026-09-30)

- El filtro general de `/admin/planeaciones` se reemplazó por tres criterios independientes:
  Maestro, Grupo y Propósito. Maestro y Grupo seleccionan IDs exactos mediante el componente
  común de autocompletado; Propósito es una búsqueda textual exclusiva sobre ese campo.
- Al enfocar Maestro o Grupo se consultan 10 opciones iniciales. A partir de tres caracteres
  se realiza la búsqueda paginada en base de datos, conservando el alcance institucional del
  usuario y permitiendo consultar planeaciones históricas de registros inactivos.
- Todos los criterios se pueden combinar con institución, plantel, estado y fechas. Se
  mantienen al cambiar de página y la exportación Excel recibe el mismo filtro exacto.
- La plantilla cuenta con diseño responsivo y temas claro/oscuro. Se añadieron pruebas para
  las sugerencias y el contrato de la interfaz; Docker aprobó 392 pruebas, desplegó la imagen
  y el servicio respondió `UP`. No se modificó el esquema ni se leyó `.env`.

## Orden técnico automático en catálogos (2026-09-30)

- Se retiró la captura de orden de Niveles, Grados, Periodos de evaluación y la configuración
  de Materias por grado. Los DTO de entrada tampoco aceptan el valor y las ediciones
  preservan el dato que exista en base de datos.
- Flyway V46 actualizó los registros existentes para usar `orden = id` y creó triggers
  `BEFORE INSERT` en las cuatro tablas. La columna continúa disponible para ajustes
  excepcionales directos en PostgreSQL, sin exponer complejidad técnica al usuario.
- Niveles y Grados dejaron de mostrar la columna en pantalla y Excel; los autocompletados
  académicos tampoco mencionan el orden. Una prueba estructural protege los cuatro
  formularios y la migración.
- V46 quedó aplicada, la comprobación devolvió `0|0|0|0` diferencias y confirmó los cuatro
  triggers. Docker aprobó 393 pruebas y la siguiente migración disponible es V47.

## Comprobante oficial de pago y Adeudos de alumnos (2026-09-30)

- Los pagos `VALIDADO` exponen un comprobante Jasper en Administración, Portal familiar y
  Portal tú. Todos los enlaces abren una pestaña nueva y los endpoints responden PDF
  `inline` con `no-store`; no provocan una descarga automática.
- El documento usa datos reales del pago y de su institución, detalla cada aplicación por
  alumno y conserva totales, cuenta, referencia, validación y movimiento financiero. El
  logo institucional configurado tiene prioridad y existe un recurso gráfico provisional
  para instituciones que todavía no tengan logo.
- La autorización familiar exige coincidencia de institución y tutor propietario, además
  del estado validado. Soporte administrativo reutiliza esa verificación y audita la
  consulta. El endpoint administrativo conserva alcance institucional y autoridades de
  Pagos.
- El catálogo técnico **Cargos** se presenta actualmente como **Adeudos de alumnos** en el catálogo de
  administración, permisos, textos de ayuda y pantallas de alta, detalle y generación. Se
  conservaron el slug `cargos`, URLs, entidades y permisos `CARGO_*` como contrato interno.
- La prueba de exportación genera, lee y renderiza una página A4; la inspección visual no
  mostró textos cortados, superposiciones ni problemas de jerarquía. Docker aprobó 399
  pruebas. No se creó migración y V47 sigue disponible.

## Expediente de inscripción por pestañas (2026-09-30)

- La edición de Inscripciones comparte el navegador accesible de expedientes: una pestaña
  presenta ficha del alumno y datos de inscripción, y otra concentra grupos, historial y
  continuidad académica.
- Los hashes `#datos-inscripcion` y `#grupos-inscripcion` permiten abrir directamente cada
  bloque. Los errores de actualización seleccionan la ficha y los errores/operaciones de
  grupo conservan la pestaña de trayectoria.
- El estilo tiene bordes conectados al navegador, dos columnas en escritorio, desplazamiento
  controlado en móvil y variantes de tema oscuro. El alta nueva no muestra pestañas porque
  todavía carece de historial.
- La prueba estructural protege el contrato común y Docker aprobó 400 pruebas. No se cambió
  el esquema, no se desplegó la imagen y V47 permanece disponible.

## Historial familiar de pagos por alumno (2026-10-02)

- La sección **Tus pagos** del portal familiar dejó de presentar transferencias como una
  lista de tarjetas y ahora usa una tabla paginada y responsiva con filtros de mes y año.
- Cada fila incluye fecha, folio/referencia, método, importe aplicado al hijo seleccionado,
  estado y acceso al comprobante PDF cuando el pago ya fue validado.
- El repositorio limita los resultados por usuario tutor, institución y alumno a través de
  `solicitud_aplicacion_pago → cargo → inscripcion`. Esto evita mezclar pagos entre hermanos
  y conserva correctamente la distribución por alumno de un pago familiar conjunto.
- Los filtros se resuelven en base de datos usando la zona horaria institucional. Los años
  disponibles también se consultan para el alumno autorizado y los criterios se mantienen
  durante la paginación.
- El Portal tú administrativo reutiliza exactamente la misma consulta y presentación. Se
  añadieron pruebas de servicio e interfaz; `docker compose build app` compiló 701 fuentes
  principales y 123 de prueba y ejecutó 441 pruebas sin fallos. No hubo migración ni
  despliegue del contenedor.

## Captura simplificada del periodo de un cargo (2026-10-02)

- El formulario manual de cargos ofrece tres intenciones comprensibles: mes completo,
  fecha específica y rango personalizado. Los campos técnicos de inicio y fin sólo se
  muestran para el último caso.
- `CargoForm` deriva autoritativamente `periodoCobroInicio` y `periodoCobroFin`: primer y
  último día del mes, la misma fecha en ambos extremos, o el rango capturado. El controlador
  valida los datos obligatorios de cada modalidad antes de construir la solicitud.
- La fecha de vencimiento se presenta como **Fecha límite para pagar** y una tarjeta viva
  resume cobertura y vencimiento. JavaScript sólo mejora la interacción; el cálculo de
  negocio permanece en servidor.
- Se agregó `CargoFormTest` con los tres modos y se amplió la prueba de interfaz. Docker
  compiló 702 fuentes principales y 124 de prueba y aprobó 444 pruebas. La imagen fue
  desplegada, salud respondió `UP` y no se requirió migración.

## Fecha de registro protegida en cargos (2026-10-02)

- La fecha de emisión se presenta al usuario como **Fecha de registro del cargo**, se
  inicializa con el día actual de la institución y queda bloqueada de forma predeterminada.
- Un control explícito permite registrar excepcionalmente una fecha anterior. Al activarlo,
  la fecha se habilita y el motivo se vuelve obligatorio; las fechas futuras se rechazan.
  Si el control permanece apagado, el servidor ignora cualquier fecha manipulada y usa su
  propio día institucional.
- V53 agrega a `cargo` la columna nullable `motivo_fecha_registro_diferente`, protegida
  contra cadenas vacías. El mapper persiste el motivo normalizado y el detalle del cargo lo
  muestra como trazabilidad administrativa.
- El formulario conserva el resumen vivo, diseño responsivo y temas claro/oscuro. Se
  amplió la cobertura del formulario y del servicio; Docker ejecutó 445 pruebas sin fallos,
  Flyway aplicó V53 y `/actuator/health` respondió `UP`. La siguiente migración disponible
  es V54.

## Ayuda contextual excluida del orden de tabulación (2026-10-02)

- Se confirmó como regla de experiencia que los iconos informativos son ayuda secundaria:
  se activan con clic, pero no deben interrumpir la captura al navegar con `Tab`.
- `contextual-help.js` asigna `tabIndex = -1` tanto a los iconos generados junto a campos
  como al botón de información general del módulo. El alcance global incluye formularios,
  filtros, login, portal familiar, portal docente y controles creados dinámicamente.
- La ayuda de campo conserva el control asociado como destino al cerrar el modal. Así, un
  clic informativo no rompe la posición de captura y el siguiente `Tab` continúa hacia el
  siguiente control operativo.
- `AyudaContextualInterfazTest` protege la exclusión de ambos tipos de ayuda del recorrido.
  `node --check` aprobó la sintaxis y `docker compose build app` compiló 702 fuentes de
  producción y 124 de prueba y ejecutó 445 pruebas sin fallos. El cambio no agregó una
  migración. La imagen se desplegó, Flyway actualizó esta base desde V40 hasta V53 y Spring
  Boot inició en el puerto 8080; la siguiente migración disponible continúa siendo V54.

## Correcciones de presentación en recargos y becas (2026-10-02)

- La leyenda de **Días de gracia** se separó visualmente de los errores mediante
  `field-help`; conserva un tono neutro compatible con tema claro y oscuro.
- `BecaAlumnoServiceImpl` usa un `DateTimeFormatter` explícito `dd/MM/yyyy` al informar el
  rango permitido por la inscripción. La prueba reproduce el caso del 29/09/2026 al
  15/06/2027 y evita regresar accidentalmente al formato ISO.
- Se establece como contrato de interfaz que toda fecha visible para personas use
  `dd/MM/yyyy`; `yyyy-MM-dd` se reserva para la capa técnica.
- No hubo migración. `docker compose build app` compiló 702 fuentes principales y 125 de
  prueba y ejecutó 447 pruebas sin fallos. La imagen se desplegó y salud respondió `UP`;
  la siguiente migración disponible es V54.

## Antecedente de usabilidad — límites de las políticas de recargo

El rediseño del 06/10/2026 documentado al inicio resuelve la captura pendiente. Se conserva
el tope en periodicidad única y se incorpora ejemplo calculado; falta confirmación funcional
del propietario del nuevo caso de tope, no una nueva implementación de estos controles.

- La prueba guiada de cobranza mostró que las etiquetas **Tipo de límite** y **Valor del
  límite** no comunican por sí solas qué se está limitando ni cómo interviene en el cálculo.
- Se deberá rediseñar esa captura con términos operativos, ayuda visible y ejemplos numéricos
  que distingan sin tope, tope monetario y porcentaje máximo del importe original. Debe
  aclararse que el tope se aplica al total acumulado de recargos automáticos.
- Para periodicidad única se evaluará una opción predeterminada o una captura simplificada.
  Es una mejora pendiente de interfaz; en esta decisión no se implementó código.

## Corrección de captura de pagos parciales (2026-10-05)

- Devoluciones incorpora elección Total/Parcial y vista previa de efectos por alumno. Total
  calcula el importe de los abonos seleccionados más disponible sin aplicar; Parcial admite
  un importe menor. Se distinguen dinero devuelto, abono remanente, deuda recuperada y saldo
  posterior por cargo. Se verificaron cálculos en centavos para total, parcial, disponible
  previo y varios alumnos; el servidor sigue validando saldos y fondos antes de publicar.

- Cuentas financieras muestra **Saldo actual** en listado, Excel y edición. La consulta toma
  sólo el último movimiento por secuencia de cuenta y usa el saldo inicial si no existen
  movimientos, sin cargar todo el historial. Pruebas verifican ambos casos.

- Al aplicar un ajuste rechazado por reglas del concepto, el controlador reconstruía el
  detalle sin `puedeRegistrarPago`; Thymeleaf fallaba al convertir null a boolean y ocultaba
  el mensaje de negocio. La preparación compartida ahora incluye ese permiso y la vista
  utiliza comparación booleana explícita. Pruebas cubren rechazo de descuento y errores de
  captura conservando datos y permisos. La prueba local del cargo de $400 confirmó que su
  concepto no permitía descuentos ni recargos; esas opciones deben habilitarse desde el
  catálogo para ejecutar el escenario previsto.

- La prueba integral de cobranza detectó que al cambiar una distribución de `$1,000` a
  `$300`, el formulario conservaba el total recibido en `$1,000` y producía `$700` como
  dinero sin asignar. Ahora cualquier cambio en los importes distribuidos sincroniza el
  total recibido con su suma, evitando una recepción accidental mayor al efectivo real.
- La terminología visible cambió a **Total recibido**, **Distribuido entre cargos** y
  **Dinero pendiente de asignar**. El pago parcial informa además el saldo estimado que
  conservará el cargo después de validarlo.
- La pestaña Distribución del detalle de un pago pendiente también presenta por cargo su
  saldo actual y el saldo estimado posterior a la validación, evitando confundir dinero no
  asignado con deuda todavía pendiente del alumno.
- Adeudos de alumnos ahora muestra **Parcial** cuando un cargo tiene aplicaciones y conserva
  saldo. El detalle incorpora **Pagado acumulado**, de modo que un cargo de `$1,000` con
  `$300` abonados comunica directamente los `$700` exigibles.
- La tabla de Pagos recibidos incorpora total de cargos vinculados y saldo actual por pagar,
  separados del dinero recibido. Distribución también muestra estos valores después de la
  validación: cargo `$1,000`, abono de este pago `$300`, saldo actual `$700`. Los saldos se
  actualizan con aplicaciones posteriores; los totales de tabla deduplican cargos vinculados.
- El total sigue siendo editable después de la sincronización para conservar el caso
  válido de dinero recibido todavía no asignado. No hubo cambio de esquema. La construcción
  Docker aprobó las 462 pruebas del proyecto.

## Convenios de pago (V54, 2026-10-02)

- Se agregó un expediente formal de convenios por institución y tutor. Sus detalles enlazan
  tanto los cargos originales como los cargos nuevos, con importes históricos y distribución
  por inscripción.
- Los originales no se eliminan: cambian a `CONVENIDO`, conservan ajustes y aplicaciones y
  dejan de integrar el saldo exigible. Los estados de cuenta los identifican como
  `CONVENIDO`; los concentrados evitan duplicar el adeudo.
- La captura usa autocompletado y búsqueda limitada a 20 cargos. Admite varios hermanos,
  calcula el saldo vigente en servidor y reparte el acuerdo proporcionalmente con precisión
  de centavos.
- Se rechazan transferencias pendientes sobre los cargos seleccionados. La reversión del
  convenio sólo se habilita cuando sus cargos nuevos no tienen pagos aplicados.
- Se agregó `CONVENIO_PAGO_ADMINISTRAR`, protegido también en Spring Security. V54 lo concede
  inicialmente a roles que ya tenían `CARGO_ADMINISTRAR`; los demás se administran desde
  Roles y permisos.
- Incluye listado paginado, filtros, detalle histórico, Excel por bloques, diseño responsivo
  y estados derivados. Flyway aplicó V54 y el despliegue respondió `UP`; V55 queda libre.

## Navegación administrativa por proceso (2026-10-03)

- La navegación lateral ahora se divide en **Operación escolar** y **Administración**. Sus
  subsecciones siguen el proceso de trabajo y no el orden histórico de implementación.
- Se homologaron los nombres funcionales **Periodos de evaluación**, **Familiares del
  alumno**, **Adeudos de alumnos**, **Pagos recibidos** y **Soporte del portal familiar**.
  La pantalla de Roles obtiene estos mismos nombres desde `ModuloPermiso`.
- La generación automática ya describe adeudos por generar. “Pago” se reserva para dinero
  recibido; “cargo” permanece como nombre interno y en contextos financieros donde identifica
  la obligación concreta a la que se aplica un abono.
- No cambiaron slugs, rutas, permisos técnicos ni esquema. Se añadieron contratos de prueba
  para las secciones, la ruta de Adeudos y los nombres visibles de Roles. Docker compiló
  719 fuentes principales y 129 de prueba y aprobó 456 pruebas; V55 continúa disponible.

## Correcciones del reporte familiar y ayudas de formularios (2026-10-03)

- El plantel del reporte familiar se deriva autoritativamente de los cargos y dejó de ser una
  decisión del tutor. Las cuentas bancarias se consultan mediante un cargo autorizado; no por
  un identificador de plantel manipulable.
- Se permiten varios hijos mientras todos los cargos correspondan al mismo plantel. Una mezcla
  de planteles permanece en el formulario con una indicación para separar las transferencias.
- Los montos individuales y el total son campos calculados, visibles con formato de pesos y de
  sólo lectura; el servidor vuelve a calcularlos antes de registrar.
- El selector remoto de cargos abre resultados con foco o clic y conserva importes y cuenta
  después de una validación. Las fechas de vencimiento se presentan como `dd/MM/yyyy`.
- La ayuda contextual de cada campo ya no usa un botón labelable dentro de la etiqueta, por lo
  que sólo abre el modal al pulsar directamente el icono y continúa fuera del recorrido Tab.
- Se clasificaron todos los `th:errors` como `field-error`; las leyendas informativas quedan en
  tono neutro y sólo las validaciones reales usan rojo.
- No se agregó migración. La sintaxis JavaScript y el diff se verificaron. Maven local compiló
  719 fuentes principales y 129 de prueba y aprobó las 459 pruebas completas. Docker Desktop
  falló internamente al escribir sus bases de BuildKit/containerd; no se desplegó una imagen
  nueva y hay que reparar o reiniciar Docker antes del siguiente despliegue.

## Despliegue y comprobación de la ayuda contextual (2026-10-04)

- Se confirmó que la aplicación en ejecución todavía servía el script anterior con un
  `button` dentro de `label`; en el login administrativo se reprodujo el modal al pulsar
  la etiqueta lejos del icono. El origen del fallo persistente era la imagen Docker antigua.
- Docker volvió a funcionar: `docker compose build app` compiló 719 fuentes principales,
  129 de prueba y aprobó 459 pruebas. Se recreó sólo `app` con
  `docker compose up -d --no-deps app`; la base de datos y los volúmenes no se tocaron.
- El servicio respondió `UP` y sirvió el script nuevo con `span.context-help-trigger`.
  Tras recargar, se verificó en los login administrativo y familiar que un clic fuera del
  icono no abre modal, un clic directo sí, y Tab omite la ayuda y pasa a la contraseña.
- No se modificó código ni esquema en esta comprobación. Si una pestaña antigua conserva el
  script anterior en caché, recargarla por completo antes de repetir la prueba.

## Homologación visual administrativa y planeaciones (2026-10-05)

- Los títulos de área del menú lateral quedaron diferenciados de los acordeones mediante
  icono, divisor y colores propios; continúan siendo texto no interactivo.
- Actualizaciones familiares, Horarios y clases, Convenios de pago, Proveedores y Compras
  reutilizan las reglas base de `admin.css` y `forms.css`. Se descartó la hoja visual
  paralela porque podía forzar superficies blancas y separarse del lenguaje ya establecido;
  filtros, botones e inputs quedan homologados desde las clases comunes y respetan el tema
  oscuro.
- Proveedores y Compras muestran secciones internas `form-section` para identificación,
  origen y partidas. Horarios adoptó la estructura estándar `form-top`, `form-intro` y
  `entity-form`. No cambió el modelo de datos ni los flujos existentes.
- Los enlaces **Revisar** y **Regresar al listado** de Planeaciones tienen tratamiento de
  botón administrativo y variantes compatibles con el tema oscuro.
- El error al revisar una planeación provenía de la diferencia entre `estadoNuevo` usado en
  Thymeleaf y `nuevo` expuesto por `HistorialPlaneacionFila`. El DTO conserva ahora el alias
  de lectura requerido y una prueba evita la regresión.
- `node --check` y `git diff --check` finalizaron correctamente. La imagen Docker compiló
  719 fuentes principales y 130 de prueba y aprobó 461 pruebas. Se desplegó sólo el servicio
  `app`; PostgreSQL permaneció intacto, Flyway confirmó V54 y los recursos públicos
  respondieron HTTP 200. No hubo migración y V55 continúa libre.
