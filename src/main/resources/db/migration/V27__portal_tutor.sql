CREATE INDEX ix_evento_portal_publicado_inicio
    ON evento_escolar (institucion_id, inicio_en, id)
    WHERE estado = 'PUBLICADO';

INSERT INTO permiso (codigo, descripcion) VALUES
    ('PORTAL_TUTOR_ACCEDER', 'Acceder al portal familiar con los vínculos vigentes')
ON CONFLICT DO NOTHING;
