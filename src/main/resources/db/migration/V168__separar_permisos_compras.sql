-- Los roles anteriores tenían acceso completo por expansión de cualquiera de estos
-- permisos. Conservar ese acceso al separar los paquetes visibles en Roles.
INSERT INTO rol_permiso(rol_id,permiso_id,activo)
SELECT DISTINCT rp.rol_id, administrador.id, true
FROM rol_permiso rp JOIN permiso anterior ON anterior.id=rp.permiso_id
CROSS JOIN permiso administrador
WHERE rp.activo AND anterior.codigo IN ('COMPRA_LEER','COMPRA_ADMINISTRAR','COMPRA_CONFIRMAR','COMPRA_CANCELAR')
 AND administrador.codigo='COMPRA_ADMINISTRAR'
ON CONFLICT (rol_id,permiso_id) DO UPDATE SET activo=true;
