-- Sólo documentación aprobada. No reproduce pagos ni modifica cuentas o permisos.
INSERT INTO guia_proceso(slug,titulo,categoria,resumen,requisitos,ejemplo,version_contenido,revisada_el,estado)
VALUES('nomina-todos-elegibles-cero-confirmada','Nómina: preparar a todos y conservar al empleado con neto cero','Personal',
'Prepara automáticamente a quienes corresponden al periodo. Paga los importes positivos y conserva el resultado de quien queda en cero, sin inventar una salida de dinero ni dejarlo pendiente.',
'Administrador con permisos privados de nómina y registro financiero. Esta guía no concede permisos. El ejemplo ya terminó: consultar BOR-000014, no repetir sus pagos ni cancelar registros para volver a probar. Los saldos son históricos; no ajustar las cuentas para hacerlos coincidir.',
'Confirmado por el usuario el 03/11/2026. DIC-2026-FONDOS-TEST, 01/12/2026–15/12/2026. BOR-000014: Marta $483.87 (NPI-14), Pablo $483.87 (NPI-15), Sofía $300.00 (NPI-16), Diego neto cero confirmado sin NPI. Cuatro empleados, tres pagos, total $1,267.74 desde CAJA. Saldo CAJA $2,462.85 → $1,195.11; banco $274.29 y caja de corte $1,150.00 intactos. Nómina Aplicada · sin pendientes. No demuestra una prueba manual de veinte empleados ni de dos confirmaciones simultáneas.',1,DATE '2026-11-03','CONFIRMADA');

