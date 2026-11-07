-- Aceptación humana del recorrido docente; contenido editorial, sin permisos ni pagos.
UPDATE guia_proceso
SET ejemplo=ejemplo || ' El 06/11/2026 también se confirmó la consulta desde el portal docente con Bruce Wayne, DOC-PRUEBA-02: recibos propios, PDF pagado y cancelado y regreso al portal docente.',
    version_contenido=version_contenido+1, revisada_el=DATE '2026-11-06'
WHERE slug='acceso-personal-recibos-confirmado';

UPDATE guia_proceso_paso s
SET precaucion='El comprobante es interno, no CFDI. La navegación docente compartida fue confirmada por el usuario con Bruce; no implica aceptación de otros escenarios no probados.'
FROM guia_proceso g WHERE s.guia_id=g.id AND g.slug='acceso-personal-recibos-confirmado' AND s.numero=3;

INSERT INTO guia_proceso_paso(guia_id,numero,titulo,perfil,modulo,instrucciones,ejemplo,resultado,precaucion,ruta)
SELECT g.id,6,'Consultar recibos desde el portal docente','Maestro y administración','Portal de maestros → Mis recibos',
'1. Con el acceso a recibos ya habilitado para el empleado vinculado, inicia sesión en el portal de maestros. 2. Pulsa Mis recibos y verifica que correspondan al docente. 3. Abre un PDF pagado y otro cancelado; revisa su estado y datos históricos. 4. Pulsa Volver a maestros y comprueba el regreso sin iniciar sesión de nuevo.',
'Bruce Wayne, DOC-PRUEBA-02: RN-P-000008 pagado del 16/12/2026 al 31/12/2026, puesto Docente de primaria, sueldo y neto $6,193.55 y deducciones $0.00; RN-P-000005 marcado cancelado. También se revisó RN-P-000006 de noviembre, percepción y neto $80.00, cuya fotografía no registra puesto.',
'El usuario confirmó el listado, los PDF y el regreso al portal de maestros el 06/11/2026.',
'Los folios son del ejemplo de esta base y pueden variar. Un recibo cancelado conserva historia, no representa un pago vigente. No generar otra nómina para consultar; el PDF es un comprobante interno, no CFDI.',
'/admin/empleados'
FROM guia_proceso g WHERE g.slug='acceso-personal-recibos-confirmado';
