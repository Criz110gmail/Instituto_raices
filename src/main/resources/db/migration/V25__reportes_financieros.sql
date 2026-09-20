CREATE INDEX ix_ajuste_cargo_cargo_fecha
    ON ajuste_cargo (cargo_id, fecha_efectiva, id);

CREATE INDEX ix_aplicacion_pago_cargo_fecha
    ON aplicacion_pago (cargo_id, fecha_aplicacion, id);

CREATE INDEX ix_movimiento_cuenta_fecha_direccion
    ON movimiento_financiero (cuenta_id, fecha_operacion, direccion, id);

INSERT INTO permiso (codigo, descripcion) VALUES
    ('REPORTE_FINANCIERO_CONSULTAR', 'Consultar estados de cuenta y reportes financieros operativos')
ON CONFLICT DO NOTHING;
