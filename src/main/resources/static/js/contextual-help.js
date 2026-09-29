(() => {
    'use strict';

    const MODULES = [
        [/^\/admin\/catalogos\/instituciones|^\/admin\/instituciones/, 'Instituciones', 'Define los datos legales y operativos de cada institución que utiliza el sistema. Es la raíz que separa planteles, personas, seguridad y finanzas.', 'Registrar Instituto Raíces con su código, zona horaria y moneda de trabajo.'],
        [/^\/admin\/(catalogos\/)?planteles/, 'Planteles', 'Administra las sedes físicas de una institución y sus datos de contacto, ubicación y operación.', 'Crear el plantel Centro y relacionarlo con Instituto Raíces.'],
        [/^\/admin\/(catalogos\/)?niveles/, 'Niveles educativos', 'Organiza los niveles académicos disponibles, por ejemplo preescolar, primaria o secundaria.', 'Registrar Primaria con orden 2 para mostrarla después de Preescolar.'],
        [/^\/admin\/(catalogos\/)?oferta/, 'Oferta educativa', 'Indica qué niveles educativos ofrece cada plantel y conserva su clave oficial cuando aplica.', 'Habilitar Primaria en el plantel Centro con su clave CCT.'],
        [/^\/admin\/(catalogos\/)?grados/, 'Grados', 'Define los grados que pertenecen a cada nivel educativo y el orden en que se presentan.', 'Crear 1.º de primaria con orden 1.'],
        [/^\/admin\/(catalogos\/)?materias/, 'Materias', 'Administra las materias institucionales y configura cómo se evalúan en cada grado.', 'Crear Matemáticas y asignarla a 1.º de primaria con escala de 0 a 10.'],
        [/^\/admin\/(catalogos\/)?maestros/, 'Maestros', 'Administra el expediente docente, su cuenta independiente y las materias que puede impartir en cada grupo y vigencia.', 'Registrar a una maestra, crear su acceso y asignarle Matemáticas en 1.º A durante el ciclo vigente.'],
        [/^\/admin\/planeaciones/, 'Planeaciones semanales', 'Permite revisar las planeaciones enviadas por los maestros, solicitar ajustes, publicarlas y consultar sus versiones históricas.', 'Filtrar las planeaciones enviadas, abrir una semana y marcarla en revisión antes de publicarla.'],
        [/^\/admin\/(catalogos\/)?calificaciones/, 'Calificaciones', 'Captura resultados por grupo, materia y periodo; controla borradores y decide cuándo publicarlos para las familias.', 'Guardar parcialmente el primer bimestre y publicarlo cuando todos los alumnos tengan resultado.'],
        [/^\/admin\/(catalogos\/)?asistencia/, 'Asistencia diaria', 'Registra la asistencia de los alumnos por grupo y fecha, conservando el historial de presentes, ausencias, retardos y faltas justificadas.', 'Seleccionar 1.º A, elegir la fecha y guardar el estado de todos los alumnos del grupo.'],
        [/^\/admin\/boletas/, 'Boletas', 'Genera documentos académicos a partir de las calificaciones publicadas, sin duplicar resultados ni incluir borradores.', 'Filtrar el ciclo y el grupo, abrir la boleta de un alumno o exportar el conjunto a PDF y Excel.'],
        [/^\/admin\/(catalogos\/)?ciclos/, 'Ciclos escolares', 'Controla los periodos anuales de operación académica y cuál se utiliza de forma predeterminada.', 'Crear el ciclo 2026–2027 con sus fechas de inicio y fin.'],
        [/^\/admin\/(catalogos\/)?periodos/, 'Periodos académicos', 'Divide un ciclo escolar en bloques de evaluación o trabajo sin sustituir los periodos de cobro.', 'Crear el primer trimestre dentro del ciclo 2026–2027.'],
        [/^\/admin\/(catalogos\/)?grupos/, 'Grupos', 'Crea los grupos disponibles por plantel, ciclo, grado, turno y capacidad.', 'Registrar el grupo 1.º A matutino con capacidad para 30 alumnos.'],
        [/^\/admin\/(catalogos\/)?alumnos/, 'Alumnos', 'Concentra el expediente personal del alumno y sirve como base para su trayectoria académica, tutores, cargos y pagos.', 'Registrar matrícula IRA-2026-001 y los datos personales del alumno.'],
        [/^\/admin\/(catalogos\/)?tutores/, 'Tutores', 'Administra el expediente y contacto de madres, padres o responsables, incluida su identificación y acceso opcional al portal familiar.', 'Registrar a la madre del alumno y, si se autoriza, crear su acceso familiar.'],
        [/^\/admin\/(catalogos\/)?vinculos-tutor/, 'Vínculos alumno–tutor', 'Relaciona alumnos con sus tutores y define qué acciones puede realizar cada persona y durante qué vigencia.', 'Vincular a una madre como responsable financiera y autorizada para recibir notificaciones.'],
        [/^\/admin\/(catalogos\/)?inscripciones/, 'Inscripciones', 'Conserva la trayectoria del alumno por plantel, ciclo y grado sin sobrescribir su historial anterior.', 'Inscribir al alumno en 1.º A para el ciclo 2026–2027.'],
        [/^\/admin\/(catalogos\/)?conceptos-cobro/, 'Conceptos de cobro', 'Define qué se cobra, sus reglas generales y si permite becas, descuentos o recargos.', 'Crear el concepto COLEGIATURA para las mensualidades escolares.'],
        [/^\/admin\/(catalogos\/)?cuotas-alumno/, 'Cuotas por alumno', 'Configura el importe y calendario de cobro particular de un alumno antes de generar cargos.', 'Asignar una colegiatura mensual de $2,500 con vencimiento el día 10.'],
        [/^\/admin\/(catalogos\/)?cargos/, 'Cargos', 'Registra las obligaciones de pago del alumno y muestra su importe, aplicaciones, ajustes y saldo.', 'Emitir la colegiatura de septiembre con su fecha de vencimiento.'],
        [/^\/admin\/(catalogos\/)?tipos-beca/, 'Tipos de beca', 'Mantiene el catálogo de apoyos o becas que después pueden asignarse a los alumnos.', 'Crear la beca Académica para identificar ese beneficio.'],
        [/^\/admin\/(catalogos\/)?becas-alumno/, 'Becas por alumno', 'Asigna un beneficio a un alumno, con porcentaje o monto y una vigencia definida.', 'Aplicar 20 % de beca académica a la colegiatura durante el ciclo actual.'],
        [/^\/admin\/(catalogos\/)?ajustes-cargo/, 'Historial de ajustes', 'Consulta y registra correcciones trazables sobre cargos, como descuentos, becas, recargos o reversas.', 'Registrar un descuento autorizado de $200 con su motivo.'],
        [/^\/admin\/(catalogos\/)?politicas-recargo|^\/admin\/politicas-recargo/, 'Políticas de recargo', 'Configura cómo se calculan recargos sobre cargos vencidos, con gracia, periodicidad y límites.', 'Aplicar 5 % después de 5 días de gracia, hasta un máximo establecido.'],
        [/^\/admin\/(catalogos\/)?motivos-financieros|^\/admin\/motivos-financieros/, 'Motivos financieros', 'Clasifica ingresos y egresos para que los movimientos y reportes expliquen por qué entró o salió dinero.', 'Crear PAPELERIA como motivo de egreso en la categoría Operación escolar.'],
        [/^\/admin\/(catalogos\/)?cuentas-financieras|^\/admin\/cuentas-financieras/, 'Cuentas financieras', 'Administra cajas, bancos e inversiones donde se registran movimientos y saldos del sistema.', 'Registrar la cuenta BBVA Cobros con su saldo inicial y moneda MXN.'],
        [/^\/admin\/(catalogos\/)?pagos|^\/admin\/pagos/, 'Pagos', 'Registra, revisa y distribuye pagos entre cargos, conservando comprobantes, cuenta destino y trazabilidad.', 'Validar una transferencia de $3,000 y aplicarla a cargos autorizados.'],
        [/^\/admin\/movimientos-financieros|^\/admin\/transferencias-cuenta|^\/admin\/reversiones-financieras/, 'Movimientos financieros', 'Consulta y registra entradas, salidas, traspasos y reversas de las cuentas financieras.', 'Registrar un egreso operativo o transferir efectivo de caja a banco.'],
        [/^\/admin\/cortes-caja/, 'Cortes de caja', 'Controla la apertura y cierre de una caja, comparando el efectivo declarado con el saldo esperado.', 'Abrir la caja al inicio del turno y justificar cualquier diferencia al cerrarla.'],
        [/^\/admin\/retiros-fondo/, 'Retiros de fondos', 'Registra salidas de dinero hacia una persona o destino externo, con autorización y referencia.', 'Registrar el pago de un proveedor desde una cuenta bancaria.'],
        [/^\/admin\/reportes-financieros/, 'Reportes financieros', 'Resume tesorería, estados de cuenta y cobranza con filtros, exportaciones y datos trazables.', 'Consultar la cobranza al cierre del mes para un plantel autorizado.'],
        [/^\/admin\/eventos-escolares/, 'Eventos escolares', 'Publica actividades y fechas importantes para las familias según su alcance y destinatarios.', 'Publicar una junta para los tutores de un grupo específico.'],
        [/^\/admin\/avisos/, 'Avisos escolares', 'Prepara y publica mensajes institucionales o por plantel, conservando su vigencia e historial.', 'Publicar un aviso de suspensión de actividades hasta una fecha determinada.'],
        [/^\/admin\/(catalogos\/)?roles|^\/admin\/roles/, 'Roles y permisos', 'Agrupa el acceso administrativo por módulos completos para asignarlo posteriormente a usuarios.', 'Crear el rol Caja y habilitar los módulos que necesita para operar.'],
        [/^\/admin\/(catalogos\/)?usuarios|^\/admin\/usuarios/, 'Usuarios administrativos', 'Administra únicamente las cuentas del personal que ingresa a la consola administrativa.', 'Invitar a una persona y asignarle un rol con alcance institucional o por plantel.'],
        [/^\/admin\/auditoria/, 'Auditoría', 'Permite rastrear acciones sensibles sin modificar el historial, usando filtros por actor, entidad y fecha.', 'Localizar quién rechazó un pago y consultar el motivo registrado.'],
        [/^\/admin\/portal-soporte/, 'Soporte del portal familiar', 'Permite revisar en modo de sólo lectura lo que ve un tutor para ayudarle sin realizar operaciones en su nombre.', 'Buscar un tutor y comprobar qué hijos, avisos y pagos tiene disponibles.'],
        [/^\/portal\/pagos\/reportar/, 'Reportar una transferencia', 'Permite a la familia informar una transferencia, adjuntar comprobantes y distribuir el total entre uno o varios cargos autorizados.', 'Reportar $4,000 y repartirlos entre colegiaturas de dos hijos.'],
        [/^\/portal\/boletas/, 'Boletas familiares', 'Muestra las boletas oficiales disponibles para el hijo seleccionado y permite revisarlas en PDF sin descargarlas automáticamente.', 'Elegir el ciclo 2026–2027 y abrir la boleta en una pestaña nueva.'],
        [/^\/portal\/notificaciones/, 'Notificaciones familiares', 'Reúne novedades de avisos, eventos y resultados de pagos que corresponden a la familia.', 'Abrir una notificación para marcarla como leída y consultar su sección.'],
        [/^\/portal\/avisos/, 'Avisos para tu familia', 'Muestra únicamente los mensajes vigentes de la institución o plantel que corresponden al hijo seleccionado.', 'Consultar un aviso de cambio de horario publicado por la escuela.'],
        [/^\/portal\/agenda/, 'Agenda familiar', 'Muestra las próximas actividades escolares publicadas para el hijo seleccionado.', 'Revisar la hora y ubicación de una junta escolar.'],
        [/^\/portal\/pagos/, 'Pagos familiares', 'Presenta cargos, saldos y transferencias reportadas para los hijos que el tutor puede consultar financieramente.', 'Revisar cuánto falta por pagar y el estado de una transferencia.'],
        [/^\/portal$/, 'Portal familiar', 'Organiza en un solo inicio la información de cada hijo y ofrece accesos separados a notificaciones, avisos, agenda y pagos.', 'Seleccionar un hijo y abrir su agenda o estado de pagos.'],
        [/^\/familias$/, 'Acceso de familias', 'Permite a tutores autorizados ingresar a su portal privado con la cuenta creada por la escuela.', 'Escribir el usuario familiar y su contraseña personal.'],
        [/^\/login$/, 'Acceso al sistema', 'Permite al personal autorizado iniciar una sesión segura en la consola administrativa de su institución.', 'Escribir el usuario asignado y su contraseña personal.'],
        [/^\/activar-cuenta$/, 'Activación de cuenta', 'Permite establecer por primera vez la contraseña de una invitación vigente y dejar la cuenta lista para iniciar sesión.', 'Crear y confirmar una contraseña segura de al menos 12 caracteres.'],
        [/^\/restablecer-password$/, 'Cambio de contraseña', 'Permite establecer una contraseña nueva mediante un enlace de recuperación vigente y de un solo uso.', 'Crear y confirmar una contraseña nueva y segura.']
    ];

    const HELP = {
        codigo: ['Es una clave corta y única dentro de la institución. Facilita búsquedas, referencias e integraciones sin depender del nombre visible.', 'IRAICES01'],
        nombre: ['Es el nombre con el que este registro se mostrará en pantallas, búsquedas y documentos del sistema.', 'Instituto Raíces'],
        nombres: ['Captura únicamente el nombre o nombres de la persona, sin incluir sus apellidos.', 'María Fernanda'],
        primerapellido: ['Es el primer apellido de la persona y forma parte de su identificación y búsquedas.', 'García'],
        segundoapellido: ['Es el segundo apellido de la persona. Puede dejarse vacío cuando legalmente no exista.', 'López'],
        institucionid: ['Determina a qué institución pertenece la información y establece su aislamiento de seguridad.', 'Selecciona Instituto Raíces.'],
        plantelid: ['Define la sede relacionada con el registro o limita los resultados a ese plantel.', 'Selecciona Plantel Centro.'],
        planteloperacionid: ['Atribuye el movimiento al plantel que originó la operación, incluso si la cuenta es institucional.', 'Selecciona Plantel Centro.'],
        plantelregistroid: ['Indica el plantel donde se recibió o registró el pago.', 'Selecciona Plantel Centro.'],
        niveleducativoid: ['Relaciona el registro con un nivel educativo autorizado de la institución.', 'Selecciona Primaria.'],
        gradoid: ['Indica el grado académico correspondiente dentro del nivel seleccionado.', 'Selecciona 1.º de primaria.'],
        cicloescolarid: ['Indica el ciclo escolar al que pertenece la operación o información académica.', 'Selecciona 2026–2027.'],
        cicloid: ['Define el ciclo escolar cuyas calificaciones publicadas se utilizarán para generar las boletas.', 'Selecciona 2026–2027.'],
        periodoacademicoid: ['Relaciona el registro con un periodo de evaluación del ciclo, cuando corresponde.', 'Selecciona Primer trimestre.'],
        grupoid: ['Selecciona el grupo académico relacionado con el alumno o destinatario.', 'Selecciona 1.º A · Matutino.'],
        grupotexto: ['Permite buscar un grupo histórico del ciclo sin cargar el catálogo completo.', 'Enfoca el campo o escribe 1.º A y selecciona el resultado.'],
        alumnoid: ['Identifica al alumno sobre el que se realizará la consulta u operación.', 'Busca por matrícula o nombre y selecciona al alumno correcto.'],
        alumnotexto: ['Busca y muestra el alumno que se relacionará con el registro. La selección guarda su identificador de forma segura.', 'Escribe matrícula o nombre, por ejemplo IRA-2026-001.'],
        tutorid: ['Identifica al tutor o responsable relacionado con la operación.', 'Busca por nombre, teléfono o correo y selecciona al tutor.'],
        tutoretiqueta: ['Busca y muestra el tutor que se relacionará con la operación.', 'Escribe el nombre o teléfono y elige la coincidencia correcta.'],
        inscripcionid: ['Selecciona la inscripción vigente que conecta al alumno con plantel, ciclo y grado.', 'Selecciona la inscripción activa del ciclo 2026–2027.'],
        inscripcionanteriorid: ['Indica la inscripción previa cuando el nuevo registro continúa o cambia la trayectoria existente.', 'Selecciona la inscripción del ciclo o plantel anterior.'],
        numeroinscripcion: ['Clave legible que identifica una inscripción dentro de la institución.', 'INS-2026-0001'],
        conceptocobroid: ['Selecciona el concepto que explica qué se cobrará o ajustará.', 'Selecciona Colegiatura.'],
        tipobecaid: ['Selecciona el tipo de apoyo que se asignará al alumno.', 'Selecciona Beca académica.'],
        cuentaid: ['Selecciona la cuenta financiera cuyos movimientos o saldos se consultarán.', 'Selecciona BBVA Cobros.'],
        cuentatexto: ['Busca una cuenta financiera autorizada por código o nombre y conserva la selección realizada.', 'Escribe BBVA o CAJA y selecciona la cuenta.'],
        cuentaorigenid: ['Es la cuenta de la que saldrá el dinero. El sistema validará moneda, estado y saldo.', 'Selecciona Caja Plantel Centro.'],
        cuentaorigentexto: ['Busca y muestra la cuenta desde la que saldrá el dinero.', 'Escribe Caja y selecciona Caja Plantel Centro.'],
        cuentadestinoid: ['Es la cuenta que recibirá el dinero de la operación.', 'Selecciona BBVA Cobros.'],
        cuentadestinotexto: ['Busca y muestra la cuenta que recibirá el dinero.', 'Escribe BBVA y selecciona BBVA Cobros.'],
        cuentadeclaradaid: ['Es la cuenta a la que la persona indica haber enviado la transferencia; administración confirmará el destino real.', 'Selecciona la cuenta bancaria mostrada en tu comprobante.'],
        cuentadeclaradaetiqueta: ['Busca y muestra la cuenta que se declaró como destino del pago.', 'Escribe el banco o código y selecciona la cuenta indicada.'],
        motivofinancieroid: ['Clasifica la razón financiera del movimiento para reportes y auditoría.', 'Selecciona Papelería o Colegiaturas.'],
        matricula: ['Es la clave única del alumno dentro de la institución y se utiliza para localizar su expediente.', 'IRA-2026-001'],
        curp: ['Clave oficial de identidad en México. Si se captura, debe corresponder al alumno y no repetirse en la institución.', 'GARC100101MJCLPR09'],
        rfc: ['Registro fiscal de la institución. Se utiliza como dato legal y debe capturarse sin espacios.', 'IRA260101ABC'],
        razon_social: ['Es el nombre legal utilizado en documentos y trámites fiscales.', 'Instituto Raíces, A.C.'],
        razonsocial: ['Es el nombre legal utilizado en documentos y trámites fiscales.', 'Instituto Raíces, A.C.'],
        nombrecomercial: ['Es el nombre público o abreviado con el que las personas reconocen a la organización.', 'Instituto Raíces'],
        email: ['Correo de contacto asociado al expediente. Verifica que esté bien escrito antes de guardar.', 'contacto@institutoraices.mx'],
        telefono: ['Número principal de contacto. Incluye la clave de área cuando corresponda.', '33 1234 5678'],
        telefonoprincipal: ['Número principal para contactar a la persona.', '33 1234 5678'],
        telefonosecundario: ['Número alternativo para contactar a la persona cuando el principal no esté disponible.', '33 8765 4321'],
        telefonotrabajo: ['Número de contacto laboral del tutor, cuando resulte necesario.', '33 2468 1357'],
        sitioweb: ['Dirección pública del sitio web. Incluye https:// para formar un enlace válido.', 'https://institutoraices.mx'],
        zonahoraria: ['Define cómo se interpretan fechas y horas del sistema para esta institución.', 'America/Mexico_City'],
        moneda: ['Código de tres letras de la moneda usada en importes y movimientos.', 'MXN'],
        monedapredeterminada: ['Moneda que se propondrá por defecto en nuevas operaciones financieras.', 'MXN'],
        pais: ['País correspondiente al domicilio o configuración institucional.', 'México'],
        estado: ['Permite limitar resultados o definir la situación actual del registro según las opciones disponibles.', 'Selecciona Activo o Todos.'],
        situacion: ['Filtra o define la condición operativa del registro.', 'Selecciona Vigente.'],
        activo: ['Indica si el registro puede utilizarse en operaciones nuevas. Desactivarlo conserva el historial.', 'Marcado: disponible para nuevas operaciones.'],
        predeterminado: ['Hace que esta opción se proponga automáticamente cuando el usuario no elige otra.', 'Marcar el ciclo escolar actualmente vigente.'],
        orden: ['Controla la posición en que se muestra el registro frente a otros del mismo catálogo.', '1'],
        descripcion: ['Explica el propósito o características del registro con palabras claras para otros usuarios.', 'Cuota mensual correspondiente al servicio educativo.'],
        observaciones: ['Espacio para aclaraciones útiles que no pertenecen a otro campo estructurado.', 'Pago identificado con una referencia abreviada.'],
        titulo: ['Es el encabezado principal que verán los destinatarios o usuarios.', 'Junta general de familias'],
        contenido: ['Es el mensaje completo que se comunicará. Usa lenguaje claro y evita datos sensibles innecesarios.', 'El viernes las actividades terminarán a las 12:00.'],
        ubicacion: ['Indica dónde se realizará la actividad.', 'Auditorio del plantel Centro'],
        aula: ['Identifica el salón o espacio habitual del grupo.', 'Aula 3'],
        capacidad: ['Número máximo previsto de alumnos para el grupo.', '30'],
        turno: ['Indica el horario general en que opera el grupo.', 'Matutino'],
        modalidad: ['Describe la forma en que se imparte el servicio educativo.', 'Escolarizada'],
        clavecentrotrabajo: ['Clave oficial del centro de trabajo. Captúrala exactamente como fue asignada por la autoridad.', '14PPR0123A'],
        fechanacimiento: ['Fecha de nacimiento de la persona; se usa en su expediente e identificación.', '15/03/2015'],
        fechaingreso: ['Fecha en que la persona comenzó su relación con la institución.', '20/08/2026'],
        fechainscripcion: ['Fecha administrativa en que se formaliza la inscripción.', '20/08/2026'],
        fechainicio: ['Fecha desde la cual inicia la vigencia o periodo.', '01/08/2026'],
        fechafin: ['Fecha hasta la cual permanece vigente el periodo.', '31/07/2027'],
        fechadesde: ['Límite inicial del filtro; se incluirán registros desde esta fecha.', '01/09/2026'],
        fechahasta: ['Límite final del filtro; se incluirán registros hasta esta fecha.', '30/09/2026'],
        fechacorte: ['Fecha hasta la que se calculan saldos, vencimientos o resultados del proceso.', '30/09/2026'],
        fechaoperacion: ['Momento en que ocurrió la entrada o salida de dinero.', '27/09/2026 10:30'],
        fechapago: ['Fecha y hora que aparece en el comprobante o en que se recibió el pago.', '27/09/2026 10:30'],
        fechasaldoinicial: ['Fecha a partir de la cual el saldo inicial representa la posición de la cuenta.', '01/09/2026'],
        fechavencimiento: ['Último día previsto para cubrir el cargo antes de considerarlo vencido.', '10/09/2026'],
        fechavencimientounico: ['Fecha de vencimiento aplicada cuando la cuota genera un solo cargo.', '10/09/2026'],
        fechaefectiva: ['Fecha contable o administrativa en la que el ajuste surte efecto.', '27/09/2026'],
        fechaemision: ['Fecha en la que el cargo se considera emitido y comienza su historial.', '01/09/2026'],
        fecha: ['Fecha correspondiente a la operación, filtro o vigencia mostrada en la pantalla.', '27/09/2026'],
        iniciolocal: ['Fecha y hora de inicio mostradas en la zona horaria institucional.', '05/10/2026 09:00'],
        finlocal: ['Fecha y hora en que termina el evento.', '05/10/2026 11:00'],
        expiralocal: ['Momento opcional a partir del cual el aviso deja de mostrarse a las familias.', '10/10/2026 23:59'],
        monto: ['Importe monetario de la operación. Captúralo sin símbolo y con hasta dos decimales.', '1500.50'],
        montosolicitado: ['Importe del pago que se desea aplicar al cargo seleccionado. La suma distribuida debe coincidir con el total reportado.', '1500.00'],
        importeoriginal: ['Importe base del cargo antes de becas, descuentos, recargos o correcciones.', '2500.00'],
        importebase: ['Importe sobre el que se realizará el cálculo o generación.', '2500.00'],
        saldoinicial: ['Cantidad existente en la cuenta antes de registrar movimientos desde la fecha indicada.', '10000.00'],
        porcentaje: ['Porcentaje que se aplicará al cálculo. Captura sólo el número, sin el símbolo %.', '20'],
        montofijo: ['Cantidad fija que se aplicará en lugar de un porcentaje cuando esa modalidad esté seleccionada.', '200.00'],
        valorlimite: ['Tope máximo permitido por la política seleccionada.', '500.00'],
        efectivodeclarado: ['Cantidad de efectivo contada físicamente al cerrar la caja; se comparará con el saldo esperado.', '3250.00'],
        justificaciondiferencia: ['Explica por qué el efectivo declarado no coincide con el esperado. Es obligatoria cuando existe diferencia.', 'Faltante documentado por cambio entregado incorrectamente.'],
        referencia: ['Dato externo que ayuda a rastrear la operación, como folio bancario o número de comprobante.', 'Rastreo 123456789'],
        folio: ['Clave legible que identifica la operación dentro de la institución.', 'PAG-2026-000123'],
        claveidempotencia: ['Clave única que evita registrar dos veces la misma operación si se reintenta el envío.', 'RETIRO-20260927-001'],
        concepto: ['Resumen breve que explica qué representa el movimiento u operación.', 'Compra de material didáctico'],
        categoria: ['Agrupa registros semejantes para facilitar filtros y reportes.', 'Operación escolar'],
        naturaleza: ['Indica si el motivo puede utilizarse en ingresos, egresos o ambos.', 'EGRESO'],
        direccion: ['Define si el movimiento aumenta o disminuye el saldo de la cuenta.', 'EGRESO'],
        clase: ['Clasifica el origen operativo del movimiento para su trazabilidad.', 'OPERACIÓN'],
        metodo: ['Indica cómo se recibió el pago.', 'TRANSFERENCIA'],
        tipo: ['Clasifica el registro y determina reglas adicionales disponibles.', 'Selecciona la opción que corresponda.'],
        efecto: ['Indica si el ajuste aumenta o disminuye el importe del cargo.', 'DISMINUCIÓN para un descuento.'],
        alcance: ['Define hasta dónde aplica la información o qué destinatarios podrán verla.', 'PLANTEL'],
        agrupacion: ['Define el criterio utilizado para resumir las filas del reporte.', 'Agrupar por concepto de cobro.'],
        accion: ['Filtra la bitácora por el tipo de operación realizada sobre los datos.', 'Selecciona PAGO_RECHAZAR.'],
        frecuencia: ['Indica cada cuánto se generará el cargo configurado.', 'MENSUAL'],
        periodicidad: ['Define cada cuánto puede aplicarse nuevamente la regla.', 'Una vez por mes.'],
        periodo: ['Selecciona el periodo temporal que se consultará en el reporte.', 'Mensual'],
        periodocobroinicio: ['Primer mes en el que la configuración puede generar cargos.', 'Agosto de 2026'],
        periodocobrofin: ['Último mes incluido en la generación configurada.', 'Julio de 2027'],
        diasgracia: ['Cantidad de días posteriores al vencimiento antes de aplicar un recargo.', '5'],
        diavencimiento: ['Día del mes en que vencerán los cargos periódicos.', '10'],
        mes: ['Mes que se utilizará para la consulta o generación.', 'Septiembre'],
        anio: ['Año que se utilizará para la consulta o reporte.', '2026'],
        tamanio: ['Cantidad máxima de filas mostradas en cada página. No cambia el total de resultados.', '25 registros por página.'],
        texto: ['Busca coincidencias en los campos principales del módulo y reduce la lista mostrada.', 'Escribe un código, nombre o referencia.'],
        q: ['Escribe parte del nombre, código o dato indicado para localizar coincidencias.', 'María García'],
        actor: ['Filtra la auditoría por la persona o proceso que realizó la acción.', 'criz110'],
        tipoentidad: ['Filtra la auditoría por la clase de registro afectado.', 'PAGO'],
        entidadid: ['Busca la acción realizada sobre un identificador interno concreto.', '125'],
        correlacion: ['Agrupa eventos técnicos relacionados con una misma solicitud para rastrear el flujo completo.', 'Identificador mostrado en una incidencia.'],
        username: ['Nombre único utilizado para iniciar sesión. Evita espacios y usa una nomenclatura fácil de reconocer.', 'nombre.apellido'],
        password: ['Contraseña privada de acceso. Debe cumplir la longitud solicitada y no reutilizarse en otros servicios.', 'Una frase segura de al menos 12 caracteres.'],
        confirmarpassword: ['Repite exactamente la contraseña anterior para detectar errores de captura.', 'La misma frase segura del campo anterior.'],
        permisoids: ['Selecciona los módulos completos a los que tendrá acceso el rol.', 'Alumnos, Tutores e Inscripciones.'],
        rolid: ['Selecciona el rol administrativo que se asignará al usuario dentro del alcance indicado.', 'Selecciona Caja.'],
        activar: ['Indica si la asignación o registro debe quedar disponible para utilizarse.', 'Marcado para dejar el acceso activo.'],
        parentesco: ['Describe la relación del tutor con el alumno.', 'MADRE'],
        parentescootro: ['Especifica la relación cuando la opción seleccionada fue “Otro”.', 'Abuela materna'],
        responsablefinanciero: ['Autoriza a esta persona para consultar y atender la información financiera del alumno.', 'Marcado para el tutor que administrará los pagos.'],
        puedeverfinanzas: ['Permite que el tutor consulte cargos, saldos y pagos del alumno en el portal.', 'Marcado para mostrar la sección Tus pagos.'],
        puederecibirnotificaciones: ['Permite que el tutor reciba avisos y notificaciones relacionadas con el alumno.', 'Marcado para recibir novedades en el portal.'],
        puederecoger: ['Registra que la persona está autorizada para recoger al alumno.', 'Marcado cuando la institución verificó la autorización.'],
        puedeautorizar: ['Registra que el tutor puede aprobar decisiones o trámites definidos por la institución.', 'Marcado para un tutor con representación autorizada.'],
        archivo: ['Adjunta un documento permitido al expediente. Verifica que sea legible y no contenga información innecesaria.', 'Un archivo PDF, JPG o PNG dentro del tamaño permitido.'],
        comprobantes: ['Adjunta la evidencia de la transferencia para que administración pueda revisarla.', 'PDF o imagen legible del comprobante bancario.'],
        beneficiario: ['Nombre de la persona o destino externo que recibe el dinero.', 'Proveedor de material escolar'],
        terceronombre: ['Persona, empresa o destino relacionado con el movimiento.', 'Papelería del Centro, S.A.'],
        titular: ['Nombre de la persona u organización registrada como titular de la cuenta.', 'Instituto Raíces, A.C.'],
        numerocuenta: ['Identificador bancario de la cuenta. Se conserva como dato privado y debe capturarse con exactitud.', '0123456789'],
        clabe: ['Clave bancaria estandarizada de 18 dígitos para transferencias en México.', '012345678901234567'],
        banconombre: ['Nombre del banco o institución financiera que administra la cuenta.', 'BBVA México'],
        calle: ['Nombre de la vialidad del domicilio.', 'Avenida de las Raíces'],
        numeroexterior: ['Número exterior del inmueble.', '120'],
        numerointerior: ['Número interior, oficina o departamento, cuando exista.', 'Local 2'],
        colonia: ['Colonia o sector del domicilio.', 'Centro'],
        codigopostal: ['Código postal del domicilio.', '44100'],
        ciudad: ['Ciudad o municipio del domicilio.', 'Guadalajara'],
        domiciliofiscal: ['Domicilio utilizado para fines legales o fiscales.', 'Av. de las Raíces 120, Centro, Guadalajara, Jalisco.'],
        nacionalidad: ['Nacionalidad registrada en el expediente de la persona.', 'Mexicana'],
        sexo: ['Dato de identificación solicitado por el expediente escolar.', 'Selecciona la opción correspondiente.'],
        lugarnacimiento: ['Ciudad y estado o país donde nació la persona.', 'Guadalajara, Jalisco'],
        ocupacion: ['Actividad u ocupación principal del tutor.', 'Contadora'],
        lugartrabajo: ['Empresa o institución donde trabaja el tutor.', 'Servicios Profesionales del Centro'],
        contactoprincipal: ['Indica si este medio o persona debe usarse como contacto preferente.', 'Marcado para el número que se atiende normalmente.'],
        motivo: ['Explica de manera concreta por qué se realiza la acción. Se guarda para historial y auditoría.', 'Corrección autorizada por captura duplicada.'],
        motivobajacancelacion: ['Explica por qué termina o se cancela el registro, sin eliminar su historial.', 'Cambio de plantel solicitado por la familia.'],
        motivorechazocancelacion: ['Explica por qué el pago no fue aceptado o tuvo que cancelarse.', 'El comprobante corresponde a otra cuenta.'],
        motivoimportepersonalizado: ['Justifica por qué se usa un importe distinto del configurado normalmente.', 'Convenio autorizado por dirección.'],
        nombredelpagador: ['Identifica a quien realizó el pago cuando no coincide con el tutor registrado.', 'Juan Pérez'],
        nombrepagador: ['Identifica a quien realizó el pago cuando no coincide con el tutor registrado.', 'Juan Pérez'],
        generacionautomatica: ['Permite que el sistema genere cargos conforme a la configuración, sin perder controles de duplicidad.', 'Marcado para una colegiatura mensual.'],
        permitebeca: ['Indica si los cargos de este concepto pueden recibir ajustes de beca.', 'Marcado para Colegiatura.'],
        permitedescuento: ['Indica si se pueden registrar descuentos autorizados sobre este concepto.', 'Marcado cuando la política escolar lo permite.'],
        permiterecargo: ['Indica si el concepto admite recargos por vencimiento.', 'Marcado para colegiaturas sujetas a mora.'],
        tipolimite: ['Define si el tope del recargo se expresa como monto, porcentaje u otra modalidad disponible.', 'Monto máximo de $500.00.'],
        destinatarios: ['Selecciona los grupos, grados, niveles o alumnos específicos que recibirán la comunicación.', 'Selecciona 1.º A para enviar el evento sólo a sus familias.'],
        aplicacionidsrevertir: ['Selecciona qué aplicaciones del pago deben revertirse como parte de la corrección.', 'Marca la aplicación del cargo que se liberará.'],
        cargoetiqueta: ['Busca el cargo del alumno al que se destinará parte del pago.', 'Escribe matrícula o concepto y selecciona Colegiatura septiembre.'],
        version: ['Dato técnico utilizado para evitar que dos personas sobrescriban cambios al mismo tiempo.', 'Se conserva automáticamente; no necesita capturarse.']
    };

    const MODULE_FIELD = {
        'Instituciones:codigo': ['Es la nomenclatura única de la institución. Se utiliza para diferenciarla, formar referencias y resolver accesos cuando un usuario se repite en otra institución.', 'IRAICES'],
        'Planteles:codigo': ['Clave corta y única del plantel dentro de su institución. Facilita búsquedas, reportes y referencias internas.', 'CENTRO'],
        'Niveles educativos:codigo': ['Clave breve del nivel educativo dentro de la institución.', 'PRIM'],
        'Grados:codigo': ['Clave que identifica el grado dentro del nivel educativo.', 'P1'],
        'Ciclos escolares:codigo': ['Clave estable del ciclo escolar utilizada en relaciones académicas y reportes.', '2026-2027'],
        'Conceptos de cobro:codigo': ['Clave única que identifica el concepto aun si después cambia su nombre visible.', 'COLEGIATURA'],
        'Cuentas financieras:codigo': ['Clave interna y única de la cuenta para búsquedas y movimientos.', 'BBVA-COBROS'],
        'Motivos financieros:codigo': ['Clave estable para clasificar movimientos y reportes financieros.', 'PAPELERIA'],
        'Roles y permisos:codigo': ['Clave interna del rol; permite reconocerlo sin depender de su nombre visible.', 'CAJA'],
        'Tipos de beca:codigo': ['Clave corta y única del tipo de beca.', 'ACADEMICA']
    };

    const clean = value => (value || '').toString().normalize('NFD').replace(/[\u0300-\u036f]/g, '')
        .replace(/\[[^\]]*]/g, '').replace(/[^a-zA-Z0-9]/g, '').toLowerCase();

    const moduleForPath = () => {
        const path = window.location.pathname;
        const found = MODULES.find(([pattern]) => pattern.test(path));
        if (found) return {title: found[1], description: found[2], example: found[3]};
        const heading = document.querySelector('.topbar h1,.form-intro h1,.form-intro h2,main h1');
        return heading ? {title: heading.textContent.trim(), description: 'Esta pantalla concentra la información y operaciones relacionadas con el proceso indicado.', example: 'Consulta los datos disponibles y completa únicamente los campos necesarios.'} : null;
    };

    const fieldKey = control => {
        const raw = control.getAttribute('name') || control.id || control.getAttribute('th:field') || '';
        const segments = raw.replace(/^\*\{|}$/g, '').split(/[.\[\]]/).filter(Boolean);
        return clean(segments.at(-1) || raw);
    };

    const visibleControl = control => control && control.type !== 'hidden'
        && control.type !== 'submit' && control.type !== 'button' && control.type !== 'reset'
        && !['nav-toggle', '_csrf'].includes(control.id) && control.getAttribute('aria-hidden') !== 'true';

    const labelText = label => {
        const clone = label.cloneNode(true);
        clone.querySelectorAll('input,select,textarea,button,small,.context-help-trigger').forEach(node => node.remove());
        return clone.textContent.replace(/\*/g, '').replace(/\s+/g, ' ').trim() || 'Este campo';
    };

    const fallback = (control, label) => {
        const subject = label.toLowerCase();
        const placeholder = control.getAttribute('placeholder');
        if (control.type === 'file') return [`Permite adjuntar ${subject} al registro. Usa únicamente archivos autorizados, legibles y dentro del tamaño indicado en la pantalla.`, placeholder || 'Selecciona un archivo válido desde tu dispositivo.'];
        if (control.type === 'checkbox' || control.type === 'radio') return [`Activa o selecciona la opción “${label}”. Su valor modifica cómo se procesa o consulta este registro.`, `Marca la opción cuando corresponda al caso.`];
        if (control.tagName === 'SELECT') return [`Permite elegir ${subject} entre las opciones disponibles para tu institución y permisos. La selección se usará para relacionar o filtrar la información.`, 'Selecciona la opción que corresponda al registro.'];
        if (control.type === 'date' || control.type === 'datetime-local' || control.type === 'month') return [`Define ${subject} para delimitar la vigencia, operación o consulta. Verifica el periodo antes de guardar.`, placeholder || 'Selecciona la fecha correspondiente en el calendario.'];
        if (control.type === 'number') return [`Captura el valor numérico de ${subject}. Respeta los límites y decimales permitidos por el campo.`, placeholder && placeholder !== '0.00' ? placeholder : '100.00'];
        if (control.type === 'search') return [`Reduce los resultados buscando por ${subject}. La consulta se realiza sobre los datos autorizados para tu cuenta.`, placeholder || 'Escribe al menos tres caracteres.'];
        return [`Captura ${subject} tal como debe conservarse y mostrarse en el sistema. Revisa la información antes de guardar.`, placeholder || `Ejemplo de ${subject}.`];
    };

    let overlay;
    let previousFocus;

    const ensureModal = () => {
        if (overlay) return overlay;
        overlay = document.createElement('div');
        overlay.className = 'context-help-overlay';
        overlay.hidden = true;
        overlay.innerHTML = '<section class="context-help-dialog" role="dialog" aria-modal="true" aria-labelledby="context-help-title" aria-describedby="context-help-description"><header><div class="context-help-heading"><span aria-hidden="true">i</span><div><small id="context-help-kind">Ayuda del campo</small><h2 id="context-help-title"></h2></div></div><button class="context-help-close" type="button" aria-label="Cerrar ayuda">×</button></header><div class="context-help-body"><p id="context-help-description"></p><div class="context-help-example"><strong>Ejemplo</strong><span id="context-help-example"></span></div><p class="context-help-note" id="context-help-note"></p></div><footer><button type="button" data-context-help-accept>Entendido</button></footer></section>';
        document.body.appendChild(overlay);
        const close = () => {
            overlay.hidden = true;
            document.body.classList.remove('context-help-open');
            document.querySelectorAll('.context-help-trigger[aria-expanded=true]').forEach(button => button.setAttribute('aria-expanded', 'false'));
            if (previousFocus) previousFocus.focus();
        };
        overlay.querySelector('.context-help-close').addEventListener('click', close);
        overlay.querySelector('[data-context-help-accept]').addEventListener('click', close);
        overlay.addEventListener('click', event => { if (event.target === overlay) close(); });
        document.addEventListener('keydown', event => {
            if (overlay.hidden) return;
            if (event.key === 'Escape') close();
            if (event.key === 'Tab') {
                const focusable = [...overlay.querySelectorAll('button')];
                const first = focusable[0], last = focusable.at(-1);
                if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus(); }
                else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus(); }
            }
        });
        return overlay;
    };

    const openHelp = ({title, description, example, kind = 'Ayuda del campo', note = 'Esta ayuda no modifica la información capturada.'}, trigger) => {
        const modal = ensureModal();
        previousFocus = trigger || document.activeElement;
        modal.querySelector('#context-help-kind').textContent = kind;
        modal.querySelector('#context-help-title').textContent = title;
        modal.querySelector('#context-help-description').textContent = description;
        modal.querySelector('#context-help-example').textContent = example;
        modal.querySelector('#context-help-note').textContent = note;
        modal.hidden = false;
        document.body.classList.add('context-help-open');
        if (trigger) trigger.setAttribute('aria-expanded', 'true');
        modal.querySelector('.context-help-close').focus();
    };

    const findHeading = (label, control) => {
        const direct = [...label.children].find(child => child.tagName === 'SPAN' && !child.classList.contains('portal-upload-icon') && !child.classList.contains('portal-file-button'));
        if (direct) return direct.querySelector(':scope > strong') || direct;
        const external = control.id ? document.querySelector(`label[for="${CSS.escape(control.id)}"]`) : null;
        return external || label;
    };

    const enhanceLabel = (label, module, providedControl = null) => {
        if (label.dataset.contextHelpReady === 'true' || label.classList.contains('nav-backdrop') || label.classList.contains('menu-button')) return;
        const controls = providedControl ? [providedControl] : [...label.querySelectorAll('input,select,textarea')].filter(visibleControl);
        if (!controls.length) return;
        const control = controls.find(item => item.type !== 'checkbox' && item.type !== 'radio') || controls[0];
        const title = labelText(label);
        if (!title || title.length > 100) return;
        const key = fieldKey(control) || clean(title);
        const info = MODULE_FIELD[`${module?.title || ''}:${key}`] || HELP[key] || HELP[clean(title)] || fallback(control, title);
        const heading = findHeading(label, control);
        heading.classList.add('context-help-label');
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'context-help-trigger';
        button.textContent = 'i';
        button.setAttribute('aria-label', `Información sobre ${title}`);
        button.setAttribute('aria-haspopup', 'dialog');
        button.setAttribute('aria-expanded', 'false');
        button.addEventListener('click', event => {
            event.preventDefault();
            event.stopPropagation();
            openHelp({title, description: info[0], example: info[1]}, button);
        });
        heading.appendChild(button);
        label.dataset.contextHelpReady = 'true';
    };

    const addModuleHelp = module => {
        const isAccessScreen = /^\/(login|familias|activar-cuenta|restablecer-password)$/.test(window.location.pathname);
        if (!module || isAccessScreen || document.querySelector('.context-module-help')) return;
        const main = document.querySelector('main');
        if (!main) return;
        const card = document.createElement('aside');
        card.className = 'context-module-help';
        card.setAttribute('aria-label', `Información del módulo ${module.title}`);
        const summary = document.createElement('span');
        summary.innerHTML = '<i aria-hidden="true">i</i><span><strong></strong><small>Consulta qué hace y cuándo utilizarlo.</small></span>';
        summary.querySelector('strong').textContent = module.title;
        const button = document.createElement('button');
        button.type = 'button';
        button.textContent = '¿Qué hace este módulo?';
        button.addEventListener('click', () => openHelp({title: module.title, description: module.description, example: module.example, kind: 'Información general', note: 'La explicación es informativa; tus permisos determinan las acciones disponibles.'}, button));
        card.append(summary, button);
        const header = main.querySelector(':scope > .topbar,:scope > .form-top');
        if (header) header.insertAdjacentElement('afterend', card);
        else main.prepend(card);
    };

    const enhance = () => {
        const module = moduleForPath();
        addModuleHelp(module);
        document.querySelectorAll('label').forEach(label => enhanceLabel(label, module));
        document.querySelectorAll('input,select,textarea').forEach(control => {
            if (!visibleControl(control) || control.closest('label') || !control.id) return;
            const label = document.querySelector(`label[for="${CSS.escape(control.id)}"]`);
            if (label) enhanceLabel(label, module, control);
        });
    };

    const start = () => {
        document.body.classList.toggle('context-help-family', window.location.pathname.startsWith('/portal')
            || window.location.pathname === '/familias');
        enhance();
        let scheduled = false;
        new MutationObserver(() => {
            if (scheduled) return;
            scheduled = true;
            requestAnimationFrame(() => { scheduled = false; enhance(); });
        }).observe(document.body, {childList: true, subtree: true});
    };

    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', start, {once: true});
    else start();
})();
