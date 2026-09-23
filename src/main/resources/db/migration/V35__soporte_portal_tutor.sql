INSERT INTO permiso (codigo, descripcion) VALUES
    ('PORTAL_TUTOR_SOPORTE', 'Consultar el portal familiar de un tutor en modo soporte de solo lectura')
ON CONFLICT DO NOTHING;
