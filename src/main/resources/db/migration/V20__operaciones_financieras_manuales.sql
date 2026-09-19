INSERT INTO permiso (codigo, descripcion)
VALUES
    ('MOTIVO_FINANCIERO_LEER', 'Permite consultar el catálogo institucional de motivos financieros'),
    ('MOTIVO_FINANCIERO_ADMINISTRAR', 'Permite crear, actualizar y desactivar motivos financieros'),
    ('MOVIMIENTO_FINANCIERO_REGISTRAR', 'Permite registrar ingresos y egresos manuales en cuentas financieras')
ON CONFLICT DO NOTHING;

INSERT INTO motivo_financiero (institucion_id, codigo, nombre, naturaleza, categoria)
SELECT i.id, semilla.codigo, semilla.nombre, semilla.naturaleza, semilla.categoria
FROM institucion i
CROSS JOIN (VALUES
    ('DONATIVOS', 'Donativos', 'INGRESO', 'OTROS_INGRESOS'),
    ('OTROS_INGRESOS', 'Otros ingresos', 'INGRESO', 'OTROS_INGRESOS'),
    ('NOMINA', 'Nómina', 'EGRESO', 'PERSONAL'),
    ('SERVICIOS', 'Servicios', 'EGRESO', 'SERVICIOS'),
    ('MANTENIMIENTO', 'Mantenimiento', 'EGRESO', 'MANTENIMIENTO'),
    ('COMISIONES_BANCARIAS', 'Comisiones bancarias', 'EGRESO', 'FINANCIERO'),
    ('OTROS_EGRESOS', 'Otros egresos', 'EGRESO', 'OTROS_EGRESOS')
) AS semilla(codigo, nombre, naturaleza, categoria)
ON CONFLICT DO NOTHING;

ALTER TABLE movimiento_financiero
    ADD CONSTRAINT chk_movimiento_operacion_manual
    CHECK (clase <> 'OPERACION' OR (pago_id IS NULL AND reversa_de_id IS NULL));
