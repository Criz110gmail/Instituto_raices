-- Metadatos privados de vistas previas; no crea adeudos, recargos ni pagos.
CREATE TABLE generacion_seleccion (
 id UUID PRIMARY KEY,
 usuario_id BIGINT NOT NULL REFERENCES usuario(id),
 tipo VARCHAR(12) NOT NULL CHECK(tipo IN ('CARGOS','RECARGOS')),
 institucion_id BIGINT NOT NULL REFERENCES institucion(id),
 plantel_id BIGINT REFERENCES plantel(id),
 fecha_corte DATE NOT NULL,
 creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 expira_en TIMESTAMPTZ NOT NULL,
 completa BOOLEAN NOT NULL DEFAULT FALSE,
 utilizada BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE TABLE generacion_seleccion_item (
 seleccion_id UUID NOT NULL REFERENCES generacion_seleccion(id),
 clave VARCHAR(180) NOT NULL,
 version_origen BIGINT NOT NULL,
 importe_esperado NUMERIC(19,2) NOT NULL,
 vencimiento DATE NOT NULL,
 PRIMARY KEY(seleccion_id,clave)
);
CREATE INDEX ix_generacion_seleccion_expira ON generacion_seleccion(expira_en);

UPDATE guia_proceso_paso SET instrucciones=replace(replace(instrucciones,
    'Confirmar y generar adeudos faltantes','Confirmar y generar adeudos seleccionados'),
    'Confirmar y generar recargos.','Confirmar y generar recargos seleccionados.'),
    precaucion=precaucion || ' Antes de confirmar revisa las casillas: todas están marcadas por defecto, incluidas otras páginas. Usa Desmarcar todos y marca sólo los registros del ejemplo si hay otros de prueba. El importe seleccionado aparece separado de los totales de la vista. Las desmarcadas se omiten sólo en esta ejecución y vuelven a aparecer en una nueva consulta. La selección caduca en dos horas; si cambian los registros, vuelve a visualizar.'
WHERE ruta IN ('/admin/cargos/generar','/admin/politicas-recargo/generar');
UPDATE guia_proceso SET version_contenido=version_contenido+1,revisada_el=DATE '2026-10-06'
WHERE id IN (SELECT guia_id FROM guia_proceso_paso WHERE ruta IN ('/admin/cargos/generar','/admin/politicas-recargo/generar'));
