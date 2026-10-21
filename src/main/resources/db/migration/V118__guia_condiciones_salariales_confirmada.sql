-- Recorrido confirmado por propietario el21/10/2026. Sólo contenido editorial.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('empleado-condiciones-salariales','Puestos y sueldos: registrar, programar aumento y cancelar el futuro','Personal',
'Configura condiciones salariales sin pagar nómina: conserva el sueldo anterior, consulta por fecha y cancela sólo la última programación futura restaurando su vigencia.',
'Administrador institucional identificado con Empleados, Datos laborales de empleados y Puestos y condiciones salariales. Empleado ficticio activo con ingreso confirmado. No usar acceso de recuperación ni reactivar empleados en baja. La guía no modifica datos ni concede permisos.',
'Bruce Wayne, DOC-PRUEBA-02; puesto DOC-PRI-TEST Docente de primaria;12000MXN mensuales desde01/11/2026, aumento13000 desde01/01/2027 y cancelación del aumento. Fechas del ensayo confirmado, no fechas obligatorias de operaciones futuras.',1,DATE '2026-10-21','CONFIRMADA');
INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN ('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_ADMINISTRAR') WHERE g.slug='empleado-condiciones-salariales';
INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Confirmar acceso y actividad laboral','Administración → Personal y nómina → Empleados',
'1. Inicia sesión con administrador institucional autorizado; si cambiaron permisos, cierra sesión e inicia nuevamente. 2. En Empleados busca DOC-PRUEBA-02 o Bruce Wayne y pulsa Ver expediente. 3. Consulta la tarjeta Situación laboral y Datos laborales: Activo, fecha de ingreso correcta y sin baja. 4. Revisa condiciones existentes antes de capturar; reutiliza el empleado, no repitas altas o bajas.',
'Bruce: Activo, ingreso01/10/2026. La fecha de auditoría19/10 no es su fecha de ingreso.',
'Empleado elegible y sesión identificada; expediente activo o perfil docente no sustituyen actividad laboral.',
'No reactivar a María ni usar datos reales sin autorización. Si existe una programación, revisarla antes de ejecutar el ejemplo.','/admin/empleados'),
(2,'Crear o reutilizar el puesto','Administración → Personal y nómina → Puestos laborales',
'1. En Puestos laborales busca DOC-PRI-TEST y aplica filtros. 2. Reutiliza el puesto si ya existe; de lo contrario pulsa Nuevo puesto y captura código, nombre, descripción y activo. 3. Pulsa Crear puesto, revisa modal y prueba Cancelar: mantiene captura sin guardar. 4. Repite y confirma; el listado conserva filtros y muestra Activo en verde; Editar abre formulario separado.',
'Código DOC-PRI-TEST; nombre Docente de primaria; descripción Puesto de prueba de condiciones salariales.',
'Un puesto institucional reutilizable sin duplicados ni sueldo universal.',
'Cambiar nombre/código del puesto no reescribe la fotografía histórica del sueldo. Desactivar bloquea nuevas selecciones, no borra registros.','/admin/puestos'),
(3,'Registrar condición inicial','Empleados → Ver expediente → Condiciones salariales → Abrir condiciones salariales',
'1. Vuelve a Empleados, busca Bruce y pulsa Ver expediente → Condiciones salariales → despliega Condiciones individuales y compatibilidad → Abrir condiciones salariales. 2. En Registrar condición selecciona DOC-PRI-TEST desde la ayuda; captura12000, base Mensual, pago Mensual, inicio01/11/2026, fin vacío y motivo. 3. Sal del importe para comprobar formato moneda, pulsa Revisar y registrar condición y prueba Cancelar. 4. Revisa de nuevo y pulsa Confirmar y guardar; conserva un único registro.',
'Motivo: Condición inicial mensual de prueba, sin generación de nómina.',
'Condición de12000MXN desde01/11/2026 sin fin; Próxima antes de su inicio.',
'Usa hoy o una fecha futura si las del ejemplo ya pasaron, adaptando todas las siguientes. No configura retroactividad ni efectúa pagos.','/admin/empleados'),
(4,'Consultar sueldo por fecha','Empleados → Expediente → Condiciones salariales → Consultar condiciones',
'1. Abre Consultar condiciones del mismo empleado. 2. En Fecha a consultar captura31/10/2026 y pulsa Consultar fecha. 3. Repite con01/11/2026. 4. Comprueba que no había condición antes y que desde el inicio corresponden12000MXN, base Mensual y pago Mensual.',
'31/10 sin condición si no había otra;01/11 sueldo12000.',
'La consulta distingue vigencia del sueldo de fecha de captura y pago.',
'Si había condiciones anteriores el resultado previo puede ser distinto. Sueldo configurado no equivale a nómina pagada.','/admin/empleados'),
(5,'Programar aumento y conservar anterior','Empleados → Expediente → Condiciones salariales → Registrar condición',
'1. En el mismo expediente salarial abre Registrar condición. 2. Selecciona el mismo puesto, importe13000, base Mensual, pago Mensual, inicio01/01/2027 y fin vacío. 3. Captura motivo, pulsa Revisar y registrar condición y confirma resumen. 4. En Consultar condiciones consulta31/12/2026 y01/01/2027:12000 y13000 respectivamente; la anterior termina31/12.',
'Motivo: Aumento mensual programado de prueba a partir de enero de2027.',
'Dos condiciones conservadas, sin superposición y con cierre anterior al día previo.',
'No insertar entre programaciones posteriores ni guardar otra vez el mismo cambio. Si ambas fechas son futuras, ambas pueden aparecer Próxima.','/admin/empleados'),
(6,'Revisar historial y exportar filtrado','Empleados → Expediente → Condiciones salariales → Historial y Consultar condiciones',
'1. Abre Historial y revisa actor, fecha y motivos del registro y cierre por aumento. 2. Vuelve a Consultar condiciones y busca Docente de primaria. 3. Filtra Próximas y rango01/01/2027 a31/01/2027; aplica filtros. 4. Pulsa Exportar Excel y verifica las mismas filas, importe13000, moneda, puesto y fechas.',
'En el caso confirmado sólo coincide el aumento13000. El rango busca vigencias que tocan las fechas, no fecha de captura.',
'Trazabilidad y Excel consistente con consulta.',
'Usar Próximas sólo mientras esas fechas sigan siendo futuras; después elegir el estado correspondiente. No publicar Excel de sueldos.','/admin/empleados'),
(7,'Cancelar únicamente el aumento futuro','Empleados → Expediente → Condiciones salariales → Consultar condiciones',
'1. Limpia filtros para localizar la condición13000. 2. Pulsa Cancelar programación de esa fila, no de12000; captura motivo. 3. Pulsa Revisar cancelación, prueba Cancelar en el modal y comprueba sin cambios. 4. Repite y confirma la cancelación; vuelve a consultar01/01/2027.',
'Motivo: Cancelar aumento programado de prueba; conservar sueldo anterior.',
'13000 queda Cancelada;12000 recupera fin vacío y aplica desde01/11/2026 también en enero.',
'Sólo última programación que aún no inicia. Vigentes e históricas no se borran; motivo e historial se conservan.','/admin/empleados'),
(8,'Cerrar prueba y conservar evidencia','Administración → Personal y nómina → Empleados',
'1. Comprueba filas finales:13000 Cancelada y12000 Próxima sin fin. 2. Revisa historial del aumento y cancelación. 3. Confirma que no se generaron pagos ni movimientos y que foto, usuario, grupos y planteles no cambiaron. 4. Conserva12000: Cancelar programación es una acción disponible, no un paso pendiente; da por cerrado el ejemplo sin repetirlo.',
'Resultado aportado por propietario el21/10/2026:13000 desde01/01/2027 Cancelada;12000 desde01/11/2026 Próxima, ambas sin fin.',
'Recorrido principal confirmado. Nómina y reorganización de catálogos se prueban por separado.',
'La aceptación funcional no acredita todos los negativos de seguridad/concurrencia ni cálculos fiscales. No repetir operaciones para leer esta guía.','/admin/empleados')
)v(n,t,m,i,e,r,p,u) WHERE g.slug='empleado-condiciones-salariales';

UPDATE guia_proceso_paso SET precaucion='El recorrido de permisos está cerrado. Puestos y condiciones salariales ya cuentan con su guía confirmada: Guía de procesos → Personal → Puestos y sueldos: registrar, programar aumento y cancelar el futuro. No repetir bajas ni permisos para continuar; nómina todavía no genera pagos.'
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='empleado-permisos-laborales') AND numero=8;
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-21' WHERE slug='empleado-permisos-laborales';
