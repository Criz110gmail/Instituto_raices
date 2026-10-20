-- Documentación del recorrido confirmado el20/10/2026. No aplica bajas ni cambia roles.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES ('empleado-baja-laboral-historial',
$texto$Baja laboral: confirmación e historial sin borrar el expediente$texto$,'Personal',
$texto$Recorrido confirmado: baja válida, corrección de motivo conservando entradas previas y Cancelar sin guardar. Fotografía, perfil, acceso y planteles permanecen iguales. Activo en el listado corresponde al expediente, no a la situación laboral. No se declara probada una baja con cuenta docente activa ni todas las validaciones negativas.$texto$,
$texto$Cuenta administrativa institucional identificable con Guía de procesos, lectura de Empleados y administración de Datos laborales. Empleado de capacitación autorizado, nunca un trabajador real que siga activo para reproducir un ejemplo. Reutiliza EMP-ADM-PRUEBA-01 si sólo consultarás el resultado: ya está en Baja laboral y no debe darse de baja otra vez. Esta guía no ejecuta operaciones ni otorga accesos.$texto$,
$texto$María Prueba Administrativa, Secretaria administrativa, ingreso 01/10/2026 y baja 20/10/2026. El historial confirmado conserva dos motivos iniciales a13:20/13:22 y a13:24 el texto ESTE TEXTO NO DEBE GUARDARSE, porque se pulsó Confirmar y guardar. Después se aclaró el uso de Cancelar y el propietario confirmó que no guarda. No se borra ninguna entrada para limpiar el ensayo.$texto$,
1,DATE '2026-10-20','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN ('EMPLEADO_LEER','EMPLEADO_LABORAL_ADMINISTRAR')
WHERE g.slug='empleado-baja-laboral-historial';

-- Retira referencias a la baja como pendiente en las guías de Personal ya publicadas.
UPDATE guia_proceso_paso SET
precaucion=$texto$No se asigna sueldo ni se calcula nómina. Para la baja consulta la guía independiente Baja laboral: confirmación e historial sin borrar el expediente; no repitas operaciones ya confirmadas.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='empleado-administrativo-sin-perfil-docente') AND numero=5;

UPDATE guia_proceso_paso SET
instrucciones=$texto$1. Conserva un solo expediente y las seis etapas del caso: dos hasta19/10, dos del20/10 y dos desde21/10. 2. Comprueba que el aviso coincide con la fecha institucional y no repitas la operación. 3. Si tienes permiso de Auditoría, consulta Administración → Seguridad y soporte → Auditoría, acción EMPLEADO_PRINCIPAL_CAMBIADO, para revisar usuario, motivo y fecha efectiva; no concedas permisos para seguir la guía. 4. Consulta la guía Baja laboral: confirmación e historial sin borrar el expediente para el caso ya confirmado; después continúa con permisos privados, sin declarar terminada nómina.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='empleado-planteles-cambio-principal') AND numero=8;

UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-20'
WHERE slug IN ('empleado-administrativo-sin-perfil-docente','empleado-planteles-cambio-principal');

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,'/admin/empleados'
FROM guia_proceso g CROSS JOIN (VALUES
(1,$texto$Abrir el expediente existente$texto$,$texto$Administración → Personal y nómina → Empleados$texto$,
$texto$1. En administración abre Personal y nómina → Empleados. 2. Busca EMP-ADM-PRUEBA-01 y pulsa Ver expediente. 3. Abre Datos laborales y revisa la situación guardada. 4. Si ya está en Baja laboral, consulta el historial y los resultados; no repitas el guardado para seguir la guía.$texto$,
$texto$María Prueba Administrativa, un solo expediente. El ejercicio fue realizado sobre personal ficticio.$texto$,
$texto$Empleado identificado antes de modificar información.$texto$,
$texto$No recrees al empleado, su foto o sus asignaciones. No uses una baja ficticia en personal operativo real.$texto$),
(2,$texto$Comprobar ingreso y alcance del cambio$texto$,$texto$Expediente → Datos laborales$texto$,
$texto$1. Revisa Puesto y Fecha de ingreso laboral. 2. Para un ensayo nuevo autorizado conserva puesto y fecha de ingreso originales. 3. Si la fecha guardada no aparece, recarga la versión actual del sistema: el calendario utiliza formato ISO interno y presentación según el navegador. 4. Distingue la situación laboral de la disponibilidad del expediente, del perfil docente y de su cuenta.$texto$,
$texto$Puesto Secretaria administrativa; ingreso 01/10/2026. La corrección de formato permite ver la fecha al volver a abrir, sin invertir día y mes.$texto$,
$texto$Ingreso visible y sin cambios accidentales. Baja laboral es una operación independiente de las otras funciones.$texto$,
$texto$Guardar datos personales no sustituye Guardar información laboral. No cambies la fecha de ingreso para hacer que una baja incorrecta pase la validación.$texto$),
(3,$texto$Revisar las validaciones de fecha y motivo$texto$,$texto$Datos laborales → Situación, Fecha de baja y Motivo de baja$texto$,
$texto$1. En un ensayo nuevo selecciona Baja laboral. 2. Comprueba que requiere fecha y motivo: un intento sin esos datos debe rechazarse al validar en servidor. 3. Una fecha de baja anterior al ingreso o futura debe rechazarse. 4. Corrige la captura antes de continuar, conservando el puesto e ingreso legítimos.$texto$,
$texto$Con ingreso 01/10/2026, baja 30/09/2026 no es válida; baja 20/10/2026 con motivo sí es válida si no es futura. Adapta fechas a la operación autorizada.$texto$,
$texto$Sólo una baja con información válida puede guardarse.$texto$,
$texto$Estas validaciones están implementadas y cubiertas técnicamente; no se afirma aceptación humana individual de todos los intentos negativos. Si un inválido se guarda, detén la prueba y reporta, sin borrar historial.$texto$),
(4,$texto$Cancelar dentro del modal sin guardar$texto$,$texto$Datos laborales → Guardar información laboral → Confirmación$texto$,
$texto$1. Para una operación nueva prepara datos válidos y pulsa Guardar información laboral. 2. Espera la ventana Confirmar información laboral y revisa situación, puesto, fechas y motivo. 3. Pulsa Cancelar dentro de esa ventana; no es un botón del formulario principal. 4. Recarga y comprueba que permanece el último motivo realmente guardado y que no aparece una nueva entrada por cancelar.$texto$,
$texto$Texto temporal de capacitación: ESTE TEXTO NO DEBE GUARDARSE. Sólo se descarta si se pulsa Cancelar; si se pulsa Confirmar y guardar, sí debe registrarse.$texto$,
$texto$Cancelar cierra el modal y conserva la captura sin enviarla; al recargar se muestra lo persistido. Comportamiento confirmado por el propietario.$texto$,
$texto$Cerrar el modal no guarda. Confirmar y guardar hace lo contrario. Si se guardó un texto temporal por confirmación, conserva esa entrada como trazabilidad; no la elimines.$texto$),
(5,$texto$Confirmar una baja válida$texto$,$texto$Datos laborales → Confirmar información laboral$texto$,
$texto$1. Sólo si la baja nueva es correcta captura Situación Baja laboral, fecha y motivo; conserva puesto e ingreso. 2. Pulsa Guardar información laboral. 3. Revisa el resumen y pulsa Confirmar y guardar. 4. Recarga el expediente y vuelve a Datos laborales para comprobar los valores guardados.$texto$,
$texto$Ingreso 01/10/2026; baja 20/10/2026; motivo Baja laboral de prueba para verificar fecha, motivo e historial del expediente. Puesto Secretaria administrativa.$texto$,
$texto$Situación BAJA con fecha y motivo persistidos; expediente conservado.$texto$,
$texto$El caso ya confirmado no requiere otra baja. La operación no genera liquidación, sueldo, nómina ni movimientos financieros.$texto$),
(6,$texto$Corregir el motivo conservando el anterior$texto$,$texto$Datos laborales e Historial laboral$texto$,
$texto$1. Si existe una corrección real autorizada del motivo, captura el nuevo texto en Datos laborales. 2. Guarda mediante el modal y confirma. 3. En Historial laboral comprueba una entrada nueva con fecha, usuario, situación, puesto, ingreso, baja y motivo. 4. Verifica que la entrada anterior sigue disponible; corregir no crea otro empleado ni elimina la baja previa.$texto$,
$texto$Historial confirmado: a13:20 Prueba de validación de fecha de baja.; a13:22 Baja laboral de prueba para verificar fecha, motivo e historial del expediente.; ambas con ingreso01/10 y baja20/10. A13:24 se confirmó además el texto temporal.$texto$,
$texto$Los motivos anteriores quedan trazados y el último guardado es el vigente.$texto$,
$texto$El historial no se edita ni borra. No cambies el motivo sólo para quitar una entrada que se confirmó por error.$texto$),
(7,$texto$Comprobar lo que la baja no modifica$texto$,$texto$Ficha del empleado, Información docente, Acceso y Planteles$texto$,
$texto$1. Abre Ficha del empleado y comprueba nombre, código y foto conservados. 2. En Información docente y Acceso comprueba que continúan sin perfil y sin cuenta, como antes. 3. En Planteles revisa las mismas etapas y el cambio de principal programado, sin finalizar ni cancelar nada. 4. Vuelve al listado y distingue Expediente Activo, mostrado en verde, de la situación laboral BAJA consultada en Datos laborales.$texto$,
$texto$El empleado administrativo no docente mantiene sus planteles, incluido el cambio desde21/10. Su expediente puede seguir Activo aunque su situación laboral sea Baja laboral.$texto$,
$texto$Identidad, perfil, acceso y planteles sin modificaciones automáticas, confirmado en este caso.$texto$,
$texto$Esta separación es intencional. Baja laboral no cierra adscripciones ni desactiva usuarios; esas acciones necesitan su propio flujo autorizado. Como María no tiene cuenta docente, no se considera validado aquí el caso de un maestro con cuenta activa.$texto$),
(8,$texto$Cerrar esta prueba y continuar con permisos privados$texto$,$texto$Guía de procesos → Personal$texto$,
$texto$1. Conserva la baja y todas las entradas reales del ensayo, incluido cualquier texto temporal confirmado. 2. Da por documentados guardado válido, corrección de motivo, cancelación sin guardado y conservación del expediente del ejemplo. 3. No reactives al empleado ni registres otra baja para cerrar la guía. 4. Continúa con una prueba independiente de permisos de información laboral antes de definir puestos, condiciones salariales y nómina.$texto$,
$texto$Confirmación humana final20/10/2026: Cancelar no guarda y los demás apartados permanecen iguales. No se ejecutaron bajas por el agente.$texto$,
$texto$Recorrido principal cerrado sin perder historial ni declarar terminado todo Personal.$texto$,
$texto$Permisos privados, reingresos, baja con cuenta docente activa y todas las variantes negativas siguen pendientes de sus propias verificaciones. Las pruebas automáticas no sustituyen la aceptación humana.$texto$)
) v(numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion)
WHERE g.slug='empleado-baja-laboral-historial';
