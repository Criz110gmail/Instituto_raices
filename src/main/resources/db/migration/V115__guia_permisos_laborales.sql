-- Guía editorial del recorrido confirmado. No asigna roles ni modifica empleados.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES ('empleado-permisos-laborales',
$texto$Permisos de empleados: proteger datos laborales y verificar Excel$texto$,'Personal',
$texto$Caso funcional confirmado: sin permiso se ocultan Datos laborales y Planteles; al concederlo aparecen y al retirarlo vuelven a ocultarse tras iniciar sesión nuevamente. El Excel filtrado no expone información laboral privada. Pegar la URL del expediente tampoco reveló las secciones; no representa una auditoría exhaustiva de endpoints.$texto$,
$texto$Administrador institucional identificado autorizado para Roles y permisos y Usuarios, una cuenta administrativa de capacitación de la misma institución, un rol exclusivo de pruebas y un empleado ficticio ya existente. La cuenta de pruebas no debe heredar acceso laboral de otros roles. No modificar el rol del administrador principal ni cuentas operativas para reproducir el ejemplo. La guía no concede permisos ni ejecuta operaciones.$texto$,
$texto$Empleado existente EMP-ADM-PRUEBA-01, María Prueba Administrativa. Rol ilustrativo PRUEBA_PERSONAL_BASE, nombre Prueba de empleados sin acceso laboral. Cuenta de pruebas con alcance Toda la institución. Sin repetir altas o bajas, se consulta el mismo expediente en los tres estados de autorización. Roles concede paquetes completos por módulo; no hay selección visual independiente de sólo lectura laboral.$texto$,
1,DATE '2026-10-20','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN ('EMPLEADO_LEER','ROL_ADMINISTRAR','USUARIO_ADMINISTRAR')
WHERE g.slug='empleado-permisos-laborales';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.numero,v.titulo,'Administración',v.modulo,v.instrucciones,v.ejemplo,v.resultado,v.precaucion,v.ruta
FROM guia_proceso g CROSS JOIN (VALUES
(1,$texto$Identificar el expediente sin modificarlo$texto$,$texto$Administración → Personal y nómina → Empleados$texto$,
$texto$1. Con tu administrador habitual entra a Personal y nómina → Empleados. 2. Busca EMP-ADM-PRUEBA-01 y pulsa Ver expediente. 3. Copia la dirección completa del expediente para la comprobación posterior. 4. Consulta la información existente sin guardar una nueva baja, cambiar motivos ni modificar planteles.$texto$,
$texto$María Prueba Administrativa ya tiene ingreso 01/10/2026, baja 20/10/2026 e historial del ensayo anterior. Sólo se reutiliza el expediente.$texto$,
$texto$Una misma referencia para comparar permisos sin alterar datos laborales.$texto$,
$texto$No crear otra persona, reactivar al empleado o repetir su baja. Las fechas son evidencia del caso confirmado, no valores obligatorios de otras operaciones.$texto$,'/admin/empleados'),
(2,$texto$Preparar un rol sin acceso laboral privado$texto$,$texto$Administración → Seguridad y soporte → Roles y permisos$texto$,
$texto$1. Abre Roles y permisos; reutiliza el rol exclusivo del ensayo o crea uno sólo si no existe. 2. Captura institución del empleado, código PRUEBA_PERSONAL_BASE, nombre Prueba de empleados sin acceso laboral y Rol activo. 3. En Acceso a módulos marca Empleados y deja Datos laborales de empleados desmarcado; no agregues otros módulos al rol de pruebas. 4. Pulsa Crear rol o Guardar cambios, revisa el resumen del modal y confirma sólo si los accesos son los previstos.$texto$,
$texto$Descripción sugerida: Verificar que el expediente general no exponga información laboral privada. El resumen debe incluir Empleados, sin Datos laborales de empleados.$texto$,
$texto$Rol separado con acceso general a Empleados pero sin autorización laboral privada.$texto$,
$texto$Empleados concede su paquete completo, no un expediente general de sólo lectura. Cada rol puede afectar a todas sus cuentas asignadas. Cancelar el modal conserva la captura y no guarda.$texto$,'/admin/catalogos/roles'),
(3,$texto$Asignar la cuenta de capacitación y separar sesiones$texto$,$texto$Administración → Seguridad y soporte → Usuarios → Autorización$texto$,
$texto$1. En Usuarios abre la cuenta administrativa de pruebas de la misma institución; no uses una cuenta familiar o docente. 2. En Autorización → Roles y alcances selecciona el rol del ensayo y Toda la institución y guarda la asignación si aún no existe. 3. Revisa que ningún otro rol activo de esa cuenta conceda Datos laborales de empleados. 4. Abre otro navegador o una ventana privada e inicia sesión en el login administrativo con esa cuenta; si es nueva, completa antes su invitación y contraseña mediante el flujo normal.$texto$,
$texto$Administrador principal permanece en su navegador; la cuenta de capacitación se usa por separado. No necesita estar vinculada al empleado María.$texto$,
$texto$Sesión identificable y permisos del ensayo sin herencias que invaliden la comparación.$texto$,
$texto$Los permisos de otros roles se suman. No quitar derechos a cuentas operativas ni compartir contraseñas. Alcance Toda la institución corresponde al flujo laboral actual.$texto$,'/admin/catalogos/usuarios'),
(4,$texto$Comprobar pestañas ocultas sin permiso$texto$,$texto$Cuenta de pruebas → Personal y nómina → Empleados → Ver expediente$texto$,
$texto$1. Con la cuenta de pruebas entra a Empleados y busca EMP-ADM-PRUEBA-01. 2. Pulsa Ver expediente y observa la navegación de pestañas. 3. Comprueba que sólo aparezcan Ficha del empleado, Datos personales, Información docente y Acceso. 4. Verifica que Datos laborales y Planteles no se muestren y no captures ni guardes cambios.$texto$,
$texto$Pestañas visibles confirmadas: 01 Ficha del empleado, 02 Datos personales, 05 Información docente y 06 Acceso. Las posiciones 03 y 04 corresponden a secciones privadas ocultas.$texto$,
$texto$Sin puesto, fechas, motivo, historial laboral ni adscripciones visibles; identidad y funciones generales siguen disponibles.$texto$,
$texto$Los saltos de numeración conservan la posición original; no significan un fallo ni conceden permiso. No confundir información docente con información laboral.$texto$,'/admin/empleados'),
(5,$texto$Pegar la dirección del expediente sin revelar secciones$texto$,$texto$Cuenta de pruebas → Dirección del expediente$texto$,
$texto$1. Conserva la sesión de pruebas sin permiso laboral. 2. Pega la URL del expediente copiada desde la sesión administrativa, usando la misma aplicación y empleado. 3. Para reproducir el ejemplo agrega al final #laboral-empleado y abre esa dirección. 4. Comprueba que no aparecen Datos laborales ni Planteles; si se revelan datos privados, detén el ensayo y reporta.$texto$,
$texto$Se pega la dirección real de Ver expediente; no se inventa un identificador ni se consultan empleados de otra institución.$texto$,
$texto$La navegación por URL no reveló las secciones ocultas en el caso confirmado.$texto$,
$texto$Un fragmento #laboral-empleado no concede permisos. Esta comprobación visual no acredita todos los endpoints privados ni solicitudes de escritura; no manipules peticiones para continuar esta guía.$texto$,'/admin/empleados'),
(6,$texto$Exportar el mismo empleado sin datos laborales privados$texto$,$texto$Cuenta de pruebas → Empleados → Exportar Excel$texto$,
$texto$1. Sin permiso laboral vuelve al listado de Empleados. 2. Escribe EMP-ADM-PRUEBA-01 en búsqueda y aplica los filtros; verifica un solo resultado. 3. Pulsa Exportar Excel y abre el archivo. 4. Comprueba una fila y únicamente datos generales: Código laboral, Nombre, Correo de contacto, Teléfono, Expediente, Código docente y Perfil docente, sin información laboral privada.$texto$,
$texto$Fila confirmada: EMP-ADM-PRUEBA-01, María Prueba Administrativa, Expediente Activo, Código docente vacío y Perfil docente Sin perfil. El contacto corresponde al expediente; no es necesario reproducirlo en la guía.$texto$,
$texto$Excel respeta el filtro y no incluye puesto, situación laboral, fechas de ingreso/baja, motivo, historial ni asignaciones de planteles.$texto$,
$texto$Activo corresponde al expediente; no contradice Baja laboral. El archivo contiene datos de contacto: consérvalo de forma privada, no lo publiques ni lo adjuntes a reportes innecesariamente.$texto$,'/admin/empleados'),
(7,$texto$Conceder acceso laboral y renovar la sesión$texto$,$texto$Administrador → Roles y permisos; cuenta de pruebas → Empleados$texto$,
$texto$1. Con el administrador abre Roles y permisos y edita sólo el rol exclusivo del ensayo. 2. Conserva Empleados marcado, marca Datos laborales de empleados, pulsa Guardar cambios y confirma el resumen del modal. 3. Con la cuenta de pruebas pulsa Cerrar sesión y vuelve a iniciar sesión para actualizar sus permisos. 4. En Personal y nómina → Empleados busca el mismo código, pulsa Ver expediente y comprueba Datos laborales y Planteles con fechas, historial y asignaciones anteriores sin guardar nada.$texto$,
$texto$Aparecen las pestañas 03 Datos laborales y 04 Planteles. La baja guardada y las adscripciones conservan sus datos.$texto$,
$texto$Con permiso se pueden consultar las secciones previamente ocultas; confirmado por el propietario.$texto$,
$texto$Este módulo concede también administración laboral, no sólo lectura. No confirmes otra baja ni edites planteles para demostrar acceso. Cancelar el modal del rol no aplica la concesión.$texto$,'/admin/catalogos/roles'),
(8,$texto$Retirar el acceso y cerrar la comprobación$texto$,$texto$Administrador → Roles y permisos; cuenta de pruebas → Empleados$texto$,
$texto$1. Con el administrador edita el rol exclusivo del ensayo y desmarca sólo Datos laborales de empleados, conservando Empleados. 2. Pulsa Guardar cambios, revisa el modal y confirma el retiro correcto. 3. Con la cuenta de pruebas cierra sesión, inicia nuevamente y abre el mismo expediente desde Empleados → Ver expediente. 4. Comprueba que Datos laborales y Planteles vuelven a ocultarse; conserva el expediente y no repitas operaciones ya confirmadas.$texto$,
$texto$El recorrido sin permiso, con permiso y tras retirarlo quedó confirmado el 20/10/2026. Excel y navegación por URL también se verificaron en el alcance descrito.$texto$,
$texto$Retiro efectivo tras renovar sesión y ejemplo funcional cerrado sin modificar información laboral.$texto$,
$texto$No se declara auditoría exhaustiva de seguridad, lectura laboral seleccionable, nómina o sueldos terminados. Siguiente etapa: definir puestos y condiciones salariales; todavía no existen formularios de sueldo ni generación de nómina.$texto$,'/admin/catalogos/roles')
) v(numero,titulo,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
WHERE g.slug='empleado-permisos-laborales';

-- Reemplaza el pendiente del recorrido funcional en la guía anterior sin reescribir V114.
UPDATE guia_proceso_paso SET
instrucciones=$texto$1. Conserva la baja y todas las entradas reales del ensayo, incluido cualquier texto temporal confirmado. 2. Da por documentados guardado válido, corrección de motivo, cancelación sin guardado y conservación del expediente del ejemplo. 3. No reactives al empleado ni registres otra baja para cerrar la guía. 4. Abre Guía de procesos → Personal → Permisos de empleados: proteger datos laborales y verificar Excel para el recorrido confirmado antes de definir puestos, condiciones salariales y nómina.$texto$,
precaucion=$texto$El recorrido funcional de permisos ya está documentado por separado. Reingresos, baja con cuenta docente activa y todas las variantes negativas requieren sus propias verificaciones. No confundir una prueba de navegación con auditoría exhaustiva de endpoints.$texto$
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='empleado-baja-laboral-historial') AND numero=8;

UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-20'
WHERE slug='empleado-baja-laboral-historial';
