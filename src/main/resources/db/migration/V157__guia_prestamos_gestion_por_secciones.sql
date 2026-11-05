-- Sólo redacción y ruta visible de la guía; no afecta préstamos ni dinero.
UPDATE guia_proceso_paso s SET modulo='Préstamos → Ver → Gestión → Entregar dinero al empleado',
 instrucciones='1. Abre Gestión y elige la sección Entregar dinero al empleado. Borrador del préstamo y Cancelar borrador son secciones independientes. 2. Selecciona caja o banco compatible, motivo financiero de egreso y referencia. 3. Escribe el motivo y confirma que el empleado recibió el dinero prestado. 4. Pulsa Revisar entrega. 5. Verifica cuenta, dinero que recibirá el empleado y saldo posterior protegidos. 6. Usa Editar captura para corregir o Confirmar y registrar entrega para abrir el modal.'
 FROM guia_proceso g WHERE s.guia_id=g.id AND g.slug='prestamos-programados-nomina' AND s.numero=3;
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-11-05'
 WHERE slug='prestamos-programados-nomina';
