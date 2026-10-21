-- Recorrido principal y Excel confirmados por el propietario 21/10/2026. Sólo contenido editorial.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('asignaciones-laborales-configuracion-historica','Puesto, jornada y sueldo: asignar, programar cambios y cancelar el futuro','Personal',
'Organiza puestos, jornadas y sueldos configurados; asígnalos con vigencia, conserva los datos autorizados aunque cambie el catálogo y cancela sólo la última programación futura. Incluye filtros y Excel.',
'Administrador institucional identificado con Empleados, Datos laborales de empleados y Puestos y condiciones salariales. La guía requiere autorización salarial de administración y no concede permisos. Usa personal ficticio activo con ingreso confirmado; revisa primero las condiciones existentes. No repetir el ensayo ya realizado ni usar acceso de recuperación.',
'Bruce Wayne, DOC-PRUEBA-02; puesto DOC-PRI-TEST; jornada JOR-MAT-TEST; configuración ESQ-DOC-MAT-TEST. Enero de 2027 conserva 12000 MXN/30 horas; el catálogo cambia a 13000/25 horas, se programa febrero y después se cancela restaurando enero sin fin. Caso confirmado 21/10/2026; adaptar fechas futuras para otro ensayo autorizado.',1,DATE '2026-10-21','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN ('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_ADMINISTRAR') WHERE g.slug='asignaciones-laborales-configuracion-historica';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Revisar empleado y condiciones existentes','Administración → Personal y nómina → Empleados y Asignaciones laborales',
'1. Inicia sesión con administrador institucional autorizado; si cambiaron derechos, cierra e inicia sesión. 2. En Empleados selecciona Bruce Wayne o DOC-PRUEBA-02 desde la ayuda, aplica filtros y abre Ver expediente; comprueba actividad laboral e ingreso. 3. En Asignaciones laborales selecciona Bruce en Empleado, deja vacíos Puesto, Jornada, Configuración de puesto y sueldo y Texto adicional, y pulsa Aplicar filtros. 4. Revisa vigencias antes de capturar: reutiliza lo existente y detente si hay una programación posterior no prevista.',
'Punto de partida del ensayo: condición individual 12000 desde 01/11/2026 sin fin y condición 13000 desde 01/01/2027 Cancelada; ingreso 01/10/2026. El ensayo ya concluido tendrá también las asignaciones de enero y febrero: no repetirlo.',
'Identificas al empleado y su historial antes de registrar otro cambio.',
'Expediente activo no sustituye situación laboral activa. Fechas ilustrativas del caso; si pasaron, usa fechas futuras coherentes en un ensayo nuevo autorizado. No modificar personal real para leer esta guía.','/admin/asignaciones'),
(2,'Crear o reutilizar el puesto','Administración → Personal y nómina → Puestos laborales',
'1. Abre Puestos laborales, enfoca Puesto y selecciona DOC-PRI-TEST desde la ayuda; pulsa Aplicar filtros. 2. Si ya existe, reutilízalo sin duplicar; si no existe, pulsa Nuevo puesto. 3. Captura código DOC-PRI-TEST, nombre Docente de primaria, descripción Puesto de prueba y activo; pulsa Crear puesto. 4. Revisa el modal: Cancelar conserva la captura sin registrar; confirmar guarda el puesto y vuelve al listado.',
'DOC-PRI-TEST · Docente de primaria. El puesto es un catálogo; todavía no asigna un sueldo a ningún empleado.',
'Tienes un puesto reutilizable de la institución.',
'Crear un puesto no genera un empleado, salario ni pago. Las ayudas de consulta pueden identificar inactivos para historial, pero nuevas capturas requieren activos.','/admin/puestos'),
(3,'Crear jornada de treinta horas','Administración → Personal y nómina → Jornadas laborales',
'1. Abre Jornadas laborales, selecciona JOR-MAT-TEST en Jornada y pulsa Aplicar filtros; reutiliza si ya existe con los valores iniciales del ejemplo. 2. Si falta, pulsa Nueva jornada: código JOR-MAT-TEST, nombre Matutina de prueba y Horas semanales con valor 30 horas. 3. Captura Distribución y observaciones: Lunes a viernes de 08:00 a 14:00; jornada de prueba; deja Registro activo marcado. 4. Pulsa Crear registro, revisa el modal, prueba Cancelar sin guardar y después confirma cuando los datos sean correctos.',
'JOR-MAT-TEST · Matutina de prueba;30 horas semanales. En el ensayo ya concluido el catálogo quedó en 25 horas: no regresarlo a 30 para documentar.',
'Jornada independiente del puesto y de las clases del maestro.',
'Es una referencia semanal, no control de asistencia ni cálculo automático de horas pagables. No modifica clases ni sueldo por sí sola.','/admin/jornadas'),
(4,'Preparar puesto y sueldo reutilizables','Administración → Personal y nómina → Puestos y sueldos configurados',
'1. Abre Puestos y sueldos configurados, selecciona ESQ-DOC-MAT-TEST en Configuración de puesto y sueldo y aplica filtros; reutiliza si corresponde al inicio de tu ensayo. 2. Si falta, pulsa Nueva configuración: código ESQ-DOC-MAT-TEST, nombre Docente matutino de prueba, puesto DOC-PRI-TEST y jornada JOR-MAT-TEST desde las ayudas. 3. Captura Sueldo bruto base 12000, Base del sueldo Mensual, Frecuencia de pago Mensual y Registro activo; moneda institucional MXN. 4. Pulsa Crear registro, revisa resumen y confirma; Cancelar en el modal mantiene captura sin guardar.',
'Configuración inicial 12000 MXN,30 horas, Mensual/Mensual. El nombre técnico anterior era Esquemas de remuneración; los códigos ESQ y registros se conservan.',
'Una configuración combina puesto, jornada y sueldo para asignarla después a uno o varios empleados.',
'No es una asignación todavía: crear la configuración no cambia sueldos de empleados ni mueve dinero. El catálogo del ensayo concluido quedó 13000/25 horas; no sobrescribirlo para repetir.','/admin/esquemas'),
(5,'Asignar enero sin recapturar sueldo','Administración → Personal y nómina → Asignaciones laborales → Nueva asignación',
'1. En Asignaciones laborales pulsa Nueva asignación; selecciona Bruce desde Empleado y ESQ-DOC-MAT-TEST desde Configuración de puesto y sueldo. 2. En Revisa la configuración comprueba DOC-PRI-TEST, Matutina de prueba,30 horas y 12000 MXN, base y frecuencia Mensual. 3. Captura Inicio 01/01/2027, Fin vacío y motivo «Asignación del esquema matutino de prueba a partir de enero de 2027». 4. Pulsa Revisar y asignar, prueba Cancelar en el modal y después confirma; no recapturas el sueldo.',
'El formulario puede precargar Bruce si venías de su listado filtrado: no guarda automáticamente ni bloquea cambiarlo con la × de la ayuda.',
'Asignación de enero 12000/30 horas sin fecha de fin; se conservan los datos autorizados del catálogo.',
'Si no aparece ESQ-DOC-MAT-TEST en la tabla de asignaciones aún no asignadas es normal: ese listado no es el catálogo. Selecciónalo dentro de Nueva asignación. Si falla la consulta no confirmes.','/admin/asignaciones'),
(6,'Comprobar vigencias y entender el resumen','Administración → Personal y nómina → Asignaciones laborales → Ver → Resumen',
'1. Vuelve al listado, selecciona Bruce en Empleado, deja otros criterios vacíos y pulsa Aplicar filtros. 2. Abre Ver de la asignación que inicia 01/01/2027 y revisa Resumen: sueldo, horas, puesto/configuración, vigencia y motivo. 3. Regresa al listado y verifica que la condición individual 12000 desde 01/11/2026 termina 31/12/2026; la antigua 13000 sigue Cancelada. 4. Comprueba enero 12000/30 horas sin fin; Próxima significa que aún no llega la fecha de inicio, no que se haya pagado.',
'El resumen distingue Sueldo bruto base, Jornada autorizada, Puesto y configuración, Vigencia registrada y Motivo de registro. Base y frecuencia se muestran separadas.',
'La condición anterior cierra el día previo sin borrarse; no hay superposición activa.',
'El código docente puede aparecer junto al nombre de Bruce y no es un registro duplicado. Sin jornada registrada identifica condiciones individuales previas; no se inventan horas.','/admin/asignaciones'),
(7,'Editar catálogos sin cambiar enero','Administración → Personal y nómina → Jornadas laborales y Puestos y sueldos configurados',
'1. En Jornadas laborales selecciona JOR-MAT-TEST, aplica filtros y pulsa Editar; cambia Horas semanales a 25 y observaciones a Lunes a viernes de 08:00 a 13:00; jornada de prueba; guarda y confirma. 2. En Puestos y sueldos configurados selecciona ESQ-DOC-MAT-TEST, aplica filtros, pulsa Editar y cambia únicamente Sueldo bruto base a 13000; guarda y confirma. 3. Vuelve a Asignaciones laborales, filtra Bruce y abre Ver de enero. 4. En Resumen comprueba que enero conserva 30 horas y 12000 MXN, sin fin y con el motivo original.',
'El catálogo actual dice 25 horas/13000; enero mantiene 30 horas/12000. Este resultado fue confirmado por el propietario.',
'Los datos históricos autorizados no cambian por editar los catálogos.',
'No editar ni cancelar la asignación de enero para hacer esta comprobación. Si sus datos cambian retroactivamente, detener el ensayo y reportarlo; no compensar con otra captura.','/admin/asignaciones'),
(8,'Programar el cambio de febrero','Administración → Personal y nómina → Asignaciones laborales → Nueva asignación',
'1. Desde Asignaciones laborales pulsa Nueva asignación, o Ver enero → Gestión → Programar cambio. 2. Selecciona Bruce y ESQ-DOC-MAT-TEST; ahora Revisa la configuración debe cargar 25 horas y 13000 MXN, Mensual/Mensual. 3. Captura Inicio 01/02/2027, Fin vacío y motivo Cambio de configuración de prueba desde febrero; pulsa Revisar y asignar y confirma el resumen. 4. En el listado verifica enero 12000/30 horas hasta 31/01/2027 y febrero 13000/25 horas desde 01/02/2027 sin fin.',
'En el ensayo, enero fue la asignación 3 y febrero la 4; en otras bases los IDs serán distintos: identifica por empleado, fecha y configuración.',
'Nueva vigencia con la configuración actual, conservando enero y la condición individual anterior.',
'No confundir la programación nueva de febrero 13000 con la condición individual 13000 de enero ya Cancelada. La ficha del empleado resume lo aplicable hoy; el listado incluye el futuro.','/admin/asignaciones'),
(9,'Filtrar febrero y comprobar Excel','Administración → Personal y nómina → Asignaciones laborales',
'1. Pulsa Limpiar y selecciona Bruce desde Empleado; deja Puesto, Jornada, Configuración de puesto y sueldo y Texto adicional vacíos. 2. Elige Vigencia Próximas, Vigente desde 01/02/2027 y Vigente hasta 28/02/2027; pulsa Aplicar filtros. 3. Debe aparecer únicamente febrero 13000/25 horas si no existen otros registros del ensayo; pulsa Exportar Excel. 4. Abre el archivo y verifica las mismas filas, sueldo numérico, horas, moneda, fechas y motivos; al volver de Ver se conservan filtros.',
'Ayudas: hasta diez opciones al enfocar o dar clic y búsqueda desde tres caracteres. Selecciona la sugerencia; la etiqueta no se copia a Texto adicional. × quita un criterio y Limpiar restablece todos.',
'Pantalla y Excel coinciden; exportación filtrada confirmada por el propietario.',
'El rango consulta vigencias que tocan esas fechas, no fecha de captura. Próximas corresponde al momento del ensayo; en otro momento usa el estado aplicable. Excel de salarios es información privada.','/admin/asignaciones'),
(10,'Cancelar únicamente la última programación futura','Administración → Personal y nómina → Asignaciones laborales → Ver febrero → Gestión',
'1. Limpia filtros, selecciona Bruce, aplica y abre Ver del registro de febrero con inicio 01/02/2027,13000 y 25 horas. 2. En Gestión localiza Cancelar programación futura y captura motivo Cancelar cambio de febrero de prueba; conservar configuración anterior. 3. Pulsa Revisar cancelación y prueba Cancelar dentro del modal: no debe modificar la asignación; repite para confirmar cuando los datos sean correctos. 4. En Resumen verifica estado Cancelada,25 horas/13000 y fechas originales conservadas.',
'Cancelar el modal no es cancelar la programación. Sólo la confirmación final ejecuta la operación; no elimina historial ni devuelve dinero.',
'Febrero queda Cancelada con sus datos originales.',
'Sólo puede cancelarse la última programación que todavía no inicia. No cancelar enero ni la condición individual anterior. Si ya inició o existe una posterior activa, no forzar el cambio.','/admin/asignaciones'),
(11,'Verificar restauración de enero e historial','Administración → Personal y nómina → Asignaciones laborales → Ver enero → Resumen e Historial',
'1. Vuelve al listado de Bruce y abre Ver del registro que inicia 01/01/2027. 2. En Resumen comprueba 30 horas,12000 MXN y Fin Sin fecha de fin: enero recuperó su vigencia anterior al cancelar febrero. 3. En Historial consulta los cambios y motivos, y conserva visible febrero Cancelada; no repitas ni borres las operaciones. 4. Comprueba que el catálogo ESQ-DOC-MAT-TEST sigue 13000/25 horas; cancelar una asignación no revierte el catálogo.',
'Resultado confirmado: enero Próxima,30 horas/12000, inicio 01/01/2027 y sin fin; febrero Cancelada,25 horas/13000, inicio 01/02/2027 y sin fin original.',
'Se restaura la condición anterior sin perder el registro cancelado ni alterar los datos autorizados.',
'En Cancelada, Sin fecha de fin sólo describe el registro histórico; el estado impide que aplique. El motivo de registro es original, no el motivo posterior de cancelación; consulta Historial para los cambios.','/admin/asignaciones'),
(12,'Cerrar el recorrido sin generar nómina','Administración → Guía de procesos → Personal',
'1. Conserva el resultado: enero 12000/30 horas sin fin y febrero 13000/25 horas Cancelada; no vuelvas a registrar ni cancelar para leer la guía. 2. Consulta esta guía cuando necesites repetir el procedimiento en un nuevo caso autorizado, adaptando fechas futuras y revisando antes las condiciones existentes. 3. Recuerda que estos módulos configuran sueldos y vigencias, no generan nómina, impuestos ni movimientos de caja o banco. 4. El siguiente tema a revisar con el administrador es Conceptos de nómina; todavía no es un paso operativo de este recorrido.',
'Recorrido principal y Excel confirmados por el propietario 21/10/2026. No se declara una auditoría exhaustiva de permisos ni comprobaciones independientes no reportadas.',
'Proceso documentado y listo para consulta sin repetir operaciones.',
'No retirar permisos al administrador principal ni publicar salarios para comprobar seguridad. Conceptos, cálculos y pagos de nómina requieren definición e implementación separadas.','/admin/guias')
)v(n,t,m,i,e,r,p,u) WHERE g.slug='asignaciones-laborales-configuracion-historica';

UPDATE guia_proceso_paso SET precaucion='Este recorrido individual está confirmado. Para catálogos separados y asignaciones consulta Guía de procesos → Personal → Puesto, jornada y sueldo: asignar, programar cambios y cancelar el futuro. No repetir el aumento cancelado. Conceptos y pagos de nómina se revisan por separado.'
WHERE guia_id=(SELECT id FROM guia_proceso WHERE slug='empleado-condiciones-salariales') AND numero=8;
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-21' WHERE slug='empleado-condiciones-salariales';
