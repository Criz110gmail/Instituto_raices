-- Diseño visual versionado por institución. Los sitios existentes conservan su presentación.
ALTER TABLE sitio_web ADD COLUMN diseno JSONB;
ALTER TABLE sitio_web_publicacion ADD COLUMN diseno JSONB;
