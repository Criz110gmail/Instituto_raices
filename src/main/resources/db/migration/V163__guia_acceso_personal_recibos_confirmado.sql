-- Documentación editorial del recorrido confirmado. No crea cuentas ni concede permisos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('acceso-personal-recibos-confirmado','Recibos: habilitar el acceso personal del empleado','Personal',
'Configurar de forma opcional el acceso del empleado para consultar y descargar sus propios recibos, conservar su historia y poder desactivar o reactivar el acceso.',
'Administrador institucional identificado con acceso autorizado a Empleados y administración salarial, más Guía de procesos para consultar. El empleado y sus recibos ya deben existir. La guía no crea cuentas ni concede roles; su lectura no modifica nóminas, préstamos o saldos.',
'El propietario confirmó el 06/11/2026 el recorrido con Sofía Prueba Neto Cero, EMP-PREST-CERO-01: al deshabilitar el acceso dejó de ver el módulo y al habilitarlo de nuevo volvió a verlo. Su recibo probado corresponde a percepciones $100.00, deducciones $100.00 y neto $0.00. No repetir pagos para consultar el caso.',
1,DATE '2026-11-06','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN ('EMPLEADO_LEER','CONDICION_SALARIAL_LEER')
WHERE g.slug='acceso-personal-recibos-confirmado';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,v.perfil,v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,v.ruta
FROM guia_proceso g CROSS JOIN (VALUES
(1,'Identificar al empleado','Administración','Personal y nómina → Empleados',
'1. Busca al empleado por código y confirma su identidad. 2. Abre Editar → Acceso → Administrar acceso a sus recibos. 3. Comprueba el estado de la cuenta antes de actuar.',
'Sofía Prueba Neto Cero, EMP-PREST-CERO-01. Existen otras personas de prueba llamadas Sofía: identifica el código.',
'Acceso y cuenta del empleado identificados.','No crear una segunda cuenta si ya existe una vinculada; no compartir contraseñas.','/admin/empleados'),
(2,'Habilitar y entregar invitación','Administración','Empleados → Acceso a recibos',
'1. Para una persona sin cuenta revisa el usuario sugerido, cámbialo si es necesario y captura un correo. 2. Pulsa Habilitar acceso a recibos; revisa y confirma el modal. 3. Copia y comparte en privado el enlace mostrado. 4. Para una cuenta invitada existente usa Generar enlace de contraseña si ya no tienes la invitación.',
'El caso de Sofía usa una cuenta personal vinculada y activada durante el ensayo. No se envían correos automáticos.',
'Invitación de un uso vigente 48 horas; una nueva invalida la anterior.','Habilitar acceso no requiere volver a preparar ni pagar una nómina. No publiques el enlace.','/admin/empleados'),
(3,'Consultar el recibo propio','Empleado','Portal de personal',
'1. La persona abre su invitación y define la contraseña. 2. Entra al acceso del personal. 3. Abre Mis recibos y consulta o imprime el PDF. 4. Si hay un maestro vinculado con cuenta docente, administración puede habilitar recibos en ese empleado; con la misma cuenta el portal docente ofrece Mis recibos.',
'En Sofía el recibo es de neto cero, con percepciones $100.00 y deducciones $100.00.',
'El empleado consulta sus propios recibos y el PDF conserva percepciones y deducciones por nombre.',
'El comprobante es interno, no CFDI. La navegación docente compartida tiene cobertura técnica; no se atribuye aceptación humana separada.','/admin/empleados'),
(4,'Desactivar y volver a habilitar','Administración y empleado','Empleados → Acceso a recibos',
'1. En el acceso del empleado pulsa Desactivar acceso a recibos y confirma. 2. Comprueba que la persona ya no accede al módulo. 3. Si corresponde, habilita de nuevo el acceso y verifica que lo vea otra vez.',
'El usuario confirmó que al quitar el acceso Sofía dejó de ver el módulo y que volvió a verlo tras habilitarlo.',
'Recorrido de desactivación y reactivación confirmado el 06/11/2026.','La historia de recibos se conserva. En cuenta compartida desactivar recibos no desactiva el portal docente.','/admin/empleados'),
(5,'Recuperar la contraseña','Administración','Empleados → Acceso a recibos',
'1. Si la cuenta ya está activa y necesita una nueva contraseña, genera un enlace de contraseña. 2. Compártelo de forma privada para que la persona defina su contraseña. 3. La recuperación es de un solo uso y dura 30 minutos.',
'Si la cuenta sigue invitada se genera una invitación de activación de 48 horas; si ya está activa se genera una recuperación de 30 minutos.',
'La persona recupera su acceso sin crear otra identidad.','Con cuenta docente compartida la contraseña es la misma para ambos accesos y generar el enlace requiere permiso para administrar Maestros.','/admin/empleados')
) AS v(n,t,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
WHERE g.slug='acceso-personal-recibos-confirmado';
