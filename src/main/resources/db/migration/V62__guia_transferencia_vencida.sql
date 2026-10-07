-- Contenido editorial del caso confirmado por el propietario; no crea movimientos de prueba.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado) VALUES
('transferencia-vencida-500','Pagar un cargo vencido desde el portal familiar: $500','Cobranza',
'Caso confirmado: cuota única vencida, reporte familiar por $500 y validación administrativa que aumenta la cuenta bancaria en $500. La autorización no modifica el vencimiento.',
'Alumno e inscripción vigentes, tutor con vínculo financiero autorizado y cuenta familiar activa; cuenta bancaria compatible del plantel. Fechas del ejemplo dentro de inscripción y ciclo. Usuario administrativo persistido con permisos de Conceptos, Cuotas, Adeudos, Planteles, Pagos y Cuentas financieras. Usar registros de prueba, no duplicar un cargo ya pagado ni alterar el reloj.',
'Cuota única de $500 MXN, sin becas ni recargos; fecha límite 05/10/2026, corte 07/10/2026. Reporte familiar y validación por $500: banco B + $500 y saldo del cargo $0 si no hubo otras operaciones. El propietario confirmó disponibilidad del cargo, envío, validación e incremento de la cuenta. Revocación, duplicados y cambios concurrentes son protecciones adicionales, no escenarios humanos confirmados por esta prueba.',
1,DATE '2026-10-07','CONFIRMADA');
INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo=ANY(ARRAY[
'CONCEPTO_COBRO_LEER','CUOTA_ALUMNO_ADMINISTRAR','CARGO_ADMINISTRAR','PLANTEL_LEER',
'PAGO_LEER','PAGO_VALIDAR','CUENTA_FINANCIERA_LEER']) WHERE g.slug='transferencia-vencida-500';
INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.* FROM guia_proceso g CROSS JOIN (VALUES
(1,'Preparar un concepto sin descuentos ni recargos','Administración','Conceptos de cobro',
'1. Abre Conceptos de cobro → Nuevo registro. 2. Captura código VENC-PORTAL-TEST y nombre Prueba de transferencia vencida. 3. Categoría Otro, activo; desmarca permite beca, descuento y recargo. 4. Guarda o reutiliza el concepto exclusivo de prueba ya existente.',
'Concepto VENC-PORTAL-TEST; el importe se configura por alumno, no en el concepto.',
'Concepto disponible; todavía no existe deuda ni ingreso.',
'Usa expedientes controlados. No cambies un concepto operativo para quitar becas.', '/admin/catalogos/conceptos-cobro'),
(2,'Configurar la cuota única del alumno','Administración','Cuotas por alumno',
'1. Abre Cuotas por alumno → Nuevo registro. 2. Selecciona inscripción y concepto de prueba. 3. Elige Una sola vez, importe 500 y fecha límite 05/10/2026. 4. Habilita generación automática y guarda. 5. Comprueba que las fechas estén dentro de la inscripción y el ciclo.',
'Cuota única $500 MXN con vencimiento pasado válido.',
'Cuota guardada; guardar la cuota no recibe dinero.',
'Registrar con una fecha diferente pertenece al cargo MANUAL, no a este formulario de cuota. No crees además un cargo manual: duplicaría el ejemplo.', '/admin/catalogos/cuotas-alumno'),
(3,'Emitir únicamente el adeudo de prueba','Administración','Adeudos de alumnos',
'1. Abre Generar automáticos. 2. Selecciona institución, plantel y corte 07/10/2026. 3. Pulsa Visualizar cuotas por aplicar. 4. Desmarca todos y marca sólo la cuota de $500 del ejemplo. 5. Revisa original500, beca0 y total500. 6. Confirma la generación y anota el número del cargo.',
'Una fila seleccionada por $500, fecha límite 05/10/2026.',
'Cargo emitido vencido con saldo $500; cuenta bancaria sin cambios.',
'Adapta las fechas a un corte real posterior al vencimiento. No confirmes otros cargos de prueba; repetir no debe duplicar la misma cuota única.', '/admin/cargos/generar'),
(4,'Habilitar el reporte del cargo vencido','Administración','Adeudos de alumnos',
'1. Abre Ver del nuevo cargo. 2. Presiona Transferencia de cargo vencido. 3. Marca Habilitar transferencia aunque este cargo esté vencido. 4. Motivo: La familia no puede acudir a ventanilla y realizará transferencia. 5. Guarda autorización. La alternativa general es Planteles → Editar → Permitir reportar transferencias de cargos vencidos.',
'Excepción individual autorizada para el cargo de $500.',
'El cargo queda elegible para su tutor financiero, conservando fecha y saldo.',
'Si el plantel permite vencidos, quitar una excepción no bloquea su regla general. Cargo cancelado, convenido, liquidado o con pago en revisión no se ofrece. El motivo y actor quedan en Auditoría.', '/admin/catalogos/cargos'),
(5,'Reportar desde la cuenta familiar','Tutor','Tus pagos',
'1. Entra por /familias, en ventana privada si conservas sesión administrativa. 2. Abre Tus pagos → Reportar transferencia. 3. Selecciona solamente el cargo de $500. 4. Comprueba el desglose y total protegido. 5. Elige banco del plantel. 6. Captura fecha/hora y referencia PRUEBA-VENCIDO-500. 7. Adjunta comprobante. 8. En 5 · Revisa y envía, verifica el resumen automático y envía.',
'Original500, descuentos0, recargos0, abonos0 y total500; comprobante de prueba sin datos sensibles.',
'Regresa a Tus pagos con tarjeta Comprobante recibido y estado En revisión; deuda todavía500 y banco sin incremento.',
'La sección lavanda es sólo revisión, no captura. Si cambió el saldo, revisa el total actualizado y adjunta nuevamente. No mandes otro reporte mientras el cargo tenga pago pendiente.', NULL),
(6,'Validar el dinero efectivamente recibido','Administración','Pagos recibidos',
'1. Busca la referencia PRUEBA-VENCIDO-500. 2. Abre Ver → Gestión. 3. Revisa comprobante, monto500 y cuenta bancaria precargada. 4. Anota saldo actual B. 5. Si todo coincide, pulsa Validar y publicar.',
'Pago recibido $500 contra la cuenta bancaria donde aparece la transferencia.',
'Pago validado; un ingreso de $500 y aplicación al cargo. Banco B + $500 si no hubo otros movimientos.',
'El comprobante por sí solo no prueba recepción real. Cuenta de recuperación no valida. Si la deuda cambió, revisar diferencia; no cambiar el monto del dinero recibido.', '/admin/catalogos/pagos'),
(7,'Comprobar cuenta, cargo y portal','Administración y tutor','Pagos y cuentas',
'1. Revisa saldo de la cuenta: B + $500. 2. Abre cargo: abonos500 y saldo0. 3. Regresa a Tus pagos en el portal: ese cargo ya no debe formar parte de lo pendiente. 4. Consulta el pago validado y abre su comprobante PDF en otra pestaña.',
'Importe recibido500, aplicado500, deuda restante0 y cuenta incrementada500.',
'Caso concluido. El vencimiento original se conserva; no se eliminan registros históricos.',
'El propietario confirmó envío, validación e incremento bancario; cada revisión adicional debe comprobarse en su instalación. Los totales familiares pueden incluir otros cargos, por eso identifica el número de este ejemplo.', '/admin/catalogos/cuentas-financieras')
) AS v(numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
WHERE g.slug='transferencia-vencida-500';
