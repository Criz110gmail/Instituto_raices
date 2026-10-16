-- Aceptación humana del 16/10/2026. Sólo guía: no crea usuarios, roles, planteles ni movimientos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES ('reportes-permisos-alcance',
$texto$Comprobar permisos y alcance por plantel en Reportes financieros$texto$,'Seguridad',
$texto$Casos confirmados: menú y rutas bloqueados sin permiso del módulo; filtro y consulta/exportaciones del plantel ajeno rechazados; pantalla, Excel y PDF correctos para Parian autorizado.$texto$,
$texto$Administrador identificado con Instituciones, Planteles, Usuarios, Roles y permisos, Reportes financieros y Guía de procesos. Dos cuentas de prueba separadas: prueba.sinreportes y prueba.reportes.parian; ninguna debe recibir roles adicionales que amplíen el ensayo. Reutilizar cuentas y planteles ya creados; no cambiar derechos del administrador principal. Git no transporta usuarios, datos financieros ni archivos de la instalación. No compartir contraseñas, invitaciones ni tokens.$texto$,
$texto$Instalación del ensayo: institución instituto Raices id1; Plantel parian autorizado y Plantel prueba de permisos id2 ajeno al usuario limitado. Al 16/10/2026, Parian tiene tres cargos por $2,100.00, cobrado $500.00, por cobrar $1,600.00 y vencido $0.00. Segundo plantel vacío: confirma filtro y rechazo de acceso explícito, NO separación entre registros financieros de dos planteles con movimientos. El fallo500 inicial fue corregido y ambos rechazos de exportación se repitieron correctamente.$texto$,
1,DATE '2026-10-16','CONFIRMADA');

