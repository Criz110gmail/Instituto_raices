-- Sólo contenido editorial del recorrido confirmado el 19/10/2026.
-- No crea docentes, planeaciones, publicaciones ni asignaciones de permisos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES ('planeaciones-revision-versiones-pdf',
$texto$Planeaciones docentes: captura, revisión y versiones PDF$texto$,'Gestión académica',
$texto$Recorrido confirmado por el propietario el 19/10/2026: borrador, envío, revisión, ajustes, corrección, publicación inicial, reapertura con motivo y nueva publicación. Las dos versiones PDF conservan su contenido y el maestro no puede editar una planeación publicada. Consulta este procedimiento sin repetir operaciones sobre el registro ya validado.$texto$,
$texto$Maestro activo con cuenta docente activada y grupo/materias asignados por administración durante toda la semana. Grupo y asignación dentro del ciclo vigente. Administración con acceso a Planeaciones y alcance institucional compatible; revisar, publicar y reabrir requieren sus permisos específicos. Usa sesiones separadas para maestro y administrador, por ejemplo una ventana privada: /maestros/acceso es el login docente, no /familias. Una planeación por maestro/grupo y rango flexible de hasta siete días; no superponer otra del mismo maestro/grupo. Reutiliza registros existentes; no crees otra cuenta ni cambies roles sólo para consultar.$texto$,
$texto$Ejemplo orientativo: Matemáticas, semana 19/10/2026 a 23/10/2026, actividad Sumas con fichas, propósito Resolver sumas sencillas con material concreto y explicar los resultados. Adapta grupo, materias y fechas a la asignación real. Primera publicación con recursos originales; después de reabrir, recursos Fichas de colores, hojas blancas y lápices. Resultado: una planeación y dos publicaciones históricas. La aceptación no cierra Empleados, nómina ni todas las variantes docentes.$texto$,
1,DATE '2026-10-19','CONFIRMADA');

