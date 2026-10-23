-- Vistas previas privadas: no son nómina autorizada ni movimientos financieros.
CREATE TABLE nomina_preparacion (
 id UUID PRIMARY KEY, institucion_id BIGINT NOT NULL REFERENCES institucion(id),
 usuario_id BIGINT NOT NULL REFERENCES usuario(id), periodo_id BIGINT NOT NULL,
 borrador_id BIGINT REFERENCES nomina_borrador(id), version_origen BIGINT,
 modo VARCHAR(12) NOT NULL CHECK(modo IN ('TODOS','INDIVIDUAL','ACTUALIZAR')),
 estado VARCHAR(12) NOT NULL DEFAULT 'ABIERTA' CHECK(estado IN ('ABIERTA','GUARDADA','DESCARTADA')),
 version BIGINT NOT NULL DEFAULT 0, creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 caduca_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP+INTERVAL '2 hours',
 periodo_codigo VARCHAR(50) NOT NULL, periodo_nombre VARCHAR(150) NOT NULL,
 calendario_texto VARCHAR(450) NOT NULL, inicio DATE NOT NULL, fin DATE NOT NULL, pago DATE NOT NULL,
 moneda VARCHAR(3) NOT NULL, datos_periodo JSONB NOT NULL, sello_periodo VARCHAR(64) NOT NULL,
 nombre VARCHAR(150) NOT NULL, motivo VARCHAR(2000) NOT NULL,
 guardado_id BIGINT REFERENCES nomina_borrador(id),
 UNIQUE(id,institucion_id),
 FOREIGN KEY(periodo_id,institucion_id) REFERENCES periodo_nomina(id,institucion_id),
 CHECK((borrador_id IS NULL AND version_origen IS NULL AND modo<>'ACTUALIZAR') OR
       (borrador_id IS NOT NULL AND version_origen IS NOT NULL AND modo='ACTUALIZAR'))
);
CREATE INDEX ix_nomina_preparacion_usuario ON nomina_preparacion(institucion_id,usuario_id,caduca_en);
CREATE TABLE nomina_preparacion_empleado (
 preparacion_id UUID NOT NULL, institucion_id BIGINT NOT NULL, empleado_id BIGINT NOT NULL,
 codigo VARCHAR(50) NOT NULL, nombre VARCHAR(400) NOT NULL,
 grupo VARCHAR(12) NOT NULL CHECK(grupo IN ('LISTO','REVISION','NO_INCLUIDO')),
 motivo TEXT NOT NULL, seleccionado BOOLEAN NOT NULL,
 sueldo NUMERIC(19,2), percepciones NUMERIC(19,2), deducciones NUMERIC(19,2), aportaciones NUMERIC(19,2), propuesto NUMERIC(19,2),
 fuentes JSONB NOT NULL, sello VARCHAR(64) NOT NULL,
 PRIMARY KEY(preparacion_id,empleado_id),
 FOREIGN KEY(preparacion_id,institucion_id) REFERENCES nomina_preparacion(id,institucion_id) ON DELETE CASCADE,
 FOREIGN KEY(empleado_id,institucion_id) REFERENCES empleado(id,institucion_id),
 CHECK(NOT seleccionado OR grupo='LISTO'),
 CHECK((grupo='LISTO' AND sueldo>0 AND percepciones>=0 AND deducciones>=0 AND aportaciones>=0 AND propuesto>=0 AND propuesto=sueldo+percepciones-deducciones)
    OR (grupo<>'LISTO' AND sueldo IS NULL AND percepciones IS NULL AND deducciones IS NULL AND aportaciones IS NULL AND propuesto IS NULL))
);
CREATE INDEX ix_nomina_preparacion_grupo ON nomina_preparacion_empleado(preparacion_id,grupo,seleccionado,empleado_id);
