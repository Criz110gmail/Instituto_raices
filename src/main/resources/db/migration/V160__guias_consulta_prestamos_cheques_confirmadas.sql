-- Cierre de aceptación humana: sólo guías y requisitos de lectura, sin concesiones.
UPDATE guia_proceso SET
 requisitos=replace(requisitos,'La prueba manual de rol sólo consulta sigue pendiente; esta guía no concede permisos.',
 'Prueba manual de rol sólo consulta confirmada el 06/11/2026: lectura y Excel permitidos; nuevo, editar y reprogramar denegados también por URL. Esta guía no concede permisos.'),
 ejemplo=ejemplo||' El usuario también confirmó el rol separado Préstamos - Consultar: consulta y Excel correctos, sin acciones de modificación; rutas de nuevo, editar y reprogramar muestran No tienes acceso a esta información. No se modificaron los permisos del administrador para esta prueba.',
 version_contenido=version_contenido+1,revisada_el=DATE '2026-11-06'
 WHERE slug='prestamos-programados-nomina';

UPDATE guia_proceso_paso s SET
 precaucion='No generar pagos para probar exportaciones. La consulta sin escritura también fue confirmada por separado: ver paso 14. No se acredita concurrencia ni paginación masiva con esta aceptación.'
 FROM guia_proceso g WHERE s.guia_id=g.id AND g.slug='prestamos-programados-nomina' AND s.numero=13;

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT id,14,'Verificar un usuario de sólo consulta','Administración / Consulta',
'Roles; Usuarios; Personal y nómina → Préstamos',
'1. Este ensayo ya fue confirmado; consulta su configuración sin repetir altas. 2. El ejemplo utiliza un rol separado con únicamente Préstamos - Consultar y un usuario de alcance Institución, sin otros roles, permisos salariales ni financieros. 3. En una sesión separada, ese usuario puede ver préstamos, resumen, cuotas, historial y Excel filtrado. 4. No debe ver Nuevo préstamo ni acciones de modificación. 5. Abrir /admin/prestamos/nuevo, /admin/prestamos/2/editar o /admin/prestamos/2/reprogramar como lector muestra No tienes acceso a esta información. 6. La prueba no requiere guardar, entregar, reprogramar ni cambiar permisos del administrador.',
'Rol propuesto PRUEBA_PRESTAMOS_LECTURA, usuario prueba.prestamos.lectura, institución Instituto Raices. El usuario confirmó que todas las pruebas pasaron y las rutas protegidas mostraron No tienes acceso a esta información.',
'Prueba manual confirmada el 06/11/2026: lectura y Excel permitidos, escritura denegada por menú y URL.',
'No quitar derechos a criz ni conceder Administrar para hacer funcionar una consulta. No confundir un préstamo liquidado con autorización de edición. No acredita pruebas de escritura forzada ni instituciones ajenas.',
'/admin/prestamos'
FROM guia_proceso WHERE slug='prestamos-programados-nomina';

INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('consulta-cheques-cuenta-confirmada','Cheques: consulta, filtros y Excel sin mover dinero','Finanzas',
'Consulta los cheques emitidos y cancelados de una cuenta bancaria. El usuario confirmó filtros, exportaciones Excel y validación de fechas sin salto de pantalla. Consultar no emite ni cancela documentos.',
'Cuenta administrativa identificable con acceso autorizado a la cuenta bancaria y consulta de chequeras. La guía no asigna roles ni concede permisos. El caso existente ya terminó: no repetir los pagos de Clara ni emitir o cancelar nuevamente los cheques 1001 y 1002. La aceptación funcional se realizó con el administrador; no acredita un ensayo separado de lector de cheques.',
'Caso confirmado el 06/11/2026: BBVA_COBROS, BBVA — Chequera de prueba. Cheque 1001 por $100.00 de Clara Prueba Cheque, NPI-17, Cancelado · No reutilizable; cheque 1002 por $100.00, NPI-18, Emitido. Los filtros y Excel distinguen ambos estados. Fechas invertidas del 04/11/2026 al 03/11/2026 muestran un aviso junto a los filtros, conservan captura y permiten desplazarse sin saltos tras la corrección confirmada. BBVA $174.29, CAJA $315.11 y CAJA-CORTE-TEST $1,150.00, sin nuevos movimientos por consultar o exportar.',
1,DATE '2026-11-06','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN ('CHEQUERA_LEER','CUENTA_FINANCIERA_LEER')
WHERE g.slug='consulta-cheques-cuenta-confirmada';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Abrir Cheques, no confundir con Chequeras','Finanzas → Cuentas financieras → BBVA_COBROS',
'1. Busca BBVA_COBROS. 2. Pulsa Chequeras y abre la pestaña Cheques, o pulsa Ver cheques en la fila de BBVA — Chequera de prueba. 3. Chequeras muestra un rango 1001–1050 de 50 folios; no es el listado de documentos emitidos. 4. En Cheques consulta los documentos 1001 y 1002 existentes.',
'Cheque 1001 cancelado, NPI-17; cheque 1002 emitido, NPI-18. Ambos de Clara Prueba Cheque y $100.00.',
'Cuenta, chequera histórica y estados identificados sin crear registros.',
'Emitido no confirma que el banco haya cobrado el cheque. No crear otra chequera ni repetir pagos para obtener la tabla.','/admin/catalogos/cuentas-financieras'),
(2,'Filtrar estados y comparar Excel','Cuenta bancaria → Cheques',
'1. Selecciona Emitidos y aplica: debe aparecer 1002. 2. Exporta Excel con ese filtro y compara importe y estado; no debe incluir 1001. 3. Cambia a Cancelados, aplica y exporta: sólo 1001, con su cancelación conservada. 4. Los indicadores resumen todos los resultados del filtro, no sólo la página visible.',
'En esta base: un emitido por $100.00 y un cancelado por $100.00. El Excel respeta el estado seleccionado.',
'Consulta y Excel confirmados por el usuario.',
'No cancelar un cheque emitido para probar un filtro. Puede haber más documentos si existen operaciones posteriores.','/admin/catalogos/cuentas-financieras'),
(3,'Buscar beneficiario, folio, fechas y chequera','Cuenta bancaria → Cheques → Filtros',
'1. Pulsa Limpiar, busca Clara y aplica: aparecen ambos documentos del ejemplo. 2. Filtra folio exacto 1002: sólo el emitido; Excel debe coincidir. 3. Limpia y usa las fechas de emisión que muestra la tabla, 03/11/2026 en el ejemplo. 4. Enfoca Chequera para ver hasta diez sugerencias, escribe tres caracteres y selecciona BBVA — Chequera de prueba. 5. Desde la fila de esa chequera, Ver cheques abre la misma consulta con la selección precargada.',
'Clara localiza 1001 y 1002. El folio exacto 1002 no incluye 1001.',
'Filtros y navegación del caso confirmados, sin nuevas operaciones.',
'Usa las fechas reales que muestra la pantalla si otros datos son distintos. Ver historial de chequera consulta el catálogo, no sustituye Ver cheques.','/admin/catalogos/cuentas-financieras'),
(4,'Validar fechas invertidas sin salto de pantalla','Cuenta bancaria → Cheques → Emisión desde / hasta',
'1. El usuario probó desde 04/11/2026 y hasta 03/11/2026. 2. Aplicar muestra Revisa los filtros y explica que la fecha inicial de emisión no puede ser posterior a la final. 3. Ambas fechas permanecen capturadas y el aviso está dentro de los filtros. 4. Desplazarse arriba y abajo no devuelve automáticamente al aviso ni al botón. 5. Corrige el rango o pulsa Limpiar para continuar sin aviso.',
'El fallo visual original se corrigió: una sola restauración de posición y ningún ajuste tardío después de interacción manual. El usuario confirmó que pasó la prueba.',
'Validación y corrección visual aceptadas el 06/11/2026.',
'No se modifica la validación del servidor ni los cheques para resolver un problema visual.','/admin/catalogos/cuentas-financieras'),
(5,'Comprobar saldos e historial sin operaciones','Cuentas financieras; Movimientos financieros',
'1. Al terminar consulta BBVA_COBROS, CAJA y CAJA-CORTE-TEST. 2. Los saldos del caso permanecen $174.29, $315.11 y $1,150.00, respectivamente. 3. Consultar, filtrar y exportar no genera nuevos movimientos financieros. 4. Conserva 1001 cancelado con fecha y motivo y 1002 emitido, sin reutilizar folios.',
'Los saldos corresponden al corte posterior al préstamo de Sofía; CAJA $1,195.11 del manual original de noviembre 3 es histórica y no debe forzarse.',
'Prueba funcional cerrada sin cambios financieros.',
'No ajustar saldos iniciales. Permisos de sólo consulta y alcance ajeno de cheques son ensayos de seguridad independientes; no se declaran aceptados por probar con administrador.','/admin/catalogos/cuentas-financieras')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='consulta-cheques-cuenta-confirmada';
