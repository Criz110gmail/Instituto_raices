-- Simplificación solicitada: Consultar y Administrar. No concede acceso a lectores.
-- Los roles con Administrar lo conservan. Un permiso especializado antiguo aislado
-- no se convierte silenciosamente en administración completa: requerirá asignación explícita.
DELETE FROM rol_permiso rp USING permiso p
 WHERE rp.permiso_id=p.id AND p.codigo IN ('PRESTAMO_ENTREGAR','PRESTAMO_REESTRUCTURAR');
DELETE FROM guia_proceso_permiso gp USING permiso p
 WHERE gp.permiso_id=p.id AND p.codigo IN ('PRESTAMO_ENTREGAR','PRESTAMO_REESTRUCTURAR');
DELETE FROM permiso WHERE codigo IN ('PRESTAMO_ENTREGAR','PRESTAMO_REESTRUCTURAR');
-- Esta guía es de administración completa; ADMIN ya permite consultar préstamos.
DELETE FROM guia_proceso_permiso gp USING guia_proceso g,permiso p
 WHERE gp.guia_id=g.id AND gp.permiso_id=p.id AND g.slug='prestamos-programados-nomina'
 AND p.codigo='PRESTAMO_LEER';
UPDATE permiso SET descripcion='Préstamos - Administrar: consultar, crear, entregar y reprogramar'
 WHERE codigo='PRESTAMO_ADMINISTRAR';
UPDATE guia_proceso SET requisitos=replace(requisitos,
 'Permisos separados Préstamos - Consultar, Administrar, Entregar y Reestructurar;',
 'Dos permisos: Préstamos - Consultar o Préstamos - Administrar. Administrar incluye consulta, captura, entrega y reprogramación;'),
 version_contenido=version_contenido+1,revisada_el=DATE '2026-11-04'
 WHERE slug='prestamos-programados-nomina';
