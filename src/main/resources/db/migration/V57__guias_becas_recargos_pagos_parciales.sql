-- Amplía sólo el contenido editorial; no crea cuotas, cargos ni pagos de ejemplo.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado) VALUES
('beca-recargo-liquidacion','Cuota con beca y recargo: liquidar $880','Cobranza',
'Caso confirmado: cuota $1,000, beca del 20% por $200, recargo del 10% sobre $800 por $80 y pago total $880. Incluye vista previa y comprobaciones.',
'Un alumno activo, inscripción vigente que ya comenzó, tutor financiero autorizado y una cuenta de caja activa en MXN. Usuario administrativo persistido con permisos de Conceptos, Cuotas, Tipos de beca, Becas, Políticas de recargo, Adeudos, Historial de ajustes, Pagos y Cuentas financieras. Reutiliza los expedientes existentes. Elige un cargo de prueba sin pagos ni ajustes anteriores. Para generar el recargo, su fecha límite debe haber pasado y el corte debe alcanzar la fecha de emisión y el inicio de mora. No cambies el reloj ni registros operativos para probar.',
'Ana Prueba: cuota $1,000.00; beca 20% = $200.00; exigible $800.00. Política de recargo único 10%, cero días de gracia y sin límite: $800.00 × 10% = $80.00. Exigible final $880.00; pago efectivo $880.00. Saldo final del cargo $0.00 y cuenta de caja B + $880.00 si no hubo otros movimientos. D es una fecha límite ya pasada dentro de inscripción/ciclo; corte C es la fecha institucional actual, posterior a D. Si esas condiciones no existen, espera a que pase la fecha límite; no simules dinero ni fuerces fechas inválidas.',
1,DATE '2026-10-06','CONFIRMADA'),
('pagos-parciales-liquidacion','Liquidar un cargo con abonos de $300, $500 y $200','Cobranza',
'Caso confirmado: un cargo de $1,000 se cubre con dos abonos administrativos y una transferencia familiar del saldo restante. Cada pago se valida por separado.',
'Un alumno activo con inscripción y vínculo financiero vigentes, tutor con cuenta familiar activa, un cargo de prueba por $1,000 MXN sin beca, recargos, descuentos ni pagos previos. Fecha límite vigente para que el último saldo aparezca al reportar en el portal. Cuenta de caja para efectivo y cuenta bancaria compatible para la transferencia. Usuario administrativo persistido autorizado para registrar y validar. No usar el flujo rápido del cargo para el abono: inicia desde Pagos recibidos → Nuevo registro, donde está Registrar pago parcial.',
'Cargo $1,000.00. Primer pago efectivo $300.00 validado: pagado acumulado $300.00, saldo $700.00. Segundo pago efectivo $500.00 validado: acumulado $800.00, saldo $200.00. Transferencia familiar final $200.00 validada: acumulado $1,000.00, saldo $0.00. Son tres pagos y tres ingresos, no un solo pago modificado. Con estos métodos, la caja aumenta $800.00 y el banco $200.00, salvo otros movimientos.',
1,DATE '2026-10-06','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo=ANY(ARRAY[
'CONCEPTO_COBRO_LEER','CUOTA_ALUMNO_LEER','TIPO_BECA_LEER','BECA_ALUMNO_LEER',
'POLITICA_RECARGO_LEER','CARGO_LEER','AJUSTE_CARGO_LEER','PAGO_LEER','PAGO_REGISTRAR','PAGO_VALIDAR','CUENTA_FINANCIERA_LEER'])
WHERE g.slug='beca-recargo-liquidacion';

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo=ANY(ARRAY[
'CARGO_LEER','PAGO_LEER','PAGO_REGISTRAR','PAGO_VALIDAR','CUENTA_FINANCIERA_LEER'])
WHERE g.slug='pagos-parciales-liquidacion';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.* FROM guia_proceso g CROSS JOIN (VALUES
(1,'Preparar el concepto que admite beca y recargo','Administración','Conceptos de cobro',
'1. Abre Cobranza escolar → Conceptos de cobro → Nuevo registro. 2. Selecciona la institución. 3. Captura código COLEG-BECA-TEST y nombre Colegiatura prueba beca y recargo. 4. Selecciona categoría Colegiatura. 5. Activa Permite beca, Permite recargo y Concepto activo. Deja descuento desmarcado para aislar este ejemplo. 6. Guarda. Si ya existe un concepto dedicado a la prueba, verifica sus opciones y reutilízalo.',
'Concepto COLEG-BECA-TEST; sin precio global. Los $1,000 se configuran por inscripción.',
'Concepto disponible para cuotas y para asignar beca/política, sin deuda ni ingreso todavía.',
'No uses el concepto sin beca del ejemplo de hermanos sin revisar sus opciones. No modifiques un concepto operativo sólo para probar.', '/admin/catalogos/conceptos-cobro'),
(2,'Configurar la cuota individual sin emitirla todavía','Administración','Cuotas por alumno',
'1. En Cuotas por alumno pulsa Nuevo registro. 2. Selecciona la inscripción del alumno y COLEG-BECA-TEST. 3. Captura importe 1000.00 MXN. 4. Para aislar una emisión elige Una sola vez, estado Activa y generación automática. 5. Elige fecha límite D y revisa la vigencia dentro de inscripción/ciclo. 6. Guarda sin emitir inmediatamente: primero se asignará la beca. Si reutilizas una cuota mensual, limita el periodo a una sola mensualidad de prueba y verifica que no genere meses adicionales.',
'Una cuota de $1,000.00. D debe ser anterior a hoy para probar mora ahora y estar dentro de la vigencia; si no puede elegirse una fecha pasada válida, configura una fecha válida y realiza el recargo cuando haya vencido.',
'Cuota configurada. No hay ingreso y aún no debe emitirse el cargo que recibirá la beca.',
'Becas aplican al emitir, no retroactivamente. No canceles ni dupliques un cargo pagado para reproducir este caso. El ejemplo valida el cálculo, no todas las frecuencias.', '/admin/catalogos/cuotas-alumno'),
(3,'Asignar la beca del 20% antes de emitir','Administración','Tipos de beca y Becas por alumno',
'1. En Tipos de beca crea o verifica un tipo activo, por ejemplo Académica de prueba. 2. En Becas por alumno pulsa Nuevo registro. 3. Selecciona institución, plantel, la misma inscripción, ese tipo y COLEG-BECA-TEST. 4. En Modalidad elige Porcentaje y captura 20. 5. En Inicio y Fin usa fechas dentro de la inscripción que cubran el periodo de la cuota. 6. Selecciona Activa y escribe el motivo de autorización. 7. Pulsa Asignar beca.',
'20% de $1,000.00 = $200.00. Motivo ilustrativo: Beneficio académico autorizado para prueba controlada.',
'Beca activa para esa inscripción, concepto y periodo. El cargo futuro podrá recibir $200.00 de disminución.',
'No asignar otra beca activa superpuesta del mismo concepto. Cambiar la beca después de emitir no reescribe un ajuste histórico. Fechas fuera de inscripción/ciclo deben corregirse, no forzarse.', '/admin/catalogos/becas-alumno'),
(4,'Verificar la beca en la vista previa y emitir','Administración','Adeudos de alumnos',
'1. En Adeudos de alumnos pulsa Generar automáticos. 2. Selecciona institución, plantel y corte que alcance el inicio de la cuota. 3. Pulsa Visualizar cuotas por aplicar. 4. Revisa original, beca y total, además de todos los registros del alcance. 5. Si son correctos, pulsa Confirmar y generar adeudos faltantes. 6. En el cargo generado comprueba el Historial de ajustes y el saldo exigible.',
'Vista previa: original $1,000.00, beca $200.00, total por cobrar $800.00. Tras emitir: ajuste BECA disminución $200.00 y saldo $800.00.',
'Deuda $800.00; cuenta financiera sin cambio. El beneficio queda registrado en el cargo.',
'Si beca muestra cero, detente y revisa inscripción, concepto, estado y vigencia antes de confirmar. El generador puede incluir otros alumnos; no confirma sólo la fila visible.', '/admin/cargos/generar'),
(5,'Crear la política única del 10%','Administración','Políticas de recargo',
'1. En Políticas de recargo pulsa Nuevo registro o edita la política de prueba existente. 2. Selecciona institución y COLEG-BECA-TEST. 3. Modalidad Porcentaje; Porcentaje 10; Días de gracia 0; Periodicidad Una sola vez. 4. Tipo de límite Sin límite: no captures Valor del límite. 5. Activa Generación automática y Política activa. 6. Pulsa Crear política o Guarda cambios.',
'Base sin recargos: $1,000.00 − $200.00 = $800.00. 10% = $80.00. Una sola vez evita un recargo mensual nuevo para este caso.',
'Política lista para el proceso explícito. Guardarla por sí sola no cambia el saldo a $880.00.',
'Sin límite significa sin tope acumulado, no un cargo inmediato ni infinitos recargos en periodicidad única. Monto máximo limita en dinero y % máximo del original limita según el importe original; son variantes fuera de este ejemplo. No cambiar una política operativa para probar.', '/admin/catalogos/politicas-recargo'),
(6,'Visualizar y confirmar el recargo','Administración','Generar recargos',
'1. En Políticas de recargo pulsa Generar recargos. 2. Selecciona institución, plantel y Fecha de corte C. C debe alcanzar la emisión del cargo y ser posterior a D para cero días de gracia. 3. Pulsa Visualizar recargos. 4. Verifica cargo, saldo actual $800.00, recargo $80.00 y nuevo saldo $880.00 en todas las páginas. 5. Sólo con los importes y alcance correctos pulsa Confirmar y generar recargos. 6. Revisa el cargo y su Historial de ajustes.',
'Saldo $800.00 + recargo $80.00 = $880.00. Se conserva el ajuste de beca por $200.00 y se agrega RECARGO aumento $80.00.',
'Cargo exigible $880.00. La vista previa no escribió nada; el ajuste aparece después de confirmar. La cuenta sigue sin ingresos.',
'No es una tarea automática diaria: se aplica al ejecutar el generador. Repetir el mismo recargo único no duplica el periodo. Si la fecha no llegó, el cargo está pagado o la política está inactiva, no aparece como aplicable.', '/admin/politicas-recargo/generar'),
(7,'Registrar y validar el pago completo de $880','Administración','Pagos recibidos',
'1. Anota el Saldo actual B de la cuenta de caja que recibirá el efectivo. 2. En el detalle del cargo pulsa Registrar pago. 3. Verifica tutor, alumno y distribución de $880.00 precargados. 4. Selecciona Efectivo, la cuenta de caja obligatoria y fecha/hora reales; registra referencia u observaciones si corresponde. 5. Comprueba Total recibido $880.00, Distribuido entre cargos $880.00 y Dinero pendiente de asignar $0.00. 6. Pulsa Registrar como pendiente. 7. En Ver → Gestión conserva la cuenta declarada correcta y pulsa Validar y publicar.',
'Efectivo $880.00, asignación al único cargo $880.00. No se exige comprobante bancario para efectivo.',
'Un ingreso $880.00, pago Validado y saldo del cargo $0.00. Caja B + $880.00 sin otros movimientos.',
'Este cargo ya está vencido: el selector de Reportar transferencia del portal lo excluye. Usa administración para registrar ese pago; no cambies el vencimiento para hacerlo aparecer. El acceso de recuperación no puede validar.', '/admin/catalogos/pagos'),
(8,'Conciliar importes y abrir el comprobante','Administración y tutor','Adeudos, cuentas y Portal familiar',
'1. En el cargo comprueba original $1,000.00, beca −$200.00, recargo +$80.00, exigible $880.00, pagado $880.00 y saldo $0.00. 2. En Pagos recibidos revisa Distribución y el ingreso vinculado. 3. En Cuentas financieras confirma B + $880.00. 4. El tutor entra a Tus pagos y consulta ese pago validado. 5. Abre Ver comprobante PDF en otra pestaña.',
'$1,000.00 − $200.00 + $80.00 − $880.00 = $0.00. Se conservan beca y recargo en el historial.',
'Caso liquidado y comprobante disponible. El cargo ya no aparece en Lo que falta por pagar.',
'Los cargos de otros periodos pueden mantener saldo. Si no coincide la cuenta, comprueba otros movimientos. Esta guía no da por confirmadas otras tasas, topes o recargos mensuales.', '/admin/catalogos/cargos')
) AS v(numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
WHERE g.slug='beca-recargo-liquidacion';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.* FROM guia_proceso g CROSS JOIN (VALUES
(1,'Verificar el cargo y las cuentas del ejemplo','Administración','Adeudos de alumnos y Cuentas financieras',
'1. En Adeudos de alumnos busca el alumno y abre su cargo de prueba. 2. Verifica original y exigible $1,000.00, pagado $0.00, saldo $1,000.00 y fecha límite no vencida. 3. Confirma tutor responsable financiero y acceso familiar. 4. Anota saldo actual Bc de la caja y Bb del banco. 5. Si falta crear el cargo, configura una cuota única sin becas/recargos y emite desde la vista previa como en la guía de dos hijos, adaptada a un solo alumno.',
'Ana Prueba; un cargo de $1,000.00 vigente. Sin ajustes ni otros abonos. Caja Bc; banco Bb.',
'Punto de partida reproducible para tres pagos distintos.',
'Si reutilizas el cargo liquidado de otra prueba no tendrá saldo: usa un ejemplo autorizado distinto. Si vence antes del último abono, el portal no lo ofrece y se debe registrar el saldo desde administración.', '/admin/catalogos/cargos'),
(2,'Capturar el primer abono de $300 en efectivo','Administración','Pagos recibidos → Nuevo registro',
'1. Abre Pagos recibidos y pulsa Nuevo registro. No uses Registrar pago desde el cargo para este abono: la fila precargada de ese flujo rápido está protegida. 2. Selecciona institución, plantel, tutor, método Efectivo y cuenta de caja. 3. Captura fecha/hora reales y los datos propios de la entrega. 4. En Distribución solicitada selecciona el cargo de $1,000.00. 5. Pulsa Registrar pago parcial y captura 300.00 en Importe que se aplicará. 6. Revisa los totales y el saldo estimado. 7. Pulsa Registrar como pendiente.',
'Total recibido $300.00; Distribuido entre cargos $300.00; Dinero pendiente de asignar $0.00; saldo estimado del cargo después de validar $700.00.',
'Primer pago en revisión por $300.00. El saldo actual del cargo sigue $1,000.00 hasta validar.',
'Dinero pendiente de asignar $0.00 no significa que el alumno deba cero: el saldo pendiente estimado es $700.00. El total se sincroniza con la distribución; comprueba que sea lo efectivamente recibido.', '/admin/catalogos/pagos'),
(3,'Validar el primer abono y comprobar $700','Administración','Pagos recibidos → Ver → Gestión',
'1. Abre el pago de $300.00. 2. En Distribución confirma el cargo y monto solicitado. 3. En Gestión conserva la cuenta de caja declarada si es correcta. 4. Pulsa Validar y publicar. 5. En el cargo comprueba Pagado acumulado y Saldo exigible. 6. En la cuenta verifica el ingreso de $300.00.',
'Pagado acumulado $300.00; saldo $700.00. Caja Bc + $300.00 sin otros movimientos.',
'Cargo parcial con abono efectivo de $300.00 y deuda de $700.00.',
'El importe del primer pago permanece $300.00; no se convierte en el total del cargo. Su detalle puede mostrar saldo actual distinto cuando lleguen otros abonos.', '/admin/catalogos/pagos'),
(4,'Registrar otro pago de $500, sin editar el anterior','Administración','Pagos recibidos → Nuevo registro',
'1. Crea otro Nuevo registro en Pagos recibidos. 2. Selecciona el mismo tutor, método Efectivo y misma caja; usa fecha/hora de esta segunda entrega. 3. Selecciona el mismo cargo, cuyo saldo es ahora $700.00. 4. Pulsa Registrar pago parcial y captura 500.00. 5. Comprueba Total recibido $500.00, distribución $500.00, dinero sin asignar $0.00 y saldo estimado $200.00. 6. Guarda con Registrar como pendiente. 7. Revisa y valida este segundo pago desde Ver → Gestión.',
'Abonos validados $300.00 + $500.00 = $800.00. Saldo $1,000.00 − $800.00 = $200.00. Caja Bc + $800.00.',
'Dos pagos con folios diferentes y dos ingresos. El cargo conserva $200.00 pendientes.',
'No edites el primer pago para cambiarlo a $800.00: son entregas distintas. Un segundo pago en revisión no se suma a Pagado acumulado hasta validarlo.', '/admin/catalogos/pagos'),
(5,'Reportar los últimos $200 desde la familia','Tutor','Portal familiar → Tus pagos',
'1. El tutor inicia sesión desde /familias; en una prueba administrativa usa sesión privada separada. 2. Selecciona el alumno y revisa Lo que falta por pagar: total $1,000.00, pagado $800.00 y falta $200.00. 3. Pulsa Reportar transferencia y selecciona ese cargo. 4. El saldo y total se calculan en $200.00: el portal usa el saldo completo restante, no un importe editable. 5. Selecciona cuenta bancaria del plantel y captura fecha/hora y referencia reales. 6. Adjunta el comprobante, revisa el resumen y pulsa Enviar transferencia a revisión.',
'Total a transferir $200.00. Referencia ilustrativa TEST-SALDO-200 sólo para una prueba autorizada.',
'Tercer pago En revisión por $200.00. El cargo conserva saldo $200.00 y el banco no recibe un movimiento del sistema hasta validar.',
'El portal no permite elegir un abono menor libremente: para pagos parciales de otro importe usa la captura administrativa. Si no aparece el cargo, verifica vencimiento, vínculo financiero, estado y saldo.',NULL),
(6,'Validar la transferencia final y revisar tres ingresos','Administración','Pagos recibidos',
'1. Abre la transferencia de $200.00 en revisión. 2. Verifica comprobante, cuenta bancaria y recepción real. 3. En Distribución comprueba el cargo y $200.00. 4. En Gestión conserva la cuenta bancaria correcta y pulsa Validar y publicar. 5. Verifica cargo pagado y saldo cero. 6. Comprueba las cuentas: dos ingresos en caja y uno en banco.',
'Pagado acumulado $1,000.00; saldo $0.00. Caja Bc + $800.00; banco Bb + $200.00. Total de ingresos de estos tres pagos $1,000.00.',
'Tres pagos validados independientes; el último abono liquida exactamente lo que faltaba.',
'La caja no aumenta por la transferencia al banco. Si usas otros métodos/cuentas, adapta la comprobación por cuenta, sin inventar un único ingreso de $1,000.00.', '/admin/catalogos/pagos'),
(7,'Consultar el historial completo y sus comprobantes','Administración y tutor','Adeudos y Portal familiar',
'1. Administración consulta el cargo: importe exigible $1,000.00, Pagado acumulado $1,000.00 y Saldo exigible $0.00. 2. El tutor entra a Tus pagos: ese cargo ya no está pendiente. 3. En Tus pagos y comprobantes consulta $300.00, $500.00 y $200.00, cada uno Validado y con su comprobante oficial. 4. Si falta un registro, limpia filtros de mes/año y revisa las páginas. 5. Abre cada PDF en una pestaña nueva para comprobar su folio e importe.',
'Tres comprobantes, cada uno correspondiente a su pago; $300.00 + $500.00 + $200.00 = $1,000.00.',
'Caso confirmado de liquidación progresiva; se conserva todo el historial sin borrar los abonos previos.',
'El saldo actual mostrado en el detalle de un pago anterior puede ser $0.00 después de liquidar: no modifica el importe histórico de $300.00 o $500.00. Esta guía no incluye devoluciones, reversas ni ajustes manuales.', '/admin/catalogos/cargos')
) AS v(numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
WHERE g.slug='pagos-parciales-liquidacion';
