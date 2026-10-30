-- Documentación exclusivamente: no modifica nóminas, conceptos de empleados ni dinero.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('nomina-actualizacion-configuracion-confirmada','Nómina: actualizar configuración y bloquear una propuesta desactualizada','Personal',
'Conserva las revisiones originales, actualiza sólo después de revisar diferencias y rechaza una vista abierta antes de cambiar la configuración del empleado.',
'Administrador con permisos privados salariales y de configuración. Ensayo concluido: consultar BOR-000009 sin repetir altas, confirmar otra revisión ni pagar. Las dos pestañas A y B comparten sesión; A conserva la propuesta y B modifica temporalmente la configuración. Los permisos de lectura de esta guía no conceden facultades a roles.',
'Confirmado el 30/10/2026: Luis Prueba Posposición, EMP-POSP-TOTAL-TEST, periodo DIC-2026-FONDOS-TEST del 01/12/2026 al 15/12/2026. BOR-000009 conserva revisión 1 por $483.87 y revisión 2 por $522.58, sueldo $483.87 y transporte $38.71. El intento desde A rechazó Cambió la configuración o inclusión de uno o más empleados. No se guardó nada: vuelve a preparar y revisar el periodo. Apoyos temporales EXTRA-RED-TEST de $10, registros 10 y 11, cancelados. CAJA $3,762.85, BBVA_COBROS $274.29 y CAJA-CORTE-TEST $1,150.00 intactos. Sin revisión 3 ni pagos de este borrador.',1,DATE '2026-10-30','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_LEER','EMPLEADO_NOMINA_LEER','CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER')
WHERE g.slug='nomina-actualizacion-configuracion-confirmada';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Consultar la copia original sin repetir el ensayo','Personal y nómina → Nóminas → Ver BOR-000009 → Historial',
'1. Busca el folio BOR-000009, cuyo nombre real es Preparación · DIC-2026-FONDOS-TEST. 2. Abre Ver y después Historial → Ver revisión 1 para consultar la copia original. 3. En Detalle por empleado comprueba Luis, sueldo $483.87 y otras percepciones $0.00. 4. Usa Ver desglose para consultar su copia original y Volver al borrador para regresar a esa revisión.',
'Sueldo mensual $1,000 multiplicado por 15/31 días de diciembre: $483.87.',
'La revisión 1 permanece conservada sin transporte.',
'No crear de nuevo este borrador: ya reserva esas fechas para Luis.','/admin/preparacion-nomina'),
(2,'Identificar la configuración que originó la revisión 2','Personal y nómina → Empleados → Luis → Ver expediente → Configuración de nómina',
'1. Busca EMP-POSP-TOTAL-TEST y abre Ver expediente → Configuración de nómina. 2. En Conceptos configurados localiza TRANS-RED-TEST por $80 mensuales, del 01/12/2026 al 31/12/2026. 3. Pulsa Ver y revisa Resumen, Gestión e Historial; la ubicación superior identifica empleado y sección actual. 4. Usa ← Volver a configuración para regresar conservando los filtros de conceptos, tamaño y página.',
'Transporte proporcional de la primera quincena: $80 por 15/31 días = $38.71.',
'Configuración identificada sin sobrescribir sueldo ni pagar.',
'El transporte no debe duplicarse ni cancelarse como parte de esta consulta.','/admin/empleados'),
(3,'Consultar la actualización explícita y ambas revisiones','Personal y nómina → Nóminas → Ver BOR-000009 → Historial',
'1. Abre Ver revisión 2 y comprueba sueldo $483.87 más transporte $38.71, total $522.58. 2. Revisa Historial de gestiones y el motivo Actualizar el borrador con el transporte de Luis, conservando la revisión original. 3. Exporta cada revisión desde Detalle por empleado → Exportar esta revisión a Excel para comparar sus propias partidas. 4. Vuelve con Volver al historial del borrador; actualizar requiere Gestión del borrador → Revisar actualización y una confirmación explícita, nunca ocurre por abrir la pantalla.',
'Revisión 1 $483.87; revisión 2 $522.58, registrada el 29/10/2026 a las 16:29.',
'La nueva copia no elimina ni reescribe los importes anteriores.',
'No confirmar una revisión adicional sólo para consultar; las dos revisiones ya están guardadas.','/admin/preparacion-nomina'),
(4,'Preparar A antes de cambiar la configuración','Pestaña A → Nóminas → Ver borrador → Gestión del borrador → Revisar actualización',
'1. Para reproducir el escenario en registros nuevos autorizados, abre la propuesta de actualización en la pestaña A. 2. Comprueba revisión guardada y nueva propuesta, ambas $522.58 en este ejemplo. 3. Captura el motivo Comprobar bloqueo de propuesta desactualizada de Luis sin generar otra revisión. 4. Deja A abierta sin confirmar, recargar ni volver a preparar: debe conservar la configuración anterior al cambio de B.',
'A conserva una propuesta preparada antes del apoyo temporal.',
'Vista susceptible de quedar desactualizada identificada.',
'No reiniciar la aplicación ni cerrar la sesión durante el ensayo; no repetir este caso concluido en la base operativa.','/admin/preparacion-nomina'),
(5,'Agregar el apoyo temporal únicamente desde B','Pestaña B → Empleados → Luis → Ver expediente → Configuración de nómina → Agregar concepto',
'1. En B selecciona EXTRA-RED-TEST · Apoyo extraordinario de prueba. 2. Captura importe $10, Una sola vez, en un periodo y DIC-2026-FONDOS-TEST, con fechas 01/12/2026 al 15/12/2026. 3. Escribe el motivo Apoyo temporal para probar vista desactualizada y pulsa Revisar y guardar concepto → confirmar modal. 4. Conserva el apoyo activo hasta probar el rechazo en A; todavía no lo canceles ni actualices el borrador desde B.',
'Configuración actual estimada $532.58; la revisión guardada y A continúan por $522.58.',
'Cambio de fuente posterior a la preparación, sin pagos.',
'Cancelar el apoyo antes de intentar A invalida la secuencia de esta prueba.','/admin/empleados'),
(6,'Comprobar el rechazo sin generar otra revisión','Pestaña A sin recargar → Confirmar nueva revisión',
'1. Regresa a A sin recargar ni pulsar otra vez Revisar actualización. 2. Pulsa Confirmar nueva revisión y confirma su modal mientras el apoyo de B sigue activo. 3. Comprueba el mensaje Cambió la configuración o inclusión de uno o más empleados. No se guardó nada: vuelve a preparar y revisar el periodo. 4. En B consulta Nóminas → Ver borrador → Historial y verifica que sólo existan revisiones 1 y 2; ante una revisión inesperada detén el ensayo y repórtalo.',
'El usuario confirmó este rechazo antes de cancelar el apoyo temporal 11.',
'Guardado obsoleto bloqueado sin revisión 3 ni movimientos financieros.',
'El aviso actual es general; no identifica por nombre al empleado cambiado. No confundir este ensayo con duplicación de pagos.','/admin/preparacion-nomina'),
(7,'Cancelar sólo el apoyo temporal después del rechazo','Pestaña B → Empleados → Luis → Configuración de nómina → Conceptos configurados → Ver',
'1. Localiza el EXTRA-RED-TEST de $10 que está Próxima, no el intento anterior Cancelada. 2. Abre Ver → Gestión → Cancelar programación futura. 3. Captura Prueba de bloqueo completada; cancelar únicamente este apoyo temporal y pulsa Revisar cancelación → confirmar modal. 4. Comprueba que el nuevo apoyo quede Cancelado y conserva el historial de ambos intentos, sin tocar transporte, puesto ni borrador.',
'En el caso concluido los registros 10 y 11 están Cancelada; el transporte 9 sigue activo.',
'Configuración temporal retirada con auditoría.',
'Dos filas históricas no equivalen a dos conceptos vigentes. No cancelar el transporte mensual de $80.','/admin/empleados'),
(8,'Cerrar conservando revisión 2 y dinero intacto','Nóminas → Ver BOR-000009 → Gestión del borrador; Finanzas → Cuentas financieras',
'1. Abre Revisar actualización y confirma última revisión guardada y nueva propuesta por $522.58 después de retirar el apoyo temporal. 2. Pulsa Volver al borrador sin actualizar, sin confirmar otra revisión, autorizar, pagar ni cancelar la nómina. 3. Comprueba únicamente revisiones 1 y 2 y estado Borrador. 4. En Cuentas financieras verifica CAJA $3,762.85, BBVA_COBROS $274.29 y CAJA-CORTE-TEST $1,150.00, sin egresos de BOR-000009 ni ajustes manuales.',
'Cierre humano confirmado: rechazo obsoleto, copias originales conservadas, apoyos cancelados y cuentas intactas.',
'Prueba terminada sin pago ni revisión adicional.',
'Usa los movimientos posteriores reales para interpretar saldos futuros. Este caso no prueba impuestos ni transferencias bancarias.','/admin/cuentas-financieras')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='nomina-actualizacion-configuracion-confirmada';
