ALTER TABLE cargo DROP CONSTRAINT ck_cargo_estado;
ALTER TABLE cargo DROP CONSTRAINT ck_cargo_cancelacion;
ALTER TABLE cargo ADD CONSTRAINT ck_cargo_estado
    CHECK (estado_registro IN ('EMITIDO', 'CANCELADO', 'CONVENIDO'));
ALTER TABLE cargo ADD CONSTRAINT ck_cargo_cancelacion CHECK (
    (estado_registro IN ('EMITIDO', 'CONVENIDO') AND cancelado_en IS NULL AND motivo_cancelacion IS NULL)
    OR (estado_registro = 'CANCELADO' AND cancelado_en IS NOT NULL
        AND motivo_cancelacion IS NOT NULL AND btrim(motivo_cancelacion) <> '')
);

CREATE SEQUENCE seq_convenio_pago_folio START WITH 1 INCREMENT BY 1;

CREATE TABLE convenio_pago (
    id BIGSERIAL PRIMARY KEY,
    institucion_id BIGINT NOT NULL REFERENCES institucion(id),
    tutor_id BIGINT NOT NULL REFERENCES tutor(id),
    concepto_cobro_id BIGINT NOT NULL REFERENCES concepto_cobro(id),
    folio VARCHAR(40) NOT NULL UNIQUE,
    fecha_acuerdo DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    descripcion VARCHAR(250) NOT NULL,
    motivo TEXT NOT NULL,
    condiciones TEXT,
    moneda VARCHAR(3) NOT NULL,
    saldo_original_total NUMERIC(14,2) NOT NULL,
    monto_acordado_total NUMERIC(14,2) NOT NULL,
    monto_condonado_total NUMERIC(14,2) NOT NULL,
    estado VARCHAR(15) NOT NULL,
    cancelado_en TIMESTAMPTZ,
    motivo_cancelacion TEXT,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT now(),
    creado_por_id BIGINT REFERENCES usuario(id),
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT now(),
    actualizado_por_id BIGINT REFERENCES usuario(id),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_convenio_fechas CHECK (fecha_vencimiento >= fecha_acuerdo),
    CONSTRAINT ck_convenio_montos CHECK (saldo_original_total > 0 AND monto_acordado_total > 0
        AND monto_acordado_total <= saldo_original_total
        AND monto_condonado_total = saldo_original_total - monto_acordado_total),
    CONSTRAINT ck_convenio_estado CHECK (estado IN ('VIGENTE', 'CANCELADO')),
    CONSTRAINT ck_convenio_cancelacion CHECK (
        (estado = 'VIGENTE' AND cancelado_en IS NULL AND motivo_cancelacion IS NULL)
        OR (estado = 'CANCELADO' AND cancelado_en IS NOT NULL AND btrim(motivo_cancelacion) <> ''))
);
CREATE INDEX ix_convenio_pago_institucion_fecha ON convenio_pago(institucion_id, fecha_acuerdo DESC, id DESC);
CREATE INDEX ix_convenio_pago_tutor ON convenio_pago(tutor_id, estado, id DESC);

CREATE TABLE convenio_pago_cargo_original (
    id BIGSERIAL PRIMARY KEY,
    convenio_pago_id BIGINT NOT NULL REFERENCES convenio_pago(id),
    cargo_id BIGINT NOT NULL UNIQUE REFERENCES cargo(id),
    importe_total_snapshot NUMERIC(14,2) NOT NULL,
    monto_aplicado_snapshot NUMERIC(14,2) NOT NULL,
    saldo_incluido NUMERIC(14,2) NOT NULL,
    CONSTRAINT uq_convenio_original UNIQUE (convenio_pago_id, cargo_id),
    CONSTRAINT ck_convenio_original_montos CHECK (importe_total_snapshot >= 0
        AND monto_aplicado_snapshot >= 0 AND saldo_incluido > 0)
);
CREATE INDEX ix_convenio_original_convenio ON convenio_pago_cargo_original(convenio_pago_id, id);

CREATE TABLE convenio_pago_cargo_nuevo (
    id BIGSERIAL PRIMARY KEY,
    convenio_pago_id BIGINT NOT NULL REFERENCES convenio_pago(id),
    cargo_id BIGINT NOT NULL UNIQUE REFERENCES cargo(id),
    inscripcion_id BIGINT NOT NULL REFERENCES inscripcion(id),
    saldo_original_grupo NUMERIC(14,2) NOT NULL,
    monto_acordado NUMERIC(14,2) NOT NULL,
    CONSTRAINT uq_convenio_nuevo_inscripcion UNIQUE (convenio_pago_id, inscripcion_id),
    CONSTRAINT ck_convenio_nuevo_montos CHECK (saldo_original_grupo > 0 AND monto_acordado > 0
        AND monto_acordado <= saldo_original_grupo)
);
CREATE INDEX ix_convenio_nuevo_convenio ON convenio_pago_cargo_nuevo(convenio_pago_id, id);

INSERT INTO permiso(codigo, descripcion) VALUES
    ('CONVENIO_PAGO_ADMINISTRAR', 'Crear, consultar y cancelar convenios de pago')
ON CONFLICT DO NOTHING;

INSERT INTO rol_permiso(rol_id, permiso_id, activo)
SELECT DISTINCT rp.rol_id, nuevo.id, true
FROM rol_permiso rp
JOIN permiso actual ON actual.id=rp.permiso_id AND actual.codigo='CARGO_ADMINISTRAR'
JOIN permiso nuevo ON nuevo.codigo='CONVENIO_PAGO_ADMINISTRAR'
WHERE rp.activo=true
ON CONFLICT (rol_id, permiso_id) DO UPDATE SET activo=true, actualizado_en=now();
