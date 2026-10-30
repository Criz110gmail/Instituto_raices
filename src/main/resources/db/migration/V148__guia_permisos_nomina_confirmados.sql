-- Sólo documentación. No cambia permisos, roles, usuarios, nóminas ni dinero.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('nomina-permisos-privados-confirmados','Nómina: quién puede ver sueldos y conceptos del empleado','Personal',
'Una persona puede consultar empleados sin tener permiso para ver sueldos o conceptos de nómina. Quitar el permiso debe bloquear la pantalla, los enlaces directos y el Excel; restaurarlo permite entrar otra vez.',
'Administrador autorizado para Roles y permisos, y cuentas de prueba independientes de usuarios reales. Usar ventana normal para el administrador e incógnito para el usuario de prueba; dos pestañas normales comparten sesión. Caso concluido: consultar la evidencia, no cambiar roles automáticamente. Las opciones de módulos conceden acceso completo, no sólo lectura.',
'El usuario confirmó todas las pruebas el 30/10/2026. Cuenta prueba con PRUEBA_PERSONAL_BASE: Empleados y Datos laborales, sin salarios ni configuración de nómina. Cuenta pruebas con QA_SALARIOS_CONSULTA: inicialmente acceso salarial y de configuración; retiró únicamente Empleados — Configuración de nómina, verificó el bloqueo y finalmente lo restauró. Pantallas, URL y Excel protegidos; sin pagos. Aunque su nombre diga consulta salarial, QA_SALARIOS_CONSULTA también tiene permisos de administración.',1,DATE '2026-10-30','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('ROL_ADMINISTRAR','USUARIO_ADMINISTRAR','EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_LEER','EMPLEADO_NOMINA_LEER')
WHERE g.slug='nomina-permisos-privados-confirmados';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Separar las sesiones y elegir cuentas de prueba','Administración → Seguridad y soporte → Usuarios y Roles y permisos',
'1. Conserva tu administrador en una ventana normal y utiliza una ventana de incógnito para las cuentas de prueba. 2. Identifica prueba, sin letra s final, con rol PRUEBA_PERSONAL_BASE y alcance Toda la institución. 3. Identifica pruebas, con letra s final, con rol QA_SALARIOS_CONSULTA y el mismo alcance institucional. 4. Si esos registros no existen en otra computadora, revisa los datos disponibles antes de preparar cuentas de ensayo; no reutilices un usuario real ni cambies el rol del administrador.',
'prueba no tiene permisos salariales; pruebas sí los tiene junto con configuración de nómina.',
'Cada ventana usa la identidad correcta sin mezclar sesiones.',
'No solicitar ni documentar contraseñas. El nombre consulta del rol no significa sólo lectura: sus opciones actuales también permiten administrar.','/admin/usuarios'),
(2,'Consultar el expediente sin mostrar salarios','Ventana de incógnito → usuario prueba → Personal y nómina → Empleados',
'1. Inicia sesión como prueba y busca Luis Prueba Posposición, código EMP-POSP-TOTAL-TEST. 2. Abre Ver expediente y comprueba que puede consultar identidad y datos laborales autorizados. 3. Verifica que no aparezca Configuración de nómina, importe salarial ni Ver puesto e historial. 4. Comprueba que el menú no muestre los módulos salariales; no guardes cambios en el empleado.',
'En esta base Luis es empleado 8; en otra base busca por código y usa su identificador real.',
'Acceso general permitido sin revelar información salarial privada.',
'Si se muestran datos privados, detén el ensayo y reporta usuario, pantalla y resultado, sin copiar información privada innecesaria.','/admin/empleados'),
(3,'Comprobar enlaces directos y Excel protegidos','Misma ventana de incógnito → usuario prueba',
'1. Intenta abrir /admin/empleados/8/nomina, /admin/empleados/8/nomina/9 y /admin/empleados/8/puesto, usando los identificadores reales de la base que pruebas. 2. Intenta consultar /admin/preparacion-nomina/10 sin permisos salariales. 3. Comprueba que /admin/empleados/8/nomina/excel y /admin/asignaciones/excel?empleadoId=8&estado=TODAS rechacen el acceso sin descargar datos privados. 4. El Excel general de Empleados puede estar permitido, pero no debe revelar sueldos ni conceptos de nómina; una pantalla Whitelabel no sustituye un rechazo controlado.',
'Ocultar el botón no basta: el servidor también debe negar el enlace y la exportación.',
'Sin permiso no se accede a los datos privados ni por URL ni por Excel.',
'Son consultas sin pago. No probar operaciones de escritura ni eludir el bloqueo con otras cuentas.','/admin/empleados'),
(4,'Verificar el acceso de la cuenta autorizada','Nueva ventana de incógnito → usuario pruebas → Empleados → Luis → Ver expediente',
'1. Cierra sesión y todas las ventanas de incógnito anteriores; abre una nueva e ingresa como pruebas. 2. Comprueba Configuración de nómina, puesto e historial salarial y conceptos configurados de Luis. 3. En Simulador por fechas consulta 16/12/2026 al 31/12/2026: sueldo $516.13 más transporte $41.29, total $557.42. 4. Exporta Conceptos configurados a Excel y comprueba que ahora esté permitido, sin editar ni pagar nada.',
'El simulador conserva $557.42 aunque BOR-000010 excluyó ese pago: estimar por configuración no entrega dinero ni cambia la decisión confirmada.',
'El permiso autorizado permite consultar los datos correspondientes.',
'No confundir permiso completo de módulo con sólo lectura. No modificar los conceptos del empleado para este ensayo.','/admin/empleados'),
(5,'Retirar sólo configuración y comprobar el bloqueo','Ventana normal → administrador → Roles y permisos → QA_SALARIOS_CONSULTA → Editar',
'1. Sólo en un rol exclusivo de ensayo conserva Empleados, Datos laborales de empleados y Puestos y condiciones salariales marcados. 2. Desmarca únicamente Empleados — Configuración de nómina, guarda los cambios y confirma el modal. 3. Cierra la sesión y todas las ventanas de incógnito de pruebas, abre otra e inicia sesión de nuevo. 4. Comprueba que puesto e historial salarial continúen disponibles, pero Configuración de nómina desaparezca y sus enlaces de detalle y Excel nieguen el acceso.',
'Ver salarios y ver conceptos del empleado son autorizaciones distintas.',
'El permiso retirado bloquea la configuración sin quitar el acceso salarial conservado.',
'Otros módulos salariales pueden seguir visibles. No modificar permisos de roles compartidos con personas reales ni del administrador.','/admin/roles'),
(6,'Restaurar el rol y cerrar sin mover dinero','Administrador → Roles y permisos; nueva sesión de pruebas → Empleados',
'1. En la ventana normal edita QA_SALARIOS_CONSULTA y vuelve a marcar Empleados — Configuración de nómina. 2. Guarda y confirma el modal, sin cambiar otras opciones. 3. Cierra la sesión de incógnito e inicia una nueva como pruebas; confirma que recuperó Configuración de nómina de Luis. 4. Conserva prueba sin acceso salarial y pruebas con su acceso original restaurado; no registres pagos ni modifiques BOR-000009, BOR-000010 o la configuración de Luis.',
'Confirmación humana completa: accesos negados y permitidos según rol, enlaces y Excel protegidos, acceso recuperado al restaurar el permiso.',
'Prueba concluida y cuentas de ensayo restituidas, sin cambios financieros.',
'La siguiente prueba de nómina con varios empleados todavía no se realizó. Revisa la base disponible antes de elegir empleados, periodos y cuentas.','/admin/roles')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='nomina-permisos-privados-confirmados';
