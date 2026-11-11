-- Extensión del contenido versionado. No publica ni concede permisos automáticamente.
ALTER TABLE sitio_web ADD COLUMN extras JSONB NOT NULL DEFAULT '{}'::jsonb;
ALTER TABLE sitio_web_publicacion ADD COLUMN extras JSONB NOT NULL DEFAULT '{}'::jsonb;
