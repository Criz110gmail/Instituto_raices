INSERT INTO permiso (codigo, descripcion) VALUES
    ('PORTAL_MAESTRO_SOPORTE', 'Consultar el portal docente en soporte de solo lectura con auditoría')
ON CONFLICT DO NOTHING;
