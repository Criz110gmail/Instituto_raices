# Continuidad del proyecto — Nexo Escolar

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
