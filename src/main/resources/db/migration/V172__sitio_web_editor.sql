CREATE TABLE sitio_web (
 institucion_id BIGINT PRIMARY KEY REFERENCES institucion(id),
 nombre VARCHAR(200) NOT NULL, eslogan VARCHAR(300) NOT NULL,
 portada VARCHAR(2000) NOT NULL, nosotros VARCHAR(5000) NOT NULL,
 oferta VARCHAR(5000) NOT NULL, talleres VARCHAR(5000) NOT NULL,
 inscripciones VARCHAR(5000) NOT NULL,
 principal VARCHAR(7) NOT NULL, acento VARCHAR(7) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);
CREATE TABLE sitio_web_publicacion (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 institucion_id BIGINT NOT NULL REFERENCES institucion(id),
 nombre VARCHAR(200) NOT NULL, eslogan VARCHAR(300) NOT NULL,
 portada TEXT NOT NULL, nosotros TEXT NOT NULL, oferta TEXT NOT NULL,
 talleres TEXT NOT NULL, inscripciones TEXT NOT NULL,
 principal VARCHAR(7) NOT NULL, acento VARCHAR(7) NOT NULL,
 version BIGINT NOT NULL, publicado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 publicado_por BIGINT NOT NULL REFERENCES usuario(id),
 UNIQUE(institucion_id,version)
);
INSERT INTO permiso(codigo,descripcion) VALUES
 ('SITIO_WEB_EDITAR','Diseño web: editar borrador y vista previa'),
 ('SITIO_WEB_PUBLICAR','Diseño web: publicar el borrador');

CREATE TABLE sitio_web_imagen (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 institucion_id BIGINT NOT NULL REFERENCES institucion(id),
 contenido BYTEA NOT NULL, mime VARCHAR(30) NOT NULL,
 creado_por BIGINT NOT NULL REFERENCES usuario(id),
 creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CHECK(octet_length(contenido)<=5242880)
);
ALTER TABLE sitio_web ADD COLUMN logo_id BIGINT REFERENCES sitio_web_imagen(id),
 ADD COLUMN portada_id BIGINT REFERENCES sitio_web_imagen(id),
 ADD COLUMN portada_alt VARCHAR(200);
ALTER TABLE sitio_web_publicacion ADD COLUMN logo_id BIGINT REFERENCES sitio_web_imagen(id),
 ADD COLUMN portada_id BIGINT REFERENCES sitio_web_imagen(id),
 ADD COLUMN portada_alt VARCHAR(200);
CREATE TABLE sitio_web_revision (
 institucion_id BIGINT NOT NULL REFERENCES institucion(id),
 version BIGINT NOT NULL, contenido JSONB NOT NULL,
 guardado_por BIGINT NOT NULL REFERENCES usuario(id),
 guardado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY(institucion_id,version)
);
