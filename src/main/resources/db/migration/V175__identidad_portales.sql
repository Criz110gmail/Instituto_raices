CREATE TABLE portal_identidad (
 institucion_id BIGINT NOT NULL REFERENCES institucion(id),
 portal VARCHAR(12) NOT NULL CHECK(portal IN ('admin','familias','maestros','personal')),
 version BIGINT NOT NULL DEFAULT 0 CHECK(version>=0),
 publicada_version BIGINT,
 PRIMARY KEY(institucion_id,portal)
);
CREATE TABLE portal_identidad_revision (
 institucion_id BIGINT NOT NULL,
 portal VARCHAR(12) NOT NULL,
 version BIGINT NOT NULL CHECK(version>0),
 principal VARCHAR(7) NOT NULL CHECK(principal ~ '^#[0-9a-fA-F]{6}$'),
 fondo VARCHAR(7) NOT NULL CHECK(fondo ~ '^#[0-9a-fA-F]{6}$'),
 acento VARCHAR(7) NOT NULL CHECK(acento ~ '^#[0-9a-fA-F]{6}$'),
 original BOOLEAN NOT NULL,
 nombre VARCHAR(200) NOT NULL,
 logo BYTEA CHECK(logo IS NULL OR octet_length(logo)<=5242880),
 guardado_por BIGINT NOT NULL REFERENCES usuario(id),
 guardado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 publicado_por BIGINT REFERENCES usuario(id),
 publicado_en TIMESTAMPTZ,
 PRIMARY KEY(institucion_id,portal,version),
 FOREIGN KEY(institucion_id,portal) REFERENCES portal_identidad(institucion_id,portal),
 CHECK((publicado_por IS NULL)=(publicado_en IS NULL))
);
ALTER TABLE portal_identidad ADD CONSTRAINT fk_portal_publicada FOREIGN KEY(institucion_id,portal,publicada_version)
 REFERENCES portal_identidad_revision(institucion_id,portal,version);
INSERT INTO permiso(codigo,descripcion) VALUES
 ('PORTAL_IDENTIDAD_LEER','Portales e identidad: consultar diseños y enlaces'),
 ('PORTAL_IDENTIDAD_ADMINISTRAR','Portales e identidad: editar, publicar y restaurar diseños');
