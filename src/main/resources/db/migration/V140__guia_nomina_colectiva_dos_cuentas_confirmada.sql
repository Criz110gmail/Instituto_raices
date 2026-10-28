-- Sólo documentación de un recorrido confirmado; no registra pagos ni modifica saldos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('nomina-colectiva-dos-cuentas-confirmada','Nómina: revisar el cálculo y pagar desde banco y caja','Personal',
'Consulta configuración, simula el periodo, revisa el borrador y registra un pago colectivo con varias fuentes. Comprueba egresos y saldos por cuenta sin duplicar el pago.',
'Administrador institucional identificado con permisos laborales, configuración de nómina, administración salarial, pagos y consulta financiera. Para leer esta guía se requieren sus permisos de consulta y Guía de procesos. El caso de Diana ya fue pagado: consultar, no repetir. Las cuentas y códigos son ejemplos del ensayo; seleccionar registros autorizados de tu institución.',
'Confirmado por el usuario el 28/10/2026: Diana Prueba Puestos, JUL-2027-RED-TEST, 01/07/2027 al 31/07/2027, BOR-000002. Sueldo $14,000.00 + adicionales $630.00 - deducciones $442.85 = $14,187.15. Fuentes: BBVA_COBROS $10,000.00 y CAJA $4,187.15. Referencia financiera NPI-1. No implica timbrado ni dispersión bancaria real.',1,DATE '2026-10-28','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_LEER','EMPLEADO_NOMINA_LEER','CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER')
WHERE g.slug='nomina-colectiva-dos-cuentas-confirmada';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Localizar empleado y configuración sin duplicarla','Personal y nómina → Empleados → Ver expediente → Configuración de nómina',
'1. Inicia sesión con una cuenta administrativa autorizada y entra a Personal y nómina → Empleados. 2. Busca Diana Prueba Puestos y pulsa Ver expediente. 3. Pulsa el botón Configuración de nómina: abre una pantalla independiente con Conceptos configurados, Simulador por fechas y Puesto y sueldo. 4. En Conceptos configurados consulta los importes y sus vigencias; no agregues otra vez conceptos existentes ni sueldo base.',
'Transporte mensual $80 desde julio; apoyo quincenal $150; apoyo único $250 sólo JUL-2027-RED-TEST; deducción semanal $100. El transporte anterior $50 termina en junio.',
'Configuración identificada; cambiar pestañas no guarda ni paga.',
'El sueldo proviene del historial de puestos. Los importes de referencia tienen frecuencias distintas: no sumarlos directamente como si todos fueran mensuales.','/admin/empleados'),
(2,'Simular julio con fechas completas','Empleados → Ver expediente → Configuración de nómina → Simulador por fechas',
'1. Desde la configuración de Diana abre Simulador por fechas. 2. En Vigencia desde elige 01/07/2027 y en Vigencia hasta 31/07/2027. 3. Pulsa Simular nómina; el resultado aparece en esa misma pestaña. 4. Comprueba sueldo $14,000.00, adicionales $630.00, percepciones totales $14,630.00, deducciones $442.85 y neto estimado $14,187.15.',
'Adicionales: $80 mensual + $300 por las dos quincenas + $250 de una sola aplicación. Deducción semanal proporcional por días reales y ajustes de centavos: $442.85.',
'Simulación confirmada de $14,187.15; no crea periodo, borrador, reserva ni movimiento.',
'Reconoce el periodo por sus fechas exactas para incluir el concepto único. Es una estimación con configuración actual, no un saldo pendiente ni sustituto del histórico pagado.','/admin/empleados'),
(3,'Revisar periodo y nómina existentes','Personal y nómina → Periodos de nómina y Nóminas',
'1. Entra a Periodos de nómina y consulta JUL-2027-RED-TEST; comprueba cobertura 01/07/2027 al 31/07/2027. 2. Entra a Personal y nómina → Nóminas y localiza BOR-000002 del mismo periodo. 3. Pulsa Ver y comprueba un empleado, Diana, con importe $14,187.15; revisa Empleados y su desglose. 4. Si el registro ya está pagado, continúa sólo con la comprobación financiera de los pasos siguientes; no crees otro borrador.',
'La copia guardada conserva sueldo, conceptos y cálculo por vigencias. El periodo no requiere calendario del flujo anterior.',
'Periodo y fotografía histórica identificados sin repetir la preparación.',
'Para un ensayo nuevo autorizado, prepara el periodo, incluye al empleado y guarda el borrador tras revisar su modal. Guardar borrador no mueve dinero; no repetir estas acciones sobre julio ya concluido.','/admin/preparacion-nomina'),
(4,'Revisar autorización y fuentes del pago','Nóminas → Ver → Autorización y pagos por empleado → Resumen',
'1. Desde Ver de la nómina entra a Autorización y pagos por empleado. 2. Revisa Resumen y Pagos por empleado; si ya aparece Pagado, consulta Ver pago e intentos sin volver a pagar. 3. Sólo en un ensayo nuevo pendiente autorizado, revisa las decisiones de la nómina y pulsa Preparar pago colectivo cuando esté disponible. 4. Anota los saldos previos de las cuentas; la suma de las fuentes debe coincidir exactamente con el importe a pagar.',
'Antes del pago confirmado: BBVA_COBROS $33,330.00 y CAJA $16,950.00; CAJA-CORTE-TEST $1,150.00 no participa.',
'Fuentes e importe revisados antes de cualquier egreso.',
'Si una cuenta no alcanza, revisa disponible, solicitado y faltante. No se compensa automáticamente con otra cuenta; no confirmar hasta corregir la distribución.','/admin/preparacion-nomina'),
(5,'Capturar banco y caja y revisar confirmación','Nóminas → Ver → Autorización y pagos por empleado → Preparar pago colectivo',
'1. Para un pago nuevo autorizado, selecciona BBVA_COBROS como primera Cuenta de origen y captura $10,000.00; el medio Transferencia se asigna automáticamente. 2. Pulsa Agregar otra cuenta, selecciona CAJA y captura $4,187.15; el medio Efectivo se asigna automáticamente. 3. Selecciona el motivo financiero autorizado, captura referencia NOM-RED-JUL-2027 y motivo Pago íntegro de Diana para julio2027 desde banco y caja.; pulsa Revisar distribución. 4. Comprueba total $14,187.15 y ambas cuentas; al confirmar, prueba Cancelar dentro del modal antes de revisar y confirmar definitivamente en un ensayo nuevo.',
'Una fuente inicial; Agregar otra cuenta permite repartir. Quitar cuenta elimina sólo esa fuente y obliga a revisar de nuevo.',
'El pago confirmado guarda dos fuentes; cancelar el modal no registra movimientos.',
'No ejecutar nuevamente este paso para BOR-000002: ya está pagado. Es un registro interno, no una transferencia bancaria enviada por el sistema.','/admin/preparacion-nomina'),
(6,'Comprobar empleado pagado e historial','Nóminas → Ver → Autorización y pagos por empleado → Pagos por empleado',
'1. Abre Pagos por empleado. 2. En Buscar empleado enfoca para ver hasta diez empleados de esa nómina o escribe Dia y selecciona Diana; aplica el estado Pagado. 3. Pulsa Ver pago e intentos y consulta Historial de intentos. 4. Comprueba el pago vigente de $14,187.15 con fuentes BBVA_COBROS $10,000.00 y CAJA $4,187.15; vuelve a la nómina sin confirmar otro pago.',
'El historial registra cuentas, importes e identificadores de movimiento; el caso financiero confirmado tiene referencia NPI-1.',
'Diana pagada, sin pendiente ordinario de julio; se mantiene su configuración.',
'No marcar como probado un escenario adicional sólo por consultar esta guía. Cancelación, nuevo pago y concurrencia del flujo nuevo requieren ensayos independientes.','/admin/preparacion-nomina'),
(7,'Verificar egresos y saldos por cuenta','Administración → Movimientos financieros y Cuentas financieras',
'1. Entra a Movimientos financieros y localiza los egresos NOMINA del pago BOR-000002, referencia NPI-1. 2. Comprueba egreso $10,000.00 en BBVA_COBROS, saldo anterior $33,330.00 y posterior $23,330.00. 3. Comprueba egreso $4,187.15 en CAJA, saldo anterior $16,950.00 y posterior $12,762.85. 4. Entra a Cuentas financieras y verifica esos saldos actuales; CAJA-CORTE-TEST debe permanecer $1,150.00 si no hubo otros movimientos posteriores.',
'Usuario confirmó movimientos del 28/10/2026 a las 11:32, total egresado $14,187.15 y saldos $23,330.00 / $12,762.85.',
'Pago y afectación financiera de las dos fuentes confirmados; otra caja no afectada.',
'Estos saldos son evidencia del ensayo, no valores fijos del sistema. Si después hubo otros movimientos, concilia el importe de este pago con su saldo anterior y posterior.','/admin/movimientos-financieros'),
(8,'Cerrar el caso sin repetirlo','Personal y nómina → Empleados y Nóminas',
'1. Vuelve a Empleados → Diana → Configuración de nómina y confirma que la configuración conserva sus importes y vigencias. 2. Si consultas de nuevo el simulador de julio, distingue estimación actual de nómina histórica pagada: simular no autoriza otro pago. 3. Conserva folio BOR-000002 y evidencia de las dos fuentes como cierre confirmado del ensayo. 4. Deja pendiente la prueba independiente de cancelación y nuevo pago hasta recibir sus instrucciones; no cancelar ahora sólo por leer esta guía.',
'Cierre confirmado: sueldo $14,000.00 + percepciones adicionales $630.00 - deducciones $442.85 = $14,187.15; banco $10,000.00 y caja $4,187.15.',
'Prueba de pago colectivo y simulador documentada sin nuevas operaciones.',
'La cancelación requiere motivo y recuperación real del dinero; no forma parte de la aceptación de este caso. No se borran pagos ni se modifica el histórico.','/admin/empleados')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='nomina-colectiva-dos-cuentas-confirmada';