-- Lectura de guía y lectura del módulo; nunca exigir el rol docente al administrador.
INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo='PLANEACION_LEER'
WHERE g.slug='planeaciones-revision-versiones-pdf';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.* FROM guia_proceso g CROSS JOIN (VALUES
(1,$texto$Comprobar acceso, grupo y semana$texto$,'Administración y maestro',$texto$Maestros y Portal de maestros$texto$,
$texto$1. Administración entra a Operación escolar → Gestión académica → Maestros, localiza al maestro y abre Editar; verifica cuenta docente activa y Grupos y materias asignadas. 2. Comprueba que grupo, ciclo y vigencia de la asignación cubren todas las fechas de la semana. Si el perfil ya existe, no lo dupliques. 3. En una sesión separada, el maestro inicia desde /maestros/acceso y entra a Mis planeaciones. 4. Comprueba que no exista otra planeación del mismo grupo en ese rango; si vas a corregir una, abre la existente.$texto$,
$texto$Grupo con Matemáticas asignada del 19/10/2026 al 23/10/2026. Si tu ciclo o asignación no cubren esas fechas, adapta el ejemplo antes de capturar.$texto$,
$texto$Maestro con acceso a su portal, grupo y materias disponibles para esa semana. Todavía no se ha enviado ni publicado una planeación.$texto$,
$texto$El acceso docente no concede permisos administrativos. El portal familiar no es el login del maestro. No alteres permisos ni datos en base de datos para forzar el ejemplo.$texto$,'/admin/maestros'),
(2,$texto$Capturar grupo, materias y propósito$texto$,'Maestro',$texto$Mis planeaciones → Nueva planeación$texto$,
$texto$1. Portal de maestros → Mis planeaciones → Nueva planeación. 2. En Grupo y semana captura inicio y fin, selecciona grupo y marca sólo las materias que trabajarás. 3. En Objetivos completa Propósito. 4. En Alineación curricular elimina la tarjeta vacía si no aplica; si conservas una, completa campo formativo, contenido y proceso de desarrollo de aprendizaje.$texto$,
$texto$Semana 19/10/2026 a 23/10/2026; Matemáticas. Propósito: Resolver sumas sencillas con material concreto y explicar los resultados.$texto$,
$texto$Captura preparada en las pestañas del formulario; aún no guardada.$texto$,
$texto$No necesitas seleccionar todas las materias del grupo. General permite una alineación no ligada a una materia, pero no vuelve opcionales los campos obligatorios de esa tarjeta. Cambiar pestaña no guarda automáticamente.$texto$,NULL),
(3,$texto$Completar actividades y guardar borrador$texto$,'Maestro',$texto$Captura → Actividades y Recursos y evaluación$texto$,
$texto$1. En Actividades agrega al menos una actividad con fecha dentro de la semana, materia seleccionada, título, inicio, desarrollo y cierre. 2. En Recursos y evaluación captura los materiales originales; duración y campos de evaluación pueden ser opcionales, pero administración puede solicitarlos. 3. Pulsa Guardar planeación; si faltan obligatorios, corrige los campos rojos y las pestañas señaladas. 4. Con captura válida revisa maestro, grupo, semana y materias en el modal; Confirmar guardado guarda el borrador. Cancelar sólo cierra el modal y conserva la captura sin guardarla.$texto$,
$texto$19/10/2026; Matemáticas; Sumas con fichas; inicio Repasar los números; desarrollo Resolver sumas por equipos; cierre Compartir resultados. Duración opcional 45 minutos. Recursos iniciales: Fichas y cuaderno.$texto$,
$texto$Estado Borrador. Guardar no envía a revisión ni publica.$texto$,
$texto$No salgas con cambios sin guardar. Las validaciones indican la pestaña y el campo por corregir; completa lo obligatorio antes de esperar una confirmación.$texto$,NULL),
(4,$texto$Revisar el PDF y enviar a administración$texto$,'Maestro',$texto$Resumen y envío y Documento PDF$texto$,
$texto$1. En la misma planeación alterna Captura, Resumen y envío, Documento PDF y Gestión. 2. En Documento PDF pulsa Abrir PDF en otra pestaña y revisa la última captura guardada. 3. Si cambias algo, vuelve a Captura → Guardar planeación → Confirmar guardado. 4. En Resumen y envío pulsa Enviar a revisión; Cancelar cierra sin enviar. Reabre el modal cuando corresponda y pulsa Confirmar envío.$texto$,
$texto$Mismo grupo, semana y Matemáticas; el resumen debe coincidir con lo guardado.$texto$,
$texto$Estado Enviada; edición docente bloqueada. PDF visible en nueva pestaña sin descarga automática.$texto$,
$texto$El PDF no incluye cambios sin guardar. El visor permite descargar después si lo deseas. Gestión → Descartar no es un paso obligatorio y no forma parte del recorrido aceptado.$texto$,NULL),
(5,$texto$Iniciar revisión y solicitar ajustes$texto$,'Administración',$texto$Operación escolar → Gestión académica → Planeaciones$texto$,
$texto$1. En administración abre Operación escolar → Gestión académica → Planeaciones; filtra por maestro, grupo y semana y pulsa Revisar en el registro Enviada. 2. En el detalle abre Revisión → Iniciar revisión → Confirmar revisión; pasa a En revisión. 3. Consulta el contenido en Planeación y vuelve a Revisión; escribe Correcciones solicitadas. 4. Pulsa Solicitar ajustes, comprueba el motivo del modal y Confirmar solicitud. Cancelar no solicita ajustes y conserva el texto.$texto$,
$texto$Motivo: Agregar cómo se evaluará la actividad y qué evidencia entregará el alumno.$texto$,
$texto$Estado Requiere ajustes; motivo trazado y edición docente habilitada.$texto$,
$texto$Las pestañas son Planeación, Revisión, Versiones PDF e Historial. No publiques si necesitas correcciones. Las acciones requieren permisos y estado compatibles, además de poder leer el módulo.$texto$,'/admin/planeaciones'),
(6,$texto$Leer correcciones, modificar y reenviar$texto$,'Maestro',$texto$Mis planeaciones → misma planeación → Captura$texto$,
$texto$1. Portal de maestros → Mis planeaciones → abre el mismo registro; lee la tarjeta ámbar Correcciones solicitadas por administración, con fecha y texto. 2. En Captura → Recursos y evaluación completa técnica e instrumento; en Actividades completa la evidencia solicitada en el cierre. 3. Pulsa Guardar planeación → Confirmar guardado. 4. En Resumen y envío pulsa Enviar a revisión → Confirmar envío.$texto$,
$texto$Técnica: Observación del trabajo en clase. Instrumento: Lista de cotejo. Cierre: El alumno entregará una hoja con cinco sumas resueltas.$texto$,
$texto$Estado Enviada; edición bloqueada. La corrección deja de presentarse como pendiente, pero el motivo no se borra del historial.$texto$,
$texto$No crees otra planeación para responder. La tarjeta administrativa no es un error de validación ni el campo editable Observaciones generales; permanece mientras se encuentre en Requiere ajustes.$texto$,NULL),
(7,$texto$Publicar y abrir la primera versión PDF$texto$,'Administración',$texto$Planeaciones → Revisar → Revisión$texto$,
$texto$1. Administración → Operación escolar → Gestión académica → Planeaciones; localiza el mismo registro y pulsa Revisar → Revisión. 2. Iniciar revisión → Confirmar revisión; verifica las correcciones en Planeación o en Abrir PDF actual del encabezado. 3. Vuelve a Revisión → Publicar planeación; revisa el resumen del modal y pulsa Confirmar publicación. Cancelar no publica ni genera una versión. 4. Tras el éxito se abre Versiones PDF; pulsa Ver PDF · Versión 1 — Publicación inicial.$texto$,
$texto$El PDF histórico conserva las actividades, evaluación y recursos originales de la primera publicación: Fichas y cuaderno.$texto$,
$texto$Estado Publicada; Versión 1 disponible en nueva pestaña. El maestro ve Publicada, puede consultar PDF y no editar.$texto$,
$texto$La fecha pequeña bajo el botón es la publicación, formato dd/MM/yyyy · HH:mm en la zona institucional; no es la semana planeada. Guardar y cambiar estado no generan por sí solos un PDF histórico.$texto$,'/admin/planeaciones'),
(8,$texto$Reabrir con motivo sin perder lo publicado$texto$,'Administración',$texto$Planeaciones → Revisar → Revisión$texto$,
$texto$1. En administración → Planeaciones localiza la misma planeación Publicada y pulsa Revisar → Revisión. 2. Escribe el Motivo de reapertura obligatorio. 3. Pulsa Reabrir para edición y comprueba el motivo en el modal; Cancelar no reabre ni pierde el texto. 4. Sólo si corresponde pulsa Confirmar reapertura y comprueba el estado y el historial.$texto$,
$texto$Motivo: Actualizar los materiales necesarios para la actividad del viernes.$texto$,
$texto$Estado Reabierta; maestro puede editar y ve una tarjeta con el motivo. Versión 1 permanece conservada.$texto$,
$texto$Reabrir no borra la publicación anterior ni crea otra planeación. No repitas la reapertura sobre el caso ya validado sólo por consultar esta guía.$texto$,'/admin/planeaciones'),
(9,$texto$Actualizar materiales y publicar de nuevo$texto$,'Maestro y administración',$texto$Portal docente y Planeaciones administrativas$texto$,
$texto$1. Maestro → Mis planeaciones → mismo registro Reabierta → Captura → Recursos y evaluación → Recursos; actualiza materiales. 2. Guardar planeación → Confirmar guardado; Resumen y envío → Enviar a revisión → Confirmar envío. 3. Administración → Operación escolar → Gestión académica → Planeaciones → mismo registro → Revisar → Revisión → Iniciar revisión → Confirmar revisión; comprueba los nuevos recursos en Planeación. 4. Vuelve a Revisión → Publicar planeación → Confirmar publicación.$texto$,
$texto$Nuevos recursos: Fichas de colores, hojas blancas y lápices.$texto$,
$texto$Estado Publicada nuevamente, edición bloqueada y dos publicaciones del mismo registro.$texto$,
$texto$No son dos planeaciones duplicadas. Cada publicación conserva una copia independiente del contenido, no cada cambio de estado.$texto$,'/admin/planeaciones'),
(10,$texto$Comparar versiones y cerrar el recorrido$texto$,'Administración y maestro',$texto$Versiones PDF, Historial y portal docente$texto$,
$texto$1. Administración → Planeaciones → mismo registro → Revisar → Versiones PDF; abre Ver PDF · Versión 1 — Publicación inicial. 2. Abre Ver PDF · Versión 2 — Nueva publicación y compara los materiales; los enlaces abren otra pestaña, sin descargar automáticamente. 3. En Historial consulta estados y motivos; en Portal de maestros → Mis planeaciones → mismo registro comprueba Publicada, PDF disponible y ausencia de edición. 4. Conserva este recorrido como confirmado el 19/10/2026; no publiques ni reabras otra vez para cerrar la prueba.$texto$,
$texto$Versión 1 conserva materiales anteriores. Versión 2 contiene Fichas de colores, hojas blancas y lápices. Puede aparecer primero la versión más reciente; el número identifica la publicación, no la posición del botón.$texto$,
$texto$Cierre humano confirmado: ajustes, corrección, reapertura, segunda publicación y conservación de ambos PDF. Una planeación y dos versiones históricas.$texto$,
$texto$PDF actual e histórico tienen funciones distintas. Descarte, concurrencia, solapamientos y aislamiento entre maestros siguen pendientes de aceptación específica. Este cierre no valida todo Empleados ni nómina. No confundas pruebas automáticas con aceptación humana.$texto$,'/admin/planeaciones')
) AS v(numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
WHERE g.slug='planeaciones-revision-versiones-pdf';