-- Restringe la consulta de esta guía de administración; no otorga derechos a ningún rol.
INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p
ON p.codigo=ANY(ARRAY['INSTITUCION_ADMINISTRAR','PLANTEL_ADMINISTRAR','USUARIO_ADMINISTRAR','ROL_ADMINISTRAR','REPORTE_FINANCIERO_CONSULTAR'])
WHERE g.slug='reportes-permisos-alcance';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,v.ruta
FROM guia_proceso g JOIN (VALUES
(1,$texto$Identificar la institución y los dos planteles de prueba$texto$,$texto$Instituciones y Planteles$texto$,
$texto$1. En la ventana del administrador principal abre Instituciones → Editar la institución del ensayo; en esta instalación la URL termina /admin/instituciones/1/editar. 2. En Planteles identifica Parian y Plantel prueba de permisos; consulta este último sin guardar: /admin/planteles/2/editar. 3. Si reproduces en otra instalación y falta el segundo plantel, créalo sólo con autorización: misma institución, código CAMPUS-PRUEBA-02, nombre Plantel prueba de permisos, Activo; deja desmarcado Permitir reportar transferencias de cargos vencidos. 4. Anota los identificadores reales; los números 1 y 2 pertenecen a este ensayo y no deben copiarse suponiendo que coinciden en otro equipo.$texto$,
$texto$Segundo plantel de prueba vacío, sin cargos ni movimientos financieros.$texto$,
$texto$Contexto del ensayo identificado; los registros existentes no se duplican.$texto$,
$texto$No crear datos financieros, editar fechas ni cambiar saldos para probar accesos. La opción de transferencias vencidas no interviene en permisos de reportes. No guardar la edición sólo para consultar su URL.$texto$,
$texto$/admin/catalogos/planteles$texto$),
(2,$texto$Preparar la cuenta sin permiso de reportes$texto$,$texto$Roles y permisos → Usuarios$texto$,
$texto$1. Con el administrador busca PRUEBA_SIN_REPORTES en Roles y permisos; si falta, crea el rol activo Prueba sin reportes financieros, de la institución del ensayo, marcando únicamente Guía de procesos. 2. Busca prueba.sinreportes en Usuarios; si falta, crea su identidad en esa institución con correo ficticio prueba.sinreportes@example.com. 3. En Autorización asigna sólo PRUEBA_SIN_REPORTES, alcance Un plantel y Plantel parian; si ya existe, comprueba la asignación sin duplicarla. 4. Sólo si necesita activación, usa Estado y activación → Generar invitación y configura personalmente la contraseña mediante su enlace privado; después entra en incógnito con esa cuenta.$texto$,
$texto$Cuenta de prueba sin Reportes financieros; el propietario confirmó que sólo aparece Guía de procesos en el menú.$texto$,
$texto$Usuario sin permiso del módulo identificado para comprobar rechazo.$texto$,
$texto$No modificar criz ni otro administrador principal. La guía no crea cuentas ni concede permisos. Una asignación adicional puede ampliar derechos e invalidar esta prueba. No enviar contraseñas ni enlaces de activación al chat.$texto$,
$texto$/admin/catalogos/usuarios$texto$),
(3,$texto$Preparar otra cuenta con reportes sólo de Parian$texto$,$texto$Roles y permisos → Usuarios → Autorización$texto$,
$texto$1. Con el administrador busca el rol PRUEBA_REPORTES_PARIAN; si falta, crea Reportes sólo Plantel parian, activo, institución del ensayo y únicamente Reportes financieros. 2. Busca prueba.reportes.parian; si falta, crea su identidad con correo ficticio prueba.reportes.parian@example.com en la misma institución. 3. En Autorización asigna sólo ese rol con alcance Un plantel → Plantel parian, nunca Toda la institución ni Plantel prueba de permisos. 4. Activa mediante invitación sólo si corresponde y entra con esta segunda cuenta en una ventana de prueba distinta de la sesión del administrador; conserva su contraseña privada.$texto$,
$texto$prueba.reportes.parian tiene Reportes financieros, pero no alcance sobre el plantel2.$texto$,
$texto$Cuenta limitada preparada sin ampliar permisos de la cuenta sin reportes.$texto$,
$texto$No reutilizar prueba.sinreportes cambiándole el rol: conservar separados los dos casos. Las ventanas normales comparten sesión; comprueba qué usuario está conectado antes de abrir cada enlace.$texto$,
$texto$/admin/catalogos/usuarios$texto$),
(4,$texto$Comprobar menú y rutas sin permiso del módulo$texto$,$texto$Cuenta prueba.sinreportes$texto$,
$texto$1. Con prueba.sinreportes comprueba que el menú no muestre Reportes financieros y que sólo aparezca Guía de procesos. 2. Escribe /admin/reportes-financieros/tesoreria en el mismo sitio: debe mostrar No tienes acceso a esta información y explicar que la sesión es válida. 3. Prueba /admin/reportes-financieros/concentrado-cobranza/excel y /admin/reportes-financieros/concentrado-cobranza/pdf con esa misma cuenta: ambas rutas deben mostrar el mismo rechazo, sin archivos ni datos financieros. 4. Comprueba que la sesión no se cierre por el rechazo; el propietario confirmó estas tres rutas y el menú restringido.$texto$,
$texto$Sin permiso del módulo: acceso denegado antes de consultar datos o preparar exportaciones.$texto$,
$texto$Menú y rutas probadas bloqueados sin permiso de Reportes financieros.$texto$,
$texto$Las rutas sin parámetros de este paso se probaron sólo con una cuenta SIN el módulo. No sirven para probar alcance con una cuenta que sí tiene reportes: en los pasos siguientes deben conservarse todos los parámetros obligatorios.$texto$,
$texto$/admin/reportes-financieros/tesoreria$texto$),
(5,$texto$Comprobar el filtro y la consulta de un plantel ajeno$texto$,$texto$Cuenta prueba.reportes.parian → Concentrado de cobranza$texto$,
$texto$1. Entra con prueba.reportes.parian a Reportes financieros → Concentrado de cobranza. 2. Revisa Plantel: debe ofrecer únicamente Parian, no Plantel prueba de permisos; si aparece Todos autorizados, significa sólo los planteles permitidos a esa cuenta. 3. En esta instalación prueba la consulta /admin/reportes-financieros/concentrado-cobranza?plantelId=2&agrupacion=CONCEPTO&fechaCorte=2026-10-16: la institución se deriva del alcance del ensayo y debe rechazar el plantel2. 4. Comprueba No tienes acceso a esta información, sin resultados del plantel ajeno; el propietario confirmó filtro limitado y rechazo del enlace modificado.$texto$,
$texto$Ocultar el plantel del selector y bloquear la URL son controles diferentes, ambos confirmados.$texto$,
$texto$El servidor rechaza la consulta explícita del plantel no autorizado.$texto$,
$texto$No basta ocultar una opción. Verifica el usuario de la sesión y el identificador del plantel antes de interpretar el resultado. El segundo plantel está vacío; esta prueba no acredita aún separación de datos financieros de dos planteles poblados.$texto$,
$texto$/admin/reportes-financieros/concentrado-cobranza$texto$),
(6,$texto$Comprobar Excel y PDF del plantel no autorizado$texto$,$texto$Concentrado de cobranza → Exportaciones directas$texto$,
$texto$1. Mantén prueba.reportes.parian, sin cambiar al administrador. 2. Abre /admin/reportes-financieros/concentrado-cobranza/excel?institucionId=1&plantelId=2&agrupacion=CONCEPTO&fechaCorte=2026-10-16 en el mismo sitio. 3. Abre /admin/reportes-financieros/concentrado-cobranza/pdf?institucionId=1&plantelId=2&agrupacion=CONCEPTO&fechaCorte=2026-10-16. 4. Ambos deben mostrar No tienes acceso a esta información, sin descarga ni reporte PDF; el propietario repitió los enlaces completos tras la corrección y confirmó el rechazo correcto.$texto$,
$texto$Respuesta esperada de acceso denegado, no Whitelabel ni error500.$texto$,
$texto$Excel y PDF bloqueados por alcance del plantel, con rechazo visible y sin archivos.$texto$,
$texto$Las primeras URLs sin institucionId devolvieron400 y no contaron como prueba de alcance. Los enlaces completos revelaron un fallo500 corregido: se abría la salida del archivo antes del rechazo. Sólo el reensayo correcto posterior se considera aceptado. No modificar roles para ocultar un error técnico.$texto$,
$texto$/admin/reportes-financieros/concentrado-cobranza$texto$),
(7,$texto$Consultar el plantel autorizado sin perder acceso válido$texto$,$texto$Concentrado de cobranza → Parian autorizado$texto$,
$texto$1. Con la misma cuenta limitada vuelve a Concentrado de cobranza desde el menú o su ruta normal. 2. Selecciona institución del ensayo, Plantel parian, Agrupar por Concepto de cobro y Fecha de corte 16/10/2026; pulsa Generar. 3. Comprueba ANTICIPO-TEST-02: dos cargos, importe/por cobrar $1,600.00 y cobrado/vencido $0.00. 4. Comprueba MATERIAL: un cargo, importe/cobrado $500.00 y por cobrar/vencido $0.00; son tres cargos en total, sin cambiar registros.$texto$,
$texto$Importe $2,100.00, cobrado $500.00, por cobrar $1,600.00 y vencido $0.00.$texto$,
$texto$Pantalla del plantel autorizado confirmada con la cuenta limitada.$texto$,
$texto$Acceso limitado no significa bloquear todo el módulo. Si los datos del ensayo ya cambiaron, revisa los registros existentes sin crear cargos ni pagos para forzar estas cifras.$texto$,
$texto$/admin/reportes-financieros/concentrado-cobranza$texto$),
(8,$texto$Comparar ambas hojas Excel del plantel autorizado$texto$,$texto$Concentrado de cobranza → Exportar Excel$texto$,
$texto$1. Conserva los filtros generados del paso anterior y pulsa Exportar Excel. 2. En Resumen comprueba fecha 16/10/2026, institución instituto Raices, Plantel parian y Concepto de cobro. 3. Revisa Cargos tres, Importe $2,100.00, Cobrado $500.00, Por cobrar $1,600.00, Vencido $0.00 y MXN. 4. En Concentrado verifica ANTICIPO-TEST-02 con dos cargos/$1,600.00 sin cobro y MATERIAL con un cargo/$500.00 pagado; ambas filas deben coincidir con pantalla y resumen.$texto$,
$texto$El archivo autorizado conserva los filtros y las dos hojas, sin mezclar el plantel2.$texto$,
$texto$Resumen y Concentrado del Excel confirmados por el propietario con la cuenta limitada.$texto$,
$texto$El segundo plantel no contiene datos financieros: la coincidencia no prueba todavía separación entre registros de dos planteles con movimientos. Descargar una copia nueva; los archivos anteriores no cambian al modificar filtros.$texto$,
$texto$/admin/reportes-financieros/concentrado-cobranza$texto$),
(9,$texto$Comparar PDF autorizado y cerrar la ronda$texto$,$texto$Concentrado de cobranza → PDF$texto$,
$texto$1. Con prueba.reportes.parian y los mismos filtros abre PDF desde el concentrado permitido. 2. Comprueba Agrupado por concepto de cobro y Corte 16/10/2026. 3. Revisa tres cargos, importe $2,100.00, cobrado $500.00 y por cobrar $1,600.00; tabla de dos conceptos con vencido $0.00, coincidente con pantalla y Excel. 4. Concluye sin modificar permisos ni generar operaciones: el propietario confirmó rechazo sin módulo, rechazo del plantel ajeno y consulta/Excel/PDF permitidos para Parian.$texto$,
$texto$El mismo usuario limitado puede exportar Parian y no puede exportar plantel2.$texto$,
$texto$Ronda de permisos y alcance descrita confirmada el 16/10/2026.$texto$,
$texto$No extender esta aceptación a todos los reportes, otras instituciones, grandes volúmenes o planteles con datos financieros propios. Si se desactivan después cuentas de prueba, hacerlo con autorización del propietario; conservar registros y trazabilidad, sin borrar datos.$texto$,
$texto$/admin/reportes-financieros/concentrado-cobranza$texto$)
) v(numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion,ruta) ON true
WHERE g.slug='reportes-permisos-alcance';

UPDATE guia_proceso_paso SET precaucion=$texto$No sumar las hojas entre sí ni registrar dinero para repetir la consulta histórica. El corte por plantel/ciclo, datos vacíos y grandes volúmenes siguen sin aceptación humana en este ensayo al día 15. Los permisos y el alcance de Reportes al 16/10/2026 se verificaron por separado: consultar Comprobar permisos y alcance por plantel en Reportes financieros.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='reporte-concentrado-agrupaciones') AND numero=6;
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-16'
WHERE slug='reporte-concentrado-agrupaciones';