INSERT INTO guia_proceso_permiso(guia_id,permiso_id)
SELECT g.id,p.id FROM guia_proceso g JOIN permiso p ON p.codigo IN
('EMPLEADO_LEER','EMPLEADO_LABORAL_LEER','CONDICION_SALARIAL_LEER','EMPLEADO_NOMINA_LEER','CUENTA_FINANCIERA_LEER','MOVIMIENTO_FINANCIERO_LEER')
WHERE g.slug='nomina-todos-elegibles-cero-confirmada';

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,v.n,v.t,'Administración',v.m,v.i,v.e,v.r,v.p,v.u FROM guia_proceso g CROSS JOIN (VALUES
(1,'Revisar quién corresponde al periodo','Personal y nómina → Empleados; Periodos de nómina',
'Consulta DIC-2026-FONDOS-TEST del 01/12/2026 al 15/12/2026. Marta y Pablo tienen sueldo mensual de $1,000 vigente desde noviembre. Por estos quince días de diciembre corresponden $483.87 a cada uno. Bruce y Luis ya tienen estas fechas reservadas en otras nóminas: no se vuelven a incluir.',
'Todos los elegibles no significa pagar otra vez a todos los registros del catálogo.',
'Cobertura y sueldos identificados sin duplicar pagos.',
'No modificar el periodo ni los sueldos para repetir una prueba ya concluida.','/admin/periodos-nomina'),
(2,'Configurar los conceptos antes de preparar','Empleados → Ver expediente → Configuración de nómina → Agregar concepto',
'Para un ensayo nuevo autorizado, configura en Sofía un apoyo único de $300 y en Diego una percepción de $200 y una deducción de $200, ligados al periodo completo. Revisa y confirma cada concepto. Conserva los conceptos anteriores en su periodo original. En este caso las capturas ya existen y sólo se consultan.',
'Diego: $200 menos $200 da $0. Sofía: apoyo de $300 sin sueldo base. El icono ! de Sin sueldo base asignado abre la explicación sin cambiar la captura.',
'Configuración completa antes de generar la vista previa.',
'No recapturar conceptos de noviembre ni agregarlos dos veces en diciembre.','/admin/empleados'),
(3,'Usar la selección automática','Personal y nómina → Nóminas → Nuevo borrador',
'Selecciona el periodo y abre Opciones de selección. Deja Todos los elegibles del periodo · recomendado y pulsa Preparar nómina del periodo. A diferencia de la selección específica, los empleados elegibles ya quedan incluidos. Los que requieren revisión o tienen una nómina coincidente se identifican aparte.',
'Cuatro incluidos: Marta, Pablo, Sofía y Diego. No es necesario pulsar Incluir en sus filas.',
'Selección automática de todos los elegibles, sin pagos todavía.',
'Para este ejemplo consulta BOR-000014. No crear otro borrador con las mismas personas y fechas.','/admin/preparacion-nomina'),
(4,'Comprobar el resumen de los cuatro','Vista previa → Resumen y Detalle por empleado',
'Revisa sueldo total $967.74, otras percepciones $500, deducciones $200 y neto $1,267.74. Diego debe seguir incluido con neto $0. Los filtros sólo cambian la tabla visible; no quitan a los demás de la selección. Ver desglose permite comprobar cada concepto.',
'Marta $483.87 + Pablo $483.87 + Sofía $300 + Diego $0 = $1,267.74.',
'Los cuatro empleados permanecen en la revisión.',
'Si aparecen otros incluidos o importes distintos, detenerse antes de guardar y revisar las vigencias; no forzar el total.','/admin/preparacion-nomina'),
(5,'Guardar sin mover dinero','Vista previa → Nombre y observaciones · ya propuestos',
'Usa el nombre Diciembre — todos los elegibles con neto cero y explica el motivo. Pulsa Guardar borrador revisado, revisa los cuatro empleados y el total. Cancelar en el modal vuelve sin guardar; confirmar conserva el borrador y su detalle. Guardar todavía no paga.',
'BOR-000014, revisión 1, cuatro empleados y $1,267.74.',
'Borrador revisable conservado, sin egresos por el guardado.',
'No confundir el borrador con una nómina aplicada.','/admin/preparacion-nomina'),
(6,'Revisar la fuente del colectivo','Ver borrador → Autorización y pagos por empleado → Resumen → Preparar pago colectivo',
'Captura una fuente CAJA por $1,267.74, motivo financiero NOMINA, referencia DIC-TODOS-CERO-01 y motivo del pago. Pulsa Revisar distribución. La revisión muestra tres empleados para pagar y un neto cero por confirmar. Las cuentas sólo financian los importes positivos.',
'CAJA antes $2,462.85; salida $1,267.74; saldo posterior esperado $1,195.11.',
'Distribución revisada sin pedir dinero para Diego.',
'No capturar una cuenta de $0 ni pagar a Diego por separado.','/admin/preparacion-nomina'),
(7,'Cancelar el modal y después confirmar','Revisión del pago colectivo → Confirmar y registrar pagos',
'Revisa cuenta, total y aviso del neto cero en el modal. Cancelar no registra los pagos ni confirma el cero. La confirmación final registra los pagos positivos y el cierre cero en una sola operación. Si falla la operación, no debe quedar una parte aplicada.',
'Tres pagos reales por $483.87, $483.87 y $300. Diego conserva un cierre cero sin NPI ni egreso.',
'Nómina Aplicada · sin pendientes con cuatro resultados conservados.',
'Este ejemplo ya está aplicado. No volver a confirmar ni cancelar para reproducirlo.','/admin/preparacion-nomina'),
(8,'Comprobar resultados, cuentas y Excel','Nóminas → Ver → Autorización y pagos por empleado; Finanzas',
'En Pagos por empleado, estado Todos, verifica tres Pagado y Diego Neto cero confirmado. Su cierre se consulta en Ver pago e intentos → Resumen, con fecha, responsable y motivo. Comprueba tres egresos y los saldos finales. Desde el detalle de la nómina usa el único botón superior Exportar revisión a Excel: conserva los cuatro empleados y el neto cero.',
'CAJA $1,195.11; BBVA_COBROS $274.29; CAJA-CORTE-TEST $1,150.00. Salida total $1,267.74. Las configuraciones de los empleados no cambian.',
'Prueba colectiva confirmada por el usuario y documentada, sin pendiente ficticio de Diego.',
'Si hubo movimientos posteriores, reconstruir el saldo con el historial; no modificar cuentas para coincidir con el ejemplo.','/admin/preparacion-nomina')
) AS v(n,t,m,i,e,r,p,u) WHERE g.slug='nomina-todos-elegibles-cero-confirmada';
