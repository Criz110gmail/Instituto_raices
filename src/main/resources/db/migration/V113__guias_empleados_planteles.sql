-- Sólo guías del recorrido confirmado por el propietario el 20/10/2026.
-- No registra empleados, adscripciones, cambios de principal ni permisos a roles.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES
('empleado-administrativo-sin-perfil-docente',
$texto$Registrar un empleado administrativo sin perfil docente$texto$,'Personal',
$texto$Caso confirmado: expediente único, fotografía, datos laborales, un plantel principal, edición de contacto y Excel filtrado, sin crear maestro ni usuario automáticamente. No incluye nómina ni valida por sí solo bajas o permisos privados entre cuentas.$texto$,
$texto$Cuenta administrativa institucional identificable, no de recuperación, con Guía de procesos, lectura y administración de Empleados y administración de Datos laborales. Institución y plantel activos. Usa capacitación o un alta autorizada: no existe borrado especial de prueba. Consultar esta guía no ejecuta operaciones; si el empleado ya existe, reutilízalo y no repitas su alta.$texto$,
$texto$EMP-ADM-PRUEBA-01, María Prueba Administrativa, puesto Secretaria administrativa, ingreso 01/10/2026, un plantel principal desde 01/10/2026. Correo ficticio maria.administrativa@example.invalid. Adapta fechas y datos a una operación autorizada; no uses datos ficticios en producción por seguir el ejemplo.$texto$,
1,DATE '2026-10-20','CONFIRMADA'),
('empleado-planteles-cambio-principal',
$texto$Varios planteles y cambio programado de principal$texto$,'Personal',
$texto$Confirmados el recorrido manual de dos planteles y el cambio programado con conservación de historial. El aviso distingue el principal de hoy del que comenzará después. El caso ya aplicado no debe repetirse: Parian (A) será principal desde 21/10/2026 y B adicional. La aceptación no abarca todas las variantes ni una baja laboral.$texto$,
$texto$Empleado existente y cuenta administrativa institucional con lectura de Empleados y administración de Datos laborales, además de Guía de procesos. Dos planteles activos de la misma institución. No cambia grupos, permisos o cuentas de acceso. Para consultar Auditoría se necesita su permiso específico; ese paso es opcional y no concede accesos. Conserva registros existentes y no programes otra operación sólo para leer esta guía.$texto$,
$texto$Ejemplo confirmado: A principal/B adicional del 01/10/2026 al 19/10/2026; B principal/A adicional el 20/10/2026; A principal/B adicional desde 21/10/2026 sin fin. A corresponde a Plantel Parian en esta instalación. Son seis etapas históricas, no seis asignaciones vigentes al mismo tiempo. Las fechas son evidencia del ensayo, no fechas que debas volver a capturar.$texto$,
1,DATE '2026-10-20','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN ('EMPLEADO_LEER','EMPLEADO_LABORAL_ADMINISTRAR')
WHERE g.slug IN ('empleado-administrativo-sin-perfil-docente','empleado-planteles-cambio-principal');
INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo='EMPLEADO_ADMINISTRAR'
WHERE g.slug='empleado-administrativo-sin-perfil-docente';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,'/admin/empleados'
FROM guia_proceso g CROSS JOIN (VALUES
(1,$texto$Localizar al empleado antes de registrar$texto$,$texto$Administración → Personal y nómina → Empleados$texto$,
$texto$1. Inicia con tu cuenta administrativa habitual y abre Administración → Personal y nómina → Empleados. 2. Busca el código laboral del ejemplo y aplica el filtro. 3. Si ya existe, abre su expediente y consulta los pasos sin registrar otro. 4. Sólo para un alta nueva autorizada pulsa Nuevo empleado.$texto$,
$texto$Buscar EMP-ADM-PRUEBA-01. El código laboral identifica el expediente, no es un nombre de usuario.$texto$,
$texto$Un expediente identificado o formulario nuevo listo, sin duplicar personal.$texto$,
$texto$Si no ves el módulo, una persona autorizada debe revisar tus permisos. Esta guía no otorga permisos ni recomienda usar el acceso de recuperación.$texto$),
(2,$texto$Capturar datos personales y confirmar$texto$,$texto$Nuevo empleado → Datos personales$texto$,
$texto$1. Selecciona institución y captura código laboral, nombres y primer apellido; segundo apellido, correo y teléfono son opcionales. 2. Pulsa Registrar empleado y revisa el resumen. 3. Para comprobar la cancelación en un alta de capacitación pulsa Cancelar: debe conservar lo escrito sin guardar. 4. Vuelve a Registrar empleado y confirma sólo si la información es correcta.$texto$,
$texto$Código EMP-ADM-PRUEBA-01; nombres María; primer apellido Prueba; segundo apellido Administrativa; correo maria.administrativa@example.invalid; teléfono vacío.$texto$,
$texto$Un empleado registrado, sin crear perfil docente, usuario, sueldo ni nómina.$texto$,
$texto$No vuelvas a registrar el ejemplo ya confirmado. Registrar un empleado y crear una cuenta de acceso son operaciones diferentes.$texto$),
(3,$texto$Guardar y comprobar la fotografía$texto$,$texto$Expediente → Ficha del empleado$texto$,
$texto$1. En el expediente abre Ficha del empleado. 2. En Fotografía del expediente pulsa Seleccionar o reemplazar imagen y elige un archivo autorizado. 3. Pulsa Guardar fotografía. 4. Recarga y comprueba que la imagen permanece junto al nombre y código.$texto$,
$texto$Imagen de capacitación JPG, PNG o HEIC de hasta 20 MB. El sistema optimiza la fotografía antes de guardarla.$texto$,
$texto$Fotografía del expediente visible, sin generar perfil de maestro o cuenta.$texto$,
$texto$La foto del expediente no sustituye la fotografía de acceso del usuario. No subas fotografías de otra persona sin autorización.$texto$),
(4,$texto$Comprobar que no se creó un maestro ni acceso$texto$,$texto$Expediente → Información docente y Acceso$texto$,
$texto$1. Abre Información docente y comprueba que indica que todavía no tiene perfil docente. 2. No pulses Habilitar perfil docente para este empleado administrativo. 3. Abre Acceso y comprueba Sin cuenta vinculada al portal de maestros. 4. Si consultas Operación escolar → Gestión académica → Maestros con tus permisos habituales, busca código y nombre: no debe aparecer como maestro.$texto$,
$texto$María es secretaria administrativa. Sus funciones no requieren grupos, materias ni portal docente.$texto$,
$texto$Identidad laboral sin perfil ni cuenta docente, como se confirmó en el ejercicio.$texto$,
$texto$Que exista el botón Habilitar perfil docente no obliga a utilizarlo. No crees una invitación o cambies roles para esta prueba.$texto$),
(5,$texto$Confirmar datos laborales y su historial$texto$,$texto$Expediente → Datos laborales$texto$,
$texto$1. Abre Datos laborales. 2. Selecciona Actividad laboral confirmada, captura Puesto y Fecha de ingreso laboral; deja baja y motivo vacíos para este ejemplo. 3. Pulsa Guardar información laboral, revisa el modal y confirma. 4. Consulta Historial laboral: debe conservar situación, puesto, ingreso, fecha de operación y usuario.$texto$,
$texto$Puesto Secretaria administrativa; ingreso 01/10/2026. La fecha de creación técnica del expediente no se toma como contratación.$texto$,
$texto$Información laboral privada guardada e historial disponible para la cuenta autorizada.$texto$,
$texto$No se asigna sueldo ni se calcula nómina. Baja laboral es un escenario independiente todavía pendiente de aceptación.$texto$),
(6,$texto$Asignar el primer plantel$texto$,$texto$Expediente → Planteles → Agregar plantel laboral$texto$,
$texto$1. Abre Planteles y localiza Agregar plantel laboral. 2. Enfoca Plantel para consultar hasta diez opciones; desde tres caracteres busca por código o nombre y selecciona una opción. 3. Captura Inicio, deja Fin previsto vacío y marca Plantel principal durante esta vigencia. 4. Pulsa Agregar asignación laboral, revisa y confirma.$texto$,
$texto$Plantel activo de la institución; inicio 01/10/2026; principal marcado; sin fecha de fin.$texto$,
$texto$Una tarjeta principal con fechas. Planteles muestra actuales, próximas e historial según la fecha institucional.$texto$,
$texto$No basta con escribir un nombre sin seleccionar la ayuda. Una adscripción laboral no asigna grupos ni concede permisos. Para más planteles consulta la guía Varios planteles y cambio programado de principal.$texto$),
(7,$texto$Editar contacto y exportar la consulta$texto$,$texto$Datos personales y listado de Empleados$texto$,
$texto$1. En Datos personales corrige el correo de contacto y pulsa Guardar datos personales; revisa y confirma. 2. Vuelve al listado, busca el código y aplica filtros: debe existir un solo empleado. 3. Abre el expediente y comprueba fotografía, información laboral y plantel conservados. 4. Regresa a la consulta filtrada y pulsa Exportar Excel.$texto$,
$texto$Correo de prueba actualizado: maria.administrativa.actualizada@example.invalid. Excel del filtro EMP-ADM-PRUEBA-01 con una fila de datos.$texto$,
$texto$Contacto actualizado sin duplicados; Excel conserva el universo filtrado y datos generales.$texto$,
$texto$Correo de contacto no cambia el correo de login. La exportación básica no expone el historial laboral privado. No repetir cambios ficticios sobre información operativa.$texto$)
) v(numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion)
WHERE g.slug='empleado-administrativo-sin-perfil-docente';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,'/admin/empleados'
FROM guia_proceso g CROSS JOIN (VALUES
(1,$texto$Reutilizar el expediente y reconocer los planteles$texto$,$texto$Administración → Personal y nómina → Empleados → Planteles$texto$,
$texto$1. Busca EMP-ADM-PRUEBA-01 en Empleados y abre el expediente existente. 2. Entra a Planteles y distingue Asignaciones actuales, Próximas asignaciones y Ver asignaciones anteriores. 3. Reconoce A y B por sus nombres reales, no por la posición de una tarjeta. 4. Lee el aviso superior: fecha de hoy, Principal actual y, si existe, Próximo plantel principal con fecha efectiva.$texto$,
$texto$En el ensayo, A es Plantel Parian y B es Plantel B. El 20/10/2026 sigue vigente B principal; A está programado desde 21/10/2026.$texto$,
$texto$Empleado y etapas identificados antes de capturar; no se altera información al consultar.$texto$,
$texto$No crees otro empleado ni otra asignación para repetir el ejemplo. Las fechas de esta guía son evidencia del caso confirmado; usa fechas válidas y autorizadas en nuevas operaciones.$texto$),
(2,$texto$Entender principal único y planteles adicionales$texto$,$texto$Planteles → Agregar plantel laboral$texto$,
$texto$1. Si necesitas asignar un segundo plantel en una operación nueva autorizada, selecciona otro activo de la misma institución. 2. Captura Inicio y Fin previsto; si ya existe un principal durante ese rango, deja desmarcado Plantel principal durante esta vigencia. 3. Revisa el modal de Agregar asignación laboral antes de confirmar. 4. Si intentas dos principales simultáneos se rechaza; corrige la captura en lugar de duplicar o borrar asignaciones.$texto$,
$texto$Preparación del ensayo: A principal/B adicional desde 01/10/2026. El intento de B también principal fue rechazado y después se registró como adicional.$texto$,
$texto$Puede trabajar en varios planteles con un único principal durante las mismas fechas.$texto$,
$texto$No se permite duplicar el mismo plantel con vigencias superpuestas. El bloqueo de dos principales fue confirmado; no es un error que deba omitirse.$texto$),
(3,$texto$Consultar el historial sin editarlo libremente$texto$,$texto$Planteles → Ver asignaciones anteriores$texto$,
$texto$1. Despliega Ver asignaciones anteriores. 2. Consulta nombre, inicio, fin y tipo registrado en cada tarjeta. 3. Reconoce las etapas A principal/B adicional finalizadas el 19/10/2026. 4. Conserva ese historial: para cambiar principal utiliza el asistente y no repitas las finalizaciones manuales del ejercicio anterior.$texto$,
$texto$La fecha final es inclusiva: una etapa termina 19/10 y la siguiente comienza 20/10. Principal en una tarjeta finalizada describe su función histórica.$texto$,
$texto$Historial visible y separado de lo vigente; no existen dos principales simultáneos por conservar tarjetas antiguas.$texto$,
$texto$Cada sección consulta páginas de20. Un cambio de plantel no borra etapas ni modifica asignaciones académicas, cuentas o permisos.$texto$),
(4,$texto$Preparar un cambio de principal$texto$,$texto$Planteles → Cambiar plantel principal$texto$,
$texto$1. Sólo para un cambio nuevo autorizado abre Cambiar plantel principal. 2. Captura Fecha efectiva del cambio, hoy o futura y posterior al inicio de las etapas que reemplazará. 3. En Nuevo plantel principal selecciona una opción de la ayuda de diez registros o busca desde tres caracteres. 4. Decide si Continuará en el plantel anterior como adicional y captura el Motivo del cambio obligatorio.$texto$,
$texto$Ensayo ya realizado: nuevo principal A desde21/10/2026, mantener B como adicional; motivo de reorganización administrativa. No vuelvas a capturarlo: ya está guardado.$texto$,
$texto$Captura preparada, todavía sin cambios mientras no se confirme.$texto$,
$texto$Si una etapa empezó hoy, su siguiente etapa debe comenzar al menos mañana. No se permite retroactividad ni reemplazar por el mismo que ya es principal para la fecha elegida. No fuerces fechas por base de datos.$texto$),
(5,$texto$Visualizar, cancelar o confirmar el cambio$texto$,$texto$Cambiar plantel principal → Visualizar cambio de principal$texto$,
$texto$1. Pulsa Visualizar cambio de principal y revisa en el modal principal reemplazado, cierre anterior, nuevo principal, fecha efectiva, fines previstos, continuidad del anterior y motivo. 2. Cancelar cierra el resumen sin guardar y conserva los campos; no equivale a cancelar una asignación existente. 3. Para una operación nueva correcta vuelve a visualizar y pulsa Confirmar cambio de principal. 4. Si los datos cambiaron en otra pestaña, vuelve a visualizar; no intentes repetir una confirmación aplicada.$texto$,
$texto$Cambio confirmado: B deja de ser principal al finalizar20/10; A será principal y B adicional desde21/10. Se conserva el historial y las seis etapas resultantes.$texto$,
$texto$El cambio se guarda completo en una transacción o no se guarda. No hay necesidad de finalizar y recrear manualmente cuatro etapas.$texto$,
$texto$Cancelar/Escape/fondo y protección ante concurrencia tienen cobertura técnica; no se declara por ello que todas sus variantes hayan sido ensayadas manualmente. Desmarcar continuidad es otra variante, no el resultado de este ejemplo.$texto$),
(6,$texto$Distinguir hoy del cambio programado$texto$,$texto$Planteles → Aviso superior y Próximas asignaciones$texto$,
$texto$1. Lee el aviso Cambio de principal programado encima de las tarjetas. 2. Comprueba Principal actual con la fecha de hoy y Próximo plantel principal con Desde el. 3. Abre Próximas asignaciones para consultar las etapas nuevas; las actuales siguen mostrando lo vigente hoy. 4. Desde la fecha efectiva vuelve a consultar la pantalla: las nuevas etapas aparecen como actuales y las anteriores pasan al historial, sin confirmar otra vez.$texto$,
$texto$20/10/2026: B principal actual; Parian(A) principal desde21/10/2026. El aviso aclara que el nuevo principal todavía no está vigente. El propietario confirmó que esta presentación quedó clara.$texto$,
$texto$La fecha efectiva explica las tarjetas; que B sea principal hoy no significa que haya fallado el cambio a A.$texto$,
$texto$No cambies el reloj ni reprogrames la operación para comprobar el paso del día. La actualización se consulta según la fecha institucional; no requiere un nuevo guardado. La observación del día siguiente no se da por confirmada por anticipado.$texto$),
(7,$texto$Reconocer el mensaje de cambio ya existente$texto$,$texto$Planteles → Cambiar plantel principal$texto$,
$texto$1. Si aparece Ese plantel ya es principal en la fecha elegida, detén el intento. 2. Revisa el aviso y Próximas asignaciones para esa fecha. 3. Comprueba si el cambio deseado ya está programado y conserva los registros correctos. 4. No canceles ni vuelvas a confirmar el mismo cambio sólo para completar esta guía.$texto$,
$texto$Al elegir A otra vez para21/10 se encontró que Parian(A) ya era principal desde esa fecha. La lectura del caso confirmó6 filas y ningún cambio se realizó al diagnosticar.$texto$,
$texto$Se evita duplicar una operación ya aplicada; el mensaje es una validación, no una pérdida del cambio.$texto$,
$texto$Si lo mostrado no corresponde al empleado o a tus fechas, solicita revisión antes de alterar vigencias. No asumas que seleccionar otra fecha arbitraria corrige el problema.$texto$),
(8,$texto$Cerrar el caso y reconocer lo pendiente$texto$,$texto$Expediente, Planteles y Auditoría autorizada$texto$,
$texto$1. Conserva un solo expediente y las seis etapas del caso: dos hasta19/10, dos del20/10 y dos desde21/10. 2. Comprueba que el aviso coincide con la fecha institucional y no repitas la operación. 3. Si tienes permiso de Auditoría, consulta Administración → Seguridad y soporte → Auditoría, acción EMPLEADO_PRINCIPAL_CAMBIADO, para revisar usuario, motivo y fecha efectiva; no concedas permisos para seguir la guía. 4. Continúa después con la prueba independiente de baja laboral y permisos privados, sin declarar terminada nómina.$texto$,
$texto$Sin movimientos financieros, usuarios nuevos ni cambios de grupos por intercambiar el principal. Auditoría del asistente está implementada y probada técnicamente; su consulta humana separada no se presupone.$texto$,
$texto$Caso confirmado de planteles y claridad del aviso documentado. Conservación del historial sin operaciones adicionales.$texto$,
$texto$Baja laboral, continuidad desmarcada, aislamiento entre cuentas, otras concurrencias y toda la nómina requieren sus propias verificaciones. Un caso CONFIRMADA no representa aceptación de todo Personal.$texto$)
) v(numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion)
WHERE g.slug='empleado-planteles-cambio-principal';
