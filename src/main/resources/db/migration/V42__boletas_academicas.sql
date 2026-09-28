INSERT INTO permiso (codigo, descripcion) VALUES
    ('BOLETA_CONSULTAR', 'Consultar y exportar boletas académicas')
ON CONFLICT DO NOTHING;

CREATE INDEX ix_inscripcion_boleta_ciclo
    ON inscripcion (ciclo_escolar_id, plantel_id, grado_id, alumno_id)
    WHERE estado <> 'CANCELADA';

