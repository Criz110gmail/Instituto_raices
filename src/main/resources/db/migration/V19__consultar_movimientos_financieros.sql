CREATE INDEX ix_movimiento_institucion_filtros
    ON movimiento_financiero (institucion_id, direccion, clase, fecha_operacion DESC, id DESC);

CREATE INDEX ix_movimiento_plantel_fecha
    ON movimiento_financiero (plantel_operacion_id, fecha_operacion DESC, id DESC);

INSERT INTO permiso (codigo, descripcion) VALUES
    ('MOVIMIENTO_FINANCIERO_LEER', 'Consultar el libro de movimientos financieros y saldos de cuenta');
